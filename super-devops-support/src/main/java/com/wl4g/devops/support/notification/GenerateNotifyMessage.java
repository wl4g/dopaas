package com.wl4g.devops.support.notification;

import static com.wl4g.devops.common.web.RespBase.*;

/**
 * @author vjay
 * @date 2020-03-13 17:31:00
 */
public class GenerateNotifyMessage implements NotifyMessage{

    /**
     * Template Key
     */
    private String templateKey;

    /**
     * Parameters
     */
    private DataMap parameters;

    /**
     * Targets, send message to which ones;
     */
    private String[] targets;

    /**
     * create timestamp
     */
    private long timestamp;

    public static GenerateNotifyMessage build(){//TODO params
        GenerateNotifyMessage generateNotifyMessage = new GenerateNotifyMessage();
        generateNotifyMessage.setTimestamp(System.currentTimeMillis());
        generateNotifyMessage.setParameters(new DataMap());
        return generateNotifyMessage;
    }

    public String getTemplateKey() {
        return templateKey;
    }

    public GenerateNotifyMessage setTemplateKey(String templateKey) {
        this.templateKey = templateKey;
        return this;
    }

    public DataMap getParameters() {
        return parameters;
    }

    public GenerateNotifyMessage setParameters(DataMap parameters) {
        this.parameters = parameters;
        return this;
    }

    public String[] getTargets() {
        return targets;
    }

    public GenerateNotifyMessage setTargets(String[] targets) {
        this.targets = targets;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
