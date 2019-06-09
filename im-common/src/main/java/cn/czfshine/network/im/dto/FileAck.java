package cn.czfshine.network.im.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:czfshine
 * @date:2019/6/9 15:02
 */
@Data
public class FileAck extends Message implements Serializable {
    private String filename;
}
