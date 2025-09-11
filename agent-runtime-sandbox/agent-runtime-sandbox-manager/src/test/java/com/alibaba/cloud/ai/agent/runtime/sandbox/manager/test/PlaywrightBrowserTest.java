package com.alibaba.cloud.ai.agent.runtime.sandbox.manager.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.HashMap;

@SpringBootTest
@DisplayName("Playwright Browser Tool Test")
public class PlaywrightBrowserTest {

	private static final Logger logger = LoggerFactory.getLogger(PlaywrightBrowserTest.class);

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private SyncMcpToolCallbackProvider toolCallbackProvider;

	@Test
	@DisplayName("Test opening Taobao website using playwright browser")
	public void testOpenTaobao() {
		logger.info("Starting Playwright Browser test to open Taobao");

		// 获取所有可用的工具回调
		ToolCallback[] toolCallbacks = toolCallbackProvider.getToolCallbacks();
		logger.info("Available tools count: {}", toolCallbacks.length);

		// 寻找浏览器相关的工具
		ToolCallback navigateToolCallback = null;
		ToolCallback screenshotToolCallback = null;

		for (ToolCallback toolCallback : toolCallbacks) {
			String toolName = toolCallback.getToolDefinition().name();
			// logger.info("Available tool: {}", toolName);

			// 查找导航工具（通常名为 navigate_to 或类似）
			if (toolName.toLowerCase().endsWith("browser_navigate")) {
				navigateToolCallback = toolCallback;
				logger.info("Found navigate tool: {}", toolName);
			}

			// 查找截图工具
			if (toolName.toLowerCase().endsWith("browser_take_screenshot")) {
				screenshotToolCallback = toolCallback;
				logger.info("Found screenshot tool: {}", toolName);
			}
		}

		// 如果找到导航工具，使用它打开淘宝
		if (navigateToolCallback != null) {
			try {
				Map<String, Object> arguments = new HashMap<>();
				arguments.put("url", "https://www.taobao.com");

				String argumentsJson = objectMapper.writeValueAsString(arguments);
				logger.info("Navigating to Taobao using tool: {}", navigateToolCallback.getToolDefinition().name());
				String result = navigateToolCallback.call(argumentsJson);
				logger.info("Navigation result: {}", result);

				// 等待一下让页面加载
				Thread.sleep(3000);

				// 如果有截图工具，拍个截图
				if (screenshotToolCallback != null) {
					Map<String, Object> screenshotArgs = new HashMap<>();
					screenshotArgs.put("filename", "taobao_homepage");

					String screenshotArgsJson = objectMapper.writeValueAsString(screenshotArgs);
					logger.info("Taking screenshot using tool: {}", screenshotToolCallback.getToolDefinition().name());
					String screenshotResult = screenshotToolCallback.call(screenshotArgsJson);
					logger.info("Screenshot result: {}", screenshotResult);
				}

			}
			catch (Exception e) {
				logger.error("Error during browser operation", e);
			}
		}
		else {
			logger.warn("No navigate tool found. Available tools:");
			for (ToolCallback toolCallback : toolCallbacks) {
				logger.warn("- {}: {}", toolCallback.getToolDefinition().name(),
						toolCallback.getToolDefinition().description());
			}
		}
	}

	@Test
	@DisplayName("List all available playwright browser tools")
	public void testListBrowserTools() {
		logger.info("Listing all available browser tools");

		ToolCallback[] toolCallbacks = toolCallbackProvider.getToolCallbacks();

		for (ToolCallback toolCallback : toolCallbacks) {
			String toolName = toolCallback.getToolDefinition().name();
			String toolDescription = toolCallback.getToolDefinition().description();

			// 只显示可能与浏览器相关的工具
			if (toolName.toLowerCase().contains("browser") || toolName.toLowerCase().contains("navigate")
					|| toolName.toLowerCase().contains("click") || toolName.toLowerCase().contains("screenshot")
					|| toolName.toLowerCase().contains("goto") || toolName.toLowerCase().contains("page")) {

				logger.info("Browser Tool - Name: {}, Description: {}", toolName, toolDescription);

				// 打印工具的参数信息
				logger.info("  Parameters: {}", toolCallback.getToolDefinition().inputSchema());
			}
		}
	}

	@Test
	@DisplayName("Advanced Taobao interaction demo")
	public void testAdvancedTaobaoInteraction() {
		logger.info("Starting advanced Taobao interaction demo");

		ToolCallback[] toolCallbacks = toolCallbackProvider.getToolCallbacks();

		// 收集各种浏览器工具
		Map<String, ToolCallback> browserTools = new HashMap<>();

		for (ToolCallback toolCallback : toolCallbacks) {
			String toolName = toolCallback.getToolDefinition().name().toLowerCase();

			if (toolName.contains("navigate") || toolName.contains("goto")) {
				browserTools.put("navigate", toolCallback);
			}
			else if (toolName.contains("click")) {
				browserTools.put("click", toolCallback);
			}
			else if (toolName.contains("type") || toolName.contains("fill")) {
				browserTools.put("type", toolCallback);
			}
			else if (toolName.contains("screenshot")) {
				browserTools.put("screenshot", toolCallback);
			}
			else if (toolName.contains("wait")) {
				browserTools.put("wait", toolCallback);
			}
		}

		logger.info("Found {} browser tools", browserTools.size());

		try {
			// 1. 导航到淘宝
			if (browserTools.containsKey("navigate")) {
				Map<String, Object> args = new HashMap<>();
				args.put("url", "https://www.taobao.com");

				String argsJson = objectMapper.writeValueAsString(args);
				String result = browserTools.get("navigate").call(argsJson);
				logger.info("Navigation to Taobao: {}", result);
				Thread.sleep(2000);
			}

			// 2. 拍摄首页截图
			if (browserTools.containsKey("screenshot")) {
				Map<String, Object> args = new HashMap<>();
				args.put("name", "taobao_homepage");

				String argsJson = objectMapper.writeValueAsString(args);
				String result = browserTools.get("screenshot").call(argsJson);
				logger.info("Homepage screenshot: {}", result);
			}

			// 3. 如果有搜索框，可以尝试搜索（这里只是演示，实际需要根据页面元素调整）
			if (browserTools.containsKey("type")) {
				try {
					Map<String, Object> args = new HashMap<>();
					args.put("selector", "#q"); // 淘宝搜索框的选择器
					args.put("text", "手机");

					String argsJson = objectMapper.writeValueAsString(args);
					String result = browserTools.get("type").call(argsJson);
					logger.info("Search input: {}", result);
					Thread.sleep(1000);

					// 拍摄搜索后的截图
					if (browserTools.containsKey("screenshot")) {
						Map<String, Object> screenshotArgs = new HashMap<>();
						screenshotArgs.put("name", "taobao_search_input");

						String screenshotArgsJson = objectMapper.writeValueAsString(screenshotArgs);
						String screenshotResult = browserTools.get("screenshot").call(screenshotArgsJson);
						logger.info("Search input screenshot: {}", screenshotResult);
					}

				}
				catch (Exception e) {
					logger.info("Search input failed (element might not exist): {}", e.getMessage());
				}
			}

		}
		catch (Exception e) {
			logger.error("Error during advanced interaction", e);
		}
	}

}
