package com.web.oa.controller;

import com.sun.org.apache.bcel.internal.generic.MONITORENTER;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.web.oa.mapper.SysPermissionDaoCustom;
import com.web.oa.mapper.TreeMenuDao;
import com.web.oa.pojo.*;
import com.web.oa.service.EmployeeService;
import com.web.oa.service.TreeMenuService;
import jdk.nashorn.internal.ir.CallNode;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private TreeMenuService treeMenuService;

    @Autowired
    private SysPermissionDaoCustom sysPermissionDaoCustom;

    @Autowired
    private TreeMenuDao treeMenuDao;


    @RequestMapping("/login")
    public String employeeLogin(HttpServletRequest request, Model model) {
        String errorException = (String) request.getAttribute("shiroLoginFailure");
        System.out.println("1:"+request.getAttribute("username"));
        if (errorException != null) {
            if (UnknownAccountException.class.getName().equals(errorException)) {
                model.addAttribute("empMsg", "账号错误");
                System.out.println(UnknownAccountException.class.getName());
            }
            if (IncorrectCredentialsException.class.getName().equals(errorException)) {
                model.addAttribute("empMsg", "密码错误");
                System.out.println(UnknownAccountException.class.getName());
            }
        }
        return "login";
    }

    @RequestMapping("/main")  //成功跳转的页面
    public String employeeLogin(HttpSession session) {
        Subject subject = SecurityUtils.getSubject();
        //取身份信息
        ActiveUser activeUser = (ActiveUser) subject.getPrincipal();
        session.setAttribute("activeUser", activeUser);
        return "index";
    }

    /*系统管理*/
    @RequestMapping("/findUserList")  //用户管理
    public String findUserList(Model model) {
        List<Employee> userList = sysPermissionDaoCustom.findEmpAndRole();
        List<SysRole> allRoles = employeeService.findRoleAll();
        model.addAttribute("userList", userList);
        model.addAttribute("allRoles", allRoles);
        return "userlist";
    }

    /*用户添加*/
    @RequestMapping("/saveUser")  //用户管理
    public String saveUser(Employee employee, Model model) {
        employeeService.saveUser(employee);
        return "redirect:/findUserList";
    }

    /*查看权限   findRoleAndPermissionByUserName() emp*/
    @RequestMapping("/viewPermissionByUserName")  //用户管理
    @ResponseBody
    public SysRole viewPermissionByUserName(String userName) {
        SysRole sysRole = employeeService.findRoleAndPermissionByUserName(userName);
        return sysRole;
    }

    @RequestMapping("/assignRole")  //用户管理
    @ResponseBody
    public Map<String, String> assignRole(String roleId, String userName) {
        Map<String, String> map = new HashMap<>();
        //更换角色的权限
        boolean flag = employeeService.updatePermissionByName(roleId, userName);
        if (flag) {
            map.put("msg", "重新分配成功");
        } else {
            map.put("msg", "重新分配失败");
        }
        return map;
    }

    /*--------角色添加-------------*/
    /*权限显示*/
    @RequestMapping("/toAddRole")
    public String toAddRole(Model model) {
        List<TreeMenu> menuList = treeMenuService.findMenuList();
        model.addAttribute("allPermissions", menuList);
        /*查询出所有的父节点 就是 menu 或者 menu|permission*/
        List<SysPermission> menuTypes = employeeService.findParseLByType();
        for (SysPermission menuType : menuTypes) {
            System.out.println(menuType.getName()+menuType.getType());
        }
        model.addAttribute("menuTypes", menuTypes);
        return "rolelist";
    }

    /*保存角色及权限*/
    @RequestMapping("/saveRoleAndPermissions")
    public String saveRoleAndPermissions(Model model, String roleName, String[] permissionIds) {
        employeeService.saveRoleAndPermission(roleName, permissionIds);
        return "redirect:/toAddRole";
    }

    /*新建权限  saveSubmitPermission*/
    @RequestMapping("/saveSubmitPermission")
    public String saveSubmitPermission(SysPermission sysPermission) {
        /*添加权限表*/
        employeeService.savePermission(sysPermission);
        return "redirect:/toAddRole";
    }



    /*------------角色列表--------------*/
    @RequestMapping("/findRoles")
    public String findRoles(Model model) {
        /*查询角色列表*/
        List<SysRole> roleList = employeeService.findRoleAll();
        List<TreeMenu> menuList = treeMenuDao.findMenuList();
        model.addAttribute("allRoles", roleList);
        model.addAttribute("allMenuAndPermissions", menuList);
        return "permissionlist";
    }

    /*通过当前角色名称查出当前角色的权限*/
    @RequestMapping("/loadMyPermissions")
    @ResponseBody
    public List<SysPermission> loadMyPermissions(Model model, String roleId) {
        //通过roleId查询该角色的权限
        List<SysPermission> permissionList = sysPermissionDaoCustom.findPermissionListByRoleId(roleId);
        return permissionList;
    }

    /*修改当前用户的权限*/
    @RequestMapping("/updateRoleAndPermission")
    public String updateRoleAndPermission(String[] permissionIds, String roleId) {
        employeeService.deleteAndSavePermission(permissionIds, roleId);
        return "redirect:/findRoles";
    }

    /*删除当前角色*/
    @RequestMapping("/deleteRole")
    public String deleteRole(String roleId) {
        employeeService.deletePermissionAndRole(roleId);
        return "redirect:/findRoles";
    }

}
