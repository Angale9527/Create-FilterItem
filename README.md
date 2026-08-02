# Create-FilterItem

基于 NeoForge 1.21.1 的 Minecraft 模组项目（Create 过滤器相关扩展，开发中）。

## 功能列表 (Features)

- 项目骨架已搭建（NeoForge ModDevGradle）
- 客户端模组入口 `CreateFilterItem`

## 安装与配置 (Installation & Configuration)

1. 安装 JDK 21
2. 按需调整 `gradle.properties` 中的 `org.gradle.java.home` 与代理设置
3. 构建：

```bat
.\gradlew.bat build
```

## 使用说明 (Usage)

当前仅为模组骨架，尚无游戏内玩法内容。

## 许可证 (License)

本项目采用 [MIT License](LICENSE)。

## 项目结构 (Project Structure)

```text
├── build.gradle
├── gradle.properties
├── LICENSE
├── README.md
└── src/main/
    ├── java/com/angale9527/createfilteritem/
    ├── resources/assets/createfilteritem/
    └── templates/META-INF/neoforge.mods.toml
```
