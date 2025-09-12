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

package com.alibaba.cloud.ai.agent.runtime.converter.convertot;

import com.alibaba.cloud.ai.agent.runtime.AgentRuntimeSchema;
import com.alibaba.cloud.ai.agent.runtime.converter.AbstractAgentRuntimeSchemaConvertor;
import com.alibaba.cloud.ai.agent.runtime.converter.IAgentRuntimeSchemaConvertor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.List;
import java.util.Map;

class AgentRuntimeSchemaConvertorTest {

    private String getResourcePath(final String resourceName) {

        URL url = getClass().getClassLoader().getResource(resourceName);
        Assertions.assertNotNull(url, "Resource not found: " + resourceName);

        return url.getPath();
    }

    @Test
    void testJsonConvertor() {

        String path = getResourcePath("template/runtime.config.json");

        IAgentRuntimeSchemaConvertor convertor = AbstractAgentRuntimeSchemaConvertor.createConvertor(path);
        AgentRuntimeSchema schema = convertor.convert();
        Assertions.assertEquals("MyAgent", schema.getName());
        Assertions.assertEquals("1.0.0", schema.getVersion());
        Assertions.assertEquals("A demo agent for testing.", schema.getDescription());
        Assertions.assertEquals("Spring AI Alibaba Graph", schema.getFramework());
        List<Map<String, String>> envs = schema.getEnvs();
        Assertions.assertEquals("/usr/lib/jvm/java-17", envs.get(0).get("JAVA_HOME"));
        Assertions.assertEquals("test", envs.get(1).get("AGENT_MODE"));
    }

    @Test
    void testYamlConvertor() {

        String path = getResourcePath("template/runtime.config.yaml");

        IAgentRuntimeSchemaConvertor convertor = AbstractAgentRuntimeSchemaConvertor.createConvertor(path);
        AgentRuntimeSchema schema = convertor.convert();
        Assertions.assertEquals("MyAgent", schema.getName());
        Assertions.assertEquals("1.0.0", schema.getVersion());
        Assertions.assertEquals("A demo agent for testing.", schema.getDescription());
        Assertions.assertEquals("Spring AI Alibaba Graph", schema.getFramework());
        List<Map<String, String>> envs = schema.getEnvs();
        Assertions.assertEquals("/usr/lib/jvm/java-17", envs.get(0).get("JAVA_HOME"));
        Assertions.assertEquals("test", envs.get(1).get("AGENT_MODE"));
    }

    @Test
    void testPropertiesConvertor() {

        String path = getResourcePath("template/runtime.config.properties");

        IAgentRuntimeSchemaConvertor convertor = AbstractAgentRuntimeSchemaConvertor.createConvertor(path);
        AgentRuntimeSchema schema = convertor.convert();
        Assertions.assertEquals("MyAgent", schema.getName());
        Assertions.assertEquals("1.0.0", schema.getVersion());
        Assertions.assertEquals("A demo agent for testing.", schema.getDescription());
        Assertions.assertEquals("Spring AI Alibaba Graph", schema.getFramework());
        List<Map<String, String>> envs = schema.getEnvs();
        Assertions.assertEquals("/usr/lib/jvm/java-17", envs.get(0).get("JAVA_HOME"));
        Assertions.assertEquals("test", envs.get(1).get("AGENT_MODE"));
    }
}
