package com.wl4g.dopaas.umc.client.metrics.advice.counter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.wl4g.dopaas.common.constant.UmcConstants;

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
public class CounterMetricsProperties {
    final public static String CONF_P = UmcConstants.KEY_UMC_CLIENT_PREFIX + ".counter";

    /**
     * An expression of the statistical AOP point cut for the number of calls.
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
