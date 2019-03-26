import com.bzd.activiti.ActivitiApplication;
import com.bzd.activiti.controller.Demo5Controller;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ActivitiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Test {

    Logger logger = LoggerFactory.getLogger(Test.class);

    @Autowired
    Demo5Controller demo5Controller;

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @Autowired
    ProcessEngine processEngine;

    String cfgName ="activiti.cfg.xml";
    String taskId ="";
    String assignee ="employee";
    String process ="myProcess_1";
    String deployName = "templates/test.bpmn";


    @org.junit.Test
    public void test(){
        demo5Controller.firstDemo();

        System.out.println("234");

    }

    @org.junit.Test
    public void testCreateProcessEngineByCfgXml() {
        ProcessEngineConfiguration cfg = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource(cfgName);
        processEngine = cfg.buildProcessEngine();
        logger.info("根据配置文件"+cfgName+"创建ProcessEngine成功");
    }
    @org.junit.Test
    public void deployProcess() {
        RepositoryService repositoryService =processEngine.getRepositoryService();
        DeploymentBuilder builder = repositoryService.createDeployment();
        builder.addClasspathResource(deployName);
        builder.deploy();
        logger.info("发布流程"+deployName+"成功");
    }


    @org.junit.Test
    public void startProcess() {
        RuntimeService runtimeService =processEngine.getRuntimeService();
//可根据id、key、message启动流程
    runtimeService.startProcessInstanceByKey(process);
        logger.info("启动流程"+process+"成功");
    }

    @org.junit.Test
    public void queryExecTask() {
        TaskService taskService =processEngine.getTaskService();
//根据assignee(代理人)查询任务
    List<Task> tasks = taskService.createTaskQuery().processInstanceId("10002").list();
        Calendar calendar = Calendar.getInstance();
        for (Task task : tasks) {
            taskId= task.getId();
            calendar.setTime(task.getCreateTime());
            logger.info("查看任务：taskId:" +taskId +
                    ",taskName:" + task.getName() +
                    ",assignee:" + task.getAssignee() +
                    ",createTime:" +calendar.getTime());
            handleTask();
        }
    }

    @org.junit.Test
    public void handleTask() {
        TaskService taskService =processEngine.getTaskService();
        taskService.complete(taskId);
        logger.info("执行任务taskId:" +taskId +" complete");
    }

    /**查询流程状态（判断流程正在执行，还是结束）*/
    @org.junit.Test
    public void isProcessEnd(){
        String processInstanceId = "1401";
        //去正在执行的任务表查询
        List<ProcessInstance> pi = processEngine.getRuntimeService()//表示正在执行的流程实例和执行对象
                .createProcessInstanceQuery()//创建流程实例查询
                //.processInstanceId(processInstanceId)//使用流程实例ID查询
                .list();
        for (ProcessInstance p:pi
             ) {
            System.out.println("流程："+p);
        }
//      输出：
//      该流程实例还没走完
    }


}
