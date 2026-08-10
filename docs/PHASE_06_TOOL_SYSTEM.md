# Geometry Teaching Engine

# Phase 06 Tool System 开发任务

版本：

v1.0

---

# 1. 当前角色

你现在是一名：

Java 几何教学软件工具系统架构工程师。

当前任务：

实现 Geometry Teaching Engine 的 Tool System。

---

# 2. 当前阶段目标

建立：

Tool System

负责：

将用户操作转换为：

几何编辑行为

支持：

- 移动
- 旋转
- 缩放
- 绘制
- 测量
- 删除
- 切割接口

---

# 3. 核心设计原则

## 3.1 Tool 不关心输入来源

禁止：

Tool里面出现：

```java
MouseEvent

TouchEvent

KeyboardEvent


原因：

Tool不能知道：

用户是：

鼠标操作

还是：

手指操作。

正确：

Input

↓

Action

↓

Tool

↓

SceneObject

4. Action驱动设计

Tool接收：

Action

例如：

鼠标移动：

Mouse Drag

↓

MoveAction

↓

MoveTool

触摸移动：

Finger Drag

↓

MoveAction

↓

MoveTool

两个流程完全一致。

5. 创建目录

新增：

tools


├── Tool.java


├── ToolManager.java


├── ToolContext.java


│


├── move


│
├── MoveTool.java


│
└── MoveHandler.java



├── rotate


│
├── RotateTool.java



├── scale


│
├── ScaleTool.java



├── draw


│
├── DrawTool.java



├── measure


│
├── MeasureTool.java



└── cut


    └── CutTool.java

6. Tool接口

创建：

Tool.java

定义：

public interface Tool {


    String getName();


    void activate();


    void deactivate();


    void handle(Action action);


    void update();


}
7. Tool生命周期

每个工具拥有：

激活：

activate()

使用：

handle(Action)

关闭：

deactivate()
8. ToolManager

创建：

ToolManager.java

职责：

管理当前工具。

包含：

currentTool

支持：

registerTool()

switchTool()

getCurrentTool()

dispatchAction()

9. ToolContext

创建：

ToolContext.java

作用：

给工具提供环境。

包含：

Scene

SelectionManager

Camera

Renderer


示例：

public class ToolContext {


    Scene scene;


    SelectionManager selection;


}

10. MoveTool

实现：

移动工具

功能：

修改：

Transform.position

输入：

MoveAction

流程：

Action

↓

获取Selected Object

↓

修改Transform

↓

Scene更新

11. RotateTool

功能：

旋转对象。

输入：

RotateAction

修改：

Transform.rotation

支持：

2D模式：

限制：

Z轴旋转

3D模式：

支持：

X/Y/Z
12. ScaleTool

功能：

缩放对象。

输入：

ScaleAction

修改：

Transform.scale

支持：

统一缩放。

以及：

XYZ独立缩放接口。

13. DrawTool

教学软件核心工具。

用途：

创建几何对象。

输入：

DrawAction

流程：

用户绘制


↓

生成点集合


↓

GeometryFactory


↓

GeometryObject


↓

Scene.addObject()

14. DrawTool第一阶段限制

只实现：

基础创建。

支持：

Point

Line

Rectangle

Circle

暂不实现：

AI识别。

15. MeasureTool

测量工具。

支持：

距离：

Point A

Point B

↓

Distance

角度：

Three Points

↓

Angle

结果：

创建：

Measurement对象。

16. CutTool接口

当前阶段：

只建立接口。

不要实现切割算法。

原因：

Mesh Cutter属于：

Phase 08。

接口：

CutTool


{

executeCut()

}

17. 2D / 3D模式适配

Tool必须支持：

ApplicationMode

例如：

2D：

MoveTool：

限制：

z = 0

3D：

MoveTool：

允许：

x,y,z
18. 白板交互适配

白板模式：

例如：

手指拖动：

Gesture

↓

MoveAction

↓

MoveTool


电子笔：

Pen Stroke

↓

DrawAction

↓

DrawTool

19. 键鼠模式适配

鼠标：

Drag

↓

MoveAction

↓

MoveTool

键盘：

Delete Action

↓

DeleteTool
20. Undo预留接口

当前不实现。

但是Tool执行操作时：

需要保留扩展接口。

例如：

execute()


undo()


未来：

Phase 10实现。

21. Demo要求

创建：

ToolDemo

测试：

场景：

Cube

Cylinder

Rectangle


实现：

MoveTool

鼠标移动对象。

MoveTool

模拟Touch Action移动对象。

要求：

两个输入最终效果一致。

22. 测试要求

创建：

ToolTest

测试：

Tool切换

验证：

MoveTool

↓

RotateTool


正常切换。

Action分发

输入：

MoveAction

检查：

MoveTool执行。

23. 当前禁止实现

禁止：

1. Mesh切割

禁止：

Plane Mesh Intersection

属于：

Phase 08。

2. 教学动画

禁止：

Animation Timeline

属于：

Phase 09。

3. 文件保存

禁止：

Scene Serialization

属于：

Phase 10。

24. 输出要求

完成代码时：

必须输出：

第一部分：

新增目录结构。

第二部分：

所有新增文件完整代码。

第三部分：

说明：

Action到Tool流程
双输入兼容方式
Tool扩展方法

第四部分：

测试方法。

25. 验收标准

Phase 06完成：

必须满足：

√ Tool接口存在

√ ToolManager可管理工具

√ Action可以驱动Tool

√ MoveTool可工作

√ RotateTool可工作

√ ScaleTool可工作

√ DrawTool有基础实现

√ 不依赖鼠标和触摸

√ 白板和键鼠共享Tool逻辑
```

26. 下一阶段

进入：

PHASE_07_TEACHING_SYSTEM

下一阶段实现：

教学模式
标注系统
辅助线
几何构造
手写支持
白板教学流程
