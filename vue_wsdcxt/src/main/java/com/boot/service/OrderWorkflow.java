package com.boot.service;

import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class OrderWorkflow {
    private final JdbcTemplate jdbc;

    public OrderWorkflow(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @Transactional
    public Map<String, Object> act(String id, String action, String reason, String role, String actor) {
        if (actor == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        List<Map<String, Object>> rows = jdbc.queryForList("SELECT usersid,status FROM orders WHERE ordersid=? FOR UPDATE", id);
        if (rows.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "订单不存在");
        Map<String, Object> order = rows.get(0);
        boolean admin = "admin".equals(role);
        if (!admin && !actor.equals(order.get("usersid"))) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "不能操作他人的订单");
        String current = (String) order.get("status");
        String target = null;
        boolean needsReason = false;
        if (admin) {
            if ("accept".equals(action) && "已付款".equals(current)) target = "已接单";
            if ("prepare".equals(action) && "已接单".equals(current)) target = "制作中";
            if ("dispatch".equals(action) && "制作中".equals(current)) target = "配送中";
            if ("complete".equals(action) && "配送中".equals(current)) target = "已完成";
            if ("cancelDelivery".equals(action) && "配送中".equals(current)) { target = "制作中"; needsReason = true; }
            if ("cancel".equals(action)) {
                if ("待付款".equals(current)) target = "已取消";
                if (Arrays.asList("已付款", "已接单", "制作中").contains(current)) target = "退款中";
                needsReason = true;
            }
            if ("approveRefund".equals(action) && "退款中".equals(current)) { target = "已退款"; needsReason = true; }
            if ("rejectRefund".equals(action) && "退款中".equals(current)) {
                List<String> previous = jdbc.queryForList("SELECT from_status FROM order_events WHERE ordersid=? AND to_status='退款中' ORDER BY created_at DESC LIMIT 1", String.class, id);
                if (previous.isEmpty()) throw new ResponseStatusException(HttpStatus.CONFLICT, "旧退款订单缺少申请记录，不能自动拒绝恢复");
                target = previous.get(0);
                needsReason = true;
            }
        } else {
            if ("pay".equals(action) && "待付款".equals(current)) target = "已付款";
            if ("cancel".equals(action) && "待付款".equals(current)) target = "已取消";
            if ("complete".equals(action) && "配送中".equals(current)) target = "已完成";
            if ("refund".equals(action) && Arrays.asList("已付款", "已接单").contains(current)) { target = "退款中"; needsReason = true; }
        }
        if (target == null) throw new ResponseStatusException(HttpStatus.CONFLICT, "当前状态不允许该操作，请刷新订单");
        String note = reason == null ? "" : reason.trim();
        if ((needsReason && note.isEmpty()) || note.length() > 500) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请填写1到500字的操作原因");
        int changed = jdbc.update("UPDATE orders SET status=? WHERE ordersid=? AND status=?", target, id, current);
        if (changed != 1) throw new ResponseStatusException(HttpStatus.CONFLICT, "订单已更新，请刷新后重试");
        jdbc.update("INSERT INTO order_events(eventid,ordersid,action,from_status,to_status,actor_role,actor_id,reason) VALUES(?,?,?,?,?,?,?,?)", UUID.randomUUID().toString(), id, action, current, target, role, actor, note);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", true);
        result.put("code", 1);
        result.put("status", target);
        result.put("message", "已退款".equals(target) ? "已完成模拟退款登记（不涉及真实资金）" : "操作成功，当前状态：" + target);
        return result;
    }

    public List<Map<String, Object>> history(String id) {
        return jdbc.queryForList("SELECT action,from_status,to_status,actor_role,reason,created_at FROM order_events WHERE ordersid=? ORDER BY created_at,eventid", id);
    }
}

