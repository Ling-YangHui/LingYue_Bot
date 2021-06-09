package com.yanghui.lingYueBot.core.messageHandler;

import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.events.UserMessageEvent;

public abstract class UserMessageHandler {

    public User user = null;

    public abstract void onCreate() throws Exception;

    public abstract void onHandleMessage(UserMessageEvent event);

    public abstract void onDelete() throws Exception;

}
