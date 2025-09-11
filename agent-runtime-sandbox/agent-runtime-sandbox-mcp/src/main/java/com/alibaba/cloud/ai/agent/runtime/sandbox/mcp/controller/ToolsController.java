package com.alibaba.cloud.ai.agent.runtime.sandbox.mcp.controller;

import com.alibaba.cloud.ai.agent.runtime.sandbox.mcp.utils.ToolCallbackUtils;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

		ToolCallback toolCallback = ToolCallbackUtils.findToolCallbackByName(toolCallbackProvider.getToolCallbacks(),
				name);
		if (toolCallback == null) {
			return ResponseEntity.badRequest().body("Tool not found: " + name);
		}
		Object result = toolCallback.call(args);
		return ResponseEntity.ok(result);
	}

}
