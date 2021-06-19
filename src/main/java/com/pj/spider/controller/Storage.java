package com.pj.spider.controller;

import com.pj.spider.entity.ResponseData;
import com.pj.spider.entity.Task;
import com.pj.spider.esservice.StructDataService;
import com.pj.spider.plugin.StorageBase;
import com.pj.spider.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@Controller
public class Storage {
    @Autowired
    StructDataService structDataService;

    @ResponseBody
    @RequestMapping("/storage")
    public ResponseData storage(@RequestBody Task task) {
        Object[] object = CommonUtil.getPlugin(task, "storage");
        StorageBase storage = (StorageBase) object[0];
        HashMap map = (HashMap) object[1];
        return storage.processTask(map, structDataService);
    }
}
