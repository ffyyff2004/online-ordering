/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 50639
 Source Host           : localhost:3306
 Source Schema         : vue_wsdcxt

 Target Server Type    : MySQL
 Target Server Version : 50639
 File Encoding         : 65001
*/

drop database if exists vue_wsdcxt;
create database vue_wsdcxt charset utf8;
use vue_wsdcxt;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `adminid` varchar(32) NOT NULL COMMENT '管理员表主键编号',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `password` varchar(50) DEFAULT NULL COMMENT '密码',
  `realname` varchar(50) DEFAULT NULL COMMENT '姓名',
  `contact` varchar(50) DEFAULT NULL COMMENT '联系方式',
  `addtime` varchar(28) DEFAULT NULL COMMENT '创建日期',
  PRIMARY KEY (`adminid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='管理员表';

-- ----------------------------
-- Records of admin
-- ----------------------------
BEGIN;
INSERT INTO `admin` (`adminid`, `username`, `password`, `realname`, `contact`, `addtime`) VALUES ('A20220406113957529', 'admin', 'admin', 'admin', '13888888888', '2026-05-02');
INSERT INTO `admin` (`adminid`, `username`, `password`, `realname`, `contact`, `addtime`) VALUES ('A20220816220417106', 'tom', '123', '汤姆', '13777777777', '2026-05-19');
INSERT INTO `admin` (`adminid`, `username`, `password`, `realname`, `contact`, `addtime`) VALUES ('A20220816220431414', 'mike', '123', '麦克', '13555555555', '2026-05-29');
COMMIT;

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
  `articleid` varchar(32) NOT NULL COMMENT '新闻公告表主键编号',
  `title` varchar(50) DEFAULT NULL COMMENT '标题',
  `image` varchar(255) DEFAULT NULL COMMENT '图片',
  `contents` text COMMENT '内容',
  `addtime` varchar(28) DEFAULT NULL COMMENT '发布日期',
  `hits` int(11) DEFAULT '0' COMMENT '点击数',
  PRIMARY KEY (`articleid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='新闻公告表';

-- ----------------------------
-- Records of article
-- ----------------------------
BEGIN;
INSERT INTO `article` (`articleid`, `title`, `image`, `contents`, `addtime`, `hits`) VALUES ('A20220406162413672', '网上订餐量大增，疫情火了在线业务', '20220816215745.jpg', '因疫情原因，大多数城市不允许去餐厅用餐，但允许餐厅开展点餐送餐业务。\n\n据经济第一大省广东省3月3日透露，通过对企业开展“预约式订餐”和“无接触式配餐”监测，推动网络订餐平台实施优惠政策助力餐饮行业发展网上经营模式。\n\n从2月中旬到现在，仅仅一个月时间，网上订餐平台“饿了么”与“美团”两大平台的在线商家数量已经从9万增长到了11.4万，订单量一个月猛涨增长了32.6%。', '2026-04-17', 1);
INSERT INTO `article` (`articleid`, `title`, `image`, `contents`, `addtime`, `hits`) VALUES ('A20220816215432567', '网上订餐也能看“脸”了 谨记这4点提示！', '20220816215704.png', '据上海市消保委消息，近期，上海市各级市场监管部门正开展网络餐饮食品安全专项整治，餐饮单位的营业执照、食品经营许可信息、食品安全量化分级等原先在线下公示的食品安全信息现正在平台上进行公示。国庆假期即将来临，网上订餐如何看“脸”吃饭呢？上海市消保委提醒消费者注意以下几点：\n\n提高食品安全风险防范意识。消费者在订餐时，要查看餐饮单位经营页面上是否具有营业执照和食品经营许可证。以饿了么为例，可在餐厅页面点击【商家】-【查看食品安全信息】。', '2026-04-06', 6);
INSERT INTO `article` (`articleid`, `title`, `image`, `contents`, `addtime`, `hits`) VALUES ('A20220816215839112', '网上订餐：要方便更要安全！', '20220816215827.jpg', '在日常生活中，人们经常可看到外卖骑手穿街走巷进行送餐，人们在享受外卖送餐便利的同时，也会遇到食品安全问题带来的“舌尖焦虑”。\n　　食品加工相关规定要求，制作盒饭必须要有粗加工场所、餐具洗消场所、喷淋场所和仓库等功能分区，以保障食品加工过程中的卫生达标。但事实上，网络外卖APP展示的食品，制作过程看不见摸不着，消费者无法了解店铺的卫生状况。除此之外，无证经营、追责等问题难以把控，消费者稍不留神，就可能会在食品安全上栽跟头。\n　　在市场机制的作用下，网店自身在食品安全方面需要加强自律，因为市场的逻辑很简单，食品安全卫生美味的店家，才会被口口相传，而赢得口碑才能最终赢得市场。然而，保障网络订餐的饮食安全，不能止步于网店自律，必须要加强他律。工商、食药监等相关部门要建立健全机制，强化登记备案制度，加大执法力度，净化网店市场环境。\n　　国家食品药品监督管理总局10日发布《网络餐饮服务食品安全监督管理办法》，规定入网餐饮服务提供者应当具有实体经营门店并依法取得食品经营许可证，不得超范围经营。办法明确了“线上线下一致”原则。办法规定，网络销售的餐饮食品应当与实体店销售的餐饮食品质量安全保持一致。', '2026-04-28', 0);
COMMIT;

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `cartid` varchar(32) NOT NULL COMMENT '购物车表主键编号',
  `usersid` varchar(32) DEFAULT NULL COMMENT '用户',
  `foodsid` varchar(32) DEFAULT NULL COMMENT '食品',
  `price` varchar(50) DEFAULT NULL COMMENT '单价',
  `num` varchar(50) DEFAULT NULL COMMENT '数量',
  `addtime` varchar(28) DEFAULT NULL COMMENT '日期',
  PRIMARY KEY (`cartid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='购物车表';

-- ----------------------------
-- Records of cart
-- ----------------------------
BEGIN;
INSERT INTO `cart` (`cartid`, `usersid`, `foodsid`, `price`, `num`, `addtime`) VALUES ('C20220817203945964', 'U20220406182102296', 'F20220406163544998', '22.6', '1', '2026-05-08');
INSERT INTO `cart` (`cartid`, `usersid`, `foodsid`, `price`, `num`, `addtime`) VALUES ('C20220817203949622', 'U20220406182102296', 'F20220406164507745', '119', '1', '2026-05-31');
INSERT INTO `cart` (`cartid`, `usersid`, `foodsid`, `price`, `num`, `addtime`) VALUES ('C20220817204212192', 'U20220406165507542', 'F20220406163544998', '22.6', '1', '2026-05-05');
INSERT INTO `cart` (`cartid`, `usersid`, `foodsid`, `price`, `num`, `addtime`) VALUES ('C20220817204220335', 'U20220406165507542', 'F20220406165251797', '22.6', '1', '2026-05-26');
INSERT INTO `cart` (`cartid`, `usersid`, `foodsid`, `price`, `num`, `addtime`) VALUES ('C20220817204233886', 'U20220406165507542', 'F20220406162758948', '16.8', '1', '2026-05-22');
COMMIT;

-- ----------------------------
-- Table structure for cate
-- ----------------------------
DROP TABLE IF EXISTS `cate`;
CREATE TABLE `cate` (
  `cateid` varchar(32) NOT NULL COMMENT '食品类型表主键编号',
  `catename` varchar(50) DEFAULT NULL COMMENT '类型名称',
  `addtime` varchar(28) DEFAULT NULL COMMENT '创建日期',
  `memo` varchar(50) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`cateid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='食品类型表';

-- ----------------------------
-- Records of cate
-- ----------------------------
BEGIN;
INSERT INTO `cate` (`cateid`, `catename`, `addtime`, `memo`) VALUES ('C20220406162439343', '中餐', '2026-05-05', '中餐');
INSERT INTO `cate` (`cateid`, `catename`, `addtime`, `memo`) VALUES ('C20220406164307344', '西餐', '2026-05-02', '西餐');
INSERT INTO `cate` (`cateid`, `catename`, `addtime`, `memo`) VALUES ('C20220406164557448', '炒菜', '2026-05-22', '炒菜');
COMMIT;

-- ----------------------------
-- Table structure for complains
-- ----------------------------
DROP TABLE IF EXISTS `complains`;
CREATE TABLE `complains` (
  `complainsid` varchar(32) NOT NULL COMMENT '意见反馈表主键编号',
  `usersid` varchar(32) DEFAULT NULL COMMENT '用户',
  `title` varchar(50) DEFAULT NULL COMMENT '标题',
  `contents` varchar(50) DEFAULT NULL COMMENT '内容',
  `addtime` varchar(28) DEFAULT NULL COMMENT '日期',
  `status` varchar(50) DEFAULT NULL COMMENT '状态',
  `reps` varchar(50) DEFAULT NULL COMMENT '管理回复',
  PRIMARY KEY (`complainsid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='意见反馈表';

-- ----------------------------
-- Records of complains
-- ----------------------------
BEGIN;
INSERT INTO `complains` (`complainsid`, `usersid`, `title`, `contents`, `addtime`, `status`, `reps`) VALUES ('C20220816220750126', 'U20220406165507542', '希望菜的种类可以丰富一点！', '希望菜的种类可以丰富一点！', '2026-05-12', '已回复', ' 收到，感谢您的反馈。');
INSERT INTO `complains` (`complainsid`, `usersid`, `title`, `contents`, `addtime`, `status`, `reps`) VALUES ('C20220816220800675', 'U20220406165507542', '很不错的平台，特别方便。', '很不错的平台，特别方便。', '2026-05-30', '未回复', ' ');
INSERT INTO `complains` (`complainsid`, `usersid`, `title`, `contents`, `addtime`, `status`, `reps`) VALUES ('C20220816220924791', 'U20220406182102296', '总体来说还不错吧。', '总体来说还不错吧。', '2026-05-31', '未回复', ' ');
COMMIT;

-- ----------------------------
-- Table structure for details
-- ----------------------------
DROP TABLE IF EXISTS `details`;
CREATE TABLE `details` (
  `detailsid` varchar(32) NOT NULL COMMENT '订单明细表主键编号',
  `ordercode` varchar(50) DEFAULT NULL COMMENT '订单号',
  `foodsid` varchar(32) DEFAULT NULL COMMENT '食品',
  `price` varchar(50) DEFAULT NULL COMMENT '单价',
  `num` varchar(50) DEFAULT NULL COMMENT '数量',
  PRIMARY KEY (`detailsid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单明细表';

-- ----------------------------
-- Records of details
-- ----------------------------
BEGIN;
INSERT INTO `details` (`detailsid`, `ordercode`, `foodsid`, `price`, `num`) VALUES ('202204061739532923.3416625379673', 'PD20220406173953', 'F20220406165326750', '22.6', '1');
INSERT INTO `details` (`detailsid`, `ordercode`, `foodsid`, `price`, `num`) VALUES ('202204061739541112.8684684727039', 'PD20220406173953', 'F20220406165345143', '54.8', '1');
INSERT INTO `details` (`detailsid`, `ordercode`, `foodsid`, `price`, `num`) VALUES ('202204061739547941.16045300091', 'PD20220406173953', 'F20220406164354137', '45', '1');
INSERT INTO `details` (`detailsid`, `ordercode`, `foodsid`, `price`, `num`) VALUES ('202204061739549693.298816644203', 'PD20220406173953', 'F20220406165345143', '54.8', '1');
INSERT INTO `details` (`detailsid`, `ordercode`, `foodsid`, `price`, `num`) VALUES ('202204061818235699.5331362892075', 'PD20220406181823', 'F20220406164825506', '28', '1');
INSERT INTO `details` (`detailsid`, `ordercode`, `foodsid`, `price`, `num`) VALUES ('202204061818237949.6250229999705', 'PD20220406181823', 'F20220406165326750', '22.6', '1');
INSERT INTO `details` (`detailsid`, `ordercode`, `foodsid`, `price`, `num`) VALUES ('202204061818239690.98550827228', 'PD20220406181823', 'F20220406164954745', '54.8', '1');
INSERT INTO `details` (`detailsid`, `ordercode`, `foodsid`, `price`, `num`) VALUES ('202208162208253448.695592469869', 'PD20220816220825', 'F20220406164825506', '28', '1');
INSERT INTO `details` (`detailsid`, `ordercode`, `foodsid`, `price`, `num`) VALUES ('202208162208266769.934709553285', 'PD20220816220825', 'F20220406163254430', '35.9', '1');
COMMIT;

-- ----------------------------
-- Table structure for fav
-- ----------------------------
DROP TABLE IF EXISTS `fav`;
CREATE TABLE `fav` (
  `favid` varchar(32) NOT NULL COMMENT '用户收藏表主键编号',
  `usersid` varchar(32) DEFAULT NULL COMMENT '用户',
  `foodsid` varchar(32) DEFAULT NULL COMMENT '食品',
  `addtime` varchar(28) DEFAULT NULL COMMENT '收藏日期',
  PRIMARY KEY (`favid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户收藏表';

-- ----------------------------
-- Records of fav
-- ----------------------------
BEGIN;
INSERT INTO `fav` (`favid`, `usersid`, `foodsid`, `addtime`) VALUES ('F20220406172859552', 'U20220406165507542', 'F20220406165251797', '2026-05-25');
INSERT INTO `fav` (`favid`, `usersid`, `foodsid`, `addtime`) VALUES ('F20220817204003855', 'U20220406182102296', 'F20220406163528128', '2026-05-13');
INSERT INTO `fav` (`favid`, `usersid`, `foodsid`, `addtime`) VALUES ('F20220817204007555', NULL, 'F20220406165326750', '2026-05-12');
INSERT INTO `fav` (`favid`, `usersid`, `foodsid`, `addtime`) VALUES ('F20220817204018200', 'U20220406182102296', 'F20220406164825506', '2026-05-04');
INSERT INTO `fav` (`favid`, `usersid`, `foodsid`, `addtime`) VALUES ('F20220817204318178', 'U20220406165507542', 'F20220406164354137', '2026-05-03');
INSERT INTO `fav` (`favid`, `usersid`, `foodsid`, `addtime`) VALUES ('F20220817204327353', 'U20220406165507542', 'F20220406164507745', '2026-05-20');
COMMIT;

-- ----------------------------
-- Table structure for foods
-- ----------------------------
DROP TABLE IF EXISTS `foods`;
CREATE TABLE `foods` (
  `foodsid` varchar(32) NOT NULL COMMENT '食品表主键编号',
  `foodsname` varchar(50) DEFAULT NULL COMMENT '食品名称',
  `image` varchar(255) DEFAULT NULL COMMENT '食品图片',
  `cateid` varchar(32) DEFAULT NULL COMMENT '食品类型',
  `price` varchar(50) DEFAULT NULL COMMENT '销售价格',
  `recommend` varchar(10) DEFAULT NULL COMMENT '是否推荐',
  `special` varchar(10) DEFAULT NULL COMMENT '是否特价',
  `addtime` varchar(28) DEFAULT NULL COMMENT '上架日期',
  `hits` int(11) DEFAULT '0' COMMENT '点击数',
  `sellnum` int(11) DEFAULT '0' COMMENT '销售单数',
  `contents` text COMMENT '食品介绍',
  PRIMARY KEY (`foodsid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='食品表';

-- ----------------------------
-- Records of foods
-- ----------------------------
BEGIN;
INSERT INTO `foods` (`foodsid`, `foodsname`, `image`, `cateid`, `price`, `recommend`, `special`, `addtime`, `hits`, `sellnum`, `contents`) VALUES ('F20220406162744279', '爆炒腰花', '20220406162736.jpg', 'C20220406162439343', '36.8', '是', '是', '2026-05-05', 1, 0, '爆炒腰花爆炒腰花爆炒腰花爆炒腰花');
INSERT INTO `foods` (`foodsid`, `foodsname`, `image`, `cateid`, `price`, `recommend`, `special`, `addtime`, `hits`, `sellnum`, `contents`) VALUES ('F20220406162758948', '川味豆腐', '20220406162750.jpg', 'C20220406162439343', '16.8', '是', '是', '2026-05-26', 3, 0, '川味豆腐');
INSERT INTO `foods` (`foodsid`, `foodsname`, `image`, `cateid`, `price`, `recommend`, `special`, `addtime`, `hits`, `sellnum`, `contents`) VALUES ('F20220406163238297', '川味粉蒸排骨', '20220406163227.jpg', 'C20220406162439343', '54.8', '是', '是', '2026-05-28', 1, 0, '川味粉蒸排骨');
INSERT INTO `foods` (`foodsid`, `foodsname`, `image`, `cateid`, `price`, `recommend`, `special`, `addtime`, `hits`, `sellnum`, `contents`) VALUES ('F20220406163254430', '川味辣子鸡', '20220406163245.jpg', 'C20220406162439343', '35.9', '是', '是', '2026-05-07', 1, 1, '川味辣子鸡');
INSERT INTO `foods` (`foodsid`, `foodsname`, `image`, `cateid`, `price`, `recommend`, `special`, `addtime`, `hits`, `sellnum`, `contents`) VALUES ('F20220406163330568', '川味魔芋', '20220406163320.jpg', 'C20220406162439343', '21.8', '是', '是', '2026-05-02', 0, 0, '川味魔芋');
INSERT INTO `foods` (`foodsid`, `foodsname`, `image`, `cateid`, `price`, `recommend`, `special`, `addtime`, `hits`, `sellnum`, `contents`) VALUES ('F20220406163528128', '川味蹄花汤', '20220406163518.jpg', 'C20220406162439343', '54.8', '是', '是', '2026-05-24', 1, 0, '川味蹄花汤');
INSERT INTO `foods` (`foodsid`, `foodsname`, `image`, `cateid`, `price`, `recommend`, `special`, `addtime`, `hits`, `sellnum`, `contents`) VALUES ('F20220406163544998', '干惼四季豆', '20220406163535.jpg', 'C20220406162439343', '22.6', '是', '是', '2026-05-28', 2, 0, '干惼四季豆');
INSERT INTO `foods` (`foodsid`, `foodsname`, `image`, `cateid`, `price`, `recommend`, `special`, `addtime`, `hits`, `sellnum`, `contents`) VALUES ('F20220406164354137', '罗宋汤', '20220406164345.jpg', 'C20220406164307344', '45', '是', '是', '2026-05-29', 3, 1, '罗宋汤');
INSERT INTO `foods` (`foodsid`, `foodsname`, `image`, `cateid`, `price`, `recommend`, `special`, `addtime`, `hits`, `sellnum`, `contents`) VALUES ('F20220406164405516', '奶油蘑菇汤', '20220406164359.jpg', 'C20220406164307344', '55', '是', '是', '2026-05-28', 1, 0, '奶油蘑菇汤');
INSERT INTO `foods` (`foodsid`, `foodsname`, `image`, `cateid`, `price`, `recommend`, `special`, `addtime`, `hits`, `sellnum`, `contents`) VALUES ('F20220406164418220', '南瓜奶油浓汤', '20220406164411.jpg', 'C20220406164307344', '65.9', '是', '是', '2026-05-19', 0, 0, '南瓜奶油浓汤');
INSERT INTO `foods` (`foodsid`, `foodsname`, `image`, `cateid`, `price`, `recommend`, `special`, `addtime`, `hits`, `sellnum`, `contents`) VALUES ('F20220406164451109', '煎鳕鱼', '20220406164440.jpg', 'C20220406164307344', '89', '是', '是', '2026-05-22', 0, 0, '煎鳕鱼');
INSERT INTO `foods` (`foodsid`, `foodsname`, `image`, `cateid`, `price`, `recommend`, `special`, `addtime`, `hits`, `sellnum`, `contents`) VALUES ('F20220406164507745', '清蒸三文鱼', '20220406164456.jpg', 'C20220406164307344', '119', '是', '是', '2026-05-01', 2, 0, '清蒸三文鱼');
INSERT INTO `foods` (`foodsid`, `foodsname`, `image`, `cateid`, `price`, `recommend`, `special`, `addtime`, `hits`, `sellnum`, `contents`) VALUES ('F20220406164520326', '三文鱼薯饼', '20220406164513.jpg', 'C20220406164307344', '129', '是', '是', '2026-05-16', 2, 0, '三文鱼薯饼');
INSERT INTO `foods` (`foodsid`, `foodsname`, `image`, `cateid`, `price`, `recommend`, `special`, `addtime`, `hits`, `sellnum`, `contents`) VALUES ('F20220406164537144', '盐焗三文鱼', '20220406164528.jpg', 'C20220406164307344', '139', '是', '是', '2026-05-14', 0, 0, '盐焗三文鱼');
INSERT INTO `foods` (`foodsid`, `foodsname`, `image`, `cateid`, `price`, `recommend`, `special`, `addtime`, `hits`, `sellnum`, `contents`) VALUES ('F20220406164825506', '豉油辣炒小观蚬蛤', '20220406164816.jpg', 'C20220406164557448', '28', '是', '是', '2026-05-30', 4, 2, '豉油辣炒小观蚬蛤');
INSERT INTO `foods` (`foodsid`, `foodsname`, `image`, `cateid`, `price`, `recommend`, `special`, `addtime`, `hits`, `sellnum`, `contents`) VALUES ('F20220406164954745', '鲜虾炒白菜', '20220406164948.jpg', 'C20220406164557448', '54.8', '是', '是', '2026-05-07', 2, 1, '鲜虾炒白菜');
INSERT INTO `foods` (`foodsid`, `foodsname`, `image`, `cateid`, `price`, `recommend`, `special`, `addtime`, `hits`, `sellnum`, `contents`) VALUES ('F20220406165200112', '大酱炒鸡蛋', '20220406165155.jpg', 'C20220406164557448', '22', '是', '是', '2026-05-06', 0, 0, '大酱炒鸡蛋');
INSERT INTO `foods` (`foodsid`, `foodsname`, `image`, `cateid`, `price`, `recommend`, `special`, `addtime`, `hits`, `sellnum`, `contents`) VALUES ('F20220406165214412', '韩式牛肉炒杂蔬', '20220406165207.jpg', 'C20220406164557448', '54.8', '是', '是', '2026-05-16', 0, 0, '韩式牛肉炒杂蔬');
INSERT INTO `foods` (`foodsid`, `foodsname`, `image`, `cateid`, `price`, `recommend`, `special`, `addtime`, `hits`, `sellnum`, `contents`) VALUES ('F20220406165236657', '双味东北酸菜粉', '20220406165229.jpg', 'C20220406164557448', '36.8', '是', '是', '2026-05-02', 0, 0, '双味东北酸菜粉');
INSERT INTO `foods` (`foodsid`, `foodsname`, `image`, `cateid`, `price`, `recommend`, `special`, `addtime`, `hits`, `sellnum`, `contents`) VALUES ('F20220406165251797', '酸菜炒肉丝', '20220406165244.jpg', 'C20220406164557448', '22.6', '是', '是', '2026-05-01', 3, 0, '酸菜炒肉丝');
INSERT INTO `foods` (`foodsid`, `foodsname`, `image`, `cateid`, `price`, `recommend`, `special`, `addtime`, `hits`, `sellnum`, `contents`) VALUES ('F20220406165326750', '干锅花菜', '20220406165318.jpeg', 'C20220406164557448', '22.6', '是', '是', '2026-05-05', 4, 2, '干锅花菜');
INSERT INTO `foods` (`foodsid`, `foodsname`, `image`, `cateid`, `price`, `recommend`, `special`, `addtime`, `hits`, `sellnum`, `contents`) VALUES ('F20220406165345143', '小炒牛肉', '20220406165337.jpg', 'C20220406164557448', '54.8', '是', '是', '2026-05-21', 34, 2, '小炒牛肉');
COMMIT;

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `ordersid` varchar(32) NOT NULL COMMENT '订单表主键编号',
  `ordercode` varchar(50) DEFAULT NULL COMMENT '订单号',
  `usersid` varchar(32) DEFAULT NULL COMMENT '用户',
  `total` varchar(50) DEFAULT NULL COMMENT '总计',
  `status` varchar(50) DEFAULT NULL COMMENT '状态',
  `addtime` varchar(28) DEFAULT NULL COMMENT '日期',
  `receiver` varchar(50) DEFAULT NULL COMMENT '收货人',
  `address` varchar(50) DEFAULT NULL COMMENT '送餐地址',
  `contact` varchar(50) DEFAULT NULL COMMENT '联系方式',
  PRIMARY KEY (`ordersid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单表';

-- ----------------------------
-- Records of orders
-- ----------------------------
BEGIN;
INSERT INTO `orders` (`ordersid`, `ordercode`, `usersid`, `total`, `status`, `addtime`, `receiver`, `address`, `contact`) VALUES ('O20220406173954757', 'PD20220406173953', 'U20220406165507542', '177.2', '已完成', '2026-05-02', '张三', '中山路28号', '13666666666');
INSERT INTO `orders` (`ordersid`, `ordercode`, `usersid`, `total`, `status`, `addtime`, `receiver`, `address`, `contact`) VALUES ('O20220406181823616', 'PD20220406181823', 'U20220406165507542', '105.4', '配送中', '2026-05-27', '张三', '知行东路12号', '13777777777');
INSERT INTO `orders` (`ordersid`, `ordercode`, `usersid`, `total`, `status`, `addtime`, `receiver`, `address`, `contact`) VALUES ('O20220816220826381', 'PD20220816220825', 'U20220406182102296', '63.9', '已完成', '2026-05-24', '李四', '中山路28号', '13777777777');
COMMIT;

-- ----------------------------
-- Table structure for topic
-- ----------------------------
DROP TABLE IF EXISTS `topic`;
CREATE TABLE `topic` (
  `topicid` varchar(32) NOT NULL COMMENT '订单评价表主键编号',
  `ordersid` varchar(32) DEFAULT NULL COMMENT '订单',
  `usersid` varchar(32) DEFAULT NULL COMMENT '用户',
  `foodsid` varchar(32) DEFAULT NULL COMMENT '食品',
  `num` varchar(50) DEFAULT NULL COMMENT '评分',
  `contents` varchar(50) DEFAULT NULL COMMENT '内容',
  `addtime` varchar(28) DEFAULT NULL COMMENT '日期',
  PRIMARY KEY (`topicid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单评价表';

-- ----------------------------
-- Records of topic
-- ----------------------------
BEGIN;
INSERT INTO `topic` (`topicid`, `ordersid`, `usersid`, `foodsid`, `num`, `contents`, `addtime`) VALUES ('T202204061801551010', 'O20220406173954757', 'U20220406165507542', 'F20220406165326750', '4', '味道很棒，赞！', '2026-05-25 18:01:55');
INSERT INTO `topic` (`topicid`, `ordersid`, `usersid`, `foodsid`, `num`, `contents`, `addtime`) VALUES ('T202204061801556412', 'O20220406173954757', 'U20220406165507542', 'F20220406164354137', '5', '很好吃，哈哈', '2026-05-07 18:01:55');
INSERT INTO `topic` (`topicid`, `ordersid`, `usersid`, `foodsid`, `num`, `contents`, `addtime`) VALUES ('T202204061801558163', 'O20220406173954757', 'U20220406165507542', 'F20220406165345143', '3', '味道很棒，赞！', '2026-05-11 18:01:55');
INSERT INTO `topic` (`topicid`, `ordersid`, `usersid`, `foodsid`, `num`, `contents`, `addtime`) VALUES ('T202204061801558721', 'O20220406173954757', 'U20220406165507542', 'F20220406165345143', '5', '还行吧，一般。', '2026-05-30 18:01:55');
INSERT INTO `topic` (`topicid`, `ordersid`, `usersid`, `foodsid`, `num`, `contents`, `addtime`) VALUES ('T202208162209047061', 'O20220816220826381', 'U20220406182102296', 'F20220406163254430', '3', '一般吧。', '2026-05-30 22:09:04');
INSERT INTO `topic` (`topicid`, `ordersid`, `usersid`, `foodsid`, `num`, `contents`, `addtime`) VALUES ('T202208162209049730', 'O20220816220826381', 'U20220406182102296', 'F20220406164825506', '5', '色香味俱全，哈哈', '2026-05-17 22:09:04');
COMMIT;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `usersid` varchar(32) NOT NULL COMMENT '网站用户表主键编号',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `password` varchar(50) DEFAULT NULL COMMENT '密码',
  `realname` varchar(50) DEFAULT NULL COMMENT '姓名',
  `sex` varchar(10) DEFAULT NULL COMMENT '性别',
  `birthday` varchar(28) DEFAULT NULL COMMENT '出生日期',
  `contact` varchar(50) DEFAULT NULL COMMENT '联系方式',
  `regdate` varchar(28) DEFAULT NULL COMMENT '注册日期',
  PRIMARY KEY (`usersid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='网站用户表';

-- ----------------------------
-- Records of users
-- ----------------------------
BEGIN;
INSERT INTO `users` (`usersid`, `username`, `password`, `realname`, `sex`, `birthday`, `contact`, `regdate`) VALUES ('U20220406165507542', 'zhangsan', '123', '张三', '男', '2000-11-06', '13666666666', '2026-05-25');
INSERT INTO `users` (`usersid`, `username`, `password`, `realname`, `sex`, `birthday`, `contact`, `regdate`) VALUES ('U20220406182102296', 'lisi', '123', '李四', '男', '2001-11-06', '13777777777', '2026-05-30');
INSERT INTO `users` (`usersid`, `username`, `password`, `realname`, `sex`, `birthday`, `contact`, `regdate`) VALUES ('U20220816220359245', 'wangwu', '123', '王五', '男', '2001-09-16', '13999999999', '2026-05-31');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
