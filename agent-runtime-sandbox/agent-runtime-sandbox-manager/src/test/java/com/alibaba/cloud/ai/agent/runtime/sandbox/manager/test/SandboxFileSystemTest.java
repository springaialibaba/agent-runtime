package com.alibaba.cloud.ai.agent.runtime.sandbox.manager.test;

import com.alibaba.cloud.ai.agent.runtime.sandbox.core.client.SandboxClientFactory;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.client.SandboxSession;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.enums.SandboxType;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.exceptions.SandboxClientException;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
@DisplayName("Java Sandbox Client Example")
public class SandboxFileSystemTest {

    private static final Logger logger = LoggerFactory.getLogger(SandboxBaseTest.class);

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
        logger.info("\n2. Creating directory '/workspace/demo':");
        Object createDirResult = session.call("filesystem_create_directory", Map.of("path", "/workspace/demo"));
        logger.info("Create directory result: {}", createDirResult);

        // 3. Write a file
        logger.info("\n3. Writing file '/workspace/demo/hello.txt':");
        String content = "Hello, Filesystem Sandbox!\nThis is a demo file.\nCreated by Java client.";
        Object writeResult = session.call("filesystem_write_file", Map.of("path", "/workspace/demo/hello.txt", "content", content));
        logger.info("Write file result: {}", writeResult);

        // 4. Read the file
        logger.info("\n4. Reading file '/workspace/demo/hello.txt':");
        Object readResult = session.call("filesystem_read_file", Map.of("path", "/workspace/demo/hello.txt"));
        logger.info("Read file result: {}", readResult);

        // 5. Get file info
        logger.info("\n5. Getting file info for '/workspace/demo/hello.txt':");
        Object fileInfo = session.call("filesystem_get_file_info", Map.of("path", "/workspace/demo/hello.txt"));
        logger.info("File info: {}", fileInfo);

        // 6. List directory contents
        logger.info("\n6. Listing directory '/workspace/demo':");
        Object listResult = session.call("filesystem_list_directory", Map.of("path", "/workspace/demo"));
        logger.info("Directory listing: {}", listResult);

        // 7. Create another file for demonstration
        logger.info("\n7. Creating another file '/workspace/demo/config.json':");
        String jsonContent = "{\n  \"name\": \"demo\",\n  \"version\": \"1.0.0\",\n  \"description\": \"Filesystem sandbox demo\"\n}";
        session.call("filesystem_write_file", Map.of("path", "/workspace/demo/config.json", "content", jsonContent));

        // 12. Read the edited file
        logger.info("\n12. Reading edited file '/workspace/demo/hello.txt':");
        Object editedReadResult = session.call("filesystem_read_file", Map.of("path", "/workspace/demo/hello.txt"));
        logger.info("Edited file content: {}", editedReadResult);

        // 13. Move file
        logger.info("\n13. Moving file '/workspace/demo/config.json' to '/workspace/demo/settings.json':");
        Object moveResult = session.call("filesystem_move_file", Map.of("source", "/workspace/demo/config.json", "destination", "/workspace/demo/settings.json"));
        logger.info("Move file result: {}", moveResult);

        // 14. Final directory listing
        logger.info("\n14. Final directory listing '/workspace/demo':");
        Object finalListResult = session.call("filesystem_list_directory", Map.of("path", "/workspace/demo"));
        logger.info("Final directory listing: {}", finalListResult);

        logger.info("\n=== Filesystem Operations Demo Completed ===");
    }
}
