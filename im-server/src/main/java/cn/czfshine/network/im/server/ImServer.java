package cn.czfshine.network.im.server;
import cn.czfshine.network.im.ImConfig;
import cn.czfshine.network.im.Server;
import cn.czfshine.network.im.ServerImpl;
import cn.czfshine.network.im.dto.Login;
import cn.czsgine.network.p2p.common.PortTools;
import lombok.extern.slf4j.Slf4j;

import javax.naming.ldap.SortKey;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;

/**
 * @author:czfshine
 * @date:2019/6/7 20:40
 */

@Slf4j
public class ImServer extends ServerImpl {
    /**
     * 所有用户的连接
     */
    private HashMap<String, ImConnect> allUserConnect = new HashMap<>();
    public class ImConnect extends Server.SocketThread{
        private Login login;
        @Override
        protected void recv(Object o) {
            log.info("read {}",o.toString());
            if(o instanceof Login){
                log.info("服务器监听到用户{}登录",((Login) o).getUsername());
                allUserConnect.put(((Login) o).getUsername(),this);
                login= (Login) o;
                notifyLogin((Login) o);
                sendLogins(this);
            }
        }
        public ImConnect(){};

        public Login getLogin() {
            return login;
        }
    }

    /**
     * 通知所有用户有用户登录
     */
    private void notifyLogin(Login login){
        log.info("开始广播给{}个用户登录消息",allUserConnect.size());
        for (String key: allUserConnect.keySet()
             ) {
            if (!key.equals(login.getUsername())) {
                ImConnect imConnect = allUserConnect.get(key);
                try {
                    imConnect.getOos().writeObject(login);
                } catch (IOException e) {
                    //todo
                    e.printStackTrace();
                }
            }
        }
    }

    /** 将已登录的用户列表通知给目前的登录用户
     */
    private void sendLogins( ImConnect connect){
        for (String key: allUserConnect.keySet()
        ) {
            if (!key.equals(connect.getLogin().getUsername())) {
                try {
                    connect.getOos().writeObject(allUserConnect.get(key).getLogin());
                } catch (IOException e) {
                    //todo
                    e.printStackTrace();
                }
            }
        }
    }

    public ImServer(){
        newServerImpl(this::createThread, ImConfig.imServerPort);
    }

    public SocketThread createThread(){
        return new ImConnect();
    }
    @Override
    public void start(){
        super.start();
    }
}
