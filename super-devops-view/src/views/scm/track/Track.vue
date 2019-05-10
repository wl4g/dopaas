<template>
  <section class="track">
    <el-form :inline="true" :model="formInline" class="demo-form-inline">
      <!-- <el-form-item label="Data ID:" >
        <el-input  v-model="formInline.user" placeholder="版本ID"></el-input>
      </el-form-item> -->
       <el-form-item label="Group:">
        <el-select v-model="formInline.group"  @change="getenvir()" placeholder="Please group" >
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
        <el-select v-model="formInline.environment" @change="getinstance()" placeholder="Please environment" >
          <el-option label="ALL" value=""></el-option>
          <el-option
          v-for="item in envirFormData"
          :key="item.id"
          :label="item.remark"
          :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="  Node :">
        <el-select v-model="formInline.instance" placeholder="Please node" >
          <el-option label="ALL" value=""></el-option>
            <el-option
            v-for="item in instanceFormData"
            :key="item.id"
            :label="item.host"
            :value="item.id">
            </el-option>
        </el-select>
      </el-form-item>

      <!-- 选择日期 -->
      <!-- <div class="starttime">
        <span class="demonstration">始止日期:</span>
        <el-date-picker v-model="value1" type="date" placeholder="选择日期">
        </el-date-picker>
      </div>
      <div class="endtime">
        <span class="demonstration"></span>
        <el-date-picker
          v-model="value2"
          type="date"
          placeholder="选择日期">
        </el-date-picker>
      </div> -->

      <el-form-item>
        <el-button type="success" @click="onSubmit">Search</el-button>
      </el-form-item>
    </el-form>

    <!-- 查询结果数值 -->
    <div class="query">
      <div class="line"></div>
      <div class="">Result Total： <span class="number">{{total}}</span></div>
    </div>

    <!-- 查询结果表格 -->
    <div>
      <template>
        <el-table
          :data="tableData"
          style="width: 100%" v-loading='loading'>
          <el-table-column
            prop="historyId"
            label="Release ID"
               min-width="60">
          </el-table-column>
          <el-table-column
            prop="id"
            label="Version ID"
               min-width="60">
          </el-table-column>
          <el-table-column
            prop="groupName"
            label="Group"
               min-width="50">
          </el-table-column>
          <el-table-column
            prop="envRemark"
            label="Environment"
              min-width="70">
          </el-table-column>
          <el-table-column
            prop="host"
            label="Host"
              >
          </el-table-column>
          
          <el-table-column
            prop="type"
            label="Type"
            :filters="[{ text: '发布', value: '1' }, { text: '回滚', value: '2' }]"
            :filter-method="filterTag"
            filter-placement="bottom-end" min-width="50">
            <template slot-scope="scope">
              <el-tag
                :type="scope.row.type === '发布' ? 'primary' : 'success'"
                disable-transitions>{{scope.row.type==1?'发布':'回滚'}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column
            prop="remark"
            label="Remark">
             <template slot-scope="scope">
                 <el-input  v-model="scope.row.remark" size="small" ></el-input>
              </template>
          </el-table-column>
          <el-table-column
            prop="createDate"
            label="Create Date"
               min-width="90">
          </el-table-column>
           <el-table-column label="Operation" min-width="60">
            <template slot-scope="scope">
                <el-button @click="rollback(scope.row)" type="text" size="small">Rollback</el-button>
                <el-button @click="details(scope.row)" type="text" size="small">Detail</el-button>
                <!-- <el-button type="text" @click="updateVersion(scope.row)" size="small">保存</el-button>
                <el-button type="text" size="small" @click="handleDelete(scope.row)">删除</el-button> -->
              </template>
          </el-table-column>
        </el-table>
      </template>
    </div>
      <el-dialog
            :title="logtitle"
            :visible.sync="dialogDisable"
            width="80%"
            custom-class="mytc"
            >
            <div v-loading='dialogLoading'>
              <div class="mysss"><p>版本号: {{ detail.releaseId }}</p></div>
              <div class="mysss"><p>发布结果状态:  
                <el-tag
                :type="detail.status == '1' ? 'success' : detail.status == '-1'? 'info':'danger'"
                disable-transitions>{{detail.status == '1' ? '成功' : detail.status == '-1'? '未更改':'更新失败'}}</el-tag></p></div>
              <div class="mysss"><p>描述: {{ detail.description }}</p></div>
              <div class="mysss"><p>返回结果: </p></div>
              <el-input type="textarea" autosize v-model="detail.result" :disabled="false"></el-input>
            </div>
            <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="dialogDisable = false">OK</el-button>
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
  import Track from './Track.js'

  export default Track
</script>
<style scoped>
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
  .starttime,.endtime{
    display: inline-block;
  }
  .mytc .el-dialog__body {
    padding: 10px 10px;
    color: #48576a;
    font-size: 14px;
  }
  .mysss {
    padding: 5px 5px;
  }
  
</style>
