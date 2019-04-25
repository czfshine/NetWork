package cn.czfshine.network.p2p.client.file;

import cn.czfshine.network.p2p.client.fun.Logger;
import cn.czfshine.network.p2p.client.fun.RecvBlock;
import cn.czsgine.network.p2p.common.dto.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.List;

/**
 * @author:czfshine
 * @date:2019/4/17 22:43
 */


public class Downloader {

    private String filename;
    private ClientList clientList;
    private long blocksize=1024*256;
    private long length;
    private int maxclient=5;
    private int[] blockmap;

    private RecvBlock recvBlock;

    public void setRecvBlock(RecvBlock recvBlock) {
        this.recvBlock = recvBlock;
    }

    private void defrecv(FileClient fileClient, BlockResponse blockResponse){};
    private Logger logger;

    public void setLogger(Logger logger){
        this.logger=logger;
    }
    private void deflog(String msg){
        System.out.println(msg);
    };
    public Downloader(String filename, ClientList clientList) {
        this.filename = filename;
        this.clientList=clientList;
        length=clientList.getFileInfo().getSize();
        setLogger(this::deflog);
        setRecvBlock(this::defrecv);
    }
    private List<FileClient> fileClients=new LinkedList<>();
    private void downloadBlock(int seq) throws IOException, InterruptedException {
        while(true){
            if(fileClients.size()>0){
                FileClient fileClient = fileClients.get(0);
                fileClients.remove(0);
                fileClient.sendRequest(new BlockRequest(filename,blocksize,seq));
                logger.log("下载第"+seq+"个块");
                return;
            }
            Thread.sleep(500);
        }
    }
    private void recvBlock(FileClient fileClient, BlockResponse blockResponse) throws IOException {
        fileClients.add(fileClient);
        int blockseq = blockResponse.getBlockseq();
        logger.log("收到第"+blockseq+"个块数据");
        if(blockmap.length>blockseq){
            if(blockmap[blockseq]==0){
                blockmap[blockseq]=1;
                writeToOutputFile(blockResponse);
            }else{
                logger.log("块重复下载？？");
            }
        }else{
            logger.log("块序号超过最大？？");
        }
        recvBlock.recv(fileClient, blockResponse);
    }
    private RandomAccessFile output;
    private synchronized void  writeToOutputFile(BlockResponse blockResponse) throws IOException {
        int blockseq = blockResponse.getBlockseq();
        output.seek(blockseq*blocksize);
        output.write(blockResponse.getData()); //长度是data的长度
    }

    private Runnable ok;

    public void setOk(Runnable ok) {
        this.ok = ok;
    }

    public void start() throws IOException, InterruptedException {
        logger.log("开始下载文件"+filename);
        long l=length/blocksize +(length%blocksize==0?0:1);
        logger.log("块数量："+l);
        List<ClientInfo> clientLists = clientList.getClientLists();
        int count = clientLists.size();
        if(count<maxclient){
            maxclient=count;
        }
        blockmap=new int[(int)l];
        for (int i = 0; i < maxclient; i++) {
            FileClient fileClient = new FileClient();
            ClientInfo clientInfo = clientLists.get(i);
            fileClient.connect(clientInfo.getLogininfo().getIp(),
                    clientInfo.getLogininfo().getListenedTcpPort());
            fileClient.setRecvBlock(this::recvBlock);
            fileClients.add(fileClient);
        }
        File file = new File(DirInfo.dirname + filename);
        output= new RandomAccessFile(
                file, "rw");
        System.out.println("outputfilename:"+file.getPath());
        output.setLength(length);
        for (int i = 0; i < l; i++) {
            downloadBlock(i);
        }
        output.close();
        ok.run();
    }

}
