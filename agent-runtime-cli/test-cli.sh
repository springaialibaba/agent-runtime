#!/bin/bash

echo "🧪 测试Agent Runtime CLI功能"

# 清理测试环境
rm -rf test-project
rm -f .agent.pid

echo "1. 测试版本命令"
./agent-runtime version

echo -e "\n2. 测试初始化项目"
./agent-runtime init test-project --framework SPRING_AI_ALIBABA_GRAPH

echo -e "\n3. 检查生成的文件结构"
ls -la test-project/
echo "Java源码:"
ls -la test-project/src/main/java/com/example/

echo -e "\n4. 进入项目目录"
cd test-project

echo -e "\n5. 测试配置验证"
../agent-runtime config validate

echo -e "\n6. 测试配置显示"
../agent-runtime config show

echo -e "\n7. 测试构建项目"
../agent-runtime build

echo -e "\n8. 检查构建结果"
ls -la target/

echo -e "\n9. 测试Agent状态"
../agent-runtime agent status

echo -e "\n10. 测试Agent启动（后台模式）"
../agent-runtime agent start --detach
sleep 2

echo -e "\n11. 测试Agent状态（运行中）"
../agent-runtime agent status

echo -e "\n12. 测试Agent停止"
../agent-runtime agent stop

echo -e "\n13. 清理测试环境"
cd ..
rm -rf test-project

echo -e "\n✅ CLI测试完成"
