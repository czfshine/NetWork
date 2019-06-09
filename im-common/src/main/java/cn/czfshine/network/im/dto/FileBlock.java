package cn.czfshine.network.im.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:czfshine
 * @date:2019/6/9 15:02
 */

@Data
public class FileBlock extends  Message implements Serializable {
    private String filename;
    private byte[] data;
    private int blockLength;

    @Override
    public String toString() {
        return "FileBlock{" +
                "filename='" + filename + '\'' +
                ", blockLength=" + blockLength +
                ", username=" + getUsername() +
                ", seq=" + getSeq() +
                '}';
    }
}
