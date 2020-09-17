import de from "element-ui/src/locale/lang/de";

export default {
    name: 'organization-selector',
    components: {},
    data() {
        return {
            data: [],
            dataList: [],
            displayName: '',
            treeShow: false,
            defaultProps: {
                children: 'children',
                label: 'name',
            },
        }
    },

    props: {
        inputData: {
            type: Object,
        },
    },

    mounted() {
        this.getOrganizations();
        console.info('into org init'+ this.inputData);
    },
    methods: {
        //get data from server
        getOrganizations() {
            this.$$api_iam_getOrganizations({
                data: {},
                fn: data => {
                    //this.handleData(data.data.tree);
                    this.data = data.data.tree;
                    this.dataList = data.data.list;
                    this.$nextTick(() => {
                        this.setKeys();
                    });
                },
            })
        },

        focusDo() {
            if (this.$refs.modulesTree && this.inputData.organizationCode) {
                this.$refs.modulesTree.setCheckedKeys([this.inputData.organizationCode])
            }
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
                if (selfChecked) {
                    this.$refs.modulesTree.setCheckedNodes([node]);
                    this.inputData.organizationCode = node.organizationCode;
                    this.displayName = node.name;
                    this.treeShow = false;
                    this.$emit('onChangeOrganization', node.organizationCode);
            }
        },

        setKeys(){
            if (this.inputData.organizationCode && this.dataList) {
                for(let i in this.dataList){
                    if(this.dataList[i].organizationCode ===this.inputData.organizationCode){
                        this.displayName = this.dataList[i].name;
                    }
                }
            }else{
                this.displayName = '';
            }
        }
    },

    watch: {
        'inputData.organizationCode': {
            handler (newName, oldName) {
                console.info('inputData changed',newName,oldName)
                this.setKeys();
            }
        }
    }
}
