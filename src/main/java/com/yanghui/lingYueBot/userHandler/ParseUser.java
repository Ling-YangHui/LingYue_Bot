package com.yanghui.lingYueBot.userHandler;

import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.contact.User;

import java.util.Random;

public class ParseUser {

    public static JSONObject ParseJSONFromUser(User user) {
        JSONObject newUser = new JSONObject();
        newUser.put("id", user.getId()); // Long
        newUser.put("nick", user.getNick()); // String
        newUser.put("like", new Random().nextInt(10)); // Int
        newUser.put("emotion", new Random().nextInt(100)); // Int
        newUser.put("isBUAAer", false); // Bool
        newUser.put("gender", ""); // String
        newUser.put("valid", false); // Bool
        newUser.put("schedule", true); // Bool
        newUser.put("isAdministrator", false); // Bool
        newUser.put("isSpecial", false); // Bool
        return newUser;
    }


}
