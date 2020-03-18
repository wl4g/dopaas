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

import org.apache.shiro.authz.Permission;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.wl4g.devops.iam.common.authz.permission.EnhancedWildcardPermission.PermitValidityUtil.checkWildcard;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * {@link EnhancedWildcardPermission}
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年12月13日
 * @since
 */
public class EnhancedWildcardPermission implements Permission, Serializable {
	private static final long serialVersionUID = 7804887804507086263L;
	protected static final String WILDCARD_TOKEN = "*";
	protected static final String DOUBLE_WILDCARD_TOKEN = "**";
	protected static final String PERMIT_DIVIDER_TOKEN = ",";
	protected static final String PERMIT_PART_DIVIDER_TOKEN = ":";

	private List<Set<String>> permitParts = emptyList();

	public EnhancedWildcardPermission(String wildcardString) {
		this(wildcardString, true);
	}

	public EnhancedWildcardPermission(String wildcardString, boolean caseSensitive) {
		initWildcardString(wildcardString, caseSensitive);
	}

	protected void initWildcardString(String wildcardString, boolean caseSensitive) {
		wildcardString = StringUtils.clean(wildcardString);
		if (isBlank(wildcardString)) {
			throw new IllegalArgumentException(
					"Wildcard string cannot be null or empty. Make sure permission strings are properly formatted.");
		}
		checkWildcard(wildcardString);
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
		if (!(p instanceof EnhancedWildcardPermission)) {
			return false;
		}
		EnhancedWildcardPermission gwp = (EnhancedWildcardPermission) p;
		// e.g: @RequiresPermissions(value="ci", "ci:list")
		List<Set<String>> defines = gwp.getPermitParts();
		// e.g: Get the login-user permission info from the db "ci,ci:list"
		List<Set<String>> owns = getPermitParts();
		for (Set<String> defineSet : defines) { // must all true
			boolean match = false;
			for (Set<String> ownSet : owns) {// one true
				if (doMatch(defineSet, ownSet)) {
					match = true;
					break;
				}
			}
			if (!match) { // not one match
				return false;
			}
		}
		return true;
	}

	/**
	 * <pre>
	 *  e.g define(ci:list)     own(ci:list)   => true
	 *  e.g define(ci)          own(ci:list)   => true
	 *  e.g define(ci:list)     own(ci)        => false
	 *  e.g define(ci:*)        own(ci:list)   => true
	 * </pre>
	 * 
	 * @param defineSet
	 * @param ownSet
	 * @return
	 */
	private boolean doMatch(Set<String> defineSet, Set<String> ownSet) {
		if (defineSet.size() <= 0) {
			return true;
		}
		Iterator<String> define = defineSet.iterator();
		Iterator<String> own = ownSet.iterator();
		while (define.hasNext() && own.hasNext()) {
			String defineStr = define.next();
			String ownStr = own.next();
			if (defineStr.equals(WILDCARD_TOKEN)) {
				continue;
			} else if (defineStr.equals(DOUBLE_WILDCARD_TOKEN)) {
				return true;
			} else if (!defineStr.equals(ownStr)) {
				return false;
			}
		}
		if (define.hasNext() || own.hasNext()) {
			return false;
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
		if (o instanceof EnhancedWildcardPermission) {
			EnhancedWildcardPermission wp = (EnhancedWildcardPermission) o;
			return permitParts.equals(wp.permitParts);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return permitParts.hashCode();
	}

	/**
	 * Permission wildcard validity utils.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年12月20日
	 * @since
	 */
	public final static class PermitValidityUtil {

		/**
		 * Check validation permission wildcard string
		 * 
		 * @param wildcardString
		 * @return
		 */
		public static void checkWildcard(String wildcardString) {
			// DOUBLE_WILDCARD_TOKEN must at Last
			if (wildcardString.contains(DOUBLE_WILDCARD_TOKEN)
					&& wildcardString.indexOf(DOUBLE_WILDCARD_TOKEN) != wildcardString.length() - 2) {
				throw new IllegalArgumentException(String.format("Illegal permit wildcard string '%s', %s must as last!",
						DOUBLE_WILDCARD_TOKEN, wildcardString));
			}
			// just suppost one WILDCARD_TOKEN
			else if (!wildcardString.contains(DOUBLE_WILDCARD_TOKEN)
					&& wildcardString.indexOf(WILDCARD_TOKEN) != wildcardString.lastIndexOf(WILDCARD_TOKEN)) {
				throw new IllegalArgumentException(String.format("Illegal permit wildcard string '%s', just suppost one %s!",
						wildcardString, WILDCARD_TOKEN));
			}
			String[] wildcard = wildcardString.split(PERMIT_PART_DIVIDER_TOKEN);
			for (String wildcardPart : wildcard) {
				if (!wildcardPart.equals(WILDCARD_TOKEN) && !wildcardPart.equals(DOUBLE_WILDCARD_TOKEN)
						&& wildcardPart.contains(WILDCARD_TOKEN)) {
					throw new IllegalArgumentException(String.format(
							"Illegal permit wildcard string '%s', Wildcard '%s' is not allowed in '%s' separator part character!  For example: 'iam:user:*' or 'iam:*:edit'",
							wildcardString, WILDCARD_TOKEN, PERMIT_PART_DIVIDER_TOKEN));
				}
			}
		}

	}

	public static void main(String[] args) {
		String str = "asdf**";
		System.out.println(str.indexOf(DOUBLE_WILDCARD_TOKEN));
		System.out.println(str.length() - 2);
	}

}