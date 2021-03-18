package com.yanghui.LingYueBot.groupHandler;

import com.yanghui.LingYueBot.core.GroupMessageHandler;
import net.mamoe.mirai.event.events.GroupMessageEvent;

public class BeiShiCheDui extends GroupMessageHandler {

    @Override
    public void onCreate() throws Exception {

    }

    @Override
    public void onHandleMessage(GroupMessageEvent event) {
        if (event.getMessage().contentToString().contains("@3598326822"))
            event.getGroup().sendMessage(event.getMessage().contentToString().replace("@3598326822", ""));
    }

    @Override
    public void onDelete() throws Exception {

    }
}
