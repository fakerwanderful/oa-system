package com.web.oa.mapper;

import com.web.oa.pojo.SysPermission;
import com.web.oa.pojo.TreeMenu;

import java.util.List;

public interface TreeMenuDao {

    List<TreeMenu> findMenuList();

    List<SysPermission> getSubMenu();

}
