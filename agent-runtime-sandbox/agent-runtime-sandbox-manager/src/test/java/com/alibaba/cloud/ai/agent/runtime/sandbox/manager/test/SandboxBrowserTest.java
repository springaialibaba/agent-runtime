/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.alibaba.cloud.ai.agent.runtime.sandbox.manager.test;

import com.alibaba.cloud.ai.agent.runtime.sandbox.core.enums.SandboxType;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.exceptions.SandboxClientException;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.client.SandboxClientFactory;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.client.SandboxSession;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

/**
 * Playwright MCP 功能演示测试
 * 展示如何使用 SandboxSession 中的 Playwright MCP 方法进行浏览器自动化
 */
@SpringBootTest
@DisplayName("Sandbox Browser 功能测试")
public class SandboxBrowserTest {

	private static final Logger logger = LoggerFactory.getLogger(SandboxBrowserTest.class);

	@Resource
	private SandboxClientFactory factory;

	@Test
	@DisplayName("基础浏览器操作测试")
	public void testBasicBrowserOperations() {
		logger.info("开始基础浏览器操作测试...");
		
		try (SandboxSession session = factory.createSession(SandboxType.BROWSER)) {
			logger.info("Sandbox session 创建成功: {}", session.getSessionId());
			// 演示基础浏览器操作
			demonstrateBasicBrowserOperations(session);
			
		} catch (SandboxClientException e) {
			logger.error("基础浏览器操作测试失败", e);
		}
	}

	/**
	 * 演示基础浏览器操作
	 */
	private static void demonstrateBasicBrowserOperations(SandboxSession session) throws SandboxClientException {
		logger.info("=== 基础浏览器操作演示 ===");

		// 1. 导航到网页
		logger.info("\n1. 导航到示例网页:");
		Object navigateResult = session.call("playwright_browser_navigate", Map.of("url","https://taobao.com/"));
		logger.info("导航结果: {}", navigateResult);

		// 2. 获取页面快照
		logger.info("\n2. 获取页面快照 (可访问性树):");
		Object snapshotResult = session.call("playwright_browser_snapshot", Map.of());
		logger.info("页面快照: {}", snapshotResult);

		// 3. 截图
		logger.info("\n3. 截取页面截图:");
		Object screenshotResult = session.call("playwright_browser_take_screenshot", Map.of());
		logger.info("截图结果: {}", screenshotResult);

		// 4. 全页截图
		logger.info("\n4. 截取全页截图:");
		Object fullScreenshotResult = session.call("playwright_browser_take_screenshot", Map.of("fullPage", true));
		logger.info("全页截图结果: {}", fullScreenshotResult);

		// 5. 浏览器导航操作
		logger.info("\n5. 导航到另一个页面:");
		session.call("playwright_browser_navigate", Map.of("url", "https://www.npmjs.com/package/@playwright/mcp"));
		
		logger.info("\n6. 后退:");
		Object backResult = session.call("playwright_browser_navigate_back", Map.of());
		logger.info("后退结果: {}", backResult);


		logger.info("\n=== 基础浏览器操作演示完成 ===");
	}
}
