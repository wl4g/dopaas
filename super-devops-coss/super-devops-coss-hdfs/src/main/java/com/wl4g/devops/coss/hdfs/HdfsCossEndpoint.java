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
package com.wl4g.devops.coss.hdfs;

import com.wl4g.devops.coss.AbstractCossEndpoint;
import com.wl4g.devops.coss.CossProvider;
import com.wl4g.devops.coss.exception.CossException;
import com.wl4g.devops.coss.exception.ServerCossException;
import com.wl4g.devops.coss.hdfs.config.HdfsCossProperties;
import com.wl4g.devops.coss.hdfs.model.HdfsObjectListing;
import com.wl4g.devops.coss.hdfs.model.HdfsObjectSummary;
import com.wl4g.devops.coss.hdfs.model.bucket.HdfsBucketList;
import com.wl4g.devops.coss.model.*;
import com.wl4g.devops.coss.model.bucket.Bucket;
import com.wl4g.devops.coss.model.bucket.BucketMetadata;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import static com.wl4g.devops.coss.utils.PosixFileSystemUtils.*;
import static com.wl4g.devops.tool.common.io.FileSizeUtils.getHumanReadable;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;

/**
 * File object storage based on HDFS.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月17日
 * @since
 */
public class HdfsCossEndpoint extends AbstractCossEndpoint<HdfsCossProperties> {

	/**
	 * Cache refresh timestamp.
	 */
	final private AtomicLong cacheRefreshLastTime = new AtomicLong(0);

	/**
	 * {@link FileSystem}
	 */
	protected FileSystem hdfsFS;

	public HdfsCossEndpoint(HdfsCossProperties config) {
		super(config);
	}

	@Override
	public CossProvider kind() {
		return CossProvider.Hdfs;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		checkAndInitializingHdfsFileSystem();
	}

	@Override
	public boolean preHandle(Method method, Object[] args) {
		checkAndInitializingHdfsFileSystem();
		return true;
	}

	@Override
	public Bucket createBucket(String bucketName) {
		try {
			Path path = new Path(config.getBucketRootPath(), bucketName);
			hdfsFS.mkdirs(path, DEFAULT_BUCKET_PERMISSION);
		} catch (IOException e) {
			throw new ServerCossException(e);
		}
		Bucket bucket = new Bucket(bucketName);
		bucket.setCreationDate(new Date());
		bucket.setOwner(getCurrentOwner());
		return bucket;
	}

	@Override
	public HdfsBucketList listBuckets(String prefix, String marker, Integer maxKeys) {
		HdfsBucketList bucketList = new HdfsBucketList();
		try {
			FileStatus[] fileStats = hdfsFS.listStatus(config.getBucketRootPath());
			if (!isNull(fileStats)) {
				for (FileStatus fileStat : fileStats) {
					if (fileStat.isDirectory()) {
						Bucket bucket = new Bucket(fileStat.getPath().getName());
						bucket.setCreationDate(new Date(fileStat.getAccessTime()));
						bucket.setOwner(new Owner(fileStat.getOwner(), fileStat.getOwner()));
						bucketList.getBucketList().add(bucket);
					}
				}
			}
		} catch (IOException e) {
			throw new ServerCossException(e);
		}
		return bucketList;
	}

	@Override
	public void deleteBucket(String bucketName) {
		try {
			Path path = new Path(config.getBucketRootPath(), bucketName);
			// TODO logisic delete?
			hdfsFS.delete(path, true);
		} catch (IOException e) {
			throw new ServerCossException(e);
		}
	}

	@Override
	public BucketMetadata getBucketMetadata(String bucketName) {
		BucketMetadata metadata = new BucketMetadata(bucketName);
		try {
			Path path = new Path(config.getBucketRootPath(), bucketName);
			FileStatus fileStat = hdfsFS.getFileStatus(path);
			if (!isNull(fileStat)) {
				// TODO default is private hdfs server
				metadata.setBucketRegion("private-hdfs");
				metadata.getAttributes().put("blockSize", getHumanReadable(fileStat.getBlockSize()));
				metadata.getAttributes().put("owner", fileStat.getOwner());
				metadata.getAttributes().put("group", fileStat.getGroup());
				metadata.getAttributes().put("len", getHumanReadable(fileStat.getLen()));
			}
		} catch (IOException e) {
			throw new ServerCossException(e);
		}
		return metadata;
	}

	@Override
	public AccessControlList getBucketAcl(String bucketName) {
		AccessControlList acl = new AccessControlList();
		try {
			Path path = new Path(config.getBucketRootPath(), bucketName);
			FileStatus fileStat = hdfsFS.getFileStatus(path);
			if (!isNull(fileStat)) {
				acl.setOwner(new Owner(fileStat.getOwner(), fileStat.getOwner()));
				acl.setAcl(toAcl(fileStat.getPermission()));
			}
		} catch (IOException e) {
			throw new ServerCossException(e);
		}
		return acl;
	}

	@Override
	public void setBucketAcl(String bucketName, ACL acl) {
		try {
			Path path = new Path(config.getBucketRootPath(), bucketName);
			hdfsFS.setPermission(path, toFsPermission(acl));
		} catch (IOException e) {
			throw new ServerCossException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public HdfsObjectListing listObjects(String bucketName, String prefix) {
		HdfsObjectListing objectList = new HdfsObjectListing();
		objectList.setBucketName(bucketName);
		objectList.setPrefix(prefix);
		// TODO next, prefix... No implements
		try {
			RemoteIterator<LocatedFileStatus> it = hdfsFS.listFiles(config.getBucketRootPath(), true);
			if (!isNull(it)) {
				while (it.hasNext()) {
					LocatedFileStatus fileStat = it.next();
					if (fileStat.isFile()) {
						// Object summary
						HdfsObjectSummary summary = new HdfsObjectSummary();
						summary.setBucketName(bucketName);
						summary.setAtime(fileStat.getAccessTime());
						summary.setMtime(fileStat.getModificationTime());
						summary.setOwner(new Owner(fileStat.getOwner(), fileStat.getOwner()));
						summary.setKey(config.getObjectKey(bucketName, fileStat.getPath()));
						summary.setStorageType("hdfs");
						summary.setSize(fileStat.getLen());
						// Checksum
						Path path = new Path(config.getBucketRootPath(), bucketName + "/" + summary.getKey());
						FileChecksum checkSum = hdfsFS.getFileChecksum(path);
						summary.setETag(checkSum.getChecksumOpt().getBytesPerChecksum() + "@" + checkSum.getAlgorithmName());
						objectList.getObjectSummaries().add(summary);
					}
				}
			}
		} catch (IOException e) {
			throw new ServerCossException(e);
		}
		return objectList;
	}

	@Override
	public ObjectValue getObject(String bucketName, String key) {
		ObjectValue value = new ObjectValue(key, bucketName);
		try {
			Path path = new Path(config.getBucketRootPath(), bucketName + "/" + key);
			FileStatus fileStat = hdfsFS.getFileStatus(path);
			if (!isNull(fileStat)) {
				value.getMetadata().setAtime(fileStat.getAccessTime());
				value.getMetadata().setMtime(fileStat.getModificationTime());
				value.getMetadata().setContentLength(fileStat.getLen());
				value.getMetadata().getUserMetadata().put("blockSize", getHumanReadable(fileStat.getBlockSize()));
				value.getMetadata().getUserMetadata().put("owner", fileStat.getOwner());
				value.getMetadata().getUserMetadata().put("group", fileStat.getGroup());
				// Checksum
				FileChecksum checkSum = hdfsFS.getFileChecksum(path);
				if (!isNull(checkSum)) {
					value.getMetadata()
							.setEtag(checkSum.getChecksumOpt().getBytesPerChecksum() + "@" + checkSum.getAlgorithmName());
				}
				value.getMetadata().setVersionId(null); // TODO
			}
			FSDataInputStream input = hdfsFS.open(path);
			value.setObjectContent(input);
		} catch (IOException e) {
			throw new ServerCossException(e);
		}
		return value;
	}

	@Override
	public PutObjectResult putObjectMetaData(String bucketName, String key, ObjectMetadata metadata) {
		// TODO
		return null;
	}

	@Override
	public PutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata) {
		PutObjectResult result = new PutObjectResult();
		try {
			Path path = new Path(config.getBucketRootPath(), bucketName + "/" + key);
			// TODO Existed check?
			FSDataOutputStream output = hdfsFS.create(path, true);
			IOUtils.copyBytes(input, output, DEFAULT_WRITE_BUFFER, true);
			// Sets permission
			if (!isNull(metadata) && !isNull(metadata.getAcl())) {
				FsPermission fp = toFsPermission(metadata.getAcl());
				hdfsFS.setPermission(path, fp);
			}
			// Checksum
			FileChecksum checkSum = hdfsFS.getFileChecksum(path);
			result.setETag(checkSum.getChecksumOpt().getBytesPerChecksum() + "@" + checkSum.getAlgorithmName());
			result.setVersionId(null); // TODO
		} catch (IOException e) {
			throw new ServerCossException(e);
		}
		return result;
	}

	@Override
	public void deleteObject(String bucketName, String key) {
		try {
			Path path = new Path(config.getBucketRootPath(), bucketName + "/" + key);
			// TODO logisic delete and multiple version?
			hdfsFS.delete(path, true);
		} catch (IOException e) {
			throw new ServerCossException(e);
		}
	}

	@Override
	public CopyObjectResult copyObject(String sourceBucketName, String sourceKey, String destinationBucketName,
			String destinationKey) throws CossException, ServerCossException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteVersion(String bucketName, String key, String versionId) throws CossException, ServerCossException {
		// TODO Auto-generated method stub

	}

	@Override
	public RestoreObjectResult restoreObject(RestoreObjectRequest request, String bucketName, String key)
			throws CossException, ServerCossException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectAcl getObjectAcl(String bucketName, String key) {
		ObjectAcl acl = new ObjectAcl();
		try {
			Path path = new Path(config.getBucketRootPath(), bucketName + "/" + key);
			FileStatus fileStat = hdfsFS.getFileStatus(path);
			if (!isNull(fileStat)) {
				acl.setOwner(new Owner(fileStat.getOwner(), fileStat.getOwner()));
				acl.setAcl(toAcl(fileStat.getPermission()));
			}
		} catch (IOException e) {
			throw new ServerCossException(e);
		}
		return acl;
	}

	@Override
	public void setObjectAcl(String bucketName, String key, ACL acl) {
		try {
			Path path = new Path(config.getBucketRootPath(), bucketName + "/" + key);
			hdfsFS.setPermission(path, toFsPermission(acl));
		} catch (IOException e) {
			throw new ServerCossException(e);
		}
	}

	@Override
	public boolean doesObjectExist(String bucketName, String key) {
		try {
			Path path = new Path(config.getBucketRootPath(), bucketName + "/" + key);
			return !hdfsFS.exists(path);
		} catch (IOException e) {
			throw new ServerCossException(e);
		}
	}

	@Override
	public void createSymlink(String bucketName, String symlink, String target) {
		try {
			Path targetPath = new Path(config.getBucketRootPath(), bucketName + "/" + target);
			Path symlinkPath = new Path(config.getBucketRootPath(), bucketName + "/" + symlink);
			hdfsFS.createSymlink(targetPath, symlinkPath, true);
		} catch (IOException e) {
			throw new ServerCossException(e);
		}
	}

	@Override
	public ObjectSymlink getSymlink(String bucketName, String symlink) {
		ObjectSymlink objSymlink = new ObjectSymlink();
		try {
			Path symlinkPath = new Path(config.getBucketRootPath(), bucketName + "/" + symlink);
			FileStatus fileStat = hdfsFS.getFileLinkStatus(symlinkPath);
			objSymlink.setSymlink(symlink);
			objSymlink.setTarget(fileStat.getSymlink().getName());

			objSymlink.getMetadata().setAtime(fileStat.getAccessTime());
			objSymlink.getMetadata().setMtime(fileStat.getModificationTime());
			objSymlink.getMetadata().setContentLength(fileStat.getLen());
			objSymlink.getMetadata().getUserMetadata().put("blockSize", getHumanReadable(fileStat.getBlockSize()));
			objSymlink.getMetadata().getUserMetadata().put("owner", fileStat.getOwner());
			objSymlink.getMetadata().getUserMetadata().put("group", fileStat.getGroup());
			// Checksum
			FileChecksum checkSum = hdfsFS.getFileChecksum(symlinkPath);
			if (!isNull(checkSum)) {
				objSymlink.getMetadata()
						.setEtag(checkSum.getChecksumOpt().getBytesPerChecksum() + "@" + checkSum.getAlgorithmName());
			}
			objSymlink.getMetadata().setVersionId(null); // TODO
		} catch (IOException e) {
			throw new ServerCossException(e);
		}
		return objSymlink;
	}

	/**
	 * Check hdfs {@link FileSystem} or re-initializing.
	 */
	protected void checkAndInitializingHdfsFileSystem() {
		long now = currentTimeMillis();
		if ((now - cacheRefreshLastTime.get()) < DEFAULT_CACHE_REFRESH_MS) {
			return;
		}

		boolean isRenew = false;
		if (!isNull(hdfsFS)) {
			// Check current FileSystem status
			try {
				hdfsFS.exists(new Path(config.getEndpointHdfsRootUri()));
			} catch (Exception e1) {
				log.warn("Could't check hdfs FileSystem. cause by: {}", e1.getMessage());
				if (e1 instanceof IOException) {
					try {
						FileSystem.closeAll();
					} catch (IOException e) {
						log.error("Failed to close hdfs FileSystem", e);
					}
					isRenew = true;
				}
			}
		}

		// Initializing FileSystem
		if (isNull(hdfsFS) || isRenew) {
			hdfsFS = getHdfsFileSystem(config);
			cacheRefreshLastTime.set(now);
		}
	}

	/**
	 * Gets creation hdfs {@link FileSystem}
	 * 
	 * @param config
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	protected FileSystem getHdfsFileSystem(HdfsCossProperties config) {
		log.info("Creation hdfs filesystem for user '{}' with root URI: '{}'", config.getUser(), config.getEndpointHdfsRootUri());
		try {
			return FileSystem.get(config.getEndpointHdfsRootUri(), new Configuration(), config.getUser());
		} catch (IOException | InterruptedException e) {
			throw new ServerCossException(e);
		}
	}

	/**
	 * {@link FsPermission} to {@link Acl}
	 * 
	 * @param fp
	 * @return
	 */
	final public static ACL toAcl(FsPermission fp) {
		int posixPermission = toPosixPermission(fp.getUserAction().ordinal(), fp.getGroupAction().ordinal(),
				fp.getOtherAction().ordinal());
		return toPosixAcl(posixPermission);
	}

	/**
	 * {@link Acl} to {@link FsPermission}
	 * 
	 * @param acl
	 * @return
	 */
	final public static FsPermission toFsPermission(ACL acl) {
		int posixPermission = toPosixPermission(acl);
		for (FsPermission fp : ACL_PERMISSSIONS) {
			if (posixPermission == fp.toShort())
				return fp;
		}
		throw new IllegalStateException(format("Unkown acl: %s", acl));
	}

	/**
	 * {@link ACL} <=> POSIX permission.
	 */
	final public static FsPermission[] ACL_PERMISSSIONS = { new FsPermission(valueOf(ACL_PRIVATE_POSIX)),
			new FsPermission(valueOf(ACL_READ_POSIX)), new FsPermission(valueOf(ACL_READ_WRITE_POSIX)) };

	/**
	 * Bucket default permission.(755) {@link FsPermission}
	 */
	final public static FsPermission DEFAULT_BUCKET_PERMISSION = ACL_PERMISSSIONS[1];

	public static final long DEFAULT_CACHE_REFRESH_MS = 2000L;

}
