package cn.czfshine.network.p2p.client.file;

import cn.czfshine.network.p2p.client.fun.RecvBlock;
import cn.czsgine.network.p2p.common.dto.BlockRequest;
import cn.czsgine.network.p2p.common.dto.BlockResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author:czfshine
 * @date:2019/4/17 15:06
 */

public class FileClient {

    private ObjectInputStream ois;
    Socket socket;
    ObjectOutputStream oos;
    boolean isdownloading=false;

    private String ip;
    private int port;

    @Override
    public String toString() {
        return ip + ':' +port;
    }

    public void connect(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        this.ip=ip;
        this.port=port;
        socket.setSendBufferSize(4096);
        socket.setTcpNoDelay(true);
        socket.setSoTimeout(6000 * 1000);
        socket.setKeepAlive(true);
        oos = new ObjectOutputStream(socket.getOutputStream());
        Thread thread = new Thread(this::listen);
        thread.setDaemon(true);
        thread.start();
    }

    public void recvBlock(BlockResponse blockResponse){
        System.out.println(blockResponse.toString());
        isdownloading=false;
    }

    private RecvBlock recvBlock;
    public void setRecvBlock(RecvBlock recvBlock){
        this.recvBlock=recvBlock;
    }

    public void listen(){

        try {
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("开始监听");
        while(true){
            try {

                Object o = ois.readObject();
                //System.out.println(o.toString());
                //recvBlock((BlockResponse)o);
                if(o instanceof BlockResponse){
                    if(recvBlock!=null){
                        recvBlock.recv(this,(BlockResponse)o);
                    }
                }else{
                    System.out.println(o.getClass());
                }

            } catch (IOException e) {
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

    public void sendRequest(BlockRequest blockRequest) throws IOException {
        System.out.println("send"+blockRequest.toString());
        oos.writeObject(blockRequest);
        oos.flush();
        isdownloading=true;
    }
    public static void main(String args[]) throws IOException, InterruptedException {
        DirInfo.dirname="./A/";
        FileClient fc = new FileClient();
        fc.connect("127.0.0.1",2333);

        for (int i = 0; i < 100; i++) {
            fc.sendRequest(new BlockRequest("1.txt",1024*256,i));
            Thread.sleep(100);
        }
        System.in.read();
    }


}
