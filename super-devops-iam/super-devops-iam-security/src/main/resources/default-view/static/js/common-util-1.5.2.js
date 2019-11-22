/**
 * Common util v1.5.2 | (c) 2017, 2022 wl4g Foundation, Inc.
 * Copyright 2017-2032 <wangsir@gmail.com, 983708408@qq.com>, Inc. x
 * Licensed under Apache2.0 (https://github.com/wl4g/super-devops/blob/master/LICENSE)
 */
(function(window, document) {
	// Exposing the API to the outside.
	if(!window.Common){ window.Common = {}; }

	// Common util.
	window.Common.Util = {
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
		isObject : function (obj) {
		    return Object.prototype.toString.call(obj) === '[object Object]';
		},
		isFunction : function (obj) {
		    return Object.prototype.toString.call(obj) === '[object Function]';
		},
		isString : function (obj) {
		    return Object.prototype.toString.call(obj) === '[object String]';
		},
		getCookie: function(cookieName){
			var cookiesArr = document.cookie.split(";");
			for(var i=0; i < cookiesArr.length; i++){
				var cookie = cookiesArr[i].split("=");
				var value = cookie[1];
				if(cookie[0].trim() == cookieName.trim()){
					return value;
				}
			}
		},
		isEnabled: function(value){
			if(!Common.Util.isEmpty(value)){
				value = value.toLowerCase();
				return  (value == "y" || value == "on" || value == "yes" || value == "1" || value == "enabled" || value == "true" || value == "t");
			}
			return false;
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
		PlatformType: (function() {
		    var ua = navigator.userAgent.toLowerCase();
		    var mua = {
		        WECHAT: /micromessenger/.test(ua), //WeChat MicroMessenger
		        IOS: /ipod|iphone|ipad/.test(ua), //iOS
		       	MAC: /macintosh|mac os/.test(ua), // Mac
		        IPHONE: /iphone/.test(ua), //iPhone
		        IPAD: /ipad/.test(ua), //iPad
		        ANDROID: /android/.test(ua), //Android Device
		        WINDOWS: /windows/.test(ua), //Windows Device
		        TOUCH_DEVICE: ('ontouchstart' in window) || /touch/.test(ua), //Touch Device
		        MOBILE: /mobile/.test(ua), //Mobile Device (iPad)
		        ANDROID_TABLET: false, //Android Tablet
		        WINDOWS_TABLET: false, //Windows Tablet
		        TABLET: false, //Tablet (iPad, Android, Windows)
		        SMART_PHONE: false //Smart Phone (iPhone, Android)
		    };
		    mua.ANDROID_TABLET = mua.ANDROID && !mua.MOBILE;
		    mua.WINDOWS_TABLET = mua.WINDOWS && /tablet/.test(ua);
		    mua.TABLET = mua.IPAD || mua.ANDROID_TABLET || mua.WINDOWS_TABLET;
		    mua.SMART_PHONE = mua.MOBILE && !mua.TABLET;
		    return mua;
		})(),
		int2char(n) {
		    return "0123456789abcdefghijklmnopqrstuvwxyz".charAt(n);
		},
		language() {
		    return (navigator.language || navigator.browserLanguage || navigator.systemLanguage).toLowerCase();
		},
		isZhCN() {
		    return Common.Util.language().indexOf('zh') >= 0;
		},
		base64ToHex(s) { // convert a base64 string to hex
		    var ret = "";
		    var i;
		    var k = 0; // b64 state, 0-3
		    var slop = 0;
		    for (i = 0; i < s.length; ++i) {
		        if (s.charAt(i) == "=") {
		            break;
		        }
		        var v = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf(s.charAt(i));
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
        }

	};
})(window, document);