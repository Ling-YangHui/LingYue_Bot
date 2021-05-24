package com.yanghui.LingYueBot.functions;

import com.alibaba.fastjson.JSONArray;
import com.yanghui.LingYueBot.core.coreDatabaseUtil.BaseDatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Repeat extends BaseDatabaseUtil {

    public static JSONArray getRepeat(long groupID) throws SQLException {
        JSONArray returnValue = new JSONArray();
        String sql = "SELECT * FROM Repeat " +
                "WHERE groupID = ? OR groupID = 0";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, groupID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        while (resultSet.next())
            returnValue.add(resultSet.getString("content"));
        statement.close();
        return returnValue;
    }

}
