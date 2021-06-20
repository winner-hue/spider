package com.pj.spider.util;

import com.pj.spider.dispatch.DispatchConfig;
import okhttp3.*;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 用于内部服务下载
 */
public class DownloadUtil {
    private static OkHttpClient client;
    public static volatile DownloadUtil instance;


    public static DownloadUtil getInstance() {
        if (instance == null) {
            synchronized (DownloadUtil.class) {
                if (instance == null) {
                    instance = new DownloadUtil();
                }
            }
        }
        return instance;
    }

    public DownloadUtil() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectionPool(new ConnectionPool(Integer.parseInt(DispatchConfig.downloadOkhttp3ConnectionPool), Integer.parseInt(DispatchConfig.downloadOkhttp3KeepAlive), TimeUnit.SECONDS));
        builder.connectTimeout(Integer.parseInt(DispatchConfig.downloadOkhttp3ConnectSocket), TimeUnit.SECONDS);
        client = builder.build();
    }

    public String get(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String post(String url, String postBody) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(postBody, MediaType.parse("application/json; charset=utf-8")))
                .build();
        try {
            Response response = client.newCall(request).execute();
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
