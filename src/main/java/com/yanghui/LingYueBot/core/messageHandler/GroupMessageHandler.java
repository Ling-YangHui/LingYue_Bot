package com.yanghui.LingYueBot.core.messageHandler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yanghui.LingYueBot.functions.DriftBottle;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.HashMap;

public abstract class GroupMessageHandler {

    public GroupMessageHandler(String str) {
        rootPath = str;
    }

    public Group group = null;
    // Bot设置状态
    public final HashMap<String, Object> configList = new HashMap<>();
    public final HashMap<String, Object> functionMap = new HashMap<>();
    // Bot参数
    public final HashMap<String, Object> paramList = new HashMap<>();
    // 存档路径
    public String rootPath;
    public JSONObject userList;
    public JSONArray repeatList;
    public JSONArray replyList;
    public JSONArray scheduleTaskList;
    public DriftBottle driftBottle;

    public abstract void onCreate() throws Exception;

    public abstract void onHandleMessage(GroupMessageEvent event);

    public abstract void onDelete() throws Exception;

}
