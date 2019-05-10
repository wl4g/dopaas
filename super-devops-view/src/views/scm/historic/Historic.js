import { transDate, getDay } from 'utils/'
export default {
  name: 'historic',
  data () {
    return {
      formInline: {
        type: '',
        region: '',
        instance: "",
        environment: "",
        group: "",
      },

      // 插入表格状态：选择是否为缺陷状态
      options: [{
        value: 1,
        label: '正常'
      }, {
        value: 2,
        label: '缺陷'
      }],
      value4: '',

      // 表格数据
      tableData: [{
        data: '3',
        group:'sso',
        s_time:'2018-10-10 10:10',
        e_time:'2018-10-10 10:10',
      },{
        data: '3',
        group:'sso',
        s_time:'2018-10-10 10:10',
        e_time:'2018-10-10 10:10',
      },{
        data: '3',
        group:'sso',
        s_time:'2018-10-10 10:10',
        e_time:'2018-10-10 10:10',
      }],

      // 动态标签JS
 

      // 日期值
      value1: '',
      value2: '',
      type: '',
      dialogDisable: false,
      propertiesData: [],
      propertiesid: '',
      content: '',
      total: 0,
      logtitle: '',
      loading: true,
    }
  },
  mounted () {
    this.$nextTick(()=>{
      this.getData();      
    })
   
  },
  methods: {
    details(row){
      this.logtitle = 'Version Detail';
      this.dialogDisable = true;
      var id = row.id;
      this.propertiesid = '';
      this.getdetails(id);
    },
    getdetails(id,falg){
      this.$$api_configguration_configselect({
        data:{
          id: id
        },
        fn: data => {
          this.loading = false;
          if(data.code == 200){
            this.propertiesData = data.data.configVersions;
            if(!falg){
              if(this.propertiesData.length>0){
                this.propertiesid = this.propertiesData[0].id;
                this.content = this.propertiesData[0].content;
              }
            }
          }else{
            this.$alert(data.message, '错误', {
              confirmButtonText: '确定'
            });
          }
        },
        errFn: () => {
          this.loading = false;
          this.$alert('访问失败，请稍后重试！', '错误', {
            confirmButtonText: '确定',
          });
        }
      })      
    },
    submitflsh() {
      if(this.propertiesData.length == 0){
        return;
      }
      var versionId = this.propertiesData[0].versionId;
      this.getdetails(versionId,true);
      this.selectproperties();
    },
    selectproperties() {
      var test = this.propertiesid ;
      for(let rowData of this.propertiesData){
        if(test == rowData.id){
          this.content = rowData.content
        }
      }
    },
    onSubmit() {
      this.tableData = [];
      this.getData();
    },
     // 获取列表数据
     getData() {
      let startDate = this.value1;
      let endDate = this.value2;
      var start = '';
      var end = '';
      if(startDate!=''){
        start = this.getDate(startDate);
      }
      if(endDate!=''){
        end = this.getDate(endDate);
      }
      // var startDate = this.value1.toString();
      // var endDate = this.value2.toString();
      this.$$api_historic_versionlist({
        data: {
          startDate : start,
          endDate : end,
          pageNum: this.pageNum,
          pageSize: this.pageSize,
        },
        fn: data => {
          this.loading = false;
          if(data.code == 200){
            this.total = data.data.page.total;
            this.tableData = data.data.list;
          }else{
            this.$alert(data.message, '错误', {
              confirmButtonText: '确定'
            });
          }
        },
        errFn: () => {
          this.loading = false;
          this.$alert('访问失败，请稍后重试！', '错误', {
            confirmButtonText: '确定',
          });
        }
      })
    },
    getDate(startDate){
      let Y = startDate.getFullYear() + '-';
      let M = (startDate.getMonth()+1 < 10 ? '0'+(startDate.getMonth()+1) : startDate.getMonth()+1) + '-';
      let D = startDate.getDate() <10 ? '0'+(startDate.getDate()) : startDate.getDate();  
      return Y+M+D;
    },
    handleClose(tag) {
      this.dynamicTags.splice(this.dynamicTags.indexOf(tag), 1);
    },

    showInput() {
      this.inputVisible = true;
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus();
      });
    },
    
    updateVersion(row) {
      this.$confirm('是否修改标签?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$$api_historic_versionupdate({
          data: {
            id: row.id,
            tag: row.tag
          },
          fn: data => {
            this.loading = false;
            if(data.code == 200){
              this.getData();
              this.$message({
                type: 'success',
                message: '修改成功!'
              });
            }else{
              this.getData();
              this.$message({
                type: 'success',
                message: '修改失败!'
              });
            }
          },
          errFn: () => {
            this.loading = false;
            this.getData();
            this.$alert('访问失败，请稍后重试！', '错误', {
              confirmButtonText: '确定',
            });
          }
        })
      }).catch(() => {
        this.getData();
        this.$message({
          type: 'info',
          message: '已取消操作'
        });          
      });
    },
    currentChange(i) {
      this.loading = true;
      this.pageNum = i;
      this.getData();
    },
    handleDelete(row) {
      this.$confirm('是否删除版本?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        var id = row.id;
        this.$$api_historic_versiondelete({
          data: {
            id: id
          },
          fn: data => {
            this.loading = false;
            if(data.code == 200){
              this.$message({
                type: 'success',
                message: '删除成功!'
              });
              this.getData();
            }else{
              this.$alert(data.message, '错误', {
                confirmButtonText: '确定'
              });
            }
          },
          errFn: () => {
            this.loading = false;
            this.$alert('访问失败，请稍后重试！', '错误', {
              confirmButtonText: '确定',
            });
          }
        })
      }).catch(() => {
        this.getData();
        this.$message({
          type: 'info',
          message: '已取消操作'
        });          
      });
   
    },
    deleteVersion(id) {
      this.$$api_historic_versiondelete({
        data: {
          id: id
        },
        fn: data => {
          this.loading = false;
          if(data.code == 200){
            this.getData();
          }else{
            this.$alert(data.message, '错误', {
              confirmButtonText: '确定'
            });
          }
        },
        errFn: () => {
          this.loading = false;
          this.$alert('访问失败，请稍后重试！', '错误', {
            confirmButtonText: '确定',
          });
        }
      })
    },
    handleInputConfirm() {
      let inputValue = this.inputValue;
      if (inputValue) {
        this.dynamicTags.push(inputValue);
      }
      this.inputVisible = false;
      this.inputValue = '';
    }
  }
}
