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
public class Repository {

    private int id;
    private String name;
    private String description;
    private String logo;
    private String token;
    private boolean visibility;
    private int ownerId;
    private String organizationId;
    private int creatorId;
    private String lockerId;
    private Date createdAt;
    private Date updatedAt;
    private String deletedAt;
    private Long creator;//创建者id
    private Long owner;//所有者id
    private String locker;
    private List<String> members;
    private String organization;
    private List<String> collaborators;
    private List<Modules> modules;
    private boolean canUserEdit;

}