-- 客户端字符集声明（避免 Windows 命令行按 GBK 解析 UTF-8 文件）
SET NAMES utf8mb4;

-- ============================================================
-- BookRecommendation 数据库初始化脚本（MySQL 8.x）
-- 用途：补全遗失的建表 SQL 与演示数据（含预置推荐结果，推荐功能开箱可演示）
-- 执行：mysql -uroot -proot < database/init.sql
-- 说明：user 表密码为明文（后端登录逻辑对库中密码再做 BCrypt 校验，明文可登录）
-- ============================================================

CREATE DATABASE IF NOT EXISTS `library` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `library`;

-- ---------- 用户表（系统登录） ----------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `account`   VARCHAR(32)  NOT NULL COMMENT '登录账号（主键）',
  `password`  VARCHAR(128) NOT NULL COMMENT '密码（登录时后端会再做 BCrypt 校验）',
  `username`  VARCHAR(64)  DEFAULT NULL COMMENT '显示名称',
  `time`      VARCHAR(32)  DEFAULT NULL COMMENT '注册时间',
  `head`      VARCHAR(255) DEFAULT NULL COMMENT '头像路径',
  `cert_id`   VARCHAR(32)  DEFAULT NULL COMMENT '关联读者学号',
  `status`    INT          DEFAULT 0 COMMENT '状态：0 正常',
  PRIMARY KEY (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ---------- 读者表（学生信息） ----------
DROP TABLE IF EXISTS `reader`;
CREATE TABLE `reader` (
  `CERT_ID`    VARCHAR(32)  NOT NULL COMMENT '学号（主键）',
  `NAME`       VARCHAR(64)  DEFAULT NULL COMMENT '姓名',
  `DEPT`       VARCHAR(64)  DEFAULT NULL COMMENT '学院/专业',
  `REDR_REG_D` VARCHAR(32)  DEFAULT NULL COMMENT '注册日期',
  `REDR_TYPE`  VARCHAR(16)  DEFAULT NULL COMMENT '读者类型',
  PRIMARY KEY (`CERT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='读者表';

-- ---------- 图书表 ----------
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
  `M_CALL_NO`   VARCHAR(64)  NOT NULL COMMENT '索书号（主键）',
  `M_TITLE`     VARCHAR(255) DEFAULT NULL COMMENT '书名',
  `M_AUTHOR`    VARCHAR(128) DEFAULT NULL COMMENT '作者',
  `M_PUBLISHER` VARCHAR(128) DEFAULT NULL COMMENT '出版社',
  `M_PUB_YEAR`  VARCHAR(16)  DEFAULT NULL COMMENT '出版年份',
  `Status`      VARCHAR(16)  DEFAULT NULL COMMENT '馆藏状态',
  `img`         VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  PRIMARY KEY (`M_CALL_NO`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';

-- ---------- 收藏表 ----------
DROP TABLE IF EXISTS `collect`;
CREATE TABLE `collect` (
  `cert_id` VARCHAR(32) NOT NULL COMMENT '学号',
  `call_no` VARCHAR(64) NOT NULL COMMENT '索书号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- ---------- 借阅表 ----------
DROP TABLE IF EXISTS `lend`;
CREATE TABLE `lend` (
  `CERT_ID`    VARCHAR(32) NOT NULL COMMENT '学号',
  `NAME`       VARCHAR(64) DEFAULT NULL COMMENT '姓名',
  `M_CALL_NO`  VARCHAR(64) NOT NULL COMMENT '索书号',
  `M_TITLE`    VARCHAR(255) DEFAULT NULL COMMENT '书名',
  `M_AUTHOR`   VARCHAR(128) DEFAULT NULL COMMENT '作者',
  `M_PUBLISHER` VARCHAR(128) DEFAULT NULL COMMENT '出版社',
  `LEND_DATE`  VARCHAR(32) DEFAULT NULL COMMENT '借阅日期'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅表';

-- ---------- 推荐结果表（Spark 计算回写，此处预置演示数据） ----------
DROP TABLE IF EXISTS `recommend`;
CREATE TABLE `recommend` (
  `CERT_ID`  VARCHAR(32) NOT NULL COMMENT '学号',
  `CALL_NO`  VARCHAR(64) NOT NULL COMMENT '索书号',
  `recommend` DOUBLE DEFAULT NULL COMMENT '推荐分数'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个性化推荐结果表';

-- ---------- 相关图书表 ----------
DROP TABLE IF EXISTS `relatedbook`;
CREATE TABLE `relatedbook` (
  `CALL_NO1` VARCHAR(64) NOT NULL COMMENT '图书1索书号',
  `CALL_NO2` VARCHAR(64) NOT NULL COMMENT '相关图书2索书号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='相关图书表';

-- ---------- 新书表 ----------
DROP TABLE IF EXISTS `newbook`;
CREATE TABLE `newbook` (
  `CALL_NO` VARCHAR(64) NOT NULL COMMENT '新书索书号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='新书表';

-- ============================================================
-- 演示数据
-- ============================================================

INSERT INTO `user` (`account`, `password`, `username`, `time`, `head`, `cert_id`, `status`) VALUES
('2020001', '123456', '张明', '2023-02-01', NULL, '2020001', 0),
('2020002', '123456', '李华', '2023-02-01', NULL, '2020002', 0),
('2020003', '123456', '王芳', '2023-02-01', NULL, '2020003', 0);

INSERT INTO `reader` (`CERT_ID`, `NAME`, `DEPT`, `REDR_REG_D`, `REDR_TYPE`) VALUES
('2020001', '张明', '计算机科学与技术', '2020-09-01', '本科生'),
('2020002', '李华', '软件工程', '2020-09-01', '本科生'),
('2020003', '王芳', '数据科学与大数据技术', '2020-09-01', '本科生');

INSERT INTO `book` (`M_CALL_NO`, `M_TITLE`, `M_AUTHOR`, `M_PUBLISHER`, `M_PUB_YEAR`, `Status`, `img`) VALUES
('TP312.8JA-1', 'Java 核心技术 卷I', '凯·S. 霍斯特曼', '机械工业出版社', '2020', 'False', NULL),
('TP312.8JA-2', 'Java 编程思想', 'Bruce Eckel', '机械工业出版社', '2007', 'True', NULL),
('TP311.13-1', '深入理解计算机系统', 'Randal E. Bryant', '机械工业出版社', '2016', 'True', NULL),
('TP311.56-1', 'Spring 实战', 'Craig Walls', '人民邮电出版社', '2022', 'False', NULL),
('TP311.56-2', 'Spring Boot 编程思想', '小马哥', '电子工业出版社', '2020', 'False', NULL),
('TP316.81-1', '鸟哥的 Linux 私房菜', '鸟哥', '人民邮电出版社', '2018', 'False', NULL),
('TP274-1', 'Spark 快速大数据分析', 'Holden Karau', '人民邮电出版社', '2019', 'False', NULL),
('TP274-2', 'Hadoop 权威指南', 'Tom White', '清华大学出版社', '2017', 'False', NULL),
('TP274-3', '数据挖掘导论', 'Pang-Ning Tan', '人民邮电出版社', '2019', 'True', NULL),
('TP393.09-1', 'Head First 设计模式', 'Eric Freeman', '中国电力出版社', '2007', 'False', NULL),
('TP311.52-1', '代码整洁之道', 'Robert C. Martin', '人民邮电出版社', '2020', 'False', NULL),
('TP311.52-2', '重构：改善既有代码的设计', 'Martin Fowler', '人民邮电出版社', '2019', 'True', NULL);

INSERT INTO `collect` (`cert_id`, `call_no`) VALUES
('2020001', 'TP312.8JA-1'),
('2020001', 'TP311.56-1'),
('2020002', 'TP274-1'),
('2020002', 'TP274-2'),
('2020003', 'TP393.09-1'),
('2020003', 'TP311.52-1');

INSERT INTO `lend` (`CERT_ID`, `NAME`, `M_CALL_NO`, `M_TITLE`, `M_AUTHOR`, `M_PUBLISHER`, `LEND_DATE`) VALUES
('2020001', '张明', 'TP312.8JA-2', 'Java 编程思想', 'Bruce Eckel', '机械工业出版社', '2023-03-01'),
('2020001', '张明', 'TP311.13-1', '深入理解计算机系统', 'Randal E. Bryant', '机械工业出版社', '2023-03-15'),
('2020002', '李华', 'TP274-3', '数据挖掘导论', 'Pang-Ning Tan', '人民邮电出版社', '2023-03-10'),
('2020003', '王芳', 'TP311.52-2', '重构：改善既有代码的设计', 'Martin Fowler', '人民邮电出版社', '2023-03-20');

-- 预置个性化推荐结果（Spark 计算回写的同构数据，推荐页开箱可演示）
INSERT INTO `recommend` (`CERT_ID`, `CALL_NO`, `recommend`) VALUES
('2020001', 'TP311.56-2', 0.92),
('2020001', 'TP312.8JA-2', 0.85),
('2020001', 'TP311.52-1', 0.78),
('2020001', 'TP274-1', 0.71),
('2020002', 'TP274-2', 0.95),
('2020002', 'TP274-3', 0.88),
('2020002', 'TP311.13-1', 0.76),
('2020002', 'TP312.8JA-1', 0.64),
('2020003', 'TP311.52-2', 0.90),
('2020003', 'TP393.09-1', 0.82),
('2020003', 'TP312.8JA-1', 0.73),
('2020003', 'TP316.81-1', 0.66);

-- 相关图书（详情页"相关推荐"）
INSERT INTO `relatedbook` (`CALL_NO1`, `CALL_NO2`) VALUES
('TP312.8JA-1', 'TP312.8JA-2'),
('TP312.8JA-1', 'TP311.56-1'),
('TP311.56-1', 'TP311.56-2'),
('TP274-1', 'TP274-2'),
('TP274-2', 'TP274-3'),
('TP311.52-1', 'TP311.52-2');

-- 新书（新书推荐页）
INSERT INTO `newbook` (`CALL_NO`) VALUES
('TP311.56-1'),
('TP311.56-2'),
('TP274-3'),
('TP393.09-1');
