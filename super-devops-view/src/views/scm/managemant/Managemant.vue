<template>
  <section id="managemant" class="managemant">
    <!-- //表单 -->
    <el-form :inline="true" :model="formInline" class="demo-form-inline">

        <el-form-item label="Group:">
        <!--<el-select v-model="formInline.region" placeholder="请选择Group:">
          <el-option label="sso" value="sso"></el-option>
          <el-option label="portal" value="portal"></el-option>
        </el-select>-->
        <el-input v-model="selectProp.group" placeholder="Please a group name"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="success" @click="queryList">Serarch</el-button>
      </el-form-item>
      <el-form-item style='float:right'>
        <!-- 新增按钮 -->
        <el-button type="primary" @click="addOne">Add</el-button>
          <!-- 弹窗内容 -->
          <el-dialog
            :title="dialogTitle"
            :visible.sync="dialogVisible"
            width="80%"
            custom-class="tanchuang"
            @close='closeDialog'
            >
            <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="80px" :inline="true">
            <!-- 插入内容 -->
          <el-form-item label="Group:" prop="group">
            <!--<el-select v-model="formInline.region" placeholder="请选择Group:">
              <el-option label="sso" value="sso"></el-option>
              <el-option label="portal" value="portal"></el-option>
            </el-select>-->
            <el-input v-model="ruleForm.group" placeholder="Please a group name" ></el-input>
          </el-form-item>
            <el-form-item label="Remark：" prop="desc" class="ms" style="padding-bottom: 20px;padding-top: 20px;">
              <el-input type="textarea" v-model="ruleForm.desc" placeholder="Please input remark..."></el-input>
            </el-form-item>
              <!-- 插入表格 -->
              <el-form-item label="Environment：" >
                <!-- 查询结果表格 -->
                <div style="float:left;width: 222%;margin-left: 20px;" v-loading='dialogLoading'>
                  <template>
                    <el-table
                      :data="ruleForm.tableData1"
                      @selection-change='selectGo'
                      style="width: 95%">
                      <!-- 动态标签 -->
                      <el-table-column prop="name" label="Environment Name" min-width="160">
                          <template scope="scope">
                            <el-form-item :prop="'tableData1.' + scope.$index + '.name'" :rules='rules.name'>
                              <el-input class="mi" size="small" v-model="scope.row.name" placeholder="请输入环境简写名称" ></el-input> 
                            </el-form-item>
                          </template>
                      </el-table-column>
                      <el-table-column prop="remark" label="Remark" min-width="200">
                          <template scope="scope">
                            <el-form-item :prop="'tableData1.' + scope.$index + '.remark'" :rules='rules.remark'>
                              <el-input size="small" class="mi" v-model="scope.row.remark" placeholder="请输入环境描述" ></el-input>
                            </el-form-item>
                          </template>
                      </el-table-column> 
                      <el-table-column style="padding-right: 10px;"
                        label="Operation"
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
                <el-button style="margin-left:20px" type="primary"  @click.native.prevent="addRow()">Append</el-button>
              </el-form-item>
            </el-form>
            <!-- end -->
            <span slot="footer" class="dialog-footer" style="text-align:center;">
              <el-button @click="dialogVisible = false">Cancel</el-button>
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
            prop="remark"
            label="Remark"
            >
          </el-table-column>
          <el-table-column
            label="Operation"
            >
            <template slot-scope="scope">
              <el-button @click="deleteRow(scope.row.id)" type="text" size="small">Delete</el-button>
              <el-button type="text" size="small" @click="editOne(scope.row.id)" >Edit</el-button>
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
  import Managemant from './Managemant.js'

  export default Managemant
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

  .el-dialog .el-dialog__header{
    background: #3a3630;
    padding: 0 8px;
  }
  .el-dialog__header .el-dialog__title{
    color: #fff;
  }
  .el-dialog__headerbtn .el-icon{
    margin: 8px 6px;

  }
  .el-dialog__headerbtn .el-dialog__close{
    color: #fff;
  }
  .managemant .el-dialog--small{
    width: 60%;
  }
  .tanchuang .mi .el-input--small .el-input__inner {
    height: 30px;
    width: 140px;
  }
  #managemant .el-form-item__error{
  position: static;
  width: 120px;
}
</style>
