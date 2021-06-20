package com.pj.spider.dispatch;

import com.pj.spider.entity.Task;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DispatchStart {
    public static void main(String[] args) {
        ArrayBlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<Runnable>(Integer.parseInt(DispatchConfig.dispatchTaskThreadQueueSize));
        ThreadPoolExecutor taskThread = new ThreadPoolExecutor(Integer.parseInt(DispatchConfig.dispatchTaskCoreThreadSize), Integer.parseInt(DispatchConfig.dispatchTaskThreadMaximumPoolSize), Integer.parseInt(DispatchConfig.dispatchTaskThreadKeepAliveTime), TimeUnit.SECONDS, blockingQueue);
        // 更新因错误重启导致的任务错误
        SqlService.updateTask();
        try {
            while (true) {
                List<Task> task = SqlService.getTask();
                if (task != null) {
                    for (Task value : task) {
                        taskThread.execute(new DispatchConsumer(value));
                    }
                }
                try {
                    Thread.sleep(Integer.parseInt(DispatchConfig.dispatchTaskCell) * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.exit(-1);
        }
    }
}
