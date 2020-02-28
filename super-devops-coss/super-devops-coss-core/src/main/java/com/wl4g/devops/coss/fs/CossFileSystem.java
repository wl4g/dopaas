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
package com.wl4g.devops.coss.fs;

import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.ClosedFileSystemException;
import java.nio.file.FileStore;

import org.slf4j.Logger;

/**
 * Composite object storage server file system API.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年2月28日
 * @since
 */
public abstract class CossFileSystem implements Closeable {

	final protected Logger log = getLogger(getClass());

	/**
	 * Returns the name separator, represented as a string.
	 *
	 * <p>
	 * The name separator is used to separate names in a path string. An
	 * implementation may support multiple name separators in which case this
	 * method returns an implementation specific <em>default</em> name
	 * separator. This separator is used when creating path strings by invoking
	 * the {@link Path#toString() toString()} method.
	 *
	 * <p>
	 * In the case of the default provider, this method returns the same
	 * separator as {@link java.io.File#separator}.
	 *
	 * @return The name separator
	 */
	public String getSeparator() {
		return "/";
	}

	/**
	 * Tells whether or not this file system allows only read-only access to its
	 * file stores.
	 *
	 * @return {@code true} if, and only if, this file system provides read-only
	 *         access
	 */
	public boolean isReadOnly() {
		return false;
	}

	/**
	 * Closes this file system.
	 *
	 * <p>
	 * After a file system is closed then all subsequent access to the file
	 * system, either by methods defined by this class or on objects associated
	 * with this file system, throw {@link ClosedFileSystemException}. If the
	 * file system is already closed then invoking this method has no effect.
	 * <p>
	 *
	 * @throws IOException
	 *             If an I/O error occurs
	 * @throws UnsupportedOperationException
	 *             Thrown in the case of the default file system
	 */
	@Override
	public abstract void close() throws IOException;

	/**
	 * Tells whether or not this file system is open.
	 *
	 * <p>
	 * File systems created by the default provider are always open.
	 *
	 * @return {@code true} if, and only if, this file system is open
	 */
	public abstract boolean isOpen();

	/**
	 * Returns an object to iterate over the paths of the root directories.
	 *
	 * <p>
	 * A file system provides access to a file store that may be composed of a
	 * number of distinct file hierarchies, each with its own top-level root
	 * directory. Unless denied by the security manager, each element in the
	 * returned iterator corresponds to the root directory of a distinct file
	 * hierarchy. The order of the elements is not defined. The file hierarchies
	 * may change during the lifetime of the Java virtual machine. For example,
	 * in some implementations, the insertion of removable media may result in
	 * the creation of a new file hierarchy with its own top-level directory.
	 *
	 * <p>
	 * 
	 * @return An object to iterate over the root directories
	 */
	public abstract Iterable<Path> getRootDirectories();

	/**
	 * Returns an object to iterate over the underlying file stores.
	 *
	 * <p>
	 * The elements of the returned iterator are the {@link FileStore
	 * FileStores} for this file system. The order of the elements is not
	 * defined and the file stores may change during the lifetime of the Java
	 * virtual machine. When an I/O error occurs, perhaps because a file store
	 * is not accessible, then it is not returned by the iterator.
	 * </p>
	 * <b>Usage Example:</b> Suppose we want to print the space usage for all
	 * file stores:
	 * 
	 * <pre>
	 * for (FileStore store : FileSystems.getDefault().getFileStores()) {
	 * 	long total = store.getTotalSpace() / 1024;
	 * 	long used = (store.getTotalSpace() - store.getUnallocatedSpace()) / 1024;
	 * 	long avail = store.getUsableSpace() / 1024;
	 * 	System.out.format("%-20s %12d %12d %12d%n", store, total, used, avail);
	 * }
	 * </pre>
	 *
	 * @return An object to iterate over the backing file stores
	 */
	public abstract Iterable<FileStatus> getFileStatusIter();

	/**
	 * Return a file status object that represents the path.
	 * 
	 * @param f
	 *            The path we want information from
	 * @return a FileStatus object
	 * @throws FileNotFoundException
	 *             when the path does not exist; IOException see specific
	 *             implementation
	 */
	public abstract FileStatus getFileStatus(Path f) throws IOException;

	/**
	 * Set permission of a path.
	 * 
	 * @param p
	 * @param permission
	 */
	public abstract void setPermission(Path p, FsPermission permission) throws IOException;

	/**
	 * Set owner of a path (i.e. a file or a directory). The parameters username
	 * and groupname cannot both be null.
	 * 
	 * @param p
	 *            The path
	 * @param username
	 *            If it is null, the original username remains unchanged.
	 * @param groupname
	 *            If it is null, the original groupname remains unchanged.
	 */
	public abstract void setOwner(Path p, String username, String groupname) throws IOException;

	/**
	 * Set access time of a file
	 * 
	 * @param p
	 *            The path
	 * @param mtime
	 *            Set the modification time of this file. The number of
	 *            milliseconds since Jan 1, 1970. A value of -1 means that this
	 *            call should not set modification time.
	 * @param atime
	 *            Set the access time of this file. The number of milliseconds
	 *            since Jan 1, 1970. A value of -1 means that this call should
	 *            not set access time.
	 */
	public abstract void setTimes(Path p, long mtime, long atime) throws IOException;

}
