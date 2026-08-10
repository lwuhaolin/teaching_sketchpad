# Geometry Teaching Engine

# Phase 05 Input & Interaction System 开发任务

版本：

v1.0

---

# 1. 当前角色

你现在是一名：

Java 图形软件交互架构工程师。

当前任务：

实现 Geometry Teaching Engine 的输入与交互系统。

本阶段目标：

同时支持：

- 智慧白板触控模式
- 传统键鼠桌面模式

---

# 2. 软件交互定位

本软件不是传统3D建模软件。

主要运行环境：

- 教室电子白板
- 触控一体机
- 平板设备

同时兼容：

- Windows鼠标
- 键盘

因此：

触控交互是主要设计。

鼠标键盘是兼容设计。

---

# 3. 核心架构原则

## 3.1 输入来源与业务逻辑分离

禁止：

Tool直接判断输入设备。

错误：

```java
if(mouse){

}

if(touch){

}

原因：

未来增加设备会导致代码混乱。

正确结构：

Input Device

↓

Input Event

↓

Gesture / Action

↓

Tool

↓

Scene Object

4. 整体架构

设计：

Input Layer


├── Desktop Input

│
├── Mouse

└── Keyboard



├── Whiteboard Input

│
├── Touch

├── Multi Touch

└── Pen



        ↓


Input Event


        ↓


Gesture System


        ↓


Action System


        ↓


Tool System


        ↓


Scene

5. 创建目录

新增：

interaction


├── input


│
├── InputDevice.java
│
├── MouseDevice.java
│
├── KeyboardDevice.java
│
├── TouchDevice.java
│
└── PenDevice.java



├── event


│
├── InputEvent.java
│
├── PointerEvent.java
│
└── GestureEvent.java



├── gesture


│
├── GestureRecognizer.java
│
├── DragGesture.java
│
├── PinchGesture.java
│
└── RotateGesture.java



├── action


│
├── Action.java
│
├── MoveAction.java
│
├── RotateAction.java
│
├── ScaleAction.java
│
└── SelectAction.java



├── InteractionManager.java


└── InteractionMode.java

6. InteractionMode

创建：

InteractionMode.java

定义：

public enum InteractionMode {


    DESKTOP,


    WHITEBOARD


}


作用：

用户可以自由切换。

例如：

设置：

交互方式：

○ 白板模式

○ 键鼠模式

7. InputDevice接口

创建：

InputDevice.java

要求：

public interface InputDevice {


    void update();


    List<InputEvent> getEvents();


}


所有输入设备实现。

8. 鼠标输入

创建：

MouseDevice.java

支持：

左键
右键
滚轮
移动

产生：

PointerEvent

禁止：

直接修改Scene。

9. 键盘输入

创建：

KeyboardDevice.java

支持：

基础按键：

W

A

S

D

Delete

Ctrl

Shift

ESC


用途：

摄像机移动
快捷操作
软件控制
10. 触控输入

创建：

TouchDevice.java

支持：

单指：

Pointer Down

Pointer Move

Pointer Up


多指：

Multi Touch


产生：

PointerEvent。

11. 电子笔输入

创建：

PenDevice.java

支持：

位置

压力

笔状态


结构：

float pressure;

boolean pressed;


用途：

未来：

手写
标注
绘制几何图形
12. PointerEvent

统一输入事件。

结构：

public class PointerEvent {


    int pointerId;


    PointerType type;


    Vec2 position;


    Vec2 delta;


    EventType eventType;


}


PointerType：

MOUSE

TOUCH

PEN

13. Gesture系统

白板核心。

创建：

GestureRecognizer

负责：

输入：

PointerEvent

输出：

GestureEvent。

14. 手势支持
单指拖动

流程：

Touch Move


↓

DragGesture


↓

MoveAction


↓

MoveTool


用途：

移动几何对象。

双指缩放

流程：

Two Finger Distance Change


↓

PinchGesture


↓

ScaleAction


↓

Camera/Object Scale

双指旋转

流程：

Two Finger Rotation


↓

RotateGesture


↓

RotateAction

15. Action系统

重要设计。

Tool不能接收：

Mouse事件。

Tool只接收：

Action。

例如：

Mouse Drag

↓

MoveAction



或者：

Finger Drag

↓

MoveAction


最终相同。

16. Action接口

创建：

Action.java

例如：

public interface Action {


    void execute();


}

17. MoveAction

包含：

target Object

movement Vector


作用：

修改：

Transform.position。

18. Selection系统

保留对象选择。

流程：

输入


↓

Ray


↓

Object Picking


↓

SelectionManager


19. Ray Picking

继续支持。

原因：

白板点击和鼠标点击都需要选择。

流程：

Screen Position


↓

Ray


↓

BoundingBox


↓

Object

20. 白板模式规则

默认：

单指

选择和移动。

双指

控制：

缩放
旋转
笔

默认：

绘制和标注。

21. 键鼠模式规则

鼠标：

左键：

选择。

拖动：

移动。

滚轮：

缩放。

右键：

旋转视角。

键盘：

快捷控制。

22. 当前禁止实现

禁止：

Tool具体实现

不要创建：

MoveTool

RotateTool

ScaleTool


属于：

Phase 06。

禁止：

教学逻辑

不要实现：

手写识别
几何识别

属于：

Phase 07。

禁止：

Mesh修改

不要实现：

切割。

属于：

Phase 08。

23. Demo要求

创建：

InteractionDemo

要求：

同时测试：

Desktop模式

鼠标：

选择Cube

拖动Cube

Whiteboard模式

模拟Touch事件：

拖动Cube。

要求：

最终：

两者都产生：

MoveAction。

24. 测试要求

创建：

InteractionTest

测试：

输入：

MouseEvent

输出：

Action。

输入：

TouchEvent

输出：

Action。

验证：

两种输入结果一致。

25. 输出要求

完成代码时：

必须输出：

第一部分：

目录结构。

第二部分：

完整代码。

第三部分：

说明：

输入架构
双模式设计
Action转换流程

第四部分：

运行测试方法。

26. 验收标准

Phase 05完成：

必须满足：

√ 支持鼠标输入

√ 支持触摸输入

√ 支持电子笔接口

√ 支持多点手势接口

√ 白板模式和桌面模式可切换

√ Tool不知道输入来源

√ 输入最终转换为Action

√ 不破坏Scene和Geometry架构

```
26. 下一阶段

进入：

PHASE_06_TOOL_SYSTEM