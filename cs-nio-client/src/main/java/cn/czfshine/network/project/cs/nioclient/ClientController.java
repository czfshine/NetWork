package cn.czfshine.network.project.cs.nioclient;
import cn.czfshine.network.project.cs.config.Config;
import cn.czfshine.network.project.cs.dto.Message;
import io.datafx.controller.ViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Random;

/**
 * 界面的控制器
 */
@ViewController(value = "/cs/sample.fxml")
public class ClientController {

    /**
     * 输出文本框
     */
    @FXML
    private TextArea output;

    /**
     * 断开连接按钮
     */
    @FXML
    private Button disconnectButton;

    /**
     * 端口文本框
     */
    @FXML
    private TextField serverport;

    /**
     * 消息文本框
     */
    @FXML
    private TextField messageTextBox;

    /**
     * 服务器ip文本框
     */
    @FXML
    private TextField severip;

    /**
     * 用户名
     */
    @FXML
    private TextField username;

    /**
     * 日志文本框
     */
    @FXML
    private TextArea logText;

    /**
     * 连接按钮
     */
    @FXML
    private Button connectButton;

    /**
     * 发送按钮
     */
    @FXML
    private Button sendButton;

    /**
     * 初始化函数，在载入界面前运行
     */
    @PostConstruct
    public void init() {

        serverport.setText(String.valueOf(Config.DEFAULTSERVERPORT));

        //随机用户名
        username.setText("czfshine"+(new Random().nextInt(1000)));
        disconnectButton.setDisable(true);
        sendButton.setDisable(true);
    }

    /**
     * 日志缓存
     */
    private StringBuilder logcache =new StringBuilder();
    public void log(String msg){
        logcache.append(msg+"\n");
        logText.setText(logcache.toString());
        logText.setScrollLeft(Double.MAX_VALUE);
        logText.setScrollTop(Double.MAX_VALUE);
    }

    private Client client;

    @FXML
    void Onconnect(ActionEvent event) {
        String IP = severip.getText();
        String text = serverport.getText();
        int port=Integer.valueOf(text);

        log("开始连接服务器"+IP+":"+port);
        client = new Client();
        try {
            client.connect(IP,port);
            log("连接成功");
            String usernameText = username.getText();
            client.sendLogin(usernameText);
            connectButton.setDisable(true);
            disconnectButton.setDisable(false);
            sendButton.setDisable(false);
        } catch (IOException e) {
            log("连接失败");
            log(e.getMessage());
            e.printStackTrace();
        }

        Client.MessageHandler messageHandler = message -> {
            log("收到消息"+message.toString());
            receiveMessage(message);
            return false;
        };
        client.setHandle(messageHandler);

        Client.ErrorHandler errorHandler = () -> {
            log("服务器已自动关闭");
            connectButton.setDisable(false);
            disconnectButton.setDisable(true);
            sendButton.setDisable(true);
        };
        client.setErrorHandler(errorHandler);
        System.out.println(1);
    }

    private void sendlogout(){
        client.close(username.getText());
    }
    @FXML
    void OnDisconnect(ActionEvent event) {
        log("准备向服务器发送离线消息");
        Thread thread = new Thread(this::sendlogout);
        thread.setDaemon(true);
        thread.start();
        connectButton.setDisable(false);
        disconnectButton.setDisable(true);
        sendButton.setDisable(true);
    }
    
    private void receiveMessage(Message message){
        String text = output.getText();
        text+=(message.getWho()+":"+message.getContent()+"\n");
        output.setText(text);
        output.setScrollTop(Double.MAX_VALUE);
        output.setScrollLeft(Double.MAX_VALUE);
    }
    private Message message;
    private void sender(){
        try {
            client.sendMessage(message);
        } catch (IOException e) {
            log("向服务器发送消息失败");
            e.printStackTrace();
            return ;
        }
        log("向服务器发送消息成功");
    }
    @FXML
    void OnSend(ActionEvent event) {
        message = new Message();
        String usernameText = username.getText();
        String text = messageTextBox.getText();
        message.setContent(text);
        message.setWho(usernameText);
        log("准备向服务器发送消息");

        Thread thread = new Thread(this::sender);
        thread.setDaemon(true);
        thread.start();
    }

}
