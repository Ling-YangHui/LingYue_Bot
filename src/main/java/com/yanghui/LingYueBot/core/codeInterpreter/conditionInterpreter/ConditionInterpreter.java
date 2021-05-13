package com.yanghui.LingYueBot.core.codeInterpreter.conditionInterpreter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yanghui.LingYueBot.UserHandler.CommonUserHandler;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionInterpreter {

    public static boolean getConditionSatisfied(JSONArray conditionArray, int index, MessageEvent event, JSONObject userInfo, JSONArray replyList, HashMap<String, Object> botStatus) {
        String regex = "(\\[[^\\]]*\\])";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(conditionArray.getString(index));
        List<String> conditionStr = new ArrayList<>();
        while (matcher.find()) {
            conditionStr.add(matcher.group().substring(1, matcher.group().length() - 1));
        }
        String nextConditionStr = conditionStr.get(1);
        String[] nextConditionList = nextConditionStr.split(",");
        boolean result_1 = getSelfConditionSatisfied(conditionStr.get(0) + "=", event, userInfo, replyList, botStatus);
        boolean result_2 = true;
        /* 如果没有接下来的连接那就直接结束 */
        if (nextConditionStr.isEmpty())
            return result_1;
        for (String s : nextConditionList) {
            result_2 &= getConditionSatisfied(conditionArray, Integer.parseInt(s), event, userInfo, replyList, botStatus);
        }
        return result_1 & result_2;
    }

    public static boolean getConditionSatisfied(JSONArray conditionArray, int index, MessageEvent event) {
        return getConditionSatisfied(conditionArray, index, event, CommonUserHandler.userInfo, CommonUserHandler.replyList, CommonUserHandler.botStatus);
    }

    public static boolean getSelfConditionSatisfied(String condition, MessageEvent event, JSONObject userInfo, JSONArray replyList, HashMap<String, Object> botStatus) {
        Stack<Node.Sign> signStack = new Stack<>();
        Stack<Boolean> valueStack = new Stack<>();
        InputBuffer buffer = new InputBuffer(condition);
        Node node;
        Node.Sign s;
        do {
            s = null;
            node = buffer.getNode(event, userInfo, replyList, botStatus);
            if (node.type == Node.NodeType.VALUE)
                valueStack.push(node.value);
            else {
                try {
                    do {
                        if (signStack.isEmpty())
                            break;
                        s = signStack.pop();
                        if (node.type == Node.NodeType.SIGN && node.sign == Node.Sign.END)
                            break;
                        if (node.sign != Node.Sign.RIGHT) {
                            while (Node.Sign.getPriority(s) > Node.Sign.getPriority(node.sign) && s != Node.Sign.LEFT) {
                                valueStack.push(calculate(s, valueStack));
                                if (signStack.isEmpty()) {
                                    s = null;
                                    break;
                                }
                                s = signStack.pop();
                            }
                        } else {
                            while (s != Node.Sign.LEFT) {
                                valueStack.push(calculate(s, valueStack));
                                if (signStack.isEmpty()) {
                                    s = null;
                                    break;
                                }
                                s = signStack.pop();
                            }
                        }
                    } while (Node.Sign.getPriority(node.sign) < Node.Sign.getPriority(s) && s != Node.Sign.LEFT);
                } catch (EmptyStackException ignore) {
                }
                if (node.sign != Node.Sign.RIGHT) {
                    if (s != null) {
                        signStack.push(s);
                    }
                    if (node.sign != Node.Sign.END) {
                        signStack.push(node.sign);
                    }
                }
            }
        } while (!(node.type == Node.NodeType.SIGN && node.sign == Node.Sign.END));
        while (!signStack.isEmpty()) {
            valueStack.push(calculate(signStack.pop(), valueStack));
        }
        return valueStack.pop();
    }

    private static boolean calculate(Node.Sign sign, Stack<Boolean> valueStack) {
        switch (sign) {
            case AND:
                return valueStack.pop() & valueStack.pop();
            case OR:
                return valueStack.pop() | valueStack.pop();
            case NOT:
                return !valueStack.pop();
            default:
                return false;
        }
    }
}