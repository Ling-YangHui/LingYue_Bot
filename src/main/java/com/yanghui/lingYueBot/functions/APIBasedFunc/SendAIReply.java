package com.yanghui.lingYueBot.functions.APIBasedFunc;

import com.alibaba.fastjson.JSONObject;
import com.yanghui.lingYueBot.utils.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

public class SendAIReply {

    private IOException e;

    private static String getRequestURL(String question) {
        return "http://api.qingyunke.com/api.php?key=free&appid=0&msg=" + java.net.URLEncoder.encode(question);
    }

    private static String decodeAnswer(String answer) {
        return answer.replace("{br}", "\n");
    }

    public static String getAnswer(String question) {
        /** 获取JSON数据 **/
        String JSON = getAnswer_JSON(question);
        // 若未找到结果，则返回null
        if (JSON == null) {
            return "执行API访问错误";
        }
        /** 解析JSON数据 **/
        JSONObject response = com.alibaba.fastjson.JSON.parseObject(JSON);
        String content = response.getString("content");
        /** 封装JSON数据 **/
        content = decodeAnswer(content);
        return content;
    }


    private static String getAnswer_JSON(String question) {
        String result = null;
        OkHttpClient client = new OkHttpClient();
        Request request;
        String URL = getRequestURL(question);
        request = new Request.Builder().url(URL).get().build();
        Response response = null;
        String JSON;
        try {
            response = client.newCall(request).execute();
            JSON = response.body().string();
            result = JSON;
        } catch (IOException e) {
            Logger.logError(e);
        }
        /** 关闭Response的body **/
        if (response != null) {
            Objects.requireNonNull(response.body()).close();
        }
        return result;
    }
}
