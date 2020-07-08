package com.wl4g.devops;

import reactor.core.publisher.Mono;

public interface IRouteCacheRefresh {

    /**
     * 刷新内存中的路由信息
     * @return
     */
    public Mono<Void> flushRoutesPermanentToMemery();
}
