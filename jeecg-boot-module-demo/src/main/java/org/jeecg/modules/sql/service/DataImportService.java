package org.jeecg.modules.sql.service;

import org.jeecg.modules.entity.SqlDatabase;
import org.jeecg.modules.util.DruidUtil;
import org.jeecg.modules.util.ExcelUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataImportService {
    public Integer ReadExcel(MultipartFile file, SqlDatabase sqlDatabase, String database, String tablename){

        ExcelUtil excelUtil = new ExcelUtil();
        List<List<String>> lists = new ArrayList<>();
        Integer size ;
        try {
            lists = excelUtil.getexcel(file);
            Connection conn = DruidUtil.getConnection(sqlDatabase,database);
            DruidUtil.InsertList(conn,tablename,lists);
            size = lists.size();
        }catch (Exception e){
            size = 0;
        }
        return size;
    }

}
