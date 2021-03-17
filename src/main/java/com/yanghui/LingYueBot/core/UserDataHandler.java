package com.yanghui.LingYueBot.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;

public class UserDataHandler {
    public long userID;
    public int userLike;
    public boolean userIsAdministrator;
    public boolean userIsSpecial;
    public int hasFuck;

    public UserDataHandler(long userID, int userLike, boolean userIsSpecial, boolean userIsAdministrator, int hasFuck) {
        this.userID = userID;
        this.userLike = userLike;
        this.userIsAdministrator = userIsAdministrator;
        this.userIsSpecial = userIsSpecial;
        this.hasFuck = hasFuck;
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
            userJsonArray.add(userObject);
        }
        BufferedWriter jsonBufferedWriter = new BufferedWriter(new FileWriter(path));
        jsonBufferedWriter.write(userJsonArray.toJSONString());
        jsonBufferedWriter.flush();
        jsonBufferedWriter.close();
    }
}
