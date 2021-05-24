package com.yanghui.LingYueBot.functions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SatelliteGetPosition {

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
