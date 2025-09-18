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

package com.alibaba.cloud.ai.agent.runtime.sandbox.core.service;

import com.alibaba.cloud.ai.agent.runtime.sandbox.core.enums.SandboxType;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.model.ContainerModel;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.properties.SandboxProperties;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Service for managing Docker containers
 */
public class ContainerService {

	private static final Logger logger = LoggerFactory.getLogger(ContainerService.class);

	public ContainerService(SandboxProperties config) {
		this.config = config;
	}

	private final SandboxProperties config;

	private DockerClient dockerClient;

	private final Map<String, ContainerModel> activeContainers = new ConcurrentHashMap<>();

	private final Set<Integer> occupiedPorts = ConcurrentHashMap.newKeySet();

	@PostConstruct
	public void init() {
		initializeDockerClient();
	}

	@PreDestroy
	public void cleanup() {
		if (config.isAutoCleanup()) {
			cleanupAllContainers();
		}
		if (dockerClient != null) {
			try {
				dockerClient.close();
			}
			catch (Exception e) {
				logger.error("Error closing Docker client", e);
			}
		}
	}

	/**
	 * Initialize Docker client
	 */
	private void initializeDockerClient() {
		try {
			DefaultDockerClientConfig dockerConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
				.withDockerHost(config.getDockerHost())
				.build();

			ApacheDockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
				.dockerHost(dockerConfig.getDockerHost())
				.sslConfig(dockerConfig.getSSLConfig())
				.maxConnections(100)
				.connectionTimeout(Duration.ofSeconds(30))
				.responseTimeout(Duration.ofSeconds(45))
				.build();

			dockerClient = DockerClientBuilder.getInstance(dockerConfig).withDockerHttpClient(httpClient).build();

			// Test connection
			dockerClient.pingCmd().exec();
			logger.info("Docker client initialized successfully");

		}
		catch (Exception e) {
			logger.error("Failed to initialize Docker client", e);
			throw new RuntimeException("Docker client initialization failed", e);
		}
	}

	/**
	 * Create and start a new container
	 */
	public ContainerModel createContainer(String sessionId, SandboxType sandboxType) {
		try {
			String imageName = sandboxType.getImageName();
			String containerName = generateContainerName(sessionId);

			// Allocate ports
			List<Integer> ports = allocatePorts(sandboxType.getPorts().size()); // Main port and browser port

			// Build port bindings and environment variables and mounts
			PortBinding[] portBindings = buildPortBindings(ports, sandboxType.getPorts());
			List<String> environmentVariables = buildEnvironmentVariables(sessionId);
			this.processSandboxSpecEnv(environmentVariables, portBindings, sandboxType);
			List<Mount> mounts = buildMounts();

			// Create container
			CreateContainerResponse container = dockerClient.createContainerCmd(imageName)
				.withName(containerName)
				.withEnv(environmentVariables)
				.withHostConfig(HostConfig.newHostConfig()
				.withMounts(mounts)
				.withPortBindings(portBindings)
				// .withAutoRemove(config.isAutoCleanup())
				.withNetworkMode("bridge"))
				.exec();

			String containerId = container.getId();

			// Start container
			dockerClient.startContainerCmd(containerId).exec();

			// Wait for container to be ready
			waitForContainerReady(containerId, ports.get(0));

			// Create container model
			ContainerModel model = new ContainerModel(sessionId, containerId, "http://localhost:" + ports.get(0), ports, sandboxType.getValue());

			model.setBearerToken(config.getBearerToken());
			activeContainers.put(sessionId, model);

			logger.info("Container created successfully: {} for session: {}", containerId, sessionId);
			return model;

		}
		catch (Exception e) {
			logger.error("Failed to create container for session: {}", sessionId, e);
			throw new RuntimeException("Container creation failed", e);
		}
	}

	/**
	 * Process sandbox specific environment variables
	 * @param environmentVariables environmentVariables
	 * @param portBindings portBindings
	 * @param sandboxType sandboxType
	 */
	private void processSandboxSpecEnv(List<String> environmentVariables, PortBinding[] portBindings, SandboxType sandboxType){
		if (sandboxType == SandboxType.BROWSER) {
			if (portBindings.length >= 3) {
				String portSpec = portBindings[2].getBinding().getHostPortSpec();
				environmentVariables.add("DOMAIN=localhost:" + portSpec);
			}
		}

	}

	/**
	 * Stop and remove container
	 */
	public void removeContainer(String sessionId) {
		ContainerModel container = activeContainers.get(sessionId);
		if (container != null) {
			try {
				// Stop container
				dockerClient.stopContainerCmd(container.getContainerId()).withTimeout(10).exec();

				// Remove container
				dockerClient.removeContainerCmd(container.getContainerId()).withForce(true).exec();

				// Release ports
				container.getPorts().forEach(occupiedPorts::remove);

				activeContainers.remove(sessionId);

				logger.info("Container removed: {} for session: {}", container.getContainerId(), sessionId);

			}
			catch (Exception e) {
				logger.error("Failed to remove container for session: {}", sessionId, e);
			}
		}
	}

	/**
	 * Get container information
	 */
	public ContainerModel getContainer(String sessionId) {
		return activeContainers.get(sessionId);
	}

	/**
	 * List all active containers
	 */
	public Map<String, ContainerModel> listContainers() {
		return new HashMap<>(activeContainers);
	}


	/**
	 * Generate container name
	 */
	private String generateContainerName(String sessionId) {
		return config.getContainerPrefixKey() + sessionId;
	}

	/**
	 * Allocate available ports
	 */
	private List<Integer> allocatePorts(int count) {
		List<Integer> ports = new ArrayList<>();
		List<Integer> portRange = config.getPortRange();
		int minPort = portRange.get(0);
		int maxPort = portRange.get(1);

		for (int i = 0; i < count; i++) {
			int port = findAvailablePort(minPort, maxPort);
			ports.add(port);
			occupiedPorts.add(port);
		}

		return ports;
	}

	/**
	 * Find available port in range
	 */
	private int findAvailablePort(int minPort, int maxPort) {
		for (int i = 0; i < 100; i++) { // Try 100 times
			int port = ThreadLocalRandom.current().nextInt(minPort, maxPort + 1);
			if (!occupiedPorts.contains(port)) {
				return port;
			}
		}
		throw new RuntimeException("No available ports in range " + minPort + "-" + maxPort);
	}

	/**
	 * Build port bindings
	 */
	private PortBinding[] buildPortBindings(List<Integer> ports, List<Integer> occupiedPorts) {
		List<PortBinding> bindings = new ArrayList<>();
		for(int i = 0 ; i < ports.size() ; i++) {
			bindings.add(new PortBinding(Ports.Binding.bindPort(ports.get(i)), ExposedPort.tcp(occupiedPorts.get(i))));
		}
		return bindings.toArray(new PortBinding[0]);
	}

	private List<Mount> buildMounts() {
		List<Mount> mounts = new ArrayList<>();
		if (config.getDefaultMountDir() != null && !config.getDefaultMountDir().isEmpty()) {
			String hostDir = config.getDefaultMountDir();
			String containerDir = "/workspace";
			Mount mount = new Mount().withType(MountType.BIND)
				.withSource(hostDir)
				.withTarget(containerDir)
				.withReadOnly(false);
			mounts.add(mount);
		}
		return mounts;
	}

	/**
	 * Build environment variables
	 */
	private List<String> buildEnvironmentVariables(String sessionId) {
		List<String> env = new ArrayList<>();
		env.add("SESSION_ID=" + sessionId);
		env.add("SECRET_TOKEN=" + config.getBearerToken());
		env.add("WORKSPACE_DIR=/workspace");
		// Add sandbox type specific envs
		if (config.getDockerEnvironment() != null) {
			config.getDockerEnvironment().forEach((key, value) -> env.add(key + "=" + value));
		}
		return env;
	}

	/**
	 * Wait for container to be ready
	 */
	private void waitForContainerReady(String containerId, int port) {
		int maxAttempts = 30;
		int attempt = 0;

		while (attempt < maxAttempts) {
			try {
				// Check if container is running
				InspectContainerResponse containerInfo = dockerClient.inspectContainerCmd(containerId).exec();

				if (Boolean.TRUE.equals(containerInfo.getState().getRunning())) {
					// Additional check: try to connect to the service
					Thread.sleep(1000); // Wait a bit more for service to start
					return;
				}

				Thread.sleep(1000);
				attempt++;

			}
			catch (Exception e) {
				logger.debug("Waiting for container to be ready, attempt: {}", attempt + 1);
				try {
					Thread.sleep(1000);
				}
				catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					throw new RuntimeException("Interrupted while waiting for container", ie);
				}
				attempt++;
			}
		}

		throw new RuntimeException("Container failed to become ready within timeout");
	}

	/**
	 * Generate runtime token
	 */
	private String generateToken() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * Cleanup all containers
	 */
	private void cleanupAllContainers() {
		logger.info("Cleaning up all containers...");

		for (String sessionId : new ArrayList<>(activeContainers.keySet())) {
			removeContainer(sessionId);
		}

		logger.info("Container cleanup completed");
	}

}
