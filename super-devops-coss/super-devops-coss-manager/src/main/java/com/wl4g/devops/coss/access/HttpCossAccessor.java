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
import com.wl4g.devops.coss.ServerCossEndpoint;
import com.wl4g.devops.coss.access.model.GenericCossParameter;
import com.wl4g.devops.coss.access.model.ObjectMetadataModel;
import com.wl4g.devops.coss.access.model.ObjectValueModel;
import com.wl4g.devops.coss.access.model.ProviderBucketModel;
import com.wl4g.devops.coss.common.CossEndpoint;
import com.wl4g.devops.coss.common.CossEndpoint.CossProvider;
import com.wl4g.devops.coss.common.exception.CossException;
import com.wl4g.devops.coss.common.model.ACL;
import com.wl4g.devops.coss.common.model.ObjectMetadata;
import com.wl4g.devops.coss.common.model.ObjectValue;
import com.wl4g.devops.coss.common.model.CossPutObjectResult;
import com.wl4g.devops.coss.common.model.bucket.Bucket;
import com.wl4g.devops.coss.common.model.bucket.BucketList;
import com.wl4g.devops.coss.config.CossAccessProperties;
import com.wl4g.devops.coss.config.StandardFSCossProperties;
import com.wl4g.devops.coss.common.model.metadata.BucketStatusMetaData;
import com.wl4g.devops.coss.natives.MetadataIndexManager;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.wl4g.devops.components.tools.common.lang.Assert2.*;
import static com.wl4g.devops.coss.natives.MetadataIndexManager.BUCKET_METADATA;

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
	final protected GenericOperatorAdapter<CossProvider, ServerCossEndpoint<?>> endpointAdapter;

	@Autowired
	private MetadataIndexManager metadataIndexManager;

	@Autowired
	private StandardFSCossProperties standardFSConfig;

	@Autowired
	private CossAccessProperties accessConfig;

	public HttpCossAccessor(GenericOperatorAdapter<CossProvider, ServerCossEndpoint<?>> endpointAdapter) {
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

	@RequestMapping("listBucketsWithProvider")
	public RespBase<Object> listBucketsWithProvider(String organizationCode) {
		RespBase<Object> resp = RespBase.create();
		List<ProviderBucketModel> result = new ArrayList<>();
		CossProvider[] values = CossProvider.values();
		for (CossProvider value : values) {
			GenericCossParameter param = new GenericCossParameter();
			param.setCossProvider(value.toString());
			try {// TODO just for now
				BucketList<Bucket> bucketBucketList = getCossEndpoint(param).listBuckets("", null, null);
				for (Bucket bucket : bucketBucketList.getBucketList()) {
					ProviderBucketModel providerBucketModel = new ProviderBucketModel(value.toString(), bucket.getName());
					result.add(providerBucketModel);
				}
			} catch (Exception e) {

			}
		}
		resp.setData(result);
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
		// TODO ?? 应在具体cossEndpoint中实现
		String metaPath = standardFSConfig.getEndpointRootDir() + File.separator + bucketName + BUCKET_METADATA;
		BucketStatusMetaData metadataIndex = metadataIndexManager.readBucketMetaData(new File(metaPath));
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
		ObjectValueModel objValModel = new ObjectValueModel(getCossEndpoint(param).getObject(bucketName, key));
		String downloadUrl = accessConfig.getHttpDownloadBaseUri() + "?bucketName=" + bucketName + "&key=" + key;
		objValModel.setDownloadUrl(downloadUrl);
		resp.setData(objValModel);
		return resp;
	}

	@RequestMapping("putObject")
	public RespBase<Object> putObject(GenericCossParameter param, String bucketName, String acl, String key, MultipartFile file) {
		RespBase<Object> resp = RespBase.create();
		try {
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setAcl(ACL.parse(acl));
			if (Objects.isNull(key)) {
				key = "";
			}
			key = key + File.separator + file.getOriginalFilename();
			resp.setData(putObject(param, bucketName, key, file.getInputStream(), metadata));
			return resp;
		} catch (IOException e) {
			throw new CossException(e);
		}
	}

	@RequestMapping("putObjectMetaData")
	public RespBase<Object> putObjectMetaData(@RequestBody ObjectMetadataModel objectMetadataModel) {
		RespBase<Object> resp = RespBase.create();
		ObjectMetadata metadata = new ObjectMetadata();
		BeanUtils.copyProperties(objectMetadataModel, metadata, "acl");
		metadata.setAcl(ACL.parse(objectMetadataModel.getAcl()));
		getCossEndpoint(objectMetadataModel.getParam()).putObjectMetaData(objectMetadataModel.getBucketName(),
				objectMetadataModel.getKey(), metadata);
		return resp;
	}

	public CossPutObjectResult putObject(GenericCossParameter param, String bucketName, String key, InputStream input,
			ObjectMetadata metadata) {
		return getCossEndpoint(param).putObject(bucketName, key, input, metadata);
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

	/**
	 * Gets current running coss providers.
	 * 
	 * @return
	 */
	@RequestMapping("getCossProviders")
	public RespBase<Object> getRunningProviders() {
		RespBase<Object> resp = RespBase.create();
		resp.setData(getCossProviderWithEnable());
		return resp;
	}

	/**
	 * Gets acls defintions
	 * 
	 * @return
	 */
	@RequestMapping("getACLs")
	public RespBase<Object> getACLDefinitions() {
		RespBase<Object> resp = RespBase.create();
		resp.setData(ACL.cannedAclStrings());
		return resp;
	}

	/**
	 * Creation directory.
	 * 
	 * @param bucketName
	 * @param currentPath
	 * @param dirName
	 * @return
	 */
	@RequestMapping("createDir")
	public RespBase<Object> createDir(String bucketName, String currentPath, String dirName) {
		RespBase<Object> resp = RespBase.create();
		// TODO
		// File path = new File(config.getEndpointRootDir() + File.separator +
		// bucketName + currentPath + dirName);
		// path.mkdirs();
		// isTrue(path.exists(), "createBucketMeta dir fail");
		return resp;
	}

	/**
	 * Download object file.
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @param bucketName
	 * @param key
	 * @throws IOException
	 */
	@RequestMapping("download")
	public void download(HttpServletRequest request, HttpServletResponse response, GenericCossParameter param, String bucketName,
			String key) throws IOException {
		ObjectValue object = getCossEndpoint(param).getObject(bucketName, key);
		String fileName = getFileNameByKey(key);
		InputStream inputStream = object.getObjectContent();
		ServletOutputStream out = null;
		try {
			// ips = new FileInputStream(objectContent);
			response.setContentType("multipart/form-data");
			// 为文件重新设置名字，采用数据库内存储的文件名称
			// new String(fileName.getBytes("UTF-8"),"ISO8859-1")
			response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			out = response.getOutputStream();
			// 读取文件流
			int len = 0;
			byte[] buffer = new byte[1024 * 10];
			while ((len = inputStream.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (null != inputStream) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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

	private List<Map<String, Object>> getCossProviderWithEnable() {
		List<Map<String, Object>> list = new ArrayList<>();
		Set<CossProvider> runningKinds = endpointAdapter.getRunningKinds();
		for (CossProvider cossProvider : CossProvider.values()) {
			Map<String, Object> map = new HashMap<>();
			map.put("name", cossProvider);
			if (runningKinds.contains(cossProvider)) {
				map.put("enable", true);
			} else {
				map.put("enable", false);
			}
			list.add(map);
		}
		return list;
	}

	private static String getFileNameByKey(String key) {
		int i = key.lastIndexOf("/");
		return key.substring(i + 1, key.length());
	}

}