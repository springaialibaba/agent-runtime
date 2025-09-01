package com.alibaba.cloud.ai.agent.runtime.sandbox.manager.test;


import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpSyncClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@DisplayName("Mcp Client Test")
public class McpTest {
    private static final Logger logger = LoggerFactory.getLogger(McpTest.class);

    @Autowired
    private List<McpSyncClient> mcpAsyncClients;

    @Test
    public void test(){
        mcpAsyncClients.forEach(item -> {

            logger.info("McpAsyncClient: {}", item.getClientInfo().name());

        });
    }
}
