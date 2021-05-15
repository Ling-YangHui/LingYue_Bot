package com.yanghui.LingYueBot.functions;

import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

public class ArknightsRandCard {

    private static final String[] sixArray = {"棘刺", "铃兰", "早露", "温蒂", "傀影", "风笛", "刻俄柏", "阿", "煌", "莫斯提马", "麦哲伦", "赫拉格", "黑", "陈", "斯卡蒂", "银灰", "塞雷娅", "星熊", "夜莺", "闪灵", "安洁莉娜", "艾雅法拉", "伊芙利特", "推进之王", "能天使", "森蚺", "史尔特尔", "瑕光", "泥岩", "山", "空弦", "凯尔希", "刻俄柏", "异客"};
    private static final String[] fiveArray = {"安哲拉", "贾维", "蜜蜡", "断崖", "莱恩哈特", "月禾", "石棉", "极境", "巫恋", "慑砂", "惊蛰", "吽", "灰喉", "布洛卡", "苇草", "槐琥", "送葬人", "星极", "格劳克斯", "诗怀雅", "夜魔", "食铁兽", "狮蝎", "空", "真理", "初雪", "崖心", "守林人", "普罗旺斯", "可颂", "雷蛇", "红", "临光", "华法琳", "赫默", "梅尔", "天火", "陨星", "白金", "蓝毒", "幽灵鲨", "拉普兰德", "芙兰卡", "德克萨斯", "凛冬", "白面鸮", "燧石", "四月", "奥斯塔", "絮雨", "卡夫卡", "爱丽丝"};
    private static final String[] fourArray = {"孑", "卡达", "波登可", "刻刀", "宴", "安比尔", "梅", "红云", "桃金娘", "苏苏洛", "格雷伊", "猎蜂", "阿消", "地灵", "深海色", "古米", "蛇屠箱", "角峰", "调香师", "嘉维尔", "末药", "暗索", "砾", "慕斯", "霜叶", "缠丸", "杜宾", "红豆", "清道夫", "讯使", "白雪", "流星", "杰西卡", "远山", "夜烟", "酸糖", "芳汀", "泡泡", "杰克", "松果", "豆苗"};
    private static final String[] threeArray = {"斑点", "泡普卡", "月见夜", "空爆", "梓兰", "史都华德", "安塞尔", "芙蓉", "炎熔", "安德切尔", "克洛斯", "米格鲁", "卡缇", "梅兰莎", "翎羽", "香草", "芬"};
    private static final String[] upSixArray = {"森蚺", "阿"};
    private static final String[] upFiveArray = {"白面鸮", "真理", "蓝毒"};
    private static final String[] priorityArray = {};

    private static final Vector<String> six = new Vector<>();
    private static final Vector<String> five = new Vector<>();
    private static final Vector<String> four = new Vector<>();
    private static final Vector<String> three = new Vector<>();
    private static final Vector<String> upSix = new Vector<>();
    private static final Vector<String> upFive = new Vector<>();
    private static final Vector<String> priority = new Vector<>();

    private static final int upSixPossibility = 7;
    private static final int upFivePossibility = 5;

    static {
        six.addAll(Arrays.asList(sixArray));
        five.addAll(Arrays.asList(fiveArray));
        four.addAll(Arrays.asList(fourArray));
        three.addAll(Arrays.asList(threeArray));
        upSix.addAll(Arrays.asList(upSixArray));
        upFive.addAll(Arrays.asList(upFiveArray));
        priority.addAll(Arrays.asList(priorityArray));
        six.removeAll(upSix);
        six.removeAll(priority);
    }

    public static Vector<Vector<String>> rand(int num) throws Exception {
        double[] possibility = {0.02, 0.1, 0.6, 1};
        if (num > 100 || num <= 0)
            throw new Exception();
        Vector<Vector<String>> result = new Vector<>();
        Vector<String> allResult = new Vector<>();
        Vector<String> sixResult = new Vector<>();
        Vector<String> fiveResult = new Vector<>();
        Vector<String> fourResult = new Vector<>();
        Vector<String> threeResult = new Vector<>();
        int noSixNum = 0;
        for (int i = 0; i < num; i++) {
            String name = randOne(possibility);
            if (isSixStar(name)) {
                noSixNum = 0;
                possibility[0] = 0.02;
                possibility[1] = 0.1;
                possibility[2] = 0.6;
                possibility[3] = 1;
                sixResult.add(name);
            } else
                noSixNum++;
            int star = getStar(name);
            switch (star) {
                case 5:
                    fiveResult.add(name);
                    break;
                case 4:
                    fourResult.add(name);
                    break;
                case 3:
                    threeResult.add(name);
                    break;
            }
            if (noSixNum > 50)
                for (int j = 0; j < 4; j++) {
                    possibility[j] += 0.02;
                }
            allResult.add(name);
        }
        result.add(allResult);
        result.add(sixResult);
        result.add(fiveResult);
        result.add(fourResult);
        result.add(threeResult);
        return result;
    }

    private static String randOne(double[] possibility) {
        int randCache = new Random().nextInt(100);
        if (randCache < possibility[0] * 100) { // 6星
            // 先处理up干员
            randCache = new Random().nextInt(10);
            if (randCache < upSixPossibility) // 出现up
            {
                return upSix.get(new Random().nextInt(upSix.size()));
            } else { // 池子歪了
                // 如果没有五倍权值
                if (priority.isEmpty())
                    return six.get(new Random().nextInt(six.size()));
                else {
                    int allNum = six.size() + 5 * priority.size();
                    randCache = new Random().nextInt(allNum);
                    if (randCache < six.size())
                        return six.get(randCache);
                    else
                        return priority.get(0);
                }
            }
        } else if (randCache < possibility[1] * 100) {
            // 先处理up干员
            randCache = new Random().nextInt(10);
            // 如果没有up
            if (upFive.isEmpty())
                return five.get(new Random().nextInt(five.size()));

            if (randCache < upFivePossibility) // 出现up
            {
                return upFive.get(new Random().nextInt(upFive.size()));
            } else { // 池子歪了
                return five.get(new Random().nextInt(five.size()));
            }

        } else if (randCache < possibility[2] * 100) {
            return four.get(new Random().nextInt(four.size()));
        } else {
            return three.get(new Random().nextInt(three.size()));
        }
    }

    public static boolean isSixStar(String name) {
        return upSix.contains(name) || priority.contains(name) || six.contains(name);
    }

    private static int getStar(String name) {
        if (isSixStar(name))
            return 6;
        if (upFive.contains(name) || five.contains(name))
            return 5;
        if (four.contains(name))
            return 4;
        return 3;
    }
}
