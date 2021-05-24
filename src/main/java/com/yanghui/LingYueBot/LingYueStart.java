package com.yanghui.LingYueBot;

import com.yanghui.LingYueBot.core.coreDatabaseUtil.BaseDatabaseUtil;
import com.yanghui.LingYueBot.core.messageHandler.GroupMessageHandler;
import com.yanghui.LingYueBot.core.messageHandler.UserMessageHandler;
import com.yanghui.LingYueBot.groupHandler.BeiShiCheDui;
import com.yanghui.LingYueBot.groupHandler.BingShuJu;
import com.yanghui.LingYueBot.groupHandler.DaShiTang;
import com.yanghui.LingYueBot.groupHandler.XiaoFangZhou;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.extension.PluginComponentStorage;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;


public class LingYueStart extends JavaPlugin {

    public static final HashMap<Long, GroupMessageHandler> groupHandlerHashMap = new HashMap<>();
    public static final HashMap<Long, UserMessageHandler> userHandlerHashMap = new HashMap<>();

    public LingYueStart() {
        super(new JvmPluginDescriptionBuilder(
                "com.yanghui.LingDongBot.LingDongStart",
                "2.0.0").author("me").name("test").info("测试").build());
    }

    @Override
    public void onEnable() {
        /* 监听登录事件 */
        Listener<?> loginListener = GlobalEventChannel.INSTANCE.subscribeAlways(BotOnlineEvent.class, botOnlineEvent -> {
            for (long groupID : groupHandlerHashMap.keySet()) {
                groupHandlerHashMap.get(groupID).group = Bot.getInstances().get(0).getGroup(groupID);
                System.out.println("成功加载群: " + groupID + " " + groupHandlerHashMap.get(groupID).group.getName());
            }
        });

        /* TODO: 处理群消息 */
        Listener<?> groupMessageEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, groupMessageEvent -> {
            synchronized (groupHandlerHashMap.get(groupMessageEvent.getGroup().getId())) {
                try {
                    groupHandlerHashMap.get(groupMessageEvent.getGroup().getId()).onHandleMessage(groupMessageEvent);
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Listener<?> newFriendsEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(NewFriendRequestEvent.class, NewFriendRequestEvent::accept);
    }

    @Override
    public void onLoad(@NotNull PluginComponentStorage $this$onLoad) {
        BaseDatabaseUtil.initDatabase();
        /* TODO: 这里写不同消息对象的初始化事件 */
        // 初始化事件handler
        groupHandlerHashMap.put(717151707L, new XiaoFangZhou());
        groupHandlerHashMap.put(1121098457L, new BeiShiCheDui());
        groupHandlerHashMap.put(904280379L, new BingShuJu());
        groupHandlerHashMap.put(541674751L, new DaShiTang());
        for (GroupMessageHandler handler : groupHandlerHashMap.values()) {
            try {
                handler.onCreate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        /* TODO: 这里写退出以后的保存数据方法 */
        for (GroupMessageHandler handler : groupHandlerHashMap.values()) {
            try {
                handler.onDelete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
