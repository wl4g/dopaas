package com.wl4g.devops.common.bean.doc.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
/**
 * Auto-generated: 2020-12-11 10:33:3
 *
 * @author heweijie
 */
@Getter
@Setter
public class Modules {

    private int id;
    private String name;
    private String description;
    private int priority;
    private int creatorId;
    private int repositoryId;
    private Date createdAt;
    private Date updatedAt;
    private String deletedAt;
    private List<Interfaces> interfaces;

}