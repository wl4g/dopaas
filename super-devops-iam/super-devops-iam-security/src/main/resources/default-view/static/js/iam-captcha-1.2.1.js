(function ($) {
    'use strict';

    var applyToken;
    var y = 0;
    var secret;

    var JigsawIamCaptcha = function (element, options) {
        this.$element = $(element);
        this.options = $.extend({}, JigsawIamCaptcha.DEFAULTS, options);
        this.$element.css({'width': this.options.width + 'px', 'margin': '0 auto' });
        this.init();
    };

    JigsawIamCaptcha.VERSION = '1.0';
    JigsawIamCaptcha.Author = 'argo@163.com';

    JigsawIamCaptcha.DEFAULTS = {
        width: 280,     // canvas宽度
        height: 155,    // canvas高度
        loadingText: '加载中...',
        failedText: '再试一次',
        barText: '按住滑块拖动完成拼图',
        repeatIcon: 'fa fa-repeat',
        applycaptchaUrl: null,

        verify: function (arr, left) {
            //left = window.IAM.util().signature(secret,left);
            var ret = null;
            var url = 'http://localhost:14040/iam-server/verify/verifyAnalyze?verifyType=VerifyWithJigsawGraph';
            var verifyInfo = {
                applyToken: applyToken,
                x: left,
                trail: arr,
            }
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

    function Plugin(option) {
        return this.each(function () {
            var $this = $(this);
            var data = $this.data('lgb.JigsawIamCaptcha');
            var options = typeof option === 'object' && option;
            if (data && !/reset/.test(option)) return;
            if (!data) $this.data('lgb.JigsawIamCaptcha', data = new JigsawIamCaptcha(this, options));
            if (typeof option === 'string') data[option]();
        });
    }

    $.fn.JigsawIamCaptcha = Plugin;
    $.fn.JigsawIamCaptcha.Constructor = JigsawIamCaptcha;

    var _proto = JigsawIamCaptcha.prototype;
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
		cardHeader.style.paddingLeft="90px";
        var cardHeaderText = createElementValue('span', '请完成安全验证');
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
        }
        else {
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

        var applycaptcha = function(){
            var url = "http://localhost:14040/iam-server/verify/applycaptcha?verifyType=VerifyWithJigsawGraph";
            if(that.options.applycaptchaUrl){
                url = that.options.applycaptchaUrl+"?verifyType=VerifyWithJigsawGraph";
            }
            $.ajax({
                url: url,
				type: 'GET',
                success: function (result) {
                    console.info(result);
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

        var originX, originY, trail = [], isMouseDown = false;

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
            trail.push(moveY);
        };

        var handleDragEnd = function (e) {
            if (!isMouseDown) return false;
            isMouseDown = false;
            var eventX = e.clientX;
            if (eventX === originX) return false;
            that.sliderContainer.removeClass('sliderContainer_active');
            that.trail = trail;
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

    _proto.verify = function () {
        var arr = this.trail; // 拖动时y轴的移动距离
        var left = parseInt(this.block.style.left);
        var verified = this.options.verify(arr, left);
        return verified;
    };

    _proto.reset = function () {
            this.sliderContainer.removeClass('sliderContainer_fail sliderContainer_success');
            this.slider.style.left = 0;
            this.block.style.left = 0;
            this.sliderMask.style.width = 0;
            this.clean();
            this.text.attr('data-text', this.text.text());
            this.text.text(this.options.loadingText);
            //TODO
            this.applycaptcha();
    };
})(jQuery);