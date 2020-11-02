import { getDay, transDate } from 'utils/'

export default {
    name: 'user',
    data() {
        return {
            //查询条件
            searchParams: {
                userName: '',
                nameZh: '',
                roleId: '',
            },

            //分页信息
            total: 0,
            pageNum: 1,
            pageSize: 10,

            //弹窗表单
            saveForm: {
                nameEn: '',
                nameZh: '',
                userName: '',
                oldPassword: '',
                password: '',
                email: '',
                phone: '',
                remark: '',
                roleIds: [],

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
                label: 'nameZh',
            },
            treeShow: false,


            //验证
            rules: {
                userName: [{ required: true, message: 'Please input userName', trigger: 'change' }],
                nameEn: [{ required: true, message: 'Please input nameEn', trigger: 'change' }],
                nameZh: [{ required: true, message: 'Please input displayName', trigger: 'change' }],
                password: [{ required: true, message: 'Please input password', trigger: 'change' }],
                roleIds: [{ required: true, message: 'Please select role', trigger: 'change' }],

            },
            loading: false
        }
    },

    activated() {
        let roleId = this.$route.query.roleId;
        if(roleId){
            this.searchParams.roleId = roleId;
        }

        this.getData();
        this.getRoles();
    },

    mounted() {

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


        addData() {
            this.getRoles();

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
                    nameEn: this.searchParams.nameEn,
                    roleId: this.searchParams.roleId,
                    nameZh: this.searchParams.nameZh,
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
                nameZh: '',
                userName: '',
                oldPassword: '',
                password: '',
                email: '',
                phone: '',
                remark: '',
                roleIds: [],
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
                deploy: global.iam,
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


    }
}
