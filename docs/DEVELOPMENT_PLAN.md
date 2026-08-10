# Geometry Teaching Engine

# 开发路线规划文档（DEVELOPMENT_PLAN）

版本：

v1.0

---

# 1. 开发目标

本项目采用分阶段开发方式。

原则：

- 先完成底层能力
- 再扩展功能
- 每个阶段必须可运行
- 禁止跨阶段堆叠代码

开发顺序：

基础架构

↓

几何核心

↓

渲染系统

↓

场景系统

↓

交互系统

↓

工具系统

↓

教学功能

↓

高级功能

---

# 2. 开发阶段总览

项目分为：

Phase 00
项目初始化

Phase 01
Geometry Core

Phase 02
Geometry Object

Phase 03
Renderer

Phase 04
Scene System

Phase 05
Interaction System

Phase 06
Tool System

Phase 07
Teaching System

Phase 08
Cut System

Phase 09
Animation System

Phase 10
Persistence System

Phase 11
优化与发布

---

# Phase 00

# 项目初始化

## 目标

建立基础 Java 项目。

## 内容

完成：

- Gradle/Maven 配置
- LWJGL依赖
- 项目目录
- 基础启动类

目录：

src/main/java

com.geometry

├── app

└── Main.java

---

## 输出结果

运行程序：

显示：

Geometry Teaching Engine Started

---

## 不实现

禁止：

- OpenGL绘制
- UI
- 几何对象

---

# Phase 01

# Geometry Core

## 目标

建立整个系统最核心的数据结构。

这是项目最重要阶段。

---

## 实现内容

数学模块：

core.math

├── Vec2

├── Vec3

├── Matrix4

└── MathUtil

---

几何基础：

core.mesh

├── Vertex

├── Edge

├── Face

└── Mesh

---

变换系统：

core.transform

└── Transform

---

对象接口：

GeometryObject

---

## 完成标准

必须可以：

创建：

Vec3

Vertex

Mesh

并生成：

Cube Mesh

测试：

输出：

Vertex Count

Face Count

---

## 不实现

禁止：

- OpenGL
- 鼠标
- UI

---

# Phase 02

# Geometry Object

## 目标

建立参数化几何对象系统。

---

## 核心思想

对象保存：

参数

Mesh缓存

不是：

只有Mesh

---

## 实现

基础对象：

Rectangle

Circle

Polygon

Cube

Cylinder

Cone

Sphere

---

## MeshFactory

实现：

createRectangle()

createCircle()

createCube()

createCylinder()

createCone()

createSphere()

---

## 完成标准

可以：

创建：

Cylinder

修改：

radius

height

自动重新生成 Mesh。

---

# Phase 03

# Renderer

## 目标

建立 OpenGL 渲染系统。

---

## 实现

窗口：

Window

Shader：

Shader

摄像机：

Camera

渲染器：

Renderer

OpenGLRenderer

---

## 支持

3D：

- 透视投影
- 深度测试
- 摄像机旋转

2D：

- 正交投影
- 平面显示

---

## 完成标准

窗口显示：

Cube

并可以：

鼠标旋转观察。

---

# Phase 04

# Scene System

## 目标

建立场景管理。

---

## 实现

Scene：

管理：

GeometryObject List

功能：

addObject()

removeObject()

findObject()

update()

render()

---

## 完成标准

场景中：

同时存在：

Cube

Rectangle

并正常显示。

---

# Phase 05

# Interaction System

## 目标

实现用户操作。

---

## 实现

输入：

MouseInput

KeyboardInput

管理：

InteractionManager

选择：

SelectionManager

---

## 支持

鼠标：

- 点击选择
- 拖动
- 缩放
- 旋转

---

## 完成标准

用户可以：

点击 Cube

拖动 Cube

改变位置。

---

# Phase 06

# Tool System

## 目标

将操作模块化。

---

## 架构

接口：

Tool

实现：

MoveTool

RotateTool

ScaleTool

DrawTool

MeasureTool

CutTool

---

## 要求

工具之间：

动态切换。

禁止：

大量：

if else

---

# Phase 07

# Teaching System

## 目标

增加教学能力。

---

## MeasureSystem

支持：

长度

角度

面积

体积

---

## AnnotationSystem

支持：

文字

标注线

辅助线

---

## AnimationSystem

支持：

旋转

展开

拆分

组合

---

# Phase 08

# Cut System

## 目标

实现几何切割。

---

## 核心结构

Plane

Mesh

↓

MeshCutter

↓

New Mesh

---

## 支持

第一阶段：

立方体切割。

第二阶段：

圆柱切割。

第三阶段：

复杂模型。

---

## 完成标准

输入：

一个切割平面。

输出：

两个新的 Mesh。

---

# Phase 09

# Animation System

## 目标

实现教学动画。

---

## 支持

例如：

立方体展开：

Cube

↓

6个面

↓

平面展开图

---

圆柱：

Cylinder

↓

圆面

矩形侧面

---

# Phase 10

# Persistence System

## 目标

保存教学文件。

---

## 支持

保存：

- 场景
- 对象
- 参数
- 动画状态

格式：

建议：

JSON

---

# Phase 11

# 优化与发布

## 目标

形成可使用的软件。

---

## 内容

优化：

- 内存
- 渲染性能
- 加载速度

发布：

支持：

Windows 7

Windows 10

Windows 11

---

# 3. AI开发规则

每个阶段 AI 必须：

1. 只实现当前阶段。

2. 不提前编写未来模块。

3. 保持已有接口稳定。

4. 修改架构必须说明原因。

5. 每次输出必须包含：

- 文件路径
- 完整代码
- 使用说明
- 测试方法

---

# 4. 阶段完成判断

一个阶段完成必须满足：

代码可以运行

↓

功能可以测试

↓

没有明显架构问题

↓

才进入下一阶段

---

# End
