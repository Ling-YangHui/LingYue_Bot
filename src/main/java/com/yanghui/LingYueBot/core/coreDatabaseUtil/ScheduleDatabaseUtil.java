package com.yanghui.LingYueBot.core.coreDatabaseUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class ScheduleDatabaseUtil extends BaseDatabaseUtil {

    /**
     * 获取计划事件表
     *
     * @param groupID QQ群号
     * @return 计划事件表
     * @throws SQLException 查询SQL错误
     */
    public static JSONArray getSchedule(long groupID) throws SQLException {
        JSONArray returnValue = new JSONArray();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String sql = "SELECT * FROM Schedule " +
                "WHERE groupID = ? OR groupID = 0";
        PreparedStatement statement = getStatement(sql);
        statement.setLong(1, groupID);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            JSONObject object = new JSONObject();
            object.put("time", sdf.format(resultSet.getTime("time")));
            object.put("message", resultSet.getString("content"));
            returnValue.add(object);
        }
        statement.close();
        return returnValue;
    }

}
