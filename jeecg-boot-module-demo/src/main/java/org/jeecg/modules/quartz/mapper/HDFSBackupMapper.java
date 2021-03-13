package org.jeecg.modules.quartz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.quartz.entity.HDFSBackup;

import java.util.List;

public interface HDFSBackupMapper extends BaseMapper<HDFSBackup>{
    public List findByHDFSValue(@Param("value")String value);
}
