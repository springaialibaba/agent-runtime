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

package com.alibaba.cloud.ai.agent.runtime.sandbox.core.enums;

import java.util.List;

/**
 * Sandbox type enumeration
 */
public enum SandboxType {

	BASE("base", "agentruntime/sandbox:base", List.of(8000)),
	FILESYSTEM("filesystem", "agentruntime/sandbox:filesystem", List.of(8000)),
	BROWSER("browser", "agentruntime/sandbox:browser", List.of(8000, 80, 3000, 9223)),
	CUSTOM("custom", "agentruntime/sandbox:custom", List.of(8000));

	private final String value;

	private final List<Integer> ports;

	private final String imageName;

	SandboxType(String value, String imageName, List<Integer> ports) {
		this.value = value;
		this.imageName = imageName;
		this.ports = ports;
	}

	public String getValue() {
		return value;
	}

	public List<Integer> getPorts() {
		return ports;
	}

	public String getImageName() {
		return imageName;
	}

	public static SandboxType fromValue(String value) {
		for (SandboxType type : values()) {
			if (type.value.equals(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Unknown sandbox type: " + value);
	}

	@Override
	public String toString() {
		return value;
	}


}
