package com.wl4g.devops.umc.store.opentsdb.client.exception.http;

import com.wl4g.devops.umc.store.opentsdb.client.bean.response.ErrorResponse;

/**
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2019/2/22 下午7:40
 * @Version: 1.0
 */
public class HttpException extends RuntimeException {

    private ErrorResponse errorResponse;

    public HttpException(ErrorResponse errorResponse) {
        super(errorResponse.toString());
        this.errorResponse = errorResponse;
    }

}
