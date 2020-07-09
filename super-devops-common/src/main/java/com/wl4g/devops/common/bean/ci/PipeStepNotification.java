package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.tool.common.collection.Collections2;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public class PipeStepNotification extends BaseBean {
    private static final long serialVersionUID = 6815608076300843748L;

    private Integer pipeId;

    private Integer enable;

    private Integer[] contactGroupId;

    private String contactGroupIds;

    public Integer getPipeId() {
        return pipeId;
    }

    public void setPipeId(Integer pipeId) {
        this.pipeId = pipeId;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Integer[] getContactGroupId() {
        return contactGroupId;
    }

    public void setContactGroupId(Integer[] contactGroupId) {
        this.contactGroupId = contactGroupId;
    }

    public void setContactGroupId2(String[] contactGroupId) {
        if(!Collections2.isEmptyArray(contactGroupId)){
            List<Integer> list = new ArrayList<>();
            for(int i = 0; i<contactGroupId.length;i++){
                if(NumberUtils.isCreatable(contactGroupId[i])){
                    list.add(Integer.parseInt(contactGroupId[i]));
                }
            }
            Integer[] result = new Integer[list.size()];
            list.toArray(result);
            this.contactGroupId = result;
        }
    }

    public String getContactGroupIds() {
        return contactGroupIds;
    }

    public void setContactGroupIds(String contactGroupIds) {
        this.contactGroupIds = contactGroupIds;
    }
}