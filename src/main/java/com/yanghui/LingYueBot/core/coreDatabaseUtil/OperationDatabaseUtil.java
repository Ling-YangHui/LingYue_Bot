package com.yanghui.LingYueBot.core.coreDatabaseUtil;

import com.yanghui.LingYueBot.core.coreTools.ParseOperationCode;
import net.mamoe.mirai.event.events.MessageEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class OperationDatabaseUtil extends BaseDatabaseUtil {

    /**
     * 新增操作记录
     *
     * @param operation 操作代码
     * @param event     操作事件
     * @param groupID   操作群
     * @return 操作事件id
     * @throws SQLException 查询SQL错误
     */
    public static long insertOperation(String operation, MessageEvent event, long groupID) throws SQLException {
        String sql = "INSERT INTO Operation VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = getStatement(sql);
        long num = getOperationNum();
        statement.setLong(1, num + 1);
        statement.setLong(2, event.getSender().getId());
        statement.setInt(3, ParseOperationCode.parseOperationCode(operation));
        statement.setTimestamp(4, new Timestamp(new Date().getTime()));
        statement.setLong(5, groupID);
        statement.executeUpdate();
        statement.close();
        return num + 1;
    }

    /**
     * 获取事件id
     *
     * @return 新增事件操作id
     * @throws SQLException 查询SQL错误
     */
    public static long getOperationNum() throws SQLException {
        String sql = "SELECT * FROM Operation";
        PreparedStatement statement = BaseDatabaseUtil.getStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        resultSet.last();
        long num = resultSet.getRow();
        statement.close();
        return num;
    }

}
