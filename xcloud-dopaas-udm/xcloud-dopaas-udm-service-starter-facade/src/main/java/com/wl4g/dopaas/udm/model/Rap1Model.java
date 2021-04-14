package com.wl4g.dopaas.udm.model;

import java.util.List;

/**
 * @author vjay
 * @date 2021-04-13 17:40:00
 */
public class Rap1Model {
    private String createDateStr;
    private User user;
    private long id;
    private String version;
    private String introduction;
    private String name;
    private List<Model> moduleList;

    public String getCreateDateStr() {
        return createDateStr;
    }

    public void setCreateDateStr(String createDateStr) {
        this.createDateStr = createDateStr;
    }

    public User getUser() { return user; }
    public void setUser(User value) { this.user = value; }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVersion() { return version; }
    public void setVersion(String value) { this.version = value; }

    public String getIntroduction() { return introduction; }
    public void setIntroduction(String value) { this.introduction = value; }

    public String getName() { return name; }
    public void setName(String value) { this.name = value; }

    public List<Model> getModuleList() {
        return moduleList;
    }

    public void setModuleList(List<Model> moduleList) {
        this.moduleList = moduleList;
    }

    public static class Model {
        private Long id;
        private String introduction;
        private String name;
        private List<Model> pageList;
        private List<Action> actionList;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getIntroduction() { return introduction; }
        public void setIntroduction(String value) { this.introduction = value; }

        public String getName() { return name; }
        public void setName(String value) { this.name = value; }

        public List<Model> getPageList() {
            return pageList;
        }

        public void setPageList(List<Model> pageList) {
            this.pageList = pageList;
        }

        public List<Action> getActionList() {
            return actionList;
        }

        public void setActionList(List<Action> actionList) {
            this.actionList = actionList;
        }
    }

    public static class Action {
        private Long id;
        private String name;
        private String description;
        private String requestType;
        private String requestUrl;
        private String responseTemplate;
        private List<Parameter> requestParameterList;
        private List<Parameter> responseParameterList;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() { return name; }
        public void setName(String value) { this.name = value; }

        public String getDescription() { return description; }
        public void setDescription(String value) { this.description = value; }

        public String getRequestType() { return requestType; }
        public void setRequestType(String value) { this.requestType = value; }

        public String getRequestUrl() {
            return requestUrl;
        }

        public void setRequestUrl(String requestUrl) {
            this.requestUrl = requestUrl;
        }

        public String getResponseTemplate() { return responseTemplate; }
        public void setResponseTemplate(String value) { this.responseTemplate = value; }

        public List<Parameter> getRequestParameterList() {
            return requestParameterList;
        }

        public void setRequestParameterList(List<Parameter> requestParameterList) {
            this.requestParameterList = requestParameterList;
        }

        public List<Parameter> getResponseParameterList() {
            return responseParameterList;
        }

        public void setResponseParameterList(List<Parameter> responseParameterList) {
            this.responseParameterList = responseParameterList;
        }
    }

    public static class Parameter {
        private Long id;
        private String identifier;
        private String name;
        private String remark;
        private List<Parameter> parameterList;
        private String validator;
        private String dataType;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getIdentifier() { return identifier; }
        public void setIdentifier(String value) { this.identifier = value; }

        public String getName() { return name; }
        public void setName(String value) { this.name = value; }

        public String getRemark() { return remark; }
        public void setRemark(String value) { this.remark = value; }

        public List<Parameter> getParameterList() {
            return parameterList;
        }

        public void setParameterList(List<Parameter> parameterList) {
            this.parameterList = parameterList;
        }

        public String getValidator() { return validator; }
        public void setValidator(String value) { this.validator = value; }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }
    }

    public static class User {
        private String name;
        private Long id;

        public String getName() { return name; }
        public void setName(String value) { this.name = value; }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    /*public static enum DataType {
        ARRAY_OBJECT, EMPTY, OBJECT, STRING;

        public String toValue() {
            switch (this) {
                case ARRAY_OBJECT: return "array<object>";
                case EMPTY: return "";
                case OBJECT: return "object";
                case STRING: return "string";
            }
            return null;
        }

        public static DataType forValue(String value) throws IOException {
            if (value.equals("array<object>")) return ARRAY_OBJECT;
            if (value.equals("")) return EMPTY;
            if (value.equals("object")) return OBJECT;
            if (value.equals("string")) return STRING;
            throw new IOException("Cannot deserialize DataType");
        }
    }*/

}















