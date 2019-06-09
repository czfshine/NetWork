package cn.czfshine.network.im.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:czfshine
 * @date:2019/6/9 15:01
 */
@Data
public class FileSend extends Message implements Serializable {
    private String filename;
    private long fileLength;
}
