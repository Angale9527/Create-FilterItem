# `/getfilter` 客户端退货发包复现命令 — 设计规格

日期：2026-08-03  
状态：待用户审查（已按最新意图修订）

## 目标

用客户端命令复现 Create `StockKeeperCategoryScreen` 删除分类时的发包路径：  
服务端 `StockKeeperCategoryRefundPacket` 在未校验 categories 归属的情况下，把包内任意 `FilterItem` 退回背包。

## 行为

| 项 | 约定 |
|---|---|
| 命令 | `/getfilter <item>` |
| 示例 | `/getfilter create:package_filter[]`、`/getfilter create:filter[{...}]` |
| `<item>` | 使用原版物品参数解析（含可选组件/NBT 语法），结果必须是非空且为 Create `FilterItem` |
| 注册侧 | 仅客户端（`RegisterClientCommandsEvent`） |
| 物品来源 | **命令参数构造的 ItemStack**（不要求主手有物品） |
| 目标方块 | 准星命中的方块；BE 须为 `StockTickerBlockEntity` |
| 发包 | `CatnipServices.NETWORK.sendToServer(new StockKeeperCategoryRefundPacket(pos, parsedStack))` |

退货得到的过滤器与命令参数解析出的栈一致（含括号内任意 NBT/组件）。

## 校验与反馈

1. 非客户端世界 / 无本地玩家 → 失败提示  
2. 物品参数解析失败 → 由 Brigadier 报错  
3. 解析结果不是 `FilterItem`（或为空）→ 失败：「不是 Create 过滤器」  
4. 准星未命中方块，或方块实体不是 Stock Ticker → 失败：「请对准 Stock Ticker」  
5. 通过后发包，成功提示：坐标 + 物品显示名  

不做：主手读取、坐标参数、服务端命令注册、GUI 修改、自动伪造默认过滤器（必须显式写物品 ID）。

## 文件范围（实现时预计 ≤ 4）

1. 新建客户端命令类（注册 + 执行 `/getfilter`）  
2. 在客户端入口挂接 `RegisterClientCommandsEvent`  
3. 更新 `README.md`  
4. 本规格文档（修订）  

## 成功标准

对准任意 Stock Ticker，执行例如：

```text
/getfilter create:package_filter[]
```

后，背包获得带有命令中指定 NBT/组件的对应过滤器；可重复执行以验证无归属校验。

## 验证

- `.\gradlew.bat compileJava`  
- 游戏内：未对准 / 非 FilterItem / 合法 `create:filter[...]` / `create:attribute_filter[...]` / `create:package_filter[...]`  
