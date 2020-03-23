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

import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.coss.CossEndpoint;
import com.wl4g.devops.coss.CossProvider;
import com.wl4g.devops.coss.access.model.GenericCossParameter;
import com.wl4g.devops.coss.config.NativeFSCossProperties;
import com.wl4g.devops.coss.exception.CossException;
import com.wl4g.devops.coss.model.ACL;
import com.wl4g.devops.coss.model.ObjectMetadata;
import com.wl4g.devops.coss.model.PutObjectResult;
import com.wl4g.devops.coss.natives.MetadataIndexManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.wl4g.devops.coss.natives.MetadataIndexManager.indexFileName;
import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;

/**
 * Web/HTTP based coss accessor
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月4日
 * @since
 */
@ResponseBody
public class HttpCossAccessor extends BaseController {

	final public static String URL_BASE = "/webservice/";

	/**
	 * {@link CossEndpoint}
	 */
	final protected GenericOperatorAdapter<CossProvider, CossEndpoint> endpointAdapter;

	@Autowired
	private MetadataIndexManager metadataIndexManager;

	@Autowired
	private NativeFSCossProperties config;

	public HttpCossAccessor(GenericOperatorAdapter<CossProvider, CossEndpoint> endpointAdapter) {
		notNullOf(endpointAdapter, "endpointAdapter");
		this.endpointAdapter = endpointAdapter;
	}

	@RequestMapping("createBucket")
	public RespBase<Object> createBucket(GenericCossParameter param, String bucketName) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(getCossEndpoint(param).createBucket(bucketName));
		return resp;
	}

	/**
	 * e.g:
	 * http://wl4g.debug:14061/coss-server/webservice/listBuckets?cossProvider=hdfs&prefix=sm&marker=sm-clound&maxKeys=100&_stacktrace=true
	 * {"bucketList":[{"name":"sm-clound","owner":{"displayName":"root","id":"root"},"creationDate":0}]}
	 */
	@RequestMapping("listBuckets")
	public RespBase<Object> listBuckets(GenericCossParameter param, String prefix, String marker, Integer maxKeys) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(getCossEndpoint(param).listBuckets(prefix, marker, maxKeys));
		return resp;
	}

	@RequestMapping("deleteBucket")
	public RespBase<Object> deleteBucket(GenericCossParameter param, String bucketName) {
		RespBase<Object> resp = RespBase.create();
		getCossEndpoint(param).deleteBucket(bucketName);
		return resp;
	}

	@RequestMapping("getBucketMetadata")
	public RespBase<Object> getBucketMetadata(GenericCossParameter param, String bucketName) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(getCossEndpoint(param).getBucketMetadata(bucketName));
		return resp;
	}

	@RequestMapping("getBucketAcl")
	public RespBase<Object> getBucketAcl(GenericCossParameter param, String bucketName) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(getCossEndpoint(param).getBucketAcl(bucketName));
		return resp;
	}

	@RequestMapping("setBucketAcl")
	public RespBase<Object> setBucketAcl(GenericCossParameter param, String bucketName, String acl) {
		RespBase<Object> resp = RespBase.create();
		getCossEndpoint(param).setBucketAcl(bucketName, ACL.parse(acl));
		return resp;
	}

	@RequestMapping("getBucketIndex")
	public RespBase<Object> getBucketIndex(GenericCossParameter param, String bucketName) throws IOException {
		RespBase<Object> resp = RespBase.create();
		String indexPath = config.getEndpointRootDir() + File.separator + bucketName + indexFileName;
		MetadataIndexManager.MetadataIndex metadataIndex = metadataIndexManager.read(new File(indexPath));
		resp.setData(metadataIndex);
		return resp;
	}

	/**
	 * e.g:
	 * http://wl4g.debug:14061/coss-server/webservice/listObjects?cossProvider=hdfs&prefix=sm&bucketName=sm-clound
	 * {"objectSummaries":[{"bucketName":"sm-clound","key":"hdfs-coss-sample.txt","size":9800,"mtime":1584348593100,"atime":1584348592700,"storageType":"hdfs","owner":{"displayName":"root","id":"root"},"etag":"512@MD5-of-0MD5-of-512CRC32C"}],"commonPrefixes":[],"bucketName":null,"nextMarker":null,"prefix":"sm","marker":null,"maxKeys":0,"delimiter":"/","encodingType":"UTF-8","truncated":false}
	 */
	@RequestMapping("listObjects")
	public RespBase<Object> listObjects(GenericCossParameter param, String bucketName, String prefix) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(getCossEndpoint(param).listObjects(bucketName, prefix));
		return resp;
	}

	@RequestMapping("getObject")
	public RespBase<Object> getObject(GenericCossParameter param, String bucketName, String key) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(getCossEndpoint(param).getObject(bucketName, key));
		return resp;
	}

	@RequestMapping("putObject")
	public PutObjectResult putObject(GenericCossParameter param, String bucketName, String key,
			@RequestParam(required = false) ObjectMetadata metadata, MultipartFile file) {
		try {
			if (StringUtils.isBlank(key)) {
				key = file.getOriginalFilename();
			}
			return putObject(param, bucketName, key, file.getInputStream(), metadata);
		} catch (IOException e) {
			throw new CossException(e);
		}
	}

	public PutObjectResult putObject(GenericCossParameter param, String bucketName, String key, InputStream input,
			ObjectMetadata metadata) {
		return getCossEndpoint(param).putObject(bucketName, key, input);
	}

	@RequestMapping("deleteObject")
	public RespBase<Object> deleteObject(GenericCossParameter param, String bucketName, String key) {
		RespBase<Object> resp = RespBase.create();
		getCossEndpoint(param).deleteObject(bucketName, key);
		return resp;
	}

	@RequestMapping("getObjectAcl")
	public RespBase<Object> getObjectAcl(GenericCossParameter param, String bucketName, String key) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(getCossEndpoint(param).getObjectAcl(bucketName, key));
		return resp;
	}

	@RequestMapping("setObjectAcl")
	public RespBase<Object> setObjectAcl(GenericCossParameter param, String bucketName, String key, String acl) {
		RespBase<Object> resp = RespBase.create();
		getCossEndpoint(param).setObjectAcl(bucketName, key, ACL.parse(acl));
		return resp;
	}

	@RequestMapping("createSymlink")
	public RespBase<Object> createSymlink(GenericCossParameter param, String bucketName, String symlink, String target) {
		RespBase<Object> resp = RespBase.create();
		getCossEndpoint(param).createSymlink(bucketName, symlink, target);
		return resp;
	}

	@RequestMapping("getSymlink")
	public RespBase<Object> getSymlink(GenericCossParameter param, String bucketName, String symlink) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(getCossEndpoint(param).getSymlink(bucketName, symlink));
		return resp;
	}

	@RequestMapping("getCossProviders")
	public RespBase<Object> getCossProvider() {
		RespBase<Object> resp = RespBase.create();
		resp.setData(getCossProviderWithEnable());
		return resp;
	}

	@RequestMapping("getACLs")
	public RespBase<Object> getACLs() {
		RespBase<Object> resp = RespBase.create();
		resp.setData(ACL.cannedAclStrings());
		return resp;
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

	private List<Map<String,Object>> getCossProviderWithEnable(){
		List<Map<String,Object>> list = new ArrayList<>();
		Set<CossProvider> runningKinds = endpointAdapter.getRunningKinds();
		for(CossProvider cossProvider : CossProvider.values()){
			Map<String, Object> map = new HashMap<>();
			map.put("name",cossProvider);
			if(runningKinds.contains(cossProvider)){
				map.put("enable", true);
			}else{
				map.put("enable", false);
			}
			list.add(map);
		}
		return list;
	}

}
