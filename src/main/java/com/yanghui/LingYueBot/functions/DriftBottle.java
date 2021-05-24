package com.yanghui.LingYueBot.functions;

import com.alibaba.fastjson.JSONObject;
import com.yanghui.LingYueBot.core.coreDatabaseUtil.BaseDatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

public class DriftBottle extends BaseDatabaseUtil {

    private static final Object lock = new Object();
    private final Object selfLock = new Object();
    private final long groupID;

    public DriftBottle(long groupID) {
        this.groupID = groupID;
    }

    public static void addDriftBottleALL(JSONObject newBottle, long operationID) throws SQLException {
        if (hasSameBottleALL(newBottle.getString("message")))
            return;
        if (newBottle.getString("message").isEmpty())
            return;
        String sql = "INSERT INTO DriftBottle " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, operationID);
        statement.setLong(2, newBottle.getLong("senderID"));
        statement.setString(3, newBottle.getString("message").trim());
        statement.setLong(4, 0);
        statement.setShort(5, (short) 2);
        statement.setTimestamp(6, new Timestamp(new Date().getTime()));
        statement.executeUpdate();
        statement.close();
    }

    public static JSONObject getDriftBottleAll() throws SQLException {
        JSONObject result = new JSONObject();
        String sql = "SELECT TOP 1 * FROM DriftBottle " +
                "WHERE groupID = ? AND restPick > 0" +
                "ORDER BY newid()";
        PreparedStatement statement = getStatement(sql);
        statement.setString(1, "0");
        ResultSet resultSet = statement.executeQuery();
        resultSet.first();
        result.put("sendTime", resultSet.getTimestamp("sendTime").getTime());
        result.put("message", resultSet.getString("content"));
        synchronized (lock) {
            setBottleShort(resultSet.getLong("operationID"), "restPick", (short) (resultSet.getShort("restPick") - 1));
        }
        statement.close();
        return result;
    }

    public static long getBottleNumAll() throws SQLException {
        String sql = "SELECT * FROM DriftBottle";
        PreparedStatement statement = getStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        resultSet.last();
        long num = resultSet.getRow();
        statement.close();
        return num;
    }

    public static void setBottleShort(long id, String key, short num) throws SQLException {
        String sql = "UPDATE DriftBottle SET " + key + " = ? " +
                "WHERE operationID = ?";
        PreparedStatement statement = getStatement(sql);
//        statement.setString(1, key);
        statement.setShort(1, num);
        statement.setLong(2, id);
        statement.executeUpdate();
        statement.close();
    }

    public static void deleteBottle(long bottleID) throws SQLException {
        String sql = "DELETE FROM DriftBottle " +
                "WHERE operationID = ?";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, bottleID);
        statement.executeUpdate();
        statement.close();
    }

    public static boolean hasSameBottleALL(String message) throws SQLException {
        String sql = "SELECT * FROM DriftBottle " +
                "WHERE content = ? AND GroupID = 0 AND restPick > 0";
        PreparedStatement statement = getStatement(sql);
        statement.setString(1, message);
        ResultSet resultSet = statement.executeQuery();
        resultSet.last();
        int num = resultSet.getRow();
        statement.close();
        return num != 0;
    }

    public void addDriftBottle(JSONObject newBottle, long operationID) throws SQLException {
        if (hasSameBottle(newBottle.getString("message")))
            return;
        if (newBottle.getString("message").isEmpty())
            return;
        String sql = "INSERT INTO DriftBottle " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, operationID);
        statement.setLong(2, newBottle.getLong("senderID"));
        statement.setString(3, newBottle.getString("message").trim());
        statement.setLong(4, groupID);
        statement.setShort(5, (short) 2);
        statement.setTimestamp(6, new Timestamp(new Date().getTime()));
        statement.executeUpdate();
        statement.close();
    }

    public JSONObject getDriftBottle() throws SQLException {
        JSONObject result = new JSONObject();
        String sql = "SELECT TOP 1 * FROM DriftBottle " +
                "WHERE groupID = ? AND restPick > 0" +
                "ORDER BY newid()";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, groupID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.first();
        result.put("sendTime", resultSet.getTimestamp("sendTime").getTime());
        result.put("message", resultSet.getString("content"));
        synchronized (selfLock) {
            setBottleShort(resultSet.getLong("operationID"), "restPick", (short) (resultSet.getShort("restPick") - 1));
        }
        statement.close();
        return result;
    }

    public Vector<JSONObject> removeBottle(String str, long id) throws SQLException {
        Vector<JSONObject> remove = new Vector<>();
        String sql = "SELECT * FROM DriftBottle " +
                "WHERE groupID = ? AND content = ?";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, groupID);
        statement.setString(2, str);
        ResultSet resultSet = statement.executeQuery();
        synchronized (selfLock) {
            while (resultSet.next()) {
                if (resultSet.getLong("senderID") == id || id == 2411046022L) {
                    JSONObject removeObject = new JSONObject();
                    removeObject.put("senderID", resultSet.getLong("senderID"));
                    removeObject.put("content", resultSet.getString("content"));
                    remove.add(removeObject);
                    deleteBottle(resultSet.getLong("operationID"));
                }
            }
        }
        statement.close();
        return remove;
    }

    public long getBottleNum() throws SQLException {
        String sql = "SELECT * FROM DriftBottle " +
                "WHERE groupID = ?";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, groupID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.last();
        long num = resultSet.getRow();
        statement.close();
        return num;
    }

    public boolean hasSameBottle(String message) throws SQLException {
        String sql = "SELECT * FROM DriftBottle " +
                "WHERE content = ? AND groupID = ? AND restPick > 0";
        PreparedStatement statement = getStatement(sql);
        statement.setString(1, message);
        statement.setLong(2, groupID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.last();
        int num = resultSet.getRow();
        statement.close();
        return num != 0;
    }
}
