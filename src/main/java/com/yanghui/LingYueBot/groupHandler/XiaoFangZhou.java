package com.yanghui.LingYueBot.groupHandler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yanghui.LingYueBot.core.coreTools.FunctionHandler;
import com.yanghui.LingYueBot.core.coreTools.JsonLoader;
import com.yanghui.LingYueBot.core.coreTools.MessageBuilder;
import com.yanghui.LingYueBot.core.coreTools.UserDataHandler;
import com.yanghui.LingYueBot.core.messageHandler.GroupMessageHandler;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class XiaoFangZhou extends GroupMessageHandler {

    public final HashMap<String, Object> configList = new HashMap<>();
    public final Vector<File> sexPictureArray = new Vector<>();
    private final HashMap<String, Object> paramList = new HashMap<>();
    private final HashMap<Long, UserDataHandler> userArray = new HashMap<>();
    private final String rootPath = "D:\\IntelliJ IDEA programming\\MiraiRobot\\MiraiResources\\LingYue_resources\\";
    public String picPath = "D:\\IntelliJ IDEA programming\\MiraiRobot\\MiraiResources\\LingYue_resources\\XiaoFangZhou\\pictureSrc";
    private JSONArray repeatArray;
    private JSONArray specialResponseArray;
    private JSONArray scheduleTaskArray;

    public XiaoFangZhou() {
        paramList.put("SuccessiveRepeat", false);
        paramList.put("LastMessage", "");
        configList.put("SuccessiveRepeat_Permission", true);
        configList.put("SpecialMessageReply_Permission", true);
        configList.put("Picture_Permission", true);
        configList.put("OnActive", true);
    }

    @Override
    public void onCreate() throws Exception {
        onLoad();
        /* TODO：启动定时任务 */
        // 获取当前系统日期
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Timer scheduleTimer = new Timer();
        final String[] pastTime = {""};
        scheduleTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                String nowTime = sdf.format(new Date());
                if (nowTime.equals(pastTime[0])) {
                    return;
                }
                System.out.println(nowTime);
                for (int i = 0; i < scheduleTaskArray.size(); i++) {
                    if (nowTime.equals(scheduleTaskArray.getJSONObject(i).getString("time"))) {
                        if (group != null) {
                            group.sendMessage(MessageBuilder.MessageBuild(scheduleTaskArray.getJSONObject(i).getString("message"), null));
                        }
                    }
                }
                pastTime[0] = nowTime;
            }
        }, 10, 1000 * 30);
    }

    @Override
    public void onHandleMessage(GroupMessageEvent event) {
        MessageChain message = event.getMessage();
        String messageContent = event.getMessage().contentToString();
        group = event.getGroup();
        long senderID = event.getSender().getId();
        /* TODO：最高优先级——管理员任务 */
        if (senderID == 2411046022L) {
            switch (messageContent) {
                case "LingYue滚回去卷吧":
                    configList.put("OnActive", false);
                    group.sendMessage("呜~这就去卷");
                    break;
                case "LingYue -reload":
                    try {
                        onDelete();
                        onLoad();
                        group.sendMessage("LingYue的里面，已经被换成新的形状了呢~");
                    } catch (Exception e) {
                        group.sendMessage("呜~数据读取错误了呢");
                    }
                    break;
                case "LingYue -traceBack":
                    try {
                        onLoad();
                        group.sendMessage("咻~回溯完毕");
                    } catch (Exception e) {
                        group.sendMessage("呜~数据读取错误了呢");
                    }
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
                    group.sendMessage("呜呜呜，我再也不复读了");
                    break;
                case "LingYue -close pic":
                    configList.put("Picture_Permission", false);
                    group.sendMessage("呜呜呜，阳姐姐我再也不ghs了");
                    break;
                case "LingYue -open pic":
                    configList.put("Picture_Permission", true);
                    break;
                case "LingYue -open repeat":
                    configList.put("SuccessiveRepeat_Permission", true);
                    break;
                case "LingYue -close special":
                    configList.put("SpecialMessageReply_Permission", false);
                    group.sendMessage("呜呜呜，我再也不说怪话了");
                    break;
                case "LingYue -open special":
                    configList.put("SpecialMessageReply_Permission", true);
                    break;
            }
        }
        if (!(boolean) configList.get("OnActive"))
            return;

        /* 数据列表存在性认证 */
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

        /* 列表数据修正 */
        userArray.get(senderID).userIsSpecial = userArray.get(senderID).userLike > 200;

        /* TODO：自动复读 */
        if ((boolean) configList.get("SuccessiveRepeat_Permission")) {
            boolean isRepeat = false;
            for (int i = 0; i < repeatArray.size(); i++) {
                // 获取字符串对象
                String str = repeatArray.getString(i);
                if (str.equals(messageContent)) {
                    // 设置当前处于复读状态
                    isRepeat = true;
                    if (!((boolean) paramList.get("SuccessiveRepeat")) && messageContent.equals(paramList.get("LastMessage"))) {
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
                        trig = messageContent.startsWith(specialResponseObject.getString("message"));
                        break;
                    case "any": // 从任何地方开始查找
                        trig = messageContent.contains(specialResponseObject.getString("message"));
                        break;
                    case "rear": // 从尾部开始查找
                        trig = messageContent.endsWith(specialResponseObject.getString("message"));
                        break;
                }
                // 启动关键字匹配
                if (trig) {
                    trig = false; // 先预设假
                    JSONArray containMessage = specialResponseObject.getJSONArray("containMessage");
                    if (containMessage.size() == 0) {
                        // 如果什么都没写，那么就直接进行字符串检查
                        trig = messageContent.equals(specialResponseObject.getString("message"));
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
                                    if (messageContent.contains(str.substring(1))) {
                                        System.out.println(str.substring(1));
                                        trig = false;
                                        break;
                                    }
                                    continue;
                                }
                                if (!(trig = messageContent.contains(str))) {
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
                            FunctionHandler.xiaoFangZhouFunction(function, userArray.get(senderID), event, this);
                        }
                    } else {
                        JSONArray functionArray = specialResponseObject.getJSONArray("function");
                        for (int j = 0; j < functionArray.size(); j++) {
                            String function = functionArray.getString(j);
                            FunctionHandler.xiaoFangZhouFunction(function, userArray.get(senderID), event, this);
                        }
                    }
                    System.out.println(!specialResponseObject.getBoolean("randReply")
                            || (specialResponseObject.getBoolean("randReply") && new Random().nextBoolean()));
                    if (!specialResponseObject.getBoolean("randReply")
                            || (specialResponseObject.getBoolean("randReply") && new Random().nextBoolean())) {
                        if (!isSpecialUser && replyArray.size() != 0) {
                            int replyIndex = new Random().nextInt(replyArray.size());
                            group.sendMessage(MessageBuilder.MessageBuild(
                                    replyArray.getString(replyIndex), event));
                        } else if (replyArray.size() + specialReplyArray.size() != 0) {
                            int replyIndex = new Random().nextInt(replyArray.size() + specialReplyArray.size());
                            if (replyIndex < replyArray.size())
                                group.sendMessage(MessageBuilder.MessageBuild(
                                        replyArray.getString(replyIndex), event));
                            else
                                group.sendMessage(MessageBuilder.MessageBuild(
                                        specialReplyArray.getString(replyIndex - replyArray.size()), event));
                        }
                    }
                    break;
                }
            }
        }
        paramList.put("LastMessage", messageContent);
    }

    @Override
    public void onDelete() throws Exception {
        UserDataHandler.saveJsonFile(userArray, rootPath + "XiaoFangZhou\\user.json");
    }

    public void onLoad() throws Exception {
        /* TODO：json读取 */
        // 读取自动复读
        repeatArray = JsonLoader.jsonArrayLoader(rootPath + "XiaoFangZhou\\repeatList.json", this);
        // 读取特殊语句回复
        specialResponseArray = JsonLoader.jsonArrayLoader(rootPath + "XiaoFangZhou\\specialRepeatList.json", this);
        // 读取用户信息
        JSONArray userJsonArray = JsonLoader.jsonArrayLoader(rootPath + "XiaoFangZhou\\user.json", this);
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
        scheduleTaskArray = JsonLoader.jsonArrayLoader(rootPath + "XiaoFangZhou\\scheduleTask.json", this);
        // 读取涩图
        synchronized (sexPictureArray) {
            sexPictureArray.clear();
            File[] pictures = new File(picPath).listFiles();
            if (pictures == null) {
                throw new Exception("图片加载错误");
            }
            sexPictureArray.addAll(Arrays.asList(pictures));
        }
    }
}
