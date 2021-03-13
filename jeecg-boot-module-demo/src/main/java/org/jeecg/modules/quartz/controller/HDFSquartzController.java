package org.jeecg.modules.quartz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.quartz.entity.HDFSBackup;
import org.jeecg.modules.quartz.service.HDFSBackService;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/HDFSbBackup")
@Slf4j
public class HDFSquartzController {

    @Autowired
    public HDFSBackService hdfsBackService;
    @Autowired
    public Scheduler scheduler;
    /**
     * 查询HDFS备份策略
     */
    @RequestMapping(value = "/policy",method = RequestMethod.GET)
    public Result HDFSBackup(@RequestParam(name = "pageNo",defaultValue = "1") Integer pageNo,
                             @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){

        IPage HDFSBackupPolicyIPage = hdfsBackService.listAllByPage(pageNo, pageSize);
        return Result.ok(HDFSBackupPolicyIPage);
    }
    /**
     * 添加HDFS备份策略
     */
    @RequestMapping(value = "/policy",method = RequestMethod.POST)
    public Result addHDFSBackup(@RequestBody HDFSBackup hdfsBackup){

        List<HDFSBackup> list = hdfsBackService.findByHDFSValue(hdfsBackup.getValue());
        if(list != null && list.size() >0 ){
            return Result.error("选择备份的HDFS文件已经存在策略");
        }
        hdfsBackService.addPolicy(hdfsBackup);
        return Result.ok();
    }
    /**
     * 暂停定时任务
     * @param id hdfs备份策略的id
     * @return
     */
    @RequestMapping(value = "/pause",method = RequestMethod.GET)
    public Result<Object> pauseJob(@RequestParam String id) {
        log.info("暂停定时任务"+id);
        HDFSBackup job = null;
        //根据id获取到对应的HDFS备份策略
        try{
            job = hdfsBackService.getById(id);
            if (job == null){
                return Result.error("没有这个备份策略！");
            }
            //暂停trigger的name和id一致的触发器
            scheduler.pauseTrigger(TriggerKey.triggerKey(job.getValue()));
        }catch (SchedulerException e) {
            throw new JeecgBootException("暂停定时任务失败!");
        }
        //修改hdfs备份策略的运行状态，更新
        job.setStatus(CommonConstant.STATUS_DISABLE);
        hdfsBackService.updateById(job);
        return Result.ok("暂停定时任务成功");
    }

    /**
     * 启动定时任务
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/resume",method = RequestMethod.GET)
    public Result<Object> resumeJob(@RequestParam String id) {
        HDFSBackup job = hdfsBackService.getById(id);
        if (job == null) {
            return Result.error("定时任务不存在！");
        }
        hdfsBackService.resumeJob(job);
        //scheduler.resumeJob(JobKey.jobKey(job.getJobClassName().trim()));
        return Result.ok("恢复定时任务成功");
    }
    /**
     * 更新定时任务
     *
     * @param quartzJob
     * @return
     */
    @RequestMapping(value = "/policy", method = RequestMethod.PUT)
    public Result<?> eidt(@RequestBody HDFSBackup quartzJob) {
        try {
            hdfsBackService.editAndScheduleJob(quartzJob);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return Result.error("更新定时任务失败!");
        }
        return Result.ok("更新定时任务成功!");
    }
    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/policy", method = RequestMethod.DELETE)
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        HDFSBackup job = hdfsBackService.getById(id);
        if (job == null) {
            return Result.error("未找到对应实体");
        }
        hdfsBackService.deleteAndStopJob(job);
        return Result.ok("删除成功!");

    }

}
