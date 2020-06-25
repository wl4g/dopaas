/**
 * SuperOps-Cloud WebSDK Bootstrap latest | (c) 2017 ~ 2050 wl4g Foundation, Inc.
 * Copyright 2017-2050 <wangsir@gmail.com, 983708408@qq.com>, Inc. x
 * Licensed under Apache2.0 (@see https://github.com/wl4g/super-cloudops/blob/master/LICENSE)
 */
var VAR_PLUGIN_MODULES = "${{plugin_modules}}";
(function(window, document, initDefaultModules) {
	var _g_modules = initDefaultModules;

	// 对Date的扩展，将 Date 转化为指定格式的String 
	// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
	// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
	// 例子： 
	// (new Date()).format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
	// (new Date()).format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
	(function() {
		Date.prototype.format = function(fmt) {
			var o = {
			  "M+" : this.getMonth()+1,                 //月份 
			  "d+" : this.getDate(),                    //日 
			  "h+" : this.getHours(),                   //小时 
			  "m+" : this.getMinutes(),                 //分 
			  "s+" : this.getSeconds(),                 //秒 
			  "q+" : Math.floor((this.getMonth()+3)/3), //季度 
			  "S"  : this.getMilliseconds()             //毫秒 
			}; 
			if(/(y+)/.test(fmt)) {
			  fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
			}
			for(var k in o) {
			  if(new RegExp("("+ k +")").test(fmt)) {
				  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length))); 
			  }
			}
			return fmt;
		}
	})();

	// Ip util.
	var _isIp = function(ip) {
    	var patternV4 = /^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$/;
    	var patternV6 = /^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$/;
        var isIpv4 = patternV4.test(ip);
        var isIpv6 = patternV6.test(ip);
        return isIpv4 || isIpv6;
    };

    // Gets site baseURI with default,
    var _getServerBaseURI = function(opt) {
		var defaultOpt = {
			// 当前(页面)地址
			host: location.hostname,
			// 用于检查是否本地开发环境
			checkDevEnvHostPatterns: ["localhost", "127.0.0.1", "0:0:0:0:0:0:0:1", "*.debug", "*.local", "*.dev"],
			// 当确定是本地开发环境时, 指定的地址(方便调试用)
			devServerHost: null, // 默认: {opt.host.protocol}//{opt.host.name}:{opt.host.port}
			// 目标接口服务端口
			serverPort: 8080,
			// 目标接口服务的二级(子级)域名前缀(e.g: iam.wl4g.com/iam.console.wl4g.com)
			serverHostForSubLevelDomain: "iam", // iam.console
		};

		// Overlay config options
		opt = Object.assign(defaultOpt, opt);

		// Build apiServer base URI
		var hostname = opt.host;
		var port = opt.serverPort ? opt.serverPort : location.port;
		var protocol = location.protocol;
	 	// 1. 以下情况会认为是本地开发环境部署:
	 	// a. 当访问的地址是IP;
	 	// b. 当访问域名的后缀是.debug/.local/.dev等。
		var matchedDevEnv = opt.checkDevEnvHostPatterns.find(e => (hostname == e || hostname.endsWith(e.substring(e.lastIndexOf("*") + 1)))); // 检查是否本地环境
        if (_isIp(hostname) || matchedDevEnv) {
        	if (opt.devServerHost) { // 若指定了本地环境接口服务地址, 则优先使用
        		return opt.devServerHost;
			}
        	// 默认根据当前页面地址生成
        	return protocol + "//" + hostname + ":" + port;
        }
        // 2. 使用域名访问时走服务器部署结构:(根据顶级域名自动生成二级域名, 以作为目标接口服务的baseURI)
        else {
        	var topDomainName = hostname.split('.').slice(-2).join('.');
        	if(hostname.indexOf("com.cn") > 0) {
        		topDomainName = hostname.split('.').slice(-3).join('.');
        	}
        	return protocol + "//" + opt.serverHostForSubLevelDomain + "." + topDomainName;
        }
	};

	// Gets g_module
	var getGModule = function(name) {
		for(m in _g_modules.modules) {
			var module = _g_modules.modules[m];
			if(name == module.modName){
				return module;
			}
		}
	}

	// Parsing module dependencies 
	var getDependModules = function(feature) {
		var modules = new Array();
		for(var d=0; d<_g_modules.dependencies.length; d++){
			var matched = false;
			for(var f=0; f < _g_modules.dependencies[d].features.length; f++){
				if(_g_modules.dependencies[d].features[f] == feature){
					matched = true;
					break;
				}
			}
			if(matched) {
				for(var dn=0; dn<_g_modules.dependencies[d].depends.length; dn++){
					var gModule = getGModule(_g_modules.dependencies[d].depends[dn]);
					modules.push(gModule);
				}
			}
		}
		// 去重
		var setModules = [];
		if (modules && modules.length>0) {
			setModules = [modules[0]];
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
		}

		return (setModules.length==0) ? null : {"modules":setModules};
	};

	/**
	 * Resolve relative path to absolute.
	 * For example:
	 * document.location.pathname = http://localhost:14070/webjars-example/plugin/example/index.html
	 * resovleRelativePathIfNecessary('../../js/aaa.js') => "/webjars-example/js/aaa.js/"
	 */
	var resovleRelativePathIfNecessary = function(path) {
		var curRelativeSpec = "./";
		var relativeSpec = "../";
		var currentPath = document.location.pathname;
		if (!path) {
			path = curRelativeSpec;
		}
        // Check relative spec.
        if (path.indexOf(curRelativeSpec + curRelativeSpec) >= 0) {
            throw Error("Illegal path '" + path + "'");
        }
		if (path.startsWith(curRelativeSpec)) {
			return currentPath.substring(0, currentPath.lastIndexOf("/")) + path.substring(path.indexOf(curRelativeSpec) + curRelativeSpec.length);
		}
		if (path.startsWith(relativeSpec)) {
			var parts = path.split(relativeSpec);
            var pathSuffix = "";
			// Gets relative path parent level count.
			var relativeParentLevelCount = 0;
			for (var index in parts) {
				if (parts[index] == '') {
					relativeParentLevelCount += 1;
				} else {
                    pathSuffix += parts[index] + "/";
                }
			}
			// Gets absolute path.
			var currentPathParts = currentPath.split("/");
			var absolutePath = "";
			for (var i=0; i < (currentPathParts.length-relativeParentLevelCount-1); i++) {
				absolutePath += currentPathParts[i] + "/";
			}
			return absolutePath + pathSuffix;
		}
		return path;
	};
	
	/**
	 * Gets current script attributes settings,
	 * load(css and js) specification module JSSDK API.
	 * 
	 * @return.param path Module jssdk file path(relative prefix).
	 * @return.param cache Whether to use caching.
	 * @return.param mode Operating mode (or environment) Optional: stable | grey
	 **/
	var _modules_settings = (function() {
		var _settings = {
			path: "",
			cache: "false",
			mode: "stable",
			refreshLevel: "yyMMddhh"
		}
		// Gets default path(<script src=http://cdn.wl4g.com/sdk/2.0.0/dist/js/IAM.js  => defaultPath=http://cdn.wl4g.com/sdk/2.0.0/dist).
		var defaultPath = "";
		// 获取的当前script引用
		var scripts = document.getElementsByTagName("script");
		var curScript = scripts[scripts.length - 1]; // 最后一个script元素，即引用了本文件的script元素
		curScript = document.currentScript || curScript;
		if (curScript) {
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
			// Merge configs
			var fileUriPort = curScript.getAttribute("fileUriPort");
			var fileUriDomainSubLevel = curScript.getAttribute("fileUriDomainSubLevel");
			var _path = curScript.getAttribute("path") || _settings.path;
			if (_path.toUpperCase().startsWith("HTTP://") ||_path.toUpperCase().startsWith("HTTPS://")) {
				_settings.path = _path;
			} else {
				var baseUri = _getServerBaseURI({ serverPort: fileUriPort, serverHostForSubLevelDomain: fileUriDomainSubLevel });
				_settings.path = baseUri + resovleRelativePathIfNecessary(_path); // Resolve relative path(if necessary)
			}
			_settings.cache = curScript.getAttribute("cache") || _settings.cache;
			_settings.mode = curScript.getAttribute("mode") || _settings.mode;
		} else {
			console.warn("Not currently running in a formal environment!");
		}

		// Print settings
		console.debug("Using IAM JSSDK settings: ", _settings);
		if(_settings.cache){
			console.debug("IAM JSSDK cache is enabled!");
		}
		if(_settings.mode == 'grey'){
			console.warn("Using IAM JSSDK [GREY] mode!");
		}
		return _settings;
	})();

	// Use js
	var _useJs = function(feature, callback) {
		if(Object.prototype.toString.call(feature) == '[object Function]' || !feature || callback == null || !callback) {
			throw Error("useJs parameters (feature, callback) is required!");
		}
		// Gets configs.
		var path = _modules_settings.path + "/js/";
		var cache = _modules_settings.cache;
		var mode = _modules_settings.mode;
		var rtime = new Date().format(_modules_settings.refreshLevel);

		// When the onload event has been monitored by others, it will not be executed here
		// (for example, in the Vue project)
		//window.onload = function() {
		var depends = getDependModules(feature);
		if(!depends) throw Error("No such module feature of '"+ feature +"'");
		var scripts = depends.modules.map(d => {
			var t = (cache == 'true') ? 1 : new Date().getTime();
			return {"modName": d.modName, "url": d[mode]+"?t="+rtime};
		});
		// Loading multiple scripts.
		(function loadScripts(scripts, path) {
			scripts.forEach(function(src, i) {
				// Already load?
				if(window[src.modName]) {
					console.debug("Skip already load script module '" + src.modName + "'");
					callback(); // [MARK2]
					return;
				}
				var script = document.createElement('script');
				script.type = 'text/javascript';
				script.charset = 'UTF-8';
				script.src = (path || "") + src.url;
				script.async = false;
				// If last script, bind the callback event to resolve
				if (i == scripts.length - 1) {
					// Multiple binding for browser compatibility
					script.onload = script.onreadystatechange = function(e) {
						if (!script.readyState || script.readyState == 'loaded' || script.readyState == 'complete') {
			            	console.debug("Loaded scripts feature: "+feature+", readyState: "+ this.readyState);
							callback(); // [MARK1]
						}
			        };
				}
				// Fire the loading
				document.head.appendChild(script);
				//document.body.appendChild(script);
			});
		})(scripts, path);

		// --- JQuery Versions. ---
		//$.when(scripts, $.Deferred(d => $(d.resolve))).done(function(response, status){
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
	};

	// Use css
	var _useCss = function(feature, callback) {
		if(Object.prototype.toString.call(feature) == '[object Function]' || !feature || callback == null || !callback) {
			throw Error("useCss parameters (feature, callback) is required!");
		}
		// Gets settings.
		var path = _modules_settings.path + "/css/";
		var cache = _modules_settings.cache;
		var mode = _modules_settings.mode;
		var rtime = new Date().format(_modules_settings.refreshLevel);

		var depends = getDependModules(feature);
		if(!depends) throw Error("No such module feature of '"+ feature +"'");
		var csses = depends.modules.filter(d => d["css_"+mode] != null && d["css_"+mode] != "").map(d => {
			var t = (cache == 'true') ? 1 : new Date().getTime();
			return {"modName": d.modName, "url": d["css_"+mode]+"?t="+rtime};
		});
		// Loading multiple css
		(function loadCss(csses, path) {
			csses.forEach(function(src, i) {
				// Already load?
				if(window[src.modName]) {
					console.debug("Skip already load css module '" + src.modName + "'");
					callback();
					return;
				}
				var link = document.createElement('link');
				link.rel = 'stylesheet';
				link.charset = 'UTF-8';
				link.href = (path || "") + src.url;
				link.async = false;
				// If last link, bind the callback event to resolve
				if (i == csses.length - 1) {
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
		})(csses, path);
	};

	// Export LoaderJS.
	window.LoaderJS = function(modules) {
		if (modules && (_g_modules || _g_modules == VAR_PLUGIN_MODULES)) {
			var oldModules = _g_modules;
			try {
				oldModules = JSON.stringify(_g_modules);
			} catch(e) {
			}
			console.debug("Overlay default modules: "+ oldModules);
			_g_modules = modules;
		}
		// Check global modules
		if (!_g_modules) {
			throw Error("Must configure modules dependencies!");
		}
		console.debug("Plugin modules : " + JSON.stringify(_g_modules));
	};
	/**
	 * Use load(css and js) specification module JSSDK API.
	 * 
	 * @param name Module feature(alias).
	 * @param callback Loaded callback function.
	 **/
	LoaderJS.prototype.use = function(feature, callback) {
		this.useCss(feature, function(){});
		this.useJs(feature, callback);
	};
	LoaderJS.prototype.useJs = _useJs,
	LoaderJS.prototype.useCss = _useCss;
	LoaderJS.isIp = _isIp;
	LoaderJS.getServerBaseURI = _getServerBaseURI;

})(window, document, VAR_PLUGIN_MODULES);