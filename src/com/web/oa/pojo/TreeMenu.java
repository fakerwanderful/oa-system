package com.web.oa.pojo;

import java.util.List;

public class TreeMenu {

    private Long id;

    private String name;//一级菜单的名称

    List<SysPermission> children;//二级菜单的名称


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SysPermission> getChildren() {
        return children;
    }

    public void setChildren(List<SysPermission> children) {
        this.children = children;
    }
}
