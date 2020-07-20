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
package com.wl4g.devops.coss.client;

import java.io.InputStream;
import java.net.URL;

import com.wl4g.devops.coss.client.config.ClientCossConfiguration;
import com.wl4g.devops.coss.common.auth.CredentialsProvider;
import com.wl4g.devops.coss.common.exception.CossException;
import com.wl4g.devops.coss.common.exception.ServerCossException;
import com.wl4g.devops.coss.common.model.ACL;
import com.wl4g.devops.coss.common.model.AccessControlList;
import com.wl4g.devops.coss.common.model.CopyObjectResult;
import com.wl4g.devops.coss.common.model.ObjectAcl;
import com.wl4g.devops.coss.common.model.ObjectListing;
import com.wl4g.devops.coss.common.model.ObjectMetadata;
import com.wl4g.devops.coss.common.model.ObjectSummary;
import com.wl4g.devops.coss.common.model.ObjectSymlink;
import com.wl4g.devops.coss.common.model.ObjectValue;
import com.wl4g.devops.coss.common.model.CossPutObjectResult;
import com.wl4g.devops.coss.common.model.CossRestoreObjectRequest;
import com.wl4g.devops.coss.common.model.CossRestoreObjectResult;
import com.wl4g.devops.coss.common.model.bucket.Bucket;
import com.wl4g.devops.coss.common.model.bucket.BucketList;
import com.wl4g.devops.coss.common.model.bucket.BucketMetadata;

/**
 * {@link CossClientImpl}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年6月28日
 * @since
 */
public class CossClientImpl implements CossClient {
	
	

	public CossClientImpl(String endpoint, CredentialsProvider credsProvider, ClientCossConfiguration config) {
	}

	@Override
	public Bucket createBucket(String bucketName) throws CossException, ServerCossException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Bucket> BucketList<T> listBuckets(String prefix, String marker, Integer maxKeys)
			throws CossException, ServerCossException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteBucket(String bucketName) throws CossException, ServerCossException {
		// TODO Auto-generated method stub

	}

	@Override
	public BucketMetadata getBucketMetadata(String bucketName) throws CossException, ServerCossException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccessControlList getBucketAcl(String bucketName) throws CossException, ServerCossException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBucketAcl(String bucketName, ACL acl) throws CossException, ServerCossException {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends ObjectSummary> ObjectListing<T> listObjects(String bucketName, String prefix)
			throws CossException, ServerCossException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectValue getObject(String bucketName, String key) throws CossException, ServerCossException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CossPutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata)
			throws CossException, ServerCossException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CopyObjectResult copyObject(String sourceBucketName, String sourceKey, String destinationBucketName,
			String destinationKey) throws CossException, ServerCossException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteObject(String bucketName, String key) throws CossException, ServerCossException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteVersion(String bucketName, String key, String versionId) throws CossException, ServerCossException {
		// TODO Auto-generated method stub

	}

	@Override
	public CossRestoreObjectResult restoreObject(CossRestoreObjectRequest request) throws CossException, ServerCossException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectAcl getObjectAcl(String bucketName, String key) throws CossException, ServerCossException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setObjectAcl(String bucketName, String key, ACL acl) throws CossException, ServerCossException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean doesObjectExist(String bucketName, String key) throws CossException, ServerCossException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createSymlink(String bucketName, String symlink, String target) throws CossException, ServerCossException {
		// TODO Auto-generated method stub

	}

	@Override
	public ObjectSymlink getSymlink(String bucketName, String symlink) throws CossException, ServerCossException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getUrl(String bucketName, String key) throws CossException, ServerCossException {
		// TODO Auto-generated method stub
		return null;
	}

}