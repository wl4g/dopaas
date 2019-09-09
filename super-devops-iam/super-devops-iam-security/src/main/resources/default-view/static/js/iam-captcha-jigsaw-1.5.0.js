/**
 * Iam captcha jigsaw v1.2.1 | (c) 2017, 2022 wl4g Foundation, Inc.
 * Copyright 2017-2032 <wangsir@gmail.com>, Inc. x
 * Licensed under Apache2.0 (https://github.com/wl4g/super-devops/blob/master/LICENSE)
 */
(function ($) {
    'use strict';
    var runtime = {
		applyToken: null,
		y: 0,
		secret: null,
	};
    var _JigsawCaptcha = function (element, options) {
        this.$element = $(element);
        this.options = $.extend({}, _JigsawCaptcha.DEFAULTS, options);
        this.$element.css({'width': this.options.width + 'px', 'margin': '0 auto' });
        this.init();
    };
    _JigsawCaptcha.VERSION = 'v1.2.1';
    _JigsawCaptcha.Author = '<wanglsir@gmail.com>';
    _JigsawCaptcha.BaseURI = 'http://localhost:8080';
    _JigsawCaptcha.DEFAULTS = {
        width: 280, // canvas宽度
        height: 155, // canvas高度
        loadingText: IAM.Util.isZhCN()?'加载中...':'Loading...',
        failedText: IAM.Util.isZhCN()?'再试一次':"Let\'s try again?",
        barText: IAM.Util.isZhCN()?'请拖动滑块完成拼图':'Drag to complete the jigsaw',
        repeatIcon: 'fa fa-repeat',
        applycaptchaUrl: null,
        verify: function (arr, left) {
			// Additional algorithmic salt.
			left = new String(left);
			var applyTokenCrc = IAM.Util.Crc16CheckSum.crc16Modbus(runtime.applyToken);
			var tmpX = IAM.Crypto.sha512WithHex(left + runtime.applyToken).substring(31, 97) + (left*applyTokenCrc);
            // Do encryption x-position.
			var cipherX = IAM.Crypto.rivestShamirAdleman(runtime.secret, tmpX);
            var ret = null;
            var url = 'http://localhost:14040/iam-server/verify/verifyAnalyze?verifyType=VerifyWithJigsawGraph';
            var verifyInfo = {
                applyToken: runtime.applyToken,
                x: cipherX,
                trails: arr,
            };
            $.ajax({
                url: url,
                data: JSON.stringify(verifyInfo),
                async: false,
                cache: false,
                type: 'post',
                contentType: 'application/json',
                dataType: 'json',
                success: function (data) {
                    data = data.data.verifiedModel;
                    ret = data;
                }
            });
            return ret;
        },
    };

    $.fn.JigsawIamCaptcha = function(option) {
        return this.each(function () {
            var $this = $(this);
            var data = $this.data('lgb.JigsawIamCaptcha');
            var options = typeof option === 'object' && option;
            if (data && !/reset/.test(option)) return;
            if (!data) $this.data('lgb.JigsawIamCaptcha', data = new _JigsawCaptcha(this, options));
            if (typeof option === 'string') data[option]();
        });
    };

    var _proto = _JigsawCaptcha.prototype;
    _proto.init = function () {
        this.initDOM();
        this.initImg();
        this.bindEvents();
    };

    _proto.initDOM = function () {
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
		card.style.backgroundColor="rgb(254,249,249)"

        var cardHeader = createElement('div', 'card-header');
		cardHeader.style.paddingLeft="20px";
		cardHeader.style.paddingTop="5px";
        var cardHeaderText = createElementValue('span', IAM.Util.isZhCN()?'请完成人机验证':'Please complete man-machine verification');
        var cardBody = createElement('div', 'card-body2');

        var canvas = createCanvas(this.options.width - 2, this.options.height); // 画布
        var block = createCanvas(this.options.width - 2, this.options.height); // 滑块
        var sliderContainer = createElement('div', 'sliderContainer');
        var refreshIcon = createElement('i', 'refreshIcon ' + this.options.repeatIcon);
        var sliderMask = createElement('div', 'sliderMask');
        var sliderbg = createElement('div', 'sliderbg');
        var slider = createElement('div', 'slider');
        var sliderIcon = createElement('i', 'fa fa-arrow-right sliderIcon');
        var text = createElement('span', 'sliderText');
        block.className = 'block';
        text.innerHTML = this.options.barText;

        var el = this.$element;
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

    _proto.initImg = function () {
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
            console.info(img2.imagey);
            that.text.text(that.text.attr('data-text'));
        };

        img1.setSrc = function (imgBase64) {
            that.text.removeClass('text-danger');
            img1.src='data:image/png;base64,'+imgBase64;
        };
        img2.setSrc = function (imgBase64) {
            that.text.removeClass('text-danger');
            img2.src='data:image/png;base64,'+imgBase64;
        };
		// Apply captcha.
        var applycaptcha = function(){
            var url = "http://localhost:14040/iam-server/verify/applycaptcha?verifyType=VerifyWithJigsawGraph";
            if(that.options.applycaptchaUrl){
                url = that.options.applycaptchaUrl+"?verifyType=VerifyWithJigsawGraph";
            }
            $.ajax({
                url: url,
				type: 'GET',
                success: function (result) {
                    //console.info(result);
                    if(result.code==200){//success
                        result = result.data.applyModel;
                        img1.setSrc(result.primaryImg);
                        img2.setSrc(result.blockImg);
                        img2.imagey = result.y;
                        applyToken = result.applyToken;
                        secret = result.secret;
                    }else{//fail
                        if ($.isFunction(that.options.onFail(result))) that.options.onFail.call(that.$element);
                    }

                }
            });
        };
        applycaptcha();
        this.text.attr('data-text', this.options.barText);
        this.text.text(this.options.loadingText);
        this.img1 = img1;
        this.img2 = img2;
        this.applycaptcha = applycaptcha;
    };

    _proto.clean = function () {
        this.canvasCtx.clearRect(0, 0, this.options.width, this.options.height);
        this.blockCtx.clearRect(0, 0, this.options.width, this.options.height);
        this.block.width = this.options.width;
    };

    _proto.bindEvents = function () {
        var that = this;
        this.$element.on('selectstart', function () {
            return false;
        });

        $(this.refreshIcon).on('click', function () {
            that.text.text(that.options.barText);
            that.reset();
            if ($.isFunction(that.options.onRefresh)) that.options.onRefresh.call(that.$element);
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
                $(that.card).fadeOut(200);
            }
        };

        var handleDragMove = function (e) {
            e.preventDefault();
            if (!isMouseDown) return false;
            var eventX = e.clientX;
            var eventY = e.clientY;
            var moveX = eventX - originX;
            var moveY = eventY - originY;
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
            var data = that.verify();
            //TODO 认证是否要抽离出去html
            if (data&&data.verified) {
                that.sliderContainer.addClass('sliderContainer_success');
                if ($.isFunction(that.options.onSuccess(data.verifiedToken))) that.options.onSuccess.call(that.$element);
            } else {
                that.sliderContainer.addClass('sliderContainer_fail');
                if ($.isFunction(that.options.onFail)) that.options.onFail.call(that.$element);
                setTimeout(function () {
                    that.text.text(that.options.failedText);
                    that.reset();
                }, 1000);
            }
        };
        this.slider.addEventListener('mousedown', handleDragStart);
        this.slider.addEventListener('touchstart', handleDragStart);
        this.slider.addEventListener('mouseenter',handleOnmouseenter);
        this.$element.on('mouseleave',handleOnmouseleave);
        document.addEventListener('mousemove', handleDragMove);
        document.addEventListener('touchmove', handleDragMove);
        document.addEventListener('mouseup', handleDragEnd);
        document.addEventListener('touchend', handleDragEnd);
        document.addEventListener('mousedown', function () { return false; });
        document.addEventListener('touchstart', function () { return false; });
        document.addEventListener('swipe', function () { return false; });
    };
	// Verify submit captcha.
    _proto.verify = function () {
        var left = parseInt(this.block.style.left);
        var verified = this.options.verify(this.trails, left); // 拖动时x/y轴的移动距离,最总x位置
        return verified;
    };
	// Reset apply captcha.
    _proto.reset = function () {
        this.sliderContainer.removeClass('sliderContainer_fail sliderContainer_success');
        this.slider.style.left = 0;
        this.block.style.left = 0;
        this.sliderMask.style.width = 0;
        this.clean();
        this.text.attr('data-text', this.text.text());
        this.text.text(this.options.loadingText);
        this.applycaptcha();
    };
})(jQuery);