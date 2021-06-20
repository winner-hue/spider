package com.pj.spider.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "struct", createIndex = true)
public class StructData {
    @Field(type = FieldType.Text)
    protected String title; // 标题
    @Field(type = FieldType.Text)
    protected String content; // 正文
    @Field(type = FieldType.Text)
    protected String postTime; // 发布时间
    @Field(type = FieldType.Text)
    protected String author; // 作者
    @Field(type = FieldType.Text)
    protected String source; // 来源
    @Id
    @Field(type = FieldType.Text)
    protected String id; // 唯一id
    @Field(type = FieldType.Text)
    protected String url; // 网页url
}
