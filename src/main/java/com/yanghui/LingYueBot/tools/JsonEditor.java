package com.yanghui.LingYueBot.tools;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yanghui.LingYueBot.core.coreTools.JsonLoader;

import java.io.IOException;

public class JsonEditor {

    public static final String rootPath = "D:\\IntelliJ IDEA programming\\MiraiRobot\\MiraiResources\\LingYue_resources\\XiaoFangZhou\\driftBottle.json";

    public static void main(String[] args) {
        try {
            JSONArray jsonArray = JsonLoader.jsonArrayLoader(rootPath);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                object.remove("sendTime");
            }
//            JsonLoader.saveJSONArray(rootPath, jsonArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
