package com.pj.spider.controller;

import ch.qos.logback.core.db.dialect.DBUtil;
import com.alibaba.fastjson.JSONObject;
import com.pj.spider.config.CommonConfig;
import com.pj.spider.entity.ResponseData;
import com.pj.spider.entity.Task;
import com.pj.spider.plugin.DownloadBase;
import com.pj.spider.util.CommonUtil;
import com.pj.spider.util.DownloadUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@Controller
public class Download {
    @ResponseBody
    @RequestMapping("/download")
    public ResponseData pageSource(Task task) {
        ResponseData responseData = new ResponseData();
        responseData.setStatusCode(200);
        Object[] object = CommonUtil.getPlugin(task, "download");
        DownloadBase download = (DownloadBase) object[0];
        HashMap map = (HashMap) object[1];
        return download.processTask(map);
    }

    @ResponseBody
    @RequestMapping("/download2")
    public ResponseData pageSource2(Task task) {
        ResponseData responseData = new ResponseData();
        String s = DownloadUtil.getInstance().get("http://www.baidu.com");
        System.out.println(s);
        return responseData;
    }
}
