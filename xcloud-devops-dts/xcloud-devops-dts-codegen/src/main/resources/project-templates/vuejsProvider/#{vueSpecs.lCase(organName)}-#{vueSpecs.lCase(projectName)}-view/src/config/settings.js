import router from '../router'

var gbs = {
    db_prefix: 'devops_', // 本地存储的key
    api_status_key_field: 'code',
    api_status_value_field: 200,
    api_data_field: 'data',
    api_custom: {
        401: function (that, res, method, url, success, error, dataParams) {
            IAMCore.multiModularMutexAuthenticatingHandler(res, method, url, success, error, dataParams, function (res) {
                IAMCore.Console.info("Devops redirection...");
                // window.location.href = res.data.redirect_url;
                // TODO that is null??
                if (that) {
                    that.$alert('请登录<br/>RequestId: ' + res.requestId, '提示', {
                        confirmButtonText: '确定',
                        dangerouslyUseHTMLString: true,
                        type: 'warning',
                        callback: action => {
                            router.push("/login");
                            window.location.reload();
                        }
                    });
                } else {
                    alert('[提示]请登录\nRequestId: ' + res.requestId)
                    router.push("/login");
                    window.location.reload();
                }
            });
        },
        403: function (that, res, url, success, error, data) {
            if (res && res.message) {
                console.error(res.message);
                // TODO that is null??
                if (that) {
                    alert(res.message);
                } else {
                    that.$alert(res.message);
                }
            }
        },
    }
};

var cbs = {
    /**
     * ajax请求成功，返回的状态码不是200时调用
     * @param  {object} err 返回的对象，包含错误码和错误信息
     */
    statusError(err) {
        console.log('err')
        if (err.status !== 404) {
            this.$message({
                showClose: true,
                message: '返回错误：' + err.msg,
                type: 'error'
            })
        } else {
            this.$store.dispatch('remove_userinfo').then(() => {
                this.$alert(err.status + ',' + err.msg + '！', 'Login Timeout', {
                    confirmButtonText: 'OK',
                    callback: action => {
                        //this.$router.push('/login')
                    }
                })
            })
        }
    },
    /**
     * ajax请求网络出错时调用
     */
    requestError(err) {
        this.$message({
            showClose: true,
            message: '请求错误：' + (err.response ? err.response.status : '') + ',' + (err.response ? err.response.statusText : ''),
            type: 'error'
        })
    }
}

export {
    gbs,
    cbs
}
