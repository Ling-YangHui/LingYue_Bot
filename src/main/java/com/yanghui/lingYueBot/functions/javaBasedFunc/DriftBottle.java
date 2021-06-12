package com.yanghui.lingYueBot.functions.javaBasedFunc;

import com.alibaba.fastjson.JSONObject;
import com.yanghui.lingYueBot.core.coreDatabaseUtil.BaseDatabaseUtil;
import com.yanghui.lingYueBot.core.coreDatabaseUtil.OperationDatabaseUtil;
import net.mamoe.mirai.event.events.MessageEvent;

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

    /**
     * 初始化一个群聊的漂流瓶Handler
     *
     * @param groupID QQ群号
     */
    public DriftBottle(long groupID) {
        this.groupID = groupID;
    }

    /**
     * 发送全局漂流瓶
     *
     * @param newBottle   漂流瓶Json格式
     * @param operationID 操作ID，用于写入数据库
     * @throws SQLException 查询SQL错误
     */
    public static void addDriftBottleALL(JSONObject newBottle, long operationID) throws SQLException {
        if (hasSameBottleALL(newBottle.getString("message")))
            return;
        if (newBottle.getString("message").isEmpty())
            return;
        String sql = "INSERT INTO DriftBottle (operationID, senderID, bottleContent, GroupID, restPick, sendTime, giveLike)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, operationID);
        statement.setLong(2, newBottle.getLong("senderID"));
        statement.setString(3, newBottle.getString("message").trim());
        statement.setLong(4, 0);
        statement.setShort(5, (short) 2);
        statement.setTimestamp(6, new Timestamp(new Date().getTime()));
        statement.setInt(7, 0);
        statement.executeUpdate();
        statement.close();
    }

    /**
     * 接收全局漂流瓶
     *
     * @param operationID 操作ID
     * @param event       消息事件句柄
     * @return 漂流瓶Json格式
     * @throws SQLException 查询SQL错误
     */
    public static JSONObject getDriftBottleAll(long operationID, MessageEvent event) throws SQLException {
        JSONObject result = new JSONObject();
        String sql = "SELECT TOP 1 * FROM DriftBottle " +
                "WHERE groupID = ? AND restPick > 0" +
                "ORDER BY newid()";
        PreparedStatement statement = getStatement(sql);
        statement.setString(1, "0");
        ResultSet resultSet = statement.executeQuery();
        resultSet.first();
        result.put("bottleID", resultSet.getLong("operationID"));
        result.put("sendTime", resultSet.getTimestamp("sendTime").getTime());
        result.put("message", resultSet.getString("bottleContent"));
        synchronized (lock) {
            setBottleShort(resultSet.getLong("operationID"), "restPick", (short) (resultSet.getShort("restPick") - 1));
        }

        addPickRecord(operationID, resultSet.getLong("operationID"));
        statement.close();
        return result;
    }

    /**
     * 获取全局漂流瓶数量
     *
     * @return 全局漂流瓶数量
     * @throws SQLException 查询SQL错误
     */
    public static long getBottleNumAll() throws SQLException {
        String sql = "SELECT * FROM DriftBottle";
        PreparedStatement statement = getStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        resultSet.last();
        long num = resultSet.getRow();
        statement.close();
        return num;
    }

    public static short getBottleShort(long id, String key) throws SQLException {
        String sql = "SELECT " + key + " FROM DriftBottle WHERE operationID = ?";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, id);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        short returnValue = resultSet.getShort(1);
        statement.close();
        return returnValue;
    }

    /**
     * 设置一个漂流瓶的short-SmallInt数值
     *
     * @param id  漂流瓶id
     * @param key 列名
     * @param num 需要设置的数值
     * @throws SQLException 查询SQL错误
     */
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

    public static int getBottleInt(long id, String key) throws SQLException {
        String sql = "SELECT " + key + " FROM DriftBottle WHERE operationID = ?";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, id);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        int returnValue = resultSet.getInt(1);
        statement.close();
        return returnValue;
    }

    public static void setBottleInt(long id, String key, int num) throws SQLException {
        String sql = "UPDATE DriftBottle SET " + key + " = ? " +
                "WHERE operationID = ?";
        PreparedStatement statement = getStatement(sql);
//        statement.setString(1, key);
        statement.setInt(1, num);
        statement.setLong(2, id);
        statement.executeUpdate();
        statement.close();
    }

    /**
     * 删除一个漂流瓶
     *
     * @param bottleID 漂流瓶id
     * @throws SQLException 查询SQL错误
     */
    public static void deleteBottle(long bottleID) throws SQLException {
        String sql = "DELETE FROM DriftBottle " +
                "WHERE operationID = ?";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, bottleID);
        statement.executeUpdate();
        statement.close();
    }

    /**
     * 检查是否有相同全局漂流瓶
     *
     * @param message 漂流瓶内容
     * @return 是否含有相同的全局漂流瓶
     * @throws SQLException 查询SQL错误
     */
    public static boolean hasSameBottleALL(String message) throws SQLException {
        String sql = "SELECT * FROM DriftBottle " +
                "WHERE bottleContent = ? AND GroupID = 0 AND restPick > 0";
        PreparedStatement statement = getStatement(sql);
        statement.setString(1, message);
        ResultSet resultSet = statement.executeQuery();
        resultSet.last();
        int num = resultSet.getRow();
        statement.close();
        return num != 0;
    }

    public static void addPickRecord(long operationID, long pickBottleID) throws SQLException {
        OperationDatabaseUtil.operationAddTarget(operationID, pickBottleID);
    }

    private static long getLastPickBottle(long userID) throws SQLException {
        String sql = "SELECT TOP 1 target FROM Operation " +
                "WHERE userID = ? " +
                "AND ( OperationCode = 20 OR OperationCode = 21)" +
                "ORDER BY id DESC";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, userID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        long returnValue = resultSet.getLong(1);
        statement.close();
        return returnValue;
    }

    /**
     * 删除漂流瓶的对外方法
     *
     * @param str 漂流瓶内容
     * @param id  用户id，除了管理员，用户只能删除自己的漂流瓶
     * @return 删除的漂流瓶的列表
     * @throws SQLException 查询SQL错误
     */
    public Vector<JSONObject> removeBottle(String str, long id) throws SQLException {
        Vector<JSONObject> remove = new Vector<>();
        String sql = "SELECT * FROM DriftBottle " +
                "WHERE groupID = ? AND bottleContent = ?";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, groupID);
        statement.setString(2, str);
        ResultSet resultSet = statement.executeQuery();
        synchronized (selfLock) {
            while (resultSet.next()) {
                if (resultSet.getLong("senderID") == id || id == 2411046022L) {
                    JSONObject removeObject = new JSONObject();
                    removeObject.put("senderID", resultSet.getLong("senderID"));
                    removeObject.put("bottleContent", resultSet.getString("bottleContent"));
                    remove.add(removeObject);
                    deleteBottle(resultSet.getLong("operationID"));
                }
            }
        }
        statement.close();
        return remove;
    }

    /**
     * 获取群内漂流瓶数量
     *
     * @return 漂流瓶的数量
     * @throws SQLException 查询SQL错误
     */
    public long getBottleNum() throws SQLException {
        String sql = "SELECT * FROM DriftBottle " +
                "WHERE groupID = ? AND restPick > 0";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, groupID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.last();
        long num = resultSet.getRow();
        statement.close();
        return num;
    }

    /**
     * 是否含有相同的群内漂流瓶
     *
     * @param message 消息内容
     * @return 是否含有相同内容的群内漂流瓶
     * @throws SQLException 查询SQL错误
     */
    public boolean hasSameBottle(String message) throws SQLException {
        String sql = "SELECT * FROM DriftBottle " +
                "WHERE bottleContent = ? AND groupID = ? AND restPick > 0";
        PreparedStatement statement = getStatement(sql);
        statement.setString(1, message);
        statement.setLong(2, groupID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.last();
        int num = resultSet.getRow();
        statement.close();
        return num != 0;
    }

    public static boolean likeDriftBottle(long operationID, long userID) throws SQLException {
        // 检查是否已经点赞过了
        long bottleID = getLastPickBottle(userID);
        String sql = "SELECT COUNT(*) FROM Operation WHERE OperationCode = 24 " +
                "AND target = ? AND userID = ?";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, bottleID);
        statement.setLong(2, userID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        if (resultSet.getInt(1) > 0) {
            statement.close();
            return false;
        }

        sql = "UPDATE DriftBottle SET giveLike = ? WHERE operationID = ?";
        statement = getStatement(sql);
        statement.setLong(2, bottleID);
        int like = getBottleInt(bottleID, "giveLike");
        statement.setInt(1, like + 1);
        statement.executeUpdate();
        statement.close();

        OperationDatabaseUtil.operationAddTarget(operationID, bottleID);
        return true;
    }

    public static boolean likeDriftBottle(long operationID, long userID, long bottleID) throws SQLException {
        if (!getBottleExist(bottleID))
            return false;
        String sql = "SELECT COUNT(*) FROM Operation WHERE OperationCode = 24 " +
                "AND target = ? AND userID = ?";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, bottleID);
        statement.setLong(2, userID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        if (resultSet.getInt(1) > 0) {
            statement.close();
            return false;
        }

        sql = "UPDATE DriftBottle SET giveLike = ? WHERE operationID = ?";
        statement = getStatement(sql);
        statement.setLong(2, bottleID);
        int like = getBottleInt(bottleID, "giveLike");
        statement.setInt(1, like + 1);
        statement.executeUpdate();
        statement.close();

        OperationDatabaseUtil.operationAddTarget(operationID, bottleID);
        return true;
    }

    public static boolean getBottleExist(long bottleID) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DriftBottle WHERE operationID = ?";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, bottleID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        boolean returnValue = resultSet.getInt(1) > 0;
        statement.close();
        return returnValue;
    }

    /**
     * 发送群内漂流瓶
     *
     * @param newBottle   漂流瓶Json格式
     * @param operationID 操作代码，用于写入数据库
     * @throws SQLException 查询SQL错误
     */
    public void addDriftBottle(JSONObject newBottle, long operationID) throws SQLException {
        if (hasSameBottle(newBottle.getString("message")))
            return;
        if (newBottle.getString("message").isEmpty())
            return;
        String sql = "INSERT INTO DriftBottle (operationID, senderID, bottleContent, GroupID, restPick, sendTime, giveLike)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, operationID);
        statement.setLong(2, newBottle.getLong("senderID"));
        statement.setString(3, newBottle.getString("message").trim());
        statement.setLong(4, groupID);
        statement.setShort(5, (short) 2);
        statement.setTimestamp(6, new Timestamp(new Date().getTime()));
        statement.setInt(7, 0);
        statement.executeUpdate();
        statement.close();
    }

    /**
     * 接收全局漂流瓶
     *
     * @param operationID 操作ID
     * @param event       事件句柄
     * @return 漂流瓶Json格式
     * @throws SQLException 查询SQL错误
     */
    public JSONObject getDriftBottle(long operationID, MessageEvent event) throws SQLException {
        JSONObject result = new JSONObject();
        String sql = "SELECT TOP 1 * FROM DriftBottle " +
                "WHERE groupID = ? AND restPick > 0" +
                "ORDER BY newid()";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, groupID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.first();
        result.put("bottleID", resultSet.getLong("operationID"));
        result.put("sendTime", resultSet.getTimestamp("sendTime").getTime());
        result.put("message", resultSet.getString("bottleContent"));
        synchronized (selfLock) {
            setBottleShort(resultSet.getLong("operationID"), "restPick", (short) (resultSet.getShort("restPick") - 1));
        }

        addPickRecord(operationID, resultSet.getLong("operationID"));
        statement.close();
        return result;
    }
}
