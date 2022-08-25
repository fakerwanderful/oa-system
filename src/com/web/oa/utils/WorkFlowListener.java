package com.web.oa.utils;


import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.Employee;
import com.web.oa.service.EmployeeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;

public class WorkFlowListener implements TaskListener {

    @Override
    public void notify(DelegateTask task) {
        //硬编码获取spring容器
        WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
        EmployeeService employeeService = (EmployeeService) context.getBean("EmployeeService");
        //获取session
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpSession session = servletRequestAttributes.getRequest().getSession();
        ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
        Employee manger = employeeService.findManger(activeUser.getManagerId());
        task.setAssignee(manger.getName());
    }
}
