package com.alibaba.cloud.ai.agent.runtime.sandbox.mcp.utils;

import org.springframework.ai.tool.ToolCallback;

public class ToolCallbackUtils {

	public static ToolCallback findToolCallbackByName(ToolCallback[] toolCallbacks, String toolNameSuffix) {
		for (ToolCallback toolCallback : toolCallbacks) {
			String toolName = toolCallback.getToolDefinition().name();
			if (toolName.toLowerCase().endsWith(toolNameSuffix.toLowerCase())) {
				return toolCallback;
			}
		}
		return null;
	}

}
