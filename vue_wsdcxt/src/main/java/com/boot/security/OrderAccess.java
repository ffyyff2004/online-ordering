package com.boot.security;

import java.net.URI;
import java.util.*;
import javax.servlet.http.*;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.server.ResponseStatusException;

@Component
public class OrderAccess implements HandlerInterceptor {
    private final JdbcTemplate jdbc;
    private static final Set<String> USER_PATHS = new HashSet<String>(Arrays.asList("cart", "deletecart", "addcart", "checkout", "showOrders", "orderdetail", "orderHistory", "prePay", "pay", "cancel", "refund", "over", "preTopic", "addTopic", "logout"));
    private static final Set<String> WRITES = new HashSet<String>(Arrays.asList("deletecart", "addcart", "checkout", "pay", "cancel", "refund", "over", "addTopic", "logout"));

    public OrderAccess(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = (String) request.getAttribute(org.springframework.web.servlet.HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (path == null) return true;
        while (path.endsWith("/")) path = path.substring(0, path.length() - 1);
        if (!path.endsWith(".action")) return true;
        String name = path.substring(path.lastIndexOf('/') + 1, path.length() - 7);
        boolean admin = !path.startsWith("/index/") && !path.startsWith("/login/") && !path.startsWith("/smart-meal/");
        boolean user = path.startsWith("/index/") && USER_PATHS.contains(name);
        if (!admin && !user) return true;
        HttpSession session = request.getSession(false);
        String actor = session == null ? null : (String) session.getAttribute(admin ? "adminUserId" : "userId");
        if (actor == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "登录已失效，请重新登录");
        String origin = request.getHeader("Origin");
        if (origin != null) {
            URI actual;
            try { actual = URI.create(origin); } catch (IllegalArgumentException exception) { throw new ResponseStatusException(HttpStatus.FORBIDDEN, "不允许跨站操作"); }
            int port = actual.getPort() < 0 ? ("https".equals(actual.getScheme()) ? 443 : 80) : actual.getPort();
            if (!request.getScheme().equals(actual.getScheme()) || !request.getServerName().equals(actual.getHost()) || request.getServerPort() != port) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "不允许跨站操作");
        }
        if ("cross-site".equals(request.getHeader("Sec-Fetch-Site"))) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "不允许跨站操作");
        if (admin && (path.startsWith("/orders/") || path.startsWith("/details/"))) {
            if (Arrays.asList("insertOrders", "updateOrders", "deleteOrders", "deleteOrdersByIds", "insertDetails", "updateDetails", "deleteDetails", "deleteDetailsByIds").contains(name)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "订单及明细不能直接改写或删除，请使用订单操作");
            if ("status".equals(name) && !"POST".equals(request.getMethod())) throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "订单操作仅允许POST");
        }
        if (user) {
            if (WRITES.contains(name) && !"POST".equals(request.getMethod())) throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "订单操作仅允许POST");
            if (Arrays.asList("orderHistory", "prePay", "pay", "cancel", "refund", "over", "preTopic").contains(name)) requireOwner("orders", "ordersid", request.getParameter("id"), actor);
            if ("orderdetail".equals(name)) requireOwner("orders", "ordercode", request.getParameter("id"), actor);
            if ("deletecart".equals(name)) requireOwner("cart", "cartid", request.getParameter("id"), actor);
        }
        return true;
    }

    private void requireOwner(String table, String key, String id, String actor) {
        List<String> owners = jdbc.queryForList("SELECT usersid FROM " + table + " WHERE " + key + "=?", String.class, id);
        if (owners.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "记录不存在");
        if (!actor.equals(owners.get(0))) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "不能访问他人的记录");
    }
}
