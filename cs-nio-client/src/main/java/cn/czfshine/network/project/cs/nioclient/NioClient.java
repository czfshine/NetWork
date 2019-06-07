package cn.czfshine.network.project.cs.nioclient;

import cn.czfshine.network.project.cs.Client;
import cn.czfshine.network.project.cs.dto.Login;
import cn.czfshine.network.project.cs.dto.Message;
import lombok.extern.slf4j.Slf4j;
import sun.nio.ch.SelectionKeyImpl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * @author:czfshine
 * @date:2019/4/4 12:17
 */
@Slf4j
public class NioClient implements Client {

    private SocketChannel socketChannel = null;
    private ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);
    private Selector selector;

    private Queue<Object> todo=new LinkedList<>();
    @Override
    public void setErrorHandler(ErrorHandler errorHandler) {

    }

    /**
     * 连接到服务器
     *
     * @param ip   服务器地址
     * @param port 服务器端口
     * @throws IOException 连接异常
     */
    @Override
    public void connect(String ip, int port) throws IOException {
        socketChannel = SocketChannel.open();
        InetAddress ia = InetAddress.getLocalHost();
        InetSocketAddress isa = new InetSocketAddress(ia, 8000);
        socketChannel.connect(isa);
        socketChannel.configureBlocking(false);
        selector = Selector.open();
        log.info("连接到{}：{}成功", ip, port);
        new Thread(this::listen).start();
    }

    /**
     * 发送登录消息
     *
     * @param username 登录的用户名
     * @throws IOException 连接异常
     */
    @Override
    public void sendLogin(String username) throws IOException {
        todo.add(new Login(username));
        //startListenWrite(n);
    }


    /**
     * 发送普通消息
     *
     * @param message 消息对象
     * @throws IOException 连接异常
     */
    @Override
    public void sendMessage(Message message) throws IOException {

    }

    @Override
    public void setHandle(MessageHandler handle) {

    }

    private void startListenWrite(SelectionKey selectionKey) throws ClosedChannelException {
        selectionKey.interestOps(SelectionKey.OP_WRITE);
        selector.wakeup();
    }
    private void stopListenWrite(SelectionKey selectionKey){
        selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(1);
        integers.add(1);
        integers.add(1);
        integers.add(1);
        integers.add(1);

        integers.forEach(System.out::println);

        integers.stream().filter((e)-> e>2).count();
        integers.stream().map(Long::new);


    }


    /**
     * 开始监听
     */
    @Override
    public void listen() {
        log.info("监听线程已开启...");
        try {
            SelectionKey register = socketChannel.register(selector,
                    SelectionKey.OP_READ |
                            SelectionKey.OP_WRITE);
        } catch (ClosedChannelException e) {
            log.error("通道已被关闭？");
            e.printStackTrace();
            return; //无法处理，只能退出程序了
        }

        try {
            while (selector.select() > 0) {
                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = readyKeys.iterator();
                while (it.hasNext()) {
                    SelectionKey key;
                    key = it.next();
                    it.remove();
                    if (key.isReadable()) {
                        receive(key);
                    }
                    if (key.isWritable()) {
                        send(key);
                    }
                }
            }
        } catch (IOException e) {
            log.error("处理数据失败");
            e.printStackTrace();
            return ;
        }
    }

    /**
     * 可发送数据
     *
     * @param key
     */
    private void send(SelectionKey key) {
        log.debug("可发送数据");
    }


    /**
     * 收到数据
     *
     * @param key
     */
    private void receive(SelectionKey key) {
        log.debug("收到数据了");
    }

    /**
     * 正常关闭
     *
     * @param username
     */
    @Override
    public void close(String username) {

    }
}
