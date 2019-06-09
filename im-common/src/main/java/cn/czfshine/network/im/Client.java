package cn.czfshine.network.im;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author:czfshine
 * @date:2019/6/7 21:32
 */
@Data
@Slf4j
public abstract class Client {
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    public void Connect(String address ,int port) throws IOException {
        socket = new Socket(address, port);
        oos=new ObjectOutputStream(socket.getOutputStream());
        new Thread(this::listen).start();
    }
    public void send(Object o){
        try {
            oos.writeObject(o);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void recv(Object obj);
    private void listen(){
        try {
            //todo warring!!!
            ois= new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            while(true){

                Object o = ois.readObject();
                recv(o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
