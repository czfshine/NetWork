package cn.czfshine.network.project.cs.bioclient;

import cn.czfshine.network.project.cs.dto.Login;
import cn.czfshine.network.project.cs.dto.Logout;
import cn.czfshine.network.project.cs.dto.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author:czfshine
 * @date:2019/4/4 12:17
 */

public class Client {
    private ObjectInputStream ois;

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @FunctionalInterface
    public interface  MessageHandler {
        boolean receive(Message message);
    }

    @FunctionalInterface
    public interface ErrorHandler{
        void handle();
    }
    Socket socket;
    ObjectOutputStream oos;
    public void connect(String ip,int port) throws IOException {
        socket = new Socket(ip, port);
        socket.setSendBufferSize(4096);
        socket.setTcpNoDelay(true);
        socket.setSoTimeout(6000 * 1000);
        socket.setKeepAlive(true);
        oos = new ObjectOutputStream(socket.getOutputStream());
        Thread thread = new Thread(this::listen);
        thread.setDaemon(true);
        thread.start();
    }
    public void sendLogin(String username) throws IOException {
        //oos.writeObject(new Login(username));
    }
    public void sendMessage(Message message) throws IOException {
        //oos.writeObject(message);
    }
    MessageHandler handler;
    private ErrorHandler errorHandler;
    public void setHandle(MessageHandler handle){
        handler=handle;
    }
    public void listen(){

        try {
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true){
            try {
                Object o = ois.readObject();
                if(o instanceof Message){
                    Message message=(Message)o;
                    handler.receive(message);
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
        Logout logout = new Logout();
        logout.setUsername(username);
        try {
            oos.writeObject(logout);
        } catch (IOException e) {
                e.printStackTrace();
        }
    }
}
