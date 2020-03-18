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

import com.wl4g.devops.coss.AbstractCossEndpoint;
import com.wl4g.devops.coss.config.NativeCossProperties;
import com.wl4g.devops.coss.exception.ServerCossException;
import com.wl4g.devops.coss.model.*;
import com.wl4g.devops.coss.model.bucket.Bucket;
import com.wl4g.devops.coss.model.bucket.BucketList;
import com.wl4g.devops.coss.model.bucket.BucketMetadata;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.wl4g.devops.tool.common.lang.Assert2.isTrue;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

/**
 * File object storage based on native fileSystem.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月17日
 * @since
 */
public class NativeCossEndpoint extends AbstractCossEndpoint<NativeCossProperties> {

	/**
	 * {@link FileSystem}
	 */
	protected FileSystem nativeFS;

	public NativeCossEndpoint(NativeCossProperties config) {
		super(config);
	}

	@Override
	public CossProvider kind() {
		return CossProvider.NativeFs;
	}

	@Override
	public Bucket createBucket(String bucketName) {
		File bucketPath = new File(config.getEndpointRootDir(), bucketName);
		isTrue(!bucketPath.exists(), ServerCossException.class, "Duplicate creation directory '%s'", bucketPath);
		bucketPath.mkdirs();
		isTrue(bucketPath.exists(), ServerCossException.class, "Couldn't mkdirs bucket directory to '%s'", bucketPath);

		Bucket bucket = new Bucket(bucketName);
		bucket.setCreationDate(new Date());
		bucket.setOwner(getCurrentOwner());
		return bucket;
	}

	@Override
	public BucketList<Bucket> listBuckets(String prefix, String marker, Integer maxKeys) {
		BucketList<Bucket> bucketList = new BucketList<>();

		List<Bucket> buckets = asList(config.getEndpointRootDir().listFiles(f -> {
			// TODO
			if (f.getName().startsWith(prefix)) {
				return true;
			}
			return false;
		})).stream().map(f -> {
			if (f.isDirectory()) {
				try {
					Bucket bucket = new Bucket(config.getBucketKey(f.getPath()));
					bucket.setCreationDate(new Date(f.lastModified()));
					String owner = Files.getOwner(f.toPath()).getName();
					bucket.setOwner(new Owner(owner, owner));
					bucketList.getBucketList().add(bucket);
					return bucket;
				} catch (Exception e) {
					log.warn(format("Couldn't gets file attributes of '%s'", f), e);
				}
			}
			return null;
		}).collect(toList());

		return bucketList;
	}

	@Override
	public void deleteBucket(String bucketName) {
		File bucketPath = new File(config.getEndpointRootDir(), bucketName);
		bucketPath.delete();
		isTrue(!bucketPath.exists(), ServerCossException.class, "Couldn't delete bucket directory to '%s'", bucketPath);
	}

	@Override
	public BucketMetadata getBucketMetadata(String bucketName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccessControlList getBucketAcl(String bucketName) {
		File bucketPath = new File(config.getEndpointRootDir(), bucketName);
		Path path = Paths.get(URI.create(bucketPath.getPath()));
		AclFileAttributeView aclView = Files.getFileAttributeView(path, AclFileAttributeView.class);
		if (!isNull(aclView)) {
			try {
				List<AclEntry> aclEntries = aclView.getAcl();
				for (AclEntry entry : aclEntries) {
					System.out.format("Principal: %s%n", entry.principal());
					System.out.format("Type: %s%n", entry.type());
					System.out.format("Permissions are:%n");

					Set<AclEntryPermission> permissions = entry.permissions();
					for (AclEntryPermission p : permissions) {
						System.out.format("%s %n", p);
						// TODO
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public void setBucketAcl(String bucketName, ACL acl) {
		// TODO Auto-generated method stub

	}

	@Override
	public ObjectListing<ObjectSummary> listObjects(String bucketName, String prefix) {
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
