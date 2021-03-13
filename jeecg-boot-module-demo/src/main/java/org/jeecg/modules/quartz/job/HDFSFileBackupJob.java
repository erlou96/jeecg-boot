package org.jeecg.modules.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class HDFSFileBackupJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        //获取hdfs文件path参数
        String path = jobExecutionContext.getMergedJobDataMap().getString("parameter");
        String printTime = new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date());
        log.info(path);
        System.out.println("HDFS文件备份 start at:" + printTime + ", 开始备份: ~~~~"+jobExecutionContext.getTrigger().getJobKey().getName());
        try {
            Configuration configuration = initKerberosENV();
            Boolean backup = backup(configuration, path);
            if (backup){
                System.out.println("HDFS文件备份完成:" + new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date()));
            }else{
                System.out.println("HDFS文件备份失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * HDFS kerberos认证
     * @param
     * @return
     * @throws Exception
     */
    public static Configuration initKerberosENV() throws Exception {
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

    public static void listDir(Configuration conf, String dir) throws IOException {
        FileSystem fs = FileSystem.get(conf);
        FileStatus files[] = fs.listStatus(new Path(dir));
        for (FileStatus file : files) {
            System.out.println(file.getPath());
        }
    }

    /**
     * HDFS之间的文件备份
     * @param conf
     * @param path hdfs全路径
     * @throws IOException
     */
    public static Boolean backup(Configuration conf,String path) {
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
    public static void main(String[] args) {
        String user = "admin/admin";
        String keytab = "D:/admin.keytab";
        String dir = "hdfs://hadoop/cp";
        try {
            Configuration configuration = initKerberosENV();
            backup(configuration,"hdfs://hadoop/user/admin/");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
