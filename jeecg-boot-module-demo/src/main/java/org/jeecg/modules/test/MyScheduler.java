package org.jeecg.modules.test;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

public class MyScheduler {
    public static void main(String[] args) throws SchedulerException, InterruptedException {
        // 1、创建调度器Scheduler
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        // 2、创建JobDetail实例，并与PrintWordsJob类绑定(Job执行内容)
        JobDetail jobDetail = JobBuilder.newJob(QuartzTest.class)
                .usingJobData("jobDetail1", "这个Job用来测试的")
                .withIdentity("job1", "group1").build();
        JobDetail jobDetail1 = JobBuilder.newJob(QuartzTest.class)
                .usingJobData("jobDetail11", "这个Job1用来测试的")
                .withIdentity("job2", "group2").build();
        // 3、构建Trigger实例,每隔1s执行一次
        Date startDate = new Date();
        startDate.setTime(startDate.getTime());
        Date endDate = new Date();
        endDate.setTime(startDate.getTime());

        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "triggerGroup1")
                .usingJobData("trigger1", "这是jobDetail1的trigger")
                .startNow()//立即生效
                //.startAt(startDate)

                .withSchedule(CronScheduleBuilder.cronSchedule("0/30 * * * * ? *"))
                .build();

        CronTrigger cronTrigger1 = TriggerBuilder.newTrigger().withIdentity("trigger2", "triggerGroup2")
                .usingJobData("trigger2", "这是jobDetail2的trigger")
                .startNow()//立即生效
                //.startAt(startDate)

                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * * * ? *"))
                .build();
        //4、执行
        scheduler.scheduleJob(jobDetail, cronTrigger);
        scheduler.scheduleJob(jobDetail1, cronTrigger1);
        scheduler.pauseTrigger(TriggerKey.triggerKey("trigger","triggerGroup2"));
        System.out.println("--------scheduler start ! ------------");
        scheduler.start();
        System.out.println("--------scheduler shutdown ! ------------");

    }
}
