#!/bin/bash

cp ../../agent-runtime-sandbox-mcp/target/agent-runtime-sandbox-mcp-1.0.0-DEV.jar .
docker build -t agentruntime/sandbox:browser .