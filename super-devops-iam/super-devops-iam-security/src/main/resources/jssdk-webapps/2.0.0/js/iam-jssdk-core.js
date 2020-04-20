/**
 * IAM WebSDK CORE v2.0.0 | (c) 2017 ~ 2050 wl4g Foundation, Inc.
 * Copyright 2017-2032 <wangsir@gmail.com, 983708408@qq.com>, Inc. x
 * Licensed under Apache2.0 (https://github.com/wl4g/super-devops/blob/master/LICENSE)
 */
(function(window, document) {

	// Base constants definition.
    var constant = {
        baseUriStoredKey : '__IAM_BASEURI',
        umidTokenStorageKey : '__IAM_UMIDTOKEN',
        useSecureAlgorithmName: 'RSA', // 提交认证相关请求时，选择的非对称加密算法（ 默认：RSA）
    };

	// 运行时状态值/全局变量/临时缓存
	var runtime = {
		umid: {
			_value: null, // umidToken
			getValue: function() {
				return Common.Util.checkEmpty("Fatal error, umidToken value is null, No attention to call order (must be executed after " +
						"runtime.umid.getValuePromise())", runtime.umid._value);
			},
			_currentlyInGettingValuePromise: null, // 仅umid.getValuePromise使用
			getValuePromise: function() {
				// 若当前正在获取umidToken直接返回该promise对象（解决并发调用）
				if (runtime.umid._currentlyInGettingValuePromise) {
					return runtime.umid._currentlyInGettingValuePromise;
				}
				// 首先从缓存获取
				var cacheUmidToken = Common.Util.Codec.decodeBase58(sessionStorage.getItem(constant.umidTokenStorageKey));
				if(!Common.Util.isEmpty(cacheUmidToken)) {
					runtime.umid._value = cacheUmidToken;
					return new Promise((reslove, reject) => reslove(cacheUmidToken));
				}
				// 新请求获取umidToken/uaToken等(页面加载时调用一次即可)
				return (runtime.umid._currentlyInGettingValuePromise = new Promise((reslove, reject) => {
					// 获取设备指纹信息
					Common.Util.getFingerprint({}, function(fpObj){
						var umItem = new Map();
						// 设备指纹参数项(必须)
						umItem.set("userAgent", fpObj.components.get("userAgent"));
						umItem.set("platform", fpObj.components.get("platform"));
						umItem.set("pixelRatio", fpObj.components.get("pixelRatio"));
						umItem.set("timezone", fpObj.components.get("timezone"));
						umItem.set("language", fpObj.components.get("language"));
						umItem.set("cpuClass", fpObj.components.get("cpuClass"));
						umItem.set("touchSupport", fpObj.components.get("touchSupport"));
						umItem.set("deviceMemory", fpObj.components.get("deviceMemory"));
						umItem.set("availableScreenResolution", fpObj.components.get("availableScreenResolution"));
						// 基于Web指纹附加参数项(可选)
						umItem.set("canvas", CryptoJS.MD5(fpObj.components.get("canvas")).toString(CryptoJS.enc.Hex));
						umItem.set("webgl", CryptoJS.MD5(fpObj.components.get("webgl")).toString(CryptoJS.enc.Hex));
						umItem.set("indexedDb", fpObj.components.get("indexedDb"));
						umItem.set("sessionStorage", fpObj.components.get("sessionStorage"));
						umItem.set("localStorage", fpObj.components.get("localStorage"));
						umItem.set("colorDepth", fpObj.components.get("colorDepth"));
						// 请求握手
						var umidParam = new Map();
						// 规则算法(私有):用base58迭代随机n%3+1次得到指纹集合数据的编码密文data
						var umItemData = Common.Util.toUrl({}, umItem);
						var n = 100 + parseInt(Math.random() * 100);
						var iterations = parseInt(n % 3 + 1), umdata = umItemData;
						for (var i=0; i<iterations; i++){
							umdata = Common.Util.Codec.encodeBase58(umdata);
						}
						umdata = n + "!" + umdata;
						console.debug("Generated apply umidToken data: "+ umdata);
						umidParam.set("umdata", umdata);
						doIamRequest("post", "{applyUmTokenUri}", umidParam, function(res){
							Common.Util.checkEmpty("init.onPostUmidToken", settings.init.onPostUmidToken)(res); //获得token回调
							var codeOkValue = Common.Util.checkEmpty("definition.codeOkValue",settings.definition.codeOkValue);
							if(!Common.Util.isEmpty(res) && (res.code == codeOkValue)){
								console.debug("Got umidToken: " + res.data.umidToken);
								// Encoding umidToken
								var encodeUmidToken = Common.Util.Codec.encodeBase58(res.data.umidToken);
								sessionStorage.setItem(constant.umidTokenStorageKey, encodeUmidToken);
								// Completed
								reslove(res.data.umidToken);
								runtime.umid._value = res.data.umidToken;
								runtime.umid._currentlyInGettingValuePromise = null;
							}
						}, function(errmsg){
							console.warn("Failed to gets umidToken, " + errmsg);
							Common.Util.checkEmpty("init.onError", settings.init.onError)(errmsg); // 异常回调
							reject(errmsg);
						}, false);
					});
				}));
			},
		},
		handshake: {
			/**
			 * _value: {
			 *  sk: null, // sessionKey
			 *	sv: null, // sessionValue
			 *	algs: [], // algorithms
			 * }
			 */
			_value: null,
			getValue: function() {
				return Common.Util.checkEmpty("Fatal error, handshake value is null, No attention to call order (must be executed after " +
						"runtime.handshake.getValuePromise())", runtime.handshake._value);
			},
			_currentlyInGettingValuePromise: null, // 仅handshake.getValuePromise使用
			getValuePromise: function(umidToken) {
				// 若当前正在获取handshake._value直接返回该promise对象（解决并发调用）
				if (runtime.handshake._currentlyInGettingValuePromise) {
					return runtime.handshake._currentlyInGettingValuePromise;
				}
				// 若已有值
				if(!Common.Util.isEmpty(runtime.handshake._value)) {
					return new Promise((reslove, reject) => reslove(runtime.handshake._value));
				}
				// 新请求获取handshake._value等(页面加载时调用一次即可)
				return (runtime.handshake._currentlyInGettingValuePromise = new Promise((reslove, reject) => {
					var handshakeParam = new Map();
					handshakeParam.set("{umidTokenKey}", Common.Util.checkEmpty("umidToken", umidToken));
					doIamRequest("post", "{handshakeUri}", handshakeParam, function(res) {
						var codeOkValue = Common.Util.checkEmpty("definition.codeOkValue", settings.definition.codeOkValue);
						if(!Common.Util.isEmpty(res) && (res.code == codeOkValue)){
							runtime.handshake._value = $.extend(true, runtime.handshake._value, res.data);
							reslove(runtime.handshake._value);
						}
					}, function(errmsg){
						console.log("Failed to handshake, " + errmsg);
						Common.Util.checkEmpty("init.onError", settings.init.onError)(errmsg); // 异常回调
					}, false);
				}));
			},
			handleSessionTo: function(param) {
				// 手动提交session(解决跨顶级域名共享cookie失效问题, 如, chrome80+)
				if(!Common.Util.isAnyEmpty(runtime.handshake._value.session.sk, runtime.handshake._value.session.sv)){
					if(Common.Util.isObject(param)){
						param[runtime.handshake._value.session.sk] = runtime.handshake._value.session.sv;
					} else if (Common.Util.isMap(param)) {
						param.set(runtime.handshake._value.session.sk, runtime.handshake._value.session.sv);
					}
				}
			},
			// 提交认证等相关请求时，选择非对称加密算法
			handleChooseSecureAlg: function() {
				var _algs = runtime.handshake.getValue().algs;
				for (index in _algs) {
					var alg = Common.Util.Codec.decodeBase58(_algs[index]);
					if (alg.startsWith(constant.useSecureAlgorithmName)) {
						return _algs[index]; // 提交也使用编码的字符串
					}
				}
				throw Error('No such secure algoritm of: ' + constant.useSecureAlgorithmName);
			},
		},
		safeCheck: { // Safe check result
			checkGeneric: {
				secretKey: null,
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
		clientSecretKey: {}, // Authenticating clientSecretKey info
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
			isCurrentlyApplying: false,
			isVerifying: false,
		}
	};

	// DefaultCaptcha配置实现(JPEG/Gif验证码)
	var _defaultCaptchaVerifier = {
		captchaLen: 5,
		captchaDestroy: function(destroy) {
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
		captchaRender: function() {
			// Sets the current applying verify code.
			runtime.flags.isCurrentlyApplying = false;

			var imgInput = $(Common.Util.checkEmpty("captcha.input", settings.captcha.input));
			var img = Common.Util.checkEmpty("captcha.img", settings.captcha.img);
			imgInput.val(""); // 清空验证码input
			// 绑定刷新验证码
			$(img).click(function(){ resetCaptcha(); });
			// 请求申请Captcha
			doIamRequest("get", getApplyCaptchaUrl(), new Map(), function(res) {
				// Apply captcha completed.
				runtime.flags.isCurrentlyApplying = false;
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
			}, function(req, status, errmsg){
				console.debug("Failed to apply captcha, " + errmsg);
				Common.Util.checkEmpty("captcha.onError", settings.captcha.onError)(errmsg);
			}, true);
		}
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
			clientSecretKey: "clientSecretKey", // 客户端秘钥(公钥)参数名
			verifyTypeKey: "verifyType", // 验证码verifier别名参数名（通用）
			applyTokenKey: "applyToken", // 申请的验证码f返回token参数名（通用）
			verifyDataKey: "verifyData", // 提交验证码参数名（通用：simple/gif/jigsaw）
			verifiedTokenKey: "verifiedToken", // 验证码已校验的凭据token参数名（通用）
			clientRefKey: "client_ref", // 提交登录的客户端类型参数名
			umidTokenKey: "umidToken", // 提交umidToken的参数名
			secureAlgKey: "alg", // 提交secureAlgorithm的参数名
			smsActionKey: "action", // SMS登录action参数名
			smsActionValueLogin: "login", // SMS登录action=login的值
			applyUmTokenUri: "/rcm/applyumtoken", // 页面初始化时请求umidToken的接口URL后缀
			handshakeUri: "/login/handshake", // 页面初始化后请求handshake建立连接的接口URL后缀
			checkUri: "/login/check", // 认证前安全检查接口URL后缀
			captchaApplyUri: "/verify/applycaptcha", // 申请GRAPH验证码URI后缀
			verifyAnalyzeUri: "/verify/verifyanalysis", // 校验分析GRAPH验证码URI后缀
			accountSubmitUri: "/auth/generic", // 账号登录提交的URL后缀
			smsApplyUri: "/verify/applysmsverify", // 申请SMS验证码URI后缀
			smsSubmitUri: "/auth/sms", // SMS登录提交的URL后缀
			snsConnectUri: "/sns/connect/", // 请求连接到社交平台的URL后缀
			codeOkValue: "200" // 接口返回成功码判定标准
		},
		deploy: { // 部署配置
			baseUri: null, // IAM后端服务baseURI
			defaultTwoDomain: "iam", // IAM后端服务部署二级域名，当iamBaseUri为空时，会自动与location.hostnamee拼接一个IAM后端地址.
			defaultContextPath: "/iam-server", // 默认IAM Server的context-path
		},
 		init: { // 初始相关配置(Event)
 			onPostUmidToken: function(res){
 				console.debug("onPostUmidToken... "+ res);
 			},
 			onPreCheck: function(principal, checkUrl){
 				console.debug("onPostCheck... principal:"+ principal +", checkUrl:"+ checkUrl);
 				return true; // continue after?
 			},
 			onPostCheck: function(res){
 				console.debug("onPostCheck... " + res);
 			},
 			onError: function(errmsg){
 				console.error("Failed to initialize... "+ errmsg);
 			}
 		},
		// 图像验证码配置
		captcha: {
			enable: false,
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
			registry: { // 图像验证码实程序注册器
				VerifyWithSimpleGraph: _defaultCaptchaVerifier,
				VerifyWithGifGraph: _defaultCaptchaVerifier,
				VerifyWithJigsawGraph: {  // JigsawCaptcha配置实现
					captchaDestroy: function(destroy) {
						var jigsawPanel = Common.Util.checkEmpty("captcha.panel", settings.captcha.panel);
						if(destroy){
							$(jigsawPanel).css({"display":"none"});
						}
					},
					captchaRender: function() {
						// Set the current application verify code.
						runtime.flags.isCurrentlyApplying = false;
						var jigsawPanel = Common.Util.checkEmpty("captcha.panel", settings.captcha.panel);

						// 加载Jigsaw插件滑块
                        $(jigsawPanel).JigsawIamCaptcha({
                        	// 提交验证码的参数名
                        	verifyDataKey: Common.Util.checkEmpty("definition.verifyDataKey", settings.definition.verifyDataKey),
                            getApplyCaptchaUrl: getApplyCaptchaUrl,
							getVerifyAnalysisUrl: getVerifyAnalysisUrl,
                            repeatIcon: 'fa fa-redo',
                            onSuccess: function (verifiedToken) {
								console.debug("Jigsaw captcha verify successful. verifiedToken is '"+ verifiedToken + "'");
								runtime.flags.isCurrentlyApplying = false; // Apply captcha completed.
								runtime.verifiedModel.verifiedToken = verifiedToken; // [MARK4], See: 'MARK2'
								Common.Util.checkEmpty("captcha.onSuccess", settings.captcha.onSuccess)(verifiedToken);
                            },
							onFail: function(element){
								console.debug("Failed to jigsaw captcha verify. element => "+ element);
								runtime.flags.isCurrentlyApplying = false; // Apply captcha completed.
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
			enable: false,
			submitBtn: null, // 登录提交触发对象
			principalInput: null, // 登录账号input对象
			credentialInput: null, // 登录凭据input对象
			customParamMap: new Map(), // 提交登录附加参数
			onBeforeSubmit: function(principal, credentials, verifiedToken){ // 默认提交之前回调实现
				console.debug("Prepare to submit login request. principal=" + principal + ", verifiedToken=" + verifiedToken);
				return true;
			},
			onSuccess: function(data){ // 登录成功回调
				console.info("Sign in successfully. " + data.principal + ", " + data.redirectUrl);
				return true;
			},
			onError: function(errmsg){ // 登录异常回调
				console.error("Sign in error. " + errmsg);
			}
		},
		// SMS认证配置
		sms: {
			enable: false,
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
			enable: false,
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
	var _initConfigure = function(obj) {
		// 将外部配置深度拷贝到settings，注意：Object.assign(oldObj, newObj)只能浅层拷贝
		settings = $.extend(true, settings, obj);
		console.debug("After merge settings: "+ JSON.stringify(settings));

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
	        if (hostname == 'localhost' || hostname == '127.0.0.1'
	        	|| Common.Util.isIp(hostname) || hostname.endsWith('.debug')
	        	|| hostname.endsWith('.local') || hostname.endsWith('.dev')) {
	        	settings.deploy.baseUri = protocol + "//" + hostname + ":14040" + contextPath;
	        }
	        // 2. 使用域名部署时认为是完全分布式部署，自动生成二级域名，(接口地址如：iam-server.wl4g.com/iam-server, ci-server.wl4g.com/ci-server)每个应用通过二级子域名访问
	        else {
	        	var topDomainName = hostname.split('.').slice(-2).join('.');
	        	if(hostname.indexOf("com.cn") > 0) {
	        		topDomainName = hostname.split('.').slice(-3).join('.');
	        	}
	            settings.deploy.baseUri = protocol + "//" + twoDomain + "." + topDomainName + contextPath;
	        }
	        console.debug("Using overlay iamBaseURI: "+ settings.deploy.baseUri);
	    }

		// Storage iamBaseUri
        window.sessionStorage.setItem(constant.baseUriStoredKey, settings.deploy.baseUri);
	};

	// Gets URL to request a connection to a sns provider
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

	// Gets apply captcha URL.
	var getApplyCaptchaUrl = function() {
		var paramMap = new Map();
		// umidToken参数
		paramMap.set(Common.Util.checkEmpty("definition.umidTokenKey",settings.definition.umidTokenKey), runtime.umid.getValue());
		paramMap.set(Common.Util.checkEmpty("definition.verifyTypeKey",settings.definition.verifyTypeKey), Common.Util.checkEmpty("captcha.use",settings.captcha.use));
		paramMap.set(Common.Util.checkEmpty("definition.responseType",settings.definition.responseType), Common.Util.checkEmpty("definition.responseTypeValue",settings.definition.responseTypeValue));
		paramMap.set(Common.Util.checkEmpty("definition.secureAlgKey",settings.definition.secureAlgKey), runtime.handshake.handleChooseSecureAlg());
		runtime.handshake.handleSessionTo(paramMap);
		paramMap.set("r", Math.random());
		return Common.Util.checkEmpty("checkCaptcha.applyUri",runtime.safeCheck.checkCaptcha.applyUri)+"?"+Common.Util.toUrl({}, paramMap);
	};

	// Gets verify & analyze captcha URL.
	var getVerifyAnalysisUrl = function() {
		var paramMap = new Map();
		// umidToken参数
		paramMap.set(Common.Util.checkEmpty("definition.umidTokenKey",settings.definition.umidTokenKey),runtime.umid.getValue());
		paramMap.set(Common.Util.checkEmpty("definition.verifyTypeKey",settings.definition.verifyTypeKey),Common.Util.checkEmpty("captcha.use", settings.captcha.use));
		//paramMap.set(Common.Util.checkEmpty("definition.verifyTypeKey",settings.definition.verifyTypeKey),Common.Util.checkEmpty("applyModel.verifyType",runtime.applyModel.verifyType));
		paramMap.set(Common.Util.checkEmpty("definition.responseType", settings.definition.responseType),Common.Util.checkEmpty("definition.responseTypeValue",settings.definition.responseTypeValue));
		paramMap.set(Common.Util.checkEmpty("definition.secureAlgKey",settings.definition.secureAlgKey), runtime.handshake.handleChooseSecureAlg());
		paramMap.set("r", Math.random());
		runtime.handshake.handleSessionTo(paramMap);
		return Common.Util.checkEmpty("deploy.baseUri",settings.deploy.baseUri)
				+ Common.Util.checkEmpty("definition.verifyAnalyzeUri", settings.definition.verifyAnalyzeUri)+"?"+Common.Util.toUrl({},paramMap);
	};

	// Reset graph captcha.
	var resetCaptcha = function() {
		_InitSafeCheck(function(checkCaptcha, checkGeneric, checkSms){
			if(checkCaptcha.enabled && !runtime.flags.isCurrentlyApplying){ // 启用验证码且不是申请中(防止并发)?
				// 获取当前配置CaptchaVerifier实例、显示
				Common.Util.checkEmpty("captcha.getVerifier", settings.captcha.getVerifier)().captchaRender();
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
	var _InitSNSAuthenticator = function() {
		// Check authenticator enable?
		if (!settings.sns.enable) {
			console.debug("SNS authenticator not enable!");
			return;
		}

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

	// 请求安全检查
	var _InitSafeCheck = function(callback){
		$(function(){
			var principal = encodeURIComponent(Common.Util.getEleValue("account.principalInput", settings.account.principalInput, false));
			// 初始化前回调
			if(!Common.Util.checkEmpty("init.onPreCheck", settings.init.onPreCheck)(principal)){
				console.warn("Skip the init safeCheck, because onPreCheck() return false");
				return;
			}
			// 请求安全预检
			var checkParam = new Map();
			checkParam.set("{principalKey}", principal);
			checkParam.set("{verifyTypeKey}", Common.Util.checkEmpty("captcha.use", settings.captcha.use));
			checkParam.set("{umidTokenKey}", runtime.umid.getValue());
			checkParam.set("{secureAlgKey}", runtime.handshake.handleChooseSecureAlg());
			doIamRequest("post", "{checkUri}", checkParam, function(res){
				// 初始化完成回调
				Common.Util.checkEmpty("init.onPostCheck", settings.init.onPostCheck)(res);
				var codeOkValue = Common.Util.checkEmpty("definition.codeOkValue", settings.definition.codeOkValue);
				if(!Common.Util.isEmpty(res) && (res.code == codeOkValue)){
					runtime.safeCheck = $.extend(true, runtime.safeCheck, res.data); // [MARK3]
					callback(res.data.checkCaptcha, res.data.checkGeneric, res.data.checkSms);
				}
			}, function(errmsg){
				console.log("Failed to safe check, " + errmsg);
				Common.Util.checkEmpty("init.onError", settings.init.onError)(errmsg); // 登录异常回调
			}, true);
		});
	};

	// Init captcha verifier implement.
	var _InitCaptchaVerifier = function() {
		// Check authenticator enable?
		if (!settings.captcha.enable) {
			console.debug("Captcha verifier not enable!");
			return;
		}

		$(function(){
			// 初始刷新验证码
			resetCaptcha();

			// 初始化&绑定验证码事件
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
							var captchaParam = new Map();
							captchaParam.put("{verifyDataKey}", captcha);
							captchaParam.put("{applyTokenKey}", _check("applyModel.applyToken", runtime.applyModel.applyToken));
							captchaParam.put("{verifyTypeKey}", _check("applyModel.verifyType", runtime.applyModel.verifyType));
							captchaParam.set("{umidTokenKey}", runtime.umid.getValue());
							// 提交验证码
							doIamRequest("post", getVerifyAnalysisUrl(), captchaParam, function(res){
								runtime.flags.isVerifying = false; // Reset verify status.
								var codeOkValue = _check("definition.codeOkValue",settings.definition.codeOkValue);
								if(!Common.Util.isEmpty(res) && (res.code != codeOkValue)){ // Failed?
									resetCaptcha();
									settings.captcha.onError(res.message); // Call after captcha error.
								} else { // Verify success.
									runtime.verifiedModel = res.data.verifiedModel;
									Common.Util.checkEmpty("captcha.getVerifier", settings.captcha.getVerifier)().captchaDestroy(false); // Hide captcha when success.
								}
							}, function(errmsg){
								runtime.flags.isVerifying = false; // Reset verify status.
								resetCaptcha();
								settings.captcha.onError(errmsg); // Call after captcha error.
							}, true);
						}

					}
				});
			};
		});
	};

	// Init Account login implements.
	var _InitAccountAuthenticator = function() {
		// Check authenticator enable?
		if (!settings.account.enable) {
			console.debug("Account authenticator not enable!");
			return;
		}

		$(function(){
			// Init bind key-enter auto submit.
			$(document).bind("keydown",function(event) {
				if(event.keyCode == 13){
					$(Common.Util.checkEmpty("account.submitBtn", settings.account.submitBtn)).click();
				}
			});

			// Bind login btn click.
			$(Common.Util.checkEmpty("account.submitBtn", settings.account.submitBtn)).click(function() {
				var principal = encodeURIComponent(Common.Util.getEleValue("account.principalInput", settings.account.principalInput));
				// 获取明文密码并非对称加密，同时编码(否则base64字符串中有“+”号会自动转空格，导致登录失败)
				var plainPasswd = Common.Util.getEleValue("account.credentialInput", settings.account.credentialInput);
				// Check principal/password.
				if(Common.Util.isAnyEmpty(principal, plainPasswd)){
					settings.account.onError(Common.Util.isZhCN()?"请输入账户名和密码":"Please input your account and password");
					return;
				}

				_InitSafeCheck(function(checkCaptcha, checkGeneric, checkSms){
					// 生成client公钥(用于获取认证成功后加密接口的密钥)
					runtime.clientSecretKey = IAMCrypto.RSA.generateKey();
					// 获取Server公钥(用于提交账号密码)
					var secretKey = Common.Util.checkEmpty("Secret is empty", checkGeneric.secretKey);
					var credentials = encodeURIComponent(IAMCrypto.RSA.encryptToHexString(secretKey, plainPasswd));
					// 已校验的验证码Token(如果有)
					var verifiedToken = "";
					if(runtime.safeCheck.checkCaptcha.enabled){
						verifiedToken = runtime.verifiedModel.verifiedToken; // [MARK2], see: 'MARK1,MARK4'
						if(Common.Util.isEmpty(verifiedToken)){ // Required
							settings.account.onError(Common.Util.isZhCN()?"请完成人机验证":"Please complete man-machine verify");
							return;
						}
					}
					// 检查必须参数
					if(Common.Util.isAnyEmpty(principal, credentials)){
						settings.account.onError("No empty login name or password allowed");
						return;
					}
					// Call before submission login.
					if(!settings.account.onBeforeSubmit(principal, credentials, verifiedToken)){
						return;
					}

					// 锁定登录按钮
					$(Common.Util.checkEmpty("account.submitBtn", settings.account.submitBtn)).attr("disabled", true);
					// 创建登录请求参数
					var loginParam = new Map();
					loginParam.set("{principalKey}", principal);
					//loginParam.set("{principalKey}", Common.Util.Codec.toHex(principal));
					loginParam.set("{credentialKey}", credentials);
					loginParam.set("{clientSecretKey}", runtime.clientSecretKey.publicKeyHex);
					loginParam.set("{clientRefKey}", getClientRef());
					loginParam.set("{verifiedTokenKey}", verifiedToken);
					loginParam.set("{verifyTypeKey}", Common.Util.checkEmpty("captcha.use", settings.captcha.use));
					loginParam.set("{secureAlgKey}", runtime.handshake.handleChooseSecureAlg());
					// 设备指纹umidToken(初始化页面时获取, 必须)
					loginParam.set("{umidTokenKey}", runtime.umid.getValue());
					// 添加自定义参数
					Common.Util.mergeMap(settings.account.customParamMap, loginParam);
					// 请求提交登录
					doIamRequest("post", "{accountSubmitUri}", loginParam, function(res){
						// 解锁登录按钮
						$(Common.Util.checkEmpty("account.submitBtn", settings.account.submitBtn)).removeAttr("disabled");

						runtime.verifiedModel.verifiedToken = ""; // Clear
						var codeOkValue = Common.Util.checkEmpty("definition.codeOkValue",settings.definition.codeOkValue);
						if(!Common.Util.isEmpty(res) && (res.code != codeOkValue)){ // Failed?
							resetCaptcha(); // 刷新验证码
							settings.account.onError(res.message); // 登录失败回调
						} else { // 登录成功，直接重定向
                            $(document).unbind("keydown");
							var redirectUrl = Common.Util.checkEmpty("Login successfully, response data.redirect_url is empty", res.data[settings.definition.redirectUrlKey]);
							if(settings.account.onSuccess(res.data)){
								Common.Util.getRootWindow(window).location.href = redirectUrl;
							}
						}
					}, function(errmsg){
						// 失败时也要解锁登录按钮
						$(Common.Util.checkEmpty("account.submitBtn", settings.account.submitBtn)).removeAttr("disabled");
						runtime.verifiedModel.verifiedToken = ""; // Clear
						settings.account.onError(errmsg); // 登录异常回调
					}, true);
				});
			});
		});
	};

	// Init SMS authentication implements.
	var _InitSMSAuthenticator = function(){
		// Check authenticator enable?
		if (!settings.sms.enable) {
			console.debug("SMS authenticator not enable!");
			return;
		}

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

				// 请求申请SMS验证码
				var getSmsParam = new Map();
				getSmsParam.set("{principalKey}", encodeURIComponent(mobileNum));
				getSmsParam.set("{verifiedTokenKey}", captcha);
				doIamRequest("post", "{smsApplyUri}", getSmsParam, function(res){
					var codeOkValue = Common.Util.checkEmpty("definition.codeOkValue",settings.definition.codeOkValue);
					// 登录失败
					if(!Common.Util.isEmpty(res) && (res.code != codeOkValue)){
						settings.sms.onError(res.message); // 申请失败回调
					} else {
						settings.sms.onSuccess(res); // 申请成功回调
						var remainDelaySec = res.data.checkSms.remainDelayMs/1000;
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
				}, function(errmsg){
					settings.sms.onError(errmsg); // 申请失败回调
				}, true);
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

				var smsLoginParam = new Map();
				smsLoginParam.set("{principalKey}", encodeURIComponent(mobileNum));
				smsLoginParam.set("{credentialKey}", smsCode);
				smsLoginParam.set("{smsActionKey}", Common.Util.checkEmpty("definition.smsActionValueLogin", settings.definition.smsActionValueLogin));
				doIamRequest("post", "{smsSubmitUri}", smsLoginParam, function(res){
					var codeOkValue = Common.Util.checkEmpty("definition.codeOkValue",settings.definition.codeOkValue);
					if(!Common.Util.isEmpty(res) && (res.code != codeOkValue)){
						settings.sms.onError(res.message); // SMS登录失败回调
					} else {
						settings.sms.onSuccess(res); // SMS登录成功回调
						Common.Util.getRootWindow(window).location.href = res.data.redirect_url;
					}
				}, function(errmsg){
					settings.sms.onError(errmsg); // SMS登录失败回调
				}, true);
			});

			// 上次申请过SMS验证码?刷新页面之后倒计时继续
			if(runtime.safeCheck.checkSms.enabled) {
				// 填充mobile number.
				$(settings.sms.mobile).val(runtime.safeCheck.checkSms.mobileNum);
				// 继续倒计时
				var remainDelaySec = runtime.safeCheck.checkSms.remainDelayMs/1000;
				var num = parseInt(remainDelaySec);
				var timer = setInterval(() => {
					var sendSmsBtn = $(settings.sms.sendSmsBtn);
					if (num < 1) {
						sendSmsBtn.attr('disabled', false);
						var getBtnText = "新获取验证码";
						if(!Common.Util.isZhCN()){
							getBtnText = "Get verify code";
						}
						sendSmsBtn.text(getBtnText);
						clearInterval(timer);
					} else {
						sendSmsBtn.attr('disabled', true);
						sendSmsBtn.text(num + 's');
						num--;
					}
				}, 1000);
			}
		});
	};

	// Client device OS type.
	var getClientRef = function(){
		var clientRef = null;
		var osTypes = Common.Util.PlatformType;
		for(var osname in osTypes){
		    if(osTypes[osname]){
		        console.debug("Got current OS: "+ osname);
		        clientRef = osname;
		        break;
		    }
		}
		if(Common.Util.isEmpty(clientRef)) {
			clientRef = "Unknown";
			console.warn("Unknown platform browser ["+ navigator.appVersion +"]");
		}
		return clientRef;
	};

	// 提交基于IAM特征的请求(如，设置跨域允许cookie,表单,post等)
	var doIamRequest = function(method, urlAndKey, paramMap, success, error, sessionIfNecessary) {
		// 默认公共参数
		paramMap.set("{responseType}", Common.Util.checkEmpty("definition.responseTypeValue", settings.definition.responseTypeValue));
		// 手动提交session(若有必要)
		if (sessionIfNecessary) {
			runtime.handshake.handleSessionTo(paramMap);
		}
		// 拼接生成请求URL
		var _url = Common.Util.checkEmpty("deploy.baseUri", settings.deploy.baseUri);
		if(urlAndKey.startsWith("{") && urlAndKey.endsWith("}")) { // Build API URI
			var realKey = urlAndKey.substr(1, urlAndKey.length - 2);
			_url += Common.Util.checkEmpty("definition", settings.definition[realKey]);
		} else {
			_url += Common.Util.checkEmpty("definition", settings.definition[urlAndKey]);
		}
		$.ajax({
			url: _url,
			type: method,
			//headers: { ContentType: "" },
			data: Common.Util.toUrl(settings.definition, paramMap),
			xhrFields: { withCredentials: true }, // Send cookies when support cross-domain request.
			success: function(res, textStatus, jqxhr){
				success(res);
			},
			error: function(req, status, errmsg){
				error(errmsg);
			}
		});
	};

	// Exposing core APIs
	window.IAMCore = function(){};
	IAMCore.prototype.init = function(opt) {
		// 初始化配置
		_initConfigure(opt);
		// 初始化获取设备umidToken
        runtime.umid.getValuePromise()
        	.then(umidToken => runtime.handshake.getValuePromise(umidToken))
        	.then(handshakeValue => { // 为确保执行顺序（1，获取umidToken；2，请求handshake；3，初始绑定各种认证器）
    			_InitAccountAuthenticator();
        		_InitSMSAuthenticator();
        		_InitSNSAuthenticator();
        		_InitCaptchaVerifier();
        	});
        return this;
	};
	IAMCore.prototype.getIamBaseUri = function() {
        return window.sessionStorage.getItem(constant.baseUriStoredKey);
    };
	IAMCore.prototype.getUMToken = function() {
		return runtim.umid.getValue();
	};

})(window, document);
