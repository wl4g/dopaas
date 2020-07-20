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
package com.wl4g.devops.coss.common.utils;

public interface COSSHeaders extends HttpHeaders {

	static final String OSS_PREFIX = "x-coss-";
	static final String OSS_USER_METADATA_PREFIX = "x-coss-meta-";

	static final String OSS_CANNED_ACL = "x-coss-acl";
	static final String STORAGE_CLASS = "x-coss-storage-class";
	static final String OSS_VERSION_ID = "x-coss-version-id";

	static final String OSS_SERVER_SIDE_ENCRYPTION = "x-coss-server-side-encryption";
	static final String OSS_SERVER_SIDE_ENCRYPTION_KEY_ID = "x-coss-server-side-encryption-key-id";

	static final String GET_OBJECT_IF_MODIFIED_SINCE = "If-Modified-Since";
	static final String GET_OBJECT_IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
	static final String GET_OBJECT_IF_MATCH = "If-Match";
	static final String GET_OBJECT_IF_NONE_MATCH = "If-None-Match";

	static final String HEAD_OBJECT_IF_MODIFIED_SINCE = "If-Modified-Since";
	static final String HEAD_OBJECT_IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
	static final String HEAD_OBJECT_IF_MATCH = "If-Match";
	static final String HEAD_OBJECT_IF_NONE_MATCH = "If-None-Match";

	static final String COPY_OBJECT_SOURCE = "x-coss-copy-source";
	static final String COPY_SOURCE_RANGE = "x-coss-copy-source-range";
	static final String COPY_OBJECT_SOURCE_IF_MATCH = "x-coss-copy-source-if-match";
	static final String COPY_OBJECT_SOURCE_IF_NONE_MATCH = "x-coss-copy-source-if-none-match";
	static final String COPY_OBJECT_SOURCE_IF_UNMODIFIED_SINCE = "x-coss-copy-source-if-unmodified-since";
	static final String COPY_OBJECT_SOURCE_IF_MODIFIED_SINCE = "x-coss-copy-source-if-modified-since";
	static final String COPY_OBJECT_METADATA_DIRECTIVE = "x-coss-metadata-directive";
	static final String COPY_OBJECT_TAGGING_DIRECTIVE = "x-coss-tagging-directive";

	static final String OSS_HEADER_REQUEST_ID = "x-coss-request-id";
	static final String OSS_HEADER_VERSION_ID = "x-coss-version-id";

	static final String ORIGIN = "origin";
	static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
	static final String ACCESS_CONTROL_REQUEST_HEADER = "Access-Control-Request-Headers";

	static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
	static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
	static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
	static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";

	static final String OSS_SECURITY_TOKEN = "x-coss-security-token";

	static final String OSS_NEXT_APPEND_POSITION = "x-coss-next-append-position";
	static final String OSS_HASH_CRC64_ECMA = "x-coss-hash-crc64ecma";
	static final String OSS_OBJECT_TYPE = "x-coss-object-type";

	static final String OSS_OBJECT_ACL = "x-coss-object-acl";

	static final String OSS_HEADER_CALLBACK = "x-coss-callback";
	static final String OSS_HEADER_CALLBACK_VAR = "x-coss-callback-var";
	static final String OSS_HEADER_SYMLINK_TARGET = "x-coss-symlink-target";

	static final String OSS_STORAGE_CLASS = "x-coss-storage-class";
	static final String OSS_RESTORE = "x-coss-restore";
	static final String OSS_ONGOING_RESTORE = "ongoing-request=\"true\"";

	static final String OSS_BUCKET_REGION = "x-coss-bucket-region";

	static final String OSS_SELECT_PREFIX = "x-coss-select";
	static final String OSS_SELECT_CSV_ROWS = OSS_SELECT_PREFIX + "-csv-rows";
	static final String OSS_SELECT_OUTPUT_RAW = OSS_SELECT_PREFIX + "-output-raw";
	static final String OSS_SELECT_CSV_SPLITS = OSS_SELECT_PREFIX + "-csv-splits";
	static final String OSS_SELECT_INPUT_LINE_RANGE = OSS_SELECT_PREFIX + "-line-range";
	static final String OSS_SELECT_INPUT_SPLIT_RANGE = OSS_SELECT_PREFIX + "-split-range";

	static final String OSS_TAGGING = "x-coss-tagging";

	static final String OSS_REQUEST_PAYER = "x-coss-request-payer";

	static final String OSS_HEADER_TRAFFIC_LIMIT = "x-coss-traffic-limit";

	static final String OSS_HEADER_TASK_ID = "x-coss-task-id";

}