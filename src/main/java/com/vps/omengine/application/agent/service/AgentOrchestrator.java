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

        try {
            System.out.println("\n[Agent] Goal: " + userInput);

            String context = userInput;
            String observation = "";

            // 🔥 AGENT LOOP
            for (int step = 1; step <= 5; step++) {

                System.out.println("\n[Agent] Step " + step);

                String llmOutput = llmClient.callAgent(context, observation);

                JsonNode root = mapper.readTree(llmOutput);

                String tool = root.get("tool").asText();

                System.out.println("[Agent] Decision: " + tool);

                // ✅ FINAL ANSWER
                if ("final_answer".equals(tool)) {
                    return root.get("input").asText();
                }

                // 🔥 EXECUTE TOOL
                observation = toolExecutor.execute(tool, root.get("input"));

                System.out.println("[Agent] Observation: " + observation);

                // 🔁 FEEDBACK LOOP
                context = context + "\nObservation: " + observation;
            }

            return "Agent stopped after max steps";

        } catch (Exception e) {
            e.printStackTrace();
            return "Agent failed: " + e.getMessage();
        }
    }
}