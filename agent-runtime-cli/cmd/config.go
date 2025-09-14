package cmd

import (
	"encoding/json"
	"fmt"
	"os"

	"github.com/spf13/cobra"
)

var configCmd = &cobra.Command{
	Use:   "config",
	Short: "配置文件管理",
	Long:  "管理Agent运行时配置文件，包括生成、验证和查看配置",
}

var configValidateCmd = &cobra.Command{
	Use:   "validate [config-file]",
	Short: "验证配置文件",
	Args:  cobra.MaximumNArgs(1),
	Run:   runConfigValidate,
}

var configShowCmd = &cobra.Command{
	Use:   "show [config-file]",
	Short: "显示配置文件内容",
	Args:  cobra.MaximumNArgs(1),
	Run:   runConfigShow,
}

func init() {
	configCmd.AddCommand(configValidateCmd)
	configCmd.AddCommand(configShowCmd)
}

func runConfigValidate(cmd *cobra.Command, args []string) {
	configFile := "runtime.config.json"
	if len(args) > 0 {
		configFile = args[0]
	}

	fmt.Printf("🔍 验证配置文件: %s\n", configFile)

	config, err := loadConfig(configFile)
	if err != nil {
		fmt.Printf("❌ 加载配置失败: %v\n", err)
		os.Exit(1)
	}

	if err := validateConfig(config); err != nil {
		fmt.Printf("❌ 配置验证失败: %v\n", err)
		os.Exit(1)
	}

	fmt.Println("✅ 配置文件验证通过")
}

func runConfigShow(cmd *cobra.Command, args []string) {
	configFile := "runtime.config.json"
	if len(args) > 0 {
		configFile = args[0]
	}

	// 检查文件是否存在
	if _, err := os.Stat(configFile); os.IsNotExist(err) {
		fmt.Printf("❌ 配置文件不存在: %s\n", configFile)
		fmt.Println("💡 提示: 运行 'agent-runtime init <project-name>' 创建新项目")
		return
	}

	config, err := loadConfig(configFile)
	if err != nil {
		fmt.Printf("❌ 加载配置失败: %v\n", err)
		os.Exit(1)
	}

	fmt.Printf("📋 配置文件: %s\n\n", configFile)
	data, _ := json.MarshalIndent(config, "", "  ")
	fmt.Println(string(data))
}

func loadConfig(filename string) (map[string]interface{}, error) {
	file, err := os.Open(filename)
	if err != nil {
		return nil, err
	}
	defer file.Close()

	var config map[string]interface{}
	decoder := json.NewDecoder(file)
	err = decoder.Decode(&config)
	return config, err
}

func validateConfig(config map[string]interface{}) error {
	required := []string{"name", "version", "framework", "types"}

	for _, field := range required {
		if _, exists := config[field]; !exists {
			return fmt.Errorf("缺少必需字段: %s", field)
		}
	}

	// 验证框架类型
	framework, _ := config["framework"].(string)
	validFrameworks := []string{"SPRING_AI_ALIBABA_GRAPH", "LANGGRAPH4J", "ADK_JAVA"}
	valid := false
	for _, vf := range validFrameworks {
		if framework == vf {
			valid = true
			break
		}
	}
	if !valid {
		return fmt.Errorf("不支持的框架类型: %s", framework)
	}

	return nil
}
