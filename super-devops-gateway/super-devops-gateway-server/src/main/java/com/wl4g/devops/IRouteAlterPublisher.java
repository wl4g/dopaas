package com.wl4g.devops;

import reactor.core.publisher.Mono;

public interface IRouteAlterPublisher {


    /**
     * 发送《刷新》路由的通知消息
     */
    Mono<Void> notifyAllRefresh(NotifyType notifyType);

}
