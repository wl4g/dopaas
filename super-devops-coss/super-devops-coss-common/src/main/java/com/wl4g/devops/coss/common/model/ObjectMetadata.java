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
package com.wl4g.devops.coss.common.model;

import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.toJSONString;
import static java.util.Objects.isNull;

import java.util.Map;
import java.util.TreeMap;

import com.wl4g.devops.coss.common.model.ACL;
import com.wl4g.devops.coss.common.model.ObjectKey;
import com.wl4g.devops.coss.common.model.ObjectMetadata;

/**
 * Base storage object metadata wrapper.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月2日
 * @since
 */
public class ObjectMetadata implements Comparable<ObjectMetadata> {

	private ObjectKey path;

	/**
	 * <p>
	 * Gets the Content-Length HTTP header indicating the size of the associated
	 * object in bytes.
	 * </p>
	 */
	private long contentLength;

	/**
	 * <p>
	 * Gets the Content-Type HTTP header, which indicates the type of content
	 * stored in the associated object. The value of this header is a standard
	 * MIME type.
	 * </p>
	 */
	private String contentType;

	/**
	 * <p>
	 * Sets the base64 encoded 128-bit MD5 digest of the associated object
	 * (content - not including headers) according to RFC 1864. This data is
	 * used as a message integrity check to verify that the data received by
	 * Amazon S3 is the same data that the caller sent. If set to null,then the
	 * MD5 digest is removed from the metadata.
	 * </p>
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

	/**
	 * <p>
	 * Sets the optional Content-Disposition HTTP header, which specifies
	 * presentational information such as the recommended filename for the
	 * object to be saved as.
	 * </p>
	 */
	private String contentDisposition;

	/**
	 * The ETag of the object. ETag is the 128bit MD5 signature in Hex.
	 */
	private String etag;

	/**
	 * The version ID of the associated OSS object if available. Version IDs are
	 * only assigned to objects when an object is uploaded to an OSS bucket that
	 * has object versioning enabled.
	 */
	private String versionId;

	/**
	 * Last modified time
	 */
	private Long mtime;

	/**
	 * Last access time
	 */
	private Long atime;

	/**
	 * Expiration time
	 */
	private Long etime;

	/**
	 * Access control List
	 */
	private ACL acl;

	/**
	 * The user's custom metadata, whose prefix in http header is x-oss-meta-.
	 */
	private Map<String, String> userMetadata = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

	public ObjectKey getPath() {
		return path;
	}

	public void setPath(ObjectKey path) {
		this.path = path;
	}

	public long getContentLength() {
		return contentLength;
	}

	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
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

	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

	public Long getMtime() {
		return mtime;
	}

	public void setMtime(Long mtime) {
		this.mtime = mtime;
	}

	public Long getAtime() {
		return atime;
	}

	public void setAtime(Long atime) {
		this.atime = atime;
	}

	public Long getEtime() {
		return etime;
	}

	public void setEtime(Long etime) {
		this.etime = etime;
	}

	public ACL getAcl() {
		return acl;
	}

	public void setAcl(ACL acl) {
		this.acl = acl;
	}

	public Map<String, String> getUserMetadata() {
		return userMetadata;
	}

	public void setUserMetadata(Map<String, String> userMetadata) {
		if (!isNull(userMetadata)) {
			this.userMetadata.putAll(userMetadata);
		}
	}

	/**
	 * Compare this object to another object
	 * 
	 * @param o
	 *            the object to be compared.
	 * @return a negative integer, zero, or a positive integer as this object is
	 *         less than, equal to, or greater than the specified object.
	 * 
	 * @throws ClassCastException
	 *             if the specified object's is not of type FileStatus
	 */
	@Override
	public int compareTo(ObjectMetadata that) {
		return getPath().compareTo(that.getPath());
	}

	/**
	 * Compare if this object is equal to another object
	 * 
	 * @param o
	 *            the object to be compared.
	 * @return true if two file status has the same path name; false if not.
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (this == o) {
			return true;
		}
		if (!(o instanceof ObjectMetadata)) {
			return false;
		}
		ObjectMetadata other = (ObjectMetadata) o;
		return this.getPath().equals(other.getPath());
	}

	/**
	 * Returns a hash code value for the object, which is defined as the hash
	 * code of the path name.
	 *
	 * @return a hash code value for the path name.
	 */
	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

	@Override
	public String toString() {
		return "ObjectMetadata => " + toJSONString(this);
	}

}