<template>
    <div id="wrap" class="wl4glogin" style="height: 100%;background:url('/static/images/bg_icon/background.png') no-repeat;background-size:cover;z-index:8;">
        <div id="top">
            <img id="img_logo" src="static/images/logo.png"/>
        </div>
        <div id="content">
            <div id="content-left" class="lanren">
                <ul>
                    <li><img src="static/images/roll/ai1.png"></li>
                    <li><img src="static/images/roll/ai2.png"></li>
                    <li><img src="static/images/roll/ai3.png"></li>
                </ul>
                <ul>
                    <li><img src="static/images/roll/wulianwang1.png"></li>
                    <li><img src="static/images/roll/wulianwang2.png"></li>
                    <li><img src="static/images/roll/wulianwang3.png"></li>
                </ul>
                <ul>
                    <li><img src="static/images/roll/nengyuan1.png"></li>
                    <li><img src="static/images/roll/nengyuan2.png"></li>
                    <li><img src="static/images/roll/nengyuan3.png"></li>
                </ul>
                <div class="base">
                    <img src="static/images/roll/base.png" alt="图片">
                </div>
                <div class="cloud">
                    <img src="static/images/roll/yun.png" alt="图片">
                </div>
                <div class="shadow">
                    <img src="static/images/roll/yunyinyin.png" alt="图片">
                </div>
                <div class="light">
                    <img src="static/images/roll/guangzhu.png" alt="图片" style="z-index:-4;position:relative;">
                </div>
            </div>
            <div id="iam_container">
            </div>
        </div>
        <div id="foot" style="line-height:24px; position:relative;z-index:1000;">
			<a href="http://www.beian.miit.gov.cn" target="_blank">
				<label class="htmKey1a_a8" style="cursor:pointer;color:#9ac3d0;">
				Copyright © 2017 - 2035 XXXX科技有限公司· XXX云平台 ● 粤ICP备xxxxx号-1
				</label>
			</a>
		</div>
		<div class="main-right">
			<div class="rightimageBox">
				<div id="qrcode-title">微信扫码关注</div>
				<img class="rightImage" src="static/images/qrcode.jpg">
			</div>
		</div>
        <div class="gb_box" style="z-index:20"></div>
    </div>
</template>
<script>
  import Login from './Login.js'
  export default Login
</script>
<!--<style scoped src="../../../static/css/font-awesome-all.5.7.2.min.css"></style>
<style scoped src="../../../static/css/bootstrap-3.3.7.min.css"></style>
<style scoped src="../../../static/css/login-1.5.2.css"></style>
<style scoped src="../../../static/css/lanren.css"></style>
<style scoped src="../../../static/css/iam-captcha-jigsaw-1.5.2.css"></style>-->
