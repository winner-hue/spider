package com.pj.spider.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * author wangpj
 * create 2021-05-11
 */
@Data
public class Task {
    /**
     * 以下字段用于和数据库表对应
     */
    private Integer taskType; //任务类型 1表示博客，2表示论坛，3表示公众号
    private String taskUrl; //任务url
    private Integer taskStatus; //任务状态  0表示停止运行，1表示正在运行
    private Integer taskCell; //任务执行间隔  以秒记
    private Integer taskLimitTime; //任务最大执行时间，及超时时间 以秒记
    private Date taskDoTime; //任务执行时间
    private String taskQueueName; //任务队列名  用于区分不同队列
    private Date taskUpdateTime; //任务更新时间
    private String taskOwner; //任务添加者
    private String taskComputerIp; //执行任务的机器ip
    private String taskComputerName;//执行任务的机器名称
    private String taskId; // md5 任务id
    private String id; // 任务表中的自增ID
    private String taskUseTime; // 任务执行耗时
    private Integer taskLogMark; //是否开启调度中心每条任务的总任务，失败任务的日志记录,0不开启，1开启（开启之后将记录改任务跑到的所有url）
    /**
     * 以下字段用于任务的监测
     */
    private List<Task> taskFailNum; // 失败的任务数
    private Set<Task> taskListNum; // 列表任务数
    private Set<Task> taskDetailNum; //详情任务数
    private Integer isRun; //列表任务或者详情任务是否运行过，用于排掉重复任务,0表示运行过，1表示未运行

    /**
     * 以下字段运行过程中生成使用
     */
    private String pageSource;  // 网页页面源码
    private BaseStruct structData; // 生成的结构化数据
    private UrlStructData urlStructData; // 子任务携带的列表页的解析数据，供详情页面解析使用
}
