<template>
    <div ref="rightPanel" :class="{show:show}" class="rightPanel-container">
        <div class="rightPanel-background"></div>
        <div class="rightPanel">
            <div style="margin-bottom: 8px;height: 30px;margin-top: 10px"><!--head-->
                <span style="font-size: 20px;margin-left: 20px;">{{title}}</span>
                <i class="el-icon-close" style="float:right;font-size: 30px;margin-right: 10px" @click="clickClose"></i>
            </div>
            <div class="rightPanel-items">
                <slot/>
            </div>
        </div>
    </div>
</template>

<script>

    export default {
        name: 'RightPanel',
        props: {
            clickNotClose: {
                default: false,
                type: Boolean
            },
            buttonTop: {
                default: 250,
                type: Number
            },
            show: {
                default: false,
                type: Boolean
            },
            title: {
                default: '',
                type: String
            },
        },
        data() {
            return {
                //show: false
            }
        },
        computed: {},
        watch: {
            show(value) {
                if (value && !this.clickNotClose) {
                    this.addEventClick()
                }
                if (value) {
                    this.addClass(document.body, 'showRightPanel')
                } else {
                    this.removeClass(document.body, 'showRightPanel')
                }
            }
        },
        mounted() {
            //this.insertToBody()
        },
        beforeDestroy() {
            const elx = this.$refs.rightPanel
            elx.remove()
        },
        methods: {
            clickClose(){
              console.debug("close right panel");
              this.$emit('close');
            },
            addEventClick() {
                //window.addEventListener('click', this.closeSidebar)
            },
            closeSidebar(evt) {
                const parent = evt.target.closest('.rightPanel')
                if (!parent) {
                    this.show = false
                    window.removeEventListener('click', this.closeSidebar)
                }
            },
            insertToBody() {
                const elx = this.$refs.rightPanel
                const body = document.querySelector('body')
                body.insertBefore(elx, body.firstChild)
            },
            hasClass(ele, cls) {
                return !!ele.className.match(new RegExp('(\\s|^)' + cls + '(\\s|$)'))
            },
            addClass(ele, cls) {
                if (!this.hasClass(ele, cls)) ele.className += ' ' + cls
            },
            removeClass(ele, cls) {
                if (this.hasClass(ele, cls)) {
                    const reg = new RegExp('(\\s|^)' + cls + '(\\s|$)')
                    ele.className = ele.className.replace(reg, ' ')
                }
            }
        }
    }
</script>

<style>
    .showRightPanel {
        overflow: hidden;
        position: relative;
        /*width: calc(100% - 15px);*/
    }
</style>

<style lang="scss" scoped>
    .rightPanel-background {
        position: fixed;
        top: 0;
        left: 0;
        opacity: 0;
        /*transition: opacity .3s cubic-bezier(.7, .3, .1, 1);*/
        background: rgba(0, 0, 0, .2);
        z-index: -1;
    }

    .rightPanel {
        top: 50px;
        right: 0px;
        position: fixed;
        width: 43%;
        height: calc(100vh - 50px);

        transition: all 0.2s ;
        transform: translate(110%);
        background: #f8f8f8;
        z-index: 2002;
        /*border: 1px solid #eee;*/
        //left: 16vw;

        font-family: Consolas, Menlo, Courier, monospace;
        border: 1px solid #DCDFE6;

        //box-shadow: 0px 2px 2px 2px rgba(242, 239, 237, 0.7);
        box-shadow: -3px 0px 2px rgba(0, 0, 0, 0.1);
    }

    .show {
        .rightPanel {
            //transition: all 0.9s cubic-bezier(.7, .3, .1, 1);
            transition: all 0.6s ;
            transform: translate(0);
        }

        .rightPanel-background {
            z-index: 2001;
            opacity: 1;
            width: 100%;
            height: 100%;
        }
    }

    .rightPanel-items {
        padding: 8px 8px 0;
        height: 100%;
        overflow-x: hidden; /*x轴禁止滚动*/
        overflow-y: scroll; /*y轴滚动*/
    }

    .rightPanel-items::-webkit-scrollbar {
        display: none; /*隐藏滚动条*/
    }
</style>
