/**
 * IAM WebSDK UI v2.0.0 | (c) 2017 ~ 2050 wl4g Foundation, Inc.
 * Copyright 2017-2032 <wangsir@gmail.com, 983708408@qq.com>, Inc. x
 * Licensed under Apache2.0 (https://github.com/wl4g/super-devops/blob/master/LICENSE)
 */
(function(window, document){
	// Exposing IAM UI
	window.IAMUi = function(){};

	// Runtime cache
	var runtime = {
		iamCore: null,	
	};

	/**
	 * Init IAM JSSDK UI.
	 * 
	 * @param renderId Render target object element id.
	 **/
	IAMUi.prototype.initUI = function(renderObj, iamCoreConfig) {
		if(!renderObj || renderObj == undefined){
			throw Error("IAM JSSDK UI (renderObj) is required!");
		}
		console.debug("Initializing IAM JSSDK UI...");

		// Javascript multi line string supports.
		// @see https://www.jb51.net/article/49480.htm
		var loginFormHtmlStr = `
				<div class="login-form">
					<div class="login-form-header">
						<span class="login-link active" id="login_link_account" data-panel="login_account_panel">
							账号登录
						</span>
						<!--<span class="login-line">
						</span>
						<span class="login-link" id="login_link_phone" data-panel="login_phone_panel">
							手机登录
						</span>-->
						<span class="login-line">
						</span>
						<span class="login-link" id="iam_jssdk_login_link_scan" data-panel="iam_jssdk_login_scan_panel">
							扫码登录
						</span>
					</div>
					<div class="login-form-tip" id="err_tip"></div>
					<div class="login-form-body">
						<!-- 密码登录-->
						<div class="login-form-panel active" id="login_account_panel">
							<form>
								<div class="login-form-item">
									<i class="icon-user">
									</i>
									<input class="inp" id="iam_jssdk_account_username" name="username" placeholder="请输入账号"
									maxlength="20">
								</div>
								<div class="login-form-item">
									<i class="icon-pass">
									</i>
									<input class="inp" id="iam_jssdk_account_password" name="iam_jssdk_account_password" type="password" placeholder="请输入密码"
									maxlength="35" autocapitalize="off" autocomplete="off">
								</div>
								<div class="login-form-item" id="iam_jssdk_captcha_panel">
									<!-- 拖动验证-->
								</div>
								<input class="btn" id="iam_jssdk_account_submit_btn" type="button" value="登录">
							</form>
						</div>
						<!-- 手机登录-->
						<div class="login-form-panel" id="login_phone_panel">
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
							<div class="login-form-item">
								<i class="icon-phone">
								</i>
								<input id="iam_jssdk_sms_user_phone" class="inp" name="phone" placeholder="请输入手机号" maxlength="11">
								<p class="err-info phone-err">
									请输入正确的手机号
								</p>
							</div>
							<div class="login-form-item login-form-item-number">
								<i class="icon-codeNumber">
								</i>
								<input id="iam_jssdk_sms_code" class="inp" type="text" placeholder="请输入短信动态码" maxlength=6>
								<button class="btn-code" type="button" id="iam_jssdk_sms_getcode_btn">
									获取
								</button>
								<p class="err-info pass-err">
									请输入短信验证码
								</p>
							</div>
							<input class="btn" id="iam_jssdk_sms_submit_btn" type="button" value="登录">
						</div>
						<!-- 微信登录-->
						<div class="login-form-panel" id="iam_jssdk_login_scan_panel">
							<div class="box-qrcode">
								<div id="iam_jssdk_sns_qrcodePanel" style="height:255px;">
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
		loginForm.appendTo($(renderObj));

		// 绑定UI Tab事件
		initUITab();

		// 绑定IAM SDK事件
		initUISDK(iamCoreConfig);
	};

	// Exposing IAMCore object
	IAMUi.prototype.getIAMCore = function() {
		return runtime.iamCore;
	};


	//
	// --- UI event processing function's. ---
	//

	function changeTab(showId, hideId) {
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
	
	// 绑定UI Tab事件
	function initUITab() {
		$(".login-link").click(function(){
		    var that = this;
		    $(".login-link").each(function(ele, obj){
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

	// 绑定IAM UI SDK事件
	function initUISDK(iamCoreConfig) {
		Common.Util.printSafeWarn("This browser function is for developers only. Please do not paste and execute any content here, which may cause your account to be attacked and bring you loss!");

		// Default settings.
		var defaultSettings = {
			deploy: {
	    		//baseUri: "http://localhost:14040/iam-server", // Using auto extra configure
	   			defaultTwoDomain: "iam", // IAM后端服务部署二级域名，当iamBaseUri为空时，会自动与location.hostnamee拼接一个IAM后端地址.
	 			deaultContextPath: "/iam-server" // IAMServerd的context-path
	 		},
	 		// 初始相关配置(Event)
	 		init: {
	 			onPostCheck: function(res) {
	 				// 因SNS授权（如:WeChat）只能刷新页面，因此授权错误消息只能从IAM服务加载
					var url = runtime.iamCore.getIamBaseUri() +"/login/errread";	
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
				panel: document.getElementById("iam_jssdk_captcha_panel"), // Jigsaw验证码时必须
				img: document.getElementById("iam_jssdk_captcha_img"), // 验证码显示 img对象（仅jpeg/gif时需要）
				input: document.getElementById("iam_jssdk_captcha_input"), // 验证码input对象（仅jpeg/gif时需要）
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
				submitBtn: document.getElementById("iam_jssdk_account_submit_btn"), // 登录提交触发对象
				principalInput: document.getElementById("iam_jssdk_account_username"), // 必填，获取填写的登录用户名
				credentialInput: document.getElementById("iam_jssdk_account_password"), // 获取登录账号密码，账号登录时必填
				onBeforeSubmit: function (principal, plainPasswd, captcha) { // 提交之前
					console.debug("Iam account login... principal: "+ principal+", plainPasswd: ******, captcha: "+captcha);
					return true;
				},
				onSuccess: function (data) {
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
				submitBtn: document.getElementById("iam_jssdk_sms_submit_btn"), // 登录提交触发对象
				sendSmsBtn: document.getElementById("iam_jssdk_sms_getcode_btn"), // 发送SMS验证码对象
				mobileArea: $(".select-area"), // 手机号区域select对象
				mobile: document.getElementById("iam_jssdk_sms_user_phone"), // 手机号input对象
				smsCode: document.getElementById("iam_jssdk_sms_code"), // SMS验证码input对象
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
					src: document.getElementById("iam_jssdk_sns_qrcodePanel"),
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
						src: document.getElementById("iam_jssdk_login_link_scan") // 绑定Wechat授权点击事件源
					}
				},
				// 点击SNS服务商授权请求之前回调事件
				onBefore: function (provider, panelType) {
					if (provider == 'wechat') { // 只有微信等扫码登录时，才切换tab
						changeTab('iam_jssdk_login_scan_panel', 'iam_jssdk_login_scan_pass');
					}
					// 执行后续逻辑，返回false会阻止继续
					return true;
				}
			}
		};

		runtime.iamCore = new IAMCore();
		// Overerly default settings.
		iamCoreConfig = $.extend(true, defaultSettings, iamCoreConfig);
		console.debug("Intializing iamCore of config properties: " + JSON.stringify(iamCoreConfig));
		runtime.iamCore.init(iamCoreConfig);
	}

	// 监听panelType为pagePanel类型的SNS授权回调
	$(function() {
		window.onmessage = function (e) {
			if(e && e.data && !Common.Util.isEmpty(e.data)) {
				window.location.href = JSON.parse(e.data).refresh_url;
			}
		}
	});

})(window, document);
