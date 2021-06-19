package com.pj.spider.entity;

import lombok.Data;

import java.util.List;

@Data
public class ResponseData {
    private int statusCode; // 返回状态码
    private String errorContent; // 返回错误信息
    /**
     * 下载中心使用
     */
    private int downloadStatusCode; // 返回网页下载的状态码
    private String pageSource; // 返回下载的源码
    /**
     * 提取中心使用
     */
    private BaseStruct baseStruct; // 返回结构化数据
    /**
     * 排重中心使用
     */
    private List<DupResponse> dupResponses; // 返回排重结果
    /**
     * 存储中心使用
     */
    private List<Boolean> storageList; // 存储结果
}
