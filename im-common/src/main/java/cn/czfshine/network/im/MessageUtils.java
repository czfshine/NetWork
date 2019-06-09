package cn.czfshine.network.im;

import cn.czfshine.network.im.dto.Message;
import cn.czfshine.network.im.dto.TextMessage;

import java.awt.*;
import java.text.SimpleDateFormat;

/**
 * @author:czfshine
 * @date:2019/6/9 11:03
 */

public class MessageUtils {

    public static String format(Message message){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd hh:mm:ss ");
        String dateStr = simpleDateFormat.format(message.getTime());
        String head = dateStr+message.getUsername()+"\n\t";
        String res="";
        if(message instanceof TextMessage){
            res+=head;
            res+=((TextMessage) message).getContent().replace("\n","\n\t");
            res+="\n\n";
        }
        return res;
    }
}
