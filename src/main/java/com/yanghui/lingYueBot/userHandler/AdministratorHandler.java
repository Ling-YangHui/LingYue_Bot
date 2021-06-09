package com.yanghui.lingYueBot.userHandler;

import com.yanghui.lingYueBot.core.messageHandler.UserMessageHandler;
import net.mamoe.mirai.event.events.UserMessageEvent;

public class AdministratorHandler extends UserMessageHandler {
    @Override
    public void onCreate() throws Exception {

    }

    @Override
    public void onHandleMessage(UserMessageEvent event) {
        if (user == null || user.getId() != event.getSender().getId()) {
            return;
        }
    }

    @Override
    public void onDelete() throws Exception {

    }
}
