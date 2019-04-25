package cn.czsgine.network.p2p.common.dto;

import lombok.Data;

import java.io.Serializable;

/**块数据
 * @author:czfshine
 * @date:2019/4/17 14:29
 */

@Data
public class BlockResponse implements Serializable {

    private static final long serialVersionUID = 6502084089361527000L;
    private String filename;
    private String hash;
    private byte [] data;
    private long blocksize;
    private int blockseq;

    @Override
    public String toString() {
        return "BlockResponse{" +
                "filename='" + filename + '\'' +
                ", hash='" + hash + '\'' +
                ", blocksize=" + blocksize +
                ", blockseq=" + blockseq +
                ", datalen=" +data.length+
                '}';
    }
}
