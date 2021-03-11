<template>
    <section class="configuration">
        <el-form :inline="true" :model="searchParams" class="searchbar" @keyup.enter.native="onSubmit()">
            <el-form-item>
                <el-button @click="onSubmit" type="success" :loading="loading">{{$t('message.common.search')}}</el-button>
            </el-form-item>
            <el-button type="primary" @click="addData()" style='float:right;margin:5px'> + New Role </el-button>
        </el-form>

        <!--================================table================================-->
        <!-- 查询结果表格 -->
        <div class="components-container">
            <split-pane split="vertical" @resize="resize" :min-percent='15' :default-percent='20' >
                <template slot="paneL">
                    <div class="left-container" >
                        <el-input
                                placeholder="输入关键字进行过滤"
                                v-model="filterText">
                        </el-input>
                        <el-tree
                                default-expand-all
                                :data="groupsTreeData"
                                ref="modulesTree3"
                                show-checkbox
                                node-key="id"
                                :check-strictly="true"
                                :expand-on-click-node="false"
                                :check-on-click-node="true"
                                @check-change = "selectOrganization"
                                :props="defaultProps"
                                :filter-node-method="filterNode"
                        >
                        </el-tree>
                    </div>
                </template>
                <template slot="paneR">
                    <div class="right-container">
                        <template>
                            <el-table :data="tableData" :border="false"  style="width: 100%" v-loading="loading">
                                <el-table-column :label="$t('message.common.selectAll')" type="selection"></el-table-column>
                                <el-table-column prop="id" label="ID"></el-table-column>
                                <el-table-column prop="nameZh" :label="$t('message.common.displayName')"></el-table-column>
                                <el-table-column prop="roleCode" :label="$t('message.iam.roleCode')"></el-table-column>
                                <el-table-column prop="userCount" label="用户数">
                                    <template slot-scope="{ row }">
                                        <el-button type="text" @click="$router.push({path:'/iam/user',query: {roleId:row.id}})">{{row.userCount}}</el-button>
                                    </template>
                                </el-table-column>
                                <el-table-column prop="menusStr" label="菜单权限" :show-overflow-tooltip="true" ></el-table-column>

                                <el-table-column :label="$t('message.common.operation')" min-width="100">
                                    <template slot-scope="scope">
                                        <el-button v-if="permitutil.hasPermit('iam:role:edit')" type="info" icon='edit' @click="editData(scope.row)">{{$t('message.common.edit')}}</el-button>
                                        <el-button type="danger" icon='delete' @click="delData(scope.row)">{{$t('message.common.del')}}</el-button>
                                    </template>
                                </el-table-column>

                            </el-table>
                        </template>
                        <el-pagination
                                @size-change="currentChange"
                                @current-change="currentChange"
                                :current-page.sync="pageNum"
                                :page-size.sync="pageSize"
                                :page-sizes="[10, 20, 50, 100]"
                                layout="total, sizes, prev, pager, next, jumper"
                                :total="total">
                        </el-pagination>
                    </div>
                </template>
            </split-pane>
        </div>


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
                        <el-form-item :label="$t('message.common.displayName')" prop="nameZh">
                            <el-input v-model="saveForm.nameZh" placeholder="e.g:devoper"></el-input>
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
                                    :expand-on-click-node="false"
                                    :check-strictly="true"
                                    @check-change = "checkChange"
                                    :props="defaultProps">
                                <span class="custom-tree-node" slot-scope="{ node, data }">
                                    <span>{{ node.label }}</span>
                                    <span style="float: right">
                                      <el-button v-if="node.childNodes.length>0" round type="" size="mini" @click="() => selectAllChildren(node, data)"
                                        style="font-size: 12px; line-height: 0; padding: 7px 5px;margin-left: 10px">All Child</el-button>
                                    </span>
                                </span>
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
<style >
    .components-container {
        position: relative;
        height: 80vh;
    }

    .left-container {
        /*background-color: #F38181;*/
        height: 100%;
    }

    .right-container {
        /*background-color: #FCE38A;*/
        width: 100%;
        height: 100%;
    }
</style>
