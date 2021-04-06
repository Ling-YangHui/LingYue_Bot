package com.yanghui.LingYueBot.handlerTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yanghui.LingYueBot.core.coreTools.FunctionHandler;
import com.yanghui.LingYueBot.core.coreTools.JsonLoader;
import com.yanghui.LingYueBot.core.coreTools.UserDataHandler;
import com.yanghui.LingYueBot.core.messageHandler.GroupMessageHandler;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.text.SimpleDateFormat;
import java.util.*;

/*
* 这是基于XiaoFangZhou开发的一个模板，其中的UserDataHandler可以继承重写哦
* */

public abstract class Template_1 extends GroupMessageHandler {

    private final HashMap<String, Object> paramList = new HashMap<>();
    private final HashMap<String, Object> configList = new HashMap<>();
    private final HashMap<Long, UserDataHandler> userArray = new HashMap<>();
    private final String rootPath = "D:\\IntelliJ IDEA programming\\MiraiRobot\\MiraiCore\\plugins\\LingYue_resources\\";
    private JSONArray repeatArray;
    private JSONArray specialResponseArray;
    private JSONArray scheduleTaskArray;
    private Timer scheduleTimer = null;
    private final String groupName;

    public Template_1(String groupName) {
        paramList.put("SuccessiveRepeat", false);
        paramList.put("LastMessage", "");
        configList.put("SuccessiveRepeat_Permission", true);
        configList.put("SpecialMessageReply_Permission", true);
        configList.put("OnActive", true);
        this.groupName = groupName;
    }

    @Override
    public abstract void onCreate() throws Exception;

    @Override
    public abstract void onHandleMessage(GroupMessageEvent event);

    @Override
    public abstract void onDelete() throws Exception;

    public void onLoad() throws Exception {
        /* TODO：json读取 */
        // 读取自动复读
        repeatArray = JsonLoader.jsonArrayLoader(rootPath + groupName + "\\repeatList.json");
        // 读取特殊语句回复
        specialResponseArray = JsonLoader.jsonArrayLoader(rootPath + groupName + "\\specialRepeatList.json");
        // 读取用户信息
        JSONArray userJsonArray = JsonLoader.jsonArrayLoader(rootPath + groupName + "\\user.json");
        for (int i = 0; i < userJsonArray.size(); i++) {
            JSONObject userObject = userJsonArray.getJSONObject(i);
            this.userArray.put(userObject.getLong("userID"),
                    new UserDataHandler(
                            userObject.getLong("userID"),
                            userObject.getIntValue("like"),
                            userObject.getBoolean("isSpecialUser"),
                            userObject.getBoolean("isAdministrator"),
                            userObject.getIntValue("hasFuck"),
                            userObject.getString("name")
                    ));
        }
        // 读取定时任务列表
        scheduleTaskArray = JsonLoader.jsonArrayLoader(rootPath + groupName + "\\scheduleTask.json");
    }

    private void startSchedule() {
        /* TODO：启动定时任务 */
        // 获取当前系统日期
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        scheduleTimer = new Timer();
        scheduleTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                String nowTime = sdf.format(new Date());
                System.out.println(nowTime);
                for (int i = 0; i < scheduleTaskArray.size(); i++) {
                    if (nowTime.equals(scheduleTaskArray.getJSONObject(i).getString("time"))) {
                        if (group != null) {
                            group.sendMessage(scheduleTaskArray.getJSONObject(i).getString("message"));
                        }
                    }
                }
            }
        }, 10, 1000 * 30);
    }

    private void refreshUserArray(GroupMessageEvent event) {
        long senderID = event.getSender().getId();
        if (userArray.get(senderID) == null)
            userArray.put(senderID, new UserDataHandler(
                    senderID,
                    0,
                    false,
                    false,
                    0,
                    event.getSenderName()));
        else if (!userArray.get(senderID).userName.equals(event.getSenderName())) {
            userArray.get(senderID).userName = event.getSenderName();
        }
    }

    private void autoRepeat(GroupMessageEvent event) {
        String message = event.getMessage().contentToString();
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
    }

    private void autoSpecial(GroupMessageEvent event) {
        String message = event.getMessage().contentToString();
        long senderID = event.getSender().getId();
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
                                if (str.startsWith("$")) {
                                    if (message.contains(str.substring(1))) {
                                        System.out.println(str.substring(1));
                                        trig = false;
                                        break;
                                    }
                                    continue;
                                }
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
                    boolean isSpecialUser = userArray.get(senderID).userIsSpecial;
                    // 执行预定函数
                    if (isSpecialUser) {
                        JSONArray functionArray = specialResponseObject.getJSONArray("specialFunction");
                        for (int j = 0; j < functionArray.size(); j++) {
                            String function = functionArray.getString(j);
                            FunctionHandler.xiaoFangZhouFunction(function, userArray.get(senderID), event, null);
                        }
                    } else {
                        JSONArray functionArray = specialResponseObject.getJSONArray("function");
                        for (int j = 0; j < functionArray.size(); j++) {
                            String function = functionArray.getString(j);
                            FunctionHandler.xiaoFangZhouFunction(function, userArray.get(senderID), event,null);
                        }
                    }
                    if (!specialResponseObject.getBoolean("randReply")) {
                        if (!isSpecialUser && replyArray.size() != 0) {
                            // 获取一个索引
                            int replyIndex = new Random().nextInt(replyArray.size());
                            group.sendMessage(replyArray.getString(replyIndex));
                        } else if (replyArray.size() + specialReplyArray.size() != 0) {
                            // 获取一个索引，在两个Array之间切换
                            int replyIndex = new Random().nextInt(replyArray.size() + specialReplyArray.size());
                            if (replyIndex < replyArray.size())
                                group.sendMessage(replyArray.getString(replyIndex));
                            else
                                group.sendMessage(specialReplyArray.getString(replyIndex - replyArray.size()));
                        }
                    } else {
                        if (new Random().nextBoolean()) {
                            if (!isSpecialUser && replyArray.size() != 0) {
                                int replyIndex = new Random().nextInt(replyArray.size());
                                group.sendMessage(replyArray.getString(replyIndex));
                            } else if (replyArray.size() + specialReplyArray.size() != 0) {
                                int replyIndex = new Random().nextInt(replyArray.size() + specialReplyArray.size());
                                if (replyIndex < replyArray.size())
                                    group.sendMessage(replyArray.getString(replyIndex));
                                else
                                    group.sendMessage(specialReplyArray.getString(replyIndex - replyArray.size()));
                            }
                        }
                    }
                }
            }
        }

        paramList.put("LastMessage", message);
    }
}
