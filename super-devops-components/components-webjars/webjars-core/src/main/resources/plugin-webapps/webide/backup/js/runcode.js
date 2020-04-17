function guid() {
	function s4() {
		return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
	}
	return s4() + s4() + s4() + s4() + s4() + s4() + s4() + s4();
}
function ie_error() {
	var ret = false;
	if (navigator.appName == "Microsoft Internet Explorer" && navigator.appVersion.split(";")[1].replace(/[ ]/g, "") == "MSIE6.0") {
		ret = true;
	} else if (navigator.appName == "Microsoft Internet Explorer" && navigator.appVersion.split(";")[1].replace(/[ ]/g, "") == "MSIE7.0") {
		ret = true;
	} else if (navigator.appName == "Microsoft Internet Explorer" && navigator.appVersion.split(";")[1].replace(/[ ]/g, "") == "MSIE8.0") {
		ret = true;
	} else if (navigator.appName == "Microsoft Internet Explorer" && navigator.appVersion.split(";")[1].replace(/[ ]/g, "") == "MSIE9.0") {
		ret = true;
	}
	return ret;
}
var gid = $.jStorage.get(codetype + "-cookie", null);
if (!gid) {
	gid = guid();
	$.jStorage.set(codetype + "-cookie", gid);
}
if (gid.indexOf("*") == -1) {

	gid = gid + "*" + unid;
	$.jStorage.set(codetype + "-cookie", gid);
}