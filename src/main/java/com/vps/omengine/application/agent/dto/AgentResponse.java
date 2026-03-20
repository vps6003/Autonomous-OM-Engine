package com.vps.omengine.application.agent.dto;

public record AgentResponse(
        String status,
        String type,
        Object data,
        String message
) {
}