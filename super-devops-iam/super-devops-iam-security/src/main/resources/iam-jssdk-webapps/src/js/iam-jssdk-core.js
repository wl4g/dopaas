/**
 * IAM WebSDK CORE v2.0.0 | (c) 2017 ~ 2050 wl4g Foundation, Inc.
 * Copyright 2017-2032 <wangsir@gmail.com, 983708408@qq.com>, Inc. x
 * Licensed under Apache2.0 (https://github.com/wl4g/super-devops/blob/master/LICENSE)
 */
(function(window, document) {
	'use strict';

	// Base constants definition.
    var constant = {
		iamVerboseStoredKey : '__IAM_VERBOSE',
        baseUriStoredKey : '__IAM_BASEURI',
        umidTokenStorageKey : '__IAM_UMIDTOKEN',
        authRedirectRecordStorageKey : '__IAM_AUTHC_REDIRECT_RECORD',
        useSecureAlgorithmName: 'RSA', // 提交认证相关请求时，选择的非对称加密算法（ 默认：RSA）
    };

	// 运行时状态值/全局变量/临时缓存
	var runtime = {
		__that: null,
		umid: {
			_value: null, // umidToken
			getValue: function() {
				return Common.Util.checkEmpty("Fatal error, umidToken value is null, No attention to call order (must be executed after " +
						"runtime.umid.getValuePromise())", runtime.umid._value);
			},
			_currentlyInGettingValuePromise: null, // 仅umid.getValuePromise使用
			getValuePromise: function () {
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
					IamFingerprint.getFingerprint({}, function(fpObj){
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
						_iamConsole.debug("Generated apply umidToken data: "+ umdata);
						umidParam.set("umdata", umdata);
						_doIamRequest("post", "{applyUmTokenUri}", umidParam, function(res){
							Common.Util.checkEmpty("init.onPostUmidToken", settings.init.onPostUmidToken)(res); // 获得umtoken完成回调
							var codeOkValue = Common.Util.checkEmpty("definition.codeOkValue",settings.definition.codeOkValue);
							if(!Common.Util.isEmpty(res) && (res.code == codeOkValue)){
								_iamConsole.debug("Got umidToken: " + res.data.umidToken);
								// Encoding umidToken
								var encodeUmidToken = Common.Util.Codec.encodeBase58(res.data.umidToken);
								sessionStorage.setItem(constant.umidTokenStorageKey, encodeUmidToken);
								// Completed
								reslove(res.data.umidToken);
								runtime.umid._value = res.data.umidToken;
							}
							runtime.umid._currentlyInGettingValuePromise = null;
						}, function(errmsg) {
							runtime.umid._currentlyInGettingValuePromise = null;
							console.warn("Failed to gets umidToken, " + errmsg);
							Common.Util.checkEmpty("init.onError", settings.init.onError)(errmsg); // 异常回调
							reject(errmsg);
						}, null, false);
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
			getValuePromise: function (umidToken, refresh) {
				if (!refresh) {
					// 若当前正在获取handshake._value直接返回该promise对象（解决并发调用）
					if (runtime.handshake._currentlyInGettingValuePromise) {
						return runtime.handshake._currentlyInGettingValuePromise;
					}
					// 若已有值
					if(!Common.Util.isEmpty(runtime.handshake._value)) {
						return new Promise((reslove, reject) => reslove(runtime.handshake._value));
					}
				}
				// 新请求获取handshake._value等(页面加载时调用一次即可)
				return (runtime.handshake._currentlyInGettingValuePromise = new Promise((reslove, reject) => {
					var handshakeParam = new Map();
					handshakeParam.set("{umidTokenKey}", Common.Util.checkEmpty("umidToken", umidToken));
					_doIamRequest("post", "{handshakeUri}", handshakeParam, function(res) {
						Common.Util.checkEmpty("init.onPostHandshake", settings.init.onPostHandshake)(res); // handshake完成回调
						var codeOkValue = Common.Util.checkEmpty("definition.codeOkValue", settings.definition.codeOkValue);
						if(!Common.Util.isEmpty(res) && (res.code == codeOkValue)){
							runtime.handshake._value = $.extend(true, runtime.handshake._value, res.data);
							reslove(res);
						}
						runtime.handshake._currentlyInGettingValuePromise = null;
					}, function(errmsg) {
						runtime.handshake._currentlyInGettingValuePromise = null;
						_iamConsole.log("Failed to handshake, " + errmsg);
						Common.Util.checkEmpty("init.onError", settings.init.onError)(errmsg); // 异常回调
					}, null, false);
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
				for (var index in _algs) {
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
		},
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
			$(img).click(function(){ _resetCaptcha(true); });
			// 请求申请Captcha
			_doIamRequest("get", _getApplyCaptchaUrl(), new Map(), function(res) {
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
						$(img).click(function(){ _resetCaptcha(true); });
					}, 15000); // 至少15sec才能点击刷新
				}
			}, function(req, status, errmsg){
				_iamConsole.error("Failed to apply captcha, " + errmsg);
				Common.Util.checkEmpty("captcha.onError", settings.captcha.onError)(errmsg);
			}, null, true);
		}
	};

	// Global settings.
	var settings = {
		// 基础资源定义
		resources: {
			loading: "data:image/gif;base64,R0lGODlhHgAeAPf/AGzI8iqM4ev4/YnU9YrR9HLK8le67+f2/Knf94zU9ZLV9nbK8vr9/uP1/fL7/k+57mTC8TCm6TOp6t/0/WG+8GPE8FO98HvH8rbj+LLi+JzX9mjE8czq+k2679Pu+yqI4Tar637L8z+y7fT7/s7s+8zt+zCg53jM83HJ8vf7/uv3/d7z/M3s+i+g6MLn+bTj+KPe95HV9Uy57j2x7dHu+4HP9DCm6S2b5iyV5SyW5S2Y5SyW5C6c5i2Z5i2X5S6e5zGn6S6f5yyV5C2Z5S2a5iyS4/b8/iyU5C6d5y+l6S+k6IPO8y6d5vT7/lW98GrH8S+j6C+i6C+h6P7///z+/yuQ4/X7/v3+/2zG8UK07cXq+i6e5vL6/drx/CuR45DW9dTw/O75/fH6/YjS9fn9/l7A77bj+Tiu62zD8dfx/IbP9C+k6XfN81zA7yyT5Nbw/EK27tv0/b/m+e/5/p/b93zK8kCy7C2b5TKn6r7n+fb9/vz//4XQ9GfG8I7T9XbL85XX9svr+km07cbp+tvx/MDo+oDQ9E257qXd+Pf9/t7z/XjK8pPX9tfw/D6s673m+SyT43/P9LPl+X3L8zKn6S2a5X/O84HP8+Dz/YHO9Jva9yuP4k237lu/7zCj6fz9/jer6srr+rfj+a3f963h97/n+WnF8Fa88NXu+2/J8vn9/4jS9F/C8JLV9ZLW9VzB70257qLd+HXK8jGn6pzZ9t3y/ILP9O34/S2a5aDe+GLB777m+XHI8fX8/4vS9afc9y6d53XL82/H8oHN8/v+/37J8v3//1a98JLX9lq/72fA74XP84XR9IfR9J3a9/D5/dfv/F2/8VK779vy/C6c5rzl+bvm+XbI8pvc9l7B8C+h6C6e6Eaw7Ea17ZbZ9zOd5pfX9UOy7L7n+i6a5S2Y5ZTV9p/a9y6h5/T8/tDu+8fp+uD0/c/u+57b99jx/L3o+SyS5JDU9ZvY9tLy/LXi9zeu633I8vf+/7Dh90Cv66Hd95zZ95za9i6f6P///////yH/C05FVFNDQVBFMi4wAwEAAAAh/wtYTVAgRGF0YVhNUDw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDE0IDc5LjE1MTQ4MSwgMjAxMy8wMy8xMy0xMjowOToxNSAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIChNYWNpbnRvc2gpIiB4bXBNTTpJbnN0YW5jZUlEPSJ4bXAuaWlkOkIyQjlEMzRFOEY2QzExRTU5MzVCODg0NzA4NjRDMDNCIiB4bXBNTTpEb2N1bWVudElEPSJ4bXAuZGlkOkIyQjlEMzRGOEY2QzExRTU5MzVCODg0NzA4NjRDMDNCIj4gPHhtcE1NOkRlcml2ZWRGcm9tIHN0UmVmOmluc3RhbmNlSUQ9InhtcC5paWQ6QjJCOUQzNEM4RjZDMTFFNTkzNUI4ODQ3MDg2NEMwM0IiIHN0UmVmOmRvY3VtZW50SUQ9InhtcC5kaWQ6QjJCOUQzNEQ4RjZDMTFFNTkzNUI4ODQ3MDg2NEMwM0IiLz4gPC9yZGY6RGVzY3JpcHRpb24+IDwvcmRmOlJERj4gPC94OnhtcG1ldGE+IDw/eHBhY2tldCBlbmQ9InIiPz4B//79/Pv6+fj39vX08/Lx8O/u7ezr6uno5+bl5OPi4eDf3t3c29rZ2NfW1dTT0tHQz87NzMvKycjHxsXEw8LBwL++vby7urm4t7a1tLOysbCvrq2sq6qpqKempaSjoqGgn56dnJuamZiXlpWUk5KRkI+OjYyLiomIh4aFhIOCgYB/fn18e3p5eHd2dXRzcnFwb25tbGtqaWhnZmVkY2JhYF9eXVxbWllYV1ZVVFNSUVBPTk1MS0pJSEdGRURDQkFAPz49PDs6OTg3NjU0MzIxMC8uLSwrKikoJyYlJCMiISAfHh0cGxoZGBcWFRQTEhEQDw4NDAsKCQgHBgUEAwIBAAAh+QQFAAD/ACwAAAAAHgAeAEAITwD/CRxIsKDBgwL9KVzIsKHDhxAHfIFIsaLFixgzWowRpJbGjyBDihxJsqRJhw/GnFzJsqXLlzBjygQ5TUxLaTNcYkngUguYmUCDCh3aMiAAIfkEBQAA/wAsBgAIAAYABgBACCYA//0zQgHcP0NH4ggUCGBTPnNX/nHJ8iEJmTRHBN0TeArOwn8BAQAh+QQFAAD/ACwJAAcABgAFAEAIIgD/DdJgS1mgRP8SJqxCTokNcnTQ3CiC4wMyO37y0GBxKyAAIfkEBQAA/wAsDAAHAAcABQBACCQA+WwoUyfQv4MIm3k7iCcJtSBuUPzz0WPHFjYHuxR6QuRGu4AAIfkEBQAA/wAsEAAJAAUABgBACCEAf5D786+gB2BD5NEKQgSElSDAbiAq2IfIkRgFS8z7FxAAIfkEBQAA/wAsEAALAAkABgBACCwA/wn8x2DgPzFyrBgcqCgIDihrosjoJbAHOTpzHAx8Y2GBmiUiKjAgs3BgQAAh+QQFAAD/ACwWAA0ABgAHAEAIKgCTcHK155/Bf7Y2eXH1T8GPIgUOGin4r4MbL7D0HHRCzkYXg6u4xVMUEAAh+QQFAAD/ACwWABAABgAIAEAIMQD/CfTxD9O/F1G0MRkl8F8Pck+e6RJyx0zDMT6AARL46sYQWSyo9WNyponAa7kEBgQAIfkEBQAA/wAsEQAUAAkABQBACDAA/wkcKErglH0hatSY1EqAwALUZim5EYUak38HWDR6lMqTNmpTRATAkWOID1CSAgIAIfkEBQAA/wAsDQAVAAcABABACB8AqXhgR6MgjUUBduTAgWOHNRM2gES0sSvEpRoYawQEACH5BAUAAP8ALAgAFQAIAAQAQAgiAI25Y0ejYMF1N6rsyJFDiBAH9bTZmGgDiABmIWporPEvIAAh+QQFAAD/ACwEABUABwAEAEAIIQCrGcBAiIZBXkJwCNmRI8eGKlAo2bABJAWCJZZq1MgUEAAh+QQFAAD/ACwBABIABQAHAEAIJABVLHj3r2CsHz1w+RpCbVHBgp/+CQvQoWAYCURuOClIytGdgAAh+QQFAAD/ACwBAA0ABgAIAEAIKQD//aNy4N8xUz/WCRQIq81CCJAsLPxnpAIGgRluhFrYiVyphYxuTAwIACH5BAUAAP8ALAMACgAGAAgAQAghAP0J9JcsAKtITQYKpDJFYCiFEINFU6UJ1RWBbSCugxgQACH5BAUAAP8ALAcABwAGAAUAQAgbAP/9Y+BCoL+DAv8dnFKAiMED5wIoPJiCXrqAACH5BAUAAP8ALAkABwAJAAUAQAgeAP0JHOivAUGC/IIcrMUEx5CD/gB9Q3FwQoUfwAICACH5BAUAAP8ALA4ABwAHAAgAQAgfAP0JHEhQIIwDBQuq2OJFScKHBBmUKwJuYIkOC9QEBAAh+QQFAAD/ACwQAAsACgAGAEAIHQD9CRxIsKBAX9uKGPTnggCxhRAF2vkgA6ITHwEBACH5BAUAAP8ALBcADQAFAAgAQAgaAP0JHEiwoL9swHCdMFjwFgsOK0RpAzbkV0AAIfkEBQAA/wAsFAASAAgABwBACB4A/QkcKLBFOHQEExKsMUwYIYUKOQhQuCNAtzAEAwIAIfkEBQAA/wAsDwAVAAkABABACBkASby54q+gwQASqBg02GJGk4UFizmDWDAgACH5BAUAAP8ALAsAFQAHAAQAQAgVAAM18EeQ4IdkBQmOQ5HQn70MDQMCACH5BAUAAP8ALAcAFQAGAAQAQAgUAKE18EfQ34doBf2ZgJDwgriEAQEAIfkEBQAA/wAsAgAUAAcABQBACBgAb9xY4K+gwYP+9O1YdlACvAcIseE7GBAAIfkEBQAA/wAsAAAQAAUACABACBgA/cUqos6fwYNTmhxc6G/KiWRWGEpkGBAAIfkEBQAA/wAsAAANAAYABgBACBEA/Qn058HDwIMIDzZokjBhQAA7",
		},
		// 字典参数定义
		definition: {
			codeOkValue: "200", // 接口返回成功码判定标准
			code401Value: "401", // 接口返回未认证状态码判定标准
			statusUnauthenticatedValue: "Unauthenticated", // 接口返回未认证状态判定标准
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
			applyXsrfTokenUrlKey: "/xsrf/xtoken", // 申请xsrfToken接口地址
			// Due to the cross domain limitation of set cookie, it can only be set as the top-level domain name,
			// so the cookie name of xsrf for each sub service (sub domain name) is different.
			//xsrfTokenCookieKey: "IAM-XSRF-TOKEN", // xsrfToken保存的cookie名(@Deprecated), used: IAM-{service}-XSRF-TOKEN  @see: #MARK55
			xsrfTokenHeaderKey: "X-Iam-Xsrf-Token", // xsrfToken保存的header名
			xsrfTokenParamKey: "_xsrf", // xsrfToken保存的Param名
			replayTokenHeaderKey: "X-Iam-Replay-Token", // 重放攻击replayToken保存的header名
			replayTokenParamKey: "_replayToken", // 重放攻击replayToken保存的Param名
		},
		// 部署配置
		deploy: {
			baseUri: null, // IAM后端服务baseURI
			defaultTwoDomain: "iam", // IAM后端服务部署二级域名，当iamBaseUri为空时，会自动与location.hostnamee拼接一个IAM后端地址.
			defaultServerPort: 14040, // 默认IAM Server的port
			defaultContextPath: "/iam-server", // 默认IAM Server的context-path
		},
		// 初始相关配置(Event)
 		init: {
 			onPostUmidToken: function(res){
 				_iamConsole.debug("onPostUmidToken... "+ res);
 			},
 			onPostHandshake: function(res){
 				_iamConsole.debug("onPostHandshake... ", res);
 			},
 			onPreCheck: function(principal){
 				_iamConsole.debug("onPreCheck... principal:"+ principal);
 				return true; // continue after?
 			},
 			onPostCheck: function(res){
 				_iamConsole.debug("onPostCheck... " + res);
 			},
 			onError: function(errmsg){
 				console.error("Failed to initialize... "+ errmsg);
 			}
 		},
		// 验证码配置
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
                            getApplyCaptchaUrl: _getApplyCaptchaUrl,
							getVerifyAnalysisUrl: _getVerifyAnalysisUrl,
                            repeatIcon: 'fa fa-redo',
                            onSuccess: function (verifiedToken) {
                            	_iamConsole.debug("Jigsaw captcha verify successful. verifiedToken is '"+ verifiedToken + "'");
								runtime.flags.isCurrentlyApplying = false; // Apply captcha completed.
								runtime.verifiedModel.verifiedToken = verifiedToken; // [MARK4], See: 'MARK2'
								Common.Util.checkEmpty("captcha.onSuccess", settings.captcha.onSuccess)(verifiedToken);
                            },
							onFail: function(element){
								_iamConsole.debug("Failed to jigsaw captcha verify. element => "+ element);
								runtime.flags.isCurrentlyApplying = false; // Apply captcha completed.
								runtime.verifiedModel.verifiedToken = ""; // Clear
								Common.Util.checkEmpty("captcha.onError", settings.captcha.onError)(element);
							}
                        });
					},
				},
			},
			onSuccess: function(verifiedToken) {
				_iamConsole.debug("Jigsaw captcha verify successfully. verifiedToken is '"+ verifiedToken+"'");
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
				_iamConsole.debug("Prepare to submit login request. principal=" + principal + ", verifiedToken=" + verifiedToken);
				return true;
			},
			onSuccess: function(principal, data){ // 登录成功回调
				_iamConsole.info("Sign in successfully. " + data.principal + ", " + data.redirectUrl);
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
				_iamConsole.log("Prepare to submit SMS login request. mobileNum=" + mobileNum + ", smsCode=" + smsCode);
				return true;
			},
			onSuccess: function(resp){
				_iamConsole.log("SMS success. " + resp.message);
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

	// --- [Start Helper function's. ---

	// IAM console.
	var _iamConsole = {
		// Check verbose enabled. (output run details.)
		_isVerbose: function () {
			var verbose = sessionStorage.getItem(constant.iamVerboseStoredKey);
			if (verbose) {
				verbose = verbose.toUpperCase();
				return verbose == 'TRUE' || verbose == '1' || verbose == 'Y' || verbose == 'YES';
			}
			return false;
		},
		_doLog: function (level, msgArgs) {
			if (_iamConsole._isVerbose()) {
				var prefix = new Date().format("[yyyy-MM-dd hh:mm:ss.S]") + " " + level + " --- ";
				var args = [];
				args.push(prefix);
				for(var i in msgArgs) {
					args.push(msgArgs[i]);
				}
				switch (level) {
				case "TRACE":
					console.trace.apply(console, args);
					break;
				case "DEBUG":
					console.debug.apply(console, args);
					break;
				case "INFO":
					console.info.apply(console, args);
					break;
				case "WARN":
					console.warn.apply(console, args);
					break;
				case "ERROR":
					console.error.apply(console, args);
					break;
				default:
					console.log.apply(console, args);
					break;
				}
			}
		},
		trace: function () {
			_iamConsole._doLog("TRACE", arguments);
		},
		debug: function () {
			_iamConsole._doLog("DEBUG", arguments);
		},
		info: function () {
			_iamConsole._doLog("INFO", arguments);
		},
		warn: function () {
			_iamConsole._doLog("WARN", arguments);
		},
		error: function () {
			_iamConsole._doLog("ERROR", arguments);
		},
		log: function () {
			_iamConsole._doLog("INFO", arguments);
		},
	};

	// Check is response is Unauthenticated?
	var _isRespUnauthenticated = function (res) {
		if (res) {
			var isCode401 = res.code && (res.code == settings.definition.code401Value || (res.code + '') == settings.definition.code401Value);
			var isStatusUnauthenticated = res.status && (res.status == settings.definition.statusUnauthenticatedValue);
			return isCode401 || isStatusUnauthenticated;
		}
		return false;
	};

	// Check is response is successful?
	var _isRespSuccess = function (res) {
		if (res.code == settings.definition.code401Value 
				|| (res.code + '') == settings.definition.code401Value) {
			return true;
		}
		return false;
	};

	// Gets Xsrf token.
	var _getXsrfToken = function(/*xsrfTokenCookieName, */ callback) {
		var xsrfTokenHeaderName = Common.Util.checkEmpty("definition.xsrfTokenHeaderKey", settings.definition.xsrfTokenHeaderKey);
		var xsrfTokenParamName = Common.Util.checkEmpty("definition.xsrfTokenParamKey", settings.definition.xsrfTokenParamKey);

		// Return out xsrfToken
		var _outXsrfToken = function(xsrfTokenHeaderName, xsrfTokenParamName, xsrfTokenValue) {
			var _xsrfToken = {
				headerName: xsrfTokenHeaderName,
				paramName: xsrfTokenParamName,
				value: xsrfTokenValue
			};
			_iamConsole.debug("Got xsrfToken:", _xsrfToken);
			return _xsrfToken;
		};

		// [MARK55]
		var host = location.hostname;
		var topDomain = Common.Util.extTopDomainString(host);
		var defaultServiceName = host;
		var index = host.indexOf(topDomain);
		if (index > 0) {
			defaultServiceName = host.substring(0, index - 1);
		}
		defaultServiceName = defaultServiceName.replace(".", "_").toUpperCase();
		var _xsrfTokenCookieName = "IAM-" + defaultServiceName + "-XSRF-TOKEN";
		// _xsrfTokenCookieName = xsrfTokenCookieName ? xsrfTokenCookieName : _xsrfTokenCookieName;

		// Gets xsrf from cookie.
		var xsrfTokenValue = Common.Util.getCookie(_xsrfTokenCookieName, null);
		_iamConsole.debug("Loaded cache xsrfTokenValue:", xsrfTokenValue, "by cookieName:", _xsrfTokenCookieName);

		var _sync = !callback; // Synchronous XMLHttpRequest?
		// First visit? init xsrf token
		if (!xsrfTokenValue) {
			_iamConsole.debug("Loading new xsrf token...");
			var applyXsrfTokenUrl = IAMCore.getIamBaseUri() + Common.Util.checkEmpty("definition.applyXsrfTokenUrlKey", settings.definition.applyXsrfTokenUrlKey);
			Common.Util.Http.request({
				url: applyXsrfTokenUrl,
				type: 'HEAD',
				async: !_sync, // Note: Jquery1.8 has deprecated, @see https://api.jquery.com/jQuery.ajax/#jQuery-ajax-settings
				xhrFields: { withCredentials: true }, // Send cookies when support cross-domain request.
				success: function(data, textStatus, xhr){
					xsrfTokenValue = Common.Util.getCookie(_xsrfTokenCookieName);
					_iamConsole.info("Loaded new xsrfTokenValue:", xsrfTokenValue, "by cookieName:", _xsrfTokenCookieName);
					if (!_sync) {
						callback(_outXsrfToken(xsrfTokenHeaderName, xsrfTokenParamName, xsrfTokenValue));
					}
				},
				error: function(xhr, textStatus, errmsg){
					_iamConsole.error("Failed to init xsrf token. " + errmsg);
				}
			});
		}

		if (_sync) {
			return _outXsrfToken(xsrfTokenHeaderName, xsrfTokenParamName, Common.Util.getCookie(_xsrfTokenCookieName));
		}
	};

	// Gets Replay token.
	var _generateReplayToken = function() {
		var timestamp = new Date().getTime();
		var nonce = "";
		for (var i=0; i<2; i++) {
			nonce += Math.random().toString(36).substr(2);
		}
		// Signature replay token.
		var replayTokenPlain = Common.Util.sortWithAscii(nonce + timestamp); // Ascii sort
		// Gets crc16
		var replayTokenPlainCrc16 = Common.Util.Crc16CheckSum.crc16Modbus(replayTokenPlain);
		// Gets iters
		var iters = parseInt(replayTokenPlainCrc16 % replayTokenPlain.length / Math.PI) + 1;
		// Gets signature
		var signature = replayTokenPlain;
		for (var i=0; i<iters; i++) {
			signature = CryptoJS.MD5(signature).toString(CryptoJS.enc.Hex);
		}
		var replayTokenPlain = JSON.stringify({
			"n": nonce, // nonce
			"t": timestamp, // timestamp
			"s": signature // signature
		});
		// Encode replay token
		var replayTokenHeaderName = Common.Util.checkEmpty("definition.replayTokenHeaderKey", settings.definition.replayTokenHeaderKey);
		var replayTokenParamName = Common.Util.checkEmpty("definition.replayTokenParamKey", settings.definition.replayTokenParamKey);
		var replayToken = Common.Util.Codec.encodeBase58(replayTokenPlain);
		_iamConsole.debug("Generated replay token(plain): ", replayTokenPlain, " - ", replayToken);
		return {
			headerName: replayTokenHeaderName,
			paramName: replayTokenParamName,
			value: replayToken
		};
	};

	// --- Helper function's. End] ---

	// Gets default IAM baseUri
	var _getIamBaseUri = function() {
		// 获取地址栏默认baseUri
		var protocol = location.protocol;
		var hostname = location.hostname;
		var servPort = settings.deploy.defaultServerPort;
		var twoDomain = settings.deploy.defaultTwoDomain;
		var contextPath = settings.deploy.defaultContextPath;
		contextPath = contextPath.startsWith("/") ? contextPath : ("/" + contextPath);

		// 为了可以自动配置IAM后端接口基础地址，下列按照不同的部署情况自动获取iamBaseURi。
	 	// 1. 以下情况会认为是非完全分布式部署，随地址栏走，即认为所有服务(接口地址如：10.0.0.12:14040/iam-server, 10.0.0.12:14046/ci-server)都部署于同一台机。
	 	// a，当访问的地址是IP；
	 	// b，当访问域名的后者是.debug/.local/.dev等。
		if (Common.Util.isIp(hostname)
        	|| hostname == 'localhost'
        	|| hostname == '127.0.0.1'
        	|| hostname.endsWith('.debug')
        	|| hostname.endsWith('.local')
        	|| hostname.endsWith('.dev')) {
        	return protocol + "//" + hostname + ":" + servPort + contextPath;
        }
        // 2. 使用域名部署时认为是完全分布式部署，自动生成二级域名，
		// (接口地址如：iam-server.wl4g.com/iam-server, ci-server.wl4g.com/ci-server)每个应用通过二级子域名访问
        else {
        	var topDomainName = Common.Util.extTopDomainString(hostname);
        	return protocol + "//" + twoDomain + "." + topDomainName + contextPath;
        }
	};

	// Configure settings
	var _initConfigure = function(obj) {
		// 将外部配置深度拷贝到settings，注意：Object.assign(oldObj, newObj)只能浅层拷贝
		settings = $.extend(true, settings, obj);
		_iamConsole.debug("Merged iam core settings: ", settings);

		//if (Common.Util.isEmpty(settings.deploy.baseUri)) {
        settings.deploy.baseUri = _getIamBaseUri();
        _iamConsole.info("Use overlay iam baseUri: ", settings.deploy.baseUri);
	    //}

		// Storage iamBaseUri
        sessionStorage.setItem(constant.baseUriStoredKey, settings.deploy.baseUri);
	};

	// Gets URL to request a connection to a sns provider
	var _getSnsConnectUrl = function(provider, panelType){
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
	var _getApplyCaptchaUrl = function() {
		var paramMap = new Map();
		// principal参数（申请验证码接口会检查是否启用,因为factors有包括rip/principal等,所有只要principal输入框有值就传,如：同一网段内多次登录root失败，此时该网段另一客户端登录root时也应该要启用验证码）
		var principal = encodeURIComponent(Common.Util.getEleValue("account.principalInput", settings.account.principalInput));
		paramMap.set(Common.Util.checkEmpty("definition.principalKey",settings.definition.principalKey), principal);
		// umidToken参数
		paramMap.set(Common.Util.checkEmpty("definition.umidTokenKey",settings.definition.umidTokenKey), runtime.umid.getValue());
		paramMap.set(Common.Util.checkEmpty("definition.verifyTypeKey",settings.definition.verifyTypeKey), Common.Util.checkEmpty("captcha.use",settings.captcha.use));
		paramMap.set(Common.Util.checkEmpty("definition.responseType",settings.definition.responseType), Common.Util.checkEmpty("definition.responseTypeValue",settings.definition.responseTypeValue));
		paramMap.set(Common.Util.checkEmpty("definition.secureAlgKey",settings.definition.secureAlgKey), runtime.handshake.handleChooseSecureAlg());
		// XSRF token
		var xsrfToken = _getXsrfToken();
		paramMap.set(xsrfToken.paramName, xsrfToken.value);
		// Replay token
		var replayToken = _generateReplayToken();
		paramMap.set(replayToken.paramName, replayToken.value);
		// Session info
		runtime.handshake.handleSessionTo(paramMap);
		paramMap.set("r", Math.random());
		return Common.Util.checkEmpty("checkCaptcha.applyUri",runtime.safeCheck.checkCaptcha.applyUri)+"?"+Common.Util.toUrl({}, paramMap);
	};

	// Gets verify & analyze captcha URL.
	var _getVerifyAnalysisUrl = function() {
		var paramMap = new Map();
		// principal参数（申请验证码接口会检查是否启用,因为factors有包括rip/principal等,所有只要principal输入框有值就传,如：同一网段内多次登录root失败，此时该网段另一客户端登录root时也应该要启用验证码）
		var principal = encodeURIComponent(Common.Util.getEleValue("account.principalInput", settings.account.principalInput));
		paramMap.set(Common.Util.checkEmpty("definition.principalKey",settings.definition.principalKey), principal);
		// umidToken参数
		paramMap.set(Common.Util.checkEmpty("definition.umidTokenKey",settings.definition.umidTokenKey),runtime.umid.getValue());
		paramMap.set(Common.Util.checkEmpty("definition.verifyTypeKey",settings.definition.verifyTypeKey),Common.Util.checkEmpty("captcha.use", settings.captcha.use));
		//paramMap.set(Common.Util.checkEmpty("definition.verifyTypeKey",settings.definition.verifyTypeKey),Common.Util.checkEmpty("applyModel.verifyType",runtime.applyModel.verifyType));
		paramMap.set(Common.Util.checkEmpty("definition.responseType", settings.definition.responseType),Common.Util.checkEmpty("definition.responseTypeValue",settings.definition.responseTypeValue));
		paramMap.set(Common.Util.checkEmpty("definition.secureAlgKey",settings.definition.secureAlgKey), runtime.handshake.handleChooseSecureAlg());
		paramMap.set("r", Math.random());
		// XSRF token
		var xsrfToken = _getXsrfToken();
		paramMap.set(xsrfToken.paramName, xsrfToken.value);
		// Replay token
		var replayToken = _generateReplayToken();
		paramMap.set(replayToken.paramName, replayToken.value);
		// Session info
		runtime.handshake.handleSessionTo(paramMap);
		return Common.Util.checkEmpty("deploy.baseUri",settings.deploy.baseUri)
				+ Common.Util.checkEmpty("definition.verifyAnalyzeUri", settings.definition.verifyAnalyzeUri)+"?"+Common.Util.toUrl({},paramMap);
	};

	// Reset graph captcha.
	var _resetCaptcha = function(refresh) {
		if (refresh) {
			var principal = encodeURIComponent(Common.Util.getEleValue("account.principalInput", settings.account.principalInput, false));
			_initSafeCheck(principal, function(res){
				if(runtime.safeCheck.checkCaptcha.enabled && !runtime.flags.isCurrentlyApplying){ // 启用验证码且不是申请中(防止并发)?
					// 获取当前配置的 CaptchaVerifier实例并显示
					Common.Util.checkEmpty("captcha.getVerifier", settings.captcha.getVerifier)().captchaRender();
				}
			});
		} else { // 获取当前配置的CaptchaVerifier实例并显示
			Common.Util.checkEmpty("captcha.getVerifier", settings.captcha.getVerifier)().captchaRender();
		}
	};

	// 渲染SNS授权二维码或页面, 使用setTimeout以解决 如,微信long请求导致父窗体长时间处于加载中问题
	var _snsViewReader = function(connectUrl, panelType) {
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

	// Init SNS authorizing authentication login implement.
	var _initSNSAuthenticator = function() {
		// Check authenticator enable?
		if (!settings.sns.enable) {
			_iamConsole.debug("SNS authenticator not enable!");
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
				var connectUrl = _getSnsConnectUrl(provider, panelType);
				// 执行点击SNS按钮事件
				if(!settings.sns.onBefore(provider, panelType, connectUrl)){
					console.warn("onBefore has blocked execution");
					return this;
				}
				// 渲染SNS登录二维码或页面
				_snsViewReader(connectUrl, panelType);
			}
		}
	};

	// Init safety check(PRE).
	var _initSafeCheck = function(principal, callback){
		$(function() {
			if (!callback) {
				callback = principal; // Real callback function
				principal = '';
			}
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
			_doIamRequest("post", "{checkUri}", checkParam, function(res){
				// 初始化完成回调
				Common.Util.checkEmpty("init.onPostCheck", settings.init.onPostCheck)(res);
				var codeOkValue = Common.Util.checkEmpty("definition.codeOkValue", settings.definition.codeOkValue);
				if(!Common.Util.isEmpty(res) && (res.code == codeOkValue)){
					runtime.safeCheck = $.extend(true, runtime.safeCheck, res.data); // [MARK3]
					callback(res);
				}
			}, function(errmsg){
				_iamConsole.log("Failed to safe check, " + errmsg);
				Common.Util.checkEmpty("init.onError", settings.init.onError)(errmsg); // 登录异常回调
			}, null, true);
		});
	};

	// Init Captcha verifier implement.
	var _initCaptchaVerifier = function() {
		// Check authenticator enable?
		if (!settings.captcha.enable) {
			_iamConsole.debug("Captcha verifier not enable!");
			return;
		}

		$(function(){
			// 初始刷新验证码
			_resetCaptcha(true);	// 初始化&绑定验证码事件
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
							_doIamRequest("post", _getVerifyAnalysisUrl(), captchaParam, function(res){
								runtime.flags.isVerifying = false; // Reset verify status.
								var codeOkValue = _check("definition.codeOkValue",settings.definition.codeOkValue);
								if(!Common.Util.isEmpty(res) && (res.code != codeOkValue)){ // Failed?
									_resetCaptcha(true);
									settings.captcha.onError(res.message); // Call after captcha error.
								} else { // Verify success.
									runtime.verifiedModel = res.data.verifiedModel;
									Common.Util.checkEmpty("captcha.getVerifier", settings.captcha.getVerifier)().captchaDestroy(false); // Hide captcha when success.
								}
							}, function(errmsg){
								runtime.flags.isVerifying = false; // Reset verify status.
								_resetCaptcha(true);
								settings.captcha.onError(errmsg); // Call after captcha error.
							}, null, true);
						}

					}
				});
			};
		});
	};

	// Init Account login implements.
	var _initAccountAuthenticator = function() {
		// Check authenticator enable?
		if (!settings.account.enable) {
			_iamConsole.debug("Account authenticator not enable!");
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

				// [bugfix] 建议强制刷新handshake， 可以解决如：当从官网点击‘登录演示账号’window.open()到首页后，
				// 点击了退出，此时再次点击‘登录演示账号’时之前handshake保存的session已被销毁，check接口会出现400错误.
				_initHandshakeIfNecessary(true).then(res0 => {
					_initSafeCheck(principal, function(res) {
						// 生成client公钥(用于获取认证成功后加密接口的密钥)
						runtime.clientSecretKey = IAMCrypto.RSA.generateKey();
						// 获取Server公钥(用于提交账号密码)
						var secretKey = Common.Util.checkEmpty("Secret is required", runtime.safeCheck.checkGeneric.secretKey);
						var credentials = encodeURIComponent(IAMCrypto.RSA.encryptToHexString(secretKey, plainPasswd));
						// 已校验的验证码Token(如果有)
						var verifiedToken = "";
						if(runtime.safeCheck.checkCaptcha.enabled) {
							verifiedToken = runtime.verifiedModel.verifiedToken; // [MARK2], see: 'MARK1,MARK4'
							if(Common.Util.isEmpty(verifiedToken)){ // Required
								settings.account.onError(Common.Util.isZhCN()?"请完成人机验证":"Please complete man-machine verify");
								_resetCaptcha(false);
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
						loginParam.set("{clientRefKey}", _getClientRef());
						loginParam.set("{verifiedTokenKey}", verifiedToken);
						loginParam.set("{verifyTypeKey}", Common.Util.checkEmpty("captcha.use", settings.captcha.use));
						loginParam.set("{secureAlgKey}", runtime.handshake.handleChooseSecureAlg());
						// 设备指纹umidToken(初始化页面时获取, 必须)
						loginParam.set("{umidTokenKey}", runtime.umid.getValue());
						// 添加自定义参数
						Common.Util.mergeMap(settings.account.customParamMap, loginParam);
						// 请求提交登录
						_doIamRequest("post", "{accountSubmitUri}", loginParam, function(res) {
							// 解锁登录按钮
							$(Common.Util.checkEmpty("account.submitBtn", settings.account.submitBtn)).removeAttr("disabled");

							runtime.verifiedModel.verifiedToken = ""; // Clear
							var codeOkValue = Common.Util.checkEmpty("definition.codeOkValue", settings.definition.codeOkValue);
							if(!Common.Util.isEmpty(res) && (res.code != codeOkValue)){ // Failed?
								_resetCaptcha(true); // 刷新验证码
								settings.account.onError(res.message); // 登录失败回调
							} else { // 登录成功，直接重定向
	                            $(document).unbind("keydown");
								var redirectUrl = Common.Util.checkEmpty("Login successfully, response data.redirect_url is empty", res.data[settings.definition.redirectUrlKey]);
								if(settings.account.onSuccess(principal, res.data)){
									Common.Util.getRootWindow(window).location.href = redirectUrl;
								}
							}
						}, function(errmsg){
							// 失败时也要解锁登录按钮
							$(Common.Util.checkEmpty("account.submitBtn", settings.account.submitBtn)).removeAttr("disabled");
							runtime.verifiedModel.verifiedToken = ""; // Clear
							settings.account.onError(errmsg); // 登录异常回调
						}, null, true);
					});
				});
			});
		});
	};

	// Init SMS authentication implements.
	var _initSMSAuthenticator = function(){
		// Check authenticator enable?
		if (!settings.sms.enable) {
			_iamConsole.debug("SMS authenticator not enable!");
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
				_doIamRequest("post", "{smsApplyUri}", getSmsParam, function(res) {
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
				}, null, true);
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
				_doIamRequest("post", "{smsSubmitUri}", smsLoginParam, function(res){
					var codeOkValue = Common.Util.checkEmpty("definition.codeOkValue",settings.definition.codeOkValue);
					if(!Common.Util.isEmpty(res) && (res.code != codeOkValue)){
						settings.sms.onError(res.message); // SMS登录失败回调
					} else {
						settings.sms.onSuccess(res); // SMS登录成功回调
						Common.Util.getRootWindow(window).location.href = res.data.redirect_url;
					}
				}, function(errmsg){
					settings.sms.onError(errmsg); // SMS登录失败回调
				}, null, true);
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
	var _getClientRef = function(){
		var clientRef = null;
		var osTypes = Common.Util.PlatformType;
		for(var osname in osTypes){
		    if(osTypes[osname]){
		    	_iamConsole.debug("Got current OS: "+ osname);
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
	var _doIamRequest = function(method, urlOrKey, params, successFn, errorFn, completeFn, sessionIfNecessary) {
		// Add default generic params.
		if (Common.Util.isMap(params)) {
			params.set("{responseType}", Common.Util.checkEmpty("definition.responseTypeValue", settings.definition.responseTypeValue));
		} else if (Common.Util.isObject(params)) {
			params['responseType'] = Common.Util.checkEmpty("definition.responseTypeValue", settings.definition.responseTypeValue);
		}
		// Add sessions. (If necessary)
		if (sessionIfNecessary) {
			runtime.handshake.handleSessionTo(params);
		}
		// Convert request url
		var _url = null;
		if (urlOrKey.startsWith("@")) { // Absolute url?
			_url = urlOrKey.substring(urlOrKey.indexOf('@') + 1);
		} else { // Iam build-in api url
			_url = Common.Util.checkEmpty("deploy.baseUri", settings.deploy.baseUri);
			if(urlOrKey.startsWith("{") && urlOrKey.endsWith("}")) { // definition url of placeholder key
				var realKey = urlOrKey.substr(1, urlOrKey.length - 2);
				_url += Common.Util.checkEmpty("definition", settings.definition[realKey]);
			} else { // definition url key
				_url += Common.Util.checkEmpty("definition", settings.definition[urlOrKey]);
			}
		}
		// Add headers of Iam security tokens(xsrf/replay).
		var headers = new Map();
		if (method.toUpperCase() == 'POST' || method.toUpperCase() == 'DELETE') {
			// XSRF token
			var xsrfToken = _getXsrfToken();
			headers.set(xsrfToken.headerName, xsrfToken.value);
			// Replay token
			var replayToken = _generateReplayToken();
			headers.set(replayToken.headerName, replayToken.value);
		}
		// Convert data params
		var dataParams = Common.Util.isMap(params) ? Common.Util.toUrl(settings.definition, params) : params;
		_iamConsole.debug("Requesting for - url:", _url, "headers:", headers);
		$.ajax({
			url: _url,
			type: method,
			async: true, // Note: Jquery1.8 has deprecated, @see https://api.jquery.com/jQuery.ajax/#jQuery-ajax-settings
			headers: JSON.fromMap(headers),
			//dataType: "json",
			data: dataParams,
			xhrFields: { withCredentials: true }, // Send cookies when support cross-domain request.
			success: function(res, textStatus, jqxhr) {
				if (successFn) {
					successFn(res);
				}
			},
			error: function(req, status, errmsg) {
				if (errorFn) {
					errorFn(errmsg);
				}
			},
			complete: function (xhr) {
	            if (completeFn) {
	            	completeFn(xhr);
	            }
	        }
		});
	};

	// Multi modular authenticating handler
	var _multiModularAuthenticatingHandler = {
		isRedirectingLogin: false, // 标记正在中定向到登录页（当iam-server会话失效），解决并发请求时会多次重复执行回调函数redirectFn
		mutexControllerManager: new Map(),
		// Do multi modular authenticating and biz request.
		doMultiModularRequest: function (method, url, params, successFn, errorFn, completeFn) {
			var url = '@' + url; // Use absolute url
			_doIamRequest(method, url, params || {}, successFn, errorFn, completeFn, false);
		},
		// 检查返回未登录(code=401)时是否跳转登录页，(仅当TGC过期(真正过期)是才跳转登录页，iam-client过期无需跳转登陆页)
		checkTGCExpiredAndRedirectToLogin: function (res, redirectFn) {
			const handler = _multiModularAuthenticatingHandler;
			_iamConsole.debug("TGC validating... res: ", res);
		    if (_isRespUnauthenticated(res)) {
		        // IamWithCasAppClient/IamWithCasAppServer
		        if (res.data && res.data.serviceRole == 'IamWithCasAppServer') { // TGC过期?
		        	_iamConsole.info("TGC expired, redirectTo: ",res.data[settings.definition.redirectUrlKey],
		        			"isRedirectingLogin: ", handler.isRedirectingLogin);
		            // e.g: window.location.href = '/#/login';
		        	if (redirectFn && !handler.isRedirectingLogin) {
		        		handler.isRedirectingLogin = true; // 正在重定向
		        		redirectFn(res);
					}
		            return true;
		        }
		    }
		    return false;
		},
		// 获取URL同源的并发认证控制器
		getMutexController: function (url) {
			const handler = _multiModularAuthenticatingHandler;
			var urlSame = null;
			if (url.toUpperCase().startsWith("HTTP://")) { // Absloute url
				const _url = new URL(url);
				urlSame = _url.protocol + "://" + _url.host;
			} else { // Relative url
				urlSame = location.origin + "//" + url;
			}
		    var controller = handler.mutexControllerManager.get(urlSame);
		    if (!controller) {
		    	handler.mutexControllerManager.set(urlSame, (controller = {
		    		_cache_auth_state: {
		    			state: false, time: 0,
		    		},
		            urlSame: urlSame,
		            currentlyInAuthenticatingState: false,
		            requestQueue: [], // FIFO
		            authenticated: function (state) {
		            	var _cas = controller._cache_auth_state;
		            	if (state) { // Setting
		            		_cas.state = state;
		            		_cas.time = new Date().getTime();
						} else { // Getting
							// 1, 使用authenticated状态判断是为了解决同一模块接口并发请求的问题,
							// 2, 给authenticated增加有效期, 是为了防止后台session过期而authenticated还是为true, 导致误认为还是已认证状态
							return _cas.state && Math.abs(new Date().getTime() - _cas.time) < 10000;
						}
		            },
		        }));
		    }
		    return controller;
		},
		// 拦截处理多模块并发认证请求（401重定向）
		doHandle: function (res, method, url, successFn, errorFn, params, redirectFn) {
			const handler = _multiModularAuthenticatingHandler;
			// Check parameters requires.
			Common.Util.checkEmpty('multiModularAuthenticatingRequest.res', res);
			Common.Util.checkEmpty('multiModularAuthenticatingRequest.method', method);
			Common.Util.checkEmpty('multiModularAuthenticatingRequest.url', url);
			Common.Util.checkEmpty('multiModularAuthenticatingRequest.redirectFn', redirectFn);
			// Check authentication status.
			if (!_isRespUnauthenticated(res)) {
				_iamConsole.debug("Ignore authenticated of url: ", url, ", res: ", res);
				return;
			}
			// 获取url(源)对应的并发认证控制器
            const controller = handler.getMutexController(url);
            if (!controller.currentlyInAuthenticatingState) {
                controller.currentlyInAuthenticatingState = true; // Mark authenticating
                new Promise(function (resolve, reject) {
                	_iamConsole.info("Biz unauth response: ", res);
                    if (controller.authenticated()) {
                        resolve();
                        return;
                    }
                    if (handler.checkTGCExpiredAndRedirectToLogin(res, redirectFn)) {
                        return;
                    }
                    if (!res.data || !res.data.redirect_url) {
                        errorFn(res);
                        return;
                    }
                    // Request IAM server authenticator.
                    handler.doMultiModularRequest(method, res.data.redirect_url, null, resolve, errorFn, null);
                }).then(function (res1) {
                	_iamConsole.info("Iam-server response: ", res1);
                    if (controller.authenticated()) {
                        return;
                    }
                    if (handler.checkTGCExpiredAndRedirectToLogin(res1, redirectFn)) {
                        return;
                    }
                    if (!res1.data || !res1.data.redirect_url) {
                    	if (errorFn) {
                    		errorFn(res1);
                    	}
                        return;
                    }
                    return new Promise((resolve, reject) => {
                    	// Request IAM client authenticator.
                        handler.doMultiModularRequest('get', res1.data.redirect_url, null, resolve, errorFn, null);
                    });
                }).then(function (res2) {
                	_iamConsole.info("Iam-client response: ", res2);
                    controller.currentlyInAuthenticatingState = false;  // Mark authentication completed

                    handler.doMultiModularRequest(method, url, params, function (res3) {
                    	_iamConsole.info("Redirect origin biz response: ", res3);
                        if (!_isRespUnauthenticated(res3)) {
                            if (successFn) {
                            	successFn(res3);
							}
                            controller.authenticated(true);
                        } else { // Need authRequest???
                            controller.authenticated(false);
                        }
                    }, function (errmsg) {
                        if (errorFn) {
                            errorFn(errmsg);
                        } else {
                        	_iamConsole.error(errmsg);
                        }
                    }, function () {
                        // Next biz requests
                        if (controller.requestQueue.length > 0) {
                            const authRequest = controller.requestQueue[0]; // Poll first
                            controller.requestQueue.splice(0, 1); // Remove
                            _iamConsole.info('Poll authenticating queue first: ', authRequest, ', requestQueue: ', controller.requestQueue);
                            handler.doHandle(authRequest.res, authRequest.method, authRequest.url,
                                authRequest.successFn, authRequest.errorFn, authRequest.params, redirectFn);
                        }
                    });
                });
            } else { // Offer queue
                const authRequest = { res: res, method: method, url: url, successFn: successFn, errorFn: errorFn, params: params };
                controller.requestQueue.push(authRequest);
                _iamConsole.info('Offered authenticating authRequest: ', authRequest, ', requestQueue: ', controller.requestQueue);
            }
        },
	};

	/**
	 * Check authentication and redirection.
	 * <pre>
	 * Using for example:
	 * -----------------------------------------------
	 * <head>
     *   <script type="text/javascript" src="./js/jquery.min.js"></script>
     *   <script type="text/javascript">
     *      // [1.动态引入js文件]
     *      // 使用document.write动态引入js文件，不能将此段代码放到如loader.js文件里执行，
     *      // 这样不能保证它执行的顺序（因为leader.js加载完成但还没有执行，但是document后面的js代码会马上执行）
     *      var sdkBaseUri=location.protocol+"//sso-services."+location.hostname.split('.').slice(-2).join('.')+"/sso/iam-jssdk/assets/";
     *      //var sdkBaseUri="http://wl4g.debug:14040/iam-server/iam-jssdk/assets/"; // for debug
     *      document.write('<link rel="stylesheet" href="'+ sdkBaseUri +'/css/IAM.all.min.css" />');
     *      document.write('<scr'+'ipt src="'+ sdkBaseUri +'/js/IAM.all.min.js"></scr'+'ipt>');
     *
     *      // [2.初始化IAM JSSDK]
	 *	    var options = {
	 *	        deploy: {
	 *				// You can also display the address of the specified SSO back-end API service
	 *	            //baseUri: "http://sso.wl4g.com/sso", // 也可写死sso后端api服务地址
	 *	            defaultTwoDomain: "sso-services", // sso后端api服务对应二级域名
	 *	            defaultContextPath: "/sso" // sso后端api服务的跟路径
	 *	        }
	 *	    };
	 *	    // Automatic redirection to home. (Optional)
	 *	    console.log("Check authentication redirect... ");
	 *	    var topDomain = Common.Util.extTopDomainString(location.host);
	 *	    var homeUrl = location.protocol + "//base." + topDomain;
	 *	    new IAMCore(options).checkAuthenticationAndRedirect(homeUrl).then(() => {
	 *	        $(function() {
	 *	            console.log("IAM JSSDK UI creation... ");
	 *	            new IAMUi().initUI(document.getElementById("content-right"), options);
	 *	        });
	 *	    });
     *   </script>
	 * </head>
	 * -----------------------------------------------
	 * </pre>
	 */
	var _checkAuthenticationAndRedirect = {
		cache: {
			bodyStyle: null,
			bodyClass: null,
		},
		hideDocumentAndOpenLoading: function() {
			var handler = _checkAuthenticationAndRedirect;
			var _body = $("body");
			// Hide body
			handler.cache.bodyStyle = _body.attr("style");
			handler.cache.bodyClass = _body.attr("class");
			_body.removeAttr("style");
			_body.removeAttr("class");
			// Hide elements and open loading. (if necessary)
			if ($(".iam_check_authc_redirect_style").length <= 0) {
				$("<style class='iam_check_authc_redirect_style'>" +
					"div,img,span,p,a,b{display:none;}body{background:url(" + settings.resources.loading +
					") no-repeat;background-position:center;!important}</style>").appendTo($("head"));
			}
		},
		showDocumentAndCloseLoading: function() {
			var handler = _checkAuthenticationAndRedirect;
			var _body = $("body");
			// Show body(If necessary)
			if (handler.cache.bodyStyle) { _body.attr("style", handler.cache.bodyStyle); }
			if (handler.cache.bodyClass) { _body.attr("class", handler.cache.bodyClass); }
			// Show elements and close loading
			$(".iam_check_authc_redirect_style").remove();
		},
		// Prevent flashing when redirecting to the home page.
		doHandle: function(redirectUrl) {
			_iamConsole.info("Checking unauthenticated and redirection ... ");
			var handler = _checkAuthenticationAndRedirect;

			// 首先添加隐藏元素的style, 避免body先渲染完出现闪屏)
			handler.hideDocumentAndOpenLoading();
			$(function() { handler.hideDocumentAndOpenLoading(); }); // body渲染完立即执行, loading才能显示

			return new Promise(resolve => {
				// When initializing the page, the delayed loading animation is specially displayed to prevent the white flash screen.
				_iamConsole.info("Checking authentication state ...");
				_initHandshakeIfNecessary(true).then(res => {
					if(!IAMCore.checkRespUnauthenticated(res)) { // Authenticated?
						var redirectRecord = JSON.parse(sessionStorage.getItem(constant.authRedirectRecordStorageKey));
						// Check null or expired?
						if (!redirectRecord || (redirectRecord && Math.abs(new Date().getTime() - redirectRecord.t) > 10000)) {
							sessionStorage.removeItem(constant.authRedirectRecordStorageKey); // For renew
							redirectRecord = { c: 0, t: new Date().getTime() };
						}
						if (redirectRecord.c > 10) {
							throw "Too many failure redirections: "+ redirectRecord.c;
						}
						++redirectRecord.c;
						redirectRecord.t = new Date().getTime();
						sessionStorage.setItem(constant.authRedirectRecordStorageKey, JSON.stringify(redirectRecord));
						_iamConsole.info("Authenticated and redirection to: ", redirectUrl);
						setTimeout(function() {
							//handler.showDocumentAndCloseLoading(); // 即将跳转无需关闭
							window.location = redirectUrl;
						}, (200+parseInt(Math.random()*500))); // Random
					} else {
						_iamConsole.info("Unauthentication rendering login page ... ");
						setTimeout(function() {
							handler.showDocumentAndCloseLoading();
							sessionStorage.removeItem(constant.authRedirectRecordStorageKey); // reset
							resolve(res);
						}, (200+parseInt(Math.random()*2000))); // Random
					}
				});
			});
		}
	};

	// Init Handshake authentication(PRE) implements.
	var _initHandshakeIfNecessary = function(refresh) {
		// Init gets umidToken and handshake.
		return runtime.umid.getValuePromise().then(umidToken => runtime.handshake.getValuePromise(umidToken, refresh));
	};

	// --- Exposing IAMCore APIs. ---

	window.IAMCore = function(opt) {
		runtime.__that = this;
		// Initializing.
		_initConfigure(opt);
	};
	// Export umToken
	IAMCore.prototype.getUMToken = function() {
		return runtim.umid.getValue();
	};
	// Export safeCheck
	IAMCore.prototype.safeCheck = function(principal, callback, refreshHandshake) {
		_initHandshakeIfNecessary(refreshHandshake).then(res => {
			_initSafeCheck(principal, callback);
		});
		return this;
	};
	// Export enable anyAuthenticators
	IAMCore.prototype.anyAuthenticators = function() {
		return this.accountAuthenticator()
				.smsAuthenticator()
				.snsAuthenticator()
				.captchaVerifier();
	};
	// Export enable accountAuthenticators
	IAMCore.prototype.accountAuthenticator = function() {
		settings.account.enable = true;
		return this;
	};
	// Export enable smsAuthenticators.
	IAMCore.prototype.smsAuthenticator = function() {
		settings.sms.enable = true;
		return this;
	};
	// Export enable snsAuthenticators.
	IAMCore.prototype.snsAuthenticator = function() {
		settings.sns.enable = true;
		return this;
	};
	// Export enable captchaVerifier.
	IAMCore.prototype.captchaVerifier = function() {
		settings.captcha.enable = true;
		return this;
	};
	// Export build.
	IAMCore.prototype.build = function() {
		_iamConsole.info("IAMCore init and building ...");
		// 1: Ensure execution sequence.
		// 1.1: get umidToken; 
		// 1.2: get handshake;
		// 1.3: init any authenticators
		// 2: Forced refresh is to solve the problem that you can't log in again after exiting SPM application. 
		// The reason is that SPM project is a single page application, and the action of logging out is only to 
		// execute push('/#/login'), but not to refresh the page At this time, the old session information (due to cache) 
		// is used, and the correct operation should refresh the handshake interface to get the new session information
		// as long as the login page is rendered. of course, external calls can also be made iamUi.destroy() to solve this problem.
		_initHandshakeIfNecessary(true).then(res => {
			_initAccountAuthenticator();
			_initSMSAuthenticator();
			_initSNSAuthenticator();
			_initCaptchaVerifier();
		});
	};
	// Export IAMCore destroy
	IAMCore.prototype.destroy = function() {
		for (var key in constant) {
			sessionStorage.removeItem(constant[key]);
		}
		constant = null;
		_defaultCaptchaVerifier = null;
		runtime = null;
		settings = null;
		_iamConsole.log("Destroyed IAMCore instance.");
	};

	// Export getXsrfToken
	IAMCore.prototype.getXsrfToken = _getXsrfToken;

	// Export generateReplayToken
	IAMCore.prototype.generateReplayToken = _generateReplayToken;

	// Export check authentication and redirection
	IAMCore.prototype.checkAuthenticationAndRedirect = _checkAuthenticationAndRedirect.doHandle;

	// Export function Iam console
	IAMCore.Console = _iamConsole;

	// Export function check resp unauthenticated
	IAMCore.checkRespUnauthenticated = _isRespUnauthenticated;

	// Export function check resp success.
	IAMCore.checkRespSuccess = _isRespSuccess;

	// Export function multi modular authenticating handler.
	IAMCore.multiModularMutexAuthenticatingHandler = _multiModularAuthenticatingHandler.doHandle;

	// Export function getIamBaseURI
	IAMCore.getIamBaseUri = function() {
		var iamBaseUri = _getIamBaseUri(); 
		// Overlay cache
		sessionStorage.setItem(constant.baseUriStoredKey, iamBaseUri);
		return iamBaseUri;
	};

})(window, document);
