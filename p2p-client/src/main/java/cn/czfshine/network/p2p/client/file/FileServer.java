package cn.czfshine.network.p2p.client.file;

import cn.czfshine.network.p2p.client.fun.Logger;
import cn.czsgine.network.p2p.common.PortTools;
import cn.czsgine.network.p2p.common.dto.BlockRequest;
import cn.czsgine.network.p2p.common.dto.BlockResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**客户端文件服务
 * @author:czfshine
 * @date:2019/4/17 15:00
 */

@Slf4j
public class FileServer {
    //存放当前所有活动的socket和对应的outputStream
    private HashMap<String,ObjectOutputStream> alloos;

    private HashMap<String, FileBlokcer> allfile=new HashMap<>();

    private static int port;

    public static int getPort() {
        return port;
    }
    private Logger logger;

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * 服务器监听到一个新的链接时启动一个新的线程处理该Socket
     */
    private class Accepter implements Runnable{
        private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Accepter.class);

        private Socket socket;
        private String hostname;
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info("等待客户端消息");
            while(true){
                try {
                    //大部分时间都会阻塞在这一句
                    Object o = ois.readObject();
                    if(o instanceof BlockRequest){
                        BlockRequest blockRequest=(BlockRequest)o;
                        String filename = blockRequest.getFilename();
                        FileBlokcer fb = allfile.getOrDefault(filename, null);
                        if(fb == null){
                            fb=new FileBlokcer(DirInfo.dirname+filename);
                            allfile.put(filename,fb);
                        }
                        byte[] block = fb.getBlock(blockRequest.getBlockseq(), blockRequest.getBlocksize());
                        try {
                            Thread.sleep(1024);//模拟网络延迟
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        BlockResponse res = new BlockResponse();
                        res.setBlockseq(blockRequest.getBlockseq());
                        res.setBlocksize(blockRequest.getBlocksize());
                        res.setFilename(filename);
                        res.setData(block);
                        ObjectOutputStream objectOutputStream=null;
                        String hostAddress = socket.getInetAddress().getHostAddress();
                        for (String s:alloos.keySet()
                             ) {
                            if( s.equals(hostAddress)){
                                objectOutputStream=alloos.get(s);
                            }
                        }
                        if(objectOutputStream!=null){
                            objectOutputStream.writeObject(res);
                            objectOutputStream.flush();
                            log.info("回送到{}:{}的数据{}",socket.getInetAddress(), socket.getPort(),res );
                            if(logger!=null) {
                                logger.log("回送到"+socket.getInetAddress()+":"+socket.getPort()+"的数据"+res);
                            }
                        }
                    }
                } catch (IOException e) {
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
            //监听的循环结束，删掉缓存
            alloos.remove(socket);
        }
    }


    /**
     * 监听线程
     */
    private void listen()  {
        ServerSocket serverSocket;
        serverSocket= PortTools.getServerSocket();
        port=serverSocket.getLocalPort();
        log.info("开始监听端口:{}",serverSocket.getLocalPort());
        if(logger!=null){
            logger.log("文件服务启动");
        }

        try {
            do {
                Socket accept = serverSocket.accept();
                log.info("服务器监听到{}:{}的链接接入",
                        accept.getInetAddress(), accept.getPort());
                if(logger!=null){
                    logger.log("监听到"+accept.getInetAddress()+":"+accept.getPort()+"的链接接入");
                }
                Accepter accepter = new Accepter(accept);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(accept.getOutputStream());
                alloos.put(accept.getInetAddress().getHostAddress(), objectOutputStream);
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
        alloos=new HashMap<>();
        startListen();
    }

    public static void main(String args[]) {
        DirInfo.dirname="./A/";
        new FileServer().start();
    }
}
