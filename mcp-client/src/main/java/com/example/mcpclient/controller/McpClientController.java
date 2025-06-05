package com.example.mcpclient.controller;

import com.example.mcpclient.model.McpMessage;
import com.example.mcpclient.service.McpClientService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/mcp")
public class McpClientController {
    
    private final McpClientService mcpClientService;
    
    public McpClientController(McpClientService mcpClientService) {
        this.mcpClientService = mcpClientService;
    }
    
    @PostMapping("/connect")
    public CompletableFuture<String> connect(@RequestBody Map<String, String> request) {
        String serverUrl = request.get("serverUrl");
        return mcpClientService.connectToServer(serverUrl);
    }
    
    @PostMapping("/initialize")
    public CompletableFuture<McpMessage> initialize() {
        return mcpClientService.initialize();
    }
    
    @GetMapping("/tools")
    public CompletableFuture<McpMessage> listTools() {
        return mcpClientService.listTools();
    }
    
    @PostMapping("/tools/call")
    public CompletableFuture<McpMessage> callTool(@RequestBody Map<String, Object> request) {
        String toolName = (String) request.get("toolName");
        @SuppressWarnings("unchecked")
        Map<String, Object> arguments = (Map<String, Object>) request.get("arguments");
        return mcpClientService.callTool(toolName, arguments);
    }
    
    @PostMapping("/chat")
    public CompletableFuture<McpMessage> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        return mcpClientService.chatComplete(message);
    }
    
    @PostMapping("/disconnect")
    public String disconnect() {
        mcpClientService.disconnect();
        return "Disconnected";
    }
}