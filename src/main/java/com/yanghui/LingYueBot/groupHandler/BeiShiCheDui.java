package com.yanghui.LingYueBot.groupHandler;

import com.yanghui.LingYueBot.core.messageHandler.GroupMessageHandler;
import com.yanghui.LingYueBot.functions.SendSexPictures;
import net.mamoe.mirai.event.events.GroupMessageEvent;

public class BeiShiCheDui extends GroupMessageHandler {

    @Override
    public void onCreate() throws Exception {

    }

    @Override
    public void onHandleMessage(GroupMessageEvent event) {
        if (event.getMessage().contentToString().contains("@3598326822 来点涩图"))
            SendSexPictures.sendSexPicturesFromInternet(event.getGroup());
    }

    @Override
    public void onDelete() throws Exception {

    }
}
