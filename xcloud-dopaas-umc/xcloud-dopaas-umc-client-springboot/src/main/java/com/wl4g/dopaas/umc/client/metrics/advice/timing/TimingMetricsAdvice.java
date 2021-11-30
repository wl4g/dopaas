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
package com.wl4g.dopaas.umc.client.metrics.advice.timing;

import static java.util.Objects.isNull;

import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.component.common.lang.FastTimeClock;
import com.wl4g.dopaas.common.exception.umc.UmcException;
import com.wl4g.dopaas.umc.client.health.timeout.TimeoutMethodHealthIndicator;
import com.wl4g.dopaas.umc.client.metrics.advice.BaseMetricsAdvice;

import io.micrometer.core.instrument.Timer;

/**
 * A simple statistical method to perform time-consuming aspects. If you want a
 * more comprehensive APM analysis, please use frameworks such as
 * skywalking/elasticAPM/zipkin</br>
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-11-19 v1.0.0
 * @since v1.0
 * @see https://github.com/apache/skywalking/pull/1118
 * @see {@link io.micrometer.core.aop.TimedAspect}
 */
public class TimingMetricsAdvice extends BaseMetricsAdvice {

    @Autowired(required = false)
    private TimeoutMethodHealthIndicator timingIndicator; // Non-required

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

            postProperties(metricName, deltaMs);
            return res;
        } catch (Throwable e) {
            throw new UmcException(e);
        }
    }

    /**
     * Post properties.
     * 
     * @param metricName
     * @param deltaMs
     */
    protected void postProperties(String metricName, long deltaMs) {
        if (isNull(timingIndicator)) {
            timingIndicator.record(metricName, deltaMs);
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

}