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
package com.wl4g.devops.coss.aliyun.model;

import static com.wl4g.devops.components.tools.common.reflect.ReflectionUtils2.findField;
import static com.wl4g.devops.components.tools.common.reflect.ReflectionUtils2.getField;
import static com.wl4g.devops.components.tools.common.reflect.ReflectionUtils2.makeAccessible;
import static java.util.Objects.isNull;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.CannedAccessControlList;
import com.wl4g.devops.coss.common.model.ACL;
import com.wl4g.devops.coss.common.model.ObjectMetadata;

/**
 * Hdfs storage object metadata wrapper.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月2日
 * @since
 */
public class OssObjectMetadata extends ObjectMetadata {

	/**
	 * The object's storage type, which only supports "normal" and "appendable"
	 * for now.
	 */
	private String objectType;

	/**
	 * The object's server side encryption key ID.
	 */
	private String serverSideEncryptionKeyId;

	/**
	 * The object's server side encryption.
	 */
	private String serverSideEncryption;

	/**
	 * The request Id.
	 */
	private String requestId;

	/**
	 * The service crc.
	 */
	private Long serverCRC;

	public OssObjectMetadata() {
		super();
	}

	public OssObjectMetadata(com.aliyun.oss.model.ObjectMetadata metadata) {
		setUserMetadata(metadata.getUserMetadata());
		setContentLength(metadata.getContentLength());
		setContentType(metadata.getContentType());
		setContentMd5(metadata.getContentMD5());
		setContentEncoding(metadata.getContentEncoding());
		setCacheControl(metadata.getCacheControl());
		setContentDisposition(metadata.getContentDisposition());
		setEtag(metadata.getETag());
		setVersionId(metadata.getVersionId());
		setMtime(metadata.getLastModified().getTime());
		try {
			setEtime(metadata.getExpirationTime().getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		setAcl(ACL.parse(getAliyunOssObjectMetadata(metadata, OSSHeaders.OSS_OBJECT_ACL)));

		setObjectType(metadata.getObjectType());
		setServerSideEncryptionKeyId(metadata.getServerSideEncryptionKeyId());
		setServerSideEncryption(metadata.getServerSideEncryption());
		setRequestId(metadata.getRequestId());
		setServerCRC(metadata.getServerCRC());
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getServerSideEncryptionKeyId() {
		return serverSideEncryptionKeyId;
	}

	public void setServerSideEncryptionKeyId(String serverSideEncryptionKeyId) {
		this.serverSideEncryptionKeyId = serverSideEncryptionKeyId;
	}

	public String getServerSideEncryption() {
		return serverSideEncryption;
	}

	public void setServerSideEncryption(String serverSideEncryption) {
		this.serverSideEncryption = serverSideEncryption;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Long getServerCRC() {
		return serverCRC;
	}

	public void setServerCRC(Long serverCRC) {
		this.serverCRC = serverCRC;
	}

	/**
	 * Convert to {@link com.aliyun.oss.model.ObjectMetadata}
	 * 
	 * @return
	 */
	public com.aliyun.oss.model.ObjectMetadata toAliyunOssObjectMetadata() {
		com.aliyun.oss.model.ObjectMetadata _metadata = new com.aliyun.oss.model.ObjectMetadata();
		_metadata.setUserMetadata(getUserMetadata());
		_metadata.setContentLength(getContentLength());
		_metadata.setContentType(getContentType());
		_metadata.setContentMD5(getContentMd5());
		_metadata.setContentEncoding(getContentEncoding());
		_metadata.setCacheControl(getCacheControl());
		_metadata.setContentDisposition(getContentDisposition());
		_metadata.setLastModified(new Date(getMtime()));
		_metadata.setExpirationTime(new Date(getEtime()));
		_metadata.setObjectAcl(CannedAccessControlList.parse(getAcl().toString()));

		_metadata.setServerSideEncryptionKeyId(getServerSideEncryptionKeyId());
		setServerSideEncryption(getServerSideEncryption());
		return _metadata;
	}

	/**
	 * Gets aliyun oss {@link com.aliyun.oss.model.ObjectMetadata#metadata}
	 * 
	 * @param metadata
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	final public static String getAliyunOssObjectMetadata(com.aliyun.oss.model.ObjectMetadata metadata, String key) {
		Map<String, String> aliyunOssObjectMetadata = (Map<String, String>) getField(ossMetadataField, metadata);
		return !isNull(aliyunOssObjectMetadata) ? aliyunOssObjectMetadata.get(key) : null;
	}

	final private static Field ossMetadataField;

	static {
		ossMetadataField = findField(com.aliyun.oss.model.ObjectMetadata.class, "metadata");
		makeAccessible(ossMetadataField);
	}

}