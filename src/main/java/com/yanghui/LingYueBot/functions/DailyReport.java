package com.yanghui.LingYueBot.functions;

import com.yanghui.LingYueBot.Template.GroupHandler;
import com.yanghui.LingYueBot.core.coreDatabaseUtil.BaseDatabaseUtil;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DailyReport extends BaseDatabaseUtil {

    public static void dailyReport(GroupHandler handler) throws SQLException {
        MessageChainBuilder builder = new MessageChainBuilder();
        String sql = "SELECT COUNT(id) FROM Operation\n" +
                "WHERE DATEDIFF([DAY], operationTime, GETDATE()) = 0\n" +
                "AND groupID = ?";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, handler.groupID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        builder.add(new PlainText("今天在本群一共执行了" + resultSet.getInt(1) + "次指令"));
        statement.close();
        handler.group.sendMessage(builder.asMessageChain());
    }

}
