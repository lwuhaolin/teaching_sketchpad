# Geometry Teaching Engine

# Project Specification

项目总体设计文档

版本：

v1.0

---

# 1. 项目介绍

## 项目名称

Geometry Teaching Engine

中文：

几何教学引擎

---

# 2. 项目定位

这是一个：

基于 Java + OpenGL 的跨 Windows 平台几何教学软件。

目标：

通过二维和三维交互方式：

帮助学生理解：

- 平面几何
- 立体几何
- 空间关系
- 几何变换
- 几何公式

---

# 3. 核心理念

软件不是普通3D查看器。

而是：

Geometry Engine

Interactive System

Teaching System

Animation System

最终形成：

可用于：

- 学校教学
- 几何演示
- 自主学习
- 教学课件制作

的软件平台。

---

# 4. 技术路线

## 编程语言

主要：

Java 8

原因：

- Windows 7兼容
- 稳定
- 跨平台

---

## 图形技术

3D：

LWJGL

OpenGL

负责：

- Mesh渲染
- 摄像机
- 光照
- 图形显示

---

## UI技术

采用：

Swing

原因：

- Windows兼容性高
- 不依赖新版系统API

---

## 构建工具

使用：

Maven

---

# 5. 系统整体架构

整体结构：

Application

|

|

+----------------+

| |

UI Layer Core Engine

| |

| |

Event System Geometry System

                 |

                 |

          Renderer System


                 |

                 |

             OpenGL

---

# 6. 核心模块

项目分为：

core

renderer

scene

interaction

tools

teaching

cut

animation

persistence

ui

application

---

# 7. Geometry Core

核心几何数据系统。

所有二维和三维对象：

统一使用：

Mesh

禁止：

单独创建：

Point2D

Circle2D

Shape2D

---

# 8. 统一空间模型

二维图形：

实际上是三维空间特殊情况。

规则：

z = 0

例如：

矩形：

(x1,y1,0)

(x2,y1,0)

(x2,y2,0)

(x1,y2,0)

---

# 9. 基础几何结构

必须包含：

## Vec3

三维向量。

```text
x

y

z
Vertex

顶点。

包含：

position

normal

uv
Edge

边。

连接两个Vertex。

Face

面。

通常：

三角面。

Mesh

网格。

由：

Vertex

Face

组成。

10. Geometry Object

所有图形：

必须实现：

GeometryObject

接口：

Mesh getMesh();

Transform getTransform();

11. 支持对象

第一版本支持：

2D
Point

Line

Circle

Rectangle

Polygon
3D
Cube

Sphere

Cylinder

Cone
12. 曲面处理原则

圆：

不是数学无限圆。

使用：

Triangle Mesh Approximation

例如：

Cylinder：

segments=32


生成：

32个侧面
13. Renderer系统

负责：

Mesh

↓

GPU

↓

Screen


不负责：

几何计算
教学逻辑
输入处理
14. 双模式渲染

支持：

2D模式

特点：

Orthographic Projection

限制：

z=0
3D模式

特点：

Perspective Projection

支持：

摄像机旋转
缩放
空间观察
15. Scene系统

负责管理：

GeometryObject集合

支持：

添加
删除
查询
选择
渲染
16. Interaction系统

负责：

用户输入。

包括：

鼠标
键盘
拾取
拖动
17. Tool系统

所有操作工具化。

例如：

SelectTool

MoveTool

RotateTool

ScaleTool

DrawTool

MeasureTool

CutTool

禁止：

大量：

if/else
18. Teaching系统

教学功能。

包括：

测量

支持：

长度
角度
面积
体积
标注

支持：

文字
尺寸线
辅助线
19. Cut系统

实现：

Plane

+

Mesh

↓

New Mesh

支持：

平面切割
截面生成
剖面展示
20. Animation系统

用于教学演示。

支持：

旋转
平移
缩放
展开
分解
21. Persistence系统

项目文件：

.gtp

保存：

场景
对象
参数
标注
动画
22. UI系统

界面组成：

Toolbar

Scene Tree

3D View

Property Panel

Teaching Panel

23. 文件格式设计

原则：

保存参数。

不保存：

完整Mesh。

例如：

保存：

{
"type":"Cylinder",

"radius":5,

"height":10

}

加载：

重新生成Mesh。

24. Windows兼容目标

必须支持：

Windows 7

Windows 10

Windows 11
25. 性能目标

最低配置：

CPU:

Intel i3


RAM:

4GB


GPU:

Intel HD Graphics

26. 性能优化原则

必须：

Mesh缓存
减少Draw Call
避免频繁GC
控制内存分配
27. 项目开发阶段

按照：

Phase 01

Geometry Core


Phase 02

Geometry Object


Phase 03

Renderer


Phase 04

Scene


Phase 05

Interaction


Phase 06

Tool


Phase 07

Teaching


Phase 08

Cut


Phase 09

Animation


Phase 10

Persistence


Phase 11

UI


Phase 12

Packaging

28. 开发原则
原则1

先架构，后功能。

不要快速堆代码。

原则2

模块必须独立。

例如：

Geometry不能依赖UI。

原则3

所有功能考虑扩展。

未来可能增加：

VR
Web版本
更多几何体
AI教学助手
29. AI开发要求

AI开发代码时必须：

先阅读：

PROJECT_SPECIFICATION.md

再阅读：

AGENTS.md

按照：

Phase文档

逐阶段开发。

30. 禁止事项

禁止：

大量硬编码
单文件巨型代码
UI和核心耦合
几何对象特殊判断
直接修改Mesh实现动画
使用Windows专属API
31. 最终目标

构建一个：

稳定

可扩展

低性能消耗

支持Win7

支持2D/3D

面向教学


的几何交互软件平台。
```
