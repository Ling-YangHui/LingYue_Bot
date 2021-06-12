package com.yanghui.lingYueBot.core.coreUtils;

import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.QuoteReply;

import java.util.Vector;

public class ExtractSingleMessage {

    public static Vector<Image> extractImage(MessageChain chain) {
        Vector<Image> returnValue = new Vector<>();
        System.out.println(chain.toString());
        for (net.mamoe.mirai.message.data.SingleMessage singleMessage : chain) {
            System.out.println(Image.class.isAssignableFrom(singleMessage.getClass()));
            System.out.println(singleMessage.getClass());
            if (Image.class.isAssignableFrom(singleMessage.getClass())) {
                returnValue.add((Image) singleMessage);
            }
        }
        return returnValue;
    }

    public static QuoteReply extractQuote(MessageChain chain) {
        for (net.mamoe.mirai.message.data.SingleMessage singleMessage : chain) {
            System.out.println(QuoteReply.class.isAssignableFrom(singleMessage.getClass()));
            System.out.println(singleMessage.getClass());
            if (QuoteReply.class.isAssignableFrom(singleMessage.getClass())) {
                return (QuoteReply) singleMessage;
            }
        }
        return null;
    }

    public static MessageChain extractQuoteOrigin(MessageChain chain) {
        QuoteReply message = extractQuote(chain);
        if (message != null) {
            return message.getSource().getOriginalMessage();
        }
        return null;
    }

    public static boolean containsQuote(MessageChain chain) {
        return !(extractQuoteOrigin(chain) == null);
    }

    public static boolean containsImage(MessageChain chain) {
        return !(extractImage(chain).size() == 0);
    }

}
