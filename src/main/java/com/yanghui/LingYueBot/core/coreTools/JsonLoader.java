package com.yanghui.LingYueBot.core.coreTools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JsonLoader {

    public static JSONArray jsonArrayLoader(String path) throws IOException {
        BufferedReader jsonBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
        StringBuilder fileContent = new StringBuilder();
        String cache;
        while ((cache = jsonBufferedReader.readLine()) != null)
            fileContent.append(cache);
        jsonBufferedReader.close();
        return JSON.parseArray(fileContent.toString());
    }

    public static JSONObject jsonObjectLoader(String path) throws IOException {
        BufferedReader jsonBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
        StringBuilder fileContent = new StringBuilder();
        String cache;
        while ((cache = jsonBufferedReader.readLine()) != null)
            fileContent.append(cache);
        jsonBufferedReader.close();
        return JSON.parseObject(fileContent.toString());
    }

    public static void saveJSONObject(String rootPath, String name, JSON json) throws IOException {
        String path = rootPath + name;
        Writer jsonWriter = new OutputStreamWriter(new FileOutputStream(rootPath + "cache\\" + name + ".json"), StandardCharsets.UTF_8);
        jsonWriter.write(json.toJSONString());
        jsonWriter.flush();
        jsonWriter.close();
        // 开始校验文件是否合法写入
        JSONObject array = JsonLoader.jsonObjectLoader(rootPath + "cache\\" + name + ".json");
        if (!array.isEmpty()) {
            FileInputStream inputStream = new FileInputStream(rootPath + "cache\\" + name + ".json");
            FileOutputStream outputStream = new FileOutputStream(path);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
        }
    }

    public static void saveJSONArray(String rootPath, String name, JSON json) throws IOException {
        String path = rootPath + name;
        Writer jsonWriter = new OutputStreamWriter(new FileOutputStream(rootPath + "cache\\" + name + ".json"), StandardCharsets.UTF_8);
        jsonWriter.write(json.toJSONString());
        jsonWriter.flush();
        jsonWriter.close();
        // 开始校验文件是否合法写入
        JSONArray array = JsonLoader.jsonArrayLoader(rootPath + "cache\\" + name + ".json");
        if (!array.isEmpty()) {
            FileInputStream inputStream = new FileInputStream(rootPath + "cache\\" + name + ".json");
            FileOutputStream outputStream = new FileOutputStream(path);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
        }
    }
}
