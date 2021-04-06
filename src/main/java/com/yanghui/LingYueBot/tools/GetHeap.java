package com.yanghui.LingYueBot.tools;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yanghui.LingYueBot.core.coreTools.JsonLoader;

import java.io.IOException;
import java.util.Vector;

public class GetHeap {

    public static void main(String[] args) {
        try {
            JSONArray userArray = JsonLoader.jsonArrayLoader("D:\\IntelliJ IDEA programming\\MiraiRobot\\MiraiResources\\LingYue_resources\\XiaoFangZhou\\user.json");
            Vector<JSONObject> userVector = new Vector<>();
            for (int i = 0; i < userArray.size(); i++) {
                userVector.add(userArray.getJSONObject(i));
            }
            /* 比较like */
            userVector.sort((a, b) -> Integer.compare(b.getIntValue("like"), a.getIntValue("like")));
            System.out.println("\n--- Like Max ---");
            for (int i = 0; i < 5; i++) {
                System.out.println(userVector.get(i).getString("name") + "  " + userVector.get(i).getIntValue("like"));
            }

            /* 比较Fuck */
            userVector.sort((a, b) -> Integer.compare(b.getIntValue("hasFuck"), a.getIntValue("hasFuck")));
            System.out.println("\n--- Fuck Max ---");
            for (int i = 0; i < 5; i++) {
                System.out.println(userVector.get(i).getString("name") + "  " + userVector.get(i).getIntValue("hasFuck"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
