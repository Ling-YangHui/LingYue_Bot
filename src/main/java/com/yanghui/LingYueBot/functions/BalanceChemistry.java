package com.yanghui.LingYueBot.functions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BalanceChemistry {

    public static String balanceChemistry(String str) throws IOException {
        if (str.equals("--help")) {
            return "运行格式：\n@Bot(空格)配平(空格)方程式\n方程式可以是直接用等号连接，也可以用分号进行模糊匹配，如H2;O2;H2O，元素符号必须要保证正确";
        }
        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec("python -u C:\\LingYue\\Balance.py " + str);
        BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream(), "GBK"));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = input.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }
}
