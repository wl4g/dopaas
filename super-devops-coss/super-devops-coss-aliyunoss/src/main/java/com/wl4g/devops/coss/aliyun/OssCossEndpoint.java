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
package com.wl4g.devops.coss.aliyun;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.AccessControlList;
import com.aliyun.oss.model.*;
import com.aliyun.oss.model.ObjectAcl;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.PutObjectResult;
import com.wl4g.devops.coss.ServerCossEndpoint;
import com.wl4g.devops.coss.aliyun.config.AliyunOssProperties;
import com.wl4g.devops.coss.aliyun.model.*;
import com.wl4g.devops.coss.aliyun.model.bucket.OssBucket;
import com.wl4g.devops.coss.aliyun.model.bucket.OssBucketList;
import com.wl4g.devops.coss.aliyun.model.bucket.OssBucketMetadata;
import com.wl4g.devops.coss.common.exception.CossException;
import com.wl4g.devops.coss.common.exception.ServerCossException;
import com.wl4g.devops.coss.common.model.*;
import com.wl4g.devops.coss.common.model.ObjectMetadata;
import com.wl4g.devops.coss.common.model.Owner;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;

import static java.util.stream.Collectors.toList;

public class OssCossEndpoint extends ServerCossEndpoint<AliyunOssProperties> {

	/**
	 * Aliyun OSS client.
	 */
	final protected OSS ossClient;

	public OssCossEndpoint(AliyunOssProperties config) {
		super(config);
		// Constructs a client instance with your account for accessing OSS
		this.ossClient = new OSSClientBuilder().build(config.getEndpoint(), config.getAccessKeyId(), config.getAccessKeySecret());
		log.info("Initialized OSS client: {}", ossClient);
	}

	@Override
	public CossProvider kind() {
		return CossProvider.AliyunOss;
	}

	/**
	 * The bucket name specified must be globally unique and follow the naming
	 * rules from
	 * https://www.alibabacloud.com/help/doc-detail/31827.htm?spm=a3c0i.o32012en
	 * .a3.1.64ece5e0jPpa2t.
	 */
	@Override
	public OssBucket createBucket(String bucketName) {
		OssBucket bucket = new OssBucket();
		Bucket ossBucket = ossClient.createBucket(bucketName);
		bucket.setName(ossBucket.getName());
		bucket.setLocation(ossBucket.getLocation());
		bucket.setOwner(new Owner(ossBucket.getOwner().getId(), ossBucket.getOwner().getDisplayName()));
		bucket.setStorageType(ossBucket.getStorageClass().name());
		bucket.setCreationDate(ossBucket.getCreationDate());
		bucket.setExtranetEndpoint(ossBucket.getExtranetEndpoint());
		bucket.setIntranetEndpoint(ossBucket.getIntranetEndpoint());
		return bucket;
	}

	@SuppressWarnings("unchecked")
	@Override
	public OssBucketList listBuckets(String prefix, String marker, Integer maxKeys) {
		OssBucketList buckets = new OssBucketList();
		BucketList ossBucketList = ossClient.listBuckets(prefix, marker, maxKeys);
		buckets.setBucketList(ossBucketList.getBucketList().stream().map(b -> new OssBucket(b)).collect(toList()));
		return buckets;
	}

	@Override
	public void deleteBucket(String bucketName) {
		ossClient.deleteBucket(bucketName);
	}

	@Override
	public OssBucketMetadata getBucketMetadata(String bucketName) {
		OssBucketMetadata metadata = new OssBucketMetadata();
		BucketMetadata ossMetadata = ossClient.getBucketMetadata(bucketName);
		metadata.setBucketName(bucketName);
		metadata.setBucketRegion(ossMetadata.getBucketRegion());
		metadata.setAttributes(ossMetadata.getHttpMetadata());
		return metadata;
	}

	@Override
	public OssAccessControlList getBucketAcl(String bucketName) {
		OssAccessControlList acl = new OssAccessControlList();
		AccessControlList ossAcl = ossClient.getBucketAcl(bucketName);
		acl.setOwner(new Owner(ossAcl.getOwner().getId(), ossAcl.getOwner().getDisplayName()));
		acl.setAcl(ACL.parse(ossAcl.getCannedACL().toString()));
		return acl;
	}

	@Override
	public void setBucketAcl(String bucketName, ACL acl) {
		ossClient.setBucketAcl(bucketName, CannedAccessControlList.parse(acl.toString()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public OssObjectListing listObjects(String bucketName, String prefix) {
		OssObjectListing objectList = new OssObjectListing();
		ObjectListing ossObjectListing = ossClient.listObjects(bucketName, prefix);
		objectList.setBucketName(ossObjectListing.getBucketName());
		objectList.setPrefix(ossObjectListing.getPrefix());
		objectList.setEncodingType(ossObjectListing.getEncodingType());
		objectList.setDelimiter(ossObjectListing.getDelimiter());
		objectList.setMarker(ossObjectListing.getMarker());
		objectList.setMaxKeys(ossObjectListing.getMaxKeys());
		objectList.setTruncated(ossObjectListing.isTruncated());
		objectList.setNextMarker(ossObjectListing.getNextMarker());
		objectList.setCommonPrefixes(ossObjectListing.getCommonPrefixes());
		return objectList;
	}

	@Override
	public OssObjectValue getObject(String bucketName, String key) {
		OssObjectValue objectValue = new OssObjectValue();
		OSSObject ossObject = ossClient.getObject(bucketName, key);
		objectValue.setBucketName(ossObject.getBucketName());
		objectValue.setKey(ossObject.getKey());
		objectValue.setObjectContent(ossObject.getObjectContent());
		objectValue.setMetadata(new OssObjectMetadata(ossObject.getObjectMetadata()));
		return objectValue;
	}

	@Override
	public CossPutObjectResult putObjectMetaData(String bucketName, String key, ObjectMetadata metadata) {
		// TODO
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public OssPutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata) {
		try {
			OssPutObjectResult putObjectRes = new OssPutObjectResult();
			PutObjectResult ossPutObjectRes = ossClient.putObject(bucketName, key, input,
					((OssObjectMetadata) metadata).toAliyunOssObjectMetadata());
			putObjectRes.setETag(ossPutObjectRes.getETag());
			putObjectRes.setVersionId(ossPutObjectRes.getVersionId());
			return putObjectRes;
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	@Override
	public OssCopyObjectResult copyObject(String sourceBucketName, String sourceKey, String destinationBucketName,
			String destinationKey) throws CossException, ServerCossException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteObject(String bucketName, String key) {
		ossClient.deleteObject(bucketName, key);
	}

	@Override
	public void deleteVersion(String bucketName, String key, String versionId) throws CossException, ServerCossException {
		ossClient.deleteVersion(bucketName, key, versionId);
	}

	@Override
	public CossRestoreObjectResult restoreObject(CossRestoreObjectRequest request) throws CossException, ServerCossException {
		RestoreObjectResult ossRes = ossClient.restoreObject(request.getBucketName(), request.getKey());
		log.debug("Restore object result: {}", ossRes);
		return new CossRestoreObjectResult();
	}

	@Override
	public OssObjectAcl getObjectAcl(String bucketName, String key) {
		OssObjectAcl objectAcl = new OssObjectAcl();
		ObjectAcl ossObjectAcl = ossClient.getObjectAcl(bucketName, key);
		objectAcl.setOwner(new Owner(ossObjectAcl.getOwner().getId(), ossObjectAcl.getOwner().getDisplayName()));
		objectAcl.setVersionId(ossObjectAcl.getVersionId());
		objectAcl.setAcl(ACL.parse(ossObjectAcl.getPermission().toString()));
		return objectAcl;
	}

	@Override
	public void setObjectAcl(String bucketName, String key, ACL acl) {
		ossClient.setObjectAcl(bucketName, key, CannedAccessControlList.parse(acl.toString()));
	}

	@Override
	public boolean doesObjectExist(String bucketName, String key) {
		return ossClient.doesObjectExist(bucketName, key);
	}

	@Override
	public void createSymlink(String bucketName, String symlink, String target) {
		ossClient.createSymlink(bucketName, symlink, target);
	}

	@Override
	public ObjectSymlink getSymlink(String bucketName, String symlink) {
		ObjectSymlink objectSymlink = new ObjectSymlink();
		OSSSymlink ossSymlink = ossClient.getSymlink(bucketName, symlink);
		objectSymlink.setSymlink(ossSymlink.getSymlink());
		objectSymlink.setTarget(ossSymlink.getTarget());
		objectSymlink.setMetadata(new OssObjectMetadata(ossSymlink.getMetadata()));
		return objectSymlink;
	}

}