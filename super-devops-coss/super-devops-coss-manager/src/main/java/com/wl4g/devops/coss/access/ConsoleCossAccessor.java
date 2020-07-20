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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.components.shell.annotation.ShellComponent;
import com.wl4g.devops.components.shell.annotation.ShellMethod;
import com.wl4g.devops.components.shell.annotation.ShellOption;
import com.wl4g.devops.coss.ServerCossEndpoint;
import com.wl4g.devops.coss.access.model.GenericCossParameter;
import com.wl4g.devops.coss.common.CossEndpoint;
import com.wl4g.devops.coss.common.CossEndpoint.CossProvider;
import com.wl4g.devops.coss.common.exception.CossException;
import com.wl4g.devops.coss.common.model.ACL;
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
 * Console based coss accessor
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月4日
 * @since
 */
@ShellComponent
public class ConsoleCossAccessor implements CossAccessor {

	final public static String CONSOLE_GROUP = "Composite OSS Console";

	/**
	 * {@link CossEndpoint}
	 */
	final protected GenericOperatorAdapter<CossProvider, ServerCossEndpoint<?>> endpointAdapter;

	public ConsoleCossAccessor(GenericOperatorAdapter<CossProvider, ServerCossEndpoint<?>> endpointAdapter) {
		notNullOf(endpointAdapter, "endpointAdapter");
		this.endpointAdapter = endpointAdapter;
	}

	@ShellMethod(keys = "createBucket", group = CONSOLE_GROUP, help = "creation bucket")
	@Override
	public Bucket createBucket(GenericCossParameter param,
			@ShellOption(opt = "b", lopt = "bucketName", help = "Bucket name") String bucketName) {
		return getCossEndpoint(param).createBucket(bucketName);
	}

	@ShellMethod(keys = "listBuckets", group = CONSOLE_GROUP, help = "List buckets")
	@Override
	public BucketList<? extends Bucket> listBuckets(GenericCossParameter param,
			@ShellOption(opt = "p", lopt = "prefix", help = "Bucket name prefix") String prefix,
			@ShellOption(opt = "m", lopt = "marker", help = "Bucket name sort marker string") String marker,
			@ShellOption(opt = "k", lopt = "maxKeys", help = "Bucket name max keys") Integer maxKeys) {
		return getCossEndpoint(param).listBuckets(prefix, marker, maxKeys);
	}

	@ShellMethod(keys = "deleteBucket", group = CONSOLE_GROUP, help = "Delete bucket")
	@Override
	public void deleteBucket(GenericCossParameter param,
			@ShellOption(opt = "b", lopt = "bucketName", help = "Bucket name") String bucketName) {
		getCossEndpoint(param).deleteBucket(bucketName);
	}

	@ShellMethod(keys = "getBucketMetadata", group = CONSOLE_GROUP, help = "Gets bucket metadata")
	@Override
	public BucketMetadata getBucketMetadata(GenericCossParameter param,
			@ShellOption(opt = "b", lopt = "bucketName", help = "Bucket name") String bucketName) {
		return getCossEndpoint(param).getBucketMetadata(bucketName);
	}

	@ShellMethod(keys = "getBucketAcl", group = CONSOLE_GROUP, help = "Gets bucket acl")
	@Override
	public AccessControlList getBucketAcl(GenericCossParameter param,
			@ShellOption(opt = "b", lopt = "bucketName", help = "Bucket name") String bucketName) {
		return getCossEndpoint(param).getBucketAcl(bucketName);
	}

	@ShellMethod(keys = "setBucketAcl", group = CONSOLE_GROUP, help = "Sets bucket acl")
	@Override
	public void setBucketAcl(GenericCossParameter param,
			@ShellOption(opt = "b", lopt = "bucketName", help = "Bucket name") String bucketName,
			@ShellOption(opt = "a", lopt = "acl", help = "Access control list") String acl) {
		getCossEndpoint(param).setBucketAcl(bucketName, ACL.parse(acl));
	}

	@ShellMethod(keys = "listObjects", group = CONSOLE_GROUP, help = "List objects")
	@Override
	public ObjectListing<? extends ObjectSummary> listObjects(GenericCossParameter param,
			@ShellOption(opt = "b", lopt = "bucketName", help = "Bucket name") String bucketName,
			@ShellOption(opt = "p", lopt = "prefix", help = "Bucket name prefix") String prefix) {
		return getCossEndpoint(param).listObjects(bucketName, prefix);
	}

	@ShellMethod(keys = "getObject", group = CONSOLE_GROUP, help = "Get object")
	@Override
	public ObjectValue getObject(GenericCossParameter param,
			@ShellOption(opt = "b", lopt = "bucketName", help = "Bucket name") String bucketName,
			@ShellOption(opt = "k", lopt = "key", help = "Bucket key") String key) {
		return getCossEndpoint(param).getObject(bucketName, key);
	}

	@ShellMethod(keys = "putObject", group = CONSOLE_GROUP, help = "Put object")
	public CossPutObjectResult putObject(GenericCossParameter param,
			@ShellOption(opt = "b", lopt = "bucketName", help = "Bucket name") String bucketName,
			@ShellOption(opt = "k", lopt = "key", help = "Bucket key") String key,
			@ShellOption(opt = "f", lopt = "file", help = "Local input file path") String localInputFile,
			@ShellOption(opt = "m", lopt = "userMetadata", required = false, help = "User metadata") Map<String, String> userMetadata,
			@ShellOption(opt = "a", lopt = "acl", required = false, help = "Access control list") String acl) {
		try {
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setUserMetadata(userMetadata);
			metadata.setAcl(ACL.parse(acl));
			return putObject(param, bucketName, key, new FileInputStream(new File(localInputFile)), metadata);
		} catch (FileNotFoundException e) {
			throw new CossException(e);
		}
	}

	@Override
	public CossPutObjectResult putObject(GenericCossParameter param, String bucketName, String key, InputStream input,
			ObjectMetadata metadata) {
		return getCossEndpoint(param).putObject(bucketName, key, input, metadata);
	}

	@ShellMethod(keys = "deleteObject", group = CONSOLE_GROUP, help = "Delete object")
	@Override
	public void deleteObject(GenericCossParameter param,
			@ShellOption(opt = "b", lopt = "bucketName", help = "Bucket name") String bucketName,
			@ShellOption(opt = "k", lopt = "key", help = "Bucket key") String key) {
		getCossEndpoint(param).deleteObject(bucketName, key);
	}

	@ShellMethod(keys = "getObjectAcl", group = CONSOLE_GROUP, help = "Gets object acl")
	@Override
	public ObjectAcl getObjectAcl(GenericCossParameter param,
			@ShellOption(opt = "b", lopt = "bucketName", help = "Bucket name") String bucketName,
			@ShellOption(opt = "k", lopt = "key", help = "Bucket key") String key) {
		return getCossEndpoint(param).getObjectAcl(bucketName, key);
	}

	@ShellMethod(keys = "setObjectAcl", group = CONSOLE_GROUP, help = "Sets object acl")
	@Override
	public void setObjectAcl(GenericCossParameter param,
			@ShellOption(opt = "b", lopt = "bucketName", help = "Bucket name") String bucketName,
			@ShellOption(opt = "k", lopt = "key", help = "Bucket key") String key,
			@ShellOption(opt = "a", lopt = "acl", help = "Access control list") String acl) {
		getCossEndpoint(param).setObjectAcl(bucketName, key, ACL.parse(acl));
	}

	@ShellMethod(keys = "createSymlink", group = CONSOLE_GROUP, help = "Creation symlink")
	@Override
	public void createSymlink(GenericCossParameter param,
			@ShellOption(opt = "b", lopt = "bucketName", help = "Bucket name") String bucketName,
			@ShellOption(opt = "l", lopt = "symlink", help = "Symlink") String symlink,
			@ShellOption(opt = "t", lopt = "target", help = "Symlink target") String target) {
		getCossEndpoint(param).createSymlink(bucketName, symlink, target);
	}

	@ShellMethod(keys = "getSymlink", group = CONSOLE_GROUP, help = "Creation symlink")
	@Override
	public ObjectSymlink getSymlink(GenericCossParameter param,
			@ShellOption(opt = "b", lopt = "bucketName", help = "Bucket name") String bucketName,
			@ShellOption(opt = "l", lopt = "symlink", help = "Symlink") String symlink) {
		return getCossEndpoint(param).getSymlink(bucketName, symlink);
	}

	/**
	 * Gets {@link CossEndpoint}
	 * 
	 * @param param
	 * @return
	 */
	private CossEndpoint getCossEndpoint(GenericCossParameter param) {
		return endpointAdapter.forOperator(param.getCossProvider());
	}

}