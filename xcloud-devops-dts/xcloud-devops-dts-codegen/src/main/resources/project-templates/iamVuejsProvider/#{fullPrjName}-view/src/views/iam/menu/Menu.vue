/**
* Created by Penn Peng on 2018/10/01.
*/
<template>
<div class="">
    <el-button class="top-level-btn" type="primary" @click="onGetList()" :loading="loading">{{ $t('message.common.refresh') }}</el-button>
    <el-button class="top-level-btn" type="primary" @click="addTopLevelModule()" style="float: right">Add Root</el-button>

    <tree-table border :data="data" rowKey="id" :columns="columns" :BtnInfo="btn_info" @onClickBtnAdd="onClickBtnAdd" @onClickBtnDelete="onClickBtnDelete" @onClickBtnUpdate="onClickBtnUpdate">
    </tree-table>
    <el-dialog :close-on-click-modal="false" :title="windowTitle" :visible.sync="dialogVisible" width="820">
        <el-form ref="menuForm" :label-position="labelPosition" :model="formFields" label-width="100px" :rules="rules">
            <el-row>
                <el-col :span="12">
                    <el-form-item :label="$t('message.common.enName')" prop="nameEn">
                        <el-col :span="22">
                            <el-input v-model="formFields.nameEn"></el-input>
                        </el-col>
                        <el-col :span="2" class="text-center">
                            <el-tooltip placement="top">
                                <div slot="content">在系统不同语言版本切换时使用</div>
                                <i class="el-icon-question"></i>
                            </el-tooltip>
                        </el-col>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item :label="$t('message.common.displayName')" prop="nameZh">
                        <el-col :span="22">
                            <el-input v-model="formFields.nameZh"></el-input>
                        </el-col>
                        <el-col :span="2" class="text-center">
                            <el-tooltip placement="top">
                                <div slot="content">在系统不同语言版本切换时使用</div>
                                <i class="el-icon-question"></i>
                            </el-tooltip>
                        </el-col>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item :label="$t('message.iam.permission')" prop="permission">
                        <el-col :span="22">
                            <el-input :disabled="formFields.id!=''" v-model="formFields.permission"></el-input>
                        </el-col>
                        <el-col :span="2" class="text-center">
                            <el-tooltip placement="top">
                                <div slot="content">权限标识符，用于前后端权限检查及校验，如shiro @RequiresPermissions。<br/>因菜单唯一硬编码标识所以暂不支持修改。</div>
                                <i class="el-icon-question"></i>
                            </el-tooltip>
                        </el-col>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item :label="$t('message.common.type')" prop="type">
                        <el-col :span="22">
                            <el-select :disabled="formFields.id!=''" v-model="formFields.type" placeholder="Type" style="width: 100%">
                                <el-option v-for="item in dictutil.getDictListByType('menu_type')" :key="item.value" :label="item.label" :value="item.value">
                                </el-option>
                            </el-select>
                        </el-col>
                        <el-col :span="2" class="text-center">
                            <el-tooltip placement="top">
                                <div slot="content">不同类型的菜单按钮点击时打开页面的方式不同，如静态菜单和按钮都只支持打开vue页面，<br/>而若需嵌入外部页面就必须使用动态菜单，暂不支持修改。</div>
                                <i class="el-icon-question"></i>
                            </el-tooltip>
                        </el-col>
                    </el-form-item>
                </el-col>

            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item :label="$t('message.common.icon')" prop="icon">
                        <el-col :span="22">
                            <!--<el-input v-model="formFields.icon"></el-input>-->
                            <el-select v-model="formFields.icon" style="width: 100%">
                                <el-option v-for="item in icons" :key="item.font_class" :label="item.font_class" :value="item.font_class">
                                    <svg class="top-menu-iconfont" aria-hidden="true" style="cursor:pointer;">
                                        <use :xlink:href="'#'+item.font_class"></use>
                                    </svg>
                                    <span>{{ item.font_class }}</span>
                                </el-option>
                            </el-select>
                        </el-col>
                        <el-col :span="2" class="text-center">
                            <el-tooltip placement="top">
                                <div slot="content">菜单图标地址,如:http://xx.com/a.icon或/img/a.png</div>
                                <i class="el-icon-question"></i>
                            </el-tooltip>
                        </el-col>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item :label="$t('message.common.sort')" prop="sort">
                        <el-col :span="22">
                            <el-input v-model.number="formFields.sort"></el-input>
                        </el-col>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item :label="$t('message.iam.routePath')" prop="routeNamespace">
                        <el-col :span="22">
                            <el-input v-model="formFields.routeNamespace">
                                <template v-if="formFields.parentRoutePath" slot="prepend">/#{{formFields.parentRoutePath}}</template>
                            </el-input>
                        </el-col>
                        <el-col :span="2" class="text-center">
                            <el-tooltip placement="top">
                                <div slot="content">
                                    <!--点击菜单时请求的路由的地址,如:/pipeline<br />最终真实的地址会与所有父级拼接在一齐，得到例如：<br />/ci/pipeline-->
                                    菜单路径提示（route_namespace）： 点击菜单的请求路径，<br />如：用户编辑路径为：/#/user/edit  那么需要配置为：/edit。<br />注：区别于菜单打开页面路径(page_location)，这类似于Java Serlvet将请求转发到Jsp。
                                </div>
                                <i class="el-icon-question"></i>
                            </el-tooltip>
                        </el-col>
                    </el-form-item>
                </el-col>

                <el-col :span="12" v-if="formFields.type == '2' || formFields.type == '1' || formFields.type == '3'"><!--TODO formFields.type == '3' 待商榷：编辑页面算是按钮类型吗? -->
                    <el-form-item :label="$t('message.iam.pageLocation')" prop="pageLocation">
                        <el-col :span="22">
                            <el-input v-model="formFields.pageLocation"></el-input>
                        </el-col>
                        <el-col :span="2" class="text-center">
                            <el-tooltip placement="top">
                                <div slot="content">点击菜单时实际打开的页面地址,与路由地址对应,<br />当类型为:静态菜单时,表示vue页面文件路径,如:/ci/pipeline;<br />当类型为:动态菜单时,表示内容页面地址,如:http://google.com;
                                </div>
                                <i class="el-icon-question"></i>
                            </el-tooltip>
                        </el-col>
                    </el-form-item>
                </el-col>

            </el-row>

            <el-row>
                <el-col :span="12" v-if="isDynamicMenu">
                    <el-form-item label="打开方式" prop="renderTarget">
                        <el-col :span="22">
                            <el-select v-model="formFields.renderTarget" style="width: 100%">
                                <el-option value="_self" label="_self"></el-option>
                                <el-option value="_blank" label="_blank"></el-option>
                            </el-select>
                        </el-col>
                        <el-col :span="2" class="text-center">
                            <el-tooltip placement="top">
                                <div slot="content">动态菜单点击的超链接页面打开方式，_self：在浏览器当前页，_blank：新开浏览器tab</div>
                                <i class="el-icon-question"></i>
                            </el-tooltip>
                        </el-col>
                    </el-form-item>
                </el-col>
            </el-row>

            <el-row>
                <el-col :span="20">
                    <el-form-item label="Parent" prop="menu">
                        <el-input :readonly="true" class="noHide" v-model="formFields.parentName" @click.native='focusDo()'></el-input>
                        <el-tree style="max-height: 240px;overflow: scroll" v-show="treeShow" default-expand-all :data="data" ref="modulesTree" show-checkbox node-key="id" :check-strictly="true" @check-change="checkChange" :props="defaultProps">
                        </el-tree>
                    </el-form-item>
                </el-col>
            </el-row>

        </el-form>
        <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="save">{{$t('message.common.save')}}</el-button>
            <el-button @click="dialogVisible = false">{{$t('message.common.cancel')}}</el-button>
        </span>
    </el-dialog>
</div>
</template>

<script>
import Menu from './Menu.js'

export default Menu
</script>

<style lang="less" scoped>
.top-level-btn {
    margin-bottom: 10px;
}
</style>
