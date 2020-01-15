package com.wl4g.devops.common.bean.doc;

import com.wl4g.devops.common.bean.BaseBean;

public class FileLabel extends BaseBean {

    private Integer labelId;

    private Integer fileId;

    public Integer getLabelId() {
        return labelId;
    }

    public void setLabelId(Integer labelId) {
        this.labelId = labelId;
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }
}