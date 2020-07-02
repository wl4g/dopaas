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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

/**
 * Abstract base class for resources which resolve URLs into File references,
 * such as {@link UrlStreamResource} or {@link ClassPathStreamResource}.
 *
 * <p>
 * Detects the "file" protocol as well as the JBoss "vfs" protocol in URLs,
 * resolving file system references accordingly.
 *
 * @author Juergen Hoeller
 * @since 3.0
 */
abstract class AbstractFileResolvingResource extends AbstractStreamResource {

	/**
	 * This implementation returns a File reference for the underlying class
	 * path resource, provided that it refers to a file in the file system.
	 * 
	 * @see com.wl4g.devops.tool.common.resource.resolver.ResourceUtils2.util.ResourceUtils#getFile(java.net.URL,
	 *      String)
	 */
	@Override
	public File getFile() throws IOException {
		URL url = getURL();
		if (url.getProtocol().startsWith(ResourceUtils2.URL_PROTOCOL_VFS)) {
			return VfsResourceDelegate.getResource(url).getFile();
		}
		return ResourceUtils2.getFile(url, getDescription());
	}

	/**
	 * This implementation determines the underlying File (or jar file, in case
	 * of a resource in a jar/zip).
	 */
	@Override
	protected File getFileForLastModifiedCheck() throws IOException {
		URL url = getURL();
		if (ResourceUtils2.isJarURL(url)) {
			URL actualUrl = ResourceUtils2.extractArchiveURL(url);
			if (actualUrl.getProtocol().startsWith(ResourceUtils2.URL_PROTOCOL_VFS)) {
				return VfsResourceDelegate.getResource(actualUrl).getFile();
			}
			return ResourceUtils2.getFile(actualUrl, "Jar URL");
		} else {
			return getFile();
		}
	}

	/**
	 * This implementation returns a File reference for the given URI-identified
	 * resource, provided that it refers to a file in the file system.
	 * 
	 * @see com.wl4g.devops.tool.common.resource.resolver.ResourceUtils2.util.ResourceUtils#getFile(java.net.URI,
	 *      String)
	 */
	protected File getFile(URI uri) throws IOException {
		if (uri.getScheme().startsWith(ResourceUtils2.URL_PROTOCOL_VFS)) {
			return VfsResourceDelegate.getResource(uri).getFile();
		}
		return ResourceUtils2.getFile(uri, getDescription());
	}

	@Override
	public boolean exists() {
		try {
			URL url = getURL();
			if (ResourceUtils2.isFileURL(url)) {
				// Proceed with file system resolution
				return getFile().exists();
			} else {
				// Try a URL connection content-length header
				URLConnection con = url.openConnection();
				customizeConnection(con);
				HttpURLConnection httpCon = (con instanceof HttpURLConnection ? (HttpURLConnection) con : null);
				if (httpCon != null) {
					int code = httpCon.getResponseCode();
					if (code == HttpURLConnection.HTTP_OK) {
						return true;
					} else if (code == HttpURLConnection.HTTP_NOT_FOUND) {
						return false;
					}
				}
				if (con.getContentLength() >= 0) {
					return true;
				}
				if (httpCon != null) {
					// no HTTP OK status, and no content-length header: give up
					httpCon.disconnect();
					return false;
				} else {
					// Fall back to stream existence: can we open the stream?
					InputStream is = getInputStream();
					is.close();
					return true;
				}
			}
		} catch (IOException ex) {
			return false;
		}
	}

	@Override
	public boolean isReadable() {
		try {
			URL url = getURL();
			if (ResourceUtils2.isFileURL(url)) {
				// Proceed with file system resolution
				File file = getFile();
				return (file.canRead() && !file.isDirectory());
			} else {
				return true;
			}
		} catch (IOException ex) {
			return false;
		}
	}

	@Override
	public long contentLength() throws IOException {
		URL url = getURL();
		if (ResourceUtils2.isFileURL(url)) {
			// Proceed with file system resolution
			return getFile().length();
		} else {
			// Try a URL connection content-length header
			URLConnection con = url.openConnection();
			customizeConnection(con);
			return con.getContentLength();
		}
	}

	@Override
	public long lastModified() throws IOException {
		URL url = getURL();
		if (ResourceUtils2.isFileURL(url) || ResourceUtils2.isJarURL(url)) {
			// Proceed with file system resolution
			try {
				return super.lastModified();
			} catch (FileNotFoundException ex) {
				// Defensively fall back to URL connection check instead
			}
		}
		// Try a URL connection last-modified header
		URLConnection con = url.openConnection();
		customizeConnection(con);
		return con.getLastModified();
	}

	/**
	 * Customize the given {@link URLConnection}, obtained in the course of an
	 * {@link #exists()}, {@link #contentLength()} or {@link #lastModified()}
	 * call.
	 * <p>
	 * Calls {@link ResourceUtils2#useCachesIfNecessary(URLConnection)} and
	 * delegates to {@link #customizeConnection(HttpURLConnection)} if possible.
	 * Can be overridden in subclasses.
	 * 
	 * @param con
	 *            the URLConnection to customize
	 * @throws IOException
	 *             if thrown from URLConnection methods
	 */
	protected void customizeConnection(URLConnection con) throws IOException {
		ResourceUtils2.useCachesIfNecessary(con);
		if (con instanceof HttpURLConnection) {
			customizeConnection((HttpURLConnection) con);
		}
	}

	/**
	 * Customize the given {@link HttpURLConnection}, obtained in the course of
	 * an {@link #exists()}, {@link #contentLength()} or {@link #lastModified()}
	 * call.
	 * <p>
	 * Sets request method "HEAD" by default. Can be overridden in subclasses.
	 * 
	 * @param con
	 *            the HttpURLConnection to customize
	 * @throws IOException
	 *             if thrown from HttpURLConnection methods
	 */
	protected void customizeConnection(HttpURLConnection con) throws IOException {
		con.setRequestMethod("HEAD");
	}

	/**
	 * Inner delegate class, avoiding a hard JBoss VFS API dependency at
	 * runtime.
	 */
	private static class VfsResourceDelegate {

		public static StreamResource getResource(URL url) throws IOException {
			return new VfsStreamResource(VfsUtils2.getRoot(url));
		}

		public static StreamResource getResource(URI uri) throws IOException {
			return new VfsStreamResource(VfsUtils2.getRoot(uri));
		}
	}

}