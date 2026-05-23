package com.agent.agentj.service;

import com.agent.agentj.model.ClaudeResponse;
import com.agent.agentj.model.ContentBlock;
import com.agent.agentj.model.Message;
import com.agent.agentj.model.ToolDefinition;
import com.agent.agentj.tool.ToolRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The agent loop — the core of the project.
 *
 * Algorithm (runs until Claude produces a plain-text answer):
 *
 *   1. Add the user's prompt to the message history.
 *   2. Call Claude with the full history and available tools.
 *   3. If Claude responds with stop_reason = "end_turn"  → return the text.
 *   4. If Claude responds with stop_reason = "tool_use":
 *        a. Add Claude's response (with tool_use blocks) to the history.
 *        b. For every tool_use block, run the requested tool.
 *        c. Add all tool results back as a "user" message.
 *        d. Go to step 2.
 *
 * This loop is why it's called an "agent" — it doesn't produce a single answer;
 * it reasons, acts, observes, and reasons again until it's done.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentService {

    private final ClaudeClient claudeClient;
    private final ToolRegistry toolRegistry;

    /** Maximum tool-call rounds to prevent infinite loops */
    private static final int MAX_ITERATIONS = 10;

    /**
     * Run the agent loop for a single user prompt.
     *
     * @param userPrompt the user's question or instruction
     * @return Claude's final text answer
     */
    public String chat(String userPrompt) {
        List<Message> history = new ArrayList<>();
        history.add(new Message("user", userPrompt));

        List<ToolDefinition> tools = toolRegistry.getDefinitions();

        for (int iteration = 1; iteration <= MAX_ITERATIONS; iteration++) {
            log.info("--- Agent iteration {} ---", iteration);

            ClaudeResponse response = claudeClient.sendMessages(history, tools);

            if (response == null || response.getContent() == null) {
                throw new RuntimeException("Received an empty response from Claude.");
            }

            String stopReason = response.getStopReason();
            log.info("Claude stop_reason: {}", stopReason);

            // ----------------------------------------------------------------
            // Case 1: Claude has a final answer — extract and return the text
            // ----------------------------------------------------------------
            if ("end_turn".equals(stopReason)) {
                return response.getContent().stream()
                        .filter(block -> "text".equals(block.getType()))
                        .map(ContentBlock::getText)
                        .collect(Collectors.joining("\n"))
                        .trim();
            }

            // ----------------------------------------------------------------
            // Case 2: Claude wants to use tools
            // ----------------------------------------------------------------
            if ("tool_use".equals(stopReason)) {

                // Step 2a — record Claude's full response (including tool_use blocks) in history
                history.add(new Message("assistant", response.getContent()));

                // Step 2b & 2c — run each requested tool and collect results
                List<ContentBlock> toolResults = new ArrayList<>();
                for (ContentBlock block : response.getContent()) {
                    if ("tool_use".equals(block.getType())) {
                        log.info("Claude calls tool '{}' with input: {}", block.getName(), block.getInput());
                        String result = toolRegistry.execute(block.getName(), block.getInput());
                        log.info("Tool '{}' returned: {}", block.getName(), result);
                        toolResults.add(ContentBlock.toolResult(block.getId(), result));
                    }
                }

                // Step 2d — add tool results back as a user message and loop
                history.add(new Message("user", toolResults));
                continue;
            }

            // Unexpected stop reason — treat as an error
            log.warn("Unexpected stop_reason: {}", stopReason);
            break;
        }

        return "The agent reached the maximum number of iterations without producing a final answer.";
    }
}
