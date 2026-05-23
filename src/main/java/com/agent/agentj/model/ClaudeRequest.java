package com.agent.agentj.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * The JSON body sent to the Claude Messages API (POST /v1/messages).
 *
 * Key fields:
 *   model      — which Claude model to use
 *   max_tokens — upper limit on the response length (required by the API)
 *   tools      — list of tools Claude can call
 *   messages   — the full conversation history so far
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClaudeRequest {

    private String model;

    @JsonProperty("max_tokens")
    private int maxTokens;

    private List<ToolDefinition> tools;

    private List<Message> messages;
}
