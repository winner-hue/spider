package com.pj.spider.entity;

import lombok.Data;

import java.util.List;

@Data
public class BaseStruct {
    protected List<StructData> structData;   // 正文解析的内容
    protected List<UrlStructData> urlStructData; // 列表解析的内容及详情页面的URL
    protected List<String> listUrls; // 列表解析页面时，生成的所有列表页
}
