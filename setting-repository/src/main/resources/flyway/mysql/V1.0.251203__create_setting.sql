-- 若库不存在创建一个
CREATE DATABASE IF NOT EXISTS `setting`;
USE `setting`;

drop table if exists `setting_mail`;
CREATE TABLE IF NOT EXISTS `setting_mail`(
    `email_setting_id` bigint(19) DEFAULT NULL COMMENT '简历邮箱配置ID',
    `reference_id` bigint(19) DEFAULT NULL COMMENT '引用ID(引用表ID)',
    `reference_source` varchar(64) DEFAULT NULL COMMENT '引用源[数据表]',
    `channel` varchar(64) DEFAULT NULL COMMENT '邮箱通道（163',
    `email_address` varchar(64) DEFAULT NULL COMMENT '邮箱地址(123@qq.com）',
    `auth_code` varchar(64) DEFAULT NULL COMMENT '加密授权码',
    `protocol` varchar(64) DEFAULT NULL COMMENT '协议（IMAP',
    `mail_host` varchar(64) DEFAULT NULL COMMENT '服务器地址',
    `mail_port` varchar(64) DEFAULT NULL COMMENT '服务器端口',
    `email_type` integer(11) DEFAULT NULL COMMENT '邮箱类型（0:个人，1:企业）',
    `poll_interval` bigint(19) DEFAULT NULL COMMENT '轮询间隔（毫秒）',
    `folder` varchar(64) DEFAULT NULL COMMENT '邮箱文件夹',
    `ssl_enabled` tinyint(4) DEFAULT NULL COMMENT '是否启用SSL',
    ` should_mark_as_read` tinyint(4) DEFAULT NULL COMMENT '是否标记为已读',
    `should_delete_messages` tinyint(4) DEFAULT NULL COMMENT '是否删除邮件',
    `max_fetch_size` integer(11) DEFAULT NULL COMMENT '最大获取数量',
    `state` integer(11) DEFAULT NULL COMMENT '状态（NORMAL-正常',
    `revision` integer(11) DEFAULT NULL COMMENT '版本号',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用id',
    `create_time` datetime DEFAULT NULL COMMENT '时间戳',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
    `create_id` bigint(19) DEFAULT NULL COMMENT '创建者ID',
    `update_id` bigint(19) DEFAULT NULL COMMENT '更新者ID',
    PRIMARY KEY (`email_setting_id`)
) ENGINE=InnoDB COMMENT='';

drop table if exists `setting_sms`;
CREATE TABLE IF NOT EXISTS `setting_sms`(
    `setting_sms_id` bigint(19) NOT NULL COMMENT '短信配置ID',
    `reference_id` bigint(19) DEFAULT NULL COMMENT '引用ID(引用表ID)',
    `reference_source` varchar(64) DEFAULT NULL COMMENT '引用源[数据表]',
    `revision` integer(11) DEFAULT NULL COMMENT '版本号',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用id',
    `create_time` datetime DEFAULT NULL COMMENT '时间戳',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
    `create_id` bigint(19) DEFAULT NULL COMMENT '创建者ID',
    `update_id` bigint(19) DEFAULT NULL COMMENT '更新者ID',
    PRIMARY KEY (`setting_sms_id`)
) ENGINE=InnoDB COMMENT='';

drop table if exists `setting_llm`;
CREATE TABLE IF NOT EXISTS `setting_llm`(
    `setting_llm_id` bigint(19) NOT NULL COMMENT '模型配置ID',
    `reference_id` bigint(19) DEFAULT NULL COMMENT '引用ID(引用表ID)',
    `reference_source` varchar(64) DEFAULT NULL COMMENT '引用源[数据表]',
    `revision` integer(11) DEFAULT NULL COMMENT '版本号',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用id',
    `create_time` datetime DEFAULT NULL COMMENT '时间戳',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
    `create_id` bigint(19) DEFAULT NULL COMMENT '创建者ID',
    `update_id` bigint(19) DEFAULT NULL COMMENT '更新者ID',
    PRIMARY KEY (`setting_llm_id`)
) ENGINE=InnoDB COMMENT='';

drop table if exists `setting_store`;
CREATE TABLE IF NOT EXISTS `setting_store`(
    `setting_store_id` bigint(19) NOT NULL COMMENT '存储配置ID',
    `reference_id` bigint(19) DEFAULT NULL COMMENT '引用ID(引用表ID)',
    `reference_source` varchar(64) DEFAULT NULL COMMENT '引用源[数据表]',
    `revision` integer(11) DEFAULT NULL COMMENT '版本号',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用id',
    `create_time` datetime DEFAULT NULL COMMENT '时间戳',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
    `create_id` bigint(19) DEFAULT NULL COMMENT '创建者ID',
    `update_id` bigint(19) DEFAULT NULL COMMENT '更新者ID',
    PRIMARY KEY (`setting_store_id`)
) ENGINE=InnoDB COMMENT='';


