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

# 第一阶段：安装浏览器相关依赖
FROM node:22-slim AS browser-stage

RUN rm -f /etc/apt/apt.conf.d/docker-clean; \
    echo 'Binary::apt::APT::Keep-Downloaded-Packages "true";' > /etc/apt/apt.conf.d/keep-cache; \
    apt-get update -qq && \
    DEBIAN_FRONTEND=noninteractive apt-get -yq dist-upgrade

# Install dependencies
RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -yq --no-install-recommends \
    wget \
    vim \
    nginx \
    build-essential \
    pkg-config \
    python-is-python3 \
    fonts-ipafont-gothic \
    fonts-wqy-zenhei \
    fonts-thai-tlwg \
    fonts-kacst \
    fonts-freefont-ttf \
    libxss1 \
    xvfb \
    curl \
    unzip \
    default-jre \
    git \
    dbus \
    dbus-x11 \
    procps \
    x11-xserver-utils

# Install Chrome and ChromeDriver
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y chromium chromium-driver \
    # Clean up
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* \
    && rm -rf /var/cache/apt/*

# 第二阶段：安装 steel-browser api
FROM browser-stage AS steel-browser-stage
# 克隆 steel-browser 项目
WORKDIR /app
RUN git clone https://github.com/steel-dev/steel-browser.git

WORKDIR /app/steel-browser
# 依赖
RUN npm ci --include=dev -w api -w ui --ignore-scripts 
RUN cd api/extensions/recorder && npm ci --include=dev
# 构建
RUN npm run build -w api -w ui
RUN cd api/extensions/recorder && npm run build 
# 清理
RUN npm prune --omit=dev -w api -w ui
RUN cd api/extensions/recorder && npm prune --omit=dev

# 第三阶段：安装 JDK17
FROM steel-browser-stage AS jdk-stage

# 安装 JDK17
RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get install -y openjdk-17-jdk

# 设置 JAVA_HOME
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# 第四阶段：拷贝 jar 包
FROM jdk-stage AS jar-stage

# 创建应用目录
WORKDIR /app/java

# 拷贝 agent-runtime-sandbox-mcp 模块的 jar 包
COPY agent-runtime-sandbox-mcp-1.0.0-DEV.jar app.jar
COPY playwright_mcp_config.json playwright_mcp_config.json
COPY mcp-servers.json mcp-servers.json

# 第五阶段：最终运行阶段
FROM jar-stage AS final-stage

# 暴露端口
EXPOSE 3000 8080 80

# 创建启动脚本
WORKDIR /app
COPY start.sh .

# 给启动脚本执行权限
RUN chmod +x /app/start.sh

# 启动命令
CMD ["/app/start.sh"]