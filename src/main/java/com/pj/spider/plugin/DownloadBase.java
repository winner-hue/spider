package com.pj.spider.plugin;

import com.pj.spider.entity.ResponseData;
import com.pj.spider.entity.Task;

public class DownloadBase extends Spider {
    public DownloadBase(Task task) {
        super(task);
    }

    public ResponseData processTask() {
        ResponseData responseData = new ResponseData();
        responseData.setStatusCode(200);

        return responseData;
    }
}
