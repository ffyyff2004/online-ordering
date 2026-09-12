# 在线点餐系统

这是一个基于 Spring Boot、MyBatis、MySQL 和 Vue.js 的在线点餐系统，包含用户端和管理端。

## 项目结构

- `vue_wsdcxt/`：Spring Boot 后端及前端静态页面
- `vue_wsdcxt.sql`：数据库表结构和示例数据

## 本地运行

1. 创建 MySQL 数据库 `vue_wsdcxt`。
2. 执行根目录下的 `vue_wsdcxt.sql`。
3. 设置数据库环境变量（也可以使用默认值）：

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "你的MySQL密码"
```

4. 在 `vue_wsdcxt` 目录执行：

```powershell
mvn spring-boot:run
```

5. 浏览器访问：

- 用户端：`http://localhost:8080/vue_wsdcxt/users/index.html`
- 管理端：`http://localhost:8080/vue_wsdcxt/admin/index.html`

## 说明

- `target/` 等构建生成文件不会提交到 GitHub。
- 数据库连接密码通过环境变量配置，不在代码中保存真实密码。
- SQL 文件中的账号仅用于本地演示，部署时请及时修改。

## 智能选餐助手

首页右下角的“智能选餐助手”会读取数据库中的当前餐品，将用户的口味、预算和忌口发送给大模型，并且只返回数据库中存在的 3 个餐品方案。用户可以直接把方案加入购物车。

启动后配置大模型 API Key：

```powershell
$env:AI_API_KEY = "你的API Key"
$env:AI_MODEL = "gpt-4o-mini"
```

也可以通过 `AI_BASE_URL` 配置兼容 OpenAI 接口格式的模型服务地址。
