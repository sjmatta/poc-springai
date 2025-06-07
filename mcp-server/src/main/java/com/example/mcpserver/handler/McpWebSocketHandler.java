package com.example.mcpserver.handler;

import com.example.mcpserver.model.McpMessage;
import com.example.mcpserver.service.SpringAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@Component
public class McpWebSocketHandler extends TextWebSocketHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(McpWebSocketHandler.class);
    private static final String PROTOCOL_VERSION = "2024-11-05";
    
    private final SpringAiService springAiService;
    private final ObjectMapper objectMapper;
    
    public McpWebSocketHandler(SpringAiService springAiService) {
        this.springAiService = springAiService;
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("MCP WebSocket connection established: {}", session.getId());
        
        Map<String, Object> initResponse = new HashMap<>();
        initResponse.put("protocolVersion", PROTOCOL_VERSION);
        initResponse.put("capabilities", Map.of("tools", Map.of()));
        
        McpMessage response = new McpMessage();
        response.setId("init");
        response.setResult(initResponse);
        
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            McpMessage mcpMessage = objectMapper.readValue(message.getPayload(), McpMessage.class);
            
            McpMessage response = new McpMessage();
            response.setId(mcpMessage.getId());
            
            switch (mcpMessage.getMethod()) {
                case "initialize":
                    handleInitialize(response);
                    break;
                case "tools/list":
                    handleToolsList(response);
                    break;
                case "tools/call":
                    handleToolCall(response, mcpMessage.getParams());
                    break;
                case "chat/complete":
                    handleChatComplete(response, mcpMessage.getParams());
                    break;
                default:
                    response.setResult(Map.of("error", "Unknown method: " + mcpMessage.getMethod()));
            }
            
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            
        } catch (Exception e) {
            logger.error("Error handling WebSocket message from session {}: {}", session.getId(), e.getMessage(), e);
            sendErrorResponse(session, "internal_error", "Internal server error occurred");
        }
    }
    
    private void handleInitialize(McpMessage response) {
        Map<String, Object> result = new HashMap<>();
        result.put("protocolVersion", PROTOCOL_VERSION);
        result.put("capabilities", Map.of("tools", Map.of()));
        result.put("serverInfo", Map.of("name", "spring-ai-mcp-server", "version", "1.0.0"));
        response.setResult(result);
        logger.debug("MCP initialization completed");
    }
    
    private void handleToolsList(McpMessage response) {
        Map<String, Object> tool = new HashMap<>();
        tool.put("name", "spring_ai_chat");
        tool.put("description", "Chat with Spring AI using Anthropic Claude");
        tool.put("inputSchema", Map.of(
            "type", "object",
            "properties", Map.of(
                "message", Map.of("type", "string", "description", "The message to send to the AI")
            ),
            "required", new String[]{"message"}
        ));
        
        response.setResult(Map.of("tools", new Object[]{tool}));
    }
    
    private void handleToolCall(McpMessage response, Object params) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> paramMap = (Map<String, Object>) params;
            String toolName = (String) paramMap.get("name");
            @SuppressWarnings("unchecked")
            Map<String, Object> arguments = (Map<String, Object>) paramMap.get("arguments");
            
            if ("spring_ai_chat".equals(toolName)) {
                String message = (String) arguments.get("message");
                String aiResponse = springAiService.generateResponse(message);
                
                response.setResult(Map.of(
                    "content", new Object[]{
                        Map.of("type", "text", "text", aiResponse)
                    }
                ));
            } else {
                response.setResult(Map.of("error", "Unknown tool: " + toolName));
            }
        } catch (Exception e) {
            response.setResult(Map.of("error", "Error: " + e.getMessage()));
        }
    }
    
    private void handleChatComplete(McpMessage response, Object params) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> paramMap = (Map<String, Object>) params;
            @SuppressWarnings("unchecked")
            Object[] messages = (Object[]) paramMap.get("messages");
            
            if (messages != null && messages.length > 0) {
                @SuppressWarnings("unchecked")
                Map<String, Object> lastMessage = (Map<String, Object>) messages[messages.length - 1];
                String content = (String) lastMessage.get("content");
                
                String aiResponse = springAiService.generateResponse(content);
                
                response.setResult(Map.of(
                    "model", "anthropic-claude",
                    "message", Map.of(
                        "role", "assistant",
                        "content", aiResponse
                    )
                ));
            } else {
                response.setResult(Map.of("error", "No messages provided"));
            }
        } catch (Exception e) {
            response.setResult(Map.of("error", "Error: " + e.getMessage()));
        }
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("MCP WebSocket connection closed: {} with status: {}", session.getId(), status.toString());
    }
    
    private void sendErrorResponse(WebSocketSession session, String errorCode, String errorMessage) {
        try {
            McpMessage errorResponse = new McpMessage();
            errorResponse.setId("error");
            errorResponse.setResult(Map.of("error", Map.of("code", errorCode, "message", errorMessage)));
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(errorResponse)));
        } catch (Exception e) {
            logger.error("Failed to send error response: {}", e.getMessage(), e);
        }
    }
}