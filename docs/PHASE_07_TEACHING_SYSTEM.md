# Phase 07 Teaching System

**版本**: v1.0  
**完成日期**: 2026-08-09

---

## 1. 概述

Teaching System 是 Geometry Teaching Engine 的教学层，负责：
- 教学课程管理（Lesson/Step）
- 标注系统（文字、箭头、高亮）
- 几何构造系统（点、线、圆）
- 辅助系统（网格、坐标系、辅助线）
- 手写识别接口

---

## 2. 新增目录结构

```
src/main/java/com/geometry/teaching/
├── TeachingMode.java              # 教学模式枚举
├── TeachingManager.java           # 教学核心管理器
├── TeachingAction.java            # 教学动作标记接口
├── Lesson.java                    # 课程定义
├── Step.java                      # 教学步骤
├── TeachingDemo.java              # 演示程序
│
├── annotation/
│   ├── Annotation.java            # 标注接口
│   ├── TextAnnotation.java        # 文字标注
│   ├── ArrowAnnotation.java       # 箭头标注
│   └── HighlightAnnotation.java   # 高亮标注
│
├── construction/
│   ├── Construction.java          # 构造接口
│   ├── PointConstruction.java     # 点构造
│   ├── LineConstruction.java      # 线构造
│   └── CircleConstruction.java    # 圆构造（圆心+半径 / 三点定圆）
│
├── assistant/
│   ├── HelperLine.java            # 辅助线
│   ├── Grid.java                  # 坐标网格
│   └── CoordinateSystem.java      # 三维坐标系
│
└── recognition/
    ├── StrokeRecognizer.java      # 手写识别接口
    ├── ShapeRecognitionResult.java # 识别结果
    └── DefaultStrokeRecognizer.java # 默认实现（占位）

src/test/java/com/geometry/teaching/
└── TeachingTest.java              # 101项测试
```

---

## 3. 完整代码说明

### 3.1 TeachingMode — 教学模式

```java
public enum TeachingMode {
    TEACHER,  // 教师模式：全部编辑和标注权限
    STUDENT,  // 学生模式：受限制交互
    EXAM,     // 考试模式：受时间限制
    FREE      // 自由模式：无限制
}
```

**设计原因**: 模式控制 TeachingManager 的行为，决定哪些工具可用、是否允许标注等。

**交互模块**:
- TeachingManager 根据模式决定 canEdit() 和 canAnnotate() 的返回值

### 3.2 TeachingManager — 教学核心管理器

**职责**:
- 管理当前教学模式
- 管理当前课程和步骤导航
- 管理标注（添加/删除/渲染）
- 管理辅助对象（网格/坐标系/辅助线）
- 渲染所有标注和辅助对象

**核心方法**:
| 方法 | 说明 |
|------|------|
| `startLesson(Lesson)` | 开始课程 |
| `nextStep()` / `previousStep()` | 步骤导航 |
| `addTextAnnotation()` | 添加文字标注 |
| `addArrowAnnotation()` | 添加箭头标注 |
| `addHighlightAnnotation()` | 添加高亮标注 |
| `addAssistant()` | 添加辅助对象 |
| `render()` | 渲染所有标注和辅助对象 |
| `canEdit()` / `canAnnotate()` | 检查操作权限 |
| `reset()` | 重置所有状态 |

**设计原因**: 作为教学层的中央管理器，统一协调所有教学相关操作。不依赖 UI，只依赖 Scene 和 Renderer 接口。

### 3.3 Lesson & Step — 课程与步骤

**Lesson**:
- 课程名称和描述
- 有序步骤列表
- 步骤导航（goToFirst/goToLast/nextStep/previousStep）

**Step**:
- 步骤编号、标题、描述
- Scene 状态快照
- 预期学生动作列表

**设计原因**: Lesson/Step 分离了课程结构和步骤内容，支持教学流程的顺序播放和回退。

### 3.4 标注系统

**Annotation 接口**:
```java
public interface Annotation {
    void render(Renderer renderer);
    String getDescription();
}
```

**三种标注实现**:

1. **TextAnnotation**: 文字标签
   - 属性: text, position, size, color
   - 用途: 半径标注 r=5、高度标注 h=10

2. **ArrowAnnotation**: 箭头标注
   - 属性: start, end, arrowSize, color
   - 用途: 指向顶点/边/面

3. **HighlightAnnotation**: 高亮标注
   - 属性: target, state (NORMAL/OUTLINE/GLOW), color, alpha
   - 用途: 强调某个几何对象

**设计原因**: 标注与场景几何分离，不影响 GeometryObject 的 Mesh 数据。

### 3.5 几何构造系统

**Construction 接口**:
```java
public interface Construction {
    GeometryObject build();
}
```

**三种构造实现**:

1. **PointConstruction**: 点构造
   - 输入: position, radius
   - 输出: Sphere GeometryObject（用作点标记）

2. **LineConstruction**: 线构造
   - 输入: pointA, pointB, radius
   - 输出: Cylinder GeometryObject（两端点之间）
   - 特殊情况: 两点重合时退化为点

3. **CircleConstruction**: 圆构造
   - 两种模式: CENTER_RADIUS / THREE_POINTS
   - 输入: 圆心+半径 或 三个点
   - 输出: Circle GeometryObject
   - 三点模式: 计算外接圆（非共线校验）

**设计原因**: 构造对象持有临时状态，用户操作后通过 build() 生成最终几何对象。不直接修改 Scene。

### 3.6 辅助系统

1. **Grid**: 坐标网格
   - 三种密度: SPARSE / MEDIUM / DENSE
   - 支持可见性控制
   - 仅用于 2D 模式

2. **CoordinateSystem**: 三维坐标系
   - RGB 标准轴颜色
   - 可配置轴长度
   - 仅用于 3D 模式

3. **HelperLine**: 辅助线
   - 三种类型: SOLID / DASHED / CENTER
   - 可配置颜色和透明度
   - 用途: 垂线、延长线、中心线

### 3.7 手写识别接口

**StrokeRecognizer 接口**:
```java
public interface StrokeRecognizer {
    ShapeRecognitionResult recognize(List<Vec2> stroke);
    String getName();
}
```

**ShapeRecognitionResult**:
- type: 识别出的形状类型
- confidence: 置信度 [0.0, 1.0]
- points: 重构的几何点

**DefaultStrokeRecognizer**: 占位实现，返回 UNKNOWN

**设计原因**: Phase 07 只定义接口，实际 AI 识别在后续 Phase 实现。

---

## 4. 架构说明

### 4.1 与现有模块的交互

```
UI Layer
    ↓
Event System (Phase 05)
    ↓
Tool System (Phase 06)
    ↓
Teaching System (Phase 07) ← 新增
    ↓
Scene System (Phase 04)
    ↓
Renderer (Phase 03)
```

### 4.2 与 Geometry 架构的隔离

- TeachingManager **不直接访问 GeometryObject**，只通过 SceneObject 操作
- 标注对象调用 `renderer.renderAnnotation(this)`，由 Renderer 负责绘制
- Construction 对象的 build() 返回 GeometryObject，但不修改已有 Mesh
- 辅助对象实现 Annotation 接口，与标注统一渲染

### 4.3 Renderer 扩展

新增 `renderAnnotation(Annotation)` 方法到 Renderer 接口（默认 no-op），不影响现有 Renderer 实现。

---

## 5. 测试覆盖

**101 项测试，全部通过**:

| 测试类别 | 数量 | 内容 |
|---------|------|------|
| TeachingMode | 1 | 枚举值验证 |
| TextAnnotation | 6 | 创建/属性/校验 |
| ArrowAnnotation | 5 | 创建/属性/校验 |
| HighlightAnnotation | 5 | 创建/状态切换/校验 |
| Lesson | 8 | 创建/步骤添加/导航/校验 |
| Step | 6 | 创建/属性/校验 |
| TeachingManager | 13 | 模式/注解/辅助/课程/渲染/重置 |
| PointConstruction | 5 | 创建/默认半径/校验 |
| LineConstruction | 5 | 创建/长度计算/校验 |
| CircleConstruction | 7 | 圆心+半径/三点/共线校验 |
| Grid | 5 | 创建/密度/可见性/校验 |
| CoordinateSystem | 6 | 创建/颜色/端点/可见性 |
| HelperLine | 4 | 创建/类型/校验 |
| ShapeRecognitionResult | 7 | 创建/属性/setter/校验 |
| StrokeRecognizer | 3 | 默认实现/空输入校验 |
| 集成测试 | 3 | 完整流程/架构隔离/2D/3D模式 |

---

## 6. 后续扩展

- **Phase 08**: Cut System — 平面切割几何体
- **Phase 09**: Animation System — 展开/分解/旋转动画
- **Phase 10**: Persistence — 项目文件保存/加载
- **Phase 11**: UI — Swing 界面
- **Phase 12**: Packaging — Windows 打包发布
