package org.jeecg.modules.sql.controller;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.entity.SqlDatabase;
import org.jeecg.modules.sql.service.DataImportService;
import org.jeecg.modules.sql.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping(value = "/database/sql/import")
public class DataImportController {

    @Autowired
    private DataImportService dataImportService;
    @Autowired
    private DatabaseService databaseService;

    @RequestMapping(value = "/excel",method = RequestMethod.POST)
    public Result uploadExcelFile(@RequestParam String id, @RequestParam String database, @RequestParam String tablename,HttpServletRequest request) throws Exception {

        MultipartHttpServletRequest multipartRequest=(MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");//file是form-data中二进制字段对应的name
        SqlDatabase sqlDatabase = databaseService.getConnectById(id);
        log.info(file.getName()+"-----"+file.getOriginalFilename()+"+++"+file.getSize());
        String filename = file.getOriginalFilename();
        String fileType = filename.split("\\.")[1];
        if("xls".equals(fileType)||"xlsx".equals(fileType)){
            Integer nums = dataImportService.ReadExcel(file, sqlDatabase, database, tablename);
                return Result.ok(nums);
        }else {
            return Result.error("上传文件格式错误，请重新上传文件");
        }
    }
}
