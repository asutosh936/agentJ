package com.agent.agentj.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * The JSON response received from the Claude Messages API.
 *
 * The most important field is "stop_reason":
 *   "end_turn"  — Claude finished and has a text answer
 *   "tool_use"  — Claude wants to call one or more tools before continuing
 *
 * @JsonIgnoreProperties(ignoreUnknown = true) tells Jackson to silently skip
 * any fields in the JSON that we haven't mapped here — safe for API responses.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClaudeResponse {

    private String id;

    private String type;

    private String role;

    /** The content blocks in this response (text and/or tool_use blocks) */
    private List<ContentBlock> content;

    /**
     * Why Claude stopped generating:
     *   "end_turn"  — has a final answer
     *   "tool_use"  — needs to run a tool
     */
    @JsonProperty("stop_reason")
    private String stopReason;

    private Usage usage;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usage {
        @JsonProperty("input_tokens")
        private int inputTokens;
        @JsonProperty("output_tokens")
        private int outputTokens;
    }
}
