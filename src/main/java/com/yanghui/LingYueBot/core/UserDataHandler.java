package com.yanghui.LingYueBot.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class UserDataHandler {
    public long userID;
    public int userLike;
    public boolean userIsAdministrator;
    public boolean userIsSpecial;
    public int hasFuck;
    public String userName;

    public UserDataHandler(long userID, int userLike, boolean userIsSpecial, boolean userIsAdministrator, int hasFuck, String name) {
        this.userID = userID;
        this.userLike = userLike;
        this.userIsAdministrator = userIsAdministrator;
        this.userIsSpecial = userIsSpecial;
        this.hasFuck = hasFuck;
        this.userName = name;
    }

    public static void saveJsonFile(HashMap<Long, UserDataHandler> userArray, String path) throws Exception{
        JSONArray userJsonArray = new JSONArray();
        for (UserDataHandler handler : userArray.values()) {
            JSONObject userObject = new JSONObject();
            userObject.put("userID", handler.userID);
            userObject.put("like", handler.userLike);
            userObject.put("isSpecialUser", handler.userIsSpecial);
            userObject.put("isAdministrator", handler.userIsAdministrator);
            userObject.put("hasFuck", handler.hasFuck);
            userObject.put("name", handler.userName);
            userJsonArray.add(userObject);
        }
        Writer jsonWriter = new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8);
        jsonWriter.write(userJsonArray.toJSONString());
        jsonWriter.flush();
        jsonWriter.close();
    }
}
