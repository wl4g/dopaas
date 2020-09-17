<template>

    <section id="configuration" class="configuration">
        <el-form :inline="true" :model="searchParams" class="searchbar" @keyup.enter.native="onSubmit()">
            <el-form-item :label="$t('message.common.name')">
                <el-input v-model="searchParams.displayName" placeholder="e.g zhangsan"></el-input>
            </el-form-item>
            <el-form-item :label="$t('message.common.username')">
                <el-input v-model="searchParams.userName" placeholder="e.g zhangsan"></el-input>
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
                Result Total： <span class="number">{{total}}</span>
            </div>

            <!-- 新增按钮 -->
            <el-button type="primary" @click="addData()"> + </el-button>
        </div>
        <!-- 查询结果表格 -->
        <div>
            <template>
                <el-table :data="tableData" :border="false" style="width: 100%">
                    <el-table-column :label="$t('message.common.selectAll')" type="selection"></el-table-column>
                    <el-table-column prop="id" label="ID" width="60"></el-table-column>
                    <el-table-column prop="displayName" :label="$t('message.common.name')"></el-table-column>
                    <el-table-column prop="userName" :label="$t('message.common.username')"></el-table-column>
                    <el-table-column prop="roleStrs" :label="$t('message.iam.role')" ></el-table-column>
                    <el-table-column prop="groupNameStrs" :label="$t('message.common.group')" show-overflow-tooltip ></el-table-column>
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
                        <el-form-item :label="$t('message.common.name')" prop="displayName">
                            <el-input v-model="saveForm.displayName" placeholder="e.g:张三"></el-input>
                        </el-form-item>
                    </el-col>

                    <el-col :span="12">
                        <el-form-item :label="$t('message.common.username')" prop="userName">
                            <el-input v-model="saveForm.userName" :disabled="isEdit" placeholder="e.g zhangsan"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :span="12">
                        <el-form-item :label="$t('message.common.password')" prop="password">
                            <el-input type="password" v-model="saveForm.password" placeholder="e.g:123456"></el-input>
                        </el-form-item>
                    </el-col>

                    <el-col :span="12">
                        <el-form-item :label="$t('message.common.email')" prop="email">
                            <el-input v-model="saveForm.email" placeholder="e.g zhangsan@gmail.com"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :span="12">
                        <el-form-item :label="$t('message.common.phone')" prop="phone">
                            <el-input v-model="saveForm.phone" placeholder="e.g:13888888888"></el-input>
                        </el-form-item>
                    </el-col>

                    <el-col :span="12">
                        <el-form-item :label="$t('message.common.remark')" prop="remark">
                            <el-input v-model="saveForm.remark" placeholder="e.g remark"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :span="20">
                        <el-form-item :label="$t('message.iam.role')" prop="role">
                            <el-select v-model="saveForm.roleIds"  multiple  placeholder="请选择" style="width: 100%">
                                <el-option
                                        v-for="item in rolesData"
                                        :key="item.id"
                                        :label="item.displayName"
                                        :value="item.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :span="20">
                        <el-form-item  :label="$t('message.common.group')"   prop="groupNames">
                            <el-input type="textarea" :readonly="true" class="noHide"  v-model="saveForm.groupNameStrs" @click.native='focusDo()'></el-input>
                            <el-tree
                                    v-show="treeShow"
                                    default-expand-all
                                    :data="groupsTreeData"
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


            </el-form>
            <span slot="footer" class="dialog-footer">
                        <el-button type="primary" @click="save()" :loading="dialogLoading">{{$t('message.common.save')}}</el-button>
                        <el-button @click="dialogVisible = false;">{{$t('message.common.cancel')}}</el-button>
                    </span>
        </el-dialog>

    </section>
</template>
<script>
    import User from './User.js'
    export default User
</script>
<style scoped>
</style>
