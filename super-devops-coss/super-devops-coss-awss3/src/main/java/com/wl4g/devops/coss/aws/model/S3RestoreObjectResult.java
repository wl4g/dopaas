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

import com.amazonaws.services.s3.model.RestoreObjectResult;
import com.wl4g.devops.coss.common.model.CossRestoreObjectRequest;

public class S3RestoreObjectResult extends CossRestoreObjectRequest {

	/**
	 * Indicate if the requester is charged for conducting this operation from
	 * Requester Pays Buckets.
	 */
	private boolean isRequesterCharged;

	/**
	 * The path in the provided S3 output location where Select results will be
	 * restored to
	 */
	private String restoreOutputPath;

	/**
	 * @return if the requester is charged for conducting this operation from
	 *         Requester Pays Buckets.
	 */
	public boolean isRequesterCharged() {
		return isRequesterCharged;
	}

	public void setRequesterCharged(boolean isRequesterCharged) {
		this.isRequesterCharged = isRequesterCharged;
	}

	/**
	 * @return the path in the provided S3 output location where Select results
	 *         will be restored to.
	 */
	public String getRestoreOutputPath() {
		return restoreOutputPath;
	}

	public void setRestoreOutputPath(String restoreOutputPath) {
		this.restoreOutputPath = restoreOutputPath;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		if (getRestoreOutputPath() != null)
			sb.append("restoreOutputPath: ").append(getRestoreOutputPath()).append(",");
		sb.append("isRequestCharged: ").append(isRequesterCharged());
		sb.append("}");
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof RestoreObjectResult)) {
			return false;
		}

		final RestoreObjectResult other = (RestoreObjectResult) obj;

		if (other.getRestoreOutputPath() == null ^ this.getRestoreOutputPath() == null)
			return false;
		if (other.getRestoreOutputPath() != null && !other.getRestoreOutputPath().equals(this.getRestoreOutputPath()))
			return false;
		if (other.isRequesterCharged() != this.isRequesterCharged())
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hashCode = 1;

		hashCode = prime * hashCode + ((getRestoreOutputPath() == null) ? 0 : getRestoreOutputPath().hashCode());
		return hashCode;
	}

}