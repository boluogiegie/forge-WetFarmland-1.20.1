# Wet Farmland Mod

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Forge](https://img.shields.io/badge/Forge-1.20.1-brightgreen)](https://files.minecraftforge.net/net/minecraftforge/forge/)

一个改变 Minecraft 耕地机制的模组，实现耕地湿润保持、延迟板结和防踩踏功能。

## 功能特性

- **湿润保持**：耕地湿润后保持指定天数（默认 4 天）才开始干燥
- **板结机制**：干燥耕地经过指定天数（默认 30 天）后会硬化成泥土
- **作物销毁**：板结时上方的作物会直接消失，无掉落物（可配置）
- **防踩踏**：玩家和生物不会踩坏耕地（可配置）
- **可配置**：所有时间参数和功能开关都可通过配置文件调整

## 下载

[Releases 页面](https://github.com/boluogiegie/wetfarmland/releases)

## 安装

1. 安装 Minecraft Forge 1.20.1
2. 将模组 jar 文件放入 `.minecraft/mods/` 文件夹
3. 启动游戏

## 配置文件

配置文件位于 `.minecraft/config/wetfarmland.toml`

```toml
[general]
# 耕地保持湿润的天数（默认 4 天）
wetPeriod = 4
# 干燥耕地硬化所需天数（默认 30 天）
hardenPeriod = 30
# 是否防止踩踏破坏耕地
enableTrampling = true
# 硬化时是否销毁上方作物
destroyCropsOnHarden = true