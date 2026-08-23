# Create-FilterItem

客户端模组。对准机械动力的 **仓储发报机（Stock Ticker）** 后，用 `/getfilter` 发送退货请求，让服务端按命令里指定的过滤器直接退货（不校验过滤器内容）。

支持 **NeoForge 1.21.1** 与 **Forge 1.20.1**，均为客户端安装即可。

## 功能

- `/getfilter <物品>`：对准仓储发报机时发包，用命令参数里的过滤器复现退货
- `/getfilter gui`：打开半透明界面，方便粘贴较长参数，效果与命令相同
- 界面支持两种输入：短参数（1.21.1 用 `[]`，1.20.1 用 `{}`）和完整物品 SNBT
- 命令会补全三种过滤器：列表过滤器、属性过滤器、包裹过滤器

## 安装

本模组只装在客户端。游戏版本与前置需对应：

| 游戏版本 | 加载器 | 机械动力 |
| --- | --- | --- |
| Minecraft 1.21.1 | NeoForge 21.1.x | Create 6.0.0 及以上 |
| Minecraft 1.20.1 | Forge 47.x | Create 6.0.0 及以上 |

把对应版本的 jar 放进 `.minecraft/mods`，与 Create 一起加载即可。无需在专用服务器上安装本模组。

## 构建

需要 **JDK 21**。在仓库根目录执行：

```bat
.\gradlew.bat build
```

构建完成后，在下列目录取 jar：

- NeoForge 1.21.1：`neoforge-1.21.1/build/libs/`
- Forge 1.20.1：`forge-1.20.1/build/libs/`

只编其中一个版本：

```bat
.\gradlew.bat :neoforge-1.21.1:build
.\gradlew.bat :forge-1.20.1:build
```

## 使用

无需手持物品。准星对准 **仓储发报机**，在聊天栏执行命令。

### 命令

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

括号或花括号内可按该版本原版 `/give` 的物品参数填写。若服务端按包内物品直接退货，背包会得到命令中指定的那份过滤器。

### 界面

参数较长时用界面：

```text
/getfilter gui
```

顶部可切换输入方式：

1. **短参数**：选择过滤器类型，输入框只填括号内的内容（可留空）
   - 1.21.1：`[]`，等同 `id[]`
   - 1.20.1：`{}`，等同 `id{}`
2. **SNBT**：粘贴完整物品 SNBT。两个版本格式不同：

```text
# NeoForge 1.21.1
{components:{...},count:1,id:"create:filter"}

# Forge 1.20.1
{id:"create:filter",Count:1b,tag:{...}}
```

对准仓储发报机后点「确定」。若整段外层多包了一对引号，会自动去掉。

## 许可证

本项目采用 [MIT License](LICENSE)。
