package com.yanghui.LingYueBot;

import com.yanghui.LingYueBot.core.GroupMessageHandler;
import com.yanghui.LingYueBot.groupHandler.BeiShiCheDui;
import com.yanghui.LingYueBot.groupHandler.XiaoFangZhou;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.extension.PluginComponentStorage;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.events.BotOfflineEvent;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;


public class LingYueStart extends JavaPlugin {

    private final HashMap<Long, GroupMessageHandler> handlerHashMap = new HashMap<>();

    public LingYueStart() {
        super(new JvmPluginDescriptionBuilder(
                "com.yanghui.LingDongBot.LingDongStart",
                "2.0.0").author("me").name("test").info("测试").build());
    }

    @Override
    public void onEnable() {
        /* 监听登录事件 */
        Listener<?> loginListener = GlobalEventChannel.INSTANCE.subscribe(BotOnlineEvent.class, botOnlineEvent -> {
            for (long groupID : handlerHashMap.keySet()) {
                (handlerHashMap.get(groupID).group = Bot.getInstance(3598326822L).getGroup(groupID)).sendMessage("----Bot已上线----");
                System.out.println("成功加载群: " + groupID + " " + handlerHashMap.get(groupID).group.getName());
            }
            return ListeningStatus.STOPPED;
        });

        /* 监听下线事件 */
        Listener<?> offlineListener = GlobalEventChannel.INSTANCE.subscribe(BotOfflineEvent.class, botOfflineEvent -> {
            for (long groupID: handlerHashMap.keySet()) {
                handlerHashMap.get(groupID).group.sendMessage("----Bot已下线----");
            }
            return ListeningStatus.STOPPED;
        });
        /* TODO: 处理群消息 */
        Listener<?> messageListener = GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, groupMessageEvent -> {
            synchronized (handlerHashMap.get(groupMessageEvent.getGroup().getId())) {
                try {
                handlerHashMap.get(groupMessageEvent.getGroup().getId()).onHandleMessage(groupMessageEvent);
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onLoad(@NotNull PluginComponentStorage $this$onLoad) {
        /* TODO: 这里写不同群的初始化事件 */
        // 初始化事件handler
        handlerHashMap.put(717151707L, new XiaoFangZhou());
        handlerHashMap.put(1121098457L, new BeiShiCheDui());
        for (GroupMessageHandler handler : handlerHashMap.values()) {
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
        for (GroupMessageHandler handler : handlerHashMap.values()) {
            try {
                handler.onDelete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (long groupID: handlerHashMap.keySet()) {
            handlerHashMap.get(groupID).group.sendMessage("----Bot已下线----");
        }
    }
}
