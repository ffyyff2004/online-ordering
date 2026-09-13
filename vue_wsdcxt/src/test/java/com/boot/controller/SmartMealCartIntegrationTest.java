package com.boot.controller;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.boot.entity.Cart;
import com.boot.entity.Foods;
import com.boot.service.CartService;
import com.boot.service.FoodsService;
import com.boot.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@Transactional
class SmartMealCartIntegrationTest {
    @Autowired private IndexController index;
    @Autowired private FoodsService foods;
    @Autowired private UsersService users;
    @Autowired private CartService cart;
    @Autowired private ObjectMapper mapper;

    @org.junit.jupiter.api.AfterEach
    void clearRequest() { org.springframework.web.context.request.RequestContextHolder.resetRequestAttributes(); }

    @Test
    void twoIndividuallySelectedDishesWriteTwoCartRowsAndRollback() throws Exception {
        String userid = users.getAllUsers().get(0).getUsersid();
        List<Foods> menu = foods.getAllFoods();
        Cart filter = new Cart();
        filter.setUsersid(userid);
        org.springframework.mock.web.MockHttpServletRequest requestContext = new org.springframework.mock.web.MockHttpServletRequest();
        requestContext.getSession().setAttribute("userId", userid);
        org.springframework.web.context.request.RequestContextHolder.setRequestAttributes(new org.springframework.web.context.request.ServletRequestAttributes(requestContext));
        int before = cart.getCartByCond(filter).size();
        for (int selected = 0; selected < 2; selected++) {
            Foods food = menu.get(selected);
            String request = mapper.createObjectNode().put("userid", userid).put("foodsid", food.getFoodsid())
                    .put("price", food.getPrice()).put("num", "1").toString();
            assertEquals(true, index.addcart(request).get("success"));
            assertEquals(before + selected + 1, cart.getCartByCond(filter).size());
        }
    }
}
