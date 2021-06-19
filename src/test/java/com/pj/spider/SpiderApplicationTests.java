package com.pj.spider;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

@SpringBootTest
class SpiderApplicationTests {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Test
    void contextLoads() {
        stringRedisTemplate.opsForValue().set("hello", "java");
        String hello = stringRedisTemplate.opsForValue().get("hello");
        System.out.println(hello);
    }


}
