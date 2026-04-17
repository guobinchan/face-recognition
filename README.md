# 人脸识别系统

## 项目简介

这是一个基于 Vue 3 和 Spring Boot 3 的人脸识别系统，具有以下功能：

1. **人脸库管理**：用户可以批量上传包含人脸的图片，形成一个人脸库
2. **人脸识别搜索**：用户可以通过上传一个图片，在人脸库中查找匹配的人脸

## 技术栈

- **前端**：Vue 3 + Vite
- **后端**：Spring Boot 3, MyBatis-Plus
- **人脸识别**：OpenCV
- **数据库**：MySQL

## 数据库设计

### 表结构

#### 人脸库表

- **主键**：`id`（自增整数）
- **字段**：
  - `name`（人脸库名称）
  - `description`（人脸库描述）
  - `created_at`（创建时间）
  - `updated_at`（更新时间）
  - `is_deleted`（是否删除，0：未删除，1：已删除）
  - `file_path`（人脸库文件路径，用于存储人脸图片）
  - `face_embedding`（人脸嵌入向量）

## 项目结构

```
memo/
├── frontend/          # 前端项目
│   ├── index.html     # 前端入口页面
│   ├── package.json   # 前端项目配置
│   ├── vite.config.js # Vite 配置
│   └── src/           # 前端源代码
├── backend/           # 后端项目
│   ├── pom.xml        # Maven 配置
│   └── src/           # 后端源代码
└── README.md          # 项目说明
```

## 安装步骤

### 前端安装

1. 进入前端目录
```bash
cd frontend
```

2. 安装依赖
```bash
npm install
```

3. 启动开发服务器
```bash
npm run dev
```

### 后端安装

1. 进入后端目录
```bash
cd backend
```

2. 编译项目
```bash
mvn clean package
```

3. 运行项目
```bash
java -jar target/face-recognition-backend-0.0.1-SNAPSHOT.jar
```

### 环境要求

- **前端**：Node.js 16+，npm 7+
- **后端**：Java OpenJDK 21.0.10，Maven 3.8.7
- **数据库**：MySQL 8+
- **OpenCV**：4.6.0+

## 配置说明

### 后端配置

在 `backend/src/main/resources/application.properties` 文件中配置：

```properties
# 服务器配置
server.port=8080

# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/face_recognition
spring.datasource.username=root
spring.datasource.password=mM741741

# 文件存储配置
file.upload-dir=./uploads
face.recognizer-model=./models/haarcascade_frontalface_default.xml
```

### 前端配置

在 `frontend/vite.config.js` 文件中配置后端 API 代理：

```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## 使用说明

1. **人脸库管理**：
   - 进入系统后，在「人脸库管理」区域点击或拖拽图片到上传区域
   - 选择多个包含人脸的图片进行批量上传
   - 点击「上传到服务器」按钮，将图片上传到后端服务器

2. **人脸识别搜索**：
   - 在「人脸识别搜索」区域点击或拖拽一张包含人脸的图片
   - 点击「开始搜索」按钮，系统会在人脸库中查找匹配的人脸
   - 搜索结果会显示在页面下方，包括匹配的图片和相似度

## 注意事项

1. 确保安装了 OpenCV 库，并在系统环境变量中配置了 OpenCV 的路径
2. 确保 PostgreSQL 数据库已创建，并且配置了正确的数据库连接信息
3. 上传的图片应包含清晰的人脸，以提高识别准确率
4. 对于大量图片的上传，可能会需要较长的处理时间

## 优化建议

1. **性能优化**：
   - 实现图片压缩，减少上传和处理时间
   - 使用异步处理，提高系统响应速度
   - 实现人脸特征提取和存储，提高搜索速度

2. **功能扩展**：
   - 添加人脸标注和管理功能
   - 实现人脸识别的实时预览
   - 添加用户认证和权限管理

3. **用户体验**：
   - 添加上传进度显示
   - 优化搜索结果的展示方式
   - 添加错误处理和提示信息
