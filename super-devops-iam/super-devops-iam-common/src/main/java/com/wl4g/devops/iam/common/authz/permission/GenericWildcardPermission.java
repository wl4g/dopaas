///*
// * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.wl4g.devops.iam.common.authz.permission;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Set;
//
//import org.apache.shiro.authz.Permission;
//import org.apache.shiro.authz.permission.DomainPermission;
//import org.apache.shiro.authz.permission.WildcardPermission;
//import org.apache.shiro.util.CollectionUtils;
//import org.apache.shiro.util.StringUtils;
//
//public class GenericWildcardPermission implements Permission, Serializable {
//	private static final long serialVersionUID = 7804887804507086263L;
//
//	/*--------------------------------------------
//	|             C O N S T A N T S             |
//	============================================*/
//	protected static final String WILDCARD_TOKEN = "*";
//	protected static final String PART_DIVIDER_TOKEN = ":";
//	protected static final String SUBPART_DIVIDER_TOKEN = ",";
//	protected static final boolean DEFAULT_CASE_SENSITIVE = false;
//
//	/*--------------------------------------------
//	|    I N S T A N C E   V A R I A B L E S    |
//	============================================*/
//	private List<Set<String>> parts;
//
//	/*--------------------------------------------
//	|         C O N S T R U C T O R S           |
//	============================================*/
//	/**
//	 * Default no-arg constructor for subclasses only - end-user developers
//	 * instantiating Permission instances must provide a wildcard string at a
//	 * minimum, since Permission instances are immutable once instantiated.
//	 * <p/>
//	 * Note that the WildcardPermission class is very robust and typically
//	 * subclasses are not necessary unless you wish to create type-safe
//	 * Permission objects that would be used in your application, such as
//	 * perhaps a {@code UserPermission}, {@code SystemPermission},
//	 * {@code PrinterPermission}, etc. If you want such type-safe permission
//	 * usage, consider subclassing the {@link DomainPermission DomainPermission}
//	 * class for your needs.
//	 */
//	protected GenericWildcardPermission() {
//	}
//
//	public GenericWildcardPermission(String wildcardString) {
//		this(wildcardString, DEFAULT_CASE_SENSITIVE);
//	}
//
//	public GenericWildcardPermission(String wildcardString, boolean caseSensitive) {
//		setParts(wildcardString, caseSensitive);
//	}
//
//	protected void setParts(String wildcardString) {
//		setParts(wildcardString, DEFAULT_CASE_SENSITIVE);
//	}
//
//	protected void setParts(String wildcardString, boolean caseSensitive) {
//		wildcardString = StringUtils.clean(wildcardString);
//
//		if (wildcardString == null || wildcardString.isEmpty()) {
//			throw new IllegalArgumentException(
//					"Wildcard string cannot be null or empty. Make sure permission strings are properly formatted.");
//		}
//
//		if (!caseSensitive) {
//			wildcardString = wildcardString.toLowerCase();
//		}
//
//		List<String> parts = CollectionUtils.asList(wildcardString.split(PART_DIVIDER_TOKEN));
//
//		this.parts = new ArrayList<Set<String>>();
//		for (String part : parts) {
//			Set<String> subparts = CollectionUtils.asSet(part.split(SUBPART_DIVIDER_TOKEN));
//
//			if (subparts.isEmpty()) {
//				throw new IllegalArgumentException(
//						"Wildcard string cannot contain parts with only dividers. Make sure permission strings are properly formatted.");
//			}
//			this.parts.add(subparts);
//		}
//
//		if (this.parts.isEmpty()) {
//			throw new IllegalArgumentException(
//					"Wildcard string cannot contain only dividers. Make sure permission strings are properly formatted.");
//		}
//	}
//
//	/*--------------------------------------------
//	|  A C C E S S O R S / M O D I F I E R S    |
//	============================================*/
//	protected List<Set<String>> getParts() {
//		return this.parts;
//	}
//
//	/**
//	 * Sets the pre-split String parts of this <code>WildcardPermission</code>.
//	 * 
//	 * @since 1.3.0
//	 * @param parts
//	 *            pre-split String parts.
//	 */
//	protected void setParts(List<Set<String>> parts) {
//		this.parts = parts;
//	}
//
//	/*--------------------------------------------
//	|               M E T H O D S               |
//	============================================*/
//
//	public boolean implies(Permission p) {
//		// By default only supports comparisons with other WildcardPermissions
//		if (!(p instanceof WildcardPermission)) {
//			return false;
//		}
//
//		WildcardPermission wp = (WildcardPermission) p;
//
//		List<Set<String>> otherParts = wp.getParts();
//
//		int i = 0;
//		for (Set<String> otherPart : otherParts) {
//			// If this permission has less parts than the other permission,
//			// everything after the number of parts contained
//			// in this permission is automatically implied, so return true
//			if (getParts().size() - 1 < i) {
//				return true;
//			} else {
//				Set<String> part = getParts().get(i);
//				if (!part.contains(WILDCARD_TOKEN) && !part.containsAll(otherPart)) {
//					return false;
//				}
//				i++;
//			}
//		}
//
//		// If this permission has more parts than the other parts, only imply it
//		// if all of the other parts are wildcards
//		for (; i < getParts().size(); i++) {
//			Set<String> part = getParts().get(i);
//			if (!part.contains(WILDCARD_TOKEN)) {
//				return false;
//			}
//		}
//
//		return true;
//	}
//
//	public String toString() {
//		StringBuilder buffer = new StringBuilder();
//		for (Set<String> part : parts) {
//			if (buffer.length() > 0) {
//				buffer.append(PART_DIVIDER_TOKEN);
//			}
//			Iterator<String> partIt = part.iterator();
//			while (partIt.hasNext()) {
//				buffer.append(partIt.next());
//				if (partIt.hasNext()) {
//					buffer.append(SUBPART_DIVIDER_TOKEN);
//				}
//			}
//		}
//		return buffer.toString();
//	}
//
//	public boolean equals(Object o) {
//		if (o instanceof WildcardPermission) {
//			WildcardPermission wp = (WildcardPermission) o;
//			return parts.equals(wp.parts);
//		}
//		return false;
//	}
//
//	public int hashCode() {
//		return parts.hashCode();
//	}
//
//}
