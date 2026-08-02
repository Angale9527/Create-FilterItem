# Create-FilterItem

基于 NeoForge 1.21.1 的 Minecraft 模组项目（Create 过滤器相关扩展，开发中）。硬性前置：**Create ≥ 6.0.0**。

## 功能列表 (Features)

- 项目骨架已搭建（NeoForge ModDevGradle）
- 客户端模组入口 `CreateFilterItem`
- 已接入 Create 6.x 开发依赖（Create / Ponder / Flywheel / Registrate）
- 运行时在 `neoforge.mods.toml` 中要求 `create` 版本范围 `[6.0.0,)`
- 客户端命令 `/getfilter <item>`：对准 Stock Ticker 时发送 `StockKeeperCategoryRefundPacket`，用命令参数中的过滤器（含组件/NBT）复现退货发包
- 客户端命令 `/getfilter gui`：打开半透明界面，三选一过滤器类型 + 输入 `[]` 内内容，绕过聊天长度限制后同样发包

## 安装与配置 (Installation & Configuration)

1. 安装 JDK 21
2. 按需调整 `gradle.properties` 中的 `org.gradle.java.home` 与代理设置
3. 确保能访问 Create Maven（`https://maven.createmod.net`）与 Registrate Maven
4. 构建：

```bat
.\gradlew.bat build
```

5. 游戏内需同时安装 **Create ≥ 6.0.0**（与本模组同加载器：NeoForge 1.21.1）

开发环境编译钉死版本见 `gradle.properties`（当前对应 Create 6.0.10 发布构建 `6.0.10-280`）。

## 使用说明 (Usage)

开发客户端：

```bat
.\gradlew.bat runClient
```

### `/getfilter`（客户端）

1. 准星对准 **Stock Ticker**（库存报机）
2. 聊天栏执行（无需手持物品）：

```text
/getfilter create:package_filter[]
/getfilter create:filter[]
/getfilter create:attribute_filter[]
```

方括号内可填任意物品组件/NBT（与原版 `/give` 物品参数语法一致）。服务端若按包内栈直接退货，背包会获得命令中指定的那份过滤器。

长 NBT 可用 GUI：

```text
/getfilter gui
```

在界面中选择过滤器类型，输入框只填 `[]` 内的内容（可留空，等同 `id[]`），对准 Stock Ticker 后点「确定」。

## 许可证 (License)

本项目采用 [MIT License](LICENSE)。

## 项目结构 (Project Structure)

```text
├── build.gradle
├── gradle.properties
├── LICENSE
├── README.md
├── docs/superpowers/
│   ├── plans/
│   └── specs/
└── src/main/
    ├── java/com/angale9527/createfilteritem/
    │   ├── CreateFilterItem.java
    │   └── client/
    │       ├── GetFilterCommand.java
    │       ├── GetFilterRefund.java
    │       └── GetFilterScreen.java
    ├── resources/assets/createfilteritem/
    └── templates/META-INF/neoforge.mods.toml
```

本地构建与运行产物（`.gradle/`、`build/`、`run/`、`runs/`、日志与崩溃报告等）已写入 `.gitignore`，不会提交到版本库。
