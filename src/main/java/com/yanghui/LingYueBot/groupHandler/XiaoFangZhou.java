package com.yanghui.LingYueBot.groupHandler;

import com.yanghui.LingYueBot.Template.GroupHandler;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;

public class XiaoFangZhou extends GroupHandler {

    // Bot涩图列表
    public final Vector<File> sexPictureArray = new Vector<>();
    // 图片路径
    public String picPath = "D:\\IntelliJ IDEA programming\\MiraiRobot\\MiraiResources\\LingYue_resources\\pictureSrc";

    public XiaoFangZhou(String path) {
        super(path);
    }

    @Override
    public void onLoad() throws Exception {
        super.onLoad();
        // 读取涩图
        synchronized (sexPictureArray) {
            sexPictureArray.clear();
            File[] pictures = new File(picPath).listFiles();
            if (pictures == null) {
                throw new Exception("图片加载错误");
            }
            sexPictureArray.addAll(Arrays.asList(pictures));
        }
        functionMap.put("DriftBottle", driftBottle);
    }

    @Override
    public void administratorInstructionHandler(GroupMessageEvent event) {
        super.administratorInstructionHandler(event);
    }
}
