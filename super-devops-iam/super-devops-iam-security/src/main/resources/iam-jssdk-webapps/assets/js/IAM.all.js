/**
 * Common Util v2.0.0 | (c) 2017 ~ 2050 wl4g Foundation, Inc.
 * Copyright 2017-2032 <wangsir@gmail.com, 983708408@qq.com>, Inc. x
 * Licensed under Apache2.0 (https://github.com/wl4g/super-devops/blob/master/LICENSE)
 */
(function(window, document) {
	'use strict';

	// Exposing the API to the outside.
	if(!window.Common){ window.Common = {}; }
	if(!window.Common.Constants){ window.Common.Constants = {}; }

	// Common util.
	window.Common.Util = {
		printSafeWarn: function(msg){
			console.log("%cSECURITY WARNING!","font-size:50px;color:red;-webkit-text-fill-color:red;-webkit-text-stroke:1px black;");
		 	if(!Common.Util.isEmpty(msg)){
				console.log("%c"+msg,"font-size:14px;color:#303d65;");
		 	}
		},
		getEleValue: function(name, obj){
			return getEleValue(name, obj, true);
		},
		getEleValue: function(name, obj, assertion){
			if(!assertion) {
				return $(obj).val();
			}
			return Common.Util.checkEmpty(name, $(obj).val());
		},
		checkEmpty: function(name, param){
			if(Common.Util.isEmpty(param)){
				throw "Argument '" + name + "' must not be empty";
			}
			return param;
		},
		empty: function(param){
			if(Common.Util.isEmpty(param)){
				return "";
			}
			return param;
		},
		isEmpty: function(param){
			if(param == null || param == undefined || param == '')
				return true;
			else if(Common.Util.isFunction(param))
				return false;
			else
				return param.length == 0;
		},
		isAnyEmpty: function(params){
			for(var i = 0; i < arguments.length; i++){
				if(Common.Util.isEmpty(arguments[i]))
					return true;
			}
			return false;
		},
		isAnyContains: function(params, search){
			if(!Common.Util.isArray(Common.Util.checkEmpty("Arguments", params))){
				throw "Argument must is type of Array";
			}
			for(var i = 0; i < params.length; i++){
				if(params[i] == search)
					return true;
			}
			return false;
		},
		isArray : function (obj) {
		    return Object.prototype.toString.call(obj) === '[object Array]';
		},
		isMap : function (obj) {
		    return Object.prototype.toString.call(obj) === '[object Map]';
		},
		isObject : function (obj) {
		    return Object.prototype.toString.call(obj) === '[object Object]';
		},
		isFunction : function (obj) {
		    return Object.prototype.toString.call(obj) === '[object Function]';
		},
		isString : function (obj) {
		    return Object.prototype.toString.call(obj) === '[object String]';
		},
		sortWithAscii : function(str) { // 按ASCII排序
			return Array.prototype.sort.call(Array.from(str), function(a, b) {
			    return a.charCodeAt(0) - b.charCodeAt(0); // (a,b)=>(a.charCodeAt(0) - b.charCodeAt(0))
			}).join('');
		},
		extTopDomainString: function(hostOrUri) {
			var domain = hostOrUri; // Is host?
			if (hostOrUri.indexOf('/') > 0) { // Is URI?
				domain = new URL(hostOrUri).host;
			}
			// Check domain available?
			if (Common.Util.isEmpty(domain)) {
				return "";
			}
			var topDomainName = domain.split('.').slice(-2).join('.');
        	if(domain.indexOf("com.cn") > 0) {
        		topDomainName = domain.split('.').slice(-3).join('.');
        	}
        	return topDomainName;
		},
		getCookie: function(cookieName, cookies) {
			if (!cookies) {
				cookies = document.cookie;
			}
			var cookiesArr = cookies.split(";");
			for(var i = 0; i < cookiesArr.length; i++){
				var cookie = cookiesArr[i].split("=");
				var value = cookie[1];
				if(cookie[0].trim() == cookieName.trim()){
					return value;
				}
			}
			return null;
		},
		// 获取最顶层window对象(对于嵌套iframe刷新页面跳转非常有用)
		getRootWindow: function(currentWindow) {
			var _window = currentWindow;
			while (_window.self.frameElement && _window.self.frameElement.tagName == "IFRAME"
				|| _window.self != _window.top) {
			  _window = _window.parent;
			}
			return _window;
		},
		isEnabled: function(value){
			if(!Common.Util.isEmpty(value)){
				value = value.toLowerCase();
				return  (value == "y" || value == "on" || value == "yes" || value == "1" || value == "enabled" || value == "true" || value == "t");
			}
			return false;
		},
		mergeMap: function(dst, src) {
		    dst = dst || new Map();
		    src = src || new Map();
		    dst.forEach((v, k) => { src.set(k, v); });
		    return src;
		},
		clone: function(obj) { // 或使用JSON.parse(JSON.stringify(oldObj))实现深度拷贝，注意：Object.assign(oldObj,newObj)只能浅层拷贝
			if(!Common.Util.isEmpty(obj)){
				var newobj = obj.constructor === Array ? [] : {};
				if (typeof obj !== "object") {
					return obj;
				} else {
					for (var i in obj) {
						newobj[i] = typeof obj[i] === "object" ? Common.Util.clone(obj[i]) : obj[i];
					}
				}
				return newobj;
			}
			return null;
		},
		Http: {
			createXMLHttpRequest: function() {      
				if (window.ActiveXObject) {           
					var ieArr = ["Msxml2.XMLHTTP.6.0", "Msxml2.XMLHTTP.3.0", "Msxml2.XMLHTTP", "Microsoft.XMLHTTP"];                          
					for (var i = 0; i < ieArr.length; i++)                {               
						var xmlhttp = new ActiveXObject(ieArr[i]);               
					}               
					return xmlhttp;      
				} else if (window.XMLHttpRequest) {              
					return new XMLHttpRequest();           
				}
			},
			/**
			 * e.g:
			 * <pre>
			 * Common.Util.Http.request({
			 *	    url: "http://my.domain.com/myapp/list", 
			 *	    method: "head",
			 *	    timeout: 1000,
			 *	    async: false,
			 *	    withCredentials: true,
			 *	    success: function(data, textStatus, xhr) {
			 *	    debugger
			 *	        console.log("Response data:", data)
			 *	    },
			 *	    error: function(xhr, textStatus, errmsg) {
			 *	        console.log("Request processing error:", errmsg)
			 *	    }
			 *	})
			 * </pre>
			 */
			request: function(option) {
				var url = option.url,
				method = option.method || "GET",
				type = option.type || option.method, // For jQuery compatible
				async = option.async || true,
				data = option.data || null,
				withCredentials = option.withCredentials || false,
				timeout = option.timeout || 30000,
				success = option.success || function(data, textStatus, xhr) {},
				error = option.error || function(xhr, textStatus, errmsg) { console.error(errmsg); };
				try {
					// Check arguments requires.
					Common.Util.checkEmpty("url", url);

					// 1.创建XMLHttpRequest组建
					var _xhr = null;
					if (!_xhr) {
						_xhr = Common.Util.Http.createXMLHttpRequest();
					}
					_xhr.withCredentials = withCredentials;

					var _responsed = false;
					// 2.设置超时检查函数
					var _timeoutChecker = window.setTimeout(function() {
						if (!_responsed) {
							error(_xhr, null, "Timeout waiting for response, " + timeout);
						}
					}, timeout);

					// 3.设置回调函数
					_xhr.onreadystatechange = function() {
						if (_xhr.readyState == 4) {
							_responsed = true;
							window.clearTimeout(_timeoutChecker);
							// 3.1获取返回数据
							var res = _xhr.responseText;
							if (_xhr.status == 200) {
								success(res, _xhr.textStatus, _xhr);
							} else {
								error(_xhr, _xhr.textStatus, "Error status");
							}
						}
					};
					// 4.初始化XMLHttpRequest组建
					_xhr.open(type.toUpperCase(), url, async);
					// 5.发送请求
					_xhr.send(data);
				} catch(e) {
					error(_xhr, null, e);
				}
			}
		},
		PlatformType: (function() {
		    var ua = navigator.userAgent.toLowerCase();
		    var mua = {
		        MicroMessenger: /micromessenger/.test(ua), //WeChat MicroMessenger
		        iOS: /ipod|iphone|ipad/.test(ua), //iOS
		       	Mac: /macintosh|mac os|mac/.test(ua), // Mac
		        iPhone: /iphone/.test(ua), // iPhone
		        iPad: /ipad/.test(ua), // iPad
		        Android: /android/.test(ua), // Android Device
		        Windows: /windows/.test(ua), // Windows Device
		        Linux: /linux/.test(ua), // Linux Device
		        FreeBSD: /freebsd/.test(ua), // FreeBSD Device
		        OpenBSD: /openbsd/.test(ua), // OpenBSD Device
		        SunOS: /sunos/.test(ua), // SunOS Device
		        AIX: /aix/.test(ua), // AIX Device
		        Irix: /irix/.test(ua), // Irix Device
		        Solaris: /solaris/.test(ua), // Solaris Device
		        TouchDevice: ('ontouchstart' in window) || /touch/.test(ua), // Touch Device
		        Mobile: /mobile/.test(ua), // Mobile Device (iPad)
		        AndroidTablet: false, // Android Tablet
		        WINDOWS_TABLET: false, // Windows Tablet
		        Tablet: false, // Tablet (iPad, Android, Windows)
		        SmartPhone: false // Smart Phone (iPhone, Android)
		    };
		    mua.AndroidTablet = mua.Android && !mua.Mobile;
		    mua.WindowsTablet = mua.Windows && /tablet/.test(ua);
		    mua.Tablet = mua.iPad || mua.AndroidTablet || mua.WindowsTablet;
		    mua.SmartPhone = mua.Mobile && !mua.Tablet;
		    return mua;
		})(),
		int2char: function(n) {
		    return "0123456789abcdefghijklmnopqrstuvwxyz".charAt(n);
		},
		language: function() {
		    return (navigator.language || navigator.browserLanguage || navigator.systemLanguage).toLowerCase();
		},
		isZhCN: function() {
		    return Common.Util.language().indexOf('zh') >= 0;
		},
		// Convertion paramMap to formData url
		toUrl: function(templateMap, paramMap) {
			Common.Util.checkEmpty("templateMap", templateMap);
			Common.Util.checkEmpty("paramMap", paramMap);
			var formData = "";
			paramMap.forEach((value, key) => {
				var paramName = key;
				// Check template placeholder key.
				if(!Common.Util.isEmpty(key)) {
					if(key.startsWith("{") && key.endsWith("}")) {
						var realKey = key.substr(1, key.length - 2);
						paramName = Common.Util.checkEmpty("templateMap." + realKey, templateMap[realKey]);
					}
				} else {
					console.warn("Null param.key of parameters: "+ JSON.stringify(paramMap));
				}
				formData += (paramName + "=" + value + "&");
			});
			if(formData.endsWith("&")){
				formData = formData.substr(0, formData.lastIndexOf('&'));
			}
			return formData;
		},
		toUrlQueryParam: function(url) {
		    if(!url) {
		        return null;
		    }
		    var index = url.lastIndexOf("?");
		    if(index >= 0) {
		        url = url.substring(index+1);
		    }
		    var paramPairs = url.split("&");
		    var paramsMap = new Map();
		    for (var i=0; i<paramPairs.length; i++) {
		        var parts = paramPairs[i].split("=");
		        if (parts.length >= 2) {
		            paramsMap.set(parts[0], parts[1]);
		        }
		    }
		    return paramsMap;
		},
		Codec: {
			encodeBase58: function(plaintext){ // 明文字符串base58编码
				if(!plaintext) { return null; }
				var ALPHABET = '123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz';
				var ALPHABET_MAP = {};
				var BASE = 58;
				for (var i = 0; i < ALPHABET.length; i++) {
					ALPHABET_MAP[ALPHABET.charAt(i)] = i;
				}
				// 内部明文字符串转字节函数(toUTF8())
				var plainBuffer = (function(str){
				    var result = new Array();
				    var k = 0;
				    for (var i = 0; i < str.length; i++) {
				        var j = encodeURI(str[i]);
				        if (j.length==1) {
				            // 未转换的字符
				            result[k++] = j.charCodeAt(0);
				        } else {
				            // 转换成%XX形式的字符
				            var bytes = j.split("%");
				            for (var l = 1; l < bytes.length; l++) {
				                result[k++] = parseInt("0x" + bytes[l]);
				            }
				        }
				    }
				    return result;
				})(plaintext);
				// 编码base58字符串
				if (plainBuffer.length === 0) return '';
				var i, j, digits = [0];
				for (i = 0; i < plainBuffer.length; i++) {
					for (j = 0; j < digits.length; j++){
			            // 将数据转为二进制，再位运算右边添8个0，得到的数转二进制
			            // 位运算-->相当于 digits[j].toString(2);parseInt(10011100000000,2)
			            digits[j] <<= 8;
			        }
					digits[0] += plainBuffer[i];
					var carry = 0;
					for (j = 0; j < digits.length; ++j) {
						digits[j] += carry;
						carry = (digits[j] / BASE) | 0;
						digits[j] %= BASE;
					}
					while (carry) {
						digits.push(carry % BASE);
						carry = (carry / BASE) | 0;
					}
				}
				// Deal with leading zeros
				for (i = 0; plainBuffer[i] === 0 && i < plainBuffer.length - 1; i++) digits.push(0);
				return digits.reverse().map(function(digit) { return ALPHABET[digit]; }).join('');
			},
			decodeBase58: function(base58) { // base58字符串解码
				if(!base58) { return null; }
				var ALPHABET = '123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz';
				var ALPHABET_MAP = {};
				var BASE = 58;
				for (var i = 0; i < ALPHABET.length; i++) {
					ALPHABET_MAP[ALPHABET.charAt(i)] = i;
				}
				if (base58.length === 0) return [];
				var i, j, bytes = [0];
				for (i = 0; i < base58.length; i++) {
					var c = base58[i];
			        // c是不是ALPHABET_MAP的key 
					if (!(c in ALPHABET_MAP)) throw new Error('Non-base58 character');
					for (j = 0; j < bytes.length; j++) bytes[j] *= BASE;
					bytes[0] += ALPHABET_MAP[c];
					var carry = 0;
					for (j = 0; j < bytes.length; ++j) {
						bytes[j] += carry;
						carry = bytes[j] >> 8;
			            // 0xff --> 11111111
						bytes[j] &= 0xff;
					}
					while (carry) {
						bytes.push(carry & 0xff);
						carry >>= 8;
					}
				}
				// Deal with leading zeros
				for (i=0; base58[i] === '1' && i < base58.length - 1; i++) bytes.push(0);
				// Ascii to string.
				var plainBuffer = bytes.reverse();
				var plaintext = "";
				for (i=0; i<plainBuffer.length; i++) {
					plaintext += String.fromCharCode(plainBuffer[i]);
				}
				return plaintext;
			},
			encodeBase64: function(str) {
			    var encode = encodeURI(str);
			    var base64Str = btoa(encode);
			    return base64Str;
			},
			decodeBase64: function(base64Str) {
				var decode = atob(base64Str);
				var str = decodeURI(decode);
				return str;
			},
			toHex: function(str) {
				return Common.Util.Codec.base64ToHex(Common.Util.Codec.encodeBase64(str));
			},
			// Base64 and Hex functions.
            hexToBase64: function(str) {
            	var tableStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        		var table = tableStr.split("");
				var btoa = function(bin) {
					for (var i = 0, j = 0, len = bin.length / 3, base64 = []; i < len; ++i) {
	                    var a = bin.charCodeAt(j++), b = bin.charCodeAt(j++), c = bin.charCodeAt(j++);
	                    if ((a | b | c) > 255) throw new Error("String contains an invalid character");
	                    base64[base64.length] = table[a >> 2] + table[((a << 4) & 63) | (b >> 4)] +
	                        (isNaN(b) ? "=" : table[((b << 2) & 63) | (c >> 6)]) +
	                        (isNaN(b + c) ? "=" : table[c & 63]);
	                }
	                return base64.join("");
				};
	            return btoa(String.fromCharCode.apply(null,
	                str.replace(/\r|\n/g, "").replace(/([\da-fA-F]{2}) ?/g, "0x$1 ").replace(/ +$/, "").split(" "))
	            );
	        },
	        base64ToHex: function(str) { // convert a base64 string to hex
	        	if(str == null || !str) { return null; }
			    var ret = "";
			    var k = 0; // b64 state, 0-3
			    var slop = 0;
			    for (var i = 0; i < str.length; ++i) {
			        if (str.charAt(i) == "=") {
			            break;
			        }
			        var v = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf(str.charAt(i));
			        if (v < 0) {
			            continue;
			        }
			        if (k == 0) {
			            ret += Common.Util.int2char(v >> 2);
			            slop = v & 3;
			            k = 1;
			        }
			        else if (k == 1) {
			            ret += Common.Util.int2char((slop << 2) | (v >> 4));
			            slop = v & 0xf;
			            k = 2;
			        }
			        else if (k == 2) {
			            ret += Common.Util.int2char(slop);
			            ret += Common.Util.int2char(v >> 2);
			            slop = v & 3;
			            k = 3;
			        }
			        else {
			            ret += Common.Util.int2char((slop << 2) | (v >> 4));
			            ret += Common.Util.int2char(v & 0xf);
			            k = 0;
			        }
			    }
			    if (k == 1) {
			        ret += Common.Util.int2char(slop << 2);
			    }
			    return ret;
			}
		},
		Crc16CheckSum: {
			_auchCRCHi : [
				0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
				0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
				0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
				0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
				0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
				0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41,
				0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
				0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
				0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
				0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40,
				0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
				0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
				0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
				0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40,
				0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
				0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
				0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
				0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
				0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
				0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
				0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
				0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40,
				0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
				0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
				0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
				0x80, 0x41, 0x00, 0xC1, 0x81, 0x40
			],
			_auchCRCLo : [
				0x00, 0xC0, 0xC1, 0x01, 0xC3, 0x03, 0x02, 0xC2, 0xC6, 0x06,
				0x07, 0xC7, 0x05, 0xC5, 0xC4, 0x04, 0xCC, 0x0C, 0x0D, 0xCD,
				0x0F, 0xCF, 0xCE, 0x0E, 0x0A, 0xCA, 0xCB, 0x0B, 0xC9, 0x09,
				0x08, 0xC8, 0xD8, 0x18, 0x19, 0xD9, 0x1B, 0xDB, 0xDA, 0x1A,
				0x1E, 0xDE, 0xDF, 0x1F, 0xDD, 0x1D, 0x1C, 0xDC, 0x14, 0xD4,
				0xD5, 0x15, 0xD7, 0x17, 0x16, 0xD6, 0xD2, 0x12, 0x13, 0xD3,
				0x11, 0xD1, 0xD0, 0x10, 0xF0, 0x30, 0x31, 0xF1, 0x33, 0xF3,
				0xF2, 0x32, 0x36, 0xF6, 0xF7, 0x37, 0xF5, 0x35, 0x34, 0xF4,
				0x3C, 0xFC, 0xFD, 0x3D, 0xFF, 0x3F, 0x3E, 0xFE, 0xFA, 0x3A,
				0x3B, 0xFB, 0x39, 0xF9, 0xF8, 0x38, 0x28, 0xE8, 0xE9, 0x29,
				0xEB, 0x2B, 0x2A, 0xEA, 0xEE, 0x2E, 0x2F, 0xEF, 0x2D, 0xED,
				0xEC, 0x2C, 0xE4, 0x24, 0x25, 0xE5, 0x27, 0xE7, 0xE6, 0x26,
				0x22, 0xE2, 0xE3, 0x23, 0xE1, 0x21, 0x20, 0xE0, 0xA0, 0x60,
				0x61, 0xA1, 0x63, 0xA3, 0xA2, 0x62, 0x66, 0xA6, 0xA7, 0x67,
				0xA5, 0x65, 0x64, 0xA4, 0x6C, 0xAC, 0xAD, 0x6D, 0xAF, 0x6F,
				0x6E, 0xAE, 0xAA, 0x6A, 0x6B, 0xAB, 0x69, 0xA9, 0xA8, 0x68,
				0x78, 0xB8, 0xB9, 0x79, 0xBB, 0x7B, 0x7A, 0xBA, 0xBE, 0x7E,
				0x7F, 0xBF, 0x7D, 0xBD, 0xBC, 0x7C, 0xB4, 0x74, 0x75, 0xB5,
				0x77, 0xB7, 0xB6, 0x76, 0x72, 0xB2, 0xB3, 0x73, 0xB1, 0x71,
				0x70, 0xB0, 0x50, 0x90, 0x91, 0x51, 0x93, 0x53, 0x52, 0x92,
				0x96, 0x56, 0x57, 0x97, 0x55, 0x95, 0x94, 0x54, 0x9C, 0x5C,
				0x5D, 0x9D, 0x5F, 0x9F, 0x9E, 0x5E, 0x5A, 0x9A, 0x9B, 0x5B,
				0x99, 0x59, 0x58, 0x98, 0x88, 0x48, 0x49, 0x89, 0x4B, 0x8B,
				0x8A, 0x4A, 0x4E, 0x8E, 0x8F, 0x4F, 0x8D, 0x4D, 0x4C, 0x8C,
				0x44, 0x84, 0x85, 0x45, 0x87, 0x47, 0x46, 0x86, 0x82, 0x42,
				0x43, 0x83, 0x41, 0x81, 0x80, 0x40
			],
			crc16Modbus : function (obj) {
				var buffer = Object.prototype.toString.call(obj) === '[object Array]' ? obj : Common.Util.Crc16CheckSum.strToByte(obj);
				var ucCRCHi = (0xffff & 0xff00) >> 8;
				var ucCRCLo = 0xffff & 0x00ff;
				var iIndex;
				for (var i = 0; i < buffer.length; ++i) {
					iIndex = (ucCRCLo ^ buffer[i]) & 0x00ff;
					ucCRCLo = ucCRCHi ^ Common.Util.Crc16CheckSum._auchCRCHi[iIndex];
					ucCRCHi = Common.Util.Crc16CheckSum._auchCRCLo[iIndex];
				}
				return ((ucCRCHi & 0x00ff) << 8) | (ucCRCLo & 0x00ff) & 0xffff;
			},
			strToByte : function (str) {
				var tmp = str.split(''), arr = [];
				for (var i = 0, c = tmp.length; i < c; i++) {
					var j = encodeURI(tmp[i]);
					if (j.length == 1) {
						arr.push(j.charCodeAt());
					} else {
						var b = j.split('%');
						for (var m = 1; m < b.length; m++) {
							arr.push(parseInt('0x' + b[m]));
						}
					}
				}
				return arr;
			}
		},
		FastMap: function() {
			var length = 0;
			var obj = new Object();
			this.asJsonString = function(){
				return JSON.stringify(obj);
			};
			this.isEmpty = function(){
				return length == 0;
			};
			this.containsKey = function(key){
				return (key in obj);
			};
			this.containsValue = function(value){
				for(var key in obj){
					if(obj[key] == value){
						return true;
					}
				}
				return false;
			};
			this.put = function(key,value){
				if(!this.containsKey(key)){
					length++;
				}
				obj[key] = value;
			};
			this.get = function(key){
				return this.containsKey(key) ? obj[key] : null;
			};
			this.remove = function(key){
				if(this.containsKey(key) && (delete obj[key])){
					length--;
				}
			};
			this.values = function(){
				var _values= new Array();
				for(var key in obj){
					_values.push(obj[key]);
				}
				return _values;
			};
			this.keySet = function(){
				var _keys = new Array();
				for(var key in obj){
					_keys.push(key);
				}
				return _keys;
			};
			this.size = function(){
				return length;
			};
			this.clear = function(){
				length = 0;
				obj = new Object();
			};
		},
		HashMap: function(){
			// See: https://yq.aliyun.com/articles/245764?do=login&accounttraceid=2d2d9331-a204-4c8a-aaf4-a6d3ebd3be6e
			//初始大小
			var size = 0;
			//数组
			var table = [];
			//初始数组长度为16
			var length = 2 << 3;
			//数组扩容临界值为12
			var threshold = 0.75 * length;
			// hash值计算
			this.hash = function(h) {
				h ^= (h >>> 20) ^ (h >>> 12);
				return h ^ (h >>> 7) ^ (h >>> 4);
			};

			//返回HashMap的size
			this.size = function(){
				return size;
			};

			//是否包含某个key
			this.containsKey = function(key) {
				if(key == null || key == undefined)
					return false;
				else {
					var hashCode = this.hashCode(key);
					var hash = this.hash(hashCode);
					var index = this.indexFor(hash, length);
					for(var e = table[index]; e != null && e != undefined; e = e.next){
						if(e.key === key){
							return true;
						}
					}
					return false;
				}
			};

			//是否包含某个value
			this.containsValue = function(value) {
				for(var index = 0; index < table.length; index++) {
					for (var e = table[index]; e != null && e != undefined; e = e.next) {
						if (JSON.stringify(e.value) === JSON.stringify(value)) {
							return true;
						}
					}
				}
				return false;
			};

			//HashMap是否为空
			this.isEmpty = function(){
				return size === 0;
			};

			//计算HashCode值，不同的key有不同的HashCode，这里使用字符串转ASCII码并拼接的方式
			this.hashCode = function(key){
				var hashcode = '';
				for(var i=0 ;i< key.length; i++){
					hashcode += key.charCodeAt(i);
				}
				return hashcode;
			};

			//向HashMap中存放值
			this.put = function(key, value){
				if(key == null || key == undefined){
					return;
				}
				var hashCode = this.hashCode(key);
				var hash = this.hash(hashCode);
				var index = this.indexFor(hash, length);
				for(var e = table[index]; e != null && e != undefined; e = e.next){
					if(e.key === key){
						var oldValue = e.value;
						e.value = value;
						return oldValue;
					}
				}
				this.addEntry(key, value, index)
			};

			//从HashMap中获取值
			this.get = function(key){
				if(key == null || key == undefined)
					return undefined;
				var hashCode = this.hashCode(key);
				var hash = this.hash(hashCode);
				var index = this.indexFor(hash, length);
				for(var e = table[index]; e != null && e != undefined; e = e.next){
					if(e.key === key){
						return e.value;
					}
				}
				return undefined;
			};

			//从HashMap中删除值
			this.remove = function(key){
				if(key == null || key == undefined)
					return undefined;
				var hashCode = this.hashCode(key);
				var hash = this.hash(hashCode);
				var index = this.indexFor(hash, length);
				var prev = table[index];
				var e = prev;
				while(e != null && e!= undefined){
					var next = e.next;
					if(e.key === key){
						size--;
						if(prev == e){
							table[index] = next;
						} else{
							prev.next = next;
						}
						return e;
					}
					prev = e;
					e = next;
				}
				return e == null||e == undefined? undefined: e.value;
			};

			//清空HashMap
			this.clear = function() {
				table = [];
				// 设置size为0
				size = 0;
				length = 2 << 3;
				threshold = 0.75 * length;
			};

			//根据hash值获取数据应该存放到数组的哪个桶(下标)中
			this.indexFor = function(h, length) {
				return h & (length-1);
			};

			//添加一个新的桶来保存key和value
			this.addEntry = function(key, value, bucketIndex) {
				// 保存对应table的值
				var e = table[bucketIndex];
				// 然后用新的桶套住旧的桶，链表
				table[bucketIndex] = { key: key, value: value, next: e};
				// 如果当前size大于等于阈值
				if (size++ >= threshold) { // 调整容量
					length = length << 1;
					threshold = 0.75 * length;
				}
			};

			//获取HashMap中所有的键值对
			this.getEntries = function(){
				var entries = [];
				for(var index = 0; index < table.length; index++) {
					for (var e = table[index]; e != null && e != undefined; e = e.next) {
						entries.push({key: e.key, value: e.value});
					}
				}
				return entries;
			};
        },
        isIpv4 : function(ip) {
            var pattern = /^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$/;
            return pattern.test(ip);
        },
        isIpv6 : function(ip) {
            var pattern = /^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$/;
            return pattern.test(ip);
        },
        isIp : function(ip) {
            return Common.Util.isIpv4(ip) || Common.Util.isIpv6(ip);
        }
	},
	// 对Date的扩展，将 Date 转化为指定格式的String 
	// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
	// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
	// 例子： 
	// (new Date()).format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
	// (new Date()).format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
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
	},
	// Map to json object.
	JSON.fromMap = function(map) {
		let json = Object.create(null);
		for (let[k,v] of map) {
			json[k] = v;
		}
		return json;
	},
	// JSON to map object.
	JSON.toMap = function(json) {
		let map = new Map();
		for (let k of Object.keys(json)) {
			map.set(k, json[k]);
		}
		return map;
	}
})(window, document);
/** @see https://cdnjs.com/libraries/crypto-js */
!function(t,e){"object"==typeof exports?module.exports=exports=e():"function"==typeof define&&define.amd?define([],e):t.CryptoJS=e()}(this,function(){var h,t,e,r,i,n,f,o,s,c,a,l,d,m,x,b,H,z,A,u,p,_,v,y,g,B,w,k,S,C,D,E,R,M,F,P,W,O,I,U,K,X,L,j,N,T,q,Z,V,G,J,$,Q,Y,tt,et,rt,it,nt,ot,st,ct,at,ht,lt,ft,dt,ut,pt,_t,vt,yt,gt,Bt,wt,kt,St,bt=bt||function(l){var t;if("undefined"!=typeof window&&window.crypto&&(t=window.crypto),!t&&"undefined"!=typeof window&&window.msCrypto&&(t=window.msCrypto),!t&&"undefined"!=typeof global&&global.crypto&&(t=global.crypto),!t&&"function"==typeof require)try{t=require("crypto")}catch(t){}function i(){if(t){if("function"==typeof t.getRandomValues)try{return t.getRandomValues(new Uint32Array(1))[0]}catch(t){}if("function"==typeof t.randomBytes)try{return t.randomBytes(4).readInt32LE()}catch(t){}}throw new Error("Native crypto module could not be used to get secure random number.")}var r=Object.create||function(t){var e;return n.prototype=t,e=new n,n.prototype=null,e};function n(){}var e={},o=e.lib={},s=o.Base={extend:function(t){var e=r(this);return t&&e.mixIn(t),e.hasOwnProperty("init")&&this.init!==e.init||(e.init=function(){e.$super.init.apply(this,arguments)}),(e.init.prototype=e).$super=this,e},create:function(){var t=this.extend();return t.init.apply(t,arguments),t},init:function(){},mixIn:function(t){for(var e in t)t.hasOwnProperty(e)&&(this[e]=t[e]);t.hasOwnProperty("toString")&&(this.toString=t.toString)},clone:function(){return this.init.prototype.extend(this)}},f=o.WordArray=s.extend({init:function(t,e){t=this.words=t||[],this.sigBytes=null!=e?e:4*t.length},toString:function(t){return(t||a).stringify(this)},concat:function(t){var e=this.words,r=t.words,i=this.sigBytes,n=t.sigBytes;if(this.clamp(),i%4)for(var o=0;o<n;o++){var s=r[o>>>2]>>>24-o%4*8&255;e[i+o>>>2]|=s<<24-(i+o)%4*8}else for(o=0;o<n;o+=4)e[i+o>>>2]=r[o>>>2];return this.sigBytes+=n,this},clamp:function(){var t=this.words,e=this.sigBytes;t[e>>>2]&=4294967295<<32-e%4*8,t.length=l.ceil(e/4)},clone:function(){var t=s.clone.call(this);return t.words=this.words.slice(0),t},random:function(t){for(var e=[],r=0;r<t;r+=4)e.push(i());return new f.init(e,t)}}),c=e.enc={},a=c.Hex={stringify:function(t){for(var e=t.words,r=t.sigBytes,i=[],n=0;n<r;n++){var o=e[n>>>2]>>>24-n%4*8&255;i.push((o>>>4).toString(16)),i.push((15&o).toString(16))}return i.join("")},parse:function(t){for(var e=t.length,r=[],i=0;i<e;i+=2)r[i>>>3]|=parseInt(t.substr(i,2),16)<<24-i%8*4;return new f.init(r,e/2)}},h=c.Latin1={stringify:function(t){for(var e=t.words,r=t.sigBytes,i=[],n=0;n<r;n++){var o=e[n>>>2]>>>24-n%4*8&255;i.push(String.fromCharCode(o))}return i.join("")},parse:function(t){for(var e=t.length,r=[],i=0;i<e;i++)r[i>>>2]|=(255&t.charCodeAt(i))<<24-i%4*8;return new f.init(r,e)}},d=c.Utf8={stringify:function(t){try{return decodeURIComponent(escape(h.stringify(t)))}catch(t){throw new Error("Malformed UTF-8 data")}},parse:function(t){return h.parse(unescape(encodeURIComponent(t)))}},u=o.BufferedBlockAlgorithm=s.extend({reset:function(){this._data=new f.init,this._nDataBytes=0},_append:function(t){"string"==typeof t&&(t=d.parse(t)),this._data.concat(t),this._nDataBytes+=t.sigBytes},_process:function(t){var e,r=this._data,i=r.words,n=r.sigBytes,o=this.blockSize,s=n/(4*o),c=(s=t?l.ceil(s):l.max((0|s)-this._minBufferSize,0))*o,a=l.min(4*c,n);if(c){for(var h=0;h<c;h+=o)this._doProcessBlock(i,h);e=i.splice(0,c),r.sigBytes-=a}return new f.init(e,a)},clone:function(){var t=s.clone.call(this);return t._data=this._data.clone(),t},_minBufferSize:0}),p=(o.Hasher=u.extend({cfg:s.extend(),init:function(t){this.cfg=this.cfg.extend(t),this.reset()},reset:function(){u.reset.call(this),this._doReset()},update:function(t){return this._append(t),this._process(),this},finalize:function(t){return t&&this._append(t),this._doFinalize()},blockSize:16,_createHelper:function(r){return function(t,e){return new r.init(e).finalize(t)}},_createHmacHelper:function(r){return function(t,e){return new p.HMAC.init(r,e).finalize(t)}}}),e.algo={});return e}(Math);function mt(t,e,r){return t^e^r}function xt(t,e,r){return t&e|~t&r}function Ht(t,e,r){return(t|~e)^r}function zt(t,e,r){return t&r|e&~r}function At(t,e,r){return t^(e|~r)}function Ct(t,e){return t<<e|t>>>32-e}function Dt(t,e,r,i){var n,o=this._iv;o?(n=o.slice(0),this._iv=void 0):n=this._prevBlock,i.encryptBlock(n,0);for(var s=0;s<r;s++)t[e+s]^=n[s]}function Et(t){if(255==(t>>24&255)){var e=t>>16&255,r=t>>8&255,i=255&t;255===e?(e=0,255===r?(r=0,255===i?i=0:++i):++r):++e,t=0,t+=e<<16,t+=r<<8,t+=i}else t+=1<<24;return t}function Rt(){for(var t=this._X,e=this._C,r=0;r<8;r++)ft[r]=e[r];e[0]=e[0]+1295307597+this._b|0,e[1]=e[1]+3545052371+(e[0]>>>0<ft[0]>>>0?1:0)|0,e[2]=e[2]+886263092+(e[1]>>>0<ft[1]>>>0?1:0)|0,e[3]=e[3]+1295307597+(e[2]>>>0<ft[2]>>>0?1:0)|0,e[4]=e[4]+3545052371+(e[3]>>>0<ft[3]>>>0?1:0)|0,e[5]=e[5]+886263092+(e[4]>>>0<ft[4]>>>0?1:0)|0,e[6]=e[6]+1295307597+(e[5]>>>0<ft[5]>>>0?1:0)|0,e[7]=e[7]+3545052371+(e[6]>>>0<ft[6]>>>0?1:0)|0,this._b=e[7]>>>0<ft[7]>>>0?1:0;for(r=0;r<8;r++){var i=t[r]+e[r],n=65535&i,o=i>>>16,s=((n*n>>>17)+n*o>>>15)+o*o,c=((4294901760&i)*i|0)+((65535&i)*i|0);dt[r]=s^c}t[0]=dt[0]+(dt[7]<<16|dt[7]>>>16)+(dt[6]<<16|dt[6]>>>16)|0,t[1]=dt[1]+(dt[0]<<8|dt[0]>>>24)+dt[7]|0,t[2]=dt[2]+(dt[1]<<16|dt[1]>>>16)+(dt[0]<<16|dt[0]>>>16)|0,t[3]=dt[3]+(dt[2]<<8|dt[2]>>>24)+dt[1]|0,t[4]=dt[4]+(dt[3]<<16|dt[3]>>>16)+(dt[2]<<16|dt[2]>>>16)|0,t[5]=dt[5]+(dt[4]<<8|dt[4]>>>24)+dt[3]|0,t[6]=dt[6]+(dt[5]<<16|dt[5]>>>16)+(dt[4]<<16|dt[4]>>>16)|0,t[7]=dt[7]+(dt[6]<<8|dt[6]>>>24)+dt[5]|0}function Mt(){for(var t=this._X,e=this._C,r=0;r<8;r++)wt[r]=e[r];e[0]=e[0]+1295307597+this._b|0,e[1]=e[1]+3545052371+(e[0]>>>0<wt[0]>>>0?1:0)|0,e[2]=e[2]+886263092+(e[1]>>>0<wt[1]>>>0?1:0)|0,e[3]=e[3]+1295307597+(e[2]>>>0<wt[2]>>>0?1:0)|0,e[4]=e[4]+3545052371+(e[3]>>>0<wt[3]>>>0?1:0)|0,e[5]=e[5]+886263092+(e[4]>>>0<wt[4]>>>0?1:0)|0,e[6]=e[6]+1295307597+(e[5]>>>0<wt[5]>>>0?1:0)|0,e[7]=e[7]+3545052371+(e[6]>>>0<wt[6]>>>0?1:0)|0,this._b=e[7]>>>0<wt[7]>>>0?1:0;for(r=0;r<8;r++){var i=t[r]+e[r],n=65535&i,o=i>>>16,s=((n*n>>>17)+n*o>>>15)+o*o,c=((4294901760&i)*i|0)+((65535&i)*i|0);kt[r]=s^c}t[0]=kt[0]+(kt[7]<<16|kt[7]>>>16)+(kt[6]<<16|kt[6]>>>16)|0,t[1]=kt[1]+(kt[0]<<8|kt[0]>>>24)+kt[7]|0,t[2]=kt[2]+(kt[1]<<16|kt[1]>>>16)+(kt[0]<<16|kt[0]>>>16)|0,t[3]=kt[3]+(kt[2]<<8|kt[2]>>>24)+kt[1]|0,t[4]=kt[4]+(kt[3]<<16|kt[3]>>>16)+(kt[2]<<16|kt[2]>>>16)|0,t[5]=kt[5]+(kt[4]<<8|kt[4]>>>24)+kt[3]|0,t[6]=kt[6]+(kt[5]<<16|kt[5]>>>16)+(kt[4]<<16|kt[4]>>>16)|0,t[7]=kt[7]+(kt[6]<<8|kt[6]>>>24)+kt[5]|0}return h=bt.lib.WordArray,bt.enc.Base64={stringify:function(t){var e=t.words,r=t.sigBytes,i=this._map;t.clamp();for(var n=[],o=0;o<r;o+=3)for(var s=(e[o>>>2]>>>24-o%4*8&255)<<16|(e[o+1>>>2]>>>24-(o+1)%4*8&255)<<8|e[o+2>>>2]>>>24-(o+2)%4*8&255,c=0;c<4&&o+.75*c<r;c++)n.push(i.charAt(s>>>6*(3-c)&63));var a=i.charAt(64);if(a)for(;n.length%4;)n.push(a);return n.join("")},parse:function(t){var e=t.length,r=this._map,i=this._reverseMap;if(!i){i=this._reverseMap=[];for(var n=0;n<r.length;n++)i[r.charCodeAt(n)]=n}var o=r.charAt(64);if(o){var s=t.indexOf(o);-1!==s&&(e=s)}return function(t,e,r){for(var i=[],n=0,o=0;o<e;o++)if(o%4){var s=r[t.charCodeAt(o-1)]<<o%4*2,c=r[t.charCodeAt(o)]>>>6-o%4*2,a=s|c;i[n>>>2]|=a<<24-n%4*8,n++}return h.create(i,n)}(t,e,i)},_map:"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="},function(l){var t=bt,e=t.lib,r=e.WordArray,i=e.Hasher,n=t.algo,H=[];!function(){for(var t=0;t<64;t++)H[t]=4294967296*l.abs(l.sin(t+1))|0}();var o=n.MD5=i.extend({_doReset:function(){this._hash=new r.init([1732584193,4023233417,2562383102,271733878])},_doProcessBlock:function(t,e){for(var r=0;r<16;r++){var i=e+r,n=t[i];t[i]=16711935&(n<<8|n>>>24)|4278255360&(n<<24|n>>>8)}var o=this._hash.words,s=t[e+0],c=t[e+1],a=t[e+2],h=t[e+3],l=t[e+4],f=t[e+5],d=t[e+6],u=t[e+7],p=t[e+8],_=t[e+9],v=t[e+10],y=t[e+11],g=t[e+12],B=t[e+13],w=t[e+14],k=t[e+15],S=o[0],m=o[1],x=o[2],b=o[3];S=z(S,m,x,b,s,7,H[0]),b=z(b,S,m,x,c,12,H[1]),x=z(x,b,S,m,a,17,H[2]),m=z(m,x,b,S,h,22,H[3]),S=z(S,m,x,b,l,7,H[4]),b=z(b,S,m,x,f,12,H[5]),x=z(x,b,S,m,d,17,H[6]),m=z(m,x,b,S,u,22,H[7]),S=z(S,m,x,b,p,7,H[8]),b=z(b,S,m,x,_,12,H[9]),x=z(x,b,S,m,v,17,H[10]),m=z(m,x,b,S,y,22,H[11]),S=z(S,m,x,b,g,7,H[12]),b=z(b,S,m,x,B,12,H[13]),x=z(x,b,S,m,w,17,H[14]),S=A(S,m=z(m,x,b,S,k,22,H[15]),x,b,c,5,H[16]),b=A(b,S,m,x,d,9,H[17]),x=A(x,b,S,m,y,14,H[18]),m=A(m,x,b,S,s,20,H[19]),S=A(S,m,x,b,f,5,H[20]),b=A(b,S,m,x,v,9,H[21]),x=A(x,b,S,m,k,14,H[22]),m=A(m,x,b,S,l,20,H[23]),S=A(S,m,x,b,_,5,H[24]),b=A(b,S,m,x,w,9,H[25]),x=A(x,b,S,m,h,14,H[26]),m=A(m,x,b,S,p,20,H[27]),S=A(S,m,x,b,B,5,H[28]),b=A(b,S,m,x,a,9,H[29]),x=A(x,b,S,m,u,14,H[30]),S=C(S,m=A(m,x,b,S,g,20,H[31]),x,b,f,4,H[32]),b=C(b,S,m,x,p,11,H[33]),x=C(x,b,S,m,y,16,H[34]),m=C(m,x,b,S,w,23,H[35]),S=C(S,m,x,b,c,4,H[36]),b=C(b,S,m,x,l,11,H[37]),x=C(x,b,S,m,u,16,H[38]),m=C(m,x,b,S,v,23,H[39]),S=C(S,m,x,b,B,4,H[40]),b=C(b,S,m,x,s,11,H[41]),x=C(x,b,S,m,h,16,H[42]),m=C(m,x,b,S,d,23,H[43]),S=C(S,m,x,b,_,4,H[44]),b=C(b,S,m,x,g,11,H[45]),x=C(x,b,S,m,k,16,H[46]),S=D(S,m=C(m,x,b,S,a,23,H[47]),x,b,s,6,H[48]),b=D(b,S,m,x,u,10,H[49]),x=D(x,b,S,m,w,15,H[50]),m=D(m,x,b,S,f,21,H[51]),S=D(S,m,x,b,g,6,H[52]),b=D(b,S,m,x,h,10,H[53]),x=D(x,b,S,m,v,15,H[54]),m=D(m,x,b,S,c,21,H[55]),S=D(S,m,x,b,p,6,H[56]),b=D(b,S,m,x,k,10,H[57]),x=D(x,b,S,m,d,15,H[58]),m=D(m,x,b,S,B,21,H[59]),S=D(S,m,x,b,l,6,H[60]),b=D(b,S,m,x,y,10,H[61]),x=D(x,b,S,m,a,15,H[62]),m=D(m,x,b,S,_,21,H[63]),o[0]=o[0]+S|0,o[1]=o[1]+m|0,o[2]=o[2]+x|0,o[3]=o[3]+b|0},_doFinalize:function(){var t=this._data,e=t.words,r=8*this._nDataBytes,i=8*t.sigBytes;e[i>>>5]|=128<<24-i%32;var n=l.floor(r/4294967296),o=r;e[15+(64+i>>>9<<4)]=16711935&(n<<8|n>>>24)|4278255360&(n<<24|n>>>8),e[14+(64+i>>>9<<4)]=16711935&(o<<8|o>>>24)|4278255360&(o<<24|o>>>8),t.sigBytes=4*(e.length+1),this._process();for(var s=this._hash,c=s.words,a=0;a<4;a++){var h=c[a];c[a]=16711935&(h<<8|h>>>24)|4278255360&(h<<24|h>>>8)}return s},clone:function(){var t=i.clone.call(this);return t._hash=this._hash.clone(),t}});function z(t,e,r,i,n,o,s){var c=t+(e&r|~e&i)+n+s;return(c<<o|c>>>32-o)+e}function A(t,e,r,i,n,o,s){var c=t+(e&i|r&~i)+n+s;return(c<<o|c>>>32-o)+e}function C(t,e,r,i,n,o,s){var c=t+(e^r^i)+n+s;return(c<<o|c>>>32-o)+e}function D(t,e,r,i,n,o,s){var c=t+(r^(e|~i))+n+s;return(c<<o|c>>>32-o)+e}t.MD5=i._createHelper(o),t.HmacMD5=i._createHmacHelper(o)}(Math),e=(t=bt).lib,r=e.WordArray,i=e.Hasher,n=t.algo,f=[],o=n.SHA1=i.extend({_doReset:function(){this._hash=new r.init([1732584193,4023233417,2562383102,271733878,3285377520])},_doProcessBlock:function(t,e){for(var r=this._hash.words,i=r[0],n=r[1],o=r[2],s=r[3],c=r[4],a=0;a<80;a++){if(a<16)f[a]=0|t[e+a];else{var h=f[a-3]^f[a-8]^f[a-14]^f[a-16];f[a]=h<<1|h>>>31}var l=(i<<5|i>>>27)+c+f[a];l+=a<20?1518500249+(n&o|~n&s):a<40?1859775393+(n^o^s):a<60?(n&o|n&s|o&s)-1894007588:(n^o^s)-899497514,c=s,s=o,o=n<<30|n>>>2,n=i,i=l}r[0]=r[0]+i|0,r[1]=r[1]+n|0,r[2]=r[2]+o|0,r[3]=r[3]+s|0,r[4]=r[4]+c|0},_doFinalize:function(){var t=this._data,e=t.words,r=8*this._nDataBytes,i=8*t.sigBytes;return e[i>>>5]|=128<<24-i%32,e[14+(64+i>>>9<<4)]=Math.floor(r/4294967296),e[15+(64+i>>>9<<4)]=r,t.sigBytes=4*e.length,this._process(),this._hash},clone:function(){var t=i.clone.call(this);return t._hash=this._hash.clone(),t}}),t.SHA1=i._createHelper(o),t.HmacSHA1=i._createHmacHelper(o),function(n){var t=bt,e=t.lib,r=e.WordArray,i=e.Hasher,o=t.algo,s=[],B=[];!function(){function t(t){for(var e=n.sqrt(t),r=2;r<=e;r++)if(!(t%r))return;return 1}function e(t){return 4294967296*(t-(0|t))|0}for(var r=2,i=0;i<64;)t(r)&&(i<8&&(s[i]=e(n.pow(r,.5))),B[i]=e(n.pow(r,1/3)),i++),r++}();var w=[],c=o.SHA256=i.extend({_doReset:function(){this._hash=new r.init(s.slice(0))},_doProcessBlock:function(t,e){for(var r=this._hash.words,i=r[0],n=r[1],o=r[2],s=r[3],c=r[4],a=r[5],h=r[6],l=r[7],f=0;f<64;f++){if(f<16)w[f]=0|t[e+f];else{var d=w[f-15],u=(d<<25|d>>>7)^(d<<14|d>>>18)^d>>>3,p=w[f-2],_=(p<<15|p>>>17)^(p<<13|p>>>19)^p>>>10;w[f]=u+w[f-7]+_+w[f-16]}var v=i&n^i&o^n&o,y=(i<<30|i>>>2)^(i<<19|i>>>13)^(i<<10|i>>>22),g=l+((c<<26|c>>>6)^(c<<21|c>>>11)^(c<<7|c>>>25))+(c&a^~c&h)+B[f]+w[f];l=h,h=a,a=c,c=s+g|0,s=o,o=n,n=i,i=g+(y+v)|0}r[0]=r[0]+i|0,r[1]=r[1]+n|0,r[2]=r[2]+o|0,r[3]=r[3]+s|0,r[4]=r[4]+c|0,r[5]=r[5]+a|0,r[6]=r[6]+h|0,r[7]=r[7]+l|0},_doFinalize:function(){var t=this._data,e=t.words,r=8*this._nDataBytes,i=8*t.sigBytes;return e[i>>>5]|=128<<24-i%32,e[14+(64+i>>>9<<4)]=n.floor(r/4294967296),e[15+(64+i>>>9<<4)]=r,t.sigBytes=4*e.length,this._process(),this._hash},clone:function(){var t=i.clone.call(this);return t._hash=this._hash.clone(),t}});t.SHA256=i._createHelper(c),t.HmacSHA256=i._createHmacHelper(c)}(Math),function(){var n=bt.lib.WordArray,t=bt.enc;t.Utf16=t.Utf16BE={stringify:function(t){for(var e=t.words,r=t.sigBytes,i=[],n=0;n<r;n+=2){var o=e[n>>>2]>>>16-n%4*8&65535;i.push(String.fromCharCode(o))}return i.join("")},parse:function(t){for(var e=t.length,r=[],i=0;i<e;i++)r[i>>>1]|=t.charCodeAt(i)<<16-i%2*16;return n.create(r,2*e)}};function s(t){return t<<8&4278255360|t>>>8&16711935}t.Utf16LE={stringify:function(t){for(var e=t.words,r=t.sigBytes,i=[],n=0;n<r;n+=2){var o=s(e[n>>>2]>>>16-n%4*8&65535);i.push(String.fromCharCode(o))}return i.join("")},parse:function(t){for(var e=t.length,r=[],i=0;i<e;i++)r[i>>>1]|=s(t.charCodeAt(i)<<16-i%2*16);return n.create(r,2*e)}}}(),function(){if("function"==typeof ArrayBuffer){var t=bt.lib.WordArray,n=t.init;(t.init=function(t){if(t instanceof ArrayBuffer&&(t=new Uint8Array(t)),(t instanceof Int8Array||"undefined"!=typeof Uint8ClampedArray&&t instanceof Uint8ClampedArray||t instanceof Int16Array||t instanceof Uint16Array||t instanceof Int32Array||t instanceof Uint32Array||t instanceof Float32Array||t instanceof Float64Array)&&(t=new Uint8Array(t.buffer,t.byteOffset,t.byteLength)),t instanceof Uint8Array){for(var e=t.byteLength,r=[],i=0;i<e;i++)r[i>>>2]|=t[i]<<24-i%4*8;n.call(this,r,e)}else n.apply(this,arguments)}).prototype=t}}(),Math,c=(s=bt).lib,a=c.WordArray,l=c.Hasher,d=s.algo,m=a.create([0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,7,4,13,1,10,6,15,3,12,0,9,5,2,14,11,8,3,10,14,4,9,15,8,1,2,7,0,6,13,11,5,12,1,9,11,10,0,8,12,4,13,3,7,15,14,5,6,2,4,0,5,9,7,12,2,10,14,1,3,8,11,6,15,13]),x=a.create([5,14,7,0,9,2,11,4,13,6,15,8,1,10,3,12,6,11,3,7,0,13,5,10,14,15,8,12,4,9,1,2,15,5,1,3,7,14,6,9,11,8,12,2,10,0,4,13,8,6,4,1,3,11,15,0,5,12,2,13,9,7,10,14,12,15,10,4,1,5,8,7,6,2,13,14,0,3,9,11]),b=a.create([11,14,15,12,5,8,7,9,11,13,14,15,6,7,9,8,7,6,8,13,11,9,7,15,7,12,15,9,11,7,13,12,11,13,6,7,14,9,13,15,14,8,13,6,5,12,7,5,11,12,14,15,14,15,9,8,9,14,5,6,8,6,5,12,9,15,5,11,6,8,13,12,5,12,13,14,11,8,5,6]),H=a.create([8,9,9,11,13,15,15,5,7,7,8,11,14,14,12,6,9,13,15,7,12,8,9,11,7,7,12,7,6,15,13,11,9,7,15,11,8,6,6,14,12,13,5,14,13,13,7,5,15,5,8,11,14,14,6,14,6,9,12,9,12,5,15,8,8,5,12,9,12,5,14,6,8,13,6,5,15,13,11,11]),z=a.create([0,1518500249,1859775393,2400959708,2840853838]),A=a.create([1352829926,1548603684,1836072691,2053994217,0]),u=d.RIPEMD160=l.extend({_doReset:function(){this._hash=a.create([1732584193,4023233417,2562383102,271733878,3285377520])},_doProcessBlock:function(t,e){for(var r=0;r<16;r++){var i=e+r,n=t[i];t[i]=16711935&(n<<8|n>>>24)|4278255360&(n<<24|n>>>8)}var o,s,c,a,h,l,f,d,u,p,_,v=this._hash.words,y=z.words,g=A.words,B=m.words,w=x.words,k=b.words,S=H.words;l=o=v[0],f=s=v[1],d=c=v[2],u=a=v[3],p=h=v[4];for(r=0;r<80;r+=1)_=o+t[e+B[r]]|0,_+=r<16?mt(s,c,a)+y[0]:r<32?xt(s,c,a)+y[1]:r<48?Ht(s,c,a)+y[2]:r<64?zt(s,c,a)+y[3]:At(s,c,a)+y[4],_=(_=Ct(_|=0,k[r]))+h|0,o=h,h=a,a=Ct(c,10),c=s,s=_,_=l+t[e+w[r]]|0,_+=r<16?At(f,d,u)+g[0]:r<32?zt(f,d,u)+g[1]:r<48?Ht(f,d,u)+g[2]:r<64?xt(f,d,u)+g[3]:mt(f,d,u)+g[4],_=(_=Ct(_|=0,S[r]))+p|0,l=p,p=u,u=Ct(d,10),d=f,f=_;_=v[1]+c+u|0,v[1]=v[2]+a+p|0,v[2]=v[3]+h+l|0,v[3]=v[4]+o+f|0,v[4]=v[0]+s+d|0,v[0]=_},_doFinalize:function(){var t=this._data,e=t.words,r=8*this._nDataBytes,i=8*t.sigBytes;e[i>>>5]|=128<<24-i%32,e[14+(64+i>>>9<<4)]=16711935&(r<<8|r>>>24)|4278255360&(r<<24|r>>>8),t.sigBytes=4*(e.length+1),this._process();for(var n=this._hash,o=n.words,s=0;s<5;s++){var c=o[s];o[s]=16711935&(c<<8|c>>>24)|4278255360&(c<<24|c>>>8)}return n},clone:function(){var t=l.clone.call(this);return t._hash=this._hash.clone(),t}}),s.RIPEMD160=l._createHelper(u),s.HmacRIPEMD160=l._createHmacHelper(u),p=bt.lib.Base,_=bt.enc.Utf8,bt.algo.HMAC=p.extend({init:function(t,e){t=this._hasher=new t.init,"string"==typeof e&&(e=_.parse(e));var r=t.blockSize,i=4*r;e.sigBytes>i&&(e=t.finalize(e)),e.clamp();for(var n=this._oKey=e.clone(),o=this._iKey=e.clone(),s=n.words,c=o.words,a=0;a<r;a++)s[a]^=1549556828,c[a]^=909522486;n.sigBytes=o.sigBytes=i,this.reset()},reset:function(){var t=this._hasher;t.reset(),t.update(this._iKey)},update:function(t){return this._hasher.update(t),this},finalize:function(t){var e=this._hasher,r=e.finalize(t);return e.reset(),e.finalize(this._oKey.clone().concat(r))}}),y=(v=bt).lib,g=y.Base,B=y.WordArray,w=v.algo,k=w.SHA1,S=w.HMAC,C=w.PBKDF2=g.extend({cfg:g.extend({keySize:4,hasher:k,iterations:1}),init:function(t){this.cfg=this.cfg.extend(t)},compute:function(t,e){for(var r=this.cfg,i=S.create(r.hasher,t),n=B.create(),o=B.create([1]),s=n.words,c=o.words,a=r.keySize,h=r.iterations;s.length<a;){var l=i.update(e).finalize(o);i.reset();for(var f=l.words,d=f.length,u=l,p=1;p<h;p++){u=i.finalize(u),i.reset();for(var _=u.words,v=0;v<d;v++)f[v]^=_[v]}n.concat(l),c[0]++}return n.sigBytes=4*a,n}}),v.PBKDF2=function(t,e,r){return C.create(r).compute(t,e)},E=(D=bt).lib,R=E.Base,M=E.WordArray,F=D.algo,P=F.MD5,W=F.EvpKDF=R.extend({cfg:R.extend({keySize:4,hasher:P,iterations:1}),init:function(t){this.cfg=this.cfg.extend(t)},compute:function(t,e){for(var r,i=this.cfg,n=i.hasher.create(),o=M.create(),s=o.words,c=i.keySize,a=i.iterations;s.length<c;){r&&n.update(r),r=n.update(t).finalize(e),n.reset();for(var h=1;h<a;h++)r=n.finalize(r),n.reset();o.concat(r)}return o.sigBytes=4*c,o}}),D.EvpKDF=function(t,e,r){return W.create(r).compute(t,e)},I=(O=bt).lib.WordArray,U=O.algo,K=U.SHA256,X=U.SHA224=K.extend({_doReset:function(){this._hash=new I.init([3238371032,914150663,812702999,4144912697,4290775857,1750603025,1694076839,3204075428])},_doFinalize:function(){var t=K._doFinalize.call(this);return t.sigBytes-=4,t}}),O.SHA224=K._createHelper(X),O.HmacSHA224=K._createHmacHelper(X),L=bt.lib,j=L.Base,N=L.WordArray,(T=bt.x64={}).Word=j.extend({init:function(t,e){this.high=t,this.low=e}}),T.WordArray=j.extend({init:function(t,e){t=this.words=t||[],this.sigBytes=null!=e?e:8*t.length},toX32:function(){for(var t=this.words,e=t.length,r=[],i=0;i<e;i++){var n=t[i];r.push(n.high),r.push(n.low)}return N.create(r,this.sigBytes)},clone:function(){for(var t=j.clone.call(this),e=t.words=this.words.slice(0),r=e.length,i=0;i<r;i++)e[i]=e[i].clone();return t}}),function(d){var t=bt,e=t.lib,u=e.WordArray,i=e.Hasher,l=t.x64.Word,r=t.algo,C=[],D=[],E=[];!function(){for(var t=1,e=0,r=0;r<24;r++){C[t+5*e]=(r+1)*(r+2)/2%64;var i=(2*t+3*e)%5;t=e%5,e=i}for(t=0;t<5;t++)for(e=0;e<5;e++)D[t+5*e]=e+(2*t+3*e)%5*5;for(var n=1,o=0;o<24;o++){for(var s=0,c=0,a=0;a<7;a++){if(1&n){var h=(1<<a)-1;h<32?c^=1<<h:s^=1<<h-32}128&n?n=n<<1^113:n<<=1}E[o]=l.create(s,c)}}();var R=[];!function(){for(var t=0;t<25;t++)R[t]=l.create()}();var n=r.SHA3=i.extend({cfg:i.cfg.extend({outputLength:512}),_doReset:function(){for(var t=this._state=[],e=0;e<25;e++)t[e]=new l.init;this.blockSize=(1600-2*this.cfg.outputLength)/32},_doProcessBlock:function(t,e){for(var r=this._state,i=this.blockSize/2,n=0;n<i;n++){var o=t[e+2*n],s=t[e+2*n+1];o=16711935&(o<<8|o>>>24)|4278255360&(o<<24|o>>>8),s=16711935&(s<<8|s>>>24)|4278255360&(s<<24|s>>>8),(x=r[n]).high^=s,x.low^=o}for(var c=0;c<24;c++){for(var a=0;a<5;a++){for(var h=0,l=0,f=0;f<5;f++){h^=(x=r[a+5*f]).high,l^=x.low}var d=R[a];d.high=h,d.low=l}for(a=0;a<5;a++){var u=R[(a+4)%5],p=R[(a+1)%5],_=p.high,v=p.low;for(h=u.high^(_<<1|v>>>31),l=u.low^(v<<1|_>>>31),f=0;f<5;f++){(x=r[a+5*f]).high^=h,x.low^=l}}for(var y=1;y<25;y++){var g=(x=r[y]).high,B=x.low,w=C[y];l=w<32?(h=g<<w|B>>>32-w,B<<w|g>>>32-w):(h=B<<w-32|g>>>64-w,g<<w-32|B>>>64-w);var k=R[D[y]];k.high=h,k.low=l}var S=R[0],m=r[0];S.high=m.high,S.low=m.low;for(a=0;a<5;a++)for(f=0;f<5;f++){var x=r[y=a+5*f],b=R[y],H=R[(a+1)%5+5*f],z=R[(a+2)%5+5*f];x.high=b.high^~H.high&z.high,x.low=b.low^~H.low&z.low}x=r[0];var A=E[c];x.high^=A.high,x.low^=A.low}},_doFinalize:function(){var t=this._data,e=t.words,r=(this._nDataBytes,8*t.sigBytes),i=32*this.blockSize;e[r>>>5]|=1<<24-r%32,e[(d.ceil((1+r)/i)*i>>>5)-1]|=128,t.sigBytes=4*e.length,this._process();for(var n=this._state,o=this.cfg.outputLength/8,s=o/8,c=[],a=0;a<s;a++){var h=n[a],l=h.high,f=h.low;l=16711935&(l<<8|l>>>24)|4278255360&(l<<24|l>>>8),f=16711935&(f<<8|f>>>24)|4278255360&(f<<24|f>>>8),c.push(f),c.push(l)}return new u.init(c,o)},clone:function(){for(var t=i.clone.call(this),e=t._state=this._state.slice(0),r=0;r<25;r++)e[r]=e[r].clone();return t}});t.SHA3=i._createHelper(n),t.HmacSHA3=i._createHmacHelper(n)}(Math),function(){var t=bt,e=t.lib.Hasher,r=t.x64,i=r.Word,n=r.WordArray,o=t.algo;function s(){return i.create.apply(i,arguments)}var mt=[s(1116352408,3609767458),s(1899447441,602891725),s(3049323471,3964484399),s(3921009573,2173295548),s(961987163,4081628472),s(1508970993,3053834265),s(2453635748,2937671579),s(2870763221,3664609560),s(3624381080,2734883394),s(310598401,1164996542),s(607225278,1323610764),s(1426881987,3590304994),s(1925078388,4068182383),s(2162078206,991336113),s(2614888103,633803317),s(3248222580,3479774868),s(3835390401,2666613458),s(4022224774,944711139),s(264347078,2341262773),s(604807628,2007800933),s(770255983,1495990901),s(1249150122,1856431235),s(1555081692,3175218132),s(1996064986,2198950837),s(2554220882,3999719339),s(2821834349,766784016),s(2952996808,2566594879),s(3210313671,3203337956),s(3336571891,1034457026),s(3584528711,2466948901),s(113926993,3758326383),s(338241895,168717936),s(666307205,1188179964),s(773529912,1546045734),s(1294757372,1522805485),s(1396182291,2643833823),s(1695183700,2343527390),s(1986661051,1014477480),s(2177026350,1206759142),s(2456956037,344077627),s(2730485921,1290863460),s(2820302411,3158454273),s(3259730800,3505952657),s(3345764771,106217008),s(3516065817,3606008344),s(3600352804,1432725776),s(4094571909,1467031594),s(275423344,851169720),s(430227734,3100823752),s(506948616,1363258195),s(659060556,3750685593),s(883997877,3785050280),s(958139571,3318307427),s(1322822218,3812723403),s(1537002063,2003034995),s(1747873779,3602036899),s(1955562222,1575990012),s(2024104815,1125592928),s(2227730452,2716904306),s(2361852424,442776044),s(2428436474,593698344),s(2756734187,3733110249),s(3204031479,2999351573),s(3329325298,3815920427),s(3391569614,3928383900),s(3515267271,566280711),s(3940187606,3454069534),s(4118630271,4000239992),s(116418474,1914138554),s(174292421,2731055270),s(289380356,3203993006),s(460393269,320620315),s(685471733,587496836),s(852142971,1086792851),s(1017036298,365543100),s(1126000580,2618297676),s(1288033470,3409855158),s(1501505948,4234509866),s(1607167915,987167468),s(1816402316,1246189591)],xt=[];!function(){for(var t=0;t<80;t++)xt[t]=s()}();var c=o.SHA512=e.extend({_doReset:function(){this._hash=new n.init([new i.init(1779033703,4089235720),new i.init(3144134277,2227873595),new i.init(1013904242,4271175723),new i.init(2773480762,1595750129),new i.init(1359893119,2917565137),new i.init(2600822924,725511199),new i.init(528734635,4215389547),new i.init(1541459225,327033209)])},_doProcessBlock:function(t,e){for(var r=this._hash.words,i=r[0],n=r[1],o=r[2],s=r[3],c=r[4],a=r[5],h=r[6],l=r[7],f=i.high,d=i.low,u=n.high,p=n.low,_=o.high,v=o.low,y=s.high,g=s.low,B=c.high,w=c.low,k=a.high,S=a.low,m=h.high,x=h.low,b=l.high,H=l.low,z=f,A=d,C=u,D=p,E=_,R=v,M=y,F=g,P=B,W=w,O=k,I=S,U=m,K=x,X=b,L=H,j=0;j<80;j++){var N,T,q=xt[j];if(j<16)T=q.high=0|t[e+2*j],N=q.low=0|t[e+2*j+1];else{var Z=xt[j-15],V=Z.high,G=Z.low,J=(V>>>1|G<<31)^(V>>>8|G<<24)^V>>>7,$=(G>>>1|V<<31)^(G>>>8|V<<24)^(G>>>7|V<<25),Q=xt[j-2],Y=Q.high,tt=Q.low,et=(Y>>>19|tt<<13)^(Y<<3|tt>>>29)^Y>>>6,rt=(tt>>>19|Y<<13)^(tt<<3|Y>>>29)^(tt>>>6|Y<<26),it=xt[j-7],nt=it.high,ot=it.low,st=xt[j-16],ct=st.high,at=st.low;T=(T=(T=J+nt+((N=$+ot)>>>0<$>>>0?1:0))+et+((N+=rt)>>>0<rt>>>0?1:0))+ct+((N+=at)>>>0<at>>>0?1:0),q.high=T,q.low=N}var ht,lt=P&O^~P&U,ft=W&I^~W&K,dt=z&C^z&E^C&E,ut=A&D^A&R^D&R,pt=(z>>>28|A<<4)^(z<<30|A>>>2)^(z<<25|A>>>7),_t=(A>>>28|z<<4)^(A<<30|z>>>2)^(A<<25|z>>>7),vt=(P>>>14|W<<18)^(P>>>18|W<<14)^(P<<23|W>>>9),yt=(W>>>14|P<<18)^(W>>>18|P<<14)^(W<<23|P>>>9),gt=mt[j],Bt=gt.high,wt=gt.low,kt=X+vt+((ht=L+yt)>>>0<L>>>0?1:0),St=_t+ut;X=U,L=K,U=O,K=I,O=P,I=W,P=M+(kt=(kt=(kt=kt+lt+((ht=ht+ft)>>>0<ft>>>0?1:0))+Bt+((ht=ht+wt)>>>0<wt>>>0?1:0))+T+((ht=ht+N)>>>0<N>>>0?1:0))+((W=F+ht|0)>>>0<F>>>0?1:0)|0,M=E,F=R,E=C,R=D,C=z,D=A,z=kt+(pt+dt+(St>>>0<_t>>>0?1:0))+((A=ht+St|0)>>>0<ht>>>0?1:0)|0}d=i.low=d+A,i.high=f+z+(d>>>0<A>>>0?1:0),p=n.low=p+D,n.high=u+C+(p>>>0<D>>>0?1:0),v=o.low=v+R,o.high=_+E+(v>>>0<R>>>0?1:0),g=s.low=g+F,s.high=y+M+(g>>>0<F>>>0?1:0),w=c.low=w+W,c.high=B+P+(w>>>0<W>>>0?1:0),S=a.low=S+I,a.high=k+O+(S>>>0<I>>>0?1:0),x=h.low=x+K,h.high=m+U+(x>>>0<K>>>0?1:0),H=l.low=H+L,l.high=b+X+(H>>>0<L>>>0?1:0)},_doFinalize:function(){var t=this._data,e=t.words,r=8*this._nDataBytes,i=8*t.sigBytes;return e[i>>>5]|=128<<24-i%32,e[30+(128+i>>>10<<5)]=Math.floor(r/4294967296),e[31+(128+i>>>10<<5)]=r,t.sigBytes=4*e.length,this._process(),this._hash.toX32()},clone:function(){var t=e.clone.call(this);return t._hash=this._hash.clone(),t},blockSize:32});t.SHA512=e._createHelper(c),t.HmacSHA512=e._createHmacHelper(c)}(),Z=(q=bt).x64,V=Z.Word,G=Z.WordArray,J=q.algo,$=J.SHA512,Q=J.SHA384=$.extend({_doReset:function(){this._hash=new G.init([new V.init(3418070365,3238371032),new V.init(1654270250,914150663),new V.init(2438529370,812702999),new V.init(355462360,4144912697),new V.init(1731405415,4290775857),new V.init(2394180231,1750603025),new V.init(3675008525,1694076839),new V.init(1203062813,3204075428)])},_doFinalize:function(){var t=$._doFinalize.call(this);return t.sigBytes-=16,t}}),q.SHA384=$._createHelper(Q),q.HmacSHA384=$._createHmacHelper(Q),bt.lib.Cipher||function(){var t=bt,e=t.lib,r=e.Base,a=e.WordArray,i=e.BufferedBlockAlgorithm,n=t.enc,o=(n.Utf8,n.Base64),s=t.algo.EvpKDF,c=e.Cipher=i.extend({cfg:r.extend(),createEncryptor:function(t,e){return this.create(this._ENC_XFORM_MODE,t,e)},createDecryptor:function(t,e){return this.create(this._DEC_XFORM_MODE,t,e)},init:function(t,e,r){this.cfg=this.cfg.extend(r),this._xformMode=t,this._key=e,this.reset()},reset:function(){i.reset.call(this),this._doReset()},process:function(t){return this._append(t),this._process()},finalize:function(t){return t&&this._append(t),this._doFinalize()},keySize:4,ivSize:4,_ENC_XFORM_MODE:1,_DEC_XFORM_MODE:2,_createHelper:function(i){return{encrypt:function(t,e,r){return h(e).encrypt(i,t,e,r)},decrypt:function(t,e,r){return h(e).decrypt(i,t,e,r)}}}});function h(t){return"string"==typeof t?w:g}e.StreamCipher=c.extend({_doFinalize:function(){return this._process(!0)},blockSize:1});var l,f=t.mode={},d=e.BlockCipherMode=r.extend({createEncryptor:function(t,e){return this.Encryptor.create(t,e)},createDecryptor:function(t,e){return this.Decryptor.create(t,e)},init:function(t,e){this._cipher=t,this._iv=e}}),u=f.CBC=((l=d.extend()).Encryptor=l.extend({processBlock:function(t,e){var r=this._cipher,i=r.blockSize;p.call(this,t,e,i),r.encryptBlock(t,e),this._prevBlock=t.slice(e,e+i)}}),l.Decryptor=l.extend({processBlock:function(t,e){var r=this._cipher,i=r.blockSize,n=t.slice(e,e+i);r.decryptBlock(t,e),p.call(this,t,e,i),this._prevBlock=n}}),l);function p(t,e,r){var i,n=this._iv;n?(i=n,this._iv=void 0):i=this._prevBlock;for(var o=0;o<r;o++)t[e+o]^=i[o]}var _=(t.pad={}).Pkcs7={pad:function(t,e){for(var r=4*e,i=r-t.sigBytes%r,n=i<<24|i<<16|i<<8|i,o=[],s=0;s<i;s+=4)o.push(n);var c=a.create(o,i);t.concat(c)},unpad:function(t){var e=255&t.words[t.sigBytes-1>>>2];t.sigBytes-=e}},v=(e.BlockCipher=c.extend({cfg:c.cfg.extend({mode:u,padding:_}),reset:function(){var t;c.reset.call(this);var e=this.cfg,r=e.iv,i=e.mode;this._xformMode==this._ENC_XFORM_MODE?t=i.createEncryptor:(t=i.createDecryptor,this._minBufferSize=1),this._mode&&this._mode.__creator==t?this._mode.init(this,r&&r.words):(this._mode=t.call(i,this,r&&r.words),this._mode.__creator=t)},_doProcessBlock:function(t,e){this._mode.processBlock(t,e)},_doFinalize:function(){var t,e=this.cfg.padding;return this._xformMode==this._ENC_XFORM_MODE?(e.pad(this._data,this.blockSize),t=this._process(!0)):(t=this._process(!0),e.unpad(t)),t},blockSize:4}),e.CipherParams=r.extend({init:function(t){this.mixIn(t)},toString:function(t){return(t||this.formatter).stringify(this)}})),y=(t.format={}).OpenSSL={stringify:function(t){var e=t.ciphertext,r=t.salt;return(r?a.create([1398893684,1701076831]).concat(r).concat(e):e).toString(o)},parse:function(t){var e,r=o.parse(t),i=r.words;return 1398893684==i[0]&&1701076831==i[1]&&(e=a.create(i.slice(2,4)),i.splice(0,4),r.sigBytes-=16),v.create({ciphertext:r,salt:e})}},g=e.SerializableCipher=r.extend({cfg:r.extend({format:y}),encrypt:function(t,e,r,i){i=this.cfg.extend(i);var n=t.createEncryptor(r,i),o=n.finalize(e),s=n.cfg;return v.create({ciphertext:o,key:r,iv:s.iv,algorithm:t,mode:s.mode,padding:s.padding,blockSize:t.blockSize,formatter:i.format})},decrypt:function(t,e,r,i){return i=this.cfg.extend(i),e=this._parse(e,i.format),t.createDecryptor(r,i).finalize(e.ciphertext)},_parse:function(t,e){return"string"==typeof t?e.parse(t,this):t}}),B=(t.kdf={}).OpenSSL={execute:function(t,e,r,i){i=i||a.random(8);var n=s.create({keySize:e+r}).compute(t,i),o=a.create(n.words.slice(e),4*r);return n.sigBytes=4*e,v.create({key:n,iv:o,salt:i})}},w=e.PasswordBasedCipher=g.extend({cfg:g.cfg.extend({kdf:B}),encrypt:function(t,e,r,i){var n=(i=this.cfg.extend(i)).kdf.execute(r,t.keySize,t.ivSize);i.iv=n.iv;var o=g.encrypt.call(this,t,e,n.key,i);return o.mixIn(n),o},decrypt:function(t,e,r,i){i=this.cfg.extend(i),e=this._parse(e,i.format);var n=i.kdf.execute(r,t.keySize,t.ivSize,e.salt);return i.iv=n.iv,g.decrypt.call(this,t,e,n.key,i)}})}(),bt.mode.CFB=((Y=bt.lib.BlockCipherMode.extend()).Encryptor=Y.extend({processBlock:function(t,e){var r=this._cipher,i=r.blockSize;Dt.call(this,t,e,i,r),this._prevBlock=t.slice(e,e+i)}}),Y.Decryptor=Y.extend({processBlock:function(t,e){var r=this._cipher,i=r.blockSize,n=t.slice(e,e+i);Dt.call(this,t,e,i,r),this._prevBlock=n}}),Y),bt.mode.ECB=((tt=bt.lib.BlockCipherMode.extend()).Encryptor=tt.extend({processBlock:function(t,e){this._cipher.encryptBlock(t,e)}}),tt.Decryptor=tt.extend({processBlock:function(t,e){this._cipher.decryptBlock(t,e)}}),tt),bt.pad.AnsiX923={pad:function(t,e){var r=t.sigBytes,i=4*e,n=i-r%i,o=r+n-1;t.clamp(),t.words[o>>>2]|=n<<24-o%4*8,t.sigBytes+=n},unpad:function(t){var e=255&t.words[t.sigBytes-1>>>2];t.sigBytes-=e}},bt.pad.Iso10126={pad:function(t,e){var r=4*e,i=r-t.sigBytes%r;t.concat(bt.lib.WordArray.random(i-1)).concat(bt.lib.WordArray.create([i<<24],1))},unpad:function(t){var e=255&t.words[t.sigBytes-1>>>2];t.sigBytes-=e}},bt.pad.Iso97971={pad:function(t,e){t.concat(bt.lib.WordArray.create([2147483648],1)),bt.pad.ZeroPadding.pad(t,e)},unpad:function(t){bt.pad.ZeroPadding.unpad(t),t.sigBytes--}},bt.mode.OFB=(et=bt.lib.BlockCipherMode.extend(),rt=et.Encryptor=et.extend({processBlock:function(t,e){var r=this._cipher,i=r.blockSize,n=this._iv,o=this._keystream;n&&(o=this._keystream=n.slice(0),this._iv=void 0),r.encryptBlock(o,0);for(var s=0;s<i;s++)t[e+s]^=o[s]}}),et.Decryptor=rt,et),bt.pad.NoPadding={pad:function(){},unpad:function(){}},it=bt.lib.CipherParams,nt=bt.enc.Hex,bt.format.Hex={stringify:function(t){return t.ciphertext.toString(nt)},parse:function(t){var e=nt.parse(t);return it.create({ciphertext:e})}},function(){var t=bt,e=t.lib.BlockCipher,r=t.algo,h=[],l=[],f=[],d=[],u=[],p=[],_=[],v=[],y=[],g=[];!function(){for(var t=[],e=0;e<256;e++)t[e]=e<128?e<<1:e<<1^283;var r=0,i=0;for(e=0;e<256;e++){var n=i^i<<1^i<<2^i<<3^i<<4;n=n>>>8^255&n^99,h[r]=n;var o=t[l[n]=r],s=t[o],c=t[s],a=257*t[n]^16843008*n;f[r]=a<<24|a>>>8,d[r]=a<<16|a>>>16,u[r]=a<<8|a>>>24,p[r]=a;a=16843009*c^65537*s^257*o^16843008*r;_[n]=a<<24|a>>>8,v[n]=a<<16|a>>>16,y[n]=a<<8|a>>>24,g[n]=a,r?(r=o^t[t[t[c^o]]],i^=t[t[i]]):r=i=1}}();var B=[0,1,2,4,8,16,32,64,128,27,54],i=r.AES=e.extend({_doReset:function(){if(!this._nRounds||this._keyPriorReset!==this._key){for(var t=this._keyPriorReset=this._key,e=t.words,r=t.sigBytes/4,i=4*(1+(this._nRounds=6+r)),n=this._keySchedule=[],o=0;o<i;o++)o<r?n[o]=e[o]:(a=n[o-1],o%r?6<r&&o%r==4&&(a=h[a>>>24]<<24|h[a>>>16&255]<<16|h[a>>>8&255]<<8|h[255&a]):(a=h[(a=a<<8|a>>>24)>>>24]<<24|h[a>>>16&255]<<16|h[a>>>8&255]<<8|h[255&a],a^=B[o/r|0]<<24),n[o]=n[o-r]^a);for(var s=this._invKeySchedule=[],c=0;c<i;c++){o=i-c;if(c%4)var a=n[o];else a=n[o-4];s[c]=c<4||o<=4?a:_[h[a>>>24]]^v[h[a>>>16&255]]^y[h[a>>>8&255]]^g[h[255&a]]}}},encryptBlock:function(t,e){this._doCryptBlock(t,e,this._keySchedule,f,d,u,p,h)},decryptBlock:function(t,e){var r=t[e+1];t[e+1]=t[e+3],t[e+3]=r,this._doCryptBlock(t,e,this._invKeySchedule,_,v,y,g,l);r=t[e+1];t[e+1]=t[e+3],t[e+3]=r},_doCryptBlock:function(t,e,r,i,n,o,s,c){for(var a=this._nRounds,h=t[e]^r[0],l=t[e+1]^r[1],f=t[e+2]^r[2],d=t[e+3]^r[3],u=4,p=1;p<a;p++){var _=i[h>>>24]^n[l>>>16&255]^o[f>>>8&255]^s[255&d]^r[u++],v=i[l>>>24]^n[f>>>16&255]^o[d>>>8&255]^s[255&h]^r[u++],y=i[f>>>24]^n[d>>>16&255]^o[h>>>8&255]^s[255&l]^r[u++],g=i[d>>>24]^n[h>>>16&255]^o[l>>>8&255]^s[255&f]^r[u++];h=_,l=v,f=y,d=g}_=(c[h>>>24]<<24|c[l>>>16&255]<<16|c[f>>>8&255]<<8|c[255&d])^r[u++],v=(c[l>>>24]<<24|c[f>>>16&255]<<16|c[d>>>8&255]<<8|c[255&h])^r[u++],y=(c[f>>>24]<<24|c[d>>>16&255]<<16|c[h>>>8&255]<<8|c[255&l])^r[u++],g=(c[d>>>24]<<24|c[h>>>16&255]<<16|c[l>>>8&255]<<8|c[255&f])^r[u++];t[e]=_,t[e+1]=v,t[e+2]=y,t[e+3]=g},keySize:8});t.AES=e._createHelper(i)}(),function(){var t=bt,e=t.lib,n=e.WordArray,r=e.BlockCipher,i=t.algo,h=[57,49,41,33,25,17,9,1,58,50,42,34,26,18,10,2,59,51,43,35,27,19,11,3,60,52,44,36,63,55,47,39,31,23,15,7,62,54,46,38,30,22,14,6,61,53,45,37,29,21,13,5,28,20,12,4],l=[14,17,11,24,1,5,3,28,15,6,21,10,23,19,12,4,26,8,16,7,27,20,13,2,41,52,31,37,47,55,30,40,51,45,33,48,44,49,39,56,34,53,46,42,50,36,29,32],f=[1,2,4,6,8,10,12,14,15,17,19,21,23,25,27,28],d=[{0:8421888,268435456:32768,536870912:8421378,805306368:2,1073741824:512,1342177280:8421890,1610612736:8389122,1879048192:8388608,2147483648:514,2415919104:8389120,2684354560:33280,2952790016:8421376,3221225472:32770,3489660928:8388610,3758096384:0,4026531840:33282,134217728:0,402653184:8421890,671088640:33282,939524096:32768,1207959552:8421888,1476395008:512,1744830464:8421378,2013265920:2,2281701376:8389120,2550136832:33280,2818572288:8421376,3087007744:8389122,3355443200:8388610,3623878656:32770,3892314112:514,4160749568:8388608,1:32768,268435457:2,536870913:8421888,805306369:8388608,1073741825:8421378,1342177281:33280,1610612737:512,1879048193:8389122,2147483649:8421890,2415919105:8421376,2684354561:8388610,2952790017:33282,3221225473:514,3489660929:8389120,3758096385:32770,4026531841:0,134217729:8421890,402653185:8421376,671088641:8388608,939524097:512,1207959553:32768,1476395009:8388610,1744830465:2,2013265921:33282,2281701377:32770,2550136833:8389122,2818572289:514,3087007745:8421888,3355443201:8389120,3623878657:0,3892314113:33280,4160749569:8421378},{0:1074282512,16777216:16384,33554432:524288,50331648:1074266128,67108864:1073741840,83886080:1074282496,100663296:1073758208,117440512:16,134217728:540672,150994944:1073758224,167772160:1073741824,184549376:540688,201326592:524304,218103808:0,234881024:16400,251658240:1074266112,8388608:1073758208,25165824:540688,41943040:16,58720256:1073758224,75497472:1074282512,92274688:1073741824,109051904:524288,125829120:1074266128,142606336:524304,159383552:0,176160768:16384,192937984:1074266112,209715200:1073741840,226492416:540672,243269632:1074282496,260046848:16400,268435456:0,285212672:1074266128,301989888:1073758224,318767104:1074282496,335544320:1074266112,352321536:16,369098752:540688,385875968:16384,402653184:16400,419430400:524288,436207616:524304,452984832:1073741840,469762048:540672,486539264:1073758208,503316480:1073741824,520093696:1074282512,276824064:540688,293601280:524288,310378496:1074266112,327155712:16384,343932928:1073758208,360710144:1074282512,377487360:16,394264576:1073741824,411041792:1074282496,427819008:1073741840,444596224:1073758224,461373440:524304,478150656:0,494927872:16400,511705088:1074266128,528482304:540672},{0:260,1048576:0,2097152:67109120,3145728:65796,4194304:65540,5242880:67108868,6291456:67174660,7340032:67174400,8388608:67108864,9437184:67174656,10485760:65792,11534336:67174404,12582912:67109124,13631488:65536,14680064:4,15728640:256,524288:67174656,1572864:67174404,2621440:0,3670016:67109120,4718592:67108868,5767168:65536,6815744:65540,7864320:260,8912896:4,9961472:256,11010048:67174400,12058624:65796,13107200:65792,14155776:67109124,15204352:67174660,16252928:67108864,16777216:67174656,17825792:65540,18874368:65536,19922944:67109120,20971520:256,22020096:67174660,23068672:67108868,24117248:0,25165824:67109124,26214400:67108864,27262976:4,28311552:65792,29360128:67174400,30408704:260,31457280:65796,32505856:67174404,17301504:67108864,18350080:260,19398656:67174656,20447232:0,21495808:65540,22544384:67109120,23592960:256,24641536:67174404,25690112:65536,26738688:67174660,27787264:65796,28835840:67108868,29884416:67109124,30932992:67174400,31981568:4,33030144:65792},{0:2151682048,65536:2147487808,131072:4198464,196608:2151677952,262144:0,327680:4198400,393216:2147483712,458752:4194368,524288:2147483648,589824:4194304,655360:64,720896:2147487744,786432:2151678016,851968:4160,917504:4096,983040:2151682112,32768:2147487808,98304:64,163840:2151678016,229376:2147487744,294912:4198400,360448:2151682112,425984:0,491520:2151677952,557056:4096,622592:2151682048,688128:4194304,753664:4160,819200:2147483648,884736:4194368,950272:4198464,1015808:2147483712,1048576:4194368,1114112:4198400,1179648:2147483712,1245184:0,1310720:4160,1376256:2151678016,1441792:2151682048,1507328:2147487808,1572864:2151682112,1638400:2147483648,1703936:2151677952,1769472:4198464,1835008:2147487744,1900544:4194304,1966080:64,2031616:4096,1081344:2151677952,1146880:2151682112,1212416:0,1277952:4198400,1343488:4194368,1409024:2147483648,1474560:2147487808,1540096:64,1605632:2147483712,1671168:4096,1736704:2147487744,1802240:2151678016,1867776:4160,1933312:2151682048,1998848:4194304,2064384:4198464},{0:128,4096:17039360,8192:262144,12288:536870912,16384:537133184,20480:16777344,24576:553648256,28672:262272,32768:16777216,36864:537133056,40960:536871040,45056:553910400,49152:553910272,53248:0,57344:17039488,61440:553648128,2048:17039488,6144:553648256,10240:128,14336:17039360,18432:262144,22528:537133184,26624:553910272,30720:536870912,34816:537133056,38912:0,43008:553910400,47104:16777344,51200:536871040,55296:553648128,59392:16777216,63488:262272,65536:262144,69632:128,73728:536870912,77824:553648256,81920:16777344,86016:553910272,90112:537133184,94208:16777216,98304:553910400,102400:553648128,106496:17039360,110592:537133056,114688:262272,118784:536871040,122880:0,126976:17039488,67584:553648256,71680:16777216,75776:17039360,79872:537133184,83968:536870912,88064:17039488,92160:128,96256:553910272,100352:262272,104448:553910400,108544:0,112640:553648128,116736:16777344,120832:262144,124928:537133056,129024:536871040},{0:268435464,256:8192,512:270532608,768:270540808,1024:268443648,1280:2097152,1536:2097160,1792:268435456,2048:0,2304:268443656,2560:2105344,2816:8,3072:270532616,3328:2105352,3584:8200,3840:270540800,128:270532608,384:270540808,640:8,896:2097152,1152:2105352,1408:268435464,1664:268443648,1920:8200,2176:2097160,2432:8192,2688:268443656,2944:270532616,3200:0,3456:270540800,3712:2105344,3968:268435456,4096:268443648,4352:270532616,4608:270540808,4864:8200,5120:2097152,5376:268435456,5632:268435464,5888:2105344,6144:2105352,6400:0,6656:8,6912:270532608,7168:8192,7424:268443656,7680:270540800,7936:2097160,4224:8,4480:2105344,4736:2097152,4992:268435464,5248:268443648,5504:8200,5760:270540808,6016:270532608,6272:270540800,6528:270532616,6784:8192,7040:2105352,7296:2097160,7552:0,7808:268435456,8064:268443656},{0:1048576,16:33555457,32:1024,48:1049601,64:34604033,80:0,96:1,112:34603009,128:33555456,144:1048577,160:33554433,176:34604032,192:34603008,208:1025,224:1049600,240:33554432,8:34603009,24:0,40:33555457,56:34604032,72:1048576,88:33554433,104:33554432,120:1025,136:1049601,152:33555456,168:34603008,184:1048577,200:1024,216:34604033,232:1,248:1049600,256:33554432,272:1048576,288:33555457,304:34603009,320:1048577,336:33555456,352:34604032,368:1049601,384:1025,400:34604033,416:1049600,432:1,448:0,464:34603008,480:33554433,496:1024,264:1049600,280:33555457,296:34603009,312:1,328:33554432,344:1048576,360:1025,376:34604032,392:33554433,408:34603008,424:0,440:34604033,456:1049601,472:1024,488:33555456,504:1048577},{0:134219808,1:131072,2:134217728,3:32,4:131104,5:134350880,6:134350848,7:2048,8:134348800,9:134219776,10:133120,11:134348832,12:2080,13:0,14:134217760,15:133152,2147483648:2048,2147483649:134350880,2147483650:134219808,2147483651:134217728,2147483652:134348800,2147483653:133120,2147483654:133152,2147483655:32,2147483656:134217760,2147483657:2080,2147483658:131104,2147483659:134350848,2147483660:0,2147483661:134348832,2147483662:134219776,2147483663:131072,16:133152,17:134350848,18:32,19:2048,20:134219776,21:134217760,22:134348832,23:131072,24:0,25:131104,26:134348800,27:134219808,28:134350880,29:133120,30:2080,31:134217728,2147483664:131072,2147483665:2048,2147483666:134348832,2147483667:133152,2147483668:32,2147483669:134348800,2147483670:134217728,2147483671:134219808,2147483672:134350880,2147483673:134217760,2147483674:134219776,2147483675:0,2147483676:133120,2147483677:2080,2147483678:131104,2147483679:134350848}],u=[4160749569,528482304,33030144,2064384,129024,8064,504,2147483679],o=i.DES=r.extend({_doReset:function(){for(var t=this._key.words,e=[],r=0;r<56;r++){var i=h[r]-1;e[r]=t[i>>>5]>>>31-i%32&1}for(var n=this._subKeys=[],o=0;o<16;o++){var s=n[o]=[],c=f[o];for(r=0;r<24;r++)s[r/6|0]|=e[(l[r]-1+c)%28]<<31-r%6,s[4+(r/6|0)]|=e[28+(l[r+24]-1+c)%28]<<31-r%6;s[0]=s[0]<<1|s[0]>>>31;for(r=1;r<7;r++)s[r]=s[r]>>>4*(r-1)+3;s[7]=s[7]<<5|s[7]>>>27}var a=this._invSubKeys=[];for(r=0;r<16;r++)a[r]=n[15-r]},encryptBlock:function(t,e){this._doCryptBlock(t,e,this._subKeys)},decryptBlock:function(t,e){this._doCryptBlock(t,e,this._invSubKeys)},_doCryptBlock:function(t,e,r){this._lBlock=t[e],this._rBlock=t[e+1],p.call(this,4,252645135),p.call(this,16,65535),_.call(this,2,858993459),_.call(this,8,16711935),p.call(this,1,1431655765);for(var i=0;i<16;i++){for(var n=r[i],o=this._lBlock,s=this._rBlock,c=0,a=0;a<8;a++)c|=d[a][((s^n[a])&u[a])>>>0];this._lBlock=s,this._rBlock=o^c}var h=this._lBlock;this._lBlock=this._rBlock,this._rBlock=h,p.call(this,1,1431655765),_.call(this,8,16711935),_.call(this,2,858993459),p.call(this,16,65535),p.call(this,4,252645135),t[e]=this._lBlock,t[e+1]=this._rBlock},keySize:2,ivSize:2,blockSize:2});function p(t,e){var r=(this._lBlock>>>t^this._rBlock)&e;this._rBlock^=r,this._lBlock^=r<<t}function _(t,e){var r=(this._rBlock>>>t^this._lBlock)&e;this._lBlock^=r,this._rBlock^=r<<t}t.DES=r._createHelper(o);var s=i.TripleDES=r.extend({_doReset:function(){var t=this._key.words;if(2!==t.length&&4!==t.length&&t.length<6)throw new Error("Invalid key length - 3DES requires the key length to be 64, 128, 192 or >192.");var e=t.slice(0,2),r=t.length<4?t.slice(0,2):t.slice(2,4),i=t.length<6?t.slice(0,2):t.slice(4,6);this._des1=o.createEncryptor(n.create(e)),this._des2=o.createEncryptor(n.create(r)),this._des3=o.createEncryptor(n.create(i))},encryptBlock:function(t,e){this._des1.encryptBlock(t,e),this._des2.decryptBlock(t,e),this._des3.encryptBlock(t,e)},decryptBlock:function(t,e){this._des3.decryptBlock(t,e),this._des2.encryptBlock(t,e),this._des1.decryptBlock(t,e)},keySize:6,ivSize:2,blockSize:2});t.TripleDES=r._createHelper(s)}(),function(){var t=bt,e=t.lib.StreamCipher,r=t.algo,i=r.RC4=e.extend({_doReset:function(){for(var t=this._key,e=t.words,r=t.sigBytes,i=this._S=[],n=0;n<256;n++)i[n]=n;n=0;for(var o=0;n<256;n++){var s=n%r,c=e[s>>>2]>>>24-s%4*8&255;o=(o+i[n]+c)%256;var a=i[n];i[n]=i[o],i[o]=a}this._i=this._j=0},_doProcessBlock:function(t,e){t[e]^=n.call(this)},keySize:8,ivSize:0});function n(){for(var t=this._S,e=this._i,r=this._j,i=0,n=0;n<4;n++){r=(r+t[e=(e+1)%256])%256;var o=t[e];t[e]=t[r],t[r]=o,i|=t[(t[e]+t[r])%256]<<24-8*n}return this._i=e,this._j=r,i}t.RC4=e._createHelper(i);var o=r.RC4Drop=i.extend({cfg:i.cfg.extend({drop:192}),_doReset:function(){i._doReset.call(this);for(var t=this.cfg.drop;0<t;t--)n.call(this)}});t.RC4Drop=e._createHelper(o)}(),bt.mode.CTRGladman=(ot=bt.lib.BlockCipherMode.extend(),st=ot.Encryptor=ot.extend({processBlock:function(t,e){var r,i=this._cipher,n=i.blockSize,o=this._iv,s=this._counter;o&&(s=this._counter=o.slice(0),this._iv=void 0),0===((r=s)[0]=Et(r[0]))&&(r[1]=Et(r[1]));var c=s.slice(0);i.encryptBlock(c,0);for(var a=0;a<n;a++)t[e+a]^=c[a]}}),ot.Decryptor=st,ot),at=(ct=bt).lib.StreamCipher,ht=ct.algo,lt=[],ft=[],dt=[],ut=ht.Rabbit=at.extend({_doReset:function(){for(var t=this._key.words,e=this.cfg.iv,r=0;r<4;r++)t[r]=16711935&(t[r]<<8|t[r]>>>24)|4278255360&(t[r]<<24|t[r]>>>8);var i=this._X=[t[0],t[3]<<16|t[2]>>>16,t[1],t[0]<<16|t[3]>>>16,t[2],t[1]<<16|t[0]>>>16,t[3],t[2]<<16|t[1]>>>16],n=this._C=[t[2]<<16|t[2]>>>16,4294901760&t[0]|65535&t[1],t[3]<<16|t[3]>>>16,4294901760&t[1]|65535&t[2],t[0]<<16|t[0]>>>16,4294901760&t[2]|65535&t[3],t[1]<<16|t[1]>>>16,4294901760&t[3]|65535&t[0]];for(r=this._b=0;r<4;r++)Rt.call(this);for(r=0;r<8;r++)n[r]^=i[r+4&7];if(e){var o=e.words,s=o[0],c=o[1],a=16711935&(s<<8|s>>>24)|4278255360&(s<<24|s>>>8),h=16711935&(c<<8|c>>>24)|4278255360&(c<<24|c>>>8),l=a>>>16|4294901760&h,f=h<<16|65535&a;n[0]^=a,n[1]^=l,n[2]^=h,n[3]^=f,n[4]^=a,n[5]^=l,n[6]^=h,n[7]^=f;for(r=0;r<4;r++)Rt.call(this)}},_doProcessBlock:function(t,e){var r=this._X;Rt.call(this),lt[0]=r[0]^r[5]>>>16^r[3]<<16,lt[1]=r[2]^r[7]>>>16^r[5]<<16,lt[2]=r[4]^r[1]>>>16^r[7]<<16,lt[3]=r[6]^r[3]>>>16^r[1]<<16;for(var i=0;i<4;i++)lt[i]=16711935&(lt[i]<<8|lt[i]>>>24)|4278255360&(lt[i]<<24|lt[i]>>>8),t[e+i]^=lt[i]},blockSize:4,ivSize:2}),ct.Rabbit=at._createHelper(ut),bt.mode.CTR=(pt=bt.lib.BlockCipherMode.extend(),_t=pt.Encryptor=pt.extend({processBlock:function(t,e){var r=this._cipher,i=r.blockSize,n=this._iv,o=this._counter;n&&(o=this._counter=n.slice(0),this._iv=void 0);var s=o.slice(0);r.encryptBlock(s,0),o[i-1]=o[i-1]+1|0;for(var c=0;c<i;c++)t[e+c]^=s[c]}}),pt.Decryptor=_t,pt),yt=(vt=bt).lib.StreamCipher,gt=vt.algo,Bt=[],wt=[],kt=[],St=gt.RabbitLegacy=yt.extend({_doReset:function(){for(var t=this._key.words,e=this.cfg.iv,r=this._X=[t[0],t[3]<<16|t[2]>>>16,t[1],t[0]<<16|t[3]>>>16,t[2],t[1]<<16|t[0]>>>16,t[3],t[2]<<16|t[1]>>>16],i=this._C=[t[2]<<16|t[2]>>>16,4294901760&t[0]|65535&t[1],t[3]<<16|t[3]>>>16,4294901760&t[1]|65535&t[2],t[0]<<16|t[0]>>>16,4294901760&t[2]|65535&t[3],t[1]<<16|t[1]>>>16,4294901760&t[3]|65535&t[0]],n=this._b=0;n<4;n++)Mt.call(this);for(n=0;n<8;n++)i[n]^=r[n+4&7];if(e){var o=e.words,s=o[0],c=o[1],a=16711935&(s<<8|s>>>24)|4278255360&(s<<24|s>>>8),h=16711935&(c<<8|c>>>24)|4278255360&(c<<24|c>>>8),l=a>>>16|4294901760&h,f=h<<16|65535&a;i[0]^=a,i[1]^=l,i[2]^=h,i[3]^=f,i[4]^=a,i[5]^=l,i[6]^=h,i[7]^=f;for(n=0;n<4;n++)Mt.call(this)}},_doProcessBlock:function(t,e){var r=this._X;Mt.call(this),Bt[0]=r[0]^r[5]>>>16^r[3]<<16,Bt[1]=r[2]^r[7]>>>16^r[5]<<16,Bt[2]=r[4]^r[1]>>>16^r[7]<<16,Bt[3]=r[6]^r[3]>>>16^r[1]<<16;for(var i=0;i<4;i++)Bt[i]=16711935&(Bt[i]<<8|Bt[i]>>>24)|4278255360&(Bt[i]<<24|Bt[i]>>>8),t[e+i]^=Bt[i]},blockSize:4,ivSize:2}),vt.RabbitLegacy=yt._createHelper(St),bt.pad.ZeroPadding={pad:function(t,e){var r=4*e;t.clamp(),t.sigBytes+=r-(t.sigBytes%r||r)},unpad:function(t){var e=t.words,r=t.sigBytes-1;for(r=t.sigBytes-1;0<=r;r--)if(e[r>>>2]>>>24-r%4*8&255){t.sigBytes=r+1;break}}},bt});// Fingerprintjs2 - Copyright (c) 2019 Valentin Vasilyev (valentin.vasilyev@outlook.com)
// Licensed under the MIT (http://www.opensource.org/licenses/mit-license.php) license.
//
// @See https://cdn.jsdelivr.net/npm/fingerprintjs2@2.1.0/dist/fingerprint2.min.js
// @See https://cdnjs.cloudflare.com/ajax/libs/fingerprintjs2/2.1.0/fingerprint2.js
!function(e,t,a){"use strict";"undefined"!=typeof window&&"function"==typeof define&&define.amd?define(a):"undefined"!=typeof module&&module.exports?module.exports=a():t.exports?t.exports=a():t.Fingerprint2=a()}(0,this,function(){"use strict";var d=function(e,t){e=[e[0]>>>16,65535&e[0],e[1]>>>16,65535&e[1]],t=[t[0]>>>16,65535&t[0],t[1]>>>16,65535&t[1]];var a=[0,0,0,0];return a[3]+=e[3]+t[3],a[2]+=a[3]>>>16,a[3]&=65535,a[2]+=e[2]+t[2],a[1]+=a[2]>>>16,a[2]&=65535,a[1]+=e[1]+t[1],a[0]+=a[1]>>>16,a[1]&=65535,a[0]+=e[0]+t[0],a[0]&=65535,[a[0]<<16|a[1],a[2]<<16|a[3]]},g=function(e,t){e=[e[0]>>>16,65535&e[0],e[1]>>>16,65535&e[1]],t=[t[0]>>>16,65535&t[0],t[1]>>>16,65535&t[1]];var a=[0,0,0,0];return a[3]+=e[3]*t[3],a[2]+=a[3]>>>16,a[3]&=65535,a[2]+=e[2]*t[3],a[1]+=a[2]>>>16,a[2]&=65535,a[2]+=e[3]*t[2],a[1]+=a[2]>>>16,a[2]&=65535,a[1]+=e[1]*t[3],a[0]+=a[1]>>>16,a[1]&=65535,a[1]+=e[2]*t[2],a[0]+=a[1]>>>16,a[1]&=65535,a[1]+=e[3]*t[1],a[0]+=a[1]>>>16,a[1]&=65535,a[0]+=e[0]*t[3]+e[1]*t[2]+e[2]*t[1]+e[3]*t[0],a[0]&=65535,[a[0]<<16|a[1],a[2]<<16|a[3]]},f=function(e,t){return 32===(t%=64)?[e[1],e[0]]:t<32?[e[0]<<t|e[1]>>>32-t,e[1]<<t|e[0]>>>32-t]:(t-=32,[e[1]<<t|e[0]>>>32-t,e[0]<<t|e[1]>>>32-t])},h=function(e,t){return 0===(t%=64)?e:t<32?[e[0]<<t|e[1]>>>32-t,e[1]<<t]:[e[1]<<t-32,0]},m=function(e,t){return[e[0]^t[0],e[1]^t[1]]},T=function(e){return e=m(e,[0,e[0]>>>1]),e=g(e,[4283543511,3981806797]),e=m(e,[0,e[0]>>>1]),e=g(e,[3301882366,444984403]),e=m(e,[0,e[0]>>>1])},l=function(e,t){t=t||0;for(var a=(e=e||"").length%16,n=e.length-a,r=[0,t],i=[0,t],o=[0,0],l=[0,0],s=[2277735313,289559509],c=[1291169091,658871167],u=0;u<n;u+=16)o=[255&e.charCodeAt(u+4)|(255&e.charCodeAt(u+5))<<8|(255&e.charCodeAt(u+6))<<16|(255&e.charCodeAt(u+7))<<24,255&e.charCodeAt(u)|(255&e.charCodeAt(u+1))<<8|(255&e.charCodeAt(u+2))<<16|(255&e.charCodeAt(u+3))<<24],l=[255&e.charCodeAt(u+12)|(255&e.charCodeAt(u+13))<<8|(255&e.charCodeAt(u+14))<<16|(255&e.charCodeAt(u+15))<<24,255&e.charCodeAt(u+8)|(255&e.charCodeAt(u+9))<<8|(255&e.charCodeAt(u+10))<<16|(255&e.charCodeAt(u+11))<<24],o=g(o,s),o=f(o,31),o=g(o,c),r=m(r,o),r=f(r,27),r=d(r,i),r=d(g(r,[0,5]),[0,1390208809]),l=g(l,c),l=f(l,33),l=g(l,s),i=m(i,l),i=f(i,31),i=d(i,r),i=d(g(i,[0,5]),[0,944331445]);switch(o=[0,0],l=[0,0],a){case 15:l=m(l,h([0,e.charCodeAt(u+14)],48));case 14:l=m(l,h([0,e.charCodeAt(u+13)],40));case 13:l=m(l,h([0,e.charCodeAt(u+12)],32));case 12:l=m(l,h([0,e.charCodeAt(u+11)],24));case 11:l=m(l,h([0,e.charCodeAt(u+10)],16));case 10:l=m(l,h([0,e.charCodeAt(u+9)],8));case 9:l=m(l,[0,e.charCodeAt(u+8)]),l=g(l,c),l=f(l,33),l=g(l,s),i=m(i,l);case 8:o=m(o,h([0,e.charCodeAt(u+7)],56));case 7:o=m(o,h([0,e.charCodeAt(u+6)],48));case 6:o=m(o,h([0,e.charCodeAt(u+5)],40));case 5:o=m(o,h([0,e.charCodeAt(u+4)],32));case 4:o=m(o,h([0,e.charCodeAt(u+3)],24));case 3:o=m(o,h([0,e.charCodeAt(u+2)],16));case 2:o=m(o,h([0,e.charCodeAt(u+1)],8));case 1:o=m(o,[0,e.charCodeAt(u)]),o=g(o,s),o=f(o,31),o=g(o,c),r=m(r,o)}return r=m(r,[0,e.length]),i=m(i,[0,e.length]),r=d(r,i),i=d(i,r),r=T(r),i=T(i),r=d(r,i),i=d(i,r),("00000000"+(r[0]>>>0).toString(16)).slice(-8)+("00000000"+(r[1]>>>0).toString(16)).slice(-8)+("00000000"+(i[0]>>>0).toString(16)).slice(-8)+("00000000"+(i[1]>>>0).toString(16)).slice(-8)},e={preprocessor:null,audio:{timeout:1e3,excludeIOS11:!0},fonts:{swfContainerId:"fingerprintjs2",swfPath:"flash/compiled/FontList.swf",userDefinedFonts:[],extendedJsFonts:!1},screen:{detectScreenOrientation:!0},plugins:{sortPluginsFor:[/palemoon/i],excludeIE:!1},extraComponents:[],excludes:{enumerateDevices:!0,pixelRatio:!0,doNotTrack:!0,fontsFlash:!0},NOT_AVAILABLE:"not available",ERROR:"error",EXCLUDED:"excluded"},c=function(e,t){if(Array.prototype.forEach&&e.forEach===Array.prototype.forEach)e.forEach(t);else if(e.length===+e.length)for(var a=0,n=e.length;a<n;a++)t(e[a],a,e);else for(var r in e)e.hasOwnProperty(r)&&t(e[r],r,e)},s=function(e,n){var r=[];return null==e?r:Array.prototype.map&&e.map===Array.prototype.map?e.map(n):(c(e,function(e,t,a){r.push(n(e,t,a))}),r)},a=function(){return navigator.mediaDevices&&navigator.mediaDevices.enumerateDevices},n=function(e){var t=[window.screen.width,window.screen.height];return e.screen.detectScreenOrientation&&t.sort().reverse(),t},r=function(e){if(window.screen.availWidth&&window.screen.availHeight){var t=[window.screen.availHeight,window.screen.availWidth];return e.screen.detectScreenOrientation&&t.sort().reverse(),t}return e.NOT_AVAILABLE},i=function(e){if(null==navigator.plugins)return e.NOT_AVAILABLE;for(var t=[],a=0,n=navigator.plugins.length;a<n;a++)navigator.plugins[a]&&t.push(navigator.plugins[a]);return u(e)&&(t=t.sort(function(e,t){return e.name>t.name?1:e.name<t.name?-1:0})),s(t,function(e){var t=s(e,function(e){return[e.type,e.suffixes]});return[e.name,e.description,t]})},o=function(t){var e=[];if(Object.getOwnPropertyDescriptor&&Object.getOwnPropertyDescriptor(window,"ActiveXObject")||"ActiveXObject"in window){e=s(["AcroPDF.PDF","Adodb.Stream","AgControl.AgControl","DevalVRXCtrl.DevalVRXCtrl.1","MacromediaFlashPaper.MacromediaFlashPaper","Msxml2.DOMDocument","Msxml2.XMLHTTP","PDF.PdfCtrl","QuickTime.QuickTime","QuickTimeCheckObject.QuickTimeCheck.1","RealPlayer","RealPlayer.RealPlayer(tm) ActiveX Control (32-bit)","RealVideo.RealVideo(tm) ActiveX Control (32-bit)","Scripting.Dictionary","SWCtl.SWCtl","Shell.UIHelper","ShockwaveFlash.ShockwaveFlash","Skype.Detection","TDCCtl.TDCCtl","WMPlayer.OCX","rmocx.RealPlayer G2 Control","rmocx.RealPlayer G2 Control.1"],function(e){try{return new window.ActiveXObject(e),e}catch(e){return t.ERROR}})}else e.push(t.NOT_AVAILABLE);return navigator.plugins&&(e=e.concat(i(t))),e},u=function(e){for(var t=!1,a=0,n=e.plugins.sortPluginsFor.length;a<n;a++){var r=e.plugins.sortPluginsFor[a];if(navigator.userAgent.match(r)){t=!0;break}}return t},p=function(t){try{return!!window.sessionStorage}catch(e){return t.ERROR}},v=function(t){try{return!!window.localStorage}catch(e){return t.ERROR}},A=function(t){try{return!!window.indexedDB}catch(e){return t.ERROR}},S=function(e){return navigator.hardwareConcurrency?navigator.hardwareConcurrency:e.NOT_AVAILABLE},C=function(e){return navigator.cpuClass||e.NOT_AVAILABLE},B=function(e){return navigator.platform?navigator.platform:e.NOT_AVAILABLE},w=function(e){return navigator.doNotTrack?navigator.doNotTrack:navigator.msDoNotTrack?navigator.msDoNotTrack:window.doNotTrack?window.doNotTrack:e.NOT_AVAILABLE},t=function(){var t,e=0;void 0!==navigator.maxTouchPoints?e=navigator.maxTouchPoints:void 0!==navigator.msMaxTouchPoints&&(e=navigator.msMaxTouchPoints);try{document.createEvent("TouchEvent"),t=!0}catch(e){t=!1}return[e,t,"ontouchstart"in window]},y=function(e){var t=[],a=document.createElement("canvas");a.width=2e3,a.height=200,a.style.display="inline";var n=a.getContext("2d");return n.rect(0,0,10,10),n.rect(2,2,6,6),t.push("canvas winding:"+(!1===n.isPointInPath(5,5,"evenodd")?"yes":"no")),n.textBaseline="alphabetic",n.fillStyle="#f60",n.fillRect(125,1,62,20),n.fillStyle="#069",e.dontUseFakeFontInCanvas?n.font="11pt Arial":n.font="11pt no-real-font-123",n.fillText("Cwm fjordbank glyphs vext quiz, \ud83d\ude03",2,15),n.fillStyle="rgba(102, 204, 0, 0.2)",n.font="18pt Arial",n.fillText("Cwm fjordbank glyphs vext quiz, \ud83d\ude03",4,45),n.globalCompositeOperation="multiply",n.fillStyle="rgb(255,0,255)",n.beginPath(),n.arc(50,50,50,0,2*Math.PI,!0),n.closePath(),n.fill(),n.fillStyle="rgb(0,255,255)",n.beginPath(),n.arc(100,50,50,0,2*Math.PI,!0),n.closePath(),n.fill(),n.fillStyle="rgb(255,255,0)",n.beginPath(),n.arc(75,100,50,0,2*Math.PI,!0),n.closePath(),n.fill(),n.fillStyle="rgb(255,0,255)",n.arc(75,75,75,0,2*Math.PI,!0),n.arc(75,75,25,0,2*Math.PI,!0),n.fill("evenodd"),a.toDataURL&&t.push("canvas fp:"+a.toDataURL()),t},E=function(){var o,e=function(e){return o.clearColor(0,0,0,1),o.enable(o.DEPTH_TEST),o.depthFunc(o.LEQUAL),o.clear(o.COLOR_BUFFER_BIT|o.DEPTH_BUFFER_BIT),"["+e[0]+", "+e[1]+"]"};if(!(o=F()))return null;var l=[],t=o.createBuffer();o.bindBuffer(o.ARRAY_BUFFER,t);var a=new Float32Array([-.2,-.9,0,.4,-.26,0,0,.732134444,0]);o.bufferData(o.ARRAY_BUFFER,a,o.STATIC_DRAW),t.itemSize=3,t.numItems=3;var n=o.createProgram(),r=o.createShader(o.VERTEX_SHADER);o.shaderSource(r,"attribute vec2 attrVertex;varying vec2 varyinTexCoordinate;uniform vec2 uniformOffset;void main(){varyinTexCoordinate=attrVertex+uniformOffset;gl_Position=vec4(attrVertex,0,1);}"),o.compileShader(r);var i=o.createShader(o.FRAGMENT_SHADER);o.shaderSource(i,"precision mediump float;varying vec2 varyinTexCoordinate;void main() {gl_FragColor=vec4(varyinTexCoordinate,0,1);}"),o.compileShader(i),o.attachShader(n,r),o.attachShader(n,i),o.linkProgram(n),o.useProgram(n),n.vertexPosAttrib=o.getAttribLocation(n,"attrVertex"),n.offsetUniform=o.getUniformLocation(n,"uniformOffset"),o.enableVertexAttribArray(n.vertexPosArray),o.vertexAttribPointer(n.vertexPosAttrib,t.itemSize,o.FLOAT,!1,0,0),o.uniform2f(n.offsetUniform,1,1),o.drawArrays(o.TRIANGLE_STRIP,0,t.numItems);try{l.push(o.canvas.toDataURL())}catch(e){}l.push("extensions:"+(o.getSupportedExtensions()||[]).join(";")),l.push("webgl aliased line width range:"+e(o.getParameter(o.ALIASED_LINE_WIDTH_RANGE))),l.push("webgl aliased point size range:"+e(o.getParameter(o.ALIASED_POINT_SIZE_RANGE))),l.push("webgl alpha bits:"+o.getParameter(o.ALPHA_BITS)),l.push("webgl antialiasing:"+(o.getContextAttributes().antialias?"yes":"no")),l.push("webgl blue bits:"+o.getParameter(o.BLUE_BITS)),l.push("webgl depth bits:"+o.getParameter(o.DEPTH_BITS)),l.push("webgl green bits:"+o.getParameter(o.GREEN_BITS)),l.push("webgl max anisotropy:"+function(e){var t=e.getExtension("EXT_texture_filter_anisotropic")||e.getExtension("WEBKIT_EXT_texture_filter_anisotropic")||e.getExtension("MOZ_EXT_texture_filter_anisotropic");if(t){var a=e.getParameter(t.MAX_TEXTURE_MAX_ANISOTROPY_EXT);return 0===a&&(a=2),a}return null}(o)),l.push("webgl max combined texture image units:"+o.getParameter(o.MAX_COMBINED_TEXTURE_IMAGE_UNITS)),l.push("webgl max cube map texture size:"+o.getParameter(o.MAX_CUBE_MAP_TEXTURE_SIZE)),l.push("webgl max fragment uniform vectors:"+o.getParameter(o.MAX_FRAGMENT_UNIFORM_VECTORS)),l.push("webgl max render buffer size:"+o.getParameter(o.MAX_RENDERBUFFER_SIZE)),l.push("webgl max texture image units:"+o.getParameter(o.MAX_TEXTURE_IMAGE_UNITS)),l.push("webgl max texture size:"+o.getParameter(o.MAX_TEXTURE_SIZE)),l.push("webgl max varying vectors:"+o.getParameter(o.MAX_VARYING_VECTORS)),l.push("webgl max vertex attribs:"+o.getParameter(o.MAX_VERTEX_ATTRIBS)),l.push("webgl max vertex texture image units:"+o.getParameter(o.MAX_VERTEX_TEXTURE_IMAGE_UNITS)),l.push("webgl max vertex uniform vectors:"+o.getParameter(o.MAX_VERTEX_UNIFORM_VECTORS)),l.push("webgl max viewport dims:"+e(o.getParameter(o.MAX_VIEWPORT_DIMS))),l.push("webgl red bits:"+o.getParameter(o.RED_BITS)),l.push("webgl renderer:"+o.getParameter(o.RENDERER)),l.push("webgl shading language version:"+o.getParameter(o.SHADING_LANGUAGE_VERSION)),l.push("webgl stencil bits:"+o.getParameter(o.STENCIL_BITS)),l.push("webgl vendor:"+o.getParameter(o.VENDOR)),l.push("webgl version:"+o.getParameter(o.VERSION));try{var s=o.getExtension("WEBGL_debug_renderer_info");s&&(l.push("webgl unmasked vendor:"+o.getParameter(s.UNMASKED_VENDOR_WEBGL)),l.push("webgl unmasked renderer:"+o.getParameter(s.UNMASKED_RENDERER_WEBGL)))}catch(e){}return o.getShaderPrecisionFormat&&c(["FLOAT","INT"],function(i){c(["VERTEX","FRAGMENT"],function(r){c(["HIGH","MEDIUM","LOW"],function(n){c(["precision","rangeMin","rangeMax"],function(e){var t=o.getShaderPrecisionFormat(o[r+"_SHADER"],o[n+"_"+i])[e];"precision"!==e&&(e="precision "+e);var a=["webgl ",r.toLowerCase()," shader ",n.toLowerCase()," ",i.toLowerCase()," ",e,":",t].join("");l.push(a)})})})}),l},M=function(){try{var e=F(),t=e.getExtension("WEBGL_debug_renderer_info");return e.getParameter(t.UNMASKED_VENDOR_WEBGL)+"~"+e.getParameter(t.UNMASKED_RENDERER_WEBGL)}catch(e){return null}},x=function(){var e=document.createElement("div");e.innerHTML="&nbsp;";var t=!(e.className="adsbox");try{document.body.appendChild(e),t=0===document.getElementsByClassName("adsbox")[0].offsetHeight,document.body.removeChild(e)}catch(e){t=!1}return t},O=function(){if(void 0!==navigator.languages)try{if(navigator.languages[0].substr(0,2)!==navigator.language.substr(0,2))return!0}catch(e){return!0}return!1},b=function(){return window.screen.width<window.screen.availWidth||window.screen.height<window.screen.availHeight},P=function(){var e,t=navigator.userAgent.toLowerCase(),a=navigator.oscpu,n=navigator.platform.toLowerCase();if(e=0<=t.indexOf("windows phone")?"Windows Phone":0<=t.indexOf("win")?"Windows":0<=t.indexOf("android")?"Android":0<=t.indexOf("linux")||0<=t.indexOf("cros")?"Linux":0<=t.indexOf("iphone")||0<=t.indexOf("ipad")?"iOS":0<=t.indexOf("mac")?"Mac":"Other",("ontouchstart"in window||0<navigator.maxTouchPoints||0<navigator.msMaxTouchPoints)&&"Windows Phone"!==e&&"Android"!==e&&"iOS"!==e&&"Other"!==e)return!0;if(void 0!==a){if(0<=(a=a.toLowerCase()).indexOf("win")&&"Windows"!==e&&"Windows Phone"!==e)return!0;if(0<=a.indexOf("linux")&&"Linux"!==e&&"Android"!==e)return!0;if(0<=a.indexOf("mac")&&"Mac"!==e&&"iOS"!==e)return!0;if((-1===a.indexOf("win")&&-1===a.indexOf("linux")&&-1===a.indexOf("mac"))!=("Other"===e))return!0}return 0<=n.indexOf("win")&&"Windows"!==e&&"Windows Phone"!==e||((0<=n.indexOf("linux")||0<=n.indexOf("android")||0<=n.indexOf("pike"))&&"Linux"!==e&&"Android"!==e||((0<=n.indexOf("mac")||0<=n.indexOf("ipad")||0<=n.indexOf("ipod")||0<=n.indexOf("iphone"))&&"Mac"!==e&&"iOS"!==e||((n.indexOf("win")<0&&n.indexOf("linux")<0&&n.indexOf("mac")<0&&n.indexOf("iphone")<0&&n.indexOf("ipad")<0)!==("Other"===e)||void 0===navigator.plugins&&"Windows"!==e&&"Windows Phone"!==e)))},L=function(){var e,t=navigator.userAgent.toLowerCase(),a=navigator.productSub;if(("Chrome"===(e=0<=t.indexOf("firefox")?"Firefox":0<=t.indexOf("opera")||0<=t.indexOf("opr")?"Opera":0<=t.indexOf("chrome")?"Chrome":0<=t.indexOf("safari")?"Safari":0<=t.indexOf("trident")?"Internet Explorer":"Other")||"Safari"===e||"Opera"===e)&&"20030107"!==a)return!0;var n,r=eval.toString().length;if(37===r&&"Safari"!==e&&"Firefox"!==e&&"Other"!==e)return!0;if(39===r&&"Internet Explorer"!==e&&"Other"!==e)return!0;if(33===r&&"Chrome"!==e&&"Opera"!==e&&"Other"!==e)return!0;try{throw"a"}catch(e){try{e.toSource(),n=!0}catch(e){n=!1}}return n&&"Firefox"!==e&&"Other"!==e},I=function(){var e=document.createElement("canvas");return!(!e.getContext||!e.getContext("2d"))},k=function(){if(!I())return!1;var e=F();return!!window.WebGLRenderingContext&&!!e},R=function(){return"Microsoft Internet Explorer"===navigator.appName||!("Netscape"!==navigator.appName||!/Trident/.test(navigator.userAgent))},D=function(){return void 0!==window.swfobject},N=function(){return window.swfobject.hasFlashPlayerVersion("9.0.0")},_=function(t,e){var a="___fp_swf_loaded";window[a]=function(e){t(e)};var n,r,i=e.fonts.swfContainerId;(r=document.createElement("div")).setAttribute("id",n.fonts.swfContainerId),document.body.appendChild(r);var o={onReady:a};window.swfobject.embedSWF(e.fonts.swfPath,i,"1","1","9.0.0",!1,o,{allowScriptAccess:"always",menu:"false"},{})},F=function(){var e=document.createElement("canvas"),t=null;try{t=e.getContext("webgl")||e.getContext("experimental-webgl")}catch(e){}return t||(t=null),t},G=[{key:"userAgent",getData:function(e){e(navigator.userAgent)}},{key:"webdriver",getData:function(e,t){e(null==navigator.webdriver?t.NOT_AVAILABLE:navigator.webdriver)}},{key:"language",getData:function(e,t){e(navigator.language||navigator.userLanguage||navigator.browserLanguage||navigator.systemLanguage||t.NOT_AVAILABLE)}},{key:"colorDepth",getData:function(e,t){e(window.screen.colorDepth||t.NOT_AVAILABLE)}},{key:"deviceMemory",getData:function(e,t){e(navigator.deviceMemory||t.NOT_AVAILABLE)}},{key:"pixelRatio",getData:function(e,t){e(window.devicePixelRatio||t.NOT_AVAILABLE)}},{key:"hardwareConcurrency",getData:function(e,t){e(S(t))}},{key:"screenResolution",getData:function(e,t){e(n(t))}},{key:"availableScreenResolution",getData:function(e,t){e(r(t))}},{key:"timezoneOffset",getData:function(e){e((new Date).getTimezoneOffset())}},{key:"timezone",getData:function(e,t){window.Intl&&window.Intl.DateTimeFormat?e((new window.Intl.DateTimeFormat).resolvedOptions().timeZone):e(t.NOT_AVAILABLE)}},{key:"sessionStorage",getData:function(e,t){e(p(t))}},{key:"localStorage",getData:function(e,t){e(v(t))}},{key:"indexedDb",getData:function(e,t){e(A(t))}},{key:"addBehavior",getData:function(e){e(!(!document.body||!document.body.addBehavior))}},{key:"openDatabase",getData:function(e){e(!!window.openDatabase)}},{key:"cpuClass",getData:function(e,t){e(C(t))}},{key:"platform",getData:function(e,t){e(B(t))}},{key:"doNotTrack",getData:function(e,t){e(w(t))}},{key:"plugins",getData:function(e,t){R()?t.plugins.excludeIE?e(t.EXCLUDED):e(o(t)):e(i(t))}},{key:"canvas",getData:function(e,t){I()?e(y(t)):e(t.NOT_AVAILABLE)}},{key:"webgl",getData:function(e,t){k()?e(E()):e(t.NOT_AVAILABLE)}},{key:"webglVendorAndRenderer",getData:function(e){k()?e(M()):e()}},{key:"adBlock",getData:function(e){e(x())}},{key:"hasLiedLanguages",getData:function(e){e(O())}},{key:"hasLiedResolution",getData:function(e){e(b())}},{key:"hasLiedOs",getData:function(e){e(P())}},{key:"hasLiedBrowser",getData:function(e){e(L())}},{key:"touchSupport",getData:function(e){e(t())}},{key:"fonts",getData:function(e,t){var u=["monospace","sans-serif","serif"],d=["Andale Mono","Arial","Arial Black","Arial Hebrew","Arial MT","Arial Narrow","Arial Rounded MT Bold","Arial Unicode MS","Bitstream Vera Sans Mono","Book Antiqua","Bookman Old Style","Calibri","Cambria","Cambria Math","Century","Century Gothic","Century Schoolbook","Comic Sans","Comic Sans MS","Consolas","Courier","Courier New","Geneva","Georgia","Helvetica","Helvetica Neue","Impact","Lucida Bright","Lucida Calligraphy","Lucida Console","Lucida Fax","LUCIDA GRANDE","Lucida Handwriting","Lucida Sans","Lucida Sans Typewriter","Lucida Sans Unicode","Microsoft Sans Serif","Monaco","Monotype Corsiva","MS Gothic","MS Outlook","MS PGothic","MS Reference Sans Serif","MS Sans Serif","MS Serif","MYRIAD","MYRIAD PRO","Palatino","Palatino Linotype","Segoe Print","Segoe Script","Segoe UI","Segoe UI Light","Segoe UI Semibold","Segoe UI Symbol","Tahoma","Times","Times New Roman","Times New Roman PS","Trebuchet MS","Verdana","Wingdings","Wingdings 2","Wingdings 3"];t.fonts.extendedJsFonts&&(d=d.concat(["Abadi MT Condensed Light","Academy Engraved LET","ADOBE CASLON PRO","Adobe Garamond","ADOBE GARAMOND PRO","Agency FB","Aharoni","Albertus Extra Bold","Albertus Medium","Algerian","Amazone BT","American Typewriter","American Typewriter Condensed","AmerType Md BT","Andalus","Angsana New","AngsanaUPC","Antique Olive","Aparajita","Apple Chancery","Apple Color Emoji","Apple SD Gothic Neo","Arabic Typesetting","ARCHER","ARNO PRO","Arrus BT","Aurora Cn BT","AvantGarde Bk BT","AvantGarde Md BT","AVENIR","Ayuthaya","Bandy","Bangla Sangam MN","Bank Gothic","BankGothic Md BT","Baskerville","Baskerville Old Face","Batang","BatangChe","Bauer Bodoni","Bauhaus 93","Bazooka","Bell MT","Bembo","Benguiat Bk BT","Berlin Sans FB","Berlin Sans FB Demi","Bernard MT Condensed","BernhardFashion BT","BernhardMod BT","Big Caslon","BinnerD","Blackadder ITC","BlairMdITC TT","Bodoni 72","Bodoni 72 Oldstyle","Bodoni 72 Smallcaps","Bodoni MT","Bodoni MT Black","Bodoni MT Condensed","Bodoni MT Poster Compressed","Bookshelf Symbol 7","Boulder","Bradley Hand","Bradley Hand ITC","Bremen Bd BT","Britannic Bold","Broadway","Browallia New","BrowalliaUPC","Brush Script MT","Californian FB","Calisto MT","Calligrapher","Candara","CaslonOpnface BT","Castellar","Centaur","Cezanne","CG Omega","CG Times","Chalkboard","Chalkboard SE","Chalkduster","Charlesworth","Charter Bd BT","Charter BT","Chaucer","ChelthmITC Bk BT","Chiller","Clarendon","Clarendon Condensed","CloisterBlack BT","Cochin","Colonna MT","Constantia","Cooper Black","Copperplate","Copperplate Gothic","Copperplate Gothic Bold","Copperplate Gothic Light","CopperplGoth Bd BT","Corbel","Cordia New","CordiaUPC","Cornerstone","Coronet","Cuckoo","Curlz MT","DaunPenh","Dauphin","David","DB LCD Temp","DELICIOUS","Denmark","DFKai-SB","Didot","DilleniaUPC","DIN","DokChampa","Dotum","DotumChe","Ebrima","Edwardian Script ITC","Elephant","English 111 Vivace BT","Engravers MT","EngraversGothic BT","Eras Bold ITC","Eras Demi ITC","Eras Light ITC","Eras Medium ITC","EucrosiaUPC","Euphemia","Euphemia UCAS","EUROSTILE","Exotc350 Bd BT","FangSong","Felix Titling","Fixedsys","FONTIN","Footlight MT Light","Forte","FrankRuehl","Fransiscan","Freefrm721 Blk BT","FreesiaUPC","Freestyle Script","French Script MT","FrnkGothITC Bk BT","Fruitger","FRUTIGER","Futura","Futura Bk BT","Futura Lt BT","Futura Md BT","Futura ZBlk BT","FuturaBlack BT","Gabriola","Galliard BT","Gautami","Geeza Pro","Geometr231 BT","Geometr231 Hv BT","Geometr231 Lt BT","GeoSlab 703 Lt BT","GeoSlab 703 XBd BT","Gigi","Gill Sans","Gill Sans MT","Gill Sans MT Condensed","Gill Sans MT Ext Condensed Bold","Gill Sans Ultra Bold","Gill Sans Ultra Bold Condensed","Gisha","Gloucester MT Extra Condensed","GOTHAM","GOTHAM BOLD","Goudy Old Style","Goudy Stout","GoudyHandtooled BT","GoudyOLSt BT","Gujarati Sangam MN","Gulim","GulimChe","Gungsuh","GungsuhChe","Gurmukhi MN","Haettenschweiler","Harlow Solid Italic","Harrington","Heather","Heiti SC","Heiti TC","HELV","Herald","High Tower Text","Hiragino Kaku Gothic ProN","Hiragino Mincho ProN","Hoefler Text","Humanst 521 Cn BT","Humanst521 BT","Humanst521 Lt BT","Imprint MT Shadow","Incised901 Bd BT","Incised901 BT","Incised901 Lt BT","INCONSOLATA","Informal Roman","Informal011 BT","INTERSTATE","IrisUPC","Iskoola Pota","JasmineUPC","Jazz LET","Jenson","Jester","Jokerman","Juice ITC","Kabel Bk BT","Kabel Ult BT","Kailasa","KaiTi","Kalinga","Kannada Sangam MN","Kartika","Kaufmann Bd BT","Kaufmann BT","Khmer UI","KodchiangUPC","Kokila","Korinna BT","Kristen ITC","Krungthep","Kunstler Script","Lao UI","Latha","Leelawadee","Letter Gothic","Levenim MT","LilyUPC","Lithograph","Lithograph Light","Long Island","Lydian BT","Magneto","Maiandra GD","Malayalam Sangam MN","Malgun Gothic","Mangal","Marigold","Marion","Marker Felt","Market","Marlett","Matisse ITC","Matura MT Script Capitals","Meiryo","Meiryo UI","Microsoft Himalaya","Microsoft JhengHei","Microsoft New Tai Lue","Microsoft PhagsPa","Microsoft Tai Le","Microsoft Uighur","Microsoft YaHei","Microsoft Yi Baiti","MingLiU","MingLiU_HKSCS","MingLiU_HKSCS-ExtB","MingLiU-ExtB","Minion","Minion Pro","Miriam","Miriam Fixed","Mistral","Modern","Modern No. 20","Mona Lisa Solid ITC TT","Mongolian Baiti","MONO","MoolBoran","Mrs Eaves","MS LineDraw","MS Mincho","MS PMincho","MS Reference Specialty","MS UI Gothic","MT Extra","MUSEO","MV Boli","Nadeem","Narkisim","NEVIS","News Gothic","News GothicMT","NewsGoth BT","Niagara Engraved","Niagara Solid","Noteworthy","NSimSun","Nyala","OCR A Extended","Old Century","Old English Text MT","Onyx","Onyx BT","OPTIMA","Oriya Sangam MN","OSAKA","OzHandicraft BT","Palace Script MT","Papyrus","Parchment","Party LET","Pegasus","Perpetua","Perpetua Titling MT","PetitaBold","Pickwick","Plantagenet Cherokee","Playbill","PMingLiU","PMingLiU-ExtB","Poor Richard","Poster","PosterBodoni BT","PRINCETOWN LET","Pristina","PTBarnum BT","Pythagoras","Raavi","Rage Italic","Ravie","Ribbon131 Bd BT","Rockwell","Rockwell Condensed","Rockwell Extra Bold","Rod","Roman","Sakkal Majalla","Santa Fe LET","Savoye LET","Sceptre","Script","Script MT Bold","SCRIPTINA","Serifa","Serifa BT","Serifa Th BT","ShelleyVolante BT","Sherwood","Shonar Bangla","Showcard Gothic","Shruti","Signboard","SILKSCREEN","SimHei","Simplified Arabic","Simplified Arabic Fixed","SimSun","SimSun-ExtB","Sinhala Sangam MN","Sketch Rockwell","Skia","Small Fonts","Snap ITC","Snell Roundhand","Socket","Souvenir Lt BT","Staccato222 BT","Steamer","Stencil","Storybook","Styllo","Subway","Swis721 BlkEx BT","Swiss911 XCm BT","Sylfaen","Synchro LET","System","Tamil Sangam MN","Technical","Teletype","Telugu Sangam MN","Tempus Sans ITC","Terminal","Thonburi","Traditional Arabic","Trajan","TRAJAN PRO","Tristan","Tubular","Tunga","Tw Cen MT","Tw Cen MT Condensed","Tw Cen MT Condensed Extra Bold","TypoUpright BT","Unicorn","Univers","Univers CE 55 Medium","Univers Condensed","Utsaah","Vagabond","Vani","Vijaya","Viner Hand ITC","VisualUI","Vivaldi","Vladimir Script","Vrinda","Westminster","WHITNEY","Wide Latin","ZapfEllipt BT","ZapfHumnst BT","ZapfHumnst Dm BT","Zapfino","Zurich BlkEx BT","Zurich Ex BT","ZWAdobeF"]));d=(d=d.concat(t.fonts.userDefinedFonts)).filter(function(e,t){return d.indexOf(e)===t});var a=document.getElementsByTagName("body")[0],r=document.createElement("div"),g=document.createElement("div"),n={},i={},f=function(){var e=document.createElement("span");return e.style.position="absolute",e.style.left="-9999px",e.style.fontSize="72px",e.style.fontStyle="normal",e.style.fontWeight="normal",e.style.letterSpacing="normal",e.style.lineBreak="auto",e.style.lineHeight="normal",e.style.textTransform="none",e.style.textAlign="left",e.style.textDecoration="none",e.style.textShadow="none",e.style.whiteSpace="normal",e.style.wordBreak="normal",e.style.wordSpacing="normal",e.innerHTML="mmmmmmmmmmlli",e},o=function(e){for(var t=!1,a=0;a<u.length;a++)if(t=e[a].offsetWidth!==n[u[a]]||e[a].offsetHeight!==i[u[a]])return t;return t},l=function(){for(var e=[],t=0,a=u.length;t<a;t++){var n=f();n.style.fontFamily=u[t],r.appendChild(n),e.push(n)}return e}();a.appendChild(r);for(var s=0,c=u.length;s<c;s++)n[u[s]]=l[s].offsetWidth,i[u[s]]=l[s].offsetHeight;var h=function(){for(var e,t,a,n={},r=0,i=d.length;r<i;r++){for(var o=[],l=0,s=u.length;l<s;l++){var c=(e=d[r],t=u[l],a=void 0,(a=f()).style.fontFamily="'"+e+"',"+t,a);g.appendChild(c),o.push(c)}n[d[r]]=o}return n}();a.appendChild(g);for(var m=[],T=0,p=d.length;T<p;T++)o(h[d[T]])&&m.push(d[T]);a.removeChild(g),a.removeChild(r),e(m)},pauseBefore:!0},{key:"fontsFlash",getData:function(t,e){return D()?N()?e.fonts.swfPath?void _(function(e){t(e)},e):t("missing options.fonts.swfPath"):t("flash not installed"):t("swf object not loaded")},pauseBefore:!0},{key:"audio",getData:function(a,e){var t=e.audio;if(t.excludeIOS11&&navigator.userAgent.match(/OS 11.+Version\/11.+Safari/))return a(e.EXCLUDED);var n=window.OfflineAudioContext||window.webkitOfflineAudioContext;if(null==n)return a(e.NOT_AVAILABLE);var r=new n(1,44100,44100),i=r.createOscillator();i.type="triangle",i.frequency.setValueAtTime(1e4,r.currentTime);var o=r.createDynamicsCompressor();c([["threshold",-50],["knee",40],["ratio",12],["reduction",-20],["attack",0],["release",.25]],function(e){void 0!==o[e[0]]&&"function"==typeof o[e[0]].setValueAtTime&&o[e[0]].setValueAtTime(e[1],r.currentTime)}),i.connect(o),o.connect(r.destination),i.start(0),r.startRendering();var l=setTimeout(function(){return console.warn('Audio fingerprint timed out. Please report bug at https://github.com/Valve/fingerprintjs2 with your user agent: "'+navigator.userAgent+'".'),r.oncomplete=function(){},r=null,a("audioTimeout")},t.timeout);r.oncomplete=function(e){var t;try{clearTimeout(l),t=e.renderedBuffer.getChannelData(0).slice(4500,5e3).reduce(function(e,t){return e+Math.abs(t)},0).toString(),i.disconnect(),o.disconnect()}catch(e){return void a(e)}a(t)}}},{key:"enumerateDevices",getData:function(t,e){if(!a())return t(e.NOT_AVAILABLE);navigator.mediaDevices.enumerateDevices().then(function(e){t(e.map(function(e){return"id="+e.deviceId+";gid="+e.groupId+";"+e.kind+";"+e.label}))}).catch(function(e){t(e)})}}],U=function(e){throw new Error("'new Fingerprint()' is deprecated, see https://github.com/Valve/fingerprintjs2#upgrade-guide-from-182-to-200")};return U.get=function(a,n){n?a||(a={}):(n=a,a={}),function(e,t){if(null==t)return;var a,n;for(n in t)null==(a=t[n])||Object.prototype.hasOwnProperty.call(e,n)||(e[n]=a)}(a,e),a.components=a.extraComponents.concat(G);var r={data:[],addPreprocessedComponent:function(e,t){"function"==typeof a.preprocessor&&(t=a.preprocessor(e,t)),r.data.push({key:e,value:t})}},i=-1,o=function(e){if((i+=1)>=a.components.length)n(r.data);else{var t=a.components[i];if(a.excludes[t.key])o(!1);else{if(!e&&t.pauseBefore)return i-=1,void setTimeout(function(){o(!0)},1);try{t.getData(function(e){r.addPreprocessedComponent(t.key,e),o(!1)},a)}catch(e){r.addPreprocessedComponent(t.key,String(e)),o(!1)}}}};o(!1)},U.getPromise=function(a){return new Promise(function(e,t){U.get(a,e)})},U.getV18=function(i,o){return null==o&&(o=i,i={}),U.get(i,function(e){for(var t=[],a=0;a<e.length;a++){var n=e[a];if(n.value===(i.NOT_AVAILABLE||"not available"))t.push({key:n.key,value:"unknown"});else if("plugins"===n.key)t.push({key:"plugins",value:s(n.value,function(e){var t=s(e[2],function(e){return e.join?e.join("~"):e}).join(",");return[e[0],e[1],t].join("::")})});else if(-1!==["canvas","webgl"].indexOf(n.key))t.push({key:n.key,value:n.value.join("~")});else if(-1!==["sessionStorage","localStorage","indexedDb","addBehavior","openDatabase"].indexOf(n.key)){if(!n.value)continue;t.push({key:n.key,value:1})}else n.value?t.push(n.value.join?{key:n.key,value:n.value.join(";")}:n):t.push({key:n.key,value:n.value})}var r=l(s(t,function(e){return e.value}).join("~~~"),31);o(r,t)})},U.x64hash128=l,U.VERSION="2.1.0",U});﻿/**
 * IAM WebSDK CAPTCHA JIGSAW v2.0.0 | (c) 2017 ~ 2050 wl4g Foundation, Inc.
 * Copyright 2017-2032 <wangsir@gmail.com, 983708408@qq.com, babaa1f4@163.com>, Inc. x
 * Licensed under Apache2.0 (https://github.com/wl4g/super-devops/blob/master/LICENSE)
 */
(function ($) {
    'use strict';
    var runtime = {
		applyModel: {
			primaryImg: null,
			applyToken: null,
			verifyType: null,
			secret: null,
			y: 0
		},
		verifiedModel: {
			verified: false,
			verifiedToken: null
		}
	};
    var JigsawCaptcha = function (element, options) {
        this.element0 = $(element);
        this.options = $.extend({}, JigsawCaptcha.DEFAULTS, options);
        var w = this.element0.width();
        w = !w?this.options.width:w;
        this.element0.css({'width':w+'px'/*,'margin':'0 auto'*/});
        this.initDOM();
        this.initImg();
        this.bindEvents();
    };
    JigsawCaptcha.VERSION = 'latest';
    JigsawCaptcha.Author = '<Wanglsir@gmail.com, 983708408@qq.com, babaa1f4@163.com>';
    JigsawCaptcha.DEFAULTS = {
        width: 280, // canvas宽度
        height: 155, // canvas高度
        loadingText: Common.Util.isZhCN()?'加载中...':'Loading...',
        failedText: Common.Util.isZhCN()?'再试一次':"Let\'s try again?",
        barText: Common.Util.isZhCN()?'请拖动滑块完成拼图':'Drag to complete the jigsaw',
        repeatIcon: 'fa fa-repeat',
        getApplyCaptchaUrl: null,
        getVerifyAnalysisUrl: null,
        verifyDataKey: "verifyData", // Default: 'verifyData'
		applyCaptcha: function(img1, img2, tipText) {
			var that = this;
			var applyCaptchaUrl = Common.Util.checkEmpty("options.getApplyCaptchaUrl", that.getApplyCaptchaUrl());
			var _uri = applyCaptchaUrl.substring(0, applyCaptchaUrl.lastIndexOf("?"));
			$.ajax({
				url: _uri,
				type: 'post',
				data: Common.Util.toUrl({}, Common.Util.toUrlQueryParam(applyCaptchaUrl)),
				xhrFields: { withCredentials: true }, // Send cookies when support cross-domain request.
				success: function (res) {
					if(res.code == 200){
						runtime.applyModel = res.data.applyModel; // [MARK5]
						img1.setSrc(runtime.applyModel.primaryImg);
						img2.setSrc(runtime.applyModel.blockImg);
						img2.imagey = runtime.applyModel.y;
					} else {
						// Remove silder mouse event all.
						$(".sliderContainer").find(".slider").unbind(); // [MARK9], See: MARK6
						$(tipText).text(res.message);
						Common.Util.checkEmpty("options.onFail", that.onFail)("Failed to jigsaw apply captcha, " + res.message);
					}
				},
				error: function(req, status, errmsg) {
					console.debug(errmsg);
					// Remove silder mouse event all.
					$(".sliderContainer").find(".slider").unbind(); // [MARK8], See: MARK6
					Common.Util.checkEmpty("options.onFail", that.onFail)("Failed to jigsaw apply captcha, " + errmsg);
				}
			});
		},
		verifyAnalysis: function (arr, left) {
			// Additional algorithmic salt.
			left = new String(left);
			var applyTokenCrc = Common.Util.Crc16CheckSum.crc16Modbus(runtime.applyModel.applyToken);
			var tmpX = CryptoJS.enc.Hex.stringify(CryptoJS.SHA512(left + runtime.applyModel.applyToken)).substring(31, 97) + (left*applyTokenCrc);
            // Do encryption x-position.
			var cipherX = IAMCrypto.RSA.encryptToHexString(runtime.applyModel.secret, tmpX);
            var ret = null;
            var verifyData = {
                applyToken: runtime.applyModel.applyToken,
                x: cipherX,
                trails: arr,
            };
            // 提交验证码获取分析结果
			var that = this;
			var verifyAnalysisUrl = Common.Util.checkEmpty("options.getVerifyAnalysisUrl", that.getVerifyAnalysisUrl());
			var _uri = verifyAnalysisUrl.substring(0, verifyAnalysisUrl.lastIndexOf("?"));
			var paramMap = Common.Util.toUrlQueryParam(verifyAnalysisUrl);
			paramMap.set(Common.Util.checkEmpty("options.verifyDataKey", that.verifyDataKey), Common.Util.Codec.encodeBase58(JSON.stringify(verifyData)));
            $.ajax({
                url: _uri,
				xhrFields: { withCredentials: true }, // Send cookies when support cross-domain request.
                type: 'post',
                //contentType: 'application/json',
                //dataType: 'json',
				async: false,
				data: Common.Util.toUrl({}, paramMap),
                success: function(res) {
					if(res.code == 200){
						runtime.verifiedModel = res.data.verifiedModel;
						ret = res.data.verifiedModel;
						// Remove silder mouse event all.
						$(".sliderContainer").find(".slider").unbind(); // [MARK7], See: MARK6
						// Call jigsaw captcha verified.
						Common.Util.checkEmpty("options.onSuccess", that.onSuccess)(runtime.verifiedModel.verifiedToken);
					} else {
						Common.Util.checkEmpty("options.onFail", that.onFail)("Failed to jigsaw verify captcha, caused by: " + res.message);
					}
                },
				error: function(req, status, errmsg){
					console.debug(errmsg);
					Common.Util.checkEmpty("options.onFail", that.onFail)("Failed to jigsaw verify captcha, caused by: " + errmsg);
				}
            });
            return ret;
        },
		onSuccess: function(verifiedToken){
			console.debug("Jigsaw captcha verifyed successful. verifiedToken: "+ verifiedToken);
		},
		onFail: function(errmsg){
			console.error(errmsg);
		}
    };

    JigsawCaptcha.prototype.initDOM = function () {
        var createElement = function (tagName, className) {
            var elment = document.createElement(tagName);
            elment.className = className;
            return elment;
        };
        var createElementValue = function (tagName, value) {
            var elment = document.createElement(tagName);
            elment.innerText = value;
            return elment;
        };
        var createCanvas = function (width, height) {
            var canvas = document.createElement('canvas');
            canvas.width = width;
            canvas.height = height;
            return canvas;
        };
        var card = createElement('div', 'JigsawIamCaptcha card');
        card.style.display="none";

        var cardHeader = createElement('div', 'card-header');
		cardHeader.style.paddingLeft="20px";
		cardHeader.style.paddingTop="5px";
        var cardHeaderText = createElementValue('span', Common.Util.isZhCN()?'请完成人机验证':'Please complete man-machine verification');
        var cardBody = createElement('div', 'card-body');

        var canvas = createCanvas(this.options.width, this.options.height); // 画布
        var block = createCanvas(this.options.width, this.options.height); // 滑块
        var sliderContainer = createElement('div', 'sliderContainer');
        var refreshIcon = createElement('i', 'refreshIcon ' + this.options.repeatIcon);
        var sliderMask = createElement('div', 'sliderMask');
        var sliderbg = createElement('div', 'sliderbg');
        var slider = createElement('div', 'slider');
        var sliderIcon = createElement('i', 'fa fa-arrow-right sliderIcon');
        var text = createElement('span', 'sliderText');
        block.className = 'block';
        text.innerHTML = this.options.barText;

        var el = this.element0;
        el.append(card);
        card.append(cardHeader);
        cardHeader.append(cardHeaderText);
        cardHeader.append(refreshIcon);

        card.append(cardBody);
        cardBody.append(canvas);
        cardBody.append(block);

        slider.appendChild(sliderIcon);
        sliderMask.appendChild(slider);
        sliderContainer.appendChild(sliderbg);
        sliderContainer.appendChild(sliderMask);
        sliderbg.appendChild(text);
        el.append(sliderContainer);

        var _canvas = {
            canvas: canvas,
            block: block,
            card: card,
            sliderContainer: $(sliderContainer),
            refreshIcon: refreshIcon,
            slider: slider,
            sliderMask: sliderMask,
            sliderIcon: sliderIcon,
            text: $(text),
            canvasCtx: canvas.getContext('2d'),
            blockCtx: block.getContext('2d')
        };
        if ($.isFunction(Object.assign)) {
            Object.assign(this, _canvas);
        } else {
            $.extend(this, _canvas);
        }
    };

    JigsawCaptcha.prototype.initImg = function () {
        var that = this;
        var img1 = new Image();
        img1.crossOrigin = "Anonymous";

        var img2 = new Image();
        img2.crossOrigin = "Anonymous";

        img1.onload = function () {
            that.canvasCtx.drawImage(img1, 0, 0);
        };
        img2.onload = function () {
            that.blockCtx.drawImage(img2, 0, img2.imagey);
            console.debug(img2.imagey);
            that.text.text(that.text.attr('data-text'));
        };
        img1.setSrc = function (imgBase64) {
            that.text.removeClass('text-danger');
            img1.src = imgBase64.startsWith("data:") ? imgBase64: ('data:image/png;base64,'+imgBase64);
        };
        img2.setSrc = function (imgBase64) {
            that.text.removeClass('text-danger');
            img2.src = imgBase64.startsWith("data:") ? imgBase64: ('data:image/png;base64,'+imgBase64);
        };

        this.text.attr('data-text', this.options.barText);
        this.text.text(this.options.loadingText);
        this.img1 = img1;
        this.img2 = img2;

		// Apply captcha.
        this.applyCaptcha();
    };

    JigsawCaptcha.prototype.bindEvents = function () {
        var that = this;
        this.element0.on('selectstart', function () {
            return false;
        });

        $(this.refreshIcon).on('click', function () {
            that.text.text(that.options.barText);
            that.reset();
            if ($.isFunction(that.options.onRefresh)) that.options.onRefresh.call(that.element0);
        });

        var originX, originY, trails = [], isMouseDown = false;

        var handleDragStart = function (e) {
            if (that.text.hasClass('text-danger')) return;
            originX = e.clientX;
            originY = e.clientY;
            isMouseDown = true;
        };

        var handleOnmouseenter = function () {
            $(that.card).fadeIn(200);
        };

        var handleOnmouseleave = function () {
            //console.info("into out");
            if(!that.sliderContainer.hasClass('sliderContainer_active')){
                $(that.card).fadeOut(50);
            }
        };

        var handleDragMove = function (e) {
            e.preventDefault();
            if (!isMouseDown) return false;
            var eventX = e.clientX;
            var eventY = e.clientY;
            var scaling = 1.0;
			if(document.body.style.zoom){
				scaling = parseFloat(document.body.style.zoom);
			}
            var moveX = (eventX - originX)/scaling;
            var moveY = (eventY - originY)/scaling;
            if (moveX < 0 || moveX + 46 > that.options.width) return false;
            that.slider.style.left = (moveX) + 'px';
            var blockLeft =  moveX;
            that.block.style.left = blockLeft + 'px';
            that.sliderContainer.addClass('sliderContainer_active');
            that.sliderMask.style.width = (moveX + 4) + 'px';
            trails.push({
				t: new Date().getTime(),
				x: moveX,
				y: moveY
			});
        };

        var handleDragEnd = function (e) {
            if (!isMouseDown) return false;
            isMouseDown = false;
            var eventX = e.clientX;
            if (eventX === originX) return false;
            that.sliderContainer.removeClass('sliderContainer_active');
            that.trails = trails;
            var data = that.verifyAnalysis();
            if (data && data.verified) {
                that.sliderContainer.addClass('sliderContainer_success');
                that.text.text(Common.Util.isZhCN()?'验证通过':'Verified');
				// Call verified successful.
                if ($.isFunction(that.options.onSuccess(data.verifiedToken))) that.options.onSuccess.call(that.element0);
            } else {
                that.sliderContainer.addClass('sliderContainer_fail');
                //if ($.isFunction(that.options.onFail)) that.options.onFail.call(that.element0);
                setTimeout(function () {
                    that.text.text(that.options.failedText);
                    that.reset();
                }, 1000);
            }
        };

		// [MARK6], See: 'MARK7,MARK8,MARK9'
		$(this.slider).bind("mousedown", handleDragStart);
		$(this.slider).bind("touchstart", handleDragStart);
		$(this.slider).bind("mouseenter", handleOnmouseenter);
		$(this.element0).bind('mouseleave', handleOnmouseleave);
		$(document).bind("mousemove", handleDragMove);
		$(document).bind("touchmove", handleDragMove);
		$(document).bind("mouseup", handleDragEnd);
		$(document).bind("touchend", handleDragEnd);

		/*
		 * Note: If you add events using native js, jQuery will not be able to unbind.
		 */ 
		//this.slider.addEventListener('mousedown', handleDragStart);
        //this.slider.addEventListener('touchstart', handleDragStart);
        //this.slider.addEventListener('mouseenter', handleOnmouseenter);
		//this.element0.on('mouseleave', handleOnmouseleave);
        //document.addEventListener('mousemove', handleDragMove);
        //document.addEventListener('touchmove', handleDragMove);
        //document.addEventListener('mouseup', handleDragEnd);
        //document.addEventListener('touchend', handleDragEnd);
        //document.addEventListener('mousedown', function () { return false; });
        //document.addEventListener('touchstart', function () { return false; });
        //document.addEventListener('swipe', function () { return false; });
    };

	// Apply captcha.
    JigsawCaptcha.prototype.applyCaptcha = function() {
		var tipText = this.text;
		this.options.applyCaptcha(this.img1, this.img2, tipText);
	};

	// Verify captcha.
    JigsawCaptcha.prototype.verifyAnalysis = function () {
        var left = parseInt(this.block.style.left);
        var verified = this.options.verifyAnalysis(this.trails, left); // 拖动时x/y轴的移动距离,最总x位置
        return verified;
    };

	// Reset apply captcha.
    JigsawCaptcha.prototype.reset = function () {
        this.sliderContainer.removeClass('sliderContainer_fail sliderContainer_success');
        this.slider.style.left = 0;
        this.block.style.left = 0;
        this.sliderMask.style.width = 0;

		this.canvasCtx.clearRect(0, 0, this.options.width, this.options.height);
        this.blockCtx.clearRect(0, 0, this.options.width, this.options.height);
        this.block.width = this.options.width;

        this.text.attr('data-text', this.text.text());
        this.text.text(this.options.loadingText);
        this.applyCaptcha();
    };

	// Register to JQuery.
    $.fn.JigsawIamCaptcha = function(option) {
        return this.each(function () {
            var $this = $(this);
            var jigsawCaptcha0 = $this.data('lgb.JigsawIamCaptcha');
			if (jigsawCaptcha0) {
				//jigsawCaptcha0.reset(); return;
				$(".sliderContainer").remove();
				$this.removeData("lgb.JigsawIamCaptcha");
			}
            var options = typeof option === 'object' && option;
            $this.data('lgb.JigsawIamCaptcha', jigsawCaptcha0 = new JigsawCaptcha(this, options));
            if (typeof option === 'string') jigsawCaptcha0[option]();
        });
    };

})(jQuery);/**
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
			$.ajax({
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
					_iamConsole.error("Failed to init xsrf token. " + err);
				}
			});
		}

		if (_sync) {
			return _outXsrfToken(xsrfTokenHeaderName, xsrfTokenParamName, xsrfTokenValue);
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

				_initSafeCheck(principal, function(res){
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
		        	_iamConsole.info("TGC expired, redirectTo: ", res.data[settings.definition.redirectUrlKey]);
		            // e.g: window.location.href = '/#/login';
		        	redirectFn(res);
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

	// Check authentication and redirection
	var _checkAuthenticationAndRedirect = function(redirectUrl) {
		// Prevent flashing when redirecting to the home page.
		_iamConsole.info("Hidden login document(*) ... ");
		$("<style id='iam_check_authc_redirect_style'>*{display:none;}</style>").appendTo($("head")); // Hide

		return new Promise(resolve => {
			_initHandshakeIfNecessary(true).then(res => {
				if(!IAMCore.checkRespUnauthenticated(res)) { // Authenticated?
					var redirectRecord = JSON.parse(sessionStorage.getItem(constant.authRedirectRecordStorageKey));
					// Check null or expired? 
					if (!redirectRecord || (redirectRecord && Math.abs(new Date().getTime() - redirectRecord.t) > 60000)) {
						sessionStorage.removeItem(constant.authRedirectRecordStorageKey); // For renew
						redirectRecord = { c: 0, t: new Date().getTime() };
					}
					if (redirectRecord.c > 5) {
						throw "Too many failure redirections: "+ redirectRecord.c;
					}
					++redirectRecord.c;
					redirectRecord.t = new Date().getTime();
					sessionStorage.setItem(constant.authRedirectRecordStorageKey, JSON.stringify(redirectRecord));
					_iamConsole.info("Authenticated and redirection to: ", redirectUrl);
					window.location = redirectUrl;
				} else {
					_iamConsole.info("Unauthentication rendering login ... ");
					$("#iam_check_authc_redirect_style").remove(); // Show
					sessionStorage.removeItem(constant.authRedirectRecordStorageKey); // For reset
					resolve(res);
				}
			});
		});
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
	IAMCore.prototype.safeCheck = function(principal, callback) {
		_initHandshakeIfNecessary().then(res => {
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
		// 1: Ensure execution sequence（1.1: get umidToken; 1.2: get handshake; 1.3: init any authenticators）
		// 2: Forced refresh is to solve the problem that you can't log in again after exiting SPM application. 
		// The reason is that SPM project is a single page application, and the action of logging out is only to 
		// execute push('/#/login'), but not to refresh the page At this time, the old session information (due to cache) 
		// is used, and the correct operation should refresh the handshake interface to get the new session information 
		// as long as the login page is rendered. Of course, external calls can also be made iamUi.destroy() to solve this problem.
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
	IAMCore.prototype.checkAuthenticationAndRedirect = _checkAuthenticationAndRedirect;

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
/**
 * IAM WebSDK CRYPTO v2.0.0 | (c) 2017 ~ 2050 wl4g Foundation, Inc.
 * Copyright 2017-2032 <wangsir@gmail.com, 983708408@qq.com>, Inc. x
 * Licensed under Apache2.0 (https://github.com/wl4g/super-devops/blob/master/LICENSE)
 * ----------------------------------------------------------------------------
 * A JavaScript implementation of the Secure Hash Algorithm, SHA-512, as defined
 * in FIPS 180-2
 * Version 2.2 Copyright Anonymous Contributor, Paul Johnston 2000 - 2009.
 * Other contributors: Greg Holt, Andrew Kepert, Ydnar, Lostinet
 * Distributed under the BSD License
 * See http://pajhome.org.uk/crypt/md5 for details.
 */
(function(window, document){
	'use strict';

	// 暴露API给外部
	window.IAMCrypto = {
		/**
		 * 测试示例:
		 * 准备数据：plain=123, base64=MTIz, hex=313233
		 * 
		 * 第一步（生成秘钥对）：
		 * var keypair = IAMCrypto.RSA.generateKey();
		 * console.log(JSON.stringify(keypair));
		 *
		 * //publicKeyBase64=MIGeMA0GCSqGSIb3DQEBAQUAA4GMADCBiAKBgGUqVNE4Jc9qYhahq/CnpzlJ2qIr5tvUiMXJbFSFbe1winhYxn3NExrDkV+ZfsfZOc1/O6wxFoudX/ZGzAMpiym9LAnrcN5dyMrMgVH/iHrY0jlAGis/5Qkk5JTfftDsADsH1iC5b2O55Pybm/HJNb8XhD0T34Bst9Qk7Iz5PdjjAgMBAAE=
		 * //privateKeyBase64=MIGeMA0GCSqGSIb3DQEBAQUAA4GMADCBiAKBgGUqVNE4Jc9qYhahq/CnpzlJ2qIr5tvUiMXJbFSFbe1winhYxn3NExrDkV+ZfsfZOc1/O6wxFoudX/ZGzAMpiym9LAnrcN5dyMrMgVH/iHrY0jlAGis/5Qkk5JTfftDsADsH1iC5b2O55Pybm/HJNb8XhD0T34Bst9Qk7Iz5PdjjAgMBAAE=
		 * //publicKeyHex=30819e300d06092a864886f70d010101050003818c00308188028180652a54d13825cf6a6216a1abf0a7a73949daa22be6dbd488c5c96c54856ded708a7858c67dcd131ac3915f997ec7d939cd7f3bac31168b9d5ff646cc03298b29bd2c09eb70de5dc8cacc8151ff887ad8d239401a2b3fe50924e494df7ed0ec003b07d620b96f63b9e4fc9b9bf1c935bf17843d13df806cb7d424ec8cf93dd8e30203010001
		 * //privateKeyHex=3082025a020100028180652a54d13825cf6a6216a1abf0a7a73949daa22be6dbd488c5c96c54856ded708a7858c67dcd131ac3915f997ec7d939cd7f3bac31168b9d5ff646cc03298b29bd2c09eb70de5dc8cacc8151ff887ad8d239401a2b3fe50924e494df7ed0ec003b07d620b96f63b9e4fc9b9bf1c935bf17843d13df806cb7d424ec8cf93dd8e302030100010281805f600b5abc0e997f783e51e962170d46ab641e24399fe2bd978a904117124e1a1dd1dc10362612eed695c58556cb5ef669d09c1778a802b439f65b44976ea12d668c02f0e1ea694f1ac8f1b4e409d10ca81a6998c39c563ba052b3523f9b875f6d445d09aa309b7170b6ae6eeffff0f8f6ab5707081687e7226eeb0537902111024100ab542337f447603cc01587fa6c598d9581fe9f5fbddf3dac959e6c82645e1cbdf0629bd6c47a04eba486a2a89589976ba41c5acc2932a69d66caba6d7bb7b26f024100972964f61ba164b5b2d37ea2416416ed24d3caa3273e30ae1921335b2d4d335dc03e0c945b9674d4d8eb1dc7350a67dedbdfe267238a5b923c01483915de6acd0240625d9536dbf65ae7a634f6742ddf20adf50bb67f26a9546491267b104605cea4b2ae3ae10cbf2db2092d0f9890fa854854d9bebbb6ef90bf9033d6e36303addb02400cf5d49d3143463239f1de32a52ea2b4946ac03dfad85f2e1e237596c4ac90d1e1f0affd6c58db0d80c7afd6eb9a47cb98c87a4de3833254b86657bde53d1ba502405d90024a45bb2bd846968fb8761a6bc6650dead71d3a20088b089a71bbb3a179c7cf86aea64dec3600e3cf56f71369a4ff0c960ffc9b8ad71d5d1be93756ecb4
		 * 
		 * 第二步（publicKey加密数据）：
	     * var hexCiphertext = IAMCrypto.RSA.encryptToHexString(keypair.publicKeyHex, "123");
		 * console.log("加密后hex字符串" + hexCiphertext);
		 * // 结果：127125a5499a06d7cd53581b277b783cddf4f745bab16776878f04b60d849d01806c5f56800a131432ec2ada1f6ecd7c8c0328362df45c07a78d8b1a14a10e333ed9fd6d0075250a53286f0fb727fec7d1ac4f05dd98cad11794ccddadfc3238d77b28c05bae0eb644e289b294942a0defa26444a357c0ac5e60eed1fbd12895
		 * 
		 * 第三步（privateKey解密数据）：
	     * var hexPlaintext = IAMCrypto.RSA.encryptToHexString(keypair.privateKeyHex, hexCiphertext);
		 * console.log("解密后hex字符串" + hexPlaintext);
		 * // 结果：313233
		 **/
		RSA: {
		    encryptToHexString: function(publicKey, plaintext){ // RSA1 encrypt
				var crypt = new JSEncrypt();
				// You can use also setPrivateKey and setPublicKey, they are both alias to setKey
				publicKey = Common.Util.Codec.hexToBase64(Common.Util.checkEmpty("publicKey", publicKey));
				crypt.setKey(publicKey);

				//Eventhough the methods are called setPublicKey and setPrivateKey, remember
				//that they are only alias to setKey, so you can pass them both a private or
				//a public openssl key, just remember that setting a public key allows you to only encrypt.
				// Encrypt the data with the public key.
				if(typeof plaintext != "string"){
					plaintext = plaintext.toString(); // e.g. Is int-type encryption background Java rsa1_padding5 cannot be decrypted.
				}
				var ciphertextBase64 = crypt.encrypt(plaintext);
				if(!ciphertextBase64){
					throw "Failed to RSA encryption, maybe the key is set incorrectly. '" + publicKey + "'";
				}
				return Common.Util.Codec.base64ToHex(ciphertextBase64);
			},
		    decryptFromHexString: function(privateKey, hexCiphertext){ // RSA1 decrypt
				var crypt = new JSEncrypt();
				// You can use also setPrivateKey and setPublicKey, they are both alias to setKey
				privateKey = Common.Util.Codec.hexToBase64(Common.Util.checkEmpty("privateKey", privateKey));
				crypt.setKey(privateKey);

				//Eventhough the methods are called setPublicKey and setPrivateKey, remember
				//that they are only alias to setKey, so you can pass them both a private or
				//a public openssl key, just remember that setting a public key allows you to only encrypt.
				// Encrypt the data with the public key.
				if(typeof hexCiphertext != "string"){
					hexCiphertext = hexCiphertext.toString(); // e.g. Is int-type encryption background Java rsa1_padding5 cannot be decrypted.
				}
				var plaintext = crypt.decrypt(Common.Util.Codec.hexToBase64(hexCiphertext));
				if(!plaintext){
					throw "Failed to RSA encryption, maybe the key is set incorrectly. '" + privateKey + "'";
				}
				return plaintext;
			},
		    generateKey: function(){
				var crypt = new JSEncrypt();
				var publicKeyBase64 = crypt.getPublicKeyB64();
				var privateKeyBase64 = crypt.getPrivateKeyB64();
				return {
					publicKeyBase64: publicKeyBase64,
				 	privateKeyBase64: privateKeyBase64,
					publicKeyHex: Common.Util.Codec.base64ToHex(publicKeyBase64),
				 	privateKeyHex: Common.Util.Codec.base64ToHex(privateKeyBase64),
				};
			},
		}
	};

})(window, document);

/* RSA-Implements: See https://github.com/travist/jsencrypt, http://travistidwell.com/jsencrypt/ */
!function(t,e){"object"==typeof exports&&"undefined"!=typeof module?e(exports):"function"==typeof define&&define.amd?define(["exports"],e):e(t.JSEncrypt={})}(this,function(t){"use strict";var e="0123456789abcdefghijklmnopqrstuvwxyz";function a(t){return e.charAt(t)}function i(t,e){return t&e}function u(t,e){return t|e}function r(t,e){return t^e}function n(t,e){return t&~e}function s(t){if(0==t)return-1;var e=0;return 0==(65535&t)&&(t>>=16,e+=16),0==(255&t)&&(t>>=8,e+=8),0==(15&t)&&(t>>=4,e+=4),0==(3&t)&&(t>>=2,e+=2),0==(1&t)&&++e,e}function o(t){for(var e=0;0!=t;)t&=t-1,++e;return e}var h="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";function c(t){var e,i,r="";for(e=0;e+3<=t.length;e+=3)i=parseInt(t.substring(e,e+3),16),r+=h.charAt(i>>6)+h.charAt(63&i);for(e+1==t.length?(i=parseInt(t.substring(e,e+1),16),r+=h.charAt(i<<2)):e+2==t.length&&(i=parseInt(t.substring(e,e+2),16),r+=h.charAt(i>>2)+h.charAt((3&i)<<4));0<(3&r.length);)r+="=";return r}function f(t){var e,i="",r=0,n=0;for(e=0;e<t.length&&"="!=t.charAt(e);++e){var s=h.indexOf(t.charAt(e));s<0||(0==r?(i+=a(s>>2),n=3&s,r=1):1==r?(i+=a(n<<2|s>>4),n=15&s,r=2):2==r?(i+=a(n),i+=a(s>>2),n=3&s,r=3):(i+=a(n<<2|s>>4),i+=a(15&s),r=0))}return 1==r&&(i+=a(n<<2)),i}var l,p=function(t,e){return(p=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(t,e){t.__proto__=e}||function(t,e){for(var i in e)e.hasOwnProperty(i)&&(t[i]=e[i])})(t,e)};var g,d=function(t){var e;if(void 0===l){var i="0123456789ABCDEF",r=" \f\n\r\t \u2028\u2029";for(l={},e=0;e<16;++e)l[i.charAt(e)]=e;for(i=i.toLowerCase(),e=10;e<16;++e)l[i.charAt(e)]=e;for(e=0;e<r.length;++e)l[r.charAt(e)]=-1}var n=[],s=0,o=0;for(e=0;e<t.length;++e){var h=t.charAt(e);if("="==h)break;if(-1!=(h=l[h])){if(void 0===h)throw new Error("Illegal character at offset "+e);s|=h,2<=++o?(n[n.length]=s,o=s=0):s<<=4}}if(o)throw new Error("Hex encoding incomplete: 4 bits missing");return n},v={decode:function(t){var e;if(void 0===g){var i="= \f\n\r\t \u2028\u2029";for(g=Object.create(null),e=0;e<64;++e)g["ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(e)]=e;for(e=0;e<i.length;++e)g[i.charAt(e)]=-1}var r=[],n=0,s=0;for(e=0;e<t.length;++e){var o=t.charAt(e);if("="==o)break;if(-1!=(o=g[o])){if(void 0===o)throw new Error("Illegal character at offset "+e);n|=o,4<=++s?(r[r.length]=n>>16,r[r.length]=n>>8&255,r[r.length]=255&n,s=n=0):n<<=6}}switch(s){case 1:throw new Error("Base64 encoding incomplete: at least 2 bits missing");case 2:r[r.length]=n>>10;break;case 3:r[r.length]=n>>16,r[r.length]=n>>8&255}return r},re:/-----BEGIN [^-]+-----([A-Za-z0-9+\/=\s]+)-----END [^-]+-----|begin-base64[^\n]+\n([A-Za-z0-9+\/=\s]+)====/,unarmor:function(t){var e=v.re.exec(t);if(e)if(e[1])t=e[1];else{if(!e[2])throw new Error("RegExp out of sync");t=e[2]}return v.decode(t)}},m=1e13,y=function(){function t(t){this.buf=[+t||0]}return t.prototype.mulAdd=function(t,e){var i,r,n=this.buf,s=n.length;for(i=0;i<s;++i)(r=n[i]*t+e)<m?e=0:r-=(e=0|r/m)*m,n[i]=r;0<e&&(n[i]=e)},t.prototype.sub=function(t){var e,i,r=this.buf,n=r.length;for(e=0;e<n;++e)(i=r[e]-t)<0?(i+=m,t=1):t=0,r[e]=i;for(;0===r[r.length-1];)r.pop()},t.prototype.toString=function(t){if(10!=(t||10))throw new Error("only base 10 is supported");for(var e=this.buf,i=e[e.length-1].toString(),r=e.length-2;0<=r;--r)i+=(m+e[r]).toString().substring(1);return i},t.prototype.valueOf=function(){for(var t=this.buf,e=0,i=t.length-1;0<=i;--i)e=e*m+t[i];return e},t.prototype.simplify=function(){var t=this.buf;return 1==t.length?t[0]:this},t}(),b="…",T=/^(\d\d)(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])([01]\d|2[0-3])(?:([0-5]\d)(?:([0-5]\d)(?:[.,](\d{1,3}))?)?)?(Z|[-+](?:[0]\d|1[0-2])([0-5]\d)?)?$/,S=/^(\d\d\d\d)(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])([01]\d|2[0-3])(?:([0-5]\d)(?:([0-5]\d)(?:[.,](\d{1,3}))?)?)?(Z|[-+](?:[0]\d|1[0-2])([0-5]\d)?)?$/;function E(t,e){return t.length>e&&(t=t.substring(0,e)+b),t}var w,D=function(){function i(t,e){this.hexDigits="0123456789ABCDEF",t instanceof i?(this.enc=t.enc,this.pos=t.pos):(this.enc=t,this.pos=e)}return i.prototype.get=function(t){if(void 0===t&&(t=this.pos++),t>=this.enc.length)throw new Error("Requesting byte offset "+t+" on a stream of length "+this.enc.length);return"string"==typeof this.enc?this.enc.charCodeAt(t):this.enc[t]},i.prototype.hexByte=function(t){return this.hexDigits.charAt(t>>4&15)+this.hexDigits.charAt(15&t)},i.prototype.hexDump=function(t,e,i){for(var r="",n=t;n<e;++n)if(r+=this.hexByte(this.get(n)),!0!==i)switch(15&n){case 7:r+="  ";break;case 15:r+="\n";break;default:r+=" "}return r},i.prototype.isASCII=function(t,e){for(var i=t;i<e;++i){var r=this.get(i);if(r<32||176<r)return!1}return!0},i.prototype.parseStringISO=function(t,e){for(var i="",r=t;r<e;++r)i+=String.fromCharCode(this.get(r));return i},i.prototype.parseStringUTF=function(t,e){for(var i="",r=t;r<e;){var n=this.get(r++);i+=n<128?String.fromCharCode(n):191<n&&n<224?String.fromCharCode((31&n)<<6|63&this.get(r++)):String.fromCharCode((15&n)<<12|(63&this.get(r++))<<6|63&this.get(r++))}return i},i.prototype.parseStringBMP=function(t,e){for(var i,r,n="",s=t;s<e;)i=this.get(s++),r=this.get(s++),n+=String.fromCharCode(i<<8|r);return n},i.prototype.parseTime=function(t,e,i){var r=this.parseStringISO(t,e),n=(i?T:S).exec(r);return n?(i&&(n[1]=+n[1],n[1]+=+n[1]<70?2e3:1900),r=n[1]+"-"+n[2]+"-"+n[3]+" "+n[4],n[5]&&(r+=":"+n[5],n[6]&&(r+=":"+n[6],n[7]&&(r+="."+n[7]))),n[8]&&(r+=" UTC","Z"!=n[8]&&(r+=n[8],n[9]&&(r+=":"+n[9]))),r):"Unrecognized time: "+r},i.prototype.parseInteger=function(t,e){for(var i,r=this.get(t),n=127<r,s=n?255:0,o="";r==s&&++t<e;)r=this.get(t);if(0===(i=e-t))return n?-1:0;if(4<i){for(o=r,i<<=3;0==(128&(+o^s));)o=+o<<1,--i;o="("+i+" bit)\n"}n&&(r-=256);for(var h=new y(r),a=t+1;a<e;++a)h.mulAdd(256,this.get(a));return o+h.toString()},i.prototype.parseBitString=function(t,e,i){for(var r=this.get(t),n="("+((e-t-1<<3)-r)+" bit)\n",s="",o=t+1;o<e;++o){for(var h=this.get(o),a=o==e-1?r:0,u=7;a<=u;--u)s+=h>>u&1?"1":"0";if(s.length>i)return n+E(s,i)}return n+s},i.prototype.parseOctetString=function(t,e,i){if(this.isASCII(t,e))return E(this.parseStringISO(t,e),i);var r=e-t,n="("+r+" byte)\n";(i/=2)<r&&(e=t+i);for(var s=t;s<e;++s)n+=this.hexByte(this.get(s));return i<r&&(n+=b),n},i.prototype.parseOID=function(t,e,i){for(var r="",n=new y,s=0,o=t;o<e;++o){var h=this.get(o);if(n.mulAdd(128,127&h),s+=7,!(128&h)){if(""===r)if((n=n.simplify())instanceof y)n.sub(80),r="2."+n.toString();else{var a=n<80?n<40?0:1:2;r=a+"."+(n-40*a)}else r+="."+n.toString();if(r.length>i)return E(r,i);n=new y,s=0}}return 0<s&&(r+=".incomplete"),r},i}(),x=function(){function c(t,e,i,r,n){if(!(r instanceof R))throw new Error("Invalid tag value.");this.stream=t,this.header=e,this.length=i,this.tag=r,this.sub=n}return c.prototype.typeName=function(){switch(this.tag.tagClass){case 0:switch(this.tag.tagNumber){case 0:return"EOC";case 1:return"BOOLEAN";case 2:return"INTEGER";case 3:return"BIT_STRING";case 4:return"OCTET_STRING";case 5:return"NULL";case 6:return"OBJECT_IDENTIFIER";case 7:return"ObjectDescriptor";case 8:return"EXTERNAL";case 9:return"REAL";case 10:return"ENUMERATED";case 11:return"EMBEDDED_PDV";case 12:return"UTF8String";case 16:return"SEQUENCE";case 17:return"SET";case 18:return"NumericString";case 19:return"PrintableString";case 20:return"TeletexString";case 21:return"VideotexString";case 22:return"IA5String";case 23:return"UTCTime";case 24:return"GeneralizedTime";case 25:return"GraphicString";case 26:return"VisibleString";case 27:return"GeneralString";case 28:return"UniversalString";case 30:return"BMPString"}return"Universal_"+this.tag.tagNumber.toString();case 1:return"Application_"+this.tag.tagNumber.toString();case 2:return"["+this.tag.tagNumber.toString()+"]";case 3:return"Private_"+this.tag.tagNumber.toString()}},c.prototype.content=function(t){if(void 0===this.tag)return null;void 0===t&&(t=1/0);var e=this.posContent(),i=Math.abs(this.length);if(!this.tag.isUniversal())return null!==this.sub?"("+this.sub.length+" elem)":this.stream.parseOctetString(e,e+i,t);switch(this.tag.tagNumber){case 1:return 0===this.stream.get(e)?"false":"true";case 2:return this.stream.parseInteger(e,e+i);case 3:return this.sub?"("+this.sub.length+" elem)":this.stream.parseBitString(e,e+i,t);case 4:return this.sub?"("+this.sub.length+" elem)":this.stream.parseOctetString(e,e+i,t);case 6:return this.stream.parseOID(e,e+i,t);case 16:case 17:return null!==this.sub?"("+this.sub.length+" elem)":"(no elem)";case 12:return E(this.stream.parseStringUTF(e,e+i),t);case 18:case 19:case 20:case 21:case 22:case 26:return E(this.stream.parseStringISO(e,e+i),t);case 30:return E(this.stream.parseStringBMP(e,e+i),t);case 23:case 24:return this.stream.parseTime(e,e+i,23==this.tag.tagNumber)}return null},c.prototype.toString=function(){return this.typeName()+"@"+this.stream.pos+"[header:"+this.header+",length:"+this.length+",sub:"+(null===this.sub?"null":this.sub.length)+"]"},c.prototype.toPrettyString=function(t){void 0===t&&(t="");var e=t+this.typeName()+" @"+this.stream.pos;if(0<=this.length&&(e+="+"),e+=this.length,this.tag.tagConstructed?e+=" (constructed)":!this.tag.isUniversal()||3!=this.tag.tagNumber&&4!=this.tag.tagNumber||null===this.sub||(e+=" (encapsulates)"),e+="\n",null!==this.sub){t+="  ";for(var i=0,r=this.sub.length;i<r;++i)e+=this.sub[i].toPrettyString(t)}return e},c.prototype.posStart=function(){return this.stream.pos},c.prototype.posContent=function(){return this.stream.pos+this.header},c.prototype.posEnd=function(){return this.stream.pos+this.header+Math.abs(this.length)},c.prototype.toHexString=function(){return this.stream.hexDump(this.posStart(),this.posEnd(),!0)},c.decodeLength=function(t){var e=t.get(),i=127&e;if(i==e)return i;if(6<i)throw new Error("Length over 48 bits not supported at position "+(t.pos-1));if(0===i)return null;for(var r=e=0;r<i;++r)e=256*e+t.get();return e},c.prototype.getHexStringValue=function(){var t=this.toHexString(),e=2*this.header,i=2*this.length;return t.substr(e,i)},c.decode=function(t){var r;r=t instanceof D?t:new D(t,0);var e=new D(r),i=new R(r),n=c.decodeLength(r),s=r.pos,o=s-e.pos,h=null,a=function(){var t=[];if(null!==n){for(var e=s+n;r.pos<e;)t[t.length]=c.decode(r);if(r.pos!=e)throw new Error("Content size is not correct for container starting at offset "+s)}else try{for(;;){var i=c.decode(r);if(i.tag.isEOC())break;t[t.length]=i}n=s-r.pos}catch(t){throw new Error("Exception while decoding undefined length content: "+t)}return t};if(i.tagConstructed)h=a();else if(i.isUniversal()&&(3==i.tagNumber||4==i.tagNumber))try{if(3==i.tagNumber&&0!=r.get())throw new Error("BIT STRINGs with unused bits cannot encapsulate.");h=a();for(var u=0;u<h.length;++u)if(h[u].tag.isEOC())throw new Error("EOC is not supposed to be actual content.")}catch(t){h=null}if(null===h){if(null===n)throw new Error("We can't skip over an invalid tag with undefined length at offset "+s);r.pos=s+Math.abs(n)}return new c(e,o,n,i,h)},c}(),R=function(){function t(t){var e=t.get();if(this.tagClass=e>>6,this.tagConstructed=0!=(32&e),this.tagNumber=31&e,31==this.tagNumber){for(var i=new y;e=t.get(),i.mulAdd(128,127&e),128&e;);this.tagNumber=i.simplify()}}return t.prototype.isUniversal=function(){return 0===this.tagClass},t.prototype.isEOC=function(){return 0===this.tagClass&&0===this.tagNumber},t}(),B=[2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97,101,103,107,109,113,127,131,137,139,149,151,157,163,167,173,179,181,191,193,197,199,211,223,227,229,233,239,241,251,257,263,269,271,277,281,283,293,307,311,313,317,331,337,347,349,353,359,367,373,379,383,389,397,401,409,419,421,431,433,439,443,449,457,461,463,467,479,487,491,499,503,509,521,523,541,547,557,563,569,571,577,587,593,599,601,607,613,617,619,631,641,643,647,653,659,661,673,677,683,691,701,709,719,727,733,739,743,751,757,761,769,773,787,797,809,811,821,823,827,829,839,853,857,859,863,877,881,883,887,907,911,919,929,937,941,947,953,967,971,977,983,991,997],A=(1<<26)/B[B.length-1],O=function(){function b(t,e,i){null!=t&&("number"==typeof t?this.fromNumber(t,e,i):null==e&&"string"!=typeof t?this.fromString(t,256):this.fromString(t,e))}return b.prototype.toString=function(t){if(this.s<0)return"-"+this.negate().toString(t);var e;if(16==t)e=4;else if(8==t)e=3;else if(2==t)e=1;else if(32==t)e=5;else{if(4!=t)return this.toRadix(t);e=2}var i,r=(1<<e)-1,n=!1,s="",o=this.t,h=this.DB-o*this.DB%e;if(0<o--)for(h<this.DB&&0<(i=this[o]>>h)&&(n=!0,s=a(i));0<=o;)h<e?(i=(this[o]&(1<<h)-1)<<e-h,i|=this[--o]>>(h+=this.DB-e)):(i=this[o]>>(h-=e)&r,h<=0&&(h+=this.DB,--o)),0<i&&(n=!0),n&&(s+=a(i));return n?s:"0"},b.prototype.negate=function(){var t=M();return b.ZERO.subTo(this,t),t},b.prototype.abs=function(){return this.s<0?this.negate():this},b.prototype.compareTo=function(t){var e=this.s-t.s;if(0!=e)return e;var i=this.t;if(0!=(e=i-t.t))return this.s<0?-e:e;for(;0<=--i;)if(0!=(e=this[i]-t[i]))return e;return 0},b.prototype.bitLength=function(){return this.t<=0?0:this.DB*(this.t-1)+U(this[this.t-1]^this.s&this.DM)},b.prototype.mod=function(t){var e=M();return this.abs().divRemTo(t,null,e),this.s<0&&0<e.compareTo(b.ZERO)&&t.subTo(e,e),e},b.prototype.modPowInt=function(t,e){var i;return i=t<256||e.isEven()?new I(e):new N(e),this.exp(t,i)},b.prototype.clone=function(){var t=M();return this.copyTo(t),t},b.prototype.intValue=function(){if(this.s<0){if(1==this.t)return this[0]-this.DV;if(0==this.t)return-1}else{if(1==this.t)return this[0];if(0==this.t)return 0}return(this[1]&(1<<32-this.DB)-1)<<this.DB|this[0]},b.prototype.byteValue=function(){return 0==this.t?this.s:this[0]<<24>>24},b.prototype.shortValue=function(){return 0==this.t?this.s:this[0]<<16>>16},b.prototype.signum=function(){return this.s<0?-1:this.t<=0||1==this.t&&this[0]<=0?0:1},b.prototype.toByteArray=function(){var t=this.t,e=[];e[0]=this.s;var i,r=this.DB-t*this.DB%8,n=0;if(0<t--)for(r<this.DB&&(i=this[t]>>r)!=(this.s&this.DM)>>r&&(e[n++]=i|this.s<<this.DB-r);0<=t;)r<8?(i=(this[t]&(1<<r)-1)<<8-r,i|=this[--t]>>(r+=this.DB-8)):(i=this[t]>>(r-=8)&255,r<=0&&(r+=this.DB,--t)),0!=(128&i)&&(i|=-256),0==n&&(128&this.s)!=(128&i)&&++n,(0<n||i!=this.s)&&(e[n++]=i);return e},b.prototype.equals=function(t){return 0==this.compareTo(t)},b.prototype.min=function(t){return this.compareTo(t)<0?this:t},b.prototype.max=function(t){return 0<this.compareTo(t)?this:t},b.prototype.and=function(t){var e=M();return this.bitwiseTo(t,i,e),e},b.prototype.or=function(t){var e=M();return this.bitwiseTo(t,u,e),e},b.prototype.xor=function(t){var e=M();return this.bitwiseTo(t,r,e),e},b.prototype.andNot=function(t){var e=M();return this.bitwiseTo(t,n,e),e},b.prototype.not=function(){for(var t=M(),e=0;e<this.t;++e)t[e]=this.DM&~this[e];return t.t=this.t,t.s=~this.s,t},b.prototype.shiftLeft=function(t){var e=M();return t<0?this.rShiftTo(-t,e):this.lShiftTo(t,e),e},b.prototype.shiftRight=function(t){var e=M();return t<0?this.lShiftTo(-t,e):this.rShiftTo(t,e),e},b.prototype.getLowestSetBit=function(){for(var t=0;t<this.t;++t)if(0!=this[t])return t*this.DB+s(this[t]);return this.s<0?this.t*this.DB:-1},b.prototype.bitCount=function(){for(var t=0,e=this.s&this.DM,i=0;i<this.t;++i)t+=o(this[i]^e);return t},b.prototype.testBit=function(t){var e=Math.floor(t/this.DB);return e>=this.t?0!=this.s:0!=(this[e]&1<<t%this.DB)},b.prototype.setBit=function(t){return this.changeBit(t,u)},b.prototype.clearBit=function(t){return this.changeBit(t,n)},b.prototype.flipBit=function(t){return this.changeBit(t,r)},b.prototype.add=function(t){var e=M();return this.addTo(t,e),e},b.prototype.subtract=function(t){var e=M();return this.subTo(t,e),e},b.prototype.multiply=function(t){var e=M();return this.multiplyTo(t,e),e},b.prototype.divide=function(t){var e=M();return this.divRemTo(t,e,null),e},b.prototype.remainder=function(t){var e=M();return this.divRemTo(t,null,e),e},b.prototype.divideAndRemainder=function(t){var e=M(),i=M();return this.divRemTo(t,e,i),[e,i]},b.prototype.modPow=function(t,e){var i,r,n=t.bitLength(),s=F(1);if(n<=0)return s;i=n<18?1:n<48?3:n<144?4:n<768?5:6,r=n<8?new I(e):e.isEven()?new P(e):new N(e);var o=[],h=3,a=i-1,u=(1<<i)-1;if(o[1]=r.convert(this),1<i){var c=M();for(r.sqrTo(o[1],c);h<=u;)o[h]=M(),r.mulTo(c,o[h-2],o[h]),h+=2}var f,l,p=t.t-1,g=!0,d=M();for(n=U(t[p])-1;0<=p;){for(a<=n?f=t[p]>>n-a&u:(f=(t[p]&(1<<n+1)-1)<<a-n,0<p&&(f|=t[p-1]>>this.DB+n-a)),h=i;0==(1&f);)f>>=1,--h;if((n-=h)<0&&(n+=this.DB,--p),g)o[f].copyTo(s),g=!1;else{for(;1<h;)r.sqrTo(s,d),r.sqrTo(d,s),h-=2;0<h?r.sqrTo(s,d):(l=s,s=d,d=l),r.mulTo(d,o[f],s)}for(;0<=p&&0==(t[p]&1<<n);)r.sqrTo(s,d),l=s,s=d,d=l,--n<0&&(n=this.DB-1,--p)}return r.revert(s)},b.prototype.modInverse=function(t){var e=t.isEven();if(this.isEven()&&e||0==t.signum())return b.ZERO;for(var i=t.clone(),r=this.clone(),n=F(1),s=F(0),o=F(0),h=F(1);0!=i.signum();){for(;i.isEven();)i.rShiftTo(1,i),e?(n.isEven()&&s.isEven()||(n.addTo(this,n),s.subTo(t,s)),n.rShiftTo(1,n)):s.isEven()||s.subTo(t,s),s.rShiftTo(1,s);for(;r.isEven();)r.rShiftTo(1,r),e?(o.isEven()&&h.isEven()||(o.addTo(this,o),h.subTo(t,h)),o.rShiftTo(1,o)):h.isEven()||h.subTo(t,h),h.rShiftTo(1,h);0<=i.compareTo(r)?(i.subTo(r,i),e&&n.subTo(o,n),s.subTo(h,s)):(r.subTo(i,r),e&&o.subTo(n,o),h.subTo(s,h))}return 0!=r.compareTo(b.ONE)?b.ZERO:0<=h.compareTo(t)?h.subtract(t):h.signum()<0?(h.addTo(t,h),h.signum()<0?h.add(t):h):h},b.prototype.pow=function(t){return this.exp(t,new V)},b.prototype.gcd=function(t){var e=this.s<0?this.negate():this.clone(),i=t.s<0?t.negate():t.clone();if(e.compareTo(i)<0){var r=e;e=i,i=r}var n=e.getLowestSetBit(),s=i.getLowestSetBit();if(s<0)return e;for(n<s&&(s=n),0<s&&(e.rShiftTo(s,e),i.rShiftTo(s,i));0<e.signum();)0<(n=e.getLowestSetBit())&&e.rShiftTo(n,e),0<(n=i.getLowestSetBit())&&i.rShiftTo(n,i),0<=e.compareTo(i)?(e.subTo(i,e),e.rShiftTo(1,e)):(i.subTo(e,i),i.rShiftTo(1,i));return 0<s&&i.lShiftTo(s,i),i},b.prototype.isProbablePrime=function(t){var e,i=this.abs();if(1==i.t&&i[0]<=B[B.length-1]){for(e=0;e<B.length;++e)if(i[0]==B[e])return!0;return!1}if(i.isEven())return!1;for(e=1;e<B.length;){for(var r=B[e],n=e+1;n<B.length&&r<A;)r*=B[n++];for(r=i.modInt(r);e<n;)if(r%B[e++]==0)return!1}return i.millerRabin(t)},b.prototype.copyTo=function(t){for(var e=this.t-1;0<=e;--e)t[e]=this[e];t.t=this.t,t.s=this.s},b.prototype.fromInt=function(t){this.t=1,this.s=t<0?-1:0,0<t?this[0]=t:t<-1?this[0]=t+this.DV:this.t=0},b.prototype.fromString=function(t,e){var i;if(16==e)i=4;else if(8==e)i=3;else if(256==e)i=8;else if(2==e)i=1;else if(32==e)i=5;else{if(4!=e)return void this.fromRadix(t,e);i=2}this.t=0,this.s=0;for(var r=t.length,n=!1,s=0;0<=--r;){var o=8==i?255&+t[r]:C(t,r);o<0?"-"==t.charAt(r)&&(n=!0):(n=!1,0==s?this[this.t++]=o:s+i>this.DB?(this[this.t-1]|=(o&(1<<this.DB-s)-1)<<s,this[this.t++]=o>>this.DB-s):this[this.t-1]|=o<<s,(s+=i)>=this.DB&&(s-=this.DB))}8==i&&0!=(128&+t[0])&&(this.s=-1,0<s&&(this[this.t-1]|=(1<<this.DB-s)-1<<s)),this.clamp(),n&&b.ZERO.subTo(this,this)},b.prototype.clamp=function(){for(var t=this.s&this.DM;0<this.t&&this[this.t-1]==t;)--this.t},b.prototype.dlShiftTo=function(t,e){var i;for(i=this.t-1;0<=i;--i)e[i+t]=this[i];for(i=t-1;0<=i;--i)e[i]=0;e.t=this.t+t,e.s=this.s},b.prototype.drShiftTo=function(t,e){for(var i=t;i<this.t;++i)e[i-t]=this[i];e.t=Math.max(this.t-t,0),e.s=this.s},b.prototype.lShiftTo=function(t,e){for(var i=t%this.DB,r=this.DB-i,n=(1<<r)-1,s=Math.floor(t/this.DB),o=this.s<<i&this.DM,h=this.t-1;0<=h;--h)e[h+s+1]=this[h]>>r|o,o=(this[h]&n)<<i;for(h=s-1;0<=h;--h)e[h]=0;e[s]=o,e.t=this.t+s+1,e.s=this.s,e.clamp()},b.prototype.rShiftTo=function(t,e){e.s=this.s;var i=Math.floor(t/this.DB);if(i>=this.t)e.t=0;else{var r=t%this.DB,n=this.DB-r,s=(1<<r)-1;e[0]=this[i]>>r;for(var o=i+1;o<this.t;++o)e[o-i-1]|=(this[o]&s)<<n,e[o-i]=this[o]>>r;0<r&&(e[this.t-i-1]|=(this.s&s)<<n),e.t=this.t-i,e.clamp()}},b.prototype.subTo=function(t,e){for(var i=0,r=0,n=Math.min(t.t,this.t);i<n;)r+=this[i]-t[i],e[i++]=r&this.DM,r>>=this.DB;if(t.t<this.t){for(r-=t.s;i<this.t;)r+=this[i],e[i++]=r&this.DM,r>>=this.DB;r+=this.s}else{for(r+=this.s;i<t.t;)r-=t[i],e[i++]=r&this.DM,r>>=this.DB;r-=t.s}e.s=r<0?-1:0,r<-1?e[i++]=this.DV+r:0<r&&(e[i++]=r),e.t=i,e.clamp()},b.prototype.multiplyTo=function(t,e){var i=this.abs(),r=t.abs(),n=i.t;for(e.t=n+r.t;0<=--n;)e[n]=0;for(n=0;n<r.t;++n)e[n+i.t]=i.am(0,r[n],e,n,0,i.t);e.s=0,e.clamp(),this.s!=t.s&&b.ZERO.subTo(e,e)},b.prototype.squareTo=function(t){for(var e=this.abs(),i=t.t=2*e.t;0<=--i;)t[i]=0;for(i=0;i<e.t-1;++i){var r=e.am(i,e[i],t,2*i,0,1);(t[i+e.t]+=e.am(i+1,2*e[i],t,2*i+1,r,e.t-i-1))>=e.DV&&(t[i+e.t]-=e.DV,t[i+e.t+1]=1)}0<t.t&&(t[t.t-1]+=e.am(i,e[i],t,2*i,0,1)),t.s=0,t.clamp()},b.prototype.divRemTo=function(t,e,i){var r=t.abs();if(!(r.t<=0)){var n=this.abs();if(n.t<r.t)return null!=e&&e.fromInt(0),void(null!=i&&this.copyTo(i));null==i&&(i=M());var s=M(),o=this.s,h=t.s,a=this.DB-U(r[r.t-1]);0<a?(r.lShiftTo(a,s),n.lShiftTo(a,i)):(r.copyTo(s),n.copyTo(i));var u=s.t,c=s[u-1];if(0!=c){var f=c*(1<<this.F1)+(1<u?s[u-2]>>this.F2:0),l=this.FV/f,p=(1<<this.F1)/f,g=1<<this.F2,d=i.t,v=d-u,m=null==e?M():e;for(s.dlShiftTo(v,m),0<=i.compareTo(m)&&(i[i.t++]=1,i.subTo(m,i)),b.ONE.dlShiftTo(u,m),m.subTo(s,s);s.t<u;)s[s.t++]=0;for(;0<=--v;){var y=i[--d]==c?this.DM:Math.floor(i[d]*l+(i[d-1]+g)*p);if((i[d]+=s.am(0,y,i,v,0,u))<y)for(s.dlShiftTo(v,m),i.subTo(m,i);i[d]<--y;)i.subTo(m,i)}null!=e&&(i.drShiftTo(u,e),o!=h&&b.ZERO.subTo(e,e)),i.t=u,i.clamp(),0<a&&i.rShiftTo(a,i),o<0&&b.ZERO.subTo(i,i)}}},b.prototype.invDigit=function(){if(this.t<1)return 0;var t=this[0];if(0==(1&t))return 0;var e=3&t;return 0<(e=(e=(e=(e=e*(2-(15&t)*e)&15)*(2-(255&t)*e)&255)*(2-((65535&t)*e&65535))&65535)*(2-t*e%this.DV)%this.DV)?this.DV-e:-e},b.prototype.isEven=function(){return 0==(0<this.t?1&this[0]:this.s)},b.prototype.exp=function(t,e){if(4294967295<t||t<1)return b.ONE;var i=M(),r=M(),n=e.convert(this),s=U(t)-1;for(n.copyTo(i);0<=--s;)if(e.sqrTo(i,r),0<(t&1<<s))e.mulTo(r,n,i);else{var o=i;i=r,r=o}return e.revert(i)},b.prototype.chunkSize=function(t){return Math.floor(Math.LN2*this.DB/Math.log(t))},b.prototype.toRadix=function(t){if(null==t&&(t=10),0==this.signum()||t<2||36<t)return"0";var e=this.chunkSize(t),i=Math.pow(t,e),r=F(i),n=M(),s=M(),o="";for(this.divRemTo(r,n,s);0<n.signum();)o=(i+s.intValue()).toString(t).substr(1)+o,n.divRemTo(r,n,s);return s.intValue().toString(t)+o},b.prototype.fromRadix=function(t,e){this.fromInt(0),null==e&&(e=10);for(var i=this.chunkSize(e),r=Math.pow(e,i),n=!1,s=0,o=0,h=0;h<t.length;++h){var a=C(t,h);a<0?"-"==t.charAt(h)&&0==this.signum()&&(n=!0):(o=e*o+a,++s>=i&&(this.dMultiply(r),this.dAddOffset(o,0),o=s=0))}0<s&&(this.dMultiply(Math.pow(e,s)),this.dAddOffset(o,0)),n&&b.ZERO.subTo(this,this)},b.prototype.fromNumber=function(t,e,i){if("number"==typeof e)if(t<2)this.fromInt(1);else for(this.fromNumber(t,i),this.testBit(t-1)||this.bitwiseTo(b.ONE.shiftLeft(t-1),u,this),this.isEven()&&this.dAddOffset(1,0);!this.isProbablePrime(e);)this.dAddOffset(2,0),this.bitLength()>t&&this.subTo(b.ONE.shiftLeft(t-1),this);else{var r=[],n=7&t;r.length=1+(t>>3),e.nextBytes(r),0<n?r[0]&=(1<<n)-1:r[0]=0,this.fromString(r,256)}},b.prototype.bitwiseTo=function(t,e,i){var r,n,s=Math.min(t.t,this.t);for(r=0;r<s;++r)i[r]=e(this[r],t[r]);if(t.t<this.t){for(n=t.s&this.DM,r=s;r<this.t;++r)i[r]=e(this[r],n);i.t=this.t}else{for(n=this.s&this.DM,r=s;r<t.t;++r)i[r]=e(n,t[r]);i.t=t.t}i.s=e(this.s,t.s),i.clamp()},b.prototype.changeBit=function(t,e){var i=b.ONE.shiftLeft(t);return this.bitwiseTo(i,e,i),i},b.prototype.addTo=function(t,e){for(var i=0,r=0,n=Math.min(t.t,this.t);i<n;)r+=this[i]+t[i],e[i++]=r&this.DM,r>>=this.DB;if(t.t<this.t){for(r+=t.s;i<this.t;)r+=this[i],e[i++]=r&this.DM,r>>=this.DB;r+=this.s}else{for(r+=this.s;i<t.t;)r+=t[i],e[i++]=r&this.DM,r>>=this.DB;r+=t.s}e.s=r<0?-1:0,0<r?e[i++]=r:r<-1&&(e[i++]=this.DV+r),e.t=i,e.clamp()},b.prototype.dMultiply=function(t){this[this.t]=this.am(0,t-1,this,0,0,this.t),++this.t,this.clamp()},b.prototype.dAddOffset=function(t,e){if(0!=t){for(;this.t<=e;)this[this.t++]=0;for(this[e]+=t;this[e]>=this.DV;)this[e]-=this.DV,++e>=this.t&&(this[this.t++]=0),++this[e]}},b.prototype.multiplyLowerTo=function(t,e,i){var r=Math.min(this.t+t.t,e);for(i.s=0,i.t=r;0<r;)i[--r]=0;for(var n=i.t-this.t;r<n;++r)i[r+this.t]=this.am(0,t[r],i,r,0,this.t);for(n=Math.min(t.t,e);r<n;++r)this.am(0,t[r],i,r,0,e-r);i.clamp()},b.prototype.multiplyUpperTo=function(t,e,i){--e;var r=i.t=this.t+t.t-e;for(i.s=0;0<=--r;)i[r]=0;for(r=Math.max(e-this.t,0);r<t.t;++r)i[this.t+r-e]=this.am(e-r,t[r],i,0,0,this.t+r-e);i.clamp(),i.drShiftTo(1,i)},b.prototype.modInt=function(t){if(t<=0)return 0;var e=this.DV%t,i=this.s<0?t-1:0;if(0<this.t)if(0==e)i=this[0]%t;else for(var r=this.t-1;0<=r;--r)i=(e*i+this[r])%t;return i},b.prototype.millerRabin=function(t){var e=this.subtract(b.ONE),i=e.getLowestSetBit();if(i<=0)return!1;var r=e.shiftRight(i);B.length<(t=t+1>>1)&&(t=B.length);for(var n=M(),s=0;s<t;++s){n.fromInt(B[Math.floor(Math.random()*B.length)]);var o=n.modPow(r,this);if(0!=o.compareTo(b.ONE)&&0!=o.compareTo(e)){for(var h=1;h++<i&&0!=o.compareTo(e);)if(0==(o=o.modPowInt(2,this)).compareTo(b.ONE))return!1;if(0!=o.compareTo(e))return!1}}return!0},b.prototype.square=function(){var t=M();return this.squareTo(t),t},b.prototype.gcda=function(t,e){var i=this.s<0?this.negate():this.clone(),r=t.s<0?t.negate():t.clone();if(i.compareTo(r)<0){var n=i;i=r,r=n}var s=i.getLowestSetBit(),o=r.getLowestSetBit();if(o<0)e(i);else{s<o&&(o=s),0<o&&(i.rShiftTo(o,i),r.rShiftTo(o,r));var h=function(){0<(s=i.getLowestSetBit())&&i.rShiftTo(s,i),0<(s=r.getLowestSetBit())&&r.rShiftTo(s,r),0<=i.compareTo(r)?(i.subTo(r,i),i.rShiftTo(1,i)):(r.subTo(i,r),r.rShiftTo(1,r)),0<i.signum()?setTimeout(h,0):(0<o&&r.lShiftTo(o,r),setTimeout(function(){e(r)},0))};setTimeout(h,10)}},b.prototype.fromNumberAsync=function(t,e,i,r){if("number"==typeof e)if(t<2)this.fromInt(1);else{this.fromNumber(t,i),this.testBit(t-1)||this.bitwiseTo(b.ONE.shiftLeft(t-1),u,this),this.isEven()&&this.dAddOffset(1,0);var n=this,s=function(){n.dAddOffset(2,0),n.bitLength()>t&&n.subTo(b.ONE.shiftLeft(t-1),n),n.isProbablePrime(e)?setTimeout(function(){r()},0):setTimeout(s,0)};setTimeout(s,0)}else{var o=[],h=7&t;o.length=1+(t>>3),e.nextBytes(o),0<h?o[0]&=(1<<h)-1:o[0]=0,this.fromString(o,256)}},b}(),V=function(){function t(){}return t.prototype.convert=function(t){return t},t.prototype.revert=function(t){return t},t.prototype.mulTo=function(t,e,i){t.multiplyTo(e,i)},t.prototype.sqrTo=function(t,e){t.squareTo(e)},t}(),I=function(){function t(t){this.m=t}return t.prototype.convert=function(t){return t.s<0||0<=t.compareTo(this.m)?t.mod(this.m):t},t.prototype.revert=function(t){return t},t.prototype.reduce=function(t){t.divRemTo(this.m,null,t)},t.prototype.mulTo=function(t,e,i){t.multiplyTo(e,i),this.reduce(i)},t.prototype.sqrTo=function(t,e){t.squareTo(e),this.reduce(e)},t}(),N=function(){function t(t){this.m=t,this.mp=t.invDigit(),this.mpl=32767&this.mp,this.mph=this.mp>>15,this.um=(1<<t.DB-15)-1,this.mt2=2*t.t}return t.prototype.convert=function(t){var e=M();return t.abs().dlShiftTo(this.m.t,e),e.divRemTo(this.m,null,e),t.s<0&&0<e.compareTo(O.ZERO)&&this.m.subTo(e,e),e},t.prototype.revert=function(t){var e=M();return t.copyTo(e),this.reduce(e),e},t.prototype.reduce=function(t){for(;t.t<=this.mt2;)t[t.t++]=0;for(var e=0;e<this.m.t;++e){var i=32767&t[e],r=i*this.mpl+((i*this.mph+(t[e]>>15)*this.mpl&this.um)<<15)&t.DM;for(t[i=e+this.m.t]+=this.m.am(0,r,t,e,0,this.m.t);t[i]>=t.DV;)t[i]-=t.DV,t[++i]++}t.clamp(),t.drShiftTo(this.m.t,t),0<=t.compareTo(this.m)&&t.subTo(this.m,t)},t.prototype.mulTo=function(t,e,i){t.multiplyTo(e,i),this.reduce(i)},t.prototype.sqrTo=function(t,e){t.squareTo(e),this.reduce(e)},t}(),P=function(){function t(t){this.m=t,this.r2=M(),this.q3=M(),O.ONE.dlShiftTo(2*t.t,this.r2),this.mu=this.r2.divide(t)}return t.prototype.convert=function(t){if(t.s<0||t.t>2*this.m.t)return t.mod(this.m);if(t.compareTo(this.m)<0)return t;var e=M();return t.copyTo(e),this.reduce(e),e},t.prototype.revert=function(t){return t},t.prototype.reduce=function(t){for(t.drShiftTo(this.m.t-1,this.r2),t.t>this.m.t+1&&(t.t=this.m.t+1,t.clamp()),this.mu.multiplyUpperTo(this.r2,this.m.t+1,this.q3),this.m.multiplyLowerTo(this.q3,this.m.t+1,this.r2);t.compareTo(this.r2)<0;)t.dAddOffset(1,this.m.t+1);for(t.subTo(this.r2,t);0<=t.compareTo(this.m);)t.subTo(this.m,t)},t.prototype.mulTo=function(t,e,i){t.multiplyTo(e,i),this.reduce(i)},t.prototype.sqrTo=function(t,e){t.squareTo(e),this.reduce(e)},t}();function M(){return new O(null)}function q(t,e){return new O(t,e)}"Microsoft Internet Explorer"==navigator.appName?(O.prototype.am=function(t,e,i,r,n,s){for(var o=32767&e,h=e>>15;0<=--s;){var a=32767&this[t],u=this[t++]>>15,c=h*a+u*o;n=((a=o*a+((32767&c)<<15)+i[r]+(1073741823&n))>>>30)+(c>>>15)+h*u+(n>>>30),i[r++]=1073741823&a}return n},w=30):"Netscape"!=navigator.appName?(O.prototype.am=function(t,e,i,r,n,s){for(;0<=--s;){var o=e*this[t++]+i[r]+n;n=Math.floor(o/67108864),i[r++]=67108863&o}return n},w=26):(O.prototype.am=function(t,e,i,r,n,s){for(var o=16383&e,h=e>>14;0<=--s;){var a=16383&this[t],u=this[t++]>>14,c=h*a+u*o;n=((a=o*a+((16383&c)<<14)+i[r]+n)>>28)+(c>>14)+h*u,i[r++]=268435455&a}return n},w=28),O.prototype.DB=w,O.prototype.DM=(1<<w)-1,O.prototype.DV=1<<w;O.prototype.FV=Math.pow(2,52),O.prototype.F1=52-w,O.prototype.F2=2*w-52;var j,L,H=[];for(j="0".charCodeAt(0),L=0;L<=9;++L)H[j++]=L;for(j="a".charCodeAt(0),L=10;L<36;++L)H[j++]=L;for(j="A".charCodeAt(0),L=10;L<36;++L)H[j++]=L;function C(t,e){var i=H[t.charCodeAt(e)];return null==i?-1:i}function F(t){var e=M();return e.fromInt(t),e}function U(t){var e,i=1;return 0!=(e=t>>>16)&&(t=e,i+=16),0!=(e=t>>8)&&(t=e,i+=8),0!=(e=t>>4)&&(t=e,i+=4),0!=(e=t>>2)&&(t=e,i+=2),0!=(e=t>>1)&&(t=e,i+=1),i}O.ZERO=F(0),O.ONE=F(1);var K=function(){function t(){this.i=0,this.j=0,this.S=[]}return t.prototype.init=function(t){var e,i,r;for(e=0;e<256;++e)this.S[e]=e;for(e=i=0;e<256;++e)i=i+this.S[e]+t[e%t.length]&255,r=this.S[e],this.S[e]=this.S[i],this.S[i]=r;this.i=0,this.j=0},t.prototype.next=function(){var t;return this.i=this.i+1&255,this.j=this.j+this.S[this.i]&255,t=this.S[this.i],this.S[this.i]=this.S[this.j],this.S[this.j]=t,this.S[t+this.S[this.i]&255]},t}();var k,_,z=256,Z=null;if(null==Z){Z=[];var G=void(_=0);if(window.crypto&&window.crypto.getRandomValues){var $=new Uint32Array(256);for(window.crypto.getRandomValues($),G=0;G<$.length;++G)Z[_++]=255&$[G]}var Y=function(t){if(this.count=this.count||0,256<=this.count||z<=_)window.removeEventListener?window.removeEventListener("mousemove",Y,!1):window.detachEvent&&window.detachEvent("onmousemove",Y);else try{var e=t.x+t.y;Z[_++]=255&e,this.count+=1}catch(t){}};window.addEventListener?window.addEventListener("mousemove",Y,!1):window.attachEvent&&window.attachEvent("onmousemove",Y)}function J(){if(null==k){for(k=new K;_<z;){var t=Math.floor(65536*Math.random());Z[_++]=255&t}for(k.init(Z),_=0;_<Z.length;++_)Z[_]=0;_=0}return k.next()}var X=function(){function t(){}return t.prototype.nextBytes=function(t){for(var e=0;e<t.length;++e)t[e]=J()},t}();var Q=function(){function t(){this.n=null,this.e=0,this.d=null,this.p=null,this.q=null,this.dmp1=null,this.dmq1=null,this.coeff=null}return t.prototype.doPublic=function(t){return t.modPowInt(this.e,this.n)},t.prototype.doPrivate=function(t){if(null==this.p||null==this.q)return t.modPow(this.d,this.n);for(var e=t.mod(this.p).modPow(this.dmp1,this.p),i=t.mod(this.q).modPow(this.dmq1,this.q);e.compareTo(i)<0;)e=e.add(this.p);return e.subtract(i).multiply(this.coeff).mod(this.p).multiply(this.q).add(i)},t.prototype.setPublic=function(t,e){null!=t&&null!=e&&0<t.length&&0<e.length?(this.n=q(t,16),this.e=parseInt(e,16)):console.error("Invalid RSA public key")},t.prototype.encrypt=function(t){var e=function(t,e){if(e<t.length+11)return console.error("Message too long for RSA"),null;for(var i=[],r=t.length-1;0<=r&&0<e;){var n=t.charCodeAt(r--);n<128?i[--e]=n:127<n&&n<2048?(i[--e]=63&n|128,i[--e]=n>>6|192):(i[--e]=63&n|128,i[--e]=n>>6&63|128,i[--e]=n>>12|224)}i[--e]=0;for(var s=new X,o=[];2<e;){for(o[0]=0;0==o[0];)s.nextBytes(o);i[--e]=o[0]}return i[--e]=2,i[--e]=0,new O(i)}(t,this.n.bitLength()+7>>3);if(null==e)return null;var i=this.doPublic(e);if(null==i)return null;var r=i.toString(16);return 0==(1&r.length)?r:"0"+r},t.prototype.setPrivate=function(t,e,i){null!=t&&null!=e&&0<t.length&&0<e.length?(this.n=q(t,16),this.e=parseInt(e,16),this.d=q(i,16)):console.error("Invalid RSA private key")},t.prototype.setPrivateEx=function(t,e,i,r,n,s,o,h){null!=t&&null!=e&&0<t.length&&0<e.length?(this.n=q(t,16),this.e=parseInt(e,16),this.d=q(i,16),this.p=q(r,16),this.q=q(n,16),this.dmp1=q(s,16),this.dmq1=q(o,16),this.coeff=q(h,16)):console.error("Invalid RSA private key")},t.prototype.generate=function(t,e){var i=new X,r=t>>1;this.e=parseInt(e,16);for(var n=new O(e,16);;){for(;this.p=new O(t-r,1,i),0!=this.p.subtract(O.ONE).gcd(n).compareTo(O.ONE)||!this.p.isProbablePrime(10););for(;this.q=new O(r,1,i),0!=this.q.subtract(O.ONE).gcd(n).compareTo(O.ONE)||!this.q.isProbablePrime(10););if(this.p.compareTo(this.q)<=0){var s=this.p;this.p=this.q,this.q=s}var o=this.p.subtract(O.ONE),h=this.q.subtract(O.ONE),a=o.multiply(h);if(0==a.gcd(n).compareTo(O.ONE)){this.n=this.p.multiply(this.q),this.d=n.modInverse(a),this.dmp1=this.d.mod(o),this.dmq1=this.d.mod(h),this.coeff=this.q.modInverse(this.p);break}}},t.prototype.decrypt=function(t){var e=q(t,16),i=this.doPrivate(e);return null==i?null:function(t,e){var i=t.toByteArray(),r=0;for(;r<i.length&&0==i[r];)++r;if(i.length-r!=e-1||2!=i[r])return null;++r;for(;0!=i[r];)if(++r>=i.length)return null;var n="";for(;++r<i.length;){var s=255&i[r];s<128?n+=String.fromCharCode(s):191<s&&s<224?(n+=String.fromCharCode((31&s)<<6|63&i[r+1]),++r):(n+=String.fromCharCode((15&s)<<12|(63&i[r+1])<<6|63&i[r+2]),r+=2)}return n}(i,this.n.bitLength()+7>>3)},t.prototype.generateAsync=function(t,e,n){var s=new X,o=t>>1;this.e=parseInt(e,16);var h=new O(e,16),a=this,u=function(){var e=function(){if(a.p.compareTo(a.q)<=0){var t=a.p;a.p=a.q,a.q=t}var e=a.p.subtract(O.ONE),i=a.q.subtract(O.ONE),r=e.multiply(i);0==r.gcd(h).compareTo(O.ONE)?(a.n=a.p.multiply(a.q),a.d=h.modInverse(r),a.dmp1=a.d.mod(e),a.dmq1=a.d.mod(i),a.coeff=a.q.modInverse(a.p),setTimeout(function(){n()},0)):setTimeout(u,0)},i=function(){a.q=M(),a.q.fromNumberAsync(o,1,s,function(){a.q.subtract(O.ONE).gcda(h,function(t){0==t.compareTo(O.ONE)&&a.q.isProbablePrime(10)?setTimeout(e,0):setTimeout(i,0)})})},r=function(){a.p=M(),a.p.fromNumberAsync(t-o,1,s,function(){a.p.subtract(O.ONE).gcda(h,function(t){0==t.compareTo(O.ONE)&&a.p.isProbablePrime(10)?setTimeout(i,0):setTimeout(r,0)})})};setTimeout(r,0)};setTimeout(u,0)},t.prototype.sign=function(t,e,i){var r=function(t,e){if(e<t.length+22)return console.error("Message too long for RSA"),null;for(var i=e-t.length-6,r="",n=0;n<i;n+=2)r+="ff";return q("0001"+r+"00"+t,16)}((W[i]||"")+e(t).toString(),this.n.bitLength()/4);if(null==r)return null;var n=this.doPrivate(r);if(null==n)return null;var s=n.toString(16);return 0==(1&s.length)?s:"0"+s},t.prototype.verify=function(t,e,i){var r=q(e,16),n=this.doPublic(r);return null==n?null:function(t){for(var e in W)if(W.hasOwnProperty(e)){var i=W[e],r=i.length;if(t.substr(0,r)==i)return t.substr(r)}return t}(n.toString(16).replace(/^1f+00/,""))==i(t).toString()},t}();var W={md2:"3020300c06082a864886f70d020205000410",md5:"3020300c06082a864886f70d020505000410",sha1:"3021300906052b0e03021a05000414",sha224:"302d300d06096086480165030402040500041c",sha256:"3031300d060960864801650304020105000420",sha384:"3041300d060960864801650304020205000430",sha512:"3051300d060960864801650304020305000440",ripemd160:"3021300906052b2403020105000414"};var tt={};tt.lang={extend:function(t,e,i){if(!e||!t)throw new Error("YAHOO.lang.extend failed, please check that all dependencies are included.");var r=function(){};if(r.prototype=e.prototype,t.prototype=new r,(t.prototype.constructor=t).superclass=e.prototype,e.prototype.constructor==Object.prototype.constructor&&(e.prototype.constructor=e),i){var n;for(n in i)t.prototype[n]=i[n];var s=function(){},o=["toString","valueOf"];try{/MSIE/.test(navigator.userAgent)&&(s=function(t,e){for(n=0;n<o.length;n+=1){var i=o[n],r=e[i];"function"==typeof r&&r!=Object.prototype[i]&&(t[i]=r)}})}catch(t){}s(t.prototype,i)}}};var et={};void 0!==et.asn1&&et.asn1||(et.asn1={}),et.asn1.ASN1Util=new function(){this.integerToByteHex=function(t){var e=t.toString(16);return e.length%2==1&&(e="0"+e),e},this.bigIntToMinTwosComplementsHex=function(t){var e=t.toString(16);if("-"!=e.substr(0,1))e.length%2==1?e="0"+e:e.match(/^[0-7]/)||(e="00"+e);else{var i=e.substr(1).length;i%2==1?i+=1:e.match(/^[0-7]/)||(i+=2);for(var r="",n=0;n<i;n++)r+="f";e=new O(r,16).xor(t).add(O.ONE).toString(16).replace(/^-/,"")}return e},this.getPEMStringFromHex=function(t,e){return hextopem(t,e)},this.newObject=function(t){var e=et.asn1,i=e.DERBoolean,r=e.DERInteger,n=e.DERBitString,s=e.DEROctetString,o=e.DERNull,h=e.DERObjectIdentifier,a=e.DEREnumerated,u=e.DERUTF8String,c=e.DERNumericString,f=e.DERPrintableString,l=e.DERTeletexString,p=e.DERIA5String,g=e.DERUTCTime,d=e.DERGeneralizedTime,v=e.DERSequence,m=e.DERSet,y=e.DERTaggedObject,b=e.ASN1Util.newObject,T=Object.keys(t);if(1!=T.length)throw"key of param shall be only one.";var S=T[0];if(-1==":bool:int:bitstr:octstr:null:oid:enum:utf8str:numstr:prnstr:telstr:ia5str:utctime:gentime:seq:set:tag:".indexOf(":"+S+":"))throw"undefined key: "+S;if("bool"==S)return new i(t[S]);if("int"==S)return new r(t[S]);if("bitstr"==S)return new n(t[S]);if("octstr"==S)return new s(t[S]);if("null"==S)return new o(t[S]);if("oid"==S)return new h(t[S]);if("enum"==S)return new a(t[S]);if("utf8str"==S)return new u(t[S]);if("numstr"==S)return new c(t[S]);if("prnstr"==S)return new f(t[S]);if("telstr"==S)return new l(t[S]);if("ia5str"==S)return new p(t[S]);if("utctime"==S)return new g(t[S]);if("gentime"==S)return new d(t[S]);if("seq"==S){for(var E=t[S],w=[],D=0;D<E.length;D++){var x=b(E[D]);w.push(x)}return new v({array:w})}if("set"==S){for(E=t[S],w=[],D=0;D<E.length;D++){x=b(E[D]);w.push(x)}return new m({array:w})}if("tag"==S){var R=t[S];if("[object Array]"===Object.prototype.toString.call(R)&&3==R.length){var B=b(R[2]);return new y({tag:R[0],explicit:R[1],obj:B})}var A={};if(void 0!==R.explicit&&(A.explicit=R.explicit),void 0!==R.tag&&(A.tag=R.tag),void 0===R.obj)throw"obj shall be specified for 'tag'.";return A.obj=b(R.obj),new y(A)}},this.jsonToASN1HEX=function(t){return this.newObject(t).getEncodedHex()}},et.asn1.ASN1Util.oidHexToInt=function(t){for(var e="",i=parseInt(t.substr(0,2),16),r=(e=Math.floor(i/40)+"."+i%40,""),n=2;n<t.length;n+=2){var s=("00000000"+parseInt(t.substr(n,2),16).toString(2)).slice(-8);if(r+=s.substr(1,7),"0"==s.substr(0,1))e=e+"."+new O(r,2).toString(10),r=""}return e},et.asn1.ASN1Util.oidIntToHex=function(t){var h=function(t){var e=t.toString(16);return 1==e.length&&(e="0"+e),e},e=function(t){var e="",i=new O(t,10).toString(2),r=7-i.length%7;7==r&&(r=0);for(var n="",s=0;s<r;s++)n+="0";i=n+i;for(s=0;s<i.length-1;s+=7){var o=i.substr(s,7);s!=i.length-7&&(o="1"+o),e+=h(parseInt(o,2))}return e};if(!t.match(/^[0-9.]+$/))throw"malformed oid string: "+t;var i="",r=t.split("."),n=40*parseInt(r[0])+parseInt(r[1]);i+=h(n),r.splice(0,2);for(var s=0;s<r.length;s++)i+=e(r[s]);return i},et.asn1.ASN1Object=function(){this.getLengthHexFromValue=function(){if(void 0===this.hV||null==this.hV)throw"this.hV is null or undefined.";if(this.hV.length%2==1)throw"value hex must be even length: n="+"".length+",v="+this.hV;var t=this.hV.length/2,e=t.toString(16);if(e.length%2==1&&(e="0"+e),t<128)return e;var i=e.length/2;if(15<i)throw"ASN.1 length too long to represent by 8x: n = "+t.toString(16);return(128+i).toString(16)+e},this.getEncodedHex=function(){return(null==this.hTLV||this.isModified)&&(this.hV=this.getFreshValueHex(),this.hL=this.getLengthHexFromValue(),this.hTLV=this.hT+this.hL+this.hV,this.isModified=!1),this.hTLV},this.getValueHex=function(){return this.getEncodedHex(),this.hV},this.getFreshValueHex=function(){return""}},et.asn1.DERAbstractString=function(t){et.asn1.DERAbstractString.superclass.constructor.call(this),this.getString=function(){return this.s},this.setString=function(t){this.hTLV=null,this.isModified=!0,this.s=t,this.hV=stohex(this.s)},this.setStringHex=function(t){this.hTLV=null,this.isModified=!0,this.s=null,this.hV=t},this.getFreshValueHex=function(){return this.hV},void 0!==t&&("string"==typeof t?this.setString(t):void 0!==t.str?this.setString(t.str):void 0!==t.hex&&this.setStringHex(t.hex))},tt.lang.extend(et.asn1.DERAbstractString,et.asn1.ASN1Object),et.asn1.DERAbstractTime=function(t){et.asn1.DERAbstractTime.superclass.constructor.call(this),this.localDateToUTC=function(t){return utc=t.getTime()+6e4*t.getTimezoneOffset(),new Date(utc)},this.formatDate=function(t,e,i){var r=this.zeroPadding,n=this.localDateToUTC(t),s=String(n.getFullYear());"utc"==e&&(s=s.substr(2,2));var o=s+r(String(n.getMonth()+1),2)+r(String(n.getDate()),2)+r(String(n.getHours()),2)+r(String(n.getMinutes()),2)+r(String(n.getSeconds()),2);if(!0===i){var h=n.getMilliseconds();if(0!=h){var a=r(String(h),3);o=o+"."+(a=a.replace(/[0]+$/,""))}}return o+"Z"},this.zeroPadding=function(t,e){return t.length>=e?t:new Array(e-t.length+1).join("0")+t},this.getString=function(){return this.s},this.setString=function(t){this.hTLV=null,this.isModified=!0,this.s=t,this.hV=stohex(t)},this.setByDateValue=function(t,e,i,r,n,s){var o=new Date(Date.UTC(t,e-1,i,r,n,s,0));this.setByDate(o)},this.getFreshValueHex=function(){return this.hV}},tt.lang.extend(et.asn1.DERAbstractTime,et.asn1.ASN1Object),et.asn1.DERAbstractStructured=function(t){et.asn1.DERAbstractString.superclass.constructor.call(this),this.setByASN1ObjectArray=function(t){this.hTLV=null,this.isModified=!0,this.asn1Array=t},this.appendASN1Object=function(t){this.hTLV=null,this.isModified=!0,this.asn1Array.push(t)},this.asn1Array=new Array,void 0!==t&&void 0!==t.array&&(this.asn1Array=t.array)},tt.lang.extend(et.asn1.DERAbstractStructured,et.asn1.ASN1Object),et.asn1.DERBoolean=function(){et.asn1.DERBoolean.superclass.constructor.call(this),this.hT="01",this.hTLV="0101ff"},tt.lang.extend(et.asn1.DERBoolean,et.asn1.ASN1Object),et.asn1.DERInteger=function(t){et.asn1.DERInteger.superclass.constructor.call(this),this.hT="02",this.setByBigInteger=function(t){this.hTLV=null,this.isModified=!0,this.hV=et.asn1.ASN1Util.bigIntToMinTwosComplementsHex(t)},this.setByInteger=function(t){var e=new O(String(t),10);this.setByBigInteger(e)},this.setValueHex=function(t){this.hV=t},this.getFreshValueHex=function(){return this.hV},void 0!==t&&(void 0!==t.bigint?this.setByBigInteger(t.bigint):void 0!==t.int?this.setByInteger(t.int):"number"==typeof t?this.setByInteger(t):void 0!==t.hex&&this.setValueHex(t.hex))},tt.lang.extend(et.asn1.DERInteger,et.asn1.ASN1Object),et.asn1.DERBitString=function(t){if(void 0!==t&&void 0!==t.obj){var e=et.asn1.ASN1Util.newObject(t.obj);t.hex="00"+e.getEncodedHex()}et.asn1.DERBitString.superclass.constructor.call(this),this.hT="03",this.setHexValueIncludingUnusedBits=function(t){this.hTLV=null,this.isModified=!0,this.hV=t},this.setUnusedBitsAndHexValue=function(t,e){if(t<0||7<t)throw"unused bits shall be from 0 to 7: u = "+t;var i="0"+t;this.hTLV=null,this.isModified=!0,this.hV=i+e},this.setByBinaryString=function(t){var e=8-(t=t.replace(/0+$/,"")).length%8;8==e&&(e=0);for(var i=0;i<=e;i++)t+="0";var r="";for(i=0;i<t.length-1;i+=8){var n=t.substr(i,8),s=parseInt(n,2).toString(16);1==s.length&&(s="0"+s),r+=s}this.hTLV=null,this.isModified=!0,this.hV="0"+e+r},this.setByBooleanArray=function(t){for(var e="",i=0;i<t.length;i++)1==t[i]?e+="1":e+="0";this.setByBinaryString(e)},this.newFalseArray=function(t){for(var e=new Array(t),i=0;i<t;i++)e[i]=!1;return e},this.getFreshValueHex=function(){return this.hV},void 0!==t&&("string"==typeof t&&t.toLowerCase().match(/^[0-9a-f]+$/)?this.setHexValueIncludingUnusedBits(t):void 0!==t.hex?this.setHexValueIncludingUnusedBits(t.hex):void 0!==t.bin?this.setByBinaryString(t.bin):void 0!==t.array&&this.setByBooleanArray(t.array))},tt.lang.extend(et.asn1.DERBitString,et.asn1.ASN1Object),et.asn1.DEROctetString=function(t){if(void 0!==t&&void 0!==t.obj){var e=et.asn1.ASN1Util.newObject(t.obj);t.hex=e.getEncodedHex()}et.asn1.DEROctetString.superclass.constructor.call(this,t),this.hT="04"},tt.lang.extend(et.asn1.DEROctetString,et.asn1.DERAbstractString),et.asn1.DERNull=function(){et.asn1.DERNull.superclass.constructor.call(this),this.hT="05",this.hTLV="0500"},tt.lang.extend(et.asn1.DERNull,et.asn1.ASN1Object),et.asn1.DERObjectIdentifier=function(t){var h=function(t){var e=t.toString(16);return 1==e.length&&(e="0"+e),e},s=function(t){var e="",i=new O(t,10).toString(2),r=7-i.length%7;7==r&&(r=0);for(var n="",s=0;s<r;s++)n+="0";i=n+i;for(s=0;s<i.length-1;s+=7){var o=i.substr(s,7);s!=i.length-7&&(o="1"+o),e+=h(parseInt(o,2))}return e};et.asn1.DERObjectIdentifier.superclass.constructor.call(this),this.hT="06",this.setValueHex=function(t){this.hTLV=null,this.isModified=!0,this.s=null,this.hV=t},this.setValueOidString=function(t){if(!t.match(/^[0-9.]+$/))throw"malformed oid string: "+t;var e="",i=t.split("."),r=40*parseInt(i[0])+parseInt(i[1]);e+=h(r),i.splice(0,2);for(var n=0;n<i.length;n++)e+=s(i[n]);this.hTLV=null,this.isModified=!0,this.s=null,this.hV=e},this.setValueName=function(t){var e=et.asn1.x509.OID.name2oid(t);if(""===e)throw"DERObjectIdentifier oidName undefined: "+t;this.setValueOidString(e)},this.getFreshValueHex=function(){return this.hV},void 0!==t&&("string"==typeof t?t.match(/^[0-2].[0-9.]+$/)?this.setValueOidString(t):this.setValueName(t):void 0!==t.oid?this.setValueOidString(t.oid):void 0!==t.hex?this.setValueHex(t.hex):void 0!==t.name&&this.setValueName(t.name))},tt.lang.extend(et.asn1.DERObjectIdentifier,et.asn1.ASN1Object),et.asn1.DEREnumerated=function(t){et.asn1.DEREnumerated.superclass.constructor.call(this),this.hT="0a",this.setByBigInteger=function(t){this.hTLV=null,this.isModified=!0,this.hV=et.asn1.ASN1Util.bigIntToMinTwosComplementsHex(t)},this.setByInteger=function(t){var e=new O(String(t),10);this.setByBigInteger(e)},this.setValueHex=function(t){this.hV=t},this.getFreshValueHex=function(){return this.hV},void 0!==t&&(void 0!==t.int?this.setByInteger(t.int):"number"==typeof t?this.setByInteger(t):void 0!==t.hex&&this.setValueHex(t.hex))},tt.lang.extend(et.asn1.DEREnumerated,et.asn1.ASN1Object),et.asn1.DERUTF8String=function(t){et.asn1.DERUTF8String.superclass.constructor.call(this,t),this.hT="0c"},tt.lang.extend(et.asn1.DERUTF8String,et.asn1.DERAbstractString),et.asn1.DERNumericString=function(t){et.asn1.DERNumericString.superclass.constructor.call(this,t),this.hT="12"},tt.lang.extend(et.asn1.DERNumericString,et.asn1.DERAbstractString),et.asn1.DERPrintableString=function(t){et.asn1.DERPrintableString.superclass.constructor.call(this,t),this.hT="13"},tt.lang.extend(et.asn1.DERPrintableString,et.asn1.DERAbstractString),et.asn1.DERTeletexString=function(t){et.asn1.DERTeletexString.superclass.constructor.call(this,t),this.hT="14"},tt.lang.extend(et.asn1.DERTeletexString,et.asn1.DERAbstractString),et.asn1.DERIA5String=function(t){et.asn1.DERIA5String.superclass.constructor.call(this,t),this.hT="16"},tt.lang.extend(et.asn1.DERIA5String,et.asn1.DERAbstractString),et.asn1.DERUTCTime=function(t){et.asn1.DERUTCTime.superclass.constructor.call(this,t),this.hT="17",this.setByDate=function(t){this.hTLV=null,this.isModified=!0,this.date=t,this.s=this.formatDate(this.date,"utc"),this.hV=stohex(this.s)},this.getFreshValueHex=function(){return void 0===this.date&&void 0===this.s&&(this.date=new Date,this.s=this.formatDate(this.date,"utc"),this.hV=stohex(this.s)),this.hV},void 0!==t&&(void 0!==t.str?this.setString(t.str):"string"==typeof t&&t.match(/^[0-9]{12}Z$/)?this.setString(t):void 0!==t.hex?this.setStringHex(t.hex):void 0!==t.date&&this.setByDate(t.date))},tt.lang.extend(et.asn1.DERUTCTime,et.asn1.DERAbstractTime),et.asn1.DERGeneralizedTime=function(t){et.asn1.DERGeneralizedTime.superclass.constructor.call(this,t),this.hT="18",this.withMillis=!1,this.setByDate=function(t){this.hTLV=null,this.isModified=!0,this.date=t,this.s=this.formatDate(this.date,"gen",this.withMillis),this.hV=stohex(this.s)},this.getFreshValueHex=function(){return void 0===this.date&&void 0===this.s&&(this.date=new Date,this.s=this.formatDate(this.date,"gen",this.withMillis),this.hV=stohex(this.s)),this.hV},void 0!==t&&(void 0!==t.str?this.setString(t.str):"string"==typeof t&&t.match(/^[0-9]{14}Z$/)?this.setString(t):void 0!==t.hex?this.setStringHex(t.hex):void 0!==t.date&&this.setByDate(t.date),!0===t.millis&&(this.withMillis=!0))},tt.lang.extend(et.asn1.DERGeneralizedTime,et.asn1.DERAbstractTime),et.asn1.DERSequence=function(t){et.asn1.DERSequence.superclass.constructor.call(this,t),this.hT="30",this.getFreshValueHex=function(){for(var t="",e=0;e<this.asn1Array.length;e++){t+=this.asn1Array[e].getEncodedHex()}return this.hV=t,this.hV}},tt.lang.extend(et.asn1.DERSequence,et.asn1.DERAbstractStructured),et.asn1.DERSet=function(t){et.asn1.DERSet.superclass.constructor.call(this,t),this.hT="31",this.sortFlag=!0,this.getFreshValueHex=function(){for(var t=new Array,e=0;e<this.asn1Array.length;e++){var i=this.asn1Array[e];t.push(i.getEncodedHex())}return 1==this.sortFlag&&t.sort(),this.hV=t.join(""),this.hV},void 0!==t&&void 0!==t.sortflag&&0==t.sortflag&&(this.sortFlag=!1)},tt.lang.extend(et.asn1.DERSet,et.asn1.DERAbstractStructured),et.asn1.DERTaggedObject=function(t){et.asn1.DERTaggedObject.superclass.constructor.call(this),this.hT="a0",this.hV="",this.isExplicit=!0,this.asn1Object=null,this.setASN1Object=function(t,e,i){this.hT=e,this.isExplicit=t,this.asn1Object=i,this.isExplicit?(this.hV=this.asn1Object.getEncodedHex(),this.hTLV=null,this.isModified=!0):(this.hV=null,this.hTLV=i.getEncodedHex(),this.hTLV=this.hTLV.replace(/^../,e),this.isModified=!1)},this.getFreshValueHex=function(){return this.hV},void 0!==t&&(void 0!==t.tag&&(this.hT=t.tag),void 0!==t.explicit&&(this.isExplicit=t.explicit),void 0!==t.obj&&(this.asn1Object=t.obj,this.setASN1Object(this.isExplicit,this.hT,this.asn1Object)))},tt.lang.extend(et.asn1.DERTaggedObject,et.asn1.ASN1Object);var it=function(i){function r(t){var e=i.call(this)||this;return t&&("string"==typeof t?e.parseKey(t):(r.hasPrivateKeyProperty(t)||r.hasPublicKeyProperty(t))&&e.parsePropertiesFrom(t)),e}return function(t,e){function i(){this.constructor=t}p(t,e),t.prototype=null===e?Object.create(e):(i.prototype=e.prototype,new i)}(r,i),r.prototype.parseKey=function(t){try{var e=0,i=0,r=/^\s*(?:[0-9A-Fa-f][0-9A-Fa-f]\s*)+$/.test(t)?d(t):v.unarmor(t),n=x.decode(r);if(3===n.sub.length&&(n=n.sub[2].sub[0]),9===n.sub.length){e=n.sub[1].getHexStringValue(),this.n=q(e,16),i=n.sub[2].getHexStringValue(),this.e=parseInt(i,16);var s=n.sub[3].getHexStringValue();this.d=q(s,16);var o=n.sub[4].getHexStringValue();this.p=q(o,16);var h=n.sub[5].getHexStringValue();this.q=q(h,16);var a=n.sub[6].getHexStringValue();this.dmp1=q(a,16);var u=n.sub[7].getHexStringValue();this.dmq1=q(u,16);var c=n.sub[8].getHexStringValue();this.coeff=q(c,16)}else{if(2!==n.sub.length)return!1;var f=n.sub[1].sub[0];e=f.sub[0].getHexStringValue(),this.n=q(e,16),i=f.sub[1].getHexStringValue(),this.e=parseInt(i,16)}return!0}catch(t){return!1}},r.prototype.getPrivateBaseKey=function(){var t={array:[new et.asn1.DERInteger({int:0}),new et.asn1.DERInteger({bigint:this.n}),new et.asn1.DERInteger({int:this.e}),new et.asn1.DERInteger({bigint:this.d}),new et.asn1.DERInteger({bigint:this.p}),new et.asn1.DERInteger({bigint:this.q}),new et.asn1.DERInteger({bigint:this.dmp1}),new et.asn1.DERInteger({bigint:this.dmq1}),new et.asn1.DERInteger({bigint:this.coeff})]};return new et.asn1.DERSequence(t).getEncodedHex()},r.prototype.getPrivateBaseKeyB64=function(){return c(this.getPrivateBaseKey())},r.prototype.getPublicBaseKey=function(){var t=new et.asn1.DERSequence({array:[new et.asn1.DERObjectIdentifier({oid:"1.2.840.113549.1.1.1"}),new et.asn1.DERNull]}),e=new et.asn1.DERSequence({array:[new et.asn1.DERInteger({bigint:this.n}),new et.asn1.DERInteger({int:this.e})]}),i=new et.asn1.DERBitString({hex:"00"+e.getEncodedHex()});return new et.asn1.DERSequence({array:[t,i]}).getEncodedHex()},r.prototype.getPublicBaseKeyB64=function(){return c(this.getPublicBaseKey())},r.wordwrap=function(t,e){if(!t)return t;var i="(.{1,"+(e=e||64)+"})( +|$\n?)|(.{1,"+e+"})";return t.match(RegExp(i,"g")).join("\n")},r.prototype.getPrivateKey=function(){var t="-----BEGIN RSA PRIVATE KEY-----\n";return t+=r.wordwrap(this.getPrivateBaseKeyB64())+"\n",t+="-----END RSA PRIVATE KEY-----"},r.prototype.getPublicKey=function(){var t="-----BEGIN PUBLIC KEY-----\n";return t+=r.wordwrap(this.getPublicBaseKeyB64())+"\n",t+="-----END PUBLIC KEY-----"},r.hasPublicKeyProperty=function(t){return(t=t||{}).hasOwnProperty("n")&&t.hasOwnProperty("e")},r.hasPrivateKeyProperty=function(t){return(t=t||{}).hasOwnProperty("n")&&t.hasOwnProperty("e")&&t.hasOwnProperty("d")&&t.hasOwnProperty("p")&&t.hasOwnProperty("q")&&t.hasOwnProperty("dmp1")&&t.hasOwnProperty("dmq1")&&t.hasOwnProperty("coeff")},r.prototype.parsePropertiesFrom=function(t){this.n=t.n,this.e=t.e,t.hasOwnProperty("d")&&(this.d=t.d,this.p=t.p,this.q=t.q,this.dmp1=t.dmp1,this.dmq1=t.dmq1,this.coeff=t.coeff)},r}(Q),rt=function(){function t(t){t=t||{},this.default_key_size=parseInt(t.default_key_size,10)||1024,this.default_public_exponent=t.default_public_exponent||"010001",this.log=t.log||!1,this.key=null}return t.prototype.setKey=function(t){this.log&&this.key&&console.warn("A key was already set, overriding existing."),this.key=new it(t)},t.prototype.setPrivateKey=function(t){this.setKey(t)},t.prototype.setPublicKey=function(t){this.setKey(t)},t.prototype.decrypt=function(t){try{return this.getKey().decrypt(f(t))}catch(t){return!1}},t.prototype.encrypt=function(t){try{return c(this.getKey().encrypt(t))}catch(t){return!1}},t.prototype.sign=function(t,e,i){try{return c(this.getKey().sign(t,e,i))}catch(t){return!1}},t.prototype.verify=function(t,e,i){try{return this.getKey().verify(t,f(e),i)}catch(t){return!1}},t.prototype.getKey=function(t){if(!this.key){if(this.key=new it,t&&"[object Function]"==={}.toString.call(t))return void this.key.generateAsync(this.default_key_size,this.default_public_exponent,t);this.key.generate(this.default_key_size,this.default_public_exponent)}return this.key},t.prototype.getPrivateKey=function(){return this.getKey().getPrivateKey()},t.prototype.getPrivateKeyB64=function(){return this.getKey().getPrivateBaseKeyB64()},t.prototype.getPublicKey=function(){return this.getKey().getPublicKey()},t.prototype.getPublicKeyB64=function(){return this.getKey().getPublicBaseKeyB64()},t.version="3.0.0-rc.1",t}();window.JSEncrypt=rt,t.JSEncrypt=rt,t.default=rt,Object.defineProperty(t,"__esModule",{value:!0})});
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
/*! jQuery v3.5.1 | (c) JS Foundation and other contributors | jquery.org/license */
!function(e,t){"use strict";"object"==typeof module&&"object"==typeof module.exports?module.exports=e.document?t(e,!0):function(e){if(!e.document)throw new Error("jQuery requires a window with a document");return t(e)}:t(e)}("undefined"!=typeof window?window:this,function(C,e){"use strict";var t=[],r=Object.getPrototypeOf,s=t.slice,g=t.flat?function(e){return t.flat.call(e)}:function(e){return t.concat.apply([],e)},u=t.push,i=t.indexOf,n={},o=n.toString,v=n.hasOwnProperty,a=v.toString,l=a.call(Object),y={},m=function(e){return"function"==typeof e&&"number"!=typeof e.nodeType},x=function(e){return null!=e&&e===e.window},E=C.document,c={type:!0,src:!0,nonce:!0,noModule:!0};function b(e,t,n){var r,i,o=(n=n||E).createElement("script");if(o.text=e,t)for(r in c)(i=t[r]||t.getAttribute&&t.getAttribute(r))&&o.setAttribute(r,i);n.head.appendChild(o).parentNode.removeChild(o)}function w(e){return null==e?e+"":"object"==typeof e||"function"==typeof e?n[o.call(e)]||"object":typeof e}var f="3.5.1",S=function(e,t){return new S.fn.init(e,t)};function p(e){var t=!!e&&"length"in e&&e.length,n=w(e);return!m(e)&&!x(e)&&("array"===n||0===t||"number"==typeof t&&0<t&&t-1 in e)}S.fn=S.prototype={jquery:f,constructor:S,length:0,toArray:function(){return s.call(this)},get:function(e){return null==e?s.call(this):e<0?this[e+this.length]:this[e]},pushStack:function(e){var t=S.merge(this.constructor(),e);return t.prevObject=this,t},each:function(e){return S.each(this,e)},map:function(n){return this.pushStack(S.map(this,function(e,t){return n.call(e,t,e)}))},slice:function(){return this.pushStack(s.apply(this,arguments))},first:function(){return this.eq(0)},last:function(){return this.eq(-1)},even:function(){return this.pushStack(S.grep(this,function(e,t){return(t+1)%2}))},odd:function(){return this.pushStack(S.grep(this,function(e,t){return t%2}))},eq:function(e){var t=this.length,n=+e+(e<0?t:0);return this.pushStack(0<=n&&n<t?[this[n]]:[])},end:function(){return this.prevObject||this.constructor()},push:u,sort:t.sort,splice:t.splice},S.extend=S.fn.extend=function(){var e,t,n,r,i,o,a=arguments[0]||{},s=1,u=arguments.length,l=!1;for("boolean"==typeof a&&(l=a,a=arguments[s]||{},s++),"object"==typeof a||m(a)||(a={}),s===u&&(a=this,s--);s<u;s++)if(null!=(e=arguments[s]))for(t in e)r=e[t],"__proto__"!==t&&a!==r&&(l&&r&&(S.isPlainObject(r)||(i=Array.isArray(r)))?(n=a[t],o=i&&!Array.isArray(n)?[]:i||S.isPlainObject(n)?n:{},i=!1,a[t]=S.extend(l,o,r)):void 0!==r&&(a[t]=r));return a},S.extend({expando:"jQuery"+(f+Math.random()).replace(/\D/g,""),isReady:!0,error:function(e){throw new Error(e)},noop:function(){},isPlainObject:function(e){var t,n;return!(!e||"[object Object]"!==o.call(e))&&(!(t=r(e))||"function"==typeof(n=v.call(t,"constructor")&&t.constructor)&&a.call(n)===l)},isEmptyObject:function(e){var t;for(t in e)return!1;return!0},globalEval:function(e,t,n){b(e,{nonce:t&&t.nonce},n)},each:function(e,t){var n,r=0;if(p(e)){for(n=e.length;r<n;r++)if(!1===t.call(e[r],r,e[r]))break}else for(r in e)if(!1===t.call(e[r],r,e[r]))break;return e},makeArray:function(e,t){var n=t||[];return null!=e&&(p(Object(e))?S.merge(n,"string"==typeof e?[e]:e):u.call(n,e)),n},inArray:function(e,t,n){return null==t?-1:i.call(t,e,n)},merge:function(e,t){for(var n=+t.length,r=0,i=e.length;r<n;r++)e[i++]=t[r];return e.length=i,e},grep:function(e,t,n){for(var r=[],i=0,o=e.length,a=!n;i<o;i++)!t(e[i],i)!==a&&r.push(e[i]);return r},map:function(e,t,n){var r,i,o=0,a=[];if(p(e))for(r=e.length;o<r;o++)null!=(i=t(e[o],o,n))&&a.push(i);else for(o in e)null!=(i=t(e[o],o,n))&&a.push(i);return g(a)},guid:1,support:y}),"function"==typeof Symbol&&(S.fn[Symbol.iterator]=t[Symbol.iterator]),S.each("Boolean Number String Function Array Date RegExp Object Error Symbol".split(" "),function(e,t){n["[object "+t+"]"]=t.toLowerCase()});var d=function(n){var e,d,b,o,i,h,f,g,w,u,l,T,C,a,E,v,s,c,y,S="sizzle"+1*new Date,p=n.document,k=0,r=0,m=ue(),x=ue(),A=ue(),N=ue(),D=function(e,t){return e===t&&(l=!0),0},j={}.hasOwnProperty,t=[],q=t.pop,L=t.push,H=t.push,O=t.slice,P=function(e,t){for(var n=0,r=e.length;n<r;n++)if(e[n]===t)return n;return-1},R="checked|selected|async|autofocus|autoplay|controls|defer|disabled|hidden|ismap|loop|multiple|open|readonly|required|scoped",M="[\\x20\\t\\r\\n\\f]",I="(?:\\\\[\\da-fA-F]{1,6}"+M+"?|\\\\[^\\r\\n\\f]|[\\w-]|[^\0-\\x7f])+",W="\\["+M+"*("+I+")(?:"+M+"*([*^$|!~]?=)"+M+"*(?:'((?:\\\\.|[^\\\\'])*)'|\"((?:\\\\.|[^\\\\\"])*)\"|("+I+"))|)"+M+"*\\]",F=":("+I+")(?:\\((('((?:\\\\.|[^\\\\'])*)'|\"((?:\\\\.|[^\\\\\"])*)\")|((?:\\\\.|[^\\\\()[\\]]|"+W+")*)|.*)\\)|)",B=new RegExp(M+"+","g"),$=new RegExp("^"+M+"+|((?:^|[^\\\\])(?:\\\\.)*)"+M+"+$","g"),_=new RegExp("^"+M+"*,"+M+"*"),z=new RegExp("^"+M+"*([>+~]|"+M+")"+M+"*"),U=new RegExp(M+"|>"),X=new RegExp(F),V=new RegExp("^"+I+"$"),G={ID:new RegExp("^#("+I+")"),CLASS:new RegExp("^\\.("+I+")"),TAG:new RegExp("^("+I+"|[*])"),ATTR:new RegExp("^"+W),PSEUDO:new RegExp("^"+F),CHILD:new RegExp("^:(only|first|last|nth|nth-last)-(child|of-type)(?:\\("+M+"*(even|odd|(([+-]|)(\\d*)n|)"+M+"*(?:([+-]|)"+M+"*(\\d+)|))"+M+"*\\)|)","i"),bool:new RegExp("^(?:"+R+")$","i"),needsContext:new RegExp("^"+M+"*[>+~]|:(even|odd|eq|gt|lt|nth|first|last)(?:\\("+M+"*((?:-\\d)?\\d*)"+M+"*\\)|)(?=[^-]|$)","i")},Y=/HTML$/i,Q=/^(?:input|select|textarea|button)$/i,J=/^h\d$/i,K=/^[^{]+\{\s*\[native \w/,Z=/^(?:#([\w-]+)|(\w+)|\.([\w-]+))$/,ee=/[+~]/,te=new RegExp("\\\\[\\da-fA-F]{1,6}"+M+"?|\\\\([^\\r\\n\\f])","g"),ne=function(e,t){var n="0x"+e.slice(1)-65536;return t||(n<0?String.fromCharCode(n+65536):String.fromCharCode(n>>10|55296,1023&n|56320))},re=/([\0-\x1f\x7f]|^-?\d)|^-$|[^\0-\x1f\x7f-\uFFFF\w-]/g,ie=function(e,t){return t?"\0"===e?"\ufffd":e.slice(0,-1)+"\\"+e.charCodeAt(e.length-1).toString(16)+" ":"\\"+e},oe=function(){T()},ae=be(function(e){return!0===e.disabled&&"fieldset"===e.nodeName.toLowerCase()},{dir:"parentNode",next:"legend"});try{H.apply(t=O.call(p.childNodes),p.childNodes),t[p.childNodes.length].nodeType}catch(e){H={apply:t.length?function(e,t){L.apply(e,O.call(t))}:function(e,t){var n=e.length,r=0;while(e[n++]=t[r++]);e.length=n-1}}}function se(t,e,n,r){var i,o,a,s,u,l,c,f=e&&e.ownerDocument,p=e?e.nodeType:9;if(n=n||[],"string"!=typeof t||!t||1!==p&&9!==p&&11!==p)return n;if(!r&&(T(e),e=e||C,E)){if(11!==p&&(u=Z.exec(t)))if(i=u[1]){if(9===p){if(!(a=e.getElementById(i)))return n;if(a.id===i)return n.push(a),n}else if(f&&(a=f.getElementById(i))&&y(e,a)&&a.id===i)return n.push(a),n}else{if(u[2])return H.apply(n,e.getElementsByTagName(t)),n;if((i=u[3])&&d.getElementsByClassName&&e.getElementsByClassName)return H.apply(n,e.getElementsByClassName(i)),n}if(d.qsa&&!N[t+" "]&&(!v||!v.test(t))&&(1!==p||"object"!==e.nodeName.toLowerCase())){if(c=t,f=e,1===p&&(U.test(t)||z.test(t))){(f=ee.test(t)&&ye(e.parentNode)||e)===e&&d.scope||((s=e.getAttribute("id"))?s=s.replace(re,ie):e.setAttribute("id",s=S)),o=(l=h(t)).length;while(o--)l[o]=(s?"#"+s:":scope")+" "+xe(l[o]);c=l.join(",")}try{return H.apply(n,f.querySelectorAll(c)),n}catch(e){N(t,!0)}finally{s===S&&e.removeAttribute("id")}}}return g(t.replace($,"$1"),e,n,r)}function ue(){var r=[];return function e(t,n){return r.push(t+" ")>b.cacheLength&&delete e[r.shift()],e[t+" "]=n}}function le(e){return e[S]=!0,e}function ce(e){var t=C.createElement("fieldset");try{return!!e(t)}catch(e){return!1}finally{t.parentNode&&t.parentNode.removeChild(t),t=null}}function fe(e,t){var n=e.split("|"),r=n.length;while(r--)b.attrHandle[n[r]]=t}function pe(e,t){var n=t&&e,r=n&&1===e.nodeType&&1===t.nodeType&&e.sourceIndex-t.sourceIndex;if(r)return r;if(n)while(n=n.nextSibling)if(n===t)return-1;return e?1:-1}function de(t){return function(e){return"input"===e.nodeName.toLowerCase()&&e.type===t}}function he(n){return function(e){var t=e.nodeName.toLowerCase();return("input"===t||"button"===t)&&e.type===n}}function ge(t){return function(e){return"form"in e?e.parentNode&&!1===e.disabled?"label"in e?"label"in e.parentNode?e.parentNode.disabled===t:e.disabled===t:e.isDisabled===t||e.isDisabled!==!t&&ae(e)===t:e.disabled===t:"label"in e&&e.disabled===t}}function ve(a){return le(function(o){return o=+o,le(function(e,t){var n,r=a([],e.length,o),i=r.length;while(i--)e[n=r[i]]&&(e[n]=!(t[n]=e[n]))})})}function ye(e){return e&&"undefined"!=typeof e.getElementsByTagName&&e}for(e in d=se.support={},i=se.isXML=function(e){var t=e.namespaceURI,n=(e.ownerDocument||e).documentElement;return!Y.test(t||n&&n.nodeName||"HTML")},T=se.setDocument=function(e){var t,n,r=e?e.ownerDocument||e:p;return r!=C&&9===r.nodeType&&r.documentElement&&(a=(C=r).documentElement,E=!i(C),p!=C&&(n=C.defaultView)&&n.top!==n&&(n.addEventListener?n.addEventListener("unload",oe,!1):n.attachEvent&&n.attachEvent("onunload",oe)),d.scope=ce(function(e){return a.appendChild(e).appendChild(C.createElement("div")),"undefined"!=typeof e.querySelectorAll&&!e.querySelectorAll(":scope fieldset div").length}),d.attributes=ce(function(e){return e.className="i",!e.getAttribute("className")}),d.getElementsByTagName=ce(function(e){return e.appendChild(C.createComment("")),!e.getElementsByTagName("*").length}),d.getElementsByClassName=K.test(C.getElementsByClassName),d.getById=ce(function(e){return a.appendChild(e).id=S,!C.getElementsByName||!C.getElementsByName(S).length}),d.getById?(b.filter.ID=function(e){var t=e.replace(te,ne);return function(e){return e.getAttribute("id")===t}},b.find.ID=function(e,t){if("undefined"!=typeof t.getElementById&&E){var n=t.getElementById(e);return n?[n]:[]}}):(b.filter.ID=function(e){var n=e.replace(te,ne);return function(e){var t="undefined"!=typeof e.getAttributeNode&&e.getAttributeNode("id");return t&&t.value===n}},b.find.ID=function(e,t){if("undefined"!=typeof t.getElementById&&E){var n,r,i,o=t.getElementById(e);if(o){if((n=o.getAttributeNode("id"))&&n.value===e)return[o];i=t.getElementsByName(e),r=0;while(o=i[r++])if((n=o.getAttributeNode("id"))&&n.value===e)return[o]}return[]}}),b.find.TAG=d.getElementsByTagName?function(e,t){return"undefined"!=typeof t.getElementsByTagName?t.getElementsByTagName(e):d.qsa?t.querySelectorAll(e):void 0}:function(e,t){var n,r=[],i=0,o=t.getElementsByTagName(e);if("*"===e){while(n=o[i++])1===n.nodeType&&r.push(n);return r}return o},b.find.CLASS=d.getElementsByClassName&&function(e,t){if("undefined"!=typeof t.getElementsByClassName&&E)return t.getElementsByClassName(e)},s=[],v=[],(d.qsa=K.test(C.querySelectorAll))&&(ce(function(e){var t;a.appendChild(e).innerHTML="<a id='"+S+"'></a><select id='"+S+"-\r\\' msallowcapture=''><option selected=''></option></select>",e.querySelectorAll("[msallowcapture^='']").length&&v.push("[*^$]="+M+"*(?:''|\"\")"),e.querySelectorAll("[selected]").length||v.push("\\["+M+"*(?:value|"+R+")"),e.querySelectorAll("[id~="+S+"-]").length||v.push("~="),(t=C.createElement("input")).setAttribute("name",""),e.appendChild(t),e.querySelectorAll("[name='']").length||v.push("\\["+M+"*name"+M+"*="+M+"*(?:''|\"\")"),e.querySelectorAll(":checked").length||v.push(":checked"),e.querySelectorAll("a#"+S+"+*").length||v.push(".#.+[+~]"),e.querySelectorAll("\\\f"),v.push("[\\r\\n\\f]")}),ce(function(e){e.innerHTML="<a href='' disabled='disabled'></a><select disabled='disabled'><option/></select>";var t=C.createElement("input");t.setAttribute("type","hidden"),e.appendChild(t).setAttribute("name","D"),e.querySelectorAll("[name=d]").length&&v.push("name"+M+"*[*^$|!~]?="),2!==e.querySelectorAll(":enabled").length&&v.push(":enabled",":disabled"),a.appendChild(e).disabled=!0,2!==e.querySelectorAll(":disabled").length&&v.push(":enabled",":disabled"),e.querySelectorAll("*,:x"),v.push(",.*:")})),(d.matchesSelector=K.test(c=a.matches||a.webkitMatchesSelector||a.mozMatchesSelector||a.oMatchesSelector||a.msMatchesSelector))&&ce(function(e){d.disconnectedMatch=c.call(e,"*"),c.call(e,"[s!='']:x"),s.push("!=",F)}),v=v.length&&new RegExp(v.join("|")),s=s.length&&new RegExp(s.join("|")),t=K.test(a.compareDocumentPosition),y=t||K.test(a.contains)?function(e,t){var n=9===e.nodeType?e.documentElement:e,r=t&&t.parentNode;return e===r||!(!r||1!==r.nodeType||!(n.contains?n.contains(r):e.compareDocumentPosition&&16&e.compareDocumentPosition(r)))}:function(e,t){if(t)while(t=t.parentNode)if(t===e)return!0;return!1},D=t?function(e,t){if(e===t)return l=!0,0;var n=!e.compareDocumentPosition-!t.compareDocumentPosition;return n||(1&(n=(e.ownerDocument||e)==(t.ownerDocument||t)?e.compareDocumentPosition(t):1)||!d.sortDetached&&t.compareDocumentPosition(e)===n?e==C||e.ownerDocument==p&&y(p,e)?-1:t==C||t.ownerDocument==p&&y(p,t)?1:u?P(u,e)-P(u,t):0:4&n?-1:1)}:function(e,t){if(e===t)return l=!0,0;var n,r=0,i=e.parentNode,o=t.parentNode,a=[e],s=[t];if(!i||!o)return e==C?-1:t==C?1:i?-1:o?1:u?P(u,e)-P(u,t):0;if(i===o)return pe(e,t);n=e;while(n=n.parentNode)a.unshift(n);n=t;while(n=n.parentNode)s.unshift(n);while(a[r]===s[r])r++;return r?pe(a[r],s[r]):a[r]==p?-1:s[r]==p?1:0}),C},se.matches=function(e,t){return se(e,null,null,t)},se.matchesSelector=function(e,t){if(T(e),d.matchesSelector&&E&&!N[t+" "]&&(!s||!s.test(t))&&(!v||!v.test(t)))try{var n=c.call(e,t);if(n||d.disconnectedMatch||e.document&&11!==e.document.nodeType)return n}catch(e){N(t,!0)}return 0<se(t,C,null,[e]).length},se.contains=function(e,t){return(e.ownerDocument||e)!=C&&T(e),y(e,t)},se.attr=function(e,t){(e.ownerDocument||e)!=C&&T(e);var n=b.attrHandle[t.toLowerCase()],r=n&&j.call(b.attrHandle,t.toLowerCase())?n(e,t,!E):void 0;return void 0!==r?r:d.attributes||!E?e.getAttribute(t):(r=e.getAttributeNode(t))&&r.specified?r.value:null},se.escape=function(e){return(e+"").replace(re,ie)},se.error=function(e){throw new Error("Syntax error, unrecognized expression: "+e)},se.uniqueSort=function(e){var t,n=[],r=0,i=0;if(l=!d.detectDuplicates,u=!d.sortStable&&e.slice(0),e.sort(D),l){while(t=e[i++])t===e[i]&&(r=n.push(i));while(r--)e.splice(n[r],1)}return u=null,e},o=se.getText=function(e){var t,n="",r=0,i=e.nodeType;if(i){if(1===i||9===i||11===i){if("string"==typeof e.textContent)return e.textContent;for(e=e.firstChild;e;e=e.nextSibling)n+=o(e)}else if(3===i||4===i)return e.nodeValue}else while(t=e[r++])n+=o(t);return n},(b=se.selectors={cacheLength:50,createPseudo:le,match:G,attrHandle:{},find:{},relative:{">":{dir:"parentNode",first:!0}," ":{dir:"parentNode"},"+":{dir:"previousSibling",first:!0},"~":{dir:"previousSibling"}},preFilter:{ATTR:function(e){return e[1]=e[1].replace(te,ne),e[3]=(e[3]||e[4]||e[5]||"").replace(te,ne),"~="===e[2]&&(e[3]=" "+e[3]+" "),e.slice(0,4)},CHILD:function(e){return e[1]=e[1].toLowerCase(),"nth"===e[1].slice(0,3)?(e[3]||se.error(e[0]),e[4]=+(e[4]?e[5]+(e[6]||1):2*("even"===e[3]||"odd"===e[3])),e[5]=+(e[7]+e[8]||"odd"===e[3])):e[3]&&se.error(e[0]),e},PSEUDO:function(e){var t,n=!e[6]&&e[2];return G.CHILD.test(e[0])?null:(e[3]?e[2]=e[4]||e[5]||"":n&&X.test(n)&&(t=h(n,!0))&&(t=n.indexOf(")",n.length-t)-n.length)&&(e[0]=e[0].slice(0,t),e[2]=n.slice(0,t)),e.slice(0,3))}},filter:{TAG:function(e){var t=e.replace(te,ne).toLowerCase();return"*"===e?function(){return!0}:function(e){return e.nodeName&&e.nodeName.toLowerCase()===t}},CLASS:function(e){var t=m[e+" "];return t||(t=new RegExp("(^|"+M+")"+e+"("+M+"|$)"))&&m(e,function(e){return t.test("string"==typeof e.className&&e.className||"undefined"!=typeof e.getAttribute&&e.getAttribute("class")||"")})},ATTR:function(n,r,i){return function(e){var t=se.attr(e,n);return null==t?"!="===r:!r||(t+="","="===r?t===i:"!="===r?t!==i:"^="===r?i&&0===t.indexOf(i):"*="===r?i&&-1<t.indexOf(i):"$="===r?i&&t.slice(-i.length)===i:"~="===r?-1<(" "+t.replace(B," ")+" ").indexOf(i):"|="===r&&(t===i||t.slice(0,i.length+1)===i+"-"))}},CHILD:function(h,e,t,g,v){var y="nth"!==h.slice(0,3),m="last"!==h.slice(-4),x="of-type"===e;return 1===g&&0===v?function(e){return!!e.parentNode}:function(e,t,n){var r,i,o,a,s,u,l=y!==m?"nextSibling":"previousSibling",c=e.parentNode,f=x&&e.nodeName.toLowerCase(),p=!n&&!x,d=!1;if(c){if(y){while(l){a=e;while(a=a[l])if(x?a.nodeName.toLowerCase()===f:1===a.nodeType)return!1;u=l="only"===h&&!u&&"nextSibling"}return!0}if(u=[m?c.firstChild:c.lastChild],m&&p){d=(s=(r=(i=(o=(a=c)[S]||(a[S]={}))[a.uniqueID]||(o[a.uniqueID]={}))[h]||[])[0]===k&&r[1])&&r[2],a=s&&c.childNodes[s];while(a=++s&&a&&a[l]||(d=s=0)||u.pop())if(1===a.nodeType&&++d&&a===e){i[h]=[k,s,d];break}}else if(p&&(d=s=(r=(i=(o=(a=e)[S]||(a[S]={}))[a.uniqueID]||(o[a.uniqueID]={}))[h]||[])[0]===k&&r[1]),!1===d)while(a=++s&&a&&a[l]||(d=s=0)||u.pop())if((x?a.nodeName.toLowerCase()===f:1===a.nodeType)&&++d&&(p&&((i=(o=a[S]||(a[S]={}))[a.uniqueID]||(o[a.uniqueID]={}))[h]=[k,d]),a===e))break;return(d-=v)===g||d%g==0&&0<=d/g}}},PSEUDO:function(e,o){var t,a=b.pseudos[e]||b.setFilters[e.toLowerCase()]||se.error("unsupported pseudo: "+e);return a[S]?a(o):1<a.length?(t=[e,e,"",o],b.setFilters.hasOwnProperty(e.toLowerCase())?le(function(e,t){var n,r=a(e,o),i=r.length;while(i--)e[n=P(e,r[i])]=!(t[n]=r[i])}):function(e){return a(e,0,t)}):a}},pseudos:{not:le(function(e){var r=[],i=[],s=f(e.replace($,"$1"));return s[S]?le(function(e,t,n,r){var i,o=s(e,null,r,[]),a=e.length;while(a--)(i=o[a])&&(e[a]=!(t[a]=i))}):function(e,t,n){return r[0]=e,s(r,null,n,i),r[0]=null,!i.pop()}}),has:le(function(t){return function(e){return 0<se(t,e).length}}),contains:le(function(t){return t=t.replace(te,ne),function(e){return-1<(e.textContent||o(e)).indexOf(t)}}),lang:le(function(n){return V.test(n||"")||se.error("unsupported lang: "+n),n=n.replace(te,ne).toLowerCase(),function(e){var t;do{if(t=E?e.lang:e.getAttribute("xml:lang")||e.getAttribute("lang"))return(t=t.toLowerCase())===n||0===t.indexOf(n+"-")}while((e=e.parentNode)&&1===e.nodeType);return!1}}),target:function(e){var t=n.location&&n.location.hash;return t&&t.slice(1)===e.id},root:function(e){return e===a},focus:function(e){return e===C.activeElement&&(!C.hasFocus||C.hasFocus())&&!!(e.type||e.href||~e.tabIndex)},enabled:ge(!1),disabled:ge(!0),checked:function(e){var t=e.nodeName.toLowerCase();return"input"===t&&!!e.checked||"option"===t&&!!e.selected},selected:function(e){return e.parentNode&&e.parentNode.selectedIndex,!0===e.selected},empty:function(e){for(e=e.firstChild;e;e=e.nextSibling)if(e.nodeType<6)return!1;return!0},parent:function(e){return!b.pseudos.empty(e)},header:function(e){return J.test(e.nodeName)},input:function(e){return Q.test(e.nodeName)},button:function(e){var t=e.nodeName.toLowerCase();return"input"===t&&"button"===e.type||"button"===t},text:function(e){var t;return"input"===e.nodeName.toLowerCase()&&"text"===e.type&&(null==(t=e.getAttribute("type"))||"text"===t.toLowerCase())},first:ve(function(){return[0]}),last:ve(function(e,t){return[t-1]}),eq:ve(function(e,t,n){return[n<0?n+t:n]}),even:ve(function(e,t){for(var n=0;n<t;n+=2)e.push(n);return e}),odd:ve(function(e,t){for(var n=1;n<t;n+=2)e.push(n);return e}),lt:ve(function(e,t,n){for(var r=n<0?n+t:t<n?t:n;0<=--r;)e.push(r);return e}),gt:ve(function(e,t,n){for(var r=n<0?n+t:n;++r<t;)e.push(r);return e})}}).pseudos.nth=b.pseudos.eq,{radio:!0,checkbox:!0,file:!0,password:!0,image:!0})b.pseudos[e]=de(e);for(e in{submit:!0,reset:!0})b.pseudos[e]=he(e);function me(){}function xe(e){for(var t=0,n=e.length,r="";t<n;t++)r+=e[t].value;return r}function be(s,e,t){var u=e.dir,l=e.next,c=l||u,f=t&&"parentNode"===c,p=r++;return e.first?function(e,t,n){while(e=e[u])if(1===e.nodeType||f)return s(e,t,n);return!1}:function(e,t,n){var r,i,o,a=[k,p];if(n){while(e=e[u])if((1===e.nodeType||f)&&s(e,t,n))return!0}else while(e=e[u])if(1===e.nodeType||f)if(i=(o=e[S]||(e[S]={}))[e.uniqueID]||(o[e.uniqueID]={}),l&&l===e.nodeName.toLowerCase())e=e[u]||e;else{if((r=i[c])&&r[0]===k&&r[1]===p)return a[2]=r[2];if((i[c]=a)[2]=s(e,t,n))return!0}return!1}}function we(i){return 1<i.length?function(e,t,n){var r=i.length;while(r--)if(!i[r](e,t,n))return!1;return!0}:i[0]}function Te(e,t,n,r,i){for(var o,a=[],s=0,u=e.length,l=null!=t;s<u;s++)(o=e[s])&&(n&&!n(o,r,i)||(a.push(o),l&&t.push(s)));return a}function Ce(d,h,g,v,y,e){return v&&!v[S]&&(v=Ce(v)),y&&!y[S]&&(y=Ce(y,e)),le(function(e,t,n,r){var i,o,a,s=[],u=[],l=t.length,c=e||function(e,t,n){for(var r=0,i=t.length;r<i;r++)se(e,t[r],n);return n}(h||"*",n.nodeType?[n]:n,[]),f=!d||!e&&h?c:Te(c,s,d,n,r),p=g?y||(e?d:l||v)?[]:t:f;if(g&&g(f,p,n,r),v){i=Te(p,u),v(i,[],n,r),o=i.length;while(o--)(a=i[o])&&(p[u[o]]=!(f[u[o]]=a))}if(e){if(y||d){if(y){i=[],o=p.length;while(o--)(a=p[o])&&i.push(f[o]=a);y(null,p=[],i,r)}o=p.length;while(o--)(a=p[o])&&-1<(i=y?P(e,a):s[o])&&(e[i]=!(t[i]=a))}}else p=Te(p===t?p.splice(l,p.length):p),y?y(null,t,p,r):H.apply(t,p)})}function Ee(e){for(var i,t,n,r=e.length,o=b.relative[e[0].type],a=o||b.relative[" "],s=o?1:0,u=be(function(e){return e===i},a,!0),l=be(function(e){return-1<P(i,e)},a,!0),c=[function(e,t,n){var r=!o&&(n||t!==w)||((i=t).nodeType?u(e,t,n):l(e,t,n));return i=null,r}];s<r;s++)if(t=b.relative[e[s].type])c=[be(we(c),t)];else{if((t=b.filter[e[s].type].apply(null,e[s].matches))[S]){for(n=++s;n<r;n++)if(b.relative[e[n].type])break;return Ce(1<s&&we(c),1<s&&xe(e.slice(0,s-1).concat({value:" "===e[s-2].type?"*":""})).replace($,"$1"),t,s<n&&Ee(e.slice(s,n)),n<r&&Ee(e=e.slice(n)),n<r&&xe(e))}c.push(t)}return we(c)}return me.prototype=b.filters=b.pseudos,b.setFilters=new me,h=se.tokenize=function(e,t){var n,r,i,o,a,s,u,l=x[e+" "];if(l)return t?0:l.slice(0);a=e,s=[],u=b.preFilter;while(a){for(o in n&&!(r=_.exec(a))||(r&&(a=a.slice(r[0].length)||a),s.push(i=[])),n=!1,(r=z.exec(a))&&(n=r.shift(),i.push({value:n,type:r[0].replace($," ")}),a=a.slice(n.length)),b.filter)!(r=G[o].exec(a))||u[o]&&!(r=u[o](r))||(n=r.shift(),i.push({value:n,type:o,matches:r}),a=a.slice(n.length));if(!n)break}return t?a.length:a?se.error(e):x(e,s).slice(0)},f=se.compile=function(e,t){var n,v,y,m,x,r,i=[],o=[],a=A[e+" "];if(!a){t||(t=h(e)),n=t.length;while(n--)(a=Ee(t[n]))[S]?i.push(a):o.push(a);(a=A(e,(v=o,m=0<(y=i).length,x=0<v.length,r=function(e,t,n,r,i){var o,a,s,u=0,l="0",c=e&&[],f=[],p=w,d=e||x&&b.find.TAG("*",i),h=k+=null==p?1:Math.random()||.1,g=d.length;for(i&&(w=t==C||t||i);l!==g&&null!=(o=d[l]);l++){if(x&&o){a=0,t||o.ownerDocument==C||(T(o),n=!E);while(s=v[a++])if(s(o,t||C,n)){r.push(o);break}i&&(k=h)}m&&((o=!s&&o)&&u--,e&&c.push(o))}if(u+=l,m&&l!==u){a=0;while(s=y[a++])s(c,f,t,n);if(e){if(0<u)while(l--)c[l]||f[l]||(f[l]=q.call(r));f=Te(f)}H.apply(r,f),i&&!e&&0<f.length&&1<u+y.length&&se.uniqueSort(r)}return i&&(k=h,w=p),c},m?le(r):r))).selector=e}return a},g=se.select=function(e,t,n,r){var i,o,a,s,u,l="function"==typeof e&&e,c=!r&&h(e=l.selector||e);if(n=n||[],1===c.length){if(2<(o=c[0]=c[0].slice(0)).length&&"ID"===(a=o[0]).type&&9===t.nodeType&&E&&b.relative[o[1].type]){if(!(t=(b.find.ID(a.matches[0].replace(te,ne),t)||[])[0]))return n;l&&(t=t.parentNode),e=e.slice(o.shift().value.length)}i=G.needsContext.test(e)?0:o.length;while(i--){if(a=o[i],b.relative[s=a.type])break;if((u=b.find[s])&&(r=u(a.matches[0].replace(te,ne),ee.test(o[0].type)&&ye(t.parentNode)||t))){if(o.splice(i,1),!(e=r.length&&xe(o)))return H.apply(n,r),n;break}}}return(l||f(e,c))(r,t,!E,n,!t||ee.test(e)&&ye(t.parentNode)||t),n},d.sortStable=S.split("").sort(D).join("")===S,d.detectDuplicates=!!l,T(),d.sortDetached=ce(function(e){return 1&e.compareDocumentPosition(C.createElement("fieldset"))}),ce(function(e){return e.innerHTML="<a href='#'></a>","#"===e.firstChild.getAttribute("href")})||fe("type|href|height|width",function(e,t,n){if(!n)return e.getAttribute(t,"type"===t.toLowerCase()?1:2)}),d.attributes&&ce(function(e){return e.innerHTML="<input/>",e.firstChild.setAttribute("value",""),""===e.firstChild.getAttribute("value")})||fe("value",function(e,t,n){if(!n&&"input"===e.nodeName.toLowerCase())return e.defaultValue}),ce(function(e){return null==e.getAttribute("disabled")})||fe(R,function(e,t,n){var r;if(!n)return!0===e[t]?t.toLowerCase():(r=e.getAttributeNode(t))&&r.specified?r.value:null}),se}(C);S.find=d,S.expr=d.selectors,S.expr[":"]=S.expr.pseudos,S.uniqueSort=S.unique=d.uniqueSort,S.text=d.getText,S.isXMLDoc=d.isXML,S.contains=d.contains,S.escapeSelector=d.escape;var h=function(e,t,n){var r=[],i=void 0!==n;while((e=e[t])&&9!==e.nodeType)if(1===e.nodeType){if(i&&S(e).is(n))break;r.push(e)}return r},T=function(e,t){for(var n=[];e;e=e.nextSibling)1===e.nodeType&&e!==t&&n.push(e);return n},k=S.expr.match.needsContext;function A(e,t){return e.nodeName&&e.nodeName.toLowerCase()===t.toLowerCase()}var N=/^<([a-z][^\/\0>:\x20\t\r\n\f]*)[\x20\t\r\n\f]*\/?>(?:<\/\1>|)$/i;function D(e,n,r){return m(n)?S.grep(e,function(e,t){return!!n.call(e,t,e)!==r}):n.nodeType?S.grep(e,function(e){return e===n!==r}):"string"!=typeof n?S.grep(e,function(e){return-1<i.call(n,e)!==r}):S.filter(n,e,r)}S.filter=function(e,t,n){var r=t[0];return n&&(e=":not("+e+")"),1===t.length&&1===r.nodeType?S.find.matchesSelector(r,e)?[r]:[]:S.find.matches(e,S.grep(t,function(e){return 1===e.nodeType}))},S.fn.extend({find:function(e){var t,n,r=this.length,i=this;if("string"!=typeof e)return this.pushStack(S(e).filter(function(){for(t=0;t<r;t++)if(S.contains(i[t],this))return!0}));for(n=this.pushStack([]),t=0;t<r;t++)S.find(e,i[t],n);return 1<r?S.uniqueSort(n):n},filter:function(e){return this.pushStack(D(this,e||[],!1))},not:function(e){return this.pushStack(D(this,e||[],!0))},is:function(e){return!!D(this,"string"==typeof e&&k.test(e)?S(e):e||[],!1).length}});var j,q=/^(?:\s*(<[\w\W]+>)[^>]*|#([\w-]+))$/;(S.fn.init=function(e,t,n){var r,i;if(!e)return this;if(n=n||j,"string"==typeof e){if(!(r="<"===e[0]&&">"===e[e.length-1]&&3<=e.length?[null,e,null]:q.exec(e))||!r[1]&&t)return!t||t.jquery?(t||n).find(e):this.constructor(t).find(e);if(r[1]){if(t=t instanceof S?t[0]:t,S.merge(this,S.parseHTML(r[1],t&&t.nodeType?t.ownerDocument||t:E,!0)),N.test(r[1])&&S.isPlainObject(t))for(r in t)m(this[r])?this[r](t[r]):this.attr(r,t[r]);return this}return(i=E.getElementById(r[2]))&&(this[0]=i,this.length=1),this}return e.nodeType?(this[0]=e,this.length=1,this):m(e)?void 0!==n.ready?n.ready(e):e(S):S.makeArray(e,this)}).prototype=S.fn,j=S(E);var L=/^(?:parents|prev(?:Until|All))/,H={children:!0,contents:!0,next:!0,prev:!0};function O(e,t){while((e=e[t])&&1!==e.nodeType);return e}S.fn.extend({has:function(e){var t=S(e,this),n=t.length;return this.filter(function(){for(var e=0;e<n;e++)if(S.contains(this,t[e]))return!0})},closest:function(e,t){var n,r=0,i=this.length,o=[],a="string"!=typeof e&&S(e);if(!k.test(e))for(;r<i;r++)for(n=this[r];n&&n!==t;n=n.parentNode)if(n.nodeType<11&&(a?-1<a.index(n):1===n.nodeType&&S.find.matchesSelector(n,e))){o.push(n);break}return this.pushStack(1<o.length?S.uniqueSort(o):o)},index:function(e){return e?"string"==typeof e?i.call(S(e),this[0]):i.call(this,e.jquery?e[0]:e):this[0]&&this[0].parentNode?this.first().prevAll().length:-1},add:function(e,t){return this.pushStack(S.uniqueSort(S.merge(this.get(),S(e,t))))},addBack:function(e){return this.add(null==e?this.prevObject:this.prevObject.filter(e))}}),S.each({parent:function(e){var t=e.parentNode;return t&&11!==t.nodeType?t:null},parents:function(e){return h(e,"parentNode")},parentsUntil:function(e,t,n){return h(e,"parentNode",n)},next:function(e){return O(e,"nextSibling")},prev:function(e){return O(e,"previousSibling")},nextAll:function(e){return h(e,"nextSibling")},prevAll:function(e){return h(e,"previousSibling")},nextUntil:function(e,t,n){return h(e,"nextSibling",n)},prevUntil:function(e,t,n){return h(e,"previousSibling",n)},siblings:function(e){return T((e.parentNode||{}).firstChild,e)},children:function(e){return T(e.firstChild)},contents:function(e){return null!=e.contentDocument&&r(e.contentDocument)?e.contentDocument:(A(e,"template")&&(e=e.content||e),S.merge([],e.childNodes))}},function(r,i){S.fn[r]=function(e,t){var n=S.map(this,i,e);return"Until"!==r.slice(-5)&&(t=e),t&&"string"==typeof t&&(n=S.filter(t,n)),1<this.length&&(H[r]||S.uniqueSort(n),L.test(r)&&n.reverse()),this.pushStack(n)}});var P=/[^\x20\t\r\n\f]+/g;function R(e){return e}function M(e){throw e}function I(e,t,n,r){var i;try{e&&m(i=e.promise)?i.call(e).done(t).fail(n):e&&m(i=e.then)?i.call(e,t,n):t.apply(void 0,[e].slice(r))}catch(e){n.apply(void 0,[e])}}S.Callbacks=function(r){var e,n;r="string"==typeof r?(e=r,n={},S.each(e.match(P)||[],function(e,t){n[t]=!0}),n):S.extend({},r);var i,t,o,a,s=[],u=[],l=-1,c=function(){for(a=a||r.once,o=i=!0;u.length;l=-1){t=u.shift();while(++l<s.length)!1===s[l].apply(t[0],t[1])&&r.stopOnFalse&&(l=s.length,t=!1)}r.memory||(t=!1),i=!1,a&&(s=t?[]:"")},f={add:function(){return s&&(t&&!i&&(l=s.length-1,u.push(t)),function n(e){S.each(e,function(e,t){m(t)?r.unique&&f.has(t)||s.push(t):t&&t.length&&"string"!==w(t)&&n(t)})}(arguments),t&&!i&&c()),this},remove:function(){return S.each(arguments,function(e,t){var n;while(-1<(n=S.inArray(t,s,n)))s.splice(n,1),n<=l&&l--}),this},has:function(e){return e?-1<S.inArray(e,s):0<s.length},empty:function(){return s&&(s=[]),this},disable:function(){return a=u=[],s=t="",this},disabled:function(){return!s},lock:function(){return a=u=[],t||i||(s=t=""),this},locked:function(){return!!a},fireWith:function(e,t){return a||(t=[e,(t=t||[]).slice?t.slice():t],u.push(t),i||c()),this},fire:function(){return f.fireWith(this,arguments),this},fired:function(){return!!o}};return f},S.extend({Deferred:function(e){var o=[["notify","progress",S.Callbacks("memory"),S.Callbacks("memory"),2],["resolve","done",S.Callbacks("once memory"),S.Callbacks("once memory"),0,"resolved"],["reject","fail",S.Callbacks("once memory"),S.Callbacks("once memory"),1,"rejected"]],i="pending",a={state:function(){return i},always:function(){return s.done(arguments).fail(arguments),this},"catch":function(e){return a.then(null,e)},pipe:function(){var i=arguments;return S.Deferred(function(r){S.each(o,function(e,t){var n=m(i[t[4]])&&i[t[4]];s[t[1]](function(){var e=n&&n.apply(this,arguments);e&&m(e.promise)?e.promise().progress(r.notify).done(r.resolve).fail(r.reject):r[t[0]+"With"](this,n?[e]:arguments)})}),i=null}).promise()},then:function(t,n,r){var u=0;function l(i,o,a,s){return function(){var n=this,r=arguments,e=function(){var e,t;if(!(i<u)){if((e=a.apply(n,r))===o.promise())throw new TypeError("Thenable self-resolution");t=e&&("object"==typeof e||"function"==typeof e)&&e.then,m(t)?s?t.call(e,l(u,o,R,s),l(u,o,M,s)):(u++,t.call(e,l(u,o,R,s),l(u,o,M,s),l(u,o,R,o.notifyWith))):(a!==R&&(n=void 0,r=[e]),(s||o.resolveWith)(n,r))}},t=s?e:function(){try{e()}catch(e){S.Deferred.exceptionHook&&S.Deferred.exceptionHook(e,t.stackTrace),u<=i+1&&(a!==M&&(n=void 0,r=[e]),o.rejectWith(n,r))}};i?t():(S.Deferred.getStackHook&&(t.stackTrace=S.Deferred.getStackHook()),C.setTimeout(t))}}return S.Deferred(function(e){o[0][3].add(l(0,e,m(r)?r:R,e.notifyWith)),o[1][3].add(l(0,e,m(t)?t:R)),o[2][3].add(l(0,e,m(n)?n:M))}).promise()},promise:function(e){return null!=e?S.extend(e,a):a}},s={};return S.each(o,function(e,t){var n=t[2],r=t[5];a[t[1]]=n.add,r&&n.add(function(){i=r},o[3-e][2].disable,o[3-e][3].disable,o[0][2].lock,o[0][3].lock),n.add(t[3].fire),s[t[0]]=function(){return s[t[0]+"With"](this===s?void 0:this,arguments),this},s[t[0]+"With"]=n.fireWith}),a.promise(s),e&&e.call(s,s),s},when:function(e){var n=arguments.length,t=n,r=Array(t),i=s.call(arguments),o=S.Deferred(),a=function(t){return function(e){r[t]=this,i[t]=1<arguments.length?s.call(arguments):e,--n||o.resolveWith(r,i)}};if(n<=1&&(I(e,o.done(a(t)).resolve,o.reject,!n),"pending"===o.state()||m(i[t]&&i[t].then)))return o.then();while(t--)I(i[t],a(t),o.reject);return o.promise()}});var W=/^(Eval|Internal|Range|Reference|Syntax|Type|URI)Error$/;S.Deferred.exceptionHook=function(e,t){C.console&&C.console.warn&&e&&W.test(e.name)&&C.console.warn("jQuery.Deferred exception: "+e.message,e.stack,t)},S.readyException=function(e){C.setTimeout(function(){throw e})};var F=S.Deferred();function B(){E.removeEventListener("DOMContentLoaded",B),C.removeEventListener("load",B),S.ready()}S.fn.ready=function(e){return F.then(e)["catch"](function(e){S.readyException(e)}),this},S.extend({isReady:!1,readyWait:1,ready:function(e){(!0===e?--S.readyWait:S.isReady)||(S.isReady=!0)!==e&&0<--S.readyWait||F.resolveWith(E,[S])}}),S.ready.then=F.then,"complete"===E.readyState||"loading"!==E.readyState&&!E.documentElement.doScroll?C.setTimeout(S.ready):(E.addEventListener("DOMContentLoaded",B),C.addEventListener("load",B));var $=function(e,t,n,r,i,o,a){var s=0,u=e.length,l=null==n;if("object"===w(n))for(s in i=!0,n)$(e,t,s,n[s],!0,o,a);else if(void 0!==r&&(i=!0,m(r)||(a=!0),l&&(a?(t.call(e,r),t=null):(l=t,t=function(e,t,n){return l.call(S(e),n)})),t))for(;s<u;s++)t(e[s],n,a?r:r.call(e[s],s,t(e[s],n)));return i?e:l?t.call(e):u?t(e[0],n):o},_=/^-ms-/,z=/-([a-z])/g;function U(e,t){return t.toUpperCase()}function X(e){return e.replace(_,"ms-").replace(z,U)}var V=function(e){return 1===e.nodeType||9===e.nodeType||!+e.nodeType};function G(){this.expando=S.expando+G.uid++}G.uid=1,G.prototype={cache:function(e){var t=e[this.expando];return t||(t={},V(e)&&(e.nodeType?e[this.expando]=t:Object.defineProperty(e,this.expando,{value:t,configurable:!0}))),t},set:function(e,t,n){var r,i=this.cache(e);if("string"==typeof t)i[X(t)]=n;else for(r in t)i[X(r)]=t[r];return i},get:function(e,t){return void 0===t?this.cache(e):e[this.expando]&&e[this.expando][X(t)]},access:function(e,t,n){return void 0===t||t&&"string"==typeof t&&void 0===n?this.get(e,t):(this.set(e,t,n),void 0!==n?n:t)},remove:function(e,t){var n,r=e[this.expando];if(void 0!==r){if(void 0!==t){n=(t=Array.isArray(t)?t.map(X):(t=X(t))in r?[t]:t.match(P)||[]).length;while(n--)delete r[t[n]]}(void 0===t||S.isEmptyObject(r))&&(e.nodeType?e[this.expando]=void 0:delete e[this.expando])}},hasData:function(e){var t=e[this.expando];return void 0!==t&&!S.isEmptyObject(t)}};var Y=new G,Q=new G,J=/^(?:\{[\w\W]*\}|\[[\w\W]*\])$/,K=/[A-Z]/g;function Z(e,t,n){var r,i;if(void 0===n&&1===e.nodeType)if(r="data-"+t.replace(K,"-$&").toLowerCase(),"string"==typeof(n=e.getAttribute(r))){try{n="true"===(i=n)||"false"!==i&&("null"===i?null:i===+i+""?+i:J.test(i)?JSON.parse(i):i)}catch(e){}Q.set(e,t,n)}else n=void 0;return n}S.extend({hasData:function(e){return Q.hasData(e)||Y.hasData(e)},data:function(e,t,n){return Q.access(e,t,n)},removeData:function(e,t){Q.remove(e,t)},_data:function(e,t,n){return Y.access(e,t,n)},_removeData:function(e,t){Y.remove(e,t)}}),S.fn.extend({data:function(n,e){var t,r,i,o=this[0],a=o&&o.attributes;if(void 0===n){if(this.length&&(i=Q.get(o),1===o.nodeType&&!Y.get(o,"hasDataAttrs"))){t=a.length;while(t--)a[t]&&0===(r=a[t].name).indexOf("data-")&&(r=X(r.slice(5)),Z(o,r,i[r]));Y.set(o,"hasDataAttrs",!0)}return i}return"object"==typeof n?this.each(function(){Q.set(this,n)}):$(this,function(e){var t;if(o&&void 0===e)return void 0!==(t=Q.get(o,n))?t:void 0!==(t=Z(o,n))?t:void 0;this.each(function(){Q.set(this,n,e)})},null,e,1<arguments.length,null,!0)},removeData:function(e){return this.each(function(){Q.remove(this,e)})}}),S.extend({queue:function(e,t,n){var r;if(e)return t=(t||"fx")+"queue",r=Y.get(e,t),n&&(!r||Array.isArray(n)?r=Y.access(e,t,S.makeArray(n)):r.push(n)),r||[]},dequeue:function(e,t){t=t||"fx";var n=S.queue(e,t),r=n.length,i=n.shift(),o=S._queueHooks(e,t);"inprogress"===i&&(i=n.shift(),r--),i&&("fx"===t&&n.unshift("inprogress"),delete o.stop,i.call(e,function(){S.dequeue(e,t)},o)),!r&&o&&o.empty.fire()},_queueHooks:function(e,t){var n=t+"queueHooks";return Y.get(e,n)||Y.access(e,n,{empty:S.Callbacks("once memory").add(function(){Y.remove(e,[t+"queue",n])})})}}),S.fn.extend({queue:function(t,n){var e=2;return"string"!=typeof t&&(n=t,t="fx",e--),arguments.length<e?S.queue(this[0],t):void 0===n?this:this.each(function(){var e=S.queue(this,t,n);S._queueHooks(this,t),"fx"===t&&"inprogress"!==e[0]&&S.dequeue(this,t)})},dequeue:function(e){return this.each(function(){S.dequeue(this,e)})},clearQueue:function(e){return this.queue(e||"fx",[])},promise:function(e,t){var n,r=1,i=S.Deferred(),o=this,a=this.length,s=function(){--r||i.resolveWith(o,[o])};"string"!=typeof e&&(t=e,e=void 0),e=e||"fx";while(a--)(n=Y.get(o[a],e+"queueHooks"))&&n.empty&&(r++,n.empty.add(s));return s(),i.promise(t)}});var ee=/[+-]?(?:\d*\.|)\d+(?:[eE][+-]?\d+|)/.source,te=new RegExp("^(?:([+-])=|)("+ee+")([a-z%]*)$","i"),ne=["Top","Right","Bottom","Left"],re=E.documentElement,ie=function(e){return S.contains(e.ownerDocument,e)},oe={composed:!0};re.getRootNode&&(ie=function(e){return S.contains(e.ownerDocument,e)||e.getRootNode(oe)===e.ownerDocument});var ae=function(e,t){return"none"===(e=t||e).style.display||""===e.style.display&&ie(e)&&"none"===S.css(e,"display")};function se(e,t,n,r){var i,o,a=20,s=r?function(){return r.cur()}:function(){return S.css(e,t,"")},u=s(),l=n&&n[3]||(S.cssNumber[t]?"":"px"),c=e.nodeType&&(S.cssNumber[t]||"px"!==l&&+u)&&te.exec(S.css(e,t));if(c&&c[3]!==l){u/=2,l=l||c[3],c=+u||1;while(a--)S.style(e,t,c+l),(1-o)*(1-(o=s()/u||.5))<=0&&(a=0),c/=o;c*=2,S.style(e,t,c+l),n=n||[]}return n&&(c=+c||+u||0,i=n[1]?c+(n[1]+1)*n[2]:+n[2],r&&(r.unit=l,r.start=c,r.end=i)),i}var ue={};function le(e,t){for(var n,r,i,o,a,s,u,l=[],c=0,f=e.length;c<f;c++)(r=e[c]).style&&(n=r.style.display,t?("none"===n&&(l[c]=Y.get(r,"display")||null,l[c]||(r.style.display="")),""===r.style.display&&ae(r)&&(l[c]=(u=a=o=void 0,a=(i=r).ownerDocument,s=i.nodeName,(u=ue[s])||(o=a.body.appendChild(a.createElement(s)),u=S.css(o,"display"),o.parentNode.removeChild(o),"none"===u&&(u="block"),ue[s]=u)))):"none"!==n&&(l[c]="none",Y.set(r,"display",n)));for(c=0;c<f;c++)null!=l[c]&&(e[c].style.display=l[c]);return e}S.fn.extend({show:function(){return le(this,!0)},hide:function(){return le(this)},toggle:function(e){return"boolean"==typeof e?e?this.show():this.hide():this.each(function(){ae(this)?S(this).show():S(this).hide()})}});var ce,fe,pe=/^(?:checkbox|radio)$/i,de=/<([a-z][^\/\0>\x20\t\r\n\f]*)/i,he=/^$|^module$|\/(?:java|ecma)script/i;ce=E.createDocumentFragment().appendChild(E.createElement("div")),(fe=E.createElement("input")).setAttribute("type","radio"),fe.setAttribute("checked","checked"),fe.setAttribute("name","t"),ce.appendChild(fe),y.checkClone=ce.cloneNode(!0).cloneNode(!0).lastChild.checked,ce.innerHTML="<textarea>x</textarea>",y.noCloneChecked=!!ce.cloneNode(!0).lastChild.defaultValue,ce.innerHTML="<option></option>",y.option=!!ce.lastChild;var ge={thead:[1,"<table>","</table>"],col:[2,"<table><colgroup>","</colgroup></table>"],tr:[2,"<table><tbody>","</tbody></table>"],td:[3,"<table><tbody><tr>","</tr></tbody></table>"],_default:[0,"",""]};function ve(e,t){var n;return n="undefined"!=typeof e.getElementsByTagName?e.getElementsByTagName(t||"*"):"undefined"!=typeof e.querySelectorAll?e.querySelectorAll(t||"*"):[],void 0===t||t&&A(e,t)?S.merge([e],n):n}function ye(e,t){for(var n=0,r=e.length;n<r;n++)Y.set(e[n],"globalEval",!t||Y.get(t[n],"globalEval"))}ge.tbody=ge.tfoot=ge.colgroup=ge.caption=ge.thead,ge.th=ge.td,y.option||(ge.optgroup=ge.option=[1,"<select multiple='multiple'>","</select>"]);var me=/<|&#?\w+;/;function xe(e,t,n,r,i){for(var o,a,s,u,l,c,f=t.createDocumentFragment(),p=[],d=0,h=e.length;d<h;d++)if((o=e[d])||0===o)if("object"===w(o))S.merge(p,o.nodeType?[o]:o);else if(me.test(o)){a=a||f.appendChild(t.createElement("div")),s=(de.exec(o)||["",""])[1].toLowerCase(),u=ge[s]||ge._default,a.innerHTML=u[1]+S.htmlPrefilter(o)+u[2],c=u[0];while(c--)a=a.lastChild;S.merge(p,a.childNodes),(a=f.firstChild).textContent=""}else p.push(t.createTextNode(o));f.textContent="",d=0;while(o=p[d++])if(r&&-1<S.inArray(o,r))i&&i.push(o);else if(l=ie(o),a=ve(f.appendChild(o),"script"),l&&ye(a),n){c=0;while(o=a[c++])he.test(o.type||"")&&n.push(o)}return f}var be=/^key/,we=/^(?:mouse|pointer|contextmenu|drag|drop)|click/,Te=/^([^.]*)(?:\.(.+)|)/;function Ce(){return!0}function Ee(){return!1}function Se(e,t){return e===function(){try{return E.activeElement}catch(e){}}()==("focus"===t)}function ke(e,t,n,r,i,o){var a,s;if("object"==typeof t){for(s in"string"!=typeof n&&(r=r||n,n=void 0),t)ke(e,s,n,r,t[s],o);return e}if(null==r&&null==i?(i=n,r=n=void 0):null==i&&("string"==typeof n?(i=r,r=void 0):(i=r,r=n,n=void 0)),!1===i)i=Ee;else if(!i)return e;return 1===o&&(a=i,(i=function(e){return S().off(e),a.apply(this,arguments)}).guid=a.guid||(a.guid=S.guid++)),e.each(function(){S.event.add(this,t,i,r,n)})}function Ae(e,i,o){o?(Y.set(e,i,!1),S.event.add(e,i,{namespace:!1,handler:function(e){var t,n,r=Y.get(this,i);if(1&e.isTrigger&&this[i]){if(r.length)(S.event.special[i]||{}).delegateType&&e.stopPropagation();else if(r=s.call(arguments),Y.set(this,i,r),t=o(this,i),this[i](),r!==(n=Y.get(this,i))||t?Y.set(this,i,!1):n={},r!==n)return e.stopImmediatePropagation(),e.preventDefault(),n.value}else r.length&&(Y.set(this,i,{value:S.event.trigger(S.extend(r[0],S.Event.prototype),r.slice(1),this)}),e.stopImmediatePropagation())}})):void 0===Y.get(e,i)&&S.event.add(e,i,Ce)}S.event={global:{},add:function(t,e,n,r,i){var o,a,s,u,l,c,f,p,d,h,g,v=Y.get(t);if(V(t)){n.handler&&(n=(o=n).handler,i=o.selector),i&&S.find.matchesSelector(re,i),n.guid||(n.guid=S.guid++),(u=v.events)||(u=v.events=Object.create(null)),(a=v.handle)||(a=v.handle=function(e){return"undefined"!=typeof S&&S.event.triggered!==e.type?S.event.dispatch.apply(t,arguments):void 0}),l=(e=(e||"").match(P)||[""]).length;while(l--)d=g=(s=Te.exec(e[l])||[])[1],h=(s[2]||"").split(".").sort(),d&&(f=S.event.special[d]||{},d=(i?f.delegateType:f.bindType)||d,f=S.event.special[d]||{},c=S.extend({type:d,origType:g,data:r,handler:n,guid:n.guid,selector:i,needsContext:i&&S.expr.match.needsContext.test(i),namespace:h.join(".")},o),(p=u[d])||((p=u[d]=[]).delegateCount=0,f.setup&&!1!==f.setup.call(t,r,h,a)||t.addEventListener&&t.addEventListener(d,a)),f.add&&(f.add.call(t,c),c.handler.guid||(c.handler.guid=n.guid)),i?p.splice(p.delegateCount++,0,c):p.push(c),S.event.global[d]=!0)}},remove:function(e,t,n,r,i){var o,a,s,u,l,c,f,p,d,h,g,v=Y.hasData(e)&&Y.get(e);if(v&&(u=v.events)){l=(t=(t||"").match(P)||[""]).length;while(l--)if(d=g=(s=Te.exec(t[l])||[])[1],h=(s[2]||"").split(".").sort(),d){f=S.event.special[d]||{},p=u[d=(r?f.delegateType:f.bindType)||d]||[],s=s[2]&&new RegExp("(^|\\.)"+h.join("\\.(?:.*\\.|)")+"(\\.|$)"),a=o=p.length;while(o--)c=p[o],!i&&g!==c.origType||n&&n.guid!==c.guid||s&&!s.test(c.namespace)||r&&r!==c.selector&&("**"!==r||!c.selector)||(p.splice(o,1),c.selector&&p.delegateCount--,f.remove&&f.remove.call(e,c));a&&!p.length&&(f.teardown&&!1!==f.teardown.call(e,h,v.handle)||S.removeEvent(e,d,v.handle),delete u[d])}else for(d in u)S.event.remove(e,d+t[l],n,r,!0);S.isEmptyObject(u)&&Y.remove(e,"handle events")}},dispatch:function(e){var t,n,r,i,o,a,s=new Array(arguments.length),u=S.event.fix(e),l=(Y.get(this,"events")||Object.create(null))[u.type]||[],c=S.event.special[u.type]||{};for(s[0]=u,t=1;t<arguments.length;t++)s[t]=arguments[t];if(u.delegateTarget=this,!c.preDispatch||!1!==c.preDispatch.call(this,u)){a=S.event.handlers.call(this,u,l),t=0;while((i=a[t++])&&!u.isPropagationStopped()){u.currentTarget=i.elem,n=0;while((o=i.handlers[n++])&&!u.isImmediatePropagationStopped())u.rnamespace&&!1!==o.namespace&&!u.rnamespace.test(o.namespace)||(u.handleObj=o,u.data=o.data,void 0!==(r=((S.event.special[o.origType]||{}).handle||o.handler).apply(i.elem,s))&&!1===(u.result=r)&&(u.preventDefault(),u.stopPropagation()))}return c.postDispatch&&c.postDispatch.call(this,u),u.result}},handlers:function(e,t){var n,r,i,o,a,s=[],u=t.delegateCount,l=e.target;if(u&&l.nodeType&&!("click"===e.type&&1<=e.button))for(;l!==this;l=l.parentNode||this)if(1===l.nodeType&&("click"!==e.type||!0!==l.disabled)){for(o=[],a={},n=0;n<u;n++)void 0===a[i=(r=t[n]).selector+" "]&&(a[i]=r.needsContext?-1<S(i,this).index(l):S.find(i,this,null,[l]).length),a[i]&&o.push(r);o.length&&s.push({elem:l,handlers:o})}return l=this,u<t.length&&s.push({elem:l,handlers:t.slice(u)}),s},addProp:function(t,e){Object.defineProperty(S.Event.prototype,t,{enumerable:!0,configurable:!0,get:m(e)?function(){if(this.originalEvent)return e(this.originalEvent)}:function(){if(this.originalEvent)return this.originalEvent[t]},set:function(e){Object.defineProperty(this,t,{enumerable:!0,configurable:!0,writable:!0,value:e})}})},fix:function(e){return e[S.expando]?e:new S.Event(e)},special:{load:{noBubble:!0},click:{setup:function(e){var t=this||e;return pe.test(t.type)&&t.click&&A(t,"input")&&Ae(t,"click",Ce),!1},trigger:function(e){var t=this||e;return pe.test(t.type)&&t.click&&A(t,"input")&&Ae(t,"click"),!0},_default:function(e){var t=e.target;return pe.test(t.type)&&t.click&&A(t,"input")&&Y.get(t,"click")||A(t,"a")}},beforeunload:{postDispatch:function(e){void 0!==e.result&&e.originalEvent&&(e.originalEvent.returnValue=e.result)}}}},S.removeEvent=function(e,t,n){e.removeEventListener&&e.removeEventListener(t,n)},S.Event=function(e,t){if(!(this instanceof S.Event))return new S.Event(e,t);e&&e.type?(this.originalEvent=e,this.type=e.type,this.isDefaultPrevented=e.defaultPrevented||void 0===e.defaultPrevented&&!1===e.returnValue?Ce:Ee,this.target=e.target&&3===e.target.nodeType?e.target.parentNode:e.target,this.currentTarget=e.currentTarget,this.relatedTarget=e.relatedTarget):this.type=e,t&&S.extend(this,t),this.timeStamp=e&&e.timeStamp||Date.now(),this[S.expando]=!0},S.Event.prototype={constructor:S.Event,isDefaultPrevented:Ee,isPropagationStopped:Ee,isImmediatePropagationStopped:Ee,isSimulated:!1,preventDefault:function(){var e=this.originalEvent;this.isDefaultPrevented=Ce,e&&!this.isSimulated&&e.preventDefault()},stopPropagation:function(){var e=this.originalEvent;this.isPropagationStopped=Ce,e&&!this.isSimulated&&e.stopPropagation()},stopImmediatePropagation:function(){var e=this.originalEvent;this.isImmediatePropagationStopped=Ce,e&&!this.isSimulated&&e.stopImmediatePropagation(),this.stopPropagation()}},S.each({altKey:!0,bubbles:!0,cancelable:!0,changedTouches:!0,ctrlKey:!0,detail:!0,eventPhase:!0,metaKey:!0,pageX:!0,pageY:!0,shiftKey:!0,view:!0,"char":!0,code:!0,charCode:!0,key:!0,keyCode:!0,button:!0,buttons:!0,clientX:!0,clientY:!0,offsetX:!0,offsetY:!0,pointerId:!0,pointerType:!0,screenX:!0,screenY:!0,targetTouches:!0,toElement:!0,touches:!0,which:function(e){var t=e.button;return null==e.which&&be.test(e.type)?null!=e.charCode?e.charCode:e.keyCode:!e.which&&void 0!==t&&we.test(e.type)?1&t?1:2&t?3:4&t?2:0:e.which}},S.event.addProp),S.each({focus:"focusin",blur:"focusout"},function(e,t){S.event.special[e]={setup:function(){return Ae(this,e,Se),!1},trigger:function(){return Ae(this,e),!0},delegateType:t}}),S.each({mouseenter:"mouseover",mouseleave:"mouseout",pointerenter:"pointerover",pointerleave:"pointerout"},function(e,i){S.event.special[e]={delegateType:i,bindType:i,handle:function(e){var t,n=e.relatedTarget,r=e.handleObj;return n&&(n===this||S.contains(this,n))||(e.type=r.origType,t=r.handler.apply(this,arguments),e.type=i),t}}}),S.fn.extend({on:function(e,t,n,r){return ke(this,e,t,n,r)},one:function(e,t,n,r){return ke(this,e,t,n,r,1)},off:function(e,t,n){var r,i;if(e&&e.preventDefault&&e.handleObj)return r=e.handleObj,S(e.delegateTarget).off(r.namespace?r.origType+"."+r.namespace:r.origType,r.selector,r.handler),this;if("object"==typeof e){for(i in e)this.off(i,t,e[i]);return this}return!1!==t&&"function"!=typeof t||(n=t,t=void 0),!1===n&&(n=Ee),this.each(function(){S.event.remove(this,e,n,t)})}});var Ne=/<script|<style|<link/i,De=/checked\s*(?:[^=]|=\s*.checked.)/i,je=/^\s*<!(?:\[CDATA\[|--)|(?:\]\]|--)>\s*$/g;function qe(e,t){return A(e,"table")&&A(11!==t.nodeType?t:t.firstChild,"tr")&&S(e).children("tbody")[0]||e}function Le(e){return e.type=(null!==e.getAttribute("type"))+"/"+e.type,e}function He(e){return"true/"===(e.type||"").slice(0,5)?e.type=e.type.slice(5):e.removeAttribute("type"),e}function Oe(e,t){var n,r,i,o,a,s;if(1===t.nodeType){if(Y.hasData(e)&&(s=Y.get(e).events))for(i in Y.remove(t,"handle events"),s)for(n=0,r=s[i].length;n<r;n++)S.event.add(t,i,s[i][n]);Q.hasData(e)&&(o=Q.access(e),a=S.extend({},o),Q.set(t,a))}}function Pe(n,r,i,o){r=g(r);var e,t,a,s,u,l,c=0,f=n.length,p=f-1,d=r[0],h=m(d);if(h||1<f&&"string"==typeof d&&!y.checkClone&&De.test(d))return n.each(function(e){var t=n.eq(e);h&&(r[0]=d.call(this,e,t.html())),Pe(t,r,i,o)});if(f&&(t=(e=xe(r,n[0].ownerDocument,!1,n,o)).firstChild,1===e.childNodes.length&&(e=t),t||o)){for(s=(a=S.map(ve(e,"script"),Le)).length;c<f;c++)u=e,c!==p&&(u=S.clone(u,!0,!0),s&&S.merge(a,ve(u,"script"))),i.call(n[c],u,c);if(s)for(l=a[a.length-1].ownerDocument,S.map(a,He),c=0;c<s;c++)u=a[c],he.test(u.type||"")&&!Y.access(u,"globalEval")&&S.contains(l,u)&&(u.src&&"module"!==(u.type||"").toLowerCase()?S._evalUrl&&!u.noModule&&S._evalUrl(u.src,{nonce:u.nonce||u.getAttribute("nonce")},l):b(u.textContent.replace(je,""),u,l))}return n}function Re(e,t,n){for(var r,i=t?S.filter(t,e):e,o=0;null!=(r=i[o]);o++)n||1!==r.nodeType||S.cleanData(ve(r)),r.parentNode&&(n&&ie(r)&&ye(ve(r,"script")),r.parentNode.removeChild(r));return e}S.extend({htmlPrefilter:function(e){return e},clone:function(e,t,n){var r,i,o,a,s,u,l,c=e.cloneNode(!0),f=ie(e);if(!(y.noCloneChecked||1!==e.nodeType&&11!==e.nodeType||S.isXMLDoc(e)))for(a=ve(c),r=0,i=(o=ve(e)).length;r<i;r++)s=o[r],u=a[r],void 0,"input"===(l=u.nodeName.toLowerCase())&&pe.test(s.type)?u.checked=s.checked:"input"!==l&&"textarea"!==l||(u.defaultValue=s.defaultValue);if(t)if(n)for(o=o||ve(e),a=a||ve(c),r=0,i=o.length;r<i;r++)Oe(o[r],a[r]);else Oe(e,c);return 0<(a=ve(c,"script")).length&&ye(a,!f&&ve(e,"script")),c},cleanData:function(e){for(var t,n,r,i=S.event.special,o=0;void 0!==(n=e[o]);o++)if(V(n)){if(t=n[Y.expando]){if(t.events)for(r in t.events)i[r]?S.event.remove(n,r):S.removeEvent(n,r,t.handle);n[Y.expando]=void 0}n[Q.expando]&&(n[Q.expando]=void 0)}}}),S.fn.extend({detach:function(e){return Re(this,e,!0)},remove:function(e){return Re(this,e)},text:function(e){return $(this,function(e){return void 0===e?S.text(this):this.empty().each(function(){1!==this.nodeType&&11!==this.nodeType&&9!==this.nodeType||(this.textContent=e)})},null,e,arguments.length)},append:function(){return Pe(this,arguments,function(e){1!==this.nodeType&&11!==this.nodeType&&9!==this.nodeType||qe(this,e).appendChild(e)})},prepend:function(){return Pe(this,arguments,function(e){if(1===this.nodeType||11===this.nodeType||9===this.nodeType){var t=qe(this,e);t.insertBefore(e,t.firstChild)}})},before:function(){return Pe(this,arguments,function(e){this.parentNode&&this.parentNode.insertBefore(e,this)})},after:function(){return Pe(this,arguments,function(e){this.parentNode&&this.parentNode.insertBefore(e,this.nextSibling)})},empty:function(){for(var e,t=0;null!=(e=this[t]);t++)1===e.nodeType&&(S.cleanData(ve(e,!1)),e.textContent="");return this},clone:function(e,t){return e=null!=e&&e,t=null==t?e:t,this.map(function(){return S.clone(this,e,t)})},html:function(e){return $(this,function(e){var t=this[0]||{},n=0,r=this.length;if(void 0===e&&1===t.nodeType)return t.innerHTML;if("string"==typeof e&&!Ne.test(e)&&!ge[(de.exec(e)||["",""])[1].toLowerCase()]){e=S.htmlPrefilter(e);try{for(;n<r;n++)1===(t=this[n]||{}).nodeType&&(S.cleanData(ve(t,!1)),t.innerHTML=e);t=0}catch(e){}}t&&this.empty().append(e)},null,e,arguments.length)},replaceWith:function(){var n=[];return Pe(this,arguments,function(e){var t=this.parentNode;S.inArray(this,n)<0&&(S.cleanData(ve(this)),t&&t.replaceChild(e,this))},n)}}),S.each({appendTo:"append",prependTo:"prepend",insertBefore:"before",insertAfter:"after",replaceAll:"replaceWith"},function(e,a){S.fn[e]=function(e){for(var t,n=[],r=S(e),i=r.length-1,o=0;o<=i;o++)t=o===i?this:this.clone(!0),S(r[o])[a](t),u.apply(n,t.get());return this.pushStack(n)}});var Me=new RegExp("^("+ee+")(?!px)[a-z%]+$","i"),Ie=function(e){var t=e.ownerDocument.defaultView;return t&&t.opener||(t=C),t.getComputedStyle(e)},We=function(e,t,n){var r,i,o={};for(i in t)o[i]=e.style[i],e.style[i]=t[i];for(i in r=n.call(e),t)e.style[i]=o[i];return r},Fe=new RegExp(ne.join("|"),"i");function Be(e,t,n){var r,i,o,a,s=e.style;return(n=n||Ie(e))&&(""!==(a=n.getPropertyValue(t)||n[t])||ie(e)||(a=S.style(e,t)),!y.pixelBoxStyles()&&Me.test(a)&&Fe.test(t)&&(r=s.width,i=s.minWidth,o=s.maxWidth,s.minWidth=s.maxWidth=s.width=a,a=n.width,s.width=r,s.minWidth=i,s.maxWidth=o)),void 0!==a?a+"":a}function $e(e,t){return{get:function(){if(!e())return(this.get=t).apply(this,arguments);delete this.get}}}!function(){function e(){if(l){u.style.cssText="position:absolute;left:-11111px;width:60px;margin-top:1px;padding:0;border:0",l.style.cssText="position:relative;display:block;box-sizing:border-box;overflow:scroll;margin:auto;border:1px;padding:1px;width:60%;top:1%",re.appendChild(u).appendChild(l);var e=C.getComputedStyle(l);n="1%"!==e.top,s=12===t(e.marginLeft),l.style.right="60%",o=36===t(e.right),r=36===t(e.width),l.style.position="absolute",i=12===t(l.offsetWidth/3),re.removeChild(u),l=null}}function t(e){return Math.round(parseFloat(e))}var n,r,i,o,a,s,u=E.createElement("div"),l=E.createElement("div");l.style&&(l.style.backgroundClip="content-box",l.cloneNode(!0).style.backgroundClip="",y.clearCloneStyle="content-box"===l.style.backgroundClip,S.extend(y,{boxSizingReliable:function(){return e(),r},pixelBoxStyles:function(){return e(),o},pixelPosition:function(){return e(),n},reliableMarginLeft:function(){return e(),s},scrollboxSize:function(){return e(),i},reliableTrDimensions:function(){var e,t,n,r;return null==a&&(e=E.createElement("table"),t=E.createElement("tr"),n=E.createElement("div"),e.style.cssText="position:absolute;left:-11111px",t.style.height="1px",n.style.height="9px",re.appendChild(e).appendChild(t).appendChild(n),r=C.getComputedStyle(t),a=3<parseInt(r.height),re.removeChild(e)),a}}))}();var _e=["Webkit","Moz","ms"],ze=E.createElement("div").style,Ue={};function Xe(e){var t=S.cssProps[e]||Ue[e];return t||(e in ze?e:Ue[e]=function(e){var t=e[0].toUpperCase()+e.slice(1),n=_e.length;while(n--)if((e=_e[n]+t)in ze)return e}(e)||e)}var Ve=/^(none|table(?!-c[ea]).+)/,Ge=/^--/,Ye={position:"absolute",visibility:"hidden",display:"block"},Qe={letterSpacing:"0",fontWeight:"400"};function Je(e,t,n){var r=te.exec(t);return r?Math.max(0,r[2]-(n||0))+(r[3]||"px"):t}function Ke(e,t,n,r,i,o){var a="width"===t?1:0,s=0,u=0;if(n===(r?"border":"content"))return 0;for(;a<4;a+=2)"margin"===n&&(u+=S.css(e,n+ne[a],!0,i)),r?("content"===n&&(u-=S.css(e,"padding"+ne[a],!0,i)),"margin"!==n&&(u-=S.css(e,"border"+ne[a]+"Width",!0,i))):(u+=S.css(e,"padding"+ne[a],!0,i),"padding"!==n?u+=S.css(e,"border"+ne[a]+"Width",!0,i):s+=S.css(e,"border"+ne[a]+"Width",!0,i));return!r&&0<=o&&(u+=Math.max(0,Math.ceil(e["offset"+t[0].toUpperCase()+t.slice(1)]-o-u-s-.5))||0),u}function Ze(e,t,n){var r=Ie(e),i=(!y.boxSizingReliable()||n)&&"border-box"===S.css(e,"boxSizing",!1,r),o=i,a=Be(e,t,r),s="offset"+t[0].toUpperCase()+t.slice(1);if(Me.test(a)){if(!n)return a;a="auto"}return(!y.boxSizingReliable()&&i||!y.reliableTrDimensions()&&A(e,"tr")||"auto"===a||!parseFloat(a)&&"inline"===S.css(e,"display",!1,r))&&e.getClientRects().length&&(i="border-box"===S.css(e,"boxSizing",!1,r),(o=s in e)&&(a=e[s])),(a=parseFloat(a)||0)+Ke(e,t,n||(i?"border":"content"),o,r,a)+"px"}function et(e,t,n,r,i){return new et.prototype.init(e,t,n,r,i)}S.extend({cssHooks:{opacity:{get:function(e,t){if(t){var n=Be(e,"opacity");return""===n?"1":n}}}},cssNumber:{animationIterationCount:!0,columnCount:!0,fillOpacity:!0,flexGrow:!0,flexShrink:!0,fontWeight:!0,gridArea:!0,gridColumn:!0,gridColumnEnd:!0,gridColumnStart:!0,gridRow:!0,gridRowEnd:!0,gridRowStart:!0,lineHeight:!0,opacity:!0,order:!0,orphans:!0,widows:!0,zIndex:!0,zoom:!0},cssProps:{},style:function(e,t,n,r){if(e&&3!==e.nodeType&&8!==e.nodeType&&e.style){var i,o,a,s=X(t),u=Ge.test(t),l=e.style;if(u||(t=Xe(s)),a=S.cssHooks[t]||S.cssHooks[s],void 0===n)return a&&"get"in a&&void 0!==(i=a.get(e,!1,r))?i:l[t];"string"===(o=typeof n)&&(i=te.exec(n))&&i[1]&&(n=se(e,t,i),o="number"),null!=n&&n==n&&("number"!==o||u||(n+=i&&i[3]||(S.cssNumber[s]?"":"px")),y.clearCloneStyle||""!==n||0!==t.indexOf("background")||(l[t]="inherit"),a&&"set"in a&&void 0===(n=a.set(e,n,r))||(u?l.setProperty(t,n):l[t]=n))}},css:function(e,t,n,r){var i,o,a,s=X(t);return Ge.test(t)||(t=Xe(s)),(a=S.cssHooks[t]||S.cssHooks[s])&&"get"in a&&(i=a.get(e,!0,n)),void 0===i&&(i=Be(e,t,r)),"normal"===i&&t in Qe&&(i=Qe[t]),""===n||n?(o=parseFloat(i),!0===n||isFinite(o)?o||0:i):i}}),S.each(["height","width"],function(e,u){S.cssHooks[u]={get:function(e,t,n){if(t)return!Ve.test(S.css(e,"display"))||e.getClientRects().length&&e.getBoundingClientRect().width?Ze(e,u,n):We(e,Ye,function(){return Ze(e,u,n)})},set:function(e,t,n){var r,i=Ie(e),o=!y.scrollboxSize()&&"absolute"===i.position,a=(o||n)&&"border-box"===S.css(e,"boxSizing",!1,i),s=n?Ke(e,u,n,a,i):0;return a&&o&&(s-=Math.ceil(e["offset"+u[0].toUpperCase()+u.slice(1)]-parseFloat(i[u])-Ke(e,u,"border",!1,i)-.5)),s&&(r=te.exec(t))&&"px"!==(r[3]||"px")&&(e.style[u]=t,t=S.css(e,u)),Je(0,t,s)}}}),S.cssHooks.marginLeft=$e(y.reliableMarginLeft,function(e,t){if(t)return(parseFloat(Be(e,"marginLeft"))||e.getBoundingClientRect().left-We(e,{marginLeft:0},function(){return e.getBoundingClientRect().left}))+"px"}),S.each({margin:"",padding:"",border:"Width"},function(i,o){S.cssHooks[i+o]={expand:function(e){for(var t=0,n={},r="string"==typeof e?e.split(" "):[e];t<4;t++)n[i+ne[t]+o]=r[t]||r[t-2]||r[0];return n}},"margin"!==i&&(S.cssHooks[i+o].set=Je)}),S.fn.extend({css:function(e,t){return $(this,function(e,t,n){var r,i,o={},a=0;if(Array.isArray(t)){for(r=Ie(e),i=t.length;a<i;a++)o[t[a]]=S.css(e,t[a],!1,r);return o}return void 0!==n?S.style(e,t,n):S.css(e,t)},e,t,1<arguments.length)}}),((S.Tween=et).prototype={constructor:et,init:function(e,t,n,r,i,o){this.elem=e,this.prop=n,this.easing=i||S.easing._default,this.options=t,this.start=this.now=this.cur(),this.end=r,this.unit=o||(S.cssNumber[n]?"":"px")},cur:function(){var e=et.propHooks[this.prop];return e&&e.get?e.get(this):et.propHooks._default.get(this)},run:function(e){var t,n=et.propHooks[this.prop];return this.options.duration?this.pos=t=S.easing[this.easing](e,this.options.duration*e,0,1,this.options.duration):this.pos=t=e,this.now=(this.end-this.start)*t+this.start,this.options.step&&this.options.step.call(this.elem,this.now,this),n&&n.set?n.set(this):et.propHooks._default.set(this),this}}).init.prototype=et.prototype,(et.propHooks={_default:{get:function(e){var t;return 1!==e.elem.nodeType||null!=e.elem[e.prop]&&null==e.elem.style[e.prop]?e.elem[e.prop]:(t=S.css(e.elem,e.prop,""))&&"auto"!==t?t:0},set:function(e){S.fx.step[e.prop]?S.fx.step[e.prop](e):1!==e.elem.nodeType||!S.cssHooks[e.prop]&&null==e.elem.style[Xe(e.prop)]?e.elem[e.prop]=e.now:S.style(e.elem,e.prop,e.now+e.unit)}}}).scrollTop=et.propHooks.scrollLeft={set:function(e){e.elem.nodeType&&e.elem.parentNode&&(e.elem[e.prop]=e.now)}},S.easing={linear:function(e){return e},swing:function(e){return.5-Math.cos(e*Math.PI)/2},_default:"swing"},S.fx=et.prototype.init,S.fx.step={};var tt,nt,rt,it,ot=/^(?:toggle|show|hide)$/,at=/queueHooks$/;function st(){nt&&(!1===E.hidden&&C.requestAnimationFrame?C.requestAnimationFrame(st):C.setTimeout(st,S.fx.interval),S.fx.tick())}function ut(){return C.setTimeout(function(){tt=void 0}),tt=Date.now()}function lt(e,t){var n,r=0,i={height:e};for(t=t?1:0;r<4;r+=2-t)i["margin"+(n=ne[r])]=i["padding"+n]=e;return t&&(i.opacity=i.width=e),i}function ct(e,t,n){for(var r,i=(ft.tweeners[t]||[]).concat(ft.tweeners["*"]),o=0,a=i.length;o<a;o++)if(r=i[o].call(n,t,e))return r}function ft(o,e,t){var n,a,r=0,i=ft.prefilters.length,s=S.Deferred().always(function(){delete u.elem}),u=function(){if(a)return!1;for(var e=tt||ut(),t=Math.max(0,l.startTime+l.duration-e),n=1-(t/l.duration||0),r=0,i=l.tweens.length;r<i;r++)l.tweens[r].run(n);return s.notifyWith(o,[l,n,t]),n<1&&i?t:(i||s.notifyWith(o,[l,1,0]),s.resolveWith(o,[l]),!1)},l=s.promise({elem:o,props:S.extend({},e),opts:S.extend(!0,{specialEasing:{},easing:S.easing._default},t),originalProperties:e,originalOptions:t,startTime:tt||ut(),duration:t.duration,tweens:[],createTween:function(e,t){var n=S.Tween(o,l.opts,e,t,l.opts.specialEasing[e]||l.opts.easing);return l.tweens.push(n),n},stop:function(e){var t=0,n=e?l.tweens.length:0;if(a)return this;for(a=!0;t<n;t++)l.tweens[t].run(1);return e?(s.notifyWith(o,[l,1,0]),s.resolveWith(o,[l,e])):s.rejectWith(o,[l,e]),this}}),c=l.props;for(!function(e,t){var n,r,i,o,a;for(n in e)if(i=t[r=X(n)],o=e[n],Array.isArray(o)&&(i=o[1],o=e[n]=o[0]),n!==r&&(e[r]=o,delete e[n]),(a=S.cssHooks[r])&&"expand"in a)for(n in o=a.expand(o),delete e[r],o)n in e||(e[n]=o[n],t[n]=i);else t[r]=i}(c,l.opts.specialEasing);r<i;r++)if(n=ft.prefilters[r].call(l,o,c,l.opts))return m(n.stop)&&(S._queueHooks(l.elem,l.opts.queue).stop=n.stop.bind(n)),n;return S.map(c,ct,l),m(l.opts.start)&&l.opts.start.call(o,l),l.progress(l.opts.progress).done(l.opts.done,l.opts.complete).fail(l.opts.fail).always(l.opts.always),S.fx.timer(S.extend(u,{elem:o,anim:l,queue:l.opts.queue})),l}S.Animation=S.extend(ft,{tweeners:{"*":[function(e,t){var n=this.createTween(e,t);return se(n.elem,e,te.exec(t),n),n}]},tweener:function(e,t){m(e)?(t=e,e=["*"]):e=e.match(P);for(var n,r=0,i=e.length;r<i;r++)n=e[r],ft.tweeners[n]=ft.tweeners[n]||[],ft.tweeners[n].unshift(t)},prefilters:[function(e,t,n){var r,i,o,a,s,u,l,c,f="width"in t||"height"in t,p=this,d={},h=e.style,g=e.nodeType&&ae(e),v=Y.get(e,"fxshow");for(r in n.queue||(null==(a=S._queueHooks(e,"fx")).unqueued&&(a.unqueued=0,s=a.empty.fire,a.empty.fire=function(){a.unqueued||s()}),a.unqueued++,p.always(function(){p.always(function(){a.unqueued--,S.queue(e,"fx").length||a.empty.fire()})})),t)if(i=t[r],ot.test(i)){if(delete t[r],o=o||"toggle"===i,i===(g?"hide":"show")){if("show"!==i||!v||void 0===v[r])continue;g=!0}d[r]=v&&v[r]||S.style(e,r)}if((u=!S.isEmptyObject(t))||!S.isEmptyObject(d))for(r in f&&1===e.nodeType&&(n.overflow=[h.overflow,h.overflowX,h.overflowY],null==(l=v&&v.display)&&(l=Y.get(e,"display")),"none"===(c=S.css(e,"display"))&&(l?c=l:(le([e],!0),l=e.style.display||l,c=S.css(e,"display"),le([e]))),("inline"===c||"inline-block"===c&&null!=l)&&"none"===S.css(e,"float")&&(u||(p.done(function(){h.display=l}),null==l&&(c=h.display,l="none"===c?"":c)),h.display="inline-block")),n.overflow&&(h.overflow="hidden",p.always(function(){h.overflow=n.overflow[0],h.overflowX=n.overflow[1],h.overflowY=n.overflow[2]})),u=!1,d)u||(v?"hidden"in v&&(g=v.hidden):v=Y.access(e,"fxshow",{display:l}),o&&(v.hidden=!g),g&&le([e],!0),p.done(function(){for(r in g||le([e]),Y.remove(e,"fxshow"),d)S.style(e,r,d[r])})),u=ct(g?v[r]:0,r,p),r in v||(v[r]=u.start,g&&(u.end=u.start,u.start=0))}],prefilter:function(e,t){t?ft.prefilters.unshift(e):ft.prefilters.push(e)}}),S.speed=function(e,t,n){var r=e&&"object"==typeof e?S.extend({},e):{complete:n||!n&&t||m(e)&&e,duration:e,easing:n&&t||t&&!m(t)&&t};return S.fx.off?r.duration=0:"number"!=typeof r.duration&&(r.duration in S.fx.speeds?r.duration=S.fx.speeds[r.duration]:r.duration=S.fx.speeds._default),null!=r.queue&&!0!==r.queue||(r.queue="fx"),r.old=r.complete,r.complete=function(){m(r.old)&&r.old.call(this),r.queue&&S.dequeue(this,r.queue)},r},S.fn.extend({fadeTo:function(e,t,n,r){return this.filter(ae).css("opacity",0).show().end().animate({opacity:t},e,n,r)},animate:function(t,e,n,r){var i=S.isEmptyObject(t),o=S.speed(e,n,r),a=function(){var e=ft(this,S.extend({},t),o);(i||Y.get(this,"finish"))&&e.stop(!0)};return a.finish=a,i||!1===o.queue?this.each(a):this.queue(o.queue,a)},stop:function(i,e,o){var a=function(e){var t=e.stop;delete e.stop,t(o)};return"string"!=typeof i&&(o=e,e=i,i=void 0),e&&this.queue(i||"fx",[]),this.each(function(){var e=!0,t=null!=i&&i+"queueHooks",n=S.timers,r=Y.get(this);if(t)r[t]&&r[t].stop&&a(r[t]);else for(t in r)r[t]&&r[t].stop&&at.test(t)&&a(r[t]);for(t=n.length;t--;)n[t].elem!==this||null!=i&&n[t].queue!==i||(n[t].anim.stop(o),e=!1,n.splice(t,1));!e&&o||S.dequeue(this,i)})},finish:function(a){return!1!==a&&(a=a||"fx"),this.each(function(){var e,t=Y.get(this),n=t[a+"queue"],r=t[a+"queueHooks"],i=S.timers,o=n?n.length:0;for(t.finish=!0,S.queue(this,a,[]),r&&r.stop&&r.stop.call(this,!0),e=i.length;e--;)i[e].elem===this&&i[e].queue===a&&(i[e].anim.stop(!0),i.splice(e,1));for(e=0;e<o;e++)n[e]&&n[e].finish&&n[e].finish.call(this);delete t.finish})}}),S.each(["toggle","show","hide"],function(e,r){var i=S.fn[r];S.fn[r]=function(e,t,n){return null==e||"boolean"==typeof e?i.apply(this,arguments):this.animate(lt(r,!0),e,t,n)}}),S.each({slideDown:lt("show"),slideUp:lt("hide"),slideToggle:lt("toggle"),fadeIn:{opacity:"show"},fadeOut:{opacity:"hide"},fadeToggle:{opacity:"toggle"}},function(e,r){S.fn[e]=function(e,t,n){return this.animate(r,e,t,n)}}),S.timers=[],S.fx.tick=function(){var e,t=0,n=S.timers;for(tt=Date.now();t<n.length;t++)(e=n[t])()||n[t]!==e||n.splice(t--,1);n.length||S.fx.stop(),tt=void 0},S.fx.timer=function(e){S.timers.push(e),S.fx.start()},S.fx.interval=13,S.fx.start=function(){nt||(nt=!0,st())},S.fx.stop=function(){nt=null},S.fx.speeds={slow:600,fast:200,_default:400},S.fn.delay=function(r,e){return r=S.fx&&S.fx.speeds[r]||r,e=e||"fx",this.queue(e,function(e,t){var n=C.setTimeout(e,r);t.stop=function(){C.clearTimeout(n)}})},rt=E.createElement("input"),it=E.createElement("select").appendChild(E.createElement("option")),rt.type="checkbox",y.checkOn=""!==rt.value,y.optSelected=it.selected,(rt=E.createElement("input")).value="t",rt.type="radio",y.radioValue="t"===rt.value;var pt,dt=S.expr.attrHandle;S.fn.extend({attr:function(e,t){return $(this,S.attr,e,t,1<arguments.length)},removeAttr:function(e){return this.each(function(){S.removeAttr(this,e)})}}),S.extend({attr:function(e,t,n){var r,i,o=e.nodeType;if(3!==o&&8!==o&&2!==o)return"undefined"==typeof e.getAttribute?S.prop(e,t,n):(1===o&&S.isXMLDoc(e)||(i=S.attrHooks[t.toLowerCase()]||(S.expr.match.bool.test(t)?pt:void 0)),void 0!==n?null===n?void S.removeAttr(e,t):i&&"set"in i&&void 0!==(r=i.set(e,n,t))?r:(e.setAttribute(t,n+""),n):i&&"get"in i&&null!==(r=i.get(e,t))?r:null==(r=S.find.attr(e,t))?void 0:r)},attrHooks:{type:{set:function(e,t){if(!y.radioValue&&"radio"===t&&A(e,"input")){var n=e.value;return e.setAttribute("type",t),n&&(e.value=n),t}}}},removeAttr:function(e,t){var n,r=0,i=t&&t.match(P);if(i&&1===e.nodeType)while(n=i[r++])e.removeAttribute(n)}}),pt={set:function(e,t,n){return!1===t?S.removeAttr(e,n):e.setAttribute(n,n),n}},S.each(S.expr.match.bool.source.match(/\w+/g),function(e,t){var a=dt[t]||S.find.attr;dt[t]=function(e,t,n){var r,i,o=t.toLowerCase();return n||(i=dt[o],dt[o]=r,r=null!=a(e,t,n)?o:null,dt[o]=i),r}});var ht=/^(?:input|select|textarea|button)$/i,gt=/^(?:a|area)$/i;function vt(e){return(e.match(P)||[]).join(" ")}function yt(e){return e.getAttribute&&e.getAttribute("class")||""}function mt(e){return Array.isArray(e)?e:"string"==typeof e&&e.match(P)||[]}S.fn.extend({prop:function(e,t){return $(this,S.prop,e,t,1<arguments.length)},removeProp:function(e){return this.each(function(){delete this[S.propFix[e]||e]})}}),S.extend({prop:function(e,t,n){var r,i,o=e.nodeType;if(3!==o&&8!==o&&2!==o)return 1===o&&S.isXMLDoc(e)||(t=S.propFix[t]||t,i=S.propHooks[t]),void 0!==n?i&&"set"in i&&void 0!==(r=i.set(e,n,t))?r:e[t]=n:i&&"get"in i&&null!==(r=i.get(e,t))?r:e[t]},propHooks:{tabIndex:{get:function(e){var t=S.find.attr(e,"tabindex");return t?parseInt(t,10):ht.test(e.nodeName)||gt.test(e.nodeName)&&e.href?0:-1}}},propFix:{"for":"htmlFor","class":"className"}}),y.optSelected||(S.propHooks.selected={get:function(e){var t=e.parentNode;return t&&t.parentNode&&t.parentNode.selectedIndex,null},set:function(e){var t=e.parentNode;t&&(t.selectedIndex,t.parentNode&&t.parentNode.selectedIndex)}}),S.each(["tabIndex","readOnly","maxLength","cellSpacing","cellPadding","rowSpan","colSpan","useMap","frameBorder","contentEditable"],function(){S.propFix[this.toLowerCase()]=this}),S.fn.extend({addClass:function(t){var e,n,r,i,o,a,s,u=0;if(m(t))return this.each(function(e){S(this).addClass(t.call(this,e,yt(this)))});if((e=mt(t)).length)while(n=this[u++])if(i=yt(n),r=1===n.nodeType&&" "+vt(i)+" "){a=0;while(o=e[a++])r.indexOf(" "+o+" ")<0&&(r+=o+" ");i!==(s=vt(r))&&n.setAttribute("class",s)}return this},removeClass:function(t){var e,n,r,i,o,a,s,u=0;if(m(t))return this.each(function(e){S(this).removeClass(t.call(this,e,yt(this)))});if(!arguments.length)return this.attr("class","");if((e=mt(t)).length)while(n=this[u++])if(i=yt(n),r=1===n.nodeType&&" "+vt(i)+" "){a=0;while(o=e[a++])while(-1<r.indexOf(" "+o+" "))r=r.replace(" "+o+" "," ");i!==(s=vt(r))&&n.setAttribute("class",s)}return this},toggleClass:function(i,t){var o=typeof i,a="string"===o||Array.isArray(i);return"boolean"==typeof t&&a?t?this.addClass(i):this.removeClass(i):m(i)?this.each(function(e){S(this).toggleClass(i.call(this,e,yt(this),t),t)}):this.each(function(){var e,t,n,r;if(a){t=0,n=S(this),r=mt(i);while(e=r[t++])n.hasClass(e)?n.removeClass(e):n.addClass(e)}else void 0!==i&&"boolean"!==o||((e=yt(this))&&Y.set(this,"__className__",e),this.setAttribute&&this.setAttribute("class",e||!1===i?"":Y.get(this,"__className__")||""))})},hasClass:function(e){var t,n,r=0;t=" "+e+" ";while(n=this[r++])if(1===n.nodeType&&-1<(" "+vt(yt(n))+" ").indexOf(t))return!0;return!1}});var xt=/\r/g;S.fn.extend({val:function(n){var r,e,i,t=this[0];return arguments.length?(i=m(n),this.each(function(e){var t;1===this.nodeType&&(null==(t=i?n.call(this,e,S(this).val()):n)?t="":"number"==typeof t?t+="":Array.isArray(t)&&(t=S.map(t,function(e){return null==e?"":e+""})),(r=S.valHooks[this.type]||S.valHooks[this.nodeName.toLowerCase()])&&"set"in r&&void 0!==r.set(this,t,"value")||(this.value=t))})):t?(r=S.valHooks[t.type]||S.valHooks[t.nodeName.toLowerCase()])&&"get"in r&&void 0!==(e=r.get(t,"value"))?e:"string"==typeof(e=t.value)?e.replace(xt,""):null==e?"":e:void 0}}),S.extend({valHooks:{option:{get:function(e){var t=S.find.attr(e,"value");return null!=t?t:vt(S.text(e))}},select:{get:function(e){var t,n,r,i=e.options,o=e.selectedIndex,a="select-one"===e.type,s=a?null:[],u=a?o+1:i.length;for(r=o<0?u:a?o:0;r<u;r++)if(((n=i[r]).selected||r===o)&&!n.disabled&&(!n.parentNode.disabled||!A(n.parentNode,"optgroup"))){if(t=S(n).val(),a)return t;s.push(t)}return s},set:function(e,t){var n,r,i=e.options,o=S.makeArray(t),a=i.length;while(a--)((r=i[a]).selected=-1<S.inArray(S.valHooks.option.get(r),o))&&(n=!0);return n||(e.selectedIndex=-1),o}}}}),S.each(["radio","checkbox"],function(){S.valHooks[this]={set:function(e,t){if(Array.isArray(t))return e.checked=-1<S.inArray(S(e).val(),t)}},y.checkOn||(S.valHooks[this].get=function(e){return null===e.getAttribute("value")?"on":e.value})}),y.focusin="onfocusin"in C;var bt=/^(?:focusinfocus|focusoutblur)$/,wt=function(e){e.stopPropagation()};S.extend(S.event,{trigger:function(e,t,n,r){var i,o,a,s,u,l,c,f,p=[n||E],d=v.call(e,"type")?e.type:e,h=v.call(e,"namespace")?e.namespace.split("."):[];if(o=f=a=n=n||E,3!==n.nodeType&&8!==n.nodeType&&!bt.test(d+S.event.triggered)&&(-1<d.indexOf(".")&&(d=(h=d.split(".")).shift(),h.sort()),u=d.indexOf(":")<0&&"on"+d,(e=e[S.expando]?e:new S.Event(d,"object"==typeof e&&e)).isTrigger=r?2:3,e.namespace=h.join("."),e.rnamespace=e.namespace?new RegExp("(^|\\.)"+h.join("\\.(?:.*\\.|)")+"(\\.|$)"):null,e.result=void 0,e.target||(e.target=n),t=null==t?[e]:S.makeArray(t,[e]),c=S.event.special[d]||{},r||!c.trigger||!1!==c.trigger.apply(n,t))){if(!r&&!c.noBubble&&!x(n)){for(s=c.delegateType||d,bt.test(s+d)||(o=o.parentNode);o;o=o.parentNode)p.push(o),a=o;a===(n.ownerDocument||E)&&p.push(a.defaultView||a.parentWindow||C)}i=0;while((o=p[i++])&&!e.isPropagationStopped())f=o,e.type=1<i?s:c.bindType||d,(l=(Y.get(o,"events")||Object.create(null))[e.type]&&Y.get(o,"handle"))&&l.apply(o,t),(l=u&&o[u])&&l.apply&&V(o)&&(e.result=l.apply(o,t),!1===e.result&&e.preventDefault());return e.type=d,r||e.isDefaultPrevented()||c._default&&!1!==c._default.apply(p.pop(),t)||!V(n)||u&&m(n[d])&&!x(n)&&((a=n[u])&&(n[u]=null),S.event.triggered=d,e.isPropagationStopped()&&f.addEventListener(d,wt),n[d](),e.isPropagationStopped()&&f.removeEventListener(d,wt),S.event.triggered=void 0,a&&(n[u]=a)),e.result}},simulate:function(e,t,n){var r=S.extend(new S.Event,n,{type:e,isSimulated:!0});S.event.trigger(r,null,t)}}),S.fn.extend({trigger:function(e,t){return this.each(function(){S.event.trigger(e,t,this)})},triggerHandler:function(e,t){var n=this[0];if(n)return S.event.trigger(e,t,n,!0)}}),y.focusin||S.each({focus:"focusin",blur:"focusout"},function(n,r){var i=function(e){S.event.simulate(r,e.target,S.event.fix(e))};S.event.special[r]={setup:function(){var e=this.ownerDocument||this.document||this,t=Y.access(e,r);t||e.addEventListener(n,i,!0),Y.access(e,r,(t||0)+1)},teardown:function(){var e=this.ownerDocument||this.document||this,t=Y.access(e,r)-1;t?Y.access(e,r,t):(e.removeEventListener(n,i,!0),Y.remove(e,r))}}});var Tt=C.location,Ct={guid:Date.now()},Et=/\?/;S.parseXML=function(e){var t;if(!e||"string"!=typeof e)return null;try{t=(new C.DOMParser).parseFromString(e,"text/xml")}catch(e){t=void 0}return t&&!t.getElementsByTagName("parsererror").length||S.error("Invalid XML: "+e),t};var St=/\[\]$/,kt=/\r?\n/g,At=/^(?:submit|button|image|reset|file)$/i,Nt=/^(?:input|select|textarea|keygen)/i;function Dt(n,e,r,i){var t;if(Array.isArray(e))S.each(e,function(e,t){r||St.test(n)?i(n,t):Dt(n+"["+("object"==typeof t&&null!=t?e:"")+"]",t,r,i)});else if(r||"object"!==w(e))i(n,e);else for(t in e)Dt(n+"["+t+"]",e[t],r,i)}S.param=function(e,t){var n,r=[],i=function(e,t){var n=m(t)?t():t;r[r.length]=encodeURIComponent(e)+"="+encodeURIComponent(null==n?"":n)};if(null==e)return"";if(Array.isArray(e)||e.jquery&&!S.isPlainObject(e))S.each(e,function(){i(this.name,this.value)});else for(n in e)Dt(n,e[n],t,i);return r.join("&")},S.fn.extend({serialize:function(){return S.param(this.serializeArray())},serializeArray:function(){return this.map(function(){var e=S.prop(this,"elements");return e?S.makeArray(e):this}).filter(function(){var e=this.type;return this.name&&!S(this).is(":disabled")&&Nt.test(this.nodeName)&&!At.test(e)&&(this.checked||!pe.test(e))}).map(function(e,t){var n=S(this).val();return null==n?null:Array.isArray(n)?S.map(n,function(e){return{name:t.name,value:e.replace(kt,"\r\n")}}):{name:t.name,value:n.replace(kt,"\r\n")}}).get()}});var jt=/%20/g,qt=/#.*$/,Lt=/([?&])_=[^&]*/,Ht=/^(.*?):[ \t]*([^\r\n]*)$/gm,Ot=/^(?:GET|HEAD)$/,Pt=/^\/\//,Rt={},Mt={},It="*/".concat("*"),Wt=E.createElement("a");function Ft(o){return function(e,t){"string"!=typeof e&&(t=e,e="*");var n,r=0,i=e.toLowerCase().match(P)||[];if(m(t))while(n=i[r++])"+"===n[0]?(n=n.slice(1)||"*",(o[n]=o[n]||[]).unshift(t)):(o[n]=o[n]||[]).push(t)}}function Bt(t,i,o,a){var s={},u=t===Mt;function l(e){var r;return s[e]=!0,S.each(t[e]||[],function(e,t){var n=t(i,o,a);return"string"!=typeof n||u||s[n]?u?!(r=n):void 0:(i.dataTypes.unshift(n),l(n),!1)}),r}return l(i.dataTypes[0])||!s["*"]&&l("*")}function $t(e,t){var n,r,i=S.ajaxSettings.flatOptions||{};for(n in t)void 0!==t[n]&&((i[n]?e:r||(r={}))[n]=t[n]);return r&&S.extend(!0,e,r),e}Wt.href=Tt.href,S.extend({active:0,lastModified:{},etag:{},ajaxSettings:{url:Tt.href,type:"GET",isLocal:/^(?:about|app|app-storage|.+-extension|file|res|widget):$/.test(Tt.protocol),global:!0,processData:!0,async:!0,contentType:"application/x-www-form-urlencoded; charset=UTF-8",accepts:{"*":It,text:"text/plain",html:"text/html",xml:"application/xml, text/xml",json:"application/json, text/javascript"},contents:{xml:/\bxml\b/,html:/\bhtml/,json:/\bjson\b/},responseFields:{xml:"responseXML",text:"responseText",json:"responseJSON"},converters:{"* text":String,"text html":!0,"text json":JSON.parse,"text xml":S.parseXML},flatOptions:{url:!0,context:!0}},ajaxSetup:function(e,t){return t?$t($t(e,S.ajaxSettings),t):$t(S.ajaxSettings,e)},ajaxPrefilter:Ft(Rt),ajaxTransport:Ft(Mt),ajax:function(e,t){"object"==typeof e&&(t=e,e=void 0),t=t||{};var c,f,p,n,d,r,h,g,i,o,v=S.ajaxSetup({},t),y=v.context||v,m=v.context&&(y.nodeType||y.jquery)?S(y):S.event,x=S.Deferred(),b=S.Callbacks("once memory"),w=v.statusCode||{},a={},s={},u="canceled",T={readyState:0,getResponseHeader:function(e){var t;if(h){if(!n){n={};while(t=Ht.exec(p))n[t[1].toLowerCase()+" "]=(n[t[1].toLowerCase()+" "]||[]).concat(t[2])}t=n[e.toLowerCase()+" "]}return null==t?null:t.join(", ")},getAllResponseHeaders:function(){return h?p:null},setRequestHeader:function(e,t){return null==h&&(e=s[e.toLowerCase()]=s[e.toLowerCase()]||e,a[e]=t),this},overrideMimeType:function(e){return null==h&&(v.mimeType=e),this},statusCode:function(e){var t;if(e)if(h)T.always(e[T.status]);else for(t in e)w[t]=[w[t],e[t]];return this},abort:function(e){var t=e||u;return c&&c.abort(t),l(0,t),this}};if(x.promise(T),v.url=((e||v.url||Tt.href)+"").replace(Pt,Tt.protocol+"//"),v.type=t.method||t.type||v.method||v.type,v.dataTypes=(v.dataType||"*").toLowerCase().match(P)||[""],null==v.crossDomain){r=E.createElement("a");try{r.href=v.url,r.href=r.href,v.crossDomain=Wt.protocol+"//"+Wt.host!=r.protocol+"//"+r.host}catch(e){v.crossDomain=!0}}if(v.data&&v.processData&&"string"!=typeof v.data&&(v.data=S.param(v.data,v.traditional)),Bt(Rt,v,t,T),h)return T;for(i in(g=S.event&&v.global)&&0==S.active++&&S.event.trigger("ajaxStart"),v.type=v.type.toUpperCase(),v.hasContent=!Ot.test(v.type),f=v.url.replace(qt,""),v.hasContent?v.data&&v.processData&&0===(v.contentType||"").indexOf("application/x-www-form-urlencoded")&&(v.data=v.data.replace(jt,"+")):(o=v.url.slice(f.length),v.data&&(v.processData||"string"==typeof v.data)&&(f+=(Et.test(f)?"&":"?")+v.data,delete v.data),!1===v.cache&&(f=f.replace(Lt,"$1"),o=(Et.test(f)?"&":"?")+"_="+Ct.guid+++o),v.url=f+o),v.ifModified&&(S.lastModified[f]&&T.setRequestHeader("If-Modified-Since",S.lastModified[f]),S.etag[f]&&T.setRequestHeader("If-None-Match",S.etag[f])),(v.data&&v.hasContent&&!1!==v.contentType||t.contentType)&&T.setRequestHeader("Content-Type",v.contentType),T.setRequestHeader("Accept",v.dataTypes[0]&&v.accepts[v.dataTypes[0]]?v.accepts[v.dataTypes[0]]+("*"!==v.dataTypes[0]?", "+It+"; q=0.01":""):v.accepts["*"]),v.headers)T.setRequestHeader(i,v.headers[i]);if(v.beforeSend&&(!1===v.beforeSend.call(y,T,v)||h))return T.abort();if(u="abort",b.add(v.complete),T.done(v.success),T.fail(v.error),c=Bt(Mt,v,t,T)){if(T.readyState=1,g&&m.trigger("ajaxSend",[T,v]),h)return T;v.async&&0<v.timeout&&(d=C.setTimeout(function(){T.abort("timeout")},v.timeout));try{h=!1,c.send(a,l)}catch(e){if(h)throw e;l(-1,e)}}else l(-1,"No Transport");function l(e,t,n,r){var i,o,a,s,u,l=t;h||(h=!0,d&&C.clearTimeout(d),c=void 0,p=r||"",T.readyState=0<e?4:0,i=200<=e&&e<300||304===e,n&&(s=function(e,t,n){var r,i,o,a,s=e.contents,u=e.dataTypes;while("*"===u[0])u.shift(),void 0===r&&(r=e.mimeType||t.getResponseHeader("Content-Type"));if(r)for(i in s)if(s[i]&&s[i].test(r)){u.unshift(i);break}if(u[0]in n)o=u[0];else{for(i in n){if(!u[0]||e.converters[i+" "+u[0]]){o=i;break}a||(a=i)}o=o||a}if(o)return o!==u[0]&&u.unshift(o),n[o]}(v,T,n)),!i&&-1<S.inArray("script",v.dataTypes)&&(v.converters["text script"]=function(){}),s=function(e,t,n,r){var i,o,a,s,u,l={},c=e.dataTypes.slice();if(c[1])for(a in e.converters)l[a.toLowerCase()]=e.converters[a];o=c.shift();while(o)if(e.responseFields[o]&&(n[e.responseFields[o]]=t),!u&&r&&e.dataFilter&&(t=e.dataFilter(t,e.dataType)),u=o,o=c.shift())if("*"===o)o=u;else if("*"!==u&&u!==o){if(!(a=l[u+" "+o]||l["* "+o]))for(i in l)if((s=i.split(" "))[1]===o&&(a=l[u+" "+s[0]]||l["* "+s[0]])){!0===a?a=l[i]:!0!==l[i]&&(o=s[0],c.unshift(s[1]));break}if(!0!==a)if(a&&e["throws"])t=a(t);else try{t=a(t)}catch(e){return{state:"parsererror",error:a?e:"No conversion from "+u+" to "+o}}}return{state:"success",data:t}}(v,s,T,i),i?(v.ifModified&&((u=T.getResponseHeader("Last-Modified"))&&(S.lastModified[f]=u),(u=T.getResponseHeader("etag"))&&(S.etag[f]=u)),204===e||"HEAD"===v.type?l="nocontent":304===e?l="notmodified":(l=s.state,o=s.data,i=!(a=s.error))):(a=l,!e&&l||(l="error",e<0&&(e=0))),T.status=e,T.statusText=(t||l)+"",i?x.resolveWith(y,[o,l,T]):x.rejectWith(y,[T,l,a]),T.statusCode(w),w=void 0,g&&m.trigger(i?"ajaxSuccess":"ajaxError",[T,v,i?o:a]),b.fireWith(y,[T,l]),g&&(m.trigger("ajaxComplete",[T,v]),--S.active||S.event.trigger("ajaxStop")))}return T},getJSON:function(e,t,n){return S.get(e,t,n,"json")},getScript:function(e,t){return S.get(e,void 0,t,"script")}}),S.each(["get","post"],function(e,i){S[i]=function(e,t,n,r){return m(t)&&(r=r||n,n=t,t=void 0),S.ajax(S.extend({url:e,type:i,dataType:r,data:t,success:n},S.isPlainObject(e)&&e))}}),S.ajaxPrefilter(function(e){var t;for(t in e.headers)"content-type"===t.toLowerCase()&&(e.contentType=e.headers[t]||"")}),S._evalUrl=function(e,t,n){return S.ajax({url:e,type:"GET",dataType:"script",cache:!0,async:!1,global:!1,converters:{"text script":function(){}},dataFilter:function(e){S.globalEval(e,t,n)}})},S.fn.extend({wrapAll:function(e){var t;return this[0]&&(m(e)&&(e=e.call(this[0])),t=S(e,this[0].ownerDocument).eq(0).clone(!0),this[0].parentNode&&t.insertBefore(this[0]),t.map(function(){var e=this;while(e.firstElementChild)e=e.firstElementChild;return e}).append(this)),this},wrapInner:function(n){return m(n)?this.each(function(e){S(this).wrapInner(n.call(this,e))}):this.each(function(){var e=S(this),t=e.contents();t.length?t.wrapAll(n):e.append(n)})},wrap:function(t){var n=m(t);return this.each(function(e){S(this).wrapAll(n?t.call(this,e):t)})},unwrap:function(e){return this.parent(e).not("body").each(function(){S(this).replaceWith(this.childNodes)}),this}}),S.expr.pseudos.hidden=function(e){return!S.expr.pseudos.visible(e)},S.expr.pseudos.visible=function(e){return!!(e.offsetWidth||e.offsetHeight||e.getClientRects().length)},S.ajaxSettings.xhr=function(){try{return new C.XMLHttpRequest}catch(e){}};var _t={0:200,1223:204},zt=S.ajaxSettings.xhr();y.cors=!!zt&&"withCredentials"in zt,y.ajax=zt=!!zt,S.ajaxTransport(function(i){var o,a;if(y.cors||zt&&!i.crossDomain)return{send:function(e,t){var n,r=i.xhr();if(r.open(i.type,i.url,i.async,i.username,i.password),i.xhrFields)for(n in i.xhrFields)r[n]=i.xhrFields[n];for(n in i.mimeType&&r.overrideMimeType&&r.overrideMimeType(i.mimeType),i.crossDomain||e["X-Requested-With"]||(e["X-Requested-With"]="XMLHttpRequest"),e)r.setRequestHeader(n,e[n]);o=function(e){return function(){o&&(o=a=r.onload=r.onerror=r.onabort=r.ontimeout=r.onreadystatechange=null,"abort"===e?r.abort():"error"===e?"number"!=typeof r.status?t(0,"error"):t(r.status,r.statusText):t(_t[r.status]||r.status,r.statusText,"text"!==(r.responseType||"text")||"string"!=typeof r.responseText?{binary:r.response}:{text:r.responseText},r.getAllResponseHeaders()))}},r.onload=o(),a=r.onerror=r.ontimeout=o("error"),void 0!==r.onabort?r.onabort=a:r.onreadystatechange=function(){4===r.readyState&&C.setTimeout(function(){o&&a()})},o=o("abort");try{r.send(i.hasContent&&i.data||null)}catch(e){if(o)throw e}},abort:function(){o&&o()}}}),S.ajaxPrefilter(function(e){e.crossDomain&&(e.contents.script=!1)}),S.ajaxSetup({accepts:{script:"text/javascript, application/javascript, application/ecmascript, application/x-ecmascript"},contents:{script:/\b(?:java|ecma)script\b/},converters:{"text script":function(e){return S.globalEval(e),e}}}),S.ajaxPrefilter("script",function(e){void 0===e.cache&&(e.cache=!1),e.crossDomain&&(e.type="GET")}),S.ajaxTransport("script",function(n){var r,i;if(n.crossDomain||n.scriptAttrs)return{send:function(e,t){r=S("<script>").attr(n.scriptAttrs||{}).prop({charset:n.scriptCharset,src:n.url}).on("load error",i=function(e){r.remove(),i=null,e&&t("error"===e.type?404:200,e.type)}),E.head.appendChild(r[0])},abort:function(){i&&i()}}});var Ut,Xt=[],Vt=/(=)\?(?=&|$)|\?\?/;S.ajaxSetup({jsonp:"callback",jsonpCallback:function(){var e=Xt.pop()||S.expando+"_"+Ct.guid++;return this[e]=!0,e}}),S.ajaxPrefilter("json jsonp",function(e,t,n){var r,i,o,a=!1!==e.jsonp&&(Vt.test(e.url)?"url":"string"==typeof e.data&&0===(e.contentType||"").indexOf("application/x-www-form-urlencoded")&&Vt.test(e.data)&&"data");if(a||"jsonp"===e.dataTypes[0])return r=e.jsonpCallback=m(e.jsonpCallback)?e.jsonpCallback():e.jsonpCallback,a?e[a]=e[a].replace(Vt,"$1"+r):!1!==e.jsonp&&(e.url+=(Et.test(e.url)?"&":"?")+e.jsonp+"="+r),e.converters["script json"]=function(){return o||S.error(r+" was not called"),o[0]},e.dataTypes[0]="json",i=C[r],C[r]=function(){o=arguments},n.always(function(){void 0===i?S(C).removeProp(r):C[r]=i,e[r]&&(e.jsonpCallback=t.jsonpCallback,Xt.push(r)),o&&m(i)&&i(o[0]),o=i=void 0}),"script"}),y.createHTMLDocument=((Ut=E.implementation.createHTMLDocument("").body).innerHTML="<form></form><form></form>",2===Ut.childNodes.length),S.parseHTML=function(e,t,n){return"string"!=typeof e?[]:("boolean"==typeof t&&(n=t,t=!1),t||(y.createHTMLDocument?((r=(t=E.implementation.createHTMLDocument("")).createElement("base")).href=E.location.href,t.head.appendChild(r)):t=E),o=!n&&[],(i=N.exec(e))?[t.createElement(i[1])]:(i=xe([e],t,o),o&&o.length&&S(o).remove(),S.merge([],i.childNodes)));var r,i,o},S.fn.load=function(e,t,n){var r,i,o,a=this,s=e.indexOf(" ");return-1<s&&(r=vt(e.slice(s)),e=e.slice(0,s)),m(t)?(n=t,t=void 0):t&&"object"==typeof t&&(i="POST"),0<a.length&&S.ajax({url:e,type:i||"GET",dataType:"html",data:t}).done(function(e){o=arguments,a.html(r?S("<div>").append(S.parseHTML(e)).find(r):e)}).always(n&&function(e,t){a.each(function(){n.apply(this,o||[e.responseText,t,e])})}),this},S.expr.pseudos.animated=function(t){return S.grep(S.timers,function(e){return t===e.elem}).length},S.offset={setOffset:function(e,t,n){var r,i,o,a,s,u,l=S.css(e,"position"),c=S(e),f={};"static"===l&&(e.style.position="relative"),s=c.offset(),o=S.css(e,"top"),u=S.css(e,"left"),("absolute"===l||"fixed"===l)&&-1<(o+u).indexOf("auto")?(a=(r=c.position()).top,i=r.left):(a=parseFloat(o)||0,i=parseFloat(u)||0),m(t)&&(t=t.call(e,n,S.extend({},s))),null!=t.top&&(f.top=t.top-s.top+a),null!=t.left&&(f.left=t.left-s.left+i),"using"in t?t.using.call(e,f):("number"==typeof f.top&&(f.top+="px"),"number"==typeof f.left&&(f.left+="px"),c.css(f))}},S.fn.extend({offset:function(t){if(arguments.length)return void 0===t?this:this.each(function(e){S.offset.setOffset(this,t,e)});var e,n,r=this[0];return r?r.getClientRects().length?(e=r.getBoundingClientRect(),n=r.ownerDocument.defaultView,{top:e.top+n.pageYOffset,left:e.left+n.pageXOffset}):{top:0,left:0}:void 0},position:function(){if(this[0]){var e,t,n,r=this[0],i={top:0,left:0};if("fixed"===S.css(r,"position"))t=r.getBoundingClientRect();else{t=this.offset(),n=r.ownerDocument,e=r.offsetParent||n.documentElement;while(e&&(e===n.body||e===n.documentElement)&&"static"===S.css(e,"position"))e=e.parentNode;e&&e!==r&&1===e.nodeType&&((i=S(e).offset()).top+=S.css(e,"borderTopWidth",!0),i.left+=S.css(e,"borderLeftWidth",!0))}return{top:t.top-i.top-S.css(r,"marginTop",!0),left:t.left-i.left-S.css(r,"marginLeft",!0)}}},offsetParent:function(){return this.map(function(){var e=this.offsetParent;while(e&&"static"===S.css(e,"position"))e=e.offsetParent;return e||re})}}),S.each({scrollLeft:"pageXOffset",scrollTop:"pageYOffset"},function(t,i){var o="pageYOffset"===i;S.fn[t]=function(e){return $(this,function(e,t,n){var r;if(x(e)?r=e:9===e.nodeType&&(r=e.defaultView),void 0===n)return r?r[i]:e[t];r?r.scrollTo(o?r.pageXOffset:n,o?n:r.pageYOffset):e[t]=n},t,e,arguments.length)}}),S.each(["top","left"],function(e,n){S.cssHooks[n]=$e(y.pixelPosition,function(e,t){if(t)return t=Be(e,n),Me.test(t)?S(e).position()[n]+"px":t})}),S.each({Height:"height",Width:"width"},function(a,s){S.each({padding:"inner"+a,content:s,"":"outer"+a},function(r,o){S.fn[o]=function(e,t){var n=arguments.length&&(r||"boolean"!=typeof e),i=r||(!0===e||!0===t?"margin":"border");return $(this,function(e,t,n){var r;return x(e)?0===o.indexOf("outer")?e["inner"+a]:e.document.documentElement["client"+a]:9===e.nodeType?(r=e.documentElement,Math.max(e.body["scroll"+a],r["scroll"+a],e.body["offset"+a],r["offset"+a],r["client"+a])):void 0===n?S.css(e,t,i):S.style(e,t,n,i)},s,n?e:void 0,n)}})}),S.each(["ajaxStart","ajaxStop","ajaxComplete","ajaxError","ajaxSuccess","ajaxSend"],function(e,t){S.fn[t]=function(e){return this.on(t,e)}}),S.fn.extend({bind:function(e,t,n){return this.on(e,null,t,n)},unbind:function(e,t){return this.off(e,null,t)},delegate:function(e,t,n,r){return this.on(t,e,n,r)},undelegate:function(e,t,n){return 1===arguments.length?this.off(e,"**"):this.off(t,e||"**",n)},hover:function(e,t){return this.mouseenter(e).mouseleave(t||e)}}),S.each("blur focus focusin focusout resize scroll click dblclick mousedown mouseup mousemove mouseover mouseout mouseenter mouseleave change select submit keydown keypress keyup contextmenu".split(" "),function(e,n){S.fn[n]=function(e,t){return 0<arguments.length?this.on(n,null,e,t):this.trigger(n)}});var Gt=/^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g;S.proxy=function(e,t){var n,r,i;if("string"==typeof t&&(n=e[t],t=e,e=n),m(e))return r=s.call(arguments,2),(i=function(){return e.apply(t||this,r.concat(s.call(arguments)))}).guid=e.guid=e.guid||S.guid++,i},S.holdReady=function(e){e?S.readyWait++:S.ready(!0)},S.isArray=Array.isArray,S.parseJSON=JSON.parse,S.nodeName=A,S.isFunction=m,S.isWindow=x,S.camelCase=X,S.type=w,S.now=Date.now,S.isNumeric=function(e){var t=S.type(e);return("number"===t||"string"===t)&&!isNaN(e-parseFloat(e))},S.trim=function(e){return null==e?"":(e+"").replace(Gt,"")},"function"==typeof define&&define.amd&&define("jquery",[],function(){return S});var Yt=C.jQuery,Qt=C.$;return S.noConflict=function(e){return C.$===S&&(C.$=Qt),e&&C.jQuery===S&&(C.jQuery=Yt),S},"undefined"==typeof e&&(C.jQuery=C.$=S),S});