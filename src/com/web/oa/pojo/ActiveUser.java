package com.web.oa.pojo;

import java.util.List;

public class ActiveUser implements java.io.Serializable {

    private Long userid;//用户id（主键）
    private String username;// 用户账号
    private String password;//用户密码
    private Long managerId;//上级id
    private List<TreeMenu> menus;// 菜单

    private List<SysPermission> permissions;// 权限

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<TreeMenu> getMenus() {
        return menus;
    }

    public void setMenus(List<TreeMenu> menus) {
        this.menus = menus;
    }

    public List<SysPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<SysPermission> permissions) {
        this.permissions = permissions;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }
}
