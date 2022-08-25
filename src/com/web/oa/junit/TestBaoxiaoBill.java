package com.web.oa.junit;

import com.web.oa.mapper.SysPermissionDaoCustom;
import com.web.oa.pojo.Employee;
import com.web.oa.pojo.SysPermission;
import com.web.oa.pojo.TreeMenu;
import com.web.oa.service.TreeMenuService;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml", "classpath:spring/springmvc.xml"})
public class TestBaoxiaoBill {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private FormService formService;
    @Autowired
    private HistoryService historyService;

    @Autowired
    private SysPermissionDaoCustom sysPermissionDaoCustom;
    //1.部署流程
    @Test
    public void testDeploy() throws FileNotFoundException {
        InputStream inputStream = new FileInputStream("D:\\粤嵌\\diagram\\baoxiaoProcess.zip");
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        Deployment deployment = repositoryService.createDeployment()
                .name("报销部署8")
                .addZipInputStream(zipInputStream)
                .deploy();
        System.out.println(deployment.getName());

    }

    //2.启动实例
    @Test
    public void testProcessIns() {
        //根据key值启动
        String id = "baoxiaoProcess";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", "刘秀");
        runtimeService.startProcessInstanceByKey(id, map);
    }

    //3.结束当前任务,推进流程
    @Test
    public void testFinishTask() {
        String id = "2202";
        taskService.complete(id);
    }

    //4.结束当前任务,推进流程,有条件的推进
    @Test
    public void testFinishTask1() {
        String id = "2002";
        Map<String, Object> map = new HashMap<>();
        map.put("message", "金额大于5000");
        taskService.complete(id, map);
    }

    @Test  //5.删除流程全部
    public void testDeleteProcess() {
        String id = "701";
        repositoryService.deleteDeployment(id, true);
    }

    //6.查看当前代办人的代办事务
    @Test
    public void testFindTaskByAssignee() {
        String name = "刘秀";
        List<Task> list = taskService.createTaskQuery().taskAssignee(name).list();
        for (Task task : list) {
            System.out.println("当前任务id:" + task.getId());
            System.out.println("当前所属流程实例:" + task.getProcessInstanceId());
        }
    }

    //7/查看流程定义图
    @Test
    public void testFindPic() {
        String id = "2601";
        String pic = "baoxiaoProcess.png";
        InputStream in = repositoryService.getResourceAsStream(id, pic);
        File file = new File("D:\\" + pic);
    }

    @Autowired
    private TreeMenuService treeMenuService;

    //测试
    @Test
    public void demo1() {
        List<TreeMenu> menuList = treeMenuService.findMenuList();
        for (TreeMenu treeMenu : menuList) {
            System.out.println("主菜单名称...." + treeMenu.getName());
            List<SysPermission> children = treeMenu.getChildren();
            for (SysPermission child : children) {
                System.out.println("地址+........" + child.getUrl());
            }
        }
    }




}
