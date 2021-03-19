package com.yanghui.LingYueBot.core.messageHandler;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;

public abstract class GroupMessageHandler {

    public Group group = null;

    public abstract void onCreate() throws Exception;

    public abstract void onHandleMessage(GroupMessageEvent event);

    public abstract void onDelete() throws Exception;

}
