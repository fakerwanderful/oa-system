package com.web.oa.service;

import com.web.oa.pojo.Baoxiaobill;
import com.web.oa.pojo.Employee;
import com.web.oa.pojo.SysPermission;
import com.web.oa.pojo.SysRole;

import java.util.List;

public interface EmployeeService {
    //登录的方法
    Employee employeeLogin(String name);

    //根据userid查询所属订单呢
    List<Baoxiaobill> findBaoxiaobillByid(Long id);

    //根据报销的id查询当前报销单
    Baoxiaobill findBaoxiaobillByBillId(Long billId);

    //根据上级id查询上级
    Employee findManger(Long managerId);

    //查询用户的权限菜单
    List<SysPermission> findMenuListByUserId(String userid) throws Exception;

    //根据用户id查询权限范围的url
    List<SysPermission> findPermissionListByUserId(String userid) throws Exception;

    /*系统管理---------------------- */

    List<Employee> findUSerAll();

    List<SysRole> findRoleAll();

    void saveUser(Employee employee);

    /*权限查看,一个角色有多个权限*/
    SysRole findRoleAndPermissionByUserName(String name);

    /*通过id查询角色*/
    SysRole findRoleById(String id);

    /*查询所有的父节点*/
    List<SysPermission> findParseLByType();

    /*添加权限表*/
    void savePermission(SysPermission sysPermission);


    /*通过用户名修改权限*/
    boolean updatePermissionByName(String roleId, String userName);

    /*新增角色表*/
    void saveRoleAndPermission(String roleName, String[] permissionIds);

    void deleteAndSavePermission(String[] permissionIds, String roleId);

    /*删除角色*/
    void deletePermissionAndRole(String roleId);


}
