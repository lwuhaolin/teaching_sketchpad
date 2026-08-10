# Geometry Teaching Engine

# Phase 10 Teaching Project Persistence V2 开发任务

版本：

v2.0

---

# 1. 当前角色

你现在是一名：

Java 教学软件架构工程师。

当前任务：

实现 Geometry Teaching Engine 的教学项目持久化系统。

---

# 2. 当前阶段目标

建立：

Teaching Project Persistence System

负责保存完整教学课件。

不仅保存：

几何模型。

还需要保存：

- 教学步骤
- 动画流程
- 标注
- 白板内容
- 场景状态
- 用户编辑状态

---

# 3. 系统定位

本系统保存的不是普通3D工程。

而是一节完整数学课程。

例如：

圆柱展开课程

包含：

Geometry

↓

Scene

↓

Cut Animation

↓

Unfold Animation

↓

Formula Annotation

↓

Teaching Step

↓

Whiteboard Notes

---

# 4. 当前架构位置

完整流程：

Runtime Object

↓

Persistence Layer

↓

Project File

↓

Storage

↓

Load

↓

Runtime Object

---

# 5. 核心设计原则

## 5.1 不保存Mesh顶点数据

禁止：

保存：

Vertex Array

Face Array

Index Buffer

原因：

- 文件巨大
- 无法修改
- 不利版本升级

---

正确方式：

保存：

Geometry Type

Parameters

Transform

加载：

Parameters

↓

GeometryRegistry

↓

MeshFactory

↓

重新生成Mesh

---

# 6. 文件格式设计

文件：

.gtp

名称：

Geometry Teaching Project

---

# 7. 项目文件结构

格式：

```json
{
 "version":"2.0",

 "project":{},

 "scene":{},

 "geometry":{},

 "teaching":{},

 "animation":{},

 "whiteboard":{}

}
8. 创建目录

新增：

persistence


├── ProjectSerializer.java

├── ProjectDeserializer.java

├── ProjectManager.java

├── VersionMigration.java


├── model


│
├── ProjectData.java

├── SceneData.java

├── ObjectData.java

├── TeachingData.java

├── AnimationData.java

├── WhiteboardData.java

└── SettingData.java



├── registry


│
├── GeometryRegistry.java

├── TeachingRegistry.java

└── AnimationRegistry.java



└── command


    ├── CommandData.java

    └── HistoryData.java

9. ProjectData

项目根数据。

包含：

version;

name;

scene;

teaching;

animation;

whiteboard;

settings;

10. SceneData

保存场景。

包括：

Objects

Layers

Camera

ViewMode

11. ObjectData

保存几何对象。

结构：

{
"id":"cube001",

"type":"Cube",

"transform":{

"position":[0,0,0],

"rotation":[0,0,0],

"scale":[1,1,1]

},

"parameters":{

"width":1,

"height":1,

"depth":1

}

}
12. GeometryRegistry

负责：

类型创建。

禁止：

if(type=="Cube")

else if(type=="Cylinder")

使用：

Type

↓

Creator

↓

GeometryObject


例如：

Cube

↓

CubeCreator


Cylinder

↓

CylinderCreator

13. TeachingData

新增。

保存教学内容。

结构：

LessonData


├── Lesson

├── Step

├── Annotation

├── Measure

└── GuideLine

14. LessonData

保存一节课程。

例如：

Cylinder Unfold Lesson


包含：

title;

description;

steps;

15. StepData

保存教学步骤。

例如：

Step1:

显示圆柱


Step2:

切割


Step3:

展开


保存：

title

description

actions

animationId

16. AnnotationData

保存教学标注。

支持：

Text

Arrow

Highlight

Measure


例如：

半径 r

高度 h

17. AnimationData

保存动画系统数据。

必须支持：

Animation

Timeline

KeyFrame

Sequence

GeometryAnimation

FaceAnimation

18. AnimationSequence保存

例如：

圆柱展开


Sequence:

1 Rotate

2 Cut

3 Unfold

4 Show Formula

19. Face Animation保存

支持：

Face ID

Start Transform

End Transform

Duration


用于：

Cube展开
Cylinder展开
Cone展开
20. WhiteboardData

新增。

保存白板状态。

包括：

Stroke

Gesture

Canvas State

21. StrokeData

保存手写。

例如：

老师画：

辅助线。

保存：

Points

Pressure

Timestamp

22. Interaction State

保存当前编辑状态。

包括：

Selected Object

Current Tool

Current Mode

Camera State

23. Undo/Redo数据接口

新增。

不实现完整历史。

只设计接口。

结构：

CommandData


↓

HistoryData


用于：

未来：

撤销恢复。

24. Serializer

创建：

ProjectSerializer

流程：

Runtime Object

↓

Data Model

↓

JSON

↓

.gtp File

25. Deserializer

流程：

.gtp

↓

JSON

↓

ProjectData

↓

Registry

↓

Runtime Object

↓

Scene

26. Version Migration

必须设计。

原因：

未来：

文件格式变化。

例如：

1.0


↓

2.0


接口：

Migration migrate();

27. Demo要求

创建：

PersistenceTeachingDemo

流程：

创建：

Cube

Cylinder

Annotation

Lesson

Animation

Whiteboard Stroke


保存：

demo.gtp


关闭。

重新打开。

恢复：

Scene

Geometry

Lesson

Animation

Annotation

28. 测试要求

创建：

PersistenceTeachingTest

测试：

Geometry恢复

检查：

参数一致。

Lesson恢复

检查：

Step数量。

Animation恢复

检查：

Sequence存在。

Whiteboard恢复

检查：

Stroke数据。

29. 当前禁止实现

禁止：

云同步

不实现网络存储。

多人协作

不实现实时同步。

数据库

不使用：

MySQL

SQLite

30. 输出要求

完成代码时必须输出：

第一部分：

新增目录结构。

第二部分：

完整代码。

第三部分：

说明：

文件格式设计
教学数据保存流程
动画保存方式
白板数据保存方式
版本升级策略

第四部分：

测试方法。

31. 验收标准

Phase 10完成：

必须满足：

√ 可以保存完整教学项目

√ 可以加载教学项目

√ Geometry参数恢复

√ Scene恢复

√ Lesson恢复

√ Annotation恢复

√ Animation恢复

√ Whiteboard数据恢复

√ 不保存Mesh顶点数据

√ 支持未来版本升级

32. 下一阶段

进入：

PHASE_11_UI_SYSTEM

下一阶段实现：

白板工作空间
工具栏
属性面板
教学控制面板
2D/3D模式切换
触控布局
```
