import { getDay, transDate } from 'utils/'

export default {
    name: 'user',
    data() {
        return {
            //查询条件
            searchParams: {
                userName: '',
                displayName: '',
            },

            //分页信息
            total: 0,
            pageNum: 1,
            pageSize: 10,

            //弹窗表单
            saveForm: {
                displayName: '',
                userName: '',
                oldPassword: '',
                password: '',
                email: '',
                phone: '',
                remark: '',
                roleIds: [],
                groupIds: [],
                groupNameStrs: '',

            },

            isEdit: false,

            dialogVisible: false,
            dialogTitle: '',
            dialogLoading: false,

            tableData: [],

            //rolesData
            rolesData: [],
            groupsTreeData: [],


            defaultProps: {
                children: 'children',
                label: 'displayName',
            },
            treeShow: false,


            //验证
            rules: {
                userName: [{ required: true, message: 'Please input userName', trigger: 'blur' }],
                displayName: [{ required: true, message: 'Please input displayName', trigger: 'blur' }],
                password: [{ required: true, message: 'Please input password', trigger: 'blur' }],
            },
            loading: false
        }
    },

    mounted() {
        this.getData();
        this.getRoles();
        this.getGroupsTree();
    },

    methods: {

        onSubmit() {
            this.getData();
        },

        currentChange(i) {
            this.pageNum = i;
            this.getData();
        },

        getRoles() {
            this.$$api_iam_getRoles({
                data: {},
                fn: data => {
                    this.rolesData = data.data.data;
                }
            })
        },

        getGroupsTree() {
            this.$$api_iam_getGroupsTree({
                data: {},
                fn: data => {
                    this.groupsTreeData = data.data.data;
                }
            })
        },


        addData() {
            this.getRoles();
            this.getGroupsTree();

            this.isEdit = false;
            this.cleanSaveForm();
            this.dialogVisible = true;
            this.dialogTitle = '新增';
        },

        // 获取列表数据
        getData() {
            this.loading = true;
            this.$$api_iam_userList({
                data: {
                    userName: this.searchParams.userName,
                    displayName: this.searchParams.displayName,
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                },
                fn: data => {
                    this.total = data.data.total;
                    this.tableData = data.data.records;
                    this.loading = false;
                },
                errFn: () => {
                    this.loading = false;
                }
            })
        },

        cleanSaveForm() {
            this.saveForm = {
                displayName: '',
                userName: '',
                oldPassword: '',
                password: '',
                email: '',
                phone: '',
                remark: '',
                roleIds: [],
                groupIds: [],
                groupNameStrs: '',
            };
        },


        save() {
            this.dialogLoading = true;

            this.$refs['saveForm'].validate((valid) => {
                if (valid) {
                    if (this.saveForm.oldPassword != this.saveForm.password || this.saveForm.oldPassword == '') {//need update password
                        this.saveDataWithPassword();
                    } else {//needn't update password
                        this.saveData();
                    }
                } else {
                    this.dialogLoading = false;
                }
            })

        },

        saveDataWithPassword() {
            const that = this;
            const loginAccount = that.saveForm.userName;
            new IAMCore({
                deploy: {
                    // e.g. http://127.0.0.1:14040/iam-server
                    // e.g. http://localhost:14040/iam-server
                    // e.g. http://iam.wl4g.debug/iam-server
                    //baseUri: "http://localhost:14040/iam-server",
                    defaultTwoDomain: "iam", // IAM后端服务部署二级域名，当iamBaseUri为空时，会自动与location.hostnamee拼接一个IAM后端地址.
                    defaultContextPath: "/iam-server"
                },
            }).safeCheck(loginAccount, function (res) {
                if (res.data && res.data.checkGeneric && res.data.checkGeneric.secretKey) {
                    let secret = res.data.checkGeneric.secretKey;
                    // The api supports dynamic algorithms(ECC/RSA/...) The default algorithm is used RSA
                    let password = IAMCrypto.RSA.encryptToHexString(secret, that.saveForm.password);
                    that.saveData(password);
                }
                that.dialogLoading = false;
            });
        },

        saveData(password) {
            this.saveForm.password = password;
            this.$$api_iam_saveUser({
                data: this.saveForm,
                fn: data => {
                    this.dialogVisible = false;
                    this.dialogLoading = false;
                    this.getData();
                    this.cleanSaveForm();
                },
                errFn: () => {
                    this.dialogLoading = false;
                }
            });
        },


        editData(row) {
            this.getRoles();
            this.getGroupsTree();

            this.cleanSaveForm();
            this.isEdit = true;
            if (!row.id) {
                return;
            }
            this.$$api_iam_userDetail({
                data: {
                    userId: row.id,
                },
                fn: data => {
                    this.saveForm = data.data.data;
                    this.saveForm.oldPassword = this.saveForm.password;
                    if (this.$refs.modulesTree && this.saveForm.groupIds instanceof Array) {
                        this.$refs.modulesTree.setCheckedKeys(this.saveForm.groupIds);
                        this.checkChange();
                    }
                }
            });
            this.dialogVisible = true;
            this.dialogTitle = '编辑';
        },


        delData(row) {
            if (!row.id) {
                return;
            }
            this.$confirm('Confirm?', 'warning', {
                confirmButtonText: 'OK',
                cancelButtonText: 'Cancel',
                type: 'warning'
            }).then(() => {
                this.$$api_iam_delUser({
                    data: {
                        userId: row.id,
                    },
                    fn: data => {
                        this.$message({
                            message: 'del success',
                            type: 'success'
                        });
                        this.getData();
                    }
                })
            }).catch(() => {
                //do nothing
            });
        },


        //模块权限树展示
        focusDo() {
            if (this.$refs.modulesTree && this.saveForm.groupIds instanceof Array) this.$refs.modulesTree.setCheckedKeys(this.saveForm.groupIds)
            this.treeShow = !this.treeShow;
            let _self = this;
            this.$$lib_$(document).bind("click", function (e) {
                let target = _self.$$lib_$(e.target);
                if (target.closest(".noHide").length == 0 && _self.treeShow) {
                    _self.treeShow = false;
                }
                e.stopPropagation();
            })
        },

        //模块权限树选择
        checkChange(node, selfChecked, childChecked) {
            var checkedKeys = this.$refs.modulesTree.getCheckedKeys();
            var checkedNodes = this.$refs.modulesTree.getCheckedNodes();

            let moduleNameList = [];
            checkedNodes.forEach(function (item) {
                moduleNameList.push(item.displayName)
            });
            this.saveForm.groupIds = checkedKeys;
            //this.saveForm.groupNameStrs = moduleNameList.join(',');
            this.$set(this.saveForm, 'groupNameStrs', moduleNameList.join(','))

        },


    }
}
