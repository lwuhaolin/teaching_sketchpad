# Geometry Teaching Engine

# Phase 11 UI Workspace System 开发任务

版本：

v1.0

---

# 1. 当前角色

你现在是一名：

Java 桌面图形软件架构工程师。

当前任务：

实现 Geometry Teaching Engine 的 UI 与工作空间系统。

---

# 2. 当前阶段目标

建立：

UI Workspace Layer

负责：

- 软件窗口管理
- 工具栏
- 属性面板
- 场景管理面板
- 教学控制面板
- 白板交互辅助

---

# 3. 软件定位

本软件不是传统建模软件。

主要运行环境：

- 电子白板
- 触控一体机
- 教师电脑
- 学生平板

因此：

UI设计必须：

触控优先。

同时兼容：

鼠标键盘。

---

# 4. 当前整体架构

完整结构：

UI Workspace

↓

Event Bridge

↓

Action System

↓

Tool System

↓

Scene

↓

Renderer

UI禁止：

直接修改Geometry。

---

# 5. 核心设计原则

## 5.1 UI与核心逻辑分离

禁止：

```java
Button.click(){

    cube.move();

}


原因：

UI不应该知道：

几何如何变化。

正确：

UI

↓

Command/Event

↓

Tool

↓

Scene

6. 技术要求

使用：

Java

兼容：

Windows 7

要求：

避免依赖：

JavaFX新版本
Windows新API

推荐：

Swing/SWT + LWJGL Canvas。

7. 创建目录

新增：

ui


├── ApplicationWindow.java


├── Workspace.java


├── LayoutManager.java



├── toolbar


│
├── ToolBar.java


├── QuickToolBar.java



├── panel


│
├── SceneTreePanel.java


├── PropertyPanel.java


├── TeachingPanel.java


└── AnimationPanel.java



├── canvas


│
├── CanvasInteractionLayer.java


└── OverlayRenderer.java



├── touch


│
├── TouchUIManager.java


└── TouchLayout.java



└── bridge


    └── UIEventBridge.java

8. ApplicationWindow

创建：

ApplicationWindow.java

职责：

管理：

主窗口
菜单
Workspace

不要包含：

业务逻辑。

9. Workspace系统

创建：

Workspace.java

作为软件主要工作区域。

结构：

Workspace


├── Toolbar


├── OpenGL Canvas


├── SceneTree


├── PropertyPanel


├── TeachingPanel


└── AnimationPanel

10. OpenGL Canvas集成

保持：

LWJGL Renderer。

UI负责：

提供容器。

Renderer负责：

绘制。

结构：

Swing/SWT


↓

OpenGL Canvas


↓

LWJGL Renderer

11. Toolbar设计

创建：

ToolBar

显示：

基础工具。

包括：

Select
Move
Rotate
Scale
Draw
Measure
Cut
12. QuickToolBar

针对白板。

特点：

大按钮。

支持：

触控操作。

例如：

移动

旋转

测量

播放动画

下一步

13. SceneTreePanel

用于显示：

场景结构。

不是简单模型列表。

支持：

Scene


├── Geometry


│
├── Cube

├── Cylinder



├── Annotation


├── HelperLine


└── AnimationStep

14. PropertyPanel

显示对象属性。

分为两类：

Geometry Property

例如：

Cube:

width

height

depth


Cylinder:

radius

height

segments

Teaching Property

例如：

显示名称

是否高亮

是否显示标签

动画状态

15. TeachingPanel

教学控制区域。

支持：

Lesson显示
Step切换
播放教学流程

例如：

圆柱展开教学


Step 1

显示圆柱


Step 2

切割


Step 3

展开

16. AnimationPanel

控制动画。

支持：

播放
暂停
停止
时间轴

例如：

0s ---- 5s ---- 10s

显示

切割

展开

17. CanvasInteractionLayer

白板核心。

负责：

OpenGL画布上的额外显示。

包括：

手势提示
选择框
辅助线
标注显示
手写轨迹

注意：

不负责修改对象。

18. TouchUIManager

触控UI管理。

根据模式调整：

界面大小。

例如：

白板：

按钮60px+


桌面：

按钮24px

19. Mode系统

不要混淆模式。

创建：

ViewMode

InteractionMode

TeachingMode

20. ViewMode

负责：

显示。

例如：

2D

3D

21. InteractionMode

负责：

输入。

例如：

WHITEBOARD

DESKTOP

22. TeachingMode

负责：

教学状态。

例如：

TEACHER

STUDENT

EXAM

FREE

23. UIEventBridge

UI事件不能直接调用Tool。

流程：

Button


↓

UIEvent


↓

EventBridge


↓

Action


↓

Tool

24. 白板使用流程

示例：

用户点击移动。

或者：

直接触摸对象。

流程：

Touch


↓

Input System


↓

Action


↓

MoveTool


↓

Scene



UI只显示状态。

25. 键鼠使用流程

鼠标：

Click Toolbar


↓

Select Tool


↓

Mouse Interaction


↓

Action

26. Demo要求

创建：

UIDemo

实现：

窗口打开。

显示：

Toolbar
OpenGL Canvas
SceneTree
PropertyPanel

Canvas显示：

Cube

Rectangle

27. 测试要求

创建：

UITest

测试：

UI事件

按钮点击。

检查：

生成正确Action。

模式切换

测试：

Whiteboard

↓

Desktop

Panel同步

选择对象。

检查：

PropertyPanel更新。

28. 当前禁止实现

禁止：

1. 修改Geometry

UI不能操作Mesh。

2. 渲染逻辑

UI不负责OpenGL绘制。

3. 教学算法

属于Teaching System。

29. 输出要求

完成代码时必须输出：

第一部分：

新增目录结构。

第二部分：

完整代码。

第三部分：

说明：

UI架构
白板适配方式
EventBridge流程

第四部分：

运行测试方法。

30. 验收标准

Phase 11完成：

必须满足：

√ 存在Workspace系统

√ OpenGL Canvas嵌入UI

√ Toolbar可切换Tool

√ SceneTree可显示对象

√ PropertyPanel同步对象属性

√ TeachingPanel可控制课程

√ 支持白板大按钮布局

√ UI不依赖Geometry逻辑

√ 支持Win7环境

31. 下一阶段

进入：

Phase 12 Packaging & Compatibility System

下一阶段实现：

Windows发布
JRE打包
安装程序
Win7兼容处理
性能优化
配置管理
```
