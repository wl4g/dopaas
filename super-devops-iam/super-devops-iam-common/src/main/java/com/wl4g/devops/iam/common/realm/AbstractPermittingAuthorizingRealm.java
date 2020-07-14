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
package com.wl4g.devops.iam.common.realm;

import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.StringUtils;
import org.slf4j.Logger;

import com.wl4g.devops.iam.common.subject.IamPrincipalInfo;

import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;
import static org.apache.shiro.util.Assert.notNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract permission authorizing realm.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-11-23
 * @since
 */
public abstract class AbstractPermittingAuthorizingRealm extends AuthorizingRealm {
	final protected Logger log = getLogger(getClass());

	final public static String KEY_ROLES_ATTRIBUTE_NAME = "rolesAttribute";
	final public static String KEY_PERMITS_ATTRIBUTE_NAME = "permissionsAttribute";

	/**
	 * New create and merge {@link IamPrincipalInfo} to
	 * {@link PrincipalCollection}
	 * 
	 * @param info
	 * @return
	 */
	protected PrincipalCollection createPermitPrincipalCollection(IamPrincipalInfo info) {
		return createPermitPrincipalCollection(info.getPrincipal(), info);
	}

	/**
	 * New create and merge {@link IamPrincipalInfo} to
	 * {@link PrincipalCollection}
	 * 
	 * @param principal
	 * @param info
	 * @return
	 */
	protected PrincipalCollection createPermitPrincipalCollection(String principal, IamPrincipalInfo info) {
		notNull(principal, "Principal can't null");
		notNull(info, "IamPrincipalInfo can't null");

		// Authenticate attributes.(roles/permissions/rememberMe)
		Map<String, Object> principalMap = new HashMap<>(info.attributes());
		principalMap.put(KEY_ROLES_ATTRIBUTE_NAME, info.getRoles());
		principalMap.put(KEY_PERMITS_ATTRIBUTE_NAME, info.getPermissions());

		// Create simple-authentication info
		List<Object> principals = CollectionUtils.asList(principal, principalMap);
		return new SimplePrincipalCollection(principals, getName());
	}

	/**
	 * Setup merge authorized roles and permission string.
	 * 
	 * @param authzInfo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected SimpleAuthorizationInfo mergeAuthorizedString(PrincipalCollection principals, SimpleAuthorizationInfo authzInfo) {
		// Retrieve principal account info.
		SimplePrincipalCollection principals0 = (SimplePrincipalCollection) principals;
		Map<String, String> principalMap = (Map<String, String>) principals0.asList().get(1);

		// Principal roles.
		String roles = principalMap.get(KEY_ROLES_ATTRIBUTE_NAME);
		mergeRoles(authzInfo, splitPermitString(roles));

		// Principal permissions.
		String permissions = principalMap.get(KEY_PERMITS_ATTRIBUTE_NAME);
		return mergePermissions(authzInfo, splitPermitString(permissions));
	}

	/**
	 * Split a string into a list of not empty and trimmed strings, delimiter is
	 * a comma.
	 * 
	 * @param s
	 *            the input string
	 * @return the list of not empty and trimmed strings
	 */
	protected List<String> splitPermitString(String s) {
		List<String> list = new ArrayList<String>();
		String[] elements = StringUtils.split(s, ',');
		if (elements != null && elements.length > 0) {
			for (String element : elements) {
				if (StringUtils.hasText(element)) {
					list.add(element.trim());
				}
			}
		}
		return list;
	}

	/**
	 * Add merge roles to the simple authorization info.
	 * 
	 * @param authzInfo
	 * @param roles
	 *            the list of roles to add
	 * @return
	 */
	protected SimpleAuthorizationInfo mergeRoles(SimpleAuthorizationInfo authzInfo, List<String> roles) {
		for (String role : roles) {
			authzInfo.addRole(role);
		}
		return authzInfo;
	}

	/**
	 * Add merge permissions to the simple authorization info.
	 * 
	 * @param authzInfo
	 * @param permissions
	 *            the list of permissions to add
	 * @return
	 */
	protected SimpleAuthorizationInfo mergePermissions(SimpleAuthorizationInfo authzInfo, List<String> permissions) {
		for (String permission : permissions) {
			authzInfo.addStringPermission(permission);
		}
		return authzInfo;
	}

}