package org.jeecg.modules.hdfs.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.entity.HdfsFileInformation;
import org.jeecg.modules.entity.HdfsFstatus;
import org.jeecg.modules.hdfs.service.HdfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/hdfs")
public class HdfsController {

    @Autowired
    @Resource
    public HdfsService hdfsService;

    /***
     * 带分页查询的默认查询
     *
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/list")
    public Result listPath(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, @RequestParam(defaultValue = "/") String path, HttpServletRequest req) {

        hdfsService.init();
        try {
            List<HdfsFstatus> listHdfsInfo = hdfsService.listPath(path);
           return Result.ok(listHdfsInfo);

        } catch (IOException e) {
            return Result.error("hdfs查询错误");
        }
    }

    /**
     *
     * @param
     * @param path
     * @param req
     * @return
     */
    @RequestMapping(value = "/listByTree")
    public Result listPath(@RequestParam(defaultValue = "/") String path, HttpServletRequest req) {

        hdfsService.init();
        try {
            List<HdfsFstatus> listHdfsInfo = hdfsService.listPath(path);
            for (HdfsFstatus hf:listHdfsInfo){
                if(hf.getFile()){

                }
            }
            return Result.ok(listHdfsInfo);

        } catch (IOException e) {
            return Result.error("hdfs查询错误");
        }
    }

    @RequestMapping(value = "/fileInformation")
    public Result listPathByTree(@RequestParam(defaultValue = "/") String path, HttpServletRequest req) {

        hdfsService.init();
        try {
            List<HdfsFileInformation> listHdfsFileInfo = hdfsService.listOneFile(path);
            return Result.ok(listHdfsFileInfo);
        } catch (IOException e) {
            return Result.error("hdfs文件信息查询错误");
        }
    }

    @RequestMapping(value = "/hdfsupload")
    public Result hdfsupload(@RequestParam String localpath, @RequestParam(defaultValue = "/") String dfspath, HttpServletRequest req) {

        hdfsService.init();

        try {
            log.info("success");
            Boolean uploadSuccess = hdfsService.addFileToHdfs(localpath,dfspath);
            return Result.ok("success");

        } catch (Exception e) {
            log.info(localpath);
            return Result.error("error");
        }
    }

    @RequestMapping(value = "/saveFile",method = RequestMethod.POST)
    public Result saveFile(@RequestParam String path, HttpServletRequest request) throws IOException{
        MultipartHttpServletRequest multipartRequest=(MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartRequest.getFile("file");//file是form-data中二进制字段对应的name
        log.info(multipartFile.getName());
        log.info(multipartFile.getOriginalFilename());
        System.out.println(multipartFile.getSize());
        Boolean uploading = hdfsService.saveFile(multipartFile);
        String fileOriginalFilename = multipartFile.getOriginalFilename();
        String filepath = "D://upload/"+fileOriginalFilename;
        //String dfspath = path+fileOriginalFilename;
        if (uploading){
            try {
                hdfsService.addFileToHdfs(filepath,path);
                return Result.ok("文件上传服务器成功");
            }catch (Exception e){
                return Result.error("文件上传失败");
            }

        }
        return Result.error("文件上传失败");

    }

    @RequestMapping(value = "/downloadFile",method = RequestMethod.GET)
    public void downloadFile(@RequestParam String path, HttpServletRequest request,HttpServletResponse response) throws IOException{

        try {
            InputStream in = hdfsService.downloadFileToLocal(path);
            String filename = path.substring(path.lastIndexOf("/")+1,path.length());
            log.info(filename);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode(filename,"UTF-8"));
            OutputStream out = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while((len=in.read(buffer))!=-1){
                out.write(buffer,0,len);
            }
            in.close();
            out.close();
        }catch (Exception e){
            System.out.println(e);
        }


    }

    @RequestMapping(value = "/delete")
    public Result delete(@RequestParam String dfspath, HttpServletRequest request) {

        try {
            log.info("success");
            Boolean isok = hdfsService.delete(dfspath);
            if (isok){
                return Result.ok("success");
            }else {
                return Result.error("error");
            }
        } catch (Exception e) {
            return Result.error("error");
        }
    }

    @RequestMapping(value = "/backup")
    public Result backup(@RequestParam String dfspath, HttpServletRequest request) {

        try {
            Configuration configuration = hdfsService.initKerberosENV();
            Boolean backup = hdfsService.backup(configuration, dfspath);
            if (backup){
                return Result.ok("文件备份成功！");
            }else{
                return Result.error("文件备份失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("文件备份失败！");
        }
    }
}

