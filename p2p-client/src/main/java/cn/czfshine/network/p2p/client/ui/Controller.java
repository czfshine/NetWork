package cn.czfshine.network.p2p.client.ui;

import cn.czfshine.network.p2p.client.DO.BlockDO;
import cn.czfshine.network.p2p.client.DO.FileDO;
import cn.czfshine.network.p2p.client.P2PClient;
import cn.czfshine.network.p2p.client.file.DirInfo;
import cn.czfshine.network.p2p.client.file.Downloader;
import cn.czfshine.network.p2p.client.file.FileClient;
import cn.czfshine.network.p2p.client.file.FileServer;
import cn.czsgine.network.p2p.common.Config;
import cn.czsgine.network.p2p.common.dto.BlockResponse;
import cn.czsgine.network.p2p.common.dto.ClientList;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

/**
 * @author:czfshine
 * @date:2019/4/17 14:54
 */
@ViewController(value = "/p2p.fxml")
public class Controller {

    @FXMLViewFlowContext
    private ViewFlowContext context;


    @FXML
    private TextField serverPortText;

    @FXML
    private TextField udpText;

    @FXML
    private TableView<BlockDO> blockTable;

    @FXML
    private TableColumn<BlockDO, String> blockseqcol;

    @FXML
    private TableColumn<FileDO, String> filenamecol;

    @FXML
    private Button dirBtn;

    @FXML
    private TextArea logText;

    @FXML
    private TextField dirText;

    @FXML
    private Button connectBtn;

    @FXML
    private TextField filenameText;

    @FXML
    private TableColumn<BlockDO, String> clientcol;

    @FXML
    private TableView<FileDO> fileTable;

    @FXML
    private TableColumn<BlockDO, String> blockfilenamecol;

    @FXML
    private TextField serverText;

    @FXML
    private TextField tcpText;

    @FXML
    private TableColumn<BlockDO, String> blocksizecol;

    @FXML
    private Button downloadBtn;

    @FXML
    private TableColumn<BlockDO, String> statecol;

    @FXML
    private TableColumn<FileDO, String> sizecol;

    /**
     * 日志缓存
     */
    private StringBuilder logcache =new StringBuilder();
    public void log(String msg){
        logcache.append(msg+"\n");
        logText.setText(logcache.toString());
        //logText.setScrollLeft(Double.MAX_VALUE);
        logText.setScrollTop(Double.MAX_VALUE);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showfles(String dir){
        //默认目录
        DirInfo.dirname=dir;
        dirText.setText( DirInfo.dirname);

        //文件列表：
        File[] files = DirInfo.getFiles();
        ObservableList<FileDO> data = FXCollections.observableArrayList();
        for (File f: files
        ) {
            data.add(new FileDO(f.getName(),f.length()));
        }
        fileTable.setItems(data);
        filenamecol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFilename()));
        sizecol.setCellValueFactory(d->new SimpleStringProperty(String.valueOf(d.getValue().getSize())));
    }
    private ObservableList<BlockDO> blockdata = FXCollections.observableArrayList();
    private void initBlockTable(BlockDO blockDO){
        ObservableList<BlockDO> blockdata2 = FXCollections.observableArrayList();
        blockdata2.addAll(blockdata);
        if(blockDO ==null){
            blockdata2.add(new BlockDO("","","","",""));
        }else{
            blockdata2.add(blockDO);
        }
        blockdata=blockdata2;
        blockTable.setItems(blockdata2);
        blockseqcol.setCellValueFactory((c)->new SimpleStringProperty(c.getValue().getSeq()));
        clientcol.setCellValueFactory((c)->new SimpleStringProperty(c.getValue().getSource()));
        statecol.setCellValueFactory((c)->new SimpleStringProperty(c.getValue().getState()));
        blockfilenamecol.setCellValueFactory((c)->new SimpleStringProperty(c.getValue().getFilename()));
        blocksizecol.setCellValueFactory((c)->new SimpleStringProperty(c.getValue().getSize()));
    }

    /**
     * 初始化函数，在载入界面前运行
     */
    @PostConstruct
    public void init() {

        //1. 设置显示的内容
        showfles("./A/");
        //initBlockTable();
        //2.启动文件服务

        fileServer = new FileServer();
        fileServer.setLogger(this::log);
        fileServer.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        tcpText.setText(String.valueOf(FileServer.getPort()));
        udpText.setText("弃用");

        //3.服务器相关信息
        serverText.setText("127.0.0.1");
        serverPortText.setText(String.valueOf(Config.SERVERPORT));

    }
    private FileServer fileServer;
    private P2PClient p2PClient ;
    @FXML
    void onConnect(ActionEvent event) {
        p2PClient= new P2PClient(dirText.getText(), dirText.getText());
        p2PClient.setLogger(this::log);
        p2PClient.setRecvClientList(this::recvClientList);
        try {
            p2PClient.connect(serverText.getText(),Integer.valueOf(serverPortText.getText()));
            //log("链接p2p服务器成功");
        } catch (IOException e) {
            log("链接p2p服务器失败");
            e.printStackTrace();
        }
        p2PClient.hello();
        connectBtn.setText("断开连接");
    }

    private void recvblock(FileClient fileClient, BlockResponse blockResponse){
        BlockDO ok = new BlockDO(blockResponse.getFilename(),
                String.valueOf(blockResponse.getBlockseq()),
                fileClient.toString(),
                "OK",
                String.valueOf(blockseqcol));
        //blockdata.add(ok);
        initBlockTable(ok);
    }

    private void downloadOK(){
        log("!!!下载完成");
        showfles(DirInfo.dirname);
    }
    private ClientList clientList;
    private void recvClientList(ClientList clientList ){
        this.clientList=clientList;
        Downloader downloader = new Downloader(filenameText.getText(), clientList);
        downloader.setRecvBlock(this::recvblock);
        downloader.setLogger(this::log);
        downloader.setOk(this::downloadOK);
        try {
            downloader.start();
            //-> recvBlock
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
    @FXML
    void onDowload(ActionEvent event) throws IOException {
        String filename = filenameText.getText();
        p2PClient.sendFileRequest(filename);
        // -> recvClientList 收到消息
    }

    @FXML
    void onDir(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();

        directoryChooser.setTitle("Open Resource folder");
        directoryChooser.setInitialDirectory(new File("."));
        final Stage stage = (Stage) context.getRegisteredObject("Stage");
        File file = directoryChooser.showDialog(stage);
        if(file!=null){
            //dirText.setText(file.getPath());
            showfles(file.getPath()+"\\");
        }
    }

}
