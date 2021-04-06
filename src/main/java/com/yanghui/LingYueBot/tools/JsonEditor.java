package com.yanghui.LingYueBot.tools;

import com.alibaba.fastjson.JSONObject;
import com.yanghui.LingYueBot.core.coreTools.JsonLoader;

import java.io.IOException;

public class JsonEditor {

    public static final String rootPath = "D:\\IntelliJ IDEA programming\\MiraiRobot\\MiraiResources\\LingYue_resources\\Users\\";

    public static void main(String[] args) {
        try {
            JSONObject jsonObject = JsonLoader.jsonObjectLoader(rootPath + "userInfo.json");
            for (String s : jsonObject.keySet()) {
                JSONObject object = jsonObject.getJSONObject(s);
                object.put("forbidden", false);
            }
            JsonLoader.saveJSONObject(rootPath + "userInfo.json", jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
