package com.boot.controller;

import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderWorkflowIntegrationTest {
    @Autowired private MockMvc mvc;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private ObjectMapper mapper;
    private MockHttpSession admin;
    private MockHttpSession user;
    private String userid;
    private String orderid;

    @BeforeEach
    void setup() {
        userid = jdbc.queryForObject("SELECT usersid FROM users LIMIT 1", String.class);
        orderid = "T" + UUID.randomUUID().toString().replace("-", "").substring(0, 30);
        jdbc.update("INSERT INTO orders(ordersid,ordercode,usersid,total,status) VALUES(?,?,?,?,?)", orderid, orderid, userid, "36.80", "待付款");
        admin = new MockHttpSession();
        admin.setAttribute("adminUserId", "test-admin");
        user = new MockHttpSession();
        user.setAttribute("userId", userid);
    }

    private String body(String action) throws Exception {
        Map<String, String> data = new HashMap<String, String>();
        data.put("id", orderid);
        data.put("action", action);
        data.put("reason", "自动测试原因");
        return mapper.writeValueAsString(data);
    }

    private void adminAct(String action, String expected) throws Exception {
        mvc.perform(post("/orders/status.action").session(admin).contentType("application/json").content(body(action)))
            .andExpect(status().isOk()).andExpect(jsonPath("$.status").value(expected));
        assertEquals(expected, jdbc.queryForObject("SELECT status FROM orders WHERE ordersid=?", String.class, orderid));
    }

    @Test
    void cancelUnpaidAndPreventTerminalChanges() throws Exception {
        adminAct("cancel", "已取消");
        mvc.perform(post("/orders/status.action").session(admin).contentType("application/json").content(body("accept"))).andExpect(status().isConflict());
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM order_events WHERE ordersid=?", Integer.class, orderid));
    }

    @Test
    void deliveryCanBeRecalledAndDispatchedAgain() throws Exception {
        mvc.perform(post("/index/pay.action").session(user).param("id", orderid)).andExpect(status().isOk());
        adminAct("accept", "已接单");
        adminAct("prepare", "制作中");
        adminAct("dispatch", "配送中");
        adminAct("cancelDelivery", "制作中");
        adminAct("dispatch", "配送中");
        adminAct("complete", "已完成");
    }

    @Test
    void refundRejectionRestoresOriginalStateAndApprovalIsFinal() throws Exception {
        jdbc.update("UPDATE orders SET status='已接单' WHERE ordersid=?", orderid);
        mvc.perform(post("/index/refund.action").session(user).param("id", orderid).param("reason", "不需要了"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("退款中"));
        adminAct("rejectRefund", "已接单");
        adminAct("cancel", "退款中");
        adminAct("approveRefund", "已退款");
        mvc.perform(post("/orders/status.action").session(admin).contentType("application/json").content(body("approveRefund"))).andExpect(status().isConflict());
        mvc.perform(get("/orders/history.action").session(admin).param("id", orderid)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    void unpaidCannotBeRefundedAndPaidCannotBeCancelledByUser() throws Exception {
        mvc.perform(post("/index/refund.action").session(user).param("id", orderid).param("reason", "测试")).andExpect(status().isConflict());
        jdbc.update("UPDATE orders SET status='已付款' WHERE ordersid=?", orderid);
        mvc.perform(post("/index/cancel.action").session(user).param("id", orderid)).andExpect(status().isConflict());
        mvc.perform(post("/orders/status.action").session(admin).contentType("application/json").content(mapper.writeValueAsString(Collections.singletonMap("id", orderid)))).andExpect(status().isConflict());
    }

    @Test
    void reasonIsRequiredAndOldRefundCannotGuessPreviousState() throws Exception {
        mvc.perform(post("/orders/status.action").session(admin).contentType("application/json").content("{\"id\":\"" + orderid + "\",\"action\":\"cancel\"}"))
            .andExpect(status().isBadRequest());
        jdbc.update("UPDATE orders SET status='退款中' WHERE ordersid=?", orderid);
        mvc.perform(post("/orders/status.action").session(admin).contentType("application/json").content(body("rejectRefund"))).andExpect(status().isConflict());
    }

    @Test
    void loginAndRoleAreRequiredAndOldWriteEndpointsAreBlocked() throws Exception {
        mvc.perform(get("/orders/getOrdersByPage.action")).andExpect(status().isUnauthorized());
        mvc.perform(get("/orders/getOrdersByPage.action/")).andExpect(status().isUnauthorized());
        mvc.perform(get("/orders/getOrdersByPage.action;anything=1")).andExpect(status().isUnauthorized());
        mvc.perform(get("/details/getAllDetails.action")).andExpect(status().isUnauthorized());
        mvc.perform(post("/orders/status.action").session(user).contentType("application/json").content(body("cancel"))).andExpect(status().isUnauthorized());
        mvc.perform(get("/orders/status.action").session(admin).param("id", orderid)).andExpect(status().isMethodNotAllowed());
        mvc.perform(post("/orders/updateOrders.action").session(admin).contentType("application/json").content("{}")).andExpect(status().isForbidden());
        mvc.perform(get("/orders/deleteOrders.action").session(admin).param("id", orderid)).andExpect(status().isForbidden());
        mvc.perform(post("/details/updateDetails.action").session(admin).contentType("application/json").content("{}")).andExpect(status().isForbidden());
        mvc.perform(get("/index/pay.action").session(user).param("id", orderid)).andExpect(status().isMethodNotAllowed());
    }

    @Test
    void ownershipAndCrossSiteRequestsAreRejected() throws Exception {
        MockHttpSession other = new MockHttpSession();
        other.setAttribute("userId", "other-user");
        for (String endpoint : Arrays.asList("pay", "cancel", "refund", "over")) {
            mvc.perform(post("/index/" + endpoint + ".action").session(other).param("id", orderid).param("reason", "测试")).andExpect(status().isForbidden());
        }
        mvc.perform(get("/index/orderdetail.action").session(other).param("id", orderid)).andExpect(status().isForbidden());
        mvc.perform(get("/index/prePay.action").session(other).param("id", orderid)).andExpect(status().isForbidden());
        mvc.perform(get("/index/orderHistory.action").session(other).param("id", orderid)).andExpect(status().isForbidden());
        mvc.perform(post("/orders/status.action").session(admin).header("Origin", "https://untrusted.invalid").contentType("application/json").content(body("cancel"))).andExpect(status().isForbidden());
        mvc.perform(post("/orders/status.action").session(admin).header("Sec-Fetch-Site", "cross-site").contentType("application/json").content(body("cancel"))).andExpect(status().isForbidden());
    }

    @Test
    void spoofedUserQueryIsIgnoredAndPasswordsAreNotReturned() throws Exception {
        mvc.perform(get("/index/showOrders.action").session(user).param("userid", "other-user"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data[?(@.ordersid == '" + orderid + "')].usersid").value(org.hamcrest.Matchers.hasItem(userid)));
        String output = mvc.perform(get("/orders/getOrdersByPage.action").session(admin)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertFalse(output.contains("password"));
    }

    @Test
    void checkoutUsesSessionAndRealPrices() throws Exception {
        String foodsid = jdbc.queryForObject("SELECT foodsid FROM foods LIMIT 1", String.class);
        Integer before = jdbc.queryForObject("SELECT COUNT(*) FROM orders WHERE usersid=?", Integer.class, userid);
        mvc.perform(post("/index/addcart.action").session(user).contentType("application/json").content("{\"userid\":\"other-user\",\"foodsid\":\"" + foodsid + "\",\"price\":\"0.01\",\"num\":\"1\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true));
        mvc.perform(post("/index/checkout.action").session(user).contentType("application/json").content("{\"userid\":\"other-user\",\"receiver\":\"测试\",\"address\":\"测试地址\",\"contact\":\"00000000000\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true));
        assertEquals(before + 1, jdbc.queryForObject("SELECT COUNT(*) FROM orders WHERE usersid=?", Integer.class, userid));
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM orders WHERE usersid='other-user'", Integer.class));
    }

    @Test
    void loginCreatesServerUserIdentity() throws Exception {
        String username = "test-" + UUID.randomUUID().toString().substring(0, 8);
        String identity = "U" + UUID.randomUUID().toString().replace("-", "").substring(0, 30);
        jdbc.update("INSERT INTO users(usersid,username,password) VALUES(?,?,?)", identity, username, "test-password");
        MockHttpSession loginSession = new MockHttpSession();
        mvc.perform(post("/index/login.action").session(loginSession).contentType("application/json").content(mapper.writeValueAsString(new HashMap<String, String>() {{ put("username", username); put("password", "test-password"); }})))
            .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true));
        assertEquals(identity, loginSession.getAttribute("userId"));
        mvc.perform(get("/index/showOrders.action").session(loginSession).param("userid", userid)).andExpect(status().isOk()).andExpect(jsonPath("$.count").value(0));
    }

    @Test
    void logoutRevokesUserSession() throws Exception {
        mvc.perform(post("/index/logout.action").session(user)).andExpect(status().isOk());
        mvc.perform(get("/index/showOrders.action").session(user)).andExpect(status().isUnauthorized());
    }
}
