/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.iam.config;

import org.apache.shiro.cache.CacheManager;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_SNS_BASE;

import java.lang.annotation.Annotation;
import java.util.List;

import com.wl4g.devops.iam.annotation.SnsController;
import com.wl4g.devops.iam.common.cache.JedisCacheManager;
import com.wl4g.devops.iam.common.config.AbstractIamConfiguration;
import com.wl4g.devops.iam.context.ServerSecurityContext;
import com.wl4g.devops.iam.context.ServerSecurityCoprocessor;
import com.wl4g.devops.iam.sns.BindConnection;
import com.wl4g.devops.iam.sns.DefaultSocialConnectionFactory;
import com.wl4g.devops.iam.sns.SocialConfigureRepository;
import com.wl4g.devops.iam.sns.SocialConnectionFactory;
import com.wl4g.devops.iam.sns.handler.BindingSnsHandler;
import com.wl4g.devops.iam.sns.handler.ClientAuthcSnsHandler;
import com.wl4g.devops.iam.sns.handler.DelegateSnsHandler;
import com.wl4g.devops.iam.sns.handler.LoginSnsHandler;
import com.wl4g.devops.iam.sns.handler.SecondAuthcSnsHandler;
import com.wl4g.devops.iam.sns.handler.SnsHandler;
import com.wl4g.devops.iam.sns.handler.UnBindingSnsHandler;
import com.wl4g.devops.iam.sns.qq.QQOauth2Template;
import com.wl4g.devops.iam.sns.web.DefaultOauth2SnsController;
import com.wl4g.devops.iam.sns.wechat.WechatMpOauth2Template;
import com.wl4g.devops.iam.sns.wechat.WechatOauth2Template;

/**
 * SNS resource configuration
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年1月8日
 * @since
 */
@AutoConfigureAfter({ IamConfiguration.class })
public class SnsConfiguration extends AbstractIamConfiguration {

	@Bean
	public SnsProperties snsProperties() {
		return new SnsProperties();
	}

	//
	// Social provider template's
	//

	@Bean
	public QQOauth2Template qqOauth2Template(SnsProperties config, RestTemplate restTemplate, CacheManager cacheManager) {
		return new QQOauth2Template(config.getQq(), restTemplate, cacheManager);
	}

	@Bean
	public WechatOauth2Template wechatOauth2Template(SnsProperties config, RestTemplate restTemplate, CacheManager cacheManager) {
		return new WechatOauth2Template(config.getWechat(), restTemplate, cacheManager);
	}

	@Bean
	public WechatMpOauth2Template wechatMpOauth2Template(SnsProperties config, RestTemplate restTemplate,
			CacheManager cacheManager) {
		return new WechatMpOauth2Template(config.getWechatMp(), restTemplate, cacheManager);
	}

	@SuppressWarnings("rawtypes")
	@Bean
	public SocialConfigureRepository socialConfigureRepository(List<BindConnection> binds) {
		return new SocialConfigureRepository(binds);
	}

	@Bean
	public SocialConnectionFactory socialConnectFactory(SocialConfigureRepository repository) {
		return new DefaultSocialConnectionFactory(repository);
	}

	//
	// SNS handler's
	//

	@Bean
	public DelegateSnsHandler delegateSnsHandler(IamProperties config, List<SnsHandler> handlers) {
		return new DelegateSnsHandler(config, handlers);
	}

	@Bean
	public LoginSnsHandler loginSnsHandler(IamProperties config, SnsProperties snsConfig, SocialConnectionFactory connectFactory,
			ServerSecurityContext context, ServerSecurityCoprocessor coprocessor, JedisCacheManager cacheManager) {
		return new LoginSnsHandler(config, snsConfig, connectFactory, context);
	}

	@Bean
	public ClientAuthcSnsHandler clientAuthcSnsHandler(IamProperties config, SnsProperties snsConfig,
			SocialConnectionFactory connectFactory, ServerSecurityContext context, ServerSecurityCoprocessor coprocessor) {
		return new ClientAuthcSnsHandler(config, snsConfig, connectFactory, context);
	}

	@Bean
	public BindingSnsHandler bindingSnsHandler(IamProperties config, SnsProperties snsConfig,
			SocialConnectionFactory connectFactory, ServerSecurityContext context, ServerSecurityCoprocessor coprocessor) {
		return new BindingSnsHandler(config, snsConfig, connectFactory, context);
	}

	@Bean
	public UnBindingSnsHandler unBindingSnsHandler(IamProperties config, SnsProperties snsConfig,
			SocialConnectionFactory connectFactory, ServerSecurityContext context, ServerSecurityCoprocessor coprocessor) {
		return new UnBindingSnsHandler(config, snsConfig, connectFactory, context);
	}

	@Bean
	public SecondAuthcSnsHandler secondAuthcSnsHandler(IamProperties config, SnsProperties snsConfig,
			SocialConnectionFactory connectFactory, ServerSecurityContext context, ServerSecurityCoprocessor coprocessor) {
		return new SecondAuthcSnsHandler(config, snsConfig, connectFactory, context);
	}

	//
	// SNS controller's
	//

	@Bean
	public DefaultOauth2SnsController defaultOauth2SnsController(IamProperties config, SnsProperties snsConfig,
			DelegateSnsHandler delegate) {
		return new DefaultOauth2SnsController(config, snsConfig, delegate);
	}

	@Override
	protected String getMappingPrefix() {
		return URI_S_SNS_BASE;
	}

	@Bean
	public PrefixHandlerMapping snsControllerPrefixHandlerMapping() {
		return super.createPrefixHandlerMapping();
	}

	@Override
	protected Class<? extends Annotation> annotationClass() {
		return SnsController.class;
	}

}