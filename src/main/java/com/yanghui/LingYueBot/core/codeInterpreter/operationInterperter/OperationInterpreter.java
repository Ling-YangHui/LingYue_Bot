package com.yanghui.LingYueBot.core.codeInterpreter.operationInterperter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yanghui.LingYueBot.core.coreDatabaseUtil.OperationDatabaseUtil;
import com.yanghui.LingYueBot.core.coreDatabaseUtil.ResourceDatabaseUtil;
import com.yanghui.LingYueBot.core.coreDatabaseUtil.UserDatabaseUtil;
import com.yanghui.LingYueBot.functions.*;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OperationInterpreter {

    public static void execute(JSONObject replyObject, int num, MessageEvent event, Contact contact, HashMap<String, Object> FunctionMap, long groupID) {
        JSONArray reply = replyObject.getJSONArray("reply");
        JSONArray operation = replyObject.getJSONArray("operation");
        String conditionStr = replyObject.getJSONArray("condition").getString(num);
        String regex = "(\\[[^\\]]*\\])";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(conditionStr);
        List<String> conditionList = new ArrayList<>();
        while (matcher.find()) {
            conditionList.add(matcher.group().substring(1, matcher.group().length() - 1));
        }
        if (!conditionList.get(2).isEmpty()) {
            if (conditionList.get(2).equals("RAND")) {
                executeReply(replyObject, reply.getString(new Random().nextInt(reply.size())), event, contact, FunctionMap, groupID);
            } else {
                String[] replyStrList = conditionList.get(2).split(",");
                for (String s : replyStrList) {
                    executeReply(replyObject, reply.getString(Integer.parseInt(s.trim())), event, contact, FunctionMap, groupID);
                }
            }
        }
        if (!conditionList.get(3).isEmpty()) {
            String[] operationStrList = conditionList.get(3).split(",");
            for (String s : operationStrList) {
                s = s.trim();
                executeOperation(operation.getString(Integer.parseInt(s)), event, contact, FunctionMap, groupID);
            }
        }
    }

    public static void execute(JSONObject replyObject, int num, MessageEvent event, HashMap<String, Object> functionMap, long groupID) {
        execute(replyObject, num, event, event.getSender(), functionMap, groupID);
    }

    public static void executeReply(JSONObject replyObject, String replyStr, MessageEvent event, Contact contact, HashMap<String, Object> FunctionMap, long groupID) {
        String regex = "(\\[[^\\]]*\\])";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(replyStr);
        JSONArray operation = replyObject.getJSONArray("operation");
        List<String> replyFunc = new ArrayList<>();
        while (matcher.find()) {
            replyFunc.add(matcher.group().substring(1, matcher.group().length() - 1));
        }
        executeReply(replyFunc.get(0), contact, groupID);
        String[] operationList = replyFunc.get(1).split(",");
        for (String str : operationList) {
            try {
                executeOperation(operation.getString(Integer.parseInt(str.trim())), event, contact, FunctionMap, groupID);
            } catch (NumberFormatException ignore) {
            }
        }
    }

    public static void executeReply(String replyStr, Contact contact, long groupID) {
        String[] replyList = replyStr.split("&&");
        for (String str : replyList) {
            String[] reply = str.split("&");
            MessageChainBuilder builder = new MessageChainBuilder();
            for (String replyItem : reply) {
                replyItem = replyItem.trim();
                if (replyItem.startsWith("TEXT:")) {
                    String text = replyItem.substring(5);
                    text = text.replace("\\n", "\n");
                    builder.add(new PlainText(text));
                }
                if (replyItem.startsWith("FACE:")) {
                    builder.add(new Face(Integer.parseInt(replyItem.substring(5))));
                }
                if (replyItem.startsWith("AT:")) {
                    builder.add(new At(Long.parseLong(replyItem.substring(3))));
                }
                if (replyItem.startsWith("IMG:")) {
                    System.out.println(replyItem.substring(4));
                    try {
                        String type = ResourceDatabaseUtil.getResource(Integer.parseInt(replyItem.substring(4).trim()), groupID);
                        SendPictures.sendPictures(type, contact, groupID);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (builder.size() != 0)
                contact.sendMessage(builder.asMessageChain());
        }
    }

    public static void executeOperation(String operation, MessageEvent event, Contact contact, HashMap<String, Object> functionMap, long groupID) {

        long operationID;
        try {
            operationID = OperationDatabaseUtil.insertOperation(operation, event, groupID);
        } catch (SQLException e) {
            e.printStackTrace();
            contact.sendMessage("数据库错误，指令执行失败");
            return;
        }

        String[] instructionList = operation.split(" ");
        MessageChainBuilder response = new MessageChainBuilder(128);

        switch (instructionList[0]) {
            /* TODO: 好感变化*/
            case "Like":
                // 表示好感度变化
                int movingLimit = 5;
                switch (instructionList[2]) {
                    case "-Small":
                        movingLimit = 5;
                        break;
                    case "-Mid":
                        movingLimit = 10;
                        break;
                    case "-Large":
                        movingLimit = 15;
                        break;
                }
                int movingNum = new Random().nextInt(movingLimit);
                switch (instructionList[1]) {
                    case "-Float":
                        movingNum -= movingLimit / 2;
                        break;
                    case "-Decline":
                        movingNum *= -1;
                }
                try {
                    UserDatabaseUtil.setUserInt(event.getSender().getId(), "favor",
                            UserDatabaseUtil.getUserInt(event.getSender().getId(), "favor") + movingNum);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

            /* TODO: 获取数值 */
            case "Get":
                switch (instructionList[1]) {
                    case "-Like":
                        int like;
                        try {
                            like = UserDatabaseUtil.getUserInt(event.getSender().getId(), "favor");
                        } catch (SQLException e) {
                            e.printStackTrace();
                            break;
                        }
                        response.add("，LingYue对你的好感度是" + like);
                        if (like < 50) {
                            response.add("，LingYue对你没什么感觉");
                        } else if (like < 100) {
                            response.add("，LingYue好像有点在意你呢~");
                        } else if (like < 150) {
                            response.add("，LingYue似乎挺喜欢和你在一起");
                        } else if (like < 200) {
                            response.add("，LingYue已经和你很亲密了！");
                        } else {
                            response.add("，LingYue最……最喜欢你了~");
                        }
                        contact.sendMessage(response.asMessageChain());
                        break;
                }
                break;

            /* TODO: 漂流瓶 */
            case "DriftBottle":
                switch (instructionList[1]) {
                    case "-GET":
                        JSONObject driftBottle = null;
                        try {
                            if (instructionList[2].equals("Local"))
                                driftBottle = ((DriftBottle) functionMap.get("DriftBottle")).getDriftBottle();
                            else if (instructionList[2].equals("Global"))
                                driftBottle = DriftBottle.getDriftBottleAll();
                        } catch (SQLException e) {
                            contact.sendMessage("这片海里，什么都没有呢");
                            break;
                        }
                        if (driftBottle == null) {
                            contact.sendMessage("这片海里，什么都没有呢");
                        }
                        long day, hour, minute;
                        try {
                            assert driftBottle != null;
                            long sendTime = driftBottle.getLong("sendTime");
                            long nowTime = new Date().getTime();
                            long timeSpace = nowTime - sendTime;
                            day = timeSpace / 1000 / 3600 / 24;
                            hour = (timeSpace % (1000 * 3600 * 24)) / (1000 * 3600);
                            minute = (timeSpace % (1000 * 3600)) / (1000 * 60);
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }
                        response.add(new PlainText("你收到了一条" + (day > 0 ? (day + "天") : "") + hour + "小时" + minute + "分钟前的漂流瓶\n"));
                        response.add(new At(event.getSender().getId()));
                        response.add(new PlainText(" " + driftBottle.getString("message")));
                        contact.sendMessage(response.asMessageChain());
                        break;
                    case "-ADD":
                        driftBottle = new JSONObject();
                        driftBottle.put("sender", event.getSender().getNick());
                        driftBottle.put("senderID", event.getSender().getId());
                        if (event.getMessage().contentToString().length() > 180) {
                            contact.sendMessage("数据超长");
                            break;
                        }
                        if (instructionList[2].equals("Local")) {
                            if (!event.getMessage().contentToString().replace("@3598326822 丢瓶子", "").trim().isEmpty())
                                driftBottle.put("message", event.getMessage().contentToString().replace("@3598326822 丢瓶子", ""));
                            try {
                                ((DriftBottle) functionMap.get("DriftBottle")).addDriftBottle(driftBottle, operationID);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                contact.sendMessage("数据库错误，丢瓶子失败");
                            }
                        } else if (instructionList[2].equals("Global")) {
                            driftBottle.put("message", event.getMessage().contentToString().replace("@3598326822 丢大瓶子", ""));
                            if (!event.getMessage().contentToString().replace("@3598326822 丢大瓶子", "").trim().isEmpty()) {
                                try {
                                    DriftBottle.addDriftBottleALL(driftBottle, operationID);
                                } catch (SQLException e) {
                                    contact.sendMessage("数据库错误，丢瓶子失败");
                                }
                            }
                        }
                        break;
                }
                break;
            /* TODO: 化学式配平 */

            case "Balance": {
                String messageContent = event.getMessage().contentToString();
                messageContent = messageContent.replace("@3598326822 配平 ", "");
                if (!messageContent.isEmpty()) {
                    try {
                        String result = BalanceChemistry.balanceChemistry(messageContent);
                        contact.sendMessage(result);
                    } catch (Exception e) {
                        contact.sendMessage("出错了！");
                    }
                }
                break;
            }
            /* TODO: 抽卡 */

            case "RandCard": {
                String messageContent = event.getMessage().contentToString();
                messageContent = messageContent.replace("@3598326822 抽卡 ", "");
                int randNum;
                try {
                    randNum = Integer.parseInt(messageContent);
                    Vector<Vector<String>> result;
                    if (instructionList[1].equals("Normal"))
                        result = ArknightsRandCard.rand(randNum, 0);
                    else
                        result = ArknightsRandCard.rand(randNum, 0.5);
                    response.add(new QuoteReply(event.getMessage()));
                    response.add(new At(event.getSender().getId()));

                    if (randNum <= 20)
                        response.add(new PlainText("抽卡结果为\n六星:\n" + result.get(1) + "\n五星:" + result.get(2) + "\n四星:" + result.get(3) + "\n三星:" + result.get(4)));
                    else
                        response.add(new PlainText("抽卡结果为\n六星:\n" + result.get(1) + "\n五星:" + result.get(2) + "\n四星:" + result.get(3).size() + "个\n三星:" + result.get(4).size() + "个"));

                    int i = 1;
                    for (String name : result.get(0)) {
                        if (ArknightsRandCard.isSixStar(name)) {
                            response.add(new PlainText("\n在第" + i + "个抽到六星: " + name));
                        }
                        i++;
                    }
                    contact.sendMessage(response.asMessageChain());
                    if ((double) randNum / result.get(1).size() < 30) {
                        response.clear();
                        response.add(new At(event.getSender().getId()));
                        response.add(new PlainText(" 快红！"));
                        contact.sendMessage(response.asMessageChain());
                    }
                    ArknightsRandCard.addRandRecord(operationID, event, result);
                } catch (Exception e) {
                    response.add(new At(event.getSender().getId()));
                    response.add("发生错误了，请检查格式或者抽卡数量");
                    contact.sendMessage(response.asMessageChain());
                }
                break;
            }

            /* TODO: 卫星测控 */
            case "Satellite":
                response.add(new At(event.getSender().getId()));
                try {
                    response.add(new PlainText("\n" + SatelliteGetPosition.satelliteGetPosition(instructionList[1])));
                } catch (IOException e) {
                    response.add(new PlainText("\n发生错误了"));
                }
                contact.sendMessage(response.asMessageChain());
                break;

            /* TODO: 萌萌的图片 */
            case "MoePic":
                try {
                    SendPictures.sendPicturesFromInternet(contact);
                } catch (Exception e) {
                    contact.sendMessage("图片下载错误");
                }
                break;

            /* TODO: 获取种子 */
            case "RandSeed":
                try {
                    response.add(new At(event.getSender().getId()));
                    response.add(new PlainText(" " + RandSeed.randSeed()));
                } catch (SQLException e) {
                    contact.sendMessage("数据库错误");
                    return;
                }
                contact.sendMessage(response.asMessageChain());
                break;
        }
        /* TODO: 我他妈的好想和
         *           草海龙做爱啊*/
    }
}
