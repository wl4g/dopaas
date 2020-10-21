<#assign subDomain = projectName?lower_case />
<#assign serverName = projectName?lower_case + '-server' />
<#assign clusterId = javaSpecs.genNextId()>
/*
 ${watermark}

 Generated From Server Type    : ${datasource.type}
 Generated From Server Version : ${datasource.dbversion}
 Generated From Host           : ${datasource.dbhost}:${datasource.dbport}
 Schema                        : ${datasource.databaseName}
 File Encoding                 : 65001
 Date: ${.now?string('yyyy-MM-dd hh:mm:ss')}
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict` (
  `key` varchar(128) COLLATE utf8_bin NOT NULL COMMENT 'key,唯一',
  `value` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '数据值',
  `label` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '标签名',
  `label_en` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '标签名(EN)',
  `type` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '类型',
  `themes` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '主题/样式',
  `icon` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '图标',
  `sort` decimal(10,0) NOT NULL DEFAULT '50' COMMENT '排序（升序）',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '字典状态（1:均使用 | 2:仅后台使用 | 3:仅前端使用）释：如状态为`2`的字典的值不会返回给前端（登录后返回字典列表给前端缓存）',
  `enable` int(11) DEFAULT NULL,
  `create_by` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remark` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '备注信息',
  `del_flag` int(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`key`) USING BTREE,
  KEY `sys_dict_value` (`value`) USING BTREE,
  KEY `sys_dict_label` (`label`) USING BTREE,
  KEY `sys_dict_del_flag` (`del_flag`) USING BTREE,
  KEY `key` (`key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='字典表\r\ndict系统管理（界面）： key、type不可变（只能开发人员修改数据库，因key、type会在代码硬编码)\r\n只可修改 value、themes、icon、lable、sort、status、description';

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
BEGIN;
INSERT INTO `sys_dict` VALUES ('app_ns_type@dev', 'dev', '开发环境', 'Development Environment', 'app_ns_type', '', '', 10, 1, 1, '1', '2019-06-12 08:00:00', '1', '2020-06-03 15:07:15', '开发环境，用于开发者调试使用（Development environment）', 0);
INSERT INTO `sys_dict` VALUES ('app_ns_type@fat', 'fat', '测试环境', 'Testing Environment', 'app_ns_type', '', '', 20, 1, 1, '1', '2019-06-12 08:00:00', '1', '2020-06-03 15:08:43', '功能验收测试环境，用于软件测试使用（Feature Acceptance Test environment）', 0);
INSERT INTO `sys_dict` VALUES ('app_ns_type@pro', 'pro', '生产环境', 'Production Environment', 'app_ns_type', '', '', 40, 1, 1, '1', '2019-06-12 08:00:00', '1', '2020-06-03 15:14:10', '线上生产环境（Production environment）', 0);
INSERT INTO `sys_dict` VALUES ('app_ns_type@uat', 'uat', '验收环境', 'User Verify Environment', 'app_ns_type', '', '', 30, 1, 1, '1', '2019-06-12 08:00:00', '1', '2020-06-03 15:12:17', '用户验收测试环境，用于生产环境下的软件灰度测试使用（User Acceptance Test environment）', 0);
INSERT INTO `sys_dict` VALUES ('common_enable_status@disable', '0', '停用', 'Disable', 'common_enable_status', 'danger', NULL, 50, 1, 1, '1', '2019-08-13 15:10:32', '1', '2019-08-16 08:56:20', 'common_enable_status', 0);
INSERT INTO `sys_dict` VALUES ('common_enable_status@enable', '1', '启用', 'Enable', 'common_enable_status', '', NULL, 50, 1, 1, '1', '2019-08-13 15:10:32', '1', '2019-08-16 08:56:20', 'common_enable_status', 0);
INSERT INTO `sys_dict` VALUES ('ctl_switch_type@off', 'off', '关', 'off', 'switch_type', 'gray', NULL, 50, 1, 1, '1', '2019-08-13 15:10:32', '1', '2019-08-16 08:56:20', '控制开关（关）', 0);
INSERT INTO `sys_dict` VALUES ('ctl_switch_type@on', 'on', '开', 'on', 'switch_type', 'primary', NULL, 50, 1, 1, '1', '2019-08-13 15:10:32', '1', '2019-08-16 08:56:20', '控制开关（开）', 0);
INSERT INTO `sys_dict` VALUES ('doc_file_type@md', 'md', 'Md', 'Md', 'doc_file_type', NULL, NULL, 50, 1, 1, '1', '2020-01-14 14:51:04', '1', '2020-01-14 14:51:05', NULL, 0);
INSERT INTO `sys_dict` VALUES ('doc_file_type@txt', 'Txt', 'Txt', 'Txt', 'doc_file_type', NULL, NULL, 50, 1, 1, '1', '2020-01-14 14:51:04', '1', '2020-01-14 14:51:05', NULL, 0);
INSERT INTO `sys_dict` VALUES ('doc_lang_type@en_US', 'en_US', 'US English Edition', 'US English Edition', 'doc_lang_type', NULL, NULL, 50, 1, 1, '1', '2020-01-14 14:51:04', '1', '2020-01-14 14:51:05', NULL, 0);
INSERT INTO `sys_dict` VALUES ('doc_lang_type@ja_JP', 'ja_JP', '日陰勢', '日陰勢', 'doc_lang_type', NULL, NULL, 50, 1, 1, '1', '2020-01-14 14:51:04', '1', '2020-01-14 14:51:05', NULL, 0);
INSERT INTO `sys_dict` VALUES ('doc_lang_type@zh_CN', 'zh_CN', '简体中文版', '简体中文版', 'doc_lang_type', NULL, NULL, 50, 1, 1, '1', '2020-01-14 14:51:04', '1', '2020-01-14 14:51:05', NULL, 0);
INSERT INTO `sys_dict` VALUES ('doc_lang_type@zh_HK', 'zh_HK', '繁體中文版', '繁體中文版', 'doc_lang_type', NULL, NULL, 50, 1, 1, '1', '2020-01-14 14:51:04', '1', '2020-01-14 14:51:05', NULL, 0);
INSERT INTO `sys_dict` VALUES ('idc_provider@aliyun', '1', '阿里云', 'Aliyun Cloud', 'idc_provider', 'primary', '', 50, 1, 1, '1', '2019-08-13 15:10:32', '1', '2019-08-16 08:56:21', '', 0);
INSERT INTO `sys_dict` VALUES ('idc_provider@aws', '2', '亚马逊云', 'Aws Cloud', 'idc_provider', 'primary', '', 50, 1, 1, '1', '2019-08-13 15:10:32', '1', '2019-08-16 08:56:21', '', 0);
INSERT INTO `sys_dict` VALUES ('idc_provider@azure', '3', '微软云', 'Azure Cloud', 'idc_provider', 'primary', '', 50, 1, 1, '1', '2019-08-13 15:10:32', '1', '2019-08-16 08:56:21', '', 0);
INSERT INTO `sys_dict` VALUES ('idc_provider@baidu', '4', '百度云', 'Baidu Cloud', 'idc_provider', 'primary', '', 50, 1, 1, '1', '2019-08-13 15:10:32', '1', '2019-08-16 08:56:21', '', 0);
INSERT INTO `sys_dict` VALUES ('idc_provider@ctyun', '5', '天翼云', 'Ctyun Cloud', 'idc_provider', 'primary', '', 50, 1, 1, '1', '2019-08-13 15:10:32', '1', '2019-08-16 08:56:21', '', 0);
INSERT INTO `sys_dict` VALUES ('idc_provider@google', '6', 'Google云', 'Google Cloud', 'idc_provider', 'primary', '', 50, 1, 1, '1', '2019-08-13 15:10:32', '1', '2019-08-16 08:56:21', '', 0);
INSERT INTO `sys_dict` VALUES ('idc_provider@qingcloud', '7', '青云', 'Qing Cloud', 'idc_provider', 'primary', '', 50, 1, 1, '1', '2019-08-13 15:10:32', '1', '2019-08-16 08:56:21', NULL, 0);
INSERT INTO `sys_dict` VALUES ('idc_provider@tencent', '8', '腾讯云', 'Tencent Cloud', 'idc_provider', 'primary', '', 50, 1, 1, '1', '2019-08-13 15:10:32', '1', '2019-08-16 08:56:21', '', 0);
INSERT INTO `sys_dict` VALUES ('menu_classify_type@classifyA', 'classifyA', 'CI/CD', 'CI/CD', 'menu_classify_type', NULL, NULL, 50, 1, 1, '1', '2020-07-23 14:52:07', '1', '2020-07-24 12:06:25', '', 0);
INSERT INTO `sys_dict` VALUES ('menu_classify_type@classifyB', 'classifyB', '监控', 'Monitors', 'menu_classify_type', NULL, NULL, 50, 1, 1, '1', '2020-07-23 14:52:07', '1', '2020-07-24 12:06:00', '', 0);
INSERT INTO `sys_dict` VALUES ('menu_classify_type@classifyC', 'classifyC', '网络', 'Networks', 'menu_classify_type', NULL, NULL, 50, 1, 1, '1', '2020-07-23 14:52:07', '1', '2020-07-24 12:05:35', '', 0);
INSERT INTO `sys_dict` VALUES ('menu_classify_type@classifyD', 'classifyD', '安全', 'Securitys', 'menu_classify_type', NULL, NULL, 50, 1, 1, '1', '2020-07-24 12:08:49', '1', '2020-07-24 12:08:49', '', 0);
INSERT INTO `sys_dict` VALUES ('menu_classify_type@classifyE', 'classifyE', '基础', 'Foundations', 'menu_classify_type', NULL, NULL, 50, 1, 1, '1', '2020-07-23 14:52:07', '1', '2020-07-24 12:06:25', '', 0);
INSERT INTO `sys_dict` VALUES ('menu_classify_type@classifyF', 'classifyF', '存储', 'Storages', 'menu_classify_type', NULL, NULL, 50, 1, 1, '1', '2020-07-23 14:52:07', '1', '2020-07-24 12:06:25', '', 0);
INSERT INTO `sys_dict` VALUES ('menu_classify_type@classifyG', 'classifyG', '配置', 'Configurations', 'menu_classify_type', '', '', 50, 1, 1, '1', '2020-07-23 14:52:07', '1', '2020-07-24 12:06:25', '', 0);
INSERT INTO `sys_dict` VALUES ('menu_classify_type@classifyH', 'classifyH', '文档', 'Docs', 'menu_classify_type', '', '', 50, 1, 1, '1', '2020-07-23 14:52:07', '1', '2020-07-24 12:06:25', '', 0);
INSERT INTO `sys_dict` VALUES ('menu_type@button', '3', '按钮', 'Button', 'menu_type', NULL, NULL, 50, 1, 1, '1', '2019-12-17 14:21:38', '1', '2019-12-17 14:21:42', '', 0);
INSERT INTO `sys_dict` VALUES ('menu_type@dynamic', '2', '动态菜单', 'Dynamic Menu', 'menu_type', NULL, NULL, 50, 1, NULL, '1', '2019-12-17 14:21:38', '1', '2019-12-17 14:21:42', NULL, 0);
INSERT INTO `sys_dict` VALUES ('menu_type@static', '1', '静态菜单', 'Static Menu', 'menu_type', NULL, NULL, 50, 1, 1, '1', '2019-12-17 14:21:38', '1', '2019-12-17 14:21:42', '', 0);
INSERT INTO `sys_dict` VALUES ('relate_oper_type@gt', 'gt', '大于', 'gt', 'relate_oper_type', NULL, NULL, 50, 1, 1, '1', '2019-07-16 13:17:22', '1', '2019-08-16 08:56:22', '关系运算符（大于）', 0);
INSERT INTO `sys_dict` VALUES ('relate_oper_type@gte', 'gte', '大于等于', 'gte', 'relate_oper_type', NULL, NULL, 50, 1, 1, '1', '2019-07-16 13:17:22', '1', '2019-08-16 08:56:22', '关系运算符（大于等于）', 0);
INSERT INTO `sys_dict` VALUES ('relate_oper_type@lt', 'lt', '小于', 'lt', 'relate_oper_type', NULL, NULL, 50, 1, 1, '1', '2019-07-16 13:17:22', '1', '2019-08-16 08:56:22', '关系运算符（小于）', 0);
INSERT INTO `sys_dict` VALUES ('relate_oper_type@lte', 'lte', '小于等于', 'lte', 'relate_oper_type', NULL, NULL, 50, 1, 1, '1', '2019-07-16 13:17:22', '1', '2019-07-16 13:17:22', '关系运算符（小于等于）', 0);
INSERT INTO `sys_dict` VALUES ('sys_contact_type@AliyunSms', 'AliyunSms', 'Aliyun短信', 'AliyunSms', 'sys_contact_type', NULL, NULL, 50, 1, 1, '1', '2019-11-19 14:32:26', '1', '2019-11-19 14:32:27', NULL, 0);
INSERT INTO `sys_dict` VALUES ('sys_contact_type@AliyunVms', 'AliyunVms', 'Aliyun电话', 'AliyunVms', 'sys_contact_type', NULL, NULL, 50, 1, 1, '1', '2019-11-19 14:32:26', '1', '2019-11-19 14:32:27', NULL, 0);
INSERT INTO `sys_dict` VALUES ('sys_contact_type@dingtalk', 'Dingtalk', '钉钉', 'Dingtalk', 'sys_contact_type', NULL, NULL, 50, 1, 1, '1', '2019-11-19 14:32:26', '1', '2019-11-19 14:32:27', NULL, 0);
INSERT INTO `sys_dict` VALUES ('sys_contact_type@email', 'Mail', '邮件', 'Email', 'sys_contact_type', NULL, NULL, 50, 1, 1, '1', '2019-11-19 14:32:26', '1', '2019-11-19 14:32:27', NULL, 0);
INSERT INTO `sys_dict` VALUES ('sys_contact_type@facebook', 'Facebook', '脸书', 'Facebook', 'sys_contact_type', NULL, NULL, 50, 1, 1, '1', '2019-11-19 14:32:26', '1', '2019-11-19 14:32:27', NULL, 0);
INSERT INTO `sys_dict` VALUES ('sys_contact_type@twitter', 'Twitter', '推特', 'Twitter', 'sys_contact_type', NULL, NULL, 50, 1, 1, '1', '2019-11-19 14:32:26', '1', '2019-11-19 14:32:27', NULL, 0);
INSERT INTO `sys_dict` VALUES ('sys_contact_type@wechat', 'WechatMp', '微信', 'Wechat', 'sys_contact_type', NULL, NULL, 50, 1, 1, '1', '2019-11-19 14:32:26', '1', '2019-11-19 14:32:27', NULL, 0);
INSERT INTO `sys_dict` VALUES ('sys_group_type@company', '2', 'Company', 'Company', 'sys_group_type', NULL, NULL, 50, 1, 1, '1', '2019-11-19 14:32:26', '1', '2019-11-19 14:32:27', NULL, 0);
INSERT INTO `sys_dict` VALUES ('sys_group_type@department', '3', 'Department', 'Department', 'sys_group_type', NULL, NULL, 50, 1, 1, '1', '2019-11-19 14:32:26', '1', '2019-11-19 14:32:27', NULL, 0);
INSERT INTO `sys_dict` VALUES ('sys_group_type@park', '1', 'Park', 'Park', 'sys_group_type', NULL, NULL, 50, 1, 1, '1', '2019-11-19 14:32:26', '1', '2019-11-19 14:32:27', NULL, 0);
INSERT INTO `sys_dict` VALUES ('sys_menu_type@dynamic', '2', '动态菜单', 'DynamicMenu', 'sys_menu_type', '', '', 50, 1, 1, '1', '2019-12-11 14:49:36', '1', '2019-12-11 14:49:40', '动态菜单类型（sys_menu表）', 0);
INSERT INTO `sys_dict` VALUES ('sys_menu_type@static', '1', '静态菜单', 'StaticMenu', 'sys_menu_type', NULL, NULL, 50, 1, 1, '1', '2019-12-11 14:49:36', '1', '2019-12-11 14:49:40', '静态菜单类型（sys_menu表）', 0);
INSERT INTO `sys_dict` VALUES ('theme_type@danger', 'danger', '严重', 'danger', 'theme_type', 'danger', NULL, 50, 1, 1, '1', '2019-08-13 15:10:32', '1', '2019-08-16 08:56:20', '皮肤主题（严重）', 0);
INSERT INTO `sys_dict` VALUES ('theme_type@gray', 'gray', '灰色', 'gray', 'theme_type', 'gray', NULL, 50, 1, 1, '1', '2019-08-13 15:10:32', '1', '2019-08-16 08:56:21', '皮肤主题（灰色）', 0);
INSERT INTO `sys_dict` VALUES ('theme_type@primary', 'primary', '主要', 'primary', 'theme_type', 'primary', NULL, 50, 1, 1, '1', '2019-08-13 15:10:32', '1', '2019-08-16 08:56:21', '皮肤主题（主要）', 0);
INSERT INTO `sys_dict` VALUES ('theme_type@success', 'success', '成功', 'success', 'theme_type', 'success', NULL, 50, 1, 1, '1', '2019-08-13 15:10:32', '1', '2019-08-16 08:56:21', '皮肤主题（成功）', 0);
INSERT INTO `sys_dict` VALUES ('theme_type@warning', 'warning', '警告', 'warning', 'theme_type', 'warning', NULL, 50, 1, 1, '1', '2019-08-13 15:10:32', '1', '2019-08-16 08:56:21', '皮肤主题（警告）', 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_area
-- ----------------------------
DROP TABLE IF EXISTS `sys_area`;
CREATE TABLE `sys_area` (
  `id` bigint(25) NOT NULL,
  `parent_id` bigint(25) NOT NULL DEFAULT '0' COMMENT '父级ID',
  `name` varchar(50) NOT NULL COMMENT '名称',
  `short_name` varchar(50) NOT NULL COMMENT '简称',
  `longitude` float NOT NULL DEFAULT '0' COMMENT '经度',
  `latitude` float NOT NULL DEFAULT '0' COMMENT '纬度',
  `level` int(1) NOT NULL COMMENT '等级(1省/直辖市,2地级市,3区县,4镇/街道)',
  `sort` int(3) NOT NULL DEFAULT '1' COMMENT '排序',
  `status` int(1) NOT NULL DEFAULT '0' COMMENT '状态(0禁用/1启用)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_area
-- ----------------------------
BEGIN;
INSERT INTO `sys_area` VALUES (0, -1, '中国', '中国', 116.405, 39.905, 0, 1, 1);
INSERT INTO `sys_area` VALUES (1, -1, '美国', '美国', 116.405, 39.905, 0, 1, 1);
INSERT INTO `sys_area` VALUES (2, -1, '俄罗斯', '俄罗斯', 116.405, 39.905, 0, 1, 1);
INSERT INTO `sys_area` VALUES (3, -1, '英国', '英国', 116.405, 39.905, 0, 1, 1);
INSERT INTO `sys_area` VALUES (4, -1, '日本', '日本', 116.405, 39.905, 0, 1, 1);
INSERT INTO `sys_area` VALUES (5, -1, '德国', '德国', 116.405, 39.905, 0, 1, 1);
INSERT INTO `sys_area` VALUES (110000, 0, '北京', '北京', 116.405, 39.905, 1, 1, 1);
INSERT INTO `sys_area` VALUES (110100, 110000, '北京市', '北京', 116.405, 39.905, 2, 1, 1);
INSERT INTO `sys_area` VALUES (120000, 0, '天津', '天津', 117.19, 39.1256, 1, 2, 1);
INSERT INTO `sys_area` VALUES (120100, 120000, '天津市', '天津', 117.19, 39.1256, 2, 1, 1);
INSERT INTO `sys_area` VALUES (130000, 0, '河北省', '河北', 114.502, 38.0455, 1, 3, 1);
INSERT INTO `sys_area` VALUES (130100, 130000, '石家庄市', '石家庄', 114.502, 38.0455, 2, 8, 1);
INSERT INTO `sys_area` VALUES (130200, 130000, '唐山市', '唐山', 118.175, 39.6351, 2, 9, 1);
INSERT INTO `sys_area` VALUES (130300, 130000, '秦皇岛市', '秦皇岛', 119.587, 39.9425, 2, 7, 1);
INSERT INTO `sys_area` VALUES (130400, 130000, '邯郸市', '邯郸', 114.491, 36.6123, 2, 4, 1);
INSERT INTO `sys_area` VALUES (130500, 130000, '邢台市', '邢台', 114.509, 37.0682, 2, 10, 1);
INSERT INTO `sys_area` VALUES (130600, 130000, '保定市', '保定', 115.482, 38.8677, 2, 1, 1);
INSERT INTO `sys_area` VALUES (130700, 130000, '张家口市', '张家口', 114.884, 40.8119, 2, 11, 1);
INSERT INTO `sys_area` VALUES (130800, 130000, '承德市', '承德', 117.939, 40.9762, 2, 3, 1);
INSERT INTO `sys_area` VALUES (130900, 130000, '沧州市', '沧州', 116.857, 38.3106, 2, 2, 1);
INSERT INTO `sys_area` VALUES (131000, 130000, '廊坊市', '廊坊', 116.704, 39.5239, 2, 6, 1);
INSERT INTO `sys_area` VALUES (131100, 130000, '衡水市', '衡水', 115.666, 37.7351, 2, 5, 1);
INSERT INTO `sys_area` VALUES (140000, 0, '山西省', '山西', 112.549, 37.857, 1, 4, 1);
INSERT INTO `sys_area` VALUES (140100, 140000, '太原市', '太原', 112.549, 37.857, 2, 8, 1);
INSERT INTO `sys_area` VALUES (140200, 140000, '大同市', '大同', 113.295, 40.0903, 2, 2, 1);
INSERT INTO `sys_area` VALUES (140300, 140000, '阳泉市', '阳泉', 113.583, 37.8612, 2, 10, 1);
INSERT INTO `sys_area` VALUES (140400, 140000, '长治市', '长治', 113.114, 36.1911, 2, 1, 1);
INSERT INTO `sys_area` VALUES (140500, 140000, '晋城市', '晋城', 112.851, 35.4976, 2, 3, 1);
INSERT INTO `sys_area` VALUES (140600, 140000, '朔州市', '朔州', 112.433, 39.3313, 2, 7, 1);
INSERT INTO `sys_area` VALUES (140700, 140000, '晋中市', '晋中', 112.736, 37.6965, 2, 4, 1);
INSERT INTO `sys_area` VALUES (140800, 140000, '运城市', '运城', 111.004, 35.0228, 2, 11, 1);
INSERT INTO `sys_area` VALUES (140900, 140000, '忻州市', '忻州', 112.734, 38.4177, 2, 9, 1);
INSERT INTO `sys_area` VALUES (141000, 140000, '临汾市', '临汾', 111.518, 36.0841, 2, 5, 1);
INSERT INTO `sys_area` VALUES (141100, 140000, '吕梁市', '吕梁', 111.134, 37.5244, 2, 6, 1);
INSERT INTO `sys_area` VALUES (150000, 0, '内蒙古自治区', '内蒙古', 111.671, 40.8183, 1, 5, 1);
INSERT INTO `sys_area` VALUES (150100, 150000, '呼和浩特市', '呼和浩特', 111.671, 40.8183, 2, 6, 1);
INSERT INTO `sys_area` VALUES (150200, 150000, '包头市', '包头', 109.84, 40.6582, 2, 2, 1);
INSERT INTO `sys_area` VALUES (150300, 150000, '乌海市', '乌海', 106.826, 39.6737, 2, 9, 1);
INSERT INTO `sys_area` VALUES (150400, 150000, '赤峰市', '赤峰', 118.957, 42.2753, 2, 4, 1);
INSERT INTO `sys_area` VALUES (150500, 150000, '通辽市', '通辽', 122.263, 43.6174, 2, 8, 1);
INSERT INTO `sys_area` VALUES (150600, 150000, '鄂尔多斯市', '鄂尔多斯', 109.99, 39.8172, 2, 5, 1);
INSERT INTO `sys_area` VALUES (150700, 150000, '呼伦贝尔市', '呼伦贝尔', 119.758, 49.2153, 2, 7, 1);
INSERT INTO `sys_area` VALUES (150800, 150000, '巴彦淖尔市', '巴彦淖尔', 107.417, 40.7574, 2, 3, 1);
INSERT INTO `sys_area` VALUES (150900, 150000, '乌兰察布市', '乌兰察布', 113.115, 41.0341, 2, 10, 1);
INSERT INTO `sys_area` VALUES (152200, 150000, '兴安盟', '兴安', 122.07, 46.0763, 2, 12, 1);
INSERT INTO `sys_area` VALUES (152500, 150000, '锡林郭勒盟', '锡林郭勒', 116.091, 43.944, 2, 11, 1);
INSERT INTO `sys_area` VALUES (152900, 150000, '阿拉善盟', '阿拉善', 105.706, 38.8448, 2, 1, 1);
INSERT INTO `sys_area` VALUES (210000, 0, '辽宁省', '辽宁', 123.429, 41.7968, 1, 6, 1);
INSERT INTO `sys_area` VALUES (210100, 210000, '沈阳市', '沈阳', 123.429, 41.7968, 2, 12, 1);
INSERT INTO `sys_area` VALUES (210200, 210000, '大连市', '大连', 121.619, 38.9146, 2, 4, 1);
INSERT INTO `sys_area` VALUES (210300, 210000, '鞍山市', '鞍山', 122.996, 41.1106, 2, 1, 1);
INSERT INTO `sys_area` VALUES (210400, 210000, '抚顺市', '抚顺', 123.921, 41.876, 2, 6, 1);
INSERT INTO `sys_area` VALUES (210500, 210000, '本溪市', '本溪', 123.771, 41.2979, 2, 2, 1);
INSERT INTO `sys_area` VALUES (210600, 210000, '丹东市', '丹东', 124.383, 40.1243, 2, 5, 1);
INSERT INTO `sys_area` VALUES (210700, 210000, '锦州市', '锦州', 121.136, 41.1193, 2, 9, 1);
INSERT INTO `sys_area` VALUES (210800, 210000, '营口市', '营口', 122.235, 40.6674, 2, 14, 1);
INSERT INTO `sys_area` VALUES (210900, 210000, '阜新市', '阜新', 121.649, 42.0118, 2, 7, 1);
INSERT INTO `sys_area` VALUES (211000, 210000, '辽阳市', '辽阳', 123.182, 41.2694, 2, 10, 1);
INSERT INTO `sys_area` VALUES (211100, 210000, '盘锦市', '盘锦', 122.07, 41.1245, 2, 11, 1);
INSERT INTO `sys_area` VALUES (211200, 210000, '铁岭市', '铁岭', 123.844, 42.2906, 2, 13, 1);
INSERT INTO `sys_area` VALUES (211300, 210000, '朝阳市', '朝阳', 120.451, 41.5768, 2, 3, 1);
INSERT INTO `sys_area` VALUES (211400, 210000, '葫芦岛市', '葫芦岛', 120.856, 40.7556, 2, 8, 1);
INSERT INTO `sys_area` VALUES (220000, 0, '吉林省', '吉林', 125.325, 43.8868, 1, 7, 1);
INSERT INTO `sys_area` VALUES (220100, 220000, '长春市', '长春', 125.325, 43.8868, 2, 3, 1);
INSERT INTO `sys_area` VALUES (220200, 220000, '吉林市', '吉林', 126.553, 43.8436, 2, 4, 1);
INSERT INTO `sys_area` VALUES (220300, 220000, '四平市', '四平', 124.371, 43.1703, 2, 6, 1);
INSERT INTO `sys_area` VALUES (220400, 220000, '辽源市', '辽源', 125.145, 42.9027, 2, 5, 1);
INSERT INTO `sys_area` VALUES (220500, 220000, '通化市', '通化', 125.937, 41.7212, 2, 8, 1);
INSERT INTO `sys_area` VALUES (220600, 220000, '白山市', '白山', 126.428, 41.9425, 2, 2, 1);
INSERT INTO `sys_area` VALUES (220700, 220000, '松原市', '松原', 124.824, 45.1182, 2, 7, 1);
INSERT INTO `sys_area` VALUES (220800, 220000, '白城市', '白城', 122.841, 45.619, 2, 1, 1);
INSERT INTO `sys_area` VALUES (222400, 220000, '延边朝鲜族自治州', '延边朝鲜族', 129.513, 42.9048, 2, 9, 1);
INSERT INTO `sys_area` VALUES (230000, 0, '黑龙江省', '黑龙江', 126.642, 45.757, 1, 8, 1);
INSERT INTO `sys_area` VALUES (230100, 230000, '哈尔滨市', '哈尔滨', 126.642, 45.757, 2, 3, 1);
INSERT INTO `sys_area` VALUES (230200, 230000, '齐齐哈尔市', '齐齐哈尔', 123.958, 47.3421, 2, 9, 1);
INSERT INTO `sys_area` VALUES (230300, 230000, '鸡西市', '鸡西', 130.976, 45.3, 2, 7, 1);
INSERT INTO `sys_area` VALUES (230400, 230000, '鹤岗市', '鹤岗', 130.277, 47.3321, 2, 4, 1);
INSERT INTO `sys_area` VALUES (230500, 230000, '双鸭山市', '双鸭山', 131.157, 46.6434, 2, 11, 1);
INSERT INTO `sys_area` VALUES (230600, 230000, '大庆市', '大庆', 125.113, 46.5907, 2, 1, 1);
INSERT INTO `sys_area` VALUES (230700, 230000, '伊春市', '伊春', 128.899, 47.7248, 2, 13, 1);
INSERT INTO `sys_area` VALUES (230800, 230000, '佳木斯市', '佳木斯', 130.362, 46.8096, 2, 6, 1);
INSERT INTO `sys_area` VALUES (230900, 230000, '七台河市', '七台河', 131.016, 45.7713, 2, 10, 1);
INSERT INTO `sys_area` VALUES (231000, 230000, '牡丹江市', '牡丹江', 129.619, 44.583, 2, 8, 1);
INSERT INTO `sys_area` VALUES (231100, 230000, '黑河市', '黑河', 127.499, 50.2496, 2, 5, 1);
INSERT INTO `sys_area` VALUES (231200, 230000, '绥化市', '绥化', 126.993, 46.6374, 2, 12, 1);
INSERT INTO `sys_area` VALUES (232700, 230000, '大兴安岭地区', '大兴安岭', 124.712, 52.3353, 2, 2, 1);
INSERT INTO `sys_area` VALUES (310000, 0, '上海', '上海', 121.473, 31.2317, 1, 9, 1);
INSERT INTO `sys_area` VALUES (310100, 310000, '上海市', '上海', 121.473, 31.2317, 2, 1, 1);
INSERT INTO `sys_area` VALUES (320000, 0, '江苏省', '江苏', 118.767, 32.0415, 1, 10, 1);
INSERT INTO `sys_area` VALUES (320100, 320000, '南京市', '南京', 118.767, 32.0415, 2, 4, 1);
INSERT INTO `sys_area` VALUES (320200, 320000, '无锡市', '无锡', 120.302, 31.5747, 2, 9, 1);
INSERT INTO `sys_area` VALUES (320300, 320000, '徐州市', '徐州', 117.185, 34.2618, 2, 10, 1);
INSERT INTO `sys_area` VALUES (320400, 320000, '常州市', '常州', 119.947, 31.7728, 2, 1, 1);
INSERT INTO `sys_area` VALUES (320500, 320000, '苏州市', '苏州', 120.62, 31.2994, 2, 7, 1);
INSERT INTO `sys_area` VALUES (320600, 320000, '南通市', '南通', 120.865, 32.0162, 2, 5, 1);
INSERT INTO `sys_area` VALUES (320700, 320000, '连云港市', '连云港', 119.179, 34.6, 2, 3, 1);
INSERT INTO `sys_area` VALUES (320800, 320000, '淮安市', '淮安', 119.021, 33.5975, 2, 2, 1);
INSERT INTO `sys_area` VALUES (320900, 320000, '盐城市', '盐城', 120.14, 33.3776, 2, 11, 1);
INSERT INTO `sys_area` VALUES (321000, 320000, '扬州市', '扬州', 119.421, 32.3932, 2, 12, 1);
INSERT INTO `sys_area` VALUES (321100, 320000, '镇江市', '镇江', 119.453, 32.2044, 2, 13, 1);
INSERT INTO `sys_area` VALUES (321200, 320000, '泰州市', '泰州', 119.915, 32.4849, 2, 8, 1);
INSERT INTO `sys_area` VALUES (321300, 320000, '宿迁市', '宿迁', 118.275, 33.963, 2, 6, 1);
INSERT INTO `sys_area` VALUES (330000, 0, '浙江省', '浙江', 120.154, 30.2875, 1, 11, 1);
INSERT INTO `sys_area` VALUES (330100, 330000, '杭州市', '杭州', 120.154, 30.2875, 2, 1, 1);
INSERT INTO `sys_area` VALUES (330200, 330000, '宁波市', '宁波', 121.55, 29.8684, 2, 6, 1);
INSERT INTO `sys_area` VALUES (330300, 330000, '温州市', '温州', 120.672, 28.0006, 2, 10, 1);
INSERT INTO `sys_area` VALUES (330400, 330000, '嘉兴市', '嘉兴', 120.751, 30.7627, 2, 3, 1);
INSERT INTO `sys_area` VALUES (330500, 330000, '湖州市', '湖州', 120.102, 30.8672, 2, 2, 1);
INSERT INTO `sys_area` VALUES (330600, 330000, '绍兴市', '绍兴', 120.582, 29.9971, 2, 8, 1);
INSERT INTO `sys_area` VALUES (330700, 330000, '金华市', '金华', 119.65, 29.0895, 2, 4, 1);
INSERT INTO `sys_area` VALUES (330800, 330000, '衢州市', '衢州', 118.873, 28.9417, 2, 7, 1);
INSERT INTO `sys_area` VALUES (330900, 330000, '舟山市', '舟山', 122.107, 30.016, 2, 11, 1);
INSERT INTO `sys_area` VALUES (331000, 330000, '台州市', '台州', 121.429, 28.6614, 2, 9, 1);
INSERT INTO `sys_area` VALUES (331100, 330000, '丽水市', '丽水', 119.922, 28.452, 2, 5, 1);
INSERT INTO `sys_area` VALUES (340000, 0, '安徽省', '安徽', 117.283, 31.8612, 1, 12, 1);
INSERT INTO `sys_area` VALUES (340100, 340000, '合肥市', '合肥', 117.283, 31.8612, 2, 7, 1);
INSERT INTO `sys_area` VALUES (340200, 340000, '芜湖市', '芜湖', 118.376, 31.3263, 2, 15, 1);
INSERT INTO `sys_area` VALUES (340300, 340000, '蚌埠市', '蚌埠', 117.363, 32.9397, 2, 2, 1);
INSERT INTO `sys_area` VALUES (340400, 340000, '淮南市', '淮南', 117.018, 32.6476, 2, 9, 1);
INSERT INTO `sys_area` VALUES (340500, 340000, '马鞍山市', '马鞍山', 118.508, 31.6894, 2, 12, 1);
INSERT INTO `sys_area` VALUES (340600, 340000, '淮北市', '淮北', 116.795, 33.9717, 2, 8, 1);
INSERT INTO `sys_area` VALUES (340700, 340000, '铜陵市', '铜陵', 117.817, 30.9299, 2, 14, 1);
INSERT INTO `sys_area` VALUES (340800, 340000, '安庆市', '安庆', 117.044, 30.5088, 2, 1, 1);
INSERT INTO `sys_area` VALUES (341000, 340000, '黄山市', '黄山', 118.317, 29.7092, 2, 10, 1);
INSERT INTO `sys_area` VALUES (341100, 340000, '滁州市', '滁州', 118.316, 32.3036, 2, 5, 1);
INSERT INTO `sys_area` VALUES (341200, 340000, '阜阳市', '阜阳', 115.82, 32.897, 2, 6, 1);
INSERT INTO `sys_area` VALUES (341300, 340000, '宿州市', '宿州', 116.984, 33.6339, 2, 13, 1);
INSERT INTO `sys_area` VALUES (341500, 340000, '六安市', '六安', 116.508, 31.7529, 2, 11, 1);
INSERT INTO `sys_area` VALUES (341600, 340000, '亳州市', '亳州', 115.783, 33.8693, 2, 3, 1);
INSERT INTO `sys_area` VALUES (341700, 340000, '池州市', '池州', 117.489, 30.656, 2, 4, 1);
INSERT INTO `sys_area` VALUES (341800, 340000, '宣城市', '宣城', 118.758, 30.9457, 2, 16, 1);
INSERT INTO `sys_area` VALUES (350000, 0, '福建省', '福建', 119.306, 26.0753, 1, 13, 1);
INSERT INTO `sys_area` VALUES (350100, 350000, '福州市', '福州', 119.306, 26.0753, 2, 1, 1);
INSERT INTO `sys_area` VALUES (350200, 350000, '厦门市', '厦门', 118.11, 24.4905, 2, 8, 1);
INSERT INTO `sys_area` VALUES (350300, 350000, '莆田市', '莆田', 119.008, 25.431, 2, 5, 1);
INSERT INTO `sys_area` VALUES (350400, 350000, '三明市', '三明', 117.635, 26.2654, 2, 7, 1);
INSERT INTO `sys_area` VALUES (350500, 350000, '泉州市', '泉州', 118.589, 24.9089, 2, 6, 1);
INSERT INTO `sys_area` VALUES (350600, 350000, '漳州市', '漳州', 117.662, 24.5109, 2, 9, 1);
INSERT INTO `sys_area` VALUES (350700, 350000, '南平市', '南平', 118.178, 26.6356, 2, 3, 1);
INSERT INTO `sys_area` VALUES (350800, 350000, '龙岩市', '龙岩', 117.03, 25.0916, 2, 2, 1);
INSERT INTO `sys_area` VALUES (350900, 350000, '宁德市', '宁德', 119.527, 26.6592, 2, 4, 1);
INSERT INTO `sys_area` VALUES (360000, 0, '江西省', '江西', 115.892, 28.6765, 1, 14, 1);
INSERT INTO `sys_area` VALUES (360100, 360000, '南昌市', '南昌', 115.892, 28.6765, 2, 6, 1);
INSERT INTO `sys_area` VALUES (360200, 360000, '景德镇市', '景德镇', 117.215, 29.2926, 2, 4, 1);
INSERT INTO `sys_area` VALUES (360300, 360000, '萍乡市', '萍乡', 113.852, 27.6229, 2, 7, 1);
INSERT INTO `sys_area` VALUES (360400, 360000, '九江市', '九江', 115.993, 29.712, 2, 5, 1);
INSERT INTO `sys_area` VALUES (360500, 360000, '新余市', '新余', 114.931, 27.8108, 2, 9, 1);
INSERT INTO `sys_area` VALUES (360600, 360000, '鹰潭市', '鹰潭', 117.034, 28.2386, 2, 11, 1);
INSERT INTO `sys_area` VALUES (360700, 360000, '赣州市', '赣州', 114.94, 25.851, 2, 2, 1);
INSERT INTO `sys_area` VALUES (360800, 360000, '吉安市', '吉安', 114.986, 27.1117, 2, 3, 1);
INSERT INTO `sys_area` VALUES (360900, 360000, '宜春市', '宜春', 114.391, 27.8043, 2, 10, 1);
INSERT INTO `sys_area` VALUES (361000, 360000, '抚州市', '抚州', 116.358, 27.9839, 2, 1, 1);
INSERT INTO `sys_area` VALUES (361100, 360000, '上饶市', '上饶', 117.971, 28.4444, 2, 8, 1);
INSERT INTO `sys_area` VALUES (370000, 0, '山东省', '山东', 117.001, 36.6758, 1, 15, 1);
INSERT INTO `sys_area` VALUES (370100, 370000, '济南市', '济南', 117.001, 36.6758, 2, 5, 1);
INSERT INTO `sys_area` VALUES (370200, 370000, '青岛市', '青岛', 120.355, 36.083, 2, 10, 1);
INSERT INTO `sys_area` VALUES (370300, 370000, '淄博市', '淄博', 118.048, 36.8149, 2, 17, 1);
INSERT INTO `sys_area` VALUES (370400, 370000, '枣庄市', '枣庄', 117.558, 34.8564, 2, 16, 1);
INSERT INTO `sys_area` VALUES (370500, 370000, '东营市', '东营', 118.665, 37.4346, 2, 3, 1);
INSERT INTO `sys_area` VALUES (370600, 370000, '烟台市', '烟台', 121.391, 37.5393, 2, 15, 1);
INSERT INTO `sys_area` VALUES (370700, 370000, '潍坊市', '潍坊', 119.107, 36.7093, 2, 13, 1);
INSERT INTO `sys_area` VALUES (370800, 370000, '济宁市', '济宁', 116.587, 35.4154, 2, 6, 1);
INSERT INTO `sys_area` VALUES (370900, 370000, '泰安市', '泰安', 117.129, 36.195, 2, 12, 1);
INSERT INTO `sys_area` VALUES (371000, 370000, '威海市', '威海', 122.116, 37.5097, 2, 14, 1);
INSERT INTO `sys_area` VALUES (371100, 370000, '日照市', '日照', 119.461, 35.4286, 2, 11, 1);
INSERT INTO `sys_area` VALUES (371200, 370000, '莱芜市', '莱芜', 117.678, 36.2144, 2, 7, 1);
INSERT INTO `sys_area` VALUES (371300, 370000, '临沂市', '临沂', 118.326, 35.0653, 2, 9, 1);
INSERT INTO `sys_area` VALUES (371400, 370000, '德州市', '德州', 116.307, 37.454, 2, 2, 1);
INSERT INTO `sys_area` VALUES (371500, 370000, '聊城市', '聊城', 115.98, 36.456, 2, 8, 1);
INSERT INTO `sys_area` VALUES (371600, 370000, '滨州市', '滨州', 118.017, 37.3835, 2, 1, 1);
INSERT INTO `sys_area` VALUES (371700, 370000, '菏泽市', '菏泽', 115.469, 35.2465, 2, 4, 1);
INSERT INTO `sys_area` VALUES (410000, 0, '河南省', '河南', 113.665, 34.758, 1, 16, 1);
INSERT INTO `sys_area` VALUES (410100, 410000, '郑州市', '郑州', 113.665, 34.758, 2, 16, 1);
INSERT INTO `sys_area` VALUES (410200, 410000, '开封市', '开封', 114.341, 34.7971, 2, 5, 1);
INSERT INTO `sys_area` VALUES (410300, 410000, '洛阳市', '洛阳', 112.434, 34.663, 2, 7, 1);
INSERT INTO `sys_area` VALUES (410400, 410000, '平顶山市', '平顶山', 113.308, 33.7352, 2, 9, 1);
INSERT INTO `sys_area` VALUES (410500, 410000, '安阳市', '安阳', 114.352, 36.1034, 2, 1, 1);
INSERT INTO `sys_area` VALUES (410600, 410000, '鹤壁市', '鹤壁', 114.295, 35.7482, 2, 2, 1);
INSERT INTO `sys_area` VALUES (410700, 410000, '新乡市', '新乡', 113.884, 35.3026, 2, 14, 1);
INSERT INTO `sys_area` VALUES (410800, 410000, '焦作市', '焦作', 113.238, 35.239, 2, 3, 1);
INSERT INTO `sys_area` VALUES (410881, 410000, '济源市', '济源', 112.59, 35.0904, 2, 4, 1);
INSERT INTO `sys_area` VALUES (410900, 410000, '濮阳市', '濮阳', 115.041, 35.7682, 2, 10, 1);
INSERT INTO `sys_area` VALUES (411000, 410000, '许昌市', '许昌', 113.826, 34.023, 2, 15, 1);
INSERT INTO `sys_area` VALUES (411100, 410000, '漯河市', '漯河', 114.026, 33.5759, 2, 6, 1);
INSERT INTO `sys_area` VALUES (411200, 410000, '三门峡市', '三门峡', 111.194, 34.7773, 2, 11, 1);
INSERT INTO `sys_area` VALUES (411300, 410000, '南阳市', '南阳', 112.541, 32.9991, 2, 8, 1);
INSERT INTO `sys_area` VALUES (411400, 410000, '商丘市', '商丘', 115.65, 34.4371, 2, 12, 1);
INSERT INTO `sys_area` VALUES (411500, 410000, '信阳市', '信阳', 114.075, 32.1233, 2, 13, 1);
INSERT INTO `sys_area` VALUES (411600, 410000, '周口市', '周口', 114.65, 33.6204, 2, 17, 1);
INSERT INTO `sys_area` VALUES (411700, 410000, '驻马店市', '驻马店', 114.025, 32.9802, 2, 18, 1);
INSERT INTO `sys_area` VALUES (420000, 0, '湖北省', '湖北', 114.299, 30.5844, 1, 17, 1);
INSERT INTO `sys_area` VALUES (420100, 420000, '武汉市', '武汉', 114.299, 30.5844, 2, 12, 1);
INSERT INTO `sys_area` VALUES (420200, 420000, '黄石市', '黄石', 115.077, 30.2201, 2, 4, 1);
INSERT INTO `sys_area` VALUES (420300, 420000, '十堰市', '十堰', 110.788, 32.6469, 2, 9, 1);
INSERT INTO `sys_area` VALUES (420500, 420000, '宜昌市', '宜昌', 111.291, 30.7026, 2, 17, 1);
INSERT INTO `sys_area` VALUES (420600, 420000, '襄阳市', '襄阳', 112.144, 32.0424, 2, 13, 1);
INSERT INTO `sys_area` VALUES (420700, 420000, '鄂州市', '鄂州', 114.891, 30.3965, 2, 2, 1);
INSERT INTO `sys_area` VALUES (420800, 420000, '荆门市', '荆门', 112.204, 31.0354, 2, 5, 1);
INSERT INTO `sys_area` VALUES (420900, 420000, '孝感市', '孝感', 113.927, 30.9264, 2, 16, 1);
INSERT INTO `sys_area` VALUES (421000, 420000, '荆州市', '荆州', 112.238, 30.3269, 2, 6, 1);
INSERT INTO `sys_area` VALUES (421100, 420000, '黄冈市', '黄冈', 114.879, 30.4477, 2, 3, 1);
INSERT INTO `sys_area` VALUES (421200, 420000, '咸宁市', '咸宁', 114.329, 29.8328, 2, 14, 1);
INSERT INTO `sys_area` VALUES (421300, 420000, '随州市', '随州', 113.374, 31.7175, 2, 10, 1);
INSERT INTO `sys_area` VALUES (422800, 420000, '恩施土家族苗族自治州', '恩施', 109.487, 30.2831, 2, 1, 1);
INSERT INTO `sys_area` VALUES (429004, 420000, '仙桃市', '仙桃', 113.454, 30.365, 2, 15, 1);
INSERT INTO `sys_area` VALUES (429005, 420000, '潜江市', '潜江', 112.897, 30.4212, 2, 7, 1);
INSERT INTO `sys_area` VALUES (429006, 420000, '天门市', '天门', 113.166, 30.6531, 2, 11, 1);
INSERT INTO `sys_area` VALUES (429021, 420000, '神农架林区', '神农架', 114.299, 30.5844, 2, 8, 1);
INSERT INTO `sys_area` VALUES (430000, 0, '湖南省', '湖南', 112.982, 28.1941, 1, 18, 1);
INSERT INTO `sys_area` VALUES (430100, 430000, '长沙市', '长沙', 112.982, 28.1941, 2, 2, 1);
INSERT INTO `sys_area` VALUES (430200, 430000, '株洲市', '株洲', 113.152, 27.8358, 2, 14, 1);
INSERT INTO `sys_area` VALUES (430300, 430000, '湘潭市', '湘潭', 112.944, 27.8297, 2, 8, 1);
INSERT INTO `sys_area` VALUES (430400, 430000, '衡阳市', '衡阳', 112.608, 26.9004, 2, 4, 1);
INSERT INTO `sys_area` VALUES (430500, 430000, '邵阳市', '邵阳', 111.469, 27.2378, 2, 7, 1);
INSERT INTO `sys_area` VALUES (430600, 430000, '岳阳市', '岳阳', 113.133, 29.3703, 2, 12, 1);
INSERT INTO `sys_area` VALUES (430700, 430000, '常德市', '常德', 111.691, 29.0402, 2, 1, 1);
INSERT INTO `sys_area` VALUES (430800, 430000, '张家界市', '张家界', 110.48, 29.1274, 2, 13, 1);
INSERT INTO `sys_area` VALUES (430900, 430000, '益阳市', '益阳', 112.355, 28.5701, 2, 10, 1);
INSERT INTO `sys_area` VALUES (431000, 430000, '郴州市', '郴州', 113.032, 25.7936, 2, 3, 1);
INSERT INTO `sys_area` VALUES (431100, 430000, '永州市', '永州', 111.608, 26.4345, 2, 11, 1);
INSERT INTO `sys_area` VALUES (431200, 430000, '怀化市', '怀化', 109.978, 27.5501, 2, 5, 1);
INSERT INTO `sys_area` VALUES (431300, 430000, '娄底市', '娄底', 112.008, 27.7281, 2, 6, 1);
INSERT INTO `sys_area` VALUES (433100, 430000, '湘西土家族苗族自治州', '湘西', 109.74, 28.3143, 2, 9, 1);
INSERT INTO `sys_area` VALUES (440000, 0, '广东省', '广东', 113.325, 23.1506, 1, 19, 1);
INSERT INTO `sys_area` VALUES (440100, 440000, '广州市', '广州', 113.281, 23.1252, 2, 2, 1);
INSERT INTO `sys_area` VALUES (440200, 440000, '韶关市', '韶关', 113.592, 24.8013, 2, 15, 1);
INSERT INTO `sys_area` VALUES (440300, 440000, '深圳市', '深圳', 114.086, 22.547, 2, 1, 1);
INSERT INTO `sys_area` VALUES (440400, 440000, '珠海市', '珠海', 113.554, 22.225, 2, 3, 1);
INSERT INTO `sys_area` VALUES (440500, 440000, '汕头市', '汕头', 116.708, 23.371, 2, 13, 1);
INSERT INTO `sys_area` VALUES (440600, 440000, '佛山市', '佛山', 113.123, 23.0288, 2, 5, 1);
INSERT INTO `sys_area` VALUES (440700, 440000, '江门市', '江门', 113.095, 22.5904, 2, 8, 1);
INSERT INTO `sys_area` VALUES (440800, 440000, '湛江市', '湛江', 110.365, 21.2749, 2, 19, 1);
INSERT INTO `sys_area` VALUES (440900, 440000, '茂名市', '茂名', 110.919, 21.6598, 2, 10, 1);
INSERT INTO `sys_area` VALUES (441200, 440000, '肇庆市', '肇庆', 112.473, 23.0515, 2, 20, 1);
INSERT INTO `sys_area` VALUES (441300, 440000, '惠州市', '惠州', 114.413, 23.0794, 2, 7, 1);
INSERT INTO `sys_area` VALUES (441400, 440000, '梅州市', '梅州', 116.118, 24.2991, 2, 11, 1);
INSERT INTO `sys_area` VALUES (441500, 440000, '汕尾市', '汕尾', 115.364, 22.7745, 2, 14, 1);
INSERT INTO `sys_area` VALUES (441600, 440000, '河源市', '河源', 114.698, 23.7463, 2, 6, 1);
INSERT INTO `sys_area` VALUES (441700, 440000, '阳江市', '阳江', 111.975, 21.8592, 2, 17, 1);
INSERT INTO `sys_area` VALUES (441800, 440000, '清远市', '清远', 113.051, 23.685, 2, 12, 1);
INSERT INTO `sys_area` VALUES (441900, 440000, '东莞市', '东莞', 113.746, 23.0462, 2, 4, 1);
INSERT INTO `sys_area` VALUES (442000, 440000, '中山市', '中山', 113.382, 22.5211, 2, 21, 1);
INSERT INTO `sys_area` VALUES (445100, 440000, '潮州市', '潮州', 116.632, 23.6617, 2, 16, 1);
INSERT INTO `sys_area` VALUES (445200, 440000, '揭阳市', '揭阳', 116.356, 23.5438, 2, 9, 1);
INSERT INTO `sys_area` VALUES (445300, 440000, '云浮市', '云浮', 112.044, 22.9298, 2, 18, 1);
INSERT INTO `sys_area` VALUES (450000, 0, '广西壮族自治区', '广西', 108.32, 22.824, 1, 20, 1);
INSERT INTO `sys_area` VALUES (450100, 450000, '南宁市', '南宁', 108.32, 22.824, 2, 11, 1);
INSERT INTO `sys_area` VALUES (450200, 450000, '柳州市', '柳州', 109.412, 24.3146, 2, 10, 1);
INSERT INTO `sys_area` VALUES (450300, 450000, '桂林市', '桂林', 110.299, 25.2742, 2, 6, 1);
INSERT INTO `sys_area` VALUES (450400, 450000, '梧州市', '梧州', 111.298, 23.4748, 2, 13, 1);
INSERT INTO `sys_area` VALUES (450500, 450000, '北海市', '北海', 109.119, 21.4733, 2, 1, 1);
INSERT INTO `sys_area` VALUES (450600, 450000, '防城港市', '防城港', 108.345, 21.6146, 2, 4, 1);
INSERT INTO `sys_area` VALUES (450700, 450000, '钦州市', '钦州', 108.624, 21.9671, 2, 12, 1);
INSERT INTO `sys_area` VALUES (450800, 450000, '贵港市', '贵港', 109.602, 23.0936, 2, 5, 1);
INSERT INTO `sys_area` VALUES (450900, 450000, '玉林市', '玉林', 110.154, 22.6314, 2, 14, 1);
INSERT INTO `sys_area` VALUES (451000, 450000, '百色市', '百色', 106.616, 23.8977, 2, 2, 1);
INSERT INTO `sys_area` VALUES (451100, 450000, '贺州市', '贺州', 111.552, 24.4141, 2, 8, 1);
INSERT INTO `sys_area` VALUES (451200, 450000, '河池市', '河池', 108.062, 24.6959, 2, 7, 1);
INSERT INTO `sys_area` VALUES (451300, 450000, '来宾市', '来宾', 109.23, 23.7338, 2, 9, 1);
INSERT INTO `sys_area` VALUES (451400, 450000, '崇左市', '崇左', 107.354, 22.4041, 2, 3, 1);
INSERT INTO `sys_area` VALUES (460000, 0, '海南省', '海南', 110.331, 20.032, 1, 21, 1);
INSERT INTO `sys_area` VALUES (460100, 460000, '海口市', '海口', 110.331, 20.032, 2, 8, 1);
INSERT INTO `sys_area` VALUES (460200, 460000, '三亚市', '三亚', 109.508, 18.2479, 2, 15, 1);
INSERT INTO `sys_area` VALUES (460300, 460000, '三沙市', '三沙', 112.349, 16.831, 2, 14, 1);
INSERT INTO `sys_area` VALUES (469001, 460000, '五指山市', '五指山', 109.517, 18.7769, 2, 19, 1);
INSERT INTO `sys_area` VALUES (469002, 460000, '琼海市', '琼海', 110.467, 19.246, 2, 12, 1);
INSERT INTO `sys_area` VALUES (469003, 460000, '儋州市', '儋州', 109.577, 19.5175, 2, 5, 1);
INSERT INTO `sys_area` VALUES (469005, 460000, '文昌市', '文昌', 110.754, 19.613, 2, 18, 1);
INSERT INTO `sys_area` VALUES (469006, 460000, '万宁市', '万宁', 110.389, 18.7962, 2, 17, 1);
INSERT INTO `sys_area` VALUES (469007, 460000, '东方市', '东方', 108.654, 19.102, 2, 7, 1);
INSERT INTO `sys_area` VALUES (469025, 460000, '定安县', '定安', 110.349, 19.685, 2, 6, 1);
INSERT INTO `sys_area` VALUES (469026, 460000, '屯昌县', '屯昌', 110.103, 19.3629, 2, 16, 1);
INSERT INTO `sys_area` VALUES (469027, 460000, '澄迈县', '澄迈', 110.007, 19.7371, 2, 4, 1);
INSERT INTO `sys_area` VALUES (469028, 460000, '临高县', '临高', 109.688, 19.9083, 2, 10, 1);
INSERT INTO `sys_area` VALUES (469030, 460000, '白沙黎族自治县', '白沙', 109.453, 19.2246, 2, 1, 1);
INSERT INTO `sys_area` VALUES (469031, 460000, '昌江黎族自治县', '昌江', 109.053, 19.261, 2, 3, 1);
INSERT INTO `sys_area` VALUES (469033, 460000, '乐东黎族自治县', '乐东', 109.175, 18.7476, 2, 9, 1);
INSERT INTO `sys_area` VALUES (469034, 460000, '陵水黎族自治县', '陵水', 110.037, 18.505, 2, 11, 1);
INSERT INTO `sys_area` VALUES (469035, 460000, '保亭黎族苗族自治县', '保亭', 109.702, 18.6364, 2, 2, 1);
INSERT INTO `sys_area` VALUES (469036, 460000, '琼中黎族苗族自治县', '琼中', 109.84, 19.0356, 2, 13, 1);
INSERT INTO `sys_area` VALUES (500000, 0, '重庆', '重庆', 106.505, 29.5332, 1, 22, 1);
INSERT INTO `sys_area` VALUES (500100, 500000, '重庆市', '重庆', 106.505, 29.5332, 2, 1, 1);
INSERT INTO `sys_area` VALUES (510000, 0, '四川省', '四川', 104.066, 30.6595, 1, 23, 1);
INSERT INTO `sys_area` VALUES (510100, 510000, '成都市', '成都', 104.066, 30.6595, 2, 3, 1);
INSERT INTO `sys_area` VALUES (510300, 510000, '自贡市', '自贡', 104.773, 29.3528, 2, 20, 1);
INSERT INTO `sys_area` VALUES (510400, 510000, '攀枝花市', '攀枝花', 101.716, 26.5804, 2, 16, 1);
INSERT INTO `sys_area` VALUES (510500, 510000, '泸州市', '泸州', 105.443, 28.8891, 2, 11, 1);
INSERT INTO `sys_area` VALUES (510600, 510000, '德阳市', '德阳', 104.399, 31.128, 2, 5, 1);
INSERT INTO `sys_area` VALUES (510700, 510000, '绵阳市', '绵阳', 104.742, 31.464, 2, 13, 1);
INSERT INTO `sys_area` VALUES (510800, 510000, '广元市', '广元', 105.83, 32.4337, 2, 8, 1);
INSERT INTO `sys_area` VALUES (510900, 510000, '遂宁市', '遂宁', 105.571, 30.5133, 2, 17, 1);
INSERT INTO `sys_area` VALUES (511000, 510000, '内江市', '内江', 105.066, 29.5871, 2, 15, 1);
INSERT INTO `sys_area` VALUES (511100, 510000, '乐山市', '乐山', 103.761, 29.582, 2, 9, 1);
INSERT INTO `sys_area` VALUES (511300, 510000, '南充市', '南充', 106.083, 30.7953, 2, 14, 1);
INSERT INTO `sys_area` VALUES (511400, 510000, '眉山市', '眉山', 103.832, 30.0483, 2, 12, 1);
INSERT INTO `sys_area` VALUES (511500, 510000, '宜宾市', '宜宾', 104.631, 28.7602, 2, 19, 1);
INSERT INTO `sys_area` VALUES (511600, 510000, '广安市', '广安', 106.633, 30.4564, 2, 7, 1);
INSERT INTO `sys_area` VALUES (511700, 510000, '达州市', '达州', 107.502, 31.2095, 2, 4, 1);
INSERT INTO `sys_area` VALUES (511800, 510000, '雅安市', '雅安', 103.001, 29.9877, 2, 18, 1);
INSERT INTO `sys_area` VALUES (511900, 510000, '巴中市', '巴中', 106.754, 31.8588, 2, 2, 1);
INSERT INTO `sys_area` VALUES (512000, 510000, '资阳市', '资阳', 104.642, 30.1222, 2, 21, 1);
INSERT INTO `sys_area` VALUES (513200, 510000, '阿坝藏族羌族自治州', '阿坝', 102.221, 31.8998, 2, 1, 1);
INSERT INTO `sys_area` VALUES (513300, 510000, '甘孜藏族自治州', '甘孜', 101.964, 30.0507, 2, 6, 1);
INSERT INTO `sys_area` VALUES (513400, 510000, '凉山彝族自治州', '凉山', 102.259, 27.8868, 2, 10, 1);
INSERT INTO `sys_area` VALUES (520000, 0, '贵州省', '贵州', 106.713, 26.5783, 1, 24, 1);
INSERT INTO `sys_area` VALUES (520100, 520000, '贵阳市', '贵阳', 106.713, 26.5783, 2, 3, 1);
INSERT INTO `sys_area` VALUES (520200, 520000, '六盘水市', '六盘水', 104.847, 26.5846, 2, 4, 1);
INSERT INTO `sys_area` VALUES (520300, 520000, '遵义市', '遵义', 106.937, 27.7066, 2, 9, 1);
INSERT INTO `sys_area` VALUES (520400, 520000, '安顺市', '安顺', 105.932, 26.2455, 2, 1, 1);
INSERT INTO `sys_area` VALUES (522200, 520000, '铜仁市', '铜仁', 109.192, 27.7183, 2, 8, 1);
INSERT INTO `sys_area` VALUES (522300, 520000, '黔西南布依族苗族自治州', '黔西南', 104.898, 25.0881, 2, 7, 1);
INSERT INTO `sys_area` VALUES (522400, 520000, '毕节市', '毕节', 105.285, 27.3017, 2, 2, 1);
INSERT INTO `sys_area` VALUES (522600, 520000, '黔东南苗族侗族自治州', '黔东南', 107.977, 26.5834, 2, 5, 1);
INSERT INTO `sys_area` VALUES (522700, 520000, '黔南布依族苗族自治州', '黔南', 107.517, 26.2582, 2, 6, 1);
INSERT INTO `sys_area` VALUES (530000, 0, '云南省', '云南', 102.712, 25.0406, 1, 25, 1);
INSERT INTO `sys_area` VALUES (530100, 530000, '昆明市', '昆明', 102.712, 25.0406, 2, 7, 1);
INSERT INTO `sys_area` VALUES (530300, 530000, '曲靖市', '曲靖', 103.798, 25.5016, 2, 12, 1);
INSERT INTO `sys_area` VALUES (530400, 530000, '玉溪市', '玉溪', 102.544, 24.3505, 2, 15, 1);
INSERT INTO `sys_area` VALUES (530500, 530000, '保山市', '保山', 99.1671, 25.1118, 2, 1, 1);
INSERT INTO `sys_area` VALUES (530600, 530000, '昭通市', '昭通', 103.717, 27.337, 2, 16, 1);
INSERT INTO `sys_area` VALUES (530700, 530000, '丽江市', '丽江', 100.233, 26.8721, 2, 8, 1);
INSERT INTO `sys_area` VALUES (530800, 530000, '普洱市', '普洱', 100.972, 22.7773, 2, 11, 1);
INSERT INTO `sys_area` VALUES (530900, 530000, '临沧市', '临沧', 100.087, 23.8866, 2, 9, 1);
INSERT INTO `sys_area` VALUES (532300, 530000, '楚雄彝族自治州', '楚雄', 101.546, 25.042, 2, 2, 1);
INSERT INTO `sys_area` VALUES (532500, 530000, '红河哈尼族彝族自治州', '红河', 103.384, 23.3668, 2, 6, 1);
INSERT INTO `sys_area` VALUES (532600, 530000, '文山壮族苗族自治州', '文山', 104.244, 23.3695, 2, 13, 1);
INSERT INTO `sys_area` VALUES (532800, 530000, '西双版纳傣族自治州', '西双版纳', 100.798, 22.0017, 2, 14, 1);
INSERT INTO `sys_area` VALUES (532900, 530000, '大理白族自治州', '大理', 100.226, 25.5894, 2, 3, 1);
INSERT INTO `sys_area` VALUES (533100, 530000, '德宏傣族景颇族自治州', '德宏', 98.5784, 24.4367, 2, 4, 1);
INSERT INTO `sys_area` VALUES (533300, 530000, '怒江傈僳族自治州', '怒江', 98.8543, 25.8509, 2, 10, 1);
INSERT INTO `sys_area` VALUES (533400, 530000, '迪庆藏族自治州', '迪庆', 99.7065, 27.8269, 2, 5, 1);
INSERT INTO `sys_area` VALUES (540000, 0, '西藏自治区', '西藏', 91.1322, 29.6604, 1, 26, 1);
INSERT INTO `sys_area` VALUES (540100, 540000, '拉萨市', '拉萨', 91.1322, 29.6604, 2, 3, 1);
INSERT INTO `sys_area` VALUES (542100, 540000, '昌都地区', '昌都', 97.1785, 31.1369, 2, 2, 1);
INSERT INTO `sys_area` VALUES (542200, 540000, '山南地区', '山南', 91.7665, 29.236, 2, 7, 1);
INSERT INTO `sys_area` VALUES (542300, 540000, '日喀则地区', '日喀则', 88.8851, 29.2675, 2, 6, 1);
INSERT INTO `sys_area` VALUES (542400, 540000, '那曲地区', '那曲', 92.0602, 31.476, 2, 5, 1);
INSERT INTO `sys_area` VALUES (542500, 540000, '阿里地区', '阿里', 80.1055, 32.5032, 2, 1, 1);
INSERT INTO `sys_area` VALUES (542600, 540000, '林芝地区', '林芝', 94.3624, 29.6547, 2, 4, 1);
INSERT INTO `sys_area` VALUES (610000, 0, '陕西省', '陕西', 108.948, 34.2632, 1, 27, 1);
INSERT INTO `sys_area` VALUES (610100, 610000, '西安市', '西安', 108.948, 34.2632, 2, 7, 1);
INSERT INTO `sys_area` VALUES (610200, 610000, '铜川市', '铜川', 108.98, 34.9166, 2, 5, 1);
INSERT INTO `sys_area` VALUES (610300, 610000, '宝鸡市', '宝鸡', 107.145, 34.3693, 2, 2, 1);
INSERT INTO `sys_area` VALUES (610400, 610000, '咸阳市', '咸阳', 108.705, 34.3334, 2, 8, 1);
INSERT INTO `sys_area` VALUES (610500, 610000, '渭南市', '渭南', 109.503, 34.4994, 2, 6, 1);
INSERT INTO `sys_area` VALUES (610600, 610000, '延安市', '延安', 109.491, 36.5965, 2, 9, 1);
INSERT INTO `sys_area` VALUES (610700, 610000, '汉中市', '汉中', 107.029, 33.0777, 2, 3, 1);
INSERT INTO `sys_area` VALUES (610800, 610000, '榆林市', '榆林', 109.741, 38.2902, 2, 10, 1);
INSERT INTO `sys_area` VALUES (610900, 610000, '安康市', '安康', 109.029, 32.6903, 2, 1, 1);
INSERT INTO `sys_area` VALUES (611000, 610000, '商洛市', '商洛', 109.94, 33.8683, 2, 4, 1);
INSERT INTO `sys_area` VALUES (620000, 0, '甘肃省', '甘肃', 103.824, 36.058, 1, 28, 1);
INSERT INTO `sys_area` VALUES (620100, 620000, '兰州市', '兰州', 103.824, 36.058, 2, 7, 1);
INSERT INTO `sys_area` VALUES (620200, 620000, '嘉峪关市', '嘉峪关', 98.2773, 39.7865, 2, 4, 1);
INSERT INTO `sys_area` VALUES (620300, 620000, '金昌市', '金昌', 102.188, 38.5142, 2, 5, 1);
INSERT INTO `sys_area` VALUES (620400, 620000, '白银市', '白银', 104.174, 36.5457, 2, 1, 1);
INSERT INTO `sys_area` VALUES (620500, 620000, '天水市', '天水', 105.725, 34.5785, 2, 12, 1);
INSERT INTO `sys_area` VALUES (620600, 620000, '武威市', '武威', 102.635, 37.93, 2, 13, 1);
INSERT INTO `sys_area` VALUES (620700, 620000, '张掖市', '张掖', 100.455, 38.9329, 2, 14, 1);
INSERT INTO `sys_area` VALUES (620800, 620000, '平凉市', '平凉', 106.685, 35.5428, 2, 10, 1);
INSERT INTO `sys_area` VALUES (620900, 620000, '酒泉市', '酒泉', 98.5108, 39.744, 2, 6, 1);
INSERT INTO `sys_area` VALUES (621000, 620000, '庆阳市', '庆阳', 107.638, 35.7342, 2, 11, 1);
INSERT INTO `sys_area` VALUES (621100, 620000, '定西市', '定西', 104.626, 35.5796, 2, 2, 1);
INSERT INTO `sys_area` VALUES (621200, 620000, '陇南市', '陇南', 104.929, 33.3886, 2, 9, 1);
INSERT INTO `sys_area` VALUES (622900, 620000, '临夏回族自治州', '临夏', 103.212, 35.5994, 2, 8, 1);
INSERT INTO `sys_area` VALUES (623000, 620000, '甘南藏族自治州', '甘南', 102.911, 34.9864, 2, 3, 1);
INSERT INTO `sys_area` VALUES (630000, 0, '青海省', '青海', 101.779, 36.6232, 1, 29, 1);
INSERT INTO `sys_area` VALUES (630100, 630000, '西宁市', '西宁', 101.779, 36.6232, 2, 7, 1);
INSERT INTO `sys_area` VALUES (632100, 630000, '海东市', '海东', 102.103, 36.5029, 2, 3, 1);
INSERT INTO `sys_area` VALUES (632200, 630000, '海北藏族自治州', '海北', 100.901, 36.9594, 2, 2, 1);
INSERT INTO `sys_area` VALUES (632300, 630000, '黄南藏族自治州', '黄南', 102.02, 35.5177, 2, 6, 1);
INSERT INTO `sys_area` VALUES (632500, 630000, '海南藏族自治州', '海南藏族', 100.62, 36.2804, 2, 4, 1);
INSERT INTO `sys_area` VALUES (632600, 630000, '果洛藏族自治州', '果洛', 100.242, 34.4736, 2, 1, 1);
INSERT INTO `sys_area` VALUES (632700, 630000, '玉树藏族自治州', '玉树', 97.0085, 33.004, 2, 8, 1);
INSERT INTO `sys_area` VALUES (632800, 630000, '海西蒙古族藏族自治州', '海西', 97.3708, 37.3747, 2, 5, 1);
INSERT INTO `sys_area` VALUES (640000, 0, '宁夏回族自治区', '宁夏', 106.278, 38.4664, 1, 30, 1);
INSERT INTO `sys_area` VALUES (640100, 640000, '银川市', '银川', 106.278, 38.4664, 2, 4, 1);
INSERT INTO `sys_area` VALUES (640200, 640000, '石嘴山市', '石嘴山', 106.376, 39.0133, 2, 2, 1);
INSERT INTO `sys_area` VALUES (640300, 640000, '吴忠市', '吴忠', 106.199, 37.9862, 2, 3, 1);
INSERT INTO `sys_area` VALUES (640400, 640000, '固原市', '固原', 106.285, 36.0046, 2, 1, 1);
INSERT INTO `sys_area` VALUES (640500, 640000, '中卫市', '中卫', 105.19, 37.5149, 2, 5, 1);
INSERT INTO `sys_area` VALUES (650000, 0, '新疆维吾尔自治区', '新疆', 87.6177, 43.7928, 1, 31, 1);
INSERT INTO `sys_area` VALUES (650100, 650000, '乌鲁木齐市', '乌鲁木齐', 87.6177, 43.7928, 2, 17, 1);
INSERT INTO `sys_area` VALUES (650200, 650000, '克拉玛依市', '克拉玛依', 84.8739, 45.5959, 2, 10, 1);
INSERT INTO `sys_area` VALUES (652100, 650000, '吐鲁番地区', '吐鲁番', 89.1841, 42.9476, 2, 14, 1);
INSERT INTO `sys_area` VALUES (652200, 650000, '哈密地区', '哈密', 93.5132, 42.8332, 2, 7, 1);
INSERT INTO `sys_area` VALUES (652300, 650000, '昌吉回族自治州', '昌吉', 87.304, 44.0146, 2, 6, 1);
INSERT INTO `sys_area` VALUES (652700, 650000, '博尔塔拉蒙古自治州', '博尔塔拉', 82.0748, 44.9033, 2, 5, 1);
INSERT INTO `sys_area` VALUES (652800, 650000, '巴音郭楞蒙古自治州', '巴音郭楞', 86.151, 41.7686, 2, 4, 1);
INSERT INTO `sys_area` VALUES (652900, 650000, '阿克苏地区', '阿克苏', 80.2651, 41.1707, 2, 1, 1);
INSERT INTO `sys_area` VALUES (653000, 650000, '克孜勒苏柯尔克孜自治州', '克孜勒苏柯尔克孜', 76.1728, 39.7134, 2, 11, 1);
INSERT INTO `sys_area` VALUES (653100, 650000, '喀什地区', '喀什', 75.9891, 39.4677, 2, 9, 1);
INSERT INTO `sys_area` VALUES (653200, 650000, '和田地区', '和田', 79.9253, 37.1107, 2, 8, 1);
INSERT INTO `sys_area` VALUES (654000, 650000, '伊犁哈萨克自治州', '伊犁', 81.3179, 43.9219, 2, 18, 1);
INSERT INTO `sys_area` VALUES (654200, 650000, '塔城地区', '塔城', 82.9857, 46.7463, 2, 13, 1);
INSERT INTO `sys_area` VALUES (654300, 650000, '阿勒泰地区', '阿勒泰', 88.1396, 47.8484, 2, 3, 1);
INSERT INTO `sys_area` VALUES (659001, 650000, '石河子市', '石河子', 86.0411, 44.3059, 2, 12, 1);
INSERT INTO `sys_area` VALUES (659002, 650000, '阿拉尔市', '阿拉尔', 81.2859, 40.5419, 2, 2, 1);
INSERT INTO `sys_area` VALUES (659003, 650000, '图木舒克市', '图木舒克', 79.078, 39.8673, 2, 15, 1);
INSERT INTO `sys_area` VALUES (659004, 650000, '五家渠市', '五家渠', 87.5269, 44.1674, 2, 16, 1);
INSERT INTO `sys_area` VALUES (710000, 0, '台湾', '台湾', 121.509, 25.0443, 1, 34, 1);
INSERT INTO `sys_area` VALUES (710100, 710000, '台北市', '台北', 121.509, 25.0443, 2, 12, 1);
INSERT INTO `sys_area` VALUES (710200, 710000, '高雄市', '高雄', 121.509, 25.0443, 2, 1, 1);
INSERT INTO `sys_area` VALUES (710300, 710000, '台南市', '台南', 121.509, 25.0443, 2, 14, 1);
INSERT INTO `sys_area` VALUES (710400, 710000, '台中市', '台中', 121.509, 25.0443, 2, 15, 1);
INSERT INTO `sys_area` VALUES (710500, 710000, '金门县', '金门', 121.509, 25.0443, 2, 6, 1);
INSERT INTO `sys_area` VALUES (710600, 710000, '南投县', '南投', 121.509, 25.0443, 2, 9, 1);
INSERT INTO `sys_area` VALUES (710700, 710000, '基隆市', '基隆', 121.509, 25.0443, 2, 5, 1);
INSERT INTO `sys_area` VALUES (710800, 710000, '新竹市', '新竹', 121.509, 25.0443, 2, 18, 1);
INSERT INTO `sys_area` VALUES (710900, 710000, '嘉义市', '嘉义', 121.509, 25.0443, 2, 3, 1);
INSERT INTO `sys_area` VALUES (711100, 710000, '新北市', '新北', 121.509, 25.0443, 2, 17, 1);
INSERT INTO `sys_area` VALUES (711200, 710000, '宜兰县', '宜兰', 121.509, 25.0443, 2, 20, 1);
INSERT INTO `sys_area` VALUES (711300, 710000, '新竹县', '新竹', 121.509, 25.0443, 2, 19, 1);
INSERT INTO `sys_area` VALUES (711400, 710000, '桃园县', '桃园', 121.509, 25.0443, 2, 16, 1);
INSERT INTO `sys_area` VALUES (711500, 710000, '苗栗县', '苗栗', 121.509, 25.0443, 2, 8, 1);
INSERT INTO `sys_area` VALUES (711700, 710000, '彰化县', '彰化', 121.509, 25.0443, 2, 22, 1);
INSERT INTO `sys_area` VALUES (711900, 710000, '嘉义县', '嘉义', 121.509, 25.0443, 2, 4, 1);
INSERT INTO `sys_area` VALUES (712100, 710000, '云林县', '云林', 121.509, 25.0443, 2, 21, 1);
INSERT INTO `sys_area` VALUES (712400, 710000, '屏东县', '屏东', 121.509, 25.0443, 2, 11, 1);
INSERT INTO `sys_area` VALUES (712500, 710000, '台东县', '台东', 121.509, 25.0443, 2, 13, 1);
INSERT INTO `sys_area` VALUES (712600, 710000, '花莲县', '花莲', 121.509, 25.0443, 2, 2, 1);
INSERT INTO `sys_area` VALUES (712700, 710000, '澎湖县', '澎湖', 121.509, 25.0443, 2, 10, 1);
INSERT INTO `sys_area` VALUES (712800, 710000, '连江县', '连江', 121.509, 25.0443, 2, 7, 1);
INSERT INTO `sys_area` VALUES (810000, 0, '香港特别行政区', '香港', 114.173, 22.32, 1, 32, 1);
INSERT INTO `sys_area` VALUES (810100, 810000, '香港岛', '香港岛', 114.173, 22.32, 2, 2, 1);
INSERT INTO `sys_area` VALUES (810200, 810000, '九龙', '九龙', 114.173, 22.32, 2, 1, 1);
INSERT INTO `sys_area` VALUES (810300, 810000, '新界', '新界', 114.173, 22.32, 2, 3, 1);
INSERT INTO `sys_area` VALUES (820000, 0, '澳门特别行政区', '澳门', 113.549, 22.199, 1, 33, 1);
INSERT INTO `sys_area` VALUES (820100, 820000, '澳门半岛', '澳门半岛', 113.549, 22.1988, 2, 1, 1);
INSERT INTO `sys_area` VALUES (820200, 820000, '离岛', '离岛', 113.549, 22.199, 2, 2, 1);
INSERT INTO `sys_area` VALUES (900000100, 0, '越南胡志明市', '越南胡志明市', 108.827, 12.1846, 1, 100, 1);
INSERT INTO `sys_area` VALUES (900000101, 900000100, '大叻', '大叻', 108.827, 12.1846, 2, 1, 1);
COMMIT;


-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint(25) NOT NULL,
  `name` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '用户角色名，与displayName灵活应用',
  `display_name` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '用户角色展示名',
  `type` int(1) DEFAULT NULL COMMENT '菜单类型, (e.g 1静态菜单,2动态菜单,3按钮...参考字典)',
  `classify` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '分类类型',
  `level` int(1) NOT NULL DEFAULT '1' COMMENT '级别,顶级=1 , 菜单为0级',
  `status` int(1) NOT NULL DEFAULT '0' COMMENT '菜单状态(e.g 启用,禁用)',
  `parent_id` bigint(25) NOT NULL COMMENT '父级菜单ID ,顶级的父级id为0',
  `parent_ids` varchar(500) COLLATE utf8_bin DEFAULT NULL COMMENT '树形父级菜单ID列表（如：1,11,22）',
  `permission` varchar(500) COLLATE utf8_bin NOT NULL COMMENT '权限标识（如：sys:user:edit,sys:user:view），用于如shiro-aop方法及权限校验',
  `page_location` varchar(500) COLLATE utf8_bin DEFAULT NULL COMMENT '页面地址,(例如静态菜单:/ci/task/xx.vue文件路径(不包含.vue后缀),动态菜单www.baidu.com)',
  `route_namespace` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '路由path(#后面的部分)，类似springmvc的RequestMapping("/list")，注：规定任意层级的菜单的此字段值只有一级，如：/iam或/user或/list',
  `render_target` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '渲染目标 （_self, _blank），注：当type=2动态菜单时有值',
  `icon` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '图标',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `create_by` bigint(25) NOT NULL,
  `create_date` datetime NOT NULL,
  `update_by` bigint(25) NOT NULL,
  `update_date` datetime NOT NULL,
  `del_flag` int(1) NOT NULL DEFAULT '0' COMMENT '删除状态（0:正常/1:删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='通用系统菜单（权限）表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_menu` VALUES (5, 'System Settings', '系统设置', 1, NULL, 1, 0, 0, NULL, 'iam', '/iam', '/iam', NULL, 'icon-xitongshezhi', 900, 1, '2019-11-01 15:54:37', 1, '2020-08-26 12:49:57', 0);
INSERT INTO `sys_menu` VALUES (11, 'Online Users', '在线用户', 1, NULL, 2, 0, 5, NULL, 'iam:online', '/iam/online/Online', '/online', NULL, 'icon-zaixianyonghu', 902, 1, '2019-10-31 10:01:57', 1, '2020-08-26 12:55:12', 0);
INSERT INTO `sys_menu` VALUES (12, 'Users', '用户管理', 1, NULL, 2, 0, 5, NULL, 'iam:user', '/iam/user/User', '/user', NULL, 'icon-yonghuguanli', 903, 1, '2019-11-01 15:54:37', 1, '2020-08-26 12:40:58', 0);
INSERT INTO `sys_menu` VALUES (13, 'Menus', '菜单配置', 1, NULL, 2, 0, 5, NULL, 'iam:menu', '/iam/menu/Menu', '/menu', NULL, 'icon-caidan', 906, 1, '2019-11-01 15:54:37', 1, '2020-08-26 12:41:13', 0);
INSERT INTO `sys_menu` VALUES (14, 'Organizations', '组织机构', 1, NULL, 2, 0, 5, NULL, 'iam:group', '/iam/group/Group', '/group', NULL, 'icon-organization', 905, 1, '2019-11-01 15:54:37', 1, '2020-08-26 12:41:07', 0);
INSERT INTO `sys_menu` VALUES (15, 'Roles', '角色管理', 1, NULL, 2, 0, 5, NULL, 'iam:role', '/iam/role/Role', '/role', NULL, 'icon-jiaoseguanli', 904, 1, '2019-11-01 15:54:37', 1, '2020-08-26 12:41:03', 0);
INSERT INTO `sys_menu` VALUES (20, 'Dictionaries', '字典配置', 1, NULL, 2, 0, 5, NULL, 'iam:dict', '/iam/dict/Dict', '/dict', NULL, 'icon-zidianguanli', 907, 1, '2019-11-01 15:54:37', 1, '2020-08-26 12:41:18', 0);
INSERT INTO `sys_menu` VALUES (21, 'Notifications', '通知设置', 1, NULL, 2, 0, 5, NULL, 'iam:contact', '/iam/contact/Contact', '/contact', NULL, 'icon-lianxiren', 901, 1, '2019-11-01 15:54:37', 1, '2020-08-26 12:40:52', 0);
INSERT INTO `sys_menu` VALUES (33, 'Home', '主页', 1, NULL, 1, 0, 0, NULL, 'home', '', '/home', NULL, 'icon-zhuye', 100, 1, '2019-11-26 10:42:01', 1, '2020-08-26 12:23:21', 0);
INSERT INTO `sys_menu` VALUES (34, 'Overview', '概览', 1, NULL, 2, 0, 33, NULL, 'home:overview', '/home/overview/Overview', '/overview', NULL, 'icon-gailan', 101, 1, '2019-11-26 10:42:33', 1, '2020-08-26 12:24:40', 0);
<#if moduleMap??>
    <#assign parentMenuSortSeq = 100>
    <#list moduleMap?keys as moduleName>
        <#assign nextMenuId = javaSpecs.genNextId()>
INSERT INTO `sys_menu` VALUES (${nextMenuId}, '${moduleName}', '${moduleName}', 1, 'classifyA', 1, 0, 0, NULL, '${moduleName}', '', '/${moduleName}', 'NULL', 'icon-gongju3', ${parentMenuSortSeq}, 1, '2020-09-08 14:45:51', 1, '2020-09-21 19:59:37', 0);
        <#assign parentMenuSortSeq = parentMenuSortSeq + 100>
        <#assign subMenuSortSeq = 100>
        <#list moduleMap[moduleName] as table>
          <#assign nextTableMenuId = javaSpecs.genNextId()>
INSERT INTO `sys_menu` VALUES (${nextTableMenuId}, '${table.entityName}', '${table.functionNameSimple}', 1, 'classifyA', 2, 0, ${nextMenuId}, NULL, '${moduleName}:${table.entityName?lower_case}', '/${moduleName}/${table.entityName?lower_case}/${table.entityName?cap_first}', '/${moduleName}/${table.entityName?lower_case}', 'NULL', 'icon-gongju3', ${subMenuSortSeq}, 1, '2020-09-08 14:45:51', 1, '2020-09-21 19:59:37', 0);
            <#assign subMenuSortSeq = subMenuSortSeq + 1>
          <#if table.isEditOnPage == true>
INSERT INTO `sys_menu` VALUES (${javaSpecs.genNextId()}, '${table.entityName} Edit', '${table.functionNameSimple}编辑', 1, 'classifyA', 3, 0, ${nextTableMenuId}, NULL, '${moduleName}:${table.entityName?lower_case}:edit', '/${moduleName}/${table.entityName?lower_case}/${table.entityName?cap_first}Edit', '/${moduleName}/${table.entityName?lower_case}/edit', 'NULL', 'icon-gongju3', ${subMenuSortSeq}, 1, '2020-09-08 14:45:51', 1, '2020-09-21 19:59:37', 0);
          </#if>
        </#list>
    </#list>
</#if>
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint(25) NOT NULL,
  `user_name` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '登录账号名(唯一）',
  `display_name` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '显示名称',
  `password` varchar(768) COLLATE utf8_bin NOT NULL COMMENT '密文密码',
  `user_type` int(1) NOT NULL DEFAULT '0' COMMENT '用户类型（保留字段）',
  `enable` int(1) NOT NULL DEFAULT '1' COMMENT '启用状态（0:禁止/1:启用）',
  `status` int(1) NOT NULL DEFAULT '0' COMMENT '用户状态（预留）',
  `email` varchar(48) COLLATE utf8_bin DEFAULT NULL,
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
  `create_by` bigint(25) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_by` bigint(25) DEFAULT '0',
  `update_date` datetime DEFAULT NULL,
  `del_flag` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='通用系统用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` VALUES (1, 'root', '系统根超级管理员', 'b68bac90602f8800a51282243c7405afff8a9a0dd8e412d238c89970a221669ae13f67e3c997cac35162a88e0234ca1b345384456c3e9ac1358f953a58128f2b', 0, 1, 0, '983708408@qq.com', '18127968606', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '系统根管理员', 1, '2019-11-17 18:11:00', 1, '2019-11-26 14:11:10', 0);
INSERT INTO `sys_user` VALUES (2, 'administrator', '系统管理员', '6810ae5a577b26dd4916468fe5b21519', 0, 1, 0, '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '系统管理员1', 1, '2019-11-17 18:11:00', 1, '2019-11-26 14:11:10', 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint(25) NOT NULL,
  `role_code` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '用户角色名，与displayName灵活应用',
  `display_name` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '用户角色展示名',
  `type` int(1) DEFAULT NULL COMMENT '用户角色类型（预留）',
  `enable` int(1) NOT NULL DEFAULT '1' COMMENT '用户角色启用状态（0:禁用/1:启用）',
  `status` int(1) NOT NULL DEFAULT '0' COMMENT '用户角色状态（预留）',
  `create_by` bigint(25) NOT NULL,
  `create_date` datetime NOT NULL,
  `update_by` bigint(25) NOT NULL,
  `update_date` datetime NOT NULL,
  `del_flag` int(1) NOT NULL COMMENT '删除状态（0:正常/1:删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='通用系统角色表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_role` VALUES (1, 'manager', '管理者', 1, 1, 0, 1, '2019-10-29 11:28:03', 1, '2020-06-12 10:04:24', 0);
INSERT INTO `sys_role` VALUES (2, 'groupleader', '组长', 1, 1, 0, 1, '2019-10-29 11:28:03', 1, '2019-11-26 14:11:58', 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_group
-- ----------------------------
DROP TABLE IF EXISTS `sys_group`;
CREATE TABLE `sys_group` (
  `id` bigint(25) NOT NULL,
  `name` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '用户分租(customer）名，与displayName灵活应用',
  `display_name` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '用户分租(customer）展示名',
  `organization_code` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '唯一标识',
  `type` int(1) DEFAULT '0' COMMENT '用户分组类型（预留）1park,2company,3department',
  `parent_id` bigint(25) DEFAULT NULL COMMENT '父级id',
  `parent_ids` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '父级路径id列表, 为减少使用时计算量提高性能(逗号分隔)',
  `area_id` bigint(25) DEFAULT NULL COMMENT '区域id',
  `enable` int(1) NOT NULL DEFAULT '1' COMMENT '用户组启用状态（0:禁用/1:启用）',
  `status` int(1) NOT NULL DEFAULT '0' COMMENT '用户组状态（预留）',
  `create_by` bigint(25) NOT NULL,
  `create_date` datetime NOT NULL,
  `update_by` bigint(25) NOT NULL,
  `update_date` datetime NOT NULL,
  `del_flag` int(1) NOT NULL DEFAULT '0' COMMENT '删除状态（0:正常/1:删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `parent_id` (`parent_id`) USING BTREE,
  FULLTEXT KEY `parent_ids` (`parent_ids`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='通用系统用户组表';

-- ----------------------------
-- Records of sys_group
-- ----------------------------
BEGIN;
INSERT INTO `sys_group` VALUES (1, 'PanYuEnergyParkCenter', 'Xxx科技园中心', 'PanYuEnergyParkCenter', 1, NULL, NULL, 440100, 1, 0, 1, '2019-10-29 14:52:29', 1, '2019-11-26 14:04:16', 0);
INSERT INTO `sys_group` VALUES (2, 'Shangmaikeji-GZ', 'Xxx科技有限公司', 'Shangmaikeji-GZ', 2, 1, '1', 440100, 1, 0, 1, '2019-10-29 14:52:29', 1, '2019-11-26 14:05:05', 0);
INSERT INTO `sys_group` VALUES (3, 'BigdataDepartment', '大数据研发部', 'BigdataDepartment', 3, 2, '2,1', 440100, 1, 0, 1, '2019-10-29 14:52:29', 1, '2019-11-26 14:09:55', 0);
INSERT INTO `sys_group` VALUES (4, 'BizDepartment', '应用研发部', 'BizDepartment', 3, 2, '2,1', 110100, 1, 0, 1, '2019-10-29 14:52:29', 1, '2019-11-26 14:06:18', 0);
INSERT INTO `sys_group` VALUES (5, 'DevSecOpsFramework', 'DevSecOps+系统架构部', 'DevSecOpsFramework', 3, 2, '2,1', 110100, 1, 0, 1, '2019-10-31 15:46:29', 1, '2019-11-26 14:10:19', 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_department
-- ----------------------------
DROP TABLE IF EXISTS `sys_department`;
CREATE TABLE `sys_department` (
  `id` bigint(25) NOT NULL,
  `group_id` bigint(25) DEFAULT NULL,
  `display_name` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `contact` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `contact_phone` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `group_id` (`group_id`) USING BTREE,
  CONSTRAINT `sys_department_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `sys_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of sys_department
-- ----------------------------
BEGIN;
INSERT INTO `sys_department` VALUES (1, 3, NULL, NULL, '18127968606');
INSERT INTO `sys_department` VALUES (2, 4, NULL, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_company
-- ----------------------------
DROP TABLE IF EXISTS `sys_company`;
CREATE TABLE `sys_company` (
  `id` bigint(25) NOT NULL,
  `group_id` bigint(25) DEFAULT NULL,
  `display_name` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `contact` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `contact_phone` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `address` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `group_id` (`group_id`) USING BTREE,
  CONSTRAINT `sys_company_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `sys_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for sys_group_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_group_role`;
CREATE TABLE `sys_group_role` (
  `id` bigint(25) NOT NULL,
  `group_id` bigint(25) DEFAULT NULL,
  `role_id` bigint(25) DEFAULT NULL,
  `create_by` bigint(25) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `group_id` (`group_id`) USING BTREE,
  KEY `role_id` (`role_id`) USING BTREE,
  CONSTRAINT `sys_group_role_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `sys_group` (`id`),
  CONSTRAINT `sys_group_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='通用系统group-role中间表';

-- ----------------------------
-- Table structure for sys_group_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_group_user`;
CREATE TABLE `sys_group_user` (
  `id` bigint(25) NOT NULL,
  `group_id` bigint(25) DEFAULT NULL,
  `user_id` bigint(25) DEFAULT NULL,
  `create_by` bigint(25) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `group_id` (`group_id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE,
  CONSTRAINT `sys_group_user_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `sys_group` (`id`),
  CONSTRAINT `sys_group_user_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='通用系统group-user中间表';

-- ----------------------------
-- Table structure for sys_role_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_user`;
CREATE TABLE `sys_role_user` (
  `id` bigint(25) NOT NULL,
  `user_id` bigint(25) DEFAULT NULL,
  `role_id` bigint(25) DEFAULT NULL,
  `create_by` bigint(25) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `role_id` (`role_id`) USING BTREE,
  CONSTRAINT `sys_role_user_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `sys_role_user_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='通用系统user-role中间表';

-- ----------------------------
-- Table structure for erm_app_cluster
-- ----------------------------
DROP TABLE IF EXISTS `erm_app_cluster`;
CREATE TABLE `erm_app_cluster` (
`id` bigint(25) NOT NULL,
`name` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '应用名称',
`type` int(11) DEFAULT NULL COMMENT '应用集群的子类类型:(比docker_cluster和k8s_cluster的分类级别低)\n1如springboot app 或vue app,\n2,如umc-agent进程\n\n注:ci发布时,过滤掉type=2(不带端口的进程,如agent)',
`endpoint` varchar(16) COLLATE utf8_bin NOT NULL COMMENT '端点:像sso这种则使用 port,  像agent则使用addr',
`ssh_id` bigint(25) DEFAULT NULL,
`deploy_type` int(2) NOT NULL COMMENT '1:host代表当部署在物理机;2:docker代表部署在docker集群;3:k8s代表部署在k8s集群;4:coss代表上传至coss',
`enable` int(1) NOT NULL DEFAULT '1' COMMENT '启用状态（0:禁止/1:启用）',
`remark` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
`create_by` bigint(25) NOT NULL,
`create_date` datetime NOT NULL,
`update_by` bigint(25) NOT NULL,
`update_date` datetime NOT NULL,
`del_flag` int(1) NOT NULL DEFAULT '0',
`organization_code` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '组织编码',
PRIMARY KEY (`id`) USING BTREE,
KEY `update_by` (`update_by`) USING BTREE,
KEY `create_by` (`create_by`) USING BTREE,
CONSTRAINT `erm_app_cluster_ibfk_1` FOREIGN KEY (`update_by`) REFERENCES `sys_user` (`id`),
CONSTRAINT `erm_app_cluster_ibfk_2` FOREIGN KEY (`create_by`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='应用集群（组）定义表';

-- ----------------------------
-- Records of erm_app_cluster
-- ----------------------------
BEGIN;
INSERT INTO `erm_app_cluster` VALUES (75, 'iam-server', NULL, '80', NULL, 1, 1, 'Iam-server cluster', 1, '2019-09-20 16:54:41', 1, '2019-09-20 16:54:41', 0, 'BizDepartment');
INSERT INTO `erm_app_cluster` VALUES (${clusterId}, '${serverName}', NULL, '80', NULL, 1, 1, '${serverName}', 1, '2020-08-06 19:25:49', 1, '2020-08-06 19:25:49', 0, 'BigdataDepartment');
COMMIT;

-- ----------------------------
-- Table structure for sys_cluster_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_cluster_config`;
CREATE TABLE `sys_cluster_config` (
  `id` bigint(25) NOT NULL,
  `cluster_id` bigint(25) NOT NULL COMMENT '对应cluster表的id',
  `display_name` varchar(255) CHARACTER SET utf8 NOT NULL COMMENT '应用名称',
  `type` int(1) DEFAULT '1' COMMENT '应用集群类型（1:iam/sso，2:其他）',
  `env_type` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '环境类型,字典value',
  `view_extranet_base_uri` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '前端视图页面外网BaseURI',
  `extranet_base_uri` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '外网BaseURI',
  `intranet_base_uri` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '内网BaseURI',
  `remark` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `create_by` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_by` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `del_flag` int(1) NOT NULL DEFAULT '0' COMMENT '删除状态（0：正常，1：删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `cluster_id` (`cluster_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='应用集群配置表（cluster与环境关联的具体配置，一定程度上也相当于app_cluster的明细子表，注：如，对于公私混合部署时，一个cluster_id/env_id可对应多条记录，iam/online功能会用到）';

-- ----------------------------
-- Records of sys_cluster_config
-- ----------------------------
BEGIN;
<#assign clusterType = 2 />
<#if javaSpecs.isConf(extOpts, "iam.mode", "cluster")>
<#assign clusterType = 1 />
INSERT INTO `sys_cluster_config` VALUES (2, 75, 'iam-server', 1, 'dev', 'http://iam.${devTopDomain}:14040', 'http://iam-services.${devTopDomain}:14040/iam-server', 'http://localhost:14040/iam-server', 'IAM center service', NULL, NULL, NULL, NULL, 0);
INSERT INTO `sys_cluster_config` VALUES (8, 75, 'iam-server', 1, 'fat', 'http://iam.${fatTopDomain}', 'http://iam-services.${fatTopDomain}/iam-server', 'http://localhost:14040/iam-server', 'IAM center service', NULL, NULL, NULL, NULL, 0);
INSERT INTO `sys_cluster_config` VALUES (14, 75, 'iam-server', 1, 'uat', 'http://iam.${uatTopDomain}', 'http://iam-services.${uatTopDomain}/iam-server', 'http://localhost:14040/iam-server', 'IAM center service', NULL, NULL, NULL, NULL, 0);
INSERT INTO `sys_cluster_config` VALUES (28, 75, 'iam-server', 1, 'pro', 'http://iam.${proTopDomain}', 'http://iam-services.${proTopDomain}/iam-server', 'http://localhost:14040/iam-server', 'IAM center service', NULL, NULL, NULL, NULL, 0);
</#if>
INSERT INTO `sys_cluster_config` VALUES (39, ${clusterId}, '${serverName}', ${clusterType}, 'dev', 'http://${devTopDomain}:${devViewServicePort}', 'http://${devTopDomain}:${entryAppPort}/${serverName}', 'http://localhost:${entryAppPort}/${serverName}', '${projectName?lower_case} service', NULL, NULL, NULL, NULL, 0);
INSERT INTO `sys_cluster_config` VALUES (40, ${clusterId}, '${serverName}', ${clusterType}, 'fat', 'http://${fatViewServiceHost}', 'http://${fatServiceHost}/${serverName}', 'http://localhost:${entryAppPort}/${serverName}', '${projectName?lower_case} service', NULL, NULL, NULL, NULL, 0);
INSERT INTO `sys_cluster_config` VALUES (41, ${clusterId}, '${serverName}', ${clusterType}, 'uat', 'http://${uatViewServiceHost}', 'http://${uatServiceHost}/${serverName}', 'http://localhost:${entryAppPort}/${serverName}', '${projectName?lower_case} service', NULL, NULL, NULL, NULL, 0);
INSERT INTO `sys_cluster_config` VALUES (42, ${clusterId}, '${serverName}', ${clusterType}, 'pro', 'http://${proViewServiceHost}', 'http://${proServiceHost}/${serverName}', 'http://localhost:${entryAppPort}/${serverName}', '${projectName?lower_case} service', NULL, NULL, NULL, NULL, 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_contact
-- ----------------------------
DROP TABLE IF EXISTS `sys_contact`;
CREATE TABLE `sys_contact` (
  `id` bigint(25) NOT NULL,
  `name` varchar(30) CHARACTER SET utf8 NOT NULL COMMENT '通知分组名称',
  `create_by` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_by` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `del_flag` int(1) DEFAULT '0',
  `organization_code` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '组织编码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='告警联系人表';

-- ----------------------------
-- Records of sys_contact
-- ----------------------------
BEGIN;
INSERT INTO `sys_contact` VALUES (2, 'wangsir', '1', '2019-08-05 06:45:17', '1', '2020-04-10 14:44:22', 0, NULL);
INSERT INTO `sys_contact` VALUES (7, 'hwj', '1', '2019-08-23 15:16:39', '1', '2020-07-24 15:43:11', 0, NULL);
INSERT INTO `sys_contact` VALUES (2101651456, 'lxl', '1', '2020-07-17 18:10:20', '1', '2020-07-17 18:10:20', 0, NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_contact_channel
-- ----------------------------
DROP TABLE IF EXISTS `sys_contact_channel`;
CREATE TABLE `sys_contact_channel` (
  `id` bigint(25) NOT NULL,
  `kind` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT 'email,phone,wechat,dingtalk,twitter,facebook---对应com.wl4g.devops.support.notification.NotifierKind',
  `contact_id` bigint(25) DEFAULT NULL COMMENT '联系人id',
  `primary_address` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '具体的联系地址:可能是email,phone,wechat的open_id等',
  `time_of_freq` int(11) DEFAULT NULL COMMENT '频率时间',
  `num_of_freq` int(11) DEFAULT NULL COMMENT '频率次数',
  `enable` int(11) DEFAULT NULL COMMENT '是否启用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of sys_contact_channel
-- ----------------------------
BEGIN;
INSERT INTO `sys_contact_channel` VALUES (96824, 'Mail', 2, '983708408@qq.com', 30, 1, 1);
INSERT INTO `sys_contact_channel` VALUES (1455118336, 'Mail', 2101651456, '3091553379@qq.com', 99, 99, 1);
INSERT INTO `sys_contact_channel` VALUES (1749353472, 'Mail', 7, '1154635107@qq.com', 30, 1, 1);
COMMIT;

-- ----------------------------
-- Table structure for sys_contact_group
-- ----------------------------
DROP TABLE IF EXISTS `sys_contact_group`;
CREATE TABLE `sys_contact_group` (
  `id` bigint(25) NOT NULL,
  `name` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '告警分组名称',
  `create_date` datetime DEFAULT NULL,
  `create_by` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `update_by` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `del_flag` int(1) DEFAULT '0',
  `organization_code` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '组织编码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='告警联系人分组表';

-- ----------------------------
-- Records of sys_contact_group
-- ----------------------------
BEGIN;
INSERT INTO `sys_contact_group` VALUES (8, 'developer', '2019-08-23 16:34:06', '1', '2019-08-23 16:34:06', '1', 0, NULL);
INSERT INTO `sys_contact_group` VALUES (9, 'tester', '2019-08-23 16:34:14', '1', '2019-12-09 13:50:51', '1', 0, NULL);
INSERT INTO `sys_contact_group` VALUES (10, 'Operator', '2019-08-23 16:35:26', '1', '2019-08-23 16:35:26', '1', 0, NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_contact_group_ref
-- ----------------------------
DROP TABLE IF EXISTS `sys_contact_group_ref`;
CREATE TABLE `sys_contact_group_ref` (
  `id` bigint(25) NOT NULL,
  `contact_group_id` bigint(25) NOT NULL,
  `contact_id` bigint(25) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `contact_group_id` (`contact_group_id`) USING BTREE,
  KEY `contact_id` (`contact_id`) USING BTREE,
  CONSTRAINT `sys_contact_group_ref_ibfk_1` FOREIGN KEY (`contact_group_id`) REFERENCES `sys_contact_group` (`id`),
  CONSTRAINT `sys_contact_group_ref_ibfk_2` FOREIGN KEY (`contact_id`) REFERENCES `sys_contact` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of sys_contact_group_ref
-- ----------------------------
BEGIN;
INSERT INTO `sys_contact_group_ref` VALUES (59879, 8, 2);
INSERT INTO `sys_contact_group_ref` VALUES (709166080, 9, 7);
INSERT INTO `sys_contact_group_ref` VALUES (1120207872, 10, 7);
INSERT INTO `sys_contact_group_ref` VALUES (1401226240, 8, 7);
INSERT INTO `sys_contact_group_ref` VALUES (1866160128, 8, 2101651456);
COMMIT;

-- ----------------------------
-- Table structure for sys_group_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_group_menu`;
CREATE TABLE `sys_group_menu` (
  `id` bigint(25) NOT NULL,
  `group_id` bigint(25) DEFAULT NULL,
  `menu_id` bigint(25) DEFAULT NULL,
  `create_by` bigint(25) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `group_id` (`group_id`) USING BTREE,
  KEY `menu_id` (`menu_id`) USING BTREE,
  CONSTRAINT `sys_group_menu_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `sys_group` (`id`),
  CONSTRAINT `sys_group_menu_ibfk_2` FOREIGN KEY (`menu_id`) REFERENCES `sys_menu` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='通用系统group-menu中间表';

-- ----------------------------
-- Table structure for sys_notification_contact
-- ----------------------------
DROP TABLE IF EXISTS `sys_notification_contact`;
CREATE TABLE `sys_notification_contact` (
  `id` bigint(25) NOT NULL,
  `record_id` bigint(25) DEFAULT NULL COMMENT '信息id',
  `contact_id` bigint(25) DEFAULT NULL COMMENT '联系人',
  `status` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT 'send , unsend , accepted , unaccepted ',
  `remark` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `notification_id` (`record_id`) USING BTREE,
  KEY `contact_id` (`contact_id`) USING BTREE,
  CONSTRAINT `sys_notification_contact_ibfk_1` FOREIGN KEY (`contact_id`) REFERENCES `sys_contact` (`id`),
  CONSTRAINT `sys_notification_contact_ibfk_2` FOREIGN KEY (`record_id`) REFERENCES `umc_alarm_record` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for sys_park
-- ----------------------------
DROP TABLE IF EXISTS `sys_park`;
CREATE TABLE `sys_park` (
  `id` bigint(25) NOT NULL,
  `group_id` bigint(25) DEFAULT NULL,
  `display_name` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `contact` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `contact_phone` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `address` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `group_id` (`group_id`) USING BTREE,
  CONSTRAINT `sys_park_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `sys_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id` bigint(25) NOT NULL,
  `role_id` bigint(25) DEFAULT NULL,
  `menu_id` bigint(25) DEFAULT NULL,
  `create_by` bigint(25) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `role_id` (`role_id`) USING BTREE,
  KEY `menu_id` (`menu_id`) USING BTREE,
  CONSTRAINT `sys_role_menu_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`),
  CONSTRAINT `sys_role_menu_ibfk_2` FOREIGN KEY (`menu_id`) REFERENCES `sys_menu` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='通用系统role-menu中间表';

SET FOREIGN_KEY_CHECKS = 1;
