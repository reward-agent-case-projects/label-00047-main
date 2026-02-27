-- ============================================
-- 学生成绩管理系统 - 数据库初始化脚本
-- ============================================
-- 该脚本会在 MySQL 容器首次启动时自动执行

-- 确保数据库存在（docker-compose 已创建，这里作为备份）
CREATE DATABASE IF NOT EXISTS student_db 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE student_db;

-- 学生表
CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    student_number VARCHAR(50) NOT NULL UNIQUE COMMENT '学号',
    name VARCHAR(100) NOT NULL COMMENT '学生姓名',
    class_name VARCHAR(100) NOT NULL COMMENT '班级名称',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生信息表';

-- 成绩表
CREATE TABLE IF NOT EXISTS scores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    subject_name VARCHAR(100) NOT NULL COMMENT '科目名称',
    score_value DECIMAL(5, 2) NOT NULL COMMENT '成绩分数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成绩信息表';

-- 创建索引以提升查询性能
CREATE INDEX idx_students_class ON students(class_name);
CREATE INDEX idx_scores_student ON scores(student_id);
CREATE INDEX idx_scores_subject ON scores(subject_name);
