package com.yanghui.LingYueBot.tools;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yanghui.LingYueBot.core.JsonLoader;

import java.io.IOException;
import java.util.Vector;

public class GetHeap {

    public static void main(String[] args) {
        try {
            JSONArray userArray = JsonLoader.jsonArrayLoader("D:\\IntelliJ IDEA programming\\MiraiRobot\\MiraiResources\\LingYue_resources\\XiaoFangZhou\\user.json", null);
            Vector<JSONObject> mostLike = new Vector<>();
            Vector<JSONObject> mostFuck = new Vector<>();
            int mostLikeValue = -65536;
            int mostFuckValue = 0;
            for (int i = 0;i < userArray.size();i ++) {
                JSONObject object = userArray.getJSONObject(i);
                if (object.getIntValue("like") > mostLikeValue) {
                    mostLike.clear();
                    mostLike.add(object);
                    mostLikeValue = object.getIntValue("like");
                } else if (object.getIntValue("like") == mostLikeValue) {
                    mostLike.add(object);
                }

                if (object.getIntValue("hasFuck") > mostFuckValue) {
                    mostFuck.clear();
                    mostFuck.add(object);
                    mostFuckValue = object.getIntValue("hasFuck");
                } else if (object.getIntValue("Fuck") == mostFuckValue) {
                    mostFuck.add(object);
                }
            }
            System.out.println("\n--- Like max ---");
            for (JSONObject object : mostLike) {
                System.out.println(object.getString("name"));
            }
            System.out.println("value: " + mostLike.get(0).getIntValue("like"));

            System.out.println("--- Sex max ---");
            for (JSONObject object : mostFuck) {
                System.out.println(object.getString("name"));
            }
            System.out.println("value: " + mostFuck.get(0).getIntValue("hasFuck"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
