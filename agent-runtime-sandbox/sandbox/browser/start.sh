#!/bin/bash
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

# 启动 steel-browser API 服务
cd /app && nohup ./api/entrypoint.sh > ./api/log.file 2>./api/log.error &

cd /app && export API_URL=http://127.0.0.1:3000 && nohup ./ui/entrypoint.sh > ./ui/log.file 2>./ui/log.error &

# 启动 java 服务
cd /app/java && nohup java -jar -Dspring.ai.mcp.client.stdio.servers-configuration=file:///app/java/mcp-servers.json ./app.jar > log.file 2>log.error &

wait
