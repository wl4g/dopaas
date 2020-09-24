<template>
  <el-table :data="formatData" :row-style="showRow" v-bind="$attrs" row-key="id">
    <el-table-column v-if="columns.length===0" width="150">
      <template slot-scope="scope">
        <span v-for="space in scope.row._level" :key="space" class="ms-tree-space"/>
        {{ scope.$index }}
      </template>
    </el-table-column>


    <el-table-column v-for="(column, index) in columns" v-else :key="column.value" :label="column.text"
    :width="column.width" >
      <template slot-scope="scope">
        <span v-for="space in scope.row._level" v-if="index === 0" :key="space" class="ms-tree-space"/>

        <span v-if="column.icon">
          <!--<img :src="scope.row['icon']" onerror="this.style.display='none'"/>-->
          <svg class="top-menu-iconfont" aria-hidden="true" style="cursor:pointer;">
              <use :xlink:href="'#'+scope.row['icon']"></use>
            </svg>
        </span>
        {{ scope.row[column.value] }}
      </template>
    </el-table-column>

      <el-table-column
        v-if="btn_info.all!==false"
        :label="btn_info.label || '操作'"
        :width="btn_info.width || 160"
        :context="_self">
        <template slot-scope='scope'>
          <span v-if="btn_info.default!==false">
              <el-button
                type="info"
                icon='plus'
                size="mini"
                @click='onBtnEvent({type:"Add",data:scope.row,dataIndex:scope.$index})'>{{btn_info.add_text || ''}}</el-button>
              <el-button
                type="info"
                icon='edit'
                size="mini"
                @click='onBtnEvent({type:"Update",data:scope.row,dataIndex:scope.$index,list:allData})'>{{btn_info.update_text || ''}}</el-button>
              <el-button
                type="danger"
                icon='delete'
                size="mini"
                @click='onBtnEvent({type:"Delete",data:scope.row,dataIndex:scope.$index})'>{{btn_info.delete_text || ''}}</el-button>
          </span>
          <el-button
                v-if='btn_info.list && (!btn_info.list_position || btn_info.list_position==="after") && ((!btn.condition || typeof btn.condition!=="function") || (typeof btn.condition==="function" && btn.condition({list:list,data:scope.row,dataIndex:scope.$index,btnIndex:index,btn:btn})===true))'
                v-for='(btn,index) in btn_info.list'
                :key='index'
                :type="btn.type || 'info'"
                size="mini"
                @click='onCustomBtnEvent({list:allData,data:scope.row,dataIndex:scope.$index,btnIndex:index,btn:btn})'>
            {{btn.text}}
                    </el-button>
        </template>
      </el-table-column>

  </el-table>
</template>

<script>

import treeToArray from './TreeTable'
export default {
  name: 'tree-table',
  data () {
    return {
      tableHeight : 450,
      btn_info: this.BtnInfo, // 按钮信息
      allData : []
    }
  },
  props: {
    data: {
      type: [Array, Object],
      required: true
    },
    columns: {
      type: Array,
      default: () => []
    },
    evalFunc: Function,
    evalArgs: Array,
    expandAll: {
      type: Boolean,
      default: false
    },
    BtnInfo: {
      type: Object,
      default () {
        return {}
      }
    },
  },
  computed: {
    // 格式化数据源
    formatData: function() {
      return this.data;
      // let tmp
      // if (!Array.isArray(this.data)) {
      //   tmp = [this.data]
      // } else {
      //   tmp = this.data
      // }
      // const func = this.evalFunc || treeToArray
      // const args = this.evalArgs ? Array.concat([tmp, this.expandAll], this.evalArgs) : [tmp, this.expandAll]
      // this.allData = func.apply(null, args)
      // return this.allData
    }
  },
  methods: {
    showRow: function(row) {
      const show = (row.parent ? (row.parent._expanded && row.parent._show) : true)
      row._show = show
      return show ? 'animation:treeTableShow 0.5s;-webkit-animation:treeTableShow 0.5s;' : 'display:none;'
    },
    // 图标显示
    iconShow(index, record) {
      return (index === 0 && record.children && record.children.length > 0)
    },
     /**
     * 点击按钮事件
     * @param opts  组装的返回参数
     * @param.attr  opts.type   string      按钮类型，内置四个(添加，修改，删除)
     * @param.attr  opts.index  number      当点击列表中的按钮时，此值为当前行的索引
     * @param.attr  opts.data   object      当点击列表中的按钮时，此值为当前行数据
     * @param.attr  opts.list   array       当点击列别中的按钮时，此值为当前列表数据
     */
    onBtnEvent (opts) {
      switch (opts.type) {
        case 'Add':
          this.$emit('onClickBtnAdd', opts)
          break
        case 'Update':
          this.$emit('onClickBtnUpdate', opts)
          break
        case 'Delete':
          this.$emit('onClickBtnDelete', opts)
          break
        default:
          this.$emit('onClickBtn', opts)
      }
    },
    /**
     * 自定义按钮事件
     * @param opts
     */
    onCustomBtnEvent (opts) {
      if (opts.btn.fn) {
        if(opts.btn.callBack){
          this.$emit(opts.btn.callBack.funName, Object.assign(opts, opts.btn.callBack.params))
        }else{
          opts.btn.fn(opts)
        }
      } else {
        this.$emit('onClickBtn', opts)
      }
    },
  },
  mounted() {
    this.tableHeight = this.$$lib_$(window).height() * 0.72
  }
}
</script>

<style scoped lang='less'>

  @keyframes treeTableShow {
    from {opacity: 0;}
    to {opacity: 1;}
  }
  @-webkit-keyframes treeTableShow {
    from {opacity: 0;}
    to {opacity: 1;}
  }

  @color-blue: #2196F3;
  @space-width: 18px;
  .ms-tree-space {
    position: relative;
    top: 1px;
    display: inline-block;
    font-style: normal;
    font-weight: 400;
    line-height: 1;
    width: @space-width;
    height: 14px;
    &::before {
      content: ""
    }
  }
  .processContainer{
    width: 100%;
    height: 100%;
  }
  table td {
    line-height: 26px;
  }

  .tree-ctrl{
    position: relative;
    cursor: pointer;
    color: @color-blue;
    margin-left: -@space-width;
  }
</style>
