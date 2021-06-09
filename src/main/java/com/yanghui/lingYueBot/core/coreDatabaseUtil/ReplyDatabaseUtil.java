package com.yanghui.lingYueBot.core.coreDatabaseUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReplyDatabaseUtil extends BaseDatabaseUtil {

    /**
     * 获取回复事件表
     *
     * @param groupID QQ群号
     * @return 回复事件表
     * @throws SQLException 查询SQL失败
     */
    public static JSONArray getReply(long groupID) throws SQLException {
        JSONArray returnValue = new JSONArray();
        String sql = "SELECT * FROM Reply " +
                "WHERE groupID = ? OR groupID = 0";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, groupID);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            JSONObject object = new JSONObject();
            object.put("trigMessage", JSONObject.parseArray(resultSet.getString("trigMessage")));
            object.put("condition", JSONObject.parseArray(resultSet.getString("condition")));
            object.put("reply", JSONObject.parseArray(resultSet.getString("reply")));
            object.put("operation", JSONObject.parseArray(resultSet.getString("operation")));
            returnValue.add(object);
        }
        statement.close();
        return returnValue;
    }
}
