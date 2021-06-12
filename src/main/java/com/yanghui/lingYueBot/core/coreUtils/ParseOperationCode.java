package com.yanghui.lingYueBot.core.coreUtils;

public class ParseOperationCode {

    public static int parseOperationCode(String operation) {
        String[] instructionList = operation.split(" ");
        return switch (instructionList[0]) {
            case "Like" -> 0;
            case "Get" -> switch (instructionList[1]) {
                case "-Like" -> 10;
                default -> -1;
            };
            case "DriftBottle" -> switch (instructionList[1]) {
                case "-GET" -> switch (instructionList[2]) {
                    case "Local" -> 20;
                    case "Global" -> 21;
                    default -> -1;
                };
                case "-ADD" -> switch (instructionList[2]) {
                    case "Local" -> 22;
                    case "Global" -> 23;
                    default -> -1;
                };
                case "-Like" -> 24;
                default -> -1;
            };
            case "Balance" -> 30;
            case "RandCard" -> switch (instructionList[1]) {
                case "Normal" -> 40;
                case "Special" -> 41;
                default -> 42;
            };
            case "Satellite" -> 50;
            case "MoePic" -> 60;
            case "RandSeed" -> 70;
            case "AIReply" -> 80;
            default -> -1;
        };
    }

}
