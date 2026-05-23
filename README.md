# agentJ — Tool-Using AI Agent in Java

A beginner-friendly Spring Boot agent that uses Claude to answer questions,
calling real Java tools when it needs to compute or look something up.

## How it works

```
User prompt
    │
    ▼
┌─────────────────────────────────────────────────────┐
│                    Agent Loop                        │
│                                                     │
│  1. Send history + tools → Claude                   │
│                                                     │
│  2a. stop_reason = "end_turn"  → return answer ──► │
│                                                     │
│  2b. stop_reason = "tool_use"                       │
│       ├─ run requested Java tool(s)                 │
│       ├─ add results to history                     │
│       └─ go back to step 1                          │
└─────────────────────────────────────────────────────┘
```

## Project structure

```
src/main/java/com/agent/agentj/
├── AgentJApplication.java          ← Spring Boot entry point
├── config/AppConfig.java           ← RestTemplate bean
├── controller/AgentController.java ← REST endpoints
├── dto/                            ← Request/Response records
├── model/                          ← Claude API data models
├── service/
│   ├── ClaudeClient.java           ← HTTP calls to Claude API
│   └── AgentService.java           ← The agent loop
└── tool/
    ├── AgentTool.java              ← Tool interface
    ├── ToolRegistry.java           ← Auto-discovers all tools
    └── impl/
        ├── CalculatorTool.java     ← Evaluates math expressions
        └── ClockTool.java          ← Returns current date/time
```

## Setup

**1. Get an Anthropic API key** from https://console.anthropic.com

**2. Set the key as an environment variable:**
```bash
export ANTHROPIC_API_KEY=sk-ant-your-key-here
```

**3. Run the app:**
```bash
mvn spring-boot:run
```

## Usage

**Ask a math question** (Claude will call the calculator tool):
```bash
curl -X POST http://localhost:8080/api/agent/chat \
     -H "Content-Type: application/json" \
     -d '{"prompt": "What is 1234 * 5678?"}'
```

**Ask for the time** (Claude will call the clock tool):
```bash
curl -X POST http://localhost:8080/api/agent/chat \
     -H "Content-Type: application/json" \
     -d '{"prompt": "What time is it in Tokyo?"}'
```

**Ask a general question** (Claude answers directly, no tool needed):
```bash
curl -X POST http://localhost:8080/api/agent/chat \
     -H "Content-Type: application/json" \
     -d '{"prompt": "What is the capital of France?"}'
```

**List available tools:**
```bash
curl http://localhost:8080/api/agent/tools
```

## Adding a new tool

1. Create a class in `tool/impl/` that implements `AgentTool`
2. Annotate it with `@Component`
3. That's it — the `ToolRegistry` auto-discovers and registers it
