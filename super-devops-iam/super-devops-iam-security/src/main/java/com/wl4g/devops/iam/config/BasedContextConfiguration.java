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
package com.wl4g.devops.iam.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.BEAN_DELEGATE_MSG_SOURCE;
import com.wl4g.devops.common.exception.iam.IamException;
import com.wl4g.devops.iam.common.i18n.DelegateBundleMessageSource;
import com.wl4g.devops.iam.context.ServerSecurityContext;

/**
 * Based context configuration
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年3月24日
 * @since
 */
public class BasedContextConfiguration {

	//
	// Locale i18n configuration.
	//

	/**
	 * Build a proxy message resourcer. Note that this bean can instantiate
	 * multiple different 'basenames', so the name must be unique
	 * 
	 * @param beanFactory
	 * @return
	 */
	@Bean(BEAN_DELEGATE_MSG_SOURCE)
	@ConditionalOnMissingBean
	public DelegateBundleMessageSource delegateBundleMessageSource() {
		return new DelegateBundleMessageSource(getClass());
	}

	//
	// Context's configuration
	//

	@Bean
	public IamContextManager iamContextManager() {
		return new IamContextManager();
	}

	public static class IamContextManager implements ApplicationContextAware {
		protected ApplicationContext actx;

		@Override
		public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
			Assert.notNull(applicationContext, "'applicationContext' must not be null");
			this.actx = applicationContext;
		}

		/**
		 * IAM security context handler Instance can be implemented by external
		 * customization
		 * 
		 * @return
		 */
		public ServerSecurityContext getServerSecurityContext() {
			/*
			 * Get system service instance(requirements must be implemented
			 * externally)
			 */
			ServerSecurityContext context = null;
			try {
				context = this.actx.getBean(ServerSecurityContext.class);
			} catch (NoSuchBeanDefinitionException e) {
				String errmsg = "\n\n==>>\tWhen you rely on Iam security as a plug-in, you must implement the '"
						+ ServerSecurityContext.class.getName() + "' interface yourself !\n";
				throw new IamException(errmsg, e);
			}
			return context;
		}
	}

}