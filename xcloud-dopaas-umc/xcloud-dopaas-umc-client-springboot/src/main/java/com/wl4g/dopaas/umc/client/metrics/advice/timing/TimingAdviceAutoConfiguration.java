package com.wl4g.dopaas.umc.client.metrics.advice.timing;

import static com.wl4g.component.common.lang.Assert2.notNull;
import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;

import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.component.common.log.SmartLogger;

/**
 * {@link TimingAdviceAutoConfiguration}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-11-19 v1.0.0
 * @since v1.0
 */
@Configuration
@ConditionalOnBean(TimingMetricsProperties.class)
public class TimingAdviceAutoConfiguration {
    protected final SmartLogger log = getLogger(getClass());

    @Bean
    public AspectJExpressionPointcutAdvisor defaultTimingAspectJExpressionPointcutAdvisor(TimingMetricsProperties config,
            TimingMetricsAdvice advice) {
        notNull(config.getExpression(), "Expression of the timeouts AOP pointcut is null.");
        log.info("Intializing timing aspectJExpressionPointcutAdvisor. {}", config);
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        advisor.setExpression(config.getExpression());
        advisor.setAdvice(advice);
        return advisor;
    }

    @Bean
    public TimingMetricsAdvice defaultTimingMetricsAdvice() {
        return new TimingMetricsAdvice();
    }

}
