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

import java.util.Map;

/**
 * Playwright MCP 功能演示测试
 * 展示如何使用 SandboxSession 中的 Playwright MCP 方法进行浏览器自动化
 */
@SpringBootTest
@DisplayName("Playwright MCP 功能测试")
public class PlaywrightMcpTest {

	private static final Logger logger = LoggerFactory.getLogger(PlaywrightMcpTest.class);

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

	@Test
	@DisplayName("浏览器交互操作测试")
	public void testBrowserInteractions() {
		logger.info("开始浏览器交互操作测试...");
		
		try (SandboxSession session = factory.createSession(SandboxType.BROWSER)) {
			logger.info("Sandbox session 创建成功: {}", session.getSessionId());
			
			// 演示浏览器交互操作
			demonstrateInteractionOperations(session);
			
		} catch (SandboxClientException e) {
			logger.error("浏览器交互操作测试失败", e);
		}
	}

	@Test
	@DisplayName("高级浏览器功能测试")
	public void testAdvancedBrowserFeatures() {
		logger.info("开始高级浏览器功能测试...");
		
		try (SandboxSession session = factory.createSession(SandboxType.BROWSER)) {
			logger.info("Sandbox session 创建成功: {}", session.getSessionId());
			
			// 演示高级浏览器功能
			demonstrateAdvancedFeatures(session);
			
		} catch (SandboxClientException e) {
			logger.error("高级浏览器功能测试失败", e);
		}
	}

	/**
	 * 演示基础浏览器操作
	 */
	private static void demonstrateBasicBrowserOperations(SandboxSession session) throws SandboxClientException {
		logger.info("=== 基础浏览器操作演示 ===");

		// 1. 导航到网页
		logger.info("\n1. 导航到示例网页:");
		Map<String, Object> navigateResult = session.browserNavigate("https://example.com");
		logger.info("导航结果: {}", navigateResult);

		// 2. 获取页面快照
		logger.info("\n2. 获取页面快照 (可访问性树):");
		Map<String, Object> snapshotResult = session.browserSnapshot();
		logger.info("页面快照: {}", snapshotResult);

		// 3. 截图
		logger.info("\n3. 截取页面截图:");
		Map<String, Object> screenshotResult = session.browserTakeScreenshot();
		logger.info("截图结果: {}", screenshotResult);

		// 4. 全页截图
		logger.info("\n4. 截取全页截图:");
		Map<String, Object> fullScreenshotResult = session.browserTakeScreenshot(null, true);
		logger.info("全页截图结果: {}", fullScreenshotResult);

		// 5. 浏览器导航操作
		logger.info("\n5. 导航到另一个页面:");
		session.browserNavigate("https://httpbin.org/html");
		
		logger.info("\n6. 后退:");
		Map<String, Object> backResult = session.browserNavigateBack();
		logger.info("后退结果: {}", backResult);
		
		logger.info("\n7. 前进:");
		Map<String, Object> forwardResult = session.browserNavigateForward();
		logger.info("前进结果: {}", forwardResult);

		logger.info("\n=== 基础浏览器操作演示完成 ===");
	}

	/**
	 * 演示浏览器交互操作
	 */
	private static void demonstrateInteractionOperations(SandboxSession session) throws SandboxClientException {
		logger.info("=== 浏览器交互操作演示 ===");

		// 1. 导航到表单页面
		logger.info("\n1. 导航到表单测试页面:");
		session.browserNavigate("https://httpbin.org/forms/post");

		// 2. 点击元素
		logger.info("\n2. 点击输入框:");
		Map<String, Object> clickResult = session.browserClick("input[name='custname']");
		logger.info("点击结果: {}", clickResult);

		// 3. 输入文本
		logger.info("\n3. 输入客户姓名:");
		Map<String, Object> typeResult = session.browserType("input[name='custname']", "张三");
		logger.info("输入结果: {}", typeResult);

		// 4. 选择下拉选项
		logger.info("\n4. 选择下拉选项:");
		Map<String, Object> selectResult = session.browserSelectOption("select[name='size']", "large");
		logger.info("选择结果: {}", selectResult);

		// 5. 悬停操作
		logger.info("\n5. 悬停在提交按钮上:");
		Map<String, Object> hoverResult = session.browserHover("input[type='submit']");
		logger.info("悬停结果: {}", hoverResult);

		// 6. 按键操作
		logger.info("\n6. 按 Tab 键:");
		Map<String, Object> keyResult = session.browserPressKey("Tab");
		logger.info("按键结果: {}", keyResult);

		// 7. 等待元素
		logger.info("\n7. 等待提交按钮可见:");
		Map<String, Object> waitResult = session.browserWaitFor("input[type='submit']", 5000);
		logger.info("等待结果: {}", waitResult);

		logger.info("\n=== 浏览器交互操作演示完成 ===");
	}

	/**
	 * 演示高级浏览器功能
	 */
	private static void demonstrateAdvancedFeatures(SandboxSession session) throws SandboxClientException {
		logger.info("=== 高级浏览器功能演示 ===");

		// 1. 调整浏览器窗口大小
		logger.info("\n1. 调整浏览器窗口大小:");
		Map<String, Object> resizeResult = session.browserResize(1200, 800);
		logger.info("调整大小结果: {}", resizeResult);

		// 2. 标签页管理
		logger.info("\n2. 创建新标签页:");
		Map<String, Object> newTabResult = session.browserTabNew();
		logger.info("新标签页结果: {}", newTabResult);

		logger.info("\n3. 列出所有标签页:");
		Map<String, Object> tabListResult = session.browserTabList();
		logger.info("标签页列表: {}", tabListResult);

		// 3. 导航到新页面并保存为 PDF
		logger.info("\n4. 导航到新页面:");
		session.browserNavigate("https://example.com");
		
		logger.info("\n5. 保存页面为 PDF:");
		Map<String, Object> pdfResult = session.browserPdfSave("/workspace/example.pdf");
		logger.info("PDF 保存结果: {}", pdfResult);

		// 4. 获取控制台消息
		logger.info("\n6. 获取控制台消息:");
		Map<String, Object> consoleResult = session.browserConsoleMessages();
		logger.info("控制台消息: {}", consoleResult);

		// 5. 获取网络请求
		logger.info("\n7. 获取网络请求:");
		Map<String, Object> networkResult = session.browserNetworkRequests();
		logger.info("网络请求: {}", networkResult);

		// 6. 生成 Playwright 测试代码
		logger.info("\n8. 生成 Playwright 测试代码:");
		Map<String, Object> testCodeResult = session.browserGeneratePlaywrightTest();
		logger.info("测试代码生成结果: {}", testCodeResult);

		logger.info("\n=== 高级浏览器功能演示完成 ===");
	}

	/**
	 * 辅助方法：打印操作结果
	 */
	private static void printResult(String operation, Map<String, Object> result) {
		logger.info("--- {} ---", operation);
		logger.info("结果: {}", result);
		logger.info("");
	}
}