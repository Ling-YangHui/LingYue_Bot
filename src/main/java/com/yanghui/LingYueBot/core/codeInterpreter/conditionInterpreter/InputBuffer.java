package com.yanghui.LingYueBot.core.codeInterpreter.conditionInterpreter;

import com.yanghui.LingYueBot.annotations.PoweredByMirai;
import com.yanghui.LingYueBot.core.coreDatabaseUtil.UserDatabaseUtil;
import net.mamoe.mirai.event.events.MessageEvent;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Stack;

class InputBuffer {

    private final Stack<Character> inputBuffer = new Stack<>();

    public InputBuffer(String str) {
        str = str.replace(" ", "");
        str = str.replace("_", " ");
        char[] inputString = str.toCharArray();
        for (int i = inputString.length - 1; i >= 0; i--) {
            inputBuffer.push(inputString[i]);
        }
    }

    private boolean isSign(char c) {
        return c == '&' || c == '|' || c == '!' || c == '=' || c == '(' || c == ')';
    }

    @PoweredByMirai
    public Node getNode(MessageEvent event, HashMap<String, Object> botStatus) {
        String senderID = Long.toString(event.getSender().getId());
        long id = event.getSender().getId();
        String messageContent = event.getMessage().contentToString();
        Node.NodeType nodeType;
        StringBuilder stringCacheBuilder;
        String stringCache;
        boolean value = false;
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
                try {
                    value = UserDatabaseUtil.getUserBoolean(id, "isAdministrator");
                } catch (SQLException e) {
                    value = false;
                }
                break;
            case "isSpecial":
                try {
                    value = UserDatabaseUtil.getUserBoolean(id, "isSpecial");
                } catch (SQLException e) {
                    value = false;
                }
                break;
            case "isMale":
                try {
                    value = UserDatabaseUtil.getUserShort(id, "gender") == 1;
                } catch (SQLException e) {
                    value = false;
                }
                break;
            case "isFemale":
                try {
                    value = UserDatabaseUtil.getUserShort(id, "gender") == -1;
                } catch (SQLException e) {
                    value = false;
                }
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
            case "likeLessThan":
                stringCacheBuilder = new StringBuilder();
                do {
                    c = inputBuffer.pop();
                    stringCacheBuilder.append(c);
                } while (c != ')');
                stringCache = stringCacheBuilder.substring(1, stringCacheBuilder.length() - 1);
                try {
                    value = UserDatabaseUtil.getUserInt(id, "favor") < Integer.parseInt(stringCache);
                } catch (SQLException e) {
                    value = false;
                }
                break;
            case "likeMoreThan":
                stringCacheBuilder = new StringBuilder();
                do {
                    c = inputBuffer.pop();
                    stringCacheBuilder.append(c);
                } while (c != ')');
                stringCache = stringCacheBuilder.substring(1, stringCacheBuilder.length() - 1);
                try {
                    value = UserDatabaseUtil.getUserInt(id, "favor") > Integer.parseInt(stringCache);
                } catch (SQLException e) {
                    value = false;
                }
                break;
        }
        return new Node(nodeType, Node.Sign.END, value);
    }
}
