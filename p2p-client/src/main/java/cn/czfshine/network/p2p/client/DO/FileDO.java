package cn.czfshine.network.p2p.client.DO;

import lombok.Data;

/**
 * @author:czfshine
 * @date:2019/4/18 12:33
 */
@Data
public class FileDO {
    private String filename;
    private long size;

    public FileDO(String filename, long size) {

        this.filename = filename;
        this.size = size;
    }
}
