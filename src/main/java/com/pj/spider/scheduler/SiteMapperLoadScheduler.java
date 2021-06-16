package com.pj.spider.scheduler;

import com.pj.spider.config.CommonConfig;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 定时读取siteMapper配置
 * by wangpj 2021-05-17
 */
@Configuration
public class SiteMapperLoadScheduler {

    /**
     * siteMapper定时任务：
     */
    @Bean
    public JobDetail siteMapperJobDetail() {
        return JobBuilder.newJob(SiteMapperLoadJob.class)
                .withIdentity("site_mapper_timer_task", "siteMapper")
                .storeDurably() //即使没有Trigger关联时，也不需要删除该JobDetail
                .build();
    }

    /**
     * siteMapper定时任务：
     * （触发器）
     */
    @Bean
    public Trigger siteMapperTrigger() {
        return TriggerBuilder.newTrigger().forJob(siteMapperJobDetail())
                .withIdentity("site_mapper_trigger", "siteMapper")
                .startNow()//立即生效
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(CommonConfig.siteMapperCell)
                        .repeatForever()).build();

    }
}
