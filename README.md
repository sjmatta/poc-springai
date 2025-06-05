# Spring AI MCP Integration POCs

[![Test Status](https://github.com/sjmatta/poc-springai/workflows/Test%20Spring%20AI%20MCP%20Integration/badge.svg)](https://github.com/sjmatta/poc-springai/actions)

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
cd poc-springai

# Set up development environment
make dev-setup
# Edit .env and add your Anthropic API key
```

### 2. Development with Makefile

```bash
# See all available commands
make help

# Run all tests
make test

# Start MCP Server (port 8080)
export ANTHROPIC_API_KEY="your-key-here"
make start-server

# Start MCP Client (port 8081) - in another terminal
export ANTHROPIC_API_KEY="your-key-here"  
make start-client

# Run full integration tests
export ANTHROPIC_API_KEY="your-key-here"
make integration-test
```

### 3. Manual Setup (Alternative)

```bash
cd mcp-server && mvn spring-boot:run  # Server on port 8080
cd mcp-client && mvn spring-boot:run  # Client on port 8081
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

```bash
# Run all tests
make test

# Test individual projects  
make test-server
make test-client

# Run full integration test
make integration-test

# Check project status
make status
```