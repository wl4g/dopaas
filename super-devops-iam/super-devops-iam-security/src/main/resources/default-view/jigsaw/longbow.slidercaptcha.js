(function ($) {
    'use strict';

    var uuid;
    var y = 0;

    var SliderCaptcha = function (element, options) {
        this.$element = $(element);
        this.options = $.extend({}, SliderCaptcha.DEFAULTS, options);
        this.$element.css({ 'position': 'relative', 'width': this.options.width + 'px', 'margin': '0 auto' });
        this.init();
    };

    SliderCaptcha.VERSION = '1.0';
    SliderCaptcha.Author = 'argo@163.com';

    SliderCaptcha.DEFAULTS = {
        width: 280,     // canvas宽度
        height: 155,    // canvas高度
        loadingText: '正在加载中...',
        failedText: '再试一次',
        barText: '向右滑动填充拼图',
        repeatIcon: 'fa fa-repeat',

        verify: function (arr, left) {
            var ret = false;
            var url = 'http://localhost:14040/iam-server/public/verify';
            var verifyInfo = {
                uuid: uuid,
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
                success: function (result) {
                    ret = result;
                }
            });
            return ret;
        },
        //remoteUrl: 'http://localhost:14040/iam-server/public/verify'
    };

    function Plugin(option) {
        return this.each(function () {
            var $this = $(this);
            var data = $this.data('lgb.SliderCaptcha');
            var options = typeof option === 'object' && option;
            if (data && !/reset/.test(option)) return;
            if (!data) $this.data('lgb.SliderCaptcha', data = new SliderCaptcha(this, options));
            if (typeof option === 'string') data[option]();
        });
    }

    $.fn.sliderCaptcha = Plugin;
    $.fn.sliderCaptcha.Constructor = SliderCaptcha;

    var _proto = SliderCaptcha.prototype;
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

        var createCanvas = function (width, height) {
            var canvas = document.createElement('canvas');
            canvas.width = width;
            canvas.height = height;
            return canvas;
        };

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
        el.append($(canvas));
        el.append($(refreshIcon));
        el.append($(block));
        slider.appendChild(sliderIcon);
        sliderMask.appendChild(slider);
        sliderContainer.appendChild(sliderbg);
        sliderContainer.appendChild(sliderMask);
        sliderContainer.appendChild(text);
        el.append($(sliderContainer));

        var _canvas = {
            canvas: canvas,
            block: block,
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

        //TODO my add
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

        img1.setSrc = function (url) {
            that.text.removeClass('text-danger');
            //img1.src = 'http://localhost:14040/iam-server/public/image1?uuid='+uuid;
            img1.src = url;
        };
        img2.setSrc = function (url) {
            that.text.removeClass('text-danger');
            //img2.src = 'http://localhost:14040/iam-server/public/image2?uuid='+uuid;
            img2.src = url;
        };

        //img1.setSrc();
        //img2.setSrc();

        var applycaptcha = function(){
            $.ajax({
                url: 'http://localhost:14040/iam-server/public/applycaptcha',
                // data: JSON.stringify(arr),
                // async: false,
                cache: false,
                type: 'POST',
                contentType: 'application/json',
                dataType: 'json',
                success: function (result) {
                    console.info(result);
                    img1.setSrc(result.image1);
                    img2.setSrc(result.image2);
                    img2.imagey = result.y;
                    uuid = result.uuid;

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

        var originX, originY, trail = [],
            isMouseDown = false;

        var handleDragStart = function (e) {
            if (that.text.hasClass('text-danger')) return;
            originX = e.clientX || e.touches[0].clientX;
            originY = e.clientY || e.touches[0].clientY;
            isMouseDown = true;
        };

        var handleDragMove = function (e) {
            e.preventDefault();
            if (!isMouseDown) return false;
            var eventX = e.clientX || e.touches[0].clientX;
            var eventY = e.clientY || e.touches[0].clientY;
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
            var eventX = e.clientX || e.changedTouches[0].clientX;
            if (eventX === originX) return false;
            that.sliderContainer.removeClass('sliderContainer_active');
            that.trail = trail;
            var data = that.verify();
            //TODO 认证是否要抽离出去html
            if (data) {
                that.sliderContainer.addClass('sliderContainer_success');
                if ($.isFunction(that.options.onSuccess)) that.options.onSuccess.call(that.$element);
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