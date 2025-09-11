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

package com.alibaba.cloud.ai.agent.runtime.sandbox.core.client;

import com.alibaba.cloud.ai.agent.runtime.sandbox.core.enums.SandboxType;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.exceptions.SandboxClientException;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.model.ContainerModel;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.model.ExecutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Represents a sandbox session with a container
 */
public class SandboxSession implements AutoCloseable {

	private static final Logger logger = LoggerFactory.getLogger(SandboxSession.class);

	private ContainerModel container;

	private final SandboxHttpClient httpClient;

	private final SandboxManagerClient managerClient;

	private boolean closed = false;

	/**
	 * Constructor
	 */
	public SandboxSession(ContainerModel container, SandboxHttpClient httpClient, SandboxManagerClient managerClient) {
		this.container = container;
		this.httpClient = httpClient;
		this.managerClient = managerClient;
	}

	/**
	 * Execute Python code
	 */
	public ExecutionResult runPython(String code) {
		checkClosed();
		logger.debug("Executing Python code in session: {}", container.getSessionId());
		return httpClient.runPythonCell(code);
	}

	/**
	 * Execute Python code with split output option
	 */
	public ExecutionResult runPython(String code, boolean splitOutput) {
		checkClosed();
		logger.debug("Executing Python code in session: {} with splitOutput: {}", container.getSessionId(),
				splitOutput);
		return httpClient.runPythonCell(code, splitOutput);
	}

	/**
	 * Execute shell command
	 */
	public ExecutionResult runShell(String command) {
		checkClosed();
		logger.debug("Executing shell command in session: {}", container.getSessionId());
		return httpClient.runShellCommand(command);
	}

	/**
	 * Execute shell command with split output option
	 */
	public ExecutionResult runShell(String command, boolean splitOutput) {
		checkClosed();
		logger.debug("Executing shell command in session: {} with splitOutput: {}", container.getSessionId(),
				splitOutput);
		return httpClient.runShellCommand(command, splitOutput);
	}

	/**
	 * Read file content
	 */
	public Map<String, Object> readFile(String path) {
		checkClosed();
		logger.debug("Reading file in session: {}, path: {}", container.getSessionId(), path);
		return httpClient.readFile(path);
	}

	/**
	 * Write file content
	 */
	public Map<String, Object> writeFile(String path, String content) {
		checkClosed();
		logger.debug("Writing file in session: {}, path: {}", container.getSessionId(), path);
		return httpClient.writeFile(path, content);
	}

	/**
	 * Create directory
	 */
	public Map<String, Object> createDirectory(String path) {
		checkClosed();
		logger.debug("Creating directory in session: {}, path: {}", container.getSessionId(), path);
		return httpClient.createDirectory(path);
	}

	/**
	 * List directory contents
	 */
	public Map<String, Object> listDirectory(String path) {
		checkClosed();
		logger.debug("Listing directory in session: {}, path: {}", container.getSessionId(), path);
		return httpClient.listDirectory(path);
	}

	/**
	 * Move file
	 */
	public Map<String, Object> moveFile(String source, String destination) {
		checkClosed();
		logger.debug("Moving file in session: {}, source: {}, destination: {}", container.getSessionId(), source,
				destination);
		return httpClient.moveFile(source, destination);
	}

	/**
	 * Get file info
	 */
	public Map<String, Object> getFileInfo(String path) {
		checkClosed();
		logger.debug("Getting file info in session: {}, path: {}", container.getSessionId(), path);
		return httpClient.getFileInfo(path);
	}

	// ==================== Playwright MCP Methods ====================

	/**
	 * Navigate browser to URL
	 */
	public Map<String, Object> browserNavigate(String url) {
		checkClosed();
		logger.debug("Navigating browser in session: {}, url: {}", container.getSessionId(), url);
		return httpClient.browserNavigate(url);
	}

	/**
	 * Click element by selector
	 */
	public Map<String, Object> browserClick(String selector) {
		checkClosed();
		logger.debug("Clicking element in session: {}, selector: {}", container.getSessionId(), selector);
		return httpClient.browserClick(selector);
	}

	/**
	 * Type text into element
	 */
	public Map<String, Object> browserType(String selector, String text) {
		checkClosed();
		logger.debug("Typing text in session: {}, selector: {}, text: {}", container.getSessionId(), selector, text);
		return httpClient.browserType(selector, text);
	}

	/**
	 * Take screenshot
	 */
	public Map<String, Object> browserTakeScreenshot() {
		checkClosed();
		logger.debug("Taking screenshot in session: {}", container.getSessionId());
		return httpClient.browserTakeScreenshot();
	}

	/**
	 * Take screenshot with options
	 */
	public Map<String, Object> browserTakeScreenshot(String selector, boolean fullPage) {
		checkClosed();
		logger.debug("Taking screenshot in session: {}, selector: {}, fullPage: {}", container.getSessionId(), selector, fullPage);
		return httpClient.browserTakeScreenshot(selector, fullPage);
	}

	/**
	 * Get page snapshot (accessibility tree)
	 */
	public Map<String, Object> browserSnapshot() {
		checkClosed();
		logger.debug("Getting page snapshot in session: {}", container.getSessionId());
		return httpClient.browserSnapshot();
	}

	/**
	 * Press key
	 */
	public Map<String, Object> browserPressKey(String key) {
		checkClosed();
		logger.debug("Pressing key in session: {}, key: {}", container.getSessionId(), key);
		return httpClient.browserPressKey(key);
	}

	/**
	 * Hover over element
	 */
	public Map<String, Object> browserHover(String selector) {
		checkClosed();
		logger.debug("Hovering over element in session: {}, selector: {}", container.getSessionId(), selector);
		return httpClient.browserHover(selector);
	}

	/**
	 * Select option from dropdown
	 */
	public Map<String, Object> browserSelectOption(String selector, String value) {
		checkClosed();
		logger.debug("Selecting option in session: {}, selector: {}, value: {}", container.getSessionId(), selector, value);
		return httpClient.browserSelectOption(selector, value);
	}

	/**
	 * Wait for element or condition
	 */
	public Map<String, Object> browserWaitFor(String selector, int timeout) {
		checkClosed();
		logger.debug("Waiting for element in session: {}, selector: {}, timeout: {}", container.getSessionId(), selector, timeout);
		return httpClient.browserWaitFor(selector, timeout);
	}

	/**
	 * Navigate back
	 */
	public Map<String, Object> browserNavigateBack() {
		checkClosed();
		logger.debug("Navigating back in session: {}", container.getSessionId());
		return httpClient.browserNavigateBack();
	}

	/**
	 * Navigate forward
	 */
	public Map<String, Object> browserNavigateForward() {
		checkClosed();
		logger.debug("Navigating forward in session: {}", container.getSessionId());
		return httpClient.browserNavigateForward();
	}

	/**
	 * Resize browser window
	 */
	public Map<String, Object> browserResize(int width, int height) {
		checkClosed();
		logger.debug("Resizing browser in session: {}, width: {}, height: {}", container.getSessionId(), width, height);
		return httpClient.browserResize(width, height);
	}

	/**
	 * Close browser
	 */
	public Map<String, Object> browserClose() {
		checkClosed();
		logger.debug("Closing browser in session: {}", container.getSessionId());
		return httpClient.browserClose();
	}

	/**
	 * Create new tab
	 */
	public Map<String, Object> browserTabNew() {
		checkClosed();
		logger.debug("Creating new tab in session: {}", container.getSessionId());
		return httpClient.browserTabNew();
	}

	/**
	 * List all tabs
	 */
	public Map<String, Object> browserTabList() {
		checkClosed();
		logger.debug("Listing tabs in session: {}", container.getSessionId());
		return httpClient.browserTabList();
	}

	/**
	 * Select tab by index
	 */
	public Map<String, Object> browserTabSelect(int index) {
		checkClosed();
		logger.debug("Selecting tab in session: {}, index: {}", container.getSessionId(), index);
		return httpClient.browserTabSelect(index);
	}

	/**
	 * Close tab by index
	 */
	public Map<String, Object> browserTabClose(int index) {
		checkClosed();
		logger.debug("Closing tab in session: {}, index: {}", container.getSessionId(), index);
		return httpClient.browserTabClose(index);
	}

	/**
	 * Save page as PDF
	 */
	public Map<String, Object> browserPdfSave(String path) {
		checkClosed();
		logger.debug("Saving PDF in session: {}, path: {}", container.getSessionId(), path);
		return httpClient.browserPdfSave(path);
	}

	/**
	 * Handle dialog (alert, confirm, prompt)
	 */
	public Map<String, Object> browserHandleDialog(boolean accept) {
		return browserHandleDialog(accept, null);
	}

	/**
	 * Handle dialog (alert, confirm, prompt) with text
	 */
	public Map<String, Object> browserHandleDialog(boolean accept, String text) {
		checkClosed();
		logger.debug("Handling dialog in session: {}, accept: {}, text: {}", container.getSessionId(), accept, text);
		return httpClient.browserHandleDialog(accept, text);
	}

	/**
	 * Upload file
	 */
	public Map<String, Object> browserFileUpload(String selector, String filePath) {
		checkClosed();
		logger.debug("Uploading file in session: {}, selector: {}, filePath: {}", container.getSessionId(), selector, filePath);
		return httpClient.browserFileUpload(selector, filePath);
	}

	/**
	 * Get console messages
	 */
	public Map<String, Object> browserConsoleMessages() {
		checkClosed();
		logger.debug("Getting console messages in session: {}", container.getSessionId());
		return httpClient.browserConsoleMessages();
	}

	/**
	 * Get network requests
	 */
	public Map<String, Object> browserNetworkRequests() {
		checkClosed();
		logger.debug("Getting network requests in session: {}", container.getSessionId());
		return httpClient.browserNetworkRequests();
	}

	/**
	 * Drag element from source to target
	 */
	public Map<String, Object> browserDrag(String sourceSelector, String targetSelector) {
		checkClosed();
		logger.debug("Dragging element in session: {}, from: {} to: {}", container.getSessionId(), sourceSelector, targetSelector);
		return httpClient.browserDrag(sourceSelector, targetSelector);
	}

	/**
	 * Generate Playwright test code
	 */
	public Map<String, Object> browserGeneratePlaywrightTest() {
		checkClosed();
		logger.debug("Generating Playwright test in session: {}", container.getSessionId());
		return httpClient.browserGeneratePlaywrightTest();
	}

	/**
	 * Check if container is healthy
	 */
	public boolean isHealthy() {
		if (closed) {
			return false;
		}
		return httpClient.healthCheck();
	}

	/**
	 * Get container information
	 */
	public ContainerModel getContainer() {
		return container;
	}

	/**
	 * Get session ID
	 */
	public String getSessionId() {
		return container.getSessionId();
	}

	/**
	 * Get container ID
	 */
	public String getContainerId() {
		return container.getContainerId();
	}

	/**
	 * Get base URL
	 */
	public String getBaseUrl() {
		return container.getBaseUrl();
	}

	/**
	 * Get browser URL (if available)
	 */
	public String getBrowserUrl() {
		return container.getBrowserUrl();
	}

	/**
	 * Get sandbox type
	 */
	public String getSandboxType() {
		return container.getSandboxType();
	}

	/**
	 * Check if session is closed
	 */
	public boolean isClosed() {
		return closed;
	}

	/**
	 * Restart the session (recreate container)
	 */
	public void restart() {
		logger.info("Restarting sandbox session: {}", container.getSessionId());

		// Close current resources
		try {
			httpClient.close();
		}
		catch (IOException e) {
			logger.warn("Failed to close HTTP client during restart", e);
		}

		// Remove old container
		try {
			managerClient.deleteContainer(container.getSessionId());
		}
		catch (SandboxClientException e) {
			logger.warn("Failed to delete old container during restart", e);
		}
		// Create new container with same session ID
		try {
			this.container = managerClient.createContainer(SandboxType.fromValue(getSandboxType()), getSessionId());
		}
		catch (SandboxClientException e) {
			logger.error("Failed to create new container during restart", e);
			throw new SandboxClientException("Failed to restart session: " + container.getSessionId());
		}
	}

	/**
	 * Check if session is closed and throw exception if it is
	 */
	private void checkClosed() throws SandboxClientException {
		if (closed) {
			throw new SandboxClientException("Session is closed: " + container.getSessionId());
		}
	}

	@Override
	public void close() {
		if (closed) {
			return;
		}

		logger.info("Closing sandbox session: {}", container.getSessionId());

		try {
			// Close HTTP client
			httpClient.close();
		}
		catch (IOException e) {
			logger.error("Failed to close HTTP client", e);
		}

		try {
			// Remove container
			managerClient.deleteContainer(container.getSessionId());
		}
		catch (SandboxClientException e) {
			logger.error("Failed to delete container during close", e);
		}

		closed = true;
		logger.info("Sandbox session closed: {}", container.getSessionId());
	}

	@Override
	public String toString() {
		return String.format("SandboxSession{sessionId='%s', containerId='%s', baseUrl='%s', closed=%s}",
				container.getSessionId(), container.getContainerId(), container.getBaseUrl(), closed);
	}

}
