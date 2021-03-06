package com.yanghui.lingYueBot.SDK;

import com.yanghui.lingYueBot.utils.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

public class Install {

    public static void main(String[] args) {
        try {
            FileInputStream inputStream = new FileInputStream("D:\\IntelliJ IDEA programming\\MiraiBot\\build\\mirai\\LingYue.mirai.jar");
            FileOutputStream outputStream = new FileOutputStream("E:\\LingYue.mirai.jar");
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
            System.out.println("\n------------Finish------------");
            System.out.println("Size: " + new File("D:\\IntelliJ IDEA programming\\MiraiRobot\\MiraiCore\\plugins\\LingYue.mirai.jar").length() / 1024 + "KB");
            System.out.println("Time: " + new Date());
        } catch (Exception e) {
            Logger.logError(e);
        }
    }
}
