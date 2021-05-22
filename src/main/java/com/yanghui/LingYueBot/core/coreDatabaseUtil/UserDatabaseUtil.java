package com.yanghui.LingYueBot.core.coreDatabaseUtil;

import net.mamoe.mirai.contact.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.Vector;

public class UserDatabaseUtil extends BaseDatabaseUtil {

    public static PreparedStatement selectUser(long id) throws SQLException {
        Vector<Object> result = new Vector<>();
        String sql = "SELECT * FROM Users WHERE id = ?";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, id);
        return statement;
    }

    public static boolean getUserExist(long id) throws SQLException {
        PreparedStatement statement = selectUser(id);
        ResultSet resultSet = statement.executeQuery();
        resultSet.last();
        boolean result = (resultSet.getRow() != 0);
        statement.close();
        return result;
    }

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

    public static void setUserInt(long id, String key, int value) throws SQLException {
        String sql = "UPDATE Users SET " + key + " = ? WHERE id = ?";
        PreparedStatement statement = getStatement(sql);
        statement.setInt(1, value);
        statement.setLong(2, id);
        statement.executeUpdate();
        statement.close();
    }

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

    public static void setUserString(long id, String key, String value) throws SQLException {
        String sql = "UPDATE Users SET " + key + " = ? WHERE id = ?";
        PreparedStatement statement = getStatement(sql);
        statement.setString(1, value);
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
        String sql = "INSERT INTO Users VALUES (?,?,?,?,?,?,?,?,?)";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, user.getId());
        statement.setString(2, user.getNick());
        statement.setBoolean(3, true);
        statement.setInt(4, new Random().nextInt(100));
        statement.setFloat(5, 1000.0F);
        statement.setString(6, "保密");
        statement.setBoolean(7, false);
        statement.setBoolean(8, false);
        statement.setBoolean(9, false);
        statement.executeUpdate();
        statement.close();
    }
}
