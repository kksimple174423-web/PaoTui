# 基于 Spring Boot 的校园跑腿任务系统

> 毕业设计项目 · 软件工程专业

## 一、项目简介

高校校园里的代取快递、代买餐食、代送物品等零散需求，长期依靠熟人帮忙或分散的社交群发布，供需信息难以匹配，费用与责任缺少凭证，容易产生纠纷。

本系统面向这一实际场景，把**需求发布、接单匹配、赏金托管、进度跟踪、双向评价与信用积累**整合到统一平台，形成完整的任务闭环。学生可以就近利用碎片时间获得收入，也能更便捷地解决日常跑腿需求。

## 二、技术栈

| 层次 | 技术选型 |
| --- | --- |
| 后端框架 | Spring Boot 2.5.5 |
| 持久层 | MyBatis-Plus 3.5.2 |
| 数据库连接池 | Druid 1.2.12 |
| 数据库 | MySQL 8.0 |
| 前端框架 | Vue 3.2 + TypeScript |
| 构建工具 | Vite 4 |
| UI 组件库 | Ant Design Vue 3 |
| 状态管理 | Pinia |
| HTTP 客户端 | Axios |
| 运行环境 | JDK 21（编译目标 1.8）、Node.js 16+ |

## 三、功能模块

### 1. 用户端

- **注册登录**：学号注册、登录、密码修改、个人资料维护
- **任务发布**：选择任务类型，填写取送地址、期望时间、悬赏金额，上传物品图片
- **任务大厅**：按任务类型、标签、状态筛选，支持"最新 / 最热 / 赏金最高"排序
- **任务详情**：查看任务全部信息，一键接单
- **订单管理**：我发布的 / 我接的单，状态跟踪、确认完成、取消任务
- **钱包与支付**：余额充值、赏金托管、任务完成后自动结算、提现申请、资金明细
- **评价与信用**：双向评价打分，信用分自动累计，评价回复
- **任务收藏与关注**

### 2. 骑手端

- **骑手认证**：提交真实姓名、学号、院系、服务区域与时段、学生证照片，等待管理员审核
- **接单工作台**：可接任务抢单、进行中任务管理、推送配送进度（开始配送 / 标记已送达）
- **收益管理**：完成单数、累计收益、信用分、资金流水与提现
- **评价管理**：查看收到的评价与信用记录

### 3. 管理端

- **用户管理**：用户信息查询、违规处理
- **任务与订单管理**：任务增删改查、订单监控、状态干预
- **骑手认证审核**：认证资料审核，通过 / 驳回（附审核意见）
- **提现审核**：提现申请打款或驳回
- **内容审核**：评价内容管理
- **公共信息**：公告、任务分类、标签、轮播图、广告位维护
- **数据分析**：任务量、各状态分布、完成率、用户与骑手规模、交易额、近七日访问量
- **日志管理**：登录日志、操作日志、错误日志

## 四、任务状态流转

```
0 待接单 ──接单──> 1 已接单 ──取件──> 2 配送中 ──送达──> 3 已送达 ──确认──> 4 已完成
   │                    │
   └────── 5 已取消 ─────┘                    6 纠纷中（异常状态）
```

## 五、赏金托管与结算流程

系统采用**赏金托管**机制保障双方权益：

1. 发布任务时，悬赏金额从发布者**可用余额划入冻结金额**（余额不足则不能发布）
2. 骑手接单后开始配送，任务进入进行中状态
3. 骑手标记送达，发布者确认完成后，冻结的赏金**自动划转到骑手余额**
4. 任务被取消时，托管的赏金**自动退回**发布者余额
5. 骑手可发起提现申请，管理员审核打款

每一笔资金变动都会写入资金流水表，可追溯。

## 六、项目结构

```
PTproject/
├── server/                        后端服务（Spring Boot）
│   ├── src/main/java/com/gk/study/
│   │   ├── controller/            接口层（任务/订单/钱包/认证/评价等）
│   │   ├── service/               业务层
│   │   ├── mapper/                数据访问层
│   │   ├── entity/                实体类
│   │   ├── interceptor/           登录鉴权与日志拦截器
│   │   ├── permission/            权限注解
│   │   └── utils/                 工具类
│   ├── src/main/resources/
│   │   ├── application.yml        端口、数据源、上传路径配置
│   │   ├── mapper/                MyBatis XML
│   │   └── logback-spring.xml     日志配置
│   └── pom.xml
├── web/                           前端工程（Vue3 + Vite）
│   ├── src/api/                   接口封装
│   ├── src/views/index/           用户端页面
│   ├── src/views/admin/           管理端页面
│   ├── src/router/                路由配置
│   ├── src/store/                 全局状态与基础配置
│   └── vite.config.ts
├── doc/database/                  数据库脚本
│   └── campus_run_migration.sql   建表与初始化数据
└── .vscode/                       VSCode 一键启动配置
```

## 七、数据库设计

数据库名 `java_food`，共 23 张表。

**核心业务表**

| 表名 | 说明 |
| --- | --- |
| `b_user` | 用户表（普通用户 / 骑手 / 管理员统一账号） |
| `b_task` | 跑腿任务表 |
| `b_order` | 接单订单表 |
| `b_comment` | 订单双向评价表 |
| `b_runner_verify` | 骑手认证申请 |
| `b_wallet` | 用户钱包（可用余额 / 冻结金额 / 累计收支） |
| `b_wallet_record` | 钱包资金流水 |
| `b_withdraw` | 提现申请 |
| `b_credit_record` | 信用分变更记录 |
| `b_message` | 订单即时消息 |
| `b_report` | 投诉举报 |

**辅助表**

| 表名 | 说明 |
| --- | --- |
| `b_classification` | 任务分类（代取快递 / 代买餐食 / 代送物品 / 代排队 / 代打印 / 其他） |
| `b_tag` / `b_task_tag` | 任务标签与关联 |
| `b_task_collect` / `b_task_wish` | 任务收藏 / 关注 |
| `b_address` | 用户地址 |
| `b_notice` | 公告 |
| `b_banner` / `b_ad` | 首页轮播图 / 广告位 |
| `b_login_log` / `b_op_log` / `b_error_log` | 登录日志 / 操作日志 / 错误日志 |

关键设计说明：

- **骑手身份**采用 `b_user.runner_status` 字段标识（0未申请 / 1待审核 / 2已认证 / 3已驳回），而非独立角色。因为跑腿业务中任何人都可以发布任务，认证通过后即可接单，同一用户可在两种身份间切换
- 任务状态与订单状态共用一套编码，便于状态同步
- 金额相关字段使用 decimal 存储，计算统一走 BigDecimal

## 八、快速开始

### 1. 环境要求

- JDK 21（本机已验证；项目编译目标为 1.8，JDK 8/11/17 亦可）
- Maven 3.6+
- MySQL 8.0（5.7 亦可）
- Node.js 16+（本机 Node 22 已验证）

### 2. 初始化数据库

```sql
CREATE DATABASE IF NOT EXISTS java_food DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;
```

然后导入建表脚本（包含示例数据）：

```
mysql -u root -p --default-character-set=utf8mb4 java_food < doc/database/campus_run_migration.sql
```

> 注意：若是在已有数据库上执行改造脚本，请先确认是否已执行过，避免重复建表报错。

### 3. 修改配置

编辑 `server/src/main/resources/application.yml`，确认这几项与你的环境一致：

```yaml
BASE_LOCATION: <项目所在路径>      # 例如 E:\毕业\PTproject
spring:
  datasource:
    username: root
    password: <你的 MySQL 密码>
    url: jdbc:mysql://localhost:3306/java_food?...
```

### 4. 启动后端

```bash
cd server
mvn spring-boot:run
```

启动成功后服务地址为 `http://localhost:9100/api`。

### 5. 启动前端

```bash
cd web
npm install          # 首次运行
npm run dev
```

浏览器访问 `http://localhost:8000`。

### 6. 使用 VSCode 一键启动

项目已内置启动配置（`.vscode/tasks.json`）：按 `Ctrl+Shift+P` → `Tasks: Run Task` → **▶ 一键启动（前端+后端）**，即可同时启动两个服务。

## 九、测试账号

| 账号 | 密码 | 身份 |
| --- | --- | --- |
| `stu001` | 123456 | 普通用户（已认证骑手） |
| `stu002` | 123456 | 普通用户 |
| `runner001` | 123456 | 认证骑手 |
| `runner002` | 123456 | 认证骑手 |
| `admin123` | admin123 | 后台管理员 |

后台管理地址：`http://localhost:8000/adminLogin`

## 十、主要接口

| 模块 | 接口 | 说明 |
| --- | --- | --- |
| 任务 | `GET /api/task/list` | 任务大厅（关键词 / 类型 / 标签 / 状态 / 排序） |
| 任务 | `GET /api/task/hall` | 仅待接单任务 |
| 任务 | `POST /api/task/create` | 发布任务（自动托管赏金） |
| 任务 | `POST /api/task/accept` | 骑手接单 |
| 任务 | `POST /api/task/updateStatus` | 任务状态流转 |
| 订单 | `GET /api/order/userOrderList` | 我的订单 |
| 钱包 | `GET /api/wallet/my` | 我的钱包 |
| 钱包 | `POST /api/wallet/recharge` | 充值 |
| 钱包 | `POST /api/wallet/withdraw` | 提现申请 |
| 认证 | `POST /api/runnerVerify/apply` | 提交骑手认证 |
| 评价 | `POST /api/comment/create` | 发表评价（自动累计信用分） |

## 十一、开发说明

- **鉴权**：接口通过 `@Access` 注解声明权限级别，用户端请求带 `TOKEN` 请求头，管理端带 `ADMINTOKEN` 请求头，由拦截器统一校验
- **登录令牌**：登录成功后签发随机 token 并写入数据库，前端保存在 localStorage 实现免登录
- **文件上传**：任务图片、头像、认证证件照统一保存在 `server/upload` 目录下，通过 `/api/staticfiles/**` 访问
- **信用分规则**：5星 +2、4星 +1、3星不变、2星 -1、1星 -3，锁定在 0~150 区间，每次变动写入信用分记录表

## 十二、后续可扩展方向

- 即时通讯模块（WebSocket 实时聊天，数据表 `b_message` 已建好）
- 投诉举报处理流程（数据表 `b_report` 已建好）
- 送达凭证上传
- 任务超时自动处理与纠纷仲裁