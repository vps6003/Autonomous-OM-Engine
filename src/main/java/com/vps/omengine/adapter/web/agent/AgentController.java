package com.vps.omengine.adapter.web.agent;

import com.vps.omengine.application.agent.dto.AgentRequest;
import com.vps.omengine.application.agent.service.AgentOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vps_agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentOrchestrator agentOrchestrator;

    @PostMapping("/au_agent")
    public ResponseEntity<?> process(@RequestBody AgentRequest request) {

        Object result = agentOrchestrator.process(request.input());
        return ResponseEntity.ok(result);
    }
}