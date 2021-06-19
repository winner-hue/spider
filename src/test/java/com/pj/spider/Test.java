package com.pj.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pj.spider.entity.BaseStruct;
import com.pj.spider.entity.StructData;
import com.pj.spider.entity.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        Task task = new Task();
        task.setTaskType(2);
        task.setTaskUrl("https://blog.csdn.net/l1028386804/article/details/117917710");
        BaseStruct baseStruct = new BaseStruct();
        StructData structData = new StructData();
        structData.setTitle("123");
        structData.setContent("456");
        List<StructData> list = new ArrayList<>();
        list.add(structData);
        baseStruct.setStructData(list);
        task.setStructData(baseStruct);
        System.out.println(JSON.toJSON(task));
    }
}
