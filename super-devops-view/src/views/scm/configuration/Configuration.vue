<template>
  <section id="configuration"  class="configuration">
    <!-- //表单 -->
    <el-form :inline="true" :model="formInline" class="demo-form-inline">
      
           <el-form-item label="Group:">
        <!--<el-select v-model="formInline.region" placeholder="请选择Group:">
          <el-option label="sso" value="sso"></el-option>
          <el-option label="portal" value="portal"></el-option>
        </el-select>-->
        <el-select v-model="formInline.group"  @change="getenvir(0)" placeholder="请选择分组：" >
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
        <el-select v-model="formInline.environment" @change="getinstance(0)" placeholder="请选择环境：">
          <el-option label="ALL" value=""></el-option>
          <el-option
                v-for="item in envirData"
                :key="item.id"
                :label="item.remark"
                :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="Instance:">
        <el-select v-model="formInline.instance" placeholder="请选择实例:">
          <el-option label="ALL" value=""></el-option>
          <el-option
                v-for="item in instanceData"
                :key="item.id"
                :label="item.host"
                :value="item.id">
          </el-option>
        </el-select>
        <!-- <el-input v-model="formInline.user" placeholder=""></el-input>  -->
      </el-form-item>

      <el-form-item>
        <el-button type="success" @click="onSubmit">Search</el-button>
      </el-form-item>
      <el-form-item style='float:right'>
        <!-- 新增按钮 -->
        <el-button type="primary" @click="addOne()">Configuration</el-button>
        <!-- 弹出内容 -->
         <el-dialog
            :title="dialogTitle"
            :visible.sync="dialogVisible"
            width="80%"
            custom-class="tanchuang"
            @close='selectdisabled=false;if(!isedit){instancelist=[]}'
            >
          <el-form :inline="true" :model="ruleForm" :rules="rules" ref="ruleForm" class="demo-form-inline">
            <el-form-item label="Group:" prop="group" style="padding-bottom: 10px;">
              <el-select v-model="ruleForm.group"  @change="getenvir(1)" placeholder="Please group" v-bind:disabled="isedit">
                <el-option
                    v-for="item in groupData"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="Env:" prop="environment">
              <el-select v-model="ruleForm.environment" @change="getinstance(1)" placeholder="Please environment" v-bind:disabled="isedit">
                <el-option
                v-for="item in envirFormData"
                :key="item.id"
                :label="item.remark"
                :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="  Node :" prop="instance" style="margin-left:10px;">
              <el-select v-model="ruleForm.instance" placeholder="Please node" v-bind:disabled="isedit">
                <el-option label="ALL" value=""></el-option>
                  <el-option
                  v-for="item in instanceFormData"
                  :key="item.id"
                  :label="item.host"
                  :value="item.id">
                  </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="Remark:" prop="remark" class="ms">
              <el-input type="textarea" v-model="ruleForm.remark" placeholder="Please remark..."></el-input>
            </el-form-item>
            <!-- 插入表格 -->
            <!-- <el-form-item label="配置文本：" prop="content" class="mh">
              <el-input type="textarea" v-model="ruleForm.content" placeholder="请输入配置内容"></el-input>
            </el-form-item> -->
            <!-- <el-form-item label="文件类型：" prop="type" class="ms">
              <el-radio v-model="ruleForm.type" label="0">preperties</el-radio>
              <el-radio v-model="ruleForm.type" label="1">yml(yaml)</el-radio>
            </el-form-item> -->
            <!-- end -->
              <el-form-item label="Instance：" prop="miaoshu">
                <!-- 查询结果表格 -->
                <div style="float:left;width: 266%;" v-loading='dialogLoading'>
                  <template>
                    <el-table
                      :data="ruleForm.tableData2"
                      style="width: 100%">
                      <!-- 动态标签 -->
                      <el-table-column prop="filename" label="Filename" min-width="130">
                          <template scope="scope">
                            <el-form-item :prop="'tableData2.' + scope.$index + '.filename'" :rules='rules.filename' >
                              <el-input size="small" class="mm" v-model="scope.row.filename" placeholder="Please filename" ></el-input> 
                            </el-form-item>
                          </template>
                      </el-table-column>
                      <el-table-column prop="type" label="Type" min-width="130">
                        <template scope="scope">
                          <el-radio v-model="scope.row.type" :label="1">yml(yaml)</el-radio>
                          <el-radio v-model="scope.row.type" :label="2">preperties</el-radio>
                        </template>
                      </el-table-column> 
                      <el-table-column prop="content" label="Instance Content" min-width="140">
                          <template scope="scope">
                             <el-form-item :prop="'tableData2.' + scope.$index + '.content'" :rules='rules.content' >
                              <el-input  v-model="scope.row.content" class="mm" type="textarea" @focus="inputcontent(scope.$index,scope.row.content)" placeholder="Please instance content" ></el-input>
                             </el-form-item>
                          </template>
                      </el-table-column> 
                      <el-table-column label="Operation" min-width="140">
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
                <el-button type="primary"  @click.native.prevent="addRow()">Append</el-button>
              </el-form-item>
    
          </el-form>
          <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitForm('ruleForm')">Release</el-button>
            <el-button @click="dialogVisible = false;if(!isedit){instancelist=[]}">Cancel</el-button>
          </span>
        </el-dialog>
        <el-dialog
          title="编辑配置内容"
          :visible.sync="innerVisible"
          append-to-body custom-class="mytc">
          <template v-if="checkfalg">
            <img src="../../../assets/correct.png" class='logo' alt="" >
            </template>   
            <template v-if="!checkfalg">
              <img src="../../../assets/error.png" class='logo' alt="" >
            </template>  
          <el-input  v-model="insidecontent" type="textarea" @change="checkconf" autosize  placeholder="请输入配置内容" class="myinput" ></el-input>
           <!-- end -->
          <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="syncontent()">确 定</el-button>
            <el-button @click="innerVisible = false">取 消</el-button>
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
          style="width: 100%"
          @selection-change="handleSelectionChange">
          <el-table-column
            label="全选"
            type="selection"
            min-width="40">
          </el-table-column>
          <el-table-column
            prop="id"
            label="ID"
             min-width="50"
             >
          </el-table-column>
           <el-table-column
            prop="groupName"
            label="Group"
             >
          </el-table-column>
          <el-table-column
            prop="envRemark"
            label="Environment"
            min-width="120"
             >
          </el-table-column>
          <el-table-column
            prop="host"
            label="Host"
             >
          </el-table-column>
          <el-table-column
             min-width="90"
            prop="ip"
            label="IP"
             >
          </el-table-column>
           <el-table-column
            prop="port"
            label="Port"
             >
          </el-table-column>
          <el-table-column
            prop="remark"
            label="Remark"
            min-width="120"
             >
          </el-table-column>
          <el-table-column
            label="Operation"
            min-width="100">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="addOne(1,scope.row)">Edit</el-button>
              <el-button @click="details(scope.row)" type="text" size="small">Detail</el-button>
            </template>
          </el-table-column>
        </el-table>
      </template>
    </div>
    <!-- 弹出内容 -->
         <el-dialog
            :title="logtitle"
            :visible.sync="dialogDisable"
            width="80%"
            custom-class="tanchuang"
            >
            <el-radio-group v-model="propertiesid" @change="selectproperties()" size="medium">
              <el-radio-button  v-for="item in propertiesData"
                :label="item.id" :key="item.id" >{{item.filename}}</el-radio-button>
            </el-radio-group>
            <el-input type="textarea" autosize v-model="content" :disabled="false"></el-input>
          <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitflsh()">刷 新</el-button>
          </span>
        </el-dialog>
    <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          @current-change='currentChange'
          >
  </el-pagination>
  </section>
</template>
<script>
  import Configuration from './Configuration.js'

  export default Configuration
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
  .el-table th{
    background-color: #34302c;
  }
  .el-table__footer-wrapper thead div, .el-table__header-wrapper thead div{
    background-color: #34302c;
    color: #fff;
  }
  .tanchuang .el-form-item{
    width: 48%;
  }
  .tanchuang .el-form-item__content,.tanchuang .el-select{
    width: 77%;
  }
  .tanchuang .el-form-item{
    margin-bottom: 10px !important;
  }
  .tanchuang .ms{
    width: 100%;
  }
  .tanchuang .mh{
    width: 100%;
    height: 20px;
  }
  .tanchuang .mm .el-textarea__inner {
    display: block;
    resize: vertical;
    padding: 5px 7px;
    line-height: 1.5;
    width: 130px;
    color: #1f2d3d;
    background-color: #fff;
    background-image: none;
    border: 1px solid #bfcbd9;
    border-radius: 4px;
    transition: border-color .2s cubic-bezier(.645,.045,.355,1);
  }
  .tanchuang .el-textarea{
    width: 97%;
  }
  .myinput .el-textarea:focus { 
    border-color: #719ECE;
    box-shadow: 0 0 10px #719ECE;
  }
   .logo {
    height: 25px;
    width: auto;
    margin-left: 0px;
    margin-top: 0px;
  }
  .mytc .el-dialog__body {
    padding: 10px 10px;
    color: #48576a;
    font-size: 14px;
  }
  .el-input--small .el-input__inner {
    height: 30px;
    width: 120px;
}
   #configuration .el-form-item__error{
  position: static;
  width: 120px;
  }
</style>
