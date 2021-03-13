package org.jeecg.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class SqlDatabase implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ID_WORKER_STR)
    private String id;
    private String connectName;
    private String databaseType;
    private String databaseDriveType;
    private String databaseName;
    private String username;
    private String password;
    private String ip;
    private String port;

}
