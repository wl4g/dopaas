package com.wl4g.dopaas.common.bean.uci;

import com.wl4g.infra.core.bean.BaseBean;

public class PipeStepApi extends BaseBean {
    private static final long serialVersionUID = 6815608076300843748L;

    private Long id;

    private Long pipeId;

    private Integer enable;

    private Long repositoryId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPipeId() {
        return pipeId;
    }

    public void setPipeId(Long pipeId) {
        this.pipeId = pipeId;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Long getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
    }

}