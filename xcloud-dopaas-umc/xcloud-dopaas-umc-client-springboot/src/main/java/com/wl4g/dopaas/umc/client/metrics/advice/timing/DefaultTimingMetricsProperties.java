package com.wl4g.dopaas.umc.client.metrics.advice.timing;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.wl4g.dopaas.common.constant.UmcConstants;

/**
 * 
 * {@link DefaultTimingMetricsProperties}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-11-19 v1.0.0
 * @since v1.0
 */
@Configuration
@ConditionalOnProperty(name = DefaultTimingMetricsProperties.CONF_P + ".enabled", matchIfMissing = false)
@ConfigurationProperties(prefix = DefaultTimingMetricsProperties.CONF_P)
public class DefaultTimingMetricsProperties {
    public static final String CONF_P = UmcConstants.KEY_UMC_CLIENT_PREFIX + ".timing";

    /**
     * Call time consuming AOP point cut surface expression.
     */
    private String expression;

    public void setExpression(String pointcutExpression) {
        if (pointcutExpression == null || pointcutExpression.trim().length() == 0)
            throw new IllegalArgumentException("Timer metrics pointcut expression is null.");
        this.expression = pointcutExpression;
    }

    public String getExpression() {
        return expression;
    }

}