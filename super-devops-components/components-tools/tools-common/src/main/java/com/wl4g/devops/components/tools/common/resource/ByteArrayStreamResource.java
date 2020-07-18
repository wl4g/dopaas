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
package com.wl4g.devops.components.tools.common.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.wl4g.devops.components.tools.common.lang.Assert2;

/**
 * {@link Resource} implementation for a given byte array.
 * <p>
 * Creates a {@link ByteArrayInputStream} for the given byte array.
 *
 * <p>
 * Useful for loading content from any given byte array, without having to
 * resort to a single-use {@link InputStreamResource}. Particularly useful for
 * creating mail attachments from local content, where JavaMail needs to be able
 * to read the stream multiple times.
 *
 * @see java.io.ByteArrayInputStream
 * @see InputStreamResource
 */
public class ByteArrayStreamResource extends AbstractStreamResource {

	final private byte[] byteArray;
	final private String description;

	/**
	 * Create a new {@code ByteArrayStreamResource}.
	 * 
	 * @param byteArray
	 *            the byte array to wrap
	 */
	public ByteArrayStreamResource(byte[] byteArray) {
		this(byteArray, "resource loaded from byte array");
	}

	/**
	 * Create a new {@code ByteArrayStreamResource} with a description.
	 * 
	 * @param byteArray
	 *            the byte array to wrap
	 * @param description
	 *            where the byte array comes from
	 */
	public ByteArrayStreamResource(byte[] byteArray, String description) {
		Assert2.notNull(byteArray, "Byte array must not be null");
		this.byteArray = byteArray;
		this.description = (description != null ? description : "");
	}

	/**
	 * Return the underlying byte array.
	 */
	public final byte[] getByteArray() {
		return this.byteArray;
	}

	/**
	 * This implementation always returns {@code true}.
	 */
	@Override
	public boolean exists() {
		return true;
	}

	/**
	 * This implementation returns the length of the underlying byte array.
	 */
	@Override
	public long contentLength() {
		return this.byteArray.length;
	}

	/**
	 * This implementation returns a ByteArrayInputStream for the underlying
	 * byte array.
	 * 
	 * @see java.io.ByteArrayInputStream
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(this.byteArray);
	}

	/**
	 * This implementation returns a description that includes the passed-in
	 * {@code description}, if any.
	 */
	@Override
	public String getDescription() {
		return "Byte array resource [" + this.description + "]";
	}

	/**
	 * This implementation compares the underlying byte array.
	 * 
	 * @see java.util.Arrays#equals(byte[], byte[])
	 */
	@Override
	public boolean equals(Object obj) {
		return (obj == this || (obj instanceof ByteArrayStreamResource
				&& Arrays.equals(((ByteArrayStreamResource) obj).byteArray, this.byteArray)));
	}

	/**
	 * This implementation returns the hash code based on the underlying byte
	 * array.
	 */
	@Override
	public int hashCode() {
		return (byte[].class.hashCode() * 29 * this.byteArray.length);
	}

}