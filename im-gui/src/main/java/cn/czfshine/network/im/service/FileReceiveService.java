package cn.czfshine.network.im.service;

import cn.czfshine.network.im.ImConfig;
import cn.czfshine.network.im.dto.FileAck;
import cn.czfshine.network.im.dto.FileBlock;
import cn.czfshine.network.im.dto.FileSend;

import java.io.*;
import java.util.Date;
import java.util.function.Consumer;

/**文件接收服务
 * @author:czfshine
 * @date:2019/6/10 23:17
 */

public class FileReceiveService {


    public FileAck recv(FileBlock message){
            try {
                //写
                bos.write(message.getData(),0,message.getBlockLength());
                writedBytes+=message.getBlockLength();
                //拼接响应，等待下一个数据块
                FileAck fileAck = createFileAck(message);
                if(writedBytes<fileLength){
                    return fileAck;
                }else{
                    doneListener.accept(message.getFilename());
                    bos.flush();
                    bos.close();
                    bos=null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        return null;
    }

    private BufferedOutputStream bos;
    private int writedBytes;
    private int fileLength;
    private String selfUsername;
    public void prepareRecevie(FileSend fileSend,String selfUsername){
        this.selfUsername=selfUsername;
        String filename = fileSend.getFilename();
        File recvFile = new File(ImConfig.outputDir + filename);
        try {
            bos = new BufferedOutputStream(new FileOutputStream(recvFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writedBytes=0;
        fileLength= (int) fileSend.getFileLength();
    }
    /**
     * 取得当前的接收进度
     */
    public double getPercent(String filename){
        return 1.0 * writedBytes/fileLength;
    }
    private Consumer<String> doneListener;

    public void whenReceiveDone(Consumer<String> l){
        doneListener=l;
    }
    private static FileReceiveService ourInstance = new FileReceiveService();

    public static FileReceiveService getInstance() {
        return ourInstance;
    }

    private FileReceiveService() {
    }
    private FileAck createFileAck(FileBlock message) {
        FileAck fileAck = new FileAck();
        fileAck.setFilename(message.getFilename());
        fileAck.setSeq(message.getSeq());
        fileAck.setTime(new Date());
        fileAck.setUsername(selfUsername);
        return fileAck;
    }
}
