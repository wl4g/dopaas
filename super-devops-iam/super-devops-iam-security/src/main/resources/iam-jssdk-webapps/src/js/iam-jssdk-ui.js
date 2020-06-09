/**
 * IAM WebSDK UI v2.0.0 | (c) 2017 ~ 2050 wl4g Foundation, Inc.
 * Copyright 2017-2032 <wangsir@gmail.com, 983708408@qq.com>, Inc. x
 * Licensed under Apache2.0 (https://github.com/wl4g/super-devops/blob/master/LICENSE)
 */
(function(window, document){
	'use strict';

	// Exposing IAM UI
	window.IAMUi = function() {};

	// Runtime cache
	var runtime = {
		iamCore: null,
		renderObj: null,
	};

	/**
	 * Init IAM JSSDK UI.
	 * 
	 * @param renderId Render target object element id.
	 **/
	IAMUi.prototype.initUI = function(renderObj, iamCoreConfig) {
		if(!renderObj || renderObj == undefined){
			throw Error("IAMUi (renderObj) is required!");
		}
		console.debug("IAMUi JSSDK initializing ...");
		runtime.renderObj = renderObj;

		// Javascript multi line string supports.
		// @see https://www.jb51.net/article/49480.htm
		var loginFormHtmlStr = `
				<div class="iamjssdk-login-form">
					<div class="iamjssdk-login-form-header">
						<span class="iamjssdk-login-link active" id="iamjssdk_login_link_account" data-panel="iamjssdk_login_account_panel">
							账号登录
						</span>
						<!--<span class="iamjssdk-login-line">
						</span>
						<span class="iamjssdk-login-link" id="iamjssdk_login_link_phone" data-panel="iamjssdk_login_phone_panel">
							手机登录
						</span>-->
						<span class="iamjssdk-login-line">
						</span>
						<span class="iamjssdk-login-link" id="iamjssdk_login_link_scan" data-panel="iamjssdk_login_scan_panel">
							扫码登录
						</span>
					</div>
					<div class="iamjssdk-login-form-tip" id="err_tip"></div>
					<div class="iamjssdk-login-form-body">
						<!-- Account login-->
						<div class="iamjssdk-login-form-panel active" id="iamjssdk_login_account_panel">
							<form>
								<div class="iamjssdk-login-form-item">
									<i class="icon-user"></i>
									<input class="inp" id="iamjssdk_account_username" name="username" placeholder="请输入账号"
									maxlength="20">
								</div>
								<div class="iamjssdk-login-form-item">
									<i class="icon-pass"></i>
									<input class="inp" id="iamjssdk_account_password" name="iamjssdk_account_password" type="password" placeholder="请输入密码"
									maxlength="35" autocapitalize="off" autocomplete="off">
								</div>
								<div class="iamjssdk-login-form-item" id="iamjssdk_captcha_panel">
									<!-- Behavior verification operation area-->
								</div>
								<input class="iamjssdk-btn" id="iamjssdk_account_submit_btn" type="button" value="登录">
							</form>
						</div>
						<!-- Mobile login-->
						<div class="iamjssdk-login-form-panel" id="iamjssdk_login_phone_panel">
							<select class="select-area">
								<option value="+086">
									中国大陆+086
								</option>
								<option value="+852">
									中国香港+852
								</option>
								<option value="+853">
									中国澳门+853
								</option>
								<option value="+084">
									越南+084
								</option>
								<option value="+092">
									巴基斯坦+092
								</option>
								<option value="+065">
									新加坡+065
								</option>
								<option value="+358">
									法国+358
								</option>
								<option value="+066">
									泰国+066
								</option>
							</select>
							<div class="iamjssdk-login-form-item">
								<i class="icon-phone">
								</i>
								<input id="iamjssdk_sms_user_phone" class="inp" name="phone" placeholder="请输入手机号" maxlength="11">
								<p class="err-info phone-err">
									请输入正确的手机号
								</p>
							</div>
							<div class="iamjssdk-login-form-item iamjssdk-login-form-item-number">
								<i class="icon-codeNumber">
								</i>
								<input id="iamjssdk_sms_code" class="inp" type="text" placeholder="请输入短信动态码" maxlength=6>
								<button class="iamjssdk-btn-code" type="button" id="iamjssdk_sms_getcode_btn">
									获取
								</button>
								<p class="err-info pass-err">
									请输入短信验证码
								</p>
							</div>
							<input class="iamjssdk-btn" id="iamjssdk_sms_submit_btn" type="button" value="登录">
						</div>
						<!-- WeChat login-->
						<div class="iamjssdk-login-form-panel" id="iamjssdk_login_scan_panel">
							<div class="iamjssdk-box-qrcode">
								<div id="iamjssdk_sns_qrcodePanel" style="height:255px;">
								</div>
							</div>
							<div class="qrcode-text">
								打开
								<span class="bold">
									微信"扫一扫"
								</span>
								扫描二维码
							</div>
						</div>
					</div>
				</div>`;
		var loginForm = $(loginFormHtmlStr);
		// Already initialized? (e.g: SPM application, the skip login route will repeat when exiting)
		if (runtime.iamCore) {
			$(renderObj).empty();
		} else {
			Common.Util.printSafeWarn("This browser function is for developers only. Please do not paste and execute any content here, which may cause your account to be attacked and bring you loss!");
		}
		loginForm.appendTo($(renderObj));

		// 初始化绑定UI/Tab事件
		_initUIEvent();

		// 初始化创建IAMCore实例
		_initIAMCore(iamCoreConfig);
	};

	// Exposing IAMCore object
	IAMUi.prototype.getIAMCore = function() {
		return runtime.iamCore;
	};
	IAMUi.prototype.destroy = function() {
		$(runtime.renderObj).empty();
		runtime = null;
		IAMCore.Console.info("Destroyed IAMUi instance.");
		// Detroy iam core.
		if (runtime.iamCore) {
			runtime.iamCore.destroy();
		}
	};

	//
	// --- UI event processing function's. ---
	//

	var _changeTab = function(showId, hideId) {
	    $("#" + showId).show();
	    $("#" + showId + "_1").css({
	        "color": "#0b86f3",
	        "font-weight": "bold"
	    });
	    $("#" + hideId + "_1").css({
	        "color": "white",
	        "font-weight": "100"
	    });
	    $("#" + hideId).hide();
	}

	// 绑定UI/Tab事件
	var _initUIEvent = function() {
		$(".iamjssdk-login-link").click(function(){
		    var that = this;
		    $(".iamjssdk-login-link").each(function(ele, obj){
		        var panel = $(that).attr("data-panel");
		        var _panel = $(obj).attr("data-panel");
		        if (panel != _panel){
		            $("#"+_panel).hide();
		            $(obj).removeClass('active');
		        } else {
		            $("#"+_panel).show();
		            $(obj).addClass('active');
		        }
		    });
		});
	    $('.code-close').click(function () {
	        $('.code-write').hide();
	    });
	    $(".select-area").change(function(){
	        var selectVal = $(this).children('option:selected').val();
	        if (selectVal != "+086") {
	            alert("目前此功能仅对中国大陆用户开放！敬请谅解");
	            $(this).children('option')[0].selected = true;
	        }
	    });
	}

	// 初始化创建IAMCore实例
	var _initIAMCore = function(iamCoreConfig) {
		// Default settings.
		var defaultSettings = {
			deploy: {
	    		//baseUri: "http://localhost:14040/iam-server", // Using auto extra configure
				defaultTwoDomain: "iam", // IAM后端服务部署二级域名，当iamBaseUri为空时，会自动与location.hostnamee拼接一个IAM后端地址.
	   			defaultServerPort: 14040, // IAM server的port
	   			defaultContextPath: "/iam-server" // IAM server的contextPath
	 		},
	 		// 初始相关配置(Event)
	 		init: {
	 			onPostCheck: function(res) {
	 				// 因SNS授权（如:WeChat）只能刷新页面，因此授权错误消息只能从IAM服务加载
					var url = IAMCore.getIamBaseUri() +"/login/errread";	
					$.ajax({
						url: url,
						xhrFields: { withCredentials: true }, // Send cookies when support cross-domain request.
						success: function (res) {
							//console.log(res);
							var errmsg = res.data["errorTipsInfo"];
							if (errmsg != null && errmsg.length > 0) {
								$("#err_tip").text(errmsg).show().delay(8000).hide(100);
							}
						}
					});
	 			},
	 			onError: function(errmsg){
					console.error("初始化失败... "+ errmsg);
				}
	 		},
			// 定义验证码显示面板配置
			captcha: {
				enable: true,
				use: "VerifyWithJigsawGraph", // default by 'VerifyWithGifGraph'
				panel: document.getElementById("iamjssdk_captcha_panel"), // Jigsaw验证码时必须
				img: document.getElementById("iamjssdk_captcha_img"), // 验证码显示 img对象（仅jpeg/gif时需要）
				input: document.getElementById("iamjssdk_captcha_input"), // 验证码input对象（仅jpeg/gif时需要）
				onSuccess: function(verifiedToken) {
					console.debug("Captcha verify successful. verifiedToken is "+ verifiedToken);
				},
				onError: function(errmsg) { // 如:申请过于频繁
					console.warn(errmsg);
				}
			},
			// 登录认证配置
			account: {
				enable: true,
				submitBtn: document.getElementById("iamjssdk_account_submit_btn"), // 登录提交触发对象
				principalInput: document.getElementById("iamjssdk_account_username"), // 必填，获取填写的登录用户名
				credentialInput: document.getElementById("iamjssdk_account_password"), // 获取登录账号密码，账号登录时必填
				onBeforeSubmit: function (principal, plainPasswd, captcha) { // 提交之前
					console.debug("Iam account login... principal: "+ principal+", plainPasswd: ******, captcha: "+captcha);
					return true;
				},
				onSuccess: function (principal, data) {
					console.debug("Iam account login successful !");
					return true; // 返回false会阻止自动调整
				},
				onError: function (errmsg) {
					console.error("Failed login. "+ errmsg);
					$("#err_tip").text(errmsg).show().delay(5000).hide(100);
				}
			},
			sms: { // SMS认证配置
				enable: true,
				submitBtn: document.getElementById("iamjssdk_sms_submit_btn"), // 登录提交触发对象
				sendSmsBtn: document.getElementById("iamjssdk_sms_getcode_btn"), // 发送SMS验证码对象
				mobileArea: $(".select-area"), // 手机号区域select对象
				mobile: document.getElementById("iamjssdk_sms_user_phone"), // 手机号input对象
				smsCode: document.getElementById("iamjssdk_sms_code"), // SMS验证码input对象
				onBeforeSubmit: function (mobileNum, smsCode) {
					console.debug("Iam sms login ... mobileNum: "+ mobileNum);
					return true;
				},
				onSuccess: function(resp){
					$('.err-tip').text('');
					$('.code-write').hide();
				},
				onError: function(errmsg){
					console.error(errmsg);
					$("#err_tip").text(errmsg).show().delay(8000).hide(100);
				}
			},
			// SNS授权配置
			sns: {
				enable: true,
				// 定义必须的请求参数
				required: {
					getWhich: function () { // 执行操作类型，必须：当使用登录功能时值填"login",当使用绑定功能时值填"bind"
						return "login";
					},
					//refreshUrl: "" // SNS回调后重定向刷新的URL，可选，which=login时可空
				},
				// 定义内嵌授权页面配置
				qrcodePanel: {
					src: document.getElementById("iamjssdk_sns_qrcodePanel"),
					width: "250",
					height: "260"
				},
				// 定义新开的TAB授权页的配置
				pagePanel: {
					"width": "800px",
					"height": "500px",
					"left": "250px",
					"top": "100px"
				},
				// 定义支持的社交网络服务商配置
				provider: {
					// "qq": { // 服务商名(需与后台对应, 可选：qq/wechat/sina/github/google/dingtalk/twitter/facebook等)
					// 	panelType: "pagePanel", // 使用新开TAB页的方式渲染授权页面
					// 	src: document.getElementById("qq") // 绑定QQ授权点击事件源
					// },
					"wechat": { // 服务商名(需与后台对应, 可选：qq/wechat/sina/github/google/dingtalk/twitter/facebook等)
						panelType: "qrcodePanel", // 使用内嵌的方式渲染扫码授权页面
						src: document.getElementById("iamjssdk_login_link_scan") // 绑定Wechat授权点击事件源
					}
				},
				// 点击SNS服务商授权请求之前回调事件
				onBefore: function (provider, panelType) {
					if (provider == 'wechat') { // 只有微信等扫码登录时，才切换tab
						_changeTab('iamjssdk_login_scan_panel', 'iamjssdk_login_scan_pass');
					}
					// 执行后续逻辑，返回false会阻止继续
					return true;
				}
			}
		};

		// Overerly default settings.
		iamCoreConfig = $.extend(true, defaultSettings, iamCoreConfig);
		IAMCore.Console.debug("IAMCore JSSDK intializing ... config properties: " + JSON.stringify(iamCoreConfig));
		runtime.iamCore = new IAMCore(iamCoreConfig);
		runtime.iamCore.anyAuthenticators().build();
	}

	// 监听panelType为pagePanel类型的SNS授权回调
	$(function() {
		window.onmessage = function (e) {
			if(e && e.data && !Common.Util.isEmpty(e.data)) {
				try {
					window.location.href = JSON.parse(e.data).refresh_url;
				} catch(e) {
					IAMCore.Console.error("Can't parse event message, e.data: ", e.data);
				}
			}
		}
	});

})(window, document);
