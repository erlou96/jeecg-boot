package org.jeecg.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.entity.SqlDatabase;

import java.util.List;

public interface SqlDatabaseMapper extends BaseMapper<SqlDatabase> {

    public List listAll();
}
