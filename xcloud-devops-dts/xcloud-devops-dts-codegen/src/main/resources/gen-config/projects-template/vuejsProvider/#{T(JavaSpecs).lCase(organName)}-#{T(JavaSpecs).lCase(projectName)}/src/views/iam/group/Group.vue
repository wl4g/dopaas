/**
 * Created by Penn Peng on 2018/10/01.
 */
<template>
  <div class="">
    <el-button class="top-level-btn" type="primary" @click="addTopLevelModule()">{{ $t('message.common.addTop') }}</el-button>
      <el-button class="top-level-btn" type="primary" @click="onGetList()" :loading="loading">{{ $t('message.common.search') }}</el-button>
   <tree-table
      border
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
                  <el-form-item :label="$t('message.common.name')" prop="name">
                      <el-input v-model="saveForm.name"></el-input>
                  </el-form-item>
              </el-col>

              <el-col :span="12">
                  <el-form-item :label="$t('message.common.displayName')" prop="displayName">
                      <el-input v-model="saveForm.displayName"></el-input>
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
                              :check-strictly="true"
                              @check-change = "checkChange"
                              :props="defaultProps">
                      </el-tree>
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

          <!-- park -->
          <el-row v-if="saveForm.type==1">
              <el-col :span="8">
                  <el-form-item :label="$t('message.common.displayName')" prop="displayName">
                      <el-input v-model="saveForm.groupExt.displayName"></el-input>
                  </el-form-item>
              </el-col>
              <el-col :span="8">
                  <el-form-item :label="$t('message.ci.contactGroup')" prop="contact">
                      <el-input v-model="saveForm.groupExt.contact"></el-input>
                  </el-form-item>
              </el-col>
              <el-col :span="8">
                  <el-form-item :label="$t('message.iam.contactPhone')" prop="contactPhone">
                      <el-input v-model="saveForm.groupExt.contactPhone"></el-input>
                  </el-form-item>
              </el-col>
          </el-row>
          <el-row v-if="saveForm.type==1">
              <el-col :span="24">
                  <el-form-item :label="$t('message.iam.address')" prop="address">
                      <el-input v-model="saveForm.groupExt.address"></el-input>
                  </el-form-item>
              </el-col>
          </el-row>

          <!-- company -->
          <el-row v-if="saveForm.type==2">
              <el-col :span="8">
                  <el-form-item :label="$t('message.common.displayName')" prop="displayName">
                      <el-input v-model="saveForm.groupExt.displayName"></el-input>
                  </el-form-item>
              </el-col>
              <el-col :span="8">
                  <el-form-item :label="$t('message.ci.contactGroup')" prop="contact">
                      <el-input v-model="saveForm.groupExt.contact"></el-input>
                  </el-form-item>
              </el-col>
              <el-col :span="8">
                  <el-form-item :label="$t('message.iam.contactPhone')" prop="contactPhone">
                      <el-input v-model="saveForm.groupExt.contactPhone"></el-input>
                  </el-form-item>
              </el-col>
          </el-row>
          <el-row v-if="saveForm.type==2">
              <el-col :span="24">
                  <el-form-item :label="$t('message.iam.address')" prop="address">
                      <el-input v-model="saveForm.groupExt.address"></el-input>
                  </el-form-item>
              </el-col>
          </el-row>

          <!-- department -->
          <el-row v-if="saveForm.type==3">
              <el-col :span="8">
                  <el-form-item :label="$t('message.common.displayName')" prop="displayName">
                      <el-input v-model="saveForm.groupExt.displayName"></el-input>
                  </el-form-item>
              </el-col>
              <el-col :span="8">
                  <el-form-item :label="$t('message.ci.contactGroup')" prop="contact">
                      <el-input v-model="saveForm.groupExt.contact"></el-input>
                  </el-form-item>
              </el-col>
              <el-col :span="8">
                  <el-form-item :label="$t('message.iam.contactPhone')" prop="contactPhone">
                      <el-input v-model="saveForm.groupExt.contactPhone"></el-input>
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
  import Group from './Group.js'
  export default Group
</script>

<style scoped lang='less'>
  .top-level-btn{
    margin-bottom: 10px;
  }
</style>
