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

package com.alibaba.cloud.ai.agent.runtime.converter;

import com.alibaba.cloud.ai.agent.runtime.AgentRuntimeSchema;
import com.alibaba.cloud.ai.agent.runtime.common.AgentRuntimeException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Objects;

/**
 * Abstract base class for Agent Runtime Schema Converters.
 * Provides common behaviors for all converters.
 *
 * @author yuluo
 */
public abstract class AbstractAgentRuntimeSchemaConverter implements IAgentRuntimeSchemaConverter {

    /**
     * Common resource path, can be used by subclasses.
     */
    private final String resourcePath;

    /**
     * Common file reader, can be used by subclasses.
     */
    private final Reader fileReader;

    public AbstractAgentRuntimeSchemaConverter(final String resourcePath) {

        assert Objects.nonNull(resourcePath) && !resourcePath.isEmpty() : "Resource path must not be null or empty";
        this.resourcePath = resourcePath;

        try {
            this.fileReader = new FileReader(this.resourcePath);
        } catch (FileNotFoundException e) {
            throw new AgentRuntimeException(e);
        }
    }

    public static AbstractAgentRuntimeSchemaConverter createConverter(final String resourcePath) {

        String ext = null;
        if (resourcePath.lastIndexOf('.') != -1) {
            ext = resourcePath.substring(resourcePath.lastIndexOf('.') + 1).toLowerCase();
        }
        if (Objects.isNull(ext) || ext.isEmpty()) {
            throw new IllegalArgumentException("Resource path must have a valid file extension: " + resourcePath);
        }

        return switch (ext) {
            case "json" -> new JSONAgentRuntimeSchemaConverter(resourcePath);
            case "yaml", "yml" -> new YamlAgentRuntimeSchemaConverter(resourcePath);
            case "properties" -> new PropertiesAgentRuntimeSchemaConverter(resourcePath);
            default -> throw new IllegalArgumentException("Unsupported resource type: " + resourcePath);
        };
    }

    @Override
    public AgentRuntimeSchema convert() {

        AbstractAgentRuntimeSchemaConverter converter = createConverter(resourcePath);
        return converter.doConvert();
    }

    /**
     * Actual convert logic to be implemented by subclasses.
     */
    protected abstract AgentRuntimeSchema doConvert();

    public String getResourcePath() {

        return this.resourcePath;
    }

    public Reader getFileReader() {

        return this.fileReader;
    }

}
