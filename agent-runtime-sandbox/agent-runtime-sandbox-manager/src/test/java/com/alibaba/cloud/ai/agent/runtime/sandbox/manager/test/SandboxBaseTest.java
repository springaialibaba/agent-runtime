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
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.model.ExecutionResult;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.exceptions.SandboxClientException;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.client.SandboxClientFactory;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.client.SandboxSession;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Example demonstrating how to use the Java Sandbox Client
 */
@SpringBootTest
@DisplayName("Java Sandbox Client Example")
public class SandboxBaseTest {

	private static final Logger logger = LoggerFactory.getLogger(SandboxBaseTest.class);

	@Resource
	private SandboxClientFactory factory;


	@Test
	@DisplayName("Sandbox Client Base Test")
	public void testBase() {
		try {
			// Check if manager is healthy
			if (!factory.isManagerHealthy()) {
				System.err.println("Sandbox manager is not healthy!");
				return;
			}

			System.out.println("Creating sandbox session...");

			// Create a new sandbox session
			try (SandboxSession session = factory.createSession(SandboxType.BASE)) {

				System.out.println("Session created: " + session.getSessionId());
				System.out.println("Container URL: " + session.getBaseUrl());

				System.out.println("Sandbox session status is " + session.isHealthy());

				// Example 1: Execute Python code
				System.out.println("\n=== Python Code Execution ===");

				String pythonCode = """
						import math
						import numpy as np

						# Calculate some values
						result = math.sqrt(16)
						array = np.array([1, 2, 3, 4, 5])
						mean_value = np.mean(array)

						print(f"Square root of 16: {result}")
						print(f"Array: {array}")
						print(f"Mean value: {mean_value}")

						# Create a simple plot
						import matplotlib.pyplot as plt
						plt.figure(figsize=(8, 6))
						plt.plot(array, array**2, 'bo-')
						plt.title('Square Function')
						plt.xlabel('x')
						plt.ylabel('x^2')
						plt.grid(True)
						plt.savefig('/workspace/plot.png')
						print("Plot saved to /workspace/plot.png")
						""";

				ExecutionResult pythonResult = session.runPython(pythonCode);
				printExecutionResult("Python Code", pythonResult);

				// Example 2: Execute shell commands
				System.out.println("\n=== Shell Command Execution ===");

				ExecutionResult shellResult1 = session.runShell("ls -la /workspace");
				printExecutionResult("List workspace", shellResult1);

				ExecutionResult shellResult2 = session.runShell("python --version");
				printExecutionResult("Python version", shellResult2);

				ExecutionResult shellResult3 = session.runShell("pip list | grep numpy");
				printExecutionResult("Check numpy", shellResult3);

				// Example 3: File operations
				System.out.println("\n=== File Operations ===");

				String fileCode = """
						# Create a text file
						with open('/workspace/example.txt', 'w') as f:
						    f.write('Hello from Java Sandbox!\\n')
						    f.write('This file was created using Python code.\\n')
						    f.write('Current working directory: ')

						import os
						with open('/workspace/example.txt', 'a') as f:
						    f.write(os.getcwd())

						print('File created successfully!')
						""";

				ExecutionResult fileResult = session.runPython(fileCode);
				printExecutionResult("Create file", fileResult);

				ExecutionResult readResult = session.runShell("cat /workspace/example.txt");
				printExecutionResult("Read file", readResult);

				// Example 4: Error handling
				System.out.println("\n=== Error Handling ===");

				ExecutionResult errorResult = session.runPython("print(undefined_variable)");
				printExecutionResult("Python error", errorResult);

				ExecutionResult shellErrorResult = session.runShell("nonexistent_command");
				printExecutionResult("Shell error", shellErrorResult);

				// Example 5: Split output
				System.out.println("\n=== Split Output Example ===");

				String splitCode = """
						import sys
						print("This goes to stdout")
						print("This also goes to stdout")
						sys.stderr.write("This goes to stderr\\n")
						sys.stderr.write("This also goes to stderr\\n")
						""";

				ExecutionResult splitResult = session.runPython(splitCode, true);
				printExecutionResult("Split output", splitResult);

				System.out.println("\n=== Session Complete ===");
				System.out.println("Session will be automatically closed.");

			} // Session automatically closed here

		}
		catch (SandboxClientException e) {
			System.err.println("Sandbox error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Helper method to print execution results
	 */
	private static void printExecutionResult(String title, ExecutionResult result) {
		System.out.println("--- " + title + " ---");
		System.out.println("Error: " + result.isError());

		for (ExecutionResult.TextContent content : result.getContent()) {
			System.out.println("Type: " + content.getType());
			if (content.getDescription() != null) {
				System.out.println("Description: " + content.getDescription());
			}
			System.out.println("Content:");
			System.out.println(content.getText());
			System.out.println();
		}
	}

}
