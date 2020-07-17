package com.wl4g.devops.gataway.server;

import reactor.core.publisher.Mono;

public interface IRouteCacheRefresh {

    /**
     * 刷新内存中的路由信息
     * @return
     */
    public Mono<Void> flushRoutesPermanentToMemery();
}
