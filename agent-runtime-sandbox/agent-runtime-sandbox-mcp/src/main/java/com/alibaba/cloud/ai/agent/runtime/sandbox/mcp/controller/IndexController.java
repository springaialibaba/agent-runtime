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
