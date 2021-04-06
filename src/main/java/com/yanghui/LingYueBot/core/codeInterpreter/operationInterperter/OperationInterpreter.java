package com.yanghui.LingYueBot.core.codeInterpreter.operationInterperter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Face;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OperationInterpreter {

    public static void execute(JSONObject replyObject, int num, MessageEvent event) {
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
            String[] replyStrList = conditionList.get(2).split(",");
            for (String s : replyStrList) {
                executeReply(reply.getString(Integer.parseInt(s)), event);
            }
        }
        if (!conditionList.get(3).isEmpty()) {
            String[] operationStrList = conditionList.get(3).split(",");
            for (String s : operationStrList) {
                executeOperation(operation.getString(Integer.parseInt(s)), event);
            }
        }
    }

    public static void executeReply(String replyStr, Contact contact) {
        String[] replyList = replyStr.split("&&");
        for (String str : replyList) {
            String[] reply = str.split("&");
            MessageChainBuilder builder = new MessageChainBuilder();
            for (String replyItem : reply) {
                replyItem = replyItem.trim();
                if (replyItem.startsWith("TEXT:")) {
                    builder.add(new PlainText(replyItem.substring(5)));
                }
                if (replyItem.startsWith("FACE:")) {
                    builder.add(new Face(Integer.parseInt(replyItem.substring(5))));
                }
            }
            contact.sendMessage(builder.asMessageChain());
        }
    }

    public static void executeReply(String replyStr, MessageEvent event) {
        executeReply(replyStr, event.getSender());
    }

    public static void executeOperation(String operation, MessageEvent event) {
        String[] instructionList = operation.split(" ");
        if (instructionList[0].equals("Like")) {
            if (instructionList[1].equals("-Rise")) {

            }
        }
    }

}
