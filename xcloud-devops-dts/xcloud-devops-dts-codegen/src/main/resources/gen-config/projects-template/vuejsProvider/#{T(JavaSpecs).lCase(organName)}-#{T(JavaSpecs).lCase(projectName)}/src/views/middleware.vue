<template>
    <div>
      <div class="box">
        <p>
          申请打开外部链接<a :href="url" target="_blank">{{url}}</a>
        </p>
        <button class="btn btn-primary" @click="handleBtnClick">请点击确认</button>
      </div>
    </div>
</template>

<script>
export default {
  name: 'middleware',
  data() {
    return {
      url: ''
    }
  },
  mounted() {
    this.url = this.$route.query.url;
  },
  activated(){

  },
  methods: {
    handleBtnClick(){
      window.open(this.url)
    }
  },
  watch: {
    '$route' (to, from) {
      if(to.path == '/common/middleware' && this.$route.query.url){
        this.url = this.$route.query.url;
      }
    }
  }
};
</script>

<style scoped>
  .box {
    position: absolute;
    top: 50%;
    left: 50%;
    text-align: center;
  }
  .btn {
    margin-top: 20px;
  }
</style>
