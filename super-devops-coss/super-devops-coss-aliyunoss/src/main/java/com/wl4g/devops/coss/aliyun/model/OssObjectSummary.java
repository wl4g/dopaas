package com.wl4g.devops.coss.aliyun.model;

import com.wl4g.devops.coss.model.ObjectSummary;

/**
 * {@link ObjectValue} summary information.
 */
public class OssObjectSummary extends ObjectSummary {

	/**
	 * The object ETag. ETag is a 128bit MD5 signature about the object in hex.
	 */
	private String eTag;

	/**
	 * Constructor.
	 */
	public OssObjectSummary() {
	}

	/**
	 * Gets the object ETag. ETag is a 128bit MD5 signature about the object in
	 * hex.
	 * 
	 * @return ETag value.
	 */
	public String getETag() {
		return eTag;
	}

	/**
	 * Sets the object ETag.
	 * 
	 * @param eTag
	 *            ETag value.
	 */
	public void setETag(String eTag) {
		this.eTag = eTag;
	}

}
