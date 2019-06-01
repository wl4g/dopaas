/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.scm.client.configure.refresh;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.wl4g.devops.common.utils.AopUtils;
import com.wl4g.devops.common.utils.bean.BeanMapConvert;
import com.wl4g.devops.common.bean.scm.model.GetReleaseModel;
import com.wl4g.devops.common.bean.scm.model.ReleaseModel;
import com.wl4g.devops.common.bean.scm.model.ReportModel;
import com.wl4g.devops.common.bean.scm.model.BaseModel.ReleaseMeta;
import com.wl4g.devops.common.bean.scm.model.ReleaseModel.ReleasePropertySource;
import com.wl4g.devops.common.bean.scm.model.ReportModel.RefreshedBeanDefine;
import com.wl4g.devops.common.bean.scm.model.ReportModel.RefreshedMemberDefine;
import com.wl4g.devops.common.bean.scm.model.ReportModel.ReportStatus;
import com.wl4g.devops.common.constants.SCMDevOpsConstants;
import com.wl4g.devops.common.exception.scm.ScmException;
import com.wl4g.devops.common.exception.scm.MismatchedConfigurationException;
import com.wl4g.devops.common.exception.scm.NoChangedConfigurationException;
import com.wl4g.devops.common.exception.scm.ReportRetriesCountOutException;
import com.wl4g.devops.common.utils.codec.AES;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.scm.client.config.InstanceConfig;
import com.wl4g.devops.scm.client.config.RetryProperties;
import com.wl4g.devops.scm.client.configure.RefreshBeanRegistry;
import static com.wl4g.devops.scm.client.configure.refresh.ScmBootstrapPropertySourceLocator.*;

public abstract class AbstractBeanRefresher implements BeanRefresher {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	/** Names of beans that are currently in configure */
	final private ConcurrentMap<Class<?>, Object> beanCurrentlyInConfigure = new ConcurrentHashMap<>(1);
	final private static String CIPHER_PREFIX = "{cipher}";
	final private AtomicInteger counter = new AtomicInteger(0); // Retry-counter.

	private String baseUri;
	private RestTemplate restTemplate;
	private InstanceConfig intanceProps;
	private ConfigurableEnvironment environment;
	private RetryProperties retryProps;
	private RefreshBeanRegistry registry;

	public AbstractBeanRefresher(String baseUri, RestTemplate restTemplate, RetryProperties retryProps,
			InstanceConfig intanceProps, ConfigurableEnvironment environment, RefreshBeanRegistry registry) {
		super();
		this.baseUri = baseUri;
		this.restTemplate = restTemplate;
		this.retryProps = retryProps;
		this.intanceProps = intanceProps;
		this.environment = environment;
		this.registry = registry;
		Assert.notNull(this.baseUri, "`baseUri` is not allowed to be null.");
		Assert.notNull(this.restTemplate, "`restTemplate` is not allowed to be null.");
		Assert.notNull(this.intanceProps, "`instanceProperties` is not allowed to be null.");
		Assert.notNull(this.retryProps, "`retryProperties` is not allowed to be null.");
		Assert.notNull(this.environment, "`environment` is not allowed to be null.");
	}

	public String getOrAddConfigVersion(String newVersion) {
		String oldVersion = this.environment.getProperty("spring.config.version");

		if (!StringUtils.isEmpty(newVersion)) {
			for (Iterator<PropertySource<?>> it = environment.getPropertySources().iterator(); it.hasNext();) {
				PropertySource<?> source = it.next();
				if (source instanceof MapPropertySource) {
					if (log.isTraceEnabled()) {
						log.trace("Add remote configuration version to environment. property-source.name: {}", source.getName());
					}
					MapPropertySource anyMapSource = (MapPropertySource) source;
					anyMapSource.getSource().put("spring.config.version", newVersion);
					break;
				}
			}
		}

		return oldVersion;
	}

	@Override
	public synchronized void refresh(ReleaseMeta targetReleaseMeta) {
		if (log.isInfoEnabled()) {
			log.info("Watch configuration refresh bean all... {}", targetReleaseMeta);
		}

		final ReportModel report = new ReportModel();
		// Mark-up has executed a doRefresh() method
		boolean refreshedMark = false;
		try {
			/*
			 * 1.1 Check refresh to bean. Because the synchronization
			 * configuration source does not need to be refreshed manually to
			 * bean when the container initializes.
			 */
			this.checkBeanRefresh();

			// 1.2 Check target-version.
			Assert.notNull(targetReleaseMeta, "This 'targetReleaseMeta' must not be null");
			targetReleaseMeta.validation(true, true);

			// 1.3 Get target configuration.
			ReleaseModel release = this.getRemoteReleaseConfig(targetReleaseMeta);

			// 1.4 Check configuration to match current environment.
			this.checkReleaseConfigMatched(release, targetReleaseMeta);

			// 1.3 Copy configuration information to report.
			report.setApplication(release.getApplication());
			report.setProfile(release.getProfile());
			report.setReleaseMeta(targetReleaseMeta);
			report.setInstance(intanceProps.getBindInstance());

			// 1.4 Process refresh all.
			ReportModel refreshed = this.doRefresh(release, targetReleaseMeta);
			refreshedMark = true; // Refreshed

			// 1.5 Copy configuration and report information.
			report.setDetails(refreshed.getDetails());

		} catch (Exception e) {
			String errmsg = ExceptionUtils.getRootCauseMessage(e);
			report.setDescription(errmsg);

			if (e instanceof NoChangedConfigurationException) {
				report.setStatus(ReportStatus.noChanged);
				log.info(errmsg);
			} else {
				report.setStatus(ReportStatus.fail);
				log.error("DevOps config refresh failure. {}", ExceptionUtils.getRootCauseMessage(e));
			}
		}

		// 1.6 Report refresh change result.
		if (refreshedMark) {
			try {
				this.doReportWithRefreshed(report);
			} catch (Exception e) {
				log.error("Report refreshed failure. retry:{} {} {}", this.counter.get(), ExceptionUtils.getRootCauseMessage(e),
						report);
			}
		}

	}

	public ReleaseModel getRemoteReleaseConfig(ReleaseMeta targetReleaseMeta) {
		// Get pull release URL.
		String uri = this.baseUri + SCMDevOpsConstants.URI_S_BASE + "/" + SCMDevOpsConstants.URI_S_SOURCE_GET;

		// Create request bean.
		GetReleaseModel req = new GetReleaseModel(intanceProps.getApplicationName(), intanceProps.getProfilesActive(),
				targetReleaseMeta, intanceProps.getBindInstance());

		// Bean to map.
		String params = new BeanMapConvert(req).toUriParmaters();
		String url = uri + "?" + params;
		if (log.isDebugEnabled()) {
			log.debug("Get remote release config url: {}", url);
		}

		RespBase<ReleaseModel> resp = this.restTemplate
				.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<RespBase<ReleaseModel>>() {
				}).getBody();
		if (!RespBase.isSuccess(resp)) {
			throw new ScmException(String.format("Get remote source error. %s, %s", url, resp.getMessage()));
		}

		// Get release payload
		ReleaseModel release = resp.getData().get(SCMDevOpsConstants.KEY_RELEASE);
		Assert.notNull(release, "'releaseMessage' is required, it must not be null");
		release.validation(true, true);

		if (log.isDebugEnabled()) {
			log.debug("Get remote release config : {}", release);
		}
		return release;
	}

	public void resolvesCipherSource(ReleaseModel release) {
		if (log.isTraceEnabled()) {
			log.trace("Resolver cipher configuration propertySource ...");
		}

		for (ReleasePropertySource ps : release.getPropertySources()) {
			ps.getSource().forEach((key, value) -> {
				String cipher = String.valueOf(value);
				if (cipher.startsWith(CIPHER_PREFIX)) {
					try {
						String plain = new AES().decrypt(cipher.substring(CIPHER_PREFIX.length()));
						ps.getSource().put(key, plain);

						if (log.isDebugEnabled()) {
							log.debug("Decryption property-key: {}, cipherText: {}, plainText: {}", key, cipher, plain);
						}
					} catch (Exception e) {
						throw new ScmException("Cipher decryption error.", e);
					}
				}
			});
		}
	}

	@Override
	public boolean isBeanCurrentlyInConfigure(Object bean) {
		Object curVal = this.beanCurrentlyInConfigure.get(bean.getClass());
		return (curVal != null && (curVal.equals(bean) || (curVal instanceof Set && ((Set<?>) curVal).contains(bean))));
	}

	public CompositePropertySource getDevOpsConfigurablePropertySource() {
		// Get bootstrap CompositePropertySource.
		CompositePropertySource bootstrapSources = (CompositePropertySource) this.environment.getPropertySources()
				.get(PropertySourceBootstrapConfiguration.BOOTSTRAP_PROPERTY_SOURCE_NAME);
		Assert.notNull(bootstrapSources, "Spring cloud 'bootstrapProperties' property source must not be null.");

		// Matching bootstrap property sources.
		List<PropertySource<?>> devopsSources = bootstrapSources.getPropertySources().stream()
				.filter(source -> String.valueOf(source.getName()).equals(SCM_PROPERTY_SOURCE)).collect(Collectors.toList());

		if (!devopsSources.isEmpty()) {
			Assert.isTrue(devopsSources.size() == 1, "This expression: 'devopsPropertySource.size() == 1' must be true");
			return (CompositePropertySource) devopsSources.get(0);
		}

		return new CompositePropertySource(SCM_PROPERTY_SOURCE);
	}

	protected abstract Object doRefreshToTarget(String beanId, Object bean);

	private void doReportWithRefreshed(ReportModel report) {
		int cur = this.counter.incrementAndGet();

		// Define URL.
		String url = this.baseUri + SCMDevOpsConstants.URI_S_BASE + "/" + SCMDevOpsConstants.URI_S_REPORT_POST;
		if (log.isDebugEnabled()) {
			log.debug("Report refreshed... {} => {}", report, url);
		}

		try {
			// Report refresh change result.
			report.validation(true, true);

			RespBase<?> ack = this.restTemplate.postForObject(url, report, RespBase.class);
			if (RespBase.isSuccess(ack)) {
				// Reset
				this.counter.set(0);
				if (log.isInfoEnabled()) {
					log.info("Report refreshed successfully. {}", ack);
				}
			} else {
				throw new ScmException(String.format("ACK: %s", ack.toString()));
			}

		} catch (Exception e) {
			log.warn("Report refreshed failure. retry:{} {}", cur, ExceptionUtils.getRootCauseMessage(e));
			if (cur < this.retryProps.getMaxAttempts()) {
				new Thread(() -> {
					try {
						Thread.sleep(retryProps.getRandomSleepPeriod());
					} catch (InterruptedException e1) {
						log.error(e1.getMessage(), e1);
					}

					// Recursive retry
					this.doReportWithRefreshed(report);
				}).start();

			} else {
				this.counter.set(0); // Reset
				throw new ReportRetriesCountOutException(
						String.format("Maximum number of retries exceeded: %s", retryProps.getMaxAttempts()));
			}
		}

	}

	private ReportModel doRefresh(ReleaseModel release, ReleaseMeta targetReleaseMeta) {
		// 1.1 Resolvers cipher resource.
		this.resolvesCipherSource(release);

		// 2.1 Add release-configuration to environment.
		this.addConfigToEnvironment(release);

		// Define final configure-result.
		ReportModel result = new ReportModel();
		// 2.2 Refresh configure bean all.
		this.registry.getRefreshBeans().forEach((beanId, obj) -> {
			// 2.2.1 Get before target real object.
			Object bean = AopUtils.getTarget(obj);
			// 2.2.2 Configure release of bean.
			List<RefreshedMemberDefine> members = this.configure(beanId, bean, release);
			// 2.2.3 To wrapper.
			RefreshedBeanDefine ret = new RefreshedBeanDefine(beanId, bean.getClass().getName(), members);
			result.getDetails().add(ret);
		});

		if (log.isDebugEnabled()) {
			log.debug("Configuration refreshed: {}", result);
		}
		return result;
	}

	private void addConfigToEnvironment(ReleaseModel release) {
		// 1.1 Add configuration to environment.
		if (log.isTraceEnabled()) {
			log.trace("Add configuration to environment ...");
		}

		// Get current release property-source.
		CompositePropertySource curDevopsSource = release.convertCompositePropertySource(SCM_PROPERTY_SOURCE);
		// Get configuration devops property-source.
		CompositePropertySource envDevopsSource = this.getDevOpsConfigurablePropertySource();

		for (PropertySource<?> source : curDevopsSource.getPropertySources()) {
			// Matching DevOps property sources and get matched configuration
			// by source name.
			PropertySource<?> matcheSource = PropertySource.named(source.getName());
			/*
			 * Note: When the client starts initialization, if the update of
			 * remote configuration source fails, the property Sources
			 * corresponding to `SCM_PROPERTY_SOURCE'are empty.
			 */
			if (!envDevopsSource.getPropertySources().isEmpty()) {
				if (envDevopsSource.getPropertySources().contains(matcheSource)) {
					// Remove old bootstrapProperties.propertySource
					if (!envDevopsSource.getPropertySources().remove(matcheSource)) {
						throw new ScmException(
								String.format("Failed to clear old profile configuration %s", matcheSource.getName()));
					}

					if (log.isDebugEnabled()) {
						log.debug("Add configuration source to environment. before-devOpsSources: {}, after-devOpsSources: {}",
								envDevopsSource, source);
					}

				} else {
					// Get print info.
					List<String> envNames = Arrays.asList(envDevopsSource.getPropertyNames());
					throw new MismatchedConfigurationException(
							String.format("Invalid matching configuration source, release.source: %s, environment.sources: %s",
									source.getName(), envNames.toString()));
				}
			}

			// Add propertySource to environment(devopsPropertySources).
			// Reference:com.wl4g.devops.client.configure.refresh.DevOpsPropertySourceLocator#locate
			envDevopsSource.addFirstPropertySource(source);
		}

	}

	private List<RefreshedMemberDefine> configure(String beanId, Object before, ReleaseModel config) {
		if (log.isDebugEnabled()) {
			log.debug("Configuring refresh scope. beanId: {}, before: {}, config: {}", beanId, before, config);
		}
		List<RefreshedMemberDefine> members = new ArrayList<>();

		// 1.1 Before change fields set.
		this.beforeChangeHandle(members, before, beanId);

		// 1.2 Restart bean's life cycle.
		Object after = AopUtils.getTarget(this.doRefreshToTarget(beanId, before));

		// 1.3 After handle configuration changes.
		this.afterChangeHandle(members, after, beanId);

		return members;
	}

	private void beforeChangeHandle(List<RefreshedMemberDefine> members, Object before, String beanId) {
		// 1.1 Set the lock that is currently configuring bean.
		this.beanCurrentlyInConfigure.putIfAbsent(before.getClass(), before);

		Field[] fields = before.getClass().getDeclaredFields();
		// 1.2 Get fields value of release before.
		for (Field f : fields) {
			if (Modifier.isStatic(f.getModifiers()) || Modifier.isFinal(f.getModifiers())) {
				continue;
			}

			// 1.3 Get field property name.
			String propertyName = this.getPropertyName(before, f);
			if (StringUtils.isEmpty(propertyName)) {
				continue; // Avoid of unrelated fields.
			}

			// 1.4 Get field old value.
			ReflectionUtils.makeAccessible(f);
			members.add(new RefreshedMemberDefine(propertyName, ReflectionUtils.getField(f, before), null, f));
		}
	}

	private void afterChangeHandle(List<RefreshedMemberDefine> members, Object after, String beanId) {
		// 1.1 Release the lock that is currently configuring bean.
		this.beanCurrentlyInConfigure.remove(after.getClass(), after);

		// 1.2 Record changed members.
		for (RefreshedMemberDefine member : members) {
			// 1.3 Get field new value.
			ReflectionUtils.makeAccessible(member.getField());
			member.setNewValue(ReflectionUtils.getField(member.getField(), after));

			// 1.4 Check whether changes occur.
			member.setModifyed(isChanges(member.getField(), member.getOldValue(), member.getNewValue()));

			if (Boolean.FALSE.equals(member.getModifyed()) && log.isDebugEnabled()) {
				log.debug(String.format("Failed to change configuration bean property. bean: %s, field: %s", beanId,
						member.getPropertyName()));
			}
		}
	}

	private boolean isChanges(Field field, Object oldValue, Object newValue) {
		return !String.valueOf(oldValue).equals(String.valueOf(newValue));
	}

	private String getPropertyName(Object obj, Field field) {
		String propertyName = null;

		// 1.1 @Value inject.
		Value value = field.getAnnotation(Value.class);
		if (value != null) {
			propertyName = value.value();
		}
		// 1.2 Is there any existence @Configuration(ignore XML inject, consider
		// only the spring-boot environment.), must be a set method.
		else if (obj.getClass().getAnnotation(Configuration.class) != null) {
			String fname = field.getName();
			String setMethodName = "set" + fname.substring(0, 1).toUpperCase() + fname.substring(1);
			if (ReflectionUtils.findMethod(obj.getClass(), setMethodName) != null) {
				propertyName = fname;
			}
		}

		// 1.3 Get real property name of placeholder.
		return this.extractPlaceholder(propertyName);
	}

	private String extractPlaceholder(String placeholder) {
		if (placeholder != null && placeholder.contains("${") && placeholder.contains("}")) {
			// EG: @Value("#{'${redis.nodes:web1:6379,web2:6379}'.split(',')}")
			int startIndex = placeholder.indexOf("${") + 2;
			int endIndex = placeholder.indexOf("}", startIndex);
			if (placeholder.contains(":")) {
				endIndex = placeholder.indexOf(":", startIndex);
			}
			return placeholder.substring(startIndex, endIndex);
		}
		return placeholder;
	}

	private void checkReleaseConfigMatched(ReleaseModel release, ReleaseMeta targetReleaseMeta) {
		// 1.1 Check configuration null.
		release.validation(true, true);
		targetReleaseMeta.validation(true, true);

		// 1.2 Check base information.
		if (!String.valueOf(release.getApplication()).equals(intanceProps.getApplicationName())) {
			throw new MismatchedConfigurationException(String.format("Mismatched config application, expected: %s, actual: %s",
					intanceProps.getApplicationName(), release.getApplication()));
		}
		if (!String.valueOf(release.getProfile()).equals(intanceProps.getProfilesActive())) {
			throw new MismatchedConfigurationException(String.format("Mismatched config profile, expected: %s, actual: %s",
					intanceProps.getProfilesActive(), release.getProfile()));
		}

		// 1.3 Check configuration actual version is not request target version.
		if (!String.valueOf(targetReleaseMeta.getVersion()).equals(String.valueOf(release.getReleaseMeta().getVersion()))) {
			throw new MismatchedConfigurationException(
					String.format("Mismatched request target version, expected: %s, actual: %s", targetReleaseMeta.getVersion(),
							release.getReleaseMeta().getVersion()));
		}

		// 1.4 Check configuration version no-change.
		String oldVersion = this.getOrAddConfigVersion(release.getReleaseMeta().getVersion());
		if (String.valueOf(oldVersion).equals(release.getReleaseMeta().getVersion())) {
			throw new NoChangedConfigurationException(
					String.format("Current environment source version used is: %s, target version: %s", oldVersion,
							release.getReleaseMeta().getVersion()));
		}

	}

	private void checkBeanRefresh() {
		Assert.notNull(this.registry, "`refreshBeanRegistry` is not allowed to be null.");
	}

}