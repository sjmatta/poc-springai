package com.example.mcpclient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.ai.anthropic.api-key=test-key"
})
class McpClientIntegrationTest {

    @LocalServerPort
    private int port;
    
    @Autowired
    private ApplicationContext context;
    
    private final RestTemplate restTemplate = new RestTemplateBuilder().build();

    @Test
    void contextLoads() {
        assertNotNull(context);
        assertTrue(context.containsBean("mcpClientController"));
        assertTrue(context.containsBean("mcpClientService"));
    }

    @Test
    void clientStartsSuccessfully() {
        assertTrue(port > 0, "Client should start on a valid port");
    }
    
    @Test
    void restEndpointsAreAccessible() {
        String baseUrl = "http://localhost:" + port;
        
        // Test that the controller endpoints are mapped
        // Note: These will return errors without a server, but endpoints should be accessible
        try {
            restTemplate.postForObject(baseUrl + "/api/mcp/initialize", null, String.class);
        } catch (Exception e) {
            // Expected to fail without server connection, but endpoint should be accessible
            assertTrue(e.getMessage().contains("500") || e.getMessage().contains("connection"));
        }
    }
}