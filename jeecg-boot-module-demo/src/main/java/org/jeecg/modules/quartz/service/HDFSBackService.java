package org.jeecg.modules.quartz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.quartz.entity.HDFSBackup;
import org.jeecg.modules.quartz.job.HDFSFileBackupJob;
import org.jeecg.modules.quartz.mapper.HDFSBackupMapper;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class HDFSBackService extends ServiceImpl<HDFSBackupMapper, HDFSBackup> {

    @Autowired
    private HDFSBackupMapper hdfsBackupMapper;
    @Autowired
    private Scheduler scheduler;

    /**
     * 查询全部的定时任务
     */
    public IPage listAllByPage(int pageNo, int pageSize) {

        IPage<HDFSBackup> HDFSBackupPolicyIPage = hdfsBackupMapper.selectPage(new Page<HDFSBackup>(pageNo, pageSize), null);
        return HDFSBackupPolicyIPage;
    }

    /**
     * 保存&启动定时任务
     */
    public boolean addPolicy(HDFSBackup hdfsBackup) {
        if (CommonConstant.STATUS_NORMAL.equals(hdfsBackup.getStatus())) {
            // 定时器添加
            this.schedulerAdd(hdfsBackup.getCronExpression().trim(), hdfsBackup.getValue(), hdfsBackup.getValue());
        }
        // DB设置修改
        hdfsBackup.setDelFlag(CommonConstant.DEL_FLAG_0);
        return this.save(hdfsBackup);
    }

    /**
     * 根据HDFS文件路径查询定时任务
     */
    public List<HDFSBackup> findByHDFSValue(String value) {
        return hdfsBackupMapper.findByHDFSValue(value);
    }

    private void schedulerAdd(String cronExpression, String parameter, String id) {

        log.info("启动定时任务" + cronExpression + "---" + parameter + "---" + id);
        try {
            // 启动调度器
            scheduler.start();

            scheduler.deleteJob(JobKey.jobKey(parameter));
            // 构建job信息
            JobDetail jobDetail = JobBuilder.newJob(HDFSFileBackupJob.class).withIdentity(id).usingJobData("parameter", parameter).build();

            // 表达式调度构建器(即任务执行的时间)
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

            // 按新的cronExpression表达式构建一个新的trigger
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(id).withSchedule(scheduleBuilder).build();

            scheduler.scheduleJob(jobDetail, trigger);
            Set<JobKey> jobKeySet = scheduler.getJobKeys(GroupMatcher.anyGroup());

        } catch (SchedulerException e) {
            throw new JeecgBootException("创建定时任务失败", e);
        } catch (RuntimeException e) {
            throw new JeecgBootException(e.getMessage(), e);
        } catch (Exception e) {
            throw new JeecgBootException("后台找不到该类名：" + parameter, e);
        }
    }

    /***
     * 恢复定时任务
     * @param job
     * @return
     */
    public boolean resumeJob(HDFSBackup job) {
        schedulerDelete(job.getValue().trim());
        schedulerAdd(job.getCronExpression(), job.getValue(), job.getValue());
        job.setStatus(CommonConstant.STATUS_NORMAL);
        return this.updateById(job);
    }

    /**
     * 删除定时任务
     * 根据备份策略id进行删除定时任务
     *
     * @param id
     */
    private void schedulerDelete(String value) {
        try {
            scheduler.pauseTrigger(TriggerKey.triggerKey(value));
            scheduler.unscheduleJob(TriggerKey.triggerKey(value));
            scheduler.deleteJob(JobKey.jobKey(value));

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new JeecgBootException("删除定时任务失败");
        }
    }

    /**
     * 删除&停止删除定时任务
     */
    public boolean deleteAndStopJob(HDFSBackup job) {
        schedulerDelete(job.getValue());
        boolean ok = this.removeById(job.getId());
        return ok;
    }

    /**
     * 编辑&启停定时任务
     *
     * @throws SchedulerException
     */
    public boolean editAndScheduleJob(HDFSBackup quartzJob) throws SchedulerException {
        if (CommonConstant.STATUS_NORMAL.equals(quartzJob.getStatus())) {
            schedulerDelete(quartzJob.getId().trim());
            schedulerAdd(quartzJob.getCronExpression(), quartzJob.getValue().trim(), quartzJob.getId());
        } else {
            scheduler.pauseJob(JobKey.jobKey(quartzJob.getId().trim()));
        }
        return this.updateById(quartzJob);
    }

}