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

import java.util.ArrayList;
import java.util.List;

import com.wl4g.devops.coss.common.model.ObjectSummary;

public class ObjectListing<T extends ObjectSummary> {

	/**
	 * A list of summary information describing the objects stored in the bucket
	 */
	private List<T> objectSummaries = new ArrayList<>();

	private List<String> commonPrefixes = new ArrayList<String>();

	private String bucketName;

	private String nextMarker;

	private Boolean isTruncated = false;

	private String prefix;

	private String marker;

	private int maxKeys;

	private String delimiter = "/";

	private String encodingType = "UTF-8";

	public List<T> getObjectSummaries() {
		return objectSummaries;
	}

	public void addObjectSummary(T objectSummary) {
		this.objectSummaries.add(objectSummary);
	}

	public void setObjectSummaries(List<T> objectSummaries) {
		this.objectSummaries.clear();
		if (objectSummaries != null && !objectSummaries.isEmpty()) {
			this.objectSummaries.addAll(objectSummaries);
		}
	}

	public void clearObjectSummaries() {
		this.objectSummaries.clear();
	}

	public String getNextMarker() {
		return nextMarker;
	}

	public void setNextMarker(String nextMarker) {
		this.nextMarker = nextMarker;
	}

	public List<String> getCommonPrefixes() {
		return commonPrefixes;
	}

	public void addCommonPrefix(String commonPrefix) {
		this.commonPrefixes.add(commonPrefix);
	}

	public void setCommonPrefixes(List<String> commonPrefixes) {
		this.commonPrefixes.clear();
		if (commonPrefixes != null && !commonPrefixes.isEmpty()) {
			this.commonPrefixes.addAll(commonPrefixes);
		}
	}

	public void clearCommonPrefixes() {
		this.commonPrefixes.clear();
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getMarker() {
		return marker;
	}

	public void setMarker(String marker) {
		this.marker = marker;
	}

	public int getMaxKeys() {
		return maxKeys;
	}

	public void setMaxKeys(int maxKeys) {
		this.maxKeys = maxKeys;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getEncodingType() {
		return encodingType;
	}

	public void setEncodingType(String encodingType) {
		this.encodingType = encodingType;
	}

	public Boolean isTruncated() {
		return isTruncated;
	}

	public void setTruncated(Boolean isTruncated) {
		this.isTruncated = isTruncated;
	}

	@Override
	public String toString() {
		return "ObjectListing [objectSummaries=" + objectSummaries + ", commonPrefixes=" + commonPrefixes + ", bucketName="
				+ bucketName + ", nextMarker=" + nextMarker + ", isTruncated=" + isTruncated + ", prefix=" + prefix + ", marker="
				+ marker + ", maxKeys=" + maxKeys + ", delimiter=" + delimiter + ", encodingType=" + encodingType + "]";
	}

}