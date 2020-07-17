package com.wl4g.devops.gataway.server;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.InMemoryRouteDefinitionRepository;
import org.springframework.cloud.gateway.route.RouteDefinition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * 持久化路由信息抽象类. <br>
 * 如果需要扩展持久化方式，请继承本类，重写本类的以下方法
 *   {@link #getRouteDefinitionsByPermanent()}
 * ，{@link #save(Mono)}
 * ，{@link #delete(Mono)}
 * ，{@link #notifyAllRefresh(NotifyType)} }
 * ，{@link #initSubscriber()} ()} }
 * ，{@link #flushRoutesPermanentToMemery()}  }
 *
 * @author: guzhandong
 * @createDate: 2018/10/8 6:30 PM
 * @version: [v1.0]
 * @since [jdk 1.8]
 **/
public abstract class AbstractRouteRepository extends InMemoryRouteDefinitionRepository
        implements IRouteAlterPublisher, IRouteCacheRefresh,IRouteAlterSubscriber {

    protected static final String DEF_CHARTSET_STR = "UTF-8";

    protected static final Charset DEF_CHARTSET = Charset.forName(DEF_CHARTSET_STR);


    @Autowired
    private RouteAlterHandler routeAlterHandler;

    protected Mono<Void> notifyAllClientRefresh(NotifyType notifyType) {
        return routeAlterHandler.refresh(notifyType);
    }


    @Override
    public final Flux<RouteDefinition> getRouteDefinitions() {
        return getRouteDefinitionsByMemery();
    }


    /**
     * 从内存组件中获取路由信息
     * @return
     */
    private final Flux<RouteDefinition> getRouteDefinitionsByMemery() {
        return super.getRouteDefinitions();
    }

    /**
     * 从持久化组件中获取路由信息
     * @return
     */
    protected abstract Flux<RouteDefinition> getRouteDefinitionsByPermanent();



    /**
     *  刷新内存中的路由信息
     *  </p>
     *  通过对比内存和持久化存储中的路由差异信息，进行更新
     * @return
     */
    @Override
    public synchronized Mono<Void> flushRoutesPermanentToMemery(){
        Flux<RouteDefinition> memeryRoutes = getRouteDefinitionsByMemery();
        Flux<RouteDefinition> permanentRoutes = getRouteDefinitionsByPermanent();

        Map<RouteDefinition,Boolean> memRouteMap = Maps.newHashMap();
        memeryRoutes.subscribe(route->memRouteMap.put(route,false));

        Map<RouteDefinition,Boolean> perRouteMap = Maps.newHashMap();
        permanentRoutes.subscribe(route->perRouteMap.put(route,false));

        //给内存中新增路由信息
        Flux.fromIterable(perRouteMap.keySet())
                .filter(perRoute->!memRouteMap.containsKey(perRoute))
                .flatMap(route->super.save(Mono.just(route))).subscribe();
        //给内存中删除无效路由
        Flux.fromIterable(memRouteMap.keySet())
                .filter(perRoute->!perRouteMap.containsKey(perRoute))
                .flatMap(route->super.delete(Mono.just(route.getId()))).subscribe();
        //提醒刷新状态
        return notifyAllClientRefresh(NotifyType.state);
    }

    @PostConstruct
    public synchronized Mono<Void> initMemeryByPermanent(){
        return flushRoutesPermanentToMemery();
    }
}
