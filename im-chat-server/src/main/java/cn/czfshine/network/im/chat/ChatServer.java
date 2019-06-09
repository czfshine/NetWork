package cn.czfshine.network.im.chat;
import cn.czfshine.network.im.Sender;
import cn.czfshine.network.im.Server;
import cn.czfshine.network.im.ServerImpl;
import cn.czfshine.network.im.dto.ChatConnect;
import cn.czfshine.network.im.dto.Message;
import cn.czsgine.network.p2p.common.PortTools;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;

/**
 * @author:czfshine
 * @date:2019/6/8 9:35
 */
@Slf4j
public abstract class ChatServer extends ServerImpl implements Sender {

    private HashMap<String,ChatServerThread> allConnect=new HashMap<>();
    class ChatServerThread extends Server.SocketThread{

        @Override
        protected void recv(Object o) {
            if(o instanceof ChatConnect){
                allConnect.put(((ChatConnect) o).getUsername(),this);
                whenOtherUserConnect((ChatConnect) o);
            }else if(o instanceof Message){
                whenReceiveMessage((Message) o);
            }
        }
        public ChatServerThread(){}
    }

    /**
     * 有新用户连接到本地聊天服务
     */
    protected abstract void whenOtherUserConnect(ChatConnect chatConnect);

    /**
     * 接收到消息
     */
    protected abstract void whenReceiveMessage(Message message);

    public ChatServer() {
        ServerSocket serverSocket = PortTools.getServerSocket();
        newServerImpl(this::createThread,serverSocket);
    }
    public SocketThread createThread(){
        return new ChatServerThread();
    }


    @Override
    public void sendTo(String username, Message message) {
        ChatServerThread connect = allConnect.getOrDefault(username, null);
        if(connect!=null){
            try {
                connect.getOos().writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            log.error("username:{} not connected",username);
        }
    }
}
