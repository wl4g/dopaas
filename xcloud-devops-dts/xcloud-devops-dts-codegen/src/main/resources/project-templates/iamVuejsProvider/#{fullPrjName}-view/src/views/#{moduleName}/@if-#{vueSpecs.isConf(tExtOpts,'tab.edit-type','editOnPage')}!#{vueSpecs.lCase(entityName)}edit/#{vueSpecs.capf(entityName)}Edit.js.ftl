// ${watermark}
import {transDate, getDay} from 'utils/'

export default {
    name: '${entityName?uncap_first}Edit',
    data() {
        return {
            ${pk.attrName}: '',
            isEdit: false,
            //弹窗表单
            saveForm: {
<#list genTableColumns as param>
    <#if param.isEdit == '1'>
                ${param.attrName}: '',
    </#if>
</#list>
            },
            loading: false,
            // 表单规则
            rules: {
<#list genTableColumns as param>
    <#if param.noNull == '1' || param.validRule??>
                ${param.attrName}: [
        <#if param.noNull == '1'>
                    {required: true, message: '${param.attrName} is empty', trigger: 'change' },
        </#if>
        <#if param.validRule??>
            <#if param.validRule == '1'>
                    { pattern: /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/, message: '不合法的邮件地址' },//电子邮件
            </#if>
            <#if param.validRule == '2'>
                    { pattern: /[a-zA-z]+://[^\s]*/, message: '不合法的网址' },//网址
            </#if>
            <#if param.validRule == '3'>
                    { pattern: /^\d{4}-\d{1,2}-\d{1,2}/, message: '不合法的日期格式' },//日期
            </#if>
            <#if param.validRule == '4'>
                    { pattern: /^(\-|\+)?\d+(\.\d+)?$/, message: '请输入数字' },//数字
            </#if>
            <#if param.validRule == '5'>
                    { pattern: /^-?[1-9]\d*$/, message: '请输入整数' },//整数
            </#if>
            <#if param.validRule == '6'>
                    { pattern: /^\d+$/, message: '请输入正整数' },//正整数
            </#if>
            <#if param.validRule == '7'>
                    { pattern: /^\w+$/, message: '请输入字母数字下划线' },//字母数字下划线
            </#if>
            <#if param.validRule == '8'>
                    { pattern: /^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\d{8}$/, message: '不合法的手机号码' },//手机电话
            </#if>
            <#if param.validRule == '9'>
                    { pattern: /[1-9]\d{5}(?!\d)/, message: '不合法的邮政编码' },//邮政编码
            </#if>
            <#if param.validRule == '10'>
                    { pattern: /^((?:(?:25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))\.){3}(?:25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d))))$/, message: '不合法的ipv4' },//Ipv4
            </#if>
            <#if param.validRule == '11'>
                    { pattern: /^(([\da-fA-F]{1,4}):){8}$/, message: '不合法的ipv6' },//Ipv6
            </#if>
            <#if param.validRule == '12'>
                    { pattern: /[1-9][0-9]{4,}/, message: '不合法的qq' },//QQ
            </#if>
            <#if param.validRule == '13'>
                    { pattern: /^\d{15}|\d{18}$/, message: '不合法的身份证' },//身份证
            </#if>
        </#if>
                ],
    </#if>
</#list>
            },
        }
    },
    activated() {
        this.cleanSaveForm();
        const ${pk.attrName} = this.$route.query.${pk.attrName};
        this.${pk.attrName} = ${pk.attrName};
        this.saveForm.${pk.attrName} = ${pk.attrName};
        if(${pk.attrName}) { // edit?
            this.isEdit = true;
            this.editData();
        } else { // add
            this.isEdit = false;
        }
    },
    mounted() {
    },
    methods: {
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
            this.loading = true;
            this.$refs['saveForm'].validate((valid) => {
                if (valid) {
                    this.$$api_${moduleName?lower_case}_save${entityName?cap_first}({
                        data: this.saveForm,
                        fn: json => {
                            this.back();
                            this.cleanSaveForm();
                        },
                        errFn: () => {
                            this.loading = false;
                        }
                    });
                }else {
                    this.loading = false;
                }
            });
        },
        editData() {
            if (!this.${pk.attrName}) {
                return;
            }
            this.cleanSaveForm();
            this.$$api_${moduleName?lower_case}_${entityName?uncap_first}Detail({
                data: {
                    ${pk.attrName}: this.${pk.attrName},
                },
                fn: json => {
                    this.saveForm = json.data;
                },
            });
        },
        back(){
            this.$router.push({ path: '/${moduleName}/${entityName?lower_case}'})
        }
    }
}
