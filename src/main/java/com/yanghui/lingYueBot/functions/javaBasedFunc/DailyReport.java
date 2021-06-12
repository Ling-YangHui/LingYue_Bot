package com.yanghui.lingYueBot.functions.javaBasedFunc;

import com.yanghui.lingYueBot.core.coreDatabaseUtil.BaseDatabaseUtil;
import com.yanghui.lingYueBot.handler.GroupHandler;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DailyReport extends BaseDatabaseUtil {

    /**
     * 汇报当天群组内任务执行情况
     *
     * @param handler 群管理句柄
     * @throws SQLException 查询SQL失败
     */
    public static void dailyReport(GroupHandler handler) throws SQLException {
        MessageChainBuilder builder = new MessageChainBuilder();
        String sql = "SELECT OperationName, COUNT(id)\n" +
                "FROM Operation\n" +
                "JOIN [OperationCode-Name] [OC-N] on Operation.operationCode = [OC-N].OperationCode\n" +
                "WHERE groupID = ? AND DATEDIFF(day, operationTime, GETDATE()) = 0\n" +
                "GROUP BY OperationName\n" +
                "ORDER BY COUNT(id) DESC";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, handler.groupID);
        ResultSet resultSet = statement.executeQuery();
        builder.add("本群今日执行指令如下\n");
        int all = 0;
        while (resultSet.next()) {
            builder.add(resultSet.getString(1) + ": " + resultSet.getInt(2) + "\n");
            all += resultSet.getInt(2);
        }
        builder.add("一共执行了" + all + "次指令");
        statement.close();
        handler.group.sendMessage(builder.asMessageChain());
    }

}
