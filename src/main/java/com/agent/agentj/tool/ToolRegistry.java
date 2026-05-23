package com.agent.agentj.tool;

import com.agent.agentj.model.ToolDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Central registry that holds all available tools.
 *
 * Spring automatically collects every bean that implements AgentTool
 * and injects them as a List here. This means adding a new tool is
 * as simple as creating a new @Component class — the registry picks
 * it up with zero configuration changes.
 */
@Slf4j
@Component
public class ToolRegistry {

    private final Map<String, AgentTool> tools;

    public ToolRegistry(List<AgentTool> toolList) {
        this.tools = toolList.stream()
                .collect(Collectors.toMap(AgentTool::getName, Function.identity()));
        log.info("Registered tools: {}", tools.keySet());
    }

    /**
     * Run a tool by name with the given arguments.
     *
     * @param name  the tool name Claude used in its tool_use block
     * @param input the arguments Claude provided
     * @return the tool's result as a plain string
     */
    public String execute(String name, Map<String, Object> input) {
        AgentTool tool = tools.get(name);
        if (tool == null) {
            return "Error: no tool named '" + name + "' is registered.";
        }
        return tool.execute(input);
    }

    /** Returns the list of tool definitions to include in each Claude API request */
    public List<ToolDefinition> getDefinitions() {
        return tools.values().stream()
                .map(t -> new ToolDefinition(t.getName(), t.getDescription(), t.getInputSchema()))
                .toList();
    }
}
