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
package com.wl4g.dopaas.umc.client.health.advice;

import static com.wl4g.component.common.lang.Assert2.notNull;
import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;
import static java.util.Objects.isNull;

import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.component.common.lang.FastTimeClock;
import com.wl4g.component.common.log.SmartLogger;
import com.wl4g.dopaas.common.constant.UmcConstants;
import com.wl4g.dopaas.common.exception.umc.UmcException;
import com.wl4g.dopaas.umc.client.health.TimingMethodHealthIndicator;

import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import lombok.Setter;

/**
 * It can be used to monitor the number of times it is called. </br>
 * Thank you for the references: https://www.jianshu.com/p/e20a5f42a395
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2018年5月26日
 * @since
 */
public class TimingMetricsAdvice extends BaseMetricsAdvice {

    @Autowired(required = false)
    private TimingMethodHealthIndicator timingIndicator; // Non-required

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            // Gets metric name by method.
            final String metricName = getMetricName(invocation);
            final long start = FastTimeClock.currentTimeMillis();
            Object res = invocation.proceed();
            final long deltaMs = FastTimeClock.currentTimeMillis() - start;

            // Update gauge
            Timer timer = registry.timer(transformTimerName(metricName));
            timer.record(deltaMs, TimeUnit.MILLISECONDS);

            saveHealthIndicator(metricName, deltaMs);
            return res;
        } catch (Throwable e) {
            throw new UmcException(e);
        }
    }

    /**
     * Save health indicator.
     * 
     * @param metricName
     * @param deltaMs
     */
    private void saveHealthIndicator(String metricName, long deltaMs) {
        if (isNull(timingIndicator)) {
            timingIndicator.addTimes(metricName, deltaMs);
        }
    }

    /**
     * Note that gaugeService includes not only timer metrics, but also other
     * metrics such as counter and histogram etc, the same method of AOP
     * interception should also use different metricNames for recording each
     * metric information using gaugeService, <font color=red>otherwise, it will
     * throw "IllegalArgumentException: A metric named xxx already exists"
     * exception.</font> </br>
     * See: org.springframework.boot.actuate.metrics.dropwizard.
     * DropwizardMetricServices.submit
     * 
     * @param name
     * @return
     */
    protected String transformTimerName(String name) {
        // return "lossTime." + name; // Common type of meter.
        // It corresponds to a special timer type meter(Automatically calculate
        // the maximum and minimum mean value.).
        return "timer." + name;
    }

    @Getter
    @Setter
    @Configuration
    @ConditionalOnProperty(name = TimingMetricsProperties.CONF_P + ".enabled", matchIfMissing = false)
    @ConfigurationProperties(prefix = TimingMetricsProperties.CONF_P)
    public static class TimingMetricsProperties {
        public static final String CONF_P = UmcConstants.KEY_UMC_METRIC_PREFIX + ".timing";
        public static final int DEFAULT_SAMPLES = 32;
        public static final long DEFAULT_TIMEOUT_THRESHOLD = 5_000L;

        /**
         * Call time consuming AOP point cut surface expression.
         */
        private String expression;

        /**
         * AOP intercepts the number of historical records saved by statistical
         * calls.
         */
        private int samples = DEFAULT_SAMPLES;

        /**
         * AOP intercept call time consuming timeout alarm threshold.
         */
        private long timeoutThresholdMs = DEFAULT_TIMEOUT_THRESHOLD;

        public void setExpression(String pointcutExpression) {
            if (pointcutExpression == null || pointcutExpression.trim().length() == 0)
                throw new IllegalArgumentException("Timer metrics pointcut expression is null.");
            this.expression = pointcutExpression;
        }
    }

    @Configuration
    @ConditionalOnBean(TimingMetricsProperties.class)
    public static class TimingAdviceAutoConfiguration {
        protected final SmartLogger log = getLogger(getClass());

        @Bean
        public AspectJExpressionPointcutAdvisor timingAspectJExpressionPointcutAdvisor(TimingMetricsProperties props,
                TimingMetricsAdvice advice) {
            notNull(props.getExpression(), "Expression of the timeouts AOP pointcut is null.");
            log.info("Intializing timing aspectJExpressionPointcutAdvisor. {}", props);
            AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
            advisor.setExpression(props.getExpression());
            advisor.setAdvice(advice);
            return advisor;
        }

        @Bean
        public TimingMetricsAdvice timingMetricsAdvice() {
            return new TimingMetricsAdvice();
        }

    }

}