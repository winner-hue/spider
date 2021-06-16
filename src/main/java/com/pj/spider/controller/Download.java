package com.pj.spider.controller;

import com.pj.spider.download.DownloadBase;
import com.pj.spider.entity.ResponseData;
import com.pj.spider.entity.Task;
import com.pj.spider.util.CommonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Download {
    @ResponseBody
    @RequestMapping("/download")
    public ResponseData pageSource(Task task) {
        ResponseData responseData = new ResponseData();
        responseData.setStatusCode(200);
        DownloadBase download = (DownloadBase) CommonUtil.getPlugin(task, "download");
        return download.processTask();
    }
}
