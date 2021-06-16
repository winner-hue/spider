package com.pj.spider.plugin;

import com.alibaba.fastjson.JSONObject;
import com.pj.spider.config.CommonConfig;
import com.pj.spider.entity.ResponseData;
import com.pj.spider.entity.Task;
import com.pj.spider.util.DownloadUtil;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pj.JUA;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DownloadBase extends Spider {
    public DownloadBase(Task task) {
        super(task);
    }

    public ResponseData processTask(HashMap map) {
        ResponseData responseData = new ResponseData();
        responseData.setStatusCode(200);
        download(responseData, map);
        return responseData;
    }

    public void download(ResponseData responseData, HashMap map) {
        Object proxy_host = map.get("proxy_host");
        Object proxy_port = map.get("proxy_port");
        Object proxy_user = map.get("proxy_user");
        Object proxy_pwd = map.get("proxy_pwd");
        Object proxy_url = map.get("proxy_url");
        Object headers = map.get("headers");
        Object is_change_ua = map.get("is_change_ua");
        String userAgent = "";
        if (is_change_ua != null && (Integer) is_change_ua == 1) {
            JUA jua = new JUA();
            userAgent = jua.getUserAgent();
        } else {
            if (CommonConfig.defaultUserAgent != null) {
                userAgent = CommonConfig.defaultUserAgent;
            } else {
                userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.106 Safari/537.36";
            }
        }

        if (proxy_host == null) {
            if (proxy_url == null) {
                download(responseData, userAgent, headers == null ? "" : (String) headers);
            } else {
                String s = DownloadUtil.getInstance().get((String) proxy_url);
                JSONObject jsonObject = JSONObject.parseObject(s);
                proxy_host = jsonObject.get("proxy_host");
                proxy_port = jsonObject.get("proxy_port");
                proxy_user = jsonObject.get("proxy_user");
                proxy_pwd = jsonObject.get("proxy_pwd");
                doDownload(responseData, userAgent, headers == null ? "" : (String) headers,
                        proxy_host, proxy_port, proxy_user, proxy_pwd);
            }
        } else {
            doDownload(responseData, userAgent, headers == null ? "" : (String) headers,
                    proxy_host, proxy_port, proxy_user, proxy_pwd);
        }
    }

    public OkHttpClient initClient(String proxy_host, String proxy_port, String proxy_user, String proxy_pwd) {
        if (proxy_user != null) {
            return new OkHttpClient.Builder()
                    .connectTimeout(CommonConfig.connectionSocket, TimeUnit.SECONDS)
                    .protocols(Arrays.asList(Protocol.HTTP_1_1, Protocol.HTTP_2))
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy_host, Integer.parseInt(proxy_port))))
                    .proxyAuthenticator(new Authenticator() {
                        @Nullable
                        @Override
                        public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
                            String credential = Credentials.basic(proxy_user, proxy_pwd);
                            return response.request().newBuilder()
                                    .header("Proxy-Authorization", credential)
                                    .build();
                        }
                    })
                    .build();
        } else if (proxy_host != null) {
            return new OkHttpClient.Builder()
                    .connectTimeout(CommonConfig.connectionSocket, TimeUnit.SECONDS)
                    .protocols(Arrays.asList(Protocol.HTTP_1_1, Protocol.HTTP_2))
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy_host, Integer.parseInt(proxy_port))))
                    .build();

        } else {
            return new OkHttpClient.Builder()
                    .connectTimeout(CommonConfig.connectionSocket, TimeUnit.SECONDS)
                    .protocols(Arrays.asList(Protocol.HTTP_1_1, Protocol.HTTP_2))
                    .build();
        }

    }

    public Request initRequest(String userAgent, String headers) {
        Headers.Builder builder = new Headers.Builder();
        builder.add("User-Agent", userAgent);
        String[] split = headers.split("##");
        for (String s : split) {
            String[] tempSplit = s.split("=");
            builder.add(tempSplit[0], tempSplit[1]);
        }
        String url = this.task.getTaskUrl();
        if (url.contains("@@@")) {
            String[] urls = url.split("@@@");
            url = urls[0];
            String postBody = urls[1];
            return new Request.Builder()
                    .url(url)
                    .headers(builder.build())
                    .post(RequestBody.create(postBody, MediaType.parse("application/json; charset=utf-8")))
                    .build();

        } else {
            return new Request.Builder()
                    .url(url)
                    .headers(builder.build())
                    .get()
                    .build();
        }
    }

    public void download(ResponseData responseData, String userAgent, String headers) {
        OkHttpClient client = initClient(null, null, null, null);
        Request request = initRequest(userAgent, headers);
        Call call = client.newCall(request);
        try {
            Response execute = call.execute();
            String pageSource = Objects.requireNonNull(execute.body()).string();
            int code = execute.code();
            responseData.setPageSource(pageSource);
            responseData.setDownloadStatusCode(code);
            client.connectionPool().evictAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void download(ResponseData responseData, String userAgent, String headers,
                         String proxy_host, String proxy_port, String proxy_user, String proxy_pwd) {
        OkHttpClient client = initClient(proxy_host, proxy_port, proxy_user, proxy_pwd);
        Request request = initRequest(userAgent, headers);
        Call call = client.newCall(request);
        try {
            Response execute = call.execute();
            String pageSource = Objects.requireNonNull(execute.body()).string();
            int code = execute.code();
            responseData.setPageSource(pageSource);
            responseData.setDownloadStatusCode(code);
            client.connectionPool().evictAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void download(ResponseData responseData, String userAgent, String headers,
                         String proxy_host, String proxy_port) {
        OkHttpClient client = initClient(proxy_host, proxy_port, null, null);
        Request request = initRequest(userAgent, headers);
        Call call = client.newCall(request);
        try {
            Response execute = call.execute();
            String pageSource = Objects.requireNonNull(execute.body()).string();
            int code = execute.code();
            responseData.setPageSource(pageSource);
            responseData.setDownloadStatusCode(code);
            client.connectionPool().evictAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doDownload(ResponseData responseData, String userAgent, String headers,
                           Object proxy_host, Object proxy_port, Object proxy_user, Object proxy_pwd) {
        if (proxy_user == null) {
            download(responseData, userAgent, headers,
                    (String) proxy_host, String.valueOf(proxy_port));
        } else {
            download(responseData, userAgent, headers,
                    (String) proxy_host, String.valueOf(proxy_port), (String) proxy_user, (String) proxy_pwd);
        }
    }

}
