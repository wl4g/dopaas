package com.wl4g.devops;

public interface IRouteAlterSubscriber {


    /**
     * 初始化订阅者，用来初始化 接收持久化刷新的消息
     */
    void initSubscriber();

}
