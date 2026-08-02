# TLeaf-Link

多功能 QQ/KOOK 群服互通机器人插件，专为**白叶（White Leaf）MC 服务器**定制开发。

[![License](https://img.shields.io/badge/license-AGPL%203.0-blue.svg)](./LICENSE)
[![Minecraft](https://img.shields.io/badge/Minecraft-26.2-brightgreen.svg)]()
[![Velocity](https://img.shields.io/badge/Velocity-3.x-9cf.svg)]()

## 功能

### 跨服聊天互通

- **QQ ↔ MC**：QQ 群消息实时转发至 MC，MC 玩家消息同步回 QQ 群
- **KOOK ↔ MC**：KOOK 频道消息与 MC 互通（目前受 KookBC 上游兼容性问题影响，暂不可用）
- **按服分组显示**：在线人数按登录服/生存服/红石服分组展示

### 消息增强

- **& 颜色码**：支持传统 `&6`、`&a` 等颜色格式，兼容旧版插件生态
- **可点击回复**：MC 玩家可点击 QQ 消息直接回复至 QQ 群（`/qqreply`）
- **@ 用户显示群名片**：QQ 群内 @ 某人自动显示其群昵称

### 群管理

- **群级别开关**：群主/管理员可单独控制每个 QQ 群的转发和通知开关
- **白名单管理**：支持游戏内 `/申请白名单`、`/删除白名单` 等指令

### 计划中

- 死亡信息 & 成就达成 → QQ 转发
- PlayerChat `[i]` 物品展示 → QQ 端解析

## 架构

```
Velocity 代理端
├── tleaflink-velocity         ← QQ/KOOK Bot 核心 + 跨服广播
└── 子服（Bukkit/Paper）
    └── tleaflink-bukkit       ← 子服事件监听 + 数据同步
```

## 模块

| 模块 | 说明 |
| --- | --- |
| `tleaflink-velocity` | Velocity 代理端模块，负责 Bot 连接、消息中转、命令处理 |
| `tleaflink-bukkit` | Bukkit 子服模块，负责死亡信息、成就等事件监听与转发 |

## 技术栈

- **语言**：Java
- **构建**：Maven
- **平台**：Velocity 3.x + Paper 1.26.2
- **QQ 协议**：go-cqhttp
- **KOOK**：KookBC（等待上游修复）
- **数据库**：MySQL
- **依赖**：LuckPerms、PlaceholderAPI、Adventure Component API

## 快速开始

> 详细部署文档即将推出。

### 环境要求

- Velocity 3.x 代理端
- Paper 1.26.2+ 子服
- MySQL 数据库
- go-cqhttp 服务

### 构建

```bash
mvn clean package
```

构建产物位于各模块的 `target/` 目录。

## 来源

本项目基于 [RegadPoleCN/PlumBot](https://github.com/RegadPoleCN/PlumBot)（`build-1.3.2` 分支）二次开发，感谢原作者的贡献。

## 开发

本项目为白叶 MC 服务器定制开发，使用 `TLeaf-{功能}` 命名规范。

- **命名空间**：`me.linyefl.tleaflink`
- **提交信息**：中文
- **架构文档**：见白叶服务器设计文档

## 许可

[AGPL 3.0](./LICENSE) © LinyeFL
