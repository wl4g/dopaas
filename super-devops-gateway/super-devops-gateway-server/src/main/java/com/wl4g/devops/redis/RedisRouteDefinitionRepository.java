package com.wl4g.devops.redis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wl4g.devops.AbstractRouteRepository;
import com.wl4g.devops.NotifyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

/**
 * redis 路由信息持久化类. <br>
 * 该类包含了 路由持久化 和 分布式集群的路由刷新通知
 *
 * @author: guzhandong
 * @createDate: 2018/9/28 10:31 AM
 * @version: [v1.0]
 * @since [jdk 1.8]
 **/
public class RedisRouteDefinitionRepository extends AbstractRouteRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String REDIS_ROUTE_KEY = "GATEWAY_ROUTE";

    private static final String REDIS_NOTIFY_KEY = "GATEWAY_ROUTE_NOTIFY";


    private final Gson gson = new GsonBuilder().create();


    /**
     * 获取全部的路由信息
     * @return
     */
    @Override
    protected Flux<RouteDefinition> getRouteDefinitionsByPermanent() {
        return Flux.fromIterable(stringRedisTemplate
                .opsForHash().values(REDIS_ROUTE_KEY).stream()
                .map(routeDefinition->{
                    return gson.fromJson(routeDefinition.toString(), RouteDefinition.class);
                }).collect(Collectors.toList()));
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route.flatMap(routeDefinition -> {
            stringRedisTemplate.opsForHash().put(REDIS_ROUTE_KEY,routeDefinition.getId(),gson.toJson(routeDefinition));
            return Mono.empty();
        });
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap(id->{
            if (stringRedisTemplate.opsForHash().hasKey(REDIS_ROUTE_KEY,id)) {
                stringRedisTemplate.opsForHash().delete(REDIS_ROUTE_KEY,id);
                return Mono.empty();
            }
            return Mono.error(new NotFoundException("RouteDefinition id fot found: "+id));
        });
    }


    @Override
    public Mono<Void> notifyAllRefresh(NotifyType notifyType) {
        logger.debug("send notify msg!");
        stringRedisTemplate.getConnectionFactory().getConnection().publish(REDIS_NOTIFY_KEY.getBytes(DEF_CHARTSET),notifyType.toString().getBytes(DEF_CHARTSET));
        return Mono.empty();
    }





    /**
     * 初始化redis订阅者，用来监听《刷新消息》
     */
    @PostConstruct
    @Override
    public void initSubscriber()  {
        stringRedisTemplate.getConnectionFactory().getConnection().subscribe(new MessageListener() {
            @Override
            public void onMessage(Message message, @Nullable byte[] bytes) {
                logger.debug("receive notify msg!");
                notifyAllClientRefresh(NotifyType.valueOf(new String(message.getBody(),DEF_CHARTSET)));
            }
        },REDIS_NOTIFY_KEY.getBytes(DEF_CHARTSET));
    }

}
