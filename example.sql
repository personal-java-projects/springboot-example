/*
 Navicat Premium Data Transfer

 Source Server         : 远程
 Source Server Type    : MySQL
 Source Server Version : 50736
 Source Host           : 101.35.44.70:3306
 Source Schema         : example

 Target Server Type    : MySQL
 Target Server Version : 50736
 File Encoding         : 65001

 Date: 13/01/2022 14:29:59
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ex_file
-- ----------------------------
DROP TABLE IF EXISTS `ex_file`;
CREATE TABLE `ex_file`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '文件id',
  `fileMd5` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件的MD5',
  `fileName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件名',
  `uploadStatus` int(2) NOT NULL COMMENT '上传状态 0.上传完成而且合并成功   1.已上传部分  2 分片全部上传完成仅需合并',
  `uploadUrl` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件上传后可访问的地址',
  `suffix` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件类型',
  `chunkCount` bigint(64) NOT NULL,
  `userId` int(11) NOT NULL COMMENT '上传文件的用户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ex_file
-- ----------------------------

-- ----------------------------
-- Table structure for ex_role
-- ----------------------------
DROP TABLE IF EXISTS `ex_role`;
CREATE TABLE `ex_role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `roleName` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ex_role
-- ----------------------------
INSERT INTO `ex_role` VALUES (1, '游客');
INSERT INTO `ex_role` VALUES (2, '管理员');
INSERT INTO `ex_role` VALUES (3, '超级管理员');
INSERT INTO `ex_role` VALUES (4, '开发者');

-- ----------------------------
-- Table structure for ex_role_user
-- ----------------------------
DROP TABLE IF EXISTS `ex_role_user`;
CREATE TABLE `ex_role_user`  (
  `userId` int(11) NOT NULL COMMENT '用户id',
  `roleId` int(11) NOT NULL COMMENT '角色id',
  PRIMARY KEY (`userId`, `roleId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ex_role_user
-- ----------------------------
INSERT INTO `ex_role_user` VALUES (4, 1);
INSERT INTO `ex_role_user` VALUES (5, 4);

-- ----------------------------
-- Table structure for ex_user
-- ----------------------------
DROP TABLE IF EXISTS `ex_user`;
CREATE TABLE `ex_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `username` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户加密后的密码',
  `plain_password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户未加密的密码',
  `avatarUrl` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '用户头像',
  `disabled` int(2) NULL DEFAULT 0 COMMENT '账号是否封禁，0：解禁，1：封号',
  `disabledTime` bigint(12) NULL DEFAULT 0 COMMENT '账号封禁时长',
  `permanent_disabled` int(2) NULL DEFAULT 0 COMMENT '账号是否永久封禁，0：不封禁，1：永久封禁',
  `createTime` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '用户创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ex_user
-- ----------------------------
INSERT INTO `ex_user` VALUES (4, 'user', 'e10adc3949ba59abbe56e057f20f883e', '123456', 'http://101.35.44.70:9000/file/2022-01/12/avatar.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20220112%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20220112T144042Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=e6efec16cc952cec35826304598eac50b22b888b81ac24212c75a7e0fd7519bb', 0, 0, 0, '2022-01-13 10:07:51');
INSERT INTO `ex_user` VALUES (5, 'developer', '3d5d1299d63de6d215c684caf0a352f3', 'developer:123', 'http://101.35.44.70:9000/file/2022-01/12/avatar.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20220112%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20220112T144042Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=e6efec16cc952cec35826304598eac50b22b888b81ac24212c75a7e0fd7519bb', 0, 0, 0, '2022-01-13 10:08:38');

SET FOREIGN_KEY_CHECKS = 1;
