# Spring AI MCP Integration POCs

This repository contains two Proof of Concept (POC) projects demonstrating Model Context Protocol (MCP) integration with Spring AI:

1. **MCP Server** - A Spring AI MCP Server that exposes AI capabilities via MCP protocol
2. **MCP Client** - A Spring AI client that connects to and uses the MCP Server

## Project Structure

```
springai/
├── mcp-server/          # Spring AI MCP Server
├── mcp-client/          # Spring AI MCP Client  
├── integration-test/    # Integration test scripts
└── .github/workflows/   # GitHub Actions CI/CD
```

## Prerequisites

- Java 17+
- Maven 3.6+
- Anthropic API Key

## Quick Start

### 1. Clone and Setup

```bash
git clone <your-repo>
cd springai

# Set up environment variables
cp .env.example .env
# Edit .env and add your Anthropic API key
```

### 2. Start MCP Server

```bash
cd mcp-server
# Make sure ANTHROPIC_API_KEY is set in your environment
mvn spring-boot:run
```

The server will start on `http://localhost:8080` with WebSocket endpoint at `ws://localhost:8080/mcp`

### 3. Start MCP Client

```bash
cd mcp-client
# Make sure ANTHROPIC_API_KEY is set in your environment
mvn spring-boot:run
```

The client will start on `http://localhost:8081`

### 4. Test Integration

Run the integration test script:

```bash
# Set your API key first
export ANTHROPIC_API_KEY="your-key-here"
./integration-test/test-integration.sh
```

Or test manually using the client API:

```bash
# Connect to server
curl -X POST http://localhost:8081/api/mcp/connect \
  -H "Content-Type: application/json" \
  -d '{"serverUrl": "ws://localhost:8080/mcp"}'

# Initialize connection
curl -X POST http://localhost:8081/api/mcp/initialize

# List available tools
curl -X GET http://localhost:8081/api/mcp/tools

# Send a chat message
curl -X POST http://localhost:8081/api/mcp/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello, tell me about Spring AI!"}'
```

## MCP Server Features

- WebSocket-based MCP protocol implementation
- Spring AI integration with Anthropic Claude
- Tool support for AI chat functionality
- JSON-RPC 2.0 message handling

## MCP Client Features

- WebSocket client for MCP protocol
- REST API for easy testing
- Connection management
- Tool calling capabilities

## GitHub Actions

The project includes a GitHub Actions workflow (`.github/workflows/test.yml`) that:

- Runs unit tests for both projects
- Starts the server and tests connectivity
- Can be triggered with the `act` tool for local testing

To run with `act`:

```bash
# Install act if not already installed
# brew install act  # macOS
# or follow instructions at https://github.com/nektos/act

# Run the workflow locally  
act -s ANTHROPIC_API_KEY="your-api-key-here"
```

## Environment Variables

- `ANTHROPIC_API_KEY`: Your Anthropic API key (required)

## Architecture

The implementation follows the MCP specification:

1. **Server** exposes AI capabilities via WebSocket using JSON-RPC 2.0
2. **Client** connects to server and provides REST API for interaction
3. **Protocol** handles initialization, tool listing, and execution

## Testing

Run tests for individual projects:

```bash
# Test server
cd mcp-server && mvn test

# Test client  
cd mcp-client && mvn test
```

Or run the full integration test:

```bash
./integration-test/test-integration.sh
```