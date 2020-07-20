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
package com.wl4g.devops.coss.common.model.bucket;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;

import com.wl4g.devops.coss.common.model.bucket.Bucket;

public class BucketList<T extends Bucket> {

	private List<T> buckets = new ArrayList<>(8);

	public List<T> getBucketList() {
		return buckets;
	}

	public void setBucketList(List<T> buckets) {
		this.buckets.clear();
		if (!isNull(buckets) && !buckets.isEmpty()) {
			this.buckets.addAll(buckets);
		}
	}

	public void clearBucketList() {
		this.buckets.clear();
	}

	@Override
	public String toString() {
		return "BucketList [buckets=" + buckets + "]";
	}

}