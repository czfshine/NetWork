package cn.czfshine.network.p2p.client.file;

import java.io.File;

/**
 * @author:czfshine
 * @date:2019/4/17 16:36
 */

public class DirInfo {
    public static String dirname;
    public static File[] getFiles(){
        File file = new File(DirInfo.dirname);
        if(file.isDirectory()){
            File[] files = file.listFiles(File::isFile);
            return files;
        }
        return new File[0];
    }
}
