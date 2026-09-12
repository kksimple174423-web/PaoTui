-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: java_food
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `java_food`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `java_food` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `java_food`;

--
-- Table structure for table `b_ad`
--

DROP TABLE IF EXISTS `b_ad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `b_ad` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `image` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `link` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `create_time` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='首页广告位';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_ad`
--

LOCK TABLES `b_ad` WRITE;
/*!40000 ALTER TABLE `b_ad` DISABLE KEYS */;
INSERT INTO `b_ad` VALUES (1,'8164fc3b-f47b-4b0e-ba26-50602d4ef276.png','www.google.com','1681724561446'),(3,'64c66b8f-b82e-4c7a-b714-9adc32cd5ff6.jpeg','http://www.baidu.com111','1681725297763'),(4,'5f299ffb-c2f8-4ade-980e-cba7d4938519.png','http://www.baidu.com/kkk','1681725339563'),(6,'eb71dae5-b84d-4b9a-8e36-5b2adf25c4e2.png','www.bing.com','1682734858115');
/*!40000 ALTER TABLE `b_ad` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_address`
--

DROP TABLE IF EXISTS `b_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `b_address` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `mobile` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `description` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `def` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `create_time` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `b_address_user_id_a37a8d6a_fk_b_user_id` (`user_id`) USING BTREE,
  CONSTRAINT `b_address_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `b_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='用户地址表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_address`
--

LOCK TABLES `b_address` WRITE;
/*!40000 ALTER TABLE `b_address` DISABLE KEYS */;
INSERT INTO `b_address` VALUES (19,'单独',NULL,NULL,NULL,'1683465050274',NULL),(22,'aa','d',NULL,NULL,'1683543865613',NULL),(23,'d','d',NULL,NULL,'1683543878597',NULL),(29,'刘德华','1883333','3fddd','0','1687344510420',2);
/*!40000 ALTER TABLE `b_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_banner`
--

DROP TABLE IF EXISTS `b_banner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `b_banner` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `image` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `create_time` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `task_id` bigint DEFAULT NULL COMMENT '关联任务ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_banner_task` (`task_id`) USING BTREE,
  CONSTRAINT `fk_banner_task` FOREIGN KEY (`task_id`) REFERENCES `b_task` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='首页轮播图';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_banner`
--

LOCK TABLES `b_banner` WRITE;
/*!40000 ALTER TABLE `b_banner` DISABLE KEYS */;
/*!40000 ALTER TABLE `b_banner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_classification`
--

DROP TABLE IF EXISTS `b_classification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `b_classification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `create_time` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='任务分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_classification`
--

LOCK TABLES `b_classification` WRITE;
/*!40000 ALTER TABLE `b_classification` DISABLE KEYS */;
INSERT INTO `b_classification` VALUES (1,'代取快递','1681347523973'),(2,'代买餐食','1681347533598'),(3,'代送物品','1681347534635'),(4,'代排队','1789200275000'),(5,'代打印','1789200275000'),(6,'其他跑腿','1789200275000');
/*!40000 ALTER TABLE `b_classification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_comment`
--

DROP TABLE IF EXISTS `b_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `b_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint DEFAULT NULL COMMENT '关联订单ID',
  `content` varchar(500) DEFAULT NULL COMMENT '评价内容',
  `comment_time` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `like_count` int NOT NULL DEFAULT '0',
  `from_user_id` bigint DEFAULT NULL COMMENT '评价人ID',
  `task_id` bigint DEFAULT NULL COMMENT '任务ID',
  `to_user_id` bigint DEFAULT NULL COMMENT '被评价人ID',
  `score` int NOT NULL DEFAULT '5' COMMENT '评分(1-5星)',
  `role` varchar(2) DEFAULT NULL COMMENT '评价人角色 1用户评骑手 2骑手评用户',
  `reply` varchar(500) DEFAULT NULL COMMENT '被评价人回复',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `b_comment_user_id_46f0670f_fk_b_user_id` (`from_user_id`) USING BTREE,
  KEY `idx_comment_task` (`task_id`) USING BTREE,
  CONSTRAINT `b_comment_ibfk_1` FOREIGN KEY (`from_user_id`) REFERENCES `b_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_comment_task` FOREIGN KEY (`task_id`) REFERENCES `b_task` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='订单双向评价表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_comment`
--

LOCK TABLES `b_comment` WRITE;
/*!40000 ALTER TABLE `b_comment` DISABLE KEYS */;
INSERT INTO `b_comment` VALUES (19,77,'骑手很准时，饭菜还是热的，服务态度很好，下次还找他！','1789119275000',3,10,103,12,5,'1',NULL),(20,77,'用户很好沟通，地址描述清楚，取餐很顺利，感谢信任！','1789120275000',1,12,103,10,5,'2',NULL),(21,NULL,'骑手很给力，很快就送到了！','1789203118973',0,10,99,12,5,'1',NULL);
/*!40000 ALTER TABLE `b_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_credit_record`
--

DROP TABLE IF EXISTS `b_credit_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `b_credit_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `change_score` int NOT NULL COMMENT '变动分值(正加负减)',
  `score_after` int DEFAULT NULL COMMENT '变动后信用分',
  `reason` varchar(100) DEFAULT NULL COMMENT '变动原因',
  `order_id` bigint DEFAULT NULL COMMENT '关联订单ID',
  `create_time` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_cr_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='信用分变更记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_credit_record`
--

LOCK TABLES `b_credit_record` WRITE;
/*!40000 ALTER TABLE `b_credit_record` DISABLE KEYS */;
INSERT INTO `b_credit_record` VALUES (1,12,2,102,'获得好评，信用分提升',NULL,'1789203118982');
/*!40000 ALTER TABLE `b_credit_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_error_log`
--

DROP TABLE IF EXISTS `b_error_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `b_error_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ip` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `url` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `method` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `content` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci,
  `log_time` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_error_log`
--

LOCK TABLES `b_error_log` WRITE;
/*!40000 ALTER TABLE `b_error_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `b_error_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_login_log`
--

DROP TABLE IF EXISTS `b_login_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `b_login_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `ip` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `ua` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `log_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_login_log`
--

LOCK TABLES `b_login_log` WRITE;
/*!40000 ALTER TABLE `b_login_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `b_login_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_message`
--

DROP TABLE IF EXISTS `b_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `b_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint DEFAULT NULL COMMENT '关联订单ID',
  `from_user_id` bigint NOT NULL COMMENT '发送人ID',
  `to_user_id` bigint NOT NULL COMMENT '接收人ID',
  `content` varchar(1000) DEFAULT NULL COMMENT '消息内容',
  `type` varchar(2) NOT NULL DEFAULT '1' COMMENT '1文本 2图片 3系统通知',
  `is_read` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已读',
  `create_time` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_msg_order` (`order_id`),
  KEY `idx_msg_to` (`to_user_id`),
  KEY `idx_msg_from` (`from_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单即时消息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_message`
--

LOCK TABLES `b_message` WRITE;
/*!40000 ALTER TABLE `b_message` DISABLE KEYS */;
INSERT INTO `b_message` VALUES (1,77,10,12,'麻烦帮我多拿一双筷子，谢谢！','1',1,'1789115275000'),(2,77,12,10,'好的，已经拿到，马上送到宿舍楼下。','1',1,'1789116275000');
/*!40000 ALTER TABLE `b_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_notice`
--

DROP TABLE IF EXISTS `b_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `b_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `content` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `create_time` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_notice`
--

LOCK TABLES `b_notice` WRITE;
/*!40000 ALTER TABLE `b_notice` DISABLE KEYS */;
INSERT INTO `b_notice` VALUES (2,'新品','ddddddd','1681465796380'),(4,'test','测试消息123哈哈哈哈','1681465829203');
/*!40000 ALTER TABLE `b_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_op_log`
--

DROP TABLE IF EXISTS `b_op_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `b_op_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `re_ip` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `re_time` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `re_ua` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `re_url` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `re_method` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `re_content` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `access_time` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_op_log`
--

LOCK TABLES `b_op_log` WRITE;
/*!40000 ALTER TABLE `b_op_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `b_op_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_order`
--

DROP TABLE IF EXISTS `b_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `b_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` bigint DEFAULT NULL COMMENT '任务ID',
  `status` varchar(2) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `order_time` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `pay_time` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `publisher_id` bigint DEFAULT NULL COMMENT '发布者用户ID',
  `amount` varchar(20) DEFAULT NULL COMMENT '订单金额(悬赏+小费)',
  `order_number` varchar(30) DEFAULT NULL COMMENT '订单号',
  `receiver_address` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `receiver_name` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `receiver_phone` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `remark` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `runner_id` bigint DEFAULT NULL COMMENT '骑手ID',
  `accept_time` varchar(30) DEFAULT NULL COMMENT '接单时间',
  `finish_time` varchar(30) DEFAULT NULL COMMENT '送达时间',
  `confirm_time` varchar(30) DEFAULT NULL COMMENT '用户确认时间',
  `cancel_reason` varchar(200) DEFAULT NULL COMMENT '取消原因',
  `status_remark` varchar(200) DEFAULT NULL COMMENT '状态说明/纠纷说明',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `b_order_user_id_64854046_fk_b_user_id` (`publisher_id`) USING BTREE,
  KEY `idx_order_task` (`task_id`),
  KEY `idx_order_runner` (`runner_id`),
  CONSTRAINT `b_order_ibfk_1` FOREIGN KEY (`publisher_id`) REFERENCES `b_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_order_runner` FOREIGN KEY (`runner_id`) REFERENCES `b_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_order_task` FOREIGN KEY (`task_id`) REFERENCES `b_task` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='接单订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_order`
--

LOCK TABLES `b_order` WRITE;
/*!40000 ALTER TABLE `b_order` DISABLE KEYS */;
INSERT INTO `b_order` VALUES (74,101,'1','1789193075000',NULL,10,'8','1789193075','实验楼B302','张老师','13800000001','正在配送中',12,'1789193175000',NULL,NULL,NULL,NULL),(75,102,'2','1789196675000',NULL,11,'9','1789196675','5号宿舍楼','李小红','13800000002','已取件，配送中',13,'1789196775000',NULL,NULL,NULL,NULL),(76,105,'3','1789194875000',NULL,10,'10','1789194875','校医院一楼大厅','宋吉凯','13800000001','已送达，等待用户确认',13,'1789194975000','1789197275000',NULL,NULL,NULL),(77,103,'4','1789113875000',NULL,10,'5','1789113875000','2号宿舍楼','宋吉凯','13800000001','任务已完成',12,'1789113975000','1789117475000','1789118275000',NULL,NULL),(78,99,'4','1789201114929',NULL,10,'5','1789201114929','3号宿舍楼',NULL,'13800000001',NULL,12,'1789201114929','1789203118883','1789203118926',NULL,'用户已确认完成'),(79,104,'4','1789204086865',NULL,11,'3','1789204086865','教学楼A101',NULL,'13800000002',NULL,12,'1789204086865','1789204086985','1789204087034',NULL,'用户已确认完成');
/*!40000 ALTER TABLE `b_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_report`
--

DROP TABLE IF EXISTS `b_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `b_report` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` bigint DEFAULT NULL COMMENT '关联任务ID',
  `order_id` bigint DEFAULT NULL COMMENT '关联订单ID',
  `reporter_id` bigint NOT NULL COMMENT '举报人ID',
  `target_id` bigint DEFAULT NULL COMMENT '被举报人ID',
  `type` varchar(2) DEFAULT NULL COMMENT '1虚假任务 2恶意接单 3超时未完成 4言语辱骂 5其他',
  `content` varchar(500) DEFAULT NULL COMMENT '举报内容',
  `images` varchar(500) DEFAULT NULL COMMENT '证据图片',
  `status` varchar(2) NOT NULL DEFAULT '0' COMMENT '0待处理 1已处理 2已驳回',
  `handle_remark` varchar(200) DEFAULT NULL COMMENT '处理结果',
  `create_time` varchar(30) DEFAULT NULL,
  `handle_time` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_rp_status` (`status`),
  KEY `idx_rp_reporter` (`reporter_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='投诉举报';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_report`
--

LOCK TABLES `b_report` WRITE;
/*!40000 ALTER TABLE `b_report` DISABLE KEYS */;
INSERT INTO `b_report` VALUES (1,102,NULL,11,13,'3','接单后超过约定时间仍未取件，联系不上骑手。',NULL,'0',NULL,'1789199675000',NULL);
/*!40000 ALTER TABLE `b_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_runner_verify`
--

DROP TABLE IF EXISTS `b_runner_verify`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `b_runner_verify` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '申请人ID',
  `real_name` varchar(20) DEFAULT NULL COMMENT '真实姓名',
  `student_no` varchar(20) DEFAULT NULL COMMENT '学号',
  `college` varchar(50) DEFAULT NULL COMMENT '院系',
  `mobile` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `card_image` varchar(200) DEFAULT NULL COMMENT '学生证/证件照片',
  `service_area` varchar(100) DEFAULT NULL COMMENT '服务区域',
  `service_time` varchar(100) DEFAULT NULL COMMENT '服务时段',
  `status` varchar(2) NOT NULL DEFAULT '0' COMMENT '0待审核 1已通过 2已驳回',
  `audit_remark` varchar(200) DEFAULT NULL COMMENT '审核意见',
  `create_time` varchar(30) DEFAULT NULL COMMENT '申请时间',
  `audit_time` varchar(30) DEFAULT NULL COMMENT '审核时间',
  PRIMARY KEY (`id`),
  KEY `idx_rv_user` (`user_id`),
  KEY `idx_rv_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='骑手认证申请';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_runner_verify`
--

LOCK TABLES `b_runner_verify` WRITE;
/*!40000 ALTER TABLE `b_runner_verify` DISABLE KEYS */;
INSERT INTO `b_runner_verify` VALUES (1,11,'李小红','20210002','电子信息与计算机工程系','13800000002','b3a4fd2c-cbf4-4fad-8064-c22e09e72676.jpeg','东区宿舍群、图书馆','周一至周五 18:00-22:00','0',NULL,'1789198475000',NULL),(2,10,'宋吉凯','20210001','电子信息与计算机工程系','13800000001',NULL,'东区宿舍群、图书馆','周一至周五 18:00-22:00','1','资料齐全，认证通过','1789203837275','1789203837545');
/*!40000 ALTER TABLE `b_runner_verify` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_tag`
--

DROP TABLE IF EXISTS `b_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `b_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `create_time` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='任务标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_tag`
--

LOCK TABLES `b_tag` WRITE;
/*!40000 ALTER TABLE `b_tag` DISABLE KEYS */;
INSERT INTO `b_tag` VALUES (12,'麻辣','1681347572848'),(13,'清香','1681347572848'),(14,'烤串','1681347572848'),(16,'鱼类','1681382971561');
/*!40000 ALTER TABLE `b_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_task`
--

DROP TABLE IF EXISTS `b_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `b_task` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `cover` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `description` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci,
  `reward` varchar(50) DEFAULT NULL COMMENT '悬赏金额(元)',
  `pickup_address` varchar(100) DEFAULT NULL COMMENT '取件地址',
  `status` varchar(2) NOT NULL DEFAULT '0' COMMENT '任务状态 0待接单 1已接单 2配送中 3已送达 4已完成 5已取消 6纠纷中',
  `create_time` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `pv` int DEFAULT '0',
  `recommend_count` int DEFAULT '0',
  `wish_count` int DEFAULT '0',
  `collect_count` int DEFAULT '0',
  `classification_id` bigint DEFAULT NULL,
  `publisher_id` bigint DEFAULT NULL COMMENT '发布者用户ID',
  `runner_id` bigint DEFAULT NULL COMMENT '接单骑手ID(NULL=未接单)',
  `delivery_address` varchar(100) DEFAULT NULL COMMENT '送达地址',
  `expect_time` varchar(30) DEFAULT NULL COMMENT '期望完成时间',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `images` varchar(500) DEFAULT NULL COMMENT '物品图片(多张,逗号分隔)',
  `weight` varchar(20) DEFAULT NULL COMMENT '物品规格/重量',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注说明',
  `finish_time` varchar(30) DEFAULT NULL COMMENT '完成时间',
  `order_count` int NOT NULL DEFAULT '0' COMMENT '接单人数',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_task_status` (`status`),
  KEY `idx_task_classification` (`classification_id`),
  KEY `idx_task_publisher` (`publisher_id`),
  KEY `idx_task_runner` (`runner_id`),
  CONSTRAINT `fk_task_classification` FOREIGN KEY (`classification_id`) REFERENCES `b_classification` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_task_publisher` FOREIGN KEY (`publisher_id`) REFERENCES `b_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_task_runner` FOREIGN KEY (`runner_id`) REFERENCES `b_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='跑腿任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_task`
--

LOCK TABLES `b_task` WRITE;
/*!40000 ALTER TABLE `b_task` DISABLE KEYS */;
INSERT INTO `b_task` VALUES (99,'代取快递：中通小件一件','a0b2c17a-5100-48cd-950f-0e2f889f56ff.jpeg','快递在菜鸟驿站东门店，取件码 8-3-2045，小件，帮忙送到 3 号宿舍楼下即可。','5','菜鸟驿站(东门店)','4','1789198475000',130,3,1,2,1,10,12,'3号宿舍楼','今天 18:00 前','13800000001',NULL,'小件','取件码 8-3-2045','1789203118919',1),(100,'代买奶茶：四季春玛奇朵两杯','d9ae4f45-ddcd-4bdd-a670-d1e879880f64.jpeg','一点点奶茶店，四季春玛奇朵两杯，少冰半糖，送到图书馆三楼自习区。','6','一点点奶茶店(北门)','0','1789197875000',96,5,0,1,2,11,NULL,'图书馆3楼','今天 15:30 前','13800000002',NULL,'两杯','少冰半糖',NULL,0),(101,'代送文件：行政楼到实验楼','02669227-c406-45b5-8656-8d0817aaaf75.jpeg','一份纸质材料需要从行政楼 205 送到实验楼 B302，交到张老师手上。','8','行政楼205','1','1789193075000',211,8,2,5,3,10,12,'实验楼B302','今天 12:00 前','13800000001',NULL,'A4信封','需要本人签收',NULL,1),(102,'代取快递：西门驿站大件','066794d5-285c-467c-b2dc-de0b2597b758.jpeg','一个中号纸箱，西门菜鸟驿站，取件码 6-1-1188，需要小推车帮忙拉一下。','9','菜鸟驿站(西门)','2','1789196675000',175,6,3,4,1,11,13,'5号宿舍楼','今天 20:00 前','13800000002',NULL,'中号纸箱','有点重，建议带小推车',NULL,1),(103,'代买晚饭：二食堂麻辣香锅','07dfd814-28a7-408a-ba89-368789592361.jpeg','二食堂三楼麻辣香锅，微辣，加宽粉和午餐肉，打包送到 2 号宿舍楼。','5','第二食堂三楼','4','1789113875000',320,12,6,9,2,10,12,'2号宿舍楼','今晚 19:00 前','13800000001',NULL,'一份','微辣，打包','1789117475000',1),(104,'代打印：毕业论文装订材料','09c48dc0-9e4b-4304-9798-48e8b659f5ef.jpeg','需要打印 30 页 A4 双面，并装订成册，文件已发到打印店微信。','3','学苑打印店','4','1789199075000',88,2,0,1,5,11,12,'教学楼A101','明天 10:00 前','13800000002',NULL,'30页A4','双面打印并装订','1789204087031',1),(105,'代排队：校医院挂号取号','1085cacc-bd97-451b-b9f0-a5a3bfa0cd13.jpeg','校医院上午人比较多，帮忙排队取一个内科号，取到号联系我。','10','校医院一楼大厅','3','1789194875000',156,7,4,3,4,10,13,'校医院一楼大厅','明天 08:30 前','13800000001',NULL,'排队','取到号电话联系',NULL,1),(106,'代送物品：宿舍钥匙一把','119c87c5-b59e-4936-9fbc-cd8351470dc1.jpeg','宿舍钥匙一把，从 1 号宿舍楼送到体育馆篮球场，找穿蓝色球衣的同学。','5','1号宿舍楼','5','1789189475000',64,1,0,0,3,11,NULL,'体育馆篮球场','今天 17:00 前','13800000002',NULL,'一把钥匙','任务已被取消',NULL,0),(107,'代取快递：顺丰快递一件',NULL,'顺丰快递在快递服务中心，取件码 9-2-3312，帮忙送到 4 号宿舍楼下。','6','快递服务中心','0','1789201124145',0,0,0,0,1,10,NULL,'4号宿舍楼','今天 20:00 前','13800000001',NULL,NULL,NULL,NULL,0),(108,'代买早餐：一食堂包子',NULL,'两个肉包一杯豆浆','6','第一食堂','0','1789204067689',0,0,0,0,2,10,NULL,'6号宿舍楼','明天 08:00 前','13800000001',NULL,NULL,NULL,NULL,0);
/*!40000 ALTER TABLE `b_task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_task_collect`
--

DROP TABLE IF EXISTS `b_task_collect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `b_task_collect` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` bigint NOT NULL COMMENT '任务ID',
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_collect_task_user` (`task_id`,`user_id`) USING BTREE,
  KEY `idx_collect_user` (`user_id`) USING BTREE,
  CONSTRAINT `b_task_collect_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `b_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_collect_task` FOREIGN KEY (`task_id`) REFERENCES `b_task` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='任务收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_task_collect`
--

LOCK TABLES `b_task_collect` WRITE;
/*!40000 ALTER TABLE `b_task_collect` DISABLE KEYS */;
/*!40000 ALTER TABLE `b_task_collect` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_task_tag`
--

DROP TABLE IF EXISTS `b_task_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `b_task_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` bigint NOT NULL COMMENT '任务ID',
  `tag_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_tag_task_tag` (`task_id`,`tag_id`) USING BTREE,
  KEY `idx_tag_tag` (`tag_id`) USING BTREE,
  CONSTRAINT `fk_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `b_tag` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_tag_task` FOREIGN KEY (`task_id`) REFERENCES `b_task` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=425 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='任务标签关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_task_tag`
--

LOCK TABLES `b_task_tag` WRITE;
/*!40000 ALTER TABLE `b_task_tag` DISABLE KEYS */;
INSERT INTO `b_task_tag` VALUES (423,107,12),(424,107,13);
/*!40000 ALTER TABLE `b_task_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_task_wish`
--

DROP TABLE IF EXISTS `b_task_wish`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `b_task_wish` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` bigint NOT NULL COMMENT '任务ID',
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_wish_task_user` (`task_id`,`user_id`) USING BTREE,
  KEY `idx_wish_user` (`user_id`) USING BTREE,
  CONSTRAINT `b_task_wish_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `b_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_wish_task` FOREIGN KEY (`task_id`) REFERENCES `b_task` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='任务关注表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_task_wish`
--

LOCK TABLES `b_task_wish` WRITE;
/*!40000 ALTER TABLE `b_task_wish` DISABLE KEYS */;
/*!40000 ALTER TABLE `b_task_wish` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_user`
--

DROP TABLE IF EXISTS `b_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `b_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `student_no` varchar(20) DEFAULT NULL COMMENT '学号',
  `password` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `role` varchar(2) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `runner_status` varchar(2) NOT NULL DEFAULT '0' COMMENT '骑手认证状态 0未申请 1待审核 2已认证 3已驳回',
  `status` varchar(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `nickname` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `real_name` varchar(20) DEFAULT NULL COMMENT '真实姓名',
  `avatar` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `mobile` varchar(13) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `email` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `gender` varchar(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `description` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci,
  `create_time` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `credit_score` int NOT NULL DEFAULT '100' COMMENT '信用分(初始100分)',
  `push_email` varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `push_switch` tinyint(1) DEFAULT '0',
  `token` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `finish_count` int NOT NULL DEFAULT '0' COMMENT '累计完成任务数',
  `total_income` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '累计跑腿收入',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_student_no` (`student_no`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='用户表(普通用户/骑手/管理员)';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_user`
--

LOCK TABLES `b_user` WRITE;
/*!40000 ALTER TABLE `b_user` DISABLE KEYS */;
INSERT INTO `b_user` VALUES (2,'ddd',NULL,'a4c9855f2a97a78b604d0fb258880b21','1','0','0','test',NULL,'ec794bf4-da3c-4c69-ad67-ae17ef3803b1.jpeg','13211112222','13333@111.com',NULL,'333000',NULL,100,'hh333@mail.cn',1,'a4c9855f2a97a78b604d0fb258880b21',0,0.00),(3,'www',NULL,'dcf7e9c1ca5211b175e1572e6980d4b5','1','0','0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,100,NULL,NULL,'dcf7e9c1ca5211b175e1572e6980d4b5',0,0.00),(5,'sss',NULL,'37e8f6ae0ef304cccadd6c19481b331b','1','0','0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'1683425605400',100,NULL,NULL,'37e8f6ae0ef304cccadd6c19481b331b',0,0.00),(7,'admin123',NULL,'f159053ec4a0e4a0e3c66cfd7c254853','3','0','0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'1683963040980',100,NULL,0,'b93f46224e60f5bc8390f075d2cd8f2c',0,0.00),(8,'admin',NULL,'6d854ca8c1479c069dad7d5b7ccdfd28','3','0','0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'1683963080205',100,NULL,0,'9116392dc24b7b84483ba00b0d72b80c',0,0.00),(9,'admin1',NULL,'ea73c7f54285c49f6d23ef6062d71f2f','3','0','0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'1683967886490',100,NULL,0,'ee9aa725dbbb485c1fe8b150d6b0f2c1',0,0.00),(10,'stu001','20210001','651cba8ec21a5031306da0e28b74a164','1','2','0','小明','宋吉凯',NULL,'13800000001',NULL,'1',NULL,'1789200275000',100,NULL,0,'6b287799a9e4b5b9e7ec4dfd193f984e',0,0.00),(11,'stu002','20210002','651cba8ec21a5031306da0e28b74a164','1','0','0','小红','李小红',NULL,'13800000002',NULL,'2',NULL,'1789200275000',100,NULL,0,'ab6cfd3a0671e13b9dab978950b86927',0,0.00),(12,'runner001','20210003','651cba8ec21a5031306da0e28b74a164','1','2','0','跑腿小王','王强',NULL,'13800000003',NULL,'1',NULL,'1789200275000',102,NULL,0,'6e21e7b5f13dd970bdd4698dfe003d55',3,13.00),(13,'runner002','20210004','651cba8ec21a5031306da0e28b74a164','1','2','0','跑腿小李','李明',NULL,'13800000004',NULL,'1',NULL,'1789200275000',100,NULL,0,NULL,0,0.00);
/*!40000 ALTER TABLE `b_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_wallet`
--

DROP TABLE IF EXISTS `b_wallet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `b_wallet` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '可用余额',
  `frozen` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '冻结金额(赏金托管中)',
  `total_income` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '累计收入',
  `total_expense` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '累计支出',
  `pay_password` varchar(50) DEFAULT NULL COMMENT '支付密码(MD5)',
  `create_time` varchar(30) DEFAULT NULL,
  `update_time` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_wallet_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户钱包';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_wallet`
--

LOCK TABLES `b_wallet` WRITE;
/*!40000 ALTER TABLE `b_wallet` DISABLE KEYS */;
INSERT INTO `b_wallet` VALUES (1,2,100.00,0.00,0.00,0.00,NULL,'1789200275000','1789200275000'),(2,3,100.00,0.00,0.00,0.00,NULL,'1789200275000','1789200275000'),(3,5,100.00,0.00,0.00,0.00,NULL,'1789200275000','1789200275000'),(4,7,100.00,0.00,0.00,0.00,NULL,'1789200275000','1789200275000'),(5,8,100.00,0.00,0.00,0.00,NULL,'1789200275000','1789200275000'),(6,9,100.00,0.00,0.00,0.00,NULL,'1789200275000','1789200275000'),(7,10,95.00,30.00,50.00,55.00,NULL,'1789200275000','1789204067997'),(8,11,82.00,15.00,0.00,18.00,NULL,'1789200275000','1789204087038'),(9,12,108.00,0.00,8.00,0.00,NULL,'1789200275000','1789204087046'),(10,13,100.00,0.00,0.00,0.00,NULL,'1789200275000','1789200275000');
/*!40000 ALTER TABLE `b_wallet` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_wallet_record`
--

DROP TABLE IF EXISTS `b_wallet_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `b_wallet_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `type` varchar(2) NOT NULL COMMENT '1充值 2发布任务(托管) 3任务收入 4退款 5提现 6平台奖励',
  `amount` decimal(10,2) NOT NULL COMMENT '变动金额(正数入账/负数出账)',
  `balance_after` decimal(10,2) DEFAULT NULL COMMENT '变动后余额',
  `task_id` bigint DEFAULT NULL COMMENT '关联任务ID',
  `order_id` bigint DEFAULT NULL COMMENT '关联订单ID',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `create_time` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_wr_user` (`user_id`),
  KEY `idx_wr_type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='钱包资金流水';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_wallet_record`
--

LOCK TABLES `b_wallet_record` WRITE;
/*!40000 ALTER TABLE `b_wallet_record` DISABLE KEYS */;
INSERT INTO `b_wallet_record` VALUES (1,10,'2',-5.00,95.00,103,77,'发布任务托管赏金','1789113875000'),(2,12,'3',5.00,105.00,103,77,'完成任务获得赏金','1789118275000'),(3,10,'1',50.00,145.00,NULL,NULL,'钱包充值','1789204067610'),(4,10,'2',-6.00,139.00,108,NULL,'发布任务托管赏金','1789204067705'),(5,10,'5',-20.00,119.00,NULL,NULL,'提现申请','1789204067839'),(6,12,'3',3.00,108.00,104,79,'完成任务获得赏金','1789204087047'),(7,11,'2',-6.00,NULL,100,NULL,'发布任务托管赏金(示例数据补录)','1789204104000'),(8,10,'2',-6.00,NULL,107,NULL,'发布任务托管赏金(示例数据补录)','1789204104000'),(9,10,'2',-8.00,NULL,101,NULL,'发布任务托管赏金(示例数据补录)','1789204104000'),(10,11,'2',-9.00,NULL,102,NULL,'发布任务托管赏金(示例数据补录)','1789204104000'),(11,10,'2',-10.00,NULL,105,NULL,'发布任务托管赏金(示例数据补录)','1789204104000');
/*!40000 ALTER TABLE `b_wallet_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_withdraw`
--

DROP TABLE IF EXISTS `b_withdraw`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `b_withdraw` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '申请人ID',
  `amount` decimal(10,2) NOT NULL COMMENT '提现金额',
  `account_type` varchar(20) DEFAULT NULL COMMENT '支付宝/微信/银行卡',
  `account` varchar(100) DEFAULT NULL COMMENT '收款账号',
  `real_name` varchar(20) DEFAULT NULL COMMENT '收款人姓名',
  `status` varchar(2) NOT NULL DEFAULT '0' COMMENT '0待处理 1已打款 2已驳回',
  `apply_time` varchar(30) DEFAULT NULL COMMENT '申请时间',
  `handle_time` varchar(30) DEFAULT NULL COMMENT '处理时间',
  `handle_remark` varchar(200) DEFAULT NULL COMMENT '处理意见',
  PRIMARY KEY (`id`),
  KEY `idx_wd_user` (`user_id`),
  KEY `idx_wd_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='提现申请';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_withdraw`
--

LOCK TABLES `b_withdraw` WRITE;
/*!40000 ALTER TABLE `b_withdraw` DISABLE KEYS */;
INSERT INTO `b_withdraw` VALUES (1,10,20.00,'支付宝','13800000001','宋吉凯','1','1789204067833','1789204067979','已打款');
/*!40000 ALTER TABLE `b_withdraw` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-12 17:34:53
