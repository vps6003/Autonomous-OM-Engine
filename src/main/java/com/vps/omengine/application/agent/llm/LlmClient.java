package com.vps.omengine.application.agent.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vps.omengine.config.GroqProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LlmClient {

    private final GroqProperties groqProperties;

    private static final String URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final int MAX_RETRIES = 2;

    private final ObjectMapper mapper = new ObjectMapper();

    private final RestTemplate restTemplate = createRestTemplate();

    private RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(5000);
        return new RestTemplate(factory);
    }

    public String callAgent(String context, String observation) {
        String systemPrompt = """
                You are an AI agent that MUST strictly follow instructions.
                
                ========================
                STRICT RULES
                ========================
                - Return ONLY valid JSON
                - No explanations
                - Use ONLY allowed tools
                - NEVER repeat the same tool unnecessarily
                - NEVER rewrite tool output
                
                ========================
                TOOLS
                ========================
                search_products → { "query": string }
                create_order → { "productId": string, "quantity": number }
                final_answer → { "message": string }
                
                ========================
                SEARCH QUERY RULES (VERY IMPORTANT)
                ========================
                - Extract ONLY the core product keyword
                - REMOVE numbers, versions, extra words
                - CONVERT plural → singular
                
                Examples:
                "iphones" → "iphone"
                "iphone 16" → "iphone"
                "show latest iphones" → "iphone"
                "buy iphone 15 pro" → "iphone"
                
                NEVER pass full sentence as query.
                
                BAD:
                "show iphones"
                "iphone 16 pro max"
                
                GOOD:
                "iphone"
                
                ========================
                DECISION FLOW (STRICT)
                ========================
                1. If no observation:
                   → decide tool
                
                2. If observation = PRODUCT_SEARCH_RESULT:
                   → If user wants to buy/order → call create_order
                   → Else → return final_answer
                
                3. If observation = ORDER_SUCCESS or ORDER_FAILED:
                   → return final_answer
                
                ========================
                RULES
                ========================
                - Call search_products ONLY ONCE
                - Do NOT loop
                - If unsure → return final_answer
                
                ========================
                EXAMPLE
                ========================
                
                User: show iphones
                
                Step 1:
                {
                  "tool": "search_products",
                  "input": { "query": "iphone" }
                }
                
                Step 2:
                {
                  "tool": "final_answer",
                  "input": "Here are the products"
                }
                """;

        String userMessage = """
                USER_INPUT:
                %s
                
                OBSERVATION:
                %s
                """.formatted(context, observation == null ? "" : observation);

        return callWithRetry(systemPrompt, userMessage);
    }

    private String callWithRetry(String systemPrompt, String userMessage) {

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {

            try {
                String response = callLLM(systemPrompt, userMessage);

                if (isValidJson(response)) {
                    return response;
                }

                System.out.println("[RETRY] Invalid JSON attempt " + attempt);

            } catch (Exception e) {
                System.out.println("[RETRY] Error: " + e.getMessage());
            }
        }

        return """
                {
                  "tool": "final_answer",
                  "input": "Something went wrong"
                }
                """;
    }

    private String callLLM(String systemPrompt, String userMessage) {

        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userMessage)
                ),
                "temperature", 0,
                "max_tokens", 200
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

        System.out.println("\n[LLM RAW]\n" + content);

        return extractJson(content);
    }

    private String extractJson(String response) {
        if (response == null || response.isBlank()) return "{}";

        int start = response.indexOf("{");
        int end = response.lastIndexOf("}");

        if (start != -1 && end != -1) {
            return response.substring(start, end + 1);
        }

        return "{}";
    }

    private boolean isValidJson(String json) {
        try {
            return mapper.readTree(json).has("tool");
        } catch (Exception e) {
            return false;
        }
    }
}