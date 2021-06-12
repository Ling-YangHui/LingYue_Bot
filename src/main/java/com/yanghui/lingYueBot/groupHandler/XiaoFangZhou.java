package com.yanghui.lingYueBot.groupHandler;

import com.yanghui.lingYueBot.handler.GroupHandler;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.io.File;
import java.util.Vector;

public class XiaoFangZhou extends GroupHandler {

    public final Vector<File> sexPictureArray = new Vector<>();
    public XiaoFangZhou() {
        super(717151707L);
    }

    @Override
    public void administratorInstructionHandler(GroupMessageEvent event) {
        super.administratorInstructionHandler(event);
    }
}
