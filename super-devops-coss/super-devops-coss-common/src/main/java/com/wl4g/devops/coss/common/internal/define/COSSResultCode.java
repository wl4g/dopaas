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
package com.wl4g.devops.coss.common.internal.define;

/**
 * COSS Server side error code.
 */
public interface COSSResultCode {

	/**
	 * Access Denied (401)
	 */
	final public static String ACCESS_DENIED = "AccessDenied";

	/**
	 * Access Forbidden (403)
	 */
	final public static String ACCESS_FORBIDDEN = "AccessForbidden";

	/**
	 * Bucket pre-exists
	 */
	final public static String BUCKET_ALREADY_EXISTS = "BucketAlreadyExists";

	/**
	 * Bucket not empty.
	 */
	final public static String BUCKET_NOT_EMPTY = "BucketNotEmpty";

	/**
	 * File groups is too large.
	 */
	final public static String FILE_GROUP_TOO_LARGE = "FileGroupTooLarge";

	/**
	 * File part is stale.
	 */
	final public static String FILE_PART_STALE = "FilePartStale";

	/**
	 * Invalid argument.
	 */
	final public static String INVALID_ARGUMENT = "InvalidArgument";

	/**
	 * Non-existing Access ID
	 */
	final public static String INVALID_ACCESS_KEY_ID = "InvalidAccessKeyId";

	/**
	 * Invalid bucket name
	 */
	final public static String INVALID_BUCKET_NAME = "InvalidBucketName";

	/**
	 * Invalid object name
	 */
	final public static String INVALID_OBJECT_NAME = "InvalidObjectName";

	/**
	 * Invalid part
	 */
	final public static String INVALID_PART = "InvalidPart";

	/**
	 * Invalid part order
	 */
	final public static String INVALID_PART_ORDER = "InvalidPartOrder";

	/**
	 * The target bucket does not exist when setting logging.
	 */
	final public static String INVALID_TARGET_BUCKET_FOR_LOGGING = "InvalidTargetBucketForLogging";

	/**
	 * OSS Internal error.
	 */
	final public static String INTERNAL_ERROR = "InternalError";

	/**
	 * Missing content length.
	 */
	final public static String MISSING_CONTENT_LENGTH = "MissingContentLength";

	/**
	 * Missing required argument.
	 */
	final public static String MISSING_ARGUMENT = "MissingArgument";

	/**
	 * No bucket meets the requirement specified.
	 */
	final public static String NO_SUCH_BUCKET = "NoSuchBucket";

	/**
	 * File does not exist.
	 */
	final public static String NO_SUCH_KEY = "NoSuchKey";

	/**
	 * Version does not exist.
	 */
	final public static String NO_SUCH_VERSION = "NoSuchVersion";

	/**
	 * Not implemented method.
	 */
	final public static String NOT_IMPLEMENTED = "NotImplemented";

	/**
	 * Error occurred in precondition.
	 */
	final public static String PRECONDITION_FAILED = "PreconditionFailed";

	/**
	 * 304 Not Modified。
	 */
	final public static String NOT_MODIFIED = "NotModified";

	/**
	 * Invalid location.
	 */
	final public static String INVALID_LOCATION_CONSTRAINT = "InvalidLocationConstraint";

	/**
	 * The specified location does not match with the request.
	 */
	final public static String ILLEGAL_LOCATION_CONSTRAINT_EXCEPTION = "IllegalLocationConstraintException";

	/**
	 * The time skew between the time in request headers and server is more than
	 * 15 min.
	 */
	final public static String REQUEST_TIME_TOO_SKEWED = "RequestTimeTooSkewed";

	/**
	 * Request times out.
	 */
	final public static String REQUEST_TIMEOUT = "RequestTimeout";

	/**
	 * Invalid signature.
	 */
	final public static String SIGNATURE_DOES_NOT_MATCH = "SignatureDoesNotMatch";

	/**
	 * Too many buckets under a user.
	 */
	final public static String TOO_MANY_BUCKETS = "TooManyBuckets";

	/**
	 * Source buckets is not configured with CORS.
	 */
	final public static String NO_SUCH_CORS_CONFIGURATION = "NoSuchCORSConfiguration";

	/**
	 * The source bucket is not configured with static website (the index page
	 * is null).
	 */
	final public static String NO_SUCH_WEBSITE_CONFIGURATION = "NoSuchWebsiteConfiguration";

	/**
	 * The source bucket is not configured with lifecycle rule.
	 */
	final public static String NO_SUCH_LIFECYCLE = "NoSuchLifecycle";

	/**
	 * Malformed xml.
	 */
	final public static String MALFORMED_XML = "MalformedXML";

	/**
	 * Invalid encryption algorithm error.
	 */
	final public static String INVALID_ENCRYPTION_ALGORITHM_ERROR = "InvalidEncryptionAlgorithmError";

	/**
	 * The upload Id does not exist.
	 */
	final public static String NO_SUCH_UPLOAD = "NoSuchUpload";

	/**
	 * The entity is too small. (Part must be more than 100K)
	 */
	final public static String ENTITY_TOO_SMALL = "EntityTooSmall";

	/**
	 * The entity is too big.
	 */
	final public static String ENTITY_TOO_LARGE = "EntityTooLarge";

	/**
	 * Invalid MD5 digest.
	 */
	final public static String INVALID_DIGEST = "InvalidDigest";

	/**
	 * Invalid range of the character.
	 */
	final public static String INVALID_RANGE = "InvalidRange";

	/**
	 * Security token is not supported.
	 */
	final public static String SECURITY_TOKEN_NOT_SUPPORTED = "SecurityTokenNotSupported";

	/**
	 * The specified object does not support append operation.
	 */
	final public static String OBJECT_NOT_APPENDALBE = "ObjectNotAppendable";

	/**
	 * The position of append on the object is not same as the current length.
	 */
	final public static String POSITION_NOT_EQUAL_TO_LENGTH = "PositionNotEqualToLength";

	/**
	 * Invalid response.
	 */
	final public static String INVALID_RESPONSE = "InvalidResponse";

	/**
	 * Callback failed. The operation (such as download or upload) succeeded
	 * though.
	 */
	final public static String CALLBACK_FAILED = "CallbackFailed";

	/**
	 * The Live Channel does not exist.
	 */
	final public static String NO_SUCH_LIVE_CHANNEL = "NoSuchLiveChannel";

	/**
	 * symlink target file does not exist.
	 */
	final public static String NO_SUCH_SYM_LINK_TARGET = "SymlinkTargetNotExist";

	/**
	 * The archive file is not restored before usage.
	 */
	final public static String INVALID_OBJECT_STATE = "InvalidObjectState";

	/**
	 * The policy text is illegal.
	 */
	final public static String INVALID_POLICY_DOCUMENT = "InvalidPolicyDocument";

	/**
	 * The exsiting bucket without policy.
	 */
	final public static String NO_SUCH_BUCKET_POLICY = "NoSuchBucketPolicy";

	/**
	 * The object has already exists.
	 */
	final public static String OBJECT_ALREADY_EXISTS = "ObjectAlreadyExists";
}