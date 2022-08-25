package com.web.oa.shiro;

import com.web.oa.mapper.TreeMenuDao;
import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.Employee;
import com.web.oa.pojo.SysPermission;
import com.web.oa.pojo.TreeMenu;
import com.web.oa.service.EmployeeService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class CustomRealm extends AuthorizingRealm {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private TreeMenuDao treeMenuDao;


    @Override  //用户认证
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //先获取用户输入的账号
        String username = (String) token.getPrincipal(); /*前端传回来的*/
        Employee employee = null; /*用户表*/
        List<TreeMenu> menus = null;/*菜单列表*/
        List<SysPermission> permissionListByUserId = null;/*权限表*/
        try {
            employee = employeeService.employeeLogin(username);
            if (employee == null) {
                return null;
            }
            //查询用户的菜单列表
            menus = treeMenuDao.findMenuList();
            //查询用户的权限表
            permissionListByUserId = employeeService.findPermissionListByUserId(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String password_db = employee.getPassword();
        String salt = employee.getSalt();

        System.out.println("数据库："+employee.getName());
        System.out.println("数据库："+employee.getPassword());
        System.out.println("数据库："+employee.getSalt());

        //构建新的用户封装起来
        ActiveUser activeUser = new ActiveUser();
        activeUser.setUserid(employee.getId());
        activeUser.setUsername(employee.getName());
        activeUser.setPassword(employee.getPassword());
        activeUser.setManagerId(employee.getManagerId());
        activeUser.setMenus(menus);
        activeUser.setPermissions(permissionListByUserId);
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(activeUser, password_db, ByteSource.Util.bytes(salt), "CustomRealm");  //验证密码
        return info;
    }

    @Override //权限控制
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {
        List<String> permissions = null;
        try {
            ActiveUser activeUser = (ActiveUser) principal.getPrimaryPrincipal();
            List<SysPermission> permissionList = employeeService.findPermissionListByUserId(activeUser.getUsername());
            permissions = new ArrayList<>();
            for (SysPermission permission : permissionList) {
                permissions.add(permission.getPercode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addStringPermissions(permissions);
        return info;
    }

}
