package com.yanghui.lingYueBot.core.messageHandler;

import com.yanghui.lingYueBot.functions.javaBasedFunc.DriftBottle;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.HashMap;

/**
 * 群组管理句柄的所有父类，需要实现初始化、消息管理、退出三个方法
 * 此处有一个方法的具体实现
 *
 * @see com.yanghui.lingYueBot.handler.GroupHandler
 */
public abstract class GroupMessageHandler {

    public long groupID;

    public GroupMessageHandler(long groupID) {
        this.groupID = groupID;
    }

    public Group group = null;
    // Bot设置状态
    public final HashMap<String, Object> configList = new HashMap<>();
    public final HashMap<String, Object> functionMap = new HashMap<>();
    // Bot参数
    public final HashMap<String, Object> paramList = new HashMap<>();
    // 存档路径
    public DriftBottle driftBottle;

    public abstract void onCreate() throws Exception;

    public abstract void onHandleMessage(GroupMessageEvent event);

    public abstract void onDelete() throws Exception;

}
