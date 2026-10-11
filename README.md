<div align="center">

# RedirectTip

**一个轻量、好用的 Velocity 群组服「加入 / 跳转 / 离开」全群组广播提示插件**

[![Velocity](https://img.shields.io/badge/Velocity-3.3.0%2B%20%7C%204.x-3b82f6?style=flat-square)](https://papermc.io/software/velocity)
[![Java](https://img.shields.io/badge/Java-17%2B-orange?style=flat-square)](https://adoptium.net/)
[![Version](https://img.shields.io/badge/version-0.3-22c55e?style=flat-square)](#)
[![License](https://img.shields.io/badge/license-GPL--3.0-blue?style=flat-square)](#开源协议)

</div>

---

RedirectTip 会在玩家 **首次上线**、**在子服之间跳转**、**退出整个群组** 时，向全群组（所有子服的所有在线玩家）广播一条提示消息。

所有文字、符号、颜色都可以在 `config.yml` 里自由修改，改完用 `/redirecttip reload` 即可生效。

## 目录

- [功能特性](#功能特性)
- [效果预览](#效果预览)
- [环境要求](#环境要求)
- [安装步骤](#安装步骤)
- [配置说明](#配置说明)
- [命令与权限](#命令与权限)
- [常见问题](#常见问题)
- [构建](#构建)
- [项目结构](#项目结构)
- [开源协议](#开源协议)

## 功能特性

| 特性 | 说明 |
| --- | --- |
| 🎯 三种提示 | 加入 `[+]`（绿色）、跳转 `[»]`（黄色）、离开 `[-]`（红色），互不干扰 |
| 📢 全群组广播 | 消息发送给所有子服的所有在线玩家 |
| 👤 触发玩家可见 | 触发事件的玩家本人也会收到（延迟发送，避免切服瞬间消息丢失） |
| 🖥️ 控制台记录 | 三种提示同步打印到 Velocity 控制台，方便留档与排查 |
| ⚙️ 完全可配置 | 文字、符号、颜色全部写在 `config.yml`，不需要动代码 |
| 🌈 彩色字体 | 支持 `&0`-`&f`、`&l/o/n/m/k/r` 以及 `&#RRGGBB` 十六进制颜色 |
| 🏷️ 子服名称映射 | 把 `survival` 这样的 ID 显示成「生存服」 |
| 🔄 热重载 | `/redirecttip reload` 修改配置后立即生效 |
| 🪶 轻量 | 仅依赖 Velocity API，零第三方运行时依赖 |

## 效果预览

```
[+] Steve 加入了服务器                    ← 加入提示（绿色）[»] Steve 从 生存服 跳转到了 空岛服        ← 跳转提示（黄色）
[-] Steve 离开了服务器                    ← 离开提示（红色）
```

对应的默认配置：

```yaml
messages:
  join:   "&a[+] &e%player% &7加入了服务器"
  switch: "&e[»] &e%player% &7从 &e%from% &7跳转到了 &e%server%"
  leave:  "&c[-] &e%player% &7离开了服务器"
```

> 符号（`+` / `»` / `-`）与颜色都只是配置字符串的一部分，你可以随意换成 `→`、`⇄`、`>>` 或任意颜色。

## 环境要求

| 项目 | 要求 |
| --- | --- |
| 代理端 | Velocity **3.3.0+ / 4.x**（编译基于 velocity-api 4.0.0） |
| Java | **17+** |

## 安装步骤

1. 从 [Releases](../../releases) 下载 `RedirectTip-1.0.0.jar`（或自行构建）
2. 把 jar 放入 Velocity 的 `plugins/` 文件夹
3. 重启代理端
4. 首次启动会自动生成 `plugins/redirecttip/config.yml`，按需修改后执行 `/redirecttip reload`

## 配置说明

```yaml
# ============================================================
# RedirectTip 配置文件
#
# 颜色代码支持:
#   &0-&9 基础颜色, &a-&f 颜色, &l 加粗, &o 斜体, &n 下划线,
#   &m 删除线, &k 乱码, &r 重置
#   十六进制颜色: &#RRGGBB (例如 &#ff5555) 或 &x&f&f&5&5&5&5
#
# 占位符:
#   %player% - 玩家名
#   %server% - 子服显示名(来自下方 server-names 映射, 未配置则显示子服ID)
#   %from%   - 跳转前所在的子服显示名(仅 switch 消息可用)
# ============================================================

# 玩家首次进入代理(第一次连上子服, 即"上线")时, 是否也广播 [+] 加入提示
# 设为 false 则只在子服之间跳转时提示 [»]
announce-on-first-join: true

# 发给"触发事件的玩家本人"的消息延迟(毫秒)
# 玩家刚进入/切换服务器时, 客户端可能正处于加载过渡阶段, 聊天消息容易丢失
# 延迟一小段时间再发给本人可避免这种情况; 设为 0 则立即发送
# 注意: 其他玩家不受影响, 始终即时收到
self-message-delay-ms: 1000

# 子服ID 对应的显示名称
# 格式: 服务器ID(与 velocity.toml 中的名称一致): 显示名称
server-names:
  lobby: "&a大厅"
  survival: "&6生存服"
  skyblock: "&b空岛服"
  # 更多示例:
  # creative: "&d创造服"
  # minigame: "&e小游戏服"

# 语言/消息显示配置
# 默认配色: [+] 绿色(加入), [»] 黄色(跳转), [-] 红色(离开), 可自行修改
messages:
  # 玩家首次进入代理(上线)时, 向全群组广播
  join: "&a[+] &e%player% &7加入了服务器"
  # 玩家在子服之间跳转时, 向全群组广播
  switch: "&e[»] &e%player% &7从 &e%from% &7跳转到了 &e%server%"
  # 玩家离开整个群组(退出代理, 不是切换子服)时, 向全群组广播
  leave: "&c[-] &e%player% &7离开了服务器"
```

### 占位符

| 占位符 | 说明 | 可用消息 |
| --- | --- | --- |
| `%player%` | 玩家名 | 全部 |
| `%server%` | 子服显示名（取 `server-names` 映射，未配置则显示子服 ID） | `join` / `switch` |
| `%from%` | 跳转前所在的子服显示名 | 仅 `switch` |

### 颜色代码

- **传统代码**：`&0`-`&9`、`&a`-`&f`、`&l`(加粗)、`&o`(斜体)、`&n`(下划线)、`&m`(删除线)、`&k`(乱码)、`&r`(重置)
- **十六进制**：`&#RRGGBB`（例如 `&#ff5555`）或 `&x&f&f&5&5&5&5`

## 命令与权限

| 命令 | 权限 | 说明 |
| --- | --- | --- |
| `/redirecttip reload`<br>别名：`/rtip reload` | `redirecttip.reload` | 热重载配置文件 |

## 常见问题

**Q：为什么触发玩家本人要延迟 1 秒才看到消息？**
A：玩家刚进入/切换服务器时，客户端还在加载过渡阶段，此时聊天消息容易丢失。延迟一小段时间再发给本人更可靠。其他玩家不受影响、始终即时收到。想立即发送可把 `self-message-delay-ms` 设为 `0`。

**Q：为什么「离开」提示，玩家本人看不到？**
A：玩家退出代理的那一刻已经断开连接，消息无法送达（本人已不在线）。其他玩家能看到，控制台也会打印记录，属于正常现象。

**Q：首次上线没有提示？**
A：检查 `config.yml` 里的 `announce-on-first-join` 是否为 `true`。

**Q：改了配置没生效？**
A：执行 `/redirecttip reload`；如果是刚替换了 jar，需要重启代理端。

**Q：提示里还是显示英文子服 ID？**
A：在 `server-names` 里添加对应映射，例如 `survival: "生存服"`。

## 构建

需要 JDK 17+ 与 Maven 3.6+：

```bash
mvn clean package
```

构建产物：`target/RedirectTip-0.3.jar`

## 项目结构

```
RedirectTip/
├── pom.xml
├── README.md
├── RELEASE_NOTES.md
└── src/main
    ├── java/com/redirecttip
    │   ├── RedirectTipPlugin.java    # 插件主类：注册监听器与命令
    │   ├── RedirectListener.java     # 监听加入/跳转/离开事件并广播
    │   ├── PluginConfig.java         # 配置加载与重载
    │   ├── MessageUtil.java          # & 颜色代码 / 十六进制颜色解析
    │   └── ReloadCommand.java        # /redirecttip reload 命令
    └── resources
        ├── velocity-plugin.json      # Velocity 插件元数据
        └── config.yml                # 默认配置（首次运行自动生成）
```

### 实现要点

- 监听 `ServerConnectedEvent`：通过 `getPreviousServer()` 是否为空，区分「首次上线」与「子服间跳转」
- 监听 `DisconnectEvent`：通过 `getLoginStatus() == SUCCESSFUL_LOGIN` 排除未正常登录的情况，实现「离开群组」提示
- 广播对象为 `ProxyServer#getAllPlayers()`，其中触发玩家本人延迟发送（`self-message-delay-ms`）
- 颜色解析使用 Adventure 的 `LegacyComponentSerializer.legacyAmpersand()`，并预处理 `&#RRGGBB` 十六进制颜色
- 配置解析使用 Velocity 自带的 Configurate（YAML），零额外依赖

## 开源协议

本项目采用 **GNU General Public License v3.0**（GPL-3.0）开源。

你可以自由使用、修改和分发本项目，但**基于本项目的衍生作品必须以相同协议（GPL-3.0）开源**，并保留原作者版权声明。完整协议文本见 [LICENSE](LICENSE) 文件。

---

<details>
<summary><b>English Overview</b></summary>

**RedirectTip** is a lightweight Velocity proxy plugin that broadcasts join / server-switch / quit messages to every player on the network.

- Three distinct messages: join `[+]` (green), server switch `[»]` (yellow), quit `[-]` (red)
- Fully configurable text, symbols and colors in `config.yml`
- Legacy `&` color codes and `&#RRGGBB` hex colors supported
- Maps server IDs to friendly display names
- Console logging + `/redirecttip reload` hot reload
- Requires Velocity 3.3.0+ / 4.x and Java 17+

</details>
