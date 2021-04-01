package com.yanghui.LingYueBot.core.coreTools;

import com.yanghui.LingYueBot.functions.SendSexPictures;
import com.yanghui.LingYueBot.groupHandler.XiaoFangZhou;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.io.File;
import java.util.Random;

public class FunctionHandler {

    public static void xiaoFangZhouFunction(String function, UserDataHandler handler, GroupMessageEvent event, XiaoFangZhou target) {
        MessageChainBuilder response = new MessageChainBuilder(5);
        // 函数参数列表
        String[] functionList = function.split(" ");
        switch (functionList[0]) {
            /* TODO：获取数据 */
            case "Get":
                switch (functionList[1]) {
                    case "Like":
                        response.add(new At(event.getSender().getId()));
                        response.add("，LingYue对你的好感度是" + handler.userLike);
                        if (handler.userLike < 50) {
                            response.add("，LingYue对你没什么感觉");
                        } else if (handler.userLike < 100) {
                            response.add("，LingYue好像有点在意你呢~");
                        } else if (handler.userLike < 150) {
                            response.add("，LingYue似乎挺喜欢和你在一起");
                        } else if (handler.userLike < 200) {
                            response.add("，LingYue已经和你很亲密了！");
                        } else {
                            response.add("，LingYue最……最喜欢你了~");
                        }
                        event.getGroup().sendMessage(response.asMessageChain());
                        break;
                    case "Fuck":
                        response.add(new At(event.getSender().getId()));
                        response.add("透了LingYue" + handler.hasFuck + "次");
                        if (handler.hasFuck > 114) {
                            response.add("，啊你……你太坏了~");
                        }
                        event.getGroup().sendMessage(response.asMessageChain());
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
                switch (functionList[1]) {
                    case "-Plus":
                        handler.hasFuck = handler.hasFuck + 1;
                        break;
                }
                /* TODO：发送特殊内容相关方法 */
            case "Send":
                switch (functionList[1]) {
                    // 发送图片类型
                    case "-Image":
                        if (!(boolean) target.configList.get("Picture_Permission"))
                            break;
                        switch (functionList[2]) {
                            case "-Rand":
                                synchronized (target.sexPictureArray) {
                                    int index = new Random().nextInt(target.sexPictureArray.size());
                                    SendSexPictures.sendSexPictures(
                                            target.sexPictureArray.get(index),
                                            target.group);
                                    break;
                                }
                            case "-Named" :
                                File file = new File(target.picPath + "\\" + functionList[3]);
                                if (!file.exists()) {
                                    break;
                                }
                                SendSexPictures.sendSexPictures(
                                        new File(target.picPath + "\\" + functionList[3]),
                                        target.group
                                );
                                break;
                            case "-Internet":
                                SendSexPictures.sendSexPicturesFromInternet(target.group);
                                break;
                        }
                }
                break;
        }
    }
}
