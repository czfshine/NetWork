package cn.czfshine.network.im;
import cn.czfshine.network.im.Client;
import cn.czfshine.network.im.dto.Message;

/**
 * @author:czfshine
 * @date:2019/6/8 11:00
 */

public abstract class ChatClient extends Client  implements Sender{

    @Override
    public void recv(Object o) {
        if(o instanceof Message){
            whenReceiveMessage((Message) o);
        }
    }
    protected abstract void whenReceiveMessage(Message message);

    @Override
    public void sendTo(String username, Message message) {
        send(message);
    }
}
