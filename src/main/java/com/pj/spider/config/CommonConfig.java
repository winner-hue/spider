package com.pj.spider.config;

import com.pj.spider.util.CommonUtil;
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
    public static String storageServer;
    public static String defaultUserAgent;
    public static Integer connectionPool;
    public static Integer connectionSocket;
    public static Long keepAlive;

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

    @Value("${storage.server}")
    public void setStorageServer(String storageServer) {
        CommonConfig.storageServer = storageServer;
    }

    @Value("${download.default.useragent}")
    public void setDefaultUserAgent(String defaultUserAgent) {
        CommonConfig.defaultUserAgent = defaultUserAgent;
    }

    @Value("${download.okhttp3.connectsocket}")
    public void setConnectionSocket(String connectionSocket) {
        CommonConfig.connectionSocket = Integer.parseInt(connectionSocket);
    }

    @Value("${download.okhttp3.connectionpool}")
    public void setConnectionPool(String connectionPool) {
        CommonConfig.connectionPool = Integer.parseInt(connectionPool);
    }

    @Value("${download.okhttp3.keppalive}")
    public void setKeepAlive(String keepAlive) {
        CommonConfig.keepAlive = Long.parseLong(keepAlive);
    }
}
