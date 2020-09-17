<template>
    <div class="">
      <div id="wrap">
        <iframe :src="url"  style="width: 100%; height: 100%;border: none"></iframe>
      </div>

    </div>
</template>

<script>
export default {
  name: 'webview',
  data() {
    return {
      url: ''
    }
  },
  mounted() {
    let winHeight = document.documentElement.clientHeight;
    document.querySelector("#wrap").style.height = (winHeight - 125) + "px"

    if(this.$route.meta.linkhref){
      this.url = this.$route.meta.linkhref
    }
  },
  activated(){

  },
  methods: {

  },
  watch: {
    '$route' (to, from) {
      if(to.meta.linkhref) {
        this.url = to.meta.linkhref
      }
    }
  }
};
</script>

<style>

</style>
