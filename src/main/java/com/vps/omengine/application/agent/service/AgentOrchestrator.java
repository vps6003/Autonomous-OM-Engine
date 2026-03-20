package com.vps.omengine.application.agent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vps.omengine.application.agent.llm.LlmClient;
import com.vps.omengine.application.agent.tool.ToolExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgentOrchestrator {

    private final LlmClient llmClient;
    private final ToolExecutor toolExecutor;

    private final ObjectMapper mapper = new ObjectMapper();

    public String process(String userInput) {

        String context = userInput;
        String observation = "";

        for (int step = 1; step <= 5; step++) {

            try {

                String llmOutput = llmClient.callAgent(context, observation);
                JsonNode root = mapper.readTree(llmOutput);

                String tool = root.get("tool").asText();

                // 🔥 HARD GUARD: invalid tool → STOP
                if (!tool.equals("search_products") &&
                        !tool.equals("create_order") &&
                        !tool.equals("final_answer")) {

                    return "Agent failed: invalid tool → " + tool;
                }

                // ✅ FINAL ANSWER
                if ("final_answer".equals(tool)) {
                    return root.get("input").asText();
                }

                // 🔥 EXECUTE TOOL
                observation = toolExecutor.execute(tool, root.get("input"));

                // 🔥 CRITICAL STOP CONDITION
                if (observation.contains("ORDER_SUCCESS") ||
                        observation.contains("ORDER_FAILED")) {

                    return observation;
                }

                context = userInput;

            } catch (Exception e) {
                e.printStackTrace();
                return "Agent failed: " + e.getMessage();
            }
        }

        return "Agent stopped after max steps";
    }
}