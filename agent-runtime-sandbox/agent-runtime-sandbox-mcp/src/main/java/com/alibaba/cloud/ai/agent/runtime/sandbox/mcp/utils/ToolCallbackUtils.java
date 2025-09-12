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
