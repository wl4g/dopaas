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
package com.wl4g.devops.iam.common.utils;

import org.apache.commons.lang3.StringUtils;

import com.wl4g.devops.components.tools.common.codec.Base58;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.devops.common.utils.web.WebUtils3.*;
import static com.wl4g.devops.components.tools.common.collection.Collections2.safeList;
import static com.wl4g.devops.components.tools.common.lang.Assert2.notEmptyOf;
import static com.wl4g.devops.components.tools.common.lang.Assert2.notNull;
import static com.wl4g.devops.iam.common.subject.IamPrincipalInfo.OrganizationInfo;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * {@link IamOrganizationHolder}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @author vjay
 * @version v1.0 2020年5月20日
 * @since
 */
public abstract class IamOrganizationHolder extends IamSecurityHolder {

	/**
	 * Gets organizations from Session
	 *
	 * @return
	 */
	public static List<OrganizationInfo> getSessionOrganizations() {
		return safeList(getPrincipalInfo().getOrganization().getOrganizations());
	}

	/**
	 * Gets session organization all tree.
	 *
	 * @return
	 */
	public static List<OrganizationInfoTree> getOrganizationTrees() {
		List<OrganizationInfo> organs = getSessionOrganizations();

		List<OrganizationInfoTree> trees = new ArrayList<>();
		List<OrganizationInfo> parentOrgans = getParentOrganizations(organs);
		for (OrganizationInfo parent : parentOrgans) {
			OrganizationInfoTree tree = new OrganizationInfoTree(parent);
			addChildrenOrganizations(organs, tree);
			trees.add(tree);
		}

		return trees;
	}

	/**
	 * Gets organization codes by current request.
	 *
	 * @return
	 */
	public static List<String> getRequestOrganizationCodes() {
		String organCode = getRequestParameter(PARAM_ORGANIZATION_CODE);
		organCode = new String(Base58.decodeBase58(organCode), UTF_8);
		if (isBlank(organCode) || "ALL".equalsIgnoreCase(organCode)) {
			List<OrganizationInfo> organs = getSessionOrganizations();
			return organs.stream().map(a -> a.getOrganizationCode()).collect(toList());
		} else {
			return getChildOrganizationCodes(organCode);
		}
	}

	/**
	 * Gets organization code by current request.
	 *
	 * @return
	 */
	public static String getRequestOrganizationCode() {
		String organCode = getRequestParameter(PARAM_ORGANIZATION_CODE);
		organCode = new String(Base58.decodeBase58(organCode), UTF_8);

		if (isBlank(organCode) || "ALL".equalsIgnoreCase(organCode)) {
			List<OrganizationInfo> organs = getSessionOrganizations();
			List<OrganizationInfo> parentOrgans = getParentOrganizations(organs);

			notEmptyOf(parentOrgans, "organizationCode");
			return parentOrgans.get(0).getOrganizationCode();
		} else {
			return organCode;
		}
	}

	/**
	 * Gets child organization codes by organ code
	 *
	 * @param organCode
	 * @return
	 */
	private static List<String> getChildOrganizationCodes(String organCode) {
		List<OrganizationInfo> organs;
		if (isBlank(organCode)) {
			organs = getSessionOrganizations();
		} else {
			organs = getChildOrganizations(organCode);
		}
		return safeList(organs).stream().map(o -> o.getOrganizationCode()).collect(toList());
	}

	/**
	 * Gets child organizations by code
	 *
	 * @param organCode
	 * @return
	 */
	private static List<OrganizationInfo> getChildOrganizations(String organCode) {
		List<OrganizationInfo> organs = getSessionOrganizations();

		List<OrganizationInfo> childrens = new ArrayList<>();
		addChildrenOrganizations(organs, organCode, childrens);

		OrganizationInfo organ = extOrganization(organs, organCode);
		notNull(organ, "Not found organization code: %s", organCode);

		childrens.add(organ);
		return childrens;
	}

	/**
	 * Extract organization info by orgainzation code.
	 * 
	 * @param organs
	 * @param organCode
	 * @return
	 */
	private static OrganizationInfo extOrganization(List<OrganizationInfo> organs, String organCode) {
		Optional<OrganizationInfo> opt = safeList(organs).stream()
				.filter(o -> StringUtils.equals(o.getOrganizationCode(), organCode)).findFirst();
		return opt.get();
	}

	/**
	 * Adds children organizations.
	 * 
	 * @param organs
	 * @param organCode
	 * @param childrens
	 */
	private static void addChildrenOrganizations(List<OrganizationInfo> organs, String organCode,
			List<OrganizationInfo> childrens) {
		for (OrganizationInfo organ : organs) {
			String _organCode = organ.getOrganizationCode();
			String parent = organ.getParent();
			if (StringUtils.equals(parent, organCode)) {
				childrens.add(organ);
				addChildrenOrganizations(organs, _organCode, childrens);
			}
		}
	}

	/**
	 * Adds children organizations
	 * 
	 * @param organs
	 * @param tree
	 */
	private static void addChildrenOrganizations(List<OrganizationInfo> organs, OrganizationInfoTree tree) {
		for (OrganizationInfo o : organs) {
			if (StringUtils.equals(tree.getOrganizationCode(), o.getParent())) {
				OrganizationInfoTree childTree = new OrganizationInfoTree(o);
				tree.getChildren().add(childTree);
				addChildrenOrganizations(organs, childTree);
			}
		}
	}

	/**
	 * Gets parent organizations
	 * 
	 * @param organs
	 * @return
	 */
	private static List<OrganizationInfo> getParentOrganizations(List<OrganizationInfo> organs) {
		List<OrganizationInfo> parentOrgans = new ArrayList<>();
		for (OrganizationInfo o : organs) {
			// Find parent organization
			Optional<OrganizationInfo> opt = organs.stream()
					.filter(p -> StringUtils.equals(p.getOrganizationCode(), o.getParent())).findAny();
			if (!opt.isPresent()) {
				parentOrgans.add(o);
			}
		}
		return parentOrgans;
	}

	/**
	 * {@link OrganizationInfoTree}
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020年5月25日
	 * @since
	 */
	public static class OrganizationInfoTree extends OrganizationInfo {
		private static final long serialVersionUID = 7353905956153984552L;

		private List<OrganizationInfoTree> children = new ArrayList<>();

		public OrganizationInfoTree(OrganizationInfo organ) {
			super(organ.getOrganizationCode(), organ.getParent(), organ.getType(), organ.getName(), organ.getAreaId());
		}

		public List<OrganizationInfoTree> getChildren() {
			return children;
		}

		public void setChildren(List<OrganizationInfoTree> children) {
			this.children = children;
		}
	}

	/**
	 * Request parameter organization code.
	 */
	final private static String PARAM_ORGANIZATION_CODE = "organization_code";

}