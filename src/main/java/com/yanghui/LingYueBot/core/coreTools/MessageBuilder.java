package com.yanghui.LingYueBot.core.coreTools;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Face;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;

public class MessageBuilder {

    public static MessageChain MessageBuild(String messageStr, GroupMessageEvent event) {
        long senderID = 2411046022L;
        if (event != null)
            senderID = event.getSender().getId();
        String[] messageList = messageStr.split("&");
        MessageChainBuilder builder = new MessageChainBuilder(16);
        for (String str : messageList) {
            String order = str.trim();
            /* 先处理at信息 */
            System.out.println(order);
            if (order.equals("AT")) {
                builder.add(new At(senderID));
            } else if (order.contains("AT")) {
                long target = Long.parseLong(order.split(":")[1]);
                builder.add(new At(target));
            } else if (order.contains("FACE")) {
                System.out.println("Face");
                builder.add(new Face(Integer.parseInt(order.split(":")[1])));
            } else {
                builder.add(order);
            }
            /* 处理表情信息 */
        }
        return builder.asMessageChain();
    }
}
