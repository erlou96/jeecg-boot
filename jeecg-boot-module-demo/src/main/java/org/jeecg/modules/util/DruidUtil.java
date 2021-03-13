package org.jeecg.modules.util;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.entity.PageModel;
import org.jeecg.modules.entity.SqlDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class DruidUtil {


    static Statement stmt = null;
    static PreparedStatement ps =null;
    static ResultSet rs = null;
    final static String MYSQLJDBCDRIVE = "com.mysql.jdbc.Driver";
    final static String HIVEJDBCDRIVE = "org.apache.hive.jdbc.HiveDriver";
    final static String MYSQLJDBCURL = "jdbc:mysql://";
    final static String HIVEJDBCURL = "jdbc:hive2://";
    final static String PRINCIPAL = ";principal=hive/node5@HADOOP.COM";

    private DruidUtil() {
    }

    //获取连接池
    public static Connection open(SqlDatabase sqlDatabase) {
        Connection conn = null;

        DruidDataSource dataSource = new DruidDataSource();
        //千万要注意
        dataSource.setConnectionErrorRetryAttempts(0);
        dataSource.setFailFast(true);
        dataSource.setTestOnBorrow(true);
        dataSource.setMaxWait(500);
        dataSource.setBreakAfterAcquireFailure(true);
        String databaseType = sqlDatabase.getDatabaseType();
        String databaseDriveType = sqlDatabase.getDatabaseDriveType();
        String databaseName = sqlDatabase.getDatabaseName();
        String ip = sqlDatabase.getIp();
        String port = sqlDatabase.getPort();
        String username = sqlDatabase.getUsername();
        String password = sqlDatabase.getPassword();
        String url = ip+":"+port+"/"+databaseName;
        log.info(databaseDriveType+"---"+databaseType);
        if (databaseType.equals("Mysql") && databaseDriveType.equals("JDBC")) {
            dataSource.setDriverClassName(MYSQLJDBCDRIVE);
            dataSource.setUrl(MYSQLJDBCURL+url);
        }else if (databaseType.equals("hive2") && databaseDriveType.equals("JDBC")){
            Kerberos.kerberos();
            dataSource.setDriverClassName(HIVEJDBCDRIVE);
            dataSource.setUrl(HIVEJDBCURL+url+PRINCIPAL);
        } else{
            return null;
        }
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        log.info(dataSource.getUrl());
        log.info(dataSource.getDriverClassName());
        //加载驱动
        try {
            conn = dataSource.getConnection(500); //获取连接

            return conn;
        } catch (SQLException e) { //配置有误，关闭连接池
            if(conn!=null){
                try {
                    conn.close();
                    dataSource.close();
                } catch (SQLException ee) {
                    conn = null;
                    dataSource.close();
                }
            }
            return null;
        }catch (Exception e){

            dataSource.close();
            return null;
        }
    }

    /**
     * 创建数据库连接实例
     * @return 数据库连接实例 connection
     */
    public static Connection getConnection(SqlDatabase sqlDatabase) {

        Connection conn = null;
        try {
            if(conn==null ||conn.isClosed())
                conn = open(sqlDatabase);
        } catch (SQLException e) {
            try {
                conn.close();
            }catch (SQLException e1){
                conn = null;
            }
        }
        return conn;
    }

    /**
     * 创建数据库连接实例
     * @return 数据库连接实例 connection
     */
    public static Connection getConnection(SqlDatabase sqlDatabase, String database) {

        Connection conn = null;
        sqlDatabase.setDatabaseName(database);
        try {
            if(conn==null ||conn.isClosed())
                conn = open(sqlDatabase);
        } catch (SQLException e) {
            e.printStackTrace();
            try{
                conn.close();
            }catch (Exception e1){
                conn = null;
            }
        }
        return conn;
    }
//
//    /**
//     * 释放数据库连接 connection 到数据库缓存池，并关闭 rSet 和 pStatement 资源
//     * @param rSet 数据库处理结果集
//     * @param pStatement 数据库操作语句
//     * @param connection 数据库连接对象
//     */
//    public static void releaseSqlConnection(ResultSet rSet, PreparedStatement pStatement, Connection connection) {
//        try {
//            if (rSet != null) {
//                rSet.close();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (pStatement != null) {
//                    pStatement.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (connection != null) {
//                        connection.close();
//                    }
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//
    /**
     * 返回数据库集合
     * @return 数据库集合 Dblist
     */
    public static List<String> DBlist(Connection conn){

        if (conn == null){
            System.out.println("null");
        }
        List<String> dblist = new ArrayList<String>();
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("show databases");
            while(rs.next())
            {
                String database=rs.getString(1);//1代表数据库中表的列数，id在第一列也可以("id")！！！
                dblist.add(database);
            }
        }catch (Exception e){
            dblist = null;
        }
        return dblist;

    }
//
    /**
     * 获取指定数据库中包含的表 TBlist
     *
     * @time 2016年3月4日下午5:54:52
     * @packageName com.util
     * @return 返回所有表名(将表名放到一个集合中)
     * @throws Exception
     */
    public static List<String> TBlist(Connection conn,String databaseType,String databaseName) {

// 访问数据库 采用 JDBC方式 查看数据库下所有的表
        List<String> list = new ArrayList<>();
        try {
            DatabaseMetaData md = conn.getMetaData();
            //注意这是特别需要注意的点
            log.info(databaseType);
            ResultSet rs = null;
            if (databaseType.equals("Mysql")) {
                rs = md.getTables(conn.getCatalog(), null, "%", null);//mysql需要用到的
            }else{
                rs = md.getTables(null, databaseName, null, new String[]{"TABLE"});//hive 需要用到的
            }
            //ResultSet rs = md.getTables(conn.getCatalog(), null, "%", null);//mysql需要用到的
            //ResultSet rs = md.getTables(null, null, null, null);//sqlserver需要用到
            //ResultSet rs = md.getTables(null, "cp", null, new String[]{"TABLE"});
            if (rs != null) {
                list = new ArrayList<String>();
            }
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                log.info(tableName);
                list.add(tableName);
            }
            rs = null;
            md = null;
            conn = null;
        }catch (Exception e){
            conn=null;
        }

        return list;
    }

    /**
     * 指定数据库中的表进行List数据导入 Insert
     *
     * @time 2019年12月4日下午5:54:52
     * @packageName com.util
     * @return 返回所有表名(将表名放到一个集合中)
     * @throws Exception
     */
    public static void InsertList(Connection conn, String table, List<List<String>> lists){

        for (List list : lists) {
            int length = list.size();
            String sql1 = "(";
            String sql2 = "";
            for (int i=0;i<length-1;i++){
                sql2 = sql2+"'"+list.get(i)+"',";
            }
            sql1=sql1+sql2+"'"+list.get(length-1)+"')";
            String sql = "insert into "+table+ " values "+sql1;
            String sql5 = sql;
            System.out.println(sql5);
            try {
                PreparedStatement pst = conn.prepareStatement(sql5);
                pst.execute();
                log.info("插入成功");
            }catch (Exception e){
                log.error("插入失败");
            }
        }
    }

    public static ArrayList<String> getFieldsType(Connection conn , String tableName) {

        ArrayList<String> list = new ArrayList<String>();
        String querySql = "select * from "+tableName;
        try{
            PreparedStatement pst = conn.prepareStatement(querySql);
            ResultSet rs = pst.executeQuery();
            ResultSetMetaData data = rs.getMetaData();
            while (rs.next()) {
                for (int i = 1; i <= data.getColumnCount(); i++) {
                    //log.info(data.getColumnName(i));
                    list.add(data.getColumnTypeName(i));
                }
                break;
            }
            return list;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<String> getFieldsName(Connection conn , String tableName) {

        ArrayList<String> list = new ArrayList<String>();
        String querySql = "select * from "+tableName;
        try{
            PreparedStatement pst = conn.prepareStatement(querySql);
            ResultSet rs = pst.executeQuery();
            ResultSetMetaData data = rs.getMetaData();
            while (rs.next()) {
                for (int i = 1; i <= data.getColumnCount(); i++) {
                    log.info(data.getColumnName(i));
                    list.add(data.getColumnName(i));
                }
                break;
            }
            return list;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static Integer selectTotalRecords(Connection conn, String tablename){

        Integer totalRecords = 0;
        String queryCount = "select count(*) from "+ tablename;
        try {
            PreparedStatement pstCount = conn.prepareStatement(queryCount);
            ResultSet resultSet = pstCount.executeQuery(queryCount);
            if(resultSet.next()) {
                totalRecords=resultSet.getInt(1);
                log.info(totalRecords.toString());
            }
        }catch (SQLException e){
            log.error("查询条数失败");
        }

        return totalRecords;
    }

    public static List selectAllQuery (Connection conn, String tablename){

        List<HashMap<String, Object>> queryAllResult = new ArrayList<HashMap<String, Object>>();
        String querySql = "select * from "+tablename;
        String queryCount = "select count(*) from "+ tablename;
        int rows = 0;
        log.info(queryCount);
        //rows = DruidUtil.selectTotalRecords(conn,tablename);
        System.out.println(rows);
        try {
            ArrayList<String> tbFieldsList = DruidUtil.getFieldsName(conn,tablename);//获取表的字段信息
            int tbFieldsListSize = tbFieldsList.size(); //获取表字段数量
            PreparedStatement pst = conn.prepareStatement(querySql);
            ResultSet res = pst.executeQuery(querySql);

            while (res.next()) {
                HashMap<String, Object> queryOneResult = new HashMap<>();
                for (int i = 1;i <= tbFieldsListSize;i++ ){
                    String queryResult = res.getString(i);
                    queryOneResult.put(tbFieldsList.get(i-1),queryResult);
                }
                queryAllResult.add(queryOneResult);
            }
        }catch (Exception e){
            log.error("分页查询失败");
        }
        return queryAllResult;
    }

    public static PageModel selectAllQueryByPage (Connection conn, String tablename, Integer pageNum,Integer pageSize){

        List<HashMap<String, Object>> queryAllResult = new ArrayList<HashMap<String, Object>>();
        String querySql = "select * from "+tablename;
        String queryCount = "select count(*) from "+ tablename;
        int rows = 0;

        if (pageNum == null){
            pageNum = 1;
        }
        try {
            ArrayList<String> tbFieldsList = DruidUtil.getFieldsName(conn,tablename);//获取表的字段信息
            int tbFieldsListSize = tbFieldsList.size(); //获取表字段数量
            PreparedStatement pst = conn.prepareStatement(querySql);
            pst.setMaxRows(pageNum*pageSize);
            ResultSet res = pst.executeQuery(querySql);
            res.relative((pageNum-1)*pageSize);
            rows = DruidUtil.selectTotalRecords(conn,tablename);
            while (res.next()) {
                HashMap<String, Object> queryOneResult = new HashMap<>();
                for (int i = 1;i <= tbFieldsListSize;i++ ){
                    String queryResult = res.getString(i);
                    queryOneResult.put(tbFieldsList.get(i-1),queryResult);
                }
                queryAllResult.add(queryOneResult);
            }
            log.info("分页查询成功");
        }catch (Exception e){
            log.error("分页查询失败");
        }
        PageModel pageModel = new PageModel();
        pageModel.setList(queryAllResult);
        pageModel.setPageNo(pageNum);
        pageModel.setPageSize(10);
        pageModel.setTotalRecords(rows);
        return pageModel;
    }

//    /** 判断数据库是否支持批处理 */
//
//    public static boolean supportBatch(Connection con) {
//        try {
//            // 得到数据库的元数据
//            DatabaseMetaData md = con.getMetaData();
//            return md.supportsBatchUpdates();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//
//    }
//
//    public static int insertQuery(Connection conn, String tablename, String insertSql) {
//
//        ArrayList<String> tbFieldsList = DruidUtil.getFieldsName(conn,tablename);
//        log.info(insertSql);
//        int rows = -1;
//        try{
//        Statement stmt = conn.createStatement();
//        rows = stmt.executeUpdate(insertSql);
//        }catch (Exception e){
//            log.error(e.getMessage());
//        }
//        log.info("影响了"+rows+"行");
//        return rows;
//    }
//
//    public static void selectCustomizeQuery(Connection conn, String tablename, Integer pageNum, List<String> sqlFile) throws Exception {
//
//        Statement stmt = null;
//        stmt = conn.createStatement();
//        for (String sql : sqlFile) {
//            sql = sql.trim();
//            if(sql!=null&&!sql.equals(""))
//                stmt.addBatch(sql);
//        }
//        int[] rows = stmt.executeBatch();
//        System.out.println("Row count:" + Arrays.toString(rows));
//        conn.close();
//    }
//
//    public static void main(String[] args) {
//
//        SQLdrive sqLdrive = new SQLdrive();
//        sqLdrive.setDrive("com.mysql.jdbc.Driver");
//        sqLdrive.setPassword("root");
//        sqLdrive.setUser("root");
//        sqLdrive.setUrl("jdbc:mysql://192.168.46.136:3306");
//        Connection conn = DruidUtil.getConnection(sqLdrive,"test");
//        PageModel pageModel = DruidUtil.selectAllQuery(conn,"test1",1);
//        List<HashMap> queryAllResult = pageModel.getList();
//        log.info(String.valueOf(pageModel.getTotalRecords()));
//        log.info(String.valueOf(pageModel.getTotalPages()));
//        log.info(String.valueOf(pageModel.getPageNo()));
//        for(HashMap<String, String> map: queryAllResult) {
//            for (String key:map.keySet()){
//                System.out.print(key+"="+map.get(key)+" ");
//            }
//            System.out.println();
//        }
//
//        List<String> sqlFile = new ArrayList<String>();
//        List<String> select = new ArrayList<>();
//        List<String> batch = new ArrayList<>();
//        sqlFile.add("select * from test1");
//        sqlFile.add("select * from test2");
//        sqlFile.add("insert into test2 value ('litaibai',87)");
//        sqlFile.add("insert into test2 value ('jiangliuer',87)");
//        sqlFile.add("insert into test2 values ('jiangliuer',87),('zhaobenshan',13)");
//        for (String str : sqlFile){
//            if("select".equals(str.split(" ")[0])){
//                select.add(str);
//                }else {
//                batch.add(str);
//            }
//        }
//        try {
//            DruidUtil.selectAllQuery(conn,"test1",2);
//            DruidUtil.selectCustomizeQuery(conn,"test1",1,batch);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
}
