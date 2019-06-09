package cn.czfshine.network.im;

import lombok.Data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author:czfshine
 * @date:2019/6/7 16:49
 */

public interface Server {
    @Data
    abstract class SocketThread extends  Thread{

        @Override
        public void run() {
            try {
                oos=new ObjectOutputStream(socket.getOutputStream());
                ois=new ObjectInputStream(socket.getInputStream());
                while (true){
                    Object o = ois.readObject();
                    recv(o);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        protected abstract void recv(Object o);
        private ObjectInputStream ois;
        private ObjectOutputStream oos;
        private Socket socket;

        public Socket getSocket() {
            return socket;
        }

        public void setSocket(Socket socket) {
            this.socket = socket;
        }
        public SocketThread(){};

    }

    /**
     * 启动服务器
     */
    void start();
}
