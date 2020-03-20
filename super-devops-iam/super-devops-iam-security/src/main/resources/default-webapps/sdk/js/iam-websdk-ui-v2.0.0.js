/**
 * Iam core v2.0.0 | (c) 2017 ~ 2050 wl4g Foundation, Inc.
 * Copyright 2017-2032 <wangsir@gmail.com, 983708408@qq.com>, Inc. x
 * Licensed under Apache2.0 (https://github.com/wl4g/super-devops/blob/master/LICENSE)
 */

function changeTab(showId, hideId) {
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
};

$(function() {
	$(".login-link").click(function(){
	    var that = this;
	    $(".login-link").each(function(ele, obj){
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
        $('.code-write').hide()
    });

    $(".select-area").change(function(){
        var selectVal = $(this).children('option:selected').val();
        console.log(selectVal)
        if (selectVal != "+086") {
            alert("目前此功能仅对中国大陆用户开放！敬请谅解");
            $(this).children('option')[0].selected = true;
        }
    });

});