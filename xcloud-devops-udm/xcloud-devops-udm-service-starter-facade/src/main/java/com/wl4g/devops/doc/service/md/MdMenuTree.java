package com.wl4g.devops.doc.service.md;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vjay
 * @date 2021-02-02 16:29:00
 */
public class MdMenuTree {

    private String name;

    private String path;

    private String dir;

    private List<MdMenuTree> children = new ArrayList<>();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<MdMenuTree> getChildren() {
        return children;
    }

    public void setChildren(List<MdMenuTree> children) {
        this.children = children;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }
}
