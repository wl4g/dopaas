/**
 * IAM WebSDK All v2.0.0 | (c) 2017 ~ 2050 wl4g Foundation, Inc.
 * Copyright 2017-2032 <wangsir@gmail.com, 983708408@qq.com>, Inc. x
 * Licensed under Apache2.0 (https://github.com/wl4g/super-devops/blob/master/LICENSE)
 */
 
!function(window, document){
	var g_modules = {
		jqModule: { // JQuery
			stable: "jquery-3.3.1.min.js",
			grey: "jquery-3.3.1.min.js",
			css_stable: null,
			css_grey: null,
			ratio: 100
		},
		cryptoJSModule: { // CryptoJS
			stable: "crypto-js-4.0.0.min.js",
			grey: "crypto-js-4.0.0.min.js",
			css_stable: null,
			css_grey: null,
			ratio: 100
		},
		fpWebModule: { // Fingerprint
			stable: "fingerprint2-2.1.0.min.js",
			grey: "fingerprint2-2.1.0.js",
			css_stable: null,
			css_grey: null,
			ratio: 200
		},
		commonModule: {
			stable: "common-util.min.js",
			grey: "common-util.js",
			css_stable: "font-awesome-all.5.7.2.min.css",
			css_grey: "font-awesome-all.5.7.2.min.css",
			ratio: 250
		},
		cryptoModule: {
			stable: "iam-jssdk-crypto.min.js",
			grey: "iam-jssdk-crypto.js",
			css_stable: null,
			css_grey: null,
			ratio: 300
		},
		captchaJigsawModule: {
			stable: "iam-jssdk-captcha-jigsaw.min.js",
			grey: "iam-jssdk-captcha-jigsaw.js",
			css_stable: "iam-jssdk-captcha-jigsaw.min.css",
			css_grey: "iam-jssdk-captcha-jigsaw.css",
			ratio: 400
		},
		coreModule: {
			stable: "iam-jssdk-core.min.js",
			grey: "iam-jssdk-core.js",
			css_stable: null,
			css_grey: null,
			ratio: 500
		},
		uiModule: {
			stable: "iam-jssdk-ui.min.js",
			grey: "iam-jssdk-ui.js",
			css_stable: "iam-jssdk-ui.min.css",
			css_grey: "iam-jssdk-ui.css",
			ratio: 1000
		}
	};
	var g_dependencies = [{
		name: "uiModule",
		features: ["iamUi", "iamSdkUi", "IamUI", "IamSdkUI"],
		depends: ["fpWebModule", "jqModule", "cryptoJSModule", "commonModule", "cryptoModule", "coreModule",
			"captchaJigsawModule", "uiModule"],
		sync: !1
	},
	{
		name: "coreModule",
		features: ["iamCore", "iamSdkCore", "IamCore", "IamSdkCore"],
		depends: ["fpWebModule", "jqModule", "cryptoJSModule", "cryptoModule", "coreModule", "captchaJigsawModule"],
		sync: !1
	}];

	// Gets g_module
	function getGModule(name){
		for(m in g_modules){
			if(name == m){
				return g_modules[m];
			}
		}
	}

	// Parsing module dependencies 
	function getDependModules(name){
		var modules = new Array();
		for(var d=0; d < g_dependencies.length; d++){
			var matched = false;
			for(var f=0; f < g_dependencies[d].features.length; f++){
				if(g_dependencies[d].features[f] == name){
					matched = true;
					break;
				}
			}
			if(matched){
				for(var dn=0; dn < g_dependencies[d].depends.length; dn++){
					var gModule = getGModule(g_dependencies[d].depends[dn]);
					modules.push(gModule);
				}
			}
		}
		let setModules = [modules[0]];
		for(let i=1; i < modules.length; i++){
			let flag = false;
			for(var j=0; j < setModules.length; j++){
	        	if(modules[i].stable == setModules[j].stable || modules[i].grey == setModules[j].grey){
		        	flag = true;
		        	break;
		        }
		    }
		    if(!flag){
				setModules.push(modules[i]);
		    }
		}
		setModules.sort(function(a, b){
			return a.ratio - b.ratio;
		});
		return setModules;
	}

	// Using modules.
	window.IAM = window.IAM || {};
	/**
	 * Gets current script attributes settings,
	 * load(css and js) specification module JSSDK API.
	 * 
	 * @return.param path Module jssdk file path(relative prefix).
	 * @return.param cache Whether to use caching.
	 * @return.param mode Operating mode (or environment) Optional: stable | grey
	 **/
	var _modules_settings = (function(){
		let scripts = document.getElementsByTagName("script");
		var curScript = scripts[scripts.length - 1]; // 最后一个script元素，即引用了本文件的script元素
		curScript = document.currentScript || curScript;
		// Gets default path(<script src=http://cdn.wl4g.com/iamjssdk/2.0.0/js/IAM.js  => defaultPath=http://cdn.wl4g.com/iamjssdk/2.0.0).
		var src = curScript.getAttribute("src");
		var defaultPath = "./";
		var parts = src.split("/");
		if(src.toLocaleUpperCase().startsWith("HTTP")){
			defaultPath = ""; // reset
			for(var i=0; i<parts.length; i++){
			    if(i<parts.length-2){
			        defaultPath += parts[i]+"/";
			    }
			}
		}
		var settings = {
			path: curScript.getAttribute("path") || defaultPath,
			cache: curScript.getAttribute("cache") || "false",
			mode: curScript.getAttribute("mode") || "stable"
		};
		// Print settings
		console.debug("IAM JSSDK using settings: "+ JSON.stringify(settings));
		if(settings.cache){
			console.debug("IAM JSSDK cache is enabled!");
		}
		if(settings.mode == 'grey'){
			console.warn("IAM JSSDK using [GREY] mode!");
		}
		return settings;
	})();

	window.IAM.Modules = {
		/**
		 * Use load(css and js) specification module JSSDK API.
		 * 
		 * @param name Module name(alias).
		 * @param callback Loaded callback function.
		 **/
		use: function(name, callback){
			IAM.Modules.useCss(name, function(){});
			IAM.Modules.useJs(name, callback);
		},
		useJs: function(name, callback){
			if(Object.prototype.toString.call(name) == '[object Function]' || !name || callback == null || !callback) {
				throw Error("useJs parameters (name, callback) is required!");
			}
			// Gets settings.
			var path = _modules_settings.path + "/js/";
			var cache = _modules_settings.cache;
			var mode = _modules_settings.mode;

			window.onload = function(){
				var scriptUrls = getDependModules(name).map(d=>{
					var t = (cache == 'true') ? 1 : new Date().getTime();
					return d[mode]+"?t="+t;
				});
				// Loading multiple scripts.
				(function loadScripts(urls, path) {
					urls.forEach(function(src, i) {
						let script = document.createElement('script');
						script.type = 'text/javascript';
						script.charset = 'UTF-8';
						script.src = (path || "") + src;
						script.async = false;
						// If last script, bind the callback event to resolve
						if (i == urls.length - 1) {
							// Multiple binding for browser compatibility
							script.onload = script.onreadystatechange = function(e) {
								if (!script.readyState || script.readyState == 'loaded' || script.readyState == 'complete') {
					            	console.debug("Loaded scripts name: "+name+", readyState: "+ this.readyState);
									callback(window.IAM.UI);
								}
					        };
						}
						// Fire the loading
						document.body.appendChild(script);
					});
				})(scriptUrls, path);

				// --- JQuery Versions. ---
				//$.when(scriptUrls, $.Deferred(d => $(d.resolve))).done(function(response, status){
				//	console.debug("Loaded script of name: "+ name);
				//	callback(status);
				//});
	
				//$.when($.getScript("./js/fingerprint2-v2.1.0.js"),
				//$.getScript("./js/common-util.js"),
				//$.getScript("./js/iam-jssdk-core.js"),
				//$.getScript("./js/iam-jssdk-crypto.js"),
				//$.getScript("./js/iam-jssdk-captcha-jigsaw.js"),
				//$.getScript("./js/iam-jssdk-ui.js"),
				//$.getScript("./js/cryptojs-4.0.0/crypto-js.min.js"),
				//$.Deferred(function(deferred){
				//    $(deferred.resolve);
				//})).done(function(){
				//	console.debug("Loaded script of name: "+ name);
				//	callback("loaded");
				//});
			}
		},
		useCss: function(name, callback){
			if(Object.prototype.toString.call(name) == '[object Function]' || !name || callback == null || !callback) {
				throw Error("useCss parameters (name, callback) is required!");
			}
			// Gets settings.
			var path = _modules_settings.path + "/css/";
			var cache = _modules_settings.cache;
			var mode = _modules_settings.mode;

			var cssUrls = getDependModules(name).filter(d=>d["css_"+mode]!=null&&d["css_"+mode]!="").map(d=>{
				var t = (cache == 'true') ? 1 : new Date().getTime();
				return d["css_"+mode]+"?t="+t;
			});
			// Loading multiple css
			(function loadCss(urls, path) {
				urls.forEach(function(src, i) {
					let link = document.createElement('link');
					link.rel = 'stylesheet';
					link.charset = 'UTF-8';
					link.href = (path || "") + src;
					link.async = false;
					// If last link, bind the callback event to resolve
					if (i == urls.length - 1) {
						// Multiple binding for browser compatibility
						link.onload = link.onreadystatechange = function(e) {
							if (!link.readyState || link.readyState == 'loaded' || link.readyState == 'complete') {
				            	console.debug("Loaded links name: "+name+", readyState: "+ this.readyState);
								callback();
							}
				        };
					}
					// Fire the loading
					document.head.appendChild(link);
				});
			})(cssUrls, path);
		}
	}

}(window, document);