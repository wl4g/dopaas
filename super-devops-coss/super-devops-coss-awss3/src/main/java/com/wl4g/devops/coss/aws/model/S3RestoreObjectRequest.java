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
package com.wl4g.devops.coss.aws.model;

import com.amazonaws.services.s3.model.GlacierJobParameters;
import com.amazonaws.services.s3.model.OutputLocation;
import com.amazonaws.services.s3.model.RestoreRequestType;
import com.amazonaws.services.s3.model.SelectParameters;
import com.amazonaws.services.s3.model.Tier;
import com.wl4g.devops.coss.common.exception.ServerCossException;
import com.wl4g.devops.coss.common.model.CossRestoreObjectRequest;

/**
 * {@link S3RestoreObjectRequest}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年3月21日 v1.0.0
 * @see {@link com.amazonaws.services.s3.model.RestoreObjectRequest}
 */
public class S3RestoreObjectRequest extends CossRestoreObjectRequest {

	/**
	 * Lifetime of the active copy in days. Do not use with restores that
	 * specify OutputLocation.
	 */
	private int expirationInDays;

	/**
	 * If enabled, the requester is charged for conducting this operation from
	 * Requester Pays Buckets.
	 */
	private boolean isRequesterPays;

	/**
	 * Glacier related parameters pertaining to this job.
	 * <p>
	 * This should not be used for restores with a specified OutputLocation.
	 * </p>
	 */
	private GlacierJobParameters glacierJobParameters;

	/**
	 * Type of restore request.
	 */
	private String type;

	/**
	 * Glacier retrieval tier at which the restore will be processed.
	 */
	private String tier;

	/**
	 * The optional description for the job.
	 */
	private String description;

	/**
	 * Describes the parameters for Select job types.
	 */
	private SelectParameters selectParameters;

	/**
	 * Describes the location where the restore job's output is stored.
	 */
	private OutputLocation outputLocation;

	/**
	 * <p>
	 * Constructs a new RestoreObjectRequest.
	 * </p>
	 *
	 * <p>
	 * When using this API with an access point, you must direct requests to the
	 * access point hostname. The access point hostname takes the form
	 * <i>AccessPointName</i>-<i>AccountId</i>.s3-accesspoint.<i>Region</i>.amazonaws.com.
	 * </p>
	 * <p>
	 * When using this operation using an access point through the AWS SDKs, you
	 * provide the access point ARN in place of the bucket name. For more
	 * information about access point ARNs, see <a
	 * href=\"https://docs.aws.amazon.com/AmazonS3/latest/dev/using-access-points.html\">
	 * Using Access Points</a> in the <i>Amazon Simple Storage Service Developer
	 * Guide</i>.
	 * </p>
	 *
	 * @param bucketName
	 *            The name of the bucket, or access point ARN, containing the
	 *            reference to the object to restore which is now stored in
	 *            Amazon Glacier.
	 * @param key
	 *            The key, the name of the reference to the object to restore,
	 *            which is now stored in Amazon Glacier.
	 *
	 * @see CossRestoreObjectRequest#RestoreObjectRequest(String, String, int)
	 */
	public S3RestoreObjectRequest(String bucketName, String key) {
		this(bucketName, key, -1);
	}

	/**
	 * <p>
	 * Constructs a new RestoreObjectRequest.
	 * </p>
	 *
	 * <p>
	 * When using this API with an access point, you must direct requests to the
	 * access point hostname. The access point hostname takes the form
	 * <i>AccessPointName</i>-<i>AccountId</i>.s3-accesspoint.<i>Region</i>.amazonaws.com.
	 * </p>
	 * <p>
	 * When using this operation using an access point through the AWS SDKs, you
	 * provide the access point ARN in place of the bucket name. For more
	 * information about access point ARNs, see <a
	 * href=\"https://docs.aws.amazon.com/AmazonS3/latest/dev/using-access-points.html\">
	 * Using Access Points</a> in the <i>Amazon Simple Storage Service Developer
	 * Guide</i>.
	 * </p>
	 *
	 * @param bucketName
	 *            The name of the bucket, or access point ARN, containing the
	 *            reference to the object to restore which is now stored in
	 *            Amazon Glacier.
	 * @param key
	 *            The key, the name of the reference to the object to restore,
	 *            which is now stored in Amazon Glacier.
	 * @param expirationInDays
	 *            The time, in days, between when an object is restored to the
	 *            bucket and when it expires
	 *
	 * @see CossRestoreObjectRequest#RestoreObjectRequest(String, String)
	 */
	public S3RestoreObjectRequest(String bucketName, String key, int expirationInDays) {
		super(bucketName, key, null);
		this.expirationInDays = expirationInDays;
	}

	/**
	 * Sets the time, in days, between when an object is uploaded to the bucket
	 * and when it expires.
	 *
	 * <p>
	 * Do not use with restores that specify OutputLocation.
	 * </p>
	 */
	public void setExpirationInDays(int expirationInDays) {
		this.expirationInDays = expirationInDays;
	}

	/**
	 * Returns the time in days from an object's creation to its expiration.
	 */
	public int getExpirationInDays() {
		return expirationInDays;
	}

	/**
	 * Returns true if the user has enabled Requester Pays option when
	 * conducting this operation from Requester Pays Bucket; else false.
	 *
	 * <p>
	 * If a bucket is enabled for Requester Pays, then any attempt to upload or
	 * download an object from it without Requester Pays enabled will result in
	 * a 403 error and the bucket owner will be charged for the request.
	 *
	 * <p>
	 * Enabling Requester Pays disables the ability to have anonymous access to
	 * this bucket
	 *
	 * @return true if the user has enabled Requester Pays option for conducting
	 *         this operation from Requester Pays Bucket.
	 */
	public boolean isRequesterPays() {
		return isRequesterPays;
	}

	/**
	 * Used for conducting this operation from a Requester Pays Bucket. If set
	 * the requester is charged for requests from the bucket.
	 *
	 * <p>
	 * If a bucket is enabled for Requester Pays, then any attempt to upload or
	 * download an object from it without Requester Pays enabled will result in
	 * a 403 error and the bucket owner will be charged for the request.
	 *
	 * <p>
	 * Enabling Requester Pays disables the ability to have anonymous access to
	 * this bucket.
	 *
	 * @param isRequesterPays
	 *            Enable Requester Pays option for the operation.
	 */
	public void setRequesterPays(boolean isRequesterPays) {
		this.isRequesterPays = isRequesterPays;
	}

	/**
	 * @return Glacier related parameters pertaining to this job.
	 */
	public GlacierJobParameters getGlacierJobParameters() {
		return glacierJobParameters;
	}

	/**
	 * Sets Glacier related parameters pertaining to this job.
	 *
	 * <p>
	 * This should not be used for restores with a specified OutputLocation.
	 * </p>
	 *
	 * @param glacierJobParameters
	 *            New value for Glacier job parameters.
	 */
	public void setGlacierJobParameters(GlacierJobParameters glacierJobParameters) {
		this.glacierJobParameters = glacierJobParameters;
	}

	/**
	 * @return The type of restore request.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the restore request type.
	 *
	 * @param type
	 *            New value for type.
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Sets the restore request type.
	 *
	 * @param restoreRequestType
	 *            New value for restore request type.
	 * @return This object for method chaining.
	 */
	public CossRestoreObjectRequest withType(RestoreRequestType restoreRequestType) {
		setType(restoreRequestType == null ? null : restoreRequestType.toString());
		return this;
	}

	/**
	 * @return The glacier retrieval tier.
	 */
	public String getTier() {
		return tier;
	}

	/**
	 * Sets the glacier retrieval tier.
	 *
	 * @param tier
	 *            New value for tier.
	 */
	public void setTier(String tier) {
		this.tier = tier;
	}

	/**
	 * Sets the glacier retrieval tier.
	 *
	 * @param tier
	 *            New value for tier.
	 * @return This object for method chaining.
	 */
	public CossRestoreObjectRequest withTier(Tier tier) {
		this.tier = tier == null ? null : tier.toString();
		return this;
	}

	/**
	 * @return the description for the job.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description for the job.
	 *
	 * @param description
	 *            New value for the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the parameters for select job types.
	 */
	public SelectParameters getSelectParameters() {
		return selectParameters;
	}

	/**
	 * Sets the parameters for select job types.
	 *
	 * @param selectParameters
	 *            New value for selectParameters
	 */
	public void setSelectParameters(SelectParameters selectParameters) {
		this.selectParameters = selectParameters;
	}

	/**
	 * @return the location where the restore job's output is stored.
	 */
	public OutputLocation getOutputLocation() {
		return outputLocation;
	}

	/**
	 * Sets the location where the restore job's output is stored.
	 *
	 * @param outputLocation
	 *            New value for output location
	 */
	public void setOutputLocation(OutputLocation outputLocation) {
		this.outputLocation = outputLocation;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof S3RestoreObjectRequest)) {
			return false;
		}

		final S3RestoreObjectRequest other = (S3RestoreObjectRequest) obj;

		if (other.getExpirationInDays() != this.getExpirationInDays())
			return false;
		if (other.getBucketName() == null ^ this.getBucketName() == null)
			return false;
		if (other.getBucketName() != null && !other.getBucketName().equals(this.getBucketName()))
			return false;
		if (other.getKey() == null ^ this.getKey() == null)
			return false;
		if (other.getKey() != null && !other.getKey().equals(this.getKey()))
			return false;
		if (other.getVersionId() == null ^ this.getVersionId() == null)
			return false;
		if (other.getVersionId() != null && !other.getVersionId().equals(this.getVersionId()))
			return false;
		if (other.getGlacierJobParameters() == null ^ this.getGlacierJobParameters() == null)
			return false;
		if (other.getGlacierJobParameters() != null && !other.getGlacierJobParameters().equals(this.getGlacierJobParameters()))
			return false;
		if (other.getType() == null ^ this.getType() == null)
			return false;
		if (other.getType() != null && !other.getType().equals(this.getType()))
			return false;
		if (other.getTier() == null ^ this.getTier() == null)
			return false;
		if (other.getTier() != null && !other.getTier().equals(this.getTier()))
			return false;
		if (other.getDescription() == null ^ this.getDescription() == null)
			return false;
		if (other.getDescription() != null && !other.getDescription().equals(this.getDescription()))
			return false;
		if (other.getSelectParameters() == null ^ this.getSelectParameters() == null)
			return false;
		if (other.getSelectParameters() != null && !other.getSelectParameters().equals(this.getSelectParameters()))
			return false;
		if (other.getOutputLocation() == null ^ this.getOutputLocation() == null)
			return false;
		if (other.getOutputLocation() != null && !other.getOutputLocation().equals(this.getOutputLocation()))
			return false;
		if (other.isRequesterPays() != this.isRequesterPays())
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hashCode = 1;

		hashCode = prime * hashCode + ((getBucketName() == null) ? 0 : getBucketName().hashCode());
		hashCode = prime * hashCode + ((getKey() == null) ? 0 : getKey().hashCode());
		hashCode = prime * hashCode + ((getVersionId() == null) ? 0 : getVersionId().hashCode());
		hashCode = prime * hashCode + ((getGlacierJobParameters() == null) ? 0 : getGlacierJobParameters().hashCode());
		hashCode = prime * hashCode + ((getType() == null) ? 0 : getType().hashCode());
		hashCode = prime * hashCode + (getTier() != null ? getTier().hashCode() : 0);
		hashCode = prime * hashCode + (getDescription() != null ? getDescription().hashCode() : 0);
		hashCode = prime * hashCode + (getSelectParameters() != null ? getSelectParameters().hashCode() : 0);
		hashCode = prime * hashCode + (getOutputLocation() != null ? getOutputLocation().hashCode() : 0);
		return hashCode;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("ExpirationInDays: ").append(expirationInDays).append(",");
		sb.append("IsRequesterPays").append(isRequesterPays()).append(",");

		if (getBucketName() != null)
			sb.append("BucketName: ").append(getBucketName()).append(",");
		if (getKey() != null)
			sb.append("Key: ").append(getKey()).append(",");
		if (getVersionId() != null)
			sb.append("VersionId: ").append(getVersionId()).append(",");
		if (getGlacierJobParameters() != null)
			sb.append("GlacierJobParameters: ").append(getGlacierJobParameters()).append(",");
		if (getType() != null)
			sb.append("RestoreRequestType: ").append(getType()).append(",");
		if (getTier() != null)
			sb.append("Tier: ").append(getTier()).append(",");
		if (getDescription() != null)
			sb.append("Description: ").append(getDescription()).append(",");
		if (getSelectParameters() != null)
			sb.append("SelectParameters: ").append(getSelectParameters()).append(",");
		if (getOutputLocation() != null)
			sb.append("OutputLocation").append(getOutputLocation());
		sb.append("}");
		return sb.toString();
	}

	@Override
	public S3RestoreObjectRequest clone() {
		try {
			return (S3RestoreObjectRequest) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new ServerCossException(e);
		}
	}

}