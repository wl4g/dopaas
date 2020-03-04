package com.wl4g.devops.coss.aliyun.model;

import java.util.Map;
import java.util.TreeMap;

import com.wl4g.devops.coss.model.ObjectMetadata;

/**
 * Hdfs storage object metadata wrapper.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月2日
 * @since
 */
public class OssObjectMetadata extends ObjectMetadata {

	/**
	 * The user's custom metadata, whose prefix in http header is x-oss-meta-.
	 */
	private Map<String, String> userMetadata = new TreeMap<String, String>();

	/**
	 * The object's storage type, which only supports "normal" and "appendable"
	 * for now.
	 */
	private String objectType;

	/**
	 * Content MD5
	 */
	private String contentMd5;

	/**
	 * The Content-Encoding header which is to encode the object content.
	 */
	private String contentEncoding;

	/**
	 * The Cache-Control header. This is the standard http header.
	 */
	private String cacheControl;

	private String contentDisposition;

	/**
	 * The ETag of the object. ETag is the 128bit MD5 signature in Hex.
	 */
	private String etag;

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
	 * The version ID of the associated OSS object if available. Version IDs are
	 * only assigned to objects when an object is uploaded to an OSS bucket that
	 * has object versioning enabled.
	 */
	private String versionId;

	/**
	 * The service crc.
	 */
	private String serverCRC;

	public Map<String, String> getUserMetadata() {
		return userMetadata;
	}

	public void setUserMetadata(Map<String, String> userMetadata) {
		this.userMetadata.clear();
		if (userMetadata != null && !userMetadata.isEmpty()) {
			this.userMetadata.putAll(userMetadata);
		}
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getContentMd5() {
		return contentMd5;
	}

	public void setContentMd5(String contentMd5) {
		this.contentMd5 = contentMd5;
	}

	public String getContentEncoding() {
		return contentEncoding;
	}

	public void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}

	public String getCacheControl() {
		return cacheControl;
	}

	public void setCacheControl(String cacheControl) {
		this.cacheControl = cacheControl;
	}

	public String getContentDisposition() {
		return contentDisposition;
	}

	public void setContentDisposition(String contentDisposition) {
		this.contentDisposition = contentDisposition;
	}

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
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

	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

	public String getServerCRC() {
		return serverCRC;
	}

	public void setServerCRC(String serverCRC) {
		this.serverCRC = serverCRC;
	}

}
