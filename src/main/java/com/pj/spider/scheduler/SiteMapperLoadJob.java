package com.pj.spider.scheduler;

import com.pj.spider.util.CommonUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class SiteMapperLoadJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        CommonUtil.loadSiteMapper();
    }
}
