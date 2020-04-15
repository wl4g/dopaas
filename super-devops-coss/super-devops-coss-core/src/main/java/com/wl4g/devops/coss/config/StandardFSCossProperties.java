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
package com.wl4g.devops.coss.config;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.File;

import static com.wl4g.devops.tool.common.lang.Assert2.*;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.springframework.util.Assert.hasText;

@Validated
public abstract class StandardFSCossProperties {

	final public static String PATH_TRASH = "/.trash";

	/**
	 * Bucket of native fileSystem root directory URI.
	 */
	@NotNull
	private File endpointRootDir = new File("/mnt/disk1/coss-bucket");

	private String downloadBaseUrl;

	/**
	 * Bucket of native fileSystem operation user.
	 */
	@NotBlank
	private String user = SystemUtils.USER_NAME;

	public File getEndpointRootDir() {
		return endpointRootDir;
	}

	public File getBucketPath(String bucketName) {
		return new File(getEndpointRootDir(), bucketName);
	}

	public File getObjectPath(String bucketName, String key) {
		return new File(getEndpointRootDir().getAbsolutePath() + File.separator + bucketName + File.separator + key);
	}

	public void setEndpointRootDir(File endpointRootDir) {
		if (!endpointRootDir.exists()) {
			endpointRootDir.mkdirs();
			isTrue(endpointRootDir.exists(), "Create endpointRootDir fail");
		}
		this.endpointRootDir = endpointRootDir;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		hasText(user, "Native fileSystem  user can not be null");
		this.user = user;
	}

	//
	// --- Function's. ---
	//

	/**
	 * Gets object key from hdfs file path.
	 * 
	 * <pre>
	 * e.g:
	 * getObjectKey("/mnt/disk1/coss-bucket/bucket1/sample1/hdfs-coss-sample.txt") => sample1/hdfs-coss-sample.txt
	 * </pre>
	 * 
	 * @param bucketName
	 * @param filePath
	 * @return
	 */
	public String getObjectKey(String bucketName, String filePath) {
		hasTextOf(bucketName, "bucketName");
		notNullOf(filePath, "objectFilePath");

		// Buckets root
		String prefixPath = getEndpointRootDir().getPath() + "/" + bucketName;
		// Substring object key
		if (!filePath.contains(prefixPath)) {
			throw new Error(format("Shouldn't be here, HDFS file path: '%s' will not contain: '%s'", filePath, prefixPath));
		}

		String objectKey = filePath.substring(filePath.indexOf(prefixPath) + prefixPath.length());
		// Storage protocol cannot start with '/'
		if (startsWith(objectKey, "/")) {
			objectKey = objectKey.substring(1);
		}
		return objectKey;
	}

	/**
	 * Gets bucket key from hdfs file path.
	 * 
	 * <pre>
	 * e.g:
	 * getObjectKey("/mnt/disk1/coss-bucket/bucket1/") => bucket1
	 * </pre>
	 * 
	 * @param filePath
	 * @return
	 */
	public String getBucketKey(String filePath) {
		notNullOf(filePath, "objectFilePath");

		// Buckets root
		String prefixPath = getEndpointRootDir().getPath();
		// Substring object key
		if (!filePath.contains(prefixPath)) {
			throw new Error(format("Shouldn't be here, HDFS file path: '%s' will not contain: '%s'", filePath, prefixPath));
		}

		String objectKey = filePath.substring(filePath.indexOf(prefixPath) + prefixPath.length());
		// Storage protocol cannot start with '/'
		if (startsWith(objectKey, "/")) {
			objectKey = objectKey.substring(1);
		}
		return objectKey;
	}

	public String getBucketPathTrash() {
		return getEndpointRootDir().getAbsolutePath() + PATH_TRASH + File.separator + System.currentTimeMillis();
	}

	public String getObjectPathTrash(String bucketName) {
		return getEndpointRootDir().getAbsolutePath() + File.separator + bucketName + File.separator + PATH_TRASH + File.separator
				+ System.currentTimeMillis();
	}

	public String getDownloadBaseUrl() {
		return downloadBaseUrl;
	}

	public void setDownloadBaseUrl(String downloadBaseUrl) {
		this.downloadBaseUrl = downloadBaseUrl;
	}
}
