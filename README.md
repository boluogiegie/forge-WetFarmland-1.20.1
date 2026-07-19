# Wet Farmland

**English** | [简体中文](README_zh-CN.md)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![NeoForge](https://img.shields.io/badge/NeoForge-1.21.1-orange)](https://neoforged.net/)

A lightweight server-side Minecraft mod that changes vanilla farmland mechanics by extending hydration, delaying hardening, and preventing trampling.

## Environment

Wet Farmland runs on the logical server. It works in single-player through Minecraft's integrated server and can be installed on a dedicated server without requiring players to install it on their clients.

## Features

- **Extended hydration**: Hydrated farmland stays wet for a configurable number of days (4 days by default) before it begins to dry out.
- **Delayed hardening**: Dry farmland turns back into dirt after a configurable number of days (30 days by default).
- **Crop removal**: Crops above farmland are removed without drops when the farmland hardens (configurable).
- **Trampling protection**: Players and mobs cannot trample farmland (configurable).
- **Configurable behavior**: All time periods and feature toggles can be adjusted in the configuration file.

## Download

[GitHub Releases](https://github.com/boluogiegie/forge-WetFarmland-1.20.1/releases)

## Installation

### Single-player

1. Install NeoForge for Minecraft 1.21.1.
2. Place the mod JAR file in the `.minecraft/mods/` folder.
3. Launch the game.

### Dedicated server

1. Install NeoForge for Minecraft 1.21.1 on the server.
2. Place the mod JAR file in the server's `mods` folder.
3. Players can connect without installing the mod on their clients.

## Configuration

The configuration file is located at `.minecraft/config/wetfarmland-common.toml`.

```toml
[general]
# Number of days farmland stays hydrated (default: 4)
wetPeriod = 4
# Number of days before dry farmland hardens into dirt (default: 30)
hardenPeriod = 30
# Whether to prevent farmland from being trampled
enableTrampling = true
# Whether to remove crops above farmland when it hardens
destroyCropsOnHarden = true
```

## License

Wet Farmland is licensed under the [MIT License](LICENSE.txt).
