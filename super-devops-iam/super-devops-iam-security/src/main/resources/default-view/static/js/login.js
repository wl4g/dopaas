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
        window.location.href = "./inform.html";
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

$(document).ready(function () {

    //是否需要验证
    var needCode = false;
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
        //检查需不需要图文验证码
        checkNeed();
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
    //手机号验证
    function isPoneAvailable(pone) {
        var myreg = /^[1][3,4,5,7,8][0-9]{9}$/;
        if (!myreg.test(pone)) {
            return false;
        } else {
            return true;
        }
    }

    //检查需不要图文验证码
    function checkNeed() {
        var url = iamBaseURI+'/ext/check?principal=';
        $.ajax({
            url: url,
            // xhrFields: {
            // 	withCredentials: true // 跨域调用时，必须设置才会发送cookie
            // },
            success: function (res) {
                if (res.data.captchaEnabled == 'yes') {
                    needCode = true;
                } else {
                    needCode = false;
                }
            }
        });
    }



    //获取验证码
    $('#code-get').click(function () {
        var text = $('#user_phone').val();
        if (needCode) {
            var num = Math.random() * 100;
            fetch(iamBaseURI+'/ext/captcha-apply?r=' + num)
                .then((res) => {
                    var contentType = res.headers.get("Content-Type");
                    if (contentType.indexOf("image") >= 0) {
                        return res.blob();
                    } else if (contentType.indexOf("application/json") >= 0) {
                        return res.json();
                    }
                }).then(body => {
                    if (body instanceof Blob) {
                        var imgObjURL = URL.createObjectURL(body);
                        $(".code-image").attr("src", imgObjURL);
                        $('#user_code').attr('disabled', false);
                        $('#user_code').css('cursor', 'drop');
                    }
                    // data是非Blob类直接可断定已被锁定
                    else {
                        $(".code-image").attr("src","static/images/bg_icon/broken.png")
                        ///禁止输入验证码
                        $('#user_code').attr('disabled', true);
                        $('#user_code').css('cursor', 'no-drop');
                    }
                });
            if (!text) {
                $('.phone-err').show();
                $('.phone-err').text('请输入手机号')
            } else {
                if (isPoneAvailable(text)) {
                    $('.phone-err').hide();
                    $('.code-write').show()
                } else {
                    $('.phone-err').show();
                    $('.phone-err').text('请输入正确的手机号')
                }
            }
        } else {
            if (!text) {
                $('.phone-err').show();
                $('.phone-err').text('请输入手机号')
            } else {
                if (isPoneAvailable(text)) {
                    $('.phone-err').hide();
                    putCode();
                } else {
                    $('.phone-err').show();
                    $('.phone-err').text('请输入正确的手机号')
                }
            }
        }

    })


    //切换验证码
    $('.code-image').click(function () {
        var num = Math.random() * 100;
        $(this).attr('src', iamBaseURI+'/ext/captcha-apply?r=' + num);
    })

    //提交验证码
    $('#code_submit').click(function () {
        var code = $('#user_code').val();
        if (!code) {
            return
        } else {
            putCode(code);
        }
    })

    //提交验证码获取短信动态码
    function putCode() {
        var phone = $('.select-area').val() + $('#user_phone').val();
        var code = $('#user_code').val();
        var parms = {};
        if (needCode) {
            parms = {
                captcha: code,
                principal: phone,
                action: 'login'
            }
        } else {
            parms = {
                principal: phone,
                action: 'login'
            }
        }

        $.ajax({
            url: iamBaseURI+'/ext/verifycode-apply',
            type: 'post',
            data: parms,
            success: function (res) {
                if (res.code == 200) {
                    $('.err-tip').text('');
                    $('.code-write').hide();
                    var verifyCodeCreateTime = res.data.verifyCodeCreateTime;
                    var verifyCodeDelayMs = res.data.verifyCodeDelayMs;
                    var now = Date.now();
                    var num = parseInt((verifyCodeDelayMs - (now - verifyCodeCreateTime)) / 1000)
                    // var num = parseInt(res.data.verifyCodeRemainDelayMs/1000);
                    var timer = setInterval(() => {
                        if (num < 1) {
                            $("#code-get").attr('disabled', false);
                            $('#code-get').text('重新获取');
                            clearInterval(timer);
                        } else {
                            $("#code-get").attr('disabled', true);
                            $('#code-get').text(num + 's');
                            num--;
                        }
                    }, 1000);
                } else {
                    $('.err-tip').text(res.message);
                }

            }
        });
    };
    //短信登录
    $('#phone_submit').click(function () {
        var phone = $('#user_phone').val();
        var code = $('#codeNumber').val();
        if (!code && !phone) {
            $('.pass-err').show();
            $('.pass-err').text('请输入短信验证码')
            $('.phone-err').show();
            $('.phone-err').text('请输入手机号')
        } else if (!code && phone) {
            $('.phone-err').hide();
            $('.pass-err').show();
            $('.pass-err').text('请输入短信验证码')
        } else if (code && !phone) {
            $('.phone-err').show();
            $('.pass-err').hide();
            $('.phone-err').text('请输入手机号')
        } else {
            $('.pass-err').hide();
            if (isPoneAvailable(phone)) {
                $('.phone-err').hide();
                putPhone(phone, code)
            } else {
                $('.phone-err').show();
                $('.phone-err').text('请输入正确的手机号')
            }
        }
    })
    //短信登录接口
    function putPhone(phone, code) {
        var url = iamBaseURI+'/login-submission/sms?action=login&principal=' + phone + '&passcode=' + code;
        $.ajax({
            url: url,
            success: function (res) {
                if (res.code == 200) {
                    window.location.href = res.data;
                } else {
                    $('#err_tip').show();
                    $('#err_tip').text(res.message);
                    setTimeout(function () {
                        $('#err_tip').hide();
                    }, 3000)
                }
            },
            error(err) {
                // console.log(err)
                // console.log(6666)
            }
        });
    }


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