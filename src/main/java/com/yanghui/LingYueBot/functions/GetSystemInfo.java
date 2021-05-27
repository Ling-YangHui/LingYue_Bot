package com.yanghui.LingYueBot.functions;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;

public class GetSystemInfo {

    /**
     * 获取系统状态
     *
     * @return 返回系统状态列表
     */
    public static Vector<String> getSystemInfo() {
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec("systemInfo /FO CSV");
            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream(), "GBK"));
            String line = null;
            StringBuilder builder = new StringBuilder();
            while ((line = input.readLine()) != null) {
                builder.append(line);
            }
            String info = '[' + builder.toString() + ']';
            info = info.replace("\\", "\\\\");
            JSONArray infoArray = JSON.parseArray(info);
            Vector<String> result = new Vector<>();
            int len = infoArray.size();
            for (int i = 0; i < len / 2; i++) {
                if (infoArray.getString(i).contains("可用的物理内存") || infoArray.getString(i).contains("处理器") || infoArray.getString(i).contains("虚拟内存: 使用中")) {
                    result.add(infoArray.getString(i) + infoArray.getString(i + len / 2));
                }
            }
            return result;
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return null;
    }
}
