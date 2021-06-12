package com.yanghui.lingYueBot.functions.connectPython;

import com.yanghui.lingYueBot.core.coreDatabaseUtil.BaseDatabaseUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SatelliteGetPosition {

    /**
     * 调用python程序，获取输出流结果
     *
     * @param str 传输给python的参数
     * @return 从python中获取的流，一般是一个字符串
     * @throws IOException 获取流失败
     */
    public static String satelliteGetPosition(String str) throws IOException, SQLException {
        String sql = "SELECT * FROM Satellite WHERE name = ?";
        PreparedStatement statement = BaseDatabaseUtil.getStatement(sql);
        statement.setString(1, str.toUpperCase().trim());
        ResultSet resultSet = statement.executeQuery();
        if (!resultSet.next()) {
            return "没有找到该卫星\n当前指令已经进行了修改，请使用卫星英文/拼音进行查询\n卫星具体名称详见北美防空司令部：https://www.celestrak.com/NORAD/elements/active.txt";
        }
        String argv = "";
        String name = resultSet.getString("name").replace(" ", "$");
        String line1 = resultSet.getString("line1").replace(" ", "$");
        String line2 = resultSet.getString("line2").replace(" ", "$");
        argv += ("@" + name + "@" + line1 + "@" + line2);
        statement.close();

        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec("python -u C:\\LingYue\\SatellitePosition.py " + argv);
        BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream(), "GBK"));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = input.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString().replace(";", "\n");
    }
}