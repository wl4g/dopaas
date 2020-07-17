package com.wl4g.devops.gataway.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.actuate.GatewayControllerEndpoint;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import reactor.core.publisher.Mono;

public class RouteAlterHandler implements ApplicationListener<RefreshRoutesEvent>{

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ApplicationContext applicationContext;


    public Mono<Void> refresh(NotifyType notifyType){
        if (notifyType!=null) {
            this.applicationEventPublisher.publishEvent(new RefreshRoutesEvent(notifyType));
        }
        return Mono.empty();
    }


    @Override
    public void onApplicationEvent(RefreshRoutesEvent refreshRoutesEvent) {
        try {
//            org.springframework.cloud.gateway.route.RouteRefreshListener
            logger.debug(String.format("receive event %s source %s","refreshRoutesEvent",refreshRoutesEvent.getSource().toString()));
            if (refreshRoutesEvent.getSource() instanceof GatewayControllerEndpoint) {
                //通知所有实例更新持久化路由信息
                applicationContext.getBean(IRouteAlterPublisher.class).notifyAllRefresh(NotifyType.permanent);
            } else if (NotifyType.permanent.equals(refreshRoutesEvent.getSource())) {
                //刷当前实例内存路由信息
                applicationContext.getBean(IRouteCacheRefresh.class).flushRoutesPermanentToMemery();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
