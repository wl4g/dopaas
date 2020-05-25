package com.wl4g.devops.iam.common.utils;

import com.wl4g.devops.common.utils.web.WebUtils3;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.wl4g.devops.iam.common.subject.IamPrincipalInfo.OrganizationInfo;
import static com.wl4g.devops.iam.common.subject.IamPrincipalInfo.PrincipalOrganization;
import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.getPrincipalInfo;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * @author vjay
 * @date 2020-05-20 11:18:00
 */
public class IamOrganizationUtils {

    final private static String CURRENT_ORGANIZATION_CODE = "organization_code";


    /**
     * Get Organization Tree for show  -- complite
     *
     * @return
     */
    public static List<OrganizationInfoTree> getOrganizationTree() {
        List<OrganizationInfo> organizations = getOrganizationFromSession();
        List<OrganizationInfo> tops = getTop(organizations);
        List<OrganizationInfoTree> organizationInfoTrees = new ArrayList<>();
        for (OrganizationInfo top : tops) {
            OrganizationInfoTree organizationInfoTree = new OrganizationInfoTree(top);
            getChildTree(organizations, organizationInfoTree);
            organizationInfoTrees.add(organizationInfoTree);
        }
        return organizationInfoTrees;
    }

    /**
     * Get Organization Codes By current Code
     *
     * @return
     */
    public static List<String> getCurrentOrganizationCodes() {
        String code = WebUtils3.getRequestParameter(CURRENT_ORGANIZATION_CODE);
        if (StringUtils.isBlank(code) || StringUtils.equals("all", code)) {
            List<OrganizationInfo> organizationFromSession = getOrganizationFromSession();
            List<String> list = new ArrayList<>();
            organizationFromSession.forEach((a) -> {
                list.add(a.getOrganizationCode());
            });
            return list;
        } else {
            return getOrganizationCodesByCode(code);
        }
    }

    /**
     * Get Organizations from Session
     *
     * @return
     */
    public static List<OrganizationInfo> getOrganizationFromSession() {
        IamPrincipalInfo principalInfo = getPrincipalInfo();
        PrincipalOrganization organization = principalInfo.getOrganization();
        if (Objects.nonNull(organization)) {
            return organization.getOrganizations();
        }
        return Collections.emptyList();
    }


    /**
     * Get Organization Codes By current Code
     *
     * @param code
     * @return
     */
    private static List<OrganizationInfo> getOrganizationsByCode(String code) {
        List<OrganizationInfo> organizations = getOrganizationFromSession();
        List<OrganizationInfo> children = new ArrayList<>();
        getChilds(organizations, code, children);
        OrganizationInfo organization = getOrganizationByCode(organizations, code);
        children.add(organization);
        return children;
    }

    /**
     * Get Organization Codes By current Code
     *
     * @param code
     * @return
     */
    private static List<String> getOrganizationCodesByCode(String code) {
        List<OrganizationInfo> organizationsByCode;
        if (isBlank(code)) {
            organizationsByCode = getOrganizationFromSession();
        } else {
            organizationsByCode = getOrganizationsByCode(code);
        }

        List<String> codes = new ArrayList<>();
        for (OrganizationInfo organizationInfo : organizationsByCode) {
            codes.add(organizationInfo.getOrganizationCode());
        }
        return codes;
    }

    private static OrganizationInfo getOrganizationByCode(List<OrganizationInfo> organizations, String code) {
        for (OrganizationInfo organizationInfo : organizations) {
            String organizationCode = organizationInfo.getOrganizationCode();
            if (StringUtils.equals(organizationCode, code)) {
                return organizationInfo;
            }
        }
        return null;
    }

    private static void getChilds(List<OrganizationInfo> organizations, String code, List<OrganizationInfo> children) {
        for (OrganizationInfo organizationInfo : organizations) {
            String organizationCode = organizationInfo.getOrganizationCode();
            String parent = organizationInfo.getParent();
            if (StringUtils.equals(parent, code)) {
                children.add(organizationInfo);
                getChilds(organizations, organizationCode, children);
            }
        }
    }

    private static void getChildTree(List<OrganizationInfo> organizations, OrganizationInfoTree organizationInfoTree) {
        for (OrganizationInfo organizationInfo : organizations) {
            if (StringUtils.equals(organizationInfoTree.getOrganizationCode(), organizationInfo.getParent())) {
                OrganizationInfoTree childTree = new OrganizationInfoTree(organizationInfo);
                organizationInfoTree.getChildren().add(childTree);
                getChildTree(organizations, childTree);
            }
        }
    }

    private static List<OrganizationInfo> getTop(List<OrganizationInfo> organizationInfos) {
        List<OrganizationInfo> top = new ArrayList<>();
        for (OrganizationInfo organizationInfo : organizationInfos) {
            boolean hasParent = false;
            for (OrganizationInfo organizationInfoParent : organizationInfos) {
                if (StringUtils.equals(organizationInfoParent.getOrganizationCode(), organizationInfo.getParent())) {
                    hasParent = true;
                    break;
                }
            }
            if (!hasParent) {
                top.add(organizationInfo);
            }
        }
        return top;
    }

    public static class OrganizationInfoTree extends OrganizationInfo {

        private List<OrganizationInfoTree> children = new ArrayList<>();

        public OrganizationInfoTree(OrganizationInfo organizationInfo) {
            super(organizationInfo.getOrganizationCode(), organizationInfo.getParent(), organizationInfo.getType(),organizationInfo.getName());
        }

        public List<OrganizationInfoTree> getChildren() {
            return children;
        }

        public void setChildren(List<OrganizationInfoTree> children) {
            this.children = children;
        }
    }


}
