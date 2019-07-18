package com.iscas.cs.server.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/4 22:00
 * @since jdk1.8
 */
public class CronTriggerHelper {
    public static void trigger() throws SchedulerException {
        //初始化一个Schedule工厂
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        //通过schedule工厂类获得一个Scheduler类
        Scheduler scheduler = schedulerFactory.getScheduler();
        //通过设置job name, job group, and executable job class初始化一个JobDetail
        JobDetail jobDetail =
                new JobDetail("jobDetail2", "jobDetailGroup2", SimpleQuartzJob.class);
        //设置触发器名称和触发器所属的组名初始化一个定时触发器
        CronTrigger cronTrigger = new CronTrigger("cronTrigger", "triggerGroup2");
        try {
            //设置定时器的触发规则
            //每隔5分钟执行一次
            CronExpression cexp = new CronExpression("0 0/1 * * * ?");
//            CronExpression cexp = new CronExpression("0/5 * * ? * *");
//            CronExpression cexp = new CronExpression("0 */1 * * * ?");
            //注册这个定时规则到定时触发器中
            cronTrigger.setCronExpression(cexp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //交给调度器调度运行JobDetail和Trigger
        scheduler.scheduleJob(jobDetail, cronTrigger);
        //启动调度器
        scheduler.start();
    }
}
