# Geometry Teaching Engine

# Phase 02 Geometry Object 开发任务

版本：

v1.0

---

# 1. 当前角色

你现在是一名：

Java 几何引擎开发工程师。

当前任务：

实现 Geometry Teaching Engine 的参数化几何对象系统。

---

# 2. 当前阶段目标

在 Phase 01 Geometry Core 基础上，实现：

- 几何对象抽象
- 参数模型
- Mesh生成器
- 基础二维图形
- 基础三维图形

最终目标：

创建：

GeometryObject

对象。

对象包含：

数学参数

Mesh缓存

Transform

---

# 3. 核心设计原则

## 3.1 参数和Mesh分离

禁止：

对象 = Mesh

错误：

Cylinder

{

vertices[]

faces[]

}

原因：

无法动态修改。

---

正确：

Cylinder

{

radius

height

segments

mesh

transform

}

流程：

修改参数

↓

重新生成Mesh

↓

更新显示

---

# 4. 创建目录

新增：

core

├── geometry

│

├── GeometryObject.java

├── Rectangle.java

├── Circle.java

├── Polygon.java

├── Cube.java

├── Cylinder.java

├── Cone.java

└── Sphere.java

mesh

└── MeshFactory.java

---

# 5. GeometryObject设计

接口：

````java
public interface GeometryObject {


    Mesh getMesh();


    Transform getTransform();


    void updateMesh();


}

所有几何对象必须实现。

6. MeshFactory

创建：

MeshFactory

位置：

core.mesh.MeshFactory

作用：

负责生成Mesh。

禁止：

Geometry对象内部直接大量创建顶点。

7. MeshFactory接口

提供：

createRectangle()

createCircle()

createPolygon()

createCube()

createCylinder()

createCone()

createSphere()
8. Rectangle

二维矩形。

设计：

参数：

width

height

生成：

四个顶点：

(-w/2,-h/2,0)

(w/2,-h/2,0)

(w/2,h/2,0)

(-w/2,h/2,0)

生成：

两个三角面。

9. Circle

二维圆。

注意：

圆不是特殊Mesh。

使用：

Polygon approximation

方式。

参数：

radius

segments

生成：

中心点：

0,0,0

外围：

segments个点

连接：

三角形。

10. Polygon

参数：

List<Vec3>

要求：

支持任意二维多边形。

限制：

默认：

z = 0
11. Cube

参数：

width

height

depth

生成：

8个顶点。

6个面。

12个三角形。

结构：

8 vertices

12 triangles
12. Cylinder

重点实现。

参数：

radius

height

segments

例如：

segments = 32

表示：

圆被近似为32边形。

生成流程：

第一步

生成底部圆：

z=-height/2

生成：

segments个点
第二步

生成顶部圆：

z=height/2
第三步

连接侧面。

每两个相邻点：

形成矩形。

拆成两个三角形。

第四步

生成上下底面。

使用：

三角扇。

最终：

Vertex

+

Triangle
13. Cone

参数：

radius

height

segments

生成：

底面

一个圆。

顶点

一个点：

0,height/2,0
侧面

每两个底边点：

连接顶部。

生成三角形。

14. Sphere

参数：

radius

segments

采用：

经纬线方式。

生成：

latitude

longitude

网格。

15. 参数修改机制

所有对象支持：

例如：

Cylinder：

setRadius()

setHeight()


调用：

updateMesh()

重新生成。

16. GeometryObject示例

结构：

Cylinder


radius


height


segments


Mesh mesh


Transform transform
17. 测试要求

创建：

GeometryObjectTest

测试：

Cube

检查：

Vertex数量

Face数量
Cylinder

创建：

radius=1

height=2

segments=32

检查：

Mesh存在。

参数修改测试

流程：

创建：

Cylinder

修改：

height

重新生成Mesh。

18. 当前禁止实现

禁止：

1. Renderer

不要创建：

OpenGL

Shader

Camera
2. UI

禁止：

Swing

JavaFX
3. 交互

禁止：

鼠标拖拽。

属于：

Phase 05。

19. 输出要求

完成代码时必须输出：

第一部分

新增文件结构。

第二部分

完整代码。

每个文件注明：

路径。

第三部分

解释：

Mesh生成方式
参数设计
扩展方式
第四部分

测试方法。

20. 验收标准

Phase 02完成：

必须满足：

√ 可以创建Rectangle

√ 可以创建Circle

√ 可以创建Cube

√ 可以创建Cylinder

√ 可以创建Cone

√ 可以创建Sphere

√ 修改参数可以重新生成Mesh

√ 不依赖Renderer

21. 下一阶段

进入：

Phase 03 Renderer

下一阶段实现：

LWJGL窗口
OpenGL初始化
Shader
Camera
Mesh渲染
End

---

下一份：

```text
docs/PHASE_03_RENDERER.md
````
