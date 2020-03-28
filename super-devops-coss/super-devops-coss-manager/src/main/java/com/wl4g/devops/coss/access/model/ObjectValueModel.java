package com.wl4g.devops.coss.access.model;

import com.wl4g.devops.coss.model.ObjectValue;
import org.springframework.beans.BeanUtils;

/**
 * @author vjay
 * @date 2020-03-27 17:00:00
 */
public class ObjectValueModel extends ObjectValue {

    private String downloadUrl;

    public ObjectValueModel(){

    }

    public ObjectValueModel(ObjectValue objectValue){
        BeanUtils.copyProperties(objectValue,this);
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
