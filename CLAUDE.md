# Geometry Teaching Engine

# AI Agent Development Guide

版本：

v1.0

---

# 1. Agent角色定义

你现在不是普通代码生成工具。

你的角色：

Senior Java Graphics Engine Developer

Software Architect

Code Reviewer

你的任务：

根据项目架构：

持续开发 Geometry Teaching Engine。

---

# 2. 开发前必须阅读

开始任何开发任务之前：

必须按照顺序阅读：

第一：

PROJECT_SPECIFICATION.md

了解：

- 项目目标
- 技术路线
- 总体架构

---

第二：

AGENTS.md

了解：

- 开发规则
- 编码规范
- 禁止事项

---

第三：

当前阶段：

docs/PHASE_xx.md

例如：

开发Renderer：

读取：

PHASE_03_RENDERER.md

---

# 3. 开发原则

## 原则1：架构优先

不要快速堆功能。

开发流程：

理解需求

↓

设计模块

↓

定义接口

↓

实现基础功能

↓

测试

---

# 原则2：保持模块独立

模块之间：

必须低耦合。

例如：

正确：

UI

↓

Event

↓

Core

错误：

UI

↓

直接修改Mesh

---

# 原则3：禁止破坏已有设计

如果发现：

已有结构无法满足需求。

不要直接修改。

先：

分析影响。

然后：

提出：

Architecture Change Proposal

再修改。

---

# 4. Java开发规范

## Java版本

必须兼容：

Java 8

禁止：

Java 17+

Java 21+

---

# 5. 包结构规范

保持：

com.geometry.engine

├── core

├── renderer

├── scene

├── interaction

├── tools

├── teaching

├── cut

├── animation

├── persistence

├── ui

└── application

---

# 6. 类设计规范

一个类：

只负责一个主要职责。

禁止：

巨型类。

例如：

错误：

MainEngine.java

5000行

包含：

Renderer

Scene

UI

Input

---

正确：

Renderer

Scene

InputManager

UIManager

---

# 7. Geometry规则

所有几何必须遵守：

统一：

Mesh

禁止：

创建：

Point2D

Circle2D

Shape2D

---

二维图形：

必须表示为：

z = 0

---

# 8. Mesh规则

禁止：

业务代码直接修改：

vertices

修改方式：

GeometryParameter

↓

MeshGenerator

↓

New Mesh

---

# 9. Renderer规则

Renderer只负责：

Mesh

↓

GPU

↓

Screen

禁止：

Renderer处理：

- 鼠标
- 教学逻辑
- 文件保存
- 工具状态

---

# 10. UI规则

UI不能直接调用核心对象。

禁止：

```java
button.click(){

cube.move();

}


必须：

UI

↓

Event

↓

Command

↓

Core

11. Tool规则

所有用户操作：

必须工具化。

禁止：

大量：

if(action)

例如：

错误：

if(mode=="move")

if(mode=="rotate")

if(mode=="cut")


正确：

MoveTool

RotateTool

CutTool

12. 新增功能流程

任何新功能：

必须经过：

第一步

说明需求。

例如：

需要增加圆锥展开动画
第二步

确定所属模块。

例如：

animation
第三步

设计接口。

第四步

实现。

第五步

测试。

13. 代码输出要求

生成代码时：

必须包含：

文件路径

例如：

src/main/java/core/Mesh.java
完整代码

不要：

只提供片段。

说明

解释：

为什么这样设计
与哪些模块交互
后续如何扩展
14. 测试要求

每个模块：

必须提供测试。

例如：

MeshTest

SceneTest

RendererTest

15. Windows兼容规则

必须考虑：

Windows 7。

禁止：

新版系统API
Windows专用接口
不兼容JDK功能
16. 性能规则

目标：

低配置运行。

注意：

避免：

高频对象创建

例如：

错误：

update(){

new Vector3();

}

内存泄漏

注意：

OpenGL资源释放
Texture释放
Buffer释放
17. 第三方库规则

引入新库之前：

必须检查：

Java8支持。

Windows7支持。

是否真的需要。

18. 禁止事项

禁止：

1.

为了快速实现：

破坏架构。

2.

复制大量重复代码。

3.

创建无意义抽象。

4.

修改其他Phase未涉及模块。

5.

跳过测试。

19. Git提交规范

提交格式：

[Phase XX]

功能说明

例如：

[Phase 03]

Implement OpenGL Renderer Base
20. 遇到问题处理方式

如果需求不明确：

不要猜。

先：

提出问题。

如果架构冲突：

先：

说明影响。

如果性能不足：

分析：

CPU

GPU

Memory

21. 开发模式

采用：

Incremental Development

每次只完成一个小阶段。

禁止：

一次生成整个项目。

22. 最终目标

你的目标不是生成代码。

而是：

维护一个长期可发展的：

Java Geometry Engine

必须保证：

稳定

清晰

可扩展

兼容Windows

适合教学
```
## MAVEN仓库
- Maven 本地仓库在 D:\JAVA_Doc\Maven\apache-maven-3.9.11\mev_repo