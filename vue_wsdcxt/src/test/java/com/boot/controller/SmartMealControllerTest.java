package com.boot.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import com.boot.entity.Foods;
import com.boot.service.FoodsService;
import com.boot.service.SmartMealPlanner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

class SmartMealControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private HttpServer server;
    private SmartMealController controller;
    private final AtomicReference<JsonNode> request = new AtomicReference<JsonNode>();
    private final AtomicReference<String> auth = new AtomicReference<String>();
    private final AtomicReference<String> version = new AtomicReference<String>();
    private ObjectNode upstream;
    private int status = 200;

    @BeforeEach
    void setup() throws Exception {
        Foods meat = new Foods();
        meat.setFoodsid("meat1"); meat.setFoodsname("酸菜炒肉丝"); meat.setPrice("22.6");
        Foods vegetable = new Foods();
        vegetable.setFoodsid("veg1"); vegetable.setFoodsname("川味豆腐"); vegetable.setPrice("16.8");
        FoodsService foods = mock(FoodsService.class);
        when(foods.getAllFoods()).thenReturn(Arrays.asList(meat, vegetable));
        controller = new SmartMealController(foods, new SmartMealPlanner(), mapper);
        upstream = mapper.createObjectNode().put("type", "message").put("stop_reason", "end_turn");
        upstream.putArray("content");
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/v1/messages", exchange -> {
            request.set(mapper.readTree(exchange.getRequestBody()));
            auth.set(exchange.getRequestHeaders().getFirst("x-api-key"));
            version.set(exchange.getRequestHeaders().getFirst("anthropic-version"));
            byte[] bytes = mapper.writeValueAsBytes(upstream);
            exchange.sendResponseHeaders(status, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.start();
        ReflectionTestUtils.setField(controller, "baseUrl", "http://127.0.0.1:" + server.getAddress().getPort() + "/v1/");
        ReflectionTestUtils.setField(controller, "apiKey", "test-key");
        ReflectionTestUtils.setField(controller, "model", "claude-sonnet-5");
    }

    @AfterEach
    void cleanup() { server.stop(0); }

    private Map<String, Object> recommend() {
        return controller.recommend(mapper.createObjectNode().put("requirement", "50元以内，一荤一素").put("username", "不发送给模型的用户名"));
    }

    @Test
    void sendsAnthropicUtf8RequestAndCombinesTextBlocks() {
        String text = "{\"budget\":50,\"groups\":[{\"type\":\"meat\",\"count\":1,\"candidates\":[{\"foodsid\":\"meat1\",\"reason\":\"肉类\"}]},{\"type\":\"vegetable\",\"count\":1,\"candidates\":[{\"foodsid\":\"veg1\",\"reason\":\"豆腐\"}]}]}";
        upstream.withArray("content").addObject().put("type", "thinking").put("thinking", "ignored");
        upstream.withArray("content").addObject().put("type", "text").put("text", text.substring(0, 60));
        upstream.withArray("content").addObject().put("type", "text").put("text", text.substring(60));
        assertEquals(true, recommend().get("success"));
        assertEquals("test-key", auth.get());
        assertEquals("2023-06-01", version.get());
        assertEquals("claude-sonnet-5", request.get().path("model").asText());
        assertEquals(8192, request.get().path("max_tokens").asInt());
        assertTrue(request.get().path("system").asText().contains("分组"));
        assertTrue(request.get().path("messages").get(0).path("content").asText().contains("一荤一素"));
        assertFalse(request.get().path("messages").get(0).path("content").asText().contains("50元以内"));
        assertFalse(request.get().toString().contains("不发送给模型的用户名"));
    }

    @Test
    void emptyTextHasSpecificError() { assertEquals("AI_EMPTY", recommend().get("code")); }

    @Test
    void truncationHasSpecificError() {
        upstream.put("stop_reason", "max_tokens");
        assertEquals("AI_TRUNCATED", recommend().get("code"));
    }

    @Test
    void invalidJsonHasSpecificError() {
        upstream.withArray("content").addObject().put("type", "text").put("text", "无法完成");
        assertEquals("AI_FORMAT", recommend().get("code"));
    }

    @Test
    void upstreamErrorDoesNotLeakResponseOrKey() {
        status = 401;
        upstream.put("error", "private-upstream-data");
        Map<String, Object> result = recommend();
        assertEquals("AI_HTTP_ERROR", result.get("code"));
        assertFalse(result.toString().contains("private-upstream-data"));
        assertFalse(result.toString().contains("test-key"));
    }

    @Test
    void validatesInputBeforeCallingModel() {
        assertEquals("INVALID_REQUIREMENT", controller.recommend(mapper.createObjectNode().put("requirement", " ")).get("code"));
        ReflectionTestUtils.setField(controller, "apiKey", "");
        assertEquals("AI_NOT_CONFIGURED", recommend().get("code"));
        assertNull(request.get());
    }
}
