// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package cmd

import (
	"fmt"

	"github.com/spf13/cobra"
)

const Version = "1.0.0-DEV"

var rootCmd = &cobra.Command{
	Use:   "agent-runtime",
	Short: "Agent Runtime CLI - 智能体运行时管理工具",
	Long: `Agent Runtime CLI 是一个用于管理AI智能体运行时的命令行工具。

支持多种AI框架：
  • Spring AI Alibaba Graph
  • LangGraph4J  
  • ADK-Java

主要功能：
  • 项目初始化和配置管理
  • Agent生命周期管理
  • 多种部署方式支持`,
	Run: func(cmd *cobra.Command, args []string) {
		cmd.Help()
	},
}

func Execute() error {
	return rootCmd.Execute()
}

func init() {
	rootCmd.AddCommand(versionCmd)
	rootCmd.AddCommand(initCmd)
	rootCmd.AddCommand(configCmd)
	rootCmd.AddCommand(agentCmd)
	rootCmd.AddCommand(buildCmd)
}

var versionCmd = &cobra.Command{
	Use:   "version",
	Short: "显示版本信息",
	Run: func(cmd *cobra.Command, args []string) {
		fmt.Printf("Agent Runtime CLI v%s\n", Version)
		fmt.Println("Build with ❤️  by Alibaba Cloud AI")
	},
}
