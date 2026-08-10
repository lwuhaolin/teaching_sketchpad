# Geometry Teaching Engine

# Phase 04 Scene System 开发任务

版本：

v1.0

---

# 1. 当前角色

你现在是一名：

Java 图形引擎架构工程师。

当前任务：

实现 Geometry Teaching Engine 场景管理系统。

---

# 2. 当前阶段目标

建立：

Scene System

负责管理所有：

GeometryObject

最终实现：

多个几何对象

↓

统一管理

↓

统一更新

↓

统一渲染

---

# 3. 架构原则

## 3.1 Scene不负责创建对象

错误：

Scene里面创建Cube

例如禁止：

````java
scene.createCube();

原因：

Scene只管理。

正确：

GeometryFactory

↓

创建对象

↓

Scene.addObject()
4. 新增目录

创建：

scene


├── Scene.java


├── SceneObject.java


├── Layer.java


├── ObjectManager.java


└── SelectionManager.java

5. Scene核心设计

文件：

scene/Scene.java

职责：

管理：

GeometryObject集合

包含：

List<GeometryObject>

提供：

addObject()

removeObject()

getObjects()

clear()

update()

render()
6. Scene生命周期

标准流程：

Application


↓

Scene.update()


↓

Object.update()


↓

Renderer.render()


7. SceneObject

创建：

SceneObject.java

作用：

作为场景中的对象包装。

包含：

GeometryObject

Transform

ID

Visible状态

结构：

SceneObject

{

id

geometry

visible

transform

}
8. ObjectManager

创建：

ObjectManager.java

负责：

对象操作。

支持：

add()

remove()

findById()

getAll()
9. 对象唯一ID

每个对象必须拥有：

唯一ID。

例如：

object_001

object_002

用途：

未来：

保存文件
Undo
选择
10. Layer系统

创建：

Layer.java

作用：

管理对象分组。

例如：

Layer:

基础图形


Layer:

辅助线


Layer:

标注

包含：

name

objects
11. Selection系统

创建：

SelectionManager.java

作用：

管理当前选择对象。

支持：

选择：

select()

取消：

clearSelection()

查询：

getSelected()
12. 选择状态

SceneObject增加：

selected

状态。

例如：

Cube

selected=true

未来用于：

高亮
拖动
修改参数
13. 渲染流程

修改渲染流程：

之前：

Cube

↓

Renderer

现在：

Scene


↓

SceneObject


↓

GeometryObject


↓

Mesh


↓

Renderer
14. 更新流程

每帧：

Application Loop


↓

Scene.update()


↓

Geometry update


↓

Renderer.render()
15. Demo要求

创建：

SceneDemo

要求：

场景中加入：

Cube


Cylinder


Rectangle

显示：

三个对象。

16. 测试要求

创建：

SceneTest

测试：

添加对象

加入：

Cube

检查：

数量增加。

删除对象

删除：

Cube

检查：

数量减少。

查询

通过ID：

找到对象。

17. 当前禁止实现

禁止：

1. 鼠标选择

不要实现：

Ray Casting

属于：

Phase 05。

2. 工具系统

禁止：

MoveTool

RotateTool

属于：

Phase 06。

3. 保存系统

禁止：

JSON

文件保存

属于：

Phase 10。

18. 输出要求

完成代码时：

必须输出：

第一部分

目录变化。

第二部分

完整代码。

每个文件：

标注路径。

第三部分

解释：

Scene职责
Object生命周期
渲染流程
第四部分

测试方法。

19. 验收标准

Phase 04完成：

必须满足：

√ Scene可以管理多个对象

√ 对象可以添加删除

√ Renderer可以遍历Scene渲染

√ 存在Selection接口

√ 不包含鼠标逻辑

√ 不包含工具逻辑

20. 下一阶段

进入：

Phase 05 Interaction System

下一阶段实现：

鼠标输入
键盘输入
对象拾取
拖拽
缩放
旋转
End

---

下一份：

```text
docs/PHASE_05_INTERACTION_SYSTEM.md
````
