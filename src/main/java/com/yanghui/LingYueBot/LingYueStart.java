package com.yanghui.LingYueBot;

import com.yanghui.LingYueBot.UserHandler.AdministratorHandler;
import com.yanghui.LingYueBot.core.messageHandler.GroupMessageHandler;
import com.yanghui.LingYueBot.core.messageHandler.UserMessageHandler;
import com.yanghui.LingYueBot.groupHandler.BeiShiCheDui;
import com.yanghui.LingYueBot.groupHandler.XiaoFangZhou;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.extension.PluginComponentStorage;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.BotOfflineEvent;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
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
                (groupHandlerHashMap.get(groupID).group = Bot.getInstance(3598326822L).getGroup(groupID)).sendMessage("----Bot已上线----");
                System.out.println("成功加载群: " + groupID + " " + groupHandlerHashMap.get(groupID).group.getName());
            }
            for (long userID : userHandlerHashMap.keySet()) {
                (userHandlerHashMap.get(userID)).user = Bot.getInstance(3598326822L).getFriend(userID);
                System.out.println("成功加载用户: " + userID + " " + userHandlerHashMap.get(userID).user.getNick());
            }
        });

        /* 监听下线事件 */
        Listener<?> offlineListener = GlobalEventChannel.INSTANCE.subscribeAlways(BotOfflineEvent.class, botOfflineEvent -> {
            for (long groupID : groupHandlerHashMap.keySet()) {
                groupHandlerHashMap.get(groupID).group.sendMessage("----Bot已下线----");
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

        /* TODO：处理用户消息 */
        Listener<?> userMessageEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(UserMessageEvent.class, userMessageEvent -> {
            synchronized (userHandlerHashMap.get(userMessageEvent.getSubject().getId())) {
                try {
                    userHandlerHashMap.get(userMessageEvent.getSubject().getId()).onHandleMessage(userMessageEvent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onLoad(@NotNull PluginComponentStorage $this$onLoad) {
        /* TODO: 这里写不同消息对象的初始化事件 */
        // 初始化事件handler
        groupHandlerHashMap.put(717151707L, new XiaoFangZhou());
        groupHandlerHashMap.put(1121098457L, new BeiShiCheDui());
        for (GroupMessageHandler handler : groupHandlerHashMap.values()) {
            try {
                handler.onCreate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        userHandlerHashMap.put(2411046022L, new AdministratorHandler());
        for (UserMessageHandler handler : userHandlerHashMap.values()) {
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
        for (long groupID : groupHandlerHashMap.keySet()) {
            groupHandlerHashMap.get(groupID).group.sendMessage("----Bot已下线----");
        }
    }
}
