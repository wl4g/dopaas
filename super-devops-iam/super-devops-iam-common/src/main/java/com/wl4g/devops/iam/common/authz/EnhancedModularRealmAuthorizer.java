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
package com.wl4g.devops.iam.common.authz;

import com.wl4g.devops.iam.common.authz.permission.EnhancedWildcardPermissionResovler;

import java.util.Collection;

import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.realm.Realm;

//import org.apache.shiro.authz.Authorizer;
//import org.apache.shiro.realm.Realm;
//import org.apache.shiro.subject.PrincipalCollection;
//import static java.util.Objects.nonNull;

/**
 * Enhanced strict modular realm authorizer.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年12月13日
 * @since
 */
public class EnhancedModularRealmAuthorizer extends ModularRealmAuthorizer {

	public EnhancedModularRealmAuthorizer(Collection<Realm> realms) {
		super(realms);
		setPermissionResolver(new EnhancedWildcardPermissionResovler());
	}

	/**
	 * successfully Only after all realms are certified can they be considered
	 * as passed.
	 */
	// @Override
	// public boolean isPermitted(PrincipalCollection principals, String
	// permission) {
	// assertRealmsConfigured();
	// for (Realm realm : getRealms()) {
	// if (!(realm instanceof Authorizer)) {
	// continue;
	// }
	// if (!((Authorizer) realm).isPermitted(principals, permission)) {
	// return (permitted = false);
	// }
	// }
	// return !getRealms().isEmpty();
	// }

}