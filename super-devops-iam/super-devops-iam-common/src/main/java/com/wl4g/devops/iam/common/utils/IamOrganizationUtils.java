package com.wl4g.devops.iam.common.utils;

import com.wl4g.devops.iam.common.subject.IamPrincipalInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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

    final public static String CURRENT_ORGANIZATION_CODE = "CURRENT_ORGANIZATION_CODE_";

    /**
     * Get Organization Codes By current Code
     * @param code
     * @return
     */
    public static List<OrganizationInfo> getOrganizationsByCode(String code) {
        List<OrganizationInfo> organizations = getOrganizationFromSession();
        List<OrganizationInfo> children = new ArrayList<>();
        getChilds(organizations, code, children);
        OrganizationInfo organization = getOrganizationByCode(organizations, code);
        children.add(organization);
        return children;
    }

    /**
     * Get Organization Codes By current Code
     * @param code
     * @return
     */
    public static List<String> getOrganizationCodesByCode(String code) {
        List<OrganizationInfo> organizationsByCode;
        if(isBlank(code)){
            organizationsByCode = getOrganizationFromSession();
        }else{
            organizationsByCode = getOrganizationsByCode(code);
        }

        List<String> codes = new ArrayList<>();
        for(OrganizationInfo organizationInfo : organizationsByCode){
            codes.add(organizationInfo.getOrganizationCode());
        }
        return codes;
    }

    /**
     * Get Organization Codes By current Code
     * @param code
     * @return
     */
    public static List<String> getCurrentOrganizationCodes() {
        return getOrganizationCodesByCode((getCurrentOrganizationInfo()));
    }

    /**
     * Get Organization Tree for show
     * @return
     */
    public static List<OrganizationInfoTree> getOrganizationTree() {
        List<OrganizationInfo> organizations = getOrganizationFromSession();
        List<OrganizationInfo> tops = getTop(organizations);
        List<OrganizationInfoTree> organizationInfoTrees = new ArrayList<>();
        for(OrganizationInfo top : tops){
            OrganizationInfoTree organizationInfoTree = new OrganizationInfoTree(top);
            getChildTree(organizations,organizationInfoTree);
            organizationInfoTrees.add(organizationInfoTree);
        }
        return organizationInfoTrees;
    }

    public static String getCurrentOrganizationInfo(){
        //TODO 后续要使用注册中心的方式来实现跨模块的数据同步问题
        //return IamSecurityHolder.getBindValue(CURRENT_ORGANIZATION_CODE);

        //TODO just for now
        List<OrganizationInfo> top = getTop(getOrganizationFromSession());
        if(!CollectionUtils.isEmpty(top)){
            return top.get(0).getOrganizationCode();
        }
        return null;
    }

    public static void changeCurrentOrganizationInfo(String currentOrganizationCode){
        //TODO 后续要使用注册中心的方式来实现跨模块的数据同步问题
        //IamSecurityHolder.bind(CURRENT_ORGANIZATION_CODE,currentOrganizationCode);
    }

    public static void setDefaultCurrentOrganization(){
        List<OrganizationInfo> top = getTop(getOrganizationFromSession());
        String currentOrganizationInfo = getCurrentOrganizationInfo();
        if(!CollectionUtils.isEmpty(top) && isBlank(currentOrganizationInfo)){
            changeCurrentOrganizationInfo(top.get(0).getOrganizationCode());
        }
    }

    public static List<OrganizationInfo> getOrganizationFromSession() {
        IamPrincipalInfo principalInfo = getPrincipalInfo();
        PrincipalOrganization organization = principalInfo.getOrganization();
        if(Objects.nonNull(organization)){
            return organization.getOrganizations();
        }
        return null;
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

    private static void getChildTree(List<OrganizationInfo> organizations, OrganizationInfoTree organizationInfoTree){
        for(OrganizationInfo organizationInfo : organizations){
            if(StringUtils.equals(organizationInfoTree.getOrganizationCode(),organizationInfo.getParent())){
                OrganizationInfoTree childTree = new OrganizationInfoTree(organizationInfo);
                organizationInfoTree.getChildren().add(childTree);
                getChildTree(organizations,childTree);
            }
        }

    }

    private static List<OrganizationInfo> getTop(List<OrganizationInfo> organizationInfos){
        List<OrganizationInfo> top = new ArrayList<>();
        for(OrganizationInfo organizationInfo : organizationInfos){
            boolean hasParent = false;
            for(OrganizationInfo organizationInfoParent : organizationInfos){
                if(StringUtils.equals(organizationInfoParent.getOrganizationCode(),organizationInfo.getParent())){
                    hasParent = true;
                    break;
                }
            }
            if(!hasParent){
                top.add(organizationInfo);
            }
        }
        return top;
    }

    public static class OrganizationInfoTree extends OrganizationInfo{

        List<OrganizationInfoTree> children = new ArrayList<>();

        public OrganizationInfoTree(OrganizationInfo organizationInfo) {
            super(organizationInfo.getOrganizationCode(),organizationInfo.getParent());
        }

        public List<OrganizationInfoTree> getChildren() {
            return children;
        }

        public void setChildren(List<OrganizationInfoTree> children) {
            this.children = children;
        }
    }


}
