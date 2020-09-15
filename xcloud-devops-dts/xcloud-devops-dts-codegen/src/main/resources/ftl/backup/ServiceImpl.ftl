<#include "utils/annotation.ftl" />
<#include "utils/package.ftl" />
<#assign aDateTime = .now>
<#assign aDate = aDateTime?date>
<#--package name-->
package <@class_package package_name="${packageName}" module_name="${moduleName}" demixing_package="service.impl" sub_module_name="${subModuleName}" />

<#--import-->
import com.wl4g.components.data.page.PageModel;
import com.github.pagehelper.PageHelper;
import com.wl4g.components.core.bean.BaseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import <@class_package package_name="${packageName}" module_name="${moduleName}" demixing_package="bean" sub_module_name="${subModuleName}" />.${className};
import <@class_package package_name="${packageName}" module_name="${moduleName}" demixing_package="dao" sub_module_name="${subModuleName}" />.${className}Dao;
import <@class_package package_name="${packageName}" module_name="${moduleName}" demixing_package="service" sub_module_name="${subModuleName}" />.${className}Service;

import static java.util.Objects.isNull;

<@class_annotation class_name="${className}ServiceImpl" author="${functionAuthor}" date="${aDate?iso_utc}" />
@Service
public class ${className}ServiceImpl implements ${className}Service {

    @Autowired
    private ${className}Dao ${className?uncap_first}Dao;

    @Override
    public PageModel page(PageModel pm, String name) {
        pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
        pm.setRecords(${className?uncap_first}Dao.list(name));
        return pm;
    }

    public void save(${className} ${className?uncap_first}) {
        if (isNull(${className?uncap_first}.getId())) {
        ${className?uncap_first}.preInsert();
        insert(${className?uncap_first});
        } else {
        ${className?uncap_first}.preUpdate();
        update(${className?uncap_first});
        }
    }

    private void insert(${className} ${className?uncap_first}) {
        ${className?uncap_first}Dao.insertSelective(${className?uncap_first});
    }

    private void update(${className} ${className?uncap_first}) {
        ${className?uncap_first}Dao.updateByPrimaryKeySelective(${className?uncap_first});
    }

    public ${className} detail(Integer id) {
        Assert.notNull(id, "id is null");
        return ${className?uncap_first}Dao.selectByPrimaryKey(id);
    }

    public void del(Integer id) {
        Assert.notNull(id, "id is null");
        ${className} ${className?uncap_first} = new ${className}();
        ${className?uncap_first}.setId(id);
        ${className?uncap_first}.setDelFlag(BaseBean.DEL_FLAG_DELETE);
        ${className?uncap_first}Dao.updateByPrimaryKeySelective(${className?uncap_first});
    }
}
