# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Building and Testing
```bash
# Test individual projects
cd mcp-server && mvn test
cd mcp-client && mvn test

# Run full integration test (requires ANTHROPIC_API_KEY)
export ANTHROPIC_API_KEY="your-key-here"
./integration-test/test-integration.sh

# Test with GitHub Actions locally
act -s ANTHROPIC_API_KEY="your-key-here"
```

### Running the Applications
```bash
# Terminal 1 - Start MCP Server (port 8080)
cd mcp-server
mvn spring-boot:run

# Terminal 2 - Start MCP Client (port 8081) 
cd mcp-client
mvn spring-boot:run

# Manual testing via REST API
curl -X POST http://localhost:8081/api/mcp/connect \
  -H "Content-Type: application/json" \
  -d '{"serverUrl": "ws://localhost:8080/mcp"}'
```

## Architecture Overview

This is a dual-project POC demonstrating Model Context Protocol (MCP) integration:

### MCP Server (`mcp-server/`)
- **WebSocket Handler**: `McpWebSocketHandler` implements MCP JSON-RPC 2.0 protocol over WebSocket at `/mcp`
- **Protocol Methods**: Handles `initialize`, `tools/list`, `tools/call`, and `chat/complete` MCP methods
- **AI Service**: `SpringAiService` provides POC-level AI responses (currently mock, designed for Spring AI integration)
- **Tool**: Exposes `spring_ai_chat` tool for AI interaction

### MCP Client (`mcp-client/`)
- **REST Controller**: `McpClientController` at `/api/mcp/*` provides HTTP interface to MCP operations
- **Client Service**: `McpClientService` simulates MCP client functionality (WebSocket connection planned)
- **Bridge Pattern**: Converts REST calls to MCP protocol messages

### Message Flow
1. Client receives HTTP requests via REST API
2. Client converts to MCP JSON-RPC 2.0 messages
3. Server processes MCP protocol methods via WebSocket
4. Server delegates tool calls to SpringAiService
5. Response flows back through the same chain

### Key Files for MCP Protocol
- `McpMessage.java`: Shared JSON-RPC 2.0 message structure
- `McpWebSocketHandler.java`: Server-side protocol implementation  
- `McpClientService.java`: Client-side protocol handling

## Environment Setup

Copy `.env.example` to `.env` and configure:
- `ANTHROPIC_API_KEY`: Required for AI functionality
- Ports: Server (8080), Client (8081)

## Integration Testing

The `integration-test/test-integration.sh` script:
1. Starts both server and client
2. Tests all MCP protocol endpoints
3. Validates WebSocket connectivity
4. Provides detailed logging output