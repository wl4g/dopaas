/**
 * Common Util v2.0.0 | (c) 2017 ~ 2050 wl4g Foundation, Inc.
 * Copyright 2017-2032 <wangsir@gmail.com, 983708408@qq.com>, Inc. x
 * Licensed under Apache2.0 (https://github.com/wl4g/super-devops/blob/master/LICENSE)
 */
(function(window, document) {
	// Exposing the API to the outside.
	if(!window.IamFingerprint){ window.IamFingerprint = {}; }

	// Export.
	window.IamFingerprint = {
       	// 获取浏览器指纹, 产品可参考阿里云风控sdk的umid获取: https://help.aliyun.com/document_detail/101030.html
       	// 以下是基于开源实现: https://github.com/Valve/fingerprintjs2 , 这个插件主要是获取浏览器语言/窗口尺寸等
       	// 环境参数计算的设备唯一标识, 某些参数需排除才能接近实际情况, 如:浏览器尺寸, 窗口尺寸不影响生成设备标识
       	getFingerprint: function(excludes, callback) {
       		var _excludes = null;
       		var _callback = null;
       		if(arguments.length  <= 0) {
       			throw Error("Callback is required");
       		} else {
       			for(var i=0; i < arguments.length; i++){
					if(Common.Util.isObject(arguments[i])) {
						_excludes = arguments[i];
					} else if(Common.Util.isFunction(arguments[i])) {
						_callback = arguments[i];
					}
				}
       		}
       		// 将外部配置深度拷贝到settings，注意：Object.assign(oldObj, newObj)只能浅层拷贝
       		var defaultOptions = {};
			_excludes = jQuery.extend(true, defaultOptions, _excludes);
       		if (_callback == null) {
       			throw Error("Callback is required");
       		}
       		if(Common.Constants._fingerprintObject){
       		 	if ((new Date().getTime()-Common.Constants._fingerprintObject.time) > 30000) {
	       			Common.Constants._fingerprintObject = null;
       		 	} else {
       		 		_callback(Common.Constants._fingerprintObject);
       		 		return;
       		 	}
       		}
       		// Gets last fingerprint.
       		if(!Common.Constants._fingerprintObject) {
       			setTimeout(function() {
					Fingerprint2.get({
					    excludes: _excludes
					}, function(components){ 
					    console.debug("Gets components: "+ JSON.stringify(components));
					    var values = components.map(function (component) { return component.value });
					    var umid = Fingerprint2.x64hash128(values.join(''), Math.sqrt(31));
					   	Common.Constants._fingerprintObject = { 
					   		umid: umid,
					   		time: new Date().getTime(),
					   		components: (function(){
						   		var componentsMap = new Map();
								for(var index in components){
								    componentsMap.set(components[index].key+"", components[index].value);
								}
								return componentsMap;
					   		})()
					   	};
					    _callback(Common.Constants._fingerprintObject);
					});
				}, 500);
			}
       	}

	};

})(window, document);
