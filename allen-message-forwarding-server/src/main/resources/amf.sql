-- 消息所属业务线配置表
CREATE TABLE `amf_business_line_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `business_line_id` varchar(20) NOT NULL COMMENT '业务线ID',
  `business_line_name` varchar(30) NOT NULL COMMENT '业务线名称',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '是否删除标记：0-否，1-是',
  `created_by` varchar(20) NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认值为系统当前时间',
  `updated_by` varchar(20) NOT NULL COMMENT '最后修改人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间，默认值为系统当前时间，数据修改时自动更新',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_business_line_config` (`business_line_id`) COMMENT '业务线ID唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息所属业务线配置表';

-- 消息来源系统配置表
CREATE TABLE `amf_source_system_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `business_line_config_id` bigint NOT NULL COMMENT '业务线主键ID',
  `source_system_id` int NOT NULL COMMENT '来源系统编号，初始值为1000',
  `source_system_name` varchar(30) NOT NULL COMMENT '来源系统名称',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '是否删除标记：0-否，1-是',
  `created_by` varchar(20) NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认值为系统当前时间',
  `updated_by` varchar(20) NOT NULL COMMENT '最后修改人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间，默认值为系统当前时间，数据修改时自动更新',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_source_system_config` (`source_system_id`) COMMENT '来源系统编号唯一索引',
  KEY `idx_source_system_config` (`business_line_config_id`) COMMENT '来源系统普通索引'
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
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认值为系统当前时间',
  `updated_by` varchar(20)  NOT NULL COMMENT '最后修改人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间，默认值为系统当前时间，数据修改时自动更新',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_message_config` (`message_id`) COMMENT '消息配置唯一索引',
  KEY `idx_message_config` (`souce_system_id`) COMMENT '消息配置普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息配置表';

-- 消息转发配置表
CREATE TABLE `amf_message_forwarding_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `message_config_id` bigint NOT NULL COMMENT '消息配置主键',
  `target_system` varchar(30) NOT NULL COMMENT '目标系统名称',
  `forwarding_way` char(2) NOT NULL COMMENT '转发到目标系统的方式：01-Http，02-Kafka，03-RocketMQ',
  `target_address` varchar(200) NOT NULL COMMENT '目标地址：http接口地址/Kafka Topic/RocketMQ Topic:Tag（英文冒号分隔）',
  `retry_times` tinyint NOT NULL DEFAULT 3 COMMENT '重试次数，适用于转发及回调',
  `callback_required` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '是否需要回调，1-是，0-否',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '是否删除标记：0-否，1-是',
  `created_by` varchar(20) NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认值为系统当前时间',
  `updated_by` varchar(20)  NOT NULL COMMENT '最后修改人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间，默认值为系统当前时间，数据修改时自动更新',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_message_forwarding_config` (`message_config_id`, `forwarding_way`, `target_address`) COMMENT '消息配置唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息转发配置表';

-- 消息表
CREATE TABLE `amf_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `message_no` varchar(32) NOT NULL COMMENT '消息流水号',
  `message_keyword` varchar(32) NOT NULL COMMENT '消息关键字',
  `message_id` int NOT NULL COMMENT '消息ID',
  `business_line_id` varchar(20)  NOT NULL COMMENT '业务线ID',
  `source_system_id` int NOT NULL COMMENT '来源系统编号，初始值为1000',
  `http_headers` text COMMENT 'http header参数，json格式',
  `message_content` text NOT NULL COMMENT '消息内容',
  `forwarding_total_amount` int NOT NULL DEFAULT 0 COMMENT '应转发总数量',
  `forwarding_success_amount` int NOT NULL DEFAULT 0 COMMENT '转发成功数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认值为系统当前时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间，默认值为系统当前时间，数据修改时自动更新',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_message` (`message_no`),
  KEY `idx_message_keyword` (`message_keyword`),
  KEY `idx_message_id` (`message_id`),
  KEY `idx_message_source_system` (`source_system_id`),
  KEY `idx_message_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息表';

-- 消息转发结果表
CREATE TABLE `amf_message_forwarding` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `message_no` varchar(32)  NOT NULL COMMENT '消息流水号',
  `message_keyword` varchar(32)  NOT NULL COMMENT '消息关键字',
  `message_id` int NOT NULL COMMENT '消息ID',
  `forwarding_id` bigint NOT NULL COMMENT '消息转发配置主键',
  `forwarding_way` char(2) NOT NULL COMMENT '转发到目标系统的方式：01-Http，02-Kafka，03-RocketMQ',
  `target_address` varchar(200) NOT NULL COMMENT '目标地址：http接口地址/Kafka Topic/RocketMQ Topic:Tag（英文冒号分隔）',
  `max_retry_times` tinyint NOT NULL DEFAULT 3 COMMENT '最大重试次数，同时适用于转发重试及回调重试',
  `callback_required` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '是否需要回调，1-是，0-否',
  `callback_url` varchar(200)  DEFAULT NULL COMMENT '消息发送结果回调地址',
  `forwarding_status` tinyint unsigned NOT NULL COMMENT '转发处理状态：0-处理中，1-重试中，2-完成',
  `forwarding_result` tinyint unsigned NULL DEFAULT NULL COMMENT '转发结果：0-失败，1-成功',
  `forwarding_success_time` datetime NULL DEFAULT NULL COMMENT '转发成功时间',
  `forwarding_retry_times` tinyint NOT NULL DEFAULT 0 COMMENT '转发重试次数',
  `callback_status` tinyint unsigned DEFAULT NULL COMMENT '回调处理状态：0-处理中，1-重试中，2-完成',
  `callback_result` tinyint unsigned DEFAULT NULL COMMENT '回调结果：0-失败，1-成功',
  `callback_success_time` datetime NULL DEFAULT NULL COMMENT '回调成功时间',
  `callback_retry_times` tinyint DEFAULT NULL COMMENT '回调重试次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认值为系统当前时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间，默认值为系统当前时间，数据修改时自动更新',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_message_forwarding` (`message_no`, `forwarding_id`),
  KEY `idx_forwarding_message_keyword` (`message_keyword`),
  KEY `idx_forwarding_message_id` (`message_id`),
  KEY `idx_forwarding_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息转发结果表';

-- 消息历史表
CREATE TABLE `amf_message_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `message_no` varchar(32) NOT NULL COMMENT '消息流水号',
  `message_keyword` varchar(32) NOT NULL COMMENT '消息关键字',
  `message_id` int NOT NULL COMMENT '消息ID',
  `business_line_id` varchar(20)  NOT NULL COMMENT '业务线ID',
  `source_system_id` int NOT NULL COMMENT '来源系统编号，初始值为1000',
  `http_headers` text COMMENT 'http header参数，json格式',
  `message_content` text NOT NULL COMMENT '消息内容',
  `forwarding_total_amount` int NOT NULL DEFAULT 0 COMMENT '应转发总数量',
  `forwarding_success_amount` int NOT NULL DEFAULT 0 COMMENT '转发成功数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认值为系统当前时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间，默认值为系统当前时间，数据修改时自动更新',
  PRIMARY KEY (`id`),
  UNIQUE KEY `udx_history_message_no` (`message_no`),
  KEY `idx_history_message_keyword` (`message_keyword`),
  KEY `idx_history_message_id` (`message_id`),
  KEY `idx_message_source_system` (`source_system_id`),
  KEY `idx_history_message_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息历史表';

-- 消息转发结果历史表
CREATE TABLE `amf_message_forwarding_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `message_no` varchar(32)  NOT NULL COMMENT '消息流水号',
  `message_keyword` varchar(32)  NOT NULL COMMENT '消息关键字',
  `message_id` int NOT NULL COMMENT '消息ID',
  `forwarding_id` bigint NOT NULL COMMENT '消息转发配置主键',
  `forwarding_way` char(2) NOT NULL COMMENT '转发到目标系统的方式：01-Http，02-Kafka，03-RocketMQ',
  `target_address` varchar(200) NOT NULL COMMENT '目标地址：http接口地址/Kafka Topic/RocketMQ Topic:Tag（英文冒号分隔）',
  `max_retry_times` tinyint NOT NULL DEFAULT 3 COMMENT '最大重试次数，同时适用于转发重试及回调重试',
  `callback_required` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '是否需要回调，1-是，0-否',
  `callback_url` varchar(200)  DEFAULT NULL COMMENT '消息发送结果回调地址',
  `forwarding_status` tinyint unsigned NOT NULL COMMENT '转发处理状态：0-处理中，1-重试中，2-完成',
  `forwarding_result` tinyint unsigned NULL DEFAULT NULL COMMENT '转发结果：0-失败，1-成功',
  `forwarding_success_time` datetime NULL DEFAULT NULL COMMENT '转发成功时间',
  `forwarding_retry_times` tinyint NOT NULL DEFAULT 0 COMMENT '转发重试次数',
  `callback_status` tinyint unsigned DEFAULT NULL COMMENT '回调处理状态：0-处理中，1-重试中，2-完成',
  `callback_result` tinyint unsigned DEFAULT NULL COMMENT '回调结果：0-失败，1-成功',
  `callback_success_time` datetime NULL DEFAULT NULL COMMENT '回调成功时间',
  `callback_retry_times` tinyint DEFAULT NULL COMMENT '回调重试次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认值为系统当前时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间，默认值为系统当前时间，数据修改时自动更新',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_message_forwarding` (`message_no`, `forwarding_id`),
  KEY `idx_history_forwarding_message_keyword` (`message_keyword`),
  KEY `idx_history_forwarding_message_id` (`message_id`),
  KEY `idx_history_forwarding_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息转发结果历史表';