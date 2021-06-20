package com.pj.spider.dispatch;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DispatchConfig {
    public static String downloadServer;
    public static String extractServer;
    public static String dupServer;
    public static String storageServer;
    public static String dispatchTaskSize;
    public static String dispatchTaskCell;
    public static String dispatchTaskCoreThreadSize;
    public static String dispatchTaskThreadMaximumPoolSize;
    public static String dispatchTaskThreadKeepAliveTime;
    public static String dispatchTaskThreadQueueSize;
    public static String dispatchTaskSubThreadSize;
    public static String dispatchTaskClass;
    public static String dispatchTaskQueueName;
    public static String downloadOkhttp3ConnectionPool;
    public static String downloadOkhttp3KeepAlive;
    public static String downloadOkhttp3ConnectSocket;

    static {
        Properties props = new Properties();
        try {
            InputStream istream = DispatchConfig.class.getResourceAsStream("/application.properties");
            props.load(istream);
            istream.close();
            DispatchConfig.downloadServer = props.getProperty("download.server", "http://127.0.0.1:8080/download");
            DispatchConfig.extractServer = props.getProperty("extract.server", "http://127.0.0.1:8080/extract");
            DispatchConfig.dupServer = props.getProperty("dup.server", "http://127.0.0.1:8080/dup");
            DispatchConfig.storageServer = props.getProperty("storage.server", "http://127.0.0.1:8080/storage");
            DispatchConfig.dispatchTaskCell = props.getProperty("dispatch.task.cell", "5");
            DispatchConfig.dispatchTaskSize = props.getProperty("dispatch.task.size", "10");
            DispatchConfig.dispatchTaskCoreThreadSize = props.getProperty("dispatch.task.core.thread.size", "10");
            DispatchConfig.dispatchTaskThreadMaximumPoolSize = props.getProperty("dispatch.task.thread.maximum.pool.size", "");
            DispatchConfig.dispatchTaskThreadKeepAliveTime = props.getProperty("dispatch.task.thread.keep.alive.time", "1000");
            DispatchConfig.dispatchTaskThreadQueueSize = props.getProperty("dispatch.task.thread.queue.size", "10");
            DispatchConfig.dispatchTaskSubThreadSize = props.getProperty("dispatch.task.sub.thread.size", "5");
            DispatchConfig.dispatchTaskClass = props.getProperty("dispatch.task.class");
            DispatchConfig.dispatchTaskQueueName = props.getProperty("dispatch.task.queue.name");
            DispatchConfig.downloadOkhttp3ConnectionPool = props.getProperty("download.okhttp3.connection.pool", "30");
            DispatchConfig.downloadOkhttp3ConnectSocket = props.getProperty("download.okhttp3.connect.socket", "60");
            DispatchConfig.downloadOkhttp3KeepAlive = props.getProperty("download.okhttp3.keep.alive", "10000");
        } catch (IOException ignore) {
            System.exit(-1);
        }
    }
}
