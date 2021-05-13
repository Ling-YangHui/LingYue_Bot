package com.yanghui.LingYueBot.functions;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yanghui.LingYueBot.core.coreTools.JsonLoader;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DriftBottle {

    private static final String rootPath = "D:\\IntelliJ IDEA programming\\MiraiRobot\\MiraiResources\\LingYue_resources\\";
    private static final String name = "driftBottle.json";
    private static final String path = rootPath + name;
    public static JSONArray driftBottleArrayAcrossGroup;

    static {
        try {
            driftBottleArrayAcrossGroup = JsonLoader.jsonArrayLoader(path);

            // 获取当前系统日期
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Timer scheduleTimer = new Timer();
            final String[] pastTime = {""};
            scheduleTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    String nowTime = sdf.format(new Date());
                    if (nowTime.equals(pastTime[0])) {
                        return;
                    }
                    try {
                        saveDriftBottleFromAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    pastTime[0] = nowTime;
                }
            }, 10, 1000 * 30);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final JSONArray driftBottleArray;

    public DriftBottle(JSONArray driftBottleArray) {
        this.driftBottleArray = driftBottleArray;
    }

    public static void addDriftBottleToAll(JSONObject newBottle) {
        boolean hasSameBottle = false;
        for (int i = 0; i < driftBottleArrayAcrossGroup.size(); i++) {
            if (newBottle.getString("message").equals(driftBottleArrayAcrossGroup.getJSONObject(i).getString("message"))) {
                hasSameBottle = true;
                break;
            }
        }
        if (!hasSameBottle)
            synchronized (path) {
                driftBottleArrayAcrossGroup.add(newBottle);
            }
    }

    public static JSONObject getDriftBottleFromAll() {
        int len = driftBottleArrayAcrossGroup.size();
        if (len == 0)
            return null;
        JSONObject result = driftBottleArrayAcrossGroup.getJSONObject(new Random().nextInt(len));
        synchronized (path) {
            result.put("pick", result.getIntValue("pick") + 1);
            if (result.getIntValue("pick") >= 2)
                driftBottleArrayAcrossGroup.remove(result);
        }
        return result;
    }

    public static Vector<JSONObject> removeDriftBottleFromAll(String str) {
        Vector<JSONObject> remove = new Vector<>();
        for (int i = 0; i < driftBottleArrayAcrossGroup.size(); i++) {
            if (driftBottleArrayAcrossGroup.getJSONObject(i).getString("message").equals(str)) {
                remove.add(driftBottleArrayAcrossGroup.getJSONObject(i));
            }
        }
        synchronized (path) {
            driftBottleArrayAcrossGroup.removeAll(remove);
        }
        return remove;
    }

    public static void saveDriftBottleFromAll() throws Exception {
        JsonLoader.saveJSONArray(rootPath, name, driftBottleArrayAcrossGroup);
    }

    public static int getBottleNumFromAll() {
        return driftBottleArrayAcrossGroup.size();
    }

    public void addDriftBottle(JSONObject newBottle) {
        boolean hasSameBottle = false;
        for (int i = 0; i < this.driftBottleArray.size(); i++) {
            if (newBottle.getString("message").equals(driftBottleArray.getJSONObject(i).getString("message"))) {
                hasSameBottle = true;
                break;
            }
        }
        if (!hasSameBottle)
            synchronized (driftBottleArray) {
                driftBottleArray.add(newBottle);
            }
    }

    public JSONObject getDriftBottle() {
        int len = driftBottleArray.size();
        if (len == 0)
            return null;
        JSONObject result = driftBottleArray.getJSONObject(new Random().nextInt(len));
        synchronized (driftBottleArray) {
            result.put("pick", result.getIntValue("pick") + 1);
            if (result.getIntValue("pick") >= 2)
                driftBottleArray.remove(result);
        }
        return result;
    }

    public Vector<JSONObject> removeBottle(String str) {
        Vector<JSONObject> remove = new Vector<>();
        for (int i = 0; i < this.driftBottleArray.size(); i++) {
            if (driftBottleArray.getJSONObject(i).getString("message").equals(str)) {
                remove.add(driftBottleArray.getJSONObject(i));
            }
        }
        synchronized (driftBottleArray) {
            driftBottleArray.removeAll(remove);
        }
        return remove;
    }

    public void saveDriftBottle(String rootPath, String name) throws Exception {
        JsonLoader.saveJSONArray(rootPath, name, driftBottleArray);
    }

    public int getBottleNum() {
        return this.driftBottleArray.size();
    }

}
