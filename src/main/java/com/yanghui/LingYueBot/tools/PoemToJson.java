package com.yanghui.LingYueBot.tools;

import java.util.Scanner;

public class PoemToJson {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        StringBuilder jsonString = new StringBuilder("\"");
        String cache;
        while (true) {
            cache = scanner.nextLine();
            if (cache.isEmpty())
                continue;
            if (cache.equals("END"))
                break;
            jsonString.append(cache);
            jsonString.append("\\n");
        }
        System.out.println(jsonString.append("\"").toString());
    }
}
