package com.yanghui.LingYueBot.functions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SatelliteGetPosition {

    /**
     * 调用python程序，获取输出流结果
     *
     * @param str 传输给python的参数
     * @return 从python中获取的流，一般是一个字符串
     * @throws IOException 获取流失败
     */
    public static String satelliteGetPosition(String str) throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec("python -u C:\\LingYue\\SatellitePosition.py " + str);
        BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream(), "GBK"));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = input.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString().replace(";", "\n");
    }
}
