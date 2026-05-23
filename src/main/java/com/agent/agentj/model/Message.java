package com.agent.agentj.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A single entry in the conversation history sent to Claude.
 *
 * The "role" is either "user" or "assistant".
 *
 * The "content" is the tricky part — Claude's API accepts two forms:
 *   1. A plain String  — e.g. "What is 347 * 19?"
 *   2. A List<ContentBlock> — for structured messages like tool calls and tool results
 *
 * Using Object lets Jackson serialize whichever type is actually stored,
 * producing the correct JSON in both cases.
 */
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message {

    private String role;

    /**
     * Either a String (simple user text) or List<ContentBlock>
     * (assistant tool calls, or user tool results).
     */
    private Object content;
}
