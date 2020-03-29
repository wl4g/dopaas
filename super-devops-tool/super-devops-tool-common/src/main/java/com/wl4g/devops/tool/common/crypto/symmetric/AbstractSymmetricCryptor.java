/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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
package com.wl4g.devops.tool.common.crypto.symmetric;

import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import com.wl4g.devops.tool.common.log.SmartLogger;

/**
 * Abstract symmetric algorithm implementation.
 *
 * @author wangl.sir
 * @version v1.0 2019年1月21日
 * @since
 */
abstract class AbstractSymmetricCryptor implements SymmetricCryptor {

	final public static String DEFAULT_ENCODING = "UTF-8";
	final public static int DEFAULT_AES_KEYSIZE = 128;

	final protected SmartLogger log = getLogger(getClass());

}