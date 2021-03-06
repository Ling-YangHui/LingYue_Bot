package com.yanghui.lingYueBot.core.coreDatabaseUtil;

import net.mamoe.mirai.message.data.Image;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResourceDatabaseUtil extends BaseDatabaseUtil {

    /**
     * 从数据库中获取资源
     *
     * @param id      资源id
     * @param groupID 群组名称
     * @return 资源的类型
     * @throws SQLException 查询SQL失败
     * @throws IOException  缓存文件写入失败
     */
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

    /**
     * 输入文件资源
     *
     * @param stream 数据流
     * @param type   数据文件类型
     * @throws SQLException 查询SQL失败
     */
    public static int inputResource(InputStream stream, short type) throws SQLException {
        String sql = "INSERT INTO Resource (id, type, resource) VALUES(?, ?, ?)";
        PreparedStatement statement = getStatement(sql);
        int id = getResourceNum() + 1;
        statement.setInt(1, id);
        statement.setShort(2, type);
        statement.setBinaryStream(3, stream);
        statement.executeUpdate();
        statement.close();
        return id;
    }

    public static int inputResource(Image image, short type) throws SQLException, IOException {
        URL url = new URL(Image.queryUrl(image));
        URLConnection con = url.openConnection();
        con.setConnectTimeout(5000);
        InputStream inputStream = con.getInputStream();
        int id = ResourceDatabaseUtil.inputResource(inputStream, type);
        inputStream.close();
        return id;
    }

    /**
     * 查找资源总数量
     *
     * @return 资源数量
     * @throws SQLException 查询SQL失败
     */
    public static int getResourceNum() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Resource";
        PreparedStatement statement = getStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        int returnValue = resultSet.getInt(1);
        statement.close();
        return returnValue;
    }

}
