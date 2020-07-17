package com.wl4g.devops.gataway.server;

import com.wl4g.devops.components.tools.common.serialize.JacksonUtils;
import com.wl4g.devops.gataway.server.redis.JedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private JedisService jedisService;

    private static final String REDIS_ROUTE_KEY = "GATEWAY_ROUTE";

    private static final String REDIS_NOTIFY_KEY = "GATEWAY_ROUTE_NOTIFY";


    /**
     * 获取全部的路由信息
     * @return
     */
    @Override
    protected Flux<RouteDefinition> getRouteDefinitionsByPermanent() {
        List<RouteDefinition> list = new ArrayList<>();
        Map<String, String> map = jedisService.getMap(REDIS_ROUTE_KEY);
        for(Map.Entry<String, String> entry : map.entrySet()){
            String value = entry.getValue();
            RouteDefinition routeDefinition = null;
            try {
                routeDefinition = JacksonUtils.parseJSON(value, RouteDefinition.class);
                list.add(routeDefinition);
            }catch (Exception e){
                logger.error("parseJSON fail");
            }
        }
        if(CollectionUtils.isEmpty(list)){
            return Flux.empty();
        }
        return Flux.fromIterable(list);
        //return Flux.fromIterable(getRouteDefinitionsFromRedis());
        //return Flux.empty();
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        Map map = new HashMap();
        route.flatMap(routeDefinition -> {
            map.put(routeDefinition.getId(),JacksonUtils.toJSONString(routeDefinition));
            return Mono.empty();
        });
        jedisService.mapPut(REDIS_ROUTE_KEY,map);
        return Mono.empty();
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap(id->{
            jedisService.mapRemove(REDIS_ROUTE_KEY,id);
            return Mono.empty();
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
        System.out.println("into initSubscriber");
        /*stringRedisTemplate.getConnectionFactory().getConnection().subscribe(new MessageListener() {
            @Override
            public void onMessage(Message message, @Nullable byte[] bytes) {
                logger.debug("receive notify msg!");
                //notifyAllClientRefresh(NotifyType.valueOf(new String(message.getBody(),DEF_CHARTSET)));
            }
        },REDIS_NOTIFY_KEY.getBytes(DEF_CHARTSET));*/
    }

    private List<RouteDefinition> getRouteDefinitionsFromRedis() {
        List<RouteDefinition> list = new ArrayList<>();
        RouteDefinition routeDefinition = new RouteDefinition();
        routeDefinition.setId("route_id_1");
        List<PredicateDefinition> predicateDefinitions = new ArrayList<>();
        PredicateDefinition predicateDefinition = new PredicateDefinition("Path=/**");
        predicateDefinitions.add(predicateDefinition);
        routeDefinition.setPredicates(predicateDefinitions);
        try {
            routeDefinition.setUri(new URI("http://localhost:14086"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        list.add(routeDefinition);
        return list;
    }

}
