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

package com.alibaba.cloud.ai.agent.runtime.sandbox.mcp.controller;

import com.alibaba.cloud.ai.agent.runtime.sandbox.mcp.utils.ToolCallbackUtils;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tools")
public class ToolsController {

	@Autowired
	private SyncMcpToolCallbackProvider toolCallbackProvider;

	@PostMapping("/call")
	public ResponseEntity<Object> call(@RequestBody Map<String, String> request) {
		String name = request.get("name");
		String args = request.get("args");


		ToolCallback toolCallback = ToolCallbackUtils.findToolCallbackByName(toolCallbackProvider.getToolCallbacks(), this.completeName(name));
		if (toolCallback == null) {
			return ResponseEntity.badRequest().body("Tool not found: " + name);
		}
		Object result = toolCallback.call(args);
		return ResponseEntity.ok(result);
	}

	private String completeName(String name) {
		return "spring_ai_mcp_client_playwright_" + name;
	}

	@PostMapping("/list")
	public ResponseEntity<List<ToolDefinition>> list() {
		List<ToolDefinition> definitions = new ArrayList<>();
		ToolCallback[] toolCallbacks = toolCallbackProvider.getToolCallbacks();
		for(ToolCallback toolCallback : toolCallbacks) {
			definitions.add(toolCallback.getToolDefinition());
		}
		return ResponseEntity.ok(definitions);
	}

}
