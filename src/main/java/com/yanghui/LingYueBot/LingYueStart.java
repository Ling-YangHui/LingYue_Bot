package com.yanghui.LingYueBot;

import com.yanghui.LingYueBot.core.GroupMessageHandler;
import com.yanghui.LingYueBot.groupHandler.BeiShiCheDui;
import com.yanghui.LingYueBot.groupHandler.XiaoFangZhou;
import net.mamoe.mirai.console.extension.PluginComponentStorage;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
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
        /* TODO: 处理群消息 */
        Listener<?> listener = GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
            handlerHashMap.get(event.getGroup().getId()).onHandleMessage(event);
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
    }
}
