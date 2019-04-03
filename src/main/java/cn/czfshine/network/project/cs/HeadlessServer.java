package cn.czfshine.network.project.cs;

import cn.czfshine.network.project.cs.config.Config;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * C/S模式的服务器端
 * @author:czfshine
 * @date:2019/4/2 21:49
 */

@Slf4j
public class HeadlessServer {

    /**
     * 服务器监听到一个新的链接时启动一个新的线程处理该Socket
     */
    @Slf4j
    private class Accepter implements Runnable{


        private Socket socket;

        private Accepter(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            log.info("开始处理套接字的数据");

        }
    }

    private void listen()  {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(Config.DEFAULTSERVERPORT, 10);
            log.info("开始监听端口:"+Config.DEFAULTSERVERPORT);
        } catch (IOException e) {
            log.error("开始监听端口失败:"+Config.DEFAULTSERVERPORT);
            e.printStackTrace();
            return;
        }

        try {
            while (true){
                Socket accept = serverSocket.accept();
                log.info("服务器监听到${0}:${1}的链接接入",
                        accept.getInetAddress(),accept.getPort());
                Accepter accepter = new Accepter(accept);
                new Thread(accepter).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startListen(){
        new Thread(this::listen).start();
    }

    public void start(){
        startListen();
    }
    public static void main(String args[]) {
        new HeadlessServer().start();
    }
}
