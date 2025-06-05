.PHONY: help test test-server test-client build clean start-server start-client integration-test verify

# Default target
help: ## Show this help message
	@echo "Spring AI MCP Integration POC"
	@echo "Usage: make [target]"
	@echo ""
	@echo "Targets:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  %-20s %s\n", $$1, $$2}' $(MAKEFILE_LIST)

# Verify environment
verify: ## Verify Maven installation and environment
	@echo "Verifying Maven installation..."
	mvn --version
	@echo "Verifying Java version..."
	java --version

# Test targets
test: test-server test-client ## Run all tests

test-server: ## Run MCP Server tests
	@echo "Running MCP Server tests..."
	cd mcp-server && mvn clean test

test-client: ## Run MCP Client tests  
	@echo "Running MCP Client tests..."
	cd mcp-client && mvn clean test

# Build targets
build: build-server build-client ## Build both projects

build-server: ## Build MCP Server
	@echo "Building MCP Server..."
	cd mcp-server && mvn clean compile

build-client: ## Build MCP Client
	@echo "Building MCP Client..."
	cd mcp-client && mvn clean compile

# Clean targets
clean: clean-server clean-client ## Clean all build artifacts

clean-server: ## Clean MCP Server build artifacts
	@echo "Cleaning MCP Server..."
	cd mcp-server && mvn clean

clean-client: ## Clean MCP Client build artifacts
	@echo "Cleaning MCP Client..."
	cd mcp-client && mvn clean

# Runtime targets
start-server: ## Start MCP Server (requires ANTHROPIC_API_KEY)
	@echo "Starting MCP Server on port 8080..."
	@if [ -z "$$ANTHROPIC_API_KEY" ]; then \
		echo "Warning: ANTHROPIC_API_KEY not set"; \
	fi
	cd mcp-server && mvn spring-boot:run

start-client: ## Start MCP Client (requires ANTHROPIC_API_KEY)
	@echo "Starting MCP Client on port 8081..."
	@if [ -z "$$ANTHROPIC_API_KEY" ]; then \
		echo "Warning: ANTHROPIC_API_KEY not set"; \
	fi
	cd mcp-client && mvn spring-boot:run

# Integration testing
integration-test: ## Run full integration test suite
	@echo "Running integration tests..."
	@if [ -z "$$ANTHROPIC_API_KEY" ]; then \
		echo "ERROR: ANTHROPIC_API_KEY environment variable is required"; \
		echo "Please set it before running: export ANTHROPIC_API_KEY='your-key-here'"; \
		exit 1; \
	fi
	./integration-test/test-integration.sh

# Health check (for CI/CD)
health-check: ## Wait for server to be healthy
	@echo "Waiting for server health check..."
	@timeout 30 bash -c 'until curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; do sleep 2; done' || echo "Health check failed"

# Development workflow
dev-setup: ## Set up development environment
	@echo "Setting up development environment..."
	@if [ ! -f .env ]; then \
		cp .env.example .env; \
		echo "Created .env file from .env.example"; \
		echo "Please edit .env and add your ANTHROPIC_API_KEY"; \
	else \
		echo ".env file already exists"; \
	fi

# CI/CD targets (used by GitHub Actions)
ci-test: verify test ## CI test pipeline
	@echo "CI test pipeline completed successfully"

ci-integration: ## CI integration test pipeline  
	@echo "Starting CI integration test pipeline..."
	$(MAKE) test-server &
	$(MAKE) test-client &
	wait
	@echo "Running server in background for integration tests..."
	cd mcp-server && mvn spring-boot:run > ../server.log 2>&1 & echo $$! > ../server.pid
	sleep 30
	$(MAKE) health-check
	cd mcp-client && mvn test -Dtest=McpClientIntegrationTest
	@echo "Cleaning up background processes..."
	@if [ -f server.pid ]; then kill `cat server.pid` 2>/dev/null || true; rm server.pid; fi

# Show project status
status: ## Show project status and running processes
	@echo "Project Status:"
	@echo "==============="
	@echo "Maven version:"
	@mvn --version | head -1 || echo "Maven not found"
	@echo ""
	@echo "Java version:"
	@java --version | head -1 || echo "Java not found"
	@echo ""
	@echo "Running processes:"
	@ps aux | grep -E "(spring-boot|mvn)" | grep -v grep || echo "No Spring Boot processes running"
	@echo ""
	@echo "Environment:"
	@echo "ANTHROPIC_API_KEY: $${ANTHROPIC_API_KEY:+SET}"