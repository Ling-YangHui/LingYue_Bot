package com.yanghui.lingYueBot.core.codeInterpreter.operationInterpreter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yanghui.lingYueBot.core.coreDatabaseUtil.OperationDatabaseUtil;
import com.yanghui.lingYueBot.core.coreDatabaseUtil.ResourceDatabaseUtil;
import com.yanghui.lingYueBot.core.coreDatabaseUtil.UserDatabaseUtil;
import com.yanghui.lingYueBot.core.coreUtils.ParseOperationCode;
import com.yanghui.lingYueBot.functions.APIBasedFunc.SendAIReply;
import com.yanghui.lingYueBot.functions.APIBasedFunc.SendPictures;
import com.yanghui.lingYueBot.functions.APIBasedFunc.SendTodayMotto;
import com.yanghui.lingYueBot.functions.connectPython.BalanceChemistry;
import com.yanghui.lingYueBot.functions.connectPython.SatelliteGetPosition;
import com.yanghui.lingYueBot.functions.javaBasedFunc.ArknightsRandCard;
import com.yanghui.lingYueBot.functions.javaBasedFunc.DriftBottle;
import com.yanghui.lingYueBot.functions.javaBasedFunc.RandSeed;
import com.yanghui.lingYueBot.utils.Logger;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;

import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OperationInterpreter {

    public static void execute(JSONObject replyObject, int num, MessageEvent event, Contact contact, HashMap<String, Object> FunctionMap) {
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
                executeReply(replyObject, reply.getString(new Random().nextInt(reply.size())), event, contact, FunctionMap);
            } else if (conditionList.get(2).contains("RAND")) {
                String range = conditionList.get(2).replace("RAND", "");
                int from = Integer.parseInt(range.split(",")[0].trim());
                int to = Integer.parseInt(range.split(",")[1].trim());
                executeReply(replyObject, reply.getString(new Random().nextInt(to - from) + from), event, contact, FunctionMap);
            } else {
                String[] replyStrList = conditionList.get(2).split(",");
                for (String s : replyStrList) {
                    executeReply(replyObject, reply.getString(Integer.parseInt(s.trim())), event, contact, FunctionMap);
                }
            }
        }
        if (!conditionList.get(3).isEmpty()) {
            String[] operationStrList = conditionList.get(3).split(",");
            for (String s : operationStrList) {
                s = s.trim();
                executeOperation(operation.getString(Integer.parseInt(s)), event, contact, FunctionMap, contact.getId());
            }
        }
    }

    public static void execute(JSONObject replyObject, int num, MessageEvent event, HashMap<String, Object> functionMap) {
        execute(replyObject, num, event, event.getSender(), functionMap);
    }

    public static void executeReply(JSONObject replyObject, String replyStr, MessageEvent event, Contact contact, HashMap<String, Object> FunctionMap) {
        String regex = "(\\[[^\\]]*\\])";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(replyStr);
        JSONArray operation = replyObject.getJSONArray("operation");
        List<String> replyFunc = new ArrayList<>();
        while (matcher.find()) {
            replyFunc.add(matcher.group().substring(1, matcher.group().length() - 1));
        }
        executeReply(replyFunc.get(0), contact);
        String[] operationList = replyFunc.get(1).split(",");
        for (String str : operationList) {
            try {
                executeOperation(operation.getString(Integer.parseInt(str.trim())), event, contact, FunctionMap, contact.getId());
            } catch (NumberFormatException ignore) {
            }
        }
    }

    /**
     * ??????????????????Reply API
     *
     * @param replyStr ???????????????
     * @param contact  ????????????
     */
    public static void executeReply(String replyStr, Contact contact) {
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
                        String type = ResourceDatabaseUtil.getResource(Integer.parseInt(replyItem.substring(4).trim()), contact.getId());
                        SendPictures.sendPictures(type, contact, contact.getId());
                    } catch (Exception e) {
                        Logger.logError(e);
                    }
                }
                if (replyItem.startsWith("TASK_T:")) {
                    executeOperationTimer(replyItem.substring(7), contact);
                }
            }
            if (builder.size() != 0)
                contact.sendMessage(builder.asMessageChain());
        }
    }

    public static void executeOperationTimer(String operation, Contact contact) {
        String[] instructionList = operation.split(" ");
        MessageChainBuilder response = new MessageChainBuilder(128);
        switch (instructionList[0]) {
            case "Motto" -> {
                response.add(SendTodayMotto.getTodayMotto().getContent_en() + "\n");
                response.add(SendTodayMotto.getTodayMotto().getContent_cn());
                contact.sendMessage(response.asMessageChain());
            }
        }
    }

    public static void executeOperation(String operation, MessageEvent event, Contact contact, HashMap<String, Object> functionMap, long groupID) {

        String[] instructionList;
        MessageChainBuilder response;
        long operationID;
        {

            // ??????????????????
            try {
                operationID = OperationDatabaseUtil.insertOperation(operation, event, groupID);
            } catch (SQLException e) {
                Logger.logError(e);
                contact.sendMessage("????????????????????????????????????");
                return;
            }
            Logger.logDebug("bot????????????", String.format("????????????: %s\n?????????: %d\n????????????: %d", operation, ParseOperationCode.parseOperationCode(operation), operationID));

            instructionList = operation.split(" ");
            response = new MessageChainBuilder(128);
        }
        switch (instructionList[0]) {
            /* TODO: ????????????*/
            case "Like" -> {
                // ?????????????????????
                int movingLimit = switch (instructionList[2]) {
                    case "-Mid" -> 10;
                    case "-Large" -> 15;
                    default -> 5;
                };
                int movingNum = new Random().nextInt(movingLimit);
                switch (instructionList[1]) {
                    case "-Float" -> movingNum -= movingLimit / 2;
                    case "-Decline" -> movingNum *= -1;
                }
                try {
                    UserDatabaseUtil.setUserInt(event.getSender().getId(), "favor",
                            UserDatabaseUtil.getUserInt(event.getSender().getId(), "favor") + movingNum);
                } catch (SQLException e) {
                    Logger.logError(e);
                }
            }

            /* TODO: ???????????? */
            case "Get" -> {
                switch (instructionList[1]) {
                    case "-Like" -> {
                        int like;
                        try {
                            like = UserDatabaseUtil.getUserInt(event.getSender().getId(), "favor");
                        } catch (SQLException e) {
                            Logger.logError(e);
                            break;
                        }
                        response.add("???LingYue?????????????????????" + like);
                        if (like < 50) {
                            response.add("???LingYue?????????????????????");
                        } else if (like < 100) {
                            response.add("???LingYue????????????????????????~");
                        } else if (like < 150) {
                            response.add("???LingYue??????????????????????????????");
                        } else if (like < 200) {
                            response.add("???LingYue???????????????????????????");
                        } else {
                            response.add("???LingYue????????????????????????~");
                        }
                        contact.sendMessage(response.asMessageChain());
                    }
                    case "-Balance" -> {
                    }
                }
            }

            /* TODO: ????????? */
            case "DriftBottle" -> {
                switch (instructionList[1]) {
                    case "-GET" -> {
                        JSONObject driftBottle = null;
                        try {
                            if (instructionList[2].equals("Local"))
                                driftBottle = ((DriftBottle) functionMap.get("DriftBottle")).getDriftBottle(operationID, event);
                            else if (instructionList[2].equals("Global"))
                                driftBottle = DriftBottle.getDriftBottleAll(operationID, event);
                        } catch (SQLException e) {
                            Logger.logError(e);
                            contact.sendMessage("?????????????????????????????????");
                            break;
                        }
                        if (driftBottle == null) {
                            contact.sendMessage("?????????????????????????????????");
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
                            Logger.logError(e);
                            break;
                        }
                        response.add(new PlainText("??????????????????" + (day > 0 ? (day + "???") : "") + hour + "??????" + minute + "?????????????????????, ??????#" + driftBottle.getLong("bottleID") + "\n"));
                        response.add(new At(event.getSender().getId()));
                        response.add(new PlainText(" " + driftBottle.getString("message")));
                        contact.sendMessage(response.asMessageChain());
                    }
                    case "-ADD" -> {
                        JSONObject driftBottle = new JSONObject();
                        driftBottle.put("sender", event.getSender().getNick());
                        driftBottle.put("senderID", event.getSender().getId());
                        if (event.getMessage().contentToString().length() > 180) {
                            contact.sendMessage("????????????");
                            break;
                        }
                        if (instructionList[2].equals("Local")) {
                            if (!event.getMessage().contentToString().replace("@3598326822 ?????????", "").trim().isEmpty())
                                driftBottle.put("message", event.getMessage().contentToString().replace("@3598326822 ?????????", ""));
                            try {
                                ((DriftBottle) functionMap.get("DriftBottle")).addDriftBottle(driftBottle, operationID);
                            } catch (SQLException e) {
                                Logger.logError(e);
                                contact.sendMessage("?????????????????????????????????");
                            }
                        } else if (instructionList[2].equals("Global")) {
                            driftBottle.put("message", event.getMessage().contentToString().replace("@3598326822 ????????????", ""));
                            if (!event.getMessage().contentToString().replace("@3598326822 ????????????", "").trim().isEmpty()) {
                                try {
                                    DriftBottle.addDriftBottleALL(driftBottle, operationID);
                                } catch (SQLException e) {
                                    Logger.logError(e);
                                    contact.sendMessage("?????????????????????????????????");
                                }
                            }
                        }
                    }
                    case "-Like" -> {
                        try {
                            String messageContent = event.getMessage().contentToString().replace("@3598326822 ?????????", "").replace(" ", "");
                            if (messageContent.isEmpty()) {
                                if (DriftBottle.likeDriftBottle(operationID, event.getSender().getId()))
                                    contact.sendMessage("???????????????");
                                else
                                    contact.sendMessage("?????????????????????");
                            } else {
                                long bottleID = Long.parseLong(messageContent.trim());
                                if (DriftBottle.likeDriftBottle(operationID, event.getSender().getId(), bottleID))
                                    contact.sendMessage("???????????????");
                                else
                                    contact.sendMessage("?????????????????????????????????id?????????");
                            }
                        } catch (SQLException e) {
                            Logger.logError(e);
                            contact.sendMessage("??????????????????????????????");
                        }
                    }
                }
            }

            /* TODO: ??????????????? */
            case "Balance" -> {
                String messageContent = event.getMessage().contentToString();
                messageContent = messageContent.replace("@3598326822 ?????? ", "");
                if (!messageContent.isEmpty()) {
                    try {
                        String result = BalanceChemistry.balanceChemistry(messageContent);
                        contact.sendMessage(result);
                    } catch (Exception e) {
                        Logger.logError(e);
                        contact.sendMessage("????????????");
                    }
                }
            }

            /* TODO: ?????? */
            case "RandCard" -> {
                String messageContent = event.getMessage().contentToString();
                messageContent = messageContent.replace("@3598326822 ?????? ", "");
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
                        response.add(new PlainText("???????????????\n??????:\n" + result.get(1) + "\n??????:" + result.get(2) + "\n??????:" + result.get(3) + "\n??????:" + result.get(4)));
                    else
                        response.add(new PlainText("???????????????\n??????:\n" + result.get(1) + "\n??????:" + result.get(2) + "\n??????:" + result.get(3).size() + "???\n??????:" + result.get(4).size() + "???"));

                    int i = 1;
                    for (String name : result.get(0)) {
                        if (ArknightsRandCard.isSixStar(name)) {
                            response.add(new PlainText("\n??????" + i + "???????????????: " + name));
                        }
                        i++;
                    }
                    contact.sendMessage(response.asMessageChain());
                    if ((double) randNum / result.get(1).size() < 30) {
                        response.clear();
                        response.add(new At(event.getSender().getId()));
                        response.add(new PlainText(" ?????????"));
                        contact.sendMessage(response.asMessageChain());
                    }
                    ArknightsRandCard.addRandRecord(operationID, event, result);
                } catch (Exception e) {
                    response.add(new At(event.getSender().getId()));
                    response.add("???????????????????????????????????????????????????");
                    contact.sendMessage(response.asMessageChain());
                }
            }

            /* TODO: ???????????? */
            case "Satellite" -> {
                response.add(new At(event.getSender().getId()));
                try {
                    String name = event.getMessage().contentToString().replace("@3598326822 ??????", "").trim();
                    response.add(new PlainText("\n" + SatelliteGetPosition.satelliteGetPosition(name)));
                } catch (Exception e) {
                    Logger.logError(e);
                    response.add(new PlainText("\n???????????????"));
                }
                contact.sendMessage(response.asMessageChain());
            }

            /* TODO: ???????????? */
            case "MoePic" -> {
                switch (instructionList[1]) {
                    case "-GET" -> {
                        try {
                            SendPictures.sendPicturesFromInternet(contact);
                        } catch (Exception e) {
                            Logger.logError(e);
                            contact.sendMessage("??????????????????");
                        }
                    }
                    case "-GET-MORE" -> {
                        for (int i = 0; i < 3; i++) {
                            try {
                                SendPictures.sendPicturesFromInternet(contact);
                            } catch (Exception e) {
                                Logger.logError(e);
                                contact.sendMessage("??????????????????");
                            }
                        }
                    }
                }
            }

            /* TODO: ???????????? */
            case "RandSeed" -> {
                try {
                    response.add(new At(event.getSender().getId()));
                    response.add(new PlainText(" " + RandSeed.randSeed()));
                } catch (SQLException e) {
                    Logger.logError(e);
                    contact.sendMessage("???????????????");
                    return;
                }
                contact.sendMessage(response.asMessageChain());
            }

            /*TODO: AI??????*/
            case "AIReply" -> {
                response.add(new At((event.getSender().getId())));
                response.add(" " + SendAIReply.getAnswer(event.getMessage().contentToString().replace("@3598326822 AI", "").trim()));
                contact.sendMessage(response.asMessageChain());
            }

        }
        /* TODO: ?????????????????????
         *           ??????????????????*/
    }
}
