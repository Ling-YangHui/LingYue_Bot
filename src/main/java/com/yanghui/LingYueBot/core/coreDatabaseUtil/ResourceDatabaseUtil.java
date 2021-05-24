package com.yanghui.LingYueBot.core.coreDatabaseUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResourceDatabaseUtil extends BaseDatabaseUtil {

    public static String getResource(int id, long groupID) throws SQLException, IOException {
        String sql = "SELECT * FROM Resource " +
                "WHERE id = ?";
        PreparedStatement statement = getStatement(sql);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        String type = "png";
        switch (resultSet.getShort("type")) {
            case 1:
                type = "png";
                break;
            case 2:
                type = "jpg";
        }
        InputStream stream = resultSet.getBinaryStream("resource");
        byte[] b = new byte[16 * 1024 * 1024];
        FileOutputStream fos = new FileOutputStream("C://" + groupID + "." + type);
        while (stream.read(b) != -1) {
            fos.write(b);
        }
        stream.close();
        fos.close();
        statement.close();
        return type;
    }

}
