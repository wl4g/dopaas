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
package com.wl4g.devops.coss.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListBucketsRequest;
import com.amazonaws.services.s3.model.RestoreObjectRequest;
import com.wl4g.devops.coss.ServerCossEndpoint;
import com.wl4g.devops.coss.aws.config.S3CossProperties;
import com.wl4g.devops.coss.aws.model.*;
import com.wl4g.devops.coss.aws.model.bucket.S3Bucket;
import com.wl4g.devops.coss.aws.model.bucket.S3BucketList;
import com.wl4g.devops.coss.common.CossEndpoint.CossProvider;
import com.wl4g.devops.coss.common.exception.CossException;
import com.wl4g.devops.coss.common.exception.ServerCossException;
import com.wl4g.devops.coss.common.model.bucket.BucketMetadata;
import com.wl4g.devops.coss.common.model.ACL;
import com.wl4g.devops.coss.common.model.ObjectMetadata;
import com.wl4g.devops.coss.common.model.Owner;
import com.wl4g.devops.coss.common.model.CossPutObjectResult;
import com.wl4g.devops.coss.common.model.CossRestoreObjectRequest;
import com.wl4g.devops.coss.common.model.CossRestoreObjectResult;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import static com.wl4g.devops.components.tools.common.collection.Collections2.safeList;
import static java.lang.String.valueOf;
import static java.util.stream.Collectors.toList;

/**
 * Amazon S3 coss endpoint.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月10日
 * @since
 * @see {@link com.amazonaws.services.s3.AbstractAmazonS3}
 * @see <a href=
 *      "https://docs.aws.amazon.com/zh_cn/sdk-for-java/v1/developer-guide/examples-s3.html">Aws
 *      S3 docs</a>
 */
public class S3CossEndpoint extends ServerCossEndpoint<S3CossProperties> {

	/**
	 * {@link AmazonS3ClientBuilder}
	 */
	final protected AmazonS3 s3Client;

	public S3CossEndpoint(S3CossProperties config) {
		super(config);
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(config.getAccessKeyId(), config.getAccessKeySecret());
		this.s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds))
				.withRegion(Regions.fromName(config.getRegionName())).build();
	}

	@Override
	public CossProvider kind() {
		return CossProvider.AwsS3;
	}

	@Override
	public S3Bucket createBucket(String bucketName) {
		S3Bucket bucket = new S3Bucket();
		Bucket s3Bucket = s3Client.createBucket(bucketName);
		bucket.setName(s3Bucket.getName());
		bucket.setOwner(new Owner(s3Bucket.getOwner().getId(), s3Bucket.getOwner().getDisplayName()));
		bucket.setCreationDate(s3Bucket.getCreationDate());
		log.info("Created s3 bucket: {}", bucketName);
		return bucket;
	}

	@Override
	public S3BucketList listBuckets(String prefix, String marker, Integer maxKeys) {
		S3BucketList bucketList = new S3BucketList();

		ListBucketsRequest request = new ListBucketsRequest();
		// TODO Custom parameters condition.
		request.putCustomQueryParameter("", prefix);
		request.putCustomQueryParameter("", marker);
		request.putCustomQueryParameter("", valueOf(maxKeys));
		List<S3Bucket> s3Buckets = safeList((s3Client.listBuckets(request))).stream().map(b -> {
			S3Bucket bucket = new S3Bucket();
			bucket.setName(b.getName());
			bucket.setOwner(new Owner(b.getOwner().getId(), b.getOwner().getDisplayName()));
			bucket.setCreationDate(b.getCreationDate());
			return bucket;
		}).collect(toList());

		bucketList.getBucketList().addAll(s3Buckets);
		return bucketList;
	}

	@Override
	public void deleteBucket(String bucketName) {
		s3Client.deleteBucket(bucketName);
		log.info("Deleted s3 bucket: {}", bucketName);
	}

	@Override
	public BucketMetadata getBucketMetadata(String bucketName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public S3AccessControlList getBucketAcl(String bucketName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBucketAcl(String bucketName, ACL acl) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	@Override
	public S3ObjectListing listObjects(String bucketName, String prefix) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public S3ObjectValue getObject(String bucketName, String key) {

		return null;
	}

	@Override
	public CossPutObjectResult putObjectMetaData(String bucketName, String key, ObjectMetadata metadata) {
		return null;
	}

	@Override
	public CossPutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public S3CopyObjectResult copyObject(String sourceBucketName, String sourceKey, String destinationBucketName,
			String destinationKey) throws CossException, ServerCossException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteObject(String bucketName, String key) {
		s3Client.deleteObject(bucketName, key);
		log.info("Deleted s3 object of bucket: {}, key: {}", bucketName, key);
	}

	@Override
	public void deleteVersion(String bucketName, String key, String versionId) throws CossException, ServerCossException {
		// TODO Auto-generated method stub

	}

	@Override
	public CossRestoreObjectResult restoreObject(CossRestoreObjectRequest request) throws CossException, ServerCossException {
		RestoreObjectRequest s3Request = new RestoreObjectRequest(request.getBucketName(), request.getKey());
		s3Request.setVersionId(request.getVersionId());
		s3Client.restoreObjectV2(s3Request);
		return new CossRestoreObjectResult();
	}

	@Override
	public S3ObjectAcl getObjectAcl(String bucketName, String key) {
		S3ObjectAcl acl = new S3ObjectAcl();
		AccessControlList s3Acl = s3Client.getObjectAcl(bucketName, key);
		acl.setOwner(new Owner(s3Acl.getOwner().getId(), s3Acl.getOwner().getDisplayName()));
		// TODO
		// acl.setAcl(ACL.parse(s3Acl.getGrantsAsList()));
		return acl;
	}

	@Override
	public void setObjectAcl(String bucketName, String key, ACL acl) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean doesObjectExist(String bucketName, String key) {
		return s3Client.doesObjectExist(bucketName, key);
	}

	@Override
	public URL getUrl(String bucketName, String key) throws CossException, ServerCossException {
		return s3Client.getUrl(bucketName, key);
	}

}