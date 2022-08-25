package com.web.oa.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.Baoxiaobill;
import com.web.oa.pojo.Employee;
import com.web.oa.service.EmployeeService;
import com.web.oa.service.WorkFlowService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.List;
import java.util.Map;

@Controller
public class WorkFlowController {
    @Autowired
    private WorkFlowService workFlowService;
    @Autowired
    private EmployeeService employeeService;

    @RequestMapping("/deployProcess")
    public String deployProcess(String processName, MultipartFile fileName) {
        try {
            workFlowService.deployProcess(processName, fileName.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/processDefinitionList";
    }

    @RequestMapping("/processDefinitionList")
    public ModelAndView processDefinitionList() {
        ModelAndView mv = new ModelAndView();
        List<Deployment> depList = workFlowService.findAllDeployPress();
        List<ProcessDefinition> pdList = workFlowService.findAllProcessDefinition();
        mv.addObject("depList", depList);
        mv.addObject("pdList", pdList);
        mv.setViewName("workflow_list");
        return mv;
    }

    //删除流程id
    @RequestMapping("/delDeployment")
    public String delDeployment(String deploymentId) {
        workFlowService.deleteDeploy(deploymentId);
        return "redirect:/processDefinitionList";
    }

    //提交报销申请
    @RequestMapping("/saveStartLeave")
    public String saveStartLeave(Baoxiaobill baoxiaobill, HttpSession session) {
        ActiveUser user = (ActiveUser) session.getAttribute("activeUser");
        workFlowService.addEmpAndStartProcess(baoxiaobill, user);
        return "redirect:/myTaskList";
    }

    @RequestMapping("/myTaskList")  //我的代办任务查询
    public String myTaskList(Model model, HttpSession session) {
        ActiveUser user = (ActiveUser) session.getAttribute("activeUser");
        List<Task> taskList = workFlowService.findTaskList(user.getUsername());

        model.addAttribute("taskList", taskList);
        return "workflow_task";
    }

    //查看流程定义图 viewImage  deploymentId imageName
    @RequestMapping("/viewImage")
    public String viewImage(String deploymentId, String imageName, HttpServletResponse response) {
        InputStream in = workFlowService.ProcessImage(deploymentId, imageName);
        try {
            OutputStream out = response.getOutputStream();
            for (int b = -1; (b = in.read()) != -1; ) {
                out.write(b);
            }
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "viewimage";
    }

    //查看当前任务
    @RequestMapping("/viewTaskForm")
    public String viewTaskForm(String taskId, Model model) {
        //1.查看当前的代办任务
        Baoxiaobill baoxiaobill = workFlowService.findBaoxiaoByTask(taskId);
        //2.查看当前的批注
        List<Comment> commentList = workFlowService.findTaskComent(taskId);
        model.addAttribute("bill", baoxiaobill);
        model.addAttribute("commentList", commentList);
        model.addAttribute("taskId", taskId);
        //3.查看当前节点
        List<String> outcomeList = workFlowService.findOutComeListByTaskId(taskId);
        model.addAttribute("outcomeList", outcomeList);
        return "approve_leave";
    }

    //根据任务id查看当前任务图     //查询流程实例  viewCurrentImageByBill
    @RequestMapping("/viewCurrentImage")
    public String viewCurrentImage(String taskId, Model model) {
        /*查看流程tu*/
        ProcessDefinition processDefinition = workFlowService.findProcessDefinitionByTaskId(taskId);
        model.addAttribute("deploymentId", processDefinition.getDeploymentId());
        model.addAttribute("imageName", processDefinition.getDiagramResourceName());
        /*查看当前活动那个的位置*/
        Map<String, Object> map = workFlowService.findCordingByTask(taskId);
        model.addAttribute("acs", map);
        return "viewimage";
    }

    //根据报销id查看当前任务图
    @RequestMapping("/viewCurrentImageByBill")
    public String viewCurrentImageByBill(Long billId, Model model) {
        Task task = workFlowService.findTaskByBillId(billId);
        /*查看流程tu*/
        ProcessDefinition processDefinition = workFlowService.findProcessDefinitionByTaskId(task.getId());
        model.addAttribute("deploymentId", processDefinition.getDeploymentId());
        model.addAttribute("imageName", processDefinition.getDiagramResourceName());
        /*查看当前活动那个的位置*/
        Map<String, Object> map = workFlowService.findCordingByTask(task.getId());
        model.addAttribute("acs", map);
        return "viewimage";
    }


    //submitTask  //审批任务的提交 加上指定代办人
    @RequestMapping("/submitTask")
    public String submitTask(Long id, String taskId, String comment, String outcome, HttpSession session) {
        ActiveUser user = (ActiveUser) session.getAttribute("activeUser");
        workFlowService.submitTask(id, taskId, comment, outcome, user.getUsername());
        session.setAttribute("message", outcome);
        return "redirect:/myTaskList";
    }

    //查询我的报销单
    @RequestMapping("/myBaoxiaoBill")
    public String myBaoxiaoBill(HttpSession session, Model model, @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum) {
        ActiveUser user = (ActiveUser) session.getAttribute("activeUser");
        /*在查询之前做分页拦截*/
        PageHelper.startPage(pageNum, 4);
        List<Baoxiaobill> baoxiaobillList = employeeService.findBaoxiaobillByid(user.getUserid());
        PageInfo pageInfo = new PageInfo(baoxiaobillList);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("baoxiaobillList", baoxiaobillList);
        return "baoxiaobill";
    }

    //删除流程定义及报销单
    @RequestMapping("/deleteBaoXiaoById")
    public String deleteBaoXiaoById(long id) {
        workFlowService.deleteBaoxiaobillAndTask(id);
        return "redirect:/myBaoxiaoBill";
    }

    //查看审核记录
    @RequestMapping("/viewHisComment")
    public String viewHisComment(Long id, Model model) {
        //查询出当前的报销信息回显
        //1.查看当前的代办任务
        Baoxiaobill baoxiaobill = workFlowService.findBaoxiaoById(id);
        //查出批注信息
        List<Comment> commentList = workFlowService.findHistoricComent(id);
        model.addAttribute("baoxiaoBill",baoxiaobill);
        model.addAttribute("commentList", commentList);
        return "workflow_commentlist";
    }


}
