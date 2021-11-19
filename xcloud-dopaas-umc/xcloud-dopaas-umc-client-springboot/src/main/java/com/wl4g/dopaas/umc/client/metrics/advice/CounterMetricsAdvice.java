/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.dopaas.umc.client.metrics.advice;

import static com.wl4g.component.common.lang.Assert2.notNull;
import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.component.common.log.SmartLogger;
import com.wl4g.dopaas.common.constant.UmcConstants;
import com.wl4g.dopaas.common.exception.umc.UmcException;

import io.micrometer.core.instrument.Counter;

/**
 * It can be used to monitor the execution time of any method it is called.<br/>
 * Thank you for the references: https://www.jianshu.com/p/e20a5f42a395
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2018年5月26日
 * @since
 */
public class CounterMetricsAdvice extends BaseMetricsAdvice {

    /**
     * Number of times the AOP statistical method is called.
     */
    @Override
    public Object invoke(MethodInvocation invo) throws Throwable {
        try {
            Counter counter = registry.counter(getMetricName(invo), "method", invo.getMethod().toGenericString());
            counter.increment(1);
            return invo.proceed();
        } catch (Throwable e) {
            throw new UmcException(e);
        }
    }

    /**
     * Counter monitor measure properties.
     * 
     * @author Wangl.sir <983708408@qq.com>
     * @version v1.0
     * @date 2018年6月1日
     * @since
     */
    @Configuration
    @ConditionalOnProperty(name = CounterMetricsProperties.CONF_P + ".enabled", matchIfMissing = false)
    @ConfigurationProperties(prefix = CounterMetricsProperties.CONF_P)
    public static class CounterMetricsProperties {
        final public static String CONF_P = UmcConstants.KEY_UMC_CLIENT_PREFIX + ".counter";

        /**
         * An expression of the statistical AOP point cut for the number of
         * calls.
         */
        private String expression;

        public String getExpression() {
            return expression;
        }

        public void setExpression(String pointcutExpression) {
            if (pointcutExpression == null || pointcutExpression.trim().length() == 0)
                throw new IllegalArgumentException("Counter metrics pointcut expression is null.");

            this.expression = pointcutExpression;
        }

    }

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
    public static class CounterAdviceAutoConfiguration {
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

}