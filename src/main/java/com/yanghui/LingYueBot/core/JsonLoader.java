package com.yanghui.LingYueBot.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JsonLoader {

    public static JSONArray jsonArrayLoader(String path, GroupMessageHandler handler) throws IOException {
        BufferedReader jsonBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
        StringBuilder fileContent = new StringBuilder();
        String cache;
        while ((cache = jsonBufferedReader.readLine()) != null)
            fileContent.append(cache);
        jsonBufferedReader.close();
        return JSON.parseArray(fileContent.toString());
    }
}
