import { transDate, getDay } from 'utils/'
import dictutil from "../../../common/dictutil";
import { store } from "../../../utils";

export default {
    name: 'dict',
    data() {
        return {
            //查询条件
            searchParams: {
                key: '',
                label: '',
                type: '',
                remark: '',
            },
            //分页信息
            total: 0,
            pageNum: 1,
            pageSize: 10,

            //弹窗表单
            saveForm: {
                key: '',
                value: '',
                label: '',
                labelEn: '',
                type: '',
                remark: '',
                themes: '',
                icon: '',
                sort: '',
            },

            dialogVisible: false,
            dialogTitle: '',
            dialogLoading: false,
            //表格数据
            tableData: [],
            //所有的类型，用做下拉框
            allType: [],

            themess: ['default', 'primary', 'gray', 'success', 'warning', 'danger'],

            diseditable: false,

            //cache -- map<map<value,dict>>
            dictDataMap: new Map(),
            //cache -- map<List<map>>
            dictDataList: [],

            rules: {
                key: [{ required: true, message: 'Please input key', trigger: 'blur' }],
                type: [{ required: true, message: 'Please input type', trigger: 'blur' }],
                value: [{ required: true, message: 'Please input value', trigger: 'blur' }],
                label: [{ required: true, message: 'Please input label', trigger: 'blur' }],
                labelEn: [{ required: true, message: 'Please input labelEn', trigger: 'blur' }],
            },
            loading: false
        }
    },
    mounted() {
        this.allDictType();
        this.getData();
    },
    methods: {
        onSubmit() {
            this.getData();
        },
        currentChange(i) {
            this.pageNum = i;
            this.getData();
        },
        // 获取列表数据
        getData() {
            this.loading = true;
            this.$$api_iam_dictList({
                data: {
                    key: this.searchParams.key,
                    label: this.searchParams.label,
                    type: this.searchParams.type,
                    remark: this.searchParams.remark,
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
        // 获取列表数据
        allDictType() {
            this.$$api_iam_allDictType({
                data: {},
                fn: data => {
                    this.allType = data.data.list;
                }
            })
        },
        addData() {
            this.cleanSaveForm();
            this.dialogVisible = true;
            this.dialogTitle = 'Add dictionaries';
            this.diseditable = false;
            //默认值
            this.saveForm.themes = 'default';
            this.saveForm.sort = 50;
        },
        cleanSaveForm() {
            this.saveForm.key = '';
            this.saveForm.value = '';
            this.saveForm.label = '';
            this.saveForm.labelEn = '';
            this.saveForm.type = '';
            this.saveForm.remark = '';
            this.saveForm.themes = '';
            this.saveForm.icon = '';
            this.saveForm.sort = '';
        },
        saveData() {
            this.dialogLoading = true;
            this.$refs['saveForm'].validate((valid) => {
                if (valid) {
                    this.$$api_iam_saveDict({
                        data: {
                            key: this.saveForm.key,
                            value: this.saveForm.value,
                            label: this.saveForm.label,
                            labelEn: this.saveForm.labelEn,
                            type: this.saveForm.type,
                            remark: this.saveForm.remark,
                            themes: this.saveForm.themes,
                            icon: this.saveForm.icon,
                            sort: this.saveForm.sort,
                            isEdit: this.diseditable,
                        },
                        fn: data => {
                            // Reload dict list.
                            this.getData();
                            this.dialogLoading = false;
                            this.dialogVisible = false;
                            this.$$api_iam_dictCache({
                                fn: data => {
                                    store.set("dicts_cache", data.data);
                                },
                            });
                            this.cleanSaveForm();
                        },
                        errFn: () => {
                            this.dialogLoading = false;
                            this.dialogVisible = false;
                        }
                    });
                } else {
                    this.dialogLoading = false;
                    this.dialogVisible = false;
                    console.log('error submit!!');
                    return false;
                }
            });
        },
        dataDetail(row) {
            if (!row.key) {
                return;
            }
            this.$$api_iam_dictDetail({
                data: {
                    key: row.key,
                },
                fn: data => {
                    this.saveForm = data.data;
                }
            });
            this.dialogVisible = true;
            this.dialogTitle = '编辑';
            this.diseditable = true;
        },
        delData(row) {
            if (!row.key) {
                return;
            }
            this.$confirm('Delete Confirm', 'Warning', {
                confirmButtonText: 'OK',
                cancelButtonText: 'Cancel',
                type: 'warning'
            }).then(() => {
                this.$$api_iam_delDict({
                    data: {
                        key: row.key,
                    },
                    fn: data => {
                        this.$message({
                            message: '删除成功',
                            type: 'success'
                        });
                        this.getData();
                    },
                })
            }).catch(() => {

            });
        },

        //dict
        getDictMapByType(type, value) {
            if (!type || value) {//type can not be null
                return;
            }
            let dictGroup = this.dictDataMap.get(category);
            if (!dictGroup) {//if not found on catch ,  get from server
                var dicts = this.getDictByTypeFromServer(type);
                if (!dicts) {
                    return;
                }
                dictGroup = new Map();
                for (let i = 0; i < dicts.length; i++) {
                    dictGroup.set(dicts[i].value, dicts[i]);
                }
                this.dictDataMap.set(type, dictGroup);
            }
            return dictGroup.get(value);
        },

        getDictListByType(type) {
            if (!type) {//type can not be null
                return;
            }
            let dictGroup = this.dictDataList.get(category);
            if (!dictGroup) {//if not found on catch ,  get from server
                var dicts = this.getDictByTypeFromServer(type);
                if (!dicts) {
                    return;
                }
                this.dictDataList.set(type, dictGroup);
            }
            return dictGroup;
        },

        getDictByTypeFromServer(type) {
            if (!type) {
                return;
            }
            this.$$api_iam_getDictByType({
                data: {
                    type: type,
                },
                fn: data => {
                    return data.data.dict;
                }
            })
        },
    }
}
