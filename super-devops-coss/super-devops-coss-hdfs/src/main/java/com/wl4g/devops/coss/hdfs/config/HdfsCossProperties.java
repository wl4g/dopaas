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
package com.wl4g.devops.coss.hdfs.config;

import static com.wl4g.devops.components.tools.common.lang.Assert2.hasTextOf;
import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.springframework.util.Assert.hasText;

import java.net.URI;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.hadoop.fs.Path;
import org.springframework.validation.annotation.Validated;

@Validated
public class HdfsCossProperties {

	/**
	 * Bucket of hdfs root directory URI.
	 */
	@NotNull
	private URI endpointHdfsRootUri = URI.create("hdfs://localhost:8020/coss-bucket");

	/**
	 * Bucket of hdfs operation user.
	 */
	@NotBlank
	private String user;

	public URI getEndpointHdfsRootUri() {
		return endpointHdfsRootUri;
	}

	public void setEndpointHdfsRootUri(URI endpointHdfsRootUri) {
		this.endpointHdfsRootUri = endpointHdfsRootUri;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		hasText(user, "Hdfs user can not be null");
		this.user = user;
	}

	//
	// --- Function's. ---
	//

	private Path bucketRootPath;

	public Path getBucketRootPath() {
		if (isNull(bucketRootPath)) {
			bucketRootPath = new Path(getEndpointHdfsRootUri());
		}
		return bucketRootPath;
	}

	/**
	 * Gets object key from hdfs file path.
	 * 
	 * <pre>
	 * e.g:
	 * getObjectKey("hdfs://localhost:8020/coss-bucket/bucket1/sample1/hdfs-coss-sample.txt") => sample1/hdfs-coss-sample.txt
	 * </pre>
	 * 
	 * @param bucketName
	 * @param filePath
	 * @return
	 */
	public String getObjectKey(String bucketName, Path filePath) {
		hasTextOf(bucketName, "bucketName");
		notNullOf(filePath, "objectFilePath");
		// Buckets root
		String prefixPath = getEndpointHdfsRootUri().getPath() + "/" + bucketName;
		// Substring object key
		String uriPath = filePath.toUri().getPath();
		if (!uriPath.contains(prefixPath)) {
			throw new Error(format("Shouldn't be here, HDFS file path: '%s' will not contain: '%s'", filePath, prefixPath));
		}

		String objectKey = uriPath.substring(uriPath.indexOf(prefixPath) + prefixPath.length());
		// Storage protocol cannot start with '/'
		if (startsWith(objectKey, "/")) {
			objectKey = objectKey.substring(1);
		}
		return objectKey;
	}

}