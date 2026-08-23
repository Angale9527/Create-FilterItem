# Create-FilterItem

客户端模组：对准 Create Stock Ticker 时用 `/getfilter` 发送 `StockKeeperCategoryRefundPacket`。同一仓库并行维护 **NeoForge 1.21.1** 与 **Forge 1.20.1** 两套子项目（共用 Gradle Wrapper，不抽 common）。

## 功能列表 (Features)

- 双版本 Gradle 工程：`neoforge-1.21.1`（Java 21）与 `forge-1.20.1`（Java 17 工具链）
- 客户端模组入口 `CreateFilterItem`（两边各一份）
- 已接入 Create 6.x 开发依赖（Create / Ponder / Flywheel / Registrate）
- 客户端命令 `/getfilter <item>`：对准 Stock Ticker 时发送退货包，用命令参数中的过滤器复现发包
- 客户端命令 `/getfilter gui`：打开半透明界面，绕过聊天长度限制后同样发包
- GUI 支持两种输入：短参数（1.21.1 组件括号 `[]` / 1.20.1 花括号 NBT `{}`）与完整物品 SNBT

## 安装与配置 (Installation & Configuration)

1. 安装 **JDK 21**（根 `org.gradle.java.home` 指向它即可；Forge 子项目由 Foojay 解析 Java 17 工具链）
2. 按需调整根目录 `gradle.properties` 中的 `org.gradle.java.home` 与代理设置
3. 确保能访问 Create Maven（`https://maven.createmod.net`）以及各子项目 `build.gradle` 中的其它仓库
4. 一次构建两个版本：

```bat
.\gradlew.bat build
```

只编某一个：

```bat
.\gradlew.bat :neoforge-1.21.1:compileJava
.\gradlew.bat :forge-1.20.1:compileJava
```

5. 游戏内需安装对应加载器上的 Create：
   - NeoForge 1.21.1：编译钉 **Create 6.0.10-280**，运行时要求 `create` `[6.0.0,)`
   - Forge 1.20.1：钉死 **Create 6.0.8-289**（该 MC 版本已停止功能更新），运行时要求 `create` `[6.0.8,6.1.0)`

版本钉在各子项目的 `gradle.properties`，不要改根目录属性文件。子项目的 `generateModMetadata` 按 **UTF-8** 展开 `mods.toml` / `neoforge.mods.toml`（含中文 description），避免中文 Windows 默认编码把元数据写坏导致 `runClient` 直接崩溃。

## 使用说明 (Usage)

开发客户端（在仓库根目录执行）：

```bat
.\gradlew.bat :neoforge-1.21.1:runClient
.\gradlew.bat :forge-1.20.1:runClient
```

或用 **Gradle for Java** 侧边栏（不要点根项目）：

1. 打开活动栏的 **Gradle**（大象图标）
2. 展开 `Create-FilterItem` → `neoforge-1.21.1` 或 `forge-1.20.1`
3. 构建：`Tasks` → `build` → `build`（或 `compileJava`）
4. 启动客户端：`Tasks` → `neoforge` / `moddev` / `other` 里点 `runClient`（任务名就是 `runClient`）
5. 同一时间只跑一份 Gradle，不要和终端并行

运行和调试里用 **`neoforge-1.21.1 - Client`** / **`forge-1.20.1 - Client`**。第一次请先用上面的 `runClient` 写出 `build/moddev/` 参数文件，之后 F5 才能启动。不要选已删除的根配置 `Client` / `Data`。

### `/getfilter`（客户端）

1. 准星对准 **Stock Ticker**（库存报机）
2. 聊天栏执行（无需手持物品）：

NeoForge 1.21.1（组件括号）：

```text
/getfilter create:package_filter[]
/getfilter create:filter[]
/getfilter create:attribute_filter[]
```

Forge 1.20.1（花括号 NBT）：

```text
/getfilter create:package_filter{}
/getfilter create:filter{}
/getfilter create:attribute_filter{}
```

括号/花括号内可填该版本原版 `/give` 物品参数语法。服务端若按包内栈直接退货，背包会获得命令中指定的那份过滤器。

长 NBT 可用 GUI：

```text
/getfilter gui
```

界面顶部切换输入方式：

1. **短参数**：选择过滤器类型，输入框只填括号内内容（可留空）
   - 1.21.1：组件括号 `[]`，等同 `id[]`
   - 1.20.1：花括号 NBT `{}`，等同 `id{}`
2. **SNBT**：粘贴完整物品 SNBT。两版本格式不同：

```text
# NeoForge 1.21.1（Data Components）
{components:{...},count:1,id:"create:filter"}

# Forge 1.20.1（NBT）
{id:"create:filter",Count:1b,tag:{...}}
```

对准 Stock Ticker 后点「确定」。外层若带一对引号会自动剥掉。

改玩法或修行为级 bug 时，`neoforge-1.21.1/` 与 `forge-1.20.1/` 需要各改一遍。

## 许可证 (License)

本项目采用 [MIT License](LICENSE)。

## 项目结构 (Project Structure)

```text
├── build.gradle
├── gradle.properties          # 仅 JVM / 代理 / java.home
├── settings.gradle            # include 两个子项目
├── LICENSE
├── README.md
├── neoforge-1.21.1/
│   ├── build.gradle
│   ├── gradle.properties
│   └── src/main/
│       ├── java/com/angale9527/createfilteritem/
│       ├── resources/assets/createfilteritem/
│       └── templates/META-INF/neoforge.mods.toml
└── forge-1.20.1/
    ├── build.gradle
    ├── gradle.properties
    └── src/main/
        ├── java/com/angale9527/createfilteritem/
        ├── resources/assets/createfilteritem/
        └── templates/META-INF/mods.toml
```

本地构建与运行产物（`.gradle/`、`build/`、`run/`、`runs/`、日志与崩溃报告等）已写入 `.gitignore`，不会提交到版本库。
