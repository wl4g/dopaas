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

/**
 * Retention of upstream license agreement statement:</br>
 * Thank you very much spring framework, We fully comply with and support the open license
 * agreement of spring. The purpose of migration is to solve the problem
 * that these elegant API programs can still be easily used without running
 * in the spring environment.
 * </br>
 * Copyright 2002-2017 the original author or authors.
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * Interface for a resource descriptor that abstracts from the actual type of
 * underlying resource, such as a file or class path resource.
 *
 * <p>
 * An InputStream can be opened for every resource if it exists in physical
 * form, but a URL or File handle can just be returned for certain resources.
 * The actual behavior is implementation-specific.
 *
 * @author Juergen Hoeller
 * @since 28.12.2003
 * @see #getInputStream()
 * @see #getURL()
 * @see #getURI()
 * @see #getFile()
 * @see WritableStreamResource
 * @see ContextResource
 * @see UrlStreamResource
 * @see ClassPathStreamResource
 * @see FileStreamResource
 * @see PathResource
 * @see ByteArrayResource
 * @see StreamResource
 */
public interface StreamResource {

	/**
	 * Return an {@link InputStream} for the content of an underlying resource.
	 * <p>
	 * It is expected that each call creates a <i>fresh</i> stream.
	 * <p>
	 * This requirement is particularly important when you consider an API such
	 * as JavaMail, which needs to be able to read the stream multiple times
	 * when creating mail attachments. For such a use case, it is
	 * <i>required</i> that each {@code getInputStream()} call returns a fresh
	 * stream.
	 * 
	 * @return the input stream for the underlying resource (must not be
	 *         {@code null})
	 * @throws java.io.FileNotFoundException
	 *             if the underlying resource doesn't exist
	 * @throws IOException
	 *             if the content stream could not be opened
	 */
	InputStream getInputStream() throws IOException;

	/**
	 * Determine whether this resource actually exists in physical form.
	 * <p>
	 * This method performs a definitive existence check, whereas the existence
	 * of a {@code Resource} handle only guarantees a valid descriptor handle.
	 */
	boolean exists();

	/**
	 * Indicate whether the contents of this resource can be read via
	 * {@link #getInputStream()}.
	 * <p>
	 * Will be {@code true} for typical resource descriptors; note that actual
	 * content reading may still fail when attempted. However, a value of
	 * {@code false} is a definitive indication that the resource content cannot
	 * be read.
	 * 
	 * @see #getInputStream()
	 */
	boolean isReadable();

	/**
	 * Indicate whether this resource represents a handle with an open stream.
	 * If {@code true}, the InputStream cannot be read multiple times, and must
	 * be read and closed to avoid resource leaks.
	 * <p>
	 * Will be {@code false} for typical resource descriptors.
	 */
	boolean isOpen();

	/**
	 * Return a URL handle for this resource.
	 * 
	 * @throws IOException
	 *             if the resource cannot be resolved as URL, i.e. if the
	 *             resource is not available as descriptor
	 */
	URL getURL() throws IOException;

	/**
	 * Return a URI handle for this resource.
	 * 
	 * @throws IOException
	 *             if the resource cannot be resolved as URI, i.e. if the
	 *             resource is not available as descriptor
	 * @since 2.5
	 */
	URI getURI() throws IOException;

	/**
	 * Return a File handle for this resource.
	 * 
	 * @throws java.io.FileNotFoundException
	 *             if the resource cannot be resolved as absolute file path,
	 *             i.e. if the resource is not available in a file system
	 * @throws IOException
	 *             in case of general resolution/reading failures
	 * @see #getInputStream()
	 */
	File getFile() throws IOException;

	/**
	 * Determine the content length for this resource.
	 * 
	 * @throws IOException
	 *             if the resource cannot be resolved (in the file system or as
	 *             some other known physical resource type)
	 */
	long contentLength() throws IOException;

	/**
	 * Determine the last-modified timestamp for this resource.
	 * 
	 * @throws IOException
	 *             if the resource cannot be resolved (in the file system or as
	 *             some other known physical resource type)
	 */
	long lastModified() throws IOException;

	/**
	 * Create a resource relative to this resource.
	 * 
	 * @param relativePath
	 *            the relative path (relative to this resource)
	 * @return the resource handle for the relative resource
	 * @throws IOException
	 *             if the relative resource cannot be determined
	 */
	StreamResource createRelative(String relativePath) throws IOException;

	/**
	 * Determine a filename for this resource, i.e. typically the last part of
	 * the path: for example, "myfile.txt".
	 * <p>
	 * Returns {@code null} if this type of resource does not have a filename.
	 */
	String getFilename();

	/**
	 * Return a description for this resource, to be used for error output when
	 * working with the resource.
	 * <p>
	 * Implementations are also encouraged to return this value from their
	 * {@code toString} method.
	 * 
	 * @see Object#toString()
	 */
	String getDescription();

}