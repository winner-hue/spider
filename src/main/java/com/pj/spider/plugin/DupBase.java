package com.pj.spider.plugin;

import com.pj.spider.entity.*;
import com.pj.spider.util.CommonUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DupBase extends Spider {
    public DupBase(Task task) {
        super(task);
    }

    public ResponseData processTask(HashMap map, StringRedisTemplate stringRedisTemplate) {
        ResponseData responseData = new ResponseData();
        responseData.setStatusCode(200);
        Object dupField = map.get("dupField");
        if (dupField == null || dupField.equals("")) {
            return null;
        }

        List<DupResponse> dupResponses = new ArrayList<>();
        BaseStruct structData = this.task.getStructData();
        List<StructData> data = structData.getStructData();
        if (data != null && data.size() > 0) {
            for (StructData tempData : data) {
                DupResponse dup = new DupResponse();
                try {
                    StringBuilder builder = new StringBuilder();
                    for (String s : String.valueOf(dupField).split(",")) {
                        Field declaredField = tempData.getClass().getDeclaredField(s.trim());
                        declaredField.setAccessible(true);
                        Object object = declaredField.get(tempData);
                        builder.append(String.valueOf(object));
                    }
                    String md5 = CommonUtil.getMd5(builder.toString());
                    String s = stringRedisTemplate.opsForValue().get(md5);
                    if (s != null) {
                        dup.setDup(false);
                    } else {
                        dup.setDup(true);
                        stringRedisTemplate.opsForValue().set(md5, "1");
                    }
                    dup.setMd5(md5);
                    dupResponses.add(dup);
                } catch (Exception ignore) {
                }
            }
        }
        responseData.setDupResponses(dupResponses);
        return responseData;
    }
}
