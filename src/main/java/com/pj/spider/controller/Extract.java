package com.pj.spider.controller;

import com.pj.spider.entity.ResponseData;
import com.pj.spider.entity.Task;
import com.pj.spider.plugin.ExtractBase;
import com.pj.spider.util.CommonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@Controller
public class Extract {
    @ResponseBody
    @RequestMapping("/extract")
    public ResponseData extract(@RequestBody Task task) {
        Object[] object = CommonUtil.getPlugin(task, "extract");
        ExtractBase extract = (ExtractBase) object[0];
        HashMap map = (HashMap) object[1];
        return extract.processTask(map);
    }
}
