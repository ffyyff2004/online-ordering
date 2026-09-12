package com.boot.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import com.boot.entity.Foods;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class SmartMealPlanner {
    private static final Map<String, String> LABELS = new LinkedHashMap<String, String>();
    private static final Pattern COUNTS = Pattern.compile("([一二两三四1234])\\s*(荤|素|汤|主食)");
    private static final Pattern BUDGET = Pattern.compile("(?:预算\\s*(?:为|是|不超过)?\\s*|不超过\\s*|最多\\s*)(\\d+(?:\\.\\d{1,2})?)\\s*元?|(\\d+(?:\\.\\d{1,2})?)\\s*元\\s*(?:以内|以下|内)");

    static {
        LABELS.put("meat", "荤菜");
        LABELS.put("vegetable", "素菜");
        LABELS.put("soup", "汤品");
        LABELS.put("staple", "主食");
        LABELS.put("drink", "饮品");
        LABELS.put("dessert", "甜品");
        LABELS.put("recommended", "推荐餐品");
    }

    public String classificationRequirement(String requirement) {
        String cleaned = BUDGET.matcher(requirement).replaceAll("").replaceAll("^[，,\\s]+|[，,\\s]+$", "");
        return cleaned.isEmpty() ? "推荐餐品" : cleaned;
    }

    public Map<String, Object> plan(JsonNode response, String requirement, List<Foods> foods) {
        if (response == null || !response.path("groups").isArray() || response.path("groups").size() > 6) {
            throw new IllegalArgumentException("模型未返回有效的分组，请重新挑选。");
        }
        Map<String, Foods> menu = new HashMap<String, Foods>();
        for (Foods food : foods) {
            menu.put(food.getFoodsid(), food);
        }
        Map<String, Integer> required = requiredGroups(requirement);
        BigDecimal budget = budget(response, requirement);
        List<Group> groups = new ArrayList<Group>();
        Set<String> types = new HashSet<String>();
        Set<String> usedIds = new HashSet<String>();
        for (JsonNode node : response.path("groups")) {
            String type = node.path("type").asText();
            if (!LABELS.containsKey(type) || !types.add(type) || !node.path("candidates").isArray()) {
                throw new IllegalArgumentException("模型分组格式不正确，请重新挑选。");
            }
            if (!required.isEmpty() && !required.containsKey(type)) {
                continue;
            }
            Group group = new Group(type, required.containsKey(type) ? required.get(type) : node.path("count").asInt(1));
            if (group.count < 1 || group.count > 3) {
                throw new IllegalArgumentException("目前支持每类选择 1 到 3 道菜，请调整需求。");
            }
            for (JsonNode candidate : node.path("candidates")) {
                String foodsid = candidate.path("foodsid").asText();
                Foods food = menu.get(foodsid);
                String reason = candidate.path("reason").asText().trim();
                if (food == null || food.getPrice() == null || usedIds.contains(foodsid) || reason.isEmpty() || reason.length() > 300) {
                    continue;
                }
                try {
                    BigDecimal price = new BigDecimal(food.getPrice()).setScale(2, java.math.RoundingMode.UNNECESSARY);
                    if (price.signum() <= 0) continue;
                    group.pool.add(new Candidate(food, price, reason));
                    usedIds.add(foodsid);
                } catch (IllegalArgumentException | ArithmeticException exception) {
                    continue;
                }
            }
            groups.add(group);
        }
        if (groups.isEmpty() || !types.containsAll(required.keySet())) {
            throw new IllegalArgumentException("未能识别完整的菜品分类，请重新描述需求。");
        }
        for (Group group : groups) {
            if (group.pool.size() < group.count) {
                throw new IllegalArgumentException("当前符合条件的" + LABELS.get(group.type) + "不足，无法完成搭配。请调整需求。");
            }
        }
        if (budget == null) {
            for (Group group : groups) {
                Collections.shuffle(group.pool);
                group.selected.addAll(group.pool.subList(0, Math.min(3, group.pool.size())));
            }
        } else {
            chooseWithinBudget(groups, budget);
        }
        List<Map<String, Object>> output = new ArrayList<Map<String, Object>>();
        List<String> shortages = new ArrayList<String>();
        for (Group group : groups) {
            Collections.shuffle(group.selected);
            Map<String, Object> item = new LinkedHashMap<String, Object>();
            item.put("type", group.type);
            item.put("label", LABELS.get(group.type));
            item.put("count", group.count);
            List<Map<String, Object>> candidates = new ArrayList<Map<String, Object>>();
            for (Candidate selected : group.selected) {
                Map<String, Object> candidate = new LinkedHashMap<String, Object>();
                candidate.put("foodsid", selected.food.getFoodsid());
                candidate.put("foodsname", selected.food.getFoodsname());
                candidate.put("image", selected.food.getImage());
                candidate.put("price", selected.price);
                candidate.put("reason", selected.reason);
                candidates.add(candidate);
            }
            item.put("items", candidates);
            output.add(item);
            if (candidates.size() < 3) shortages.add(LABELS.get(group.type) + "提供 " + candidates.size() + " 个候选");
        }
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("success", true);
        result.put("groups", output);
        result.put("budget", budget);
        result.put("message", "已按需求分组随机挑选，可逐个加入购物车。" +
                (budget == null ? "" : "按各组建议数量选择时，搭配总价不超过 " + budget.toPlainString() + " 元。") +
                (shortages.isEmpty() ? "" : "为满足需求和预算，不凑数：" + String.join("，", shortages) + "。"));
        return result;
    }

    private void chooseWithinBudget(List<Group> groups, BigDecimal budget) {
        for (Group group : groups) {
            group.pool.sort(Comparator.comparing(candidate -> candidate.price));
            group.selected.addAll(group.pool.subList(0, group.count));
        }
        if (maximumTotal(groups).compareTo(budget) > 0) {
            throw new IllegalArgumentException("当前餐品无法组成符合预算的搭配，请提高预算或减少菜品数量。");
        }
        List<Group> order = new ArrayList<Group>(groups);
        Collections.shuffle(order);
        for (Group group : order) {
            List<Candidate> shuffled = new ArrayList<Candidate>(group.pool);
            Collections.shuffle(shuffled);
            for (Candidate candidate : shuffled) {
                if (group.selected.contains(candidate)) continue;
                if (group.selected.size() >= 3) break;
                group.selected.add(candidate);
                if (maximumTotal(groups).compareTo(budget) > 0) group.selected.remove(candidate);
            }
            Collections.shuffle(shuffled);
            for (Candidate candidate : shuffled) {
                if (group.selected.contains(candidate)) continue;
                int position = java.util.concurrent.ThreadLocalRandom.current().nextInt(group.selected.size());
                Candidate previous = group.selected.set(position, candidate);
                if (maximumTotal(groups).compareTo(budget) > 0) group.selected.set(position, previous);
            }
        }
    }

    private BigDecimal maximumTotal(List<Group> groups) {
        BigDecimal total = BigDecimal.ZERO;
        for (Group group : groups) {
            List<Candidate> sorted = new ArrayList<Candidate>(group.selected);
            sorted.sort(Comparator.comparing((Candidate candidate) -> candidate.price).reversed());
            for (int index = 0; index < group.count; index++) total = total.add(sorted.get(index).price);
        }
        return total;
    }

    private Map<String, Integer> requiredGroups(String requirement) {
        Map<String, Integer> groups = new LinkedHashMap<String, Integer>();
        Matcher matcher = COUNTS.matcher(requirement);
        List<String> names = Arrays.asList("荤", "素", "汤", "主食");
        List<String> types = Arrays.asList("meat", "vegetable", "soup", "staple");
        while (matcher.find()) {
            String number = matcher.group(1).replace("一", "1").replace("二", "2").replace("两", "2").replace("三", "3").replace("四", "4");
            groups.put(types.get(names.indexOf(matcher.group(2))), Integer.valueOf(number));
        }
        return groups;
    }

    private BigDecimal budget(JsonNode response, String requirement) {
        Matcher matcher = BUDGET.matcher(requirement);
        BigDecimal budget = null;
        while (matcher.find()) {
            BigDecimal amount = new BigDecimal(matcher.group(1) == null ? matcher.group(2) : matcher.group(1));
            budget = budget == null ? amount : budget.min(amount);
        }
        if (budget == null && !response.path("budget").isMissingNode() && !response.path("budget").isNull()) {
            if (!response.path("budget").isNumber()) throw new IllegalArgumentException("预算格式不正确，请输入数字预算，例如 50 元以内。");
            budget = response.path("budget").decimalValue();
        }
        if (budget != null && (budget.signum() <= 0 || budget.compareTo(new BigDecimal("100000")) > 0)) {
            throw new IllegalArgumentException("请填写有效的用餐预算。");
        }
        return budget;
    }

    private static class Group {
        private final String type;
        private final int count;
        private final List<Candidate> pool = new ArrayList<Candidate>();
        private final List<Candidate> selected = new ArrayList<Candidate>();
        private Group(String type, int count) { this.type = type; this.count = count; }
    }

    private static class Candidate {
        private final Foods food;
        private final BigDecimal price;
        private final String reason;
        private Candidate(Foods food, BigDecimal price, String reason) { this.food = food; this.price = price; this.reason = reason; }
    }
}
