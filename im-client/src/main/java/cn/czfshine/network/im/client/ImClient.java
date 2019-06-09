package cn.czfshine.network.im.client;
import cn.czfshine.network.im.Client;
import cn.czfshine.network.im.dto.Login;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * @author:czfshine
 * @date:2019/6/7 22:03
 */

public class ImClient extends Client {

    private Consumer<Login> loginConsumer;

    public void Connect(String address, int port, String username, String localAddress, String localPort) throws IOException {
        super.Connect(address, port);
        Login login = new Login();
        login.setUsername(username);
        login.setAddress(localAddress);
        login.setTcpPort(Integer.parseInt(localPort));
        send(login);
    }

    @Override
    public void recv(Object obj) {
        if(obj instanceof Login){
            if(loginConsumer!=null){
                loginConsumer.accept((Login) obj);
            }
        }
    }

    public void whenOtherUserLogin(Consumer<Login> loginConsumer){
        this.loginConsumer=loginConsumer;
    }
}
