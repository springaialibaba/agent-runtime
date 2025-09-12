package cmd

import (
	"fmt"
	"os"
	"os/exec"
	"path/filepath"

	"github.com/spf13/cobra"
)

var buildCmd = &cobra.Command{
	Use:   "build",
	Short: "构建Agent项目",
	Long:  "编译和打包Agent项目，生成可执行的JAR文件",
	Run:   runBuild,
}

var (
	output    string
	skipTests bool
)

func init() {
	buildCmd.Flags().StringVarP(&output, "output", "o", "target", "输出目录")
	buildCmd.Flags().BoolVar(&skipTests, "skip-tests", false, "跳过测试")
}

func runBuild(cmd *cobra.Command, args []string) {
	fmt.Println("🔨 构建Agent项目...")

	// 检查是否存在pom.xml
	if _, err := os.Stat("pom.xml"); os.IsNotExist(err) {
		fmt.Println("❌ 未找到pom.xml文件")
		fmt.Println("💡 请确保在Maven项目根目录下运行此命令")
		os.Exit(1)
	}

	// 检查Maven环境
	if err := checkMavenEnvironment(); err != nil {
		fmt.Printf("❌ Maven环境检查失败: %v\n", err)
		os.Exit(1)
	}

	// 构建项目
	if err := buildProject(); err != nil {
		fmt.Printf("❌ 构建失败: %v\n", err)
		os.Exit(1)
	}

	// 显示构建结果
	showBuildResults()
}

func checkMavenEnvironment() error {
	cmd := exec.Command("mvn", "--version")
	if err := cmd.Run(); err != nil {
		return fmt.Errorf("Maven未安装或不在PATH中")
	}
	return nil
}

func buildProject() error {
	fmt.Println("📦 执行Maven构建...")

	args := []string{"clean", "package"}
	if skipTests {
		args = append(args, "-DskipTests")
	}

	cmd := exec.Command("mvn", args...)
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr

	return cmd.Run()
}

func showBuildResults() {
	fmt.Println("\n✅ 构建完成!")

	// 查找生成的JAR文件
	jarFiles, err := filepath.Glob("target/*.jar")
	if err != nil || len(jarFiles) == 0 {
		fmt.Println("⚠️  未找到生成的JAR文件")
		return
	}

	fmt.Println("\n📦 生成的文件:")
	for _, jar := range jarFiles {
		info, err := os.Stat(jar)
		if err != nil {
			continue
		}
		
		size := info.Size()
		sizeStr := formatFileSize(size)
		fmt.Printf("  • %s (%s)\n", jar, sizeStr)
	}

	fmt.Println("\n🚀 运行方式:")
	fmt.Printf("  java -jar %s\n", jarFiles[0])
}

func formatFileSize(size int64) string {
	const unit = 1024
	if size < unit {
		return fmt.Sprintf("%d B", size)
	}
	div, exp := int64(unit), 0
	for n := size / unit; n >= unit; n /= unit {
		div *= unit
		exp++
	}
	return fmt.Sprintf("%.1f %cB", float64(size)/float64(div), "KMGTPE"[exp])
}
