
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
        group: [],
        desc: '',
        environment: [],
        tableData1: [{
          enable: true,
          host: '',
          ip: '',
          port: '',
          remark: '',
        }]
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
          {type:'number', required: true, message: '请选择分组', trigger: 'change' },
        ],
        environment: [
          {type:'number', required: true, message: '请选择环境', trigger: 'change' }
        ],
        remark: [
          { required: true, message: '描述内容不能为空', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
        ],
        ip: [
          { required: true, message: 'ip不能为空', trigger: 'blur' },
          { pattern: /^(1\d{2}|2[0-4]\d|25[0-5]|[1-9]\d|[1-9])(\.(1\d{2}|2[0-4]\d|25[0-5]|[1-9]\d|\d)){3}$/, message: 'ip格式错误' }
        ],
        port: [
          { type:'number',required: true, message: '端口不能为空', trigger: 'blur'},
          { pattern: /^([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-4]\d{4}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/, message: '端口必须为0-65535之间的数字' }
          //{ required: true, message: 'ip不能为空', trigger: 'blur' }
        ],
        host: [
          { required: true, message: '主机名不能为空', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
          //{ required: true, message: 'ip不能为空', trigger: 'blur' }
        ],
        
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
      instanceData: [],
      selectdisabled: false
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
          groupId=this.selectProp.group;
          if(groupId==""){
            return;
          }
        }else{
          this.envirFormData=[];
          this.ruleForm.environment="";
          groupId=this.ruleForm.group
          if(groupId==""){
            return;
          }
        }
        this.$$api_instanceman_envirlist({
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
    deleteRow(id) {
      this.loading = true;
      this.$$api_instanceman_deleteenv({
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
      var saved = true;
      for(let rowData of this.ruleForm.tableData1){
        if(typeof rowData.id === 'undefined') saved = false
      }
      // if(saved){
        this.ruleForm.tableData1.push({
          enable: true,
          host: '',
          ip: '',
          port: '',
          remark: '',
        })
      // }else{
      //   if(this.ruleForm.tableData1.length === 1){
      //     this.$alert('请逐一添加并保存节点！', '提示', {
      //       confirmButtonText: '确定',
      //     });
      //   }else{
      //     this.$alert('有新增的节点尚未保存，请保存后重试！', '提示', {
      //       confirmButtonText: '确定',
      //     });
      //   }
      // }
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
      this.$$api_instanceman_list({
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
    this.ruleForm.tableData1 = [{ enable: true,
      host: '',
      ip: '',
      port: '',
      remark: '',}];
      this.ruleForm.group = "";
      this.ruleForm.environment = "";
      this.envirFormData = [];
      this.dialogVisible = true;
      this.dialogTitle = '新增';
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
      //}
    },
    saveOfUpdateForm() {
      this.selectdisabled = false;
      this.dialogLoading = true;
        for(var i = 0; i < this.ruleForm.tableData1.length; i++){
          this.ruleForm.tableData1[i].enable === true? this.ruleForm.tableData1[i].enable = 1 : this.ruleForm.tableData1[i].enable = 0;
        }
      this.$$api_instanceman_insert({
        data: {
          appInstance: this.ruleForm.tableData1,
          deptId: 1,
          createBy: 1,
          updateBy: 1,
          envId: this.ruleForm.environment,
          remark: this.ruleForm.desc,
          id: this.ruleForm.group,
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
    inquiry(id,envId) {
      this.dialogLoading = true;
      this.$$api_instanceman_select({
        data: {
          id: id,
          evnsci: envId
        },
        fn: data => {
          if(data.code == 200){
            this.ruleForm.id = data.data.iof.id;
            this.ruleForm.group = data.data.iof.id;
            this.ruleForm.desc = data.data.iof.remark;
            this.ruleForm.environment = data.data.iof.appInstance[0].envId;
            if(null != data.data.iof.appInstance){
              for(var i = 0; i < data.data.iof.appInstance.length; i++){
                data.data.iof.appInstance[i].enable === 1? data.data.iof.appInstance[i].enable = true : data.data.iof.appInstance[i].enable = false;
              }
              this.ruleForm.tableData1 = data.data.iof.appInstance;
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
    editOne(row) {
      this.dialogTitle = 'Instance information edit';
      this.dialogVisible = true;
      this.selectdisabled = true;
      this.inquiry(row.id,row.evnsci);

    },
    closeDialog(){
      this.ruleForm.tableData1 = [{
        enable: false,
        host: '',
        ip: '',
        port: '',
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
        this.$$api_instanceman_deleteInstance({
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
        this.$$api_instanceman_updateInstance({
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
        this.$$api_instanceman_insertInstance({
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
