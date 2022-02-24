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

import static com.wl4g.infra.common.log.SmartLoggerFactory.getLogger;
import static java.util.Objects.isNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;

import com.codahale.metrics.MetricRegistry;
import com.wl4g.infra.common.log.SmartLogger;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * AOP mode service monitoring section based on spring boot admin.<br/>
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年5月30日
 * @since
 */
public abstract class BaseMetricsAdvice implements MethodInterceptor {
    private static final Map<Method, String> methodsCache = new ConcurrentHashMap<>(32);

    protected final SmartLogger log = getLogger(getClass());

    /**
     * Related and spring-boot-1.x core types: </br>
     * {@link DefaultGaugeService} </br>
     * {@link DropwizardMetricServices} </br>
     * {@link BufferGaugeService} </br>
     * {@link ServoMetricService} </br>
     * {@link MetricRegistry} </br>
     */
    protected @Autowired MeterRegistry registry;

    /**
     * Production unique name based on method name
     * 
     * @param invoc
     * @return
     */
    protected String getMetricName(MethodInvocation invoc) {
        String metricName = methodsCache.get(invoc.getMethod());
        if (isNull(metricName)) {
            synchronized (this) {
                if (isNull(metricName)) {
                    Method m = invoc.getMethod();
                    StringBuffer sign = new StringBuffer(getSimpleClassname(invoc.getThis().getClass().getName()));
                    sign.append(".");
                    sign.append(m.getName());
                    sign.append("(");
                    Parameter[] params = m.getParameters();
                    if (params != null) {
                        for (Parameter p : params) {
                            sign.append(getSimpleParameterType(p.getType().getSimpleName()));
                            sign.append(" ");
                            sign.append(p.getName());
                            sign.append(",");
                        }
                        if (sign.length() > 1) {
                            sign.delete(sign.length() - 1, sign.length());
                        }
                        sign.append(")");
                    }
                    methodsCache.put(m, (metricName = sign.toString()));
                }
            }
        }
        return metricName;
    }

    /**
     * e.g: io.transport.common.cache.JedisService -> i.t.c.c.JedisService
     * 
     * @param methodName
     * @return
     */
    public static String getSimpleClassname(String methodName) {
        StringBuffer name = new StringBuffer();
        String[] arr = methodName.split("\\.");
        for (int i = 0; i < arr.length - 1; i++) {
            if (i >= (arr.length - 1)) {
                name.append(arr[i]);
            } else {
                name.append(arr[i].substring(0, 1));
            }
            name.append(".");
        }
        name.append(arr[arr.length - 1]);
        return name.toString();
    }

    /**
     * e.g: java.lang.String -> String </br>
     * 
     * Map<java.lang.String, java.lang.String> -> Map<String, String> </br>
     * 
     * List<java.lang.String> -> List<String> </br>
     * 
     * @param paramTypeName
     * @return
     */
    public static String getSimpleParameterType(String paramTypeName) {
        String[] arr = paramTypeName.split("\\.");
        return arr[arr.length - 1];
    }

}