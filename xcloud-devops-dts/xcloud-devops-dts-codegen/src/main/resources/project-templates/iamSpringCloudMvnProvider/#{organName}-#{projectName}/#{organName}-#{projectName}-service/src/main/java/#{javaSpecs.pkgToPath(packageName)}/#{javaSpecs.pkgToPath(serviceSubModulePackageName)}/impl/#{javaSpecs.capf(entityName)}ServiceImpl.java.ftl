// ${watermark}

${javaSpecs.wrapMultiComment(copyright)}

<#assign aDateTime = .now>
<#assign aDate = aDateTime?date>
package ${packageName}.${serviceSubModulePackageName}.impl;

import static com.wl4g.components.common.lang.Assert2.notNullOf;
import com.wl4g.components.data.page.PageModel;
import com.github.pagehelper.PageHelper;
import com.wl4g.components.core.bean.BaseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ${organType}.${organName}.${projectName}.common.${moduleName}.${beanSubModulePackageName}.${entityName?cap_first};
import ${packageName}.${daoSubModulePackageName}.${entityName?cap_first}Dao;
import ${packageName}.${serviceSubModulePackageName}.${entityName?cap_first}Service;

import static java.util.Objects.isNull;

/**
 * ${comments} service implements of {@link ${entityName?cap_first}}
 *
 * @author ${author}
 * @version ${version}
 * @Date ${now}
 * @since ${since}
 */
@Service
public class ${entityName}ServiceImpl implements ${entityName}Service {

    @Autowired
    private ${entityName}Dao ${entityName?uncap_first}Dao;

    @Override
    public PageModel page(PageModel pm, ${entityName} ${entityName?uncap_first}) {
        pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
        pm.setRecords(${entityName?uncap_first}Dao.list(${entityName?uncap_first}));
        return pm;
    }

    @Override
    public int save(${entityName} ${entityName?uncap_first}) {
        if (isNull(${entityName?uncap_first}.get${pk.attrName?cap_first}())) {
        	${entityName?uncap_first}.preInsert();
            return ${entityName?uncap_first}Dao.insertSelective(${entityName?uncap_first});
        } else {
        	${entityName?uncap_first}.preUpdate();
            return ${entityName?uncap_first}Dao.updateByPrimaryKeySelective(${entityName?uncap_first});
        }
    }

    @Override
    public ${entityName} detail(${javaSpecs.toSimpleJavaType(pk.attrType)} ${pk.attrName}) {
        notNullOf(${pk.attrName}, "${pk.attrName}");
        return ${entityName?uncap_first}Dao.selectByPrimaryKey(${pk.attrName});
    }

<#-- Service delete with logical  -->
<#if javaSpecs.isConf(tableExtraOptions, "gen.tab.del-type", "deleteWithLogical")>
    @Override
    public int del(${javaSpecs.toSimpleJavaType(pk.attrType)} ${pk.attrName}) {
        notNullOf(${pk.attrName}, "${pk.attrName}");
        ${entityName} ${entityName?uncap_first} = new ${entityName}();
        ${entityName?uncap_first}.set${pk.attrName?cap_first}(${pk.attrName});
        ${entityName?uncap_first}.setDelFlag(BaseBean.DEL_FLAG_DELETE);
        return ${entityName?uncap_first}Dao.updateByPrimaryKeySelective(${entityName?uncap_first});
    }
<#-- Service delete with physical  -->
<#else>
    @Override
    public int del(${javaSpecs.toSimpleJavaType(pk.attrType)} ${pk.attrName}) {
        notNullOf(${pk.attrName}, "${pk.attrName}");
        return ${entityName?uncap_first}Dao.deleteByPrimaryKey(${pk.attrName});
    }
</#if>

}
