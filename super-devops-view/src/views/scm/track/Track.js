
export default {
  name: 'track',
  data () {
    return {
      formInline: {
        type: '',
        region: '',
        group: '',
        environment: '',
        instance: ''
      },
      options: [{
        value: 1,
        label: '成功'
      }, {
        value: 0,
        label: '未更改'
      }, {
        value: -1,
        label: '更新失败'
      }],
      tableData: [
      //   {
      //   data: '3',
      //   group:'sso',
      //   tag:'发布',
      //   time:'2018-10-10 10:10:10',
      // },{
      //   data: '3',
      //   group:'sso',
      //   tag:'回滚',
      //   time:'2018-10-10 10:10:10',
      // },{
      //   data: '3',
      //   group:'sso',
      //   tag:'发布',
      //   time:'2018-10-10 10:10:10',
      // }
    ],
      value1: '',
      value2: '',
      groupData: [],
      envirFormData: [],
      instanceFormData: [],
      loading: true,
      total: 0,
      detail: {},
      dialogLoading: true,
      dialogDisable: false,
      logtitle: ''

    }
  },
  mounted () {
    this.getGroup();
    this.getData();
  },
  methods: {
        rollback(row){
          this.$confirm('是否回滚版本?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            let id = row.id;
            let remark = row.remark;
            let groupId = row.groupId;
            let envId = row.envId;
            let instanceId = row.instanceId;
            this.$$api_track_releaseback({
              data: {
                id: id,
                groupId : groupId,
                instanceId : instanceId,
                envId: envId,
                remark: remark
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
        onSubmit(){
          this.loading = true;
          this.getData();
        },
        // 获取列表数据
        getData() {
          let groupId = this.formInline.group;
          let envId = this.formInline.environment;
          let instanceId = this.formInline.instance;
          this.$$api_track_releaselist({
            data: {
              groupId : groupId,
              instanceId : instanceId,
              envId: envId,
              pageNum: this.pageNum,
              pageSize: this.pageSize,
            },
            fn: data => {
              this.loading = false;
              if(data.code == 200){
                this.total = parseInt(data.data.page.total);
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
    getinstance(){
      this.instanceFormData=[];
      this.formInline.instance="";
      var groupId = this.formInline.group;
      var environmentId = this.formInline.environment;
      if(environmentId==""||groupId==""){
        return;
      }
      this.$$api_instanceman_instancelist({
        data: {
          groupId: groupId,
          envId: environmentId
        },
        fn: data => {
          if(data.code == 200){
            this.instanceFormData = data.data.instancelist;
          }else{
            this.$alert(data.message, '错误', {
              confirmButtonText: '确定'
            });
          }
        },
        errFn: () => {
          this.$alert('访问失败，请稍后重试！', '错误', {
            confirmButtonText: '确定',
          });
        }
      })
    },
    getGroup() {
      this.$$api_instanceman_grouplist({
        fn: data => {
          if(data.code == 200){
            this.groupData = data.data.grouplist;
          }else{
            this.$alert(data.message, '错误', {
              confirmButtonText: '确定'
            });
          }
        },
        errFn: () => {
          this.$alert('访问失败，请稍后重试！', '错误', {
            confirmButtonText: '确定',
          });
        }
      })
    },
    getenvir(){
      this.envirFormData=[];
      this.formInline.environment="";
      var groupId=this.formInline.group;
      if(groupId==""){
        return;
      }
      this.$$api_instanceman_envirlist({
        data: {
          groupId: groupId
        },
        fn: data => {
          if(data.code == 200){
            this.instanceFormData = [];
            this.envirFormData = data.data.envlist;
          }else{
            this.$alert(data.message, '错误', {
              confirmButtonText: '确定'
            });
          }
        },
        errFn: () => {
          this.$alert('访问失败，请稍后重试！', '错误', {
            confirmButtonText: '确定',
          });
        }
      })
    },
    filterTag(value, row) {
      return row.tag === value;
    },
    currentChange(i) {
      this.loading = true;
      this.pageNum = i;
      this.getData();
    },
    details(row){
      this.dialogLoading = true;
      this.dialogDisable = true;
      this.logtitle = 'History Detail';
      let instanceId = row.nodeId;
      let releaseId = row.historyId;
      this.$$api_track_reledetailselect({
        data:{
          instanceId: instanceId,
          releaseId: releaseId
        },
        fn: data => {
          if(data.code == 200){
            this.dialogLoading = false;
            this.detail = data.data.detail;
          }else{
            this.dialogLoading = false;
            this.$alert(data.message, '错误', {
              confirmButtonText: '确定'
            });
          }
        },
        errFn: () => {
          this.dialogLoading = false;
          this.loading = false;
          this.$alert('访问失败，请稍后重试！', '错误', {
            confirmButtonText: '确定',
          });
        }
      })   
    },
  }
}
