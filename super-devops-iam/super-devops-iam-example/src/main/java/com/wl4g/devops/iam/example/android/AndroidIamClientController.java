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
package com.wl4g.devops.iam.example.android;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * IAM client android SDK multiple background system authenticated routing
 * controller.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月23日
 * @since
 */
public class AndroidIamClientController implements Serializable {
	final private static long serialVersionUID = 2737562691695613598L;

	/**
	 * Table for storing authentication information.
	 */
	final protected ConcurrentMap<ServiceType, String> authTable = new ConcurrentHashMap<>(16);

	/**
	 * Singleton holder.
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年7月23日
	 * @since
	 */
	private static class SingletonHolder {

		/**
		 * Singleton instance
		 * 
		 * @return
		 */
		final private static AndroidIamClientController INSTANCE = new AndroidIamClientController();

		/**
		 * Get singleton instance
		 * 
		 * @return
		 */
		synchronized static AndroidIamClientController getInstance() {
			return INSTANCE;
		}

	}

	/**
	 * Get default IAM client controller instance.
	 * 
	 * @return
	 */
	public static AndroidIamClientController getDefault() {
		return SingletonHolder.getInstance();
	}

	/**
	 * Service type definition.
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年7月23日
	 * @since
	 */
	public static enum ServiceType {

		/**
		 * IAM certification background service.
		 */
		IAM_SERV,

		/**
		 * Management console back-end service.
		 */
		MP_SERV,

		/**
		 * Portal backstage service.
		 */
		PORTAL_SERV,

		/**
		 * Energy consumption management background Service
		 */
		EMS_SERV,

		/**
		 * Trend forecasting background service.
		 */
		TRENDS_SERV,

		/**
		 * Family cloud backstage service
		 */
		HIOT_SERV,

		/**
		 * Industrial internet of Things cloud background Service
		 */
		IIOT_SERV;

	}

}
