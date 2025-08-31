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

import java.util.Map;

/**
 * Example demonstrating how to use the Java Sandbox Client
 */
@SpringBootTest
@DisplayName("Java Sandbox Client Example")
public class SandboxTest {

	private static final Logger logger = LoggerFactory.getLogger(SandboxTest.class);

	@Resource
	private SandboxClientFactory factory;

	@Test
	@DisplayName("Sandbox Client File System Test")
	public void testFileSystem() {
		// Create a filesystem sandbox session
		logger.info("Creating filesystem sandbox session...");
		try (SandboxSession session = factory.createSession(SandboxType.FILESYSTEM)) {
			logger.info("Filesystem sandbox session created: {}", session.getSessionId());
			// Demonstrate filesystem operations
			demonstrateFilesystemOperations(session);
		}
		catch (SandboxClientException e) {
			logger.error("Error during filesystem sandbox demo", e);
		}
	}

	private static void demonstrateFilesystemOperations(SandboxSession session) throws SandboxClientException {
		logger.info("=== Filesystem Operations Demo ===");

		// 2. Create a directory
		logger.info("\n2. Creating directory '/data/demo':");
		Map<String, Object> createDirResult = session.createDirectory("/data/demo");
		logger.info("Create directory result: {}", createDirResult);

		// 3. Write a file
		logger.info("\n3. Writing file '/data/demo/hello.txt':");
		String content = "Hello, Filesystem Sandbox!\nThis is a demo file.\nCreated by Java client.";
		Map<String, Object> writeResult = session.writeFile("/data/demo/hello.txt", content);
		logger.info("Write file result: {}", writeResult);

		// 4. Read the file
		logger.info("\n4. Reading file '/data/demo/hello.txt':");
		Map<String, Object> readResult = session.readFile("/data/demo/hello.txt");
		logger.info("Read file result: {}", readResult);

		// 5. Get file info
		logger.info("\n5. Getting file info for '/data/demo/hello.txt':");
		Map<String, Object> fileInfo = session.getFileInfo("/data/demo/hello.txt");
		logger.info("File info: {}", fileInfo);

		// 6. List directory contents
		logger.info("\n6. Listing directory '/data/demo':");
		Map<String, Object> listResult = session.listDirectory("/data/demo");
		logger.info("Directory listing: {}", listResult);

		// 7. Create another file for demonstration
		logger.info("\n7. Creating another file '/data/demo/config.json':");
		String jsonContent = "{\n  \"name\": \"demo\",\n  \"version\": \"1.0.0\",\n  \"description\": \"Filesystem sandbox demo\"\n}";
		session.writeFile("/data/demo/config.json", jsonContent);

		// 12. Read the edited file
		logger.info("\n12. Reading edited file '/data/demo/hello.txt':");
		Map<String, Object> editedReadResult = session.readFile("/data/demo/hello.txt");
		logger.info("Edited file content: {}", editedReadResult);

		// 13. Move file
		logger.info("\n13. Moving file '/data/demo/config.json' to '/data/demo/settings.json':");
		Map<String, Object> moveResult = session.moveFile("/data/demo/config.json",
				"/data/demo/settings.json");
		logger.info("Move file result: {}", moveResult);

		// 14. Final directory listing
		logger.info("\n14. Final directory listing '/data/demo':");
		Map<String, Object> finalListResult = session.listDirectory("/data/demo");
		logger.info("Final directory listing: {}", finalListResult);

		logger.info("\n=== Filesystem Operations Demo Completed ===");
	}

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