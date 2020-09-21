// ${watermark}

${javaSpecs.escapeCopyright(copyright)}

<#assign aDateTime = .now>
<#assign now = aDateTime?date>
package ${packageName}.${daoSubModulePackageName};

import java.util.List;
import org.apache.ibatis.annotations.Param;
import ${packageName}.common.${moduleName}.${beanSubModulePackageName}.${entityName?cap_first};

/**
 * {@link ${entityName?cap_first}}
 *
 * @author ${author}
 * @version ${version}
 * @Date ${now}
 * @since ${since}
 */
public interface ${entityName?cap_first}Dao {

    int deleteByPrimaryKey(Integer id);

    int insertSelective(${entityName?cap_first} ${entityName?uncap_first});

    ${entityName?cap_first} selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(${entityName?cap_first} ${entityName?uncap_first});

    List<${entityName?cap_first}> list(@Param("${entityName?uncap_first}") ${entityName} ${entityName?uncap_first});

}