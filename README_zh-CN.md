# Wet Farmland

[English](README.md) | **简体中文**

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![NeoForge](https://img.shields.io/badge/NeoForge-1.21.1-orange)](https://neoforged.net/)

一个改变 Minecraft 原版耕地机制的轻量服务端模组，实现耕地湿润保持、延迟板结和防踩踏功能。

## 运行环境

Wet Farmland 的功能运行在逻辑服务端。它可以通过 Minecraft 的集成服务端用于单人游戏，也可以只安装在专用服务端，玩家客户端无须安装即可加入。

## 功能特性

- **湿润保持**：耕地湿润后保持指定天数（默认 4 天）才开始干燥。
- **板结机制**：干燥耕地经过指定天数（默认 30 天）后会硬化成泥土。
- **作物销毁**：板结时上方的作物会直接消失，无掉落物（可配置）。
- **防踩踏**：玩家和生物不会踩坏耕地（可配置）。
- **可配置**：所有时间参数和功能开关都可通过配置文件调整。

## 下载

[GitHub Releases](https://github.com/boluogiegie/forge-WetFarmland-1.20.1/releases)

## 安装

### 单人游戏

1. 安装适用于 Minecraft 1.20.1 的 Forge。
2. 将模组 JAR 文件放入 `.minecraft/mods/` 文件夹。
3. 启动游戏。

### 专用服务端

1. 在服务端安装适用于 Minecraft 1.20.1 的 Forge。
2. 将模组 JAR 文件放入服务端的 `mods` 文件夹。
3. 玩家客户端无须安装本模组即可加入。

## 配置文件

配置文件位于 `.minecraft/config/wetfarmland-common.toml`。

```toml
[general]
# 耕地保持湿润的天数（默认：4）
wetPeriod = 4
# 干燥耕地硬化成泥土所需的天数（默认：30）
hardenPeriod = 30
# 是否防止耕地被踩坏
enableTrampling = true
# 耕地硬化时是否销毁上方作物
destroyCropsOnHarden = true
```

## 许可证

Wet Farmland 使用 [MIT 许可证](LICENSE.txt)发布。
