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
package com.wl4g.devops.iam.configure;

import com.wl4g.devops.common.bean.erm.ClusterConfig;
import com.wl4g.devops.common.bean.iam.*;
import com.wl4g.devops.dao.erm.ClusterConfigDao;
import com.wl4g.devops.dao.iam.MenuDao;
import com.wl4g.devops.dao.iam.RoleDao;
import com.wl4g.devops.dao.iam.UserDao;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo.OrganizationInfo;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo.Parameter;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo.SimpleParameter;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo.SnsParameter;
import com.wl4g.devops.iam.common.subject.SimplePrincipalInfo;
import com.wl4g.devops.iam.service.GroupService;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.wl4g.devops.common.bean.BaseBean.DEFAULT_USER_ROOT;
import static com.wl4g.devops.components.tools.common.collection.Collections2.isEmptyArray;
import static com.wl4g.devops.components.tools.common.collection.Collections2.safeList;
import static com.wl4g.devops.iam.common.subject.IamPrincipalInfo.PrincipalOrganization;
import static java.lang.String.valueOf;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.equalsAny;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Standard IAM Security context handler
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月29日
 * @since
 */
@Service
public class StandardSecurityConfigurer implements ServerSecurityConfigurer {

	/**
	 * ProtoStuff can't serialize XXXDao created by MyBatis, it will throw a
	 * serialization exception. This field must be ignored. </br>
	 * The problem is method: {@link StandardSecurityConfigurer#getIamAccount()}
	 */
	@Autowired
	private transient ClusterConfigDao configDao;
	@Autowired
	private transient UserDao userDao;
	@Autowired
	private transient RoleDao roleDao;
	@Autowired
	private transient MenuDao menuDao;
	@Autowired
	private GroupService groupService;

	@Value("${spring.profiles.active}")
	private String active;

	@Override
	public String decorateAuthenticateSuccessUrl(String successUrl, AuthenticationToken token, Subject subject,
			ServletRequest request, ServletResponse response) {
		return successUrl;
	}

	@Override
	public String decorateAuthenticateFailureUrl(String loginUrl, AuthenticationToken token, Throwable ae, ServletRequest request,
			ServletResponse response) {
		return loginUrl;
	}

	@Override
	public ApplicationInfo getApplicationInfo(String appName) {
		List<ApplicationInfo> apps = safeList(findApplicationInfo(appName));
		return !isEmpty(apps) ? apps.get(0) : null;
	}

	@Override
	public List<ApplicationInfo> findApplicationInfo(String... appNames) {
		List<ApplicationInfo> appInfos = new ArrayList<>();
		if (isEmptyArray(appNames)) {
			return emptyList();
		}

		// Is IAM example demo.
		if (equalsAny("iam-example", appNames)) {
			ApplicationInfo appInfo = new ApplicationInfo("iam-example", "http://localhost:14041");
			appInfo.setIntranetBaseUri("http://localhost:14041/iam-example");
			appInfos.add(appInfo);
		} else { // Formal environment.
			List<ClusterConfig> ccs = configDao.getByAppNames(appNames, active, null);
			for (ClusterConfig cc : ccs) {
				ApplicationInfo app = new ApplicationInfo();
				app.setAppName(cc.getName());
				app.setExtranetBaseUri(cc.getExtranetBaseUri());
				app.setIntranetBaseUri(cc.getIntranetBaseUri());
				app.setViewExtranetBaseUri(cc.getViewExtranetBaseUri());
				app.setRemark(cc.getRemark());
				appInfos.add(app);
			}
		}

		//// --- For testing. ---
		//
		// if (equalsAny("scm-server", appNames)) {
		// ApplicationInfo appInfo = new ApplicationInfo("scm-server",
		// "http://localhost:14043");
		// appInfo.setIntranetBaseUri("http://localhost:14043/scm-server");
		// appInfoList.add(appInfo);
		// }
		// if (equalsAny("ci-server", appNames)) {
		// ApplicationInfo appInfo = new ApplicationInfo("ci-server",
		// "http://localhost:14046");
		// appInfo.setIntranetBaseUri("http://localhost:14046/ci-server");
		// appInfoList.add(appInfo);
		// }
		// if (equalsAny("umc-admin", appNames)) {
		// ApplicationInfo appInfo = new ApplicationInfo("umc-admin",
		// "http://localhost:14048");
		// appInfo.setIntranetBaseUri("http://localhost:14048/umc-admin");
		// appInfoList.add(appInfo);
		// }
		// if (equalsAny("share-admin", appNames)) {
		// ApplicationInfo appInfo = new ApplicationInfo("share-admin",
		// "http://localhost:14051");
		// appInfo.setIntranetBaseUri("http://localhost:14051/share-admin");
		// appInfoList.add(appInfo);
		// }
		// if (equalsAny("srm-admin", appNames)) {
		// ApplicationInfo appInfo = new ApplicationInfo("srm-admin",
		// "http://localhost:15050");
		// appInfo.setIntranetBaseUri("http://localhost:15050/srm-admin");
		// appInfoList.add(appInfo);
		// }
		//
		// http://localhost:14041 # iam-example
		// http://localhost:14043 # scm-server
		// http://localhost:14046 # ci-server
		// http://localhost:14048 # umc-manager
		// http://localhost:14050 # srm-manager
		// http://localhost:14051 # share-manager
		//

		return appInfos;
	}

	@Override
	public IamPrincipalInfo getIamAccount(Parameter parameter) {
		User user = null;

		// By SNS authorizing
		if (parameter instanceof SnsParameter) {
			SnsParameter snsParameter = (SnsParameter) parameter;
			user = userDao.selectByUnionIdOrOpenId(snsParameter.getUnionId(), snsParameter.getOpenId());
		}
		// By general account
		else if (parameter instanceof SimpleParameter) {
			SimpleParameter simpleParameter = (SimpleParameter) parameter;
			user = userDao.selectByUserName(simpleParameter.getPrincipal());
		}
		if (nonNull(user)) {
			// Sets user organizations.
			Set<Group> groupsSet = groupService.getGroupsSet(user);
			List<OrganizationInfo> oInfo = groupsSet.stream().map(o -> new OrganizationInfo(o.getOrganizationCode(),
					o.getParentCode(), o.getType(), o.getDisplayName(), o.getAreaId())).collect(toList());
			return new SimplePrincipalInfo(valueOf(user.getId()), user.getUserName(), user.getPassword(),
					getRoles(user.getUserName()), getPermissions(user.getUserName()), new PrincipalOrganization(oInfo));
		}
		return null;
	}

	@Override
	public boolean isApplicationAccessAuthorized(String principal, String application) {
		return true;
	}

	@Override
	public void bindSocialConnection(SocialConnectInfo social) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<SocialConnectInfo> findSocialConnections(String principal, String provider) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void unbindSocialConnection(SocialConnectInfo social) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Get current authenticate principal role codes.
	 *
	 * @param principal
	 * @return
	 */
	private String getRoles(String principal) {
		User user = userDao.selectByUserName(principal);
		// TODO cache
		List<Role> list;
		if (DEFAULT_USER_ROOT.equals(principal)) {
			list = roleDao.selectWithRoot(null, null);
		} else {
			list = roleDao.selectByUserId(user.getId());
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			Role role = list.get(i);
			if (i == list.size() - 1) {
				sb.append(role.getRoleCode());
			} else {
				sb.append(role.getRoleCode()).append(",");
			}
		}
		return sb.toString();
	}

	/**
	 * Get current authenticate principal permissions.
	 *
	 * @param principal
	 * @return
	 */
	private String getPermissions(String principal) {
		User user = userDao.selectByUserName(principal);
		// TODO cache
		List<Menu> list;
		if (DEFAULT_USER_ROOT.equals(principal)) {
			list = menuDao.selectWithRoot();
		} else {
			list = menuDao.selectByUserId(user.getId());
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			Menu menu = list.get(i);
			if (i == list.size() - 1) {
				sb.append(menu.getPermission());
			} else {
				sb.append(menu.getPermission()).append(",");
			}
		}
		return sb.toString();
	}

}