package cmd

import (
	"encoding/json"
	"fmt"
	"os"
	"path/filepath"

	"github.com/spf13/cobra"
)

var initCmd = &cobra.Command{
	Use:   "init [project-name]",
	Short: "初始化Agent项目",
	Long:  "创建新的Agent项目，生成基础配置文件和目录结构",
	Args:  cobra.MaximumNArgs(1),
	Run:   runInit,
}

var (
	framework string
	template  string
)

func init() {
	initCmd.Flags().StringVarP(&framework, "framework", "f", "SPRING_AI_ALIBABA_GRAPH", 
		"AI框架类型 (SPRING_AI_ALIBABA_GRAPH|LANGGRAPH4J|ADK_JAVA)")
	initCmd.Flags().StringVarP(&template, "template", "t", "basic", 
		"项目模板 (basic|web|batch)")
}

func runInit(cmd *cobra.Command, args []string) {
	projectName := "my-agent"
	if len(args) > 0 {
		projectName = args[0]
	}

	fmt.Printf("🚀 初始化Agent项目: %s\n", projectName)
	fmt.Printf("📦 使用框架: %s\n", framework)
	fmt.Printf("📋 使用模板: %s\n", template)

	if err := createProjectStructure(projectName); err != nil {
		fmt.Printf("❌ 创建项目失败: %v\n", err)
		os.Exit(1)
	}

	fmt.Printf("✅ 项目创建成功!\n\n")
	fmt.Printf("下一步:\n")
	fmt.Printf("  cd %s\n", projectName)
	fmt.Printf("  agent-runtime config validate\n")
	fmt.Printf("  agent-runtime agent start\n")
}

func createProjectStructure(projectName string) error {
	// 创建项目目录
	if err := os.MkdirAll(projectName, 0755); err != nil {
		return err
	}

	// 创建子目录
	dirs := []string{"src/main/java", "src/main/resources", "config", "logs"}
	for _, dir := range dirs {
		if err := os.MkdirAll(filepath.Join(projectName, dir), 0755); err != nil {
			return err
		}
	}

	// 生成配置文件
	config := map[string]interface{}{
		"name":        projectName,
		"version":     "1.0.0",
		"description": fmt.Sprintf("Agent project: %s", projectName),
		"framework":   framework,
		"envs": []map[string]string{
			{"JAVA_HOME": "/usr/lib/jvm/java-17"},
			{"AGENT_MODE": "development"},
		},
		"types":  "YAML",
		"schema": "./config/agent.yaml",
	}

	configFile := filepath.Join(projectName, "runtime.config.json")
	if err := writeJSONFile(configFile, config); err != nil {
		return err
	}

	// 生成pom.xml
	if err := createPomXml(projectName); err != nil {
		return err
	}

	// 生成示例配置文件
	return createSampleFiles(projectName)
}

func createPomXml(projectName string) error {
	pomContent := fmt.Sprintf(`<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>
    
    <groupId>com.example</groupId>
    <artifactId>%s</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <mainClass>com.example.Application</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>`, projectName)

	return os.WriteFile(filepath.Join(projectName, "pom.xml"), []byte(pomContent), 0644)
}

func createSampleFiles(projectName string) error {
	// 创建示例配置文件
	agentConfig := `name: example-agent
version: 1.0.0
description: Example agent configuration

runtime:
  framework: SPRING_AI_ALIBABA_GRAPH
  mode: development
  
server:
  port: 8080
  
logging:
  level: INFO
  file: logs/agent.log`

	configPath := filepath.Join(projectName, "config", "agent.yaml")
	if err := os.WriteFile(configPath, []byte(agentConfig), 0644); err != nil {
		return err
	}

	// 创建Java主类
	javaContent := `package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}`

	javaDir := filepath.Join(projectName, "src", "main", "java", "com", "example")
	if err := os.MkdirAll(javaDir, 0755); err != nil {
		return err
	}

	javaPath := filepath.Join(javaDir, "Application.java")
	return os.WriteFile(javaPath, []byte(javaContent), 0644)
}

func writeJSONFile(filename string, data interface{}) error {
	file, err := os.Create(filename)
	if err != nil {
		return err
	}
	defer file.Close()

	encoder := json.NewEncoder(file)
	encoder.SetIndent("", "  ")
	return encoder.Encode(data)
}
