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

    @Value("${ai.base-url:https://api.openai.com/v1}")
    private String baseUrl;

    @Value("${ai.api-key:}")
    private String apiKey;

    @Value("${ai.model:gpt-4o-mini}")
    private String model;

    public SmartMealController(FoodsService foodsService) {
        this.foodsService = foodsService;
    }

    @PostMapping("/recommend.action")
    public Map<String, Object> recommend(@RequestBody String jsonStr) {
        Map<String, Object> result = new HashMap<String, Object>();
        JSONObject request = JSONObject.parseObject(jsonStr);
        String requirement = request == null ? null : request.getString("requirement");
        String username = request == null ? "闁活潿鍔嶉崺? : request.getString("username");
        if (requirement == null || requirement.trim().length() == 0) {
            return failure(result, "閻犲洤鍢查崢娑㈠川婵犲懐妲橀柟瀛樺灣缂嶆﹢鎯冮崟顐㈢稉闁告稓鍋ㄩ埀顑跨窔椤ｂ晝绮诲Δ浣哥仐闊洤鑻ぐ娑㈠Υ?);
        }
        if (apiKey == null || apiKey.trim().length() == 0) {
            return failure(result, "閻忓繑纰嶅﹢顓㈡煀瀹ュ洨鏋傚鍫嗗媭渚€宕?API Key闁挎稑鐭侀顒勫礂閸綆鍟庣紓?AI_API_KEY闁?);
        }

        List<Foods> foods = foodsService.getAllFoods();
        if (foods == null || foods.size() == 0) {
            return failure(result, "鐟滅増鎸告晶鐘测柦閳╁啯绠掗柛娆樺灠閺侇厽顦﹂幇顒佹儌闁挎稑鐭侀顒勫礂閸繃韬柛姘瑜板瓨绋夋繝鍐桓濡炰焦鍔曢幖褔濡?);
        }

        try {
            String response = callModel(username, requirement, foods);
            JSONArray recommendations = parseRecommendations(response);
            List<Map<String, Object>> validRecommendations = validateRecommendations(recommendations, foods);
            if (validRecommendations.size() != 3) {
                return failure(result, "婵☆垪鈧磭鈧嘲鈻介埄鍐╃畳閺夆晜鏌ㄥú?3 濞戞搩浜濆﹢渚€寮崼銏＄暠闁绘粎澧楀﹢浣诡槮閹邦剚鎯傞柡鍌濐潐椤㈠秹鏁嶅畝鍐惧殲闁硅婢€缁斿绮斿鍫殯婵炲娲栭崯鈧悹鍥ㄦ礉閻︻垶濡?);
            }
            result.put("success", true);
            result.put("recommendations", validRecommendations);
            result.put("message", "鐎圭寮堕悧鎾箲椤旇偐绉奸柛鎾崇Т瑜版煡宕鈧ˇ鐢稿传娴ｇ儤鏅搁柟?3 濞戞搩浜濋弻鐔奉浖?);
            return result;
        } catch (Exception exception) {
            return failure(result, "闁哄懘缂氶崗姗€鏌呮径鎰垫█闁哄棗鍊瑰鍌涚▔瀹ュ懎璁查柣銏╃厜缁辨繄鎷犳搴樻＆闁告艾閰ｉ崳鍝ユ嫚閺囨ǚ鍋?);
        }
    }

    private String callModel(String username, String requirement, List<Foods> foods) throws Exception {
        StringBuilder menu = new StringBuilder();
        for (Foods food : foods) {
            menu.append("濡炰焦鍔曢幖褏绱撻弽褍濞?").append(food.getFoodsid())
                    .append("|闁告艾绉惰ⅷ:").append(food.getFoodsname())
                    .append("|闁告帒妫涚悮?").append(food.getCatename())
                    .append("|濞寸娀鏀遍悧?").append(food.getPrice())
                    .append("|濞寸姴顑囩划?").append(food.getContents()).append("\n");
        }
        String system = "濞达絿濮靛Σ鎼佸捶閵娧冩疇闁绘劕缍婇ˇ鐢靛寲閼姐倗鍩犻柣銊ュ濞呫倝鎳楁禒瀣у亾婢舵劦妯€闁告柡鏅滄晶婊堝Υ閸屾艾娑ч柤铏灊缁娀鎮介妸锕€鐓曢柟缁樺姃缁剁敻鎯冮崟顐ょЪ闁告挸绉磋ぐ鏌ュ船椤曗偓椤︾敻宕担鐤幀闁规亽鍔忓畷姗€鏁嶇仦鑲╃憹闁煎疇妫勯崹閬嶆焻閻樿京鐟濋悗娑櫭﹢顏堟儍閸曨垼妯€闁告繀闄嶉埀?
                + "闊洤鎳橀妴蹇旀交閺傛寧绀€濞戞挶鍎查悧?JSON闁挎稑濂旂粭澶屾啺?Markdown闁挎稑濂旂粭澶屾啺娓氣偓椤ゅ倹寰勯弽銊︾€悗娑欍仠閳ь兛闈朣ON 闁哄秶鍘х槐鈩冪▔?{\"recommendations\":["
                + "{\"foodsid\":\"濡炰焦鍔曢幖褏绱撻弽褍濞嘰",\"reason\":\"缂備焦鎸搁幃搴ㄦ偨閵婏箑鐓曢梻鍥ｅ亾婵懓鍊诲▓鎴犵不閳ь剟鎯岄鐘冲€為柣銏㈡祩"},"
                + "{\"foodsid\":\"濡炰焦鍔曢幖褏绱撻弽褍濞嘰",\"reason\":\"闁荤偛妫涢弫鐩?},"
                + "{\"foodsid\":\"濡炰焦鍔曢幖褏绱撻弽褍濞嘰",\"reason\":\"闁荤偛妫涢弫鐩?}]}闁?
                + "濞戞挸顦柌婊堝棘鐟欏嫷鏀抽煫鍥ф嚇閵嗗繘寮伴娆戠憹闁告艾鐭傞ˇ鐢稿传娓氬﹦绀夋鐐存构缁楁牠鏌堥懞銉ュК閻℃帒纾弫銈夊箣閻戣姤浠樻慨鐟板€堕埀?;
        String user = "闁活潿鍔嶉崺娑氱矓閺夋寧鍤?" + username + "\n闁活潿鍔嶉崺娑㈡閳ь剙效?" + requirement + "\n鐟滅増鎸告晶鐘诲矗椤栨碍鏆涘浣瑰姇閹?\n" + menu;

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
        JSONArray contentBlocks = responseJson.getJSONArray("content");
        for (int index = 0; index < contentBlocks.size(); index++) {
            JSONObject block = contentBlocks.getJSONObject(index);
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
