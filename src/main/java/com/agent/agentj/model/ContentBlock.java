package com.agent.agentj.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * A single block within a message's content array.
 *
 * Claude uses three block types:
 *   "text"        — a plain text response from the assistant
 *   "tool_use"    — Claude wants to call a tool (has id, name, input)
 *   "tool_result" — we're returning the result of a tool call (has tool_use_id, content)
 *
 * We use @JsonInclude(NON_NULL) so null fields are omitted when serializing to JSON,
 * keeping the request payload clean.
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContentBlock {

    /** "text", "tool_use", or "tool_result" */
    private String type;

    /** For "text" blocks: the actual text */
    private String text;

    /** For "tool_use" blocks: the unique ID Claude assigns to this call */
    private String id;

    /** For "tool_use" blocks: which tool to run */
    private String name;

    /** For "tool_use" blocks: the arguments Claude wants to pass to the tool */
    private Map<String, Object> input;

    /** For "tool_result" blocks: which tool_use call this result belongs to */
    @JsonProperty("tool_use_id")
    private String toolUseId;

    /** For "tool_result" blocks: the string result returned by the tool */
    private String content;

    /** Factory: build a tool_result block to send back to Claude */
    public static ContentBlock toolResult(String toolUseId, String result) {
        ContentBlock block = new ContentBlock();
        block.setType("tool_result");
        block.setToolUseId(toolUseId);
        block.setContent(result);
        return block;
    }
}
