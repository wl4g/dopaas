<template>
<div>
    <el-popover transition="transition:all 0.25s ease-in-out 0s;" ref="centerSwitch" trigger="click" :visible-arrow="true" popper-class="organization-popover centerSwitch">
        <div style="width:1100px;height:520px;">
            <div class="title-center">
                <p>组织架构图 :</p>
                <p>
                    <span style="font-size: 15px;color: green">* </span>
                    <span>{{current}}</span>
                </p>
            </div>
            <div id="organization_container"></div>
            <div style="float:right;width:500px;height:400px;">
                <div style="height:10%;margin-top:1px">
                    <el-row>
                        <el-col :span="2" :offset="16">
                            <el-button type="success" @click="resetAll()">Reset All</el-button>
                        </el-col>
                    </el-row>
                </div>
                <div style="height: 33%">
                    <el-row>
                        <el-col :span="2">
                            <i class="cs-i" style="background-color: red;">组织</i>
                        </el-col>
                        <el-col :span="8">
                            <el-input clearable @keyup.native="search" size="small" v-model="searchParams.park" placeholder="输入关键字搜索"></el-input>
                        </el-col>
                        <el-col :offset="1" :span="10">
                            <area-selector ref="clearParkArea" @onChangeAreaCode="onChangeParkAreaCode"></area-selector>
                        </el-col>
                    </el-row>
                    <el-scrollbar>
                        <div class="cs-area">
                            <span @click="changeOrganization(item)" :title="item.name" v-for="(item,i)  in parkShow" :key="i">{{item.name}}</span>
                        </div>
                    </el-scrollbar>
                </div>
                <div style="height: 33%">
                    <el-row>
                        <el-col :span="2">
                            <i class="cs-i" style="background-color: blue;">公司</i>
                        </el-col>
                        <el-col :span="8">
                            <el-input clearable @keyup.native="search" size="small" v-model="searchParams.company" placeholder="输入关键字搜索"></el-input>
                        </el-col>
                        <el-col :offset="1" :span="10">
                            <area-selector ref="clearCompanyArea" @onChangeAreaCode="onChangeCompanyAreaCode"></area-selector>
                        </el-col>
                    </el-row>
                    <el-scrollbar style="">
                        <div class="cs-area">
                            <span @click="changeOrganization(item)" :title="item.name" v-for="(item,i)  in companyShow" :key="i">{{item.name}}</span>
                        </div>
                    </el-scrollbar>
                </div>
                <div style="height: 35%">
                    <el-row>
                        <el-col :span="2">
                            <i class="cs-i" style="background-color: #4dff64;">部门</i>
                        </el-col>
                        <el-col :span="8">
                            <el-input clearable @keyup.native="search" size="small" v-model="searchParams.department" placeholder="输入关键字搜索"></el-input>
                        </el-col>
                        <el-col :offset="1" :span="10">
                            <area-selector ref="clearDepartmentArea" @onChangeAreaCode="onChangeDepartmentAreaCode"></area-selector>
                        </el-col>
                    </el-row>
                    <el-scrollbar style="">
                        <div class="cs-area">
                            <span @click="changeOrganization(item)" :title="item.name" v-for="(item,i)  in departmentShow" :key="i">{{item.name}}</span>
                        </div>
                    </el-scrollbar>
                </div>
            </div>
        </div>
    </el-popover>

    <el-button v-popover:centerSwitch class="organization-popover-button" plain>
        <i v-if="currentOrganization.type==1" class="cs-i" style="background-color: red;">组织</i>
        <i v-if="currentOrganization.type==2" class="cs-i" style="background-color: blue;">公司</i>
        <i v-if="currentOrganization.type==3" class="cs-i" style="background-color: #4dff64;">部门</i>
        <!--<span >{{current}}</span>-->
        <span :title="current" class="text-shenglue" style="margin-left: 4px">{{subCurrent()}}</span>
        <i class="el-icon-caret-bottom"></i>
    </el-button>
</div>
</template>

<script>
import OrganizationPanel from './OrganizationPanel.js'

export default OrganizationPanel
</script>

<style lang="less" scoped>
.cs-i {
    font-style: normal;
    color: white;
    float: left;
    width: 32px;
    height: 32px;
    line-height: 32px;
    text-align: center;
    border-radius: 50%;
}

.cs-area {
    padding-left: 6px;
    float: left;
    width: 100%;
    height: 110px;
    border: 0;
    margin-top: 10px;

    span {
        margin-right: 6px;
        float: left;
        line-height: 24px;
        width: 110px;
        text-align: left;
        cursor: pointer;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }
}

.centerSwitch {
    top: 42px !important;
}

.organization-popover .cs-area span:hover {
    color: #0b86f3;
}

.organization-popover .cs-area span {
    color: #f3f1ed;
}

.organization-popover-button {
    margin-top: 2px;
    border: 0;
    line-height: 32px;
}

.el-popper[x-placement^=bottom] .popper__arrow::after {
    border-bottom-color: #c71f1f;
}
</style>
