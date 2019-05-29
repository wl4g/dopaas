/*
 Navicat Premium Data Transfer

 Source Server         : 115.29.212.28
 Source Server Type    : MySQL
 Source Server Version : 50633
 Source Host           : 115.29.212.28:30601
 Source Schema         : devops

 Target Server Type    : MySQL
 Target Server Version : 50633
 File Encoding         : 65001

 Date: 29/05/2019 11:11:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for cf_app_environment
-- ----------------------------
DROP TABLE IF EXISTS `cf_app_environment`;
CREATE TABLE `cf_app_environment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL COMMENT '所属应用分组ID',
  `name` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '环境名称（dev/test/pre/prod）',
  `remark` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  `create_by` int(11) NOT NULL,
  `create_date` datetime NOT NULL,
  `update_by` int(11) NOT NULL,
  `update_date` datetime NOT NULL,
  `del_flag` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `group_id` (`group_id`),
  KEY `create_by` (`create_by`),
  KEY `update_by` (`update_by`),
  CONSTRAINT `cf_app_environment_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `cf_app_group` (`id`),
  CONSTRAINT `cf_app_environment_ibfk_2` FOREIGN KEY (`create_by`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `cf_app_environment_ibfk_3` FOREIGN KEY (`update_by`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='APP配置命名空间（eg: dev/test/pre/prod）';

-- ----------------------------
-- Records of cf_app_environment
-- ----------------------------
BEGIN;
INSERT INTO `cf_app_environment` VALUES (1, 1, 'dev', 'sso服务开发调试环境', 1, '2018-09-20 11:18:49', 1, '2018-11-05 11:42:48', 0);
INSERT INTO `cf_app_environment` VALUES (2, 1, 'test', 'sso服务特性验证环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:08:24', 0);
INSERT INTO `cf_app_environment` VALUES (3, 1, 'pre', 'sso服务灰度验证环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:08:31', 0);
INSERT INTO `cf_app_environment` VALUES (4, 1, 'prod', 'sso服务生产正式环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (5, 6, 'dev', 'dataopen开发调试环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (6, 6, 'test', 'dataopen特性验证环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (7, 6, 'prod', 'dataopen生产正式环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (15, 2, 'dev', 'portal开发调试环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (16, 2, 'test', 'portal特性验证环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (17, 2, 'prod', 'portal生产正式环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (18, 3, 'dev', 'mp开发调试环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (19, 3, 'test', 'mp特性验证环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (20, 3, 'prod', 'mp生产正式环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (21, 4, 'dev', 'ems开发调试环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (22, 4, 'test', 'ems特性验证环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (23, 4, 'prod', 'ems生产正式环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (24, 5, 'dev', 'sink开发调试环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (25, 5, 'test', 'sink特性验证环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (26, 5, 'prod', 'sink生产正式环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (27, 7, 'dev', 'mqttCollect开发调试环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (28, 7, 'test', 'mqttCollect特性验证环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (29, 7, 'prod', 'mqttCollect生产正式环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (30, 8, 'dev', 'rpcCollect开发调试环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (31, 8, 'test', 'rpcCollect特性验证环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (32, 8, 'prod', 'rpcCollect生产正式环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (33, 9, 'dev', 'base开发调试环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (34, 9, 'test', 'base特性验证环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (35, 9, 'prod', 'base生产正式环境', 1, '2018-09-20 11:18:49', 1, '2018-09-19 09:10:47', 0);
INSERT INTO `cf_app_environment` VALUES (36, 41, 'test', 'test', 1, '2018-11-06 13:57:37', 1, '2018-11-06 13:57:37', 0);
INSERT INTO `cf_app_environment` VALUES (37, 42, 'ajy', '安监云测试环境', 1, '2018-11-09 15:46:09', 1, '2018-11-09 15:46:09', 0);
INSERT INTO `cf_app_environment` VALUES (38, 43, 'test', 'test', 1, '2018-11-16 15:56:58', 1, '2018-11-16 15:56:58', 0);
INSERT INTO `cf_app_environment` VALUES (39, 62, 'test', 'test', 1, '2019-05-21 16:31:20', 1, '2019-05-21 16:31:20', 0);
COMMIT;

-- ----------------------------
-- Table structure for cf_app_group
-- ----------------------------
DROP TABLE IF EXISTS `cf_app_group`;
CREATE TABLE `cf_app_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dept_id` int(11) NOT NULL COMMENT '应用所属部门ID',
  `name` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '应用名称',
  `enable` int(1) NOT NULL DEFAULT '1' COMMENT '启用状态（0:禁止/1:启用）',
  `remark` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  `create_by` int(11) NOT NULL,
  `create_date` datetime NOT NULL,
  `update_by` int(11) NOT NULL,
  `update_date` datetime NOT NULL,
  `del_flag` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `dept_id` (`dept_id`),
  KEY `update_by` (`update_by`),
  KEY `create_by` (`create_by`),
  CONSTRAINT `cf_app_group_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_department` (`id`),
  CONSTRAINT `cf_app_group_ibfk_2` FOREIGN KEY (`update_by`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `cf_app_group_ibfk_3` FOREIGN KEY (`create_by`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='APP分组表';

-- ----------------------------
-- Records of cf_app_group
-- ----------------------------
BEGIN;
INSERT INTO `cf_app_group` VALUES (1, 1, 'sso', 1, '统一认证中心接口服务', 1, '2018-09-20 11:20:25', 1, '2018-11-09 14:23:03', 1);
INSERT INTO `cf_app_group` VALUES (2, 1, 'portal', 1, '门户系统接口服务', 1, '2018-09-20 11:20:25', 1, '2018-11-09 14:24:37', 1);
INSERT INTO `cf_app_group` VALUES (3, 1, 'mp', 1, '管理平台接口服务', 1, '2018-09-20 11:20:25', 1, '2018-11-09 15:43:41', 1);
INSERT INTO `cf_app_group` VALUES (4, 1, 'ems', 1, '能耗分析平台接口服务', 1, '2018-09-20 11:20:25', 1, '2018-11-15 17:07:18', 1);
INSERT INTO `cf_app_group` VALUES (5, 1, 'sink', 1, '实时告警服务', 1, '2018-09-20 11:20:25', 1, '2018-09-19 09:10:49', 0);
INSERT INTO `cf_app_group` VALUES (6, 1, 'dataopen', 1, '大数据RESTful接口服务', 1, '2018-09-20 11:20:25', 1, '2018-09-19 09:11:27', 0);
INSERT INTO `cf_app_group` VALUES (7, 1, 'mqttCollect', 1, '设备连接平台服务(MQTT)', 1, '2018-09-20 11:20:25', 1, '2018-09-19 09:12:14', 0);
INSERT INTO `cf_app_group` VALUES (8, 1, 'rpcCollect', 1, '设备连接平台服务(TCP)', 1, '2018-09-20 11:20:25', 1, '2018-09-19 09:12:31', 0);
INSERT INTO `cf_app_group` VALUES (9, 1, 'base', 1, '基础数据平台接口服务', 1, '2018-09-20 11:20:25', 1, '2018-09-19 09:19:12', 0);
INSERT INTO `cf_app_group` VALUES (41, 1, 'devops-example', 1, 'example', 1, '2018-11-06 13:57:37', 1, '2018-11-16 16:43:21', 1);
INSERT INTO `cf_app_group` VALUES (42, 1, 'ajy', 1, '安监云接口', 1, '2018-11-09 15:46:09', 1, '2018-11-15 17:07:28', 1);
INSERT INTO `cf_app_group` VALUES (43, 1, 'devops-scm-example', 1, 'zzh-scm', 1, '2018-11-16 15:56:58', 1, '2018-11-16 15:56:58', 0);
INSERT INTO `cf_app_group` VALUES (61, 1, 'jianzu', 1, 'hwj测试ci使用的项目', 1, '2019-05-17 17:03:56', 1, '2019-05-17 17:03:56', 0);
INSERT INTO `cf_app_group` VALUES (62, 1, 'datajob', 1, 'datajob__测试ci', 1, '2019-05-20 17:42:17', 1, '2019-05-20 17:42:17', 0);
INSERT INTO `cf_app_group` VALUES (63, 1, 'datachecker', 1, 'datachecker', 1, '2019-05-22 09:29:08', 1, '2019-05-22 09:29:08', 0);
INSERT INTO `cf_app_group` VALUES (64, 1, 'trafficmonitor', 1, 'trafficmonitor', 1, '2019-05-22 09:29:08', 1, '2019-05-22 09:29:08', 0);
COMMIT;

-- ----------------------------
-- Table structure for cf_app_instance
-- ----------------------------
DROP TABLE IF EXISTS `cf_app_instance`;
CREATE TABLE `cf_app_instance` (
  `id` int(64) NOT NULL AUTO_INCREMENT,
  `group_id` int(64) NOT NULL COMMENT '应用分组ID',
  `version_id` int(64) DEFAULT NULL COMMENT '当前应用版本ID',
  `env_id` int(64) NOT NULL COMMENT '所属环境ID',
  `enable` int(1) NOT NULL DEFAULT '1' COMMENT '启用状态（0:禁止/1:启用）',
  `host` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '实例节点Host（如：web-node1）',
  `ip` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '主机IP地址',
  `port` int(5) NOT NULL COMMENT '服务监听端口',
  `ops_ids` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '运维者userIds（逗号分隔）',
  `remark` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  `create_by` int(11) NOT NULL,
  `create_date` datetime NOT NULL,
  `update_by` int(11) NOT NULL,
  `update_date` datetime NOT NULL,
  `del_flag` int(1) NOT NULL DEFAULT '0',
  `server_account` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '服务器登录登录账号',
  `ssh_rsa` text COLLATE utf8_bin,
  PRIMARY KEY (`id`),
  KEY `group_id` (`group_id`),
  KEY `version_id` (`version_id`),
  KEY `env_id` (`env_id`),
  KEY `update_by` (`update_by`),
  KEY `create_by` (`create_by`),
  CONSTRAINT `cf_app_instance_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `cf_app_group` (`id`),
  CONSTRAINT `cf_app_instance_ibfk_2` FOREIGN KEY (`version_id`) REFERENCES `cf_version` (`id`),
  CONSTRAINT `cf_app_instance_ibfk_3` FOREIGN KEY (`env_id`) REFERENCES `cf_app_environment` (`id`),
  CONSTRAINT `cf_app_instance_ibfk_4` FOREIGN KEY (`update_by`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `cf_app_instance_ibfk_5` FOREIGN KEY (`create_by`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='应用分组实例表';

-- ----------------------------
-- Records of cf_app_instance
-- ----------------------------
BEGIN;
INSERT INTO `cf_app_instance` VALUES (1, 1, NULL, 2, 1, 'web-node1', '192.168.212.10', 28080, '1,2,3', '认证中心接口服务实例1', 1, '2018-09-20 11:20:13', 1, '2018-11-02 14:32:10', 0, NULL, NULL);
INSERT INTO `cf_app_instance` VALUES (2, 2, NULL, 16, 1, 'web-node1', '192.168.212.10', 28082, '1,2', '门户系统接口服务实例1', 1, '2018-09-20 11:20:13', 1, '2018-11-02 14:17:17', 0, NULL, NULL);
INSERT INTO `cf_app_instance` VALUES (3, 3, NULL, 19, 1, 'web-node1', '192.168.212.10', 28083, '1,2,3', '管理平台接口服务实例1', 1, '2018-09-20 11:20:13', 1, '2018-09-19 09:16:10', 0, NULL, NULL);
INSERT INTO `cf_app_instance` VALUES (4, 4, NULL, 22, 1, 'web-node1', '192.168.212.10', 28084, '1', '能耗分析平台接口服务实例1', 1, '2018-09-20 11:20:13', 1, '2018-11-09 15:44:37', 0, NULL, NULL);
INSERT INTO `cf_app_instance` VALUES (5, 5, NULL, 25, 1, 'web-node1', '192.168.212.10', 29088, '1,2,3', '实时告警服务实例1', 1, '2018-09-20 11:20:13', 1, '2018-09-19 09:16:10', 0, NULL, NULL);
INSERT INTO `cf_app_instance` VALUES (6, 5, NULL, 25, 1, 'sink-node1', '192.168.212.10', 29088, '1,2,3', '实时告警服务实例2', 1, '2018-09-20 11:20:13', 1, '2018-09-19 09:16:10', 0, NULL, NULL);
INSERT INTO `cf_app_instance` VALUES (7, 6, NULL, 6, 1, 'mq-node1', '192.168.212.10', 58081, '1,2,3', '大数据RESTful接口服务实例1', 1, '2018-09-20 11:20:13', 1, '2018-09-19 09:16:10', 0, NULL, NULL);
INSERT INTO `cf_app_instance` VALUES (8, 6, NULL, 6, 1, 'mq-node2', '192.168.212.10', 58081, '1,2,3', '大数据RESTful接口服务实例2', 1, '2018-09-20 11:20:13', 1, '2018-09-19 09:16:10', 0, NULL, NULL);
INSERT INTO `cf_app_instance` VALUES (9, 6, NULL, 6, 1, 'mq-node3', '192.168.212.10', 58081, '1,2,3', '大数据RESTful接口服务实例3', 1, '2018-09-20 11:20:13', 1, '2018-09-19 09:16:10', 0, NULL, NULL);
INSERT INTO `cf_app_instance` VALUES (10, 7, NULL, 28, 1, 'collect-node1', '192.168.212.10', 29088, '1,2,3', '设备连接平台服务(MQTT)实例1', 1, '2018-09-20 11:20:13', 1, '2018-09-19 09:16:10', 0, NULL, NULL);
INSERT INTO `cf_app_instance` VALUES (11, 7, NULL, 28, 1, 'collect-node2', '192.168.212.10', 29088, '1,2,3', '设备连接平台服务(MQTT)实例2', 1, '2018-09-20 11:20:13', 1, '2018-09-19 09:16:10', 0, NULL, NULL);
INSERT INTO `cf_app_instance` VALUES (12, 8, NULL, 31, 1, 'collect-node1', '192.168.212.10', 39088, '1,2,3', '设备连接平台服务(TCP)实例1', 1, '2018-09-20 11:20:13', 1, '2018-09-19 09:16:10', 0, NULL, NULL);
INSERT INTO `cf_app_instance` VALUES (13, 8, NULL, 31, 1, 'collect-node2', '192.168.212.10', 39088, '1,2,3', '设备连接平台服务(TCP)实例2', 1, '2018-09-20 11:20:13', 1, '2018-09-19 09:16:10', 0, NULL, NULL);
INSERT INTO `cf_app_instance` VALUES (14, 9, NULL, 34, 1, 'web-node1', '192.168.212.10', 28081, '1,2,3', '基础数据平台接口服务实例1', 1, '2018-09-20 11:20:13', 1, '2018-09-19 09:16:10', 0, NULL, NULL);
INSERT INTO `cf_app_instance` VALUES (52, 41, 231, 36, 1, 'USER-20160508FZ', '127.0.0.1', 8080, '1', '', 1, '2018-11-06 15:46:44', 1, '2018-11-16 13:47:11', 0, NULL, NULL);
INSERT INTO `cf_app_instance` VALUES (53, 41, 231, 36, 1, 'T1CTWOT9XY7055F', '127.0.0.1', 8080, '1', 'test', 1, '2018-11-07 16:32:45', 1, '2018-11-16 13:47:11', 0, NULL, NULL);
INSERT INTO `cf_app_instance` VALUES (54, 41, 231, 36, 1, 'wangl-sir', '127.0.0.1', 8080, '1', 'test', 1, '2018-11-08 21:32:51', 1, '2018-11-16 13:47:11', 0, NULL, NULL);
INSERT INTO `cf_app_instance` VALUES (55, 4, NULL, 22, 1, 'web-node2', '192.168.1.10', 28083, '1', '能耗分析平台接口', 1, '2018-11-09 15:44:37', 1, '2018-11-09 15:44:37', 0, NULL, NULL);
INSERT INTO `cf_app_instance` VALUES (56, 42, NULL, 37, 1, 'ajy', '10.0.12.38', 28080, '1', '安监云测试', 1, '2018-11-09 15:47:44', 1, '2018-11-09 15:47:44', 0, NULL, NULL);
INSERT INTO `cf_app_instance` VALUES (57, 41, NULL, 36, 1, 'UNQCWCNAVNHYLKU', '127.0.0.1', 18082, '1', 'zzh', 1, '2018-11-16 14:23:21', 1, '2018-11-16 14:23:21', 0, NULL, NULL);
INSERT INTO `cf_app_instance` VALUES (58, 43, 309, 38, 1, 'UNQCWCNAVNHYLKU', '127.0.0.1', 18082, '1', 'zzh', 1, '2018-11-16 15:57:44', 1, '2018-11-26 11:11:09', 0, NULL, NULL);
INSERT INTO `cf_app_instance` VALUES (59, 43, 310, 38, 1, 'USER-20160508FZ', '127.0.0.1', 18082, '1', 'test', 1, '2018-11-16 16:43:58', 1, '2018-11-26 11:13:25', 0, NULL, NULL);
INSERT INTO `cf_app_instance` VALUES (60, 5, 285, 25, 1, 'USER-20160508FZ', '127.0.0.1', 29088, '1', 'sink test', 1, '2018-11-22 18:47:33', 1, '2018-11-22 18:51:51', 0, NULL, NULL);
INSERT INTO `cf_app_instance` VALUES (61, 5, 306, 25, 1, 'UNQCWCNAVNHYLKU', '127.0.0.1', 29088, '1', 'sink test', 1, '2018-11-23 11:28:25', 1, '2018-11-23 11:43:53', 0, NULL, NULL);
INSERT INTO `cf_app_instance` VALUES (62, 61, NULL, 24, 1, '10.0.0.10', '10.0.0.10', 8080, '1', '本地虚拟机', 1, '2019-05-17 17:14:19', 1, '2019-05-17 17:14:19', 0, 'root', NULL);
INSERT INTO `cf_app_instance` VALUES (63, 62, NULL, 39, 1, '10.0.0.160', '10.0.0.160', 8080, '1', '办公室服务器', 1, '2019-05-20 17:45:23', 1, '2019-05-20 17:45:23', 0, 'root', NULL);
INSERT INTO `cf_app_instance` VALUES (64, 62, NULL, 39, 1, '10.0.0.161', '10.0.0.161', 8080, '1', '10.0.0.161', 1, '2019-05-22 17:19:23', 1, '2019-05-22 17:19:23', 0, 'datachecker', '60DF996522E1CC4DF951E49C095C55EC0678812D8221A5A6FBB054207AA1BA4D49D5E42AC766D7392967E778EBCF5B6A222E50FEE3BF3391D522F14BE02447A80165C6307E92E63A735A880E7C3EAD0426228C19830C124B66A16D594DD72EB948A3532A9F193F1BDCD052E8BE2E9DAA66373F1F508010D40C867875D22CEF850325DD49120C08BDA90263A9CFB234BF3615C32FCC6E9F851696C8B5C1C662130D5F36B657BA3DF8D05B0065B00875F12053BCA2EDBAB0A18AB71C982205493393F9FD98E726ED065342BFC4554F57D87314A6CB9A7F6B33A8B2D700A8BFD238F96C6DFF1032476CF19C0298E01A8F566494A1C1F7564603FD4E2DD9EB60EDDF24F28F771DA044CB15B86ED428714BC380FDA375F4E2ED4E4236F32792DE02E746F31AC4640C50D1976A82E0469F504CBFE4DB257C50C018F185D6347DE70C854100B72887DB5A82CBA16140F7842A261BF680A7FABA8BC6505032947A85C98AFF7A84BDE05682EC983FD00880392F8C85ED2C684A108DF4F312812FF8AB5CA1F47B9E1F8E1F687AB9D70BA379057A575A5E851BE6E82E4321532F3BD8BABC1EB7FB86FCD4BB4730EF0207289821F0C103FBE429D3AB75059D8CB1119887C36358FC91C5E4CF037B39360B8B96482E48FCAF0EF64AE8A85143AA637EA34DE02EBDD3C483318BA1FEFDD62FF9B6A9914716AACE94107F17EEF894315C1A3E4D703A30D96E73699A7A3B639D54BC4F7A3C4A17D2EE895C4212EF68D5D1BA8946E86D524F5016338369A8DE72DEE76FE60E108AC41B3DFE09218CBC5E66265AB0B2B44D32BD6530EEC335A636AF453B08E74780FB453823A0834F78EB4D13E6BFBC8B680D31B7903027EEF11A1C185F21F470EFE28AFA09C0853BBDCA4DE41D7B5B833F79F0053C0DA239C31796673C39F9D760D73F62520C5F6586D58D1571194B6CA05FA50D6750FA6EA365D06ABBCE4FD20703AF114357A8B3FF9431D7C5615D32A1A7EA3D02A3AD2DB73C9A1AE5D0A9D148FEF3D4854B8C2E7352E504EF264EA78ED3F70652234D3EE355CF8910038DE2B52CA08EB6A37F2561FD789A0FCA78377D0C8ECD685EAB046A5B06EDBFD1B96222E841A74BB214DB3616BBBDEF192B7100711520D53AFB07ACE8609899D0B875A26FEB7F65646CCD778B5D1F2F2C8E9487D8A56DF000AC3751002FD38273DE85853877D19FE3A89F5F21D80B3B2C4E1FEDBB486D594B478CBEC25C0A8B029C8FA018D5B40000224EE860BC4B2F3B6E30A244DFC59047ECCD8A90A0888C0DFF27FA707E82D74985AE528A8F1CBBD88E78A37F145BC9B1BBFE23957693DD4F276C06ACA76023071904240187A333F770975BC199A19C595685DC7726DDD9B3AF719439085DFA8AE6B250B23413E349B744FAC3C1C8142F76256457439FA893AEA30FE78BB99F35352F7198B668013F401B4C5CB31EBA199085334B17ED6C02534D97C707CCEC61303D3E02AC0CDD8ECA9F06190994F56F68BDBB64F28F03391F44DB04BCC4B8F4CD2DA25993640A5F8A4A727E71406163683ED858C254BFA10E2B60985ADA959507171BD87D614954C7303BC7C113A6179A9D9FCDD312547E0F1D01252519B1723BD4261C95559BDB8E61608FEB3279B91029B49BE5ABF3DED3421A4A8DB30F10827A328D22786A9E80163BD28C220F57F1744ED60DD3BE7601957EE1AEF4EC93A832B3CE6AA3F49338BDA25F88B67E07D22227785DEE2EC35F43A333CBBA3B20D4590B075D5A43EC5B5191A625FDEBA60D836B4E3510003F15A06D4CFD81F2EC973D323BC17F42A1E63F6C609FED30D4A9B86C498D6C1A1D4DFCC16A09E09BC13549A4E5C0D81B7DC74C9A5404CFCF866EBA1B13A58AEF28798AFB43D4D23362899FD4022F1BA746DECE38AB4B48C4C8619E8732B6282C0D96D81286AAD327937E64DC62825460113DE6E18158E40B97BE9AA23369632E0E26013F6CEB0B9C95FB10E3A60E39EBCB35BB14984EE4E3FEA988ACC334B0D87942EDA5965A932E1AEE45689A5C5322218E033F784E197AB65EA845E12A7AA6C99DD1794466C0623F4E4D18591DA2D1E8242AE776AABFA967685C30EFC864BD92AEF7CABCB8ED768A9B2444F6EBF66873720AF14B32EF9A1F8073F746C0AD088C0B55B4EDBD959B5E6DC59EA2A5E44EDC8EECAB06205A9851545A23988235E17594619686E969002AC41C88ED3612804A7803D2DA080CB36D80187ACCB9832DEB245F6178E7C49A33E34C1416FED1BAF9EDB70579B9C267A614B47A9919723D376B1EBF7C784A8433CF001076FA1D5D9170649A18C0655D30F9FCAC77F985F16238E8FB8298263E6F98A9D90EBB19F46B90799B01853A09C3625EE7F888AF9C8FE2');
COMMIT;

-- ----------------------------
-- Table structure for cf_release_detail
-- ----------------------------
DROP TABLE IF EXISTS `cf_release_detail`;
CREATE TABLE `cf_release_detail` (
  `id` int(64) NOT NULL AUTO_INCREMENT,
  `release_id` int(11) NOT NULL COMMENT '发布ID',
  `instance_id` int(11) NOT NULL COMMENT '应用实例ID',
  `status` int(1) DEFAULT NULL COMMENT '发布结果状态（1:成功/0:未更改/-1:更新失败）',
  `description` text COLLATE utf8_bin COMMENT '发布结果说明',
  `result` text COLLATE utf8_bin COMMENT '配置发布结果内容，JSON格式',
  PRIMARY KEY (`id`),
  KEY `of_id` (`instance_id`),
  KEY `release_id` (`release_id`),
  CONSTRAINT `cf_release_detail_ibfk_3` FOREIGN KEY (`instance_id`) REFERENCES `cf_app_instance` (`id`),
  CONSTRAINT `cf_release_detail_ibfk_4` FOREIGN KEY (`release_id`) REFERENCES `cf_release_history` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=277 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='ACM配置发布历史明细表';

-- ----------------------------
-- Records of cf_release_detail
-- ----------------------------
BEGIN;
INSERT INTO `cf_release_detail` VALUES (125, 141, 54, NULL, NULL, 'null');
INSERT INTO `cf_release_detail` VALUES (126, 142, 54, NULL, NULL, 'null');
INSERT INTO `cf_release_detail` VALUES (127, 143, 54, NULL, NULL, 'null');
INSERT INTO `cf_release_detail` VALUES (128, 144, 54, 0, 'NoChangedConfigurationException: Current environment source version used is: 178, target version: 178', '[]');
INSERT INTO `cf_release_detail` VALUES (129, 145, 54, 0, 'NoChangedConfigurationException: Current environment source version used is: 179, target version: 179', '[]');
INSERT INTO `cf_release_detail` VALUES (130, 146, 54, 0, 'NoChangedConfigurationException: Current environment source version used is: 180, target version: 180', '[]');
INSERT INTO `cf_release_detail` VALUES (131, 147, 54, NULL, NULL, 'null');
INSERT INTO `cf_release_detail` VALUES (132, 148, 54, 0, 'NoChangedConfigurationException: Current environment source version used is: 182, target version: 182', '[]');
INSERT INTO `cf_release_detail` VALUES (133, 149, 54, -1, 'NullPointerException: ', '[]');
INSERT INTO `cf_release_detail` VALUES (134, 150, 54, -1, 'NullPointerException: ', '[]');
INSERT INTO `cf_release_detail` VALUES (135, 151, 54, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"FFFF-178\",\"newValue\":\"FFFF-172\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (136, 152, 54, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"FFFF-172\",\"newValue\":\"ASDF-794\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (137, 153, 54, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"ASDF-794\",\"newValue\":\"AAAAFFF-424\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (138, 154, 54, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"AAAAFFF-373\",\"newValue\":\"啊啊啊ASDF-953\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (139, 155, 54, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"啊啊啊ASDF-985\",\"newValue\":\"啊啊啊ASDF-581\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (140, 156, 54, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"啊啊啊ASDF-581\",\"newValue\":\"啊啊-894\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (141, 157, 54, NULL, NULL, 'null');
INSERT INTO `cf_release_detail` VALUES (142, 158, 54, NULL, NULL, 'null');
INSERT INTO `cf_release_detail` VALUES (143, 159, 54, NULL, NULL, 'null');
INSERT INTO `cf_release_detail` VALUES (144, 160, 54, NULL, NULL, 'null');
INSERT INTO `cf_release_detail` VALUES (145, 161, 54, NULL, NULL, 'null');
INSERT INTO `cf_release_detail` VALUES (146, 162, 54, NULL, NULL, 'null');
INSERT INTO `cf_release_detail` VALUES (147, 163, 54, NULL, NULL, 'null');
INSERT INTO `cf_release_detail` VALUES (148, 164, 54, NULL, NULL, 'null');
INSERT INTO `cf_release_detail` VALUES (149, 165, 54, NULL, NULL, 'null');
INSERT INTO `cf_release_detail` VALUES (150, 166, 54, NULL, NULL, 'null');
INSERT INTO `cf_release_detail` VALUES (151, 167, 52, -1, 'IllegalArgumentException: Spring cloud \'bootstrapProperties\' property source must not be null.', '[]');
INSERT INTO `cf_release_detail` VALUES (152, 168, 52, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"AAAF-68\",\"newValue\":\"AAAF-513\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (153, 169, 52, NULL, NULL, 'null');
INSERT INTO `cf_release_detail` VALUES (154, 170, 52, NULL, NULL, 'null');
INSERT INTO `cf_release_detail` VALUES (155, 171, 52, NULL, NULL, 'null');
INSERT INTO `cf_release_detail` VALUES (156, 172, 52, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"FFFF-249\",\"newValue\":\"FFFF-331\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (157, 173, 52, NULL, NULL, 'null');
INSERT INTO `cf_release_detail` VALUES (158, 174, 52, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"FFFF-331\",\"newValue\":\"AAA-338\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (159, 175, 52, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"AAA-178\",\"newValue\":\"AAA-700\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (160, 176, 52, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"AAA-700\",\"newValue\":\"AAAF-917\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (161, 177, 52, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"AAAF-917\",\"newValue\":\"ASDFASGDAG-197\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (162, 178, 52, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"啊啊啊啊ASDASG-279\",\"newValue\":\"啊啊啊啊ASDASG-5\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (163, 179, 52, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"啊啊啊啊ASDASG-5\",\"newValue\":\"ASDGASGDA-419\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (164, 180, 52, -1, 'IllegalArgumentException: Spring cloud \'bootstrapProperties\' property source must not be null.', '[]');
INSERT INTO `cf_release_detail` VALUES (165, 181, 52, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"FFF-181\",\"newValue\":\"FFF-87\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (166, 182, 52, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"AAAA-149\",\"newValue\":\"AAAA-555\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (167, 183, 52, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"TTT-257\",\"newValue\":\"TTT-977\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (168, 184, 52, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"TTT-977\",\"newValue\":\"ASGASDGAG-36\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (169, 185, 52, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"ASGASDGAG-36\",\"newValue\":\"SAGASG-738\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (170, 186, 52, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"SAGASG-738\",\"newValue\":\"AGA-950\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (171, 187, 52, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"AGA-950\",\"newValue\":\"YYY-172\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (172, 188, 52, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"YYY-172\",\"newValue\":\"RERWERWE-109\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (173, 189, 52, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"WETAGD-420\",\"newValue\":\"WETAGD-374\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (174, 190, 52, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"WETAGD-374\",\"newValue\":\"TTTT-823\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (175, 191, 52, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"TTTT-823\",\"newValue\":\"1231AF-194\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (176, 193, 52, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"1231AF-194\",\"newValue\":\"ASHDGA-913\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (177, 194, 52, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"AAAA11-825\",\"newValue\":\"AAAA11-220\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (178, 195, 52, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"AAAA11-220\",\"newValue\":\"AAA啊-840\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (179, 198, 52, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (182, 201, 52, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (183, 202, 54, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (184, 203, 52, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (185, 203, 53, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (186, 203, 54, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (187, 204, 52, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (188, 204, 53, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (189, 204, 54, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (193, 208, 58, -1, 'IllegalArgumentException: Spring cloud \'bootstrapProperties\' property source must not be null.', '[]');
INSERT INTO `cf_release_detail` VALUES (198, 210, 59, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"AAA-265\",\"newValue\":\"AAA-88\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (199, 211, 59, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"AAA-88\",\"newValue\":\"BBBB-400\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (200, 212, 59, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"BBBB-954\",\"newValue\":\"CCC-845\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (201, 213, 59, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"CCC-845\",\"newValue\":\"GGGG-77\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (202, 214, 58, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"ZZHAAA-50\",\"newValue\":\"ZZHAAA-509\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (203, 215, 58, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"ZZHAAA-509\",\"newValue\":\"CCCC-189\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (204, 216, 58, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"CCCC-189\",\"newValue\":\"SSSSS-700\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (205, 217, 58, -1, 'IllegalArgumentException: Spring cloud \'bootstrapProperties\' property source must not be null.', '[]');
INSERT INTO `cf_release_detail` VALUES (206, 218, 58, -1, 'IllegalArgumentException: Spring cloud \'bootstrapProperties\' property source must not be null.', '[]');
INSERT INTO `cf_release_detail` VALUES (207, 219, 58, -1, 'IllegalArgumentException: Spring cloud \'bootstrapProperties\' property source must not be null.', '[]');
INSERT INTO `cf_release_detail` VALUES (208, 220, 58, -1, 'IllegalArgumentException: Spring cloud \'bootstrapProperties\' property source must not be null.', '[]');
INSERT INTO `cf_release_detail` VALUES (209, 221, 58, -1, 'IllegalArgumentException: Spring cloud \'bootstrapProperties\' property source must not be null.', '[]');
INSERT INTO `cf_release_detail` VALUES (210, 222, 58, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"QWE-776\",\"newValue\":\"QWE-882\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (211, 223, 58, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"QWE-882\",\"newValue\":\"SDF-111\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (212, 224, 58, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"GGGG-621\",\"newValue\":\"GGGG-576\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (213, 225, 58, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"GGGG-576\",\"newValue\":\"ZZZZXX-399\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (214, 226, 58, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"ZZZZXX-399\",\"newValue\":\"SSS-592\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (215, 227, 58, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"ZZH-511\",\"newValue\":\"ZZH-445\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (216, 228, 58, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"ZZH-445\",\"newValue\":\"JJLKL-556\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (217, 229, 58, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"ZZH-912\",\"newValue\":\"ZZH-464\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (218, 230, 58, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"ZZH-464\",\"newValue\":\"ZZHIII-110\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (219, 231, 58, -1, 'IllegalArgumentException: Spring cloud \'bootstrapProperties\' property source must not be null.', '[]');
INSERT INTO `cf_release_detail` VALUES (220, 232, 58, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"TTYRT-188\",\"newValue\":\"TTYRT-417\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (221, 233, 58, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"OOOOOO-969\",\"newValue\":\"OOOOOO-860\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (222, 234, 58, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"TTTT-158\",\"newValue\":\"TTTT-477\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (223, 235, 58, -1, 'IllegalArgumentException: Spring cloud \'bootstrapProperties\' property source must not be null.', '[]');
INSERT INTO `cf_release_detail` VALUES (224, 236, 59, -1, 'IllegalArgumentException: Spring cloud \'bootstrapProperties\' property source must not be null.', '[]');
INSERT INTO `cf_release_detail` VALUES (225, 237, 59, -1, 'IllegalArgumentException: Spring cloud \'bootstrapProperties\' property source must not be null.', '[]');
INSERT INTO `cf_release_detail` VALUES (226, 238, 59, -1, 'IllegalArgumentException: Spring cloud \'bootstrapProperties\' property source must not be null.', '[]');
INSERT INTO `cf_release_detail` VALUES (227, 239, 59, -1, 'IllegalArgumentException: Spring cloud \'bootstrapProperties\' property source must not be null.', '[]');
INSERT INTO `cf_release_detail` VALUES (228, 240, 59, -1, 'IllegalArgumentException: Spring cloud \'bootstrapProperties\' property source must not be null.', '[]');
INSERT INTO `cf_release_detail` VALUES (229, 241, 59, -1, 'IllegalArgumentException: Spring cloud \'bootstrapProperties\' property source must not be null.', '[]');
INSERT INTO `cf_release_detail` VALUES (230, 242, 59, -1, 'IllegalArgumentException: Spring cloud \'bootstrapProperties\' property source must not be null.', '[]');
INSERT INTO `cf_release_detail` VALUES (231, 243, 59, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"ASGDAG-171\",\"newValue\":\"ASGDAG-930\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (232, 244, 59, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"ASGDAG-930\",\"newValue\":\"AAAA-286\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (233, 245, 59, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"ASDGAG-924\",\"newValue\":\"ASDGAG-928\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (234, 246, 59, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"ASDGAG-928\",\"newValue\":\"AAA-753\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (235, 247, 59, -1, 'MismatchedConfigurationException: Invalid matching configuration source, release.source: aaa.yaml, environment.sources: []', '[]');
INSERT INTO `cf_release_detail` VALUES (236, 248, 59, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"王-512\",\"newValue\":\"王-340\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (237, 249, 59, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"王-340\",\"newValue\":\"王-747\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (238, 250, 59, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"王-747\",\"newValue\":\"王-933\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (239, 251, 59, -1, 'SCMException: Failed to clear old profile configuration aaa.yaml', '[]');
INSERT INTO `cf_release_detail` VALUES (240, 252, 59, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"王-705\",\"newValue\":\"ASDF-354\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (241, 253, 59, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"王-806\",\"newValue\":\"FFFF-836\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (242, 254, 58, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"王-523\",\"newValue\":\"ZZZZZ-848\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (243, 255, 58, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"王-732\",\"newValue\":\"ZZZZZSSSS-141\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (244, 256, 58, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (245, 257, 58, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"王-559\",\"newValue\":\"ZZZZZSSSS-932\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (246, 258, 58, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (247, 259, 60, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (248, 260, 60, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (249, 261, 60, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (250, 262, 58, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (251, 263, 58, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (252, 264, 59, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"王-758\",\"newValue\":\"ADSFASD-414\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (253, 265, 58, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (254, 266, 58, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (255, 267, 59, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"ADSFASD-414\",\"newValue\":\"AAAA-487\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (256, 268, 58, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (257, 268, 59, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"GYYYHH-616\",\"newValue\":\"GYYYHH-623\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (258, 269, 58, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (259, 270, 58, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (260, 271, 58, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"11111-377\",\"newValue\":\"11111-74\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (261, 272, 58, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"11111-74\",\"newValue\":\"11111222-485\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (262, 273, 61, 1, 'ok', '[{\"beanName\":\"dataflowJobScheduler\",\"beanType\":\"com.sm.sink.service.xschedule.conf.SinkJobScheduler\",\"members\":[]}]');
INSERT INTO `cf_release_detail` VALUES (263, 274, 61, 1, 'ok', '[{\"beanName\":\"dataflowJobScheduler\",\"beanType\":\"com.sm.sink.service.xschedule.conf.SinkJobScheduler\",\"members\":[]}]');
INSERT INTO `cf_release_detail` VALUES (264, 275, 61, 1, 'ok', '[{\"beanName\":\"dataflowJobScheduler\",\"beanType\":\"com.sm.sink.service.xschedule.conf.SinkJobScheduler\",\"members\":[]}]');
INSERT INTO `cf_release_detail` VALUES (265, 276, 61, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (266, 277, 61, 1, 'ok', '[{\"beanName\":\"dataflowJobScheduler\",\"beanType\":\"com.sm.sink.service.xschedule.conf.SinkJobScheduler\",\"members\":[]}]');
INSERT INTO `cf_release_detail` VALUES (267, 278, 61, 1, 'ok', '[{\"beanName\":\"dataflowJobScheduler\",\"beanType\":\"com.sm.sink.service.xschedule.conf.SinkJobScheduler\",\"members\":[]}]');
INSERT INTO `cf_release_detail` VALUES (268, 279, 61, 1, 'ok', '[{\"beanName\":\"dataflowJobScheduler\",\"beanType\":\"com.sm.sink.service.xschedule.conf.SinkJobScheduler\",\"members\":[]}]');
INSERT INTO `cf_release_detail` VALUES (269, 280, 61, 1, 'ok', '[{\"beanName\":\"dataflowJobScheduler\",\"beanType\":\"com.sm.sink.service.xschedule.conf.SinkJobScheduler\",\"members\":[]}]');
INSERT INTO `cf_release_detail` VALUES (270, 281, 61, 1, 'ok', '[{\"beanName\":\"dataflowJobScheduler\",\"beanType\":\"com.sm.sink.service.xschedule.conf.SinkJobScheduler\",\"members\":[]}]');
INSERT INTO `cf_release_detail` VALUES (271, 282, 61, 1, 'ok', '[{\"beanName\":\"dataflowJobScheduler\",\"beanType\":\"com.sm.sink.service.xschedule.conf.SinkJobScheduler\",\"members\":[]}]');
INSERT INTO `cf_release_detail` VALUES (272, 283, 58, 1, 'ok', '[{\"beanName\":\"exampleService\",\"beanType\":\"com.wl4g.devops.scm.example.service.ExampleService\",\"members\":[{\"propertyName\":\"example.firstName\",\"oldValue\":\"TTTYYY-359\",\"newValue\":\"TTTYYY-758\",\"modifyed\":true}]}]');
INSERT INTO `cf_release_detail` VALUES (273, 284, 58, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (274, 285, 58, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (275, 285, 59, NULL, NULL, '暂无结果');
INSERT INTO `cf_release_detail` VALUES (276, 286, 59, NULL, NULL, '暂无结果');
COMMIT;

-- ----------------------------
-- Table structure for cf_release_history
-- ----------------------------
DROP TABLE IF EXISTS `cf_release_history`;
CREATE TABLE `cf_release_history` (
  `id` int(64) NOT NULL AUTO_INCREMENT,
  `version_id` int(64) NOT NULL COMMENT '版本号ID',
  `status` int(1) DEFAULT NULL COMMENT '发布状态（1:成功/2:失败）',
  `type` int(1) NOT NULL DEFAULT '1' COMMENT '配置类型（1：新发布/2：重回滚）',
  `remark` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  `create_by` int(11) NOT NULL COMMENT '发布用户ID',
  `create_date` datetime NOT NULL COMMENT '发布时间',
  `del_flag` int(1) NOT NULL DEFAULT '0' COMMENT '删除状态',
  PRIMARY KEY (`id`),
  KEY `version_id` (`version_id`),
  KEY `create_by` (`create_by`),
  CONSTRAINT `cf_release_history_ibfk_1` FOREIGN KEY (`version_id`) REFERENCES `cf_version` (`id`),
  CONSTRAINT `cf_release_history_ibfk_2` FOREIGN KEY (`create_by`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=287 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='ACM配置发布历史记录表';

-- ----------------------------
-- Records of cf_release_history
-- ----------------------------
BEGIN;
INSERT INTO `cf_release_history` VALUES (141, 175, NULL, 1, 'test', 1, '2018-11-08 21:56:04', 0);
INSERT INTO `cf_release_history` VALUES (142, 176, NULL, 1, 'test', 1, '2018-11-08 21:57:51', 0);
INSERT INTO `cf_release_history` VALUES (143, 177, NULL, 1, 'test', 1, '2018-11-08 22:02:58', 0);
INSERT INTO `cf_release_history` VALUES (144, 178, NULL, 1, 'test', 1, '2018-11-08 22:21:02', 0);
INSERT INTO `cf_release_history` VALUES (145, 179, NULL, 1, 'test', 1, '2018-11-08 22:25:38', 0);
INSERT INTO `cf_release_history` VALUES (146, 180, NULL, 1, 'test', 1, '2018-11-08 22:26:50', 0);
INSERT INTO `cf_release_history` VALUES (147, 181, NULL, 1, 'test', 1, '2018-11-08 22:29:05', 0);
INSERT INTO `cf_release_history` VALUES (148, 182, NULL, 1, 'test', 1, '2018-11-08 22:29:34', 0);
INSERT INTO `cf_release_history` VALUES (149, 183, NULL, 1, 'test', 1, '2018-11-08 22:30:02', 0);
INSERT INTO `cf_release_history` VALUES (150, 184, NULL, 1, 'test', 1, '2018-11-08 22:30:48', 0);
INSERT INTO `cf_release_history` VALUES (151, 185, NULL, 1, 'test', 1, '2018-11-08 22:31:29', 0);
INSERT INTO `cf_release_history` VALUES (152, 186, NULL, 1, 'test', 1, '2018-11-08 22:35:35', 0);
INSERT INTO `cf_release_history` VALUES (153, 187, NULL, 1, 'test', 1, '2018-11-08 22:35:55', 0);
INSERT INTO `cf_release_history` VALUES (154, 188, NULL, 1, 'test', 1, '2018-11-08 22:48:33', 0);
INSERT INTO `cf_release_history` VALUES (155, 189, NULL, 1, 'test', 1, '2018-11-08 22:54:37', 0);
INSERT INTO `cf_release_history` VALUES (156, 190, NULL, 1, 'test', 1, '2018-11-08 22:55:11', 0);
INSERT INTO `cf_release_history` VALUES (157, 191, NULL, 1, 'test', 1, '2018-11-09 09:56:24', 0);
INSERT INTO `cf_release_history` VALUES (158, 192, NULL, 1, 'test', 1, '2018-11-09 09:57:20', 0);
INSERT INTO `cf_release_history` VALUES (159, 193, NULL, 1, 'test', 1, '2018-11-09 09:57:29', 0);
INSERT INTO `cf_release_history` VALUES (160, 194, NULL, 1, 'test', 1, '2018-11-09 10:01:49', 0);
INSERT INTO `cf_release_history` VALUES (161, 195, NULL, 1, 'test', 1, '2018-11-09 10:03:41', 0);
INSERT INTO `cf_release_history` VALUES (162, 196, NULL, 1, 'test', 1, '2018-11-09 10:03:56', 0);
INSERT INTO `cf_release_history` VALUES (163, 197, NULL, 1, 'test', 1, '2018-11-09 10:05:06', 0);
INSERT INTO `cf_release_history` VALUES (164, 198, NULL, 1, 'test', 1, '2018-11-09 10:06:37', 0);
INSERT INTO `cf_release_history` VALUES (165, 199, NULL, 1, 'test', 1, '2018-11-09 10:06:46', 0);
INSERT INTO `cf_release_history` VALUES (166, 200, NULL, 1, 'test', 1, '2018-11-09 11:02:19', 0);
INSERT INTO `cf_release_history` VALUES (167, 201, NULL, 1, 'test', 1, '2018-11-09 11:03:07', 0);
INSERT INTO `cf_release_history` VALUES (168, 202, NULL, 1, 'test', 1, '2018-11-09 11:03:37', 0);
INSERT INTO `cf_release_history` VALUES (169, 203, NULL, 1, 'test', 1, '2018-11-09 11:04:35', 0);
INSERT INTO `cf_release_history` VALUES (170, 204, NULL, 1, 'test', 1, '2018-11-09 11:08:56', 0);
INSERT INTO `cf_release_history` VALUES (171, 205, NULL, 1, 'test', 1, '2018-11-09 11:10:38', 0);
INSERT INTO `cf_release_history` VALUES (172, 206, NULL, 1, 'test', 1, '2018-11-09 11:13:54', 0);
INSERT INTO `cf_release_history` VALUES (173, 207, NULL, 1, 'test', 1, '2018-11-09 11:23:50', 0);
INSERT INTO `cf_release_history` VALUES (174, 208, NULL, 1, 'test', 1, '2018-11-09 11:24:10', 0);
INSERT INTO `cf_release_history` VALUES (175, 209, NULL, 1, 'test', 1, '2018-11-09 11:24:22', 0);
INSERT INTO `cf_release_history` VALUES (176, 210, NULL, 1, 'test', 1, '2018-11-09 11:26:51', 0);
INSERT INTO `cf_release_history` VALUES (177, 211, NULL, 1, 'test', 1, '2018-11-09 11:27:00', 0);
INSERT INTO `cf_release_history` VALUES (178, 212, NULL, 1, 'test', 1, '2018-11-09 11:27:28', 0);
INSERT INTO `cf_release_history` VALUES (179, 213, NULL, 1, 'test', 1, '2018-11-09 11:28:53', 0);
INSERT INTO `cf_release_history` VALUES (180, 214, NULL, 1, 'test', 1, '2018-11-09 11:32:38', 0);
INSERT INTO `cf_release_history` VALUES (181, 215, NULL, 1, 'test', 1, '2018-11-09 11:34:00', 0);
INSERT INTO `cf_release_history` VALUES (182, 216, NULL, 1, 'test', 1, '2018-11-09 11:36:34', 0);
INSERT INTO `cf_release_history` VALUES (183, 217, NULL, 1, 'test', 1, '2018-11-09 11:37:15', 0);
INSERT INTO `cf_release_history` VALUES (184, 218, NULL, 1, 'test', 1, '2018-11-09 11:38:19', 0);
INSERT INTO `cf_release_history` VALUES (185, 219, NULL, 1, 'test', 1, '2018-11-09 11:39:47', 0);
INSERT INTO `cf_release_history` VALUES (186, 220, NULL, 1, 'test', 1, '2018-11-09 11:40:13', 0);
INSERT INTO `cf_release_history` VALUES (187, 221, NULL, 1, 'test', 1, '2018-11-09 11:40:41', 0);
INSERT INTO `cf_release_history` VALUES (188, 222, NULL, 1, 'test', 1, '2018-11-09 11:41:15', 0);
INSERT INTO `cf_release_history` VALUES (189, 223, NULL, 1, 'test', 1, '2018-11-09 11:42:21', 0);
INSERT INTO `cf_release_history` VALUES (190, 224, NULL, 1, 'test', 1, '2018-11-09 11:43:25', 0);
INSERT INTO `cf_release_history` VALUES (191, 225, NULL, 1, 'test', 1, '2018-11-09 11:43:46', 0);
INSERT INTO `cf_release_history` VALUES (193, 227, NULL, 1, 'test', 1, '2018-11-09 11:45:14', 0);
INSERT INTO `cf_release_history` VALUES (194, 228, NULL, 1, 'test', 1, '2018-11-09 11:45:24', 0);
INSERT INTO `cf_release_history` VALUES (195, 229, NULL, 1, 'test', 1, '2018-11-09 12:07:58', 0);
INSERT INTO `cf_release_history` VALUES (198, 201, NULL, 2, 'test', 1, '2018-11-16 09:51:17', 0);
INSERT INTO `cf_release_history` VALUES (201, 201, NULL, 2, 'test', 1, '2018-11-16 10:59:30', 0);
INSERT INTO `cf_release_history` VALUES (202, 185, NULL, 2, 'test', 1, '2018-11-16 11:00:36', 0);
INSERT INTO `cf_release_history` VALUES (203, 230, NULL, 1, 'ajy', 1, '2018-11-16 13:46:55', 0);
INSERT INTO `cf_release_history` VALUES (204, 231, NULL, 1, 'ajy', 1, '2018-11-16 13:47:11', 0);
INSERT INTO `cf_release_history` VALUES (208, 235, NULL, 1, 'zzh-scm', 1, '2018-11-16 15:59:17', 0);
INSERT INTO `cf_release_history` VALUES (210, 237, NULL, 1, 'test', 1, '2018-11-16 16:44:46', 0);
INSERT INTO `cf_release_history` VALUES (211, 238, NULL, 1, 'test', 1, '2018-11-16 16:46:58', 0);
INSERT INTO `cf_release_history` VALUES (212, 239, NULL, 1, 'test', 1, '2018-11-16 16:48:36', 0);
INSERT INTO `cf_release_history` VALUES (213, 240, NULL, 1, 'test', 1, '2018-11-16 16:50:03', 0);
INSERT INTO `cf_release_history` VALUES (214, 241, NULL, 1, 'zzh-scm', 1, '2018-11-16 16:51:18', 0);
INSERT INTO `cf_release_history` VALUES (215, 242, NULL, 1, 'zzh-scm', 1, '2018-11-16 16:52:37', 0);
INSERT INTO `cf_release_history` VALUES (216, 243, NULL, 1, 'zzh-scm', 1, '2018-11-16 16:53:00', 0);
INSERT INTO `cf_release_history` VALUES (217, 244, NULL, 1, 'zzh-scm', 1, '2018-11-16 16:54:13', 0);
INSERT INTO `cf_release_history` VALUES (218, 245, NULL, 1, 'zzh-scm', 1, '2018-11-16 16:55:25', 0);
INSERT INTO `cf_release_history` VALUES (219, 246, NULL, 1, 'zzh-scm', 1, '2018-11-16 16:55:43', 0);
INSERT INTO `cf_release_history` VALUES (220, 247, NULL, 1, 'zzh-scm', 1, '2018-11-16 16:56:22', 0);
INSERT INTO `cf_release_history` VALUES (221, 248, NULL, 1, 'zzhwwww', 1, '2018-11-16 16:56:53', 0);
INSERT INTO `cf_release_history` VALUES (222, 249, NULL, 1, 'zzhwwww', 1, '2018-11-16 16:57:38', 0);
INSERT INTO `cf_release_history` VALUES (223, 250, NULL, 1, 'zzhwwww', 1, '2018-11-16 16:58:34', 0);
INSERT INTO `cf_release_history` VALUES (224, 251, NULL, 1, 'zzhwwww', 1, '2018-11-16 16:58:57', 0);
INSERT INTO `cf_release_history` VALUES (225, 252, NULL, 1, 'zzhwwww', 1, '2018-11-16 17:00:15', 0);
INSERT INTO `cf_release_history` VALUES (226, 253, NULL, 1, 'zzhwwww', 1, '2018-11-16 17:00:44', 0);
INSERT INTO `cf_release_history` VALUES (227, 254, NULL, 1, 'zzhwwww', 1, '2018-11-16 17:00:56', 0);
INSERT INTO `cf_release_history` VALUES (228, 255, NULL, 1, 'zzhwwww', 1, '2018-11-16 17:02:25', 0);
INSERT INTO `cf_release_history` VALUES (229, 256, NULL, 1, 'zzhwwww', 1, '2018-11-16 17:02:44', 0);
INSERT INTO `cf_release_history` VALUES (230, 257, NULL, 1, 'zzhwwww', 1, '2018-11-16 17:03:57', 0);
INSERT INTO `cf_release_history` VALUES (231, 258, NULL, 1, 'zzhwwww', 1, '2018-11-16 17:05:39', 0);
INSERT INTO `cf_release_history` VALUES (232, 259, NULL, 1, 'zzhwwww', 1, '2018-11-16 17:06:47', 0);
INSERT INTO `cf_release_history` VALUES (233, 260, NULL, 1, 'zzhwwww', 1, '2018-11-16 17:07:48', 0);
INSERT INTO `cf_release_history` VALUES (234, 261, NULL, 1, 'zzhwwww', 1, '2018-11-16 17:14:44', 0);
INSERT INTO `cf_release_history` VALUES (235, 262, NULL, 1, 'zzhwwww', 1, '2018-11-16 17:19:52', 0);
INSERT INTO `cf_release_history` VALUES (236, 263, NULL, 1, 'test', 1, '2018-11-16 17:37:32', 0);
INSERT INTO `cf_release_history` VALUES (237, 264, NULL, 1, 'test', 1, '2018-11-16 17:41:10', 0);
INSERT INTO `cf_release_history` VALUES (238, 265, NULL, 1, 'test', 1, '2018-11-16 17:47:42', 0);
INSERT INTO `cf_release_history` VALUES (239, 266, NULL, 1, 'test', 1, '2018-11-16 17:52:25', 0);
INSERT INTO `cf_release_history` VALUES (240, 267, NULL, 1, 'test', 1, '2018-11-16 17:53:50', 0);
INSERT INTO `cf_release_history` VALUES (241, 268, NULL, 1, 'test', 1, '2018-11-16 17:55:19', 0);
INSERT INTO `cf_release_history` VALUES (242, 269, NULL, 1, 'test', 1, '2018-11-16 18:23:53', 0);
INSERT INTO `cf_release_history` VALUES (243, 270, NULL, 1, 'test', 1, '2018-11-16 18:24:27', 0);
INSERT INTO `cf_release_history` VALUES (244, 271, NULL, 1, 'test', 1, '2018-11-16 18:25:01', 0);
INSERT INTO `cf_release_history` VALUES (245, 272, NULL, 1, 'test', 1, '2018-11-16 18:25:15', 0);
INSERT INTO `cf_release_history` VALUES (246, 273, NULL, 1, 'test', 1, '2018-11-16 18:27:33', 0);
INSERT INTO `cf_release_history` VALUES (247, 274, NULL, 1, 'test', 1, '2018-11-16 18:32:03', 0);
INSERT INTO `cf_release_history` VALUES (248, 275, NULL, 1, 'test', 1, '2018-11-16 18:37:20', 0);
INSERT INTO `cf_release_history` VALUES (249, 276, NULL, 1, 'test', 1, '2018-11-16 18:38:00', 0);
INSERT INTO `cf_release_history` VALUES (250, 277, NULL, 1, 'test', 1, '2018-11-16 18:38:42', 0);
INSERT INTO `cf_release_history` VALUES (251, 278, NULL, 1, 'test', 1, '2018-11-16 18:40:20', 0);
INSERT INTO `cf_release_history` VALUES (252, 279, NULL, 1, 'test', 1, '2018-11-16 18:40:56', 0);
INSERT INTO `cf_release_history` VALUES (253, 280, NULL, 1, 'test', 1, '2018-11-16 18:47:08', 0);
INSERT INTO `cf_release_history` VALUES (254, 281, NULL, 1, 'zzhwwww', 1, '2018-11-16 18:49:34', 0);
INSERT INTO `cf_release_history` VALUES (255, 282, NULL, 1, 'zzhwwww', 1, '2018-11-16 18:50:31', 0);
INSERT INTO `cf_release_history` VALUES (256, 282, NULL, 2, 'zzhwwww', 1, '2018-11-16 18:51:43', 0);
INSERT INTO `cf_release_history` VALUES (257, 282, NULL, 2, 'zzhwwww', 1, '2018-11-16 18:51:59', 0);
INSERT INTO `cf_release_history` VALUES (258, 282, NULL, 2, 'zzhwwww', 1, '2018-11-19 11:23:06', 0);
INSERT INTO `cf_release_history` VALUES (259, 283, NULL, 1, 'sink scm test', 1, '2018-11-22 18:48:50', 0);
INSERT INTO `cf_release_history` VALUES (260, 284, NULL, 1, 'sink scm test', 1, '2018-11-22 18:49:58', 0);
INSERT INTO `cf_release_history` VALUES (261, 285, NULL, 1, 'sink scm test', 1, '2018-11-22 18:51:51', 0);
INSERT INTO `cf_release_history` VALUES (262, 286, NULL, 1, 'zzhwwww', 1, '2018-11-23 10:27:51', 0);
INSERT INTO `cf_release_history` VALUES (263, 287, NULL, 1, 'zzh', 1, '2018-11-23 10:31:57', 0);
INSERT INTO `cf_release_history` VALUES (264, 288, NULL, 1, 'test', 1, '2018-11-23 10:36:01', 0);
INSERT INTO `cf_release_history` VALUES (265, 289, NULL, 1, 'zzh', 1, '2018-11-23 10:35:17', 0);
INSERT INTO `cf_release_history` VALUES (266, 290, NULL, 1, 'zzh', 1, '2018-11-23 10:36:43', 0);
INSERT INTO `cf_release_history` VALUES (267, 291, NULL, 1, 'test', 1, '2018-11-23 10:41:10', 0);
INSERT INTO `cf_release_history` VALUES (268, 292, NULL, 1, 'zzh', 1, '2018-11-23 10:44:04', 0);
INSERT INTO `cf_release_history` VALUES (269, 293, NULL, 1, 'zzh', 1, '2018-11-23 10:44:42', 0);
INSERT INTO `cf_release_history` VALUES (270, 294, NULL, 1, 'zzh', 1, '2018-11-23 10:50:52', 0);
INSERT INTO `cf_release_history` VALUES (271, 295, NULL, 1, 'zzh', 1, '2018-11-23 11:24:24', 0);
INSERT INTO `cf_release_history` VALUES (272, 296, NULL, 1, 'zzh', 1, '2018-11-23 11:25:12', 0);
INSERT INTO `cf_release_history` VALUES (273, 297, NULL, 1, 'sink config test', 1, '2018-11-23 11:29:05', 0);
INSERT INTO `cf_release_history` VALUES (274, 298, NULL, 1, 'sink config test', 1, '2018-11-23 11:29:35', 0);
INSERT INTO `cf_release_history` VALUES (275, 299, NULL, 1, 'sink config test', 1, '2018-11-23 11:32:37', 0);
INSERT INTO `cf_release_history` VALUES (276, 300, NULL, 1, 'sink config test', 1, '2018-11-23 11:40:20', 0);
INSERT INTO `cf_release_history` VALUES (277, 301, NULL, 1, 'sink config test', 1, '2018-11-23 11:40:55', 0);
INSERT INTO `cf_release_history` VALUES (278, 302, NULL, 1, 'sink config test', 1, '2018-11-23 11:41:15', 0);
INSERT INTO `cf_release_history` VALUES (279, 303, NULL, 1, 'sink config test', 1, '2018-11-23 11:41:32', 0);
INSERT INTO `cf_release_history` VALUES (280, 304, NULL, 1, 'sink config test', 1, '2018-11-23 11:43:00', 0);
INSERT INTO `cf_release_history` VALUES (281, 305, NULL, 1, 'sink config test', 1, '2018-11-23 11:43:28', 0);
INSERT INTO `cf_release_history` VALUES (282, 306, NULL, 1, 'sink config test', 1, '2018-11-23 11:43:53', 0);
INSERT INTO `cf_release_history` VALUES (283, 307, NULL, 1, 'zzh', 1, '2018-11-26 10:14:44', 0);
INSERT INTO `cf_release_history` VALUES (284, 308, NULL, 1, 'zzh', 1, '2018-11-26 11:04:39', 0);
INSERT INTO `cf_release_history` VALUES (285, 309, NULL, 1, 'zzh', 1, '2018-11-26 11:11:09', 0);
INSERT INTO `cf_release_history` VALUES (286, 310, NULL, 1, 'zzh', 1, '2018-11-26 11:13:25', 0);
COMMIT;

-- ----------------------------
-- Table structure for cf_version
-- ----------------------------
DROP TABLE IF EXISTS `cf_version`;
CREATE TABLE `cf_version` (
  `id` int(64) NOT NULL AUTO_INCREMENT,
  `sign` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '摘要签名',
  `sign_type` varchar(8) COLLATE utf8_bin NOT NULL COMMENT '摘要算法(MD5/SHA-1/SHA-512...)',
  `tag` int(1) DEFAULT NULL COMMENT '版本标记（1:健康/2:缺陷）',
  `remark` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  `create_by` int(11) NOT NULL COMMENT '创建用户ID',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `del_flag` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `create_by` (`create_by`),
  CONSTRAINT `cf_version_ibfk_1` FOREIGN KEY (`create_by`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=311 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='ACM配置历史版本表';

-- ----------------------------
-- Records of cf_version
-- ----------------------------
BEGIN;
INSERT INTO `cf_version` VALUES (175, '4f7e07693227b5115f657bac78baf2af', 'MD5', 1, 'test', 1, '2018-11-08 21:56:04', 0);
INSERT INTO `cf_version` VALUES (176, '4f7e07693227b5115f657bac78baf2af', 'MD5', NULL, 'test', 1, '2018-11-08 21:57:51', 0);
INSERT INTO `cf_version` VALUES (177, '4f7e07693227b5115f657bac78baf2af', 'MD5', NULL, 'test', 1, '2018-11-08 22:02:58', 0);
INSERT INTO `cf_version` VALUES (178, '4ad7e6c2c7d4242e1ee7ba7a5a0fd3e4', 'MD5', NULL, 'test', 1, '2018-11-08 22:21:02', 0);
INSERT INTO `cf_version` VALUES (179, 'd69b8eac0f672301b37522110ccd1341', 'MD5', NULL, 'test', 1, '2018-11-08 22:25:38', 0);
INSERT INTO `cf_version` VALUES (180, 'd69b8eac0f672301b37522110ccd1341', 'MD5', NULL, 'test', 1, '2018-11-08 22:26:50', 0);
INSERT INTO `cf_version` VALUES (181, 'd69b8eac0f672301b37522110ccd1341', 'MD5', NULL, 'test', 1, '2018-11-08 22:29:05', 0);
INSERT INTO `cf_version` VALUES (182, 'd69b8eac0f672301b37522110ccd1341', 'MD5', NULL, 'test', 1, '2018-11-08 22:29:34', 0);
INSERT INTO `cf_version` VALUES (183, 'd69b8eac0f672301b37522110ccd1341', 'MD5', NULL, 'test', 1, '2018-11-08 22:30:02', 0);
INSERT INTO `cf_version` VALUES (184, 'd69b8eac0f672301b37522110ccd1341', 'MD5', NULL, 'test', 1, '2018-11-08 22:30:48', 0);
INSERT INTO `cf_version` VALUES (185, 'd69b8eac0f672301b37522110ccd1341', 'MD5', NULL, 'test', 1, '2018-11-08 22:31:29', 0);
INSERT INTO `cf_version` VALUES (186, 'e53b9d6523fbb6bebc0a0e5c118277e4', 'MD5', NULL, 'test', 1, '2018-11-08 22:35:35', 0);
INSERT INTO `cf_version` VALUES (187, '2fa2fb93e2811fb31019e7da9d991ab0', 'MD5', NULL, 'test', 1, '2018-11-08 22:35:55', 0);
INSERT INTO `cf_version` VALUES (188, 'd3803c72a7ffc36399f11429b4818cb5', 'MD5', NULL, 'test', 1, '2018-11-08 22:48:33', 0);
INSERT INTO `cf_version` VALUES (189, 'd3803c72a7ffc36399f11429b4818cb5', 'MD5', NULL, 'test', 1, '2018-11-08 22:54:37', 0);
INSERT INTO `cf_version` VALUES (190, '24c92d7828125663d5f225f14886f952', 'MD5', NULL, 'test', 1, '2018-11-08 22:55:11', 0);
INSERT INTO `cf_version` VALUES (191, '54f1a7b5253f9988dadd595e2b951514', 'MD5', NULL, 'test', 1, '2018-11-09 09:56:24', 0);
INSERT INTO `cf_version` VALUES (192, '54f1a7b5253f9988dadd595e2b951514', 'MD5', NULL, 'test', 1, '2018-11-09 09:57:20', 0);
INSERT INTO `cf_version` VALUES (193, '111c8a79bcec462eb9e21285f0207d20', 'MD5', NULL, 'test', 1, '2018-11-09 09:57:29', 0);
INSERT INTO `cf_version` VALUES (194, '111c8a79bcec462eb9e21285f0207d20', 'MD5', NULL, 'test', 1, '2018-11-09 10:01:49', 0);
INSERT INTO `cf_version` VALUES (195, '8a600ce14f683c0c68b0763839f361ab', 'MD5', NULL, 'test', 1, '2018-11-09 10:03:41', 0);
INSERT INTO `cf_version` VALUES (196, 'a586c5ce4be43be19c63775f890dd22a', 'MD5', NULL, 'test', 1, '2018-11-09 10:03:56', 0);
INSERT INTO `cf_version` VALUES (197, 'faffb55bee6826cd57e15ee62bb074a1', 'MD5', NULL, 'test', 1, '2018-11-09 10:05:06', 0);
INSERT INTO `cf_version` VALUES (198, 'faffb55bee6826cd57e15ee62bb074a1', 'MD5', NULL, 'test', 1, '2018-11-09 10:06:37', 0);
INSERT INTO `cf_version` VALUES (199, 'faffb55bee6826cd57e15ee62bb074a1', 'MD5', NULL, 'test', 1, '2018-11-09 10:06:46', 0);
INSERT INTO `cf_version` VALUES (200, 'faffb55bee6826cd57e15ee62bb074a1', 'MD5', NULL, 'test', 1, '2018-11-09 11:02:19', 0);
INSERT INTO `cf_version` VALUES (201, 'faffb55bee6826cd57e15ee62bb074a1', 'MD5', NULL, 'test', 1, '2018-11-09 11:03:07', 0);
INSERT INTO `cf_version` VALUES (202, '212d9c5b7d2ab923355093befe747ff6', 'MD5', NULL, 'test', 1, '2018-11-09 11:03:37', 0);
INSERT INTO `cf_version` VALUES (203, '212d9c5b7d2ab923355093befe747ff6', 'MD5', NULL, 'test', 1, '2018-11-09 11:04:35', 0);
INSERT INTO `cf_version` VALUES (204, '212d9c5b7d2ab923355093befe747ff6', 'MD5', NULL, 'test', 1, '2018-11-09 11:08:56', 0);
INSERT INTO `cf_version` VALUES (205, '079fff4650159ae2106bc12961bd19bf', 'MD5', NULL, 'test', 1, '2018-11-09 11:10:38', 0);
INSERT INTO `cf_version` VALUES (206, 'd69b8eac0f672301b37522110ccd1341', 'MD5', NULL, 'test', 1, '2018-11-09 11:13:54', 0);
INSERT INTO `cf_version` VALUES (207, '4ad7e6c2c7d4242e1ee7ba7a5a0fd3e4', 'MD5', NULL, 'test', 1, '2018-11-09 11:23:50', 0);
INSERT INTO `cf_version` VALUES (208, '4ad7e6c2c7d4242e1ee7ba7a5a0fd3e4', 'MD5', NULL, 'test', 1, '2018-11-09 11:24:10', 0);
INSERT INTO `cf_version` VALUES (209, '4ad7e6c2c7d4242e1ee7ba7a5a0fd3e4', 'MD5', NULL, 'test', 1, '2018-11-09 11:24:22', 0);
INSERT INTO `cf_version` VALUES (210, '212d9c5b7d2ab923355093befe747ff6', 'MD5', NULL, 'test', 1, '2018-11-09 11:26:51', 0);
INSERT INTO `cf_version` VALUES (211, '5e5f952386ed74c747bcfdfe7d299d38', 'MD5', NULL, 'test', 1, '2018-11-09 11:27:00', 0);
INSERT INTO `cf_version` VALUES (212, 'c9408ccc6792fb8d2f3756d0fb4f265e', 'MD5', NULL, 'test', 1, '2018-11-09 11:27:28', 0);
INSERT INTO `cf_version` VALUES (213, '1621f290d8dfc00afa120248663fb179', 'MD5', NULL, 'test', 1, '2018-11-09 11:28:53', 0);
INSERT INTO `cf_version` VALUES (214, 'ff6936e7c1ab72bcbcebd9f15182aefd', 'MD5', NULL, 'test', 1, '2018-11-09 11:32:38', 0);
INSERT INTO `cf_version` VALUES (215, 'ff6936e7c1ab72bcbcebd9f15182aefd', 'MD5', NULL, 'test', 1, '2018-11-09 11:34:00', 0);
INSERT INTO `cf_version` VALUES (216, '111c8a79bcec462eb9e21285f0207d20', 'MD5', NULL, 'test', 1, '2018-11-09 11:36:34', 0);
INSERT INTO `cf_version` VALUES (217, '55da5f15773076015c7c86e27c556c1a', 'MD5', NULL, 'test', 1, '2018-11-09 11:37:15', 0);
INSERT INTO `cf_version` VALUES (218, '4e63096dabcde70ed28f1b7225606af9', 'MD5', NULL, 'test', 1, '2018-11-09 11:38:19', 0);
INSERT INTO `cf_version` VALUES (219, 'cebb816eb6fb43bec01b6198df979271', 'MD5', NULL, 'test', 1, '2018-11-09 11:39:47', 0);
INSERT INTO `cf_version` VALUES (220, 'b16f747cb371d2fff88fae8b2fa05e33', 'MD5', NULL, 'test', 1, '2018-11-09 11:40:13', 0);
INSERT INTO `cf_version` VALUES (221, 'a13965880d03d51ca5c686ea6194e3f2', 'MD5', NULL, 'test', 1, '2018-11-09 11:40:41', 0);
INSERT INTO `cf_version` VALUES (222, '4fab127244e47e1b30db4b8783c288ca', 'MD5', NULL, 'test', 1, '2018-11-09 11:41:15', 0);
INSERT INTO `cf_version` VALUES (223, '935998bfba4d42d2da1871bef1cda384', 'MD5', NULL, 'test', 1, '2018-11-09 11:42:21', 0);
INSERT INTO `cf_version` VALUES (224, '463abb3940aaf1e7f9e3e6800b2d0ac8', 'MD5', NULL, 'test', 1, '2018-11-09 11:43:25', 0);
INSERT INTO `cf_version` VALUES (225, '315287d8cd35e2f28416d167501a2a75', 'MD5', NULL, 'test', 1, '2018-11-09 11:43:46', 0);
INSERT INTO `cf_version` VALUES (227, '8ab2af98ac39dfab2cd727cccec5918e', 'MD5', NULL, 'test', 1, '2018-11-09 11:45:14', 0);
INSERT INTO `cf_version` VALUES (228, 'd448378873cdfe51a3363659b98f95f6', 'MD5', NULL, 'test', 1, '2018-11-09 11:45:24', 0);
INSERT INTO `cf_version` VALUES (229, '2af5d989bca9b5e38e40547a12d3466c', 'MD5', NULL, 'test', 1, '2018-11-09 12:07:58', 0);
INSERT INTO `cf_version` VALUES (230, 'f612269d70e414f19f48b13eb9f74d0b', 'MD5', NULL, 'ajy', 1, '2018-11-16 13:46:55', 0);
INSERT INTO `cf_version` VALUES (231, 'f612269d70e414f19f48b13eb9f74d0b', 'MD5', NULL, 'ajy', 1, '2018-11-16 13:47:11', 0);
INSERT INTO `cf_version` VALUES (235, 'e53d257ac657be765bb552096bd6c50e', 'MD5', NULL, 'zzh-scm', 1, '2018-11-16 15:59:17', 0);
INSERT INTO `cf_version` VALUES (237, 'dcbe71bd6fecccf00c03c0b7f0df898c', 'MD5', NULL, 'test', 1, '2018-11-16 16:44:46', 0);
INSERT INTO `cf_version` VALUES (238, 'f5f1502683deb43082d3597fe27320b3', 'MD5', NULL, 'test', 1, '2018-11-16 16:46:58', 0);
INSERT INTO `cf_version` VALUES (239, 'ffa3850e77fd6647c75dbf77af43caea', 'MD5', NULL, 'test', 1, '2018-11-16 16:48:36', 0);
INSERT INTO `cf_version` VALUES (240, 'cb62d3c5ac84de79e5af12c3410cd549', 'MD5', NULL, 'test', 1, '2018-11-16 16:50:03', 0);
INSERT INTO `cf_version` VALUES (241, '8261555168d856fbb59043fba13d5eb3', 'MD5', NULL, 'zzh-scm', 1, '2018-11-16 16:51:18', 0);
INSERT INTO `cf_version` VALUES (242, 'e1149e5a1bfdde628adb95696abc7902', 'MD5', NULL, 'zzh-scm', 1, '2018-11-16 16:52:37', 0);
INSERT INTO `cf_version` VALUES (243, 'f707ea913fdc5e43578e6e2f99467441', 'MD5', NULL, 'zzh-scm', 1, '2018-11-16 16:53:00', 0);
INSERT INTO `cf_version` VALUES (244, 'dbd4bdbb1adf3ed8db2876867cfbfc13', 'MD5', NULL, 'zzh-scm', 1, '2018-11-16 16:54:13', 0);
INSERT INTO `cf_version` VALUES (245, '1170fcb79c7615595efdcd55282952ee', 'MD5', NULL, 'zzh-scm', 1, '2018-11-16 16:55:25', 0);
INSERT INTO `cf_version` VALUES (246, 'db972a2b574a7fb3d7591d2df2b30cba', 'MD5', NULL, 'zzh-scm', 1, '2018-11-16 16:55:43', 0);
INSERT INTO `cf_version` VALUES (247, '0d7685a785adc012cab91149599690e7', 'MD5', NULL, 'zzh-scm', 1, '2018-11-16 16:56:22', 0);
INSERT INTO `cf_version` VALUES (248, '6d6987901908a943213fe776fba15187', 'MD5', NULL, 'zzhwwww', 1, '2018-11-16 16:56:53', 0);
INSERT INTO `cf_version` VALUES (249, 'b7dc4f4b1d45586ce751a1125da40783', 'MD5', NULL, 'zzhwwww', 1, '2018-11-16 16:57:38', 0);
INSERT INTO `cf_version` VALUES (250, 'eb0320e4f8e083c7dbf5030562140564', 'MD5', NULL, 'zzhwwww', 1, '2018-11-16 16:58:34', 0);
INSERT INTO `cf_version` VALUES (251, 'cb62d3c5ac84de79e5af12c3410cd549', 'MD5', NULL, 'zzhwwww', 1, '2018-11-16 16:58:57', 0);
INSERT INTO `cf_version` VALUES (252, '26458459a97879ebead34eb903c4240c', 'MD5', NULL, 'zzhwwww', 1, '2018-11-16 17:00:15', 0);
INSERT INTO `cf_version` VALUES (253, 'f58264874c1ef3192929a66f68dbb5bf', 'MD5', NULL, 'zzhwwww', 1, '2018-11-16 17:00:44', 0);
INSERT INTO `cf_version` VALUES (254, 'e53d257ac657be765bb552096bd6c50e', 'MD5', NULL, 'zzhwwww', 1, '2018-11-16 17:00:56', 0);
INSERT INTO `cf_version` VALUES (255, '4489a1bb44cde5222f123cf5669621e1', 'MD5', NULL, 'zzhwwww', 1, '2018-11-16 17:02:25', 0);
INSERT INTO `cf_version` VALUES (256, 'e53d257ac657be765bb552096bd6c50e', 'MD5', NULL, 'zzhwwww', 1, '2018-11-16 17:02:44', 0);
INSERT INTO `cf_version` VALUES (257, 'b19c9e1c3666429012d1eaa2c3c05a68', 'MD5', NULL, 'zzhwwww', 1, '2018-11-16 17:03:57', 0);
INSERT INTO `cf_version` VALUES (258, '5f1ecd287add2e8dbc130871e1910dd9', 'MD5', NULL, 'zzhwwww', 1, '2018-11-16 17:05:39', 0);
INSERT INTO `cf_version` VALUES (259, 'ca9c935309b3c6d04fd4303209b1158a', 'MD5', NULL, 'zzhwwww', 1, '2018-11-16 17:06:47', 0);
INSERT INTO `cf_version` VALUES (260, '5226935aaa97cb99871f385cd550d2f0', 'MD5', NULL, 'zzhwwww', 1, '2018-11-16 17:07:48', 0);
INSERT INTO `cf_version` VALUES (261, '920195aa5add2f849e36002e3b506c23', 'MD5', NULL, 'zzhwwww', 1, '2018-11-16 17:14:44', 0);
INSERT INTO `cf_version` VALUES (262, 'a707a6366920275d275f2a144c3b80d4', 'MD5', NULL, 'zzhwwww', 1, '2018-11-16 17:19:52', 0);
INSERT INTO `cf_version` VALUES (263, '8f6ca60c616fa3db421069a38fead895', 'MD5', NULL, 'test', 1, '2018-11-16 17:37:32', 0);
INSERT INTO `cf_version` VALUES (264, '0ca5963521f184b0a413f9c20a2f63b8', 'MD5', NULL, 'test', 1, '2018-11-16 17:41:10', 0);
INSERT INTO `cf_version` VALUES (265, '2a74e92d8ec09b50d0cdb5060cb55a00', 'MD5', NULL, 'test', 1, '2018-11-16 17:47:42', 0);
INSERT INTO `cf_version` VALUES (266, '52f1d24c267e8849d453d888253251a4', 'MD5', NULL, 'test', 1, '2018-11-16 17:52:25', 0);
INSERT INTO `cf_version` VALUES (267, 'a22331fc55690b7a5663e2914d790f6a', 'MD5', NULL, 'test', 1, '2018-11-16 17:53:50', 0);
INSERT INTO `cf_version` VALUES (268, 'ca8bd60890b30dfde898db435be29ef9', 'MD5', NULL, 'test', 1, '2018-11-16 17:55:19', 0);
INSERT INTO `cf_version` VALUES (269, 'cb62d3c5ac84de79e5af12c3410cd549', 'MD5', NULL, 'test', 1, '2018-11-16 18:23:53', 0);
INSERT INTO `cf_version` VALUES (270, '596ae86caaf93f8f2dde1fdd011b25fc', 'MD5', NULL, 'test', 1, '2018-11-16 18:24:27', 0);
INSERT INTO `cf_version` VALUES (271, '52f1d24c267e8849d453d888253251a4', 'MD5', NULL, 'test', 1, '2018-11-16 18:25:01', 0);
INSERT INTO `cf_version` VALUES (272, '7d4f4fa49f0bd79fdc508c2531e2e155', 'MD5', NULL, 'test', 1, '2018-11-16 18:25:15', 0);
INSERT INTO `cf_version` VALUES (273, 'dcbe71bd6fecccf00c03c0b7f0df898c', 'MD5', NULL, 'test', 1, '2018-11-16 18:27:33', 0);
INSERT INTO `cf_version` VALUES (274, '7d4f4fa49f0bd79fdc508c2531e2e155', 'MD5', NULL, 'test', 1, '2018-11-16 18:32:03', 0);
INSERT INTO `cf_version` VALUES (275, '1dc7d7c52d7a0ce0b1bc4e15d2b10d13', 'MD5', NULL, 'test', 1, '2018-11-16 18:37:20', 0);
INSERT INTO `cf_version` VALUES (276, 'dcbe71bd6fecccf00c03c0b7f0df898c', 'MD5', NULL, 'test', 1, '2018-11-16 18:38:00', 0);
INSERT INTO `cf_version` VALUES (277, '57a015b87e4931052804026573c0cf56', 'MD5', NULL, 'test', 1, '2018-11-16 18:38:42', 0);
INSERT INTO `cf_version` VALUES (278, '1dc7d7c52d7a0ce0b1bc4e15d2b10d13', 'MD5', NULL, 'test', 1, '2018-11-16 18:40:20', 0);
INSERT INTO `cf_version` VALUES (279, '563624174cc30640b2c8f3aa137b34e9', 'MD5', NULL, 'test', 1, '2018-11-16 18:40:56', 0);
INSERT INTO `cf_version` VALUES (280, 'b6b2cc039bfffe362df0150cc375f9c7', 'MD5', NULL, 'test', 1, '2018-11-16 18:47:08', 0);
INSERT INTO `cf_version` VALUES (281, '1bf590a96f9dbf1ec7efb611fe920b9e', 'MD5', NULL, 'zzhwwww', 1, '2018-11-16 18:49:34', 0);
INSERT INTO `cf_version` VALUES (282, '63f40992d6f42712c7c3be125bc2ced0', 'MD5', NULL, 'zzhwwww', 1, '2018-11-16 18:50:31', 0);
INSERT INTO `cf_version` VALUES (283, 'dddded92653f0794351c22f00a32daee', 'MD5', NULL, 'sink scm test', 1, '2018-11-22 18:48:50', 0);
INSERT INTO `cf_version` VALUES (284, '2b62cc6f410b6b651dd9e13ae87489d4', 'MD5', NULL, 'sink scm test', 1, '2018-11-22 18:49:58', 0);
INSERT INTO `cf_version` VALUES (285, '19bdf6d86ca640b284b22dcafec31d31', 'MD5', NULL, 'sink scm test', 1, '2018-11-22 18:51:51', 0);
INSERT INTO `cf_version` VALUES (286, '1170fcb79c7615595efdcd55282952ee', 'MD5', NULL, 'zzhwwww', 1, '2018-11-23 10:27:51', 0);
INSERT INTO `cf_version` VALUES (287, '265aae0e9ac1834b3e6bcfd4f7f4f705', 'MD5', NULL, 'zzh', 1, '2018-11-23 10:31:57', 0);
INSERT INTO `cf_version` VALUES (288, 'bec1a0833b2f8019e0e811ebde7d0005', 'MD5', NULL, 'test', 1, '2018-11-23 10:36:01', 0);
INSERT INTO `cf_version` VALUES (289, '729befd872ddb22d24ded66bd1b1901e', 'MD5', NULL, 'zzh', 1, '2018-11-23 10:35:17', 0);
INSERT INTO `cf_version` VALUES (290, 'b2a80e7cdb0012fe09cb1c3ec9b2da7b', 'MD5', NULL, 'zzh', 1, '2018-11-23 10:36:43', 0);
INSERT INTO `cf_version` VALUES (291, '1e3ce05fc8814d8a11582aeef496a6e4', 'MD5', NULL, 'test', 1, '2018-11-23 10:41:10', 0);
INSERT INTO `cf_version` VALUES (292, '9d10ad6c8b797bfc042690db53304858', 'MD5', NULL, 'zzh', 1, '2018-11-23 10:44:04', 0);
INSERT INTO `cf_version` VALUES (293, 'b868ad842e5bd207cd0b1d1b0ef89331', 'MD5', NULL, 'zzh', 1, '2018-11-23 10:44:42', 0);
INSERT INTO `cf_version` VALUES (294, 'd22117ad89d7a51ff72fb432a1a7cfa8', 'MD5', NULL, 'zzh', 1, '2018-11-23 10:50:52', 0);
INSERT INTO `cf_version` VALUES (295, '2194b72be0d8cefc34a4932ac5607c6e', 'MD5', NULL, 'zzh', 1, '2018-11-23 11:24:24', 0);
INSERT INTO `cf_version` VALUES (296, '16b01c5f63592e5a2c8bef60947c9f55', 'MD5', NULL, 'zzh', 1, '2018-11-23 11:25:12', 0);
INSERT INTO `cf_version` VALUES (297, '19bdf6d86ca640b284b22dcafec31d31', 'MD5', NULL, 'sink config test', 1, '2018-11-23 11:29:05', 0);
INSERT INTO `cf_version` VALUES (298, 'e0e0160e8a91092e8283225c9ae2b22c', 'MD5', NULL, 'sink config test', 1, '2018-11-23 11:29:35', 0);
INSERT INTO `cf_version` VALUES (299, '04a2eb9458afd70692f34b50e8fbff01', 'MD5', NULL, 'sink config test', 1, '2018-11-23 11:32:37', 0);
INSERT INTO `cf_version` VALUES (300, '347729968eb715ac0d3c289707994046', 'MD5', NULL, 'sink config test', 1, '2018-11-23 11:40:20', 0);
INSERT INTO `cf_version` VALUES (301, '60ee8a942ecd31e4b50c3a76893c0d68', 'MD5', NULL, 'sink config test', 1, '2018-11-23 11:40:55', 0);
INSERT INTO `cf_version` VALUES (302, '347729968eb715ac0d3c289707994046', 'MD5', NULL, 'sink config test', 1, '2018-11-23 11:41:15', 0);
INSERT INTO `cf_version` VALUES (303, '5eeb14653473267219a4a61ed1b9e403', 'MD5', NULL, 'sink config test', 1, '2018-11-23 11:41:32', 0);
INSERT INTO `cf_version` VALUES (304, '347729968eb715ac0d3c289707994046', 'MD5', NULL, 'sink config test', 1, '2018-11-23 11:43:00', 0);
INSERT INTO `cf_version` VALUES (305, '60ee8a942ecd31e4b50c3a76893c0d68', 'MD5', NULL, 'sink config test', 1, '2018-11-23 11:43:28', 0);
INSERT INTO `cf_version` VALUES (306, '751409eff89f253132036e050c2c420b', 'MD5', NULL, 'sink config test', 1, '2018-11-23 11:43:53', 0);
INSERT INTO `cf_version` VALUES (307, '76ee4b4d3984ded268ac6d52151f6a5f', 'MD5', NULL, 'zzh', 1, '2018-11-26 10:14:44', 0);
INSERT INTO `cf_version` VALUES (308, 'a7b81366a26007d3ca29c2dc7aaf092c', 'MD5', NULL, 'zzh', 1, '2018-11-26 11:04:39', 0);
INSERT INTO `cf_version` VALUES (309, '19bdf6d86ca640b284b22dcafec31d31', 'MD5', NULL, 'zzh', 1, '2018-11-26 11:11:09', 0);
INSERT INTO `cf_version` VALUES (310, 'd0f4a4fd7f3792f7c75962d6f5378119', 'MD5', NULL, 'zzh', 1, '2018-11-26 11:13:25', 0);
COMMIT;

-- ----------------------------
-- Table structure for cf_version_detail
-- ----------------------------
DROP TABLE IF EXISTS `cf_version_detail`;
CREATE TABLE `cf_version_detail` (
  `id` int(64) NOT NULL AUTO_INCREMENT,
  `version_id` int(64) NOT NULL COMMENT '版本号ID',
  `filename` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '文件名称(不含后缀)',
  `type` int(1) NOT NULL DEFAULT '1' COMMENT '内容文件类型（1：yml/2：preperties）',
  `content` text COLLATE utf8_bin NOT NULL COMMENT '配置内容',
  `remark` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `version_id` (`version_id`),
  CONSTRAINT `cf_version_detail_ibfk_1` FOREIGN KEY (`version_id`) REFERENCES `cf_version` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=296 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='ACM配置明细表';

-- ----------------------------
-- Records of cf_version_detail
-- ----------------------------
BEGIN;
INSERT INTO `cf_version_detail` VALUES (160, 175, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: 王\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (161, 176, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: 王\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (162, 177, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: 王\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (163, 178, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aaa\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (164, 179, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: ffff\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (165, 180, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: ffff\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (166, 181, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: ffff\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (167, 182, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: ffff\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (168, 183, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: ffff\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (169, 184, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: ffff\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (170, 185, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: ffff\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (171, 186, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: asdf\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (172, 187, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aaaafff\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (173, 188, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: 啊啊啊asdf\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (174, 189, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: 啊啊啊asdf\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (175, 190, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: 啊啊\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (176, 191, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: asdgad\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (177, 192, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: asdgad\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (178, 193, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aaaa\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (179, 194, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aaaa\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (180, 195, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: safdas\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (181, 196, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: a\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (182, 197, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aa\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (183, 198, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aa\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (184, 199, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aa\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (185, 200, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aa\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (186, 201, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aa\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (187, 202, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aaaf\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (188, 203, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aaaf\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (189, 204, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aaaf\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (190, 205, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aaafa\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (191, 206, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: ffff\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (192, 207, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aaa\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (193, 208, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aaa\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (194, 209, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aaa\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (195, 210, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aaaf\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (196, 211, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: asdfasgdag\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (197, 212, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: 啊啊啊啊asdasg\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (198, 213, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: asdgasgda\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (199, 214, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: fff\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (200, 215, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: fff\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (201, 216, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aaaa\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (202, 217, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: ttt\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (203, 218, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: asgasdgag\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (204, 219, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: sagasg\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (205, 220, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aga\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (206, 221, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: yyy\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (207, 222, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: rerwerwe\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (208, 223, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: wetagd\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (209, 224, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: tttt\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (210, 225, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: 1231af\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (212, 227, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: ashdga\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (213, 228, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aaaa11\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (214, 229, 'application-test', 1, '# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aaa啊\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (215, 230, 'ajy', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment base configuration. ####\n#\n# Spring boot configuration.\nspring:\n  resources.static-locations: classpath:/static/\n  profiles:\n    include: common,util', NULL);
INSERT INTO `cf_version_detail` VALUES (216, 231, 'ajy', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment base configuration. ####\n#\n# Spring boot configuration.\nspring:\n  resources.static-locations: classpath:/static/\n  profiles:\n    include: common,util', NULL);
INSERT INTO `cf_version_detail` VALUES (220, 235, 'zzh', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: zzh\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (222, 237, 'aaa', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aaa\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (223, 238, 'aaa', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: bbbb\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (224, 239, 'aaa', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: ccc\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (225, 240, 'aaa', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: gggg\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (226, 241, 'zzh', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: zzhaaa\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (227, 242, 'zzh', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: cccc\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (228, 243, 'zzh', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: sssss\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (229, 244, 'zzh', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: ddddd\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (230, 245, 'zzh', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: eeeee\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (231, 246, 'zzh', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: rrrrrr\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (232, 247, 'zzh', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: yyyyy\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (233, 248, 'zzh', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: uuuuu\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (234, 249, 'zzhww', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: qwe\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (235, 250, 'zzhww', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: sdf\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (236, 251, 'zzhww', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: gggg\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (237, 252, 'zzhww', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: zzzzxx\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (238, 253, 'zzhww', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: sss\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (239, 254, 'zzhww', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: zzh\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (240, 255, 'zzhww', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: jjlkl\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (241, 256, 'zzh', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: zzh\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (242, 257, 'zzh', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: zzhiii\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (243, 258, 'zzh', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: zzhiiilll\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (244, 259, 'zzh', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: ttyrt\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (245, 260, 'zzh', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: oooooo\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (246, 261, 'zzh', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: tttt\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (247, 262, 'zzh', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: tdfdfdd\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (248, 263, 'aaa', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: fff\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (249, 264, 'aaa', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: ttt\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (250, 265, 'aaa', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: fasdf\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (251, 266, 'aaa', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aaaa\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (252, 267, 'aaa', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: abcd\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (253, 268, 'aaa', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: hhh\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (254, 269, 'aaa', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: gggg\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (255, 270, 'aaa', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: asgdag\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (256, 271, 'aaa', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aaaa\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (257, 272, 'aaa', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: asdgag\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (258, 273, 'aaa', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aaa\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (259, 274, 'aaa', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: asdgag\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (260, 275, 'aaa', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: ffff\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (261, 276, 'aaa', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aaa\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (262, 277, 'aaa', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: asgsadg\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (263, 278, 'aaa', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: ffff\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (264, 279, 'aaa', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: asdf\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (265, 280, 'aaa', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: ffff\n  lastName: ggg', NULL);
INSERT INTO `cf_version_detail` VALUES (266, 281, 'zzh', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: zzzzz\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (267, 282, 'zzh', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: zzzzzssss\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (268, 283, 'application-test', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\njob.device:\n  rt:\n    cron: 0/50 * * * * ?', NULL);
INSERT INTO `cf_version_detail` VALUES (269, 284, 'application-test', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\njob.device:\n  rt:\n    cron: 0/51 * * * * ?', NULL);
INSERT INTO `cf_version_detail` VALUES (270, 285, 'application-test', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\njob.device:\n  rt:\n    cron: 0/52 * * * * ?', NULL);
INSERT INTO `cf_version_detail` VALUES (271, 286, 'zzh', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: eeeee\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (272, 287, 'test', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: gggggsdds\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (273, 288, 'aaa', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: adsfasd\n  lastName: ggg', NULL);
INSERT INTO `cf_version_detail` VALUES (274, 289, 'test', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: gggggsddsttt\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (275, 290, 'test', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: gyyy\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (276, 291, 'aaa', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: aaaa\n  lastName: ggg', NULL);
INSERT INTO `cf_version_detail` VALUES (277, 292, 'test', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: gyyyhh\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (278, 293, 'test', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: kkkkhhh\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (279, 294, 'test', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: kkkkhhhll\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (280, 295, 'test', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: 11111\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (281, 296, 'test', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: 11111222\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (282, 297, 'applicaasdfaf', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\njob.device:\n  rt:\n    cron: 0/52 * * * * ?', NULL);
INSERT INTO `cf_version_detail` VALUES (283, 298, 'applicaasdfaf', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\njob.device:\n  rt:\n    cron: 0/53 * * * * ?', NULL);
INSERT INTO `cf_version_detail` VALUES (284, 299, 'applicaasdfaf', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\njob.device:\n  rt:\n    cron: 0/10 * * * * ?', NULL);
INSERT INTO `cf_version_detail` VALUES (285, 300, 'applicaasdfaf', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\njob.device:\n  rt:\n    cron: 0/11 * * * * ?', NULL);
INSERT INTO `cf_version_detail` VALUES (286, 301, 'applicaasdfaf', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\njob.device:\n  rt:\n    cron: 0/12 * * * * ?', NULL);
INSERT INTO `cf_version_detail` VALUES (287, 302, 'applicaasdfaf', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\njob.device:\n  rt:\n    cron: 0/11 * * * * ?', NULL);
INSERT INTO `cf_version_detail` VALUES (288, 303, 'applicaasdfaf', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\njob.device:\n  rt:\n    cron: 0/14 * * * * ?', NULL);
INSERT INTO `cf_version_detail` VALUES (289, 304, 'applicaasdfaf', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\njob.device:\n  rt:\n    cron: 0/11 * * * * ?', NULL);
INSERT INTO `cf_version_detail` VALUES (290, 305, 'applicaasdfaf', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\njob.device:\n  rt:\n    cron: 0/12 * * * * ?', NULL);
INSERT INTO `cf_version_detail` VALUES (291, 306, 'applicaasdfaf', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\njob.device:\n  rt:\n    cron: 0/16 * * * * ?', NULL);
INSERT INTO `cf_version_detail` VALUES (292, 307, 'test', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: tttyyy\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (293, 308, 'test', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n# #### Environment(Test) configuration. ####\n#\n\n# Example configuration.\nexample:\n  firstName: tttuuu\n  lastName: jack', NULL);
INSERT INTO `cf_version_detail` VALUES (294, 309, 'test', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\njob.device:\n  rt:\n    cron: 0/52 * * * * ?', NULL);
INSERT INTO `cf_version_detail` VALUES (295, 310, 'test', 1, '# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,\n# All rights reserved. Contact us 983708408@qq.com\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\njob.device:\n  rt:\n    cron: 0/30 * * * * ?', NULL);
COMMIT;

-- ----------------------------
-- Table structure for ci_dependency
-- ----------------------------
DROP TABLE IF EXISTS `ci_dependency`;
CREATE TABLE `ci_dependency` (
  `id` int(11) NOT NULL,
  `project_id` int(11) DEFAULT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `parent_branch` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `del_flag` char(1) COLLATE utf8_bin DEFAULT '0',
  `create_date` datetime DEFAULT NULL,
  `create_by` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `update_by` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ci_dependency
-- ----------------------------
BEGIN;
INSERT INTO `ci_dependency` VALUES (1, 4, 3, NULL, '0', NULL, NULL, NULL, NULL);
INSERT INTO `ci_dependency` VALUES (2, 4, 5, NULL, '0', NULL, NULL, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for ci_project
-- ----------------------------
DROP TABLE IF EXISTS `ci_project`;
CREATE TABLE `ci_project` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_name` varchar(32) COLLATE utf8_bin NOT NULL COMMENT 'git项目名',
  `git_url` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT 'git项目url地址',
  `app_group_id` int(11) DEFAULT NULL COMMENT '项目组id',
  `tar_path` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT 'tar文件路径',
  `parent_app_home` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '真实项目存放的父级目录',
  `link_app_home` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '软连接地址',
  `remark` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `del_flag` char(1) COLLATE utf8_bin DEFAULT '0',
  `create_date` datetime DEFAULT NULL,
  `create_by` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `update_by` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='git项目,与appGroup一一对应';

-- ----------------------------
-- Records of ci_project
-- ----------------------------
BEGIN;
INSERT INTO `ci_project` VALUES (1, 'jianzugittest', 'http://10.0.0.30/group1/jianzugittest.git', 61, '/jzclient/target/jzclient-0.0.1-SNAPSHOT.jar', NULL, NULL, NULL, '0', '2019-05-17 17:24:02', '1', '2019-05-17 17:24:02', '1');
INSERT INTO `ci_project` VALUES (2, 'safecloud-devops', 'http://code.anjiancloud.owner:8443/devops-team/safecloud-devops.git', NULL, '/safecloud-devops-datachecker-starter/target/datachecker-master-bin.tar', NULL, NULL, NULL, '0', '2019-05-20 17:50:40', '1', '2019-05-20 17:50:40', '1');
INSERT INTO `ci_project` VALUES (3, 'safecloud-devops-support', 'http://codes.anjiancloud.owner/devops-team/safecloud-devops-support.git', NULL, NULL, NULL, NULL, NULL, '0', '2019-05-22 15:35:53', '1', '2019-05-22 15:35:53', '1');
INSERT INTO `ci_project` VALUES (4, 'safecloud-devops-datachecker', 'http://codes.anjiancloud.owner/devops-team/datachecker-team/safecloud-devops-datachecker.git', 63, '/boot/target/datachecker-master-bin.tar', NULL, NULL, NULL, '0', '2019-05-22 15:37:02', '1', '2019-05-22 15:37:02', '1');
INSERT INTO `ci_project` VALUES (5, 'safecloud-devops-trafficmonitor', 'http://codes.anjiancloud.owner/devops-team/trafficmonitor-team/safecloud-devops-trafficmonitor.git', 64, '/trafficmonitor-boot/target/trafficmonitor-master-bin.tar', NULL, NULL, NULL, '0', '2019-05-22 15:37:02', '1', '2019-05-22 15:37:02', '1');
COMMIT;

-- ----------------------------
-- Table structure for ci_task
-- ----------------------------
DROP TABLE IF EXISTS `ci_task`;
CREATE TABLE `ci_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` int(11) NOT NULL COMMENT '类型:1钩子触发,2一键部署,3回滚',
  `project_id` int(11) NOT NULL COMMENT '项目id',
  `status` int(11) NOT NULL COMMENT '状态:0创建,1开始,2进行中,3结束',
  `branch_name` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '分支名',
  `sha` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT 'commit的sha值,用于回滚',
  `parent_id` int(11) DEFAULT NULL COMMENT '从哪个历史回滚来的',
  `command` text COLLATE utf8_bin COMMENT '自定义命令',
  `tar_type` int(11) DEFAULT NULL COMMENT '类型:1tar,2jar,3自定义',
  `result` text COLLATE utf8_bin COMMENT '运行结果',
  `remark` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `del_flag` char(1) COLLATE utf8_bin DEFAULT '0',
  `create_date` datetime DEFAULT NULL,
  `create_by` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `update_by` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='任务表,记录部署任务';

-- ----------------------------
-- Records of ci_task
-- ----------------------------
BEGIN;
INSERT INTO `ci_task` VALUES (2, 1, 2, 3, 'hwj_test_ci', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-20 18:08:29', '1', '2019-05-20 18:15:52', '1');
INSERT INTO `ci_task` VALUES (3, 1, 2, 3, 'hwj_test_ci', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-20 18:20:05', '1', '2019-05-20 18:21:27', '1');
INSERT INTO `ci_task` VALUES (4, 1, 2, 3, 'hwj_test_ci', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-20 18:24:36', '1', '2019-05-20 18:25:16', '1');
INSERT INTO `ci_task` VALUES (5, 1, 2, 3, 'hwj_test_ci', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-20 18:28:08', '1', '2019-05-20 18:29:41', '1');
INSERT INTO `ci_task` VALUES (6, 1, 2, 1, 'hwj_test_ci', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-20 18:31:05', '1', '2019-05-20 18:31:09', '1');
INSERT INTO `ci_task` VALUES (7, 1, 2, 1, 'hwj_test_ci', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-20 18:37:26', '1', '2019-05-20 18:37:28', '1');
INSERT INTO `ci_task` VALUES (8, 1, 2, 2, 'hwj_test_ci', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-21 10:48:59', '1', '2019-05-21 10:51:56', '1');
INSERT INTO `ci_task` VALUES (9, 1, 2, 2, 'hwj_test_ci', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-21 11:10:59', '1', '2019-05-21 11:11:56', '1');
INSERT INTO `ci_task` VALUES (10, 1, 2, 1, 'hwj_test_ci', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-21 11:31:36', '1', '2019-05-21 11:31:43', '1');
INSERT INTO `ci_task` VALUES (11, 1, 2, 2, 'hwj_test_ci', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-21 13:52:27', '1', '2019-05-21 13:53:00', '1');
INSERT INTO `ci_task` VALUES (12, 1, 2, 3, 'hwj_test_ci', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-21 18:04:25', '1', '2019-05-21 18:06:29', '1');
INSERT INTO `ci_task` VALUES (13, 1, 2, 2, 'hwj_test_ci', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-21 18:12:29', '1', '2019-05-21 18:13:03', '1');
INSERT INTO `ci_task` VALUES (14, 1, 2, 2, 'hwj_test_ci', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-21 18:12:54', '1', '2019-05-21 18:13:20', '1');
INSERT INTO `ci_task` VALUES (15, 1, 2, 2, 'hwj_test_ci', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-21 18:15:35', '1', '2019-05-21 18:16:18', '1');
INSERT INTO `ci_task` VALUES (16, 1, 2, 2, 'hwj_test_ci', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-21 18:20:02', '1', '2019-05-21 18:20:28', '1');
INSERT INTO `ci_task` VALUES (17, 1, 2, 2, 'hwj_test_ci', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-22 09:44:25', '1', '2019-05-22 09:44:53', '1');
INSERT INTO `ci_task` VALUES (18, 1, 2, 3, 'hwj_test_ci', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-22 15:16:36', '1', '2019-05-22 15:16:43', '1');
INSERT INTO `ci_task` VALUES (19, 1, 4, 1, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-22 16:53:33', '1', '2019-05-22 16:53:36', '1');
INSERT INTO `ci_task` VALUES (20, 1, 4, 1, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-22 16:55:50', '1', '2019-05-22 16:55:50', '1');
INSERT INTO `ci_task` VALUES (21, 1, 4, 1, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-22 17:03:54', '1', '2019-05-22 17:03:54', '1');
INSERT INTO `ci_task` VALUES (22, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-22 17:26:43', '1', '2019-05-22 17:28:35', '1');
INSERT INTO `ci_task` VALUES (23, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-22 17:34:48', '1', '2019-05-22 17:36:31', '1');
INSERT INTO `ci_task` VALUES (24, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-23 10:51:28', '1', '2019-05-23 10:55:02', '1');
INSERT INTO `ci_task` VALUES (25, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-23 11:01:40', '1', '2019-05-23 11:02:53', '1');
INSERT INTO `ci_task` VALUES (26, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-23 11:03:31', '1', '2019-05-23 11:05:20', '1');
INSERT INTO `ci_task` VALUES (27, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-23 11:20:26', '1', '2019-05-23 11:21:34', '1');
INSERT INTO `ci_task` VALUES (28, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-23 11:27:23', '1', '2019-05-23 11:30:09', '1');
INSERT INTO `ci_task` VALUES (29, 1, 4, 1, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-23 11:30:47', '1', '2019-05-23 11:30:47', '1');
INSERT INTO `ci_task` VALUES (30, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-23 11:34:19', '1', '2019-05-23 11:35:03', '1');
INSERT INTO `ci_task` VALUES (31, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-23 11:39:02', '1', '2019-05-23 11:41:36', '1');
INSERT INTO `ci_task` VALUES (32, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-23 11:41:44', '1', '2019-05-23 11:42:25', '1');
INSERT INTO `ci_task` VALUES (33, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-23 13:50:03', '1', '2019-05-23 13:50:40', '1');
INSERT INTO `ci_task` VALUES (34, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-23 15:06:33', '1', '2019-05-23 15:07:10', '1');
INSERT INTO `ci_task` VALUES (35, 1, 4, 3, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-23 15:35:45', '1', '2019-05-23 15:36:15', '1');
INSERT INTO `ci_task` VALUES (36, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-23 16:29:33', '1', '2019-05-23 16:30:19', '1');
INSERT INTO `ci_task` VALUES (37, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-23 18:05:42', '1', '2019-05-23 18:06:24', '1');
INSERT INTO `ci_task` VALUES (38, 1, 4, 3, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-23 18:33:55', '1', '2019-05-23 18:34:00', '1');
INSERT INTO `ci_task` VALUES (39, 1, 4, 3, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-23 18:43:27', '1', '2019-05-23 18:43:30', '1');
INSERT INTO `ci_task` VALUES (40, 1, 4, 3, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-23 18:50:06', '1', '2019-05-23 18:50:11', '1');
INSERT INTO `ci_task` VALUES (41, 1, 4, 3, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-23 18:52:20', '1', '2019-05-23 18:53:29', '1');
INSERT INTO `ci_task` VALUES (42, 1, 4, 3, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-23 19:08:13', '1', '2019-05-23 19:08:48', '1');
INSERT INTO `ci_task` VALUES (43, 1, 4, 3, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 09:10:30', '1', '2019-05-24 09:10:30', '1');
INSERT INTO `ci_task` VALUES (44, 1, 4, 3, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 09:15:49', '1', '2019-05-24 09:17:00', '1');
INSERT INTO `ci_task` VALUES (45, 1, 4, 3, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 09:45:21', '1', '2019-05-24 09:46:23', '1');
INSERT INTO `ci_task` VALUES (46, 1, 4, 3, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 10:20:32', '1', '2019-05-24 10:21:15', '1');
INSERT INTO `ci_task` VALUES (47, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 10:30:23', '1', '2019-05-24 10:31:07', '1');
INSERT INTO `ci_task` VALUES (48, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 10:40:01', '1', '2019-05-24 10:40:46', '1');
INSERT INTO `ci_task` VALUES (49, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 10:46:40', '1', '2019-05-24 10:47:23', '1');
INSERT INTO `ci_task` VALUES (50, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 10:48:32', '1', '2019-05-24 10:49:14', '1');
INSERT INTO `ci_task` VALUES (51, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 10:50:59', '1', '2019-05-24 10:51:47', '1');
INSERT INTO `ci_task` VALUES (52, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 11:17:56', '1', '2019-05-24 11:19:06', '1');
INSERT INTO `ci_task` VALUES (53, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 11:19:59', '1', '2019-05-24 11:20:40', '1');
INSERT INTO `ci_task` VALUES (54, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 11:22:10', '1', '2019-05-24 11:26:17', '1');
INSERT INTO `ci_task` VALUES (55, 1, 4, 1, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 11:28:37', '1', '2019-05-24 11:28:37', '1');
INSERT INTO `ci_task` VALUES (56, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 11:29:15', '1', '2019-05-24 11:34:28', '1');
INSERT INTO `ci_task` VALUES (57, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 11:40:12', '1', '2019-05-24 11:40:55', '1');
INSERT INTO `ci_task` VALUES (58, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 11:44:20', '1', '2019-05-24 11:44:57', '1');
INSERT INTO `ci_task` VALUES (59, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 14:39:51', '1', '2019-05-24 14:41:43', '1');
INSERT INTO `ci_task` VALUES (60, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 14:47:44', '1', '2019-05-24 14:48:20', '1');
INSERT INTO `ci_task` VALUES (61, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 14:51:37', '1', '2019-05-24 14:52:17', '1');
INSERT INTO `ci_task` VALUES (62, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 15:17:36', '1', '2019-05-24 15:18:17', '1');
INSERT INTO `ci_task` VALUES (63, 1, 4, 3, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 15:54:23', '1', '2019-05-24 15:54:24', '1');
INSERT INTO `ci_task` VALUES (64, 1, 4, 3, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 15:54:33', '1', '2019-05-24 15:54:33', '1');
INSERT INTO `ci_task` VALUES (65, 1, 4, 3, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 15:56:16', '1', '2019-05-24 15:56:16', '1');
INSERT INTO `ci_task` VALUES (66, 1, 4, 3, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 15:58:54', '1', '2019-05-24 15:59:25', '1');
INSERT INTO `ci_task` VALUES (67, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 16:17:35', '1', '2019-05-24 16:18:26', '1');
INSERT INTO `ci_task` VALUES (68, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 16:19:38', '1', '2019-05-24 16:20:27', '1');
INSERT INTO `ci_task` VALUES (69, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 16:23:35', '1', '2019-05-24 16:24:11', '1');
INSERT INTO `ci_task` VALUES (70, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 16:24:30', '1', '2019-05-24 16:25:05', '1');
INSERT INTO `ci_task` VALUES (71, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 16:25:33', '1', '2019-05-24 16:26:10', '1');
INSERT INTO `ci_task` VALUES (72, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 16:31:52', '1', '2019-05-24 16:32:57', '1');
INSERT INTO `ci_task` VALUES (73, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 16:33:52', '1', '2019-05-24 16:34:28', '1');
INSERT INTO `ci_task` VALUES (74, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 16:34:55', '1', '2019-05-24 16:36:59', '1');
INSERT INTO `ci_task` VALUES (75, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 16:37:26', '1', '2019-05-24 16:43:02', '1');
INSERT INTO `ci_task` VALUES (76, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 16:44:03', '1', '2019-05-24 16:44:42', '1');
INSERT INTO `ci_task` VALUES (77, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 16:45:35', '1', '2019-05-24 16:46:10', '1');
INSERT INTO `ci_task` VALUES (78, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 16:46:30', '1', '2019-05-24 16:47:08', '1');
INSERT INTO `ci_task` VALUES (79, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 16:47:29', '1', '2019-05-24 16:48:06', '1');
INSERT INTO `ci_task` VALUES (80, 1, 4, 1, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 16:50:57', '1', '2019-05-24 16:50:57', '1');
INSERT INTO `ci_task` VALUES (81, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 16:52:11', '1', '2019-05-24 16:52:47', '1');
INSERT INTO `ci_task` VALUES (82, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 16:52:56', '1', '2019-05-24 16:53:33', '1');
INSERT INTO `ci_task` VALUES (83, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 16:53:45', '1', '2019-05-24 16:54:22', '1');
INSERT INTO `ci_task` VALUES (84, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 16:54:31', '1', '2019-05-24 16:55:06', '1');
INSERT INTO `ci_task` VALUES (85, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 16:55:49', '1', '2019-05-24 16:56:29', '1');
INSERT INTO `ci_task` VALUES (86, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 16:56:47', '1', '2019-05-24 16:57:26', '1');
INSERT INTO `ci_task` VALUES (87, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 17:00:24', '1', '2019-05-24 17:01:00', '1');
INSERT INTO `ci_task` VALUES (88, 1, 4, 1, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 17:04:57', '1', '2019-05-24 17:04:57', '1');
INSERT INTO `ci_task` VALUES (89, 1, 4, 1, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 17:06:32', '1', '2019-05-24 17:06:32', '1');
INSERT INTO `ci_task` VALUES (90, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 17:08:18', '1', '2019-05-24 17:08:54', '1');
INSERT INTO `ci_task` VALUES (91, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 17:11:40', '1', '2019-05-24 17:12:14', '1');
INSERT INTO `ci_task` VALUES (92, 1, 4, 1, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 17:27:20', '1', '2019-05-24 17:27:21', '1');
INSERT INTO `ci_task` VALUES (93, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 17:30:53', '1', '2019-05-24 17:31:31', '1');
INSERT INTO `ci_task` VALUES (94, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 17:31:48', '1', '2019-05-24 17:32:25', '1');
INSERT INTO `ci_task` VALUES (95, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 18:17:25', '1', '2019-05-24 18:18:08', '1');
INSERT INTO `ci_task` VALUES (96, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 18:18:20', '1', '2019-05-24 18:18:59', '1');
INSERT INTO `ci_task` VALUES (97, 1, 4, 2, 'master', NULL, NULL, NULL, 1, NULL, NULL, '0', '2019-05-24 18:51:38', '1', '2019-05-24 18:52:29', '1');
COMMIT;

-- ----------------------------
-- Table structure for ci_task_detail
-- ----------------------------
DROP TABLE IF EXISTS `ci_task_detail`;
CREATE TABLE `ci_task_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` int(11) NOT NULL COMMENT '任务id',
  `instance_id` int(11) NOT NULL COMMENT '实例id',
  `status` int(11) NOT NULL COMMENT '状态:0创建,1开始,2进行中,3结束',
  `result` text COLLATE utf8_bin COMMENT '结果',
  `del_flag` char(1) COLLATE utf8_bin DEFAULT '0',
  `create_date` datetime DEFAULT NULL,
  `create_by` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `update_by` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='一个任务对应的多个实例';

-- ----------------------------
-- Records of ci_task_detail
-- ----------------------------
BEGIN;
INSERT INTO `ci_task_detail` VALUES (1, 2, 63, 0, NULL, '0', '2019-05-20 18:08:48', '1', '2019-05-20 18:08:48', '1');
INSERT INTO `ci_task_detail` VALUES (2, 3, 63, 0, NULL, '0', '2019-05-20 18:20:05', '1', '2019-05-20 18:20:05', '1');
INSERT INTO `ci_task_detail` VALUES (3, 4, 63, 0, NULL, '0', '2019-05-20 18:24:36', '1', '2019-05-20 18:24:36', '1');
INSERT INTO `ci_task_detail` VALUES (4, 5, 63, 0, NULL, '0', '2019-05-20 18:28:08', '1', '2019-05-20 18:28:08', '1');
INSERT INTO `ci_task_detail` VALUES (5, 6, 63, 0, NULL, '0', '2019-05-20 18:31:05', '1', '2019-05-20 18:31:05', '1');
INSERT INTO `ci_task_detail` VALUES (6, 7, 63, 0, NULL, '0', '2019-05-20 18:37:26', '1', '2019-05-20 18:37:26', '1');
INSERT INTO `ci_task_detail` VALUES (7, 8, 63, 2, NULL, '0', '2019-05-21 10:48:59', '1', '2019-05-21 10:51:52', '1');
INSERT INTO `ci_task_detail` VALUES (8, 9, 63, 2, NULL, '0', '2019-05-21 11:10:59', '1', '2019-05-21 11:11:51', '1');
INSERT INTO `ci_task_detail` VALUES (9, 10, 63, 1, NULL, '0', '2019-05-21 11:31:36', '1', '2019-05-21 11:32:10', '1');
INSERT INTO `ci_task_detail` VALUES (10, 11, 63, 2, NULL, '0', '2019-05-21 13:52:27', '1', '2019-05-21 13:52:58', '1');
INSERT INTO `ci_task_detail` VALUES (11, 12, 63, 0, NULL, '0', '2019-05-21 18:04:26', '1', '2019-05-21 18:04:26', '1');
INSERT INTO `ci_task_detail` VALUES (12, 13, 63, 2, NULL, '0', '2019-05-21 18:12:29', '1', '2019-05-21 18:12:55', '1');
INSERT INTO `ci_task_detail` VALUES (13, 14, 63, 2, NULL, '0', '2019-05-21 18:12:54', '1', '2019-05-21 18:13:18', '1');
INSERT INTO `ci_task_detail` VALUES (14, 15, 63, 2, NULL, '0', '2019-05-21 18:15:35', '1', '2019-05-21 18:15:58', '1');
INSERT INTO `ci_task_detail` VALUES (15, 16, 63, 2, NULL, '0', '2019-05-21 18:20:02', '1', '2019-05-21 18:20:25', '1');
INSERT INTO `ci_task_detail` VALUES (16, 17, 63, 2, NULL, '0', '2019-05-22 09:44:25', '1', '2019-05-22 09:44:51', '1');
INSERT INTO `ci_task_detail` VALUES (17, 18, 63, 0, NULL, '0', '2019-05-22 15:16:36', '1', '2019-05-22 15:16:36', '1');
INSERT INTO `ci_task_detail` VALUES (18, 19, 63, 0, NULL, '0', '2019-05-22 16:53:33', '1', '2019-05-22 16:53:33', '1');
INSERT INTO `ci_task_detail` VALUES (19, 20, 63, 0, NULL, '0', '2019-05-22 16:55:50', '1', '2019-05-22 16:55:50', '1');
INSERT INTO `ci_task_detail` VALUES (20, 21, 63, 1, NULL, '0', '2019-05-22 17:03:54', '1', '2019-05-22 17:07:52', '1');
INSERT INTO `ci_task_detail` VALUES (21, 22, 64, 2, NULL, '0', '2019-05-22 17:26:43', '1', '2019-05-22 17:28:33', '1');
INSERT INTO `ci_task_detail` VALUES (22, 23, 64, 2, NULL, '0', '2019-05-22 17:34:48', '1', '2019-05-22 17:36:29', '1');
INSERT INTO `ci_task_detail` VALUES (23, 24, 64, 3, NULL, '0', '2019-05-23 10:51:28', '1', '2019-05-23 10:55:02', '1');
INSERT INTO `ci_task_detail` VALUES (24, 25, 64, 3, NULL, '0', '2019-05-23 11:01:40', '1', '2019-05-23 11:02:47', '1');
INSERT INTO `ci_task_detail` VALUES (25, 26, 64, 3, NULL, '0', '2019-05-23 11:03:31', '1', '2019-05-23 11:05:20', '1');
INSERT INTO `ci_task_detail` VALUES (26, 27, 64, 3, NULL, '0', '2019-05-23 11:20:26', '1', '2019-05-23 11:21:33', '1');
INSERT INTO `ci_task_detail` VALUES (27, 28, 64, 3, NULL, '0', '2019-05-23 11:27:23', '1', '2019-05-23 11:30:09', '1');
INSERT INTO `ci_task_detail` VALUES (28, 29, 64, 1, NULL, '0', '2019-05-23 11:30:47', '1', '2019-05-23 11:31:19', '1');
INSERT INTO `ci_task_detail` VALUES (29, 30, 64, 2, NULL, '0', '2019-05-23 11:34:19', '1', '2019-05-23 11:34:59', '1');
INSERT INTO `ci_task_detail` VALUES (30, 31, 64, 2, NULL, '0', '2019-05-23 11:39:02', '1', '2019-05-23 11:39:39', '1');
INSERT INTO `ci_task_detail` VALUES (31, 32, 64, 2, NULL, '0', '2019-05-23 11:41:44', '1', '2019-05-23 11:42:22', '1');
INSERT INTO `ci_task_detail` VALUES (32, 33, 64, 2, NULL, '0', '2019-05-23 13:50:03', '1', '2019-05-23 13:50:40', '1');
INSERT INTO `ci_task_detail` VALUES (33, 34, 64, 2, NULL, '0', '2019-05-23 15:06:33', '1', '2019-05-23 15:07:10', '1');
INSERT INTO `ci_task_detail` VALUES (34, 35, 64, 0, NULL, '0', '2019-05-23 15:35:45', '1', '2019-05-23 15:35:45', '1');
INSERT INTO `ci_task_detail` VALUES (35, 36, 64, 2, NULL, '0', '2019-05-23 16:29:33', '1', '2019-05-23 16:30:19', '1');
INSERT INTO `ci_task_detail` VALUES (36, 37, 64, 2, NULL, '0', '2019-05-23 18:05:42', '1', '2019-05-23 18:06:24', '1');
INSERT INTO `ci_task_detail` VALUES (37, 38, 64, 0, NULL, '0', '2019-05-23 18:33:55', '1', '2019-05-23 18:33:55', '1');
INSERT INTO `ci_task_detail` VALUES (38, 39, 64, 0, NULL, '0', '2019-05-23 18:43:27', '1', '2019-05-23 18:43:27', '1');
INSERT INTO `ci_task_detail` VALUES (39, 40, 64, 0, NULL, '0', '2019-05-23 18:50:06', '1', '2019-05-23 18:50:06', '1');
INSERT INTO `ci_task_detail` VALUES (40, 41, 64, 0, NULL, '0', '2019-05-23 18:52:20', '1', '2019-05-23 18:52:20', '1');
INSERT INTO `ci_task_detail` VALUES (41, 42, 64, 0, NULL, '0', '2019-05-23 19:08:13', '1', '2019-05-23 19:08:13', '1');
INSERT INTO `ci_task_detail` VALUES (42, 43, 64, 0, NULL, '0', '2019-05-24 09:10:30', '1', '2019-05-24 09:10:30', '1');
INSERT INTO `ci_task_detail` VALUES (43, 44, 64, 0, NULL, '0', '2019-05-24 09:15:49', '1', '2019-05-24 09:15:49', '1');
INSERT INTO `ci_task_detail` VALUES (44, 45, 64, 0, NULL, '0', '2019-05-24 09:45:21', '1', '2019-05-24 09:45:21', '1');
INSERT INTO `ci_task_detail` VALUES (45, 46, 64, 0, NULL, '0', '2019-05-24 10:20:32', '1', '2019-05-24 10:20:32', '1');
INSERT INTO `ci_task_detail` VALUES (46, 47, 64, 3, NULL, '0', '2019-05-24 10:30:23', '1', '2019-05-24 10:31:07', '1');
INSERT INTO `ci_task_detail` VALUES (47, 48, 64, 3, NULL, '0', '2019-05-24 10:40:01', '1', '2019-05-24 10:40:46', '1');
INSERT INTO `ci_task_detail` VALUES (48, 49, 64, 3, NULL, '0', '2019-05-24 10:46:40', '1', '2019-05-24 10:47:23', '1');
INSERT INTO `ci_task_detail` VALUES (49, 50, 64, 3, NULL, '0', '2019-05-24 10:48:32', '1', '2019-05-24 10:49:14', '1');
INSERT INTO `ci_task_detail` VALUES (50, 51, 64, 2, NULL, '0', '2019-05-24 10:50:59', '1', '2019-05-24 10:51:47', '1');
INSERT INTO `ci_task_detail` VALUES (51, 52, 64, 3, NULL, '0', '2019-05-24 11:17:56', '1', '2019-05-24 11:19:06', '1');
INSERT INTO `ci_task_detail` VALUES (52, 53, 64, 3, NULL, '0', '2019-05-24 11:19:59', '1', '2019-05-24 11:20:40', '1');
INSERT INTO `ci_task_detail` VALUES (53, 54, 64, 3, NULL, '0', '2019-05-24 11:22:10', '1', '2019-05-24 11:26:17', '1');
INSERT INTO `ci_task_detail` VALUES (54, 55, 64, 0, NULL, '0', '2019-05-24 11:28:37', '1', '2019-05-24 11:28:37', '1');
INSERT INTO `ci_task_detail` VALUES (55, 56, 64, 3, NULL, '0', '2019-05-24 11:29:15', '1', '2019-05-24 11:34:27', '1');
INSERT INTO `ci_task_detail` VALUES (56, 57, 64, 3, NULL, '0', '2019-05-24 11:40:12', '1', '2019-05-24 11:40:55', '1');
INSERT INTO `ci_task_detail` VALUES (57, 58, 64, 3, NULL, '0', '2019-05-24 11:44:21', '1', '2019-05-24 11:44:57', '1');
INSERT INTO `ci_task_detail` VALUES (58, 59, 64, 3, NULL, '0', '2019-05-24 14:39:51', '1', '2019-05-24 14:41:43', '1');
INSERT INTO `ci_task_detail` VALUES (59, 60, 64, 2, NULL, '0', '2019-05-24 14:47:44', '1', '2019-05-24 14:48:20', '1');
INSERT INTO `ci_task_detail` VALUES (60, 61, 64, 2, NULL, '0', '2019-05-24 14:51:37', '1', '2019-05-24 14:52:17', '1');
INSERT INTO `ci_task_detail` VALUES (61, 62, 64, 2, NULL, '0', '2019-05-24 15:17:36', '1', '2019-05-24 15:18:17', '1');
INSERT INTO `ci_task_detail` VALUES (62, 63, 64, 0, NULL, '0', '2019-05-24 15:54:23', '1', '2019-05-24 15:54:23', '1');
INSERT INTO `ci_task_detail` VALUES (63, 64, 64, 0, NULL, '0', '2019-05-24 15:54:33', '1', '2019-05-24 15:54:33', '1');
INSERT INTO `ci_task_detail` VALUES (64, 65, 64, 0, NULL, '0', '2019-05-24 15:56:16', '1', '2019-05-24 15:56:16', '1');
INSERT INTO `ci_task_detail` VALUES (65, 66, 64, 0, NULL, '0', '2019-05-24 15:58:54', '1', '2019-05-24 15:58:54', '1');
INSERT INTO `ci_task_detail` VALUES (66, 67, 64, 2, NULL, '0', '2019-05-24 16:17:35', '1', '2019-05-24 16:18:26', '1');
INSERT INTO `ci_task_detail` VALUES (67, 68, 64, 2, NULL, '0', '2019-05-24 16:19:38', '1', '2019-05-24 16:20:27', '1');
INSERT INTO `ci_task_detail` VALUES (68, 69, 64, 2, NULL, '0', '2019-05-24 16:23:35', '1', '2019-05-24 16:24:11', '1');
INSERT INTO `ci_task_detail` VALUES (69, 70, 64, 2, NULL, '0', '2019-05-24 16:24:30', '1', '2019-05-24 16:25:05', '1');
INSERT INTO `ci_task_detail` VALUES (70, 71, 64, 2, NULL, '0', '2019-05-24 16:25:33', '1', '2019-05-24 16:26:10', '1');
INSERT INTO `ci_task_detail` VALUES (71, 72, 64, 2, NULL, '0', '2019-05-24 16:31:52', '1', '2019-05-24 16:32:57', '1');
INSERT INTO `ci_task_detail` VALUES (72, 73, 64, 2, NULL, '0', '2019-05-24 16:33:52', '1', '2019-05-24 16:34:28', '1');
INSERT INTO `ci_task_detail` VALUES (73, 74, 64, 2, NULL, '0', '2019-05-24 16:34:55', '1', '2019-05-24 16:36:59', '1');
INSERT INTO `ci_task_detail` VALUES (74, 75, 64, 2, NULL, '0', '2019-05-24 16:37:26', '1', '2019-05-24 16:43:02', '1');
INSERT INTO `ci_task_detail` VALUES (75, 76, 64, 2, NULL, '0', '2019-05-24 16:44:03', '1', '2019-05-24 16:44:42', '1');
INSERT INTO `ci_task_detail` VALUES (76, 77, 64, 2, NULL, '0', '2019-05-24 16:45:35', '1', '2019-05-24 16:46:10', '1');
INSERT INTO `ci_task_detail` VALUES (77, 78, 64, 2, NULL, '0', '2019-05-24 16:46:30', '1', '2019-05-24 16:47:08', '1');
INSERT INTO `ci_task_detail` VALUES (78, 79, 64, 2, NULL, '0', '2019-05-24 16:47:29', '1', '2019-05-24 16:48:06', '1');
INSERT INTO `ci_task_detail` VALUES (79, 80, 64, 0, NULL, '0', '2019-05-24 16:50:57', '1', '2019-05-24 16:50:57', '1');
INSERT INTO `ci_task_detail` VALUES (80, 81, 64, 2, NULL, '0', '2019-05-24 16:52:11', '1', '2019-05-24 16:52:47', '1');
INSERT INTO `ci_task_detail` VALUES (81, 82, 64, 2, NULL, '0', '2019-05-24 16:52:56', '1', '2019-05-24 16:53:33', '1');
INSERT INTO `ci_task_detail` VALUES (82, 83, 64, 2, NULL, '0', '2019-05-24 16:53:45', '1', '2019-05-24 16:54:22', '1');
INSERT INTO `ci_task_detail` VALUES (83, 84, 64, 2, NULL, '0', '2019-05-24 16:54:31', '1', '2019-05-24 16:55:06', '1');
INSERT INTO `ci_task_detail` VALUES (84, 85, 64, 2, NULL, '0', '2019-05-24 16:55:50', '1', '2019-05-24 16:56:29', '1');
INSERT INTO `ci_task_detail` VALUES (85, 86, 64, 2, NULL, '0', '2019-05-24 16:56:47', '1', '2019-05-24 16:57:26', '1');
INSERT INTO `ci_task_detail` VALUES (86, 87, 64, 2, NULL, '0', '2019-05-24 17:00:24', '1', '2019-05-24 17:01:00', '1');
INSERT INTO `ci_task_detail` VALUES (87, 88, 64, 0, NULL, '0', '2019-05-24 17:04:57', '1', '2019-05-24 17:04:57', '1');
INSERT INTO `ci_task_detail` VALUES (88, 89, 64, 0, NULL, '0', '2019-05-24 17:06:32', '1', '2019-05-24 17:06:32', '1');
INSERT INTO `ci_task_detail` VALUES (89, 90, 64, 2, NULL, '0', '2019-05-24 17:08:18', '1', '2019-05-24 17:08:54', '1');
INSERT INTO `ci_task_detail` VALUES (90, 91, 64, 2, NULL, '0', '2019-05-24 17:11:40', '1', '2019-05-24 17:12:14', '1');
INSERT INTO `ci_task_detail` VALUES (91, 92, 64, 0, NULL, '0', '2019-05-24 17:27:20', '1', '2019-05-24 17:27:20', '1');
INSERT INTO `ci_task_detail` VALUES (92, 93, 64, 2, NULL, '0', '2019-05-24 17:30:53', '1', '2019-05-24 17:31:31', '1');
INSERT INTO `ci_task_detail` VALUES (93, 94, 64, 2, NULL, '0', '2019-05-24 17:31:48', '1', '2019-05-24 17:32:25', '1');
INSERT INTO `ci_task_detail` VALUES (94, 95, 64, 2, NULL, '0', '2019-05-24 18:17:25', '1', '2019-05-24 18:18:07', '1');
INSERT INTO `ci_task_detail` VALUES (95, 96, 64, 2, NULL, '0', '2019-05-24 18:18:20', '1', '2019-05-24 18:18:59', '1');
INSERT INTO `ci_task_detail` VALUES (96, 97, 64, 2, NULL, '0', '2019-05-24 18:51:38', '1', '2019-05-24 18:52:29', '1');
COMMIT;

-- ----------------------------
-- Table structure for ci_trigger
-- ----------------------------
DROP TABLE IF EXISTS `ci_trigger`;
CREATE TABLE `ci_trigger` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL COMMENT 'git项目id',
  `branch_name` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '分支名',
  `enable` int(11) DEFAULT NULL,
  `tar_type` int(11) DEFAULT NULL COMMENT '类型:1tar,2jar,3自定义',
  `command` text COLLATE utf8_bin COMMENT '自定义命令',
  `remark` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `del_flag` char(1) COLLATE utf8_bin DEFAULT '0',
  `create_date` datetime DEFAULT NULL,
  `create_by` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `update_by` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='自动部署的 钩子 配置, 通过项目名和分支名,得到环境id';

-- ----------------------------
-- Records of ci_trigger
-- ----------------------------
BEGIN;
INSERT INTO `ci_trigger` VALUES (1, 1, 'master', 1, 2, NULL, NULL, '0', '2019-05-17 17:25:12', '1', '2019-05-17 17:25:12', '1');
INSERT INTO `ci_trigger` VALUES (2, 2, 'hwj_test_ci', 1, 1, NULL, NULL, '0', '2019-05-20 17:53:09', '1', '2019-05-20 17:53:09', '1');
INSERT INTO `ci_trigger` VALUES (3, 4, 'master', 1, 1, NULL, NULL, '0', '2019-05-22 16:31:49', '1', '2019-05-22 16:31:49', '1');
INSERT INTO `ci_trigger` VALUES (4, 5, 'master', 1, 1, NULL, NULL, '0', '2019-05-22 16:34:16', '1', '2019-05-22 16:34:16', '1');
COMMIT;

-- ----------------------------
-- Table structure for ci_trigger_detail
-- ----------------------------
DROP TABLE IF EXISTS `ci_trigger_detail`;
CREATE TABLE `ci_trigger_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `trigger_id` int(11) NOT NULL COMMENT '钩子id',
  `instance_id` int(11) NOT NULL COMMENT '实例id',
  `del_flag` char(1) COLLATE utf8_bin DEFAULT '0',
  `create_date` datetime DEFAULT NULL,
  `create_by` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `update_by` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='一个钩子 对应 多个实例';

-- ----------------------------
-- Records of ci_trigger_detail
-- ----------------------------
BEGIN;
INSERT INTO `ci_trigger_detail` VALUES (1, 1, 62, '1', '2019-05-17 17:26:07', '1', '2019-05-17 17:26:07', '1');
INSERT INTO `ci_trigger_detail` VALUES (2, 2, 63, '0', '2019-05-20 17:53:49', '1', '2019-05-20 17:53:49', NULL);
INSERT INTO `ci_trigger_detail` VALUES (3, 3, 64, '0', '2019-05-22 16:41:01', '1', '2019-05-22 16:41:01', '1');
INSERT INTO `ci_trigger_detail` VALUES (4, 4, 64, '0', '2019-05-22 16:41:01', '1', '2019-05-22 16:41:01', '1');
COMMIT;

-- ----------------------------
-- Table structure for sys_department
-- ----------------------------
DROP TABLE IF EXISTS `sys_department`;
CREATE TABLE `sys_department` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '部门名称',
  `duty_user_id` int(11) DEFAULT NULL COMMENT '负责人用户ID',
  `enable` int(1) DEFAULT '1' COMMENT '启用状态（0:禁止/1:启用）',
  `remark` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  `update_date` datetime DEFAULT NULL,
  `del_flag` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `duty_user_id` (`duty_user_id`),
  CONSTRAINT `sys_department_ibfk_1` FOREIGN KEY (`duty_user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='部门信息表';

-- ----------------------------
-- Records of sys_department
-- ----------------------------
BEGIN;
INSERT INTO `sys_department` VALUES (1, '研发1部', 2, 1, '研发1部', '2018-09-19 09:07:37', 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_group
-- ----------------------------
DROP TABLE IF EXISTS `sys_group`;
CREATE TABLE `sys_group` (
  `id` int(11) NOT NULL,
  `name` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '用户分租(customer）名，与displayName灵活应用',
  `display_name` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '用户分租(customer）展示名',
  `type` int(1) DEFAULT '0' COMMENT '用户分组类型（预留）',
  `enable` int(1) NOT NULL DEFAULT '1' COMMENT '用户组启用状态（0:禁用/1:启用）',
  `status` int(1) NOT NULL DEFAULT '0' COMMENT '用户组状态（预留）',
  `create_by` int(11) NOT NULL,
  `create_date` datetime NOT NULL,
  `update_by` int(11) NOT NULL,
  `update_date` datetime NOT NULL,
  `del_flag` int(1) NOT NULL COMMENT '删除状态（0:正常/1:删除）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='通用系统用户组表';

-- ----------------------------
-- Table structure for sys_group_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_group_menu`;
CREATE TABLE `sys_group_menu` (
  `id` int(11) NOT NULL,
  `group_id` int(11) DEFAULT NULL,
  `menu_id` int(11) DEFAULT NULL,
  `create_by` int(11) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='通用系统group-menu中间表';

-- ----------------------------
-- Table structure for sys_group_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_group_role`;
CREATE TABLE `sys_group_role` (
  `id` int(11) NOT NULL,
  `group_id` int(11) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  `create_by` int(11) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='通用系统group-role中间表';

-- ----------------------------
-- Table structure for sys_group_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_group_user`;
CREATE TABLE `sys_group_user` (
  `id` int(11) NOT NULL,
  `group_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `create_by` int(11) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='通用系统group-user中间表';

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` int(11) NOT NULL,
  `name` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '用户角色名，与displayName灵活应用',
  `display_name` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '用户角色展示名',
  `type` int(1) DEFAULT NULL COMMENT '菜单类型（预留）',
  `enable` int(1) NOT NULL DEFAULT '1' COMMENT '用户角色启用状态（0:禁用/1:启用）',
  `status` int(1) NOT NULL DEFAULT '0' COMMENT '用户角色状态（预留）',
  `parent_id` int(11) NOT NULL COMMENT '父级菜单ID',
  `parent_ids` varchar(500) COLLATE utf8_bin NOT NULL COMMENT '树形父级菜单ID列表（如：1,11,22）',
  `permission` varchar(500) COLLATE utf8_bin NOT NULL COMMENT '权限标识（如：sys:user:edit,sys:user:view），用于如shiro-aop方法及权限校验',
  `access_uri` varchar(500) COLLATE utf8_bin NOT NULL COMMENT '访问控制URI后缀（如：/sys/user/list）',
  `icon` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '图标',
  `create_by` int(11) NOT NULL,
  `create_date` datetime NOT NULL,
  `update_by` int(11) NOT NULL,
  `update_date` datetime NOT NULL,
  `del_flag` int(1) NOT NULL COMMENT '删除状态（0:正常/1:删除）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='通用系统菜单（权限）表';

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` int(11) NOT NULL,
  `name` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '用户角色名，与displayName灵活应用',
  `display_name` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '用户角色展示名',
  `type` int(1) DEFAULT NULL COMMENT '用户角色类型（预留）',
  `enable` int(1) NOT NULL DEFAULT '1' COMMENT '用户角色启用状态（0:禁用/1:启用）',
  `status` int(1) NOT NULL DEFAULT '0' COMMENT '用户角色状态（预留）',
  `create_by` int(11) NOT NULL,
  `create_date` datetime NOT NULL,
  `update_by` int(11) NOT NULL,
  `update_date` datetime NOT NULL,
  `del_flag` int(1) NOT NULL COMMENT '删除状态（0:正常/1:删除）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='通用系统角色表';

-- ----------------------------
-- Table structure for sys_role_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_user`;
CREATE TABLE `sys_role_user` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  `create_by` int(11) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='通用系统user-role中间表';

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `depart_id` int(11) NOT NULL COMMENT '部门ID',
  `user_name` varchar(32) COLLATE utf8_bin NOT NULL,
  `display_name` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '部门名称',
  `password` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '密文密码',
  `user_type` int(1) NOT NULL DEFAULT '2' COMMENT '用户类型（如：1:管理员/2:运维者）',
  `enable` int(1) NOT NULL DEFAULT '1' COMMENT '启用状态（0:禁止/1:启用）',
  `status` int(1) DEFAULT NULL COMMENT '用户状态（预留）',
  `email` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `phone` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `wechat_open_id` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `wechat_union_id` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `facebook_id` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `google_id` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `twitter_id` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `linkedin_id` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `alipay_id` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `github_id` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `aws_id` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `remark` varchar(500) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  `create_by` int(11) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_by` int(11) DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `del_flag` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `depart_id` (`depart_id`),
  CONSTRAINT `sys_user_ibfk_1` FOREIGN KEY (`depart_id`) REFERENCES `sys_department` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='通用系统用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` VALUES (1, 1, 'admin', '超级管理员', NULL, 1, 1, NULL, '983708408@qq.com', '18127968606', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '超级管理员账号', NULL, NULL, NULL, '2018-09-19 09:07:37', 0);
INSERT INTO `sys_user` VALUES (2, 1, 'sutao', 'Su Tao', NULL, 1, 1, NULL, '171429233@qq.com', '15999972409', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '操作账号Su Tao', NULL, NULL, NULL, '2018-09-20 09:49:34', 0);
INSERT INTO `sys_user` VALUES (3, 1, 'zhangzh', 'Zhang zhi hang', NULL, 1, 1, NULL, '1196930166@qq.com', '17762759397', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '操作账号Zhang zhi hang', NULL, NULL, NULL, '2018-09-20 09:49:34', 0);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
