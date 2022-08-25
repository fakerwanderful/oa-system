package com.web.oa.mapper;

import com.web.oa.pojo.Employee;
import com.web.oa.pojo.SysPermission;
import com.web.oa.pojo.SysRole;

import java.util.List;

public interface SysPermissionDaoCustom {

    //根据用户id查询菜单
    List<SysPermission> findMenuListByUserId(String userid) throws Exception;

    //根据用户id查询权限url
    List<SysPermission> findPermissionListByUserId(String userid) throws Exception;


    //查询User和role的联表
    List<Employee> findEmpAndRole();

    /*查看权限  一个角色对应多个权限*/
    SysRole findRoleAndPermissionByUserName(String userName);

    /*通过role_id 查询权限表*/
    List<SysPermission> findPermissionListByRoleId(String roleId);


}
