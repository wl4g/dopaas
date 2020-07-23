package com.wl4g.devops.gateway.server.filter;

import com.wl4g.devops.components.tools.common.log.SmartLogger;
import com.wl4g.devops.components.tools.common.log.SmartLoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author vjay
 * @date 2020-07-22 15:31:00
 */
@Component
public class CustomerTokenFilter implements GlobalFilter, Ordered {
    private static final SmartLogger log = SmartLoggerFactory.getLogger(CustomerTokenFilter.class);

    private static final String REQUEST_TIME_BEGIN = "requestTimeBegin";


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Map<String, Object> attributes = exchange.getAttributes();
        System.out.println(attributes);
        exchange.getAttributes().put(REQUEST_TIME_BEGIN, System.currentTimeMillis());
        log.info("contain token " + exchange.getRequest().getHeaders().containsKey("token"));
        log.info("token is " + exchange.getRequest().getHeaders().get("token"));
        if (exchange.getRequest().getHeaders().containsKey("token")) {
            return chain.filter(exchange).then(
                    Mono.fromRunnable(() -> {
                        Long startTime = exchange.getAttribute(REQUEST_TIME_BEGIN);
                        if (startTime != null) {
                            log.info(exchange.getRequest().getURI().getRawPath() + ": " + (System.currentTimeMillis() - startTime) + "ms");
                        }
                    })
            );
        } else {
            byte[] bytes =
                    "{\"status\":429,\"msg\":\"Too Many Requests\",\"data\":{}}".getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            ServerHttpResponse serverHttpResponse = exchange.getResponse();
            serverHttpResponse.setStatusCode(HttpStatus.OK);
            return exchange.getResponse().writeWith(Flux.just(buffer));
        }

    }



    @Override
    public int getOrder() {
        return 0;
    }
}
