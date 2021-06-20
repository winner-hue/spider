package com.pj.spider.dispatch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pj.spider.entity.*;
import com.pj.spider.util.CommonUtil;
import com.pj.spider.util.DownloadUtil;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class DispatchBase implements Runnable {
    protected ConcurrentLinkedDeque<Task> linkedList;
    protected ConcurrentHashMap<String, Boolean> map;
    protected Task task;

    public DispatchBase(ConcurrentHashMap<String, Boolean> map, ConcurrentLinkedDeque<Task> linkedList, Task task) {
        this.linkedList = linkedList;
        this.map = map;
        this.task = task;
    }

    protected void processDownload(String postTask) {
        String downloadContent = DownloadUtil.getInstance().post(DispatchConfig.downloadServer, postTask);
        ResponseData responseData = JSON.toJavaObject(JSONObject.parseObject(downloadContent), ResponseData.class);
        if (responseData.getStatusCode() == 200) {
            this.task.setPageSource(responseData.getPageSource());
        }
    }

    protected void processExtract(String content, String postTask) {
        String extractContent = DownloadUtil.getInstance().post(DispatchConfig.extractServer, content);
        ResponseData extractResponse = JSON.toJavaObject(JSONObject.parseObject(extractContent), ResponseData.class);
        if (extractResponse.getStatusCode() == 200) {
            BaseStruct baseStruct = extractResponse.getBaseStruct();
            List<UrlStructData> urlStructData = baseStruct.getUrlStructData();
            List<String> listUrls = baseStruct.getListUrls();
            List<StructData> structData = baseStruct.getStructData();
            if (structData != null && structData.size() > 0) {
                for (StructData data : structData) {
                    if (data.getUrl() == null) {
                        data.setUrl(this.task.getTaskUrl());
                    }
                }
            }
            if (listUrls != null && listUrls.size() > 0) {
                for (String url : listUrls) {
                    String md5 = CommonUtil.getMd5(url);
                    if (map.get(md5)) {
                        continue;
                    }
                    map.put(md5, true);
                    Task task = JSON.toJavaObject(JSONObject.parseObject(postTask), Task.class);
                    task.setTaskUrl(url);
                    linkedList.addFirst(task);
                }
            }
            if (urlStructData != null && urlStructData.size() > 0) {
                for (UrlStructData url : urlStructData) {
                    Task task = JSON.toJavaObject(JSONObject.parseObject(postTask), Task.class);
                    String md5 = CommonUtil.getMd5(url.getUrl());
                    if (map.get(md5) == null) {
                        map.put(md5, true);
                        task.setTaskUrl(url.getUrl());
                        task.setUrlStructData(url);
                        linkedList.addLast(task);
                    }
                }
            }
            this.task.setStructData(extractResponse.getBaseStruct());
        }
    }

    protected void processDup(String content) {
        String dupContent = DownloadUtil.getInstance().post(DispatchConfig.dupServer, content);
        ResponseData responseData = JSON.toJavaObject(JSONObject.parseObject(dupContent), ResponseData.class);
        if (responseData.getStatusCode() == 200) {
            List<DupResponse> dupResponses = responseData.getDupResponses();
            List<StructData> structData = this.task.getStructData().getStructData();
            for (int i = 0; i < dupResponses.size(); i++) {
                DupResponse dupResponse = dupResponses.get(i);
                if (dupResponse.isDup()) {
                    String md5 = dupResponse.getMd5();
                    structData.get(i).setId(md5);
                }
            }
            if (structData != null && structData.size() > 0) {
                for (int i = structData.size() - 1; i >= 0; i--) {
                    String id = structData.get(i).getId();
                    if (id == null) {
                        structData.remove(i);
                    }
                }
            }
        }
    }

    protected void processStorage(String content) {
        String storageContent = DownloadUtil.getInstance().post(DispatchConfig.storageServer, content);
        System.out.println(storageContent);
    }

    @Override
    public void run() {
        String postTask = JSON.toJSONString(this.task);
        processDownload(postTask);
        String content = JSON.toJSONString(this.task);
        processExtract(content, postTask);
        if (this.task.getStructData() != null && this.task.getStructData().getStructData() != null) {
            content = JSON.toJSONString(this.task);
            processDup(content);
            content = JSON.toJSONString(this.task);
            processStorage(content);
        }

    }
}
