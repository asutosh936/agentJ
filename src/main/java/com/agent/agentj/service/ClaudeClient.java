package com.agent.agentj.service;

import com.agent.agentj.model.ClaudeRequest;
import com.agent.agentj.model.ClaudeResponse;
import com.agent.agentj.model.Message;
import com.agent.agentj.model.ToolDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Thin wrapper around the Claude Messages API.
 *
 * Responsibility: build the HTTP request, send it, and return the parsed response.
 * The agent loop logic lives in AgentService — this class only handles I/O.
 */
@Slf4j
@Service
public class ClaudeClient {

    private final RestTemplate restTemplate;

    @Value("${anthropic.api.key}")
    private String apiKey;

    @Value("${anthropic.api.url:https://api.anthropic.com/v1/messages}")
    private String apiUrl;

    @Value("${anthropic.model:claude-sonnet-4-6}")
    private String model;

    @Value("${anthropic.max-tokens:1024}")
    private int maxTokens;

    public ClaudeClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Send the current conversation history to Claude and get its next response.
     *
     * @param messages the full conversation so far (user, assistant, tool results…)
     * @param tools    the tool definitions Claude can choose from
     * @return Claude's response (may contain text or tool_use blocks)
     */
    public ClaudeResponse sendMessages(List<Message> messages, List<ToolDefinition> tools) {
        ClaudeRequest request = ClaudeRequest.builder()
                .model(model)
                .maxTokens(maxTokens)
                .tools(tools)
                .messages(messages)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", "2023-06-01");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ClaudeRequest> entity = new HttpEntity<>(request, headers);

        log.debug("Calling Claude API with {} messages", messages.size());
        try {
            ResponseEntity<ClaudeResponse> responseEntity =
                    restTemplate.postForEntity(apiUrl, entity, ClaudeResponse.class);
            ClaudeResponse response = responseEntity.getBody();
            log.debug("Claude stop_reason={}, content blocks={}",
                    response != null ? response.getStopReason() : "null",
                    response != null && response.getContent() != null ? response.getContent().size() : 0);
            return response;
        } catch (HttpClientErrorException e) {
            log.error("Claude API error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Claude API returned an error: " + e.getStatusCode() + " — " + e.getResponseBodyAsString(), e);
        }
    }
}
