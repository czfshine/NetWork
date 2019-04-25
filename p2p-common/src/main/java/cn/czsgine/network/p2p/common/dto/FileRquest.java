package cn.czsgine.network.p2p.common.dto;

import lombok.Data;

import java.io.Serializable;

/**文件请求
 * @author:czfshine
 * @date:2019/4/17 11:02
 */

@Data
public class FileRquest implements Serializable {

    private static final long serialVersionUID = -3245114942806533142L;
    private String filename;

    private String hash;

    public FileRquest(String filename) {
        this.filename = filename;
    }
}
