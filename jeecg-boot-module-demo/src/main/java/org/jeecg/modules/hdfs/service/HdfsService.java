package org.jeecg.modules.hdfs.service;


import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.client.HdfsDataInputStream;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.security.UserGroupInformation;

import org.jeecg.modules.entity.HdfsFileInformation;
import org.jeecg.modules.entity.HdfsFstatus;
import org.jeecg.modules.entity.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class HdfsService {

    /**
     * 配置文件的绝对路径
     */

    public final String hadoopBasePath = "jeecg-boot-module-demo/src/main/resources/hdfs/";
    private static String CONFIG_PATH = "jeecg-boot-module-demo/src/main/resources/hdfs/";

    private static final String PRINCIPAL = "username.client.kerberos.principal";
    private static final String KEYTAB = "username.client.keytab.file";
    private static final String KRBFILE = "java.security.krb5.conf";

    private static String HDFS_SITE_PATH = CONFIG_PATH+"hdfs-site.xml";
    private static String CORE_SITE_PATH = CONFIG_PATH+"core-site.xml";
    private static String USER_KEYTAB_PATH = CONFIG_PATH+"admin.keytab";
    private static String KRB5_CONF_PATH = CONFIG_PATH+"krb5.conf";
    private static Configuration conf;
    private static FileSystem fileSystem;
    private static DistributedFileSystem distributedFileSystem;
    private static String PRNCIPAL_NAME = "admin/admin@HADOOP.COM";

    /**
     * 初始化，获取一个FileSystem实例
     * @throws IOException
     */
    public void init()  {


        try {
            confLoad();
        }catch (IOException ioe){
            log.error("加载配置文件失败");
        }
//        try {
//            authentication();
//        }catch (IOException ioee){
//            log.error("kerberos认证失败");
//        }
        try {
            instanceBuild();
        }catch (IOException e){
            log.error("创建实例失败，请检查配置");
        }

    }
    /**
     *
     * Add configuration file）
     */
    public  void confLoad() throws IOException {
        conf = new Configuration();
        conf.addResource(new Path(HDFS_SITE_PATH));
        conf.addResource(new Path(CORE_SITE_PATH));
    }

    /**
     * kerberos security authentication
     */
    public  void authentication() throws IOException {
        System.out.println("li"+conf.get("hadoop.security.authentication"));
        if ("kerberos".equalsIgnoreCase(conf.get("hadoop.security.authentication"))) {
            System.setProperty("java.security.krb5.conf",KRB5_CONF_PATH);
           // System.setProperty("hadoop.home.dir","/opt/client/HDFS/hadoop");
            System.setProperty("sun.security.krb5.debug", "true");
            System.setProperty("https.protocols", "TLSv1.1,TLSv1.2");
            System.setProperty("jdk.tls.client.protocols", "TLSv1.1,TLSv1.2");

            System.out.println("https.protocols == "+ System.getProperty("https.protocols"));
            System.out.println("jdk.tls.client.protocols == "+ System.getProperty("jdk.tls.client.protocols"));
            conf.set(PRINCIPAL, PRNCIPAL_NAME);
            conf.set(KEYTAB, USER_KEYTAB_PATH);
            conf.set("hdfs.connection.timeout","5000");
            System.setProperty(KRBFILE, KRB5_CONF_PATH);
            UserGroupInformation.setConfiguration(conf);
            try {
                UserGroupInformation.loginUserFromKeytab(conf.get(PRINCIPAL), conf.get(KEYTAB));
                System.out.println("Login success!!!!!!!!!!!!!!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * build HDFS instance
     */
    public FileSystem instanceBuild() throws IOException {
        fileSystem = FileSystem.get(conf);
        return fileSystem;
    }

    /**
     * 往服务器上传文件
     */
    public Boolean saveFile(MultipartFile multipartFile){
        try {
            String fileOriginalFilename = multipartFile.getOriginalFilename();
            log.info(fileOriginalFilename);
            String filepath = "D://upload/"+fileOriginalFilename;
            multipartFile.transferTo(new File(filepath));
            System.out.println("filepath"+filepath);
            return true;
        }
        catch (Exception e){
            log.error("上传服务器失败");
            return false;
        }
    }
    /**
     * 往hdfs上传文件
     */
    public Boolean addFileToHdfs(String localPath, String dfsPath) throws Exception {
        Path src = new Path(localPath);
        //boolean isExists = fileSystem.exists(new Path(hadoopBasePath));
        boolean isExists = fileSystem.exists(new Path(dfsPath));
        if(!isExists){
            fileSystem.mkdirs(new Path(dfsPath));
            fileSystem.copyFromLocalFile(src, new Path(dfsPath));
            fileSystem.close();
            return true;
        }else {
            fileSystem.copyFromLocalFile(src, new Path(dfsPath));
            fileSystem.close();
            return false;
        }
        //Path dst = new Path(dfsPath+ File.separator +fileName);
    }

    /**
     * 从hdfs中复制文件到本地文件系统
     */
//    public void downloadFileToLocal(String resultPath) throws IllegalArgumentException, IOException {
//        fileSystem.copyToLocalFile(false,new Path(resultPath), new Path("D://download"),true);
//        fileSystem.close();
//    }

    public InputStream downloadFileToLocal(String resultPath)throws IOException{

        log.info(resultPath);
        FSDataInputStream in = fileSystem.open(new Path(resultPath));
        return in;
    }

    /**
     * 删除文件
     */
    public boolean delete(String filePath) throws IOException {

        Path path = new Path(filePath);
        //boolean isok = fileSystem.deleteOnExit(path);
        boolean isok = fileSystem.delete(path,true);
        fileSystem.close();
        log.info("删除"+filePath+"成功");
        return isok;
    }

    /**
     * 查看目录信息，只显示文件
     */
    public void listFiles() throws FileNotFoundException, IllegalArgumentException, IOException {
        System.out.println("--------------查看目录信息，只显示文件--------------");
        RemoteIterator<LocatedFileStatus> listFiles = fileSystem.listFiles(new Path("/data"), true);
        while (listFiles.hasNext()) {
            LocatedFileStatus fileStatus = listFiles.next();
            System.out.println(fileStatus.getPath().getName());
            System.out.println("fileStatus.getBlock");
            System.out.println(fileStatus.getAccessTime());
            System.out.println(fileStatus.getBlockSize());
            System.out.println(fileStatus.getPermission());
            System.out.println(fileStatus.getLen());
            BlockLocation[] blockLocations = fileStatus.getBlockLocations();
            for (BlockLocation bl : blockLocations) {
                System.out.println("block-length:" + bl.getLength() + "--" + "block-offset:" + bl.getOffset());
                String[] hosts = bl.getHosts();
                for (String host : hosts) {
                    System.out.println(host);
                }
            }
            System.out.println("--------------分割线--------------");
        }
    }

    /**
     * 查看具体某一个文件信息
     */

    public List<HdfsFileInformation> listOneFile(String path) throws FileNotFoundException, IllegalArgumentException, IOException {

        System.out.println("--------------查看某一个具体文件信息--------------");
        List<HdfsFileInformation> listHdfsFileInformation = new ArrayList<>();
        HdfsDataInputStream hdfs= (HdfsDataInputStream) fileSystem.open(new Path(path));
        List<LocatedBlock> allBlocks= hdfs.getAllBlocks();
        allBlocks.size();
        for(LocatedBlock block:allBlocks) {
            HdfsFileInformation hdfsFileInformation = new HdfsFileInformation();
            ExtendedBlock eBlock = block.getBlock();
            // 获取当前的数据块所在的DataNode的信息
            DatanodeInfo[] locations =
                    block.getLocations();
            List<String> datainfo = new ArrayList<>();
            for (DatanodeInfo info : locations) {
                datainfo.add(info.getHostName());
            }
            hdfsFileInformation.setBlockID(eBlock.getBlockId());
            hdfsFileInformation.setBlockPoolId(eBlock.getBlockPoolId());
            hdfsFileInformation.setBlockName(eBlock.getBlockName());
            hdfsFileInformation.setBlockSize(block.getBlockSize());
            hdfsFileInformation.setGenerationStamp(eBlock.getGenerationStamp());
            hdfsFileInformation.setHostName(datainfo);
            listHdfsFileInformation.add(hdfsFileInformation);
        }
        return listHdfsFileInformation;
    }

    /**
     * 查看文件及文件夹信息
     */
    public void listAll() throws FileNotFoundException, IllegalArgumentException, IOException {
        System.out.println("--------------查看文件及文件夹信息--------------");
        FileStatus[] listStatus = fileSystem.listStatus(new Path("/"));

        String flag = "d--             ";
        for (FileStatus fstatus : listStatus) {
            System.out.println(fstatus.getAccessTime());
            System.out.println(fstatus.getReplication());
            System.out.println(fstatus.getPermission());
            System.out.println(fstatus.getOwner());
            if (fstatus.isFile())
                flag = "f--         ";
            System.out.println(flag + fstatus.getPath().getName());
        }
    }


    /***
     * 查看指定路径的文件及文件夹信息
     * @throws FileNotFoundException
     * @throws IllegalArgumentException
     * @throws IOException
     *
     */
    public List listPath(String path) throws FileNotFoundException, IllegalArgumentException, IOException {

        List<HdfsFstatus> listFstatus = new ArrayList<>();
        System.out.println("--------------查看文件及文件夹信息--------------");
        try {
            FileStatus[] listStatus = fileSystem.listStatus(new Path(path));

            for (FileStatus fstatus : listStatus) {
                HdfsFstatus hdfsFstatus = new HdfsFstatus();
//                System.out.println(fstatus.getAccessTime());
//                System.out.println(fstatus.getReplication());
//                System.out.println(fstatus.getPermission());
//                System.out.println(fstatus.getOwner());
                hdfsFstatus.setFile(false);
                if (fstatus.isFile()){
                    hdfsFstatus.setFile(true);
                }
                System.out.println(fstatus.getPermission()+"      "+fstatus.getPath().getName());
                hdfsFstatus.setReplication(fstatus.getReplication());
                hdfsFstatus.setGroup(fstatus.getGroup());
                hdfsFstatus.setSize(fstatus.getLen());
                hdfsFstatus.setBlockSize(fstatus.getBlockSize());
                hdfsFstatus.setName(fstatus.getPath().getName());
                hdfsFstatus.setLastModified(fstatus.getModificationTime());
                hdfsFstatus.setPath(fstatus.getPath().toString());
                hdfsFstatus.setOwner(fstatus.getOwner());
                hdfsFstatus.setPermission(fstatus.getPermission().toString());
                listFstatus.add(hdfsFstatus);
            }
        }catch (Exception e){
            log.error("指定路径错误");
        }
        return listFstatus;
    }

    /***
     * 判断前端发送来的路径是文件还是目录
     * true 文件
     * false 目录
     * @param path
     * @return
     * @throws IOException
     */
    public boolean isFile(String path) throws IOException {

        return fileSystem.isFile(new Path(path));
    }
    /***
     *
     * @param path  前端请求访问的文件或路径
     * @param num   当前页，默认1
     * @param pageSize 每页展示的条数
     * @return 返回带HdfsFstatus的PageModel结果
     * @throws FileNotFoundException
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public PageModel listPathbyPage(String path, Integer num, Integer pageSize) throws FileNotFoundException, IllegalArgumentException, IOException {

        PageModel pageModel = new PageModel();
        try {
            List<HdfsFstatus> listFstatus = new ArrayList<>();
            log.info("--------------listPathbyPage查看文件及文件夹信息--------------");
            FileStatus[] listStatus = fileSystem.listStatus(new Path(path));
            int count = listStatus.length;
            int pages = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
            System.out.println(pages);
            System.out.println(count);
            for (int i = (num - 1) * pageSize; i < num * pageSize; i++) {
                HdfsFstatus hdfsFstatus = new HdfsFstatus();
                System.out.println(listStatus[i].getPath().getName());
                hdfsFstatus.setReplication(listStatus[i].getReplication());
                hdfsFstatus.setGroup(listStatus[i].getGroup());
                hdfsFstatus.setSize(listStatus[i].getLen());
                hdfsFstatus.setBlockSize(listStatus[i].getBlockSize());
                hdfsFstatus.setName(listStatus[i].getPath().getName());
                hdfsFstatus.setLastModified(listStatus[i].getModificationTime());
                hdfsFstatus.setPath(listStatus[i].getPath().toString());
                hdfsFstatus.setOwner(listStatus[i].getOwner());
                hdfsFstatus.setPermission(listStatus[i].getPermission().toString());
                listFstatus.add(hdfsFstatus);
            }
            pageModel.setPageSize(pageSize);
            pageModel.setTotalRecords(count);
            pageModel.setPageNo(num);
            pageModel.setList(listFstatus);
            System.out.println("df");
        } catch (Exception e) {
            log.error("指定路径错误");
        }
            return pageModel;
    }

    public static void main(String[] args) throws Exception {
        HdfsService hdfsCron = new HdfsService();
        hdfsCron.init();
        //hdfsCron.listAll();
        //hdfsCron.listFiles();
//        List<HdfsFstatus> list = hdfsCron.listPath("/data");
//        for (HdfsFstatus hdfsFstatus: list) {
//            System.out.println(hdfsFstatus.toString());
//        }
        //hdfsCron.listFiles();
        //hdfsCron.listOneFile("/data/wiki");
        //hdfsCron.listPathbyPage("/",2,5);
        //hdfsCron.addFileToHdfs("E:\\ht706\\docker.txt","hdfs://hadoop/");
        //hdfsCron.delete("hdfs://hadoop/test5");
        //hdfsCron.listPath("hdfs://hadoop/");
        hdfsCron.downloadFileToLocal("hdfs://hadoop/jeecg-boot.zip");
    }

    public Configuration initKerberosENV() throws Exception {
        Configuration conf = new Configuration();
        conf.addResource(new Path("D:/hdfs-site.xml"));
        conf.addResource(new Path("D:/core-site.xml"));
        String keytab = "D:/admin.keytab";
        String user = "admin/admin";
        System.setProperty("java.security.krb5.conf", "D:/krb5.conf");
        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation.loginUserFromKeytab(user, keytab);
        return conf;
    }

    /**
     * HDFS之间的文件备份
     * @param conf
     * @param path hdfs全路径
     * @throws IOException
     */
    public Boolean backup(Configuration conf,String path) {
        try {
            FileSystem fs = FileSystem.get(conf);
            Path spath = new Path(path);
            String sourcename = spath.getName();
            String sourceparent = path.substring(0,path.lastIndexOf(sourcename));
            String pingpath = sourceparent.replaceFirst("hdfs://hadoop/","");
            System.out.println(sourcename+"======"+sourceparent+"====="+pingpath);
            if(fs.exists(spath)){
                Path dpath = new Path("hdfs://hadoop/backup/"+pingpath+sourcename);
                boolean copy = FileUtil.copy(fs, spath, fs, dpath, false, conf);
                return copy;
            }else {
                return false;
            }
        }catch (IOException e){
            return false;
        }
    }
}
