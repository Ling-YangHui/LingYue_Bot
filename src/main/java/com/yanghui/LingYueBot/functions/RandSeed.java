package com.yanghui.LingYueBot.functions;

import com.yanghui.LingYueBot.core.coreDatabaseUtil.BaseDatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RandSeed extends BaseDatabaseUtil {

    /**
     * 抽种子
     *
     * @return 查询随机得到的种子
     * @throws SQLException 查询SQL失败
     */
    public static String randSeed() throws SQLException {
        String sql = "SELECT TOP 1 bottleContent FROM DriftBottle " +
                "WHERE bottleContent LIKE 'magnet:%' " +
                "ORDER BY NEWID()";
        PreparedStatement statement = getStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        String returnValue = resultSet.getString(1);
        statement.close();
        return returnValue;
    }

}
