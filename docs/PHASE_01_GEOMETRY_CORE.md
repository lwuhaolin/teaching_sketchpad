# Geometry Teaching Engine

# Phase 01 Geometry Core 开发任务

文件：

PHASE_01_GEOMETRY_CORE.md

版本：

v1.0

---

# 1. 当前角色

你现在是一名：

Java 几何引擎核心开发工程师。

当前任务：

实现 Geometry Teaching Engine 的核心几何数据层。

---

# 2. 当前阶段目标

建立统一几何数据结构。

完成：

- 数学基础
- 三维向量
- 变换系统
- Mesh系统
- 几何对象接口

最终目标：

任何二维或者三维图形，都可以转换为：

Triangle Mesh

---

# 3. 核心设计原则

## 3.1 统一空间模型

禁止设计：

Point2D

Point3D

Shape2D

Shape3D

统一使用：

Vec3

二维图形：

通过：

z = 0

表示。

例如：

二维矩形：

(x1,y1,0)

(x2,y1,0)

(x2,y2,0)

(x1,y2,0)

---

## 3.2 数据与渲染分离

当前阶段：

只负责数据。

禁止：

创建：

OpenGL

Shader

Renderer

---

## 3.3 Mesh不是唯一数据

注意：

Mesh只是：

渲染表示

不是：

数学定义

后续参数化对象：

会保存：

Geometry Parameter

Mesh Cache

---

# 4. 创建目录

创建：

src/main/java/com/geometry/core

├── math

│ ├── Vec2.java

│ ├── Vec3.java

│ ├── Matrix4.java

│ └── MathUtil.java

├── transform

│ └── Transform.java

├── mesh

│ ├── Vertex.java

│ ├── Edge.java

│ ├── Face.java

│ └── Mesh.java

└── geometry

└── GeometryObject.java

---

# 5. Vec2

路径：

core/math/Vec2.java

功能：

表示二维坐标。

包含：

字段：

float x

float y

提供：

add()

subtract()

multiply()

---

# 6. Vec3

路径：

core/math/Vec3.java

功能：

三维向量。

字段：

float x

float y

float z

提供：

基础运算：

add()

subtract()

multiply()

向量计算：

length()

normalize()

dot()

cross()

要求：

代码简单清晰。

---

# 7. Matrix4

路径：

core/math/Matrix4.java

作用：

提供基础矩阵结构。

暂时支持：

4x4 Matrix

用途：

未来：

- Transform
- Camera
- Projection

当前阶段：

只实现基础存储和计算接口。

不要实现完整OpenGL矩阵系统。

---

# 8. MathUtil

路径：

core/math/MathUtil.java

提供数学工具：

例如：

PI

degreeToRadian()

clamp()

---

# 9. Transform

路径：

core/transform/Transform.java

作用：

描述对象变换。

包含：

位置：

Vec3 position

旋转：

Vec3 rotation

缩放：

Vec3 scale

默认：

position:

0,0,0

rotation:

0,0,0

scale:

1,1,1

---

# 10. Vertex

路径：

core/mesh/Vertex.java

表示顶点。

包含：

Vec3 position

预留：

Vec3 normal

Vec2 uv

当前可以初始化。

---

# 11. Edge

路径：

core/mesh/Edge.java

表示边。

包含：

int vertexA

int vertexB

---

# 12. Face

路径：

core/mesh/Face.java

表示面。

要求：

最终支持三角面。

包含：

int[] vertexIndices

例如：

三角形：

0

1

2

---

# 13. Mesh

路径：

core/mesh/Mesh.java

核心结构。

包含：

List<Vertex>

List<Edge>

List<Face>

提供：

addVertex()

addEdge()

addFace()

getVertices()

getFaces()

---

# 14. GeometryObject接口

路径：

core/geometry/GeometryObject.java

定义：

````java
public interface GeometryObject {


    Mesh getMesh();


    Transform getTransform();


    void updateMesh();


}

作用：

所有几何对象必须实现。

例如未来：

Cube

Cylinder

Cone

Sphere

Rectangle
15. 测试要求

创建测试：

GeometryCoreTest

测试：

Vec3

验证：

加法

减法

点积

Mesh

创建：

简单三角形。

例如：

三个Vertex：

(0,0,0)

(1,0,0)

(0,1,0)

生成：

一个Face。

检查：

Vertex数量

Face数量
16. 当前禁止实现

禁止：

1.

OpenGL

禁止：

GL11

Shader

VAO

VBO
2.

具体几何体

禁止：

Cube

Cylinder

Cone

Sphere

原因：

属于 Phase 02。

3.

UI

禁止：

Swing

JavaFX
17. 输出要求

完成代码时：

必须输出：

第一部分

新增文件结构。

例如：

core

├── math

...
第二部分

每个文件完整代码。

格式：

文件：

xxx.java


代码：

xxxxx
第三部分

说明：

类作用
设计原因
后续如何扩展
第四部分

测试方式。

必须说明：

如何运行测试。

18. 验收标准

Phase 01 完成：

必须满足：

√ Vec3可正常计算

√ Mesh可以保存顶点和面

√ GeometryObject接口存在

√ Transform存在

√ 不依赖OpenGL

√ 项目可以编译

19. 下一阶段

完成后进入：

Phase 02 Geometry Object

下一阶段实现：

Rectangle
Circle
Polygon
Cube
Cylinder
Cone
Sphere

以及：

MeshFactory。

End

---

下一份继续：

```text
docs/PHASE_02_GEOMETRY_OBJECT.md
````
