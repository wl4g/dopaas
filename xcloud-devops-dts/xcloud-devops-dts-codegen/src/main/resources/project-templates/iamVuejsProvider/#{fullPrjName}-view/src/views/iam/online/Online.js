import {transDate, getDay} from 'utils/'

export default {
    name: 'online',
    data() {
        return {
            //查询条件
            searchParams: {
                id: '',
                principal: '',
            },

            //分页信息
            total: 0,
            pageNum: 1,
            pageSize: 10,

            dialogVisible: false,
            dialogTitle: '',
            dialogLoading: false,

            tableData: [],

            iamServers: [],

            saveForm: {
                alarmTemplate: {},

            },
            loading: false,

            rules: {
                id: [{ required: true, message: 'Private IAM Service ID is required', trigger: 'blur' }],
            },
        }
    },

    mounted() {
        this.getIamServer();
        //this.getData();
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
            this.$refs['searchForm'].validate((valid) => {
                if (valid) {
                    this.$$api_iam_onlineList({
                        data: {
                            id: this.searchParams.id,
                            principal: this.searchParams.principal,
                        },
                        fn: json => {
                            this.tableData = json.data.sessions;
                            this.loading = false;
                        },
                        errFn: () => {
                            this.loading = false;
                        }
                    })
                }
            });

        },

        getIamServer(){
            this.$$api_iam_getIamServer({
                data: {},
                fn: json => {
                    this.iamServers = json.data;
                }
            })
        },

        destroySessions(row) {
            this.loading = true;
            this.$$api_iam_destroySessions({
                data: {
                    id: this.searchParams.id,
                    sessionId: row.id,
                    //principal: row.principal,
                },
                fn: json => {
                    this.$message({
                        message: 'destroy seccess',
                        type: 'success'
                    });
                    this.getData();
                },
                errFn: () => {
                    this.loading = false;
                }
            })
        },


        convertType(row, column, cellValue, index) {
            if(cellValue){
                return "true";
            }else{
                return "false";
            }
        },

        subStr(str){
            if(str.length>10){
                str = str.substring(0,10)+"...";
            }
            return str;
        },




    }
}
