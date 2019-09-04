$(function () {
    var isPass = GetQueryString("isPass");
    if (isPass == null || isPass.toString().length < 1 || isPass != 'true') {
        myBrowser();
    }
})

function GetQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}

function myBrowser() {
    var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
    var isChrome = userAgent.indexOf("Chrome") > -1 &&
        userAgent.indexOf("Safari") > -1; //判断Chrome浏览器
    if (!isChrome) {
        window.location.href = "./upgrade.html";
    }
}

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
    // 账号登录
    $('#passwordGo').click(function () {
        $('#login_scan_pass').show();
        $('#login_scan_panel').hide();
        $('#login_phone').hide();
        $('.active').removeClass('active');
        $(this).addClass('active')
    })
    // 手机登录
    $('#phoneGo').click(function () {
        $('#login_scan_pass').hide();
        $('#login_scan_panel').hide();
        $('#login_phone').show();
        $('.active').removeClass('active');
        $(this).addClass('active')
    })
    // 扫码登录
    $('#codeGo').click(function () {
        $('#login_scan_pass').hide();
        $('#login_scan_panel').show();
        $('#login_phone').hide();
        $('.active').removeClass('active');
        $(this).addClass('active')
    })
    //关闭 code-close
    $('.code-close').click(function () {
        $('.code-write').hide()
    })
    document.querySelector(".select-area").onchange = function (e) {
        for (var i = 0; i < this.options.length; i++) {
            if (this.options[i].value != "+086") {
                alert("目前此功能仅对中国大陆用户开放！敬请谅解");
                this.selectedIndex = 0;
                break;
            }
        }
    }

});