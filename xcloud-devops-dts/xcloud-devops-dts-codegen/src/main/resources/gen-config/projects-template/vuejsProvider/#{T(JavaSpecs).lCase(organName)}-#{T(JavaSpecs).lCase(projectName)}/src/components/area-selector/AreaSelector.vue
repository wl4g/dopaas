<template>
<el-cascader size="small" :options="options" :show-all-levels="false" :change-on-select="true" @change="changeArea" placeholder="选择地区" filterable clearable :value="value"></el-cascader>
</template>

<script>
import AreaSelector from './AreaSelector.js'

export default AreaSelector
</script>

<style scoped lang='less'>

</style>
