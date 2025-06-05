package com.example.mcpclient;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.ai.anthropic.api-key=test-key"
})
class McpClientIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    void contextLoads() {
        assertTrue(true);
    }

    @Test
    void clientStartsSuccessfully() {
        assertTrue(port > 0);
    }
}