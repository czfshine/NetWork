package cn.czfshine.network.p2p.client;

import cn.czfshine.network.p2p.client.file.DirInfo;
import cn.czfshine.network.p2p.client.file.FileServer;
import cn.czfshine.network.p2p.client.fun.Logger;
import cn.czsgine.network.p2p.common.Config;
import cn.czsgine.network.p2p.common.dto.*;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/**
 * P2P 文件客户端
 * @author:czfshine
 * @date:2019/4/17 15:06
 */

public class P2PClient {
    private ObjectInputStream ois;
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void setRecvClientList(RecvClientList recvClientList) {
        this.recvClientList = recvClientList;
    }

    @FunctionalInterface
    public interface ErrorHandler{
        void handle();
    }
    @FunctionalInterface
    public static interface RecvClientList{
        void recv(ClientList clientList);
    }

    private RecvClientList recvClientList;

    private Logger logger;
    public void setLogger(Logger logger){
        this.logger=logger;
    }

    Socket socket;
    ObjectOutputStream oos;
    public void connect(String ip,int port) throws IOException {
        socket = new Socket(ip, port);
        socket.setSendBufferSize(4096);
        socket.setTcpNoDelay(true);
        socket.setSoTimeout(6000 * 1000);
        socket.setKeepAlive(true);
        logger.log("链接服务器成功");
        oos = new ObjectOutputStream(socket.getOutputStream());
        Thread thread = new Thread(this::listen);
        thread.setDaemon(true);
        thread.start();
    }
    private ErrorHandler errorHandler;
    public void listen(){
        logger.log("开始监听");
        try {
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true){
            try {
                Object o = ois.readObject();
                if(o instanceof ClientList){
                    logger.log("收到文件的客户端列表");
                    logger.log(o.toString());
                    recvClientList.recv((ClientList)o);
                }

            } catch (IOException e) {
                errorHandler.handle();
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    public void close(String username){
        try {
            //oos.writeObject(logout);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String localpath;
    private String hostname;

    public P2PClient(String localpath, String hostname) {
        this.localpath = localpath;
        this.hostname = hostname;
    }


    public void hello(){
        LoginMessage loginMessage = new LoginMessage(socket.getInetAddress().getHostAddress(), FileServer.getPort(), Config.CLIENTUDPPORT);
        loginMessage.setHostname(hostname);
        File[] files = DirInfo.getFiles();
        FileList fileList = new FileList();
        for (File f:files
             ) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFilename(f.getName());
            fileInfo.setSize(f.length());
            fileList.add(fileInfo);
        }
        try {
            oos.writeObject(loginMessage);
            oos.writeObject(fileList);
            logger.log("发送文件列表成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFileRequest(String filename) throws IOException {
        FileRquest fileRquest = new FileRquest(filename);
        oos.writeObject(fileRquest);
        oos.flush();
        logger.log("发送文件"+filename+"的请求成功");
    }
    private void deflogger(String msg){
        System.out.println(msg);
    }
    public static void main(String args[]) throws IOException {
        P2PClient p2PClient = new P2PClient("./A","A");
        p2PClient.setLogger(p2PClient::deflogger);
        p2PClient.connect("127.0.0.1", Config.SERVERPORT);
        p2PClient.hello();
        p2PClient.sendFileRequest("1.txt");

        System.in.read();//pause
    }
}
