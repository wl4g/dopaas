<template>
    <section id="configuration" class="configuration">
        <el-form :inline="true" :model="searchParams" class="searchbar" @keyup.enter.native="onSubmit()">
<#assign extractedTabComment = vueSpecs.extractComment(comments, "simple")>
<#list genTableColumns as param>
    <#assign extractedColComment = vueSpecs.extractComment(param.columnComment, "simple")>
    <#if param.isQuery == '1'>
            <el-form-item label="${extractedColComment}">
        <#if param.showType == '1'>
                <el-input v-model="searchParams.${param.attrName}" placeholder="${param.columnComment}" style="width:165px"></el-input>
        <#elseif param.showType == '2'>
                <el-input type="textarea" v-model="searchParams.${param.attrName}" placeholder="${param.columnComment}" style="width:165px"></el-input>
        <#elseif param.showType == '3'><#--do nothing-->
        <#elseif param.showType == '4'>
                <el-select filterable clearable v-model="searchParams.${param.attrName}">
                    <el-option
                            v-for="item in dictutil.getDictListByType('${param.dictType}')"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                    </el-option>
                </el-select>
        <#elseif param.showType == '5'>
                <el-select filterable multiple clearable v-model="searchParams.${param.attrName}">
                    <el-option
                            v-for="item in dictutil.getDictListByType('${param.dictType}')"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                    </el-option>
                </el-select>
        <#elseif param.showType == '6'>
                <el-checkbox-group v-model="searchParams.${param.attrName}">
                    <el-checkbox v-for="item in dictutil.getDictListByType('${param.dictType}')" :label="item.value">
                        {{item.label}}
                    </el-checkbox>
                </el-checkbox-group>
        <#elseif param.showType == '7'>
                <el-date-picker
                        v-model="searchParams.${param.attrName}"
                        type="date"
                        placeholder="选择日期">
                </el-date-picker>
        <#elseif param.showType == '8'>
                <el-date-picker
                        v-model="searchParams.${param.attrName}"
                        type="datetime"
                        placeholder="选择日期时间">
                </el-date-picker>
        </#if>
            </el-form-item>
    </#if>
</#list>
            <input hidden />
            <el-form-item>
                <el-button @click="onSubmit" type="success" :loading="loading">{{$t('message.common.search')}}</el-button>
            </el-form-item>
            <el-button type="primary" style='float:right;margin-right:20px' @click="addData()" >+ ${extractedTabComment}</el-button>
        </el-form>

        <!--================================table================================-->
        <!-- 查询结果数值 -->
        <div class="query">
            <div class="query-left">
                <div class="line"></div>
                Result Total： <span class="number">{{total}}</span>
            </div>
        </div>
        <!-- 查询结果表格 -->
        <div>
            <template>
                <el-table :data="tableData" :border="false" style="width:100%">
                    <!--<el-table-column :label="$t('message.common.selectAll')" type="selection"></el-table-column>-->
                    <!--<el-table-column width="100" prop="id" label="ID"></el-table-column>-->
<#list genTableColumns as param>
    <#if param.isList == '1'>
                    <el-table-column prop="${param.attrName}">
                        <template slot="header" slot-scope="scope">
                            <span>${param.columnComment}</span>
                        <#if param.columnComment != ''>
                            <el-tooltip class="item" effect="dark" content="${param.columnComment}" placement="right">
                                <i class="el-icon-question"></i>
                            </el-tooltip>
                        </#if>
                        </template>
                        <template slot-scope="{row}">
                            <span>{{row.${param.attrName}}}</span>
                        </template>
                    </el-table-column>
    </#if>
</#list>
                    <el-table-column :label="$t('message.common.operation')" min-width="120">
                        <template slot-scope="scope">
                            <el-button type="text" icon="el-icon-edit" @click="editData(scope.row)" :title="$t('message.common.edit')"></el-button>
<#if optionMap.isDeletable != 'deleteWithNone'>
                            <el-button type="text" icon="el-icon-delete" @click="delData(scope.row)" :title="$t('message.common.del')"></el-button>
</#if>
                        </template>
                    </el-table-column>
                </el-table>
            </template>
        </div>
        <el-pagination background layout="prev, pager, next" :total="total" @current-change='currentChange'></el-pagination>

        <!--================================save dialog================================-->
        <el-dialog :close-on-click-modal="false" :title="dialogTitle" :visible.sync="dialogVisible"  v-loading='dialogLoading'>
            <el-form label-width="150px" :model="saveForm" ref="saveForm" class="demo-form-inline" :rules="rules">
<#-- 定义要显示的列数 columnCount -->
<#assign columnCount = 2>
<#-- 每列宽度，elementui最大值为24（为了美化样式需减去一定宽度，使其尽量居中） -->
<#assign showColWidth = (24 - 2) / columnCount>
<#-- 过滤出有效的列(需显示的列) -->
<#assign availableColumns = vueSpecs.filterColumns(genTableColumns)>
<#-- 计算显示当前记录集需要的表格行数 rowCount -->
<#if availableColumns?size % columnCount == 0>
    <#assign rowCount = (availableColumns?size / columnCount) - 1 >
<#else>
    <#assign rowCount = (availableColumns?size / columnCount) >
</#if>
<#list 0..rowCount as row> <#-- 外层输出表格的 tr -->
                <el-row>
    <#-- 内层输出表格的 td -->
    <#list 0..columnCount - 1 as cell>
        <#-- 存在当前对象就输出否则输出空格 -->
        <#if availableColumns[row * columnCount + cell]??>
            <#assign col = availableColumns[row * columnCount + cell]>
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
                        <#elseif col.showType == '2'>
                            <el-input type="textarea" v-model="saveForm.${col.attrName}" placeholder="${col.columnComment}" ></el-input>
                        <#elseif col.showType == '3'><#--do nothing-->
                        <#elseif col.showType == '4'>
                            <el-select filterable clearable v-model="saveForm.${col.attrName}">
                                <el-option
                                        v-for="item in dictutil.getDictListByType('${col.dictType}')"
                                        :key="item.value"
                                        :label="item.label"
                                        :value="item.value">
                                </el-option>
                            </el-select>
                        <#elseif col.showType == '5'>
                            <el-select filterable multiple clearable v-model="saveForm.${col.attrName}">
                                <el-option
                                        v-for="item in dictutil.getDictListByType('${col.dictType}')"
                                        :key="item.value"
                                        :label="item.label"
                                        :value="item.value">
                                </el-option>
                            </el-select>
                        <#elseif col.showType == '6'>
                            <el-checkbox-group v-model="saveForm.${col.attrName}">
                                <el-checkbox v-for="item in dictutil.getDictListByType('${col.dictType}')" :label="item.value">
                                    {{item.label}}
                                </el-checkbox>
                            </el-checkbox-group>
                        <#elseif col.showType == '7'>
                            <el-date-picker
                                    v-model="saveForm.${col.attrName}"
                                    type="date"
                                    format="yyyy-MM-dd"
                                    placeholder="选择日期">
                            </el-date-picker>
                        <#elseif col.showType == '8'>
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
            <span slot="footer" class="dialog-footer">
                <el-button type="primary" @click="saveData()" :loading="dialogLoading">{{$t('message.common.save')}}</el-button>
                <el-button @click="dialogVisible = false;">{{$t('message.common.cancel')}}</el-button>
            </span>
        </el-dialog>
    </section>
</template>
<script>
    import ${entityName?cap_first} from './${entityName?cap_first}.js'

    export default ${entityName?cap_first}
</script>
<style scoped>

</style>

