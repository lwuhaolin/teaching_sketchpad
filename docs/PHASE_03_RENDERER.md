# Geometry Teaching Engine

# Phase 03 Renderer 开发任务

版本：

v1.0

---

# 1. 当前角色

你现在是一名：

Java + LWJGL OpenGL 渲染工程师。

当前任务：

实现 Geometry Teaching Engine 的渲染核心。

---

# 2. 当前阶段目标

建立：

Renderer Layer

使 GeometryObject 中的：

Mesh

能够被 OpenGL 显示。

---

# 3. 架构原则

## 3.1 Renderer独立

Renderer：

负责：

Mesh

↓

GPU Buffer

↓

Screen

不负责：

Geometry生成

对象管理

输入处理

---

## 3.2 禁止依赖业务层

Renderer禁止引用：

Cube

Cylinder

Cone

SceneObject

Renderer只认识：

Mesh

Vertex

Transform

---

# 4. 创建目录

新增：

renderer

├── Renderer.java

├── OpenGLRenderer.java

├── Window.java

├── Camera.java

├── Shader.java

├── MeshRenderer.java

├── VertexBuffer.java

├── IndexBuffer.java

└── VertexArray.java

---

# 5. Window系统

文件：

renderer/Window.java

职责：

创建 GLFW窗口。

要求：

支持：

800x600

默认标题：

Geometry Teaching Engine

---

# 6. LWJGL初始化

初始化：

GLFW

OpenGL Context

GL Capabilities

要求：

兼容：

Windows 7

避免：

新的系统API。

---

# 7. Renderer接口

创建：

Renderer.java

接口：

````java
public interface Renderer {


    void initialize();


    void clear();


    void render();


    void shutdown();


}
8. OpenGLRenderer

实现：

OpenGLRenderer

负责：

OpenGL状态管理
绘制调用

包含：

MeshRenderer
9. Shader系统

创建：

Shader.java

负责：

加载Shader源码
编译
链接
设置Uniform

基础Shader：

Vertex Shader：

负责：

Vertex Position

Transform Matrix

Fragment Shader：

负责：

Color Output
10. Mesh上传GPU

建立：

VertexArray

VertexBuffer

IndexBuffer

流程：

Mesh


↓

Vertex Buffer


↓

GPU


↓

Draw Call
11. MeshRenderer

职责：

输入：

Mesh
+
Transform

输出：

OpenGL Draw
12. Camera系统

创建：

Camera.java

负责：

观察视角。

包含：

position

rotation

projection
13. 支持两种投影
13.1 3D模式

使用：

Perspective Projection

支持：

深度
远近关系
13.2 2D模式

使用：

Orthographic Projection

要求：

限制：

z = 0
14. Render Mode

创建：

RenderMode

枚举：

public enum RenderMode {


    MODE_2D,


    MODE_3D


}
15. 渲染流程

标准流程：

Application


↓

Scene


↓

GeometryObject


↓

Mesh


↓

Renderer


↓

OpenGL

16. 测试Demo

创建：

RendererDemo

要求：

打开窗口。

显示：

一个Cube。

流程：

Cube

↓

Mesh

↓

Renderer

↓

Screen
17. 颜色支持

第一阶段：

简单颜色。

Material：

预留：

color


暂不实现：

光照
纹理
PBR
18. 当前禁止实现

禁止：

1.

Scene系统

不要创建：

Scene

属于：

Phase 04。

2.

鼠标输入

禁止：

MouseListener

属于：

Phase 05。

3.

工具系统

禁止：

Tool

属于：

Phase 06。

19. Windows 7兼容注意事项

必须遵守：

OpenGL版本

优先：

OpenGL 3.3 Core

避免：

OpenGL 4.x 特性
Java版本

建议：

Java 8

原因：

兼容：

Windows 7。

禁止使用

禁止：

JavaFX新版

Windows UI API

DirectX依赖
20. 输出要求

完成代码时：

必须输出：

第一部分

新增目录结构。

第二部分

完整代码。

每个文件：

标注路径。

第三部分

说明：

OpenGL初始化流程
Mesh上传流程
Shader流程
第四部分

运行测试。

说明：

如何启动窗口。

21. 验收标准

Phase 03完成：

必须满足：

√ 可以创建OpenGL窗口

√ 可以加载Shader

√ 可以显示Cube

√ Mesh可以发送到GPU

√ 支持2D/3D投影切换接口

√ Renderer没有业务逻辑

22. 下一阶段

进入：

Phase 04 Scene System

下一阶段：

实现：

场景管理
对象生命周期
多对象渲染
选择管理
End

---

下一份：

```text
docs/PHASE_04_SCENE_SYSTEM.md
```