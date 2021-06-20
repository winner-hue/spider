package com.pj.spider.plugin;

import com.pj.spider.entity.BaseStruct;
import com.pj.spider.entity.ResponseData;
import com.pj.spider.entity.StructData;
import com.pj.spider.entity.Task;
import com.pj.spider.esservice.StructDataService;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;

public class StorageBase extends Spider {
    public StorageBase(Task task) {
        super(task);
    }

    public ResponseData processTask(HashMap map, StructDataService structDataService) {
        ResponseData responseData = new ResponseData();
        List<Boolean> storageList = new ArrayList<>();
        BaseStruct structData = this.task.getStructData();
        List<StructData> data = structData.getStructData();
        if (data == null) {
            responseData.setStatusCode(400);
            return responseData;
        }
        for (StructData tempData : data) {
            try {
                if (tempData.getContent() == null && tempData.getTitle() == null) {
                    storageList.add(false);
                    continue;
                }
                if (tempData.getPostTime() != null) {
                    tempData.setPostTime(tempData.getPostTime().replace(" ", "T"));
                }
                structDataService.save(tempData);
                storageList.add(true);
            } catch (Exception e) {
                storageList.add(false);
            }
        }
        responseData.setStorageList(storageList);
        return responseData;
    }
}
