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
package com.wl4g.devops.umc.client.metrics;

import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.wl4g.component.core.exception.umc.UmcException;
import com.wl4g.devops.umc.client.indicator.TimeoutsHealthIndicator;

/**
 * It can be used to monitor the number of times it is called.<br/>
 * Thank you for the references: https://www.jianshu.com/p/e20a5f42a395
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2018年5月26日
 * @since
 */
public class TimerMetricsAdvice extends AbstractMetricsAdvice {
	final private static Logger log = LoggerFactory.getLogger(TimerMetricsAdvice.class);

	@Autowired(required = false)
	private TimeoutsHealthIndicator timeoutsHealthIndicator; // Non-required

	/**
	 * Specific implementations are dependent on packages and recommend relying
	 * on metrics-core packages.<br/>
	 * Optional implementations are: DefaultGaugeService / Dropwizard Metric
	 * Services / BufferGaugeService / ServoMetric Services
	 */
	@Autowired
	private MetricRegistry registry;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		try {
			// Get metric(method) name.
			String metricName = getMetricName(invocation);

			long start = System.currentTimeMillis();
			Object res = invocation.proceed();
			long timeDiff = System.currentTimeMillis() - start;

			// Save gauge.
			Timer timer = registry.timer(warpTimerName(metricName));
			timer.update(timeDiff, TimeUnit.MILLISECONDS);

			// Save gauge to healthIndicator.
			saveHealthIndicator(metricName, timeDiff);

			return res;
		} catch (Throwable e) {
			throw new UmcException(e);
		}
	}

	/**
	 * Save health indicator.
	 * 
	 * @param metricName
	 * @param timeDiff
	 */
	private void saveHealthIndicator(String metricName, long timeDiff) {
		if (timeoutsHealthIndicator != null) {
			timeoutsHealthIndicator.addTimes(metricName, timeDiff);
		}
	}

	/**
	 * Note that gaugeService includes not only timer metrics, but also other
	 * metrics such as counter and histogram etc, the same method of AOP
	 * interception should also use different metricNames for recording each
	 * metric information using gaugeService, <font color=red>otherwise, it will
	 * throw "IllegalArgumentException: A metric named xxx already exists"
	 * exception.</font><br/>
	 * See: org.springframework.boot.actuate.metrics.dropwizard.
	 * DropwizardMetricServices.submit
	 * 
	 * @param name
	 * @return
	 */
	private String warpTimerName(String name) {
		// return "lossTime." + name; // Common type of meter.
		return "timer." + name; // It corresponds to a special timer type
								// meter(Automatically calculate the maximum and
								// minimum mean value.).
	}

	/**
	 * Timer monitor measure properties.
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0
	 * @date 2018年6月1日
	 * @since
	 */
	@Configuration
	@ConditionalOnProperty(name = TimerMetricsProperties.CONF_P + ".enable", matchIfMissing = true)
	@ConfigurationProperties(prefix = TimerMetricsProperties.CONF_P)
	public static class TimerMetricsProperties {
		final public static String CONF_P = "spring.cloud.devops.metrics.timeouts";
		final public static int DEFAULT_SAMPLES = 32;
		final public static long DEFAULT_TIMEOUTS_THRESHOLD = 15_000L;

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
		private long timeoutsThreshold = DEFAULT_TIMEOUTS_THRESHOLD;

		public int getSamples() {
			return samples;
		}

		public void setSamples(int latestMeasureCount) {
			this.samples = latestMeasureCount;
		}

		public String getExpression() {
			return expression;
		}

		public void setExpression(String pointcutExpression) {
			if (pointcutExpression == null || pointcutExpression.trim().length() == 0)
				throw new IllegalArgumentException("Timer metrics pointcut expression is null.");
			this.expression = pointcutExpression;
		}

		public long getTimeoutsThreshold() {
			return timeoutsThreshold;
		}

		public void setTimeoutsThreshold(long timeoutsThreshold) {
			this.timeoutsThreshold = timeoutsThreshold;
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
	@ConditionalOnBean(TimerMetricsProperties.class)
	public static class TimerPerformanceAdviceConfiguration {

		@Bean
		public AspectJExpressionPointcutAdvisor timerAspectJExpressionPointcutAdvisor(TimerMetricsProperties conf,
				TimerMetricsAdvice advice) {
			Assert.notNull(conf.getExpression(), "Expression of the timeouts AOP pointcut is null.");
			if (log.isInfoEnabled()) {
				log.info("Initial timerAspectJExpressionPointcutAdvisor. {}", conf);
			}

			AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
			advisor.setExpression(conf.getExpression());
			advisor.setAdvice(advice);
			return advisor;
		}

		@Bean
		public TimerMetricsAdvice timerPerformanceAdvice() {
			return new TimerMetricsAdvice();
		}

	}

}