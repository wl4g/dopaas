(function ($) {
    'use strict';

    var uuid;
    var y = 0;

    var SliderCaptcha = function (element, options) {
        this.$element = $(element);
        this.options = $.extend({}, SliderCaptcha.DEFAULTS, options);
        this.$element.css({'width': this.options.width + 'px', 'margin': '0 auto' });
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
        applycaptchaUrl: null,

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

        var card = createElement('div', 'slidercaptcha card');
        card.style.display="none";

        /*card.style.position="absolute";
        card.style.bottom="123px";
        card.css({
            display: "none",
            position: "absolute",
            bottom: "123px",
        });*/





        var cardHeader = createElement('div', 'card-header');
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

        img1.setSrc = function (imgBase64) {
            that.text.removeClass('text-danger');
            //img1.src = 'http://localhost:14040/iam-server/public/image1?uuid='+uuid;
            //img1.src = url;
            //img1.src='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAFYklEQVR42sVXfUyUdRz/8XKhMZQO7nmeuwMFwrBEVJqgDOcUHZvNuTarrdKam0zdKrckAy4goowxbaM5hE1yjVVzLjLRokBFz2qsQPGFF8vmamtkL4s4OHDy6ff5EQcXx91hf/jHB777fj8v3+d57p7nOQFA3EuoPxcLhV90SlxziOSeUrHdWRz5fv1uc1fl03OGizdGgmBd/5K5mzNyyO0M4EkEXqBAiI58Ed9WEFp1+MXYm1vW68hKM7AwJR5Wmx3RZqsCa/Y4I4dcaqilx10t0PGqCOssEjuvH7H9vW1zEgzNgK5ZYVh02HUN86w65tvGwJo9zhRHcqmhlh70mtEC7a8Ie2ep6dPfW5eM3OnbhMo9S6GbLV6h04Eccqmhlh70omdQC3TsFbbLZaYL/W2ZwA+PYbQ9B780rUJu1nxoMZaAC5BDLjXU0oNe9KS33wXkqRKXXguv+as1HejOxYhzNdwS+G4NjlUsU0cX7+cscEYOudRQSw960ZPezPC9QIE69dv6Pk4exLX1GDm/Cu5z2Qojzmy4WrOxZUOSPELNz9FrikMuNR699KInvZkx/sH0WqB9rzCuH5j7Mzrk1uel8HQW3GcmgK+y4axOR0qCVX7gpoazxxk55E7W0oue9GYGs7wXKFCnP7+/cdEovs7GUHMm3C0rvHD7jMTZFSjYugAW89RvAXuckUPuf/X0pDczmMVMzwJyI8v3lVFX7pzLxMiXGXB/sdwncDoDvfXLkJFqh1WbuBSs2eOMnOn09GYGs5g5eYHcXw/HAWczMfRZOtwSQz7APloexcHdD0GLnVhAlzV7nPnT8j8zmMVMzwKdjtCy/qPJuN0kSY1L/OL250tw61gaNqy0Q7doCqzZ4yygXmYwi5meBa6Wmk4NNaRgpDEVQ8cX+YVbAk2L0VCcrL52BGv23AG0hMqQWcz0LNBTHtGFkw9D4UQQkLxR+X/LujiF0RMz0xIqc3yBb4sifmp+80G0lCcFhWYJZ0USDu6ar8C6OUit0sssZnoWaNhh+tFqGIg3dM9p9QvJM5t1vP1cvALrmWiZxUzPAidfMLWlJBjqZhKMiRarY+UiAzer5ymwZi8Y7dgNywAzPQuc3xNet3Ypv8+Bn3Y0sUqTQ3k24MM4BdZW3f9zYuKeoYNZzPQscNkRsv351WbExhoBDXT5zN+4XMcfNVYMv2dTYM0eZ4H0zGAWMz0L9JaK1P1PRQ7EWa0Bn3aJdh2NL2vAEQ2u2jGwZo+zQHq7zJBZrusyc9IZEOEX8sM/yFxokafI8LO9jrx1GgaqY+CujYHr0BhYs5eXoynO9KffADOc+eEfyUyT19Owp0SsKd0U5eYrlU+xvMaLk3S0OcxAbTRcB73BHmdpSWNcn5dPejNDZq2d8jiWG4W07Q39JCctVr3T+br2ZY+bcac6CoPvzoGryhvscUaOr88CPenNDGb5fCPqKha2o3kRF1MTKfA++uxHNNx4IwrDVZFwveMbnJFD7uSzQC960psZft8Je4rFkzXPzh5eME+HTTfUs94mbxx1W+cCVbMwuH82XNOAM3LIpUZppQe96EnvoN6K5TXaVfPMbCxO1BAVbeCJzBj89tYsDO+/DwOVEX5BDrmbpYZaetCLnkG/ll8qEqK7WOQe32lybs54AKd23I/RA+EYqDAFBXJPSg219KAXPWf8y+iKQxhXHaLyRklI363yUAzsC8OghGtfqE9wRg651FBLj7v/aTb+ul4oUi4VipIuh+i+WSKG+spC8Gd5CPr/BWv2OJOcHsl9XWoW/v/fht5LhLUXiARZb71cJOquFIlv5BH2EqzZ40xyEsm9OJMfp/cS/wC6xRqbyM2GFQAAAABJRU5ErkJggg==';
            img1.src='data:image/png;base64,'+imgBase64;
        };
        img2.setSrc = function (imgBase64) {
            that.text.removeClass('text-danger');
            //img2.src = 'http://localhost:14040/iam-server/public/image2?uuid='+uuid;
            //img2.src = url;
            img2.src='data:image/png;base64,'+imgBase64;
        };

        //img1.setSrc();
        //img2.setSrc();

        var applycaptcha = function(){
            var url = "http://localhost:14040/iam-server/verify/applycaptcha?verifyType=VerifyWithJigsawGraph";
            if(that.options.applycaptchaUrl){
                url = that.options.applycaptchaUrl+"?verifyType=VerifyWithJigsawGraph";
            }
            $.ajax({
                url: url,
                // data: JSON.stringify(arr),
                // async: false,
                cache: false,
                type: 'POST',
                contentType: 'application/json',
                dataType: 'json',
                success: function (result) {
                    console.info(result);
                    result = result.data.jigsaw;
                    img1.setSrc(result.primaryImg);
                    img2.setSrc(result.blockImg);
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
            originX = e.clientX;
            originY = e.clientY;
            isMouseDown = true;
        };

        var handleOnmouseenter = function () {
            //console.info("into in");
            //that.card.style.display="";
            $(that.card).delay(2000).show();

        };

        var handleOnmouseleave = function () {
            //console.info("into out");
            if(!that.sliderContainer.hasClass('sliderContainer_active')){
                //console.info("shout out");

                //that.card.style.display="none";
                $(that.card).delay(2000).hide();



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

        //this.$element.on('mouseenter',handleOnmouseenter);
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