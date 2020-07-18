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
package com.wl4g.devops.coss.common.internal.define;

/**
 * Miscellaneous constants used for oss client service.
 */
public final class COSSConstants {

	public static final String DEFAULT_CHARSET_NAME = "utf-8";
	public static final String DEFAULT_XML_ENCODING = "utf-8";

	public static final String DEFAULT_OBJECT_CONTENT_TYPE = "application/octet-stream";

	public static final int KB = 1024;
	public static final int DEFAULT_BUFFER_SIZE = 8 * KB;
	public static final int DEFAULT_STREAM_BUFFER_SIZE = 512 * KB;

	public static final long DEFAULT_FILE_SIZE_LIMIT = 5 * 1024 * 1024 * 1024L;

	public static final String RESOURCE_NAME_COMMON = "common";
	public static final String RESOURCE_NAME_COSS = "coss";

	public static final int OBJECT_NAME_MAX_LENGTH = 1024;

	public static final String LOGGER_PACKAGE_NAME = "com.wl4g.devops.coss";

	/** Represents a null OSS version ID */
	public static final String NULL_VERSION_ID = "null";

	/** URL encoding for OSS object keys */
	public static final String URL_ENCODING = "url";
}