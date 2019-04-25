package cn.czsgine.network.p2p.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件信息
 * @author:czfshine
 * @date:2019/4/17 10:56
 */

@Data
public class FileInfo implements Serializable {
    private static final long serialVersionUID = 8337553043149702916L;
    private String filename;
    private long size;
    private String hash;//todo
}
