package com.vps.omengine.application.agent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vps.omengine.application.agent.dto.AgentResponse;
import com.vps.omengine.application.agent.llm.LlmClient;
import com.vps.omengine.application.agent.tool.ToolExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AgentOrchestrator {

    private final LlmClient llmClient;
    private final ToolExecutor toolExecutor;

    private final ObjectMapper mapper = new ObjectMapper();

    private static final int MAX_STEPS = 5;

    public AgentResponse process(String userInput) {

        String context = userInput;
        Object observation = null;

        String lastTool = null;
        int repeatCount = 0;

        for (int step = 1; step <= MAX_STEPS; step++) {

            long start = System.currentTimeMillis();
            System.out.println("\n=== STEP " + step + " ===");

            try {
                // ✅ Send CLEAN JSON to LLM
                String observationJson = observation == null
                        ? ""
                        : mapper.writeValueAsString(observation);

                String llmOutput = llmClient.callAgent(context, observationJson);
                System.out.println("LLM OUTPUT: " + llmOutput);

                JsonNode root = mapper.readTree(llmOutput);

                // ✅ VALIDATION
                if (!root.has("tool")) {
                    return error("INVALID_LLM_OUTPUT", "Missing tool field");
                }

                String tool = root.get("tool").asText();
                System.out.println("TOOL: " + tool);

                // ✅ LOOP DETECTION
                if (tool.equals(lastTool)) {
                    repeatCount++;
                    if (repeatCount >= 2) {
                        return error("LOOP_DETECTED", "Agent repeating same action");
                    }
                } else {
                    repeatCount = 0;
                }
                lastTool = tool;

                // ✅ TOOL WHITELIST
                if (!List.of("search_products", "create_order", "final_answer")
                        .contains(tool)) {

                    return error("INVALID_TOOL", "Invalid tool: " + tool);
                }

                // ✅ FINAL ANSWER
                if ("final_answer".equals(tool)) {

                    if (observation instanceof Map obsMap) {
                        return success(
                                (String) obsMap.get("type"),
                                obsMap.get("data"),
                                (String) obsMap.getOrDefault("message", null)
                        );
                    }

                    return success("TEXT", null, root.path("input").asText());
                }

                // ✅ INPUT VALIDATION
                if (!root.has("input") || root.get("input").isNull()) {
                    return error("INVALID_INPUT", "Missing input for tool: " + tool);
                }

                JsonNode inputNode = root.get("input");

                // ✅ FLOW GUARD
                if ("create_order".equals(tool) && observation == null) {
                    return error("INVALID_FLOW", "Cannot order without product search");
                }

                // ✅ EXECUTE TOOL
                observation = toolExecutor.execute(tool, inputNode);
                System.out.println("OBSERVATION: " + observation);

                // ✅ EARLY EXIT (smart)
                if (observation instanceof Map obsMap) {

                    String type = (String) obsMap.get("type");

                    boolean isOrderIntent = userInput.toLowerCase().matches(".*(buy|order|purchase).*");

                    if ("PRODUCT_SEARCH_RESULT".equals(type) && !isOrderIntent) {
                        return success(type, obsMap.get("data"), null);
                    }

                    if ("ORDER_SUCCESS".equals(type) || "ORDER_FAILED".equals(type)) {
                        return success(
                                type,
                                obsMap.get("data"),
                                (String) obsMap.getOrDefault("message", null)
                        );
                    }
                }

                System.out.println("Step " + step + " took "
                        + (System.currentTimeMillis() - start) + " ms");

            } catch (Exception e) {
                e.printStackTrace();
                return error("SYSTEM_ERROR", e.getMessage());
            }
        }

        return error("MAX_STEPS_EXCEEDED", "Agent stopped after max steps");
    }

    private AgentResponse success(String type, Object data, String message) {
        return new AgentResponse("success", type, data, message);
    }

    private AgentResponse error(String type, String message) {
        return new AgentResponse("error", type, null, message);
    }
}