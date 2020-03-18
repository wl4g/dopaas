!
function(t, a) {
	var r = 1e4,
	g_moduleConfig = {
		uabModule: {
			stable: ["AWSC/uab/121.js"],
			grey: ["AWSC/uab/122.js"],
			ratio: 10000
		},
		umidPCModule: {
			stable: ["AWSC/WebUMID/1.79.2/um.js"],
			grey: ["iam-server/view/sdk/AWSC/WebUMID/1.80.0/um.js"],
			ratio: 9999
		},
		ncPCModule: {
			stable: ["js/nc/60.js"],
			grey: ["js/nc/60.js"],
			ratio: 1e4
		},
		ncH5Module: {
			stable: ["js/nc/60.js"],
			grey: ["js/nc/60.js"],
			ratio: 1e4
		}
	},
	s = [{
		name: "umidPCModule",
		features: ["umpc", "um", "umh5"],
		depends: [],
		sync: !1
	},
	{
		name: "uabModule",
		features: ["uab"],
		depends: [],
		sync: !1
	},
	{
		name: "nsModule",
		features: ["ns"],
		depends: [],
		sync: !1
	},
	{
		name: "ncPCModule",
		features: ["ncPC", "scPC"],
		depends: ["uab", "umpc"],
		sync: !0
	},
	{
		name: "ncH5Module",
		features: ["ncH5", "scH5"],
		depends: ["uab", "umh5"],
		sync: !0
	}];
	function c(a, s) {
		var c = "AWSC_SPECIFY_" + a.toUpperCase() + "_ADDRESSES";
		if (t[c]) return t[c];
		var v = [];
		for (var h in g_moduleConfig) if (g_moduleConfig.hasOwnProperty(h) && h === a) {
			var moduleConfig = g_moduleConfig[h];
			v = Math.ceil(Math.random() * r) <= moduleConfig.ratio ? moduleConfig.grey.slice() : moduleConfig.stable.slice();
			for (var p = (new Date).getDate(), b = 0; b < v.length; b++) {
				var w = s ? "//" + s + "/": T;
				"//assets.alicdn.com/" === w && (w += "g/"),
				v[b] = w + v[b] + "?d=" + p
			}
			return v
		}
	}
	var v = [],
	h = "loading",
	p = "loaded",
	b = "timeout",
	w = "unexpected",
	k = "no such feature",
	A = new RegExp("^([\\w\\d+.-]+:)?(?://(?:([^/?#@]*)@)?([\\w\\d\\-\\u0100-\\uffff.+%]*|\\[[^\\]]+\\])(?::([0-9]+))?)?([^?#]+)?(\\?[^#]*)?(#.*)?$"),
	T = y(j());
	function y(t) {
		var a = "//g.alicdn.com/";
		if (!t) return a;
		if (/aliexpress/.test(location.href)) return "//aeis.alicdn.com/";
		var r = A.exec(t);
		return r ? "//" + r[3] + (r[4] ? ":" + r[4] : "") + "/": a
	}
	function j() {
		for (var t = document.getElementsByTagName("script"), i = 0; i < t.length; i++) {
			var a = t[i],
			r = a.hasAttribute ? a.src: a.getAttribute("src", 4);
			if (/\/awsc\.js/.test(r)) return r
		}
	}
	function S(t) {
		for (var a = void 0,
		r = 0; r < s.length; r++) {
			for (var c = s[r], v = 0; v < c.features.length; v++) if (c.features[v] === t) {
				a = c;
				break
			}
			if (a) break
		}
		return a
	}
	function M(t) {
		for (var a = 0; a < v.length; a++) {
			var r = v[a];
			if (r.name === t) return r
		}
	}
	function W(t) {
		for (var a = void 0,
		r = 0; r < s.length; r++) {
			var c = s[r];
			if (c.name === t) {
				a = c;
				break
			}
			if (a) break
		}
		return a
	}
	function D(t) {
		return /http/.test(location.protocol) || (t = "https:" + t),
		t
	}
	function U(t, r, s) {
		if (s) for (var c = 0; c < t.length; c++) {
			var v = t[c];
			v = D(v),
			a.write("<script src=" + v + "></script>")
		} else for (var c = 0; c < t.length; c++) {
			var v = t[c];
			v = D(v);
			var h = a.createElement("script");
			h.async = !1,
			h.src = v,
			h.id = r;
			var m = a.getElementsByTagName("script")[0];
			m && m.parentNode ? m.parentNode.insertBefore(h, m) : (m = a.body || a.head, m && m.appendChild(h))
		}
	}
	function I(a, r, s) {
		var c = "//acjs.aliyun.com/error?v=" + a + "&e=" + encodeURIComponent(r) + "&stack=" + encodeURIComponent(s);
		c = D(c);
		var v = new Image,
		h = "_awsc_img_" + Math.floor(1e6 * Math.random());
		t[h] = v,
		v.onload = v.onerror = function() {
			try {
				delete t[h]
			} catch(e) {
				t[h] = null
			}
		},
		v.src = c
	}
	function x(t, a) {
		Math.random() < 1e-4 && I("awsc_state", "feature=" + t.name + "&state=" + t.state + "&href=" + encodeURIComponent(location.href));
		for (var r = void 0; r = t.callbacks.shift();) try {
			r(t.state, t.exportObj)
		} catch(e) {
			if (a) throw e;
			I(t.name, e.message, e.stack)
		}
	}
	function E(t, a, r, s) {
		var p = S(t);
		if (!p) return a && a(k),
		void 0;
		var w = r && r.cdn,
		A = r && r.sync,
		T = r && r.timeout || 5e3;
		if (0 !== p.depends.length) for (var y = 0; y < p.depends.length; y++) {
			var j = p.depends[y];
			r && (delete r.sync, delete r.timeout, delete r.cdn),
			P(j, void 0, r)
		}
		var M = s || {};
		M.module = p,
		M.name = t,
		M.state = h,
		M.callbacks = M.callbacks || [],
		M.options = r,
		a && M.callbacks.push(a),
		M.timeoutTimer = setTimeout(function() {
			M.state = b,
			x(M, r && r.throwExceptionInCallback)
		},
		T),
		s || v.push(M);
		var W = p.sync;
		A && (W = A);
		var D = c(p.name, w);
		U(D, "AWSC_" + p.name, W)
	}
	function P(t, a, r) {
		var s = M(t);
		s ? s.state === b ? E(t, a, r, s) : s.state === p ? a && a(s.state, s.exportObj) : s.state === h ? a && s.callbacks.push(a) : void 0 : E(t, a, r)
	}
	function O(t, a, r) {
		var s = !1;
		try {
			var c = W(t);
			c || void 0,
			c.moduleLoadStatus = p;
			for (var w = void 0,
			k = 0; k < v.length; k++) {
				var A = v[k];
				A.module === c && A.name === a && (s = A.options && A.options.throwExceptionInCallback, w = A, clearTimeout(w.timeoutTimer), delete w.timeoutTimer, w.exportObj = r, A.state === h || A.state === b ? (w.state = p, x(w, s)) : void 0)
			}
			w || (w = {},
			w.module = c, w.name = a, w.state = p, w.exportObj = r, w.callbacks = [], v.push(w))
		} catch(e) {
			if (s) throw e;
			I("awsc_error", e.message, e.stack)
		}
	}
	function R(t) {
		t.AWSCFY = function() {},
		t.AWSC.configFY = function(a, r, s, c) {
			var v = new t.AWSCFY,
			h = !1;
			v.umidToken = "defaultToken1_um_not_loaded@@" + location.href + "@@" + (new Date).getTime(),
			t.AWSC.use("um",
			function(t, a) {
				"loaded" === t ? (v.umidToken = "defaultToken3_init_callback_not_called@@" + location.href + "@@" + (new Date).getTime(), a.init(r,
				function(t, a) {
					"success" === t ? v.umidToken = a.tn: v.umidToken = "defaultToken4_init_failed with " + t + "@@" + location.href + "@@" + (new Date).getTime(),
					h = !0,
					w()
				})) : (v.umidToken = "defaultToken2_load_failed with " + t + "@@" + location.href + "@@" + (new Date).getTime(), h = !0, w())
			});
			var p = !1;
			v.getUA = function() {
				return "defaultUA1_uab_not_loaded@@" + location.href + "@@" + (new Date).getTime()
			},
			t.AWSC.use("uab",
			function(t, a) {
				p = !0,
				"loaded" === t ? (v.uabModule = a, v.uabConfig = s, v.getUA = function() {
					return this.uabModule.getUA(this.uabConfig)
				}) : v.getUA = function() {
					return "defaultUA2_load_failed with " + t + "@@" + location.href + "@@" + (new Date).getTime()
				},
				w()
			});
			var b = t.setTimeout(function() {
				w(!0)
			},
			c ? c: 2e3);
			function w(r) { (p && h || r) && (a(v), t.clearTimeout(b))
			}
		}
	}
	function F(t) {
		t.AWSC || (t.AWSC = {},
		t.AWSC.use = P, t.AWSCInner = {},
		t.AWSCInner.register = O, R(t))
	}
	F(t)
} (window, document);
