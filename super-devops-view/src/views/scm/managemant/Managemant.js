
export default {
  name: 'managemant',
  data() {
    return {
      formInline: {
        user: '',
        region: ''
      },
      //页
      total: 0,
      pageNum: 1,
      pageSize: 10,
      loading: true,
      // 表格数据
      tableData: [],
      //新增按钮弹窗功能,默认隐藏
      dialogVisible: false,
      // formatter:[],
      //两个弹窗表单
      ruleForm: {
        id: '',
        group: '',
        desc: '',
        environment: '',
         // 表格内容
        tableData1: [{
          groupid: '',
          name: '',
          remark: '',
        }],
      },
      grouplist:[],
      options: [{
          value: 1,
          label: 'dev'
        }, {
          value: 2,
          label: 'test'
        }],
      dialogLoading: false,
      // 表单规则
      rules: {
        group: [
          { required: true, message: '请输入分组名', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        environment: [
          { required: true, message: '请输入环境名', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
        ],
        desc: [
          { required: true, message: '请填写描述', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
        ],
        remark: [
          { required: true, message: '环境描述不能为空', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '环境名称不能为空', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
        ]
      },
      selectList: [],
      dialogTitle: '',
      selectProp: {
        group: '',
        environment: '',
      },
      isSaved: true,
      instanceIndex: '',
      // 分组名
      groupData: [],
      envirData: [],
      envirFormData: [],
      instanceData: []
    }
  },
  mounted() {
    this.getGroup();
    this.getData();
  },
  // 删除一列
  methods: {
      getenvir(flag){
        var groupId;
        if(flag==0){
          this.envirData=[];
          this.selectProp.environment="";
          groupId=this.selectProp.group
        }else{
          this.envirFormData=[];
          this.ruleForm.environment="";
          groupId=this.ruleForm.group
        }
        this.$$api_managemant_envirlist({
          data: {
            groupId: groupId
          },
          fn: data => {
            if(data.code == 200){
              if(flag==0){
                this.envirData = data.data.envlist;
              }else{
                this.envirFormData = data.data.envlist;
              }
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
      // 获取分组名称
      getGroup() {
        this.$$api_managemant_grouplist({
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
    deleteRow(id) {
      this.loading = true;
      this.$$api_managemant_delete({
        data: {
          id: id
        },
        fn: data => {
          this.getData();
        },
        errFn: () => {
          
        }
      })
    },
    // 增加一列表格
    addRow() {
        this.ruleForm.tableData1.push({
          groupid: '',
          name: '',
          remark: '',
        })
    },
    // 单选&多选&全选
    toggleSelection(rows) {
      if (rows) {
        rows.forEach(row => {
          this.$refs.multipleTable.toggleRowSelection(row);
        });
      } else {
        this.$refs.multipleTable.clearSelection();
      }
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    // 获取列表数据
    getData() {
      this.$$api_managemant_groupenvlist({
        data: {
          name : this.selectProp.group,
          evnsci : this.selectProp.environment,
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
    currentChange(i) {
      this.loading = true;
      this.pageNum = i;
      this.getData();
    },
    addOne() {
      this.dialogVisible = true;
      this.dialogTitle = '新增';
      this.ruleForm.tableData1 =  [{
        groupid: '',
        name: '',
        remark: '',
      }];
    },
    //新增或编辑应用组 
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.saveOfUpdateForm();
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    saveOfUpdateForm() {
      this.dialogLoading = true;
      this.$$api_managemant_envconfigsave({
        data: {
          environment: this.ruleForm.tableData1,
          deptId: 1,
          createBy: 1,
          updateBy: 1,
          name: this.ruleForm.group,
          remark: this.ruleForm.desc,
          id: this.ruleForm.id
        },
        fn: data => {
          this.dialogLoading = false;
          if(data.code == 200){
            this.dialogVisible = false;
            this.getData();
          }else{
            this.$alert(data.message, '错误', {
              confirmButtonText: '确定'
            });
          }
          
        },
        errFn: () => {
          this.dialogLoading = false;
          this.$alert('访问失败，请稍后重试！', '错误', {
            confirmButtonText: '确定',
          });
        }
      })
    },
    selectGo(data) {
      this.selectList = data;
    },
    //查询节点
    inquiry(id) {
      this.dialogLoading = true;
      this.$$api_managemant_envlist({
        data: {
          id: id
        },
        fn: data => {
          if(data.code == 200){
            this.ruleForm.id = data.data.iof.id;
            this.ruleForm.group = data.data.iof.name;
            this.ruleForm.desc  = data.data.iof.remark;
            if(null != data.data.iof.environment){
              this.ruleForm.tableData1 = data.data.iof.environment;
            }
            
          }else{
            this.$alert(data.message, '错误', {
              confirmButtonText: '确定'
            });
          }
          this.dialogLoading = false;
        },
        errFn: () => {
          this.dialogLoading = false;
          this.$alert('访问失败，请稍后重试！', '错误', {
            confirmButtonText: '确定',
          });
        }
      })
    },
    editOne(id) {
      this.dialogTitle = 'Group Environment Edit';
      this.dialogVisible = true;
      this.inquiry(id);

    },
    closeDialog(){
      this.ruleForm.tableData1 = [{
        groupid: '',
        name: '',
        remark: '',
      }],
      this.ruleForm = {
        id: '',
        group: '',
        desc: '',
        environment: '',
      }
    },
    queryList(){
      this.loading = true;
      this.getData();
    },
    handleEdit(index,row){
      if('' === this.instanceIndex) this.instanceIndex = index;
      // if(index != this.instanceIndex && false === this.isSaved){
      //   this.$alert('请先保存上一个已修改的节点！', '提示', {
      //     confirmButtonText: '确定',
      //   });
      // }else{
      //   this.isSaved = false;
        this.instanceIndex = index;
      //}
    },
    deleteInstance(index, id) {
      if(id==undefined){
        this.ruleForm.tableData1.splice(index, 1);
      }else{
        this.$$api_instanceman_deleteenv({
          data: {
            id: id
          },
          fn: data => {
            this.ruleForm.tableData1.splice(index, 1);
          },
          errFn: () => {
            
          }
        })
      }
     // }
    },
    submitInstance(rowData){
      this.dialogLoading = true;
      if(typeof rowData.id != 'undefined' && '' != rowData.id){
        this.$$api_managemant_updateInstance({
          data: {
            id: rowData.id,
            enable: rowData.enable===true?1:0,
            host: rowData.host,
            ip: rowData.ip,
            port: rowData.port,
            remark: rowData.remark
          },
          fn: data => {
            this.dialogLoading = false;
            if(data.code == 200){
              this.isSaved = true;
              this.$message({
                message: '修改成功！',
                type: 'success'
              });
            }else{
              this.$alert(data.message, '错误', {
                confirmButtonText: '确定'
              });
            }
          },
          errFn: () => {
            this.dialogLoading = false;
            this.$alert('访问失败，请稍后重试！', '错误', {
              confirmButtonText: '确定',
            });
          }
        })
      }else{
        var data = [{
            enable: rowData.enable===true?1:0,
            host: rowData.host,
            ip: rowData.ip,
            port: rowData.port,
            remark: rowData.remark
        }];
        this.$$api_managemant_insertInstance({
          data: {
            id: this.ruleForm.id,
            appInstance: data
          },
          fn: data => {
            this.dialogLoading = false;
            if(data.code == 200){
              this.isSaved = true;
              this.dialogVisible = false;
              this.getData();
            }else{
              this.$alert(data.message, '错误', {
                confirmButtonText: '确定'
              });
            }
          },
          errFn: () => {
            this.dialogLoading = false;
            this.$alert('访问失败，请稍后重试！', '错误', {
              confirmButtonText: '确定',
            });
          }
        })
      }
    },
  },

}
