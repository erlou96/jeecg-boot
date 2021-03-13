package org.jeecg.modules.sql.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.entity.PageModel;
import org.jeecg.modules.entity.SqlDatabase;
import org.jeecg.modules.mapper.SqlDatabaseMapper;
import org.jeecg.modules.util.DruidUtil;
import org.jeecg.modules.util.Kerberos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

@Service
@Slf4j
public class DatabaseService {

    @Autowired
    private SqlDatabaseMapper sqlDatabaseMapper;
    private Connection conn=null; //定义一个MYSQL链接对象
    private Statement stmt = null;
    private PreparedStatement ps =null;
    private ResultSet rs = null;

    public Connection getConnect(SqlDatabase sqlDatabase){

        conn = DruidUtil.getConnection(sqlDatabase);
        return conn;
    }

    public void add(SqlDatabase sqlDatabase){

        sqlDatabaseMapper.insert(sqlDatabase);
    }

    public IPage listAllByPage(int pageNo,int pageSize){

        IPage<SqlDatabase> sqlDatabaseIPage = sqlDatabaseMapper.selectPage(new Page<SqlDatabase>(pageNo, pageSize), null);
        return sqlDatabaseIPage;
    }

    public List listAll(){
        List<SqlDatabase> sqlDatabases = sqlDatabaseMapper.selectList(null);
        return sqlDatabases;
    }

    public Integer deleteBatch(List idlist){
        int result = sqlDatabaseMapper.deleteBatchIds(idlist);
        log.info("影响了"+result+"条数据");
        return result;
    }

    public SqlDatabase getConnectById(String id){
        SqlDatabase sqlDatabase = sqlDatabaseMapper.selectById(id);
        log.info(sqlDatabase.getConnectName());
        log.info(sqlDatabase.getId());
        return sqlDatabase;
    }

    public Connection getConnectByIdDatabaseName(SqlDatabase sqlDatabase,String database){

        Connection conn = DruidUtil.getConnection(sqlDatabase,database);
        return conn;
    }
    public List getJDBCDdatabases(SqlDatabase sqlDatabase){

        Connection conn = DruidUtil.getConnection(sqlDatabase);
        List<String> dblist = DruidUtil.DBlist(conn);
        return dblist;
    }

    public List<String> getJDBCDTalbe(SqlDatabase sqlDatabase,String database) {

        Connection conn = DruidUtil.getConnection(sqlDatabase,database);
        List<String> tblist = DruidUtil.TBlist(conn,sqlDatabase.getDatabaseType(),database);
        return tblist;
    }

    public PageModel queryTableData(SqlDatabase sqlDatabase,String databasename,String tablename,Integer pageNum,Integer pageSize){

        Connection conn = DruidUtil.getConnection(sqlDatabase,databasename);
        PageModel pageModel = DruidUtil.selectAllQueryByPage(conn,tablename,pageNum,pageSize);
        return pageModel;
    }

    public List queryHiveTableData(SqlDatabase sqlDatabase,String databasename,String tablename){

        Kerberos.kerberos();
        Connection conn = DruidUtil.getConnection(sqlDatabase,databasename);
        List queryList = DruidUtil.selectAllQuery(conn,tablename);
        return queryList;
    }

}
