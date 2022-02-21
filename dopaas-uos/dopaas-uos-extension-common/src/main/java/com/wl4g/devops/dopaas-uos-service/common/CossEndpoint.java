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
package com.wl4g.devops.uos.common;

import com.wl4g.devops.uos.common.exception.CossException;
import com.wl4g.devops.uos.common.exception.ServerCossException;
import com.wl4g.devops.uos.common.model.*;
import com.wl4g.devops.uos.common.model.bucket.Bucket;
import com.wl4g.devops.uos.common.model.bucket.BucketList;
import com.wl4g.devops.uos.common.model.bucket.BucketMetadata;
import com.wl4g.devops.uos.common.model.metadata.BucketStatusMetaData;
import io.minio.messages.CompressionType;
import io.minio.messages.JsonType;

import java.io.InputStream;
import java.net.URL;

import static com.wl4g.components.common.lang.Assert2.notNull;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Composite object storage server file system API.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年2月28日
 * @since
 */
public interface CossEndpoint {

    /**
     * Gets {@link CossProvider}
     *
     * @return
     */
    CossProvider kind();

    // --- Bucket's function ---

    /**
     * Creates {@link Bucket} instance.
     *
     * @param bucketName bucket name
     * @throws CossException
     * @throws ServerCossException
     */
    Bucket createBucket(String bucketName) throws CossException, ServerCossException;

    /**
     * Returns all {@link Bucket} instances of the current account that meet the
     * conditions specified.
     *
     * @param prefix  The prefix of the bucket name returned. If null, the bucket
     *                name could have any prefix.
     * @param marker  The start point in the lexicographic order for the buckets to
     *                return. If null, return the buckets from the beginning in the
     *                lexicographic order. For example, if the account has buckets
     *                bk1, bk2, bk3. If the marker is set as bk2, then only bk2 and
     *                bk3 meet the criteria. But if the marker is null, then all
     *                three buckets meet the criteria.
     * @param maxKeys Max bucket count to return. The valid value is from 1 to 1000,
     *                default is 100 if it's null.
     * @return The list of {@link Bucket} instances.
     * @throws CossException
     * @throws ServerCossException
     */
    <T extends Bucket> BucketList<T> listBuckets(String prefix, String marker, Integer maxKeys)
            throws CossException, ServerCossException;

    /**
     * Deletes the {@link Bucket} instance. A non-empty bucket could not be
     * deleted.
     *
     * @param bucketName bucket name to delete.
     * @throws CossException
     * @throws ServerCossException
     */
    void deleteBucket(String bucketName) throws CossException, ServerCossException;

    /**
     * Gets the metadata of {@link Bucket}.
     *
     * @param bucketName Bucket name.
     * @return The {@link BucketMetadata} instance.
     * @throws CossException
     * @throws ServerCossException
     */
    BucketMetadata getBucketMetadata(String bucketName) throws CossException, ServerCossException;

    /**
     * Returns the Access control List (ACL) of the {@link Bucket} instance.
     *
     * @param bucketName Bucket Name.
     * @return Access Control List(ACL) {@link AccessControlList}.
     * @throws CossException
     * @throws ServerCossException
     */
    AccessControlList getBucketAcl(String bucketName) throws CossException, ServerCossException;

    /**
     * Applies the Access Control List(ACL) on the {@link Bucket}.
     *
     * @param bucketName Bucket name.
     * @param acl        {@link CannedAccessControlList} instance. If the instance is
     *                   null, no ACL change on the bucket (but the request is still
     *                   sent).
     * @throws CossException
     * @throws ServerCossException
     */
    void setBucketAcl(String bucketName, ACL acl) throws CossException, ServerCossException;


    BucketStatusMetaData getBucketIndex(String bucketName) throws Exception;

    // --- Object's function ---

    /**
     * Lists all objects under the specified {@link Bucket}
     *
     * @param bucketName Bucket name
     * @return {@link ObjectListing} instance that has all objects.
     * @throws CossException
     * @throws ServerCossException
     */
    default <T extends ObjectSummary> ObjectListing<T> listObjects(String bucketName) {
        return listObjects(bucketName, null);
    }

    /**
     * Lists all objects under the specified {@link Bucket} with the specified
     * prefix.
     *
     * @param bucketName Bucket name.
     * @param prefix     The prefix returned object must have.
     * @return A {@link ObjectListing} instance that has all objects
     * @throws OSSException
     * @throws ClientException
     * @throws CossException
     * @throws ServerCossException
     */
    <T extends ObjectSummary> ObjectListing<T> listObjects(String bucketName, String prefix)
            throws CossException, ServerCossException;

    /**
     * Gets a {@link ObjectValue} from {@link Bucket}.
     *
     * @param bucketName Bucket name.
     * @param key        Object Key.
     * @return A {@link OSSObject} instance. The caller is responsible to close
     * the connection after usage.
     * @throws CossException
     * @throws ServerCossException
     */
    ObjectValue getObject(String bucketName, String key) throws CossException, ServerCossException;

    /**
     * Uploads the file to the {@link Bucket} from the {@link InputStream}
     * instance. It overwrites the existing one and the bucket must exist.
     *
     * @param bucketName Bucket name.
     * @param key        object key.
     * @param input      {@link InputStream} instance to write from. The must be
     *                   readable.
     * @throws CossException
     * @throws ServerCossException
     */
    default CossPutObjectResult putObject(String bucketName, String key, InputStream input) {
        return putObject(bucketName, key, input, null);
    }

    CossPutObjectResult putObjectMetaData(String bucketName, String key, ObjectMetadata metadata);

    /**
     * Uploads the file to the {@link Bucket} from the @{link InputStream} with
     * the {@link ObjectMetadata} information。
     *
     * @param bucketName Bucket name.
     * @param key        Object key.
     * @param input      {@link InputStream} instance to write from. It must be
     *                   readable.
     * @param metadata   The {@link ObjectMetadata} instance. If it does not specify
     *                   the Content-Length information, the data is encoded by chunked
     *                   tranfer encoding.
     * @throws CossException
     * @throws ServerCossException
     */
    CossPutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata)
            throws CossException, ServerCossException;

    /**
     * Copies an existing file in UOS from source bucket to the target bucket.
     * If target file exists, it would be overwritten by the source file.
     *
     * @param sourceBucketName      Source object's bucket name.
     * @param sourceKey             Source object's key.
     * @param destinationBucketName Target object's bucket name.
     * @param destinationKey        Target object's key.
     * @return A {@link CopyObjectResult} instance.
     * @throws CossException
     * @throws ServerCossException
     */
    public CopyObjectResult copyObject(String sourceBucketName, String sourceKey, String destinationBucketName,
                                       String destinationKey) throws CossException, ServerCossException;


    /**
     * Move an existing file in UOS from source bucket to the target bucket.
     * If target file exists, it would be overwritten by the source file.
     *
     * @param sourceBucketName      Source object's bucket name.
     * @param sourceKey             Source object's key.
     * @param destinationBucketName Target object's bucket name.
     * @param destinationKey        Target object's key.
     * @return A {@link CopyObjectResult} instance.
     * @throws CossException
     * @throws ServerCossException
     */
    default public CopyObjectResult moveObject(String sourceBucketName, String sourceKey, String destinationBucketName,
                                               String destinationKey) throws CossException, ServerCossException {
        throw new UnsupportedOperationException();
    }


    default public ShareObject shareObject(String bucketName, String key, Integer expireSec, Boolean presigned) throws CossException, ServerCossException {
        throw new UnsupportedOperationException();
    }

    default public void resetBucketAcl(String bucketName) throws CossException, ServerCossException {
        throw new UnsupportedOperationException();
    }

    default public String selectObjectContent(String bucket, String key, String type,
                                              CompressionType compressionType, JsonType jsonType,//for json
                                              Character recordDelimiter, Boolean useFileHeaderInfo,// for csv
                                              String sqlExpression
    )
            throws CossException{
        throw new UnsupportedOperationException();
    }

    /**
     * Deletes the specified {@link ObjectValue} by bucket name and object key.
     *
     * @param bucketName Bucket name.
     * @param key        Object key.
     * @throws CossException
     * @throws ServerCossException
     */
    void deleteObject(String bucketName, String key) throws CossException, ServerCossException;

    /**
     * Deletes a specific version of the specified object in the specified
     * bucket. Once deleted, there is no method to restore or undelete an object
     * version. This is the only way to permanently delete object versions that
     * are protected by versioning.
     * <p>
     * If attempting to delete an object that does not exist, OSS will return a
     * success message instead of an error message.
     * </p>
     *
     * @param bucketName The name of the OSS bucket containing the object to delete.
     * @param key        The key of the object to delete.
     * @param versionId  The version of the object to delete.
     * @throws ClientException     If any errors are encountered in the client while making the
     *                             request or handling the response.
     * @throws OSSException        If any errors occurred in OSS while processing the request.
     * @throws CossException
     * @throws ServerCossException
     */
    void deleteVersion(String bucketName, String key, String versionId) throws CossException, ServerCossException;

    /**
     * Restores the object of archive storage. The function is not applicable to
     * Normal or IA storage. The restoreObject() needs to be called prior to
     * calling getObject() on an archive object.
     *
     * @param request
     * @return A {@link CossRestoreObjectResult} instance.
     */
    CossRestoreObjectResult restoreObject(CossRestoreObjectRequest request) throws CossException, ServerCossException;

    /**
     * Gets the Access Control List (ACL) of the OSS object.
     *
     * @param bucketName Bucket name.
     * @param key        Object Key.
     * @return The {@link ObjectAcl} instance of the object.
     * @throws CossException
     * @throws ServerCossException
     */
    ObjectAcl getObjectAcl(String bucketName, String key) throws CossException, ServerCossException;

    /**
     * Sets the Access Control List (ACL) on a {@link ObjectValue} instance.
     *
     * @param bucketName Bucket name.
     * @param key        Object Key.
     * @param acl        One of the three values: Private, PublicRead or
     *                   PublicReadWrite.
     * @throws CossException
     * @throws ServerCossException
     */
    void setObjectAcl(String bucketName, String key, ACL acl) throws CossException, ServerCossException;

    /**
     * Checks if a specific {@link ObjectValue} exists under the specific
     * {@link Bucket}. 302 Redirect or OSS mirroring will not impact the result
     * of this function.
     *
     * @param bucketName Bucket name.
     * @param key        Object Key.
     * @return True if exists; false if not.
     * @throws CossException
     * @throws ServerCossException
     */
    boolean doesObjectExist(String bucketName, String key) throws CossException, ServerCossException;

    /**
     * Creates a symlink link to a target file under the bucket---this is not
     * supported for archive class bucket.
     *
     * @param bucketName Bucket name.
     * @param symlink    symlink name.
     * @param target     target file key.
     * @throws CossException
     * @throws ServerCossException
     */
    default void createSymlink(String bucketName, String symlink, String target) throws CossException, ServerCossException {
        throw new CossException(format("No supported operation of UOS.provider: %s", kind()));
    }

    /**
     * Gets the symlink information for the given symlink name.
     *
     * @param bucketName Bucket name.
     * @param symlink    The symlink name.
     * @return The symlink information, including the target file name and its
     * metadata.
     * @throws CossException
     * @throws ServerCossException
     */
    default ObjectSymlink getSymlink(String bucketName, String symlink) throws CossException, ServerCossException {
        throw new CossException(format("No supported operation of UOS.provider: %s", kind()));
    }

    /**
     * Returns an URL for the object stored in the specified bucket and key.
     * <p>
     * If the object identified by the given bucket and key has public read
     * permissions (ex: {@link ACL#PublicRead}), then this URL can be directly
     * accessed to retrieve the object's data.
     *
     * @param bucketName The name of the bucket containing the object whose URL is
     *                   being requested.
     * @param key        The key under which the object whose URL is being requested is
     *                   stored.
     * @return A unique URL for the object stored in the specified bucket and
     * key.
     * @throws CossException
     * @throws ServerCossException
     */
    default URL getUrl(String bucketName, String key) throws CossException, ServerCossException {
        throw new CossException(format("No supported operation of UOS.provider: %s", kind()));
    }

    /**
     * UOS provider type definitions.
     *
     * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
     * @version v1.0 2019年11月5日
     * @throws CossException
     * @throws ServerCossException
     * @since
     */
    public static enum CossProvider {

        /**
         * UOS provider for aliyun oss.
         */
        AliyunOss("aliyunoss"),

        /**
         * UOS provider for aws s3.
         */
        AwsS3("awss3"),

        /**
         * UOS provider for hdfs.
         */
        Hdfs("hdfs"),

        /**
         * UOS provider for glusterfs.
         */
        GlusterFs("glusterfs"),

        /**
         * UOS provider for native fs.
         */
        NativeFs("nativefs"),

        /**
         * UOS provider for Minio .
         */
        Minio("minio");

        final private String value;

        private CossProvider(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        /**
         * Safe converter string to {@link CossProvider}
         *
         * @param uosProvider
         * @return
         */
        final public static CossProvider safeOf(String uosProvider) {
            if (isBlank(uosProvider))
                return null;

            for (CossProvider t : values())
                if (t.getValue().equalsIgnoreCase(uosProvider) || t.name().equalsIgnoreCase(uosProvider))
                    return t;

            return null;
        }

        /**
         * Converter string to {@link CossProvider}
         *
         * @param uosProvider
         * @return
         */
        final public static CossProvider of(String uosProvider) {
            CossProvider type = safeOf(uosProvider);
            notNull(type, format("Unsupported UOS provider for %s", uosProvider));
            return type;
        }

    }

}