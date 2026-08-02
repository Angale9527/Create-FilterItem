# `/getfilter` 退货发包复现命令 实现计划

> **面向 AI 代理的工作者：** 必需子技能：使用 superpowers:executing-plans 逐任务实现此计划。步骤使用复选框（`- [ ]`）语法来跟踪进度。

**目标：** 注册客户端命令 `/getfilter <item>`，对准 Stock Ticker 时发送 `StockKeeperCategoryRefundPacket`，用命令参数中的过滤器（含 NBT/组件）完成退货复现。

**架构：** 客户端 `RegisterClientCommandsEvent` 注册字面量 `getfilter` + 原版 `ItemArgument`；执行时用准星 `BlockHitResult` 取 `StockTickerBlockEntity` 坐标，将解析出的 `ItemStack` 经 Catnip 网络发到服务端。

**技术栈：** NeoForge 1.21.1、Create 6.x、Brigadier `ItemArgument`、`CatnipServices.NETWORK`

---

## 文件结构

| 文件 | 职责 |
|---|---|
| `src/main/java/.../client/GetFilterCommand.java` | 注册命令 + 执行校验 + 发包 |
| `src/main/java/.../CreateFilterItem.java` | 将命令类挂到 NeoForge 游戏总线 |
| `README.md` | 功能与用法 |

本功能为客户端联调命令，无独立单元测试基建；以 `compileJava` + 游戏内手动路径验证。

---

### 任务 1：实现 `/getfilter` 客户端命令

**文件：**
- 创建：`src/main/java/com/angale9527/createfilteritem/client/GetFilterCommand.java`
- 修改：`src/main/java/com/angale9527/createfilteritem/CreateFilterItem.java`
- 修改：`README.md`

- [x] **步骤 1：** Baseline `.\gradlew.bat compileJava`
- [x] **步骤 2：** 实现 `GetFilterCommand`（`ItemArgument`、准星 Stock Ticker、`StockKeeperCategoryRefundPacket`）
- [x] **步骤 3：** 通过 `@EventBusSubscriber` 挂接 `RegisterClientCommandsEvent`
- [x] **步骤 4：** 更新 README 功能列表与 Usage
- [x] **步骤 5：** `.\gradlew.bat compileJava` 验证（BUILD SUCCESSFUL）
- [ ] **步骤 6：** Commit（仅具体改动文件）
