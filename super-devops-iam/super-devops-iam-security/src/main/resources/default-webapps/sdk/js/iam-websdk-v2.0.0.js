/**
 * IAM WebSDK All v2.0.0 | (c) 2017 ~ 2050 wl4g Foundation, Inc.
 * Copyright 2017-2032 <wangsir@gmail.com, 983708408@qq.com>, Inc. x
 * Licensed under Apache2.0 (https://github.com/wl4g/super-devops/blob/master/LICENSE)
 */
 
!function(window, document){
	var g_modules = {
		jqModule: { // Fingerprint
			stable: "jquery-3.3.1.min.js",
			grey: "jquery-3.3.1.min.js",
			ratio: 100
		},
		fpWebModule: { // Fingerprint
			stable: "fingerprint2-v2.1.0.js",
			grey: "fingerprint2-v2.1.0.js",
			ratio: 200
		},
		commonModule: {
			stable: "common-util-v2.0.0.js",
			grey: "common-util-v2.0.0.js",
			ratio: 200
		},
		cryptoModule: {
			stable: "iam-websdk-crypto-v2.0.0.js",
			grey: "iam-websdk-crypto-v2.0.0.js",
			ratio: 300
		},
		coreModule: {
			stable: "iam-websdk-core-v2.0.0.js",
			grey: "iam-websdk-core-v2.0.0.js",
			ratio: 500
		},
		captchaJigsawModule: {
			stable: "iam-websdk-core-v2.0.0.js",
			grey: "iam-websdk-core-v2.0.0.js",
			ratio: 400
		},
		uiModule: {
			stable: "iam-websdk-ui-v2.0.0.js",
			grey: "iam-websdk-ui-v2.0.0.js",
			ratio: 10000
		}
	};
	var g_dependencies = [{
		name: "uiModule",
		features: ["iamUi", "iamSdkUi"],
		depends: ["commonModule", "fpWebModule", "jqModule", "cryptoModule", "coreModule", "captchaJigsawModule", "uiModule"],
		sync: !1
	},
	{
		name: "coreModule",
		features: ["ui", "iamUi", "iamSdkUi"],
		depends: ["commonModule", "fpWebModule", "jqModule", "cryptoModule", "coreModule", "captchaJigsawModule"],
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

	// Using module
	window.SuperDevOps = {
		use: function(name, callback){
			var scriptUrls = getDependModules(name).map(d => d.stable+"?t="+new Date().getTime());
			// Loading multiple scripts
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
				            	console.debug("Loaded scripts of name:"+name+", readyState:"+this.readyState);
								callback("loaded");
							}
				        };
					}
					// Fire the loading
					document.body.appendChild(script);
				});
			})(scriptUrls, "./js/");

			// --- JQuery Versions. ---
			//$.when(scriptUrls, $.Deferred(d => $(d.resolve))).done(function(response, status){
			//	console.debug("Loaded script of name: "+ name);
			//	callback(status);
			//});

			//$.when($.getScript("./js/fingerprint2-v2.1.0.js"),
			//$.getScript("./js/common-util-v2.0.0.js"),
			//$.getScript("./js/iam-websdk-core-v2.0.0.js"),
			//$.getScript("./js/iam-websdk-crypto-v2.0.0.js"),
			//$.getScript("./js/iam-websdk-captcha-jigsaw-v2.0.0.js"),
			//$.getScript("./js/iam-websdk-ui-v2.0.0.js"),
			//$.getScript("./js/cryptojs-4.0.0/crypto-js.min.js"),
			//$.Deferred(function(deferred){
			//    $(deferred.resolve);
			//})).done(function(){
			//	console.debug("Loaded script of name: "+ name);
			//	callback("loaded");
			//});
		}
	}

}(window, document);