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
package com.wl4g.devops.coss.access;

import java.io.InputStream;

import com.wl4g.devops.coss.access.model.GenericCossParameter;
import com.wl4g.devops.coss.common.model.AccessControlList;
import com.wl4g.devops.coss.common.model.ObjectAcl;
import com.wl4g.devops.coss.common.model.ObjectListing;
import com.wl4g.devops.coss.common.model.ObjectMetadata;
import com.wl4g.devops.coss.common.model.ObjectSummary;
import com.wl4g.devops.coss.common.model.ObjectSymlink;
import com.wl4g.devops.coss.common.model.ObjectValue;
import com.wl4g.devops.coss.common.model.CossPutObjectResult;
import com.wl4g.devops.coss.common.model.bucket.Bucket;
import com.wl4g.devops.coss.common.model.bucket.BucketList;
import com.wl4g.devops.coss.common.model.bucket.BucketMetadata;

/**
 * Browse or access the coss file viewer.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月4日
 * @since
 */
public interface CossAccessor {

	// --- Bucket's function ---

	/**
	 * Creates {@link Bucket} instance. The bucket name specified must be
	 * globally unique and follow the naming rules from
	 * 
	 * @param param
	 *            generic COSS parameter
	 * @param bucketName
	 *            bucket name
	 */
	Bucket createBucket(GenericCossParameter param, String bucketName);

	/**
	 * Returns all {@link Bucket} instances of the current account that meet the
	 * conditions specified.
	 * 
	 * @param param
	 *            generic COSS parameter
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
	BucketList<? extends Bucket> listBuckets(GenericCossParameter param, String prefix, String marker, Integer maxKeys);

	/**
	 * Deletes the {@link Bucket} instance. A non-empty bucket could not be
	 * deleted.
	 * 
	 * @param param
	 *            generic COSS parameter
	 * @param bucketName
	 *            bucket name to delete.
	 */
	void deleteBucket(GenericCossParameter param, String bucketName);

	/**
	 * Gets the metadata of {@link Bucket}.
	 * 
	 * @param param
	 *            generic COSS parameter
	 * @param bucketName
	 *            Bucket name.
	 *
	 * @return The {@link BucketMetadata} instance.
	 */
	BucketMetadata getBucketMetadata(GenericCossParameter param, String bucketName);

	/**
	 * Returns the Access control List (ACL) of the {@link Bucket} instance.
	 * 
	 * @param param
	 *            generic COSS parameter
	 * @param bucketName
	 *            Bucket Name.
	 * @return Access Control List(ACL) {@link AccessControlList}.
	 */
	AccessControlList getBucketAcl(GenericCossParameter param, String bucketName);

	/**
	 * Applies the Access Control List(ACL) on the {@link Bucket}.
	 * 
	 * @param param
	 *            generic COSS parameter
	 * @param bucketName
	 *            Bucket name.
	 * @param acl
	 *            {@link CannedAccessControlList} instance. If the instance is
	 *            null, no ACL change on the bucket (but the request is still
	 *            sent).
	 */
	void setBucketAcl(GenericCossParameter param, String bucketName, String acl);

	// --- Object's function ---

	/**
	 * Lists all objects under the specified {@link Bucket} with the specified
	 * prefix.
	 * 
	 * @param param
	 *            generic COSS parameter
	 * @param bucketName
	 *            Bucket name.
	 * @param prefix
	 *            The prefix returned object must have.
	 * @return A {@link ObjectListing} instance that has all objects
	 * @throws OSSException
	 * @throws ClientException
	 */
	ObjectListing<? extends ObjectSummary> listObjects(GenericCossParameter param, String bucketName, String prefix);

	/**
	 * Gets a {@link ObjectValue} from {@link Bucket}.
	 * 
	 * @param param
	 *            generic COSS parameter
	 * @param bucketName
	 *            Bucket name.
	 * @param key
	 *            Object Key.
	 * @return A {@link OSSObject} instance. The caller is responsible to close
	 *         the connection after usage.
	 */
	ObjectValue getObject(GenericCossParameter param, String bucketName, String key);

	/**
	 * Uploads the file to the {@link Bucket} from the @{link InputStream} with
	 * the {@link ObjectMetadata} information。
	 * 
	 * @param param
	 *            generic COSS parameter
	 * @param bucketName
	 *            Bucket name.
	 * @param key
	 *            Object key.
	 * @param localInputFile
	 *            {@link InputStream} instance to write from. It must be
	 *            readable.
	 * @param metadata
	 *            The {@link ObjectMetadata} instance. If it does not specify
	 *            the Content-Length information, the data is encoded by chunked
	 *            tranfer encoding.
	 */
	CossPutObjectResult putObject(GenericCossParameter param, String bucketName, String key, InputStream input,
			ObjectMetadata metadata);

	/**
	 * Deletes the specified {@link ObjectValue} by bucket name and object key.
	 * 
	 * @param param
	 *            generic COSS parameter
	 * @param bucketName
	 *            Bucket name.
	 * @param key
	 *            Object key.
	 */
	void deleteObject(GenericCossParameter param, String bucketName, String key);

	/**
	 * Gets the Access Control List (ACL) of the OSS object.
	 * 
	 * @param param
	 *            generic COSS parameter
	 * @param bucketName
	 *            Bucket name.
	 * @param key
	 *            Object Key.
	 * @return The {@link ObjectAcl} instance of the object.
	 */
	ObjectAcl getObjectAcl(GenericCossParameter param, String bucketName, String key);

	/**
	 * Sets the Access Control List (ACL) on a {@link ObjectValue} instance.
	 * 
	 * @param param
	 *            generic COSS parameter
	 * @param bucketName
	 *            Bucket name.
	 * @param key
	 *            Object Key.
	 * @param acl
	 *            One of the three values: Private, PublicRead or
	 *            PublicReadWrite.
	 */
	void setObjectAcl(GenericCossParameter param, String bucketName, String key, String acl);

	/**
	 * Creates a symlink link to a target file under the bucket---this is not
	 * supported for archive class bucket.
	 * 
	 * @param param
	 *            generic COSS parameter
	 * @param bucketName
	 *            Bucket name.
	 * @param symlink
	 *            symlink name.
	 * @param target
	 *            target file key.
	 */
	void createSymlink(GenericCossParameter param, String bucketName, String symlink, String target);

	/**
	 * Gets the symlink information for the given symlink name.
	 * 
	 * @param param
	 *            generic COSS parameter
	 * @param bucketName
	 *            Bucket name.
	 * @param symlink
	 *            The symlink name.
	 * @return The symlink information, including the target file name and its
	 *         metadata.
	 */
	ObjectSymlink getSymlink(GenericCossParameter param, String bucketName, String symlink);

}