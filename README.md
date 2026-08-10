# Geometry Teaching Engine

Java + LWJGL 几何教学引擎

## 项目介绍

一个基于 Java + OpenGL 的跨 Windows 平台几何教学软件，
通过二维和三维交互方式帮助学生理解：

- 平面几何
- 立体几何
- 空间关系
- 几何变换
- 几何公式

## 技术栈

| 组件 | 技术 |
|------|------|
| 编程语言 | Java 8 |
| 图形渲染 | LWJGL 3 + OpenGL |
| 数学库 | JOML |
| UI 框架 | Swing |
| 构建工具 | Maven |

## 系统要求

- OS: Windows 7 / 10 / 11
- JDK: Java 8 (1.8+)
- 最低硬件: Intel i3 / 4GB RAM / Intel HD Graphics

## 当前开发阶段

**Phase 01 - Geometry Core** ✅

统一几何数据层已建立，Vec3/Matrix4/Mesh/Transform/GeometryObject 接口全部实现，27项单元测试通过。

## 如何运行

### 方式一：Maven 直接运行

```bash
mvn exec:java
```

### 方式二：编译后运行

```bash
mvn package
java -jar target/geometry-teaching-engine-1.0-SNAPSHOT.jar
```

### 方式三：IDE 运行

在 IntelliJ IDEA 中打开项目，直接运行 `Main` 类。

## 项目结构

```
teaching_sketchpad/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/geometry/
│   │   │       └── app/
│   │   │           └── Main.java
│   │   └── resources/
│   └── test/
│       ├── java/
│       └── resources/
└── docs/
    ├── PROJECT_SPEC.md
    ├── PHASE_00_SETUP.md
    └── ...
```

## 开发阶段

| Phase | 模块 | 状态 |
|-------|------|------|
| 00 | 项目初始化 | ✅ 完成 |
| 01 | Geometry Core | ✅ 完成 |
| 02 | Geometry Object | 待开发 |
| 03 | Renderer | 待开发 |
| 04 | Scene System | 待开发 |
| 05 | Interaction System | 待开发 |
| 06 | Tool System | 待开发 |
| 07 | Teaching System | 待开发 |
| 08 | Cut System | 待开发 |
| 09 | Animation System | 待开发 |
| 10 | Persistence | 待开发 |
| 11 | UI System | 待开发 |
| 12 | Packaging | 待开发 |

## 开发规范

详细开发规则请参考项目根目录文档：

- `PROJECT_SPECIFICATION.md` - 项目总体设计文档
- `AGENTS.md` - AI Agent 开发指南
- `docs/PHASE_xx_*.md` - 各阶段详细需求

## MAVEN仓库
- Maven 本地仓库在 D:\JAVA_Doc\Maven\apache-maven-3.9.11\mev_repo

## 构建

```bash
# 编译
mvn compile

# 测试
mvn test

# 打包
mvn package

# 清理
mvn clean
```
# teaching_sketchpad
