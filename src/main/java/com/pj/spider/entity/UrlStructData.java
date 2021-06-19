package com.pj.spider.entity;

import lombok.Data;

/**
 * 列表页面解析的数据，供详情页使用  通过在配置文件中isUseListData配置使用，如果为1表示使用，不为1表示不使用
 */
@Data
public class UrlStructData {
    protected String url;
    protected String title;
    protected String author;
    protected String postTime;
    protected String Source;
    public boolean isDetail;
}
