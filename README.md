# spider
java spider framework

使用软件：mysql 8.0.22(任务库), redis 6.2.4(排重库), es 7.13.2(存储库), kibana 7.13.2(可视化)

任务表 tasks：
     ~~~ mysql
     
     CREATE TABLE `tasks` (
       `id` int unsigned NOT NULL AUTO_INCREMENT,
       `taskType` int NOT NULL COMMENT '任务类型',
       `taskUrl` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '任务url',
       `taskStatus` bigint NOT NULL COMMENT '任务状态',
       `taskLimitTime` int DEFAULT '3600' COMMENT '任务最长运行时间',
       `taskDotime` datetime NOT NULL COMMENT '任务执行时间',
       `taskQueueName` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '任务队列名',
       `taskUpdateTime` datetime DEFAULT NULL COMMENT '任务更新时间',
       `taskOwner` varchar(20) DEFAULT NULL COMMENT '任务添加人',
       `taskComputerIp` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '执行任务的ip',
       `taskComputerName` varchar(50) DEFAULT NULL COMMENT '执行任务的电脑名称',
       `taskId` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '执行任务的id',
       `taskUseTime` int DEFAULT NULL COMMENT '执行任务耗时',
       `taskLogMark` bigint DEFAULT NULL COMMENT '是否开启调度中心每条任务的总任务，失败任务的日志记录,0不开启，1开启（开启之后将记录改任务跑到的所有url）',
       `taskCell` int NOT NULL DEFAULT '3600',
       PRIMARY KEY (`id`),
       UNIQUE KEY `taskId` (`taskId`)
     ) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

    demo task
    INSERT INTO `tasks` (`id`, `taskType`, `taskUrl`, `taskStatus`, `taskLimitTime`, `taskDotime`, `taskQueueName`, `taskUpdateTime`, `taskOwner`, `taskComputerIp`, `taskComputerName`, `taskId`, `taskUseTime`, `taskLogMark`, `taskCell`)
    VALUES
    	(1, 1, 'https://www.csdn.net/', 0, 3600, '2021-06-20 21:11:06', 'test', '2021-06-20 20:11:06', NULL, '192.168.1.115', null, 'f9751de431104b125f48dd79cc55822a', 0, 0, 3600);
    	
使用：
    启动springboot SpiderApplication
    再启动DispatchStart
    即可
