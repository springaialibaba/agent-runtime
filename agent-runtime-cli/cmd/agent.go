package cmd

import (
	"fmt"
	"os"
	"os/exec"
	"path/filepath"
	"strings"
	"time"

	"github.com/spf13/cobra"
)

var agentCmd = &cobra.Command{
	Use:   "agent",
	Short: "Agent生命周期管理",
	Long:  "管理Agent的启动、停止、重启和状态查看",
}

var agentStartCmd = &cobra.Command{
	Use:   "start [config-file]",
	Short: "启动Agent",
	Args:  cobra.MaximumNArgs(1),
	Run:   runAgentStart,
}

var agentStopCmd = &cobra.Command{
	Use:   "stop",
	Short: "停止Agent",
	Run:   runAgentStop,
}

var agentStatusCmd = &cobra.Command{
	Use:   "status",
	Short: "查看Agent状态",
	Run:   runAgentStatus,
}

var agentHealthCmd = &cobra.Command{
	Use:   "health",
	Short: "检查Agent健康状态",
	Run:   runAgentHealth,
}

var agentListCmd = &cobra.Command{
	Use:   "list",
	Short: "列出所有Agent",
	Run:   runAgentList,
}

var (
	detach bool
	port   int
)

func init() {
	agentCmd.AddCommand(agentStartCmd)
	agentCmd.AddCommand(agentStopCmd)
	agentCmd.AddCommand(agentStatusCmd)
	agentCmd.AddCommand(agentListCmd)
	agentCmd.AddCommand(agentHealthCmd)

	agentStartCmd.Flags().BoolVarP(&detach, "detach", "d", false, "后台运行")
	agentStartCmd.Flags().IntVarP(&port, "port", "p", 8080, "服务端口")
}

func runAgentStart(cmd *cobra.Command, args []string) {
	configFile := "runtime.config.json"
	if len(args) > 0 {
		configFile = args[0]
	}

	fmt.Printf("🚀 启动Agent...\n")
	fmt.Printf("📋 配置文件: %s\n", configFile)

	// 验证配置文件
	if _, err := os.Stat(configFile); os.IsNotExist(err) {
		fmt.Printf("❌ 配置文件不存在: %s\n", configFile)
		fmt.Println("💡 使用 'agent-runtime init' 创建项目")
		os.Exit(1)
	}

	config, err := loadConfig(configFile)
	if err != nil {
		fmt.Printf("❌ 加载配置失败: %v\n", err)
		os.Exit(1)
	}

	if err := validateConfig(config); err != nil {
		fmt.Printf("❌ 配置验证失败: %v\n", err)
		os.Exit(1)
	}

	// 检查Java环境
	if err := checkJavaEnvironment(); err != nil {
		fmt.Printf("❌ Java环境检查失败: %v\n", err)
		os.Exit(1)
	}

	// 启动Agent
	if err := startAgent(config, detach); err != nil {
		fmt.Printf("❌ 启动Agent失败: %v\n", err)
		os.Exit(1)
	}

	fmt.Printf("✅ Agent启动成功\n")
	if !detach {
		fmt.Printf("🌐 服务地址: http://localhost:%d\n", port)
		fmt.Printf("📝 日志文件: ./logs/agent.log\n")
	}
}

func runAgentStop(cmd *cobra.Command, args []string) {
	fmt.Println("🛑 停止Agent...")

	pidFile := ".agent.pid"
	if _, err := os.Stat(pidFile); os.IsNotExist(err) {
		fmt.Println("ℹ️  没有运行中的Agent")
		return
	}

	// 读取PID
	pidData, err := os.ReadFile(pidFile)
	if err != nil {
		fmt.Printf("❌ 读取PID文件失败: %v\n", err)
		return
	}

	pid := strings.TrimSpace(string(pidData))
	if pid == "" {
		fmt.Println("❌ PID文件为空")
		os.Remove(pidFile)
		return
	}

	// 停止进程
	killCmd := exec.Command("kill", pid)
	if err := killCmd.Run(); err != nil {
		// 尝试强制停止
		killCmd = exec.Command("kill", "-9", pid)
		if err := killCmd.Run(); err != nil {
			fmt.Printf("❌ 停止进程失败: %v\n", err)
			return
		}
	}

	// 删除PID文件
	os.Remove(pidFile)
	fmt.Println("✅ Agent已停止")
}

func runAgentStatus(cmd *cobra.Command, args []string) {
	fmt.Println("📊 Agent状态:")

	pidFile := ".agent.pid"
	if _, err := os.Stat(pidFile); os.IsNotExist(err) {
		fmt.Println("  状态: 已停止")
		return
	}

	// 读取PID
	pidData, err := os.ReadFile(pidFile)
	if err != nil {
		fmt.Println("  状态: 未知 (无法读取PID)")
		return
	}

	pid := strings.TrimSpace(string(pidData))

	// 简单检查：如果PID文件存在且日志文件在更新，认为进程运行中
	logFile := filepath.Join("logs", "agent.log")
	if stat, err := os.Stat(logFile); err == nil {
		// 检查日志文件是否在最近1分钟内更新过
		if time.Since(stat.ModTime()) < time.Minute {
			fmt.Println("  状态: 运行中")
			fmt.Printf("  PID: %s\n", pid)
			fmt.Printf("  端口: %d\n", port)
			fmt.Printf("  日志: %s (大小: %d bytes)\n", logFile, stat.Size())
			fmt.Printf("  最后更新: %s\n", stat.ModTime().Format("2006-01-02 15:04:05"))
			return
		}
	}

	fmt.Println("  状态: 已停止 (进程不存在)")
	os.Remove(pidFile)
}

func runAgentList(cmd *cobra.Command, args []string) {
	fmt.Println("📋 Agent列表:")

	// 查找当前目录下的配置文件
	configs, err := filepath.Glob("runtime.config*.json")
	if err != nil {
		fmt.Printf("❌ 查找配置文件失败: %v\n", err)
		return
	}

	if len(configs) == 0 {
		fmt.Println("  没有找到Agent配置文件")
		return
	}

	for _, configFile := range configs {
		config, err := loadConfig(configFile)
		if err != nil {
			continue
		}

		name, _ := config["name"].(string)
		version, _ := config["version"].(string)
		framework, _ := config["framework"].(string)

		fmt.Printf("  • %s (v%s) - %s\n", name, version, framework)
		fmt.Printf("    配置: %s\n", configFile)
	}
}

func runAgentHealth(cmd *cobra.Command, args []string) {
	fmt.Println("🏥 检查Agent健康状态...")

	// 检查进程状态
	pidFile := ".agent.pid"
	if _, err := os.Stat(pidFile); os.IsNotExist(err) {
		fmt.Println("❌ Agent未运行")
		return
	}

	pidData, err := os.ReadFile(pidFile)
	if err != nil {
		fmt.Println("❌ 无法读取进程信息")
		return
	}

	pid := strings.TrimSpace(string(pidData))
	checkCmd := exec.Command("ps", "-p", pid)
	if err := checkCmd.Run(); err != nil {
		fmt.Println("❌ Agent进程不存在")
		os.Remove(pidFile)
		return
	}

	fmt.Println("✅ Agent进程正常")

	// 检查端口连通性
	checkPort := exec.Command("nc", "-z", "localhost", fmt.Sprintf("%d", port))
	if err := checkPort.Run(); err != nil {
		fmt.Printf("⚠️  端口 %d 无法连接\n", port)
	} else {
		fmt.Printf("✅ 端口 %d 连接正常\n", port)
	}

	// 检查配置文件
	if _, err := os.Stat("runtime.config.json"); err != nil {
		fmt.Println("⚠️  配置文件不存在")
	} else {
		fmt.Println("✅ 配置文件存在")
	}

	fmt.Println("🎉 健康检查完成")
}

func checkJavaEnvironment() error {
	cmd := exec.Command("java", "-version")
	output, err := cmd.CombinedOutput()
	if err != nil {
		return fmt.Errorf("Java未安装或不在PATH中")
	}

	// 检查Java版本
	outputStr := string(output)
	if !strings.Contains(outputStr, "17") && !strings.Contains(outputStr, "21") {
		fmt.Println("⚠️  建议使用Java 17+")
	}

	return nil
}

func startAgent(config map[string]interface{}, detach bool) error {
	framework, _ := config["framework"].(string)
	fmt.Printf("🔧 使用框架: %s\n", framework)

	// 查找JAR文件
	jarFile, err := findJarFile()
	if err != nil {
		return fmt.Errorf("未找到JAR文件: %v", err)
	}

	fmt.Printf("📦 JAR文件: %s\n", jarFile)

	// 构建Java命令
	args := []string{"-jar", jarFile}
	if port != 8080 {
		args = append(args, fmt.Sprintf("--server.port=%d", port))
	}

	fmt.Printf("🔧 执行命令: java %s\n", strings.Join(args, " "))

	cmd := exec.Command("java", args...)

	if detach {
		// 后台运行 - 重定向输出到日志文件
		logDir := "logs"
		os.MkdirAll(logDir, 0755)

		logFile, err := os.Create(filepath.Join(logDir, "agent.log"))
		if err != nil {
			return fmt.Errorf("创建日志文件失败: %v", err)
		}

		cmd.Stdout = logFile
		cmd.Stderr = logFile

		if err := cmd.Start(); err != nil {
			logFile.Close()
			return fmt.Errorf("启动进程失败: %v", err)
		}

		// 保存PID - 确保文件被创建
		pidFile := ".agent.pid"
		if err := os.WriteFile(pidFile, []byte(fmt.Sprintf("%d", cmd.Process.Pid)), 0644); err != nil {
			cmd.Process.Kill()
			logFile.Close()
			return fmt.Errorf("保存PID文件失败: %v", err)
		}

		fmt.Println("🔄 Agent在后台运行")
		fmt.Printf("📝 日志文件: %s\n", filepath.Join(logDir, "agent.log"))
		fmt.Printf("🆔 进程ID: %d\n", cmd.Process.Pid)
		fmt.Printf("📄 PID文件: %s\n", pidFile)

		// 监控进程状态 - 不要立即删除PID文件
		go func() {
			defer logFile.Close()
			cmd.Wait()
			// 进程结束后才删除PID文件
			fmt.Printf("进程 %d 已结束\n", cmd.Process.Pid)
			os.Remove(pidFile)
		}()

	} else {
		// 前台运行
		cmd.Stdout = os.Stdout
		cmd.Stderr = os.Stderr
		fmt.Println("🔄 Agent在前台运行 (Ctrl+C 停止)")
		return cmd.Run()
	}

	return nil
}

func findJarFile() (string, error) {
	// 查找target目录下的JAR文件
	jarFiles, err := filepath.Glob("target/*.jar")
	if err != nil {
		return "", err
	}

	if len(jarFiles) == 0 {
		return "", fmt.Errorf("target目录下没有JAR文件，请先运行 'agent-runtime build'")
	}

	// 优先选择非test的JAR文件
	for _, jar := range jarFiles {
		if !strings.Contains(jar, "test") {
			return jar, nil
		}
	}

	return jarFiles[0], nil
}
