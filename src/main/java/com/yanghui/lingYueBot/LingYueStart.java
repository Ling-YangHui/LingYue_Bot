package com.yanghui.lingYueBot;

import com.yanghui.lingYueBot.core.coreDatabaseUtil.BaseDatabaseUtil;
import com.yanghui.lingYueBot.core.messageHandler.GroupMessageHandler;
import com.yanghui.lingYueBot.core.messageHandler.UserMessageHandler;
import com.yanghui.lingYueBot.groupHandler.XiaoFangZhou;
import com.yanghui.lingYueBot.handler.GroupHandler;
import com.yanghui.lingYueBot.utils.Logger;
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

    // WARNING: INSTANCE字段必须设置为public, 否则mirai-console在反射时会失败.
    public static final LingYueStart INSTANCE = new LingYueStart();
    private static final boolean pluginLoaded = false;
    private static final Bot CURRENT_BOT = null;

    public static LingYueStart getInstance() {
        return INSTANCE;
    }

    public static Bot getCurrentBot() {
        return CURRENT_BOT;
    }

    public static boolean isPluginLoaded() {
        return pluginLoaded;
    }

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
                Logger.logDebug("群加载事件", groupID + " " + groupHandlerHashMap.get(groupID).group.getName());
            }
        });

        /* TODO: 处理群消息 */
        Listener<?> groupMessageEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, groupMessageEvent -> {
            synchronized (groupHandlerHashMap.get(groupMessageEvent.getGroup().getId())) {
                try {
                    groupHandlerHashMap.get(groupMessageEvent.getGroup().getId()).onHandleMessage(groupMessageEvent);
                    Thread.sleep(100);
                } catch (Exception e) {
                    Logger.logError(e);
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
        groupHandlerHashMap.put(1121098457L, new GroupHandler(1121098457L));
        groupHandlerHashMap.put(904280379L, new GroupHandler(904280379L));
        groupHandlerHashMap.put(541674751L, new GroupHandler(541674751L));
        groupHandlerHashMap.put(583880103L, new GroupHandler(583880103L));
        for (GroupMessageHandler handler : groupHandlerHashMap.values()) {
            try {
                handler.onCreate();
            } catch (Exception e) {
                Logger.logError(e);
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
                Logger.logError(e);
            }
        }
    }
}
