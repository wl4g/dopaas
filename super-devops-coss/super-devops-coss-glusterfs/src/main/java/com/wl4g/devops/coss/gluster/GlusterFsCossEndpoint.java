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
package com.wl4g.devops.coss.gluster;

import java.io.InputStream;

import com.wl4g.devops.coss.AbstractCossEndpoint;
import com.wl4g.devops.coss.gluster.config.GlusterCossProperties;
import com.wl4g.devops.coss.model.ACL;
import com.wl4g.devops.coss.model.AccessControlList;
import com.wl4g.devops.coss.model.ObjectAcl;
import com.wl4g.devops.coss.model.ObjectListing;
import com.wl4g.devops.coss.model.ObjectMetadata;
import com.wl4g.devops.coss.model.ObjectSymlink;
import com.wl4g.devops.coss.model.ObjectValue;
import com.wl4g.devops.coss.model.PutObjectResult;
import com.wl4g.devops.coss.model.bucket.Bucket;
import com.wl4g.devops.coss.model.bucket.BucketList;
import com.wl4g.devops.coss.model.bucket.BucketMetadata;

public class GlusterFsCossEndpoint extends AbstractCossEndpoint<GlusterCossProperties> {

	public GlusterFsCossEndpoint(GlusterCossProperties config) {
		super(config);
	}

	@Override
	public CossProvider kind() {
		return CossProvider.GlusterFs;
	}

	@Override
	public Bucket createBucket(String bucketName) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public BucketList listBuckets(String prefix, String marker, Integer maxKeys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteBucket(String bucketName) {
		// TODO Auto-generated method stub

	}

	@Override
	public BucketMetadata getBucketMetadata(String bucketName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccessControlList getBucketAcl(String bucketName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBucketAcl(String bucketName, ACL acl) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ObjectListing listObjects(String bucketName) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ObjectListing listObjects(String bucketName, String prefix) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectValue getObject(String bucketName, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteObject(String bucketName, String key) {
		// TODO Auto-generated method stub

	}

	@Override
	public ObjectAcl getObjectAcl(String bucketName, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setObjectAcl(String bucketName, String key, ACL acl) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean doesObjectExist(String bucketName, String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createSymlink(String bucketName, String symlink, String target) {
		// TODO Auto-generated method stub

	}

	@Override
	public ObjectSymlink getSymlink(String bucketName, String symlink) {
		// TODO Auto-generated method stub
		return null;
	}

}
