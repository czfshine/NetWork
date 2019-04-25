package cn.czsgine.network.p2p.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 块请求
 * @author:czfshine
 * @date:2019/4/17 14:27
 */
@Data
public class BlockRequest implements Serializable {
    private static final long serialVersionUID = 4839196670665718105L;
    private String filename;
    private long blocksize;
    private int blockseq;

    public BlockRequest(String filename, long blocksize, int blockseq) {
        this.filename = filename;
        this.blocksize = blocksize;
        this.blockseq = blockseq;
    }
}
