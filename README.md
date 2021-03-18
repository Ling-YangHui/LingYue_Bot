# LingYue_Bot

## 简介

当前版本 Version 1.0.0-RC

这是一个使用[Mirai](https://github.com/mamoe/mirai) 框架搭建的小型QQ群bot项目，当前支持自动回复、好感度（数值系统）、自动复读、特殊成员以及一定的管理员操作能力，具体的操作方法见下文。

本项目由[YangHui](https://github.com/Ling-YangHui) 管理构建。

## 新特性一览

版本 V1.2.0-Alpha

回复功能有改变了！在reply中使用AT表示直接AT回去，AT:(QQ号)表示指定AT某人；使用FACE:(表情号码)可以回复表情！

## 插件功能及使用方法

插件支持一定的非代码开发自定义和代码开发深度自定义。

### 深度自定义

项目通过内部的GroupMessageHandler接口实现深度的自定义。如果自己写了一个用来监测某个群的类，需要继承GroupMessageHandler并实现其中的onCreate、handleMessage和onDelete方法。它们分别会在Bot启动、收到消息和关闭的时候调用。一般而言onCreate用来实现一些文件数据载入的方法，onDelete用来实现文件数据保存的方法。

### 非代码自定义

非代码自定义当前提供了一个XiaoFangZhou类的框架，这个框架支持了自动复读、自动回复、数值系统、特殊群员和管理员等功能。以下是该框架的功能调用方法

#### 自动复读

在文件调用的repeatList.json文件中使用json的array形式输入字符串，Bot会自动复读字符串中的内容，例如

~~~json
[
    "草",
    "?",
    "？"
]
~~~

#### 自动回复

自动回复是一个自定义程度很高的功能，它是由specialRepeatList.json文件来控制的。可以通过增加修改这个文件来实现自动回复的自定义，例如：

~~~json5
[
    {
        /* message 
        表示触发这个回复事件必须含有的字串 */
        "message": "@3598326822 透一下",
        /* trigType 
        表示触发的字串的位置：
         * head：开头 
         * any：任何位置
         * rear：结尾 */
        "trigType": "any",
        /* containMessage
        表示触发后如果满足里面任何一个字符串的要求，那么事件正式触发
        如果这个列表为空，程序将会让检测消息和message是否完全相等，若完全相等则触发
        * 字符串要求规则：
        两个字符串通过&连接表示与逻辑
        一个字符串前面加上$表示非逻辑
        */
        "containMessage": [],
        /* randReply
        表示是否会随机回复或者不回复 */
        "randReply": false,
        /* reply
        表示普通用户可以收到的回复，随机触发一条
        * *****V1.2.0***** 
        * 在reply中使用AT表示直接AT回去，AT:(QQ号)表示指定AT某人；
        * 使用FACE:(表情号码)可以回复表情！*/
        "reply": [
            "啊……好吧",
            "呜~不行",
            "想透我？……",
            "呜呜呜呜呜",
            "你怎么……怎么不去透无瞳gg!"
        ],
        /* specialReply
        表示特殊用户可以收到的回复，随机触发一条 */
        "specialReply": [
            "轻点~",
            "嗯……好吧，来~"
        ],
        /* function
        表示普通用户可以触发的功能 */
        "function": [
            "Like -Float -Small",
            "Fuck -Plus"
        ],
        /* specialFunction
        表示特殊用户可以触发的功能 */
        "specialFunction": [
            "Fuck -Plus",
            "Like -Rise -Small"
        ]
    },
]
~~~

每个回复通过dict形式封装，所有的回复写在一个array里面

对于上述的功能的语法，现在支持了以下规则：

* Like：好感度
    * 变化方向参数
        * -Rise 增加
        * -Float 浮动
        * -Decline 减少
    * 幅度参数
        * -Small 小幅度
        * -Mid 中幅度
        * -Large 大幅度

* Fuck：透的次数
    * -Plus 增加一次

* Get：获取信息
    * Fuck 获取透的次数
    * Like 获取好感度

对于用户，框架是半自动管理的。当用户列表中没有当前发言用户的ID，它就会自动添加，当然了也可以手动修改user.json文件。这个文件只有在Bot关闭的时候才能有效修改。文件格式如下：

~~~json5
{
    "like": 108,
    "hasFuck": 6,
    "name": "1902 阳辉#8474",
    "isAdministrator": true,
    "userID": 2411046022,
    "isSpecialUser": true
}
~~~
含义很清楚，不再赘述

## 版本日志

2021/3/18 &emsp; 1.2.0-Alpha：添加了消息链回复的功能。可以回复表情，at别人。

2021/3/17 &emsp; 1.1.0-Alpha：添加了定时的功能，但是定时的方式和触发方法不太友好

2021/3/17 &emsp; 1.0.0-RC：具备了Bot的基本功能

## 版权信息

CopyRight (2020-?) @YangHui · BUAA

本项目基于[Mirai](https://github.com/mamoe/mirai) 框架搭建

