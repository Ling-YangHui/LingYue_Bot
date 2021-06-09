package com.yanghui.lingYueBot.core.codeInterpreter.conditionInterpreter;

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
