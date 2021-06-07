package com.yanghui.LingYueBot.Template;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yanghui.LingYueBot.core.codeInterpreter.conditionInterpreter.ConditionInterpreter;
import com.yanghui.LingYueBot.core.codeInterpreter.operationInterpreter.OperationInterpreter;
import com.yanghui.LingYueBot.core.coreDatabaseUtil.*;
import com.yanghui.LingYueBot.core.coreTools.ExtractSingleMessage;
import com.yanghui.LingYueBot.core.messageHandler.GroupMessageHandler;
import com.yanghui.LingYueBot.functions.DailyReport;
import com.yanghui.LingYueBot.functions.DriftBottle;
import com.yanghui.LingYueBot.functions.GetSystemInfo;
import com.yanghui.LingYueBot.functions.Repeat;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class GroupHandler extends GroupMessageHandler {

    private final Object replyListLock = new Object();
    private final Object repeatListLock = new Object();
    private Vector<Long> userList;
    private JSONArray replyList;
    private JSONArray repeatList;

    // 图片路径
    public GroupHandler(long groupID) {
        super(groupID);
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
                    synchronized (replyListLock) {
                        replyList = ReplyDatabaseUtil.getReply(groupID);
                    }
                    synchronized (repeatListLock) {
                        repeatList = Repeat.getRepeat(groupID);
                    }
                    JSONArray scheduleArray = ScheduleDatabaseUtil.getSchedule(groupID);
                    for (int i = 0; i < scheduleArray.size(); i++) {
                        if (nowTime.equals(scheduleArray.getJSONObject(i).getString("time"))) {
                            if (group != null) {
                                OperationInterpreter.executeReply(scheduleArray.getJSONObject(i).getString("message"), group);
                            }
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
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
        administratorInstructionHandler(event);
        userInstructionHandler(event);

        if (!checkState("OnActive"))
            return;

        addAndChangeUser(event);
        try {
            if (UserDatabaseUtil.getUserBoolean(senderID, "isForbidden")) {
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        if (checkState("SuccessiveRepeat_Permission"))
            autoRepeat(event);

        if (checkState("SpecialMessageReply_Permission"))
            /* TODO：自动回复特殊语句 */
            reply(event);

        paramList.put("LastMessage", messageContent);
    }

    @Override
    public void onDelete() throws Exception {

    }

    public void onLoad() throws Exception {
        /* TODO：json读取 */
        driftBottle = new DriftBottle(groupID);
        functionMap.put("DriftBottle", driftBottle);

        userList = UserDatabaseUtil.getUserList();
        replyList = ReplyDatabaseUtil.getReply(groupID);
        repeatList = Repeat.getRepeat(groupID);
    }

    public void administratorInstructionHandler(GroupMessageEvent event) {
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
                        group.sendMessage("重置完毕");
                    } catch (Exception e) {
                        group.sendMessage("呜~数据读取错误了呢");
                    }
                    break;
                case "LingYue -traceBack":
                    try {
                        onLoad();
                        group.sendMessage("回溯完毕");
                    } catch (Exception e) {
                        group.sendMessage("呜~数据读取错误了呢");
                    }
                    break;
                case "LingYue -getSysStatus":
                    group.sendMessage("获取系统数据中...");
                    Vector<String> info = GetSystemInfo.getSystemInfo();
                    StringBuilder builder = new StringBuilder();
                    if (info == null) {
                        group.sendMessage("获取失败");
                        break;
                    }
                    for (String s : info)
                        builder.append(s).append("\n");
                    group.sendMessage(builder.toString());
                    break;
                case "LingYue -getStatus":
                    StringBuilder str = new StringBuilder();
                    for (String key : configList.keySet()) {
                        str.append(key).append(": ").append(configList.get(key)).append('\n');
                    }
                    group.sendMessage(str.toString());
                    break;
                case "LingYue -getBottle":
                    try {
                        group.sendMessage("海里还有" + driftBottle.getBottleNum() + "个瓶子");
                    } catch (SQLException e) {
                        group.sendMessage("数据库错误");
                    }
                    break;
                case "LingYue -close":
                    configList.put("OnActive", false);
                    break;
                case "LingYue -open":
                    configList.put("OnActive", true);
                    break;
                case "LingYue -open pic":
                    configList.put("Picture_Permission", true);
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
                case "LingYue -report":
                    try {
                        DailyReport.dailyReport(this);
                    } catch (SQLException e) {
                        group.sendMessage("读取报表出错");
                    }
                    break;
            }
            if (messageContent.contains("LingYue -ban")) {
                String[] list = messageContent.split(" ");
                try {
                    UserDatabaseUtil.setUserBoolean(Long.parseLong(list[2]), "isForbidden", true);
                } catch (SQLException e) {
                    group.sendMessage("设置失败");
                }
            } else if (messageContent.contains("LingYue -unban")) {
                String[] list = messageContent.split(" ");
                try {
                    UserDatabaseUtil.setUserBoolean(Long.parseLong(list[2]), "isForbidden", false);
                } catch (SQLException e) {
                    group.sendMessage("设置失败");
                }
            }
            if (messageContent.contains("LingYue -inputResource")) {
                try {
                    short type = Short.parseShort(messageContent.split("@")[1]);
                    Image image = ExtractSingleMessage.extractImage(event.getMessage()).get(0);
                    ResourceDatabaseUtil.inputResource(image, type);
                    group.sendMessage("写入成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e.getClass().equals(SQLException.class)) {
                        group.sendMessage("数据库写入错误");
                    } else
                        group.sendMessage("连接查询错误");
                }
            }
            if (messageContent.contains("LingYue -execute SQL")) {
                String sql = messageContent.substring(20);
                try {
                    PreparedStatement statement = BaseDatabaseUtil.getStatement(sql);
                    statement.execute();
                    statement.close();
                    group.sendMessage("执行完毕");
                } catch (SQLException e) {
                    group.sendMessage("执行错误");
                }
            }
        }
    }

    public void userInstructionHandler(GroupMessageEvent event) {
        Message message = event.getMessage();
        String messageContent = message.contentToString();
        long senderID = event.getSender().getId();
        if (messageContent.contains("LingYue -remove bottle")) {
            System.out.println(messageContent.split(" ", 4)[3]);
            Vector<JSONObject> remove;
            try {
                remove = driftBottle.removeBottle(messageContent.split(" ", 4)[3], event.getSender().getId());
                group.sendMessage("---删除列表---\n" + remove);
            } catch (SQLException e) {
                group.sendMessage("数据库错误，删除失败");
            }
        }
    }

    public boolean checkState(String key) {
        return (boolean) configList.get(key);
    }

    public void addAndChangeUser(GroupMessageEvent event) {
        MessageChain message = event.getMessage();
        String messageContent = event.getMessage().contentToString();
        group = event.getGroup();
        User user = event.getSender();
        long senderID = event.getSender().getId();

        /* 数据库操作 */
        try {
            if (!userList.contains(senderID)) {
                UserDatabaseUtil.insertUser(event.getSender());
                userList.add(senderID);
                System.out.println("INSERT USER" + event.getSenderName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void autoRepeat(GroupMessageEvent event) {
        MessageChain message = event.getMessage();
        String messageContent = event.getMessage().contentToString();
        group = event.getGroup();
        long senderID = event.getSender().getId();
        /* TODO：自动复读 */

        boolean isRepeat = false;
        synchronized (repeatListLock) {
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
    }

    public void reply(GroupMessageEvent event) {
        Message message = event.getMessage();
        String messageText = message.contentToString();
        long senderID = event.getSender().getId();
        synchronized (replyListLock) {
            for (int i = 0; i < replyList.size(); i++) {
                JSONObject replyObject = replyList.getJSONObject(i);
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
                    conditionSatisfied = ConditionInterpreter.getConditionSatisfied(replyObject.getJSONArray("condition"), j, event, configList);
                    if (conditionSatisfied) {
                        satisfiedNum = j;
                        break;
                    }
                }
                if (!conditionSatisfied)
                    continue;
                OperationInterpreter.execute(replyObject, satisfiedNum, event, group, functionMap);
            }
        }
    }
}
