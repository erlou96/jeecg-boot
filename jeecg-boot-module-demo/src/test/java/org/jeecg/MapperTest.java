package org.jeecg;

import java.sql.*;

public class MapperTest {

    private static String JDBC_DRIVER = "org.apache.hive.jdbc.HiveDriver";
    private static String CONNECTION_URL ="jdbc:hive2://node4:10000/;principal=hive/node4@HADOOP.COM";

    static {
        try {
            Class.forName(JDBC_DRIVER);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

        public static void main(String[] args) {
            //创建驱动名称，这里我们使用的是odbc
            String driverName="sun.jdbc.odbc.JdbcOdbcDriver";
            //创建odbc的名字
            String odbcName="jdbc:odbc:MySql";
            //用户名
            String userName="root";
            //密码
            String password="123123";
            //创建一个连接对象
            Connection conn=null;
            //sql语句执行对象
            Statement st=null;
            //结果集对象
            ResultSet rs=null;
            String sql="select * from jeecg_boot.";
            //1.加载启动程序
            try {
                Class.forName(driverName);
                conn= DriverManager.getConnection(odbcName, userName, password);
                System.out.println("连接成功！！");
                st=conn.createStatement();
                rs=st.executeQuery(sql);
                System.out.println("id\t登录名");
                while(rs.next()){
                    System.out.println("id:"+rs.getInt(1)+"\t"+rs.getString(2));
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }finally{
                try {
                    if(rs!=null){
                        rs.close();
                    }
                    if(st!=null){
                        st.close();
                    }
                    if(conn!=null){
                        conn.close();
                    }
                    System.out.println("关闭成功！！");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }


//        System.setProperty("java.security.krb5.conf", "D:\\krb5.conf");
//
//        Configuration configuration = new Configuration();
//        configuration.set("hadoop.security.authentication" , "Kerberos" );
//        UserGroupInformation. setConfiguration(configuration);
//        UserGroupInformation.loginUserFromKeytab("admin/admin@HADOOP.COM",
//                "D:\\admin.keytab");
//        SqlDatabase sqlDatabase = new SqlDatabase();
//        sqlDatabase.setDatabaseName("test2");
//        sqlDatabase.setConnectName("t");
//        sqlDatabase.setDatabaseType("hive2");
//        sqlDatabase.setIp("192.168.17.5");
//        sqlDatabase.setPort("10000");
//        sqlDatabase.setUsername("admin");
//        sqlDatabase.setDatabaseDriveType("JDBC");
////        Connection connection = DruidUtil.getConnection(sqlDatabase);
//////        List query = DruidUtil.selectAllQuery(connection, "student");
//        DatabaseService ds = new DatabaseService();
//        List query = ds.queryHiveTableData(sqlDatabase, "test2", "student");
//        System.out.println(query.size());
//        Class.forName(JDBC_DRIVER);
//
//        //登录Kerberos账号

//
//        Connection connection = null;
//        ResultSet rs = null;
//        PreparedStatement ps = null;
//        try {
//            connection = DriverManager.getConnection(CONNECTION_URL);
//            ps = connection.prepareStatement("select * from test2.student");
//            rs = ps.executeQuery();
//            ResultSetMetaData data = rs.getMetaData();
//            List<String> filed = new ArrayList<String>();
//
//            while (rs.next()) {
//                for (int i = 1; i <= data.getColumnCount(); i++) {
//                    System.out.println(data.getColumnName(i));
//                    filed.add(data.getColumnName(i));
//                }
//                break;
//            }
//            System.out.println(filed.size());
//            while (rs.next()) {
//                HashMap<String, Object> queryOneResult = new HashMap<>();
//                for (int i = 1;i <= filed.size();i++ ){
//                    String queryResult = rs.getString(i);
//                    queryOneResult.put(filed.get(i-1),queryResult);
//                System.out.println(rs.getString(1));
//            }
//                for (String key : queryOneResult.keySet()) {
//                    System.out.println("key= "+ key + " and value= " + queryOneResult.get(key));
//                }
//
//            }
//
//    }catch (Exception e){
//        }
    }
}