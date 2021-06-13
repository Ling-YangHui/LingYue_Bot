package com.yanghui.lingYueBot.core.codeInterpreter.conditionInterpreter;

import com.yanghui.lingYueBot.annotations.PoweredByMirai;
import com.yanghui.lingYueBot.core.coreDatabaseUtil.UserDatabaseUtil;
import com.yanghui.lingYueBot.utils.Logger;
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
                Logger.logDebug(messageContent);
                Logger.logDebug(stringCache);
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

    /**
     * Node类
     */
    static class Node {
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
}
