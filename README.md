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
