package com.pj.spider.entity;

import lombok.Data;

@Data
public class ResponseData {
    /**
     * 下载中心使用
     */
    private int statusCode; // 返回状态码
    private String errorContent; // 返回错误信息
    private int downloadStatusCode; // 返回网页下载的状态码
    private String pageSource; // 返回下载的源码

    /**
     * 提取中心使用
     */

    /**
     * 排重中心使用
     */

    /**
     * 存储中心使用
     */
}
