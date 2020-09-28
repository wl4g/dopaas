<template>
  <div ref="bottomPanel" :class="{show:show}" class="bottomPanel-container">
    <div class="bottomPanel-background" />
    <div class="bottomPanel">
      <div class="handle-button"  @click="show=!show">
        <p class="handle-button-panel">搜索条件</p>

        <!--<svg  aria-hidden="true"  style="cursor:pointer;width: 1.1em;height: 1.1em;float:right;margin: 10px 10px 3px 0">-->
          <!--<use :xlink:href="show?'#icon-lunbo-next-copy1':'#icon-lunbo-next-copy'"></use>-->
        <!--</svg>-->
      </div>
      <div class="bottomPanel-items">
        <slot />
      </div>
    </div>
  </div>
</template>

<script>

export default {
  name: 'BottomPanel',
  props: {
    clickNotClose: {
      default: false,
      type: Boolean
    },
    buttonTop: {
      default: 250,
      type: Number
    }
  },
  data() {
    return {
      show: false
    }
  },
  computed: {

  },
  watch: {
    show(value) {
      if (value && !this.clickNotClose) {
        this.addEventClick()
      }
      if (value) {
        this.addClass(document.body, 'showBottomPanel')
      } else {
        this.removeClass(document.body, 'showBottomPanel')
      }
    }
  },
  mounted() {
    //this.insertToBody()
  },
  beforeDestroy() {
    const elx = this.$refs.bottomPanel
    elx.remove()
  },
  methods: {
    addEventClick() {
      //window.addEventListener('click', this.closeSidebar)
    },
    closeSidebar(evt) {
      const parent = evt.target.closest('.bottomPanel')
      if (!parent) {
        this.show = false
        window.removeEventListener('click', this.closeSidebar)
      }
    },
    insertToBody() {
      const elx = this.$refs.bottomPanel
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
.showBottomPanel {
  overflow: hidden;
  position: relative;
  /*width: calc(100% - 15px);*/
}
</style>

<style lang="scss" scoped>
.bottomPanel-background {
  position: fixed;
  top: 0;
  left: 0;
  opacity: 0;
  /*transition: opacity .3s cubic-bezier(.7, .3, .1, 1);*/
  background: rgba(0, 0, 0, .2);
  z-index: -1;
}

.bottomPanel {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  max-width: 260px;
  transition: all 0.25s cubic-bezier(0.7, 0.3, 0.1, 1);
  transform: translate(100%);
  background: #fff;
  z-index: 1000;
  font-family: Consolas,Menlo,Courier,monospace;
  box-shadow: 0px 0px 15px 0px rgba(0,0,0,.05);
}

.show {
  transition: all .3s cubic-bezier(.7, .3, .1, 1);

  /*.bottomPanel-background {
    z-index: 2000;
    opacity: 1;
    width: 100%;
    height: 100%;
  }*/

  .bottomPanel {
    transform: translate(0);
  }
}

.handle-button {
  width: 38px;
  position: absolute;
  left: -38px;
  top: 250px;
  background-color: rgb(24, 144, 255);
  text-align: center;
  font-size: 24px;
  border-radius: 6px 0 0 6px !important;
  z-index: 0;
  pointer-events: auto;
  cursor: pointer;
  color: #fff;
  line-height: 48px;

  i {
    font-size: 24px;
    line-height: 48px;
  }
}

  .handle-button-panel {
    padding: 12px 8px;
    width: 38px;
    color: #fff;
    line-height: 16px;
  }
  .bottomPanel-items {
    padding: 72px 8px 0;
  }
</style>
