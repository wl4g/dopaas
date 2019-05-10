
export default {
  name: 'configuration',
  data () {
    return {
      formInline: {
        user: '',
        instance: '',
        dataid:'',
        group:'',
        environment: ''
      },
      //页
      total: 0,
      pageNum: 1,
      pageSize: 10,
      loading: true,
      // 弹窗刚开始关闭状态
      dialogVisible: false,
      dialogLoading:true,
      dialogTitle: '',
      logtitle: '',
      
       //两个弹窗表单
       ruleForm: {
        nodeIdList: [],
        id: '',
        group: '',
        desc: '',
        environment: '',
        instance: '',
        tableData2: [],
      },
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
        filename: [
          { required: true, message: '文件名不能为空', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '配置内容不能为空', trigger: 'blur' },
          { min: 1, max: 5000, message: '长度在 1 到 5000 个字符', trigger: 'blur' }
        ],
        
      },
      // 默认显示一条空数据
      tableData1:[],
      showData: [],

      // 最大表格数据
      tableData: [
      //   {
      //   data: '3',
      //   group:'sso',
      //   node:'192.212.1.101/sso-node1:8080',
      //   name: '王小虎',
      //   address: '上海市普陀区金沙江路 1518 弄',
      //   time:'2018-10-10 10:25',
      // }
      // "id": 1,
      // "createBy": 1,
      // "createDate": "2017-10-09 00:00:00",
      // "updateBy": null,
      // "updateDate": null,
      // "delFlag": 0,
      // "enable": null,
      // "remark": "aa",
      // "sign": "sdg2423635474",
      // "signtype": "MD5",
      // "groupName": "sso",
      // "host": "web-node1",
      // "tag": "1",
      // "instanceCount": 0

    ],
    groupData:[],
    envirData: [],
    instanceData:[],
    envirFormData: [],
    instanceFormData: [],
    selectdisabled: false,
    instancelist: [],
    isedit: false,
    envId: '',
    groupId: '',
    dialogDisable: false,
    propertiesData: [],
    propertiesid: '',
    content: '',
    innerVisible: false,
    insidecontent: '',
    index: 0,
    checkfalg: true,
    multipleSelection: [],
    }
  },
  mounted () {
    this.getGroup();
    this.getData();
  },
      // 删除一列
  methods: {
    checkconf() {
      this.$$api_configguration_configcheck({
        data: {
          content: this.insidecontent
        },
        fn: data => {
          if(data.code == 200){
            this.checkfalg = true;
          }else{
            this.checkfalg = false;
          }
        },
        errFn: () => {
          this.$alert('校验异常！', '错误', {
            confirmButtonText: '确定',
          });
        }
      });
    },
    syncontent() {
      this.ruleForm.tableData2[this.index].content = this.insidecontent;
      this.innerVisible = false;
    },
    inputcontent(index,content) {
        this.index = index;
        this.innerVisible = true;
        this.insidecontent = content;
        this.checkconf();
    },
          // 增加一列表格
    deleteInstance(index, id) {
      if(id==undefined){
        this.ruleForm.tableData2.splice(index, 1);
      }else{
        this.$$api_instanceman_deleteInstance({
          data: {
            id: id
          },
          fn: data => {
            this.ruleForm.tableData2.splice(index, 1);
          },
          errFn: () => {
            
          }
        })
      }
      // }
    },
    addRow() {
      // if(saved){
        this.ruleForm.tableData2.push({
          fileName: '',
          type: 1,
          content: '',
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
        onSubmit(){
          this.loading = true;
          this.getData();
        },
        handleEdit(index,row){
          if(row.enable == false){
            var index = this.instancelist.indexOf(row.nodeId);
            if (index > -1) {
              this.instancelist.splice(index, 1);
            }
          }else{
            if(this.envId == ""){
              this.envId = row.envId;
              this.groupId = row.groupId;
              this.instancelist.push(row.nodeId);
            }else{
              if(this.envId == row.envId && this.groupId == row.groupId){
                this.instancelist.push(row.nodeId);
              }else{
                row.enable = false;
                this.$alert('请选择同一环境和分组下的节点！', '提示', {
                  confirmButtonText: '确定',
                });
              }
            }
          }
          if(this.instancelist.length == 0){
            this.envId = "";
            this.groupId = "";
          }
        },
        addOne(falg,row) {
          this.dialogVisible = true;
          this.dialogTitle = '新增';
          this.dialogLoading = false;
          this.ruleForm.tableData2 = [{  fileName: '',
          type: 1,
          content: '',}];
          if(falg == 1){
            this.dialogTitle = 'Configuration Edit';
            this.ruleForm.group = parseInt(row.groupId);
            this.ruleForm.environment = parseInt(row.envId);
            this.ruleForm.instance = row.nodeId;
            this.ruleForm.remark = row.remark;
            let id = row.id;
            this.dialogLoading = true;
            this.getdetails(id,false);
            this.isedit = true;
          }else{
            if(this.instancelist.length==0){
              this.tableData1 = [];
              this.ruleForm.group = "";
              this.ruleForm.environment = "";
              this.ruleForm.instance = "";
              this.ruleForm.remark = "";
              this.envirFormData = [];
              this.instanceFormData = [];
              this.isedit = false;
            }else{
              this.dialogTitle = '批量新增';
              this.ruleForm.remark = "";
              this.ruleForm.group = parseInt(this.groupId);
              this.ruleForm.environment = parseInt(this.envId);
              this.ruleForm.instance = "";
              this.isedit = true;
            }
          }
        },
        getinstance(flag){
          var groupId;
          var environmentId;
          if(flag==0){
            this.instanceData=[];
            this.formInline.instance="";
            groupId = this.formInline.group;
            environmentId = this.formInline.environment;
            if(environmentId==""||groupId==""){
              return;
            }
          }else{
            this.instanceFormData=[];
            this.ruleForm.instance="";
            groupId=this.ruleForm.group;
            environmentId = this.ruleForm.environment;
            if(groupId==""){
              return;
            }
          }
          this.$$api_instanceman_instancelist({
            data: {
              groupId: groupId,
              envId: environmentId
            },
            fn: data => {
              if(data.code == 200){
                if(flag==0){
                  this.instanceData = data.data.instancelist;
                }else{
                  var title = this.dialogTitle;
                  var length = this.instancelist.length;
                  if(environmentId!="" && length==0 && title != "编辑"){
                    for(let rowData of data.data.instancelist){
                      this.instancelist.push(rowData.id);
                    }
                    this.instanceFormData = data.data.instancelist;
                  }
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
        getenvir(flag){
          var groupId;
          if(flag==0){
            this.envirData=[];
            this.formInline.environment="";
            groupId=this.formInline.group;
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
                  this.instanceData = [];
                  this.envirData = data.data.envlist;
                }else{
                  this.instanceFormData = [];
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
         // 获取列表数据
        getData() {
          this.$$api_configguration_lists({
            data: {
              groupId : this.formInline.group,
              envId : this.formInline.environment,
              instanceId: this.formInline.instance,
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
        deleteRow(index, rows) {
          rows.splice(index, 1);
        },
        // 增加一列表格
        details(row){
          this.showData = [];
          this.logtitle = 'Configuration Detail';
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
                this.dialogLoading = false;
                this.propertiesData = data.data.configVersions;
                this.ruleForm.tableData2 = data.data.configVersions;
                if(!falg){
                  if(this.propertiesData.length>0){
                    this.propertiesid = this.propertiesData[0].id;
                    this.content = this.propertiesData[0].content;
                  }
                }
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
        submitForm(formName) {
          this.$refs[formName].validate((valid) => {
            if (valid) {
              this.submit();
            } else {
              console.log('error submit!!');
              return false;
            }
          });
        },
        handleSelectionChange(val) {
          let _this = this;
          _this.instancelist = [];
          let indexlist = [];
          _this.envId = "";
          _this.groupId = "";
          val.forEach((val1, index) => {
            if(_this.envId == ""){
              _this.envId = val1.envId;
              _this.groupId = val1.groupId;
              _this.instancelist.push(val1.nodeId);
            }else{
              if(_this.envId == val1.envId && _this.groupId == val1.groupId){
                _this.instancelist.push(val1.nodeId);
              }else{
                indexlist.push(val1);
              }
            }
          })
          if(indexlist.length != 0){
            indexlist.forEach((indexval) =>{
              var index = val.indexOf(indexval);
              if (index > -1) {
                val.splice(index, 1);
              }
            })
            _this.$alert('请选择同一环境和分组下的节点！', '提示', {
              confirmButtonText: '确定',
            });
          } 
          if(_this.instancelist.length == 0){
            _this.envId = "";
            _this.groupId = "";
          }          
        },
        submit(){
          this.selectdisabled = false;
          var instances = this.ruleForm.instance;
          var nodeIdList = [];
          if(instances == ""){
            nodeIdList = this.instancelist;
          }else{
            nodeIdList.push(this.ruleForm.instance);
          }
          // this.tableData1 = [];
          // this.tableData1.push({
          //   type: this.ruleForm.type,
          //   content: this.ruleForm.content
          // })
          this.$$api_configguration_configset({
            data: {
              configGurations: this.ruleForm.tableData2,
              deptId: 1,
              createBy: 1,
              updateBy: 1,
              envId: this.ruleForm.environment,
              remark: this.ruleForm.remark,
              id: this.ruleForm.group,
              nodeIdList: nodeIdList,
              content: '',
              groupId: this.ruleForm.group
            },
            fn: data => {
              if(data.code == 200){
                this.dialogVisible = false;
                this.instancelist = [];
                this.getData();
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
      },
     

}
