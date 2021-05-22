package com.yanghui.LingYueBot.core.coreDatabaseUtil;

import java.sql.*;

public class BaseDatabaseUtil {

    public static Connection connection = null;

    /* 连接User数据库 */
    public static void initDatabase() {
        try {// 加载数据库驱动类
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("数据库驱动加载成功");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection("jdbc:sqlserver://localhost:1433; sever=YangHui; DatabaseName=LingYue", "sa", "cayh121300");
            System.out.println("数据库连接成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PreparedStatement getStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }

}
