package com.wl4g.devops.umc.store.opentsdb.client.bean.response;

import com.wl4g.devops.umc.store.opentsdb.client.bean.request.Point;

import java.util.List;

/**
 * @Author: jinyao
 * @Description:
 * @CreateDate: 2019/2/24 下午8:07
 * @Version: 1.0
 */

public class DetailResult {

    private List<ErrorPoint> errors;

    private int failed;

    private int success;


    public static class ErrorPoint{

        private Point datapoint;

        private String error;


        public Point getDatapoint() {
            return datapoint;
        }

        public void setDatapoint(Point datapoint) {
            this.datapoint = datapoint;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

    public List<ErrorPoint> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorPoint> errors) {
        this.errors = errors;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }
}
