package com.alibaba.cloud.ai.agent.runtime.sandbox.manager.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Map;

@SpringBootTest
@DisplayName("Playwright MCP 功能测试")
public class PlaywrightMcpTest {

    private static final Logger logger = LoggerFactory.getLogger(PlaywrightMcpTest.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    private ToolCallbackProvider provider;


    @Test
    @DisplayName("Playwright MCP 基础功能测试")
    public void testPlaywrightMcp() throws JsonProcessingException {
        ToolCallback[] toolCallbacks = provider.getToolCallbacks();

        ToolCallback toolCallback = mapToolCallback("browser_navigate", toolCallbacks);
        Map<String, Object> params = Map.of(
                "url", "https://www.taobao.com"
        );
        String call = toolCallback.call(objectMapper.writeValueAsString(params));
        logger.info("浏览器导航结果: {}", call);
    }


    private ToolCallback mapToolCallback(String name, ToolCallback[] toolCallbacks) {
        return Arrays.stream(toolCallbacks)
                .filter(toolCallback -> toolCallback.getToolDefinition().name().equals("spring_ai_mcp_client_playwright_" + name))
                .findFirst()
                .orElse(null);
    }
}
