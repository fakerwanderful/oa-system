package com.web.oa.service;

import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.Baoxiaobill;
import com.web.oa.pojo.Employee;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface WorkFlowService {

    //流程定义和部署
    void deployProcess(String processName, InputStream resoureFile);

    //流程部署查看
    List<Deployment> findAllDeployPress();

    //流程定义查看
    List<ProcessDefinition> findAllProcessDefinition();

    //删除流程部署
    void deleteDeploy(String deployId);

    //提交流程,启动流程实例
    void addEmpAndStartProcess(Baoxiaobill baoxiaobill, ActiveUser activeUser);

    //查看我的代办事务
    List<Task> findTaskList(String assignee);

    //查看流程定义图
    InputStream ProcessImage(String deploymentId, String imageName);

    Baoxiaobill findBaoxiaoByTask(String taskId);

    Baoxiaobill findBaoxiaoById(Long id);



    List<Comment> findHistoricComent(Long id);

    List<Comment> findTaskComent(String taskId);

    //办理当前任务,推进任务流程
    void submitTask(Long id, String taskId, String comment, String outcome, String name);

    //查看当前流程图
    ProcessDefinition findProcessDefinitionByTaskId(String taskId);

    //获取当前流程图的具体位置
    Map<String, Object> findCordingByTask(String taskId);

    //删除我的报销单及任务
    void deleteBaoxiaobillAndTask(long billId);

    //通过报销id获取任务
    Task findTaskByBillId(long billId);

    //查看流程节点的各个分支名称
    List<String> findOutComeListByTaskId(String taskId);


}
