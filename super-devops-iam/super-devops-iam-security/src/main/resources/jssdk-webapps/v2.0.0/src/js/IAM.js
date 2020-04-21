/**
 * IAM WebSDK Bootstrap v2.0.0 | (c) 2017 ~ 2050 wl4g Foundation, Inc.
 * Copyright 2017-2032 <wangsir@gmail.com, 983708408@qq.com>, Inc. x
 * Licensed under Apache2.0 (https://github.com/wl4g/super-devops/blob/master/LICENSE)
 */
(function(window, document){
	var g_modules = {
		jqModule: { // JQuery
			stable: "jquery-3.3.1.min.js",
			grey: "jquery-3.3.1.js",
			css_stable: null,
			css_grey: null,
			ratio: 100
		},
		cryptoJSModule: { // CryptoJS
			stable: "crypto-js-4.0.0.min.js",
			grey: "crypto-js-4.0.0.js",
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
		name: "IAMUi", // 需与SDK的类名一致, @see:[MARK1]
		features: ["iamUi", "IamUI", "IamUi", "iamSdkUi", "IamSdkUI"],
		depends: ["fpWebModule", "jqModule", "cryptoJSModule", "commonModule", "cryptoModule", "coreModule",
			"captchaJigsawModule", "uiModule"],
		sync: !1
	},
	{
		name: "IAMCore", // 需与SDK的类名一致, @see:[MARK1]
		features: ["iamCore", "IamCore", "iamSdkCore", "IamSdkCore"],
		depends: ["fpWebModule", "jqModule", "cryptoJSModule", "commonModule", "cryptoModule", "coreModule", 
			"captchaJigsawModule"],
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
	function getDependModules(feature){
		var modules = new Array(), moduleName = null;
		for(var d=0; d<g_dependencies.length; d++){
			var matched = false;
			for(var f=0; f < g_dependencies[d].features.length; f++){
				if(g_dependencies[d].features[f] == feature){
					matched = true;
					moduleName = g_dependencies[d].name;
					break;
				}
			}
			if(matched){
				for(var dn=0; dn<g_dependencies[d].depends.length; dn++){
					var gModule = getGModule(g_dependencies[d].depends[dn]);
					modules.push(gModule);
				}
			}
		}
		// 去重
		var setModules = [modules[0]];
		for(var i=1; i < modules.length; i++){
			var flag = false;
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

		return (!moduleName||setModules.length ==0) ? null : {"name": moduleName,"modules":setModules};
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
		var scripts = document.getElementsByTagName("script");
		var curScript = scripts[scripts.length - 1]; // 最后一个script元素，即引用了本文件的script元素
		curScript = document.currentScript || curScript;
		// Gets default path(<script src=http://cdn.wl4g.com/iamjssdk/2.0.0/js/IAM.js  => defaultPath=http://cdn.wl4g.com/iamjssdk/2.0.0).
		var defaultPath = "./";
		var src = curScript.getAttribute("src");
		if(src) {
			var parts = src.split("/");
			if(src.toLocaleUpperCase().startsWith("HTTP")){
				defaultPath = ""; // reset
				for(var i=0; i<parts.length; i++){
				    if(i<parts.length-2){
				        defaultPath += parts[i]+"/";
				    }
				}
				// Remove last '/'
				defaultPath = defaultPath.substring(0, defaultPath.length-1);
			}
		}
		var settings = {
			path: curScript.getAttribute("path") || defaultPath,
			cache: curScript.getAttribute("cache") || "false",
			mode: curScript.getAttribute("mode") || "stable"
		};
		// Print settings
		console.debug("Using IAM JSSDK settings: "+ JSON.stringify(settings));
		if(settings.cache){
			console.debug("IAM JSSDK cache is enabled!");
		}
		if(settings.mode == 'grey'){
			console.warn("Using IAM JSSDK [GREY] mode!");
		}
		return settings;
	})();

	window.IAM.Module = {
		/**
		 * Use load(css and js) specification module JSSDK API.
		 * 
		 * @param name Module feature(alias).
		 * @param callback Loaded callback function.
		 **/
		use: function(feature, callback){
			IAM.Module.useCss(feature, function(){});
			IAM.Module.useJs(feature, callback);
		},
		useJs: function(feature, callback){
			if(Object.prototype.toString.call(feature) == '[object Function]' || !feature || callback == null || !callback) {
				throw Error("useJs parameters (feature, callback) is required!");
			}
			// Gets settings.
			var path = _modules_settings.path + "/js/";
			var cache = _modules_settings.cache;
			var mode = _modules_settings.mode;

			// When the onload event has been monitored by others, it will not be executed here
			// (for example, in the Vue project)
			//window.onload = function() {
			var depends = getDependModules(feature);
			if(!depends) throw Error("No such module feature of '"+ feature +"'");
			var scriptUrls = depends.modules.map(d => {
				var t = (cache == 'true') ? 1 : new Date().getTime();
				return d[mode]+"?t="+t;
			});
			// Already load?
			if(window[depends.name]) {
				console.debug("Skip already load script module '" + name + "'");
				callback(new window[depends.name]); // [MARK2]
				return;
			}
			// Loading multiple scripts.
			(function loadScripts(name, urls, path) {
				urls.forEach(function(src, i) {
					var script = document.createElement('script');
					script.type = 'text/javascript';
					script.charset = 'UTF-8';
					script.src = (path || "") + src;
					script.async = false;
					// If last script, bind the callback event to resolve
					if (i == urls.length - 1) {
						// Multiple binding for browser compatibility
						script.onload = script.onreadystatechange = function(e) {
							if (!script.readyState || script.readyState == 'loaded' || script.readyState == 'complete') {
				            	console.debug("Loaded scripts feature: "+feature+", readyState: "+ this.readyState);
								callback(new window[name]); // [MARK1]
							}
				        };
					}
					// Fire the loading
					document.head.appendChild(script);
					//document.body.appendChild(script);
				});
			})(depends.name, scriptUrls, path);

			// --- JQuery Versions. ---
			//$.when(scriptUrls, $.Deferred(d => $(d.resolve))).done(function(response, status){
			//	console.debug("Loaded script of feature: "+ feature);
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
			//	console.debug("Loaded script of feature: "+ feature);
			//	callback("loaded");
			//});
			//}
		},
		useCss: function(feature, callback){
			if(Object.prototype.toString.call(feature) == '[object Function]' || !feature || callback == null || !callback) {
				throw Error("useCss parameters (feature, callback) is required!");
			}
			// Gets settings.
			var path = _modules_settings.path + "/css/";
			var cache = _modules_settings.cache;
			var mode = _modules_settings.mode;

			var depends = getDependModules(feature);
			if(!depends) throw Error("No such module feature of '"+ feature +"'");
			var cssUrls = depends.modules.filter(d => d["css_"+mode] != null && d["css_"+mode] != "").map(d => {
				var t = (cache == 'true') ? 1 : new Date().getTime();
				return d["css_"+mode]+"?t="+t;
			});
			// Already load?
			if(window[depends.name]) {
				console.debug("Skip already load css module '" + depends.name + "'");
				callback();
				return;
			}
			// Loading multiple css
			(function loadCss(name, urls, path) {
				urls.forEach(function(src, i) {
					var link = document.createElement('link');
					link.rel = 'stylesheet';
					link.charset = 'UTF-8';
					link.href = (path || "") + src;
					link.async = false;
					// If last link, bind the callback event to resolve
					if (i == urls.length - 1) {
						// Multiple binding for browser compatibility
						link.onload = link.onreadystatechange = function(e) {
							if (!link.readyState || link.readyState == 'loaded' || link.readyState == 'complete') {
				            	console.debug("Loaded links feature: "+feature+", readyState: "+ this.readyState);
								callback();
							}
				        };
					}
					// Fire the loading
					document.head.appendChild(link);
				});
			})(depends.name, cssUrls, path);
		}
	}

})(window, document);