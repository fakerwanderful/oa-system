package com.web.oa.service.impl;

import com.web.oa.mapper.*;
import com.web.oa.pojo.*;
import com.web.oa.service.EmployeeService;
import net.sf.ehcache.constructs.nonstop.store.RejoinAwareNonstopStore;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("EmployeeService")
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeDao employeeMapper;
    @Autowired
    private BaoxiaobillDao baoxiaobillDao;
    @Autowired
    private SysUserRoleDao sysUserRoleDao;
    @Autowired
    private SysRoleDao sysRoleDao;
    @Autowired
    private SysRolePermissionDao sysRolePermissionDao;
    @Autowired
    private SysPermissionDao sysPermissionDao;
    @Autowired
    private SysPermissionDaoCustom sysPermissionDaoCustom;

    @Override
    public Employee employeeLogin(String name) {
        EmployeeExample employeeExample = new EmployeeExample();
        EmployeeExample.Criteria criteria = employeeExample.createCriteria();
        criteria.andNameEqualTo(name);
        List<Employee> list = employeeMapper.selectByExample(employeeExample);
        if (list.size() > 0)
            return list.get(0);
        return null;
    }

    @Override
    public List<Baoxiaobill> findBaoxiaobillByid(Long id) {
        BaoxiaobillExample baoxiaobillExample = new BaoxiaobillExample();
        BaoxiaobillExample.Criteria criteria = baoxiaobillExample.createCriteria();
        criteria.andUserIdEqualTo(id);
        List<Baoxiaobill> baoxiaobills = baoxiaobillDao.selectByExample(baoxiaobillExample);
        return baoxiaobills;
    }

    @Override
    public Baoxiaobill findBaoxiaobillByBillId(Long billId) {
        return baoxiaobillDao.selectByPrimaryKey(billId);
    }

    @Override
    public Employee findManger(Long managerId) {
        return employeeMapper.selectByPrimaryKey(managerId);
    }

    @Override
    public List<SysPermission> findMenuListByUserId(String userid) throws Exception {
        return sysPermissionDaoCustom.findMenuListByUserId(userid);
    }

    @Override
    public List<SysPermission> findPermissionListByUserId(String userid) throws Exception {
        return sysPermissionDaoCustom.findPermissionListByUserId(userid);
    }

    @Override
    public List<Employee> findUSerAll() {
        return employeeMapper.selectByExample(null);
    }

    @Override
    public List<SysRole> findRoleAll() {
        return sysRoleDao.selectByExample(null);
    }

    @Override
    public void saveUser(Employee employee) {
        employee.setSalt("eteokues");
        Md5Hash md5Hash = new Md5Hash(employee.getPassword(), employee.getSalt(), 3);
        employee.setPassword(md5Hash.toString());
        employeeMapper.insert(employee);
        //??????roleid??????????????????
        SysRole sysRole = sysRoleDao.selectByPrimaryKey(employee.getRole().toString());
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setId(employee.getId().toString());
        sysUserRole.setSysRoleId(sysRole.getId());
        sysUserRole.setSysUserId(employee.getName());
        sysUserRoleDao.insert(sysUserRole);
    }

    @Override
    public SysRole findRoleAndPermissionByUserName(String name) {
        return sysPermissionDaoCustom.findRoleAndPermissionByUserName(name);
    }

    @Override
    public SysRole findRoleById(String id) {
        return sysRoleDao.selectByPrimaryKey(id);
    }

    @Override  /*????????????????????????*/
    public List<SysPermission> findParseLByType() {
        SysPermissionExample example = new SysPermissionExample();
        SysPermissionExample.Criteria criteria = example.createCriteria();
        criteria.andTypeBetween("menu", "menu|permission");
        List<SysPermission> sysPermissionList = sysPermissionDao.selectByExample(example);
        return sysPermissionList;
    }

    @Override
    public void savePermission(SysPermission sysPermission) {
        sysPermissionDao.insert(sysPermission);
    }

    @Override
    public boolean updatePermissionByName(String roleId, String userName) {
        SysUserRoleExample example = new SysUserRoleExample();
        SysUserRoleExample.Criteria criteria = example.createCriteria();
        //??????????????????????????????
        criteria.andSysUserIdEqualTo(userName);
        List<SysUserRole> sysUserRoleList = sysUserRoleDao.selectByExample(example);
        //????????????????????? ?????????????????????id????????????
        if (sysUserRoleList.size() > 0) {
            SysUserRole sysUserRole = sysUserRoleList.get(0);
            sysUserRole.setSysRoleId(roleId);
            sysUserRoleDao.updateByPrimaryKey(sysUserRole);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void saveRoleAndPermission(String roleName, String[] permissionIds) {
        //???????????????
        SysRole sysRole = new SysRole();
        sysRole.setName(roleName);
        String uuidRoleId = UUID.randomUUID().toString().replace("-", "");
        sysRole.setId(uuidRoleId);
        sysRole.setAvailable("1");
        sysRoleDao.insert(sysRole);
        //????????????_?????????
        for (String permissionId : permissionIds) {
            SysRolePermission sysRolePermission = new SysRolePermission();
            String uuidPermissionId = UUID.randomUUID().toString().replace("-", "");
            sysRolePermission.setId(uuidPermissionId);
            sysRolePermission.setSysRoleId(uuidRoleId);
            sysRolePermission.setSysPermissionId(permissionId);
            sysRolePermissionDao.insert(sysRolePermission);
        }
    }

    @Override
    public void deleteAndSavePermission(String[] permissionIds, String roleId) {
        //??????roleId??????????????????
        SysRolePermissionExample example = new SysRolePermissionExample();
        SysRolePermissionExample.Criteria criteria = example.createCriteria();
        criteria.andSysRoleIdEqualTo(roleId);
        sysRolePermissionDao.deleteByExample(example);
        //??????permissionIds??????????????????
        //????????????_?????????
        for (String permissionId : permissionIds) {
            SysRolePermission sysRolePermission = new SysRolePermission();
            String uuidPermissionId = UUID.randomUUID().toString().replace("-", "");
            sysRolePermission.setId(uuidPermissionId);
            sysRolePermission.setSysRoleId(roleId);
            sysRolePermission.setSysPermissionId(permissionId);
            sysRolePermissionDao.insert(sysRolePermission);
        }
    }

    @Override
    public void deletePermissionAndRole(String roleId) {
        // sysRoleDao sysRolePermissionDao
        sysRoleDao.deleteByPrimaryKey(roleId);//???????????????
        //??????roleId??????????????????
        SysRolePermissionExample example = new SysRolePermissionExample();
        SysRolePermissionExample.Criteria criteria = example.createCriteria();
        criteria.andSysRoleIdEqualTo(roleId);
        sysRolePermissionDao.deleteByExample(example);
    }

}
