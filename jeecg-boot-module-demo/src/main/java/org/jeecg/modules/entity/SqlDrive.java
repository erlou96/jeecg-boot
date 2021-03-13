package org.jeecg.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "sql_drive")
@Data
public class SqlDrive implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ID_WORKER_STR)
    private String id;
    private String name;
    private String databaseType;
    private String databaseName;
    private String drive;
    private String url;
    private String user;
    private String password;
    @Excel(name = "删除状态", width = 15,dicCode="del_flag")
    @TableLogic
    private String delFlag;
    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;



}
