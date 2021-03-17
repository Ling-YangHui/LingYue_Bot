package com.yanghui.LingYueBot.core;

import net.mamoe.mirai.event.events.GroupMessageEvent;

public abstract class GroupMessageHandler {

    public abstract void onCreate() throws Exception;
    public abstract void onHandleMessage(GroupMessageEvent event);
    public abstract void onDelete() throws Exception;

}
