#!/bin/bash

cp -r /app/steel-browser/ui/dist/* /usr/share/nginx/html/
cp -r /app/steel-browser/ui/nginx.conf.template /etc/nginx/nginx.conf.template

# 启动 steel-browser API 服务
cd /app/steel-browser && nohup ./api/entrypoint.sh --no-nginx > ./api/log.file 2>./api/log.error &

cd /app/steel-browser && export API_URL=http://127.0.0.1:3000 && nohup ./ui/entrypoint.sh > ./ui/log.file 2>./ui/log.error &

# 启动 java 服务
cd /app/java && nohup java -jar -Dspring.ai.mcp.client.stdio.servers-configuration=file:///app/java/mcp-servers.json ./app.jar > log.file 2>log.error &

wait
