<template>
    <section id="configuration" class="configuration">
        <el-form :inline="true" :model="searchParams" class="searchbar" style="margin-left:10px" @keyup.enter.native="onSubmit()">
            <el-form-item :label="$t('message.share.key')">
                <el-input v-model="searchParams.key" placeholder="e.g. app_ns_type@dev" style="width:165px"></el-input>
            </el-form-item>
            <el-form-item :label="$t('message.share.label')">
                <el-input v-model="searchParams.label" placeholder="e.g. 最大值" style="width:140px"></el-input>
            </el-form-item>
            <el-form-item :label="$t('message.common.type')">
                <el-select v-model="searchParams.type" placeholder="e.g. app_ns_type" style="width:160px" :filterable="true" clearable>
					<el-option v-for="item in allType" :key="item" :label="item" :value="item"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item :label="$t('message.common.remark')">
                <el-input v-model="searchParams.remark" placeholder="e.g. 开发环境"></el-input>
            </el-form-item>
            <el-form-item>
                <el-button @click="onSubmit" type="success" :loading="loading">{{$t('message.common.search')}}</el-button>
            </el-form-item>
        </el-form>
        <!--================================table================================-->
        <!-- 查询结果数值 -->
        <div class="query">
            <div class="query-left">
                <div class="line"></div>
                Total： <span class="number">{{total}}</span>
            </div>
            <el-button type="primary" style="float:right;margin-right:20px" @click="addData()">{{$t('message.common.add')}} Dictionaries</el-button>
        </div>
        <!-- 查询结果表格 -->
        <div>
            <template>
                <el-table :data="tableData" :border="false" style="width:100%">
                    <el-table-column :label="$t('message.common.selectAll')" type="selection"></el-table-column>
                    <el-table-column prop="key" label="Key" min-width="200"></el-table-column>
                    <el-table-column prop="value" :label="$t('message.share.value')"></el-table-column>
                    <el-table-column prop="type" :label="$t('message.common.type')" width=120></el-table-column>
                    <el-table-column prop="label" :label="$t('message.share.label')" :show-overflow-tooltip="true" width=120></el-table-column>
                    <el-table-column prop="labelEn" :label="$t('message.share.labelEn')" :show-overflow-tooltip="true" width=120></el-table-column>
                    <el-table-column prop="sort" :label="$t('message.common.sort')" width=68></el-table-column>
                    <el-table-column prop="themes" :label="$t('message.share.theme')" width=95>
                        <template slot-scope="scope">
                            <el-tag v-if="scope.row.themes" :type="dictutil.getDictThemesByTypeAndValue('theme_type',scope.row.themes)">{{dictutil.getDictLabelByTypeAndValue('theme_type',scope.row.themes)}}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="remark" :label="$t('message.common.remark')" :show-overflow-tooltip="true" width=140></el-table-column>
					<el-table-column :label="$t('message.common.operation')" width="156">
                        <template slot-scope="scope">
                            <el-button type="info" icon='edit' @click="dataDetail(scope.row)">{{$t('message.common.edit')}}</el-button>
                            <el-button type="danger" icon='delete' @click="delData(scope.row)">{{$t('message.common.del')}}</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </template>
        </div>
        <el-pagination background layout="prev, pager, next" :total="total" @current-change='currentChange'></el-pagination>

        <!--================================save dialog================================-->
        <el-dialog :close-on-click-modal="false" :title="dialogTitle" :visible.sync="dialogVisible" v-loading='dialogLoading'>
            <el-form label-width="80px" size="mini" :model="saveForm" ref="saveForm" class="demo-form-inline" :rules="rules">

                <el-row>
                    <el-col :span="12">
                        <el-form-item label="Key:" prop="key">
                            <el-input v-model="saveForm.key" :disabled="diseditable" placeholder="key"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item :label="$t('message.common.type')" prop="type">
                            <el-input v-model="saveForm.type" placeholder="Type" :disabled="diseditable"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
					<el-col :span="12">
                        <el-form-item :label="$t('message.share.value')" prop="value">
                            <el-input v-model="saveForm.value" :disabled="diseditable" placeholder="e.g. agg_oper_type"></el-input>
                        </el-form-item>
                    </el-col>
					<el-col :span="12">
                        <el-form-item :label="$t('message.share.theme')" prop="themes">
                            <el-select v-model="saveForm.themes" placeholder="" style="width:100%">
                                <el-option v-for="item in themess" :key="item" :label="item" :value="item">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="12">
                        <el-form-item :label="$t('message.share.label')" prop="label">
                            <el-input v-model="saveForm.label" placeholder="e.g. 最小值"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item :label="$t('message.share.labelEn')" prop="labelEn">
                            <el-input v-model="saveForm.labelEn" placeholder="e.g. min"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
					<el-col :span="12">
                        <el-form-item :label="$t('message.common.sort')" prop="sort">
                            <el-input v-model="saveForm.sort" placeholder="e.g. 50"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item :label="$t('message.common.icon')" prop="icon">
                            <el-input v-model="saveForm.icon" placeholder="e.g. https://domain/image/favicon"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24">
                        <el-form-item :label="$t('message.common.remark')" prop="remark">
                            <el-input type="textarea" v-model="saveForm.remark" placeholder="Please input remark..."></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                        <el-button type="primary" @click="saveData()" :loading="dialogLoading">{{$t('message.common.save')}}</el-button>
                        <el-button @click="dialogVisible = false;">{{$t('message.common.cancel')}}</el-button>
                    </span>
        </el-dialog>
    </section>
</template>
<script>
    import Dict from './Dict.js'
    export default Dict
</script>

<style scoped>
</style>
