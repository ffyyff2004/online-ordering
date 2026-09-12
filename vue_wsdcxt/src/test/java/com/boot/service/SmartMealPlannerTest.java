package com.boot.service;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import com.boot.entity.Foods;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

class SmartMealPlannerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final SmartMealPlanner planner = new SmartMealPlanner();
    private final List<Foods> foods = new ArrayList<Foods>();

    private ObjectNode fixture(int count) {
        foods.clear();
        ObjectNode response = mapper.createObjectNode();
        response.putNull("budget");
        response.putArray("groups");
        for (String type : new String[]{"meat", "vegetable"}) {
            ObjectNode group = response.withArray("groups").addObject();
            group.put("type", type).put("count", 1).putArray("candidates");
            for (int index = 0; index < count; index++) {
                Foods food = new Foods();
                food.setFoodsid(type + index);
                food.setFoodsname(type + index);
                food.setPrice(String.valueOf((type.equals("meat") ? 20 : 10) + index * 5));
                foods.add(food);
                group.withArray("candidates").addObject().put("foodsid", food.getFoodsid()).put("reason", "符合所需分类");
            }
        }
        return response;
    }

    @Test
    void returnsThreeDistinctCandidatesPerGroupAndUsesDatabasePrices() {
        ObjectNode response = fixture(5);
        response.path("groups").get(0).path("candidates").forEach(candidate -> ((ObjectNode) candidate).put("price", "0.01"));
        JsonNode result = mapper.valueToTree(planner.plan(response, "一荤一素", foods));
        assertTrue(result.path("success").asBoolean());
        Set<String> ids = new HashSet<String>();
        for (JsonNode group : result.path("groups")) {
            assertEquals(3, group.path("items").size());
            for (JsonNode item : group.path("items")) {
                assertTrue(ids.add(item.path("foodsid").asText()));
                assertTrue(item.path("price").decimalValue().compareTo(BigDecimal.TEN) >= 0);
            }
        }
        assertEquals(6, ids.size());
    }

    @Test
    void randomSelectionCanProduceDifferentSets() {
        ObjectNode response = fixture(5);
        Set<String> sets = new HashSet<String>();
        for (int attempt = 0; attempt < 30; attempt++) {
            JsonNode result = mapper.valueToTree(planner.plan(response, "一荤一素", foods));
            Set<String> ids = new java.util.TreeSet<String>();
            result.path("groups").forEach(group -> group.path("items").forEach(item -> ids.add(item.path("foodsid").asText())));
            sets.add(ids.toString());
        }
        assertTrue(sets.size() > 1);
    }

    @Test
    void everyDisplayedPairStaysWithinExplicitBudgetEvenWhenModelChangesIt() {
        ObjectNode response = fixture(5);
        response.put("budget", 500);
        for (int attempt = 0; attempt < 50; attempt++) {
            JsonNode result = mapper.valueToTree(planner.plan(response, "50元以内，一荤一素", foods));
            assertEquals(0, new BigDecimal("50").compareTo(result.path("budget").decimalValue()));
            for (JsonNode meat : result.path("groups").get(0).path("items")) {
                for (JsonNode vegetable : result.path("groups").get(1).path("items")) {
                    assertTrue(meat.path("price").decimalValue().add(vegetable.path("price").decimalValue()).compareTo(new BigDecimal("50")) <= 0);
                }
            }
        }
    }

    @Test
    void shortageDoesNotDuplicateOrInventCandidates() {
        ObjectNode response = fixture(2);
        ((ObjectNode) response.path("groups").get(0)).withArray("candidates").addObject().put("foodsid", "invented").put("reason", "不存在");
        ((ObjectNode) response.path("groups").get(0)).withArray("candidates").addObject().put("foodsid", "meat0").put("reason", "重复");
        JsonNode result = mapper.valueToTree(planner.plan(response, "一荤一素", foods));
        assertEquals(2, result.path("groups").get(0).path("items").size());
        assertTrue(result.path("message").asText().contains("不凑数"));
    }

    @Test
    void impossibleBudgetReturnsActionableReason() {
        ObjectNode response = fixture(4);
        assertTrue(assertThrows(IllegalArgumentException.class, () -> planner.plan(response, "5元以内，一荤一素", foods)).getMessage().contains("预算"));
    }

    @Test
    void countRequirementCannotBeRelaxedByModel() {
        ObjectNode response = fixture(4);
        JsonNode result = mapper.valueToTree(planner.plan(response, "两荤一素", foods));
        assertEquals(2, result.path("groups").get(0).path("count").asInt());
        ObjectNode incomplete = fixture(1);
        assertThrows(IllegalArgumentException.class, () -> planner.plan(incomplete, "两荤一素", foods));
    }

    @Test
    void supportsOtherRequestedGroupTypes() {
        ObjectNode response = fixture(4);
        ((ObjectNode) response.path("groups").get(1)).put("type", "soup");
        JsonNode result = mapper.valueToTree(planner.plan(response, "荤菜配一碗汤", foods));
        assertEquals("汤品", result.path("groups").get(1).path("label").asText());
    }

    @Test
    void missingRequestedGroupAndMalformedSchemaAreRejected() {
        ObjectNode response = fixture(4);
        ((ObjectNode) response.path("groups").get(1)).put("type", "soup");
        assertThrows(IllegalArgumentException.class, () -> planner.plan(response, "一荤一素", foods));
        assertThrows(IllegalArgumentException.class, () -> planner.plan(mapper.createObjectNode(), "推荐菜品", foods));
    }
}
