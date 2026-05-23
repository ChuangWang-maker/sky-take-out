# ☁️ 苍穹外卖 Sky Take-out

> 黑马程序员全栈实战项目 — 基于 **Spring Boot 2.7** 的企业级外卖平台

苍穹外卖是一个面向 **B 端（管理端）+ C 端（用户端）** 双端分离的外卖系统。管理端提供员工管理、菜品管理、订单运营、数据报表等后台功能；用户端提供微信登录、菜品浏览、购物车、下单支付、历史订单等完整点餐体验。

---

## 📸 项目结构

```
sky-take-out
├── sky-common          # 公共模块：工具类、常量、异常、配置属性、统一返回结果
├── sky-pojo            # 数据模型：Entity / DTO / VO
└── sky-server          # 核心服务：Controller / Service / Mapper / Config
    ├── controller
    │   ├── admin/      # 管理端接口（10 个 Controller）
    │   ├── user/       # 用户端接口（8 个 Controller）
    │   └── notify/     # 微信支付回调
    ├── service/        # 业务逻辑层（10 个 Service + 实现）
    ├── mapper/         # 数据访问层（11 个 Mapper + XML）
    ├── config/         # 配置类：WebMvc / Redis / OSS / WebSocket / Knife4j
    ├── interceptor/    # JWT 双端拦截器（管理端 + 用户端）
    ├── aspect/         # AOP 公共字段自动填充
    ├── handler/        # 全局异常处理器
    ├── websocket/      # WebSocket 实时消息
    └── task/           # 定时任务（订单状态检查）
```

---

## 🛠️ 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| **框架** | Spring Boot 2.7.3 | 核心框架 |
| **ORM** | MyBatis + MyBatis Spring Boot Starter 2.2.0 | 数据访问层 |
| **连接池** | Druid 1.2.1 | 数据库连接池 + 监控 |
| **分页** | PageHelper 1.3.0 | 物理分页插件 |
| **缓存** | Redis（Spring Data Redis） | 高频数据缓存 |
| **认证** | JWT（jjwt 0.9.1） | 双端 token 鉴权 |
| **接口文档** | Knife4j 3.0.2 | Swagger 增强版 UI |
| **文件存储** | 阿里云 OSS | 图片/文件上传 |
| **实时通信** | WebSocket | 订单状态推送 |
| **AOP** | AspectJ | 公共字段自动填充 |
| **支付** | 微信支付 APIv3 | 微信小程序支付 |
| **Excel** | Apache POI 3.16 | 运营数据报表导出 |
| **序列化** | FastJSON 1.2.76 | JSON 处理 |
| **工具** | Lombok / Commons-Lang | 简化开发 |

---

## 🎯 核心功能

### 🔐 双端鉴权体系
- **管理端** JWT 拦截器 → 拦截 `/admin/**`，排除登录接口
- **用户端** JWT 拦截器 → 拦截 `/user/**`，排除登录和店铺状态
- 自定义 `BaseContext` ThreadLocal 存储当前用户 ID

### ✏️ AOP 自动字段填充
```java
@AutoFill(OperationType.INSERT)  // 自动填充 create_time / create_user / update_time / update_user
@AutoFill(OperationType.UPDATE)  // 自动填充 update_time / update_user
```
- 自定义 `@AutoFill` 注解 + `AutoFillAspect` 切面，通过**反射**为实体公共字段赋值
- 彻底消除 Service 层重复书写公共字段的模板代码

### 👨‍💼 管理端功能
| 模块 | 核心能力 |
|------|----------|
| **员工管理** | 登录/退出、分页查询、启用禁用、编辑 |
| **分类管理** | 菜品/套餐分类 CRUD、分页查询 |
| **菜品管理** | 新增/修改/起售停售、图片上传至 OSS |
| **套餐管理** | 套餐 CRUD、关联菜品、起售停售 |
| **订单管理** | 订单搜索、详情查看、接单/拒单/派送/完成 |
| **店铺管理** | 营业状态开关 |
| **数据报表** | 营业额/用户/订单 Top10/销量排名 → Excel 导出 |
| **工作台** | 运营数据概览 |

### 👤 用户端功能
| 模块 | 核心能力 |
|------|----------|
| **微信登录** | 微信授权码 → OpenID → JWT |
| **菜品浏览** | 按分类查询菜品/套餐 |
| **购物车** | 添加/查看/清空/数量变更 |
| **下单支付** | 提交订单 → 微信支付预下单 |
| **历史订单** | 订单列表/详情/取消/再来一单 |
| **地址管理** | 收货地址 CRUD + 默认地址 |

### 📊 运营数据报表
- **营业额统计** — 按日/按月趋势
- **用户统计** — 新增用户数
- **订单统计** — 有效订单数、订单完成率
- **销量 Top10** — 商品销量排名
- 支持 **Excel 导出**（Apache POI）

### ⏰ 定时任务
- 订单超时自动取消（`OrderTask`）
- WebSocket 定时推送消息（`WebSocketTask`）
- `MyTask` 自定义定时任务

### 🛡️ 全局异常处理
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 统一捕获所有异常 → 返回 Result.error("错误信息")
}
```

---

## 🚀 快速启动

### 环境要求
- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- Redis

### 配置

1. 创建数据库 `sky_take_out`，执行 `sky-server/resources/sql/` 下的初始化脚本
2. 修改 `application-dev.yml` 中的配置（数据库、Redis、OSS、微信支付等）
3. 或在运行前设置环境变量：
   ```
   sky.datasource.host=localhost
   sky.datasource.port=3306
   sky.datasource.database=sky_take_out
   sky.datasource.username=root
   sky.datasource.password=xxx
   sky.redis.host=localhost
   sky.redis.port=6379
   sky.redis.password=xxx
   ```

### 启动

```bash
cd sky-server
mvn spring-boot:run
```

应用启动后访问：
- **API 文档（Knife4j）：** http://localhost:8080/doc.html
- **管理端登录：** `POST /admin/employee/login`

---

## 📦 模块依赖

```
sky-common  ←  sky-pojo  ←  sky-server
```

- `sky-common` — 无依赖，纯工具类与常量
- `sky-pojo` — 依赖 sky-common（使用父模块管理）
- `sky-server` — 依赖 sky-common + sky-pojo，包含所有运行时配置与启动类

---

## 📝 关键设计

| 设计点 | 说明 |
|--------|------|
| **双端路由分离** | `/admin/**` 管理端、`/user/**` 用户端，各自独立 JWT 拦截器 |
| **统一返回格式** | `Result<T>` 泛型封装，全局统一 code / msg / data |
| **Jackson 定制** | `JacksonObjectMapper` — 解决 Long 精度丢失、日期格式化 |
| **WebSocket** | 订单状态变更实时推送到管理端大屏 |
| **微信支付** | 完整的支付流程：下单、回调、退款 |
| **OSS 上传** | 阿里云 OSS SDK，支持菜品/套餐图片上传 |

---

## 📄 License

This project is for **learning and interview demonstration purposes**. Based on the Heima Programmer (黑马程序员) training curriculum.
