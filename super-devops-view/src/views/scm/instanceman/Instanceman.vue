<template>
  <section id="instanceman" class="instanceman">
    <!-- //表单 -->
    <el-form :inline="true" :model="formInline" class="demo-form-inline">

      <el-form-item label="Group:">
        <!--<el-select v-model="formInline.region" placeholder="请选择Group:">
          <el-option label="sso" value="sso"></el-option>
          <el-option label="portal" value="portal"></el-option>
        </el-select>-->
        <el-select v-model="selectProp.group"  @change="getenvir(0)" placeholder="请选择分组：">
            <el-option label="ALL" value=""></el-option>
            <el-option
                v-for="item in groupData"
                :key="item.id"
                :label="item.name"
                :value="item.id">
          </el-option>
        </el-select>  
      </el-form-item>

      <el-form-item label="Environment:">
        <el-select v-model="selectProp.environment" placeholder="请选择环境：">
          <el-option label="ALL" value=""></el-option>
          <el-option
                v-for="item in envirData"
                :key="item.id"
                :label="item.remark"
                :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button type="success" @click="queryList">Search</el-button>
      </el-form-item>
      <el-form-item style='float:right'>
        <!-- 新增按钮 -->
        <el-button type="primary" @click="addOne">Add</el-button>
          <!-- 弹窗内容 -->
          <el-dialog
            :title="dialogTitle"
            :visible.sync="dialogVisible"
            width="80%"
            custom-class="tanchuang1"
            @close='closeDialog;selectdisabled=false'
            >
            <el-form :model="ruleForm" :rules="rules" ref="ruleForm"  label-width="80px" :inline="true">
            <!-- 插入内容 -->
          <el-form-item label="Group:" v-if="!selectdisabled" prop="group" style="padding-bottom: 20px;padding-top: 20px;">
            <el-select v-model="ruleForm.group"  @change="getenvir(1)" placeholder="Please a group name" >
                <!-- <el-option label="全部" value=""></el-option> -->
                <el-option
                    v-for="item in groupData"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="Environment:" v-if="!selectdisabled" prop="environment" style="padding-bottom: 20px;padding-top: 20px;">
            <el-select style="margin-left: 0px;" v-model="ruleForm.environment" placeholder="Please a environment name" >
              <!-- <el-option label="全部" value=""></el-option> -->
              <el-option
                    v-for="item in envirFormData"
                    :key="item.id"
                    :label="item.remark"
                    :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
            <!-- <el-form-item label="描述：" prop="desc" class="ms" style="padding-bottom: 20px;padding-top: 20px;">
              <el-input type="textarea" v-model="ruleForm.desc" placeholder="请输入详细描述"></el-input>
            </el-form-item> -->
              <!-- 插入表格 -->
              <el-form-item label="Instance:">
                <!-- 查询结果表格 -->
                <div style="float:left; padding-left : 0px" v-loading='dialogLoading'>
                  <template>
                    <el-table
                      :data="ruleForm.tableData1">
                      <el-table-column
                        label="Enable"
                        min-width="90"
                        >
                          <template scope="scope">
                              <el-checkbox v-model="scope.row.enable" @change="handleEdit(scope.$index, scope.row)"></el-checkbox>
                          </template>
                      </el-table-column>
                      <!-- 动态标签 -->
                      <el-table-column prop="host" label="Host" min-width="160">
                          <template scope="scope">
                            <el-form-item :prop="'tableData1.' + scope.$index + '.host'" :rules='rules.host'>
                              <el-input size="small" v-model="scope.row.host" placeholder="Please host" @change="handleEdit(scope.$index, scope.row)"></el-input> 
                            </el-form-item>
                          </template>
                      </el-table-column>
                      <!-- <el-form-item prop="ip"> -->
                        <el-table-column prop="ip" label="IP" min-width="80" >
                            <template scope="scope">
                              <el-form-item :prop="'tableData1.' + scope.$index + '.ip'" :rules='rules.ip' >
                                  <!-- <el-input v-if="scope.$index < ruleForm.tableData1.length-1" v-model="scope.row.ip" ></el-input>
                                  <el-input v-if="scope.$index == ruleForm.tableData1.length-1" v-model="scope.row.ip"  @change="handleEdit(scope.$index, scope.row)"></el-input> -->
                                <el-input  size="small" v-model="scope.row.ip" placeholder="Please ip" @change="handleEdit(scope.$index, scope.row)"></el-input>
                              </el-form-item>
                            </template>
                        </el-table-column> 
                      <!-- </el-form-item> -->
                      <el-table-column prop="port" label="Port" min-width="120">
                          <template scope="scope">
                            <el-form-item :prop="'tableData1.' + scope.$index + '.port'" :rules='rules.port'>
                              <el-input size="small" v-model.number="scope.row.port" placeholder="Pleas port" @change="handleEdit(scope.$index, scope.row)"></el-input> 
                            </el-form-item>
                          </template>
                      </el-table-column>
                      <el-table-column prop="remark" label="Remark" min-width="140">
                          <template scope="scope">
                            <el-form-item :prop="'tableData1.' + scope.$index + '.remark'" :rules='rules.remark' label-width="150px" size="large">
                              <el-input size="small" v-model="scope.row.remark" placeholder="Please remark" @change="handleEdit(scope.$index, scope.row)"></el-input>
                            </el-form-item>
                          </template>
                      </el-table-column>

                      <el-table-column
                        label="Operation"
                        min-width="140"
                        >
                        <template slot-scope="scope">
                          <el-row>
                            <el-button
                              @click.native.prevent="deleteInstance(scope.$index, scope.row.id)"
                              type="text"
                              size="mini"
                              style="float: left;line-height: 20px;">
                              Delete
                            </el-button>
                            <!-- <el-button
                              @click.native.prevent="submitInstance(scope.row)"
                              type="text"
                              size="mini"
                              style="line-height: 20px;">
                              保存
                            </el-button> -->
                          </el-row>
                        </template>
                      </el-table-column>
                    </el-table>
                  </template>
                </div>
                <p>
                  <el-button type="primary"  @click.native.prevent="addRow()">Append</el-button>
                </p>
               
              </el-form-item>
            </el-form>
            <!-- end -->
            <span slot="footer" class="dialog-footer" style="text-align:center;">
              <el-button @click="dialogVisible = false;selectdisabled = false">Cancel</el-button>
              <el-button type="primary" @click="submitForm('ruleForm')">OK</el-button>
            </span>
          </el-dialog>
      </el-form-item>
    </el-form>

    <!-- 查询结果数值 -->
    <div class="query">
      <div class="line"></div>
      <div class="">Result Total： <span class="number">{{total}}</span></div>
    </div>

    <!-- 查询结果表格 -->
    <div v-loading='loading'>
      <template>
        <el-table
          :data="tableData"
          style="width: 100%">
          <el-table-column
            prop="name"
            label="Group">
          </el-table-column>
          <el-table-column
            prop="instanceCount"
            label="Instances"
            >
          </el-table-column>
          <el-table-column
            prop="evnname"
            label="Environment"
            >
          </el-table-column>
          <el-table-column
            prop="remark"
            label="Remark"
            >
          </el-table-column>
          <el-table-column
            label="Operation"
            >
            <template slot-scope="scope">
              <el-button @click="deleteRow(scope.row.evnsci)" type="text" size="small">Delete</el-button>
              <el-button type="text" size="small" @click="editOne(scope.row)" >Edit</el-button>
            </template>
          </el-table-column>
          
        </el-table>
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          @current-change='currentChange'
          >
        </el-pagination>
      </template>
    </div>
  </section>
</template>
<script>
  import Instanceman from './Instanceman.js'

  export default Instanceman
</script>
<style>
  .query{
    line-height: 18px;
    margin-bottom: 22px;
  }
  .number{
    font-weight: bold;color: #6cb33e;
  }
  .line{
    width:4px;height:18px;background:#6cb33e;display: block;float: left;margin-right: 6px;
  }
#instanceman .el-dialog--small{
  width: 70%;
}
#instanceman .el-form-item__error{
  position: static;
}
  .el-dialog .el-dialog__header{
    background: #3a3630;
    padding: 0 8px;
  }
  .tanchuang1 .cell .el-form-item{
       width: 100%;
  }
  .el-dialog__header .el-dialog__title{
    color: #fff;
  }
  .tanchuang1 .table .el-form-item__content, .tanchuang1 .table .el-select{
    width: 100%;
  }
  .el-dialog__headerbtn .el-icon{
    margin: 8px 6px;

  }
  .el-dialog__headerbtn .el-dialog__close{
    color: #fff;
  }
  .instanceman .el-dialog--small{
    width: 60%;
  }
</style>
