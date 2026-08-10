# Geometry Teaching Engine

## 项目说明文档（PROJECT_SPEC）

版本：

v1.0

项目类型：

Java + LWJGL + OpenGL 几何教学软件

---

# 1. 项目概述

## 1.1 项目名称

Geometry Teaching Engine

中文名称：

几何教学引擎

---

## 1.2 项目定位

Geometry Teaching Engine 是一个面向数学几何教学的软件平台。

项目目标不是制作游戏，而是建立一个：

> 可交互、可编辑、可展示、可扩展的二维/三维几何教学系统。

软件通过二维绘图和三维模型交互，让学生直观理解：

- 平面几何
- 空间几何
- 立体结构
- 图形变化
- 面积计算
- 体积计算
- 几何关系

---

# 2. 产品目标

## 2.1 二维几何能力

支持：

- 点
- 线段
- 射线
- 圆
- 多边形
- 坐标系
- 辅助线
- 标注

二维模式用于：

- 平面图形教学
- 几何证明辅助
- 图形绘制

---

## 2.2 三维几何能力

支持：

基础立体：

- 立方体
- 长方体
- 球体
- 圆柱
- 圆锥
- 棱柱

支持操作：

- 旋转观察
- 缩放
- 移动
- 分解
- 展开
- 剖切

---

## 2.3 教学交互能力

支持：

- 鼠标选择
- 拖拽
- 参数修改
- 动态变化
- 动画演示

例如：

调整圆柱高度：

修改 height

↓

重新生成 Mesh

↓

实时更新显示

---

# 3. 技术方案

## 3.1 开发语言

Java

原因：

- 跨平台
- 开发效率高
- 维护成本低
- 生态成熟

---

## 3.2 图形方案

使用：

LWJGL + OpenGL

原因：

- 性能足够
- 控制能力强
- 支持 Windows 7
- 适合教学软件

---

## 3.3 平台兼容

目标：

Windows 7
Windows 10
Windows 11

避免：

- WinUI
- UWP
- 新版 Windows API

---

# 4. 总体架构

采用分层设计。

结构：

Application Layer

    |

    ↓

UI Layer

    |

    ↓

Interaction Layer

    |

    ↓

Tool System

    |

    ↓

Scene System

    |

    ↓

Geometry Core

    |

    ↓

Renderer

    |

    ↓

OpenGL

---

# 5. 核心设计原则

## 5.1 数据与渲染分离

禁止：

Geometry对象直接调用OpenGL

正确：

Geometry

↓

Mesh

↓

Renderer

↓

OpenGL

---

# 5.2 统一几何模型

系统禁止设计：

Shape2D

Shape3D

所有对象统一：

GeometryObject

---

二维图形：

通过：

z = 0

表示。

例如：

矩形：

(x1,y1,0)

(x2,y1,0)

(x2,y2,0)

(x1,y2,0)

---

# 5.3 参数化设计

所有教学对象必须保存数学参数。

例如：

圆柱：

radius
height
segments

圆锥：

radius
height
segments

立方体：

width
height
depth

不能只保存 Mesh。

原因：

教学需要动态修改。

---

# 6. Geometry Core

核心目录：

core

├── math

├── mesh

├── geometry

└── transform

---

# 7. 数学基础

## Vec3

三维向量：

x
y
z

用途：

- 坐标
- 法线
- 方向

---

## Transform

对象变换：

包含：

position

rotation

scale

用于：

- 移动
- 旋转
- 缩放

---

# 8. Mesh系统

所有几何最终转换：

Triangle Mesh

结构：

Mesh

├── Vertex

├── Edge

├── Face

└── Triangle

---

# 9. 参数化几何对象

统一接口：

GeometryObject

包含：

Geometry Parameter

Mesh Cache

Transform

---

实现：

Rectangle

Circle

Polygon

Cube

Cylinder

Cone

Sphere

---

# 10. Mesh生成系统

提供：

MeshFactory

生成：

createRectangle()

createCircle()

createPolygon()

createCube()

createCylinder()

createCone()

createSphere()

---

曲面：

使用：

Triangle Mesh

近似。

例如：

圆柱：

圆周分段

↓

生成上下圆面

↓

连接侧面

---

# 11. Renderer

结构：

renderer

├── Renderer

├── OpenGLRenderer

├── Camera

├── Shader

└── Material

---

Renderer职责：

负责：

Mesh → Screen

不负责：

- 几何计算
- 用户操作
- 教学逻辑

---

# 12. Scene系统

Scene负责管理对象。

结构：

Scene

└── GeometryObject List

支持：

add()

remove()

find()

update()

render()

---

# 13. Tool系统

所有操作模块化。

接口：

Tool

实现：

MoveTool

RotateTool

ScaleTool

DrawTool

MeasureTool

CutTool

禁止：

大量：

if(type == xxx)

---

# 14. 切割系统

目标：

教学级几何切割。

不是工业 CAD。

结构：

Mesh

Plane

↓

MeshCutter

↓

New Mesh

支持：

- 平面切割
- 截面生成
- 剖面显示

---

# 15. 教学系统

预留：

## MeasureSystem

支持：

- 长度
- 面积
- 角度
- 体积

---

## AnimationSystem

支持：

- 自动旋转
- 展开
- 分解
- 组合

---

## AnnotationSystem

支持：

- 标签
- 箭头
- 尺寸线

---

# 16. Undo / Redo

采用：

Command Pattern

支持：

- 移动撤销
- 参数修改撤销
- 删除恢复
- 切割恢复

---

# 17. 项目目录

GeometryTeachingEngine

├── docs

├── src

│
└── main

└── java

    └── com.geometry


        ├── app

        ├── core

        │   ├── math

        │   ├── mesh

        │   ├── geometry

        │   └── transform


        ├── renderer


        ├── scene


        ├── interaction


        ├── tools


        ├── cutter


        ├── animation


        ├── measure


        ├── undo


        └── event

---

# 18. 开发规则

所有开发必须遵守：

1. 不破坏已有架构。

2. 新功能必须通过模块扩展。

3. Renderer不能包含业务逻辑。

4. Geometry不能依赖UI。

5. 禁止重复创建类似结构。

6. 每个模块必须可以独立测试。

---

# 19. 当前开发阶段

当前：

Phase 1
Geometry Core

目标：

完成：

- Vec3
- Transform
- Vertex
- Face
- Mesh
- GeometryObject

暂不实现：

- OpenGL
- UI
- 鼠标
- 切割

---

End
