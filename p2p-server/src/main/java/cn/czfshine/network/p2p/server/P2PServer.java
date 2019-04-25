package cn.czfshine.network.p2p.server;
import cn.czsgine.network.p2p.common.Config;
import cn.czsgine.network.p2p.common.dto.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author:czfshine
 * @date:2019/4/17 11:40
 */

@Slf4j
public class P2PServer {
    private ConcurrentMap<String, LinkedList<ClientInfo>> filemap=new ConcurrentHashMap<>();
    private ConcurrentMap<String,FileInfo> fileinfomap=new ConcurrentHashMap<>();
    /**
     * 服务器监听到一个新的链接时启动一个新的线程处理该Socket
     */
    private class Accepter implements Runnable{
        private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Accepter.class);

        private Socket socket;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;
        private String hostname;
        private ClientInfo clientInfo=new ClientInfo();
        private Accepter(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            log.info("开始处理套接字的数据");

            try {
                socket.setTcpNoDelay(true);
                socket.setSoTimeout(6000 * 1000);
                socket.setKeepAlive(true);
                ois = new ObjectInputStream(socket.getInputStream());
                oos = new ObjectOutputStream(socket.getOutputStream());

                Object o = ois.readObject();
                //第一个消息必然是LoginMessage对象
                if(o instanceof LoginMessage){
                    LoginMessage loginMessage = (LoginMessage)o;
                    log.info("登录的主机名为{}",loginMessage.getHostname());
                    hostname=loginMessage.getHostname();
                    clientInfo.setLogininfo(loginMessage);
                }else{
                    log.warn("该客户端没有遵守P2P协议");
                    return ; //todo：关闭socket在这里写会造成重复代码（虽然。。。）
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            log.info("等待客户端消息");
            while(true){
                try {
                    //大部分时间都会阻塞在这一句
                    Object o = ois.readObject();
                    if(o instanceof FileList){
                        FileList fileList =(FileList)o;
                        log.info("收到主机{}的文件列表，长度为{}",hostname,fileList.getFileInfos().size());
                        for (FileInfo fi: fileList.getFileInfos()
                             ) {
                            LinkedList<ClientInfo> clientInfos = filemap.get(fi.getFilename());
                            if(clientInfos==null){
                                clientInfos=new LinkedList<>();
                                clientInfos.add(clientInfo);
                                filemap.put(fi.getFilename(),clientInfos);
                            }else{
                                clientInfos.add(clientInfo);
                            }
                            fileinfomap.put(fi.getFilename(),fi);
                        }
                        log.info("已将主机{}文件列表加到缓存",hostname);
                    }else if(o instanceof FileRquest){
                        FileRquest fileRquest= (FileRquest)o;
                        String filename = fileRquest.getFilename();
                        log.info("收到主机{}的文件请求，文件名为{}",hostname,filename);

                        LinkedList<ClientInfo> clientInfos = filemap.get(filename);
                        ClientList clientList = new ClientList();
                        if(clientInfos==null){
                            log.info("请求的文件{}没有客户端拥有",filename);
                        }else {
                            clientList.setEmpty(false);
                            for (ClientInfo ci:clientInfos
                                 ) {
                                clientList.add(ci);
                            }
                            clientList.setFileInfo(fileinfomap.getOrDefault(filename,null));
                        }
                        oos.writeObject(clientList);
                        oos.flush();
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                    log.info("主机{}异常断开连接",hostname);
                    break;
                } catch (ClassNotFoundException e) {
                    log.warn("读取对象异常");
                    e.printStackTrace();
                }
            }
            if (socket.isClosed()) {
            } else {
                try {
                    //保证关闭了socket
                    socket.close();
                } catch (IOException e) {
                    log.error("关闭socket失败");
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 监听线程
     */
    private void listen()  {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(Config.SERVERPORT, 10);
            log.info("开始监听端口:"+Config.SERVERPORT);
        } catch (BindException e){
            log.error("开始监听端口失败:{} 端口被占用",Config.SERVERPORT);
            return ;
        } catch (IOException e) {
            log.error("开始监听端口失败:{}",Config.SERVERPORT);
            e.printStackTrace();
            return;
        }

        try {
            do {
                Socket accept = serverSocket.accept();
                log.info("服务器监听到{}:{}的链接接入",
                        accept.getInetAddress(), accept.getPort());
                Accepter accepter = new Accepter(accept);
                new Thread(accepter).start();
            } while (true);
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
    public void start(){
        startListen();
    }

    public static void main(String args[]) {
        new P2PServer().start();
    }
}
