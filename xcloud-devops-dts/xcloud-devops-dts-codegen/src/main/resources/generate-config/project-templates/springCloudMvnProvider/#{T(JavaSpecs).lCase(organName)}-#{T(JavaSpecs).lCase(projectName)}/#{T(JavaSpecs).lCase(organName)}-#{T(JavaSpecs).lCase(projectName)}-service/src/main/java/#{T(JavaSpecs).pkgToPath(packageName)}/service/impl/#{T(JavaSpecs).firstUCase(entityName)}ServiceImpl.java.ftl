// ${watermark}

${javaSpecs.escapeCopyright(copyright)}

<#macro class_annotation class_name author date>
/**
 * ${class_name}
 *
 * @author ${author}
 * @version ${version}
 * @Date ${now}
 * @since ${since}
 */
</#macro>
<#--{包名}/{模块名}/{分层(dao,entity,service,web)}/{子模块名}-->
<#--package com.wl4g.devops.dts.codegen.service;-->
<#macro class_package package_name module_name demixing_package>${package_name}.${module_name}.${demixing_package}</#macro>
<#assign aDateTime = .now>
<#assign aDate = aDateTime?date>
<#--package name-->
package <@class_package package_name="${packageName}" module_name="${moduleName}" demixing_package="service.impl" />;

<#--import-->
import com.wl4g.components.data.page.PageModel;
import com.github.pagehelper.PageHelper;
import com.wl4g.components.core.bean.BaseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import <@class_package package_name="${packageName}" module_name="${moduleName}" demixing_package="bean" />.${entityName};
import <@class_package package_name="${packageName}" module_name="${moduleName}" demixing_package="dao" />.${entityName}Dao;
import <@class_package package_name="${packageName}" module_name="${moduleName}" demixing_package="service" />.${entityName}Service;

import static java.util.Objects.isNull;

<@class_annotation class_name="${entityName}ServiceImpl" author="${functionAuthor}" date="${aDate?iso_utc}" />
@Service
public class ${entityName}ServiceImpl implements ${entityName}Service {

    @Autowired
    private ${entityName}Dao ${entityName?uncap_first}Dao;

    @Override
    public PageModel page(PageModel pm, String name) {
        pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
        pm.setRecords(${entityName?uncap_first}Dao.list(name));
        return pm;
    }

    public void save(${entityName} ${entityName?uncap_first}) {
        if (isNull(${entityName?uncap_first}.getId())) {
        ${entityName?uncap_first}.preInsert();
        insert(${entityName?uncap_first});
        } else {
        ${entityName?uncap_first}.preUpdate();
        update(${entityName?uncap_first});
        }
    }

    private void insert(${entityName} ${entityName?uncap_first}) {
        ${entityName?uncap_first}Dao.insertSelective(${entityName?uncap_first});
    }

    private void update(${entityName} ${entityName?uncap_first}) {
        ${entityName?uncap_first}Dao.updateByPrimaryKeySelective(${entityName?uncap_first});
    }

    public ${entityName} detail(Integer id) {
        Assert.notNull(id, "id is null");
        return ${entityName?uncap_first}Dao.selectByPrimaryKey(id);
    }

    public void del(Integer id) {
        Assert.notNull(id, "id is null");
        ${entityName} ${entityName?uncap_first} = new ${entityName}();
        ${entityName?uncap_first}.setId(id);
        ${entityName?uncap_first}.setDelFlag(BaseBean.DEL_FLAG_DELETE);
        ${entityName?uncap_first}Dao.updateByPrimaryKeySelective(${entityName?uncap_first});
    }
}
