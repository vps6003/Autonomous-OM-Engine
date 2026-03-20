package com.vps.omengine.application.agent.llm;

import com.vps.omengine.config.GroqProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LlmClient {

    private final GroqProperties groqProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String URL = "https://api.groq.com/openai/v1/chat/completions";

    public String callAgent(String context, String observation) {

        String systemPrompt = """
                You are an AI agent.
                
                ========================
                STRICT RULES
                ========================
                1. Return ONLY valid JSON.
                2. No explanations.
                3. Follow intent strictly.
                
                ========================
                INTENT DETECTION
                ========================
                
                If user wants to SEE or SEARCH product:
                → ONLY call search_products
                → NEVER call create_order
                
                If user wants to ORDER / BUY product:
                → first call search_products
                → then call create_order
                
                ========================
                STATE LOGIC
                ========================
                
                IF no observation:
                → decide based on intent
                
                IF observation contains PRODUCT_SEARCH_RESULT:
                → ONLY call create_order IF intent = ORDER
                → ELSE return final_answer
                
                IF observation contains ORDER_SUCCESS or ORDER_FAILED:
                → return final_answer EXACTLY
                
                ========================
                TOOLS
                ========================
                search_products(query)
                create_order(productId, quantity)
                
                ========================
                OUTPUT FORMAT
                ========================
                {
                  "tool": "...",
                  "input": ...
                }
                """;

        String userMessage = """
                USER_INPUT:
                %s
                
                OBSERVATION:
                %s
                """.formatted(context, observation == null ? "" : observation);

        return callLLM(systemPrompt, userMessage);
    }

    private String callLLM(String systemPrompt, String userMessage) {

        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userMessage)
                ),
                "temperature", 0,
                "max_tokens", 150
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(groqProperties.getApiKey());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(URL, entity, Map.class);

        String content = (String) ((Map) ((Map) ((List)
                response.getBody().get("choices")).get(0))
                .get("message")).get("content");

        System.out.println("[LLM RAW] " + content);

        return extractJson(content);
    }

    private String extractJson(String response) {
        if (response == null) return "{}";

        int start = response.indexOf("{");
        int end = response.lastIndexOf("}");

        if (start != -1 && end != -1 && start < end) {
            return response.substring(start, end + 1).trim();
        }

        return "{}";
    }
}