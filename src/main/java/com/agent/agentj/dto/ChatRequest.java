package com.agent.agentj.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body for POST /api/agent/chat
 *
 * Using a Java record (Java 16+) — immutable, compact, no boilerplate.
 */
public record ChatRequest(
        @NotBlank(message = "Prompt must not be blank")
        String prompt
) {}
