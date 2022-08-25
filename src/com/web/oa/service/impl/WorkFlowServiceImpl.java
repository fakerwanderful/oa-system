package com.web.oa.service.impl;

import com.web.oa.mapper.BaoxiaobillDao;
import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.Baoxiaobill;
import com.web.oa.pojo.Employee;
import com.web.oa.service.WorkFlowService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.imap.IMAP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipInputStream;

@Service
public class WorkFlowServiceImpl implements WorkFlowService {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private BaoxiaobillDao baoxiaobillDao;

    //部署流程
    @Override
    public void deployProcess(String processName, InputStream resoureFile) {
        ZipInputStream zipInputStream = new ZipInputStream(resoureFile);
        repositoryService.createDeployment()
                .name(processName)
                .addZipInputStream(zipInputStream)
                .deploy();
    }

    //查看流程部署信息
    @Override
    public List<Deployment> findAllDeployPress() {
        return repositoryService.createDeploymentQuery().list();
    }

    //查看流程定义的信息
    @Override
    public List<ProcessDefinition> findAllProcessDefinition() {
        return repositoryService.createProcessDefinitionQuery().list();
    }

    @Override
    public void deleteDeploy(String deployId) {
        repositoryService.deleteDeployment(deployId, true);
    }

    @Override
    public void addEmpAndStartProcess(Baoxiaobill baoxiaobill, ActiveUser activeUser) {
        //添加请假单
        baoxiaobill.setCreatdate(new Date());
        baoxiaobill.setState(1);
        baoxiaobill.setUserId(activeUser.getUserid());
        baoxiaobillDao.insert(baoxiaobill);
        //启动流程实例
        String key = "baoxiaoProcess";
        String business_key = "baoxiaoProcess" + "." + baoxiaobill.getId();
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", activeUser.getUsername());
        runtimeService.startProcessInstanceByKey(key, business_key, map);
    }

    @Override  //查看我的代办事项
    public List<Task> findTaskList(String assignee) {
        return taskService.createTaskQuery().taskAssignee(assignee).list();
    }

    @Override //查看流程定义图
    public InputStream ProcessImage(String deploymentId, String imageName) {
        InputStream input = repositoryService.getResourceAsStream(deploymentId, imageName);
        return input;
    }

    @Override //根据任务找用户
    public Baoxiaobill findBaoxiaoByTask(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        String businessKey = processInstance.getBusinessKey();
        String id = businessKey.substring(businessKey.indexOf(".") + 1);
        Baoxiaobill baoxiaobill = baoxiaobillDao.selectByPrimaryKey(Long.parseLong(id));
        return baoxiaobill;
    }

    @Override
    public Baoxiaobill findBaoxiaoById(Long id) {
        return baoxiaobillDao.selectByPrimaryKey(id);
    }

    @Override //查询历史任务批注
    public List<Comment> findHistoricComent(Long id) {
        String business_key = "baoxiaoProcess" + "." + id;
        HistoricProcessInstance historicProcessInstance = this.historyService.createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(business_key)
                .singleResult();
        List<Comment> processInstanceComments = this.taskService.getProcessInstanceComments(historicProcessInstance.getId());
        return processInstanceComments;
    }

    @Override //查询当前任务 进行中
    public List<Comment> findTaskComent(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        List<Comment> processInstanceComments = taskService.getProcessInstanceComments(processInstance.getId());
        return processInstanceComments;
    }

    @Override
    public void submitTask(Long id, String taskId, String comment, String outcome, String name) {
        //写批注
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        //流程推进
        Authentication.setAuthenticatedUserId(name);
        taskService.addComment(taskId, task.getProcessInstanceId(), comment);
        Map<String, Object> map = new HashMap();
        map.put("message", outcome);
        //带条件推进和不带条件推进
        if (outcome != null && !outcome.equals("")) {
            taskService.complete(taskId, map);
        } else {
            taskService.complete(taskId);
        }
        //假如流程结束 修改业务表
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        if (pi == null) {
            Baoxiaobill baoxiaobill = baoxiaobillDao.selectByPrimaryKey(id);
            baoxiaobill.setState(2);
            baoxiaobillDao.updateByPrimaryKey(baoxiaobill);
        }
    }

    @Override
    public ProcessDefinition findProcessDefinitionByTaskId(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        return repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
    }

    @Override
    public Map<String, Object> findCordingByTask(String taskId) {
        Map<String, Object> map = new HashMap<>();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        FlowElement flowElement = bpmnModel.getFlowElement(task.getTaskDefinitionKey());
        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowElement.getId());
        map.put("x", graphicInfo.getX());
        map.put("y", graphicInfo.getY());
        map.put("height", graphicInfo.getHeight());
        map.put("width", graphicInfo.getWidth());
        return map;
    }

    @Override //删除流程实例
    public void deleteBaoxiaobillAndTask(long billId) {
        //1.删除我的报销单
        baoxiaobillDao.deleteByPrimaryKey(billId);
        //2.删除任务流程
        String businessKey = "baoxiaoProcess." + billId;
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(businessKey)
                .singleResult();
        runtimeService.deleteProcessInstance(processInstance.getId(), "delete");


    }

    @Override
    public Task findTaskByBillId(long billId) {
        String businessKey = "baoxiaoProcess." + billId;
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(businessKey)
                .singleResult();
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).singleResult();
        return task;
    }

    @Override
    public List<String> findOutComeListByTaskId(String taskId) {
        //返回存放连线的名称集合
        List<String> list = new ArrayList<String>();
        //1:使用任务ID，查询任务对象
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        //2：获取流程定义ID
        String processDefinitionId = task.getProcessDefinitionId();
        //3：查询ProcessDefinitionEntiy对象
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
        //使用任务对象Task获取流程实例ID
        String processInstanceId = task.getProcessInstanceId();
        //使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
                .processInstanceId(processInstanceId)//使用流程实例ID查询
                .singleResult();
        //获取当前活动的id
        String activityId = pi.getActivityId();
        //4：获取当前的活动
        ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);
        //5：获取当前活动完成之后连线的名称
        List<PvmTransition> pvmList = activityImpl.getOutgoingTransitions();
        if (pvmList != null && pvmList.size() > 0) {
            for (PvmTransition pvm : pvmList) {
                String name = (String) pvm.getProperty("name");
                if (StringUtils.isNotBlank(name)) {
                    list.add(name);
                } else {
                    list.add("默认提交");
                }
            }
        }
        return list;
    }


}
