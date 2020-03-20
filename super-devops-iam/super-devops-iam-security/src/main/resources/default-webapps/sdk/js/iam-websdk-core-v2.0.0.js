/**
 * Iam core v2.0.0 | (c) 2017 ~ 2050 wl4g Foundation, Inc.
 * Copyright 2017-2032 <wangsir@gmail.com, 983708408@qq.com>, Inc. x
 * Licensed under Apache2.0 (https://github.com/wl4g/super-devops/blob/master/LICENSE)
 */
(function(window, document) {

	// Basic constant definition.
    var constant = {
        baseUriStoredKey : '__$IAM_BASEURI_STORED_KEY',
    };

	// 运行时值/状态临时缓存
	var runtime = {
		safeCheck: { // Safe check result.
			checkGeneral: {
				secret: null,
			},
			checkCaptcha: {
				enabled: false,
				support: null,
				applyUri: null,
			},
			checkSms: {
				enabled: false,
				mobileNum: null,
				remainDelayMs: null,
			}
		},
		applyModel: { // Apply captcha result.
			primaryImg: null,
			applyToken: null,
			verifyType: null,
		},
		verifiedModel: { // Verify & analyze captcha result.
			verified: true,
			verifiedToken: null,
		},
		flags: { // Runtime status flag(Prevention concurrent).
			isApplying: false,
			isVerifying: false,
		}
	};

	// DefaultCaptcha配置实现(jpeg/gif验证码)
	var _defaultCaptchaVerifier = {
		captchaLen: 5,
		cancel: function(destroy) {
			var imgInput = Common.Util.checkEmpty("captcha.input", settings.captcha.input);
			var img = Common.Util.checkEmpty("captcha.img", settings.captcha.img);
			// UnBind refresh captcha.
			$(img).unbind("click");
			$(img).attr({"src": "./static/images/ok.png"});
			$(imgInput).attr('disabled',true);
			$(imgInput).css({"cursor":"context-menu"});
			if(destroy){
				$(imgInput).val(""); // 清空验证码input
				$(imgInput).css({"display":"none"});
				$(img).attr({"src": ""});
				$(img).css({"display":"none"});
			}
		},
		required: function() {
			// Set the current application verify code.
			runtime.flags.isApplying = false;

			var imgInput = $(Common.Util.checkEmpty("captcha.input", settings.captcha.input));
			var img = Common.Util.checkEmpty("captcha.img", settings.captcha.img);
			imgInput.val(""); // 清空验证码input
			// Bind refresh captcha.
			$(img).click(function(){ resetCaptcha(); });
			// 请申请Captcha
			$.ajax({
				url: getApplyCaptchaUrl(),
				type: "get",
				dataType: "json",
				xhrFields: { withCredentials: true }, // Send cookies when support cross-domain request.
				success: function(res) {
					// Apply captcha completed.
					runtime.flags.isApplying = false;
					runtime.applyModel = res.data.applyModel; // [MARK4]
					$(imgInput).css({"display":"none","cursor":"text"});
					$(imgInput).removeAttr('disabled');
					$(img).css({"display" : "none"});
					var codeOkValue = Common.Util.checkEmpty("definition.codeOkValue",settings.definition.codeOkValue);
					if(!Common.Util.isEmpty(res) && res.code == codeOkValue){ // Success?
						$(img).attr("src", res.data.applyModel.primaryImg);
					} else {
						$(img).attr("title", res.message); // 如:刷新过快
						$(img).unbind("click");
						setTimeout(function(){
							$(img).click(function(){ resetCaptcha(); });
						}, 15000); // 至少15sec才能点击刷新
					}
				},
				error: function(req, status, errmsg){
					console.debug("Failed to apply captcha, " + errmsg);
					Common.Util.checkEmpty("captcha.onError", settings.captcha.onError)(errmsg);
				}
			});
		},
	};

	// Global settings.
	var settings = {
		// 字典参数定义
		definition: {
			responseType: "response_type", // 控制返回数据格式的参数名
			responseTypeValue: "json", // 使用返回数据格式
			whichKey: "which", // 请求连接到SNS的参数名
			redirectUrlKey: "redirect_url", // 重定向URL参数名
			refreshUrlKey: "refresh_url", // 刷新URL参数名
			principalKey: "principal", // 提交账号参数名
			credentialKey: "credential", // 提交账号凭据(如：静态密码/SMS验证码)参数名
			verifyTypeKey: "verifyType", // 验证码verifier别名参数名（通用）
			applyTokenKey: "applyToken", // 申请的验证码f返回token参数名（通用）
			verifyCodeKey: "verifyCode", // 提交验证码参数名（不通用：simple/gif）
			verifiedTokenKey: "verifiedToken", // 验证码已校验的凭据token参数名（通用）
			clientRefKey: "client_ref", // 提交登录的客户端类型参数名
			accountSubmitUri: "/auth/generic", // 账号登录提交的URL后缀
			smsSubmitUri: "/auth/sms", // SMS登录提交的URL后缀
			checkUri: "/login/check", // 登录前初始检查接口的URL后缀
			captchaApplyUri: "/verify/applycaptcha", // 申请GRAPH验证码URI后缀
			verifyAnalyzeUri: "/verify/verifyAnalyze", // 校验分析GRAPH验证码URI后缀
			smsApplyUri: "/verify/applysmsverify", // 申请SMS验证码URI后缀
			snsConnectUri: "/sns/connect/", // 请求连接到社交平台的URL后缀
			codeOkValue: "200" // 接口返回成功码判定标准
		},
		deploy: { // 部署配置
			baseUri: null, // IAM后端服务baseURI
			defaultTwoDomain: "iam", // IAM后端服务部署二级域名，当iamBaseUri为空时，会自动与location.hostnamee拼接一个IAM后端地址.
			defaultContextPath: "/iam-server", // 默认IAM Server的context-path
		},
 		init: { // 初始相关配置(Event)
 			onPreCheck: function(principal, checkUrl){
 				console.debug("onPostCheck... principal:"+ principal +", checkUrl:"+ checkUrl);
 				return true; // continue after?
 			},
 			onPostCheck: function(res){
 				console.debug("onPostCheck... " + res);
 			},
 			onError: function(req, status, errmsg){
 				console.error("Failed to initialize... "+ errmsg);
 			}
 		},
		// 图像验证码配置
		captcha: {
			use: "VerifyWithGifGraph", // Default use gif
			panel: null,
			img: null,
			input: null,
			getVerifier: function(){ // Get verifier(captcha) instance.
				var _type = Common.Util.checkEmpty("captcha.use", settings.captcha.use);
				var _registry = Common.Util.checkEmpty("captcha.registry", settings.captcha.registry);
				for(var type in _registry){
					if(type == _type){
						return _registry[type];
					}
				}
				throw "Illegal verifier type for '" + type + "'";
			},
			registry: { // 图像验证码实现程序注册器
				VerifyWithSimpleGraph: _defaultCaptchaVerifier,
				VerifyWithGifGraph: _defaultCaptchaVerifier,
				VerifyWithJigsawGraph: {  // JigsawCaptcha配置实现
					cancel: function(destroy) {
						var jigsawPanel = Common.Util.checkEmpty("captcha.panel", settings.captcha.panel);
						if(destroy){
							$(jigsawPanel).css({"display":"none"});
						}
					},
					required: function() {
						// Set the current application verify code.
						runtime.flags.isApplying = false;
						var jigsawPanel = Common.Util.checkEmpty("captcha.panel", settings.captcha.panel);

						// 加载Jigsaw插件滑块
                        $(jigsawPanel).JigsawIamCaptcha({
                            applycaptchaUrl: getApplyCaptchaUrl(),
							applyverifyUrl: getVerifyCaptchaUrl(),
                            repeatIcon: 'fa fa-redo',
                            onSuccess: function (verifiedToken) {
								console.debug("Jigsaw captcha verify successful. verifiedToken is '"+ verifiedToken + "'");
								runtime.flags.isApplying = false; // Apply captcha completed.
								runtime.verifiedModel.verifiedToken = verifiedToken; // [MARK4], See: 'MARK2'
								Common.Util.checkEmpty("captcha.onSuccess", settings.captcha.onSuccess)(verifiedToken);
                            },
							onFail: function(element){
								console.debug("Failed to jigsaw captcha verify. element => "+ element);
								runtime.flags.isApplying = false; // Apply captcha completed.
								runtime.verifiedModel.verifiedToken = ""; // Clear
								Common.Util.checkEmpty("captcha.onError", settings.captcha.onError)(element);
							}
                        });
					},
				},
			},
			onSuccess: function(verifiedToken) {
				console.debug("Jigsaw captcha verify successfully. verifiedToken is '"+ verifiedToken+"'");
			},
			onError: function(errmsg) { // 如:申请过于频繁
				console.error("Failed to jigsaw captcha verify. " + errmsg);
			}
		},
		// 账号认证配置
		account: {
			submitBtn: null, // 登录提交触发对象
			principal: null, // 登录账号input对象
			credential: null, // 登录凭据input对象
			onBeforeSubmit: function(principal, credentials, verifiedToken){ // 默认提交之前回调实现
				console.debug("Prepare to submit login request. principal=" + principal + ", verifiedToken=" + verifiedToken);
				return true;
			},
			onSuccess: function(principal, redirectUrl){ // 登录成功回调
				console.info("Sign in successfully. " + principal + ", " + redirectUrl);
				return true;
			},
			onError: function(errmsg){ // 登录异常回调
				console.error("Sign in error. " + errmsg);
			}
		},
		// SMS认证配置
		sms: {
			submitBtn: null, // 登录提交触发对象
			sendSmsBtn: null, // 发送SMS动态密码对象
			mobileArea: null, // 手机号区域select对象
			mobile: null, // 手机号input对象
			onBeforeSubmit: function(mobileNum, smsCode){ // 默认SMS提交之前回调实现
				//throw "Unsupported errors, please implement to support login submission";
				console.log("Prepare to submit SMS login request. mobileNum=" + mobileNum + ", smsCode=" + smsCode);
				return true;
			},
			onSuccess: function(resp){
				console.log("SMS success. " + resp.message);
			},
			onError: function(errmsg){ // SMS登录异常回调
				throw "SMS login error. " + errmsg;
			}
		},
		// SNS授权认证配置
		sns: {
			required: { // 必须的参数
				getWhich: function(provider, panelType){ // 获取参数'which'
					throw "Unsupported errors, please implement to support get which function";
				},
				// 回调刷新URL（如：绑定操作）
				refreshUrl: null
			},
			// 获取用户ID（如：绑定和解绑时必须）
			getPrincipal: function(){},
			// 渲染授权二维码面板配置
			qrcodePanel: null,
			// 渲染授权页面面板配置
			pagePanel: null,
			// 第三方社交网络配置
			provider: null,
			// 点击SNS服务商授权请求之前回调实现
			onBefore: function(provider, panelType, connectUrl){}
		}
	};

	// Configure settings
	var _configure = function(obj) {
		// 将外部配置深度拷贝到settings，注意：Object.assign(oldObj, newObj)只能浅层拷贝
		settings = jQuery.extend(true, settings, obj);
		console.debug("Default iamBaseURI: "+ settings.deploy.baseUri);

		if (Common.Util.isEmpty(settings.deploy.baseUri)) {
			// 获取地址栏默认baseUri
			var hostname = location.hostname;
			var pathname = location.pathname;
			var twoDomain = settings.deploy.defaultTwoDomain;
			var contextPath = settings.deploy.defaultContextPath;
			contextPath = contextPath.startsWith("/") ? contextPath : ("/" + contextPath);
			var port = location.port;
			var protocol = location.protocol;
		 	// 为了可以自动配置IAM后端接口基础地址，下列按照不同的部署情况自动获取iamBaseURi。
		 	// 1. 以下情况会认为是非完全分布式部署，随地址栏走，即认为所有服务(接口地址如：10.0.0.12:14040/iam-server, 10.0.0.12:14046/ci-server)都部署于同一台机。
		 	// a，当访问的地址是IP；
		 	// b，当访问域名的后者是.debug/.local/.dev等。
	        if (hostname == 'localhost' || hostname == '127.0.0.1' || Common.Util.isIp(hostname) || hostname.endsWith('.debug')
	        		|| hostname.endsWith('.local') || hostname.endsWith('.dev')) {
	        	settings.deploy.baseUri = protocol+"//"+hostname+":14040"+contextPath;
	        }
	        // 2. 使用域名部署时认为是完全分布式部署，自动生成二级域名，(接口地址如：iam-server.wl4g.com/iam-server, ci-server.wl4g.com/ci-server)每个应用通过二级子域名访问
	        else {
	        	var topDomainName = hostname.split('.').slice(-2).join('.');
	        	if(hostname.indexOf("com.cn") > 0) {
	        		topDomainName = hostname.split('.').slice(-3).join('.');
	        	}
	            settings.deploy.baseUri = protocol+"//"+twoDomain+"."+topDomainName+contextPath;
	        }
	    }

		// Sets iamBaseUri
        window.sessionStorage.setItem(constant.baseUriStoredKey, settings.deploy.baseUri);
        console.debug("Using overlay iamBaseURI: "+ settings.deploy.baseUri);
	};

	// 请求连接到第三方社交网络URL
	var getSnsConnectUrl = function(provider, panelType){
		var required = Common.Util.checkEmpty("sns.required", settings.sns.required);
		var which = Common.Util.checkEmpty("required.getWhich", required.getWhich(provider, panelType));
		var url = settings.deploy.baseUri + Common.Util.checkEmpty("definition.snsConnectUri", settings.definition.snsConnectUri) 
			+ Common.Util.checkEmpty("provider",provider) + "?" + Common.Util.checkEmpty("definition.whichKey",settings.definition.whichKey) + "=" + which;

		// 当绑定时必传 principal/refreshUrl
		if(which.toLowerCase() == "bind" || which.toLowerCase() == "unbind"){
			var principal = encodeURIComponent(Common.Util.checkEmpty("sns.principal", settings.sns.getPrincipal())); // 获取用户ID
			var refreshUrl = encodeURIComponent(Common.Util.checkEmpty("sns.required.refreshUrl", settings.sns.required.refreshUrl)); // 回调刷新URL
			url += "&" + Common.Util.checkEmpty("definition.principalKey", settings.definition.principalKey) + "=" + principal;
			url += "&" + Common.Util.checkEmpty("definition.refreshUrlKey", settings.definition.refreshUrlKey) + "=" + refreshUrl;
		}

		// window.open新开的窗体授权登录（如：qq的PC端授权登录是鼠标操作、sina是输入账号密码）
		if(panelType == "pagePanel"){
			url += "&agent=y"; // window.open的登录页，需使用agent页面来处理逻辑(如：自动执行关闭子窗体)
		} else if(panelType == "qrcodePanel"){
			url += "&agent=n";
		}
		return url;
	};

	// Make get apply captcha URL.
	var getApplyCaptchaUrl = function(){
		var captchaUrl = Common.Util.checkEmpty("checkCaptcha.applyUri", runtime.safeCheck.checkCaptcha.applyUri) + "?"
			+ Common.Util.checkEmpty("definition.verifyTypeKey", settings.definition.verifyTypeKey) + "=" 
			+ Common.Util.checkEmpty("captcha.use", settings.captcha.use) + "&r=" + Math.random();
		return captchaUrl;
	};

	// Make get verify & analyze captcha URL.
	var getVerifyCaptchaUrl = function(){
		var verifyUrl = Common.Util.checkEmpty("deploy.baseUri",settings.deploy.baseUri) + Common.Util.checkEmpty("definition.verifyAnalyzeUri",settings.definition.verifyAnalyzeUri) + "?"
			+ Common.Util.checkEmpty("definition.verifyTypeKey", settings.definition.verifyTypeKey) + "="
			//+ Common.Util.checkEmpty("applyModel.verifyType",runtime.applyModel.verifyType) + "&"
			+ Common.Util.checkEmpty("captcha.use", settings.captcha.use) + "&"
			+ Common.Util.checkEmpty("definition.responseType", settings.definition.responseType) + "="
			+ Common.Util.checkEmpty("definition.responseTypeValue",settings.definition.responseTypeValue);
		return verifyUrl;
	};

	// Reset graph captcha.
	var resetCaptcha = function(){
		_InitSafeCheck(function(checkCaptcha, checkGeneral, checkSms){
			if(checkCaptcha.enabled && !runtime.flags.isApplying){ // 启用验证码且不是申请中(防止并发)?
				// 获取当前配置CaptchaVerifier实例、显示
				Common.Util.checkEmpty("captcha.getVerifier", settings.captcha.getVerifier)().required();
			}
		});
	};

	// 渲染SNS授权二维码或页面, 使用setTimeout以解决 如,微信long请求导致父窗体长时间处于加载中问题
	var snsViewReader = function(connectUrl, panelType) {
		// 渲染授权二维码面板配置
		if("qrcodePanel" == panelType){
			// 获取已创建的iframe对象
			var qrcodeIframeId = "iam_qrcode_panel_iframe";
			var qrcodeIframe = document.querySelector('#'+qrcodeIframeId);
			if(qrcodeIframe == null || qrcodeIframe.length <= 0){
				var qrcodePanel = Common.Util.checkEmpty("sns.qrcodePanel", settings.sns.qrcodePanel);
				var qrcodePanelSrc = Common.Util.checkEmpty("qrcodePanel.src", qrcodePanel.src);
				var qrcodePanelW = qrcodePanel.width || "250";
				var qrcodePanelH = qrcodePanel.height || "260";
				qrcodeIframe = document.createElement("iframe"); // 初始化创建iframe
				qrcodeIframe.setAttribute("id", qrcodeIframeId);
				qrcodeIframe.setAttribute("frameborder", "1");
				qrcodeIframe.setAttribute("scrolling", "no");
				qrcodeIframe.setAttribute("width", qrcodePanelW);
				qrcodeIframe.setAttribute("height", qrcodePanelH);
				qrcodeIframe.setAttribute("style", "border:solid 0;");
				// 追加到qrcode显示面板
				qrcodePanelSrc.appendChild(qrcodeIframe);
			}

			// 异步渲染扫码授权页面
			setTimeout(function() {
				var qrcodeIframe = document.querySelector('#'+qrcodeIframeId);
				if (-1 == navigator.userAgent.indexOf("MSIE")) {
					qrcodeIframe.src = connectUrl;
				} else {
					qrcodeIframe.location = connectUrl;
				}
			}, 2);
		} else if ("pagePanel" == panelType) { // 渲染授权页面面板配置
			var pagePanel = Common.Util.checkEmpty("sns.pagePanel", settings.sns.pagePanel);
			var modal = pagePanel.modal || "yes";
			var width = pagePanel.width || "800px";
			var height = pagePanel.height || "500px";
			var left = pagePanel.left || "250px";
			var top = pagePanel.top || "100px";
			var resizable = pagePanel.resizable || "no";
			var oauth2ChildWindow = window.open(connectUrl, window, "modal="+modal+",width="+width+",height="+height+",resizable="+resizable+",left="+left+",top="+top);

			// 主窗体轮询检查子窗体是否已关闭
			var monitor = setInterval(function() {
				var refreshUrl = window.document.getElementsByTagName("body")[0].getAttribute("refreshUrl");
				if(oauth2ChildWindow != null && oauth2ChildWindow.closed) {
					clearInterval(monitor);
					if(!Common.Util.isEmpty(refreshUrl)){ // 可能未授权(用户直接点击了关闭子窗体),只有绑定的refreshUrl不为空时才表示授权成功
						// Jump to callback refreshUrl
						Common.Util.getRootWindow(window).location.href = refreshUrl;
					}
				}
			}, 200);
		} else {
			throw "Unsupported panelType, use 'qrcodePanel' or 'pagePanel'";
		}
	};

	// Init SNS authorize authentication login implement.
	var _InitSNSAuthenticator = function(){
		var providerMap = Common.Util.checkEmpty("sns.provider", settings.sns.provider);
		for(var provider in providerMap){ // provider为服务商名
			// 获取服务商配置信息
			var providerValue = providerMap[provider];
			// 获取点击触发源对象
			var src = Common.Util.checkEmpty(provider + ".src", providerValue.src);
			var panelType = Common.Util.checkEmpty(provider + ".panelType", providerValue.panelType);
			src.setAttribute("provider", provider); // 保存SNS服务商名
			src.setAttribute("panelType", panelType); // 请求SNS服务商授权时，显示确认授权页面的面板类型
			src.onclick = function(event){
				var curProviderEle = event.srcElement; 
				var provider = curProviderEle.getAttribute("provider");
				var panelType = curProviderEle.getAttribute("panelType");

				// 请求社交网络认证的URL（与which、action相关）
				var connectUrl = getSnsConnectUrl(provider, panelType);

				// 执行点击SNS按钮事件
				if(!settings.sns.onBefore(provider, panelType, connectUrl)){
					console.warn("onBefore has blocked execution");
					return this;
				}

				// 渲染SNS登录二维码或页面
				snsViewReader(connectUrl, panelType);
			}
		}
	};

	// Before safe check.
	var _InitSafeCheck = function(callback){
		$(function(){
			var principal = encodeURIComponent(Common.Util.getEleValue("account.principal", settings.account.principal, false));
			var checkUrl = Common.Util.checkEmpty("deploy.baseUri",settings.deploy.baseUri)
				+ Common.Util.checkEmpty("definition.checkUri",settings.definition.checkUri) + "?"
				+ Common.Util.checkEmpty("definition.principalKey",settings.definition.principalKey) + "=" + principal + "&"
				+ Common.Util.checkEmpty("definition.verifyTypeKey", settings.definition.verifyTypeKey) + "=" 
				+ Common.Util.checkEmpty("captcha.use", settings.captcha.use);

			// 初始化前回调
			if(!Common.Util.checkEmpty("init.onPreCheck", settings.init.onPreCheck)(principal, checkUrl)){
				console.warn("Skip the init safeCheck, because onPreCheck() return false");
				return;
			}

			// 请求安全预检
			$.ajax({
				url: checkUrl,
				type: "post",
				xhrFields: { withCredentials: true }, // Send cookies when support cross-domain request.
				cache: false,
				dataType: "json",
				headers:{'Content-Type':'application/x-www-form-urlencoded;charset=utf8'},
				success: function(res, textStatus, jqxhr){
					// 初始化完成回调
					Common.Util.checkEmpty("init.onPostCheck", settings.init.onPostCheck)(res);

					var codeOkValue = Common.Util.checkEmpty("definition.codeOkValue",settings.definition.codeOkValue);
					if(!Common.Util.isEmpty(res) && (res.code == codeOkValue)){
						runtime.safeCheck = res.data; // [MARK3]
						callback(res.data.checkCaptcha, res.data.checkGeneral, res.data.checkSms);
					}
				},
				error: function(req, status, errmsg){
					console.log("Failed to safe check, " + errmsg);
					Common.Util.checkEmpty("init.onError", settings.init.onError)(req, status, errmsg); // 登录异常回调
				}
			});
		});
	};

	// Init captcha verifier implement.
	var _InitCaptchaVerifier = function(){
		$(function(){
			// 初始刷新验证码
			resetCaptcha();

			// Init captcha event.
			if(settings.captcha.use == "VerifyWithSimpleGraph" || settings.captcha.use == "VerifyWithGifGraph") {
				var imgInput = $(Common.Util.checkEmpty("captcha.input", settings.captcha.input));
				// Set captcha input maxLength.
				imgInput.attr("maxlength", Common.Util.checkEmpty("captcha.getVerifier", settings.captcha.getVerifier)().captchaLen);

				// Auto verify simple/gif captcha for key-up event.  [MARK1], see: 'MARK2'
				imgInput.keyup(function(){
					if(runtime.safeCheck.checkCaptcha.enabled){ // See: 'MARK3'
						var captcha = imgInput.val();
						if(!Common.Util.isEmpty(captcha) && captcha.length >= parseInt(imgInput.attr("maxlength")) && !runtime.flags.isVerifying){
							runtime.flags.isVerifying = true; // Set verify status.

							// Submission verify analyze captcha.
							var _check = function(name, params){ return Common.Util.checkEmpty(name, params) };
							var _map = new Common.Util.FastMap();
							_map.put(_check("definition.verifyCodeKey", settings.definition.verifyCodeKey), captcha);
							_map.put(_check("definition.applyTokenKey", settings.definition.applyTokenKey), _check("applyModel.applyToken", runtime.applyModel.applyToken));
							_map.put(_check("definition.verifyTypeKey", settings.definition.verifyTypeKey), _check("applyModel.verifyType", runtime.applyModel.verifyType));

							$.ajax({
								url: getVerifyCaptchaUrl(),
								type: "post",
								xhrFields: { withCredentials: true }, // Send cookies when support cross-domain request.
								dataType: "json",
								contentType:"application/json",
								data: _map.asJsonString(),
								complete: function (XHR, textStatus) {
									runtime.flags.isVerifying = false; // Reset verify status.
								},
								success: function(res){
									var codeOkValue = _check("definition.codeOkValue",settings.definition.codeOkValue);
									if(!Common.Util.isEmpty(res) && (res.code != codeOkValue)){ // Failed?
										resetCaptcha();
										settings.captcha.onError(res.message); // Call after captcha error.
									} else { // Verify success.
										runtime.verifiedModel = res.data.verifiedModel;
										Common.Util.checkEmpty("captcha.getVerifier", settings.captcha.getVerifier)().cancel(false); // Hide captcha when success.
									}
								},
								error: function(req, status, errmsg){
									resetCaptcha();
									settings.captcha.onError(errmsg); // Call after captcha error.
								}
							});
						}
					}
				});
			};
		});
	};

	// Init Account login implement.
	var _InitAccountAuthenticator = function(){
		$(function(){
			// Init bind key-enter login submit.
			$(document).bind("keydown",function(event){
				if(event.keyCode == 13){
					$(Common.Util.checkEmpty("account.submitBtn", settings.account.submitBtn)).click();
				}
			});

			// Bind login btn click.
			$(Common.Util.checkEmpty("account.submitBtn", settings.account.submitBtn)).click(function() {
				var principal = encodeURIComponent(Common.Util.getEleValue("account.principal", settings.account.principal));
				// 获取明文密码并非对称加密，同时编码(否则base64字符串中有“+”号会自动转空格，导致登录失败)
				var plainPasswd = Common.Util.getEleValue("account.credential", settings.account.credential);
				// Check principal/password.
				if(Common.Util.isAnyEmpty(principal, plainPasswd)){
					settings.account.onError(Common.Util.isZhCN()?"请输入账户名和密码":"Please input your account and password");
					return;
				}

				_InitSafeCheck(function(checkCaptcha, checkGeneral, checkSms){
					var secret = Common.Util.checkEmpty("Error for secret is empty", checkGeneral.secret);
					var credentials = encodeURIComponent(IAM.Crypto.RSA.encryptToHexString(secret, plainPasswd));
					var verifiedToken = "";
					if(runtime.safeCheck.checkCaptcha.enabled){
						verifiedToken = runtime.verifiedModel.verifiedToken; // [MARK2], see: 'MARK1,MARK4'
						if(Common.Util.isEmpty(verifiedToken)){ // Required
							settings.account.onError(Common.Util.isZhCN()?"请完成人机验证":"Please complete the man-machine verify");
							return;
						}
					}
					// Check principal/credentials.
					if(Common.Util.isAnyEmpty(principal, credentials)){
						settings.account.onError("No empty login name or password allowed");
						return;
					}

					// Call before submission login.
					if(!settings.account.onBeforeSubmit(principal, credentials, verifiedToken)){
						return;
					}

					// Submission URL
					var loginSubmitUrl = Common.Util.checkEmpty("deploy.baseUri",settings.deploy.baseUri)
						+ Common.Util.checkEmpty("definition.accountSubmitUri",settings.definition.accountSubmitUri) + "?"
						+ Common.Util.checkEmpty("definition.responseType",settings.definition.responseType) + "="
						+ Common.Util.checkEmpty("definition.responseTypeValue",settings.definition.responseTypeValue)
						+ "&" + Common.Util.checkEmpty("definition.principalKey",settings.definition.principalKey) + "=" + principal
						+ "&" + Common.Util.checkEmpty("definition.credentialKey",settings.definition.credentialKey) + "=" + credentials
						+ "&" + Common.Util.checkEmpty("definition.verifiedTokenKey",settings.definition.verifiedTokenKey) + "=" + verifiedToken
						+ "&" + Common.Util.checkEmpty("definition.verifyTypeKey", settings.definition.verifyTypeKey) + "=" + Common.Util.checkEmpty("captcha.use", settings.captcha.use)
						+ "&" + Common.Util.checkEmpty("definition.clientRefKey",settings.definition.clientRefKey) + "=" + clientRef();

					// Submission
					$.ajax({
						url: loginSubmitUrl,
						type: "post",
						xhrFields: { withCredentials: true }, // Send cookies when support cross-domain request.
						dataType: "json",
						beforeSend: function(){
							$(Common.Util.checkEmpty("account.submitBtn", settings.account.submitBtn)).attr("disabled", true);
						},
						complete: function (XHR, textStatus) {
							$(Common.Util.checkEmpty("account.submitBtn", settings.account.submitBtn)).removeAttr("disabled");
						},
						success: function(resp){
							runtime.verifiedModel.verifiedToken = ""; // Clear
							var codeOkValue = Common.Util.checkEmpty("definition.codeOkValue",settings.definition.codeOkValue);
							if(!Common.Util.isEmpty(resp) && (resp.code != codeOkValue)){ // Failed?
								resetCaptcha(); // 刷新验证码
								settings.account.onError(resp.message); // 登录失败回调
							} else { // 登录成功，直接重定向
                                $(document).unbind("keydown");
								var redirectUrl = Common.Util.checkEmpty("Login successfully, response data.redirect_url is empty", resp.data[settings.definition.redirectUrlKey]);
								if(settings.account.onSuccess(principal, redirectUrl)){
							      Common.Util.getRootWindow(window).location.href = redirectUrl;
								}
							}
						},
						error: function(req, status, errmsg){
							runtime.verifiedModel.verifiedToken = ""; // Clear
							settings.account.onError(errmsg); // 登录异常回调
						}
					});
				});
			});
		});
	};

	// Init SMS authentication implement.
	var _InitSMSAuthenticator = function(){
		$(function(){
			// 绑定申请SMS验证码按钮点击事件
			$(Common.Util.checkEmpty("sms.sendSmsBtn", settings.sms.sendSmsBtn)).click(function(){
				// 获取手机号
				var mobileArea = Common.Util.getEleValue("sms.mobileArea", settings.sms.mobileArea, false);
				var mobileNum = mobileArea + Common.Util.getEleValue("sms.mobile", settings.sms.mobile, false);
				if(Common.Util.isEmpty(mobileNum)){
					settings.sms.onError("SMS login for mobile number is required.");
					return;
				}

				// 检查输入的验证码
				var imgInput = $(Common.Util.checkEmpty("captcha.input", settings.captcha.input));
				var captcha = imgInput.val();
				if(runtime.safeCheck.captchaEnabled){ // 启用时才检查
					if(Common.Util.isEmpty(captcha) || captcha.length < imgInput.attr("maxlength")){ // 检查验证码
						settings.account.onError("Illegal length of captcha input");
						return;
					}
				}

				var url = Common.Util.checkEmpty("deploy.baseUri",settings.deploy.baseUri) + Common.Util.checkEmpty("definition.smsApplyUri",settings.definition.smsApplyUri)
					+ "?" + Common.Util.checkEmpty("definition.principalKey",settings.definition.principalKey) + "=" + encodeURIComponent(mobileNum)
					+ "&" + Common.Util.checkEmpty("definition.verifiedTokenKey",settings.definition.verifiedTokenKey) + "=" + captcha;
				// 请求申请SMS验证码
				$.ajax({
					url: url,
					type: "post",
					xhrFields: { withCredentials: true }, // Send cookies when support cross-domain request.
					dataType: "json",
					success: function (resp) {
						var codeOkValue = Common.Util.checkEmpty("definition.codeOkValue",settings.definition.codeOkValue);
						// 登录失败
						if(!Common.Util.isEmpty(resp) && (resp.code != codeOkValue)){
							settings.sms.onError(resp.message); // 申请失败回调
						} else {
							settings.sms.onSuccess(resp); // 申请成功回调
							var remainDelaySec = resp.data.checkSms.remainDelayMs/1000;
							var num = parseInt(remainDelaySec);
							var timer = setInterval(() => {
								var sendSmsBtn = $(settings.sms.sendSmsBtn);
								if (num < 1) {
									sendSmsBtn.attr('disabled', false);
									sendSmsBtn.text('获取');
									clearInterval(timer);
								} else {
									sendSmsBtn.attr('disabled', true);
									sendSmsBtn.text(num + 's');
									num--;
								}
							}, 1000);
						}
					},
					error(req, status, errmsg) {
						settings.sms.onError(errmsg); // 申请失败回调
					}
				});
			});
			// 绑定SMS登录提交按钮点击事件
			$(Common.Util.checkEmpty("sms.submitBtn", settings.sms.submitBtn)).click(function(){
				// 获取手机号
				var mobileArea = Common.Util.getEleValue("sms.mobileArea", settings.sms.mobileArea, false);
				var mobileNum = mobileArea + Common.Util.getEleValue("sms.mobile", settings.sms.mobile, false);
				var smsCode = Common.Util.getEleValue("sms.smsCode", settings.sms.smsCode, false);

				// 提交SMS登录之前回调
				if(!settings.sms.onBeforeSubmit(mobileNum, smsCode)){
					return;
				}

				var url = Common.Util.checkEmpty("deploy.baseUri",settings.deploy.baseUri)+Common.Util.checkEmpty("definition.smsSubmitUri",settings.definition.smsSubmitUri)
					+ "?action=login&" + Common.Util.checkEmpty("definition.principalKey",settings.definition.principalKey) + "=" + mobileNum
					+ "&" + Common.Util.checkEmpty("definition.credentialKey",settings.definition.credentialKey) + "=" + smsCode;
				$.ajax({
					url: url,
					type: "post",
					xhrFields: {
						withCredentials: true // Send cookies when support cross-domain request.
					},
					dataType: "json",
					success: function (resp) {
						var codeOkValue = Common.Util.checkEmpty("definition.codeOkValue",settings.definition.codeOkValue);
						if(!Common.Util.isEmpty(resp) && (resp.code != codeOkValue)){
							settings.sms.onError(resp.message); // SMS登录失败回调
						} else {
							settings.sms.onSuccess(resp); // SMS登录成功回调
							Common.Util.getRootWindow(window).location.href = resp.data.redirect_url;
						}
					},
					error(req, status, errmsg) {
						settings.sms.onError(errmsg); // SMS登录失败回调
					}
				});
			});
		});
	};

	// Client device OS type mark.
	var clientRef = function(){
		var clientRef = null;
		var platformType = Common.Util.PlatformType;
		if(platformType.Windows){
			clientRef = "Windows";
		} else if (platformType.MicroMessenger) {
			clientRef = "Wechatmp";
		} else if (platformType.Android) {
			clientRef = "Android";
		} else if (platformType.iOS) {
			clientRef = "iOS";
		} else if (platformType.iPad) {
			clientRef = "iPad";
		} else if (platformType.iPhone) {
			clientRef = "iPhone";
		} else if (platformType.iOS) {
			clientRef = "iOS";
		} else if (platformType.Mac) {
			clientRef = "Mac";
		} else if (platformType.Linux) {
			clientRef = "Linux";
		} else if (platformType.Irix) {
			clientRef = "Irix";
		} else if (platformType.Solaris) {
			clientRef = "Solaris";
		} else if (platformType.AIX) {
			clientRef = "AIX";
		} else if (platformType.OpenBSD) {
			clientRef = "OpenBSD";
		} else if (platformType.FreeBSD) {
			clientRef = "FreeBSD";
		} else {
			clientRef = "Unknown";
			console.warn("Unknown platform browser ["+ navigator.appVersion +"]");
		}
		return clientRef;
	};

	// Check parent classy.
	if(!window.IAM){ window.IAM={} }

	// Exposing core APIs
	window.IAM.Core = {
		configure: function(opt){
			_configure(opt);
			return this;
		},
        getIamBaseUri: function(){
            return window.sessionStorage.getItem(constant.baseUriStoredKey);
        },
		bindForAccountAuthenticator: function(){
			_InitAccountAuthenticator();

			// 申请过SMS验证码?
			if(runtime.safeCheck.checkSms.enabled){
				// 填充mobile number.
				$(settings.sms.mobile).val(runtime.safeCheck.checkSms.mobileNum);
				// 继续倒计时
				var remainDelaySec = runtime.safeCheck.checkSms.remainDelayMs/1000;
				var num = parseInt(remainDelaySec);
				var timer = setInterval(() => {
					var sendSmsBtn = $(settings.sms.sendSmsBtn);
					if (num < 1) {
						sendSmsBtn.attr('disabled', false);
						sendSmsBtn.text('获取');
						clearInterval(timer);
					} else {
						sendSmsBtn.attr('disabled', true);
						sendSmsBtn.text(num + 's');
						num--;
					}
				}, 1000);
			}
			return this;
		},
		bindForSMSAuthenticator: function(){
			_InitSMSAuthenticator();
			return this;
		},
		bindForSNSAuthenticator: function(){
			_InitSNSAuthenticator();
			return this;
		},
		bindForCaptchaVerifier: function(){
			_InitCaptchaVerifier();
			return this;
		},
	};

})(window, document);
