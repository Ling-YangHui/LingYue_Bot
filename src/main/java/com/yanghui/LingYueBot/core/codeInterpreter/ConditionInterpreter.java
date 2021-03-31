package com.yanghui.LingYueBot.core.codeInterpreter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yanghui.LingYueBot.UserHandler.CommonUserHandler;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Node类
 */
class Node {
    NodeType type;
    Sign sign;
    boolean value;

    public Node(NodeType type, Sign sign, boolean value) {
        this.sign = sign;
        this.type = type;
        this.value = value;
    }

    enum NodeType {
        SIGN, VALUE
    }

    enum Sign {
        NOT, AND, OR, LEFT, RIGHT, END;

        public static int getPriority(Sign sign) {
            if (sign == null)
                return 0;
            switch (sign) {
                case NOT:
                    return 3;
                case AND:
                    return 2;
                case OR:
                    return 1;
                case LEFT:
                case RIGHT:
                    return 4;
                default:
                    return 0;
            }
        }
    }
}

class InputBuffer {

    private final Stack<Character> inputBuffer = new Stack<>();

    public InputBuffer(String str) {
        str = str.replace(" ", "");
        char[] inputString = str.toCharArray();
        for (int i = inputString.length - 1; i >= 0; i--) {
            if (inputString[i] != ' ')
                inputBuffer.push(inputString[i]);
        }
    }

    private boolean isSign(char c) {
        return c == '&' || c == '|' || c == '!' || c == '=' || c == '(' || c == ')';
    }

    public Node getNode(MessageEvent event) {
        String userID = Long.toString(event.getSender().getId());
        String messageContent = event.getMessage().contentToString();
        Node.NodeType nodeType;
        StringBuilder stringCacheBuilder = new StringBuilder();
        String stringCache;
        boolean value = false;
        Node.Sign sign;
        HashMap<String, Object> botStatus = CommonUserHandler.botStatus;
        JSONObject userInfo = CommonUserHandler.userInfo;
        JSONArray replyList = CommonUserHandler.replyList;
        char c = inputBuffer.pop();

        if (isSign(c)) {
            nodeType = Node.NodeType.SIGN;
        } else {
            nodeType = Node.NodeType.VALUE;
        }

        /* 获取符号 */
        if (nodeType == Node.NodeType.SIGN) {
            switch (c) {
                case '&':
                    return new Node(nodeType, Node.Sign.AND, false);
                case '|':
                    return new Node(nodeType, Node.Sign.OR, false);
                case '!':
                    return new Node(nodeType, Node.Sign.NOT, false);
                case '(':
                    return new Node(nodeType, Node.Sign.LEFT, false);
                case ')':
                    return new Node(nodeType, Node.Sign.RIGHT, false);
                default:
                    return new Node(nodeType, Node.Sign.END, false);
            }
        }

        /* 获取操作数 */
        StringBuilder apiCommandBuilder = new StringBuilder();
        while (!isSign(c)) {
            apiCommandBuilder.append(c);
            c = inputBuffer.pop();
        }
        inputBuffer.push(c);
        String apiCommand = apiCommandBuilder.toString();
        switch (apiCommand) {
            case "true":
                value = true;
                break;
            case "false":
                break;
            case "isAdministrator":
                value = userInfo.getJSONObject(userID).getBoolean("isAdministrator");
                break;
            case "isSpecial":
                value = userInfo.getJSONObject(userID).getBoolean("isSpecial");
                break;
            case "isMale":
                value = userInfo.getJSONObject(userID).getString("gender").equals("male");
                break;
            case "isFemale":
                value = userInfo.getJSONObject(userID).getString("gender").equals("female");
                break;
            case "contains":
                stringCacheBuilder = new StringBuilder();
                do {
                    c = inputBuffer.pop();
                    stringCacheBuilder.append(c);
                } while (c != ')');
                stringCache = stringCacheBuilder.substring(1, stringCacheBuilder.length() - 1);
                value = messageContent.contains(stringCache);
                break;
            case "equals":
                stringCacheBuilder = new StringBuilder();
                do {
                    c = inputBuffer.pop();
                    stringCacheBuilder.append(c);
                } while (c != ')');
                stringCache = stringCacheBuilder.substring(1, stringCacheBuilder.length() - 1);
                value = messageContent.equals(stringCache);
                break;
        }
        return new Node(nodeType, Node.Sign.END, value);
    }
}

public class ConditionInterpreter {

    public static boolean getConditionSatisfied(JSONArray conditionArray, int index, MessageEvent event) {
        String regex = "(\\[[^\\]]*\\])";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(conditionArray.getString(index));
        List<String> conditionStr = new ArrayList<>();
        System.out.println(conditionArray.getString(index));
        while (matcher.find()) {
            conditionStr.add(matcher.group().substring(1, matcher.group().length() - 1));
        }
        String nextConditionStr = conditionStr.get(1);
        String[] nextConditionList = nextConditionStr.split(",");
        boolean result_1 = getSelfConditionSatisfied(conditionStr.get(0) + "=", event);
        boolean result_2 = true;
        /* 如果没有接下来的连接那就直接结束 */
        if (nextConditionStr.isEmpty())
            return result_1;
        for (String s : nextConditionList) {
            result_2 &= getConditionSatisfied(conditionArray, Integer.parseInt(s), event);
        }
        return result_1 & result_2;
    }

    public static boolean getSelfConditionSatisfied(String condition, MessageEvent event) {
        Stack<Node.Sign> signStack = new Stack<>();
        Stack<Boolean> valueStack = new Stack<>();
        InputBuffer buffer = new InputBuffer(condition);
        Node node;
        Node.Sign s;
        do {
            s = null;
            node = buffer.getNode(event);
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
                    } while (Node.Sign.getPriority(node.sign) <= Node.Sign.getPriority(s) && s != Node.Sign.LEFT);
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