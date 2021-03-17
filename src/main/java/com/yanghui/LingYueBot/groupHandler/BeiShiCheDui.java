package com.yanghui.LingYueBot.groupHandler;

import com.yanghui.LingYueBot.core.GroupMessageHandler;
import net.mamoe.mirai.event.events.GroupMessageEvent;

public class BeiShiCheDui extends GroupMessageHandler {

    @Override
    public void onCreate() throws Exception{

    }

    @Override
    public void onHandleMessage(GroupMessageEvent event) {
        System.out.println(event.getMessage().contentToString());
    }

    @Override
    public void onDelete() throws Exception{

    }
}
