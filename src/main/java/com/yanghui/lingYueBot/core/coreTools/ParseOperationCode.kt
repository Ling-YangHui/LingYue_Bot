package com.yanghui.lingYueBot.core.coreTools

class ParseOperationCode {

    companion object {
        fun parseOperationCode(operation: String): Int {
            val instructionList: List<String> = operation.split(" ")
            return when (instructionList[0]) {
                "Like" -> 0
                "Get" -> when (instructionList[1]) {
                    "-Like" -> 10
                    else -> -1
                }
                "DriftBottle" -> when (instructionList[1]) {
                    "-GET" -> when (instructionList[2]) {
                        "Local" -> 20
                        "Global" -> 21
                        else -> -1
                    }
                    "-ADD" -> when (instructionList[2]) {
                        "Local" -> 22
                        "Global" -> 23
                        else -> -1
                    }
                    "-Like" -> 24
                    else -> -1
                }
                "Balance" -> 30
                "RandCard" -> when (instructionList[1]) {
                    "Normal" -> 40
                    "Special" -> 41
                    else -> 42
                }
                "Satellite" -> 50
                "MoePic" -> 60
                "RandSeed" -> 70
                else -> -1
            }
        }
    }

}