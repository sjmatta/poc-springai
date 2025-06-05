# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Makefile Commands (Recommended)
```bash
# See all available commands
make help

# Testing
make test                # Run all tests
make test-server         # Test MCP Server only
make test-client         # Test MCP Client only  
make integration-test    # Full integration test (requires ANTHROPIC_API_KEY)

# Development
make dev-setup          # Set up .env file
make build              # Build both projects
make clean              # Clean build artifacts
make verify             # Verify Maven/Java setup

# Running applications
export ANTHROPIC_API_KEY="your-key-here"
make start-server       # Start MCP Server (port 8080)
make start-client       # Start MCP Client (port 8081)

# CI/CD
make ci-test            # CI test pipeline
make ci-integration     # CI integration pipeline
make status             # Show project status
```

### Manual Commands (Alternative)
```bash
# Testing
cd mcp-server && mvn test
cd mcp-client && mvn test
./integration-test/test-integration.sh

# Running
cd mcp-server && mvn spring-boot:run  # port 8080
cd mcp-client && mvn spring-boot:run  # port 8081

# Local GitHub Actions testing
act -s ANTHROPIC_API_KEY="your-key-here"
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