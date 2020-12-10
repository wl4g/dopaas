package com.wl4g.devops.common.bean.doc;

import java.util.List;

/**
 * @author vjay
 * @date 2020-12-10 17:10:00
 */
public class ApisInfo {

    private List<EnterpriseOas3Api> enterpriseOas3Apis;
    private List<EnterpriseOas3Components> enterpriseOas3Components;
    private List<EnterpriseOas3Tags> enterpriseOas3Tags;

    public List<EnterpriseOas3Api> getEnterpriseOas3Apis() {
        return enterpriseOas3Apis;
    }

    public void setEnterpriseOas3Apis(List<EnterpriseOas3Api> enterpriseOas3Apis) {
        this.enterpriseOas3Apis = enterpriseOas3Apis;
    }

    public List<EnterpriseOas3Components> getEnterpriseOas3Components() {
        return enterpriseOas3Components;
    }

    public void setEnterpriseOas3Components(List<EnterpriseOas3Components> enterpriseOas3Components) {
        this.enterpriseOas3Components = enterpriseOas3Components;
    }

    public List<EnterpriseOas3Tags> getEnterpriseOas3Tags() {
        return enterpriseOas3Tags;
    }

    public void setEnterpriseOas3Tags(List<EnterpriseOas3Tags> enterpriseOas3Tags) {
        this.enterpriseOas3Tags = enterpriseOas3Tags;
    }
}
