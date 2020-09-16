package com.wl4g.devops.dts.codegen.bean;

import com.wl4g.components.core.bean.BaseBean;
import com.wl4g.devops.dts.codegen.web.model.ProviderConfigOption;

import java.util.List;

public class GenProject extends BaseBean {
    private static final long serialVersionUID = 6815608076300843748L;

    private String projectName;

    private String companyName;

    private String tplCategory;

    private String packageName;

    private String version;

    private String author;

    private String since;

    private String copyright;

    private List<GenTable> genTables;

    private String providerConfigOptions;

    private List<ProviderConfigOption> providerConfigOptionList;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName == null ? null : projectName.trim();
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author == null ? null : author.trim();
    }

    public String getSince() {
        return since;
    }

    public void setSince(String since) {
        this.since = since == null ? null : since.trim();
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright == null ? null : copyright.trim();
    }

    public String getPackageName() {
        return packageName;
    }

    public String getTplCategory() {
        return tplCategory;
    }

    public void setTplCategory(String tplCategory) {
        this.tplCategory = tplCategory;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<GenTable> getGenTables() {
        return genTables;
    }

    public void setGenTables(List<GenTable> genTables) {
        this.genTables = genTables;
    }

    public String getProviderConfigOptions() {
        return providerConfigOptions;
    }

    public void setProviderConfigOptions(String providerConfigOptions) {
        this.providerConfigOptions = providerConfigOptions;
    }

    public List<ProviderConfigOption> getProviderConfigOptionList() {
        return providerConfigOptionList;
    }

    public void setProviderConfigOptionList(List<ProviderConfigOption> providerConfigOptionList) {
        this.providerConfigOptionList = providerConfigOptionList;
    }
}