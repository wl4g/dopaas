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

import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;

import java.io.InputStream;
import java.nio.file.FileSystem;

import org.apache.sshd.common.file.FileSystemAware;

import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.coss.ServerCossEndpoint;
import com.wl4g.devops.coss.access.model.GenericCossParameter;
import com.wl4g.devops.coss.common.CossEndpoint;
import com.wl4g.devops.coss.common.CossEndpoint.CossProvider;
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
 * SFTP based coss accessor
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月4日
 * @since
 */
public class SftpCossAccessor implements CossAccessor, FileSystemAware {

	/**
	 * {@link CossEndpoint}
	 */
	final protected GenericOperatorAdapter<CossProvider, ServerCossEndpoint<?>> endpointAdapter;

	/**
	 * {@link FileSystem}
	 */
	protected FileSystem fileSystem;

	public SftpCossAccessor(GenericOperatorAdapter<CossProvider, ServerCossEndpoint<?>> endpointAdapter) {
		notNullOf(endpointAdapter, "endpointAdapter");
		this.endpointAdapter = endpointAdapter;
	}

	@Override
	public void setFileSystem(FileSystem fileSystem) {
		this.fileSystem = fileSystem;
	}

	@Override
	public Bucket createBucket(GenericCossParameter param, String bucketName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BucketList<? extends Bucket> listBuckets(GenericCossParameter param, String prefix, String marker, Integer maxKeys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteBucket(GenericCossParameter param, String bucketName) {
		// TODO Auto-generated method stub

	}

	@Override
	public BucketMetadata getBucketMetadata(GenericCossParameter param, String bucketName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccessControlList getBucketAcl(GenericCossParameter param, String bucketName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBucketAcl(GenericCossParameter param, String bucketName, String acl) {
		// TODO Auto-generated method stub

	}

	@Override
	public ObjectListing<? extends ObjectSummary> listObjects(GenericCossParameter param, String bucketName, String prefix) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectValue getObject(GenericCossParameter param, String bucketName, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CossPutObjectResult putObject(GenericCossParameter param, String bucketName, String key, InputStream input,
			ObjectMetadata metadata) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteObject(GenericCossParameter param, String bucketName, String key) {
		// TODO Auto-generated method stub

	}

	@Override
	public ObjectAcl getObjectAcl(GenericCossParameter param, String bucketName, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setObjectAcl(GenericCossParameter param, String bucketName, String key, String acl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createSymlink(GenericCossParameter param, String bucketName, String symlink, String target) {
		// TODO Auto-generated method stub

	}

	@Override
	public ObjectSymlink getSymlink(GenericCossParameter param, String bucketName, String symlink) {
		// TODO Auto-generated method stub
		return null;
	}

}