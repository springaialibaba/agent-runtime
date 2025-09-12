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

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/index")
public class IndexController {

	@Autowired
	private List<McpSyncClient> mcpSyncClients;

	@GetMapping
	public ResponseEntity<String> index() {
		return ResponseEntity.ok("Agent Runtime Sandbox MCP is running.");
	}

	@GetMapping("/clients")
	public ResponseEntity<Set<String>> clients() {
		Set<String> clients = mcpSyncClients.stream()
			.map(McpSyncClient::getClientInfo)
			.map(McpSchema.Implementation::name)
			.collect(Collectors.toSet());
		return ResponseEntity.ok(clients);
	}

}
