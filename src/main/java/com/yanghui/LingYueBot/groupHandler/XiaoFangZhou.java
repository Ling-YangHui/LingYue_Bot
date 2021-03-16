package com.yanghui.LingYueBot.groupHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yanghui.LingYueBot.core.GroupMessageHandler;
import com.yanghui.LingYueBot.core.JsonLoader;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Random;

public class XiaoFangZhou extends GroupMessageHandler {

    private final HashMap<String, Object> paramList = new HashMap<>();
    private final HashMap<String, Object> configList = new HashMap<>();
    JSONArray repeatArray;
    JSONArray specialResponseArray;
    JSONArray specialUserArray;

    public XiaoFangZhou() {
        paramList.put("SuccessiveRepeat", false);
        paramList.put("LastMessage", "");
        configList.put("SuccessiveRepeat_Permission", true);
        configList.put("SpecialMessageReply_Permission", true);
        configList.put("OnActive", true);
    }

    @Override
    public void onCreate() {
        InputStream jsonStream;
        InputStreamReader inputStreamReader;
        StringBuilder fileContent;
        char[] buffer = new char[1024];
        try {
            /* TODO：json读取 */
            // 读取自动复读
            repeatArray = JsonLoader.jsonArrayLoader("XiaoFangZhou/repeatList.json", this);
            // 读取特殊语句回复
            specialResponseArray = JsonLoader.jsonArrayLoader("XiaoFangZhou/specialRepeatList.json", this);
            // 读取特殊用户
            specialUserArray = JsonLoader.jsonArrayLoader("XiaoFangZhou/specialUser.json", this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHandleMessage(GroupMessageEvent event) {
        String message = event.getMessage().contentToString();
        Group group = event.getGroup();
        /* TODO：最高优先级——管理员任务 */
        if (event.getSender().getId() == 2411046022L) {
            switch (message) {
                case "LingDong滚回去卷吧":
                    configList.put("OnActive", false);
                    group.sendMessage("呜~这就去卷");
                    break;
                case "LingYue -getStatus":
                    StringBuilder str = new StringBuilder();
                    for (String key : configList.keySet()) {
                        str.append(key).append(": ").append(configList.get(key)).append('\n');
                    }
                    group.sendMessage(str.toString());
                    break;
                case "LingYue -close":
                    configList.put("OnActive", false);
                    break;
                case "LingYue -open":
                    configList.put("OnActive", true);
                    break;
                case "LingYue -close repeat":
                    configList.put("SuccessiveRepeat_Permission", false);
                    break;
                case "LingYue -open repeat":
                    configList.put("SuccessiveRepeat_Permission", true);
                    break;
                case "LingYue -close special":
                    configList.put("SpecialMessageReply_Permission", false);
                    break;
                case "LingYue -open special":
                    configList.put("SpecialMessageReply_Permission", true);
                    break;
            }
        }
        if (!(boolean) configList.get("OnActive"))
            return;

        /* TODO：自动复读 */
        if ((boolean) configList.get("SuccessiveRepeat_Permission")) {
            boolean isRepeat = false;
            for (int i = 0; i < repeatArray.size(); i++) {
                // 获取字符串对象
                String str = repeatArray.getString(i);
                if (str.equals(message)) {
                    // 设置当前处于复读状态
                    isRepeat = true;
                    if (!((boolean) paramList.get("SuccessiveRepeat")) && message.equals(paramList.get("LastMessage"))) {
                        group.sendMessage(message);
                        // 设置连续复读标志位
                        paramList.put("SuccessiveRepeat", true);
                    }
                }
            }
            if (!isRepeat) {
                // 如果不是复读状态，那么就退出禁止复读模式
                paramList.put("SuccessiveRepeat", false);
            }
        }

        /* TODO：自动回复特殊语句 */
        if ((boolean) configList.get("SpecialMessageReply_Permission")) {
            for (int i = 0; i < specialResponseArray.size(); i++) {
                JSONObject specialResponseObject = specialResponseArray.getJSONObject(i);
                // 启动位置匹配
                boolean trig = false;
                switch (specialResponseObject.getString("trigType")) {
                    case "head": // 从头部开始查找
                        trig = message.startsWith(specialResponseObject.getString("message"));
                        break;
                    case "any": // 从任何地方开始查找
                        trig = message.contains(specialResponseObject.getString("message"));
                        break;
                    case "rear": // 从尾部开始查找
                        trig = message.endsWith(specialResponseObject.getString("message"));
                        break;
                }
                // 启动关键字匹配
                if (trig) {
                    trig = false; // 先预设假
                    JSONArray containMessage = specialResponseObject.getJSONArray("containMessage");
                    if (containMessage.size() == 0) {
                        // 如果什么都没写，那么就直接进行字符串检查
                        trig = message.equals(specialResponseObject.getString("message"));
                    } else {
                        // 如果写了什么，那么就进行匹配
                        for (int j = 0; j < containMessage.size(); j++) {
                            // 获取每一个字符串对象
                            String containMessageStr = containMessage.getString(j);
                            if (containMessageStr.isEmpty()) {
                                // 如果有一个空字符串，表示任意匹配均可通过，直接通过
                                trig = true;
                                break;
                            }
                            // 为了支持与操作，进行字符串分割
                            String[] andMessage = containMessageStr.split("&");
                            for (String str : andMessage) {
                                // 对于分割出的每一个地方进行检测，如果为假则立刻退出
                                if (!(trig = message.contains(str))) {
                                    break;
                                }
                            }
                            // 如果是真那么就匹配成功，直接退出循环
                            if (trig) {
                                break;
                            }
                        }
                    }
                }
                // 检查前面的匹配是否成功匹配
                if (trig) {
                    JSONArray replyArray = specialResponseObject.getJSONArray("reply");
                    JSONArray specialReplyArray = specialResponseObject.getJSONArray("specialReply");
                    // 判断是否为特殊用户
                    for (int j = 0; j < specialUserArray.size(); j++) {
                        if (event.getSender().getId() == specialUserArray.getLong(j)) {
                            replyArray.addAll(specialReplyArray);
                            break;
                        }
                    }
                    if (!specialResponseObject.getBoolean("randReply")) {
                        int replyIndex = new Random().nextInt(replyArray.size());
                        group.sendMessage(replyArray.getString(replyIndex));
                    } else {
                        if (new Random().nextBoolean()) {
                            int replyIndex = new Random().nextInt(replyArray.size());
                            group.sendMessage(replyArray.getString(replyIndex));
                        }
                    }
                }
            }
        }

        paramList.put("LastMessage", message);
    }

    @Override
    public void onDelete() {

    }
}
