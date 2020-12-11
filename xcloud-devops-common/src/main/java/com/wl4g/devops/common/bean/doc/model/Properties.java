package com.wl4g.devops.common.bean.doc.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Auto-generated: 2020-12-11 10:33:3
 *
 * @author heweijie
 */
@Getter
@Setter
public class Properties {

    private int id;
    private String scope;
    private String type;
    private int pos;
    private String name;
    private String rule;
    private String value;
    private String description;
    private int parentId;
    private int priority;
    private int interfaceId;
    private int creatorId;
    private int moduleId;
    private int repositoryId;
    private boolean required;
    private Date createdAt;
    private Date updatedAt;
    private String deletedAt;

}