package com.wl4g.devops.coss.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * The entity class for representing an object in OSS.
 * <p>
 * In OSS, every file is an OSSObject and every single file should be less than
 * 5G for using Simple upload, Form upload and Append Upload. Only multipart
 * upload could upload a single file more than 5G. Any object has key, data and
 * user metadata. The key is the object's name and the data is object's file
 * content. The user metadata is a dictionary of key-value entries to store some
 * custom data about the object.
 * </p>
 * Object naming rules
 * <ul>
 * <li>use UTF-8 encoding</li>
 * <li>Length is between 1 to 1023</li>
 * <li>Could not have slash or backslash</li>
 * </ul>
 *
 */
public class ObjectValue implements Closeable {

	/**
	 * Object key (name)
	 */
	private String key;

	/**
	 * Object's bucket name
	 */
	private String bucketName;

	/**
	 * Object's metadata.
	 */
	private ObjectMetadata metadata = new ObjectMetadata();

	/** Object's content */
	@JsonIgnore
	private transient InputStream objectContent;

	public ObjectValue() {
		super();
	}

	public ObjectValue(String key, String bucketName) {
		super();
		this.key = key;
		this.bucketName = bucketName;
	}

	/**
	 * Gets the object's metadata
	 * 
	 * @return Object's metadata in（{@link ObjectMetadata}
	 */
	public ObjectMetadata getMetadata() {
		return metadata;
	}

	/**
	 * Sets the object's metadata.
	 * 
	 * @param metadata
	 *            Object's metadata.
	 */
	public void setMetadata(ObjectMetadata metadata) {
		this.metadata = metadata;
	}

	/**
	 * Get's the object's content in {@link InputStream}.
	 * 
	 * @return The object's content in {@link InputStream}.
	 */
	public InputStream getObjectContent() {
		return objectContent;
	}

	/**
	 * Sets the object's content in {@link InputStream}.
	 * 
	 * @param objectContent
	 *            The object's content in {@link InputStream}.
	 */
	public void setObjectContent(InputStream objectContent) {
		this.objectContent = objectContent;
	}

	/**
	 * Gets the object's bucket name.
	 * 
	 * @return The object's bucket name.
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * Sets the object's bucket name.
	 * 
	 * @param bucketName
	 *            The object's bucket name.
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * Gets the object's key.
	 * 
	 * @return Object Key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the object's key.
	 * 
	 * @param key
	 *            Object Key.
	 */
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public void close() throws IOException {
		if (objectContent != null) {
			objectContent.close();
		}
	}

	// /**
	// * Forcefully close the response. The remaining data in the server will
	// not
	// * be downloaded.
	// *
	// * @throws IOException
	// */
	// public void forcedClose() throws IOException {
	// this.response.abort();
	// }

	@Override
	public String toString() {
		return "ObjectValue [key=" + getKey() + ",bucket=" + (bucketName == null ? "<Unknown>" : bucketName) + "]";
	}

}
