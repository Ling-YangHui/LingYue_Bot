package com.yanghui.LingYueBot.Template;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yanghui.LingYueBot.UserHandler.ParseUser;
import com.yanghui.LingYueBot.core.codeInterpreter.conditionInterpreter.ConditionInterpreter;
import com.yanghui.LingYueBot.core.codeInterpreter.operationInterperter.OperationInterpreter;
import com.yanghui.LingYueBot.core.coreTools.JsonLoader;
import com.yanghui.LingYueBot.core.messageHandler.GroupMessageHandler;
import com.yanghui.LingYueBot.functions.DriftBottle;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class GroupHandler extends GroupMessageHandler {
    // 图片路径
    public String picPath = "D:\\IntelliJ IDEA programming\\MiraiRobot\\MiraiResources\\LingYue_resources\\pictureSrc";

    public GroupHandler(String path) {
        super(path);
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
                try {
                    onDelete();
                    onLoad();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < scheduleTaskList.size(); i++) {
                    if (nowTime.equals(scheduleTaskList.getJSONObject(i).getString("time"))) {
                        if (group != null) {
                            OperationInterpreter.executeReply(scheduleTaskList.getJSONObject(i).getString("message"), group);
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
        administratorHandler(event);

        if (!checkState("OnActive"))
            return;

        addAndChangeUser(event);

        if (checkState("SuccessiveRepeat_Permission"))
            autoRepeat(event);

        if (checkState("SpecialMessageReply_Permission"))
            /* TODO：自动回复特殊语句 */
            reply(event);

        paramList.put("LastMessage", messageContent);
    }

    @Override
    public void onDelete() throws Exception {
        JsonLoader.saveJSONObject(rootPath, "user.json", userList);
        driftBottle.saveDriftBottle(rootPath, "driftBottle.json");
    }

    public void onLoad() throws Exception {
        /* TODO：json读取 */
        // 读取自动复读
        repeatList = JsonLoader.jsonArrayLoader(rootPath + "repeatList.json");
        // 读取特殊语句回复
        replyList = JsonLoader.jsonArrayLoader(rootPath + "specialRepeatList.json");
        replyList.addAll(JsonLoader.jsonArrayLoader("D:\\IntelliJ IDEA programming\\MiraiRobot\\MiraiResources\\LingYue_resources\\Group\\globalSpecialRepeat.json"));
        // 读取用户信息
        userList = JsonLoader.jsonObjectLoader(rootPath + "user.json");
        // 读取定时任务列表
        scheduleTaskList = JsonLoader.jsonArrayLoader(rootPath + "scheduleTask.json");
        // 读取漂流瓶
        driftBottle = new DriftBottle(JsonLoader.jsonArrayLoader(rootPath + "driftBottle.json"));
        functionMap.put("DriftBottle", driftBottle);
    }

    public void administratorHandler(GroupMessageEvent event) {
        MessageChain message = event.getMessage();
        String messageContent = event.getMessage().contentToString();
        group = event.getGroup();
        long senderID = event.getSender().getId();
        /* TODO：最高优先级——管理员任务 */
        if (senderID == 2411046022L) {

        }
    }

    public boolean checkState(String key) {
        return (boolean) configList.get(key);
    }

    public void addAndChangeUser(GroupMessageEvent event) {
        MessageChain message = event.getMessage();
        String messageContent = event.getMessage().contentToString();
        group = event.getGroup();
        long senderID = event.getSender().getId();
        /* 数据列表存在性认证 */
        if (userList.get(senderID + "") == null)
            userList.put(senderID + "", ParseUser.ParseJSONFromUser(event.getSender()));
        else if (!userList.getJSONObject(senderID + "").getString("nick").equals(event.getSenderName())) {
            userList.getJSONObject(senderID + "").put("nick", event.getSenderName());
            System.out.println("添加用户" + event.getSenderName());
        }

        /* 列表数据修正 */
        userList.getJSONObject(senderID + "").put("isAdministrator", userList.getJSONObject(senderID + "").getIntValue("like") > 200);
    }

    public void autoRepeat(GroupMessageEvent event) {
        MessageChain message = event.getMessage();
        String messageContent = event.getMessage().contentToString();
        group = event.getGroup();
        long senderID = event.getSender().getId();
        /* TODO：自动复读 */

        boolean isRepeat = false;
        for (int i = 0; i < repeatList.size(); i++) {
            // 获取字符串对象
            String str = repeatList.getString(i);
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

    public void reply(GroupMessageEvent event) {
        Message message = event.getMessage();
        String messageText = message.contentToString();
        long senderID = event.getSender().getId();
        JSONObject replyObject;
        for (int i = 0; i < replyList.size(); i++) {
            replyObject = replyList.getJSONObject(i);
            JSONArray trigMessage = replyObject.getJSONArray("trigMessage");
            boolean hasTrig = false;
            for (int j = 0; j < trigMessage.size(); j++) {
                if (messageText.contains(trigMessage.getString(j))) {
                    hasTrig = true;
                    break;
                }
            }
            if (!hasTrig)
                continue;
            boolean conditionSatisfied = false;
            int satisfiedNum = -1;
            for (int j = 0; j < replyObject.getJSONArray("condition").size(); j++) {
                conditionSatisfied = ConditionInterpreter.getConditionSatisfied(replyObject.getJSONArray("condition"), j, event, userList, replyList, configList);
                if (conditionSatisfied) {
                    satisfiedNum = j;
                    break;
                }
            }
            if (!conditionSatisfied)
                continue;
            OperationInterpreter.execute(replyObject, satisfiedNum, event, userList.getJSONObject(senderID + ""), group, functionMap);
        }
    }
}
