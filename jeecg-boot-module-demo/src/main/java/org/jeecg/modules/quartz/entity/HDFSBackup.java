package org.jeecg.modules.quartz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

@Data
@TableName("hdfs_backup_policy")
public class HDFSBackup implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ID_WORKER_STR)
    private String id;
    /**HDFS路径*/
    private String path;
    /**HDFS文件/文件夹 0-文件 1-文件夹*/
    private java.lang.Integer fileStatus;
    /**HDFS文件名*/
    private String name;
    /**路径+文件名*/
    private String value;
    /**备份周期*/
    private java.lang.String backupCycle;
    /**cron表达式*/
    private java.lang.String cronExpression;
    /**删除状态*/
    private java.lang.Integer delFlag;
    /**状态 0正常 -1停止*/
    private java.lang.Integer status;
    /**创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private java.util.Date createTime;
    /**修改时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private java.util.Date updateTime;
    /**描述*/
    private java.lang.String description;
}

