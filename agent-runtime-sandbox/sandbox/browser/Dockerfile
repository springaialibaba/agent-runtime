# 多阶段构建整合 steel-browser-ui 和 steel-browser-api

# 第一阶段：从 UI 镜像复制文件
FROM ghcr.io/steel-dev/steel-browser-ui as ui-stage

# 第二阶段：从 API 镜像复制文件
FROM ghcr.io/steel-dev/steel-browser-api as api-stage

COPY --from=ui-stage /usr/share/nginx/html /usr/share/nginx/html
COPY --from=ui-stage /etc/nginx/nginx.conf.template /etc/nginx/nginx.conf.template
COPY --from=ui-stage /docker-entrypoint.sh /app/ui/entrypoint.sh


# 第三阶段：安装 JDK17
FROM api-stage AS jdk-stage

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
EXPOSE 3000 8080 80 9223

# 创建启动脚本
WORKDIR /app
COPY start.sh .

# 给启动脚本执行权限
RUN chmod +x /app/start.sh
ENV DOMAIN=localhost:3000
# 启动命令
ENTRYPOINT ["/app/start.sh"]
