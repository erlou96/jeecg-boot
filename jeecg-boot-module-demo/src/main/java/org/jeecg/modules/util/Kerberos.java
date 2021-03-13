package org.jeecg.modules.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;

public class Kerberos {

    public static void kerberos(){
        System.setProperty("java.security.krb5.conf", "D:\\krb5.conf");
        Configuration configuration = new Configuration();
        configuration.set("hadoop.security.authentication" , "Kerberos" );
        UserGroupInformation. setConfiguration(configuration);
        try {
            UserGroupInformation.loginUserFromKeytab("admin/admin@HADOOP.COM",
                    "D:\\admin.keytab");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
