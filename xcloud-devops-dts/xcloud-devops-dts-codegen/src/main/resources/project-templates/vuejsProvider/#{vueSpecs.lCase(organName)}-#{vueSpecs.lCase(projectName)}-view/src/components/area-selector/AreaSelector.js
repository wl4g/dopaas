export default {
    name: 'area-selector',
    components: {},
    data() {
        return {
            options: [],
            value: [],
        }
    },
    props: {

    },

    mounted() {
        this.getAreaTree();
    },
    methods: {
        getAreaTree() {
            this.$$api_iam_getAreaTree({
                data: {},
                fn: data => {
                    this.handleData(data.data);
                    this.options = data.data;
                },
            })
        },

        handleData(data) {
            if (data && data.length > 0) {
                for (let i in data) {
                    data[i].value = data[i].id;
                    data[i].label = data[i].name;
                    if (data[i].children) {
                        this.handleData(data[i].children);
                    }
                }
            }
        },

        changeArea(opts) {
            this.$emit('onChangeAreaCode', opts)
        },

        clearArea() {
            this.value = [];
        }
    },
}
