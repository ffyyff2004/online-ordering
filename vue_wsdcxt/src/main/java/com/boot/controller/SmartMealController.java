package com.boot.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.boot.entity.Foods;
import com.boot.service.FoodsService;

@RestController
@RequestMapping(value = "/smart-meal", produces = "application/json; charset=utf-8")
@CrossOrigin
public class SmartMealController extends BaseController {
    private final FoodsService foodsService;

    @Value("${ai.base-url:https://coding.rockyy.top}")
    private String baseUrl;

    @Value("${ai.api-key:}")
    private String apiKey;

    @Value("${ai.model:claude-sonnet-5}")
    private String model;

    public SmartMealController(FoodsService foodsService) {
        this.foodsService = foodsService;
    }

    @PostMapping("/recommend.action")
    public Map<String, Object> recommend(@RequestBody String jsonStr) {
        Map<String, Object> result = new HashMap<String, Object>();
        JSONObject request = JSONObject.parseObject(jsonStr);
        String requirement = request == null ? null : request.getString("requirement");
        String username = request == null ? "用户" : request.getString("username");
        if (requirement == null || requirement.trim().isEmpty()) {
            return failure(result, "请先告诉我你的口味、预算或忌口。");
        }
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return failure(result, "尚未配置大模型 API Key，请先设置 AI_API_KEY。");
        }
        List<Foods> foods = foodsService.getAllFoods();
        if (foods == null || foods.isEmpty()) {
            return failure(result, "当前没有可售餐品，请先在后台上架餐品。");
        }
        try {
            String response = callModel(username, requirement, foods);
            List<Map<String, Object>> valid = validateRecommendations(parseRecommendations(response), foods);
            if (valid.size() != 3) {
                return failure(result, "模型没有返回 3 个有效的现有餐品方案，请换一种说法再试试。");
            }
            result.put("success", true);
            result.put("recommendations", valid);
            result.put("message", "已根据当前可售餐品生成 3 个方案");
            return result;
        } catch (Exception exception) {
            return failure(result, "智能选餐暂时不可用，请稍后重试。");
        }
    }

    private String callModel(String username, String requirement, List<Foods> foods) throws Exception {
        StringBuilder menu = new StringBuilder();
        for (Foods food : foods) {
            menu.append("id=").append(food.getFoodsid()).append("; name=").append(food.getFoodsname())
                    .append("; category=").append(food.getCatename()).append("; price=").append(food.getPrice())
                    .append("; description=").append(food.getContents()).append("\n");
        }
        String system = "You are a smart meal assistant. Recommend only foods from the supplied available menu. Never invent foods. Return exactly three different foods in valid JSON. The JSON must contain a recommendations array. Each item must contain foodsid and reason. Do not use Markdown.";
        String user = "Username: " + username + "\nRequirement: " + requirement + "\nAvailable menu:\n" + menu;
        JSONObject payload = new JSONObject();
        payload.put("model", model);
        payload.put("max_tokens", 1200);
        payload.put("system", system);
        JSONArray messages = new JSONArray();
        messages.add(message("user", user));
        payload.put("messages", messages);

        URL url = new URL(baseUrl.replaceAll("/$", "") + "/v1/messages");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(30000);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("x-api-key", apiKey);
        connection.setRequestProperty("anthropic-version", "2023-06-01");
        connection.setDoOutput(true);
        try (OutputStream output = connection.getOutputStream()) {
            output.write(payload.toJSONString().getBytes(StandardCharsets.UTF_8));
        }
        int status = connection.getResponseCode();
        InputStream stream = status >= 200 && status < 300 ? connection.getInputStream() : connection.getErrorStream();
        String response = read(stream);
        if (status < 200 || status >= 300) {
            throw new IllegalStateException("AI request failed: " + status);
        }
        JSONObject responseJson = JSONObject.parseObject(response);
        JSONArray content = responseJson.getJSONArray("content");
        for (int index = 0; index < content.size(); index++) {
            JSONObject block = content.getJSONObject(index);
            if ("text".equals(block.getString("type"))) {
                return block.getString("text");
            }
        }
        throw new IllegalStateException("Anthropic response did not contain text content");
    }

    private JSONObject message(String role, String content) {
        JSONObject message = new JSONObject();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

    private JSONArray parseRecommendations(String content) {
        String json = content == null ? "" : content.trim();
        int start = json.indexOf('{');
        int end = json.lastIndexOf('}');
        if (start >= 0 && end > start) {
            json = json.substring(start, end + 1);
        }
        return JSONObject.parseObject(json).getJSONArray("recommendations");
    }

    private List<Map<String, Object>> validateRecommendations(JSONArray recommendations, List<Foods> foods) {
        Map<String, Foods> foodMap = new HashMap<String, Foods>();
        for (Foods food : foods) {
            foodMap.put(food.getFoodsid(), food);
        }
        Set<String> used = new HashSet<String>();
        List<Map<String, Object>> valid = new ArrayList<Map<String, Object>>();
        if (recommendations == null) {
            return valid;
        }
        for (int index = 0; index < recommendations.size() && valid.size() < 3; index++) {
            JSONObject item = recommendations.getJSONObject(index);
            String foodsid = item.getString("foodsid");
            Foods food = foodMap.get(foodsid);
            if (food == null || used.contains(foodsid)) {
                continue;
            }
            Map<String, Object> recommendation = new HashMap<String, Object>();
            recommendation.put("foodsid", food.getFoodsid());
            recommendation.put("foodsname", food.getFoodsname());
            recommendation.put("image", food.getImage());
            recommendation.put("price", food.getPrice());
            recommendation.put("reason", item.getString("reason"));
            valid.add(recommendation);
            used.add(foodsid);
        }
        return valid;
    }

    private String read(InputStream stream) throws Exception {
        if (stream == null) {
            return "";
        }
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }

    private Map<String, Object> failure(Map<String, Object> result, String message) {
        result.put("success", false);
        result.put("message", message);
        return result;
    }
}
