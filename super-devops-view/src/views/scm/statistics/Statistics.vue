<template>
  <section class="statistics">
    <!-- //表单 -->
    <el-form :inline="true" :model="formInline" class="demo-form-inline">
      
      <el-form-item label="Log-level:" >
        <el-tooltip class="item" effect="dark" content="日志级别过滤：如INFO ↑表示仅输出INFO级别即以上级别的(ERROR/WARN)日志" placement="bottom-start">
          <el-select v-model="formInline.loglevle" class="testinput" >
            <el-option
                  v-for="item in loglevle"
                  :key="item.id"
                  :label="item.value"
                  :value="item.id">
            </el-option>
          </el-select>
        </el-tooltip>
      </el-form-item>
      
      <el-form-item label="keword:">
        <el-input v-model="formInline.content" placeholder="查询value" class="testinput1"></el-input>
        <!-- <el-input v-model="formInline.user" placeholder=""></el-input>  -->
      </el-form-item>
      <el-form-item>
        <el-checkbox v-model="formInline.enable"></el-checkbox>
      </el-form-item>
      <el-form-item>
        <p @click="screen" style="color:#48576a;cursor:pointer;">高级筛选</p>
      </el-form-item>
      <el-form-item>
       <el-tooltip class="item" effect="dark" content="高级筛选说明：支持多条件查询，包括条件的包含与不包含查询" placement="bottom-start">
          <i class="el-icon-warning" style="color:#e0e0e2;"></i>
        </el-tooltip>
      </el-form-item>
      <el-form-item label="选择日期:">
        <el-date-picker
                v-model="value1"
                type="date"
                placeholder="选择日期" >
        </el-date-picker>
      </el-form-item>
       <el-form-item label="开始时间:">
        <el-time-picker
            v-model="value2"
            :picker-options="{
              selectableRange: '00:00:00 - 23:59:59'
            }"
            placeholder="任意时间点">
          </el-time-picker>
        </el-form-item>
        <el-form-item label="结束时间:">
          <el-time-picker
            arrow-control
            v-model="value3"
            :picker-options="{
              selectableRange: '00:00:00 - 23:59:59'
            }"
            placeholder="任意时间点">
          </el-time-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit()">{{excute}}</el-button>
      </el-form-item>
        <!-- 弹出内容 -->
      <el-dialog
        title="高级筛选"
        :visible.sync="dialogVisible"
        width="30%"
        class="tanchuang"
        :before-close="handleClose">
        <el-form :inline="true" :model="formInline" class="demo-form-inline">
          <!-- 插入表格 -->
          <el-form-item label="配置：" prop="miaoshu">
            <!-- 查询结果表格 -->
            <div style="float:left;">
              <template>
                <el-table
                  :data="tableData1"
                  style="width: 100%">
                  <!-- 动态标签 -->
                  <el-table-column label="包含" min-width="90">
                        <template scope="scope">
                            <el-checkbox v-model="scope.row.enable" ></el-checkbox>
                        </template>
                    </el-table-column>
                  <el-table-column prop="value" label="value" width="240">
                      <template scope="scope">
                          <el-input size="small" v-model="scope.row.value" placeholder="请输入value" ></el-input>
                      </template>
                  </el-table-column> 

                  <el-table-column  label="操作" width="100">
                    <template slot-scope="scope">
                      <el-button
                        @click.native.prevent="deleteRow(scope.$index, tableData1)"
                        type="text"
                        size="small">
                        移除
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </template>
            </div>
            <el-button type="primary"  @click.native.prevent="addRow()">增加</el-button>
          </el-form-item>
          <!-- end -->
  
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="confirm">确 定</el-button>
        </span>
      </el-dialog>

        <el-dialog
        title="日志时间选项"
        :visible.sync="historylog"
        width="30%"
        class="tanchuang1"
        :before-close="handleClose">
        <el-form :inline="true" :model="formInline" >
          <!-- 插入表格 -->
          <el-form-item label="日志加载方式：" prop="miaoshu">
            <div style="float:left;">
              <el-radio-group v-model="radio">
                <el-radio-button  :label="1" class="myradio">相对时间</el-radio-button>
                <el-radio-button :label="2" class="myradio">自定义</el-radio-button>
              </el-radio-group>
            </div>
             <div v-show="radio==1"  style="float:left;">
                 例: 即加载最新一分钟日志,日志采集有一定延迟
            </div>
             <div v-show="radio==2"  style="float:left;">
                例: 加载采集时间为[StartTime,EndTime]之间的日志,日志采集有一定延迟
            </div>
          </el-form-item>      
          <!-- end -->
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="submit">确 定</el-button>
        </span>
      </el-dialog>
        <!-- end -->
    </el-form>
       <!-- 查询结果数值 -->
    <div class="query">
      <div class="line"></div>
      <div class="">查询结果：共查询到 <span class="number">{{total}}</span> 条记录</div>
    </div>
  </section>
</template>
<script>
  import Statistics from './Statistics.js'

  export default Statistics
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
</style>
