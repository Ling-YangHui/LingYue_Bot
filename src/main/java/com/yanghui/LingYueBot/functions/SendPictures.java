package com.yanghui.LingYueBot.functions;

import net.mamoe.mirai.contact.Contact;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

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
    public static void sendPicturesFromInternet(Contact contact) throws IOException {
        URL site = new URL("https://api.dongmanxingkong.com/suijitupian/acg/1080p/index.php");
        URLConnection con = site.openConnection();
        con.setConnectTimeout(5000);
        InputStream inputStream = con.getInputStream();
        Contact.Companion.sendImage(contact, inputStream, "jpg");
    }
}
