package com.yanghui.lingYueBot.core.coreDatabaseUtil;

import net.mamoe.mirai.contact.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.Vector;

public class UserDatabaseUtil extends BaseDatabaseUtil {

    /**
     * 获取用户的QQ号列表
     *
     * @return 用户的QQ号列表
     * @throws SQLException 查询SQL失败
     */
    public static Vector<Long> getUserList() throws SQLException {
        Vector<Long> result = new Vector<>();
        String sql = "SELECT id FROM Users";
        PreparedStatement statement = getStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            result.add(resultSet.getLong(1));
        }
        return result;
    }

    /**
     * 选择一个用户并且返回它的statement
     *
     * @param id 用户的QQ号
     * @return statement
     * @throws SQLException 查询SQL失败
     */
    public static PreparedStatement selectUser(long id) throws SQLException {
        Vector<Object> result = new Vector<>();
        String sql = "SELECT * FROM Users WHERE id = ?";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, id);
        return statement;
    }

    /**
     * 查询用户是否存在
     *
     * @param id 用户QQ号
     * @return 返回用户是否存在
     * @throws SQLException 查询SQL失败
     */
    public static boolean getUserExist(long id) throws SQLException {
        PreparedStatement statement = selectUser(id);
        ResultSet resultSet = statement.executeQuery();
        resultSet.last();
        boolean result = (resultSet.getRow() != 0);
        statement.close();
        return result;
    }

    /**
     * 获取用户的一个整数类型属性
     *
     * @param id  用户QQ号
     * @param key 属性列名
     * @return 返回的整数
     * @throws SQLException 查询SQL失败
     */
    public static int getUserInt(long id, String key) throws SQLException {
        String sql = "SELECT " + key + " FROM Users WHERE id = ?";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, id);
        ResultSet resultSet = statement.executeQuery();
        resultSet.first();
        int result = resultSet.getInt(1);
        statement.close();
        return result;
    }

    /**
     * 设置用户的一个整数类型
     *
     * @param id    用户QQ号
     * @param key   属性列名
     * @param value 需要设置的数值
     * @throws SQLException 查询SQL失败
     */
    public static void setUserInt(long id, String key, int value) throws SQLException {
        String sql = "UPDATE Users SET " + key + " = ? WHERE id = ?";
        PreparedStatement statement = getStatement(sql);
        statement.setInt(1, value);
        statement.setLong(2, id);
        statement.executeUpdate();
        statement.close();
    }

    /**
     * 获取用户字符串
     *
     * @param id  用户QQ号
     * @param key 属性列名
     * @return 对应字符串
     * @throws SQLException 查询SQL失败
     */
    public static String getUserString(long id, String key) throws SQLException {
        String sql = "SELECT " + key + " FROM Users WHERE id = ?";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, id);
        ResultSet resultSet = statement.executeQuery();
        resultSet.first();
        String result = resultSet.getString(1);
        statement.close();
        return result;
    }

    /**
     * 设置用户字符串属性
     *
     * @param id    用户QQ号
     * @param key   属性列名
     * @param value 需要设置的值
     * @throws SQLException 查询SQL失败
     */
    public static void setUserString(long id, String key, String value) throws SQLException {
        String sql = "UPDATE Users SET " + key + " = ? WHERE id = ?";
        PreparedStatement statement = getStatement(sql);
        statement.setString(1, value);
        statement.setLong(2, id);
        statement.executeUpdate();
        statement.close();
    }

    /**
     * 获取用户short类型属性
     *
     * @param id  用户QQ号
     * @param key 属性列名
     * @return 用户short类型属性
     * @throws SQLException 查询SQL失败
     */
    public static short getUserShort(long id, String key) throws SQLException {
        String sql = "SELECT " + key + " FROM Users WHERE id = ?";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, id);
        ResultSet resultSet = statement.executeQuery();
        resultSet.first();
        short result = resultSet.getShort(1);
        statement.close();
        return result;
    }

    public static void setUserShort(long id, String key, short value) throws SQLException {
        String sql = "UPDATE Users SET " + key + " = ? WHERE id = ?";
        PreparedStatement statement = getStatement(sql);
        statement.setShort(1, value);
        statement.setLong(2, id);
        statement.executeUpdate();
        statement.close();
    }

    public static boolean getUserBoolean(long id, String key) throws SQLException {
        String sql = "SELECT " + key + " FROM Users WHERE id = ?";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, id);
        ResultSet resultSet = statement.executeQuery();
        resultSet.first();
        boolean result = resultSet.getBoolean(1);
        statement.close();
        return result;
    }

    public static void setUserBoolean(long id, String key, boolean value) throws SQLException {
        String sql = "UPDATE Users SET " + key + " = ? WHERE id = ?";
        PreparedStatement statement = getStatement(sql);
        statement.setBoolean(1, value);
        statement.setLong(2, id);
        statement.executeUpdate();
        statement.close();
    }

    public static void insertUser(User user) throws SQLException {
        String sql = "INSERT INTO Users (id, userName, schedule, favor, balance, gender, isSpecial, isAdministrator, isForbidden) " +
                "VALUES (?,?,?,?,?,?,?,?,?)";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, user.getId());
        statement.setString(2, user.getNick());
        statement.setBoolean(3, true);
        statement.setInt(4, new Random().nextInt(100));
        statement.setFloat(5, 1000.0F);
        statement.setShort(6, (short) 0);
        statement.setBoolean(7, false);
        statement.setBoolean(8, false);
        statement.setBoolean(9, false);
        statement.executeUpdate();
        statement.close();
    }
}
