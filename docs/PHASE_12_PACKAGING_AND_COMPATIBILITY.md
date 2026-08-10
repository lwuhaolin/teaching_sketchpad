# Geometry Teaching Engine

# Phase 12 Packaging & Compatibility System 开发任务


版本：

v1.0



---

# 1. 当前角色


你现在是一名：

Java 桌面应用工程架构工程师。


当前任务：

完成 Geometry Teaching Engine 的发布系统和兼容性系统。



---

# 2. 当前阶段目标


建立：


Application Runtime Layer



负责：


- 程序启动
- 环境检测
- 资源加载
- 配置管理
- 软件打包
- Windows兼容
- 性能优化



---

# 3. 当前软件目标平台


必须支持：


Windows：


Windows 7

Windows 10

Windows 11



---

# 4. 技术限制


必须考虑：

## Java版本


选择：

长期支持版本。


推荐：

Java 8 或 Java 11。


原因：

兼容：

- Win7
- 老机器
- 教室设备


禁止：

依赖：

- Java 17以上专属API
- Windows新API



---

# 5. 当前架构位置


最终结构：



Launcher

↓

Application Runtime

↓

UI Workspace

↓

Teaching Engine

↓

Scene

↓

Renderer

↓

LWJGL/OpenGL




---

# 6. 创建目录


新增：



runtime

├── ApplicationLauncher.java

├── RuntimeEnvironment.java

├── SystemChecker.java

│

├── config

│
├── AppConfig.java

├── ConfigLoader.java

│

├── resource

│
├── ResourceManager.java

├── AssetLoader.java

└── CacheManager.java

├── logging

│
├── Logger.java

└── CrashReporter.java

└── update

└── VersionManager.java

deploy

├── windows

├── installer

└── portable




---

# 7. ApplicationLauncher


创建：


ApplicationLauncher.java



职责：


程序入口。


负责：


启动顺序：



main()

↓

环境检测

↓

加载配置

↓

初始化资源

↓

启动UI

↓

启动Renderer



---

# 8. RuntimeEnvironment


检测运行环境。


包括：


```java
OS类型

Java版本

显卡信息

OpenGL版本

内存

CPU

9. SystemChecker

启动检测。

检查：

Java

是否满足要求。

OpenGL

检查：

支持：

OpenGL 2.1+

或者软件兼容模式。

文件权限

检查：

资源目录。

10. 配置系统

创建：

AppConfig

保存：

窗口大小

语言

主题

交互模式

渲染设置

性能设置

11. 配置文件格式

推荐：

json

或者

properties


示例：

{
 "width":1280,
 "height":720,
 "mode":"WHITEBOARD",
 "fps":60
}
12. ResourceManager

统一管理资源。

包括：

Texture
Shader
Model
Lesson文件
Audio

禁止：

业务代码直接读取文件。

13. AssetLoader

负责：

加载：

resources

↓

Memory

↓

Runtime


支持：

lazy loading
cache
14. CacheManager

减少重复加载。

例如：

第一次：

加载Cube模型。

之后：

直接使用缓存。

15. LWJGL发布处理

必须处理：

Native库。

例如：

lwjgl.dll

OpenGL native


要求：

自动加载。

16. Windows兼容处理

注意：

路径

禁止：

C:\xxx\xxx

必须：

跨平台路径。

使用：

Path

File

中文路径

测试：

桌面

用户名中文

DPI缩放

支持：

Windows高DPI。

例如：

4K白板。

17. 显卡兼容

检测：

如果：

OpenGL性能不足。

提供：

兼容模式。

例如：

High Mode

Normal Mode

Compatibility Mode

18. 性能优化

要求：

Renderer

支持：

Batch Render
Mesh缓存
减少DrawCall
Scene

避免：

每帧创建对象。

Animation

避免：

大量临时对象。

19. 日志系统

创建：

Logger

记录：

启动信息
模块加载
错误
性能数据
20. CrashReporter

崩溃自动记录。

保存：

logs/crash.log

内容：

时间

异常

堆栈

系统信息

OpenGL信息

21. 版本管理

创建：

VersionManager

管理：

major.minor.patch

例如：

1.0.0

22. 发布形式

必须支持两种。

Portable版本

目录：

GeometryEngine


├── app.jar

├── jre

├── lwjgl

├── resources

└── config


双击运行。

Installer版本

生成：

exe安装包

包含：

快捷方式
卸载程序
文件关联
23. Lesson文件支持

教学资源：

独立保存。

例如：

lesson


├── geometry

├── animation

├── annotation

└── config

24. 数据版本兼容

未来升级：

旧课件仍可打开。

设计：

LessonVersion


Migration

25. Demo要求

创建：

RuntimeDemo

测试：

启动程序。

输出：

Java Version

OS

OpenGL Version

Resource Status

26. 测试要求

创建：

CompatibilityTest

测试：

Windows环境

验证：

Win7

Win10

Win11

路径测试

中文路径。

资源测试

加载：

Cube

Cylinder

Lesson文件。

27. 当前禁止实现

禁止：

在线更新服务器

未来版本实现。

云端同步

未来实现。

用户账号系统

未来实现。

28. 输出要求

完成代码时必须输出：

第一部分：

新增目录结构。

第二部分：

完整代码。

第三部分：

说明：

发布方案
Win兼容策略
LWJGL部署方式
性能优化方案

第四部分：

测试方法。

29. 验收标准

Phase 12完成：

必须满足：

√ 程序可以独立启动

√ 支持Windows 7

√ 支持Windows 10/11

√ LWJGL native正常加载

√ 配置系统存在

√ 资源管理存在

√ 崩溃日志存在

√ 支持Portable发布

√ 支持Installer发布

√ 不破坏前面模块架构

30. 项目基础框架完成

完成Phase 12后：

Geometry Teaching Engine 基础架构完成。

最终能力：

Geometry Core

↓

Rendering Engine

↓

Interaction Engine

↓

Tool System

↓

Teaching System

↓

Animation System

↓

UI Workspace

↓

Runtime Packaging


后续开发进入：

产品功能阶段。

例如：

AI辅助教学
题库系统
网络课堂
课件市场
用户系统