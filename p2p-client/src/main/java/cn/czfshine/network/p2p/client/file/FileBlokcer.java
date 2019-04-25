package cn.czfshine.network.p2p.client.file;

import cn.czsgine.network.p2p.common.dto.BlockResponse;

import java.io.*;

/**
 * @author:czfshine
 * @date:2019/4/17 16:10
 */

public class FileBlokcer {

    private String filepath;

    private File file;
    RandomAccessFile accessFile;
    public FileBlokcer(String filepath) throws FileNotFoundException {
        this.filepath = filepath;
        file=new File(filepath);
        if(!file.exists() || !file.isFile()){
            return;    //todo
        }
        accessFile = new RandomAccessFile(file, "r");
    }

    public byte[] getBlock(int seq, long size) throws IOException {
        if(accessFile.length()<seq*size){
            //todo
            return new byte[0];
        }
        accessFile.seek(seq*size);
        byte[] bytes = new byte[(int)size];
        int len = accessFile.read(bytes);

        byte[] res = new byte[len];
        for (int i = 0; i < len; i++) {
            res[i]=bytes[i];
        }
        return res;
    }
}
