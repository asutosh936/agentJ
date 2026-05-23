package com.agent.agentj.tool;

import java.util.Map;

/**
 * Contract that every tool must implement.
 *
 * Spring will discover all beans that implement this interface,
 * and the ToolRegistry will wire them up automatically.
 * To add a new tool, just create a class, implement this interface,
 * and annotate it with @Component — nothing else needs to change.
 */
public interface AgentTool {

    /** The name Claude uses to call this tool (must be unique) */
    String getName();

    /** Human-readable description Claude uses to decide when to call this tool */
    String getDescription();

    /**
     * JSON Schema describing the tool's accepted inputs.
     * Claude reads this to know what arguments to pass.
     *
     * Example:
     * {
     *   "type": "object",
     *   "properties": { "expression": { "type": "string" } },
     *   "required": ["expression"]
     * }
     */
    Map<String, Object> getInputSchema();

    /**
     * Run the tool with the arguments Claude provided.
     *
     * @param input a map of argument name → value, matching the input schema
     * @return the result as a plain string, which Claude will read
     */
    String execute(Map<String, Object> input);
}
