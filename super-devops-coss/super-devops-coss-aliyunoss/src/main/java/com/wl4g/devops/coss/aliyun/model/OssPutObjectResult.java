package com.wl4g.devops.coss.aliyun.model;

import com.wl4g.devops.coss.model.PutObjectResult;

/**
 * The result class of a Put Object request.
 */
public class OssPutObjectResult extends PutObjectResult {

	// Object ETag
	private String eTag;

	// Object Version Id
	private String versionId;

	/**
	 * Gets the target {@link OSSObject}'s ETag.
	 * 
	 * @return Target OSSObject's ETag.
	 */
	public String getETag() {
		return eTag;
	}

	/**
	 * Sets the target {@link OSSObject}'s ETag.
	 * 
	 * @param eTag
	 *            Target OSSObject's ETag.
	 */
	public void setETag(String eTag) {
		this.eTag = eTag;
	}

	/**
	 * Gets version id.
	 * 
	 * @return version id.
	 */
	public String getVersionId() {
		return versionId;
	}

	/**
	 * Sets version id.
	 * 
	 * @param versionId
	 */
	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

}
