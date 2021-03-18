package com.wl4g.devops.uos.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vjay
 * @date 2020-08-14 17:47:00
 */
public class BucketPolicyArgs implements Serializable {

    private static final long serialVersionUID = 381411777614066880L;

    @JsonProperty("Version")
    private String version;

    @JsonProperty("Statement")
    private List<Statement> statement = new ArrayList<>();

    public static class Statement {

        @JsonProperty("Sid")
        private String sid;

        @JsonProperty("Effect")
        private String effect;

        @JsonProperty("Principal")
        private Principal principal;

        @JsonProperty("Action")
        private List<String> action = new ArrayList<>();

        @JsonProperty("Resource")
        private List<String> resource = new ArrayList<>();


        public String getSid() {
            return sid;
        }

        public void setSid(String sid) {
            this.sid = sid;
        }

        public String getEffect() {
            return effect;
        }

        public void setEffect(String effect) {
            this.effect = effect;
        }

        public Principal getPrincipal() {
            return principal;
        }

        public void setPrincipal(Principal principal) {
            this.principal = principal;
        }

        public List<String> getAction() {
            return action;
        }

        public void setAction(List<String> action) {
            this.action = action;
        }

        public List<String> getResource() {
            return resource;
        }

        public void setResource(List<String> resource) {
            this.resource = resource;
        }
    }

    public static class Principal{

        @JsonProperty("AWS")
        List<String> aws;

        public List<String> getAws() {
            return aws;
        }

        public void setAws(List<String> aws) {
            this.aws = aws;
        }
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Statement> getStatement() {
        return statement;
    }

    public void setStatement(List<Statement> statement) {
        this.statement = statement;
    }
}
