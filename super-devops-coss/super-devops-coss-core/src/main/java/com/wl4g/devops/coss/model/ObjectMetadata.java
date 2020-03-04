package com.wl4g.devops.coss.model;

import static com.wl4g.devops.tool.common.serialize.JacksonUtils.toJSONString;

import java.util.Map;
import java.util.TreeMap;

/**
 * Base storage object metadata wrapper.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月2日
 * @since
 */
public abstract class ObjectMetadata implements Comparable<ObjectMetadata> {

	private ObjectKey path;
	private long contentLength;
	private String contentType;
	private long mtime; // Last modified time
	private long atime; // Last access time
	private long etime; // Expiration time
	private ACL acl; // Access control List
	private Map<String, Object> metadata = new TreeMap<>();

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

	public long getMtime() {
		return mtime;
	}

	public void setMtime(long mtime) {
		this.mtime = mtime;
	}

	public long getAtime() {
		return atime;
	}

	public void setAtime(long atime) {
		this.atime = atime;
	}

	public long getEtime() {
		return etime;
	}

	public void setEtime(long etime) {
		this.etime = etime;
	}

	public ACL getAcl() {
		return acl;
	}

	public void setAcl(ACL acl) {
		this.acl = acl;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata.clear();
		if (metadata != null && !metadata.isEmpty()) {
			this.metadata.putAll(metadata);
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
