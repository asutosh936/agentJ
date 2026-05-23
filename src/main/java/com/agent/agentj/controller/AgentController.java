package com.agent.agentj.controller;

import com.agent.agentj.dto.ChatRequest;
import com.agent.agentj.dto.ChatResponse;
import com.agent.agentj.model.ToolDefinition;
import com.agent.agentj.service.AgentService;
import com.agent.agentj.tool.ToolRegistry;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API surface for the agent.
 *
 * Endpoints:
 *   POST /api/agent/chat   — send a prompt, get the agent's answer
 *   GET  /api/agent/tools  — list all registered tools and their schemas
 */
@Slf4j
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;
    private final ToolRegistry toolRegistry;

    /**
     * Main endpoint — runs the full agent loop and returns the final answer.
     *
     * Example:
     *   curl -X POST http://localhost:8080/api/agent/chat \
     *        -H "Content-Type: application/json" \
     *        -d '{"prompt": "What is 1234 * 5678?"}'
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        log.info("Received prompt: {}", request.prompt());
        String answer = agentService.chat(request.prompt());
        return ResponseEntity.ok(new ChatResponse(answer));
    }

    /**
     * Utility endpoint — shows all tools the agent can use.
     * Helpful for debugging and understanding what the agent is capable of.
     *
     * Example:
     *   curl http://localhost:8080/api/agent/tools
     */
    @GetMapping("/tools")
    public ResponseEntity<List<ToolDefinition>> listTools() {
        return ResponseEntity.ok(toolRegistry.getDefinitions());
    }
}
