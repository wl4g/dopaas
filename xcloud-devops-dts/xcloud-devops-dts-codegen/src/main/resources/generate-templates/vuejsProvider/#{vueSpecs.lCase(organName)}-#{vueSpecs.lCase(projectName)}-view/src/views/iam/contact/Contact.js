import {transDate, getDay} from 'utils/'

export default {
    name: 'contact',
    data() {
        return {
            //查询条件
            searchParams: {
                name: '',
            },

            //分页信息
            total: 0,
            pageNum: 1,
            pageSize: 10,

            //弹窗表单
            saveForm: {
                id: '',
                name: '',
                groups: [],
                contactChannels: [],
            },

            contactGroupData: [],
            groupTotal: 0,
            groupPageNum: 1,
            groupPageSize: 10,

            dialogVisible: false,
            dialogTitle: '',
            dialogLoading: false,

            contactData: [],



            //group
            //查询条件
            searchGroupParams: {
                name: '',
            },

            rules: {
                name: [{ required: true, message: 'Please input name', trigger: 'blur' }],
            },
            submitLoading: false,
            groupLoading: false
        }
    },

    mounted() {
        this.groupList();
        this.getData();
        this.getGroupData();

    },

    methods: {

        onSubmit() {
            this.getData();
        },

        currentChange(i) {
            this.pageNum = i;
            this.getData();
        },

        currentChangeGroup(i) {
            //this.loading = true;
            this.groupPageNum = i;
            this.getGroupData();
        },



        // 获取列表数据
        getData() {
            this.submitLoading = true;

            this.$$api_iam_contactList({
                data: {
                    name: this.searchParams.name,
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                },
                fn: data => {
                    this.submitLoading = false;
                    this.total = data.data.total;
                    this.contactData = data.data.records;
                },
                errFn: () => {
                    this.submitLoading = false;
                }
            })
        },

        cleanSaveForm() {
            this.saveForm = {
                id: '',
                name: '',
                groups: [],
                contactChannels: [],
            };
        },
        addContact() {
            this.groupList();

            this.cleanSaveForm();
            this.dialogVisible = true;
            this.dialogTitle = 'Add notification contacts';
        },
        // 获取列表数据
        groupList() {
            this.$$api_iam_groupList({
                data: {},
                fn: data => {
                    this.contactGroupData = data.data;
                }
            })
        },

        saveContact() {
            this.dialogLoading = true;
            this.$refs['saveForm'].validate((valid) => {
                if (valid) {
                    this.$$api_iam_saveContact({
                        data: this.saveForm,
                        fn: data => {
                            this.dialogLoading = false;
                            if (data.code == 200) {
                                this.dialogVisible = false;
                                this.getData();
                                this.cleanSaveForm();
                            } else {
                                this.$alert(data.message, '错误', {
                                    confirmButtonText: '确定'
                                });
                            }
                        },
                        errFn: () => {
                            this.dialogLoading = false;
                            this.$message.error('request error');
                        }
                    });
                } else {
                    this.dialogLoading = false;
                    console.log('error submit!!');
                    return false;
                }
            });
        },

        editContact(row) {
            this.groupList();
            if (!row.id) {
                return;
            }
            this.$$api_iam_contactDetail({
                data: {
                    id: row.id,
                },
                fn: data => {
                    this.saveForm = data.data.contact;
                }
            })

            this.dialogVisible = true;
            this.dialogTitle = '编辑';
        },


        delContact(row) {
            if (!row.id) {
                return;
            }
            this.$confirm('Confirm?', 'warning', {
                confirmButtonText: 'OK',
                cancelButtonText: 'Cancel',
                type: 'warning'
            }).then(() => {
                this.$$api_iam_delContact({
                    data: {
                        id: row.id,
                    },
                    fn: data => {
                        this.$message({
                            message: '删除成功',
                            type: 'success'
                        });
                        this.getData();
                    }
                })
            }).catch(() => {
                //do nothing
            });

        },


//=============================group=============================
        getGroupData() {
            this.groupLoading = true;
            this.$$api_iam_contactGroupList({
                data: {
                    name: this.searchGroupParams.name,
                    pageNum: this.groupPageNum,
                    pageSize: this.groupPageSize,
                },
                fn: data => {
                    this.groupLoading = false;
                    this.groupTotal = data.data.total;
                    this.contactGroupData = data.data.records;
                },
                errFn: () => {
                    this.groupLoading = false;
                }
            })
        },

        saveGroup(row) {
            if(!row){
                return;
            }
            this.$$api_iam_saveContactGroup({
                data: {
                    id: row.id,
                    name: row.name,
                },
                fn: data => {
                    this.$message({
                        message: 'Success',
                        type: 'success'
                    });
                    this.getGroupData();
                }
            })
        },

        addGroup(){
            this.contactGroupData.push({
                id: '',
                name: '',
            })
        },

        delContactGroup(row) {
            if(!row){
                return;
            }
            this.$$api_iam_delContactGroup({
                data: {
                    id: row.id,
                },
                fn: data => {
                    this.$message({
                        message: '删除成功',
                        type: 'success'
                    });
                    this.getGroupData();
                }
            })
        },

        deleteRow(index) {
            this.saveForm.contactChannels.splice(index, 1);
        },

        addRow() {
            if(!this.saveForm.contactChannels){
                this.saveForm.contactChannels = [];
            }
            this.saveForm.contactChannels.push({
                kind: '',
                primaryAddress: '',
                enable: 1,
                timeOfFreq: '',
                numOfFreq: '',
            })
        },



    }
}
