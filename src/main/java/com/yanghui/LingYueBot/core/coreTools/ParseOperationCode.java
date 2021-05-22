package com.yanghui.LingYueBot.core.coreTools;

public class ParseOperationCode {

    public static int parseOperationCode(String operation) {
        String[] instructionList = operation.split(" ");
        switch (instructionList[0]) {
            case "Like":
                return 0;
            case "Get":
                switch (instructionList[1]) {
                    case "-Like":
                        return 10;
                }
                break;
            case "DriftBottle":
                if (instructionList[1].equals("-GET")) {
                    if (instructionList[2].equals("Local"))
                        return 20;
                    else if (instructionList[2].equals("Global"))
                        return 21;

                } else if (instructionList[1].equals("-ADD")) {
                    if (instructionList[2].equals("Local")) {
                        return 22;
                    } else if (instructionList[2].equals("Global")) {
                        return 23;
                    }
                }
                break;
            case "Balance":
                return 30;
            case "RandCard":
                if (instructionList[1].equals("Normal"))
                    return 40;
                else if (instructionList[1].equals("Special"))
                    return 41;
            case "Satellite":
                return 50;
            case "MoePic":
                return 60;
            default:
                return -1;
        }
        return -1;
    }

}
