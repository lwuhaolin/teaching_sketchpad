# Geometry Teaching Engine

# Phase 00 项目初始化开发任务

文件：

PHASE_00_SETUP.md

版本：

v1.0

---

# 1. 当前角色

你现在是一名：

Java 项目架构工程师

负责初始化：

Geometry Teaching Engine

当前任务：

建立项目基础环境。

---

# 2. 当前阶段目标

完成：

- Java 项目结构
- 构建系统
- 基础依赖
- 启动入口

目标：

让项目可以正常编译运行。

---

# 3. 技术要求

开发语言：

Java

构建工具：

优先：

Gradle

备用：

Maven

---

图形库：

添加：

LWJGL

但当前阶段：

不初始化 OpenGL。

原因：

Phase 00 只负责项目环境。

---

# 4. 当前目录结构

创建：

GeometryTeachingEngine

├── build.gradle

├── settings.gradle

├── README.md

├── docs

│

└── src

└── main

    ├── java


    │


    │   └── com.geometry


    │


    │       └── app


    │


    │           └── Main.java


    │


    └── resources

---

# 5. Gradle配置要求

创建：

build.gradle

要求：

包含：

- Java插件
- LWJGL依赖
- 编译配置

注意：

不要引入：

- 游戏引擎
- UI框架
- 第三方几何库

---

# 6. Main启动类

创建：

路径：

src/main/java/com/geometry/app/Main.java

要求：

代码功能：

打印：

Geometry Teaching Engine Started

示例：

````java
package com.geometry.app;


public class Main {


    public static void main(String[] args){


        System.out.println(
            "Geometry Teaching Engine Started"
        );


    }

}
7. README要求

创建：

README.md

内容包含：

项目介绍
技术栈
如何运行
当前开发阶段

示例：

# Geometry Teaching Engine


Java + LWJGL 几何教学引擎


Current Phase:

Phase 00 Setup

8. 当前禁止实现

这一阶段禁止：

禁止1

创建 Geometry 类。

例如：

不要创建：

Cube

Sphere

Cylinder

原因：

这些属于 Phase 02。

禁止2

不要创建：

Renderer

不要创建：

OpenGLRenderer
Camera
Shader

原因：

属于 Phase 03。

禁止3

不要创建：

UI。

例如：

禁止：

JavaFX

Swing界面

禁止4

不要提前设计复杂架构。

当前目标：

先让项目运行。

9. 输出要求

完成代码时必须输出：

第一部分

项目结构：

例如：

GeometryTeachingEngine

├── src

...
第二部分

修改文件列表：

例如：

新增：

build.gradle

Main.java

README.md

第三部分

完整代码。

每个文件：

必须标注路径。

格式：

文件：

xxx/xxx.java


代码：

xxxxx
第四部分

运行方式。

说明：

Windows：

gradlew run

或者：

java Main
10. 验收标准

Phase 00 完成条件：

必须满足：

√ 项目可以编译

√ Main可以运行

√ 输出：

Geometry Teaching Engine Started

√ 目录符合规范

√ 没有引入未来阶段代码

11. 完成后状态

完成 Phase 00 后：

项目状态：

可以运行的Java项目

下一阶段：

Phase 01 Geometry Core
End

---

下一份继续：

```text
docs/PHASE_01_GEOMETRY_CORE.md
````
