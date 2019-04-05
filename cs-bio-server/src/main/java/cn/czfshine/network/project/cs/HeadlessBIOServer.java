package cn.czfshine.network.project.cs;

import cn.czfshine.network.project.cs.config.Config;
import cn.czfshine.network.project.cs.dto.Login;
import cn.czfshine.network.project.cs.dto.Logout;
import cn.czfshine.network.project.cs.dto.Message;
import cn.czfshine.network.project.cs.service.Server;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * C/S模式的服务器端
 * @author:czfshine
 * @date:2019/4/2 21:49
 */

@Slf4j
public class HeadlessBIOServer implements Server {

    //存放当前所有活动的socket和对应的outputStream
    HashMap<Socket,ObjectOutputStream> alloos;

    /**
     * 服务器监听到一个新的链接时启动一个新的线程处理该Socket
     */
    private class Accepter implements Runnable{
        private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Accepter.class);

        private Socket socket;
        private String username;
        private Accepter(Socket socket) {
            this.socket = socket;
        }
        private ObjectInputStream ois;
        @Override
        public void run() {
            log.info("开始处理套接字的数据");

            try {
                socket.setTcpNoDelay(true);
                socket.setSoTimeout(6000 * 1000);
                socket.setKeepAlive(true);

                ois = new ObjectInputStream(socket.getInputStream());
                Object o = ois.readObject();
                if(o instanceof Login){ //第一个消息必然是是login对象
                    Login login=(Login)o;
                    log.info("登录的用户名为{}",login.getUsername());
                    username=login.getUsername();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            log.info("等待客户端消息");
            while(true){
                try {
                    //大部分时间都会阻塞在这一句
                    Object o = ois.readObject();
                    if(o instanceof Message){
                        Message message=(Message)o;
                        log.info("收到用户：{}的信息：{}",username,message.getContent());
                        rodcast(message);
                    }else{
                        if(o instanceof Logout){
                            log.info("用户{}自动退出",((Logout)o).getUsername());
                            alloos.remove(socket);
                        }else{
                            log.warn("收到未知的消息对象",o.toString());
                        }
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                    log.info("用户{}异常断开连接",username);
                    break;
                } catch (ClassNotFoundException e) {
                    log.warn("读取对象异常");
                    e.printStackTrace();

                }
            }
            if(!socket.isClosed()){
                try {
                    //保证关闭了socket
                    socket.close();
                } catch (IOException e) {
                    log.error("关闭socket失败");
                    e.printStackTrace();
                }
            }
            //监听的循环结束，删掉缓存
            alloos.remove(socket);
        }
    }

    /**向所有活动的客户端广播一条消息
     * @param message
     */
    private void rodcast(Object message){
        log.info("开始广播消息");

        for (Socket socket:alloos.keySet()
             ) {
            ObjectOutputStream objectOutputStream = alloos.get(socket);
            try {
                objectOutputStream.writeObject(message);
            } catch (IOException e) {
                log.warn("广播失败");
                e.printStackTrace();
            }
        }
    }

    /**
     * 监听线程
     */
    private void listen()  {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(Config.DEFAULTSERVERPORT, 10);
            log.info("开始监听端口:"+Config.DEFAULTSERVERPORT);
        } catch (BindException e){
            log.error("开始监听端口失败:{} 端口被占用",Config.DEFAULTSERVERPORT);
            return ;
        } catch (IOException e) {
            log.error("开始监听端口失败:{}",Config.DEFAULTSERVERPORT);
            e.printStackTrace();
            return;
        }

        try {
            while (true){
                Socket accept = serverSocket.accept();
                log.info("服务器监听到{}:{}的链接接入",
                        accept.getInetAddress(),accept.getPort());
                Accepter accepter = new Accepter(accept);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(accept.getOutputStream());
                alloos.put(accept,objectOutputStream);
                new Thread(accepter).start();
            }
        } catch (IOException e) {
            log.error("服务器异常停止");
            e.printStackTrace();
        }
    }

    /**
     * 开启监听线程
     */
    private void startListen(){
        new Thread(this::listen).start();
    }

    /**
     * 初始化函数
     */
    @Override
    public void start(){
        alloos=new HashMap<>();
        startListen();
    }

    public static void main(String args[]) {
        new HeadlessBIOServer().start();
    }
}
