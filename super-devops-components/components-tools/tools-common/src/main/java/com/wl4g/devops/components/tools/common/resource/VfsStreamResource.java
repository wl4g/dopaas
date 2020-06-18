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

import com.wl4g.devops.components.tools.common.lang.Assert2;

/**
 * JBoss VFS based {@link StreamResource} implementation.
 *
 * <p>
 * As of Spring 4.0, this class supports VFS 3.x on JBoss AS 6+ (package
 * {@code org.jboss.vfs}) and is in particular compatible with JBoss AS 7 and
 * WildFly 8.
 *
 * @author Ales Justin
 * @author Juergen Hoeller
 * @author Costin Leau
 * @author Sam Brannen
 * @since 3.0
 * @see org.jboss.vfs.VirtualFile
 */
public class VfsStreamResource extends AbstractStreamResource {

	private final Object resource;

	public VfsStreamResource(Object resource) {
		Assert2.notNull(resource, "VirtualFile must not be null");
		this.resource = resource;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return VfsUtils2.getInputStream(this.resource);
	}

	@Override
	public boolean exists() {
		return VfsUtils2.exists(this.resource);
	}

	@Override
	public boolean isReadable() {
		return VfsUtils2.isReadable(this.resource);
	}

	@Override
	public URL getURL() throws IOException {
		try {
			return VfsUtils2.getURL(this.resource);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Failed to obtain URL for file " + this.resource, ex);
		}
	}

	@Override
	public URI getURI() throws IOException {
		try {
			return VfsUtils2.getURI(this.resource);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Failed to obtain URI for " + this.resource, ex);
		}
	}

	@Override
	public File getFile() throws IOException {
		return VfsUtils2.getFile(this.resource);
	}

	@Override
	public long contentLength() throws IOException {
		return VfsUtils2.getSize(this.resource);
	}

	@Override
	public long lastModified() throws IOException {
		return VfsUtils2.getLastModified(this.resource);
	}

	@Override
	public StreamResource createRelative(String relativePath) throws IOException {
		if (!relativePath.startsWith(".") && relativePath.contains("/")) {
			try {
				return new VfsStreamResource(VfsUtils2.getChild(this.resource, relativePath));
			} catch (IOException ex) {
				// fall back to getRelative
			}
		}

		return new VfsStreamResource(VfsUtils2.getRelative(new URL(getURL(), relativePath)));
	}

	@Override
	public String getFilename() {
		return VfsUtils2.getName(this.resource);
	}

	@Override
	public String getDescription() {
		return "VFS resource [" + this.resource + "]";
	}

	@Override
	public boolean equals(Object obj) {
		return (obj == this || (obj instanceof VfsStreamResource && this.resource.equals(((VfsStreamResource) obj).resource)));
	}

	@Override
	public int hashCode() {
		return this.resource.hashCode();
	}

}