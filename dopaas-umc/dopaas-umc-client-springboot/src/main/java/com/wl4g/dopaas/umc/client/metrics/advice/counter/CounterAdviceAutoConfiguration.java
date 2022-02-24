package com.wl4g.dopaas.umc.client.metrics.advice.counter;

import static com.wl4g.infra.common.lang.Assert2.notNull;
import static com.wl4g.infra.common.log.SmartLoggerFactory.getLogger;

import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.infra.common.log.SmartLogger;

/**
 * Starts bootstrap configuration <br/>
 * Precondition: <br/>
 * `@ConditionalOnBean(MonitorMetricsConfiguration.class)`<br/>
 * DI container must have MonitorMetricsConfiguration objects.<br/>
 * `@AutoConfigureBefore(MonitorMetricsConfiguration.class)`<br/>
 * MonitorMetricsConfiguration objects must be created before that.<br/>
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年5月31日
 * @since
 */
@Configuration
@ConditionalOnBean(CounterMetricsProperties.class)
public class CounterAdviceAutoConfiguration {
    protected final SmartLogger log = getLogger(getClass());

    @Bean
    public AspectJExpressionPointcutAdvisor counterAspectJExpressionPointcutAdvisor(CounterMetricsProperties props,
            CounterMetricsAdvice advice) {
        notNull(props.getExpression(), "Expression of the counter AOP pointcut is null.");
        log.info("Initial counterAspectJExpressionPointcutAdvisor. {}", props);
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        advisor.setExpression(props.getExpression());
        advisor.setAdvice(advice);
        return advisor;
    }

    @Bean
    public CounterMetricsAdvice counterPerformanceAdvice() {
        return new CounterMetricsAdvice();
    }

}
