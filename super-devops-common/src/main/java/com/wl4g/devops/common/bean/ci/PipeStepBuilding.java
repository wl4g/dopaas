package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.BaseBean;

public class PipeStepBuilding extends BaseBean {
    private static final long serialVersionUID = 6815608076300843748L;

    private Integer pipeId;

    private String preCommand;

    private String postCommand;

    public Integer getPipeId() {
        return pipeId;
    }

    public void setPipeId(Integer pipeId) {
        this.pipeId = pipeId;
    }

    public String getPreCommand() {
        return preCommand;
    }

    public void setPreCommand(String preCommand) {
        this.preCommand = preCommand == null ? null : preCommand.trim();
    }

    public String getPostCommand() {
        return postCommand;
    }

    public void setPostCommand(String postCommand) {
        this.postCommand = postCommand == null ? null : postCommand.trim();
    }
}