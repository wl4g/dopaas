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
package com.wl4g.devops.coss.natives;

import com.wl4g.devops.components.tools.common.io.FileIOUtils;
import com.wl4g.devops.components.tools.common.io.FileLockUtils;
import com.wl4g.devops.components.tools.common.lang.Assert2;
import com.wl4g.devops.components.tools.common.serialize.JacksonUtils;
import com.wl4g.devops.coss.common.model.metadata.BucketStatusMetaData;
import com.wl4g.devops.coss.common.model.metadata.ObjectsStatusMetaData;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Map;

import static com.wl4g.devops.coss.common.model.metadata.ObjectsStatusMetaData.ObjectStatusMetaData;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author vjay
 * @date 2020-03-19 10:00:00
 */
public class MetadataIndexManager {

	public static final String BUCKET_METADATA = "/.bucket.metadata";
	public static final String OBJECT_METADATA_DIR = "/.metadata/";
	public static final String OBJECT_METADATA_PRE = "block.";// e.g:
																// /.metadata/block.0
	private static final String charset = "UTF-8";

	// ============================================object============================================

	public void addObject(String bucketPath, File file, ObjectStatusMetaData objectStatusMetaData) throws Exception {
		File lastObjectMetaFile = getLastObjectMetaFile(file.getParentFile());
		FileLockUtils.doTryLock(file, fileLock -> {
			ObjectsStatusMetaData objectsStatusMetaData = readObjectsStatusMetaData(lastObjectMetaFile);
			if (isNull(objectsStatusMetaData)) {
				return null;
			}
			objectsStatusMetaData.setTotalCount(objectsStatusMetaData.getTotalCount() + 1);
			objectsStatusMetaData.setTotalSize(objectsStatusMetaData.getTotalSize() + Files.size(file.toPath()));
			Map<String, ObjectStatusMetaData> objects = objectsStatusMetaData.getObjects();
			objects.put(file.getName(), objectStatusMetaData);
			String s1 = JacksonUtils.toJSONString(objectsStatusMetaData);
			FileIOUtils.writeFile(lastObjectMetaFile, s1, false);
			return null;
		});

		modifyBucketMetaData(bucketPath, 1, Files.size(file.toPath()), 0);
	}

	public void delObject(String bucketPath, File file) throws Exception {
		String key = file.getName();
		File metaFile = getObjectMetaFileByKey(file.getParentFile(), key);
		if (isNull(metaFile)) {
			return;
		}
		FileLockUtils.doTryLock(file, fileLock -> {
			ObjectsStatusMetaData objectsStatusMetaData = readObjectsStatusMetaData(metaFile);
			if (isNull(objectsStatusMetaData)) {
				return null;
			}
			objectsStatusMetaData.setTotalCount(objectsStatusMetaData.getTotalCount() - 1);
			objectsStatusMetaData.setTotalSize(objectsStatusMetaData.getTotalSize() - Files.size(file.toPath()));
			Map<String, ObjectStatusMetaData> objects = objectsStatusMetaData.getObjects();
			objects.remove(key);
			String s1 = JacksonUtils.toJSONString(objectsStatusMetaData);
			FileIOUtils.writeFile(metaFile, s1);
			return null;
		});
		modifyBucketMetaData(bucketPath, -1, -Files.size(file.toPath()), 0);

	}

	public void modifyObject(File file, ObjectStatusMetaData objectStatusMetaData) throws Exception {
		String key = file.getName();
		File metaFile = getObjectMetaFileByKey(file.getParentFile(), key);
		if (isNull(metaFile)) {
			return;
		}
		FileLockUtils.doTryLock(file, fileLock -> {
			ObjectsStatusMetaData objectsStatusMetaData = readObjectsStatusMetaData(metaFile);
			if (isNull(objectsStatusMetaData)) {
				return null;
			}
			Map<String, ObjectStatusMetaData> objects = objectsStatusMetaData.getObjects();
			objects.put(key, objectStatusMetaData);
			String s1 = JacksonUtils.toJSONString(objectsStatusMetaData);
			FileIOUtils.writeFile(metaFile, s1, false);
			return null;
		});
	}

	public ObjectStatusMetaData getObject(String bucketPath, File file) throws IOException {

		modifyBucketMetaData(bucketPath, 0, 0, 1);
		String key = file.getName();
		File metaFile = getObjectMetaFileByKey(file.getParentFile(), key);
		if (isNull(metaFile)) {
			return null;
		}
		ObjectsStatusMetaData objectsStatusMetaData = readObjectsStatusMetaData(metaFile);
		if (isNull(objectsStatusMetaData) || isNull(objectsStatusMetaData.getObjects())) {
			return null;
		}
		Map<String, ObjectStatusMetaData> objects = objectsStatusMetaData.getObjects();
		ObjectStatusMetaData objectStatusMetaData = objects.get(key);
		if (nonNull(objectStatusMetaData)) {
			return objectStatusMetaData;
		}
		return null;
	}

	private File getObjectMetaFileByKey(File parentFile, String key) throws IOException {
		if (!parentFile.exists()) {
			return null;
		}
		File metaDataDir = new File(parentFile.getAbsolutePath() + OBJECT_METADATA_DIR);
		File[] files = metaDataDir.listFiles();
		if (!metaDataDir.exists() || files == null || files.length <= 0) {
			return null;
		}
		for (File file : files) {
			ObjectsStatusMetaData objectsStatusMetaData = readObjectsStatusMetaData(file);
			if (isNull(objectsStatusMetaData) || isNull(objectsStatusMetaData.getObjects())) {
				continue;
			}
			Map<String, ObjectStatusMetaData> objects = objectsStatusMetaData.getObjects();
			ObjectStatusMetaData objectStatusMetaData = objects.get(key);
			if (nonNull(objectStatusMetaData)) {
				return file;
			}
		}
		return null;
	}

	private File getLastObjectMetaFile(File parentFile) {
		File metaDataDir = new File(parentFile.getAbsolutePath() + OBJECT_METADATA_DIR);
		if (!metaDataDir.exists()) {
			metaDataDir.mkdirs();
			Assert2.isTrue(metaDataDir.exists(), "make dir fail");
			return createObjectMetaFile(parentFile);
		}
		File[] files = metaDataDir.listFiles();
		if (files.length <= 0) {
			return createObjectMetaFile(parentFile);
		}
		File lastFile = files[0];
		int largestSuffix = 0;
		for (File file : files) {
			int suffix = getSuffix(file.getName());
			if (suffix > largestSuffix) {
				largestSuffix = suffix;
				lastFile = file;
			}
		}
		return lastFile;
	}

	private File createObjectMetaFile(File parentFile) {
		ObjectsStatusMetaData objectsStatusMetaData = new ObjectsStatusMetaData();
		File file = new File(parentFile.getAbsolutePath() + OBJECT_METADATA_DIR + OBJECT_METADATA_PRE + 0);
		FileIOUtils.writeFile(file, JacksonUtils.toJSONString(objectsStatusMetaData), false);
		return file;
	}

	private static int getSuffix(String fileName) {
		int i = fileName.lastIndexOf(".");
		String substring = fileName.substring(i + 1, fileName.length());
		try {
			return Integer.valueOf(substring);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// ============================================bucket============================================

	/**
	 * Create Bucket
	 * 
	 * @param bucketPath
	 */
	public void createBucketMeta(String bucketPath) {
		File file = new File(bucketPath + BUCKET_METADATA);
		createBucketMeta(file);
	}

	/**
	 *
	 * @param bucketPath
	 * @param addFileNum
	 * @param addFileSize
	 */
	public void modifyBucketMetaData(String bucketPath, int addFileNum, long addFileSize, long addRequestTimes) {
		File file = new File(bucketPath + BUCKET_METADATA);
		checkBucketMetaData(file);
		try {
			FileLockUtils.doTryLock(file, fileLock -> {
				BucketStatusMetaData metadataIndex = readBucketMetaData(file);
				metadataIndex.setNumberOfDocuments(metadataIndex.getNumberOfDocuments() + addFileNum);
				metadataIndex.setStorageUsage(metadataIndex.getStorageUsage() + addFileSize);
				metadataIndex.setNumberOfRequests(metadataIndex.getNumberOfRequests() + addRequestTimes);
				metadataIndex.setModifyDate(System.currentTimeMillis());
				writeBucketMetaData(file, metadataIndex);
				return null;
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkBucketMetaData(File file) {
		if (!file.exists()) {
			createBucketMeta(file);
		}
		Assert2.isTrue(file.exists(), "index file not exists and createBucketMeta fail");
	}

	private void createBucketMeta(File file) {
		if (file.exists()) {// needn't create metadata
			return;
		}
		BucketStatusMetaData metadataIndex = new BucketStatusMetaData();
		metadataIndex.setNumberOfDocuments(0);
		metadataIndex.setStorageUsage(0);
		long now = System.currentTimeMillis();
		metadataIndex.setCreateDate(now);
		metadataIndex.setModifyDate(now);
		writeBucketMetaData(file, metadataIndex);
	}

	private void writeBucketMetaData(File file, BucketStatusMetaData metadataIndex) {
		String s = JacksonUtils.toJSONString(metadataIndex);
		FileIOUtils.writeFile(file, s, Charset.forName(charset), false);
	}

	public BucketStatusMetaData readBucketMetaData(File file) throws IOException {
		if (!file.exists()) {
			return null;
		}
		String s = FileIOUtils.readFileToString(file, charset);
		return JacksonUtils.parseJSON(s, BucketStatusMetaData.class);
	}

	private ObjectsStatusMetaData readObjectsStatusMetaData(File file) throws IOException {
		if (!file.exists()) {
			return null;
		}
		String s = FileIOUtils.readFileToString(file, charset);
		return JacksonUtils.parseJSON(s, ObjectsStatusMetaData.class);
	}

}