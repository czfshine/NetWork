package cn.czfshine.network.im;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

/**
 * @author:czfshine
 * @date:2019/6/7 16:57
 */
@Slf4j
public class ServerImpl implements Server {


    public Integer getPort() {
        return port;
    }

    private Integer port;
    private Supplier<SocketThread> listener;
    private ServerSocket serverSocket;
    protected void newServerImpl(Supplier<SocketThread> listener, Integer port) {
        this.listener = listener;
        this.port = port;

    }

    protected void newServerImpl(Supplier<SocketThread> listener, ServerSocket serverSocket){
        this.serverSocket=serverSocket;
        this.listener = listener;
        this.port = serverSocket.getLocalPort();
    }

    /**
     * 启动服务器
     */
    @Override
    public void start() {
        Thread thread = new Thread(this::waitAccept);
        thread.setName("server listen");
        thread.start();
    }
    private void waitAccept(){
        try {
            if(serverSocket == null ){
                serverSocket = new ServerSocket(port);
            }
            log.info("开始监听端口{}",port);
            while (true){

                Socket accept = serverSocket.accept();
                SocketThread socketThread = listener.get();
                socketThread.setSocket(accept);
                socketThread.setDaemon(true);
                socketThread.setName("bio-sokcet");
                socketThread.start();
                log.info("{}：{}接入",accept.getInetAddress(),accept.getPort());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
