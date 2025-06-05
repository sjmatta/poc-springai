package com.example.mcpserver.service;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class SpringAiService {
    
    private final AnthropicChatModel chatModel;
    
    public SpringAiService(AnthropicChatModel chatModel) {
        this.chatModel = chatModel;
    }
    
    public String generateResponse(String userMessage) {
        Prompt prompt = new Prompt(userMessage);
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getContent();
    }
    
    public String processToolCall(String toolName, Object parameters) {
        String promptText = String.format("Execute tool '%s' with parameters: %s", toolName, parameters.toString());
        return generateResponse(promptText);
    }
}