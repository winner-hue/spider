package com.pj.spider.controller;

import com.pj.spider.entity.ResponseData;
import com.pj.spider.entity.Task;
import com.pj.spider.plugin.DupBase;
import com.pj.spider.util.CommonUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Controller
public class Dup {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ResponseBody
    @RequestMapping("/dup")
    public ResponseData dup(@RequestBody Task task) {
        Object[] object = CommonUtil.getPlugin(task, "dup");
        DupBase dup = (DupBase) object[0];
        HashMap map = (HashMap) object[1];
        return dup.processTask(map, stringRedisTemplate);
    }
}
