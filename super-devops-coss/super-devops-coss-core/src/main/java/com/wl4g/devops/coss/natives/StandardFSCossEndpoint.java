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

import com.google.common.hash.HashCode;
import com.wl4g.devops.coss.AbstractCossEndpoint;
import com.wl4g.devops.coss.config.StandardFSCossProperties;
import com.wl4g.devops.coss.exception.CossException;
import com.wl4g.devops.coss.exception.ServerCossException;
import com.wl4g.devops.coss.model.*;
import com.wl4g.devops.coss.model.bucket.Bucket;
import com.wl4g.devops.coss.model.bucket.BucketList;
import com.wl4g.devops.coss.model.bucket.BucketMetadata;
import com.wl4g.devops.tool.common.io.FileIOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.hash.Hashing.md5;
import static com.wl4g.devops.coss.model.ACL.*;
import static com.wl4g.devops.coss.model.metadata.ObjectsStatusMetaData.ObjectStatusMetaData;
import static com.wl4g.devops.tool.common.io.FileDeletionUtils.deleteAnyone;
import static com.wl4g.devops.tool.common.lang.Assert2.isTrue;
import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

/**
 * Based on standard file system object storage.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月17日
 * @since
 */
public abstract class StandardFSCossEndpoint<C extends StandardFSCossProperties> extends AbstractCossEndpoint<C> {

	/**
	 * Standard FS of {@link FileSystem}
	 */
	final protected FileSystem standardFS;

	/**
	 * Storage objects metadata of {@link MetadataIndexManager}
	 */
	@Autowired
	private MetadataIndexManager metadataManager;

	public StandardFSCossEndpoint(C config, FileSystem standardFS) {
		super(config);
		notNullOf(standardFS, "standardFS");
		this.standardFS = standardFS;
	}

	@Override
	public Bucket createBucket(String bucketName) {
		File bucketPath = config.getBucketPath(bucketName);
		isTrue(!bucketPath.exists(), ServerCossException.class, "Duplicate creation directory '%s'", bucketPath);
		bucketPath.mkdirs();
		isTrue(bucketPath.exists(), ServerCossException.class, "Couldn't mkdirs bucket directory to '%s'", bucketPath);
		metadataManager.createBucketMeta(bucketPath.getAbsolutePath());
		setBucketAcl(bucketName, ACL.Default);
		Bucket bucket = new Bucket(bucketName);
		bucket.setCreationDate(new Date());
		bucket.setOwner(getCurrentOwner());
		return bucket;
	}

	@Override
	public BucketList<Bucket> listBuckets(String prefix, String marker, Integer maxKeys) {
		BucketList<Bucket> bucketList = new BucketList<>();
		List<Bucket> buckets = asList(
				config.getEndpointRootDir().listFiles(f -> !f.getName().startsWith(".") && f.getName().startsWith(prefix)))
						.stream().map(f -> {
							if (f.isDirectory()) {
								try {
									Bucket bucket = new Bucket(config.getBucketKey(f.getPath()));
									bucket.setCreationDate(new Date(f.lastModified()));
									String owner = Files.getOwner(f.toPath()).getName();
									bucket.setOwner(new Owner(owner, owner));
									return bucket;
								} catch (Exception e) {
									log.warn(format("Couldn't gets file attributes of '%s'", f), e);
								}
							}
							return null;
						}).filter(f -> !isNull(f)).collect(toList());
		bucketList.getBucketList().addAll(buckets);
		return bucketList;
	}

	@Override
	public void deleteBucket(String bucketName) {
		File bucketPath = config.getBucketPath(bucketName);
		File trashPath = new File(config.getBucketPathTrash());
		if (!trashPath.exists()) {
			trashPath.mkdirs();
		}
		// Logisic delete. // TODO add deletingTaskManager?
		bucketPath.renameTo(trashPath);
		// Check renamed
		if (bucketPath.exists() || !trashPath.exists()) {
			throw new ServerCossException(format("Couldn't delete bucket directory '%s' to '%s'", bucketPath, trashPath));
		}

		log.info("Deleted bucket directory '{}' to '{}'", bucketPath, trashPath);
	}

	@Override
	public BucketMetadata getBucketMetadata(String bucketName) {
		BucketMetadata bucketMetadata = new BucketMetadata();
		bucketMetadata.setBucketName(bucketName);
		bucketMetadata.setBucketRegion(null);// TODO
		return bucketMetadata;
	}

	@Override
	public AccessControlList getBucketAcl(String bucketName) {
		File bucketPath = config.getBucketPath(bucketName);

		AccessControlList accessControlList = new AccessControlList();
		try {
			Set<PosixFilePermission> posixFilePermissions = Files.getPosixFilePermissions(bucketPath.toPath());
			ACL acl = getPosixPermissionAcl(posixFilePermissions);
			String owner = Files.getOwner(bucketPath.toPath()).getName();
			accessControlList.setAcl(acl);
			accessControlList.setOwner(new Owner(owner, owner));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return accessControlList;
	}

	@Override
	public void setBucketAcl(String bucketName, ACL acl) {
		File bucketPath = config.getBucketPath(bucketName);
		Set<PosixFilePermission> posixPermissions = getAclPosixPermissions(acl);
		try {
			Files.setPosixFilePermissions(Paths.get(bucketPath.getAbsolutePath()), posixPermissions);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ObjectListing<ObjectSummary> listObjects(String bucketName, String prefix) {

		// e.g abc/def/hh
		int i = prefix.lastIndexOf("/");
		String subPath = prefix.substring(0, i + 1);// e.g abc/def/
		String search = prefix.substring(i + 1, prefix.length());// e.g hh

		ObjectListing<ObjectSummary> objectListing = new ObjectListing<>();
		File path = new File(config.getEndpointRootDir() + File.separator + bucketName + File.separator + subPath);

		if (!path.exists()) {
			return objectListing;
		}
		objectListing.setBucketName(bucketName);
		objectListing.setPrefix(prefix);

		List<ObjectSummary> objectSummaries = asList(
				path.listFiles(f -> !f.getName().startsWith(".") && f.getName().startsWith(search))).stream().map(f -> {
					try {
						ObjectSummary objectSummary = new ObjectSummary();
						objectSummary.setBucketName(bucketName);
						if (f.isDirectory()) {
							objectSummary.setKey(f.getName() + File.separator);
						} else {
							objectSummary.setKey(f.getName());
							objectSummary.setSize(Files.size(f.toPath()));
						}
						objectSummary.setMtime(f.lastModified());
						objectSummary.setStorageType(kind().getValue());
						String owner = Files.getOwner(f.toPath()).getName();
						objectSummary.setOwner(new Owner(owner, owner));

						return objectSummary;
					} catch (Exception e) {
						log.warn(format("Couldn't gets file attributes of '%s'", f), e);
					}
					return null;
				}).filter(f -> !isNull(f)).collect(toList());

		objectListing.getObjectSummaries().addAll(objectSummaries);
		return objectListing;
	}

	@Override
	public ObjectValue getObject(String bucketName, String key) {
		File objectPath = config.getObjectPath(bucketName, key);
		File bucketPath = config.getBucketPath(bucketName);
		ObjectValue objectValue = new ObjectValue();
		objectValue.setBucketName(bucketName);
		objectValue.setKey(key);

		try {
			objectValue.setObjectContent(new FileInputStream(objectPath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		ObjectStatusMetaData objectStatusMetaData = null;
		try {
			objectStatusMetaData = metadataManager.getObject(bucketPath.getAbsolutePath(), objectPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ObjectMetadata objectMetadata = new ObjectMetadata();
		if (nonNull(objectStatusMetaData)) {
			BeanUtils.copyProperties(objectStatusMetaData, objectMetadata);
		}
		try {
			objectMetadata.setContentLength(Files.size(objectPath.toPath()));
			objectMetadata.setMtime(Files.getLastModifiedTime(objectPath.toPath()).toMillis());
		} catch (IOException e) {
			e.printStackTrace();
		}
		ObjectAcl objectAcl = getObjectAcl(bucketName, key);
		objectMetadata.setAcl(objectAcl.getAcl());
		objectMetadata.setPath(new ObjectKey(objectPath.getAbsolutePath()));
		objectValue.setMetadata(objectMetadata);
		return objectValue;
	}

	@Override
	public PutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata) {
		File objectPath = config.getObjectPath(bucketName, key);
		File bucketPath = config.getBucketPath(bucketName);
		try {
			FileIOUtils.copyInputStreamToFile(input, objectPath);
			setObjectAcl(bucketName, key, nonNull(metadata) ? metadata.getAcl() : null);
			ObjectStatusMetaData objectStatusMetaData = new ObjectStatusMetaData();
			if (nonNull(metadata)) {
				BeanUtils.copyProperties(metadata, objectStatusMetaData);
			}
			HashCode hashCode = md5().hashBytes(Files.readAllBytes(objectPath.toPath()));
			objectStatusMetaData.setEtag(hashCode.toString());
			metadataManager.addObject(bucketPath.getAbsolutePath(), objectPath, objectStatusMetaData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public PutObjectResult putObjectMetaData(String bucketName, String key, ObjectMetadata metadata) {
		File objectPath = config.getObjectPath(bucketName, key);
		try {
			ObjectStatusMetaData objectStatusMetaData = new ObjectStatusMetaData();
			if (nonNull(metadata)) {
				BeanUtils.copyProperties(metadata, objectStatusMetaData);
			}
			metadataManager.modifyObject(objectPath, objectStatusMetaData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new PutObjectResult();
	}

	@Override
	public CopyObjectResult copyObject(String sourceBucketName, String sourceKey, String destinationBucketName,
			String destinationKey) throws CossException, ServerCossException {
		CopyObjectResult copyObjectResult = new CopyObjectResult();
		File sourceBucketPath = config.getBucketPath(sourceBucketName);
		File destinationBucketPath = config.getBucketPath(destinationBucketName);
		File sourcePath = config.getObjectPath(sourceBucketName, sourceKey);
		File destinationPath = config.getObjectPath(destinationBucketName, destinationKey);
		try {
			Files.copy(sourcePath.toPath(), destinationPath.toPath());

			ObjectStatusMetaData object = metadataManager.getObject(sourceBucketPath.getAbsolutePath(), sourcePath);
			metadataManager.addObject(destinationBucketPath.getAbsolutePath(), destinationPath, object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		copyObjectResult.setLastModified(new Date());
		return copyObjectResult;
	}

	@Override
	public void deleteObject(String bucketName, String key) {
		File bucketPath = config.getBucketPath(bucketName);
		File objectPath = config.getObjectPath(bucketName, key);

		File trash = new File(config.getObjectPathTrash(bucketName));
		if (!trash.getParentFile().exists()) {
			trash.getParentFile().mkdirs();
		}
		objectPath.renameTo(trash);
		// objectPath.delete();
		isTrue(!objectPath.exists(), ServerCossException.class, "Couldn't delete object to '%s'", objectPath);

		try {
			metadataManager.delObject(bucketPath.getAbsolutePath(), objectPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteVersion(String bucketName, String key, String versionId) throws CossException, ServerCossException {
		File bucketPath = config.getBucketPath(bucketName);
		File objectPath = config.getObjectPath(bucketName, key);
		deleteAnyone(objectPath.getAbsolutePath());

		try {
			metadataManager.delObject(bucketPath.getAbsolutePath(), objectPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public RestoreObjectResult restoreObject(RestoreObjectRequest request, String bucketName, String key)
			throws CossException, ServerCossException {
		File trash = new File(config.getObjectPathTrash(bucketName) + File.separator + key);
		if (trash.exists()) {

		}
		return null;
	}

	@Override
	public ObjectAcl getObjectAcl(String bucketName, String key) {
		File objectPath = config.getObjectPath(bucketName, key);
		ObjectAcl objectAcl = new ObjectAcl();
		try {
			Set<PosixFilePermission> posixFilePermissions = Files.getPosixFilePermissions(objectPath.toPath());
			ACL acl = getPosixPermissionAcl(posixFilePermissions);
			String owner = Files.getOwner(objectPath.toPath()).getName();
			objectAcl.setAcl(acl);
			objectAcl.setOwner(new Owner(owner, owner));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return objectAcl;
	}

	@Override
	public void setObjectAcl(String bucketName, String key, ACL acl) {
		File objectPath = config.getObjectPath(bucketName, key);
		if (isNull(acl)) {
			acl = ACL.Default;
		}
		Set<PosixFilePermission> posixFilePermissions = getAclPosixPermissions(acl);
		try {
			Files.setPosixFilePermissions(Paths.get(objectPath.getAbsolutePath()), posixFilePermissions);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean doesObjectExist(String bucketName, String key) {
		File objectPath = config.getObjectPath(bucketName, key);
		return objectPath.exists();
	}

	@Override
	public void createSymlink(String bucketName, String symlink, String target) {
		File symlinkFile = config.getObjectPath(bucketName, symlink);
		File targetFile = config.getObjectPath(bucketName, target);
		try {
			Files.createSymbolicLink(symlinkFile.toPath(), targetFile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ObjectSymlink getSymlink(String bucketName, String symlink) {
		ObjectSymlink objectSymlink = new ObjectSymlink();
		File symlinkFile = config.getObjectPath(bucketName, symlink);
		File bucketPath = config.getBucketPath(bucketName);
		try {
			Path path = Files.readSymbolicLink(symlinkFile.toPath());
			objectSymlink.setTarget(path.toString());
			objectSymlink.setSymlink(symlink);

			ObjectStatusMetaData objectStatusMetaData = null;
			try {
				objectStatusMetaData = metadataManager.getObject(bucketPath.getAbsolutePath(), symlinkFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			ObjectMetadata objectMetadata = new ObjectMetadata();
			if (nonNull(objectStatusMetaData)) {
				BeanUtils.copyProperties(objectStatusMetaData, objectMetadata);
			}

			objectMetadata.setAcl(getObjectAcl(bucketName, symlink).getAcl());
			try {
				objectMetadata.setContentLength(Files.size(symlinkFile.toPath()));
				objectMetadata.setMtime(Files.getLastModifiedTime(symlinkFile.toPath()).toMillis());
			} catch (IOException e) {
				e.printStackTrace();
			}
			objectSymlink.setMetadata(objectMetadata);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public URL getUrl(String bucketName, String key) throws CossException, ServerCossException {
		return null;
	}

	private static Set<PosixFilePermission> getAclPosixPermissions(ACL acl) {
		// using PosixFilePermission to set file permissions
		Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
		if (Default.equals(acl)) {// 0755
			// add owners permission
			perms.add(PosixFilePermission.OWNER_READ);
			perms.add(PosixFilePermission.OWNER_WRITE);
			perms.add(PosixFilePermission.OWNER_EXECUTE);
			// add group permissions
			perms.add(PosixFilePermission.GROUP_READ);
			perms.add(PosixFilePermission.GROUP_EXECUTE);
			// add others permissions
			perms.add(PosixFilePermission.OTHERS_READ);
			perms.add(PosixFilePermission.OTHERS_EXECUTE);
		} else if (Private.equals(acl)) {// 0700
			// add owners permission
			perms.add(PosixFilePermission.OWNER_READ);
			perms.add(PosixFilePermission.OWNER_WRITE);
			perms.add(PosixFilePermission.OWNER_EXECUTE);
		} else if (PublicRead.equals(acl)) {// 0755
			// add owners permission
			perms.add(PosixFilePermission.OWNER_READ);
			perms.add(PosixFilePermission.OWNER_WRITE);
			perms.add(PosixFilePermission.OWNER_EXECUTE);
			// add group permissions
			perms.add(PosixFilePermission.GROUP_READ);
			perms.add(PosixFilePermission.GROUP_EXECUTE);
			// add others permissions
			perms.add(PosixFilePermission.OTHERS_READ);
			perms.add(PosixFilePermission.OTHERS_EXECUTE);
		} else if (PublicReadWrite.equals(acl)) {// 0777
			// add owners permission
			perms.add(PosixFilePermission.OWNER_READ);
			perms.add(PosixFilePermission.OWNER_WRITE);
			perms.add(PosixFilePermission.OWNER_EXECUTE);
			// add group permissions
			perms.add(PosixFilePermission.GROUP_READ);
			perms.add(PosixFilePermission.GROUP_WRITE);
			perms.add(PosixFilePermission.GROUP_EXECUTE);
			// add others permissions
			perms.add(PosixFilePermission.OTHERS_READ);
			perms.add(PosixFilePermission.OTHERS_WRITE);
			perms.add(PosixFilePermission.OTHERS_EXECUTE);
		}
		return perms;
	}

	private static ACL getPosixPermissionAcl(Set<PosixFilePermission> posixPermissions) {
		if (posixPermissions.containsAll(getAclPosixPermissions(PublicReadWrite))) {
			return PublicReadWrite;
		} else if (posixPermissions.containsAll(getAclPosixPermissions(PublicRead))) {
			return PublicRead;
		} else if (posixPermissions.containsAll(getAclPosixPermissions(Private))) {
			return Private;
		} else {
			return null;
		}
	}

}
