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
    });
    // 手机登录
    $('#phoneGo').click(function () {
        $('#login_scan_pass').hide();
        $('#login_scan_panel').hide();
        $('#login_phone').show();
        $('.active').removeClass('active');
        $(this).addClass('active')
    });
    // 扫码登录
    $('#codeGo').click(function () {
        $('#login_scan_pass').hide();
        $('#login_scan_panel').show();
        $('#login_phone').hide();
        $('.active').removeClass('active');
        $(this).addClass('active')
    });
    //关闭 code-close
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