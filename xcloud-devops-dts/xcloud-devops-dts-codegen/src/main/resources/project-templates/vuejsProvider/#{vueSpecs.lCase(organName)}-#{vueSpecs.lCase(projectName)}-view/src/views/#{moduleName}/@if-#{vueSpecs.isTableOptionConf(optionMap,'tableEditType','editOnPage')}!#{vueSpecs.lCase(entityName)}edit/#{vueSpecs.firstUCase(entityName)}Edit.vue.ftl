<template>
    <section id="configuration" class="configuration">

        <el-form label-width="130px" :model="saveForm" ref="saveForm" class="demo-form-inline" :rules="rules">
<#list genTableColumns as param>
    <#if param.isEdit == '1'>
            <el-row>
                <el-col :span="24">
                    <el-form-item label="${param.attrName}" prop="${param.attrName}">
                        <span slot="label">
                            <span>${param.columnComment}</span>
                        <#if param.columnComment != ''>
                            <el-tooltip class="item" effect="dark" content="${param.columnComment}" placement="right">
                                <i class="el-icon-question"></i>
                            </el-tooltip>
                        </#if>
                        </span>
                    <#if param.showType == '1'>
                        <el-input v-model="saveForm.${param.attrName}" placeholder="${param.columnComment}" ></el-input>
                    </#if>
                    <#if param.showType == '2'>
                        <el-input type="textarea" v-model="saveForm.${param.attrName}" placeholder="${param.columnComment}" ></el-input>
                    </#if>
                    <#if param.showType == '3'></#if><#--do nothing-->
                    <#if param.showType == '4'>
                        <el-select filterable clearable v-model="saveForm.${param.attrName}">
                            <el-option
                                    v-for="item in dictutil.getDictListByType('${param.dictType}')"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value">
                            </el-option>
                        </el-select>
                    </#if>
                    <#if param.showType == '5'>
                        <el-select filterable multiple clearable v-model="saveForm.${param.attrName}">
                            <el-option
                                    v-for="item in dictutil.getDictListByType('${param.dictType}')"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value">
                            </el-option>
                        </el-select>
                    </#if>
                    <#if param.showType == '6'>
                        <el-checkbox-group v-model="saveForm.${param.attrName}">
                            <el-checkbox v-for="item in dictutil.getDictListByType('${param.dictType}')" :label="item.value">
                                {{item.label}}
                            </el-checkbox>
                        </el-checkbox-group>
                    </#if>
                    <#if param.showType == '7'>
                        <el-date-picker
                                v-model="saveForm.${param.attrName}"
                                type="date"
                                format="yyyy-MM-dd"
                                placeholder="选择日期">
                        </el-date-picker>
                    </#if>
                    <#if param.showType == '8'>
                        <el-date-picker
                                v-model="saveForm.${param.attrName}"
                                type="datetime"
                                format="yyyy-MM-dd HH:mm:ss"
                                placeholder="选择日期时间">
                        </el-date-picker>
                    </#if>
                    </el-form-item>
                </el-col>
            </el-row>
    </#if>
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

