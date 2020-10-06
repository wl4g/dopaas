<template>
    <section id="configuration" class="configuration">
        <el-form label-width="130px" :model="saveForm" ref="saveForm" class="demo-form-inline" :rules="rules">
<#-- 定义要显示的列数 columnCount -->
<#assign columnCount = 2>
<#-- 每列宽度，elementui最大值为24 -->
<#assign showColWidth = 24 / columnCount>
<#-- 计算显示当前记录集需要的表格行数 rowCount -->
<#if genTableColumns?size % columnCount == 0>
    <#assign rowCount = (genTableColumns?size / columnCount) - 1 >
<#else>
    <#assign rowCount = (genTableColumns?size / columnCount) >
</#if>
<#list 0..rowCount as row> <#-- 外层输出表格的 tr -->
            <el-row>
    <#-- 内层输出表格的 td -->
    <#list 0..columnCount - 1 as cell>
        <#-- 存在当前对象就输出否则输出空格 -->
        <#if genTableColumns[row * columnCount + cell]??>
            <#assign col = genTableColumns[row * columnCount + cell]>
            <#if col.isEdit == '1'>
                <el-col :span="${showColWidth}">
                    <el-form-item label="${col.attrName}" prop="${col.attrName}">
                        <span slot="label">
                            <span>${col.columnComment}</span>
                        <#if col.columnComment != ''>
                            <el-tooltip class="item" effect="dark" content="${col.columnComment}" placement="right">
                                <i class="el-icon-question"></i>
                            </el-tooltip>
                        </#if>
                        </span>
                    <#if col.showType == '1'>
                        <el-input v-model="saveForm.${col.attrName}" placeholder="${col.columnComment}" ></el-input>
                    </#if>
                    <#if col.showType == '2'>
                        <el-input type="textarea" v-model="saveForm.${col.attrName}" placeholder="${col.columnComment}" ></el-input>
                    </#if>
                    <#if col.showType == '3'></#if><#--do nothing-->
                    <#if col.showType == '4'>
                        <el-select filterable clearable v-model="saveForm.${col.attrName}">
                            <el-option
                                    v-for="item in dictutil.getDictListByType('${col.dictType}')"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value">
                            </el-option>
                        </el-select>
                    </#if>
                    <#if col.showType == '5'>
                        <el-select filterable multiple clearable v-model="saveForm.${col.attrName}">
                            <el-option
                                    v-for="item in dictutil.getDictListByType('${col.dictType}')"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value">
                            </el-option>
                        </el-select>
                    </#if>
                    <#if col.showType == '6'>
                        <el-checkbox-group v-model="saveForm.${col.attrName}">
                            <el-checkbox v-for="item in dictutil.getDictListByType('${col.dictType}')" :label="item.value">
                                {{item.label}}
                            </el-checkbox>
                        </el-checkbox-group>
                    </#if>
                    <#if col.showType == '7'>
                        <el-date-picker
                                v-model="saveForm.${col.attrName}"
                                type="date"
                                format="yyyy-MM-dd"
                                placeholder="选择日期">
                        </el-date-picker>
                    </#if>
                    <#if col.showType == '8'>
                        <el-date-picker
                                v-model="saveForm.${col.attrName}"
                                type="datetime"
                                format="yyyy-MM-dd HH:mm:ss"
                                placeholder="选择日期时间">
                        </el-date-picker>
                    </#if>
                    </el-form-item>
                </el-col>
            </#if>
        <#else>
            &nbsp;
        </#if>
    </#list>
            </el-row>
</#list>
        </el-form>
        <div style="margin-left: 130px">
            <el-button type="primary" @click="saveData()" :loading="loading">{{$t('message.common.save')}}</el-button>
            <el-button @click="back()">{{$t('message.common.cancel')}}</el-button>
        </div>
    </section>
</template>

<script>
    import ${entityName?cap_first}Edit from './${entityName?cap_first}Edit.js'

    export default ${entityName?cap_first}Edit
</script>
<style scoped>
</style>
