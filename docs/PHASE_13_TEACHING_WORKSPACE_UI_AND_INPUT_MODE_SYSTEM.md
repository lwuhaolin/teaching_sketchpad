# Geometry Teaching Engine

# Phase 13 Teaching Workspace UI And Input Mode System

版本：

v1.0

---

# 1. 当前角色

你现在是一名：

Java 教育软件 UI 架构工程师。

你的任务：

为已经完成核心引擎的 Geometry Teaching Engine
实现真正可运行的教学软件界面。

---

# 2. 项目定位

本软件不是：

- CAD软件
- 3D建模软件
- 游戏编辑器

本软件是：

```
几何教学白板系统
```

主要使用场景：

1. 教师电子白板授课

2. 学生平板学习

3. 普通电脑键鼠操作

---

# 3. 当前项目状态

已经完成：

```
Geometry Core

Mesh System

Renderer

Scene System

Tool System

Interaction System

Teaching System

Animation System

Persistence System

Logical UI System

```

禁止修改：

- Geometry
- Mesh
- Renderer核心
- Scene核心
- Animation核心
- Persistence核心

当前任务：

只增加：

```
真实UI实现层

输入模式系统

```

---

# 4. 核心设计目标

实现：

一个真正可以打开窗口运行的几何教学软件。

启动后：

用户看到：

- 教学工作空间
- 几何画布
- 工具区域
- 教学控制区域

---

# 5. UI设计原则

## 5.1 Canvas优先

几何显示区域必须占主要空间。

禁止采用传统工程软件布局：

```
左侧树

中央编辑区

右侧属性

大量工具栏

```

---

正确：

```
+--------------------------------+

| 课程状态区域                   |

+--------------------------------+

|                                |

|                                |

|       Geometry Canvas          |

|       几何教学区域             |

|                                |

|                                |

+--------------------------------+

| 常用操作区域                   |

+--------------------------------+

```

---

# 6. 支持三种交互模式

必须实现：

```
Input Mode System
```

支持：

## 1. Desktop Mode

键鼠模式。

## 2. Whiteboard Mode

电子白板触控模式。

## 3. Tablet Mode

平板触控模式。

三种模式可以自由切换。

---

# 7. 输入系统架构

创建：

```
input


├── InputModeManager.java


├── InputMode.java


├── DesktopInputMode.java


├── WhiteboardInputMode.java


├── TabletInputMode.java



├── MouseHandler.java

├── KeyboardHandler.java

├── TouchHandler.java

├── PenHandler.java

└── GestureHandler.java

```

---

# 8. 输入统一设计

禁止：

业务层判断：

```
if(mouse)

if(touch)

```

正确流程：

```
Mouse

Touch

Pen


↓

Input Adapter


↓

Input Action


↓

EventBus


↓

Tool System


↓

Core Engine

```

---

# 9. Desktop模式设计

适合：

普通电脑。

UI特点：

允许：

- 更多工具
- 属性查看
- 快捷操作

布局：

```
顶部：

菜单/状态


中央：

Geometry Canvas


底部：

工具栏

```

输入：

鼠标：

- 点击选择
- 拖动移动
- 滚轮缩放

键盘：

```
Ctrl+Z

Space播放

方向键步骤控制

Delete删除

```

---

# 10. Whiteboard模式设计

主要场景：

电子白板。

目标：

教师和学生直接触控。

UI特点：

- 大按钮
- 少文字
- 高可见性
- 操作距离短

布局：

```
+--------------------------------+

| 当前课程                      |

|                                |

|                                |

|          Geometry Canvas      |

|                                |

|                                |

+--------------------------------+

| 选择 移动 测量 切割 展开 播放 |

+--------------------------------+

```

---

# 11. 白板工具设计

创建：

```
FloatingToolBar
```

特点：

不是固定位置。

支持：

- 拖动
- 靠近用户位置
- 自动隐藏

原因：

不同身高学生无法触碰顶部区域。

---

# 12. 学生身高适配

必须考虑：

电子白板安装高度固定。

不同学生：

身高不同。

实现：

## Floating UI

工具栏可以移动。

## Follow User

工具自动靠近当前操作区域。

例如：

学生点击左上角对象：

工具栏出现附近。

---

# 13. 触控误差处理

必须实现：

## Touch Tolerance

触控范围大于视觉范围。

例如：

按钮：

视觉：

80px

实际点击：

120px。

---

## Object Selection Tolerance

选择几何对象时：

允许一定误差。

例如：

点击附近顶点：

自动吸附。

---

## Precision Mode

长按进入精准模式。

效果：

移动速度降低。

适合：

精确调整点位置。

---

# 14. Tablet模式设计

适合：

学生平板。

特点：

介于：

Desktop

和

Whiteboard

布局：

```
顶部：

课程信息


中央：

Canvas


底部：

操作栏

```

---

# 15. Geometry Canvas设计

Canvas负责：

显示：

- 2D图形
- 3D模型
- 动画
- 标注

禁止：

UI遮挡主要教学区域。

---

# 16. 上下文工具设计

不要永久显示所有工具。

采用：

Context Tool。

例如：

点击圆柱：

出现：

```
        旋转


展开    圆柱    切割


        测量

```

操作完成：

自动隐藏。

---

# 17. 教师模式设计

Teacher Mode。

显示：

```
课程名称

教学步骤

动画控制

对象列表

```

支持：

- 演示
- 暂停
- 下一步
- 标注

---

# 18. 学生模式设计

Student Mode。

隐藏：

复杂编辑功能。

显示：

```
任务

提示

操作区域

完成状态

```

---

# 19. 2D模式UI设计

类似数学课本。

显示：

- 点
- 线
- 面
- 标注

禁止：

复杂CAD界面。

---

# 20. 3D模式UI设计

教学展示优先。

默认隐藏：

- 调试信息
- 网格
- 坐标轴

显示：

- 几何体
- 尺寸
- 关键结构

---

# 21. UI组件结构

新增：

```
ui_impl


├── window

│
├── ApplicationWindow.java

└── WorkspaceFrame.java



├── workspace

│
├── TeachingWorkspace.java



├── layout

│
├── DesktopLayout.java

├── WhiteboardLayout.java

└── TabletLayout.java



├── component

│
├── GeometryCanvasView.java

├── FloatingToolBar.java

├── BottomActionBar.java

├── LessonPanel.java

├── PropertyPanel.java



├── input

│
├── InputModeManager.java

└── UIInputAdapter.java



└── theme

    ├── EducationTheme.java

    └── UIStyle.java

```

---

# 22. 技术要求

语言：

Java

窗口：

Swing

渲染：

LWJGL OpenGL

兼容：

Windows 7

Windows 10

Windows 11

禁止：

使用依赖新版系统API。

---

# 23. Swing与LWJGL连接

实现：

```
JFrame

↓

Canvas

↓

LWJGL Context

↓

Renderer

```

---

# 24. UI事件架构

必须：

```
UI Component

↓

UI Event

↓

EventBus

↓

Input System

↓

Tool System

```

禁止：

按钮直接修改：

Geometry对象。

---

# 25. Demo要求

创建：

```
TeachingWorkspaceDemo
```

启动：

显示真实窗口。

包含：

- Geometry Canvas
- Toolbar
- Lesson区域
- 模式切换按钮

显示：

Cube

Rectangle

支持：

2D/3D切换。

支持：

Desktop

↓

Whiteboard

↓

Tablet

切换。

---

# 26. 测试要求

创建：

```
TeachingWorkspaceUITest
```

测试：

- 窗口启动
- UI布局
- 模式切换
- 鼠标输入
- 触控输入
- 工具事件
- Canvas渲染

---

# 27. 禁止事项

禁止：

重新设计核心引擎。

禁止：

制作CAD风格UI。

禁止：

大量小按钮。

禁止：

输入逻辑和业务逻辑混合。

---

# 28. 输出要求

输出：

第一部分：

UI架构说明。

第二部分：

新增目录结构。

第三部分：

完整代码。

第四部分：

三种模式设计说明。

第五部分：

运行测试方法。

---

# 29. 验收标准

完成后必须满足：

√ 软件可以打开真实窗口

√ LWJGL正常显示

√ UI适合教学场景

√ 支持键鼠模式

√ 支持白板模式

√ 支持平板模式

√ 可以自由切换输入方式

√ 支持触控误差处理

√ 支持身高适配

√ 支持2D/3D切换

√ 不破坏核心架构

---

# End
