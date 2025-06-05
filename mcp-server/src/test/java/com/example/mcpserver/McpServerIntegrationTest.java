package com.example.mcpserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.ai.anthropic.api-key=test-key"
})
class McpServerIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    void contextLoads() {
        assertTrue(true);
    }

    @Test
    void serverStartsSuccessfully() throws Exception {
        URI serverUri = new URI("ws://localhost:" + port + "/mcp");
        assertNotNull(serverUri);
        assertTrue(port > 0);
    }
}