package cn.czfshine.network.im.service;

import cn.czfshine.network.im.dto.FileAck;
import cn.czfshine.network.im.dto.FileBlock;

import java.io.*;
import java.util.Date;
import java.util.function.Consumer;

import static cn.czfshine.network.im.ImConfig.blockSize;

/**文件发送服务
 * @author:czfshine
 * @date:2019/6/10 23:09
 */
public class FileSendService {

    private int readedBytes;
    public FileBlock sendBlock(String filename){
        byte[] bytes = new byte[blockSize];
        try {
            int read = bis.read(bytes, 0, blockSize);
            readedBytes +=read;
            FileBlock fileBlock = createFileBlock(bytes, read);
            if(read!=blockSize){
                doneListener.accept(filename);
            }
            if(read>0){
                seq++;
                return fileBlock;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean recvAck(FileAck fileAck){
        return true;
    }

    private BufferedInputStream bis;
    private int seq;
    private File file;
    private long length;
    /**
     * 准备发送
     */
    public void prepareFile(File file){
        try {
            bis=new BufferedInputStream(new FileInputStream(file));
            seq=0;
            this.file=file;
            readedBytes=0;
            length=file.length();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Consumer<String> doneListener;

    /**文件发送完成的回调函数
     * @param doneListener
     */
    public void whenSendDone(Consumer<String> doneListener){
        this.doneListener=doneListener;
    }

    /**
     * 取得当前的发送进度
     */
    public double getPercent(String filename){
        return 1.0 * readedBytes/length;
    }


    private static FileSendService ourInstance = new FileSendService();
    public static FileSendService getInstance() {
        return ourInstance;
    }
    private FileSendService() {
    }
    private FileBlock createFileBlock(byte[] bytes, int read) {
        FileBlock fileBlock = new FileBlock();
        fileBlock.setBlockLength(read);
        fileBlock.setData(bytes);
        fileBlock.setFilename(file.getName());
        fileBlock.setSeq(seq);
        fileBlock.setTime(new Date());
        return fileBlock;
    }

}
