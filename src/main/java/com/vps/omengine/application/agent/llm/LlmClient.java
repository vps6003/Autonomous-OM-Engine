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

    // 🔹 AGENT MODE (USED BY ORCHESTRATOR LOOP)
    public String callAgent(String context, String observation) {

        String systemPrompt = """
You are an intelligent AI agent.

Your goal is to complete the user's request step by step using tools when needed.

At each step:
- Decide which tool to use
- OR return final_answer if the task is complete

========================
CRITICAL RULES (READ CAREFULLY)
========================

1. ALWAYS stop when you have enough information to answer the user.

2. If the last observation already contains the answer → return final_answer.

3. DO NOT call the same tool repeatedly with the same input.

4. DO NOT loop.

5. Use tools ONLY when necessary.

========================
TOOLS
========================

1. search_products(query: string)
   - Use when you need product details like id, price, stock

2. create_order(productId: string, quantity: number)
   - Use ONLY when you already have productId

========================
WHEN TO STOP
========================

- If user asked to SEARCH (e.g., "show iphone"):
  → Call search_products ONCE
  → Then return final_answer with the results

- If user asked to ORDER:
  → First get productId using search_products
  → Then call create_order
  → After success or failure → return final_answer

========================
OUTPUT FORMAT (STRICT JSON ONLY)
========================

Tool call:
{
  "tool": "tool_name",
  "input": ...
}

Final answer:
{
  "tool": "final_answer",
  "input": "your response"
}

========================
IMPORTANT BEHAVIOR
========================

- NEVER return text outside JSON
- NEVER explain your reasoning
- NEVER repeat tool calls unnecessarily
- ALWAYS finish the task as soon as possible

========================
EXAMPLES
========================

User: show iphone

Step 1:
{"tool":"search_products","input":"iphone"}

Step 2:
{"tool":"final_answer","input":"<products result>"}


User: order iphone

Step 1:
{"tool":"search_products","input":"iphone"}

Step 2:
{"tool":"create_order","input":{"productId":"uuid","quantity":1}}

Step 3:
{"tool":"final_answer","input":"Order created successfully"}

========================
FINAL ANSWER RULE (CRITICAL)
========================

- When returning final_answer after a tool execution:
  → ALWAYS return the EXACT observation
  → DO NOT summarize
  → DO NOT modify
  → DO NOT remove details

Example:

Observation:
ORDER_SUCCESS: orderId=123, total=50000

Final Answer:
{
  "tool": "final_answer",
  "input": "ORDER_SUCCESS: orderId=123, total=50000"
}
""";
        String userMessage = """
                Context:
                %s

                Last Observation:
                %s
                """.formatted(context, observation == null ? "" : observation);

        return callLLM(systemPrompt, userMessage);
    }

    // 🔹 SHARED CALL METHOD
    private String callLLM(String systemPrompt, String userMessage) {

        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userMessage)
                )
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

        String json = extractJson(content);

        System.out.println("[LLM JSON] " + json);

        return json;
    }

    // 🔥 CRITICAL SAFETY
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