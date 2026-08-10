# Geometry Teaching Engine

# Phase 08 Geometry Operation System 开发任务


版本：

v1.0



---

# 1. 当前角色


你现在是一名：

Java 几何算法引擎工程师。


当前任务：

实现 Geometry Teaching Engine 的几何操作系统。



---

# 2. 当前阶段目标


建立：


Geometry Operation Layer



负责：

对 Mesh 进行数学操作。


支持：

- 平面切割
- Mesh分离
- 截面生成
- 几何分析



---

# 3. 当前架构位置


当前流程：



Geometry Object

↓

Mesh

↓

Geometry Operation

↓

New Mesh

↓

New Geometry Object

↓

Scene




---

# 4. 核心设计原则


## 4.1 不修改原Mesh


禁止：

直接修改原对象。


错误：

```java
cube.mesh.cut();

原因：

教学软件需要：

保留原始模型。

正确：

输入：

Original Mesh

+

Operation


输出：

New Mesh A

+

New Mesh B

5. 创建目录

新增：

geometry


├── operation


│
├── GeometryOperation.java


├── OperationResult.java


│
├── cutting


│
├── Plane.java


├── PlaneCutOperation.java


├── MeshCutter.java


├── IntersectionPoint.java


└── CutResult.java



├── topology


│
├── EdgeSplitter.java


├── FaceSplitter.java


└── MeshBuilder.java



└── analysis


    ├── Section.java

    └── SectionAnalyzer.java

6. GeometryOperation接口

创建：

GeometryOperation.java

定义：

public interface GeometryOperation {


    OperationResult execute(Mesh mesh);


}

所有几何操作实现该接口。

7. OperationResult

创建：

OperationResult.java

用于返回结果。

包含：

Mesh[] meshes;


boolean success;


String message;


例如：

切割成功：

Mesh A

Mesh B

8. Plane设计

创建：

Plane.java

表示切割平面。

数学形式：

ax + by + cz + d = 0

结构：

Vec3 normal;


float distance;

9. Plane与点关系判断

实现：

distanceToPoint()

公式：

ax+by+cz+d

结果：

大于0：

点在正侧。

小于0：

点在负侧。

等于0：

点在平面。

10. Mesh Cutter核心

创建：

MeshCutter.java

功能：

输入：

Mesh

+

Plane

输出：

Mesh A

Mesh B
11. 切割算法要求

采用：

Triangle Mesh Clipping。

流程：

遍历Face


↓

判断三个Vertex位置


↓

计算交点


↓

生成新的Triangle


↓

重新构建Mesh

12. Face处理规则

每个Face：

三角形。

情况：

情况1

三个点同侧。

直接保留。

情况2

两个点一侧，一个点另一侧。

产生：

两个新三角形。

情况3

三个点跨越。

重新划分。

13. IntersectionPoint

创建：

IntersectionPoint.java

表示：

边和平面的交点。

包含：

Vec3 position;


Edge edge;

14. EdgeSplitter

功能：

分割边。

输入：

Edge

+

Plane


输出：

New Vertex

15. FaceSplitter

功能：

切割三角面。

输入：

Face

+

Intersection Points

输出：

New Faces

16. MeshBuilder

作用：

重新生成Mesh。

负责：

顶点合并
Face添加
Index重新计算
17. 截面生成

切割后：

需要生成截面。

创建：

Section.java

保存：

List<Vec3>


表示：

切割轮廓。

18. SectionAnalyzer

功能：

分析截面。

例如：

输入：

圆柱切割。

输出：

Circle

Ellipse

Polygon


注意：

第一阶段只提供接口。

19. 支持几何体

必须支持：

Cube

通过Mesh切割。

Cylinder

支持：

横切
斜切
Cone

支持：

平切
斜切
Sphere

支持：

简单截面。

20. 与教学系统连接

切割结果不能直接替换。

应该：

Teacher Action


↓

CutTool


↓

PlaneCutOperation


↓

New Objects


↓

Scene

21. 白板交互支持

白板模式：

用户画切割线。

流程：

Pen Stroke


↓

Cut Plane Generate


↓

PlaneCutOperation

22. 键鼠模式支持

鼠标：

选择平面。

移动平面。

执行切割。

23. Demo要求

创建：

GeometryOperationDemo

实现：

场景：

Cylinder

Cone

Cube


功能：

创建一个切割平面。

切割Cylinder。

显示两个新的Mesh。

24. 测试要求

创建：

GeometryOperationTest

测试：

Plane测试

输入：

Plane

检测：

点的位置判断。

Cube Cut测试

切割Cube。

检查：

生成两个Mesh。

Cylinder Cut测试

切割圆柱。

检查：

Mesh有效。

25. 当前禁止实现

禁止：

1. 展开算法

例如：

圆柱展开。

属于：

Phase 09。

2. 复杂几何优化

禁止：

BVH

高级拓扑优化。

3. AI识别

不属于当前阶段。

26. 输出要求

完成代码时必须输出：

第一部分：

新增目录结构。

第二部分：

完整代码。

第三部分：

解释：

Mesh切割流程
Plane数学模型
Face重新生成方法

第四部分：

测试方法。

27. 验收标准

Phase 08完成：

必须满足：

√ Plane结构存在

√ Mesh可以被平面切割

√ 原Mesh不被破坏

√ 可以生成两个新的Mesh

√ 支持Cube切割

√ 支持Cylinder切割

√ 支持Cone切割接口

√ 与Tool系统连接

√ 支持白板和键鼠触发

28. 下一阶段

进入：

PHASE_09 Animation & Visualization System

下一阶段实现：

几何展开
分解动画
旋转展示
教学动画时间轴
动态演示
End

---

这个阶段是整个软件里算法难度最高的几个阶段之一。

目前你的架构已经形成：


几何数据
|
Mesh
|
几何操作
|
教学展示
|
动画演示


例如：

“圆柱展开教学”最终流程就是：


Cylinder Mesh

↓

Phase 08 切割

↓

得到侧面Mesh

↓

Phase 09 展开动画

↓

Teaching Step播放


所以 Phase 08 不只是切割功能，而是在为后面的三维教学动画建立数学基础。