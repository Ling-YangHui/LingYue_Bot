package com.yanghui.LingYueBot.functions;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yanghui.LingYueBot.core.coreTools.JsonLoader;

import java.util.Random;
import java.util.Vector;

public class DriftBottle {

    private final JSONArray driftBottleArray;

    public DriftBottle(JSONArray driftBottleArray) {
        this.driftBottleArray = driftBottleArray;
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

    public void saveDriftBottle(String path) throws Exception {
        JsonLoader.saveJSONArray(path, driftBottleArray);
    }

    public int getBottleNum() {
        return this.driftBottleArray.size();
    }

}
