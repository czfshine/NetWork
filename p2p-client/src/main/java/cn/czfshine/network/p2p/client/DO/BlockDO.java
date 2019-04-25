package cn.czfshine.network.p2p.client.DO;

import lombok.Data;

/**
 * @author:czfshine
 * @date:2019/4/18 12:44
 */
@Data
public class BlockDO {
    private String filename;
    private String seq;
    private String source;
    private String state;
    private String size;

    public BlockDO() {
    }

    public BlockDO(String filename, String seq, String source, String state, String size) {
        this.filename = filename;
        this.seq = seq;
        this.source = source;
        this.state = state;
        this.size = size;
    }
}
