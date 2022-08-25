package com.web.oa.service.impl;

import com.web.oa.mapper.TreeMenuDao;
import com.web.oa.pojo.SysPermission;
import com.web.oa.pojo.TreeMenu;
import com.web.oa.service.TreeMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TreeMenuServiceImpl implements TreeMenuService {

    @Autowired
    private TreeMenuDao treeMenuDao;

    @Override
    public List<TreeMenu> findMenuList() {
        return treeMenuDao.findMenuList();
    }

    @Override
    public List<SysPermission> getSubMenu() {
        return treeMenuDao.getSubMenu();
    }
}
