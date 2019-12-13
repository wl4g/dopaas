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
package com.wl4g.devops.iam.common.authz.permission;

import static java.util.Collections.emptyList;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.StringUtils;

/**
 * {@link GenericWildcardPermission}
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年12月13日
 * @since
 */
public class GenericWildcardPermission implements Permission, Serializable {
	private static final long serialVersionUID = 7804887804507086263L;
	protected static final String WILDCARD_TOKEN = "*";
	protected static final String PERMIT_DIVIDER_TOKEN = ",";
	protected static final String PERMIT_PART_DIVIDER_TOKEN = ":";

	private List<Set<String>> permitParts = emptyList();

	public GenericWildcardPermission(String wildcardString) {
		this(wildcardString, true);
	}

	public GenericWildcardPermission(String wildcardString, boolean caseSensitive) {
		initWildcardString(wildcardString, caseSensitive);
	}

	protected void initWildcardString(String wildcardString, boolean caseSensitive) {
		wildcardString = StringUtils.clean(wildcardString);
		if (wildcardString == null || wildcardString.isEmpty()) {
			throw new IllegalArgumentException(
					"Wildcard string cannot be null or empty. Make sure permission strings are properly formatted.");
		}
		if (!caseSensitive) {
			wildcardString = wildcardString.toLowerCase();
		}

		List<String> permits = CollectionUtils.asList(wildcardString.split(PERMIT_DIVIDER_TOKEN));
		permitParts = new ArrayList<Set<String>>();
		for (String permit : permits) {
			Set<String> permitPart = CollectionUtils.asSet(permit.split(PERMIT_PART_DIVIDER_TOKEN));
			if (permitPart.isEmpty()) {
				throw new IllegalArgumentException(
						"Wildcard string cannot contain parts with only dividers. Make sure permission strings are properly formatted.");
			}
			permitParts.add(permitPart);
		}

		if (permitParts.isEmpty()) {
			throw new IllegalArgumentException(
					"Wildcard string cannot contain only dividers. Make sure permission strings are properly formatted.");
		}
	}

	protected List<Set<String>> getPermitParts() {
		return permitParts;
	}

	/**
	 * Sets the pre-split String parts of this <code>WildcardPermission</code>.
	 * 
	 * @since 1.3.0
	 * @param parts
	 *            pre-split String parts.
	 */
	protected void setPermitParts(List<Set<String>> parts) {
		if (!isEmpty(parts)) {
			this.permitParts = parts;
		}
	}

	@Override
	public boolean implies(Permission p) {
		// By default only supports comparisons with other WildcardPermissions
		if (!(p instanceof GenericWildcardPermission)) {
			return false;
		}

		GenericWildcardPermission gwp = (GenericWildcardPermission) p;
		List<Set<String>> otherParts = gwp.getPermitParts();
		int i = 0;
		for (Set<String> otherPart : otherParts) {
			// If this permission has less parts than the other permission,
			// everything after the number of parts contained
			// in this permission is automatically implied, so return true
			if (i > getPermitParts().size() - 1) {
				return true;
			} else {
				Set<String> part = getPermitParts().get(i);
				if (!part.contains(WILDCARD_TOKEN) && !part.containsAll(otherPart)) {
					return false;
				}
				i++;
			}
		}

		// If this permission has more parts than the other parts, only imply it
		// if all of the other parts are wildcards
		for (; i < getPermitParts().size(); i++) {
			Set<String> part = getPermitParts().get(i);
			if (!part.contains(WILDCARD_TOKEN)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		for (Set<String> part : permitParts) {
			if (buffer.length() > 0) {
				buffer.append(PERMIT_PART_DIVIDER_TOKEN);
			}
			Iterator<String> partIt = part.iterator();
			while (partIt.hasNext()) {
				buffer.append(partIt.next());
				if (partIt.hasNext()) {
					buffer.append(PERMIT_DIVIDER_TOKEN);
				}
			}
		}
		return buffer.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof GenericWildcardPermission) {
			GenericWildcardPermission wp = (GenericWildcardPermission) o;
			return permitParts.equals(wp.permitParts);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return permitParts.hashCode();
	}

}
