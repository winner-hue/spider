package com.pj.spider.plugin;

import com.pj.spider.entity.Task;

public abstract class Spider {
    protected Task task;

    public Spider(Task task) {
        this.task = task;
    }

    public void process() {

    }
}
