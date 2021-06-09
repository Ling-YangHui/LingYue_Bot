package com.yanghui.lingYueBot.functions;

import com.yanghui.lingYueBot.core.coreDatabaseUtil.ResourceDatabaseUtil;
import net.mamoe.mirai.contact.Contact;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.Random;

public class SendPictures {

    /**
     * 从文件中获取一个图片，发送给contact
     *
     * @param contact 发送的对象
     * @param file    发送的文件
     */
    public static void sendPictures(File file, Contact contact) {
        String[] fileName = file.getName().split("\\.");
        Contact.Companion.sendImage(contact, file, fileName[fileName.length - 1]);
    }

    /**
     * 从数据库结果中获取一个图片，发送给contact
     *
     * @param type    文件类型
     * @param contact 发送对象
     * @param groupID 群组id
     */
    public static void sendPictures(String type, Contact contact, long groupID) {
        File file = new File("C://" + groupID + "." + type);
        sendPictures(file, contact);
    }

    /**
     * 从API中获取一张图片，然后发送这张图片
     *
     * @param contact 发送的对象
     * @throws IOException 启动URL失败
     */
    public static void sendPicturesFromInternet(Contact contact) throws Exception {
        String[] url = {
                "https://api.dongmanxingkong.com/suijitupian/acg/1080p/index.php",
                "https://api.vvhan.com/api/acgimg",
                "https://www.dmoe.cc/random.php",
//                "https://api.ghser.com/random/api.php",
                "https://api.ixiaowai.cn/api/api.php"};
        InputStream inputStream = null;
        int count = 5;
        while (true) {
            try {
                count--;
                inputStream = getImageFromURL(url[new Random().nextInt(url.length)]);
                break;
            } catch (IOException e) {
                if (count <= 0)
                    break;
            }
        }
        if (inputStream == null)
            throw new IOException();
        Contact.Companion.sendImage(contact, inputStream, "jpg");
        inputStream.close();
    }

    /**
     * 从URL中获取一个输入流
     *
     * @param url url
     * @return 输入流
     * @throws IOException 网络IO错误
     */
    private static InputStream getImageFromURL(String url) throws IOException {
        URL site = new URL(url);
        URLConnection connection = site.openConnection();
        connection.setConnectTimeout(5000);
        return connection.getInputStream();
    }

    public static void storeUpImage(InputStream inputStream) throws SQLException {
        ResourceDatabaseUtil.inputResource(inputStream, (short) 2);
    }
}
