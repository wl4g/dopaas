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

import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.coss.CossEndpoint;
import com.wl4g.devops.coss.CossEndpoint.CossProvider;
import com.wl4g.devops.coss.access.model.GenericCossParameter;
import com.wl4g.devops.coss.exception.CossException;
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
import com.wl4g.devops.shell.annotation.ShellComponent;

/**
 * Web/HTTP based coss accessor
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月4日
 * @since
 */
@ShellComponent
@RequestMapping("/webservice/")
public class HttpCossAccessor extends BaseController implements CossAccessor {

	/**
	 * {@link CossEndpoint}
	 */
	final protected GenericOperatorAdapter<CossProvider, CossEndpoint> endpointAdapter;

	public HttpCossAccessor(GenericOperatorAdapter<CossProvider, CossEndpoint> endpointAdapter) {
		notNullOf(endpointAdapter, "endpointAdapter");
		this.endpointAdapter = endpointAdapter;
	}

	@RequestMapping("createBucket")
	@Override
	public Bucket createBucket(GenericCossParameter param, String bucketName) {
		return getCossEndpoint(param).createBucket(bucketName);
	}

	@RequestMapping("listBuckets")
	@Override
	public BucketList<? extends Bucket> listBuckets(GenericCossParameter param, String prefix, String marker, Integer maxKeys) {
		return getCossEndpoint(param).listBuckets(prefix, marker, maxKeys);
	}

	@RequestMapping("deleteBucket")
	@Override
	public void deleteBucket(GenericCossParameter param, String bucketName) {
		getCossEndpoint(param).deleteBucket(bucketName);
	}

	@RequestMapping("getBucketMetadata")
	@Override
	public BucketMetadata getBucketMetadata(GenericCossParameter param, String bucketName) {
		return getCossEndpoint(param).getBucketMetadata(bucketName);
	}

	@RequestMapping("getBucketAcl")
	@Override
	public AccessControlList getBucketAcl(GenericCossParameter param, String bucketName) {
		return getCossEndpoint(param).getBucketAcl(bucketName);
	}

	@RequestMapping("setBucketAcl")
	@Override
	public void setBucketAcl(GenericCossParameter param, String bucketName, String acl) {
		getCossEndpoint(param).setBucketAcl(bucketName, ACL.parse(acl));
	}

	@RequestMapping("listObjects")
	@Override
	public ObjectListing<? extends ObjectSummary> listObjects(GenericCossParameter param, String bucketName, String prefix) {
		return getCossEndpoint(param).listObjects(bucketName, prefix);
	}

	@RequestMapping("getObject")
	@Override
	public ObjectValue getObject(GenericCossParameter param, String bucketName, String key) {
		return getCossEndpoint(param).getObject(bucketName, key);
	}

	@RequestMapping("putObject")
	public PutObjectResult putObject(GenericCossParameter param, String bucketName, String key,
			@RequestParam(required = false) ObjectMetadata metadata, MultipartFile file) {
		try {
			return putObject(param, bucketName, key, file.getInputStream(), metadata);
		} catch (IOException e) {
			throw new CossException(e);
		}
	}

	@Override
	public PutObjectResult putObject(GenericCossParameter param, String bucketName, String key, InputStream input,
			ObjectMetadata metadata) {
		return getCossEndpoint(param).putObject(bucketName, key, input);
	}

	@RequestMapping("deleteObject")
	@Override
	public void deleteObject(GenericCossParameter param, String bucketName, String key) {
		getCossEndpoint(param).deleteObject(bucketName, key);
	}

	@RequestMapping("getObjectAcl")
	@Override
	public ObjectAcl getObjectAcl(GenericCossParameter param, String bucketName, String key) {
		return getCossEndpoint(param).getObjectAcl(bucketName, key);
	}

	@RequestMapping("setObjectAcl")
	@Override
	public void setObjectAcl(GenericCossParameter param, String bucketName, String key, String acl) {
		getCossEndpoint(param).setObjectAcl(bucketName, key, ACL.parse(acl));
	}

	@RequestMapping("createSymlink")
	@Override

	public void createSymlink(GenericCossParameter param, String bucketName, String symlink, String target) {
		getCossEndpoint(param).createSymlink(bucketName, symlink, target);
	}

	@RequestMapping("getSymlink")
	@Override
	public ObjectSymlink getSymlink(GenericCossParameter param, String bucketName, String symlink) {
		return getCossEndpoint(param).getSymlink(bucketName, symlink);
	}

	/**
	 * Gets {@link CossEndpoint}
	 * 
	 * @param param
	 * @return
	 */
	private CossEndpoint getCossEndpoint(GenericCossParameter param) {
		return endpointAdapter.forOperator(param.getCossProvider()).get();
	}

}
