// ${watermark}
import {transDate, getDay} from 'utils/'

export default {
    name: '${entityName?uncap_first}',
    data() {
        return {
            //查询条件
            searchParams: {
<#list genTableColumns as param>
    <#if param.isQuery == '1'>
                ${param.attrName}: '',
    </#if>
</#list>
            },

            //分页信息
            total: 0,
            pageNum: 1,
            pageSize: 10,

            //弹窗表单
            saveForm: {
<#list genTableColumns as param>
    <#if param.isEdit == '1'>
                ${param.attrName}: '',
    </#if>
</#list>
            },

            dialogVisible: false,
            dialogTitle: '',
            dialogLoading: false,

            tableData: [],

            // 表单规则
            rules: {
                name: [
                    {required: true, message: 'Please Input name', trigger: 'blur' },
                    { min: 1, max: 30, message: 'length between 1 to 30', trigger: 'blur' }
                ],
            },
            loading: false
        }
    },

    mounted() {
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

        addData() {
            this.cleanSaveForm();
            this.dialogVisible = true;
            this.dialogTitle = 'Add';
        },

        // 获取列表数据
        getData() {
            this.loading = true;
            this.searchParams.pageNum = this.pageNum;
            this.searchParams.pageSize = this.pageSize;
            this.$$api_${moduleName?lower_case}_${entityName?uncap_first}List({
                data: this.searchParams,
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
<#list genTableColumns as param>
    <#if param.isEdit == '1'>
                ${param.attrName}: '',
    </#if>
</#list>
            };
        },

        saveData() {
            this.dialogLoading = true;
            this.saveForm.hostId = this.searchParams.hostId;
            this.$refs['saveForm'].validate((valid) => {
                if (valid) {
                    this.$$api_${moduleName?lower_case}_save${entityName?cap_first}({
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
                }else {
                    this.dialogLoading = false;
                }
            });
        },

        editData(row) {
            if (!row.id) {
                return;
            }
            this.cleanSaveForm();
            this.$$api_${moduleName?lower_case}_${entityName?uncap_first}Detail({
                data: {
                    id: row.id,
                },
                fn: data => {
                    this.saveForm = data.data;
                },
            });
            this.dialogVisible = true;
            this.dialogTitle = 'Edit';
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
                this.$$api_${moduleName?lower_case}_del${entityName?cap_first}({
                    data: {
                        id: row.id,
                    },
                    fn: data => {
                        this.$message({
                            message: 'Success',
                            type: 'success'
                        });
                        this.getData();
                    },
                })
            }).catch(() => {
                //do nothing
            });
        },

    }
}
