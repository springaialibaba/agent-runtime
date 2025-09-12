# Agent Runtime CLI

Agent Runtime CLI 是一个用于管理AI智能体运行时的命令行工具。

## 功能特性

- 🚀 项目初始化和配置管理
- 🔧 Agent生命周期管理
- 📦 项目构建和打包
- 🏥 健康状态检查
- 📋 多框架支持

## 支持的AI框架

- Spring AI Alibaba Graph
- LangGraph4J
- ADK-Java

## 安装

```bash
# 编译CLI工具
go build -o agent-runtime

# 或使用make
make build
```

## 快速开始

### 1. 初始化项目

```bash
# 创建新项目
agent-runtime init my-agent

# 指定框架
agent-runtime init my-agent --framework SPRING_AI_ALIBABA_GRAPH

# 指定模板
agent-runtime init my-agent --template web
```

### 2. 配置管理

```bash
# 验证配置文件
agent-runtime config validate

# 显示配置内容
agent-runtime config show
```

### 3. 构建项目

```bash
# 构建项目
agent-runtime build

# 跳过测试
agent-runtime build --skip-tests

# 指定输出目录
agent-runtime build --output dist
```

### 4. Agent管理

```bash
# 启动Agent
agent-runtime agent start

# 后台启动
agent-runtime agent start --detach

# 指定端口
agent-runtime agent start --port 9090

# 查看状态
agent-runtime agent status

# 健康检查
agent-runtime agent health

# 停止Agent
agent-runtime agent stop

# 列出所有Agent
agent-runtime agent list
```

## 命令参考

### 全局命令

- `version` - 显示版本信息
- `help` - 显示帮助信息

### 项目管理

- `init [project-name]` - 初始化新项目
  - `--framework, -f` - 指定AI框架
  - `--template, -t` - 指定项目模板

### 配置管理

- `config validate [config-file]` - 验证配置文件
- `config show [config-file]` - 显示配置内容

### 构建管理

- `build` - 构建项目
  - `--output, -o` - 指定输出目录
  - `--skip-tests` - 跳过测试

### Agent管理

- `agent start [config-file]` - 启动Agent
  - `--detach, -d` - 后台运行
  - `--port, -p` - 指定端口
- `agent stop` - 停止Agent
- `agent status` - 查看Agent状态
- `agent health` - 健康检查
- `agent list` - 列出所有Agent

## 配置文件格式

```json
{
  "name": "my-agent",
  "version": "1.0.0",
  "description": "My AI Agent",
  "framework": "SPRING_AI_ALIBABA_GRAPH",
  "types": "YAML",
  "schema": "./config/agent.yaml",
  "envs": [
    {"JAVA_HOME": "/usr/lib/jvm/java-17"},
    {"AGENT_MODE": "development"}
  ]
}
```

## 项目结构

```
my-agent/
├── src/
│   └── main/
│       ├── java/
│       └── resources/
├── config/
│   └── agent.yaml
├── logs/
├── pom.xml
└── runtime.config.json
```

## 开发

```bash
# 运行测试
./test-cli.sh

# 构建
go build -o agent-runtime

# 清理
go clean
```

## 许可证

Apache License 2.0
