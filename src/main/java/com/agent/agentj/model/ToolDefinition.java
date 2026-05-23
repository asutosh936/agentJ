package com.agent.agentj.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * Describes a tool to Claude so it knows the tool exists,
 * what it does, and what arguments it accepts.
 *
 * The "input_schema" is a JSON Schema object — Claude uses it to
 * understand and validate the arguments it should pass to the tool.
 */
@Data
@AllArgsConstructor
public class ToolDefinition {

    private String name;

    private String description;

    /** JSON Schema describing the tool's input parameters */
    @JsonProperty("input_schema")
    private Map<String, Object> inputSchema;
}
