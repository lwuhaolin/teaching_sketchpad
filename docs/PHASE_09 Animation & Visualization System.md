# Geometry Teaching Engine

# Phase 09 Animation & Visualization System V2 开发任务

版本：

v2.0

---

# 1. 当前角色

你现在是一名：

Java 几何教学动画系统架构工程师。

当前任务：

实现 Geometry Teaching Engine 的教学动画与几何可视化系统。

---

# 2. 当前阶段目标

建立：

Animation Visualization Layer

负责：

- 普通对象动画
- 几何结构动画
- 展开动画
- 分解动画
- 切割演示动画
- 教学步骤动画控制
- 白板交互动画

---

# 3. 系统定位

本系统不是普通3D动画引擎。

主要服务：

几何教学。

核心场景：

例如：

圆柱展开：

圆柱

↓

旋转观察

↓

切割

↓

侧面打开

↓

展开成为矩形

↓

显示面积公式

立方体展开：

Cube

↓

六个面分离

↓

旋转展开

↓

形成展开图

---

# 4. 当前架构位置

完整流程：

Teaching System

↓

Animation Sequence

↓

Animation System

↓

Geometry Object

↓

Renderer

---

# 5. 核心设计原则

## 5.1 动画禁止修改原始Mesh

禁止：

```java
mesh.vertex.position = newPosition;

原因：

Mesh属于几何数据。

正确：

动画控制：

Transform

+

Animation State

+

Face State

6. 动画系统分层

必须设计为：

Animation


├── TransformAnimation


│
├── MoveAnimation

├── RotateAnimation

└── ScaleAnimation



└── GeometryAnimation


    ├── UnfoldAnimation

    ├── ExplodeAnimation

    ├── CutAnimation

    └── SectionAnimation

7. 创建目录

新增：

animation


├── Animation.java


├── AnimationManager.java


├── AnimationState.java


├── AnimationSequence.java


├── Timeline.java



├── transform


│
├── MoveAnimation.java

├── RotateAnimation.java

└── ScaleAnimation.java



├── geometry


│
├── GeometryAnimation.java

├── UnfoldAnimation.java

├── ExplodeAnimation.java

├── CutAnimation.java

└── SectionAnimation.java



├── face


│
├── FaceAnimationState.java

├── FaceAnimator.java

└── FaceTransform.java



├── event


│
├── AnimationEvent.java

└── AnimationListener.java



├── interaction


│
├── InteractiveAnimation.java

└── AnimationController.java



└── interpolation


    ├── Interpolator.java

    ├── LinearInterpolator.java

    └── EaseInterpolator.java

8. Animation接口

创建：

Animation.java

定义：

public interface Animation {


    void start();


    void update(float deltaTime);


    void stop();


    boolean isFinished();


}

9. Animation生命周期

状态：

READY


↓

RUNNING


↓

PAUSED


↓

FINISHED

10. AnimationManager

负责：

管理动画
更新动画
播放控制
暂停控制

接口：

addAnimation()

removeAnimation()

update()

play()

pause()

stop()

11. Timeline系统

用于控制：

动画时间。

支持：

0s

↓

3s

↓

5s

↓

10s


但是：

教学动画必须支持：

步骤控制。

12. AnimationSequence

新增：

AnimationSequence.java

用于：

教学动画组合。

结构：

Lesson Step


↓

AnimationSequence


↓

Animation Item


↓

Animation


例如：

圆柱展开步骤


Step1:

RotateAnimation


Step2:

CutAnimation


Step3:

UnfoldAnimation

13. 插值系统

保持：

Interpolator。

支持：

Linear

Ease


用于：

平滑教学动画。

14. GeometryAnimation

新增核心。

作用：

处理几何结构变化。

例如：

展开
分解
切割展示
15. FaceAnimationState

几何展开不能只使用Transform。

必须支持：

Face级动画。

结构：

class FaceAnimationState {


    Face face;


    Vec3 startPosition;


    Vec3 endPosition;


    Quaternion startRotation;


    Quaternion endRotation;


}

16. FaceAnimator

负责：

控制：

Mesh Face运动。

用途：

Cube展开
Cylinder展开
Cone展开
17. UnfoldAnimation

几何展开动画。

输入：

Mesh

+

展开规则


输出：

Face Animation Sequence

18. Cube展开

必须支持。

流程：

Cube Mesh


↓

识别六个Face


↓

建立Face关系


↓

计算展开方向


↓

播放展开动画


效果：

六个面形成展开图。

19. Cylinder展开

必须支持。

流程：

Cylinder Mesh


↓

识别:

底面

侧面


↓

计算展开路径


↓

侧面展开


↓

形成Rectangle


注意：

不是简单缩放。

20. Cone展开

支持基础版本。

流程：

Cone


↓

侧面展开


↓

扇形

21. ExplodeAnimation

用于：

几何分解教学。

例如：

Cube


↓

六个面分离


↓

展示结构

22. CutAnimation

用于：

切割过程展示。

流程：

Plane


↓

Cut Animation


↓

生成Section


↓

显示截面

23. SectionAnimation

用于：

截面教学。

例如：

圆柱截面：

圆形


圆锥斜切：

椭圆

24. Animation事件系统

新增：

AnimationEvent

用于：

动画完成通知。

例如：

展开完成


↓

显示文字说明

↓

高亮公式


接口：

onStart()

onUpdate()

onComplete()

25. InteractiveAnimation

支持：

教师手动控制。

例如：

白板拖动展开。

结构：

setProgress(float progress)


progress：

0

↓

1


效果：

0.5：

展开一半。

26. 白板交互支持

必须支持：

单指

播放/暂停。

双指

拖动动画进度。

滑动

下一教学步骤。

流程：

Touch Gesture


↓

Animation Controller


↓

Animation Sequence

27. 键鼠支持

支持：

键盘：

Space

播放暂停


Arrow

步骤切换


鼠标：

拖动时间轴

28. 与Teaching System连接

流程：

Lesson


↓

Step


↓

AnimationSequence


↓

AnimationManager


↓

Renderer

29. Demo要求

创建：

AnimationVisualizationDemo

实现：

Demo1

Cube旋转。

Demo2

Cube展开。

Demo3

Cylinder展开。

Demo4

Cut动画。

30. 测试要求

创建：

AnimationVisualizationTest

测试：

生命周期

检查：

start

pause

finish

Face动画

检查：

Face位置变化。

展开动画

检查：

Cylinder展开流程。

31. 当前禁止实现

禁止：

高级物理动画

例如：

真实碰撞。

AI自动生成动画

未来版本。

网络同步动画

未来版本。

32. 输出要求

完成代码时必须输出：

第一部分：

新增目录结构。

第二部分：

完整代码。

第三部分：

解释：

动画架构
Face动画设计
几何展开算法流程
白板交互方式

第四部分：

测试方式。

33. 验收标准

Phase 09完成：

必须满足：

√ 基础Animation系统存在

√ 支持Transform动画

√ 支持GeometryAnimation

√ 支持Face级动画

√ 支持Cube展开

√ 支持Cylinder展开

√ 支持Cut动画接口

√ 支持AnimationSequence

√ 支持教学Step调用

√ 支持白板交互控制

√ 不修改原始Mesh

34. 下一阶段

进入：

PHASE_10_PROJECT_PERSISTENCE

下一阶段实现：

项目文件
教学课件保存
动画序列保存
场景序列化
Undo/Redo
```
