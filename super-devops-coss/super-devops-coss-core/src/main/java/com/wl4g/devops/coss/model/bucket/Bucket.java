package com.wl4g.devops.coss.model.bucket;

import java.util.Date;

import com.wl4g.devops.coss.model.Owner;

/**
 * Bucket is the namespace in OSS. You could think it's a folder or container
 * under OSS.
 * <p>
 * Bucket name is globally unique in OSS and is immutable. Every object must
 * belong to a bucket. An application such as picture sharing website could be
 * mapped to one or multiple buckets. An OSS account could only create up to 10
 * bucket. And there's no limit on the files count or size under a bucket.
 * </p>
 * Bucket naming rules:
 * <ul>
 * <li>Can only contain low case letter, number or dash(-).</li>
 * <li>Can only start with low case letter or number.</li>
 * <li>The length must be between 3 to 63 bytes.</li>
 * </ul>
 */
public class Bucket {

	/** Bucket name */
	private String name;

	/** Bucket owner */
	private Owner owner;

	/** Bucket location */
	private String location;

	/** Created date. */
	private Date creationDate;

	/** Storage class (e.g. aliyun-oss => Standard, IA, Archive) */
	private String storageType;

	/** External endpoint.It could be accessed from anywhere. */
	private String extranetEndpoint;

	/**
	 * Internal endpoint. It could be accessed within AliCloud under the same
	 * location.
	 */
	private String intranetEndpoint;

	/**
	 * Default constructor.
	 */
	public Bucket() {
	}

	/**
	 * Constructor with the bucket name parameter.
	 * 
	 * @param name
	 *            Bucket name.
	 */
	public Bucket(String name) {
		setName(name);
	}

	/**
	 * The override of toString(). Returns the bucket name, creation date, owner
	 * and location, with optional storage class.
	 */
	@Override
	public String toString() {
		if (storageType == null) {
			return "OSSBucket [name=" + getName() + ", creationDate=" + getCreationDate() + ", owner=" + getOwner()
					+ ", location=" + getLocation() + "]";
		} else {
			return "OSSBucket [name=" + getName() + ", creationDate=" + getCreationDate() + ", owner=" + getOwner()
					+ ", location=" + getLocation() + ", storageClass=" + getStorageType() + "]";
		}
	}

	/**
	 * Gets the {@link Owner}.
	 * 
	 * @return The bucket owner or null if the owner is not known.
	 */
	public Owner getOwner() {
		return owner;
	}

	/**
	 * Sets the bucket owner (only used by SDK itself).
	 * 
	 * @param owner
	 *            Bucket owner.
	 */
	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	/**
	 * Gets the bucket's creation time.
	 * 
	 * @return Bucket's creation time or null if the creation time is unknown.
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * Sets teh bucket's creation time.(it's only used by SDK itself).
	 * 
	 * @param creationDate
	 *            Bucket's creation time.
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * Gets the bucket name
	 * 
	 * @return Bucket name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the bucket name (should only be used by the SDK itself).
	 * 
	 * @param name
	 *            Bucket name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the bucket location.
	 * 
	 * @return Bucket location.
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Sets the bucket location.
	 * 
	 * @param location
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Gets the storage class
	 * 
	 * @return storage class
	 */
	public String getStorageType() {
		return storageType;
	}

	/**
	 * Sets the bucket's storage class
	 * 
	 * @param storageClass
	 */
	public void setStorageType(String storageClass) {
		this.storageType = storageClass;
	}

	/**
	 * Gets the external endpoint.
	 * 
	 * @return external endpoint
	 */
	public String getExtranetEndpoint() {
		return extranetEndpoint;
	}

	/**
	 * Sets the external endpoint.
	 * 
	 * @param endpoint
	 *            external endpoint
	 */
	public void setExtranetEndpoint(String endpoint) {
		this.extranetEndpoint = endpoint;
	}

	/**
	 * Gets the internal endpoint.
	 * 
	 * @return Internal endpoint
	 */
	public String getIntranetEndpoint() {
		return intranetEndpoint;
	}

	/**
	 * Sets the internal endpoint.
	 * 
	 * @param endpoint
	 *            Internal endpoint
	 */
	public void setIntranetEndpoint(String endpoint) {
		this.intranetEndpoint = endpoint;
	}

}
