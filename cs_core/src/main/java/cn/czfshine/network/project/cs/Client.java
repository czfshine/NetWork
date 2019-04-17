package cn.czfshine.network.project.cs;

import cn.czfshine.network.project.cs.dto.Message;

import java.io.IOException;

/**
 * @author:czfshine
 * @date:2019/4/8 16:57
 */

public interface Client {

    /**
     * 收到消息的句柄
     */
    @FunctionalInterface
    interface  MessageHandler {
        boolean receive(Message message);
    }

    /**
     * 发生错误时的句柄
     */
    @FunctionalInterface
    interface ErrorHandler{
        void handle();
    }

    void setErrorHandler(ErrorHandler errorHandler);

    /**连接到服务器
     * @param ip 服务器地址
     * @param port 服务器端口
     * @throws IOException 连接异常
     */
    void connect(String ip, int port) throws IOException;

    /**发送登录消息
     * @param username 登录的用户名
     * @throws IOException 连接异常
     */
    void sendLogin(String username) throws IOException;

    /**发送普通消息
     * @param message 消息对象
     * @throws IOException 连接异常
     */
    void sendMessage(Message message) throws IOException;

    void setHandle(MessageHandler handle);

    /**
     * 开始监听
     */
    void listen();

    /**正常关闭
     * @param username
     */
    void close(String username);
}
