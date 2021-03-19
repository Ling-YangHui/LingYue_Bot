package com.yanghui.LingYueBot.functions;

import net.mamoe.mirai.contact.Contact;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class SendSexPictures {

    public static void sendSexPictures(File file, Contact contact) {
        String[] fileName = file.getName().split("\\.");
        Contact.Companion.sendImage(contact, file, fileName[fileName.length - 1]);
    }

    /*
     * 从API中获取一张图片，然后发送这张图片
     *
     * @param: Contact contact
     *
     * */
    public static void sendSexPicturesFromInternet(Contact contact) {
        try {
            URL site = new URL("https://api.dongmanxingkong.com/suijitupian/acg/1080p/index.php");
            URLConnection con = site.openConnection();
            con.setConnectTimeout(5000);
            InputStream inputStream = con.getInputStream();
            Contact.Companion.sendImage(contact, inputStream, "jpg");
        } catch (Exception e) {
            System.err.println("下载图片错误");
            e.printStackTrace();
        }
    }
}