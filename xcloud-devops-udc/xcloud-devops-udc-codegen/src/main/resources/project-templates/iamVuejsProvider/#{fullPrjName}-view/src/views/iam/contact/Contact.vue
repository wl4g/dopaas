<template>
    <div>
        <el-tabs type="border-card">
            <el-tab-pane :label="$t('message.ci.contact')">
                <section id="configuration" class="configuration">
                    <el-form :inline="true" :model="searchParams" class="searchbar" @keyup.enter.native="onSubmit()">
                        <el-form-item :label="$t('message.common.name')">
                            <el-input v-model="searchParams.name" placeholder="名字"></el-input>
                        </el-form-item>
                        <el-form-item>
                            <el-button @click="onSubmit" type="success" :loading="submitLoading">{{$t('message.common.search')}}</el-button>
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
                        <el-button v-if="permitutil.hasPermit('iam:contact:edit')" type="primary" @click="addContact()"> + </el-button>
                    </div>
                    <!-- 查询结果表格 -->
                    <div>
                        <template>
                            <el-table :data="contactData" style="width: 100%" :border="false">
                                <el-table-column :label="$t('message.common.selectAll')" type="selection"></el-table-column>
                                <el-table-column prop="id" label="ID"></el-table-column>
                                <el-table-column prop="name" :label="$t('message.common.name')"></el-table-column>
                                <!--<el-table-column prop="email" :label="$t('message.common.email')"></el-table-column>
                                <el-table-column prop="phone" :label="$t('message.common.phone')"></el-table-column>-->

                                <el-table-column :label="$t('message.common.operation')" min-width="100">
                                    <template slot-scope="scope">
                                        <el-button v-if="permitutil.hasPermit('iam:contact:edit')" type="info" dockerRepository='edit' @click="editContact(scope.row)">{{$t('message.common.edit')}}</el-button>
                                        <el-button type="danger" icon='delete' @click="delContact(scope.row)">{{$t('message.common.del')}}</el-button>
                                    </template>
                                </el-table-column>

                            </el-table>
                        </template>
                    </div>
                    <el-pagination background layout="prev, pager, next" :total="total" @current-change='currentChange'></el-pagination>


                </section>
            </el-tab-pane>
            <el-tab-pane :label="$t('message.common.group')">
                <el-form :inline="true" :model="searchGroupParams" class="searchbar">
                    <el-form-item :label="$t('message.common.name')">
                        <el-input v-model="searchGroupParams.name" placeholder="Input group name"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button @click="getGroupData" type="success" :loading="groupLoading">{{$t('message.common.search')}}</el-button>
                    </el-form-item>
                </el-form>
                <!--================================table================================-->
                <!-- 查询结果数值 -->
                <div class="query">
                    <div class="query-left">
                        <div class="line"></div>
                        Result Total： <span class="number">{{groupTotal}}</span>
                    </div>

                    <!-- 新增按钮 -->
                    <el-button type="primary" @click="addGroup()"> + </el-button>
                </div>
                <!-- 查询结果表格 -->
                <div>
                    <template>
                        <el-table :data="contactGroupData" :border="false" style="width: 100%">
                            <el-table-column :label="$t('message.common.selectAll')" type="selection"></el-table-column>
                            <el-table-column prop="id" label="ID"></el-table-column>
                            <el-table-column prop="name" :label="$t('message.common.name')">
                                <template slot-scope="scope">
                                    <!--<el-button size="small" :disabled="convertLockStatusDisable(scope.row)" @click="unlock(scope.row)">{{convertLockStatus(scope.row)}}</el-button>-->
                                    <el-input  v-model="scope.row.name"></el-input>
                                </template>
                            </el-table-column>

                            <!--<el-table-column prop="name" :label="$t('message.common.name')">
                                <template slot-scope="scope">
                                    <el-button size="small" :disabled="convertLockStatusDisable(scope.row)" @click="unlock(scope.row)">{{convertLockStatus(scope.row)}}</el-button>
                                </template>
                            </el-table-column>-->

                            <el-table-column :label="$t('message.common.operation')" min-width="100">
                                <template slot-scope="scope">
                                    <el-button type="success" @click="saveGroup(scope.row)">{{$t('message.common.save')}}</el-button>
                                    <el-button type="danger"  @click="delContactGroup(scope.row)">{{$t('message.common.del')}}</el-button>
                                </template>
                            </el-table-column>

                        </el-table>
                    </template>
                </div>
                <el-pagination background layout="prev, pager, next" :total="groupTotal" @current-change='currentChangeGroup'></el-pagination>

            </el-tab-pane>
        </el-tabs>

        <!--================================save dialog================================-->
        <el-dialog :close-on-click-modal="false" :title="dialogTitle" :visible.sync="dialogVisible" width="80%"  v-loading='dialogLoading'>
            <el-form label-width="80px" size="mini" :model="saveForm" ref="saveForm" class="demo-form-inline" :rules="rules">

                <el-row>
                    <el-col :span="12">
                        <el-form-item :label="$t('message.common.name')" prop="name">
                            <el-input v-model="saveForm.name" placeholder="Name"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="16">
                        <el-form-item :label="$t('message.common.group')" prop="groups">
                            <el-select v-model="saveForm.groups" multiple placeholder="请选择" style="width: 100%">
                                <el-option
                                        v-for="item in contactGroupData"
                                        :key="item.id"
                                        :label="item.name"
                                        :value="item.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>

                <!--===========================================new========================================================-->
                <el-row>
                    <el-col :span="24">
                        <el-form-item :label="$t('message.iam.contactChannel')">
                            <template>
                                <el-table :data="saveForm.contactChannels" :border="false" style="width: 100%">
                                    <!-- 动态标签 -->
                                    <el-table-column prop="kind" :label="$t('message.iam.contactType')">
                                        <template scope="scope">
                                            <el-select v-model="scope.row.kind">
                                                <el-option
                                                        v-for="item in dictutil.getDictListByType('sys_contact_type')"
                                                        :key="item.value"
                                                        :label="item.label"
                                                        :value="item.value">
                                                </el-option>
                                            </el-select>
                                        </template>
                                    </el-table-column>
                                    <el-table-column prop="primaryAddress" :label="$t('message.iam.primaryAddress')" >
                                        <template scope="scope">
                                            <el-input  v-model="scope.row.primaryAddress"></el-input>
                                        </template>
                                    </el-table-column>
                                    <el-table-column prop="enable" :label="$t('message.common.enable')" >
                                        <template scope="scope">
                                            <el-switch v-model="scope.row.enable" :active-value="1" :inactive-value="0"/>
                                        </template>
                                    </el-table-column>
                                    <el-table-column prop="timeOfFreq" :label="$t('message.share.timeOfFreq')" >
                                        <template scope="scope">
                                            <el-input  v-model="scope.row.timeOfFreq"></el-input>
                                        </template>
                                    </el-table-column>
                                    <el-table-column prop="numOfFreq" :label="$t('message.share.numOfFreq')" >
                                        <template scope="scope">
                                            <el-input  v-model="scope.row.numOfFreq"></el-input>
                                        </template>
                                    </el-table-column>

                                    <el-table-column :label="$t('message.common.operation')">
                                        <template slot-scope="scope">
                                            <el-row>
                                                <el-button @click.native.prevent="deleteRow(scope.$index)" type="danger">
                                                    Delete
                                                </el-button>
                                            </el-row>
                                        </template>
                                    </el-table-column>
                                </el-table>
                            </template>
                            <!--</div>-->
                            <el-button type="primary"  @click.native.prevent="addRow()"> + </el-button>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                            <el-button type="primary" @click="saveContact()" :loading="dialogLoading">{{$t('message.common.save')}}</el-button>
                            <el-button @click="dialogVisible = false;">{{$t('message.common.cancel')}}</el-button>
                        </span>
        </el-dialog>

    </div>
</template>


<script>
    import Contact from './Contact.js'

    export default Contact
</script>

<style scoped>

</style>
