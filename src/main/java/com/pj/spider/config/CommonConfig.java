package com.pj.spider.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.HashMap;

@Component
public class CommonConfig {
    public static InetAddress addr;
    public static HashMap<String, HashMap> mapper = new HashMap<>();
    public static HashMap<Integer, String> properties;
    public static Integer siteMapperCell;
    public static String downloadServer;
    public static String extractServer;
    public static String dupServer;

    @Value("${site.mapper.cell}")
    public void setSiteMapperCell(String siteMapperCell) {
        CommonConfig.siteMapperCell = Integer.parseInt(siteMapperCell);
    }

    @Value("${download.server}")
    public void setDownloadServer(String downloadServer) {
        CommonConfig.downloadServer = downloadServer;
    }

    @Value("${extract.server}")
    public void setExtractServer(String extractServer) {
        CommonConfig.extractServer = extractServer;
    }

    @Value("${dup.server}")
    public void setDupServer(String dupServer) {
        CommonConfig.dupServer = dupServer;
    }
}
