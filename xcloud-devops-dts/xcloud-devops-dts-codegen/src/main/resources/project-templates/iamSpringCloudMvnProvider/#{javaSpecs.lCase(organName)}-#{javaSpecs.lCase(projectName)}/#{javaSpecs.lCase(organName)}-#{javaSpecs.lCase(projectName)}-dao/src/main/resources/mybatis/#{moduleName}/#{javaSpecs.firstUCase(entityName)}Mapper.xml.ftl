<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<!-- ${watermark} -->

<mapper namespace="${packageName}.${daoSubModulePackageName}.${entityName?cap_first}Dao">
    <resultMap id="BaseResultMap" type="${organType}.${organName}.${projectName}.common.${moduleName}.${beanSubModulePackageName}.${entityName?cap_first}">
		<id column="${pk.columnName}" jdbcType="${pk.sqlType}" property="${pk.attrName}" />
        <#list genTableColumns as param>
            <#if param.isPk != 1>
        <result column="${param.columnName}" jdbcType="${param.sqlType}" property="${param.attrName}" />
            </#if>
        </#list>
	</resultMap>

    <sql id="Base_Column_List">
        <#list genTableColumns as param>${param.columnName}<#if param_has_next>, </#if></#list>
    </sql>

    <select id="selectByPrimaryKey" parameterType="java.lang.Integer" resultMap="BaseResultMap">
        SELECT
       		<include refid="Base_Column_List" />
        FROM ${tableName}
        <where>
        	${pk.columnName} = ${r'#{'}${pk.attrName}, jdbcType=${pk.sqlType}}
        </where>
    </select>

    <delete id="deleteByPrimaryKey" parameterType="java.lang.Integer">
		DELETE FROM ${tableName}
		<where>
			${pk.columnName} = ${r'#{'}${pk.attrName}, jdbcType=${pk.sqlType}}
		</where>
    </delete>

    <insert id="insertSelective" parameterType="${organType}.${organName}.${projectName}.common.${moduleName}.${beanSubModulePackageName}.${entityName?cap_first}">
		INSERT INTO ${tableName}
		<trim prefix="(" suffix=")" suffixOverrides=",">
			<#list genTableColumns as param>
				<if test="${param.attrName} != null">
				${param.columnName},
				</if>
			</#list>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <#list genTableColumns as param>
               <if test="${param.attrName} != null">
                   ${r'#{'}${param.attrName}, jdbcType=${param.sqlType}},
               </if>
            </#list>
        </trim>
    </insert>

    <update id="updateByPrimaryKeySelective" parameterType="${organType}.${organName}.${projectName}.common.${moduleName}.${beanSubModulePackageName}.${entityName?cap_first}">
        UPDATE ${tableName}
		<set>
		<#list genTableColumns as param>
			<if test="${param.attrName} != null">
				${param.columnName} = ${r'#{'}${param.attrName}, jdbcType=${param.sqlType}},
			</if>
        </#list>
        </set>
		<where>
			${pk.columnName} = ${r'#{'}${pk.attrName}, jdbcType=${pk.sqlType}}
		</where>
    </update>

    <update id="updateByPrimaryKey" parameterType="${organType}.${organName}.${projectName}.common.${moduleName}.${beanSubModulePackageName}.${entityName?cap_first}">
		UPDATE ${tableName}
		<set>
		<#list genTableColumns as param>
			${param.columnName} = ${r'#{'}${param.attrName}, jdbcType=${param.sqlType}},
		</#list>
		</set>
		<where>
			${pk.columnName} = ${r'#{'}${pk.attrName}, jdbcType=${pk.sqlType}}
		</where>
    </update>

    <select id="list" resultMap="BaseResultMap" parameterType="java.util.Map" >
		SELECT
			<include refid="Base_Column_List"/>
		FROM ${tableName}
		<where>
			<#if optionMap.tableDeleteType == 'deleteWithLogical'>
				AND del_flag != 1
			</#if>
			<#list genTableColumns as param>
			<#if param.isQuery == 1>
                <#if param.queryType == 1>
			<if test="${javaSpecs.genMapperIfTestExpression(param.sqlType, entityName?uncap_first+'.'+param.attrName)}" >
	            AND `${param.columnName}` = ${r'#{'}${entityName?uncap_first}.${param.attrName}}
			</if>
				<#elseif param.queryType == 2>
			<if test="${javaSpecs.genMapperIfTestExpression(param.sqlType, entityName?uncap_first+'.'+param.attrName)}" >
				AND `${param.columnName}` != ${r'#{'}${entityName?uncap_first}.${param.attrName}}
			</if>
				<#elseif param.queryType == 3>
			<if test="${javaSpecs.genMapperIfTestExpression(param.sqlType, entityName?uncap_first+'.'+param.attrName)}" >
	            AND `${param.columnName}` &gt; ${r'#{'}${entityName?uncap_first}.${param.attrName}}
			</if>
				<#elseif param.queryType == 4>
			<if test="${javaSpecs.genMapperIfTestExpression(param.sqlType, entityName?uncap_first+'.'+param.attrName)}" >
				AND `${param.columnName}` &gt;= ${r'#{'}${entityName?uncap_first}.${param.attrName}}
			</if>
				<#elseif param.queryType == 5>
			<if test="${javaSpecs.genMapperIfTestExpression(param.sqlType, entityName?uncap_first+'.'+param.attrName)}" >
				AND `${param.columnName}` &lt; ${r'#{'}${entityName?uncap_first}.${param.attrName}}
			</if>
				<#elseif param.queryType == 6>
	        <if test="${javaSpecs.genMapperIfTestExpression(param.sqlType, entityName?uncap_first+'.'+param.attrName)}" >
	            AND `${param.columnName}` &lt;= ${r'#{'}${entityName?uncap_first}.${param.attrName}}
	        </if>
	            <#elseif param.queryType == 7>
	        <if test="${javaSpecs.genMapperIfTestExpression(param.sqlType, entityName?uncap_first+'.'+param.attrName)}" >
	            AND `${param.columnName}` LIKE CONCAT('%','${r'${'}${entityName?uncap_first}.${param.attrName}}','%')
			</if>
	            <#elseif param.queryType == 8>
			<if test="${javaSpecs.genMapperIfTestExpression(param.sqlType, entityName?uncap_first+'.'+param.attrName)}" >
	            AND `${param.columnName}` LIKE CONCAT('%','${r'${'}${entityName?uncap_first}.${param.attrName}}')
			</if>
	            <#elseif param.queryType == 9>
			<if test="${javaSpecs.genMapperIfTestExpression(param.sqlType, entityName?uncap_first+'.'+param.attrName)}" >
	            AND `${param.columnName}` LIKE CONCAT('${r'${'}${entityName?uncap_first}.${param.attrName}}','%')
			</if>
				</#if>
			</#if>
			</#list>
		</where>
    </select>

</mapper>