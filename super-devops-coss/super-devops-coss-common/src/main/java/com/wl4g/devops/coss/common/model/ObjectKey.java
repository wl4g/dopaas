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
package com.wl4g.devops.coss.common.model;

import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.wl4g.devops.coss.common.model.ObjectKey;

/**
 * Names a file or directory in a {@link FileSystem}. Path strings use slash as
 * the directory separator. A path string is absolute if it begins with a slash.
 */
public class ObjectKey implements Comparable<ObjectKey> {

	/** The directory separator, a slash. */
	final public static String SEPARATOR = "/";
	final public static char SEPARATOR_CHAR = '/';
	final public static String CUR_DIR = ".";

	/**
	 * Pre-compiled regular expressions to detect path formats.
	 */
	final private static Pattern hasDriveLetterSpecifier = Pattern.compile("^/?[a-zA-Z]:");

	/**
	 * Hierarchical uri path.
	 */
	private URI uri;

	/** Resolve a child path against a parent path. */
	public ObjectKey(String parent, String child) {
		this(new ObjectKey(parent), new ObjectKey(child));
	}

	/** Resolve a child path against a parent path. */
	public ObjectKey(ObjectKey parent, String child) {
		this(parent, new ObjectKey(child));
	}

	/** Resolve a child path against a parent path. */
	public ObjectKey(String parent, ObjectKey child) {
		this(new ObjectKey(parent), child);
	}

	/** Resolve a child path against a parent path. */
	public ObjectKey(ObjectKey parent, ObjectKey child) {
		// Add a slash to parent's path so resolution is compatible with URI's
		URI parentUri = parent.uri;
		String parentPath = parentUri.getPath();
		if (!(parentPath.equals("/") || parentPath.isEmpty())) {
			try {
				parentUri = new URI(parentUri.getScheme(), parentUri.getAuthority(), parentUri.getPath() + "/", null,
						parentUri.getFragment());
			} catch (URISyntaxException e) {
				throw new IllegalArgumentException(e);
			}
		}
		URI resolved = parentUri.resolve(child.uri);
		initialize(resolved.getScheme(), resolved.getAuthority(), resolved.getPath(), resolved.getFragment());
	}

	/**
	 * Construct a path from a String. Path strings are URIs, but with unescaped
	 * elements and some additional normalization.
	 */
	public ObjectKey(String pathString) throws IllegalArgumentException {
		checkPathArg(pathString);

		// We can't use 'new URI(String)' directly, since it assumes things are
		// escaped, which we don't require of Paths.

		// add a slash in front of paths with Windows drive letters
		if (hasWindowsDrive(pathString) && pathString.charAt(0) != '/') {
			pathString = "/" + pathString;
		}

		// parse uri components
		String scheme = null;
		String authority = null;

		int start = 0;

		// parse uri scheme, if any
		int colon = pathString.indexOf(':');
		int slash = pathString.indexOf('/');
		if ((colon != -1) && ((slash == -1) || (colon < slash))) { // has a
																	// scheme
			scheme = pathString.substring(0, colon);
			start = colon + 1;
		}

		// parse uri authority, if any
		if (pathString.startsWith("//", start) && (pathString.length() - start > 2)) { // has
																						// authority
			int nextSlash = pathString.indexOf('/', start + 2);
			int authEnd = nextSlash > 0 ? nextSlash : pathString.length();
			authority = pathString.substring(start + 2, authEnd);
			start = authEnd;
		}

		// uri path is the rest of the string -- query & fragment not supported
		String path = pathString.substring(start, pathString.length());

		initialize(scheme, authority, path, null);
	}

	/**
	 * Construct a path from a URI
	 */
	public ObjectKey(URI aUri) {
		uri = aUri.normalize();
	}

	/** Construct a Path from components. */
	public ObjectKey(String scheme, String authority, String path) {
		checkPathArg(path);

		// add a slash in front of paths with Windows drive letters
		if (hasWindowsDrive(path) && path.charAt(0) != '/') {
			path = "/" + path;
		}

		// add "./" in front of Linux relative paths so that a path containing
		// a colon e.q. "a:b" will not be interpreted as scheme "a".
		if (!IS_OS_WINDOWS && path.charAt(0) != '/') {
			path = "./" + path;
		}

		initialize(scheme, authority, path, null);
	}

	private void initialize(String scheme, String authority, String path, String fragment) {
		try {
			this.uri = new URI(scheme, authority, normalizePath(scheme, path), null, fragment).normalize();
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private void checkPathArg(String path) throws IllegalArgumentException {
		// disallow construction of a Path from an empty string
		if (path == null) {
			throw new IllegalArgumentException("Can not create a Path from a null string");
		}
		if (path.length() == 0) {
			throw new IllegalArgumentException("Can not create a Path from an empty string");
		}
	}

	/**
	 * Pathnames with scheme and relative path are illegal.
	 * 
	 * @param path
	 *            to be checked
	 */
	public void checkNotSchemeWithRelative() {
		if (toUri().isAbsolute() && !isUriPathAbsolute()) {
			throw new IllegalArgumentException("Unsupported name: has scheme but relative path-part");
		}
	}

	public void checkNotRelative() {
		if (!isAbsolute() && toUri().getScheme() == null) {
			throw new IllegalArgumentException("Path is relative");
		}
	}

	/**
	 * Is an absolute path (ie a slash relative path part) AND a scheme is null
	 * AND authority is null.
	 */
	public boolean isAbsoluteAndSchemeAuthorityNull() {
		return (isUriPathAbsolute() && uri.getScheme() == null && uri.getAuthority() == null);
	}

	/**
	 * True if the path component (i.e. directory) of this URI is absolute.
	 */
	public boolean isUriPathAbsolute() {
		int start = startPositionWithoutWindowsDrive(uri.getPath());
		return uri.getPath().startsWith(SEPARATOR, start);
	}

	/** True if the path component of this URI is absolute. */
	/**
	 * There is some ambiguity here. An absolute path is a slash relative name
	 * without a scheme or an authority. So either this method was incorrectly
	 * named or its implementation is incorrect. This method returns true even
	 * if there is a scheme and authority.
	 */
	public boolean isAbsolute() {
		return isUriPathAbsolute();
	}

	/**
	 * @return true if and only if this path represents the root of a file
	 *         system
	 */
	public boolean isRoot() {
		return getParent() == null;
	}

	/** Returns the final component of this path. */
	public String getName() {
		String path = uri.getPath();
		int slash = path.lastIndexOf(SEPARATOR);
		return path.substring(slash + 1);
	}

	/** Returns the parent of a path or null if at root. */
	public ObjectKey getParent() {
		String path = uri.getPath();
		int lastSlash = path.lastIndexOf('/');
		int start = startPositionWithoutWindowsDrive(path);
		if ((path.length() == start) || // empty path
				(lastSlash == start && path.length() == start + 1)) { // at root
			return null;
		}
		String parent;
		if (lastSlash == -1) {
			parent = CUR_DIR;
		} else {
			parent = path.substring(0, lastSlash == start ? start + 1 : lastSlash);
		}
		return new ObjectKey(uri.getScheme(), uri.getAuthority(), parent);
	}

	/** Adds a suffix to the final name in the path. */
	public ObjectKey suffix(String suffix) {
		return new ObjectKey(getParent(), getName() + suffix);
	}

	/** Convert this to a URI. */
	public URI toUri() {
		return uri;
	}

	@Override
	public String toString() {
		// we can't use uri.toString(), which escapes everything, because we
		// want
		// illegal characters unescaped in the string, for glob processing, etc.
		StringBuilder buffer = new StringBuilder();
		if (uri.getScheme() != null) {
			buffer.append(uri.getScheme());
			buffer.append(":");
		}
		if (uri.getAuthority() != null) {
			buffer.append("//");
			buffer.append(uri.getAuthority());
		}
		if (uri.getPath() != null) {
			String path = uri.getPath();
			if (path.indexOf('/') == 0 && hasWindowsDrive(path) && // has
																	// windows
																	// drive
					uri.getScheme() == null && // but no scheme
					uri.getAuthority() == null) // or authority
				path = path.substring(1); // remove slash before drive
			buffer.append(path);
		}
		if (uri.getFragment() != null) {
			buffer.append("#");
			buffer.append(uri.getFragment());
		}
		return buffer.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ObjectKey)) {
			return false;
		}
		ObjectKey that = (ObjectKey) o;
		return this.uri.equals(that.uri);
	}

	@Override
	public int hashCode() {
		return uri.hashCode();
	}

	@Override
	public int compareTo(ObjectKey that) {
		return this.uri.compareTo(that.uri);
	}

	/** Return the number of elements in this path. */
	public int depth() {
		String path = uri.getPath();
		int depth = 0;
		int slash = path.length() == 1 && path.charAt(0) == '/' ? -1 : 0;
		while (slash != -1) {
			depth++;
			slash = path.indexOf(SEPARATOR, slash + 1);
		}
		return depth;
	}

	/** Returns a qualified path object. */
	public ObjectKey makeQualified(URI defaultUri, ObjectKey workingDir) {
		ObjectKey path = this;
		if (!isAbsolute()) {
			path = new ObjectKey(workingDir, this);
		}

		URI pathUri = path.toUri();

		String scheme = pathUri.getScheme();
		String authority = pathUri.getAuthority();
		String fragment = pathUri.getFragment();

		if (scheme != null && (authority != null || defaultUri.getAuthority() == null))
			return path;

		if (scheme == null) {
			scheme = defaultUri.getScheme();
		}

		if (authority == null) {
			authority = defaultUri.getAuthority();
			if (authority == null) {
				authority = "";
			}
		}

		URI newUri = null;
		try {
			newUri = new URI(scheme, authority, normalizePath(scheme, pathUri.getPath()), null, fragment);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
		return new ObjectKey(newUri);
	}

	public static ObjectKey getPathWithoutSchemeAndAuthority(ObjectKey path) {
		// This code depends on Path.toString() to remove the leading slash
		// before
		// the drive specification on Windows.
		ObjectKey newPath = path.isUriPathAbsolute() ? new ObjectKey(null, null, path.toUri().getPath()) : path;
		return newPath;
	}

	/**
	 * Merge 2 paths such that the second path is appended relative to the
	 * first. The returned path has the scheme and authority of the first path.
	 * On Windows, the drive specification in the second path is discarded.
	 * 
	 * @param path1
	 *            Path first path
	 * @param path2
	 *            Path second path, to be appended relative to path1
	 * @return Path merged path
	 */
	public static ObjectKey mergePaths(ObjectKey path1, ObjectKey path2) {
		String path2Str = path2.toUri().getPath();
		path2Str = path2Str.substring(startPositionWithoutWindowsDrive(path2Str));
		// Add path components explicitly, because simply concatenating two path
		// string is not safe, for example:
		// "/" + "/foo" yields "//foo", which will be parsed as authority in
		// Path
		return new ObjectKey(path1.toUri().getScheme(), path1.toUri().getAuthority(), path1.toUri().getPath() + path2Str);
	}

	/**
	 * Normalize a path string to use non-duplicated forward slashes as the path
	 * separator and remove any trailing path separators.
	 * 
	 * @param scheme
	 *            Supplies the URI scheme. Used to deduce whether we should
	 *            replace backslashes or not.
	 * @param path
	 *            Supplies the scheme-specific part
	 * @return Normalized path string.
	 */
	private static String normalizePath(String scheme, String path) {
		// Remove double forward slashes.
		path = StringUtils.replace(path, "//", "/");

		// Remove backslashes if this looks like a Windows path. Avoid
		// the substitution if it looks like a non-local URI.
		if (IS_OS_WINDOWS && (hasWindowsDrive(path) || (scheme == null) || (scheme.isEmpty()) || (scheme.equals("file")))) {
			path = StringUtils.replace(path, "\\", "/");
		}

		// trim trailing slash from non-root path (ignoring windows drive)
		int minLength = startPositionWithoutWindowsDrive(path) + 1;
		if (path.length() > minLength && path.endsWith(SEPARATOR)) {
			path = path.substring(0, path.length() - 1);
		}

		return path;
	}

	private static boolean hasWindowsDrive(String path) {
		return (IS_OS_WINDOWS && hasDriveLetterSpecifier.matcher(path).find());
	}

	private static int startPositionWithoutWindowsDrive(String path) {
		if (hasWindowsDrive(path)) {
			return path.charAt(0) == SEPARATOR_CHAR ? 3 : 2;
		} else {
			return 0;
		}
	}

	/**
	 * Determine whether a given path string represents an absolute path on
	 * Windows. e.g. "C:/a/b" is an absolute path. "C:a/b" is not.
	 *
	 * @param pathString
	 *            Supplies the path string to evaluate.
	 * @param slashed
	 *            true if the given path is prefixed with "/".
	 * @return true if the supplied path looks like an absolute path with a
	 *         Windows drive-specifier.
	 */
	public static boolean isWindowsAbsolutePath(final String pathString, final boolean slashed) {
		int start = startPositionWithoutWindowsDrive(pathString);
		return start > 0 && pathString.length() > start
				&& ((pathString.charAt(start) == SEPARATOR_CHAR) || (pathString.charAt(start) == '\\'));
	}

}