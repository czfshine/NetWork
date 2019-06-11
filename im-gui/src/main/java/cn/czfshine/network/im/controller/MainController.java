package cn.czfshine.network.im.controller;
import cn.czfshine.network.im.ChatClient;
import cn.czfshine.network.im.Sender;
import cn.czfshine.network.im.chat.ChatServer;
import cn.czfshine.network.im.client.ImClient;
import cn.czfshine.network.im.dto.*;
import cn.czfshine.network.im.service.*;
import com.jfoenix.controls.*;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static cn.czfshine.network.im.MessageUtils.format;

/**
 * todo 请求拼接独立出来
 * todo 两个内部类优雅的独立出来
 * @author:czfshine
 * @date:2019/6/9 1:04
 */
@Slf4j
@ViewController(value = "/fxml/main.fxml")
public class MainController {

    /**
     * 初始化函数，登录界面的登录按钮被点击后会跳转到这个页面，可以取得对应的信息
     */
    @PostConstruct
    private void init(){
        log.info("init main ui");
        //从登录界面取数据
        LoginController login = (LoginController) flowContext.getRegisteredObject("login");
        serverAddress = login.getAddressText().getText();
        serverPort= login.getPortText().getText();
        selfUsername= login.getUsernameText().getText();
        log.info("服务器地址{}:{},用户名{}",serverAddress,serverPort,selfUsername);

        userListView.getSelectionModel().selectedItemProperty().addListener((v,o,n)->{
            whenListClicked(n);
        });


        chatServer = new ChatServer() {
            @Override
            protected void whenOtherUserConnect(ChatConnect chatConnect) {
                log.info("用户{}连接进入",chatConnect.getUsername());
                allSender.put(chatConnect.getUsername(),chatServer);
            }
            @Override
            protected void whenReceiveMessage(Message message) {
                Platform.runLater(()->whenGetNewMessage(message));
            }
        };
        chatServer.start();//todo !!

        chatServerPort=chatServer.getPort();
        ImClient imClient = new ImClient();
        try {
            imClient.Connect(serverAddress,Integer.valueOf(serverPort),selfUsername,"127.0.0.1", String.valueOf(chatServerPort));
            log.info("连接服务器成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
        imClient.whenOtherUserLogin(this::whenGetNewUserLogin);

        allUserList = FXCollections.observableArrayList();

        fileSendService.whenSendDone((s)-> whenFileSendDone());
        fileReceiveService.whenReceiveDone((s)->whenReceiveDone());

        Stage stage = (Stage)flowContext.getRegisteredObject("Stage");
        stage.setWidth(970);
        stage.setHeight(740);
        stage.setResizable(false);
    }

    //*******下面是一些事件处理函数************//

    /**发送按钮被点击
     * @param event
     */
    @FXML
    private void onSend(ActionEvent event) {
        String msg = inputText.getText();
        inputText.clear();
        log.info("send to {}:{}",curUsername,msg);
        TextMessage textMessage = createTextMessage(msg);
        send(textMessage);
        addToMessagePool(textMessage);
    }
    @FXML
    private void onFile(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/FileDialog.fxml"));
        fdc = new FileDialogController();
        fxmlLoader.setController(fdc);
        Stage stage = ShowDialog(fxmlLoader);
        fdc.setStage(stage);
        fdc.setMainController(this);
    }
    /**当新用户登录
     * @param login
     */
    private void whenGetNewUserLogin(Login login){
        addUserToListView(login.getUsername());
        allUserInfo.put(login.getUsername(),login);
    }
    /**总的路由器，取得新的消息
     * @param message
     */
    private void whenGetNewMessage(Message message){
        log.info("recv message{}",message);
        if(message instanceof TextMessage){
            addToMessagePool(message);
        }else if(message instanceof FileSend){
            whenGetFileSendRequest((FileSend)message);
        }else if(message instanceof FileRecv){
            whenGetFileRecv((FileRecv)message);
        }else if(message instanceof FileBlock){
            whenGetFileBlock((FileBlock)message);
        }else if(message instanceof FileAck){
            whenGetFileAck((FileAck)message);
        }
    }
    /**当左边的列表被点击
     * @param newUsername
     */
    private void whenListClicked(String newUsername){
        otherUserNameLabel.setText(newUsername);
        if (!newUsername.equals(curUsername)) {
            curUsername=newUsername;
            chatMessageText.clear();
            
            Sender sender = allSender.getOrDefault(newUsername, null);
            if(sender==null){
                newConnect(newUsername);
            }else{
                ShowAllMessage(newUsername);
            }
            
        }
    }

    /**连接到新用户
     * @param newUsername 待连接新用户名
     */
    private void newConnect(String newUsername) {
        ChatClient chatClient = new ChatClient() {
            @Override
            protected void whenReceiveMessage(Message message) {
              Platform.runLater(()-> whenGetNewMessage(message));
            }
        };
        Login login = allUserInfo.get(newUsername);
        try {
            chatClient.Connect(login.getAddress(),login.getTcpPort());
            ChatConnect chatConnect = createChatConnect();
            chatClient.send(chatConnect);
            log.info("连接用户{}成功",newUsername);
        } catch (IOException e) {
            e.printStackTrace();
        }
        allSender.put(newUsername,chatClient);
    }
    /**
     * 文件选择按钮被点击，显示文件选择框
     */
    public void whenFileSelect() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择待发送的文件");
        fileChooser.setInitialDirectory(new File("."));
        File file = fileChooser.showOpenDialog(fdc.getStage());
        selectFile=file;
        filePath = file.getPath();
        fdc.getPathText().setText(filePath);

    }
    /**
     * 文件发送按钮被点击，开始进入发送文件流程
     */
    public void whenFileWillSend() {

        //拼接文件发送请求，等待回应
        File file = new File(filePath);
        FileSend fileSend = createFileSend(file);
        send(fileSend);
        whenWaitingAccept();
        //接下来就等待对方发送FileRecv对象回应
    }
    /**当收到FileSend请求，弹出接收文件对话框
     * @param message
     */
    private void whenGetFileSendRequest(FileSend message) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/recvFile.fxml"));
        rfc=new ReceiveFileController();
        fxmlLoader.setController(rfc);
        Stage stage = ShowDialog(fxmlLoader);
        rfc.setStage(stage);
        stage.setAlwaysOnTop(true);
        stage.setAlwaysOnTop(false);
        rfc.setFileSend(message);
        rfc.setMainController(this);
    }
    /**收到响应
     * @param message
     */
    private void whenGetFileAck(FileAck message) {
        //简单校验
        if(fileSendService.recvAck(message)){
            send(fileSendService.sendBlock(selectFile.getName()));
            updateFileSendBar();
        }else{
            log.error("序列号不对{}",message);
        }
    }
    /**收到文件块*/
    private void whenGetFileBlock(FileBlock message) {
        send(fileReceiveService.recv(message));
        updateFileReceiveBar();
    }
    /**收到文件同意的请求*/
    private void whenGetFileRecv(FileRecv message) {
        if(message.isAccept()){
            fileSendService.prepareFile(selectFile);
            send(fileSendService.sendBlock(selectFile.getName()));
        }else{
            //todo 不接受
        }
    }
    /**拒绝接收文件*/
    public void whenFileRefuse(FileSend fileSend) {
    }
    /**
     * 同意接受文件，开始文件块传输
     * @param fileSend
     */
    public void whenFileAccept(FileSend fileSend) {
        String filename = fileSend.getFilename();
        //拼接请求
        FileRecv fileRecv = createFileRecv(filename);

        //发送同意
        send(fileRecv);
        //准备好接受的上下文环境
        whenStartSend(fileSend, filename);
    }
    /**
     * 文件取消按钮被点击
     */
    public void whenFileClean() {
        fdc.getStage().close();
    }

    //UI辅助函数
    private void addUserToListView(String username) {
        allUserList.add(username);
        userListView.setItems(allUserList);
    }
    private void showMessageToText(Message message) {
        System.out.println(1);
        chatMessageText.appendText(format(message));
    }
    private void whenReceiveDone() {
        rfc.getInfoLabel().setText("接收成功");
        rfc.getAcceptBtn().setDisable(false);
        rfc.getRefuseBtn().setDisable(true);
        rfc.getAcceptBtn().setText("完成");
    }
    private void whenFileSendDone() {
        fdc.getInfoLabel().setText("发送成功！");
        fdc.getCleanBtn().setText("完成");
    }
    private void whenStartSend(FileSend fileSend, String filename) {
        fileReceiveService.prepareRecevie(fileSend,selfUsername);
        rfc.getAcceptBtn().setDisable(true);
        rfc.getRefuseBtn().setText("取消接收");
    }
    /**
     * 更新发送框的进度条
     */
    private void updateFileSendBar(){
        fdc.getBar().setSecondaryProgress(fileSendService.getPercent(selectFile.getName()));
        fdc.getInfoLabel().setText(String.format("%.2f%%",100.0*fileSendService.getPercent(selectFile.getName())));
    }
    /**
     * 更新接受框的进度条
     */
    private void updateFileReceiveBar(){
        rfc.getBar().setSecondaryProgress(fileReceiveService.getPercent(""));
        rfc.getInfoLabel().setText(String.format("%.2f%%",100.0*fileReceiveService.getPercent("")));
    }
    private void whenWaitingAccept() {
        fdc.getInfoLabel().setText("等待对方同意接收...");
        fdc.getOkBtn().setDisable(true);
        fdc.getCleanBtn().setText("取消发送");
    }
    private void ShowAllMessage(String newUsername) {
        List<Message> messages = allMessage.getOrDefault(newUsername, null);
        if(messages!=null){
            messages.forEach(this::showMessageToText);
        }
    }

    //逻辑处理函数
    private void addToMessagePool(Message message) {
        if(message.getUsername().equals(selfUsername)){
            putMessage(curUsername,message);
            showMessageToText(message);
        }else {
            putMessage(message.getUsername(),message);
            if(message.getUsername().equals(curUsername)){
                showMessageToText(message);
            }
        }
    }
    private void putMessage(String username, Message message) {
        List<Message> list = allMessage.getOrDefault(username, null);
        if(list == null){
            list = new LinkedList<>();
            allMessage.put(username,list);
        }
        list.add(message);
    }

    private void send(Message message){
        Sender sender = allSender.get(curUsername);
        sender.sendTo(curUsername,message);
    }
    /**显示对话框*/
    private Stage ShowDialog(FXMLLoader fxmlLoader) {
        try {
            Object load = fxmlLoader.load();
            Stage stage = new Stage();
            Scene scene = new Scene((StackPane) load);
            stage.setScene(scene);
            stage.show();
            return stage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //域
    @FXMLViewFlowContext  private ViewFlowContext flowContext;
    @FXML private JFXButton sendBtn;
    @FXML private Label otherUserNameLabel;
    @FXML private JFXTextArea inputText;
    @FXML private JFXTextArea chatMessageText;
    @FXML private JFXListView<String> userListView;
    @FXML private JFXTextField logText;


    //数据域
    private String curUsername;
    private String serverAddress="";
    private String serverPort="";
    private String selfUsername="";
    private Integer chatServerPort;

    private HashMap<String,Sender> allSender=new HashMap<>();
    private HashMap<String,Login> allUserInfo=new HashMap<>();
    private HashMap<String, List<Message>> allMessage=new HashMap<>();
    private ChatServer chatServer;

    /*ui相关*/
    private ReceiveFileController rfc;
    private FileDialogController fdc;
    private ObservableList<String> allUserList;
    /*文件相关*/
    private String filePath="";
    private File selectFile;


    /*service*/
    private FileSendService fileSendService=FileSendService.getInstance();
    private FileReceiveService fileReceiveService=FileReceiveService.getInstance();

    //DTO
    private FileSend createFileSend(File file) {
        FileSend fileSend = new FileSend();
        fileSend.setFilename(file.getName());
        fileSend.setFileLength(file.length());
        fileSend.setSeq(0);
        fileSend.setTime(new Date());
        fileSend.setUsername(selfUsername);
        return fileSend;
    }
    private FileRecv createFileRecv(String filename) {
        FileRecv fileRecv = new FileRecv();
        fileRecv.setFilename(filename);
        fileRecv.setAccept(true); //同意接受
        fileRecv.setSeq(0);
        fileRecv.setTime(new Date());
        fileRecv.setUsername(selfUsername);
        return fileRecv;
    }

    private TextMessage createTextMessage(String msg) {
        TextMessage textMessage = new TextMessage();
        textMessage.setContent(msg);
        textMessage.setSeq(0);
        textMessage.setTime(new Date());
        textMessage.setUsername(selfUsername);
        return textMessage;
    }
    private ChatConnect createChatConnect() {
        ChatConnect chatConnect = new ChatConnect();
        chatConnect.setSeq(0);
        chatConnect.setTime(new Date());
        chatConnect.setUsername(selfUsername);
        return chatConnect;
    }

}
