package com.pj.spider.dispatch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.pj.spider.config.CommonConfig;
import com.pj.spider.entity.Task;
import com.pj.spider.util.CommonUtil;

import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class DispatchConsumer implements Runnable {
    private Task task;
    private ConcurrentHashMap<String, Boolean> map = new ConcurrentHashMap<String, Boolean>();
    private ConcurrentLinkedDeque<Task> linkedList = new ConcurrentLinkedDeque<>();

    public DispatchConsumer(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        task.setTaskStatus(1);
        task.setTaskComputerIp(CommonConfig.addr.getHostAddress());
        task.setTaskComputerName(CommonConfig.addr.getHostName());
        SqlService.updateTask(CommonUtil.getUpdateSql("tasks", Task.class, task));
        process();
        task.setTaskStatus(0);
        task.setTaskDoTime(LocalDateTime.now().plusSeconds(task.getTaskCell()));
        task.setTaskUpdateTime(LocalDateTime.now());
        String now = LocalDateTime.now().toString().replace("T", " ");
        String tempDo = task.getTaskDoTime().toString().replace("T", " ");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            long time = format.parse(now).getTime() - format.parse(tempDo).getTime();
            task.setTaskUseTime(Integer.parseInt(String.valueOf(time)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SqlService.updateTask(CommonUtil.getUpdateSql("tasks", Task.class, task));
    }

    protected void process() {
        Object o = JSON.toJSON(task);
        Task tempTask = JSON.toJavaObject(JSONObject.parseObject(String.valueOf(o)), Task.class);
        linkedList.addFirst(tempTask);
        ArrayBlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<Runnable>(Integer.parseInt(DispatchConfig.dispatchTaskThreadQueueSize));
        ThreadPoolExecutor taskThread = new ThreadPoolExecutor(0, Integer.parseInt(DispatchConfig.dispatchTaskSubThreadSize), Integer.parseInt(DispatchConfig.dispatchTaskThreadKeepAliveTime), TimeUnit.SECONDS, blockingQueue);
        try {
            while (linkedList.size() > 0 || taskThread.getActiveCount() > 0 || blockingQueue.size() > 0) {
                Constructor<?> constructor = Class.forName(DispatchConfig.dispatchTaskClass).getDeclaredConstructor(ConcurrentHashMap.class, ConcurrentLinkedDeque.class, Task.class);
                try {
                    Task task = linkedList.removeFirst();
                    String taskUrl = task.getTaskUrl();
                    String md5 = CommonUtil.getMd5(taskUrl);
                    map.put(md5, true);
                    taskThread.execute((DispatchBase) constructor.newInstance(map, linkedList, task));
                } catch (Exception e) {
                    Thread.sleep(2000);
                }
            }
            System.out.println("任务退出线程池");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
