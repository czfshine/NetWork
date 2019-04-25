package cn.czsgine.network.p2p.common;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;

/**
 * @author:czfshine
 * @date:2019/4/17 15:52
 */

public class PortTools {
    public static ServerSocket getServerSocket(){
        ServerSocket serverSocket = null;
        int def = Config.CLIENTTCPPORT;
        while(def<65534){
            try {
                serverSocket = new ServerSocket(def, 10);
                break;
            } catch (BindException e){
                def++;
                continue;
            } catch (IOException e) {
                continue;
            }
        }
        return serverSocket;
    }
}
