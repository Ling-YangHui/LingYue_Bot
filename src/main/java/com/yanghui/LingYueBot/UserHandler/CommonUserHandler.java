package com.yanghui.LingYueBot.UserHandler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yanghui.LingYueBot.core.JsonLoader;
import com.yanghui.LingYueBot.core.messageHandler.UserMessageHandler;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.events.UserMessageEvent;
import net.mamoe.mirai.message.data.Message;

import java.io.IOException;

public class CommonUserHandler extends UserMessageHandler {

    private static final String rootPath = "D:\\IntelliJ IDEA programming\\MiraiRobot\\MiraiResources\\LingYue_resources\\Users\\";
    public static JSONObject userInfo;
    static JSONArray replyList;
    static Long[] forbidSendList = {666000L};

    /* 初始化静态代码块 */
    static {
        try {
            replyList = JsonLoader.jsonArrayLoader(rootPath + "reply.json", null);
            userInfo = JsonLoader.jsonObjectLoader(rootPath + "userInfo.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final User user;
    private final String id;
    private final JSONObject userObject;
    private boolean isFirstSpeak = true;

    public CommonUserHandler(Long id) {
        this.id = Long.toString(id);
        this.user = Bot.getInstances().get(0).getFriend(id) == null ? Bot.getInstances().get(0).getStranger(id) : Bot.getInstances().get(0).getFriend(id);
        this.userObject = userInfo.getJSONObject(this.id);
    }

    public CommonUserHandler(User user) {
        this.user = user;
        this.id = Long.toString(user.getId());
        user.sendMessage("*** 欢迎注册LingYue！ ***\n请完成以下问题并最终完成注册");
        user.sendMessage("1. 你是BUAAer吗？（回答true & false）\n2. 你的性别？（回答male & female & secret）\n不区分大小写，请按照括号中的要求回答哦。回答请在一条回复里完成，不同答案用一个空格分开");
        addUser(user);
        this.userObject = userInfo.getJSONObject(this.id);
    }

    public static void addUser(User user) {
        userInfo.put(Long.toString(user.getId()), ParseUser.ParseJSONFromUser(user));
    }

    public static void saveData() throws IOException {
        JsonLoader.saveJSON(rootPath + "userInfo.json", userInfo);
    }

    @Override
    public void onCreate() throws Exception {

    }

    @Override
    public void onHandleMessage(UserMessageEvent event) {
        Message message = event.getMessage();
        String messageText = message.contentToString();

        System.out.println("trig");
        if (!userObject.getBoolean("valid")) {
            register(event);
            return;
        }

        try {
            orderHandle(event);
        } catch (Exception e) {
            user.sendMessage("指令错误");
        }


        isFirstSpeak = false;
    }

    @Override
    public void onDelete() throws Exception {

    }

    /**
     * 初始化设置
     */
    /* TODO: 初始化注册设置 */
    private void register(UserMessageEvent event) {
        Message message = event.getMessage();
        String messageText = message.contentToString();
        try {
            if (isFirstSpeak) {
                user.sendMessage("*** 欢迎注册LingYue！ ***\n请完成以下问题并最终完成注册");
                user.sendMessage("1. 你是BUAAer吗？（回答true & false）\n2. 你的性别？（回答male & female & secret）\n不区分大小写，请按照括号中的要求回答哦。回答请在一条回复里完成，不同答案用一个空格分开");
            }
            messageText = messageText.toLowerCase();
            String[] answer = messageText.split(" ");
            if ((!answer[0].equals("true") && !answer[0].equals("false")) ||
                    (!answer[1].equals("male") && !answer[1].equals("female") && !answer[1].equals("secret"))) {
                if (!isFirstSpeak) {
                    user.sendMessage("格式错误了！请重新输入");
                    return;
                }
            }
            userObject.put("isBUAAer", Boolean.parseBoolean(answer[0]));
            userObject.put("gender", answer[1]);
            userObject.put("valid", true);
            user.sendMessage("注册成功！注册数据：" + userObject);
        } catch (Exception e) {
            if (!isFirstSpeak)
                user.sendMessage("格式错误了！请重新输入");
        }
    }

    /**
     * 指令接管
     */
    /* TODO: LingYue指令集 */
    private void orderHandle(UserMessageEvent event) throws Exception {
        Message message = event.getMessage();
        String messageText = message.contentToString();
        String[] orderString = messageText.split(" ");
        if (!orderString[0].equals("LingYue")) {
            return;
        }
        // 编辑数据
        if (orderString[1].equals("-Edit")) {
            if (orderString[2].equals("-gender")) {
                if (!orderString[3].equals("male") && !orderString[3].equals("female") && !orderString[3].equals("secret")) {
                    throw new Exception();
                }
                userObject.put("gender", orderString[3]);
            }
        }
    }

    /**
     * 数据集写入
     */
    /* TODO: LingYue写入数据集 */
    private void writeReply(UserMessageEvent event) throws Exception {

    }
}
