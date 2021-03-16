package com.yanghui.LingYueBot.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class JsonLoader {

    public static JSONArray jsonArrayLoader(String path, GroupMessageHandler handler) throws IOException {
        InputStream jsonStream = handler.getClass().getClassLoader().getResourceAsStream(path);
        assert jsonStream != null;
        InputStreamReader inputStreamReader = new InputStreamReader(jsonStream, StandardCharsets.UTF_8);
        StringBuilder fileContent = new StringBuilder();
        while (true) {
            char[] buffer = new char[65536];
            int size = inputStreamReader.read(buffer, 0, buffer.length);
            if (size < 0) {
                break;
            }
            fileContent.append(buffer);
        }
        return JSON.parseArray(fileContent.toString());
    }
}
