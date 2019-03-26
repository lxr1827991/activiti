package com.bzd.activiti.controller;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/demo5")
public class Demo5Controller {

    //第一次修改

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @RequestMapping("/firstDemo")
    public void firstDemo() {

        //根据bpmn文件部署流程
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("templates/test.bpmn20.xml").deploy();
        //获取流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();

        System.out.println(processDefinition.getId()+";"+processDefinition.getKey());
        //启动流程定义，返回流程实例(提供了多种方式)
        //ProcessInstance pi = runtimeService.startProcessInstanceById(processDefinition.getId());
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("demo5");
        System.out.println("流程创建成功，当前流程实例ID："+pi.getId());

        List<Task> resultTask = taskService.createTaskQuery().processDefinitionKey("demo5").taskCandidateOrAssigned("sly1").list();
        System.out.println("任务列表："+resultTask);

        //在受理节点中,用户sly1表示通过该节点，进入下一个节点审批(true:通过,false：不通过)
        taskService.claim(resultTask.get(0).getId(), "sly1");
        Map<String,Object> vars = new HashMap<String,Object>();
        vars.put("sign", "true");
        taskService.complete(resultTask.get(0).getId(), vars);

        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        System.out.println("当前节点："+task.getName());

        //在审批节点中,用户uspy2表示通过该节点,进入下个系统节点“签发”中
        resultTask = taskService.createTaskQuery().processDefinitionKey("demo5").taskCandidateOrAssigned("uspy2").list();
        taskService.claim(resultTask.get(0).getId(), "sly1");
        Map<String,Object> vars2 = new HashMap<String,Object>();
        vars2.put("sign", "true");
        taskService.complete(resultTask.get(0).getId(), vars2);
    }
}