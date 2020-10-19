-- 消息所属业务线配置表
CREATE TABLE `amf_business_line_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `business_line_id` varchar(20) NOT NULL COMMENT '业务线ID',
  `business_line_name` varchar(30) NOT NULL COMMENT '业务线名称',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '是否删除标记：0-否，1-是',
  `created_by` varchar(20) NOT NULL COMMENT '创建人ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认值为系统当前时间',
  `updated_by` varchar(20) NOT NULL COMMENT '最后修改人ID',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间，默认值为系统当前时间，数据修改时自动更新',
  PRIMARY KEY (`id`),
  UNIQUE KEY `udx_business_line_config` (`business_line_id`) COMMENT '业务线ID唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息所属业务线配置表';

-- 消息来源系统配置表
CREATE TABLE `amf_source_system_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `business_line_config_id` bigint NOT NULL COMMENT '业务线主键ID',
  `source_system_id` int NOT NULL COMMENT '来源系统编号，初始值为1000',
  `source_system_name` varchar(30) NOT NULL COMMENT '来源系统名称',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '是否删除标记：0-否，1-是',
  `created_by` varchar(20) NOT NULL COMMENT '创建人ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认值为系统当前时间',
  `updated_by` varchar(20) NOT NULL COMMENT '最后修改人ID',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间，默认值为系统当前时间，数据修改时自动更新',
  PRIMARY KEY (`id`),
  UNIQUE KEY `udx_source_system_config` (`source_system_id`) COMMENT '来源系统编号唯一索引',
  KEY `idx_source_system_config` (`business_line_id`) COMMENT '来源系统普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息来源系统配置表';

-- 消息配置表
CREATE TABLE `amf_message_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `business_line_id` varchar(20)  NOT NULL COMMENT '业务线ID',
  `business_line_name` varchar(30)  NOT NULL COMMENT '业务线名称',
  `souce_system_id` int NOT NULL COMMENT '来源系统编号，初始值为1000',
  `source_system_name` varchar(30)  NOT NULL COMMENT '来源系统名称',
  `message_id` int NOT NULL COMMENT '消息ID',
  `message_name` varchar(30)  NOT NULL COMMENT '消息名称',
  `message_status` tinyint unsigned NOT NULL DEFAULT 1 COMMENT '消息状态：0-停用，1-启用',
  `callback_url` varchar(200)  DEFAULT NULL COMMENT '消息发送结果回调地址',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '是否删除标记：0-否，1-是',
  `created_by` varchar(20)  NOT NULL COMMENT '创建人ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认值为系统当前时间',
  `updated_by` varchar(20)  NOT NULL COMMENT '最后修改人ID',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间，默认值为系统当前时间，数据修改时自动更新',
  PRIMARY KEY (`id`),
  UNIQUE KEY `udx_message_config` (`message_id`) COMMENT '消息配置唯一索引',
  KEY `idx_message_config` (`souce_system_id`) COMMENT '消息配置普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息配置表';

-- 消息转发配置表
CREATE TABLE `amf_message_forwarding_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `message_config_id` bigint NOT NULL COMMENT '消息配置主键',
  `target_system` varchar(30) NOT NULL COMMENT '目标系统名称',
  `forwarding_way` char(2) NOT NULL COMMENT '转发到目标系统的方式：01-Http，02-Kafka，03-RocketMQ',
  `target_address` varchar(200) NOT NULL COMMENT '目标地址：http接口地址/Kafka Topic/RocketMQ Topic:Tag（英文冒号分隔）',
  `retry_times` tinyint NOT NULL DEFAULT 3 COMMENT '重试次数',
  `callback_required` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '是否需要回调，1-是，0-否',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '是否删除标记：0-否，1-是',
  `created_by` varchar(20) NOT NULL COMMENT '创建人ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认值为系统当前时间',
  `updated_by` varchar(20)  NOT NULL COMMENT '最后修改人ID',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间，默认值为系统当前时间，数据修改时自动更新',
  PRIMARY KEY (`id`),
  KEY `idx_message_forwarding_config` (`message_config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息转发配置表';

-- 消息表
CREATE TABLE `amf_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `message_no` varchar(32) NOT NULL COMMENT '消息流水号',
  `message_id` int NOT NULL COMMENT '消息ID',
  `message_keyword` varchar(32) NOT NULL COMMENT '消息关键字',
  `http_headers` text COMMENT 'http header参数，json格式',
  `message_content` text NOT NULL COMMENT '消息内容',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认值为系统当前时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间，默认值为系统当前时间，数据修改时自动更新',
  PRIMARY KEY (`id`),
  UNIQUE KEY `udx_message_no` (`message_no`),
  KEY `idx_message_keyword` (`message_keyword`),
  KEY `idx_message_id` (`message_id`),
  KEY `idx_message_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息表';

-- 消息转发结果表
CREATE TABLE `amf_message_forwarding` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `message_no` varchar(32)  NOT NULL COMMENT '消息流水号',
  `message_keyword` varchar(32)  NOT NULL COMMENT '消息关键字',
  `message_id` int NOT NULL COMMENT '消息ID',
  `forwarding_id` bigint NOT NULL COMMENT '消息转发配置主键',
  `target_system` varchar(30) NOT NULL COMMENT '目标系统名称',
  `forwarding_result` tinyint unsigned NOT NULL COMMENT '转发结果：0-失败，1-成功',
  `forwarding_success_time` timestamp NULL DEFAULT NULL COMMENT '转发成功时间',
  `retry_times` tinyint NOT NULL DEFAULT '0' COMMENT '重试次数',
  `callback_required` tinyint unsigned NOT NULL COMMENT '是否需要回调，1-是，0-否',
  `callback_result` tinyint unsigned DEFAULT NULL COMMENT '回调结果：0-失败，1-成功',
  `callback_retry_times` tinyint DEFAULT NULL COMMENT '回调重试次数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认值为系统当前时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间，默认值为系统当前时间，数据修改时自动更新',
  PRIMARY KEY (`id`),
  KEY `idx_message_forwarding_no` (`message_no`),
  KEY `idx_message_forwarding_keyword` (`message_keyword`),
  KEY `idx_message_forwarding_id` (`message_id`),
  KEY `idx_message_forwarding_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息转发结果表';

-- 消息历史表
CREATE TABLE `amf_message_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `message_no` varchar(32) NOT NULL COMMENT '消息流水号',
  `business_line_id` varchar(20) NOT NULL COMMENT '业务线ID',
  `source_system_id` int NOT NULL COMMENT '消息来源系统ID',
  `message_id` int NOT NULL COMMENT '消息ID',
  `message_keyword` varchar(32) NOT NULL COMMENT '消息关键字',
  `http_headers` text COMMENT 'http header参数，json格式',
  `message_content` text NOT NULL COMMENT '消息内容',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认值为系统当前时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间，默认值为系统当前时间，数据修改时自动更新',
  PRIMARY KEY (`id`),
  UNIQUE KEY `udx_message_history_no` (`message_no`),
  KEY `idx_message_history_keyword` (`message_keyword`),
  KEY `idx_message_history_id` (`message_id`),
  KEY `idx_message_history_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息表';

-- 消息转发结果历史表
CREATE TABLE `amf_message_forwarding_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `message_no` varchar(32)  NOT NULL COMMENT '消息流水号',
  `message_keyword` varchar(32)  NOT NULL COMMENT '消息关键字',
  `message_id` int NOT NULL COMMENT '消息ID',
  `forwarding_id` bigint NOT NULL COMMENT '消息转发配置主键',
  `target_system` varchar(30) NOT NULL COMMENT '目标系统名称',
  `forwarding_result` tinyint unsigned NOT NULL COMMENT '转发结果：0-失败，1-成功',
  `forwarding_success_time` timestamp NULL DEFAULT NULL COMMENT '转发成功时间',
  `retry_times` tinyint NOT NULL DEFAULT '0' COMMENT '重试次数',
  `callback_required` tinyint unsigned NOT NULL COMMENT '是否需要回调，1-是，0-否',
  `callback_result` tinyint unsigned DEFAULT NULL COMMENT '回调结果：0-失败，1-成功',
  `callback_retry_times` tinyint DEFAULT NULL COMMENT '回调重试次数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认值为系统当前时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间，默认值为系统当前时间，数据修改时自动更新',
  PRIMARY KEY (`id`),
  KEY `idx_message_forwarding_history_no` (`message_no`),
  KEY `idx_message_forwarding_history_keyword` (`message_keyword`),
  KEY `idx_message_forwarding_history_id` (`message_id`),
  KEY `idx_message_forwarding_history_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息转发结果表';