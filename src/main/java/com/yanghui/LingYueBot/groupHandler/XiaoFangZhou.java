package com.yanghui.LingYueBot.groupHandler;

import com.alibaba.fastjson.JSONObject;
import com.yanghui.LingYueBot.Template.GroupHandler;
import com.yanghui.LingYueBot.functions.GetSystemInfo;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;

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
    public void administratorHandler(GroupMessageEvent event) {
        MessageChain message = event.getMessage();
        String messageContent = event.getMessage().contentToString();
        group = event.getGroup();
        long senderID = event.getSender().getId();
        /* TODO：最高优先级——管理员任务 */
        if (senderID == 2411046022L) {
            switch (messageContent) {
                case "LingYue滚回去卷吧":
                    configList.put("OnActive", false);
                    group.sendMessage("呜~这就去卷");
                    break;
                case "LingYue -reload":
                    try {
                        onDelete();
                        onLoad();
                        group.sendMessage("LingYue的里面，已经被换成新的形状了呢~");
                    } catch (Exception e) {
                        group.sendMessage("呜~数据读取错误了呢");
                    }
                    break;
                case "LingYue -traceBack":
                    try {
                        onLoad();
                        group.sendMessage("咻~回溯完毕");
                    } catch (Exception e) {
                        group.sendMessage("呜~数据读取错误了呢");
                    }
                    break;
                case "LingYue -getSysStatus":
                    group.sendMessage("获取系统数据中...");
                    Vector<String> info = GetSystemInfo.getSystemInfo();
                    StringBuilder builder = new StringBuilder();
                    if (info == null) {
                        group.sendMessage("获取失败");
                        break;
                    }
                    for (String s : info)
                        builder.append(s).append("\n");
                    group.sendMessage(builder.toString());
                    break;
                case "LingYue -getStatus":
                    StringBuilder str = new StringBuilder();
                    for (String key : configList.keySet()) {
                        str.append(key).append(": ").append(configList.get(key)).append('\n');
                    }
                    group.sendMessage(str.toString());
                    break;
                case "LingYue -getBottle":
                    group.sendMessage("海里还有" + driftBottle.getBottleNum() + "个瓶子");
                    break;
                case "LingYue -close":
                    configList.put("OnActive", false);
                    break;
                case "LingYue -open":
                    configList.put("OnActive", true);
                    break;
                case "LingYue -close repeat":
                    configList.put("SuccessiveRepeat_Permission", false);
                    group.sendMessage("呜呜呜，我再也不复读了");
                    break;
                case "LingYue -close pic":
                    configList.put("Picture_Permission", false);
                    group.sendMessage("呜呜呜，阳姐姐我再也不ghs了");
                    break;
                case "LingYue -open pic":
                    configList.put("Picture_Permission", true);
                    break;
                case "LingYue -open repeat":
                    configList.put("SuccessiveRepeat_Permission", true);
                    break;
                case "LingYue -close special":
                    configList.put("SpecialMessageReply_Permission", false);
                    group.sendMessage("呜呜呜，我再也不说怪话了");
                    break;
                case "LingYue -open special":
                    configList.put("SpecialMessageReply_Permission", true);
                    break;
            }
            if (messageContent.contains("LingYue -remove bottle")) {
                System.out.println(messageContent.split(" ", 4)[3]);
                Vector<JSONObject> remove = driftBottle.removeBottle(" " + messageContent.split(" ", 4)[3]);
                group.sendMessage("---删除列表---\n" + remove);
            }
        }
    }
}
