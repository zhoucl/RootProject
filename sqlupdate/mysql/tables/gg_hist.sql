/*
Navicat MySQL Data Transfer

Source Server         : 192.168.6.164
Source Server Version : 50626
Source Host           : 192.168.6.164:3306
Source Database       : game

Target Server Type    : MYSQL
Target Server Version : 50626
File Encoding         : 65001

Date: 2016-04-13 13:20:34
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for gg_hist
-- ----------------------------
DROP TABLE IF EXISTS `gg_hist`;
CREATE TABLE `gg_hist` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `roomid` bigint(20) DEFAULT NULL,
  `playerid` int(11) DEFAULT NULL COMMENT '玩家id',
  `optype` int(11) DEFAULT NULL COMMENT '玩家操作类型(0:起手牌,1:摸牌,2:碰牌,3:暗杠,4:明杠,5:胡牌,6:抢杠胡,等...)',
  `opcard` varchar(20) DEFAULT NULL COMMENT '起手牌或上个玩家的出牌',
  `incard` varchar(2) DEFAULT NULL COMMENT '摸牌,杠牌,碰牌的牌',
  `outcard` varchar(2) DEFAULT NULL COMMENT '出牌',
  `round` int(11) DEFAULT NULL COMMENT '第几局',
  `createtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
