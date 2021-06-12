package com.yanghui.lingYueBot.functions.javaBasedFunc;

import com.alibaba.fastjson.JSONArray;
import com.yanghui.lingYueBot.core.coreDatabaseUtil.BaseDatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Repeat extends BaseDatabaseUtil {

    /**
     * 获取复读的列表
     *
     * @param groupID QQ群号
     * @return 复读内容的json列表
     * @throws SQLException 查询SQL失败
     */
    public static JSONArray getRepeat(long groupID) throws SQLException {
        JSONArray returnValue = new JSONArray();
        String sql = "SELECT * FROM Repeat " +
                "WHERE groupID = ? OR groupID = 0";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, groupID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        while (resultSet.next())
            returnValue.add(resultSet.getString("repeatContent"));
        statement.close();
        return returnValue;
    }

}
