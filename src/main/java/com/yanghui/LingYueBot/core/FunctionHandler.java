package com.yanghui.LingYueBot.core;

import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.Random;

public class FunctionHandler {

    public static void groupUserFunction(String function, UserDataHandler handler, GroupMessageEvent event) {
        // 函数参数列表
        String[] functionList = function.split(" ");
        String response;
        switch (functionList[0]) {
            /* TODO：获取数据 */
            case "Get":
                switch (functionList[1]) {
                    case "Like":
                        response = event.getSender().getNameCard() + "，LingYue对你的好感度是" + handler.userLike;
                        if (handler.userLike < 50) {
                            response += "，LingYue对你没什么感觉";
                        } else if (handler.userLike < 100) {
                            response += "，LingYue好像有点在意你呢~";
                        } else if (handler.userLike < 150) {
                            response += "，LingYue似乎挺喜欢和你在一起";
                        } else if (handler.userLike < 200) {
                            response += "，LingYue已经和你很亲密了！";
                        } else {
                            response += "，LingYue最……最喜欢你了~";
                        }
                        event.getGroup().sendMessage(response);
                        break;
                    case "Fuck":
                        response = event.getSender().getNameCard() + "透了LingYue" + handler.hasFuck + "次";
                        if (handler.hasFuck > 114) {
                            response += "，啊你……你太坏了~";
                        }
                        event.getGroup().sendMessage(response);
                        break;
                }
                break;
            /* TODO：好感度相关方法 */
            case "Like":
                // 表示好感度变化
                int movingLimit = 5;
                // 好感度的浮动数值
                switch (functionList[2]) {
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
                switch (functionList[1]) {
                    case "-Float":
                        movingNum -= movingLimit / 2;
                        break;
                    case "-Decline":
                        movingNum *= -1;
                }
                handler.userLike += movingNum;
                break;
            /* TODO：透相关方法 */
            case "Fuck":
                switch (functionList[1]){
                    case "-Plus":
                        handler.hasFuck = handler.hasFuck + 1;
                        break;
                }
        }
    }
}
