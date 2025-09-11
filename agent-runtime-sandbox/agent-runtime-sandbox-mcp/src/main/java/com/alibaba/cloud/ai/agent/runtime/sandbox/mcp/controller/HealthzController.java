package com.alibaba.cloud.ai.agent.runtime.sandbox.mcp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthzController {

	@GetMapping("/healthz")
	public String index() {
		return "\"OK\"";
	}

}
