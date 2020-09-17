<template>
<el-form-item label="组织结机构" prop="organization">
    <el-input :readonly="true" class="noHide" v-model="displayName" @click.native='focusDo()'></el-input>
    <el-tree style="max-height: 240px;overflow: scroll" v-show="treeShow" default-expand-all :data="data" ref="modulesTree" show-checkbox node-key="organizationCode" :check-strictly="true" @check-change="checkChange" :props="defaultProps">
    </el-tree>
</el-form-item>
</template>

<script>
import OrganizationSelector from './OrganizationSelector.js'

export default OrganizationSelector
</script>

<style scoped lang='less'>


</style>
