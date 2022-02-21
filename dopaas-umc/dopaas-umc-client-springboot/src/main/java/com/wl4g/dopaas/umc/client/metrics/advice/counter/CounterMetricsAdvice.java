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
package com.wl4g.dopaas.umc.client.metrics.advice.counter;

import org.aopalliance.intercept.MethodInvocation;

import com.wl4g.dopaas.common.exception.umc.UmcException;
import com.wl4g.dopaas.umc.client.metrics.advice.BaseMetricsAdvice;

import io.micrometer.core.instrument.Counter;

/**
 * It can be used to monitor the execution time of any method it is called.<br/>
 * Thank you for the references: https://www.jianshu.com/p/e20a5f42a395
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2018-05-26
 * @since
 * @see {@link io.micrometer.core.aop.CountedAspect}
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

}