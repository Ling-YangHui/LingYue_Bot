package com.yanghui.lingYueBot.groupHandler;

import com.yanghui.lingYueBot.template.GroupHandler;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.io.File;
import java.util.Vector;

public class XiaoFangZhou extends GroupHandler {

    // Bot涩图列表
    public final Vector<File> sexPictureArray = new Vector<>();
    // 图片路径
    public XiaoFangZhou() {
        super(717151707L);
    }

    @Override
    public void onLoad() throws Exception {
        super.onLoad();
        // 读取涩图
        functionMap.put("DriftBottle", driftBottle);
    }

    @Override
    public void administratorInstructionHandler(GroupMessageEvent event) {
        super.administratorInstructionHandler(event);
    }
}
