package cn.czfshine.network.project.cs;

import cn.czfshine.network.project.cs.config.Config;
import cn.czfshine.network.project.cs.service.Server;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * 使用NIO（New IO）的cs服务端
 *
 * @author:czfshine
 * @date:2019/4/5 19:04
 */
@Slf4j
public class HeadlessNIOServer implements Server {


    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    //对应的通道待发送给客户端的消息对象
    private HashMap<SocketChannel,Queue<Object>> todo=new HashMap<>();
    private void openListen() throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().setReuseAddress(true);
        serverSocketChannel.configureBlocking(false);  //使用非阻塞模式
        serverSocketChannel.socket().bind(new InetSocketAddress(Config.DEFAULTSERVERPORT));
    }

    @Override
    public void start() {
        try {
            openListen();
            log.info("开始监听端口：{}", Config.DEFAULTSERVERPORT);
        } catch (IOException e) {
            log.error("监听端口失败");
            e.printStackTrace();
            return;
        }

        try {
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (ClosedChannelException e) {
            log.error("通道已被关闭");
            e.printStackTrace();
            return;
        }
        try {
            while (selector.select() > 0) {
                Set<SelectionKey> readyKeys = selector.selectedKeys();
                readyKeys.forEach((key)->{
                    //readyKeys.remove(key);
                    try {
                        if (key.isAcceptable()) {
                            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                            SocketChannel socketChannel = ssc.accept();
                            if(socketChannel==null){
                                log.debug("socketChannel 为空....");
                                return ;
                            }
                            System.out.println(socketChannel);
                            log.info("监听到{}:{}的连接接入",
                                    socketChannel.socket().getInetAddress(),
                                    socketChannel.socket().getPort());
                            socketChannel.configureBlocking(false);
                            Queue<Object> queue = new LinkedList<>();
                            todo.put(socketChannel,queue);
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            socketChannel.register(selector,
                                    SelectionKey.OP_READ |
                                            SelectionKey.OP_WRITE, buffer);
                        }
                        if (key.isReadable()) {
                            //log.debug("可读啦");
                            try {
                                recv(key);
                            } catch (IOException | ClassNotFoundException e) {
                                log.error("接收消息失败");
                                e.printStackTrace();
                            }
                            //receive(key);
                        }
                        if (key.isWritable()) {
                            //log.debug("可写啦"); //??
                            //send(key);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            key.cancel();
                            key.channel().close();
                        } catch (Exception ex) {
                            e.printStackTrace();
                        }
                    }
                });
                readyKeys.clear();
            }//#while
        } catch (IOException e) { // selector.select()这一句抛出的异常，其他异常被内部捕获了
            log.error("服务器连接异常关闭");
            e.printStackTrace();
            return;
        }

    }

    private final ByteBuffer lengthByteBuffer = ByteBuffer.wrap(new byte[4]);
    private ByteBuffer dataByteBuffer = null;
    private boolean readLength = true;

    //todo
    public void recv(SelectionKey key) throws IOException, ClassNotFoundException {
        SocketChannel socket=(SocketChannel)key.channel();
        if (readLength) {
            socket.read(lengthByteBuffer);
            System.out.println(lengthByteBuffer.limit());
            if (lengthByteBuffer.remaining() == 0) {
                readLength = false;
                int anInt = lengthByteBuffer.getInt(0);
                System.out.println(anInt);
                dataByteBuffer = ByteBuffer.allocate(anInt);
                lengthByteBuffer.clear();
            }
        } else {
            socket.read(dataByteBuffer);
            if (dataByteBuffer.remaining() == 0) {
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(dataByteBuffer.array()));
                final Serializable ret = (Serializable) ois.readObject();
                // clean up
                dataByteBuffer = null;
                readLength = true;
                System.out.println(ret);
            }
        }
    }

    public static void main(String args[]) {
        new HeadlessNIOServer().start();
    }
}
