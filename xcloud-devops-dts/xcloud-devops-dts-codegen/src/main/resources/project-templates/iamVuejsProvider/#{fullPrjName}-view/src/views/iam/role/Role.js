import { getDay, transDate } from 'utils/'

export default {
    name: 'role',
    data() {
        return {
            //查询条件
            searchParams: {
                roleCode: '',
                nameZh: '',
                organizationId: '',
            },

            //分页信息
            total: 0,
            pageNum: 1,
            pageSize: 10,

            //弹窗表单
            saveForm: {
                nameZh: '',
                roleCode: '',
                menuIds: [],
                menuNameStrs: '',
                groupIds: [],
                groupNameStrs: '',
                organizationId: '',
            },

            isEdit: false,

            dialogVisible: false,
            dialogTitle: '',
            dialogLoading: false,

            tableData: [],

            //rolesData
            menuData: [],//tree
            menuDataList: [],//list

            defaultProps: {
                children: 'children',
                label: 'nameZh',
            },
            treeShow: false,

            groupTreeShow: false,
            groupsTreeData: [],

            rules: {
                roleCode: [{ required: true, message: 'Please input roleCode', trigger: 'blur' }],
                nameZh: [{ required: true, message: 'Please input displayName', trigger: 'blur' }],
                groups: [{ required: true, message: 'Please input role', trigger: 'change', validator: this.validatorGroups }],
                menu: [{ required: true, message: 'Please input menu', trigger: 'change', validator: this.validatorMenus }],

            },
            loading: false,

            filterText: '',
        }
    },

    watch: {
        filterText(val) {
            this.$refs.modulesTree3.filter(val);
        }
    },

    activated() {
        if(this.$route.query.id){
            this.searchParams.organizationId = this.$route.query.id;
            this.$refs.modulesTree3.setCheckedKeys([this.searchParams.organizationId], true);
            //this.$refs.modulesTree3.setChecked(this.searchParams.organizationId, true, false);
        }
        this.getData();
    },

    mounted() {

        this.getMenus();
        this.getGroupsTree();
    },

    methods: {

        onSubmit() {
            if (!this.searchParams.organizationId) {
                this.tableData = [];
                this.$message.error('请先选择机构');
                return;
            }
            this.getData();
        },

        currentChange(i) {
            //this.pageNum = i;
            this.getData();
        },

        validatorGroups(rule, value, callback) {
            if (this.saveForm.groupIds.length <= 0) {
                callback(new Error('roles is Empty'));
            } else {
                callback();
            }
        },


        validatorMenus(rule, value, callback) {
            if (this.saveForm.menuIds.length <= 0) {
                callback(new Error('menuIds is Empty'));
            } else {
                callback();
            }
        },

        getMenus() {
            this.$$api_iam_getMenuTree({
                data: {},
                fn: data => {
                    this.menuData = data.data.data;
                    this.menuDataList = data.data.data2;
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
            if (!this.searchParams.organizationId) {
                this.$message.error('请先选择机构');
                return;
            }
            this.getMenus();
            //this.getGroupsTree();

            this.isEdit = false;
            this.cleanSaveForm();
            this.dialogVisible = true;
            this.dialogTitle = '新增';
        },

        // 获取列表数据
        getData() {
            if (!this.searchParams.organizationId) {
                return;
            }
            this.loading = true;
            this.$$api_iam_roleList({
                data: {
                    roleCode: this.searchParams.roleCode,
                    nameZh: this.searchParams.nameZh,
                    organizationId: this.searchParams.organizationId,
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                },
                fn: data => {
                    this.loading = false;
                    this.total = data.data.total;
                    this.tableData = data.data.records;
                },
                errFn: () => {
                    this.loading = false;
                }
            })
        },

        cleanSaveForm() {
            this.saveForm = {
                nameZh: '',
                roleCode: '',
                menuIds: [],
                menuNameStrs: '',
                groupIds: [],
                groupNameStrs: '',
                organizationId: this.searchParams.organizationId,
            };
        },


        saveData() {
            this.dialogLoading = true;
            this.$refs['saveForm'].validate((valid) => {
                if (valid) {
                    this.$$api_iam_saveRole({
                        data: this.saveForm,
                        fn: data => {
                            this.dialogLoading = false;
                            this.dialogVisible = false;
                            this.getData();
                            this.cleanSaveForm();
                        },
                        errFn: () => {
                            this.dialogLoading = false;
                        }
                    });
                } else {
                    this.dialogLoading = false;
                }
            });
        },


        editData(row) {
            this.getMenus();
            this.cleanSaveForm();
            this.isEdit = true;
            if (!row.id) {
                return;
            }
            this.$$api_iam_roleDetail({
                data: {
                    id: row.id,
                },
                fn: data => {
                    this.saveForm = data.data.data;
                    if (this.$refs.modulesTree && this.saveForm.menuIds instanceof Array) {
                        this.$refs.modulesTree.setCheckedKeys(this.saveForm.menuIds);
                        this.checkChange();
                    }
                    this.saveForm.organizationId = this.searchParams.organizationId;
                }
            });
            this.dialogVisible = true;
            this.dialogTitle = '编辑';
        },


        delData(row) {
            if (!row.id) {
                return;
            }
            this.$confirm('Delete Option, Be Careful', 'Warning', {
                type: 'warning'
            }).then(() => {
                this.$$api_iam_delRole({
                    data: {
                        id: row.id,
                    },
                    fn: data => {
                        this.$message({
                            message: 'del success',
                            type: 'success'
                        });
                        this.getData();

                    },
                })
            }).catch(() => {

            });
        },


        //模块权限树展示
        focusDo() {
            if (this.$refs.modulesTree && this.saveForm.menuIds instanceof Array) this.$refs.modulesTree.setCheckedKeys(this.saveForm.menuIds)
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

        getChild(node, list) {
            if (node && node['children']) {
                let children = node['children'];
                for (let i = 0; i < children.length; i++) {
                    list.push(children[i]['id']);
                    this.getChild(children[i], list);
                }
            }
            return list;
        },

        //模块权限树选择
        checkChange(node, selfChecked, childChecked) {
            let checkedKeys = this.$refs.modulesTree.getCheckedKeys();
            if (selfChecked) {
                let parentList = this.getParent(this.menuDataList, node.parentId, []);
                checkedKeys = checkedKeys.concat(parentList)
                this.$refs.modulesTree.setCheckedKeys(checkedKeys)
            } else {
                let childList = this.getChild(node, []);
                checkedKeys = checkedKeys.filter(v => {
                    let flag = true;
                    for (var i = 0; i < childList.length; i++) {
                        if (v == childList[i]) {
                            flag = false
                        }
                    }
                    return flag
                });
                this.$refs.modulesTree.setCheckedKeys(checkedKeys)
            }
            let checkedNodes = this.$refs.modulesTree.getCheckedNodes();
            let moduleNameList = [];
            checkedNodes.forEach(function (item) {
                moduleNameList.push(item.nameZh)
            });
            this.saveForm.menuIds = checkedKeys;
            this.$set(this.saveForm, 'menuNameStrs', moduleNameList.join(','))
        },

        getParent(list, parentId, parentList) {
            if (parentId == '0') return;
            for (let i = 0; i < list.length; i++) {
                if (parentId == list[i].id) {
                    parentList.push(list[i].id);
                    this.getParent(list, list[i].parentId, parentList)
                }
            }
            return parentList
        },

        selectAllChildren(node, data) {
            if (!node.checked) { // select all childs
                let childList = this.getChild(data, []);
                let checkedKeys = this.$refs.modulesTree.getCheckedKeys();
                checkedKeys = checkedKeys.concat(data.id); // add self
                checkedKeys = checkedKeys.concat(childList); // add childs
                this.$refs.modulesTree.setCheckedKeys(checkedKeys)
            } else { // unselect all childs
                let childList2 = this.getChild(data, []);
                let checkedKeys2 = this.$refs.modulesTree.getCheckedKeys();
                // remove childs
                for (let i = 0; i < childList2.length; i++) {
                    let id = childList2[i];
                    let index = checkedKeys2.findIndex(e => e == id);
                    checkedKeys2.splice(index, index);
                }
                // remove self
                let index2 = checkedKeys2.findIndex(e => e == data.id);
                checkedKeys2.splice(index2, index2);
                this.$refs.modulesTree.setCheckedKeys(checkedKeys2)
            }
        },

        selectOrganization(node, selfChecked, childChecked) {
            if (selfChecked) {
                this.$refs.modulesTree3.setCheckedNodes([node]);
            }
            let checkedKeys = this.$refs.modulesTree3.getCheckedKeys();
            if (checkedKeys && checkedKeys.length > 0) {
                this.searchParams.organizationId = checkedKeys[0];
                this.onSubmit();
            } else {
                this.searchParams.organizationId = '';
                this.tableData = [];
            }
        },

        resize() {
            console.info("resize");
        },

        filterNode(value, data) {
            if (!value) return true;
            return data.nameZh.toLowerCase().indexOf(value.toLowerCase()) !== -1;
        }

    }
}
