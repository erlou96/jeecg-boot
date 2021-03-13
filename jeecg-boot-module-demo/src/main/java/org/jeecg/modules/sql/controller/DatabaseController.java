package org.jeecg.modules.sql.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.entity.PageModel;
import org.jeecg.modules.entity.SqlDatabase;
import org.jeecg.modules.sql.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/database/sql")
public class DatabaseController {

    @Autowired
    private DatabaseService databaseService;

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public Result addConnect(@RequestBody SqlDatabase sqlDatabase, HttpServletRequest req) {


        Connection connection = databaseService.getConnect(sqlDatabase);
        if(connection!=null){
            log.info("连接成功！");
            databaseService.add(sqlDatabase);
            return Result.ok("连接成功");
        }else{
            log.error("连接失败,请重新检查配置");
            return Result.error("连接失败,请重新检查配置");
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result listAllByPage(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
    @RequestParam(name="pageSize", defaultValue="10") Integer pageSize){

        Result<IPage<SqlDatabase>> result = new Result<IPage<SqlDatabase>>();
        try {
            IPage<SqlDatabase> pageList = databaseService.listAllByPage(pageNo, pageSize);
            result.setSuccess(true);
            result.setResult(pageList);
            return result;
        }catch (Exception e){
            return Result.error("查询失败");
        }
    }

    @RequestMapping(value = "/listall", method = RequestMethod.GET)
    public Result listAll(){

        Result<List<SqlDatabase>> result = new Result<List<SqlDatabase>>();
        try {
            List list = databaseService.listAll();
            result.setSuccess(true);
            result.setResult(list);
            return result;
        }catch (Exception e){
            return Result.error("查询失败");
        }
    }

    @RequestMapping(value = "/connect",method = RequestMethod.DELETE)
    public Result deleteConnect(@RequestParam String strId){

        List idlist = Arrays.asList(strId.split(","));
        Integer res = databaseService.deleteBatch(idlist);
        return Result.ok("批量删除连接成功");
    }

    @RequestMapping(value = "/connect",method = RequestMethod.GET)
    public Result getConnect(@RequestParam String id){

        SqlDatabase sqlDatabase = databaseService.getConnectById(id);
        return Result.ok("查询连接成功");
    }

    @RequestMapping(value = "/jdbc/database",method = RequestMethod.GET)
    public Result getDatabases(@RequestParam String id){

        List<String> databases = null;
        SqlDatabase sqlDatabase = databaseService.getConnectById(id);
        databases = databaseService.getJDBCDdatabases(sqlDatabase);
        if(databases!=null){
            return Result.ok(databases);
        }else{
            return Result.error("查询库失败");
        }
    }

    @RequestMapping(value = "/jdbc/table",method = RequestMethod.GET)
    public Result getTables(@RequestParam String id,@RequestParam String database){

        List<String> tables = null;
        SqlDatabase sqlDatabase = databaseService.getConnectById(id);
        tables = databaseService.getJDBCDTalbe(sqlDatabase,database);
        if(tables!=null){
            return Result.ok(tables);
        }else{
            return Result.error("查询数据表失败");
        }
    }

    @RequestMapping(value = "/jdbc/tabledata",method = RequestMethod.GET)
    public Result getTableData(@RequestParam String id,@RequestParam String databasename,
                               @RequestParam String tablename,@RequestParam(defaultValue = "1") Integer pageNum,@RequestParam(defaultValue = "10") Integer pageSize){

        SqlDatabase sqlDatabase = databaseService.getConnectById(id);
        if (sqlDatabase.getDatabaseType().equals("Mysql")){
            log.info(sqlDatabase.getDatabaseDriveType());
            PageModel pageModel = databaseService.queryTableData(sqlDatabase, databasename, tablename, pageNum,pageSize);
            return Result.ok(pageModel);
        }else{
            List queryHiveTableData = databaseService.queryHiveTableData(sqlDatabase, databasename, tablename);
            return Result.ok(queryHiveTableData);
        }
    }

    @RequestMapping(value = "/jdbc/hivetabledata",method = RequestMethod.GET)
    public Result getHiveTableData(@RequestParam String id,@RequestParam String databasename,
                               @RequestParam String tablename){

        SqlDatabase sqlDatabase = databaseService.getConnectById(id);
        List queryHiveTableData = databaseService.queryHiveTableData(sqlDatabase, databasename, tablename);
        return Result.ok(queryHiveTableData);
    }

}
