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
package com.wl4g.devops.coss;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.Assert.notNull;

import java.io.InputStream;

import com.wl4g.devops.common.framework.operator.Operator;
import com.wl4g.devops.coss.model.ACL;
import com.wl4g.devops.coss.model.AccessControlList;
import com.wl4g.devops.coss.model.ObjectAcl;
import com.wl4g.devops.coss.model.ObjectListing;
import com.wl4g.devops.coss.model.ObjectMetadata;
import com.wl4g.devops.coss.model.ObjectSummary;
import com.wl4g.devops.coss.model.ObjectSymlink;
import com.wl4g.devops.coss.model.ObjectValue;
import com.wl4g.devops.coss.model.PutObjectResult;
import com.wl4g.devops.coss.model.bucket.Bucket;
import com.wl4g.devops.coss.model.bucket.BucketList;
import com.wl4g.devops.coss.model.bucket.BucketMetadata;
import com.wl4g.devops.coss.CossEndpoint.CossProvider;

/**
 * Composite object storage server file system API.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年2月28日
 * @since
 */
public interface CossEndpoint extends Operator<CossProvider> {

	// --- Bucket's function ---

	/**
	 * Creates {@link Bucket} instance.
	 * 
	 * @param bucketName
	 *            bucket name
	 */
	Bucket createBucket(String bucketName);

	/**
	 * Returns all {@link Bucket} instances of the current account that meet the
	 * conditions specified.
	 * 
	 * @param prefix
	 *            The prefix of the bucket name returned. If null, the bucket
	 *            name could have any prefix.
	 * @param marker
	 *            The start point in the lexicographic order for the buckets to
	 *            return. If null, return the buckets from the beginning in the
	 *            lexicographic order. For example, if the account has buckets
	 *            bk1, bk2, bk3. If the marker is set as bk2, then only bk2 and
	 *            bk3 meet the criteria. But if the marker is null, then all
	 *            three buckets meet the criteria.
	 * @param maxKeys
	 *            Max bucket count to return. The valid value is from 1 to 1000,
	 *            default is 100 if it's null.
	 * @return The list of {@link Bucket} instances.
	 */
	<T extends Bucket> BucketList<T> listBuckets(String prefix, String marker, Integer maxKeys);

	/**
	 * Deletes the {@link Bucket} instance. A non-empty bucket could not be
	 * deleted.
	 * 
	 * @param bucketName
	 *            bucket name to delete.
	 */
	void deleteBucket(String bucketName);

	/**
	 * Gets the metadata of {@link Bucket}.
	 * 
	 * @param bucketName
	 *            Bucket name.
	 *
	 * @return The {@link BucketMetadata} instance.
	 */
	BucketMetadata getBucketMetadata(String bucketName);

	/**
	 * Returns the Access control List (ACL) of the {@link Bucket} instance.
	 * 
	 * @param bucketName
	 *            Bucket Name.
	 * @return Access Control List(ACL) {@link AccessControlList}.
	 */
	AccessControlList getBucketAcl(String bucketName);

	/**
	 * Applies the Access Control List(ACL) on the {@link Bucket}.
	 * 
	 * @param bucketName
	 *            Bucket name.
	 * @param acl
	 *            {@link CannedAccessControlList} instance. If the instance is
	 *            null, no ACL change on the bucket (but the request is still
	 *            sent).
	 */
	void setBucketAcl(String bucketName, ACL acl);

	// --- Object's function ---

	/**
	 * Lists all objects under the specified {@link Bucket}
	 * 
	 * @param bucketName
	 *            Bucket name
	 * @return {@link ObjectListing} instance that has all objects.
	 */
	<T extends ObjectSummary> ObjectListing<T> listObjects(String bucketName);

	/**
	 * Lists all objects under the specified {@link Bucket} with the specified
	 * prefix.
	 * 
	 * @param bucketName
	 *            Bucket name.
	 * @param prefix
	 *            The prefix returned object must have.
	 * @return A {@link ObjectListing} instance that has all objects
	 * @throws OSSException
	 * @throws ClientException
	 */
	<T extends ObjectSummary> ObjectListing<T> listObjects(String bucketName, String prefix);

	/**
	 * Gets a {@link ObjectValue} from {@link Bucket}.
	 * 
	 * @param bucketName
	 *            Bucket name.
	 * @param key
	 *            Object Key.
	 * @return A {@link OSSObject} instance. The caller is responsible to close
	 *         the connection after usage.
	 */
	ObjectValue getObject(String bucketName, String key);

	/**
	 * Uploads the file to the {@link Bucket} from the {@link InputStream}
	 * instance. It overwrites the existing one and the bucket must exist.
	 * 
	 * @param bucketName
	 *            Bucket name.
	 * @param key
	 *            object key.
	 * @param input
	 *            {@link InputStream} instance to write from. The must be
	 *            readable.
	 */
	default PutObjectResult putObject(String bucketName, String key, InputStream input) {
		return putObject(bucketName, key, input, null);
	}

	/**
	 * Uploads the file to the {@link Bucket} from the @{link InputStream} with
	 * the {@link ObjectMetadata} information。
	 * 
	 * @param bucketName
	 *            Bucket name.
	 * @param key
	 *            Object key.
	 * @param input
	 *            {@link InputStream} instance to write from. It must be
	 *            readable.
	 * @param metadata
	 *            The {@link ObjectMetadata} instance. If it does not specify
	 *            the Content-Length information, the data is encoded by chunked
	 *            tranfer encoding.
	 */
	PutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata);

	/**
	 * Deletes the specified {@link ObjectValue} by bucket name and object key.
	 * 
	 * @param bucketName
	 *            Bucket name.
	 * @param key
	 *            Object key.
	 */
	void deleteObject(String bucketName, String key);

	/**
	 * Gets the Access Control List (ACL) of the OSS object.
	 * 
	 * @param bucketName
	 *            Bucket name.
	 * @param key
	 *            Object Key.
	 * @return The {@link ObjectAcl} instance of the object.
	 */
	ObjectAcl getObjectAcl(String bucketName, String key);

	/**
	 * Sets the Access Control List (ACL) on a {@link ObjectValue} instance.
	 * 
	 * @param bucketName
	 *            Bucket name.
	 * @param key
	 *            Object Key.
	 * @param acl
	 *            One of the three values: Private, PublicRead or
	 *            PublicReadWrite.
	 */
	void setObjectAcl(String bucketName, String key, ACL acl);

	/**
	 * Checks if a specific {@link ObjectValue} exists under the specific
	 * {@link Bucket}. 302 Redirect or OSS mirroring will not impact the result
	 * of this function.
	 *
	 * @param bucketName
	 *            Bucket name.
	 * @param key
	 *            Object Key.
	 * @return True if exists; false if not.
	 */
	boolean doesObjectExist(String bucketName, String key);

	/**
	 * Creates a symlink link to a target file under the bucket---this is not
	 * supported for archive class bucket.
	 * 
	 * @param bucketName
	 *            Bucket name.
	 * @param symlink
	 *            symlink name.
	 * @param target
	 *            target file key.
	 */
	void createSymlink(String bucketName, String symlink, String target);

	/**
	 * Gets the symlink information for the given symlink name.
	 * 
	 * @param bucketName
	 *            Bucket name.
	 * @param symlink
	 *            The symlink name.
	 * @return The symlink information, including the target file name and its
	 *         metadata.
	 */
	ObjectSymlink getSymlink(String bucketName, String symlink);

	/**
	 * VCS type definitions.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月5日
	 * @since
	 */
	public static enum CossProvider {

		/** COSS provider for aliyun oss. */
		AliyunOss("aliyunoss"),

		/** COSS provider for aws s3. */
		AwsS3("awss3"),

		/** COSS provider for hdfs. */
		Hdfs("hdfs"),

		/** COSS provider for glusterfs. */
		GlusterFs("glusterfs"),

		/** COSS provider for native fs. */
		NativeFs("nativefs");

		final private String value;

		private CossProvider(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		/**
		 * Safe converter string to {@link CossProvider}
		 * 
		 * @param cossProvider
		 * @return
		 */
		final public static CossProvider safeOf(String cossProvider) {
			if (isBlank(cossProvider))
				return null;

			for (CossProvider t : values())
				if (t.getValue().equalsIgnoreCase(cossProvider) || t.name().equalsIgnoreCase(cossProvider))
					return t;

			return null;
		}

		/**
		 * Converter string to {@link CossProvider}
		 * 
		 * @param cossProvider
		 * @return
		 */
		final public static CossProvider of(String cossProvider) {
			CossProvider type = safeOf(cossProvider);
			notNull(type, format("Unsupported COSS provider for %s", cossProvider));
			return type;
		}

	}

}
