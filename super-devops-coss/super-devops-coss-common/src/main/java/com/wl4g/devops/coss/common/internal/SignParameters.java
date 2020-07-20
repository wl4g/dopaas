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
package com.wl4g.devops.coss.common.internal;

import java.util.Arrays;
import java.util.List;
import static com.wl4g.devops.coss.common.utils.RequestParameters.*;
import static com.wl4g.devops.coss.common.internal.ResponseHeader.*;

/**
 * {@link SignParameters}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年6月29日 v1.0.0
 * @see
 */
public class SignParameters {

	public static final String AUTHORIZATION_PREFIX = "OSS ";

	public static final String AUTHORIZATION_PREFIX_V2 = "OSS2 ";

	public static final String AUTHORIZATION_V2 = "OSS2";

	public static final String AUTHORIZATION_ACCESS_KEY_ID = "AccessKeyId";

	public static final String AUTHORIZATION_ADDITIONAL_HEADERS = "AdditionalHeaders";

	public static final String AUTHORIZATION_SIGNATURE = "Signature";

	public static final String NEW_LINE = "\n";

	public static final List<String> SIGNED_PARAMTERS = Arrays.asList(new String[] { SUBRESOURCE_ACL, SUBRESOURCE_UPLOADS,
			SUBRESOURCE_LOCATION, SUBRESOURCE_CORS, SUBRESOURCE_LOGGING, SUBRESOURCE_WEBSITE, SUBRESOURCE_REFERER,
			SUBRESOURCE_LIFECYCLE, SUBRESOURCE_DELETE, SUBRESOURCE_APPEND, SUBRESOURCE_TAGGING, SUBRESOURCE_OBJECTMETA, UPLOAD_ID,
			PART_NUMBER, SECURITY_TOKEN, POSITION, RESPONSE_HEADER_CACHE_CONTROL, RESPONSE_HEADER_CONTENT_DISPOSITION,
			RESPONSE_HEADER_CONTENT_ENCODING, RESPONSE_HEADER_CONTENT_LANGUAGE, RESPONSE_HEADER_CONTENT_TYPE,
			RESPONSE_HEADER_EXPIRES, SUBRESOURCE_IMG, SUBRESOURCE_STYLE, STYLE_NAME, SUBRESOURCE_REPLICATION,
			SUBRESOURCE_REPLICATION_PROGRESS, SUBRESOURCE_REPLICATION_LOCATION, SUBRESOURCE_CNAME, SUBRESOURCE_BUCKET_INFO,
			SUBRESOURCE_COMP, SUBRESOURCE_QOS, SUBRESOURCE_LIVE, SUBRESOURCE_STATUS, SUBRESOURCE_VOD, SUBRESOURCE_START_TIME,
			SUBRESOURCE_END_TIME, SUBRESOURCE_PROCESS, SUBRESOURCE_PROCESS_CONF, SUBRESOURCE_SYMLINK, SUBRESOURCE_STAT,
			SUBRESOURCE_UDF, SUBRESOURCE_UDF_NAME, SUBRESOURCE_UDF_IMAGE, SUBRESOURCE_UDF_IMAGE_DESC, SUBRESOURCE_UDF_APPLICATION,
			SUBRESOURCE_UDF_LOG, SUBRESOURCE_RESTORE, SUBRESOURCE_VRESIONS, SUBRESOURCE_VRESIONING, SUBRESOURCE_VRESION_ID,
			SUBRESOURCE_ENCRYPTION, SUBRESOURCE_POLICY, SUBRESOURCE_REQUEST_PAYMENT, OSS_TRAFFIC_LIMIT, SUBRESOURCE_QOS_INFO,
			SUBRESOURCE_ASYNC_FETCH, SEQUENTIAL, OSS_REQUEST_PAYER, VPCIP, VIP });

}