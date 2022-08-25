package com.web.oa.service;

import com.web.oa.pojo.SysPermission;
import com.web.oa.pojo.TreeMenu;

import java.util.List;

public interface TreeMenuService {


    List<TreeMenu> findMenuList();

    List<SysPermission> getSubMenu();
}
