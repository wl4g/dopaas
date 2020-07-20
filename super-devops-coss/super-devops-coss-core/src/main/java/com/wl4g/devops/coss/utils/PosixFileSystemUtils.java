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
package com.wl4g.devops.coss.utils;

import static com.wl4g.devops.components.tools.common.lang.TypeConverts.*;
import static java.lang.String.format;
import static java.lang.String.valueOf;

import com.wl4g.devops.coss.common.model.ACL;

/**
 * {@link PosixFileSystemUtils}
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月13日
 * @since
 */
public class PosixFileSystemUtils {

	/**
	 * {@link Acl} to POSIX style permission
	 * 
	 * @param acl
	 * @return
	 */
	final public static int toPosixPermission(ACL acl) {
		if (ACL.Private == acl) {
			return ACL_PRIVATE_POSIX;
		} else if (ACL.PublicRead == acl) {
			return ACL_READ_POSIX;
		} else if (ACL.PublicReadWrite == acl) {
			return ACL_READ_WRITE_POSIX;
		}
		throw new Error(format("Should not be wrong ah, unsupported acl: %s", acl));
	}

	/**
	 * POSIX style permission to {@link Acl}
	 * 
	 * @param posixUser
	 * @param posixGroup
	 * @param posixOther
	 * @return
	 */
	final public static int toPosixPermission(int posixUser, int posixGroup, int posixOther) {
		return parseIntOrDefault(valueOf(posixUser) + valueOf(posixGroup) + valueOf(posixOther));
	}

	/**
	 * POSIX style permission to {@link Acl}
	 * 
	 * @param fp
	 * @return
	 */
	final public static ACL toPosixAcl(int posixPermission) {
		int u = posixPermission / 100;
		int g = posixPermission % 100 / 10;
		int o = posixPermission % 10;

		if (u == 7 && g == 0 && o == 0) {
			return ACL.Private;
		} else if (u == 7 && g == 5 && o == 5) {
			return ACL.PublicRead;
		} else if (u == 7 && g == 7 && o == 7) {
			return ACL.PublicReadWrite;
		}

		throw new Error(
				format("Unsupported POSIX permission: %s, is the system set by the outside world to change the permissions?",
						posixPermission));
	}

	/**
	 * Put write default bytes buffer size.
	 */
	final public static int DEFAULT_WRITE_BUFFER = 8 * 1024;

	final public static int ACL_PRIVATE_POSIX = 700;
	final public static int ACL_READ_POSIX = 755;
	final public static int ACL_READ_WRITE_POSIX = 777;

}