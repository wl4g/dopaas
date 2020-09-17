import echarts from "echarts";
import it from "element-ui/src/locale/lang/it";
import { store } from "../../utils";
import fa from "element-ui/src/locale/lang/fa";
import AreaSelector from "../area-selector";

export default {
    name: 'organization-panel',
    components: {
        'area-selector': AreaSelector
    },
    data() {
        return {
            myChart: '',
            current: '',
            currentOrganizationCode: '',
            currentOrganization: '',

            currentNodeCode: '',//左侧当前选中的节点Code

            organizationsList: [],
            organizationsTree: [],

            searchParams: {
                park: '',
                company: '',
                department: '',
            },

            showOrganizations: [],
            parkShow: [],
            companyShow: [],
            departmentShow: [],

            parkAreaCode: '',
            companyAreaCode: '',
            departmentAreaCode: '',

            areaTree: [],

            // Echarts Option -- start
            option: {
                tooltip: {
                    trigger: 'item',
                    triggerOn: 'mousemove'
                },
                series: [
                    {
                        type: 'tree',
                        data: [],
                        top: '1%',
                        left: '10%',
                        bottom: '1%',
                        right: '20%',
                        symbolSize: 15,
                        label: {
                            normal: {
                                position: "insideTop",
                                verticalAlign: "bottom",
                                align: "center",
                                distance: 30,
                                fontSize: 12,
                                color: "#fff"
                            }
                        },
                        lineStyle: {
                            normal: { color: "#308ee0" }
                        },
                        leaves: {
                            label: {
                                normal: {
                                    position: "insideTop",
                                    verticalAlign: "middle",
                                    align: "center",
                                    distance: 25
                                }
                            }
                        },
                        expandAndCollapse: true,
                        animationDuration: 550,
                        animationDurationUpdate: 750
                    }
                ]
            }
            // Echarts Option -- end

        }
    },
    mounted() {
        this.getOrganizations();
        this.getCurrentOrganization();
        this.getAreaTree();
    },
    methods: {
        //get data from server
        getOrganizations() {
            this.$$api_iam_getOrganizations({
                data: {},
                fn: data => {
                    this.organizationsList = data.data.list;
                    this.organizationsTree = data.data.tree;

                    this.handleData();
                    this.createEchart();

                    //TODO just for now
                    this.handleShow();
                },
                errFn: () => {
                    //do nothing
                }
            })
        },

        handleData() {
            if (!this.organizationsTree) {
                return;
            }
            for (let i in this.organizationsTree) {
                this.recursiveTraversalOrganization(this.organizationsTree[i]);
            }

            if (this.organizationsTree.length > 1) { // More than one top
                this.option.series[0].data = [{
                    name: 'Top', children: [
                        this.organizationsTree
                    ]
                }];
            } else { //  Just one top
                this.option.series[0].data = this.organizationsTree;
            }

        },

        recursiveTraversalOrganization(organization) {
            this.$set(organization, 'name', organization.name);
            this.$set(organization, 'value', organization.organizationCode);
            this.setItemStyleByType(organization);

            if (organization.children) {
                for (let i in organization.children) {
                    this.recursiveTraversalOrganization(organization.children[i]);
                }
            }
        },

        setItemStyleByType(organization) {
            if (organization && organization.type) {
                if (organization.type === 1) {
                    this.$set(organization, 'itemStyle', { borderColor: "#ff192f" })
                } else if (organization.type === 2) {
                    this.$set(organization, 'itemStyle', { borderColor: "#171bff" })
                } else if (organization.type === 3) {
                    this.$set(organization, 'itemStyle', { borderColor: "#4dff64" })
                }
            }
        },

        createEchart() {
            this.myChart = echarts.init(document.getElementById("organization_container"));
            this.myChart.setOption(this.option);
            this.myChart.on("click", this.clickNode);
        },

        clickNode(param) {
            console.info(param);
            //this.$message.success(param.value);
            this.currentNodeCode = param.value;
            this.handleShow();
        },

        search() {
            this.handleShow();
        },

        handleShow() {
            if (this.currentNodeCode) {
                const currentNodeCode = this.findNodeByCode(this.organizationsTree, this.currentNodeCode);
                this.showOrganizations = this.nodeTreeToList(currentNodeCode);
                this.classifyOrganizations();
                this.filterOrganizations();
            } else {
                this.showOrganizations = this.organizationsList;
                this.classifyOrganizations();
                this.filterOrganizations();
            }
        },

        findNodeByCode(organizations, code) {
            for (let i in organizations) {
                if (code === organizations[i].organizationCode) {
                    return organizations[i];
                } else if (organizations[i].children) {
                    let node = this.findNodeByCode(organizations[i].children, code);
                    if (node) {
                        return node;
                    }
                }
            }
            return null;
        },

        nodeTreeToList(node) {
            let list = [];
            list.push(node);
            if (node.children) {
                for (let i in node.children) {
                    list = list.concat(this.nodeTreeToList(node.children[i]));
                }
            }
            return list;
        },

        classifyOrganizations() {
            this.parkShow = [];
            this.companyShow = [];
            this.departmentShow = [];
            for (let i in this.showOrganizations) {
                if (this.showOrganizations[i].type === 1 && this.strLike(this.showOrganizations[i].name, this.searchParams.park)) {
                    if (this.parkAreaCode && !this.areaInclude(this.parkAreaCode, this.showOrganizations[i].areaId)) {
                        continue;
                    }
                    this.parkShow.push(this.showOrganizations[i]);
                } else if (this.showOrganizations[i].type === 2 && this.strLike(this.showOrganizations[i].name, this.searchParams.company)) {
                    if (this.companyAreaCode && !this.areaInclude(this.companyAreaCode, this.showOrganizations[i].areaId)) {
                        continue;
                    }
                    this.companyShow.push(this.showOrganizations[i]);
                } else if (this.showOrganizations[i].type === 3 && this.strLike(this.showOrganizations[i].name, this.searchParams.department)) {
                    if (this.departmentAreaCode && !this.areaInclude(this.departmentAreaCode, this.showOrganizations[i].areaId)) {
                        continue;
                    }
                    this.departmentShow.push(this.showOrganizations[i]);
                }
            }
        },

        areaInclude(selectAreaId, eachAreaId) {
            //get sons by eachAreaId
            let areaTreeNode = this.getAreaNodeByCode(this.areaTree, selectAreaId);
            let areaList = this.areaTreeToList(areaTreeNode);
            let result = this.isSelectIncludeIt(areaList, eachAreaId);
            return result;

        },

        isSelectIncludeIt(areaList, eachAreaId) {
            let isContain = false;
            for (let i in areaList) {
                if (areaList[i].id === eachAreaId) {
                    isContain = true;
                    break;
                }
            }
            return isContain;
        },

        areaTreeToList(node) {
            let list = [];
            list.push(node);
            if (node.children) {
                for (let i in node.children) {
                    list = list.concat(this.areaTreeToList(node.children[i]));
                }
            }
            return list;
        },

        getAreaNodeByCode(areaTree, code) {
            for (let i in areaTree) {
                if (areaTree[i].id === code) {
                    return areaTree[i];
                } else {
                    if (areaTree[i].children) {
                        let result = this.getAreaNodeByCode(areaTree[i].children, code);
                        if (result) {
                            return result;
                        }
                    }
                }
            }
            return null;
        },

        getAreaTree() {
            this.$$api_iam_getAreaTree({
                data: {},
                fn: data => {
                    this.handleData(data.data);
                    this.areaTree = data.data;
                },
            })
        },

        filterOrganizations() {
            this.filterCompany();
            this.filterDepartment();
        },

        filterCompany() {
            if (this.currentNodeCode) {
                const currentNodeCode = this.findNodeByCode(this.organizationsTree, this.currentNodeCode);
                if (currentNodeCode.type === 2) {
                    return;
                }
            }

            for (let i = 0; i < this.companyShow.length; i++) {
                if (this.contactOrganization(this.parkShow, this.companyShow[i].parent) || this.contactOrganization(this.companyShow, this.companyShow[i].parent)) {
                    //保留
                } else {//删除
                    this.companyShow.splice(i, 1); // 将使后面的元素依次前移，数组长度减1
                    i--; // 如果不减，将漏掉一个元素
                }
            }
        },

        filterDepartment() {
            if (this.currentNodeCode) {
                const currentNodeCode = this.findNodeByCode(this.organizationsTree, this.currentNodeCode);
                if (currentNodeCode.type === 3) {
                    return;
                }
            }
            for (let i = 0; i < this.departmentShow.length; i++) {
                if (this.contactOrganization(this.companyShow, this.departmentShow[i].parent) || this.contactOrganization(this.departmentShow, this.departmentShow[i].parent)) {
                    //保留
                } else {//删除
                    this.departmentShow.splice(i, 1); // 将使后面的元素依次前移，数组长度减1
                    i--; // 如果不减，将漏掉一个元素
                }
            }
        },


        contactOrganization(organizations, code) {
            for (let i in organizations) {
                if (organizations[i].organizationCode === code) {
                    return true;
                }
            }
            return false;
        },

        strLike(str, like) {
            if (!like) {// if like is null == match
                return true;
            } else {
                return str.indexOf(like) >= 0;
            }
        },

        resetAll() {
            this.searchParams.park = null;
            this.searchParams.company = null;
            this.searchParams.department = null;

            this.parkAreaCode = '';
            this.companyAreaCode = '';
            this.departmentAreaCode = '';
            this.$refs.clearParkArea.clearArea();
            this.$refs.clearCompanyArea.clearArea();
            this.$refs.clearDepartmentArea.clearArea();
            // Resovery show.
            this.search();
        },

        changeOrganization(item) {
            this.current = item.name;
            this.currentOrganizationCode = item.organizationCode;
            this.currentOrganization = item;

            store.set("currentOrganization", item);

            location.reload()
            //this.$router.go(0)
        },

        getCurrentOrganization() {
            const currentOrganization = store.get("currentOrganization");
            if (currentOrganization) {
                this.current = currentOrganization.name;
                this.currentOrganizationCode = currentOrganization.organizationCode;
                this.currentOrganization = currentOrganization;
            } else {
                this.current = 'TOP';
                this.currentOrganizationCode = '';
                this.currentOrganization = { type: 1 };
            }

        },

        onChangeParkAreaCode(opt) {
            if (opt && opt.length > 0) {
                this.parkAreaCode = opt[opt.length - 1];
            } else {
                this.parkAreaCode = '';
            }
            this.search();
        },
        onChangeCompanyAreaCode(opt) {
            if (opt && opt.length > 0) {
                this.companyAreaCode = opt[opt.length - 1];
            } else {
                this.companyAreaCode = '';
            }
            this.search();
        },
        onChangeDepartmentAreaCode(opt) {
            if (opt && opt.length > 0) {
                this.departmentAreaCode = opt[opt.length - 1];
            } else {
                this.departmentAreaCode = '';
            }
            this.search();
        },

        subCurrent() {
            if (this.current.length > 5) {
                return this.current.substring(0, 5) + '...';
            } else {
                return this.current;
            }
        }

    },
}
