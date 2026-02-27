# Kotlin 控制台学生成绩管理系统

一个基于 **Kotlin**、**Spring Boot** 和 **MySQL** 构建的专业级学生成绩管理系统，提供基于控制台的交互式界面。

## 功能特性

- **学生管理**：录入和管理学生信息（学号、姓名、班级）
- **成绩录入**：记录不同科目的成绩
- **自动排名**：自动计算平均分并生成排名报告
- **数据持久化**：使用 MySQL 数据库进行永久存储
- **Docker 支持**：容器化部署，一键启动

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Kotlin | 1.9.21 | 主要开发语言 |
| Spring Boot | 3.2.1 | 应用框架 |
| Spring Data JPA | - | 数据访问层 |
| MySQL | 8.0 | 关系型数据库 |
| Gradle | 8.5 | 构建工具 |
| Docker | - | 容器化部署 |

## 环境要求

- **Java**：JDK 17 或更高版本（用于本地运行）
- **MySQL**：版本 8.0（用于本地运行）
- **Docker & Docker Compose**：用于容器化运行

## 快速开始

### 方法 1：Docker（推荐）

此方法需要安装 **Docker** 和 **Docker Compose**，会自动配置数据库和应用程序。

```bash
# 1. 构建并启动所有服务
docker-compose up -d --build

# 2. 连接到控制台界面
docker attach student_app
# 注意：如果没有立即看到菜单，请按 Enter 键

# 3. 分离会话（不停止容器）
# 按 Ctrl + P，然后按 Ctrl + Q

# 4. 停止所有服务
docker-compose down

# 5. 停止并清除数据卷（重置数据库）
docker-compose down -v
```

### 方法 2：本地运行

#### 1. 数据库设置

确保本地 MySQL 服务器正在运行，然后创建数据库：

```sql
CREATE DATABASE student_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 2. 配置数据库连接

检查 `src/main/resources/application.properties`，根据本地环境调整配置：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/student_db?useSSL=false
spring.datasource.username=root
spring.datasource.password=root
```

#### 3. 构建与运行

```bash
# 使用 Gradle Wrapper（推荐）
./gradlew bootRun

# 或者使用本地 Gradle
gradle bootRun
```

## 项目结构

```
scoreManagement/
├── src/main/
│   ├── kotlin/com/essence/student/
│   │   ├── entity/              # 数据实体
│   │   │   ├── Student.kt       # 学生实体
│   │   │   └── Score.kt         # 成绩实体
│   │   ├── repository/          # 数据访问层
│   │   │   ├── StudentRepository.kt
│   │   │   └── ScoreRepository.kt
│   │   ├── service/             # 业务逻辑层
│   │   │   └── StudentService.kt
│   │   ├── ui/                  # 控制台交互
│   │   │   └── ConsoleUI.kt
│   │   └── StudentApplication.kt # 应用入口
│   └── resources/
│       ├── application.properties # 应用配置
│       └── schema.sql             # 数据库 Schema
├── mysql/init/                  # MySQL 初始化脚本
│   └── init.sql
├── build.gradle.kts             # Gradle 构建配置
├── Dockerfile                   # Docker 镜像构建
├── docker-compose.yml           # Docker Compose 编排
└── README.md                    # 项目说明
```

## Docker 配置说明

| 服务 | 容器名 | 端口 | 说明 |
|------|--------|------|------|
| student-app | student_app | - | 应用服务（控制台交互） |
| db | student_db | 3307:3306 | MySQL 数据库 |

**数据库连接信息**（Docker 环境）：
- Host: `localhost`
- Port: `3307`
- Database: `student_db`
- Username: `root`
- Password: `root_password`

## 常见问题

### Q: Docker 启动后无法连接数据库？

A: 确保 MySQL 容器健康检查通过后再启动应用。可以查看日志：

```bash
docker-compose logs -f
```

### Q: 如何重置数据库？

A: 使用以下命令清除数据卷：

```bash
docker-compose down -v
docker-compose up -d --build
```

### Q: 本地 3306 端口被占用？

A: Docker MySQL 已配置为使用 3307 端口，不会与本地 MySQL 冲突。

## 许可证

MIT License
