<template>
  <div class="">
      <el-button class="top-level-btn" type="primary" @click="onGetList()" :loading="loading">{{ $t('message.common.refresh') }}</el-button>
      <el-button class="top-level-btn" type="primary" @click="addTopLevelModule()" style="float: right">{{ $t('message.common.addTop') }}</el-button>
   <tree-table
      border
      rowKey="organizationCode"
      :data="data"
      :columns="columns"
      :BtnInfo="btn_info"
      @onClickBtnAdd="onClickBtnAdd"
      @onClickBtnDelete="onClickBtnDelete"
      @onClickBtnUpdate="onClickBtnUpdate"
    >
    </tree-table>

    <el-dialog :close-on-click-modal="false" :title="windowTitle" :visible.sync="dialogVisible" >
      <el-form ref="groupForm" label-position="right" :model="saveForm" label-width="100px" :rules="rules">

          <el-row>
              <el-col :span="12">
                  <el-form-item :label="$t('message.common.name')" prop="nameEn">
                      <el-input v-model="saveForm.nameEn"></el-input>
                  </el-form-item>
              </el-col>

              <el-col :span="12">
                  <el-form-item :label="$t('message.common.displayName')" prop="nameZh">
                      <el-input v-model="saveForm.nameZh"></el-input>
                  </el-form-item>
              </el-col>
          </el-row>

          <el-row>
              <el-col :span="12">
                  <el-form-item label="组织机构编码" prop="organizationCode">
                      <el-input v-model="saveForm.organizationCode"></el-input>
                  </el-form-item>
              </el-col>
          </el-row>

          <el-row>
              <el-col :span="8">
                  <el-form-item :label="$t('message.common.type')" prop="type">
                      <el-select v-model="saveForm.type" :disabled="isEdit">
                          <el-option
                                  v-for="item in dictutil.getDictListByType('sys_group_type')"
                                  :key="parseInt(item.value)"
                                  :label="item.label"
                                  :value="parseInt(item.value)">
                          </el-option>
                      </el-select>
                  </el-form-item>
              </el-col>
          </el-row>

     </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button :loading="dialogSubmitBtnSwith" type="primary" @click="save">{{ $t('message.common.save') }}</el-button>
        <el-button @click="dialogVisible = false">{{ $t('message.common.cancel') }}</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
  import Organization from './Organization.js'
  export default Organization
</script>

<style scoped lang='less'>
  .top-level-btn{
    margin-bottom: 10px;
  }
</style>
