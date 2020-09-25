<template>

    <section class="configuration">
        <el-form :inline="true" :model="searchParams" class="searchbar" @keyup.enter.native="onSubmit()">
            <!--<el-form-item label="DisplayName:">
                <el-input v-model="searchParams.displayName" placeholder="e.g 开发者"></el-input>
            </el-form-item>
            <el-form-item :label="$t('message.common.name')">
                <el-input v-model="searchParams.roleCode" placeholder="e.g coder"></el-input>
            </el-form-item>-->
            <el-form-item>
                <el-button @click="onSubmit" type="success" :loading="loading">{{$t('message.common.search')}}</el-button>
            </el-form-item>
        </el-form>

        <!--================================table================================-->
        <!-- 查询结果数值 -->
        <div class="query">
            <div class="query-left">
                <div class="line"></div>
                Result Total： <span class="number">{{total}}</span>
            </div>

            <!-- 新增按钮 -->
            <el-button type="primary" @click="addData()" style='float:right;margin:5px'> + New Role </el-button>
        </div>
        <!-- 查询结果表格 -->
        <div>
            <template>
                <el-table :data="tableData" :border="false"  style="width: 100%">
                    <el-table-column :label="$t('message.common.selectAll')" type="selection"></el-table-column>
                    <el-table-column prop="id" label="ID"></el-table-column>
                    <el-table-column prop="displayName" :label="$t('message.common.displayName')"></el-table-column>
                    <el-table-column prop="roleCode" :label="$t('message.iam.roleCode')"></el-table-column>
                    <el-table-column prop="groupDisplayName" :label="$t('message.common.group')"></el-table-column>

                    <el-table-column :label="$t('message.common.operation')" min-width="100">
                        <template slot-scope="scope">
                            <el-button type="info" icon='edit' @click="editData(scope.row)">{{$t('message.common.edit')}}</el-button>
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
                        <el-form-item :label="$t('message.iam.roleCode')" prop="roleCode">
                            <el-input v-model="saveForm.roleCode" placeholder="e.g coder"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item :label="$t('message.common.displayName')" prop="displayName">
                            <el-input v-model="saveForm.displayName" placeholder="e.g:devoper"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>


                <el-row>
                    <el-col :span="20">
                        <el-form-item  :label="$t('message.iam.menu')"   prop="menu">
                            <el-input type="textarea" :readonly="true" class="noHide"  v-model="saveForm.menuNameStrs" @click.native='focusDo()'></el-input>
                            <el-tree
                                    style="max-height: 240px;overflow: scroll"
                                    v-show="treeShow"
                                    default-expand-all
                                    :data="menuData"
                                    ref="modulesTree"
                                    show-checkbox
                                    node-key="id"
                                    :check-strictly="true"
                                    @check-change = "checkChange"
                                    :props="defaultProps">
                            </el-tree>
                        </el-form-item>
                    </el-col>
                </el-row>


                <el-row>
                    <el-col :span="20">
                        <el-form-item  :label="$t('message.common.group')"   prop="groups">
                            <el-input type="textarea" :readonly="true" class="noHide"  v-model="saveForm.groupNameStrs" @click.native='focusDo2()'></el-input>
                            <el-tree
                                    v-show="groupTreeShow"
                                    default-expand-all
                                    :data="groupsTreeData"
                                    ref="modulesTree2"
                                    show-checkbox
                                    node-key="id"
                                    :check-strictly="true"
                                    @check-change = "checkChange2"
                                    :props="defaultProps">
                            </el-tree>
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
    import Role from './Role.js'
    export default Role
</script>
<style scoped>
</style>
