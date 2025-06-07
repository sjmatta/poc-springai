package com.example.mcpserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.ai.anthropic.api-key=test-key"
})
class McpServerIntegrationTest {

    @LocalServerPort
    private int port;
    
    @Autowired
    private ApplicationContext context;
    
    private final RestTemplate restTemplate = new RestTemplateBuilder().build();

    @Test
    void contextLoads() {
        assertNotNull(context);
        assertTrue(context.containsBean("mcpWebSocketHandler"));
        assertTrue(context.containsBean("springAiService"));
    }

    @Test
    void serverStartsSuccessfully() throws Exception {
        assertTrue(port > 0, "Server should start on a valid port");
        
        // Test health endpoint
        String healthUrl = "http://localhost:" + port + "/actuator/health";
        String healthResponse = restTemplate.getForObject(healthUrl, String.class);
        assertNotNull(healthResponse);
        assertTrue(healthResponse.contains("UP"), "Health endpoint should return UP status");
    }
    
    @Test
    void webSocketEndpointIsAccessible() throws Exception {
        URI serverUri = new URI("ws://localhost:" + port + "/mcp");
        assertNotNull(serverUri);
        assertEquals("/mcp", serverUri.getPath());
        assertEquals(port, serverUri.getPort());
    }
}