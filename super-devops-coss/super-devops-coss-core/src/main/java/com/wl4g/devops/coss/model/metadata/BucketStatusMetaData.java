package com.wl4g.devops.coss.model.metadata;

import java.io.Serializable;

/**
 * @author vjay
 * @date 2020-03-24 14:40:00
 */
public class BucketStatusMetaData implements Serializable {
	private static final long serialVersionUID = 381411777614066880L;

	/**
	 * Storage usage (B)
	 */
	private long storageUsage = 0;

	/**
	 * Number of request
	 */
	private long numberOfRequests = 0;

	/**
	 * Number of Documents
	 */
	private long numberOfDocuments = 0;

	/**
	 * Create Date
	 */
	private long createDate = 0;

	/**
	 * Modify Date
	 */
	private long modifyDate = 0;

	public long getStorageUsage() {
		return storageUsage;
	}

	public void setStorageUsage(long storageUsage) {
		this.storageUsage = storageUsage;
	}

	public long getNumberOfRequests() {
		return numberOfRequests;
	}

	public void setNumberOfRequests(long numberOfRequests) {
		this.numberOfRequests = numberOfRequests;
	}

	public long getNumberOfDocuments() {
		return numberOfDocuments;
	}

	public void setNumberOfDocuments(long numberOfDocuments) {
		this.numberOfDocuments = numberOfDocuments;
	}

	public long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}

	public long getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(long modifyDate) {
		this.modifyDate = modifyDate;
	}

}
