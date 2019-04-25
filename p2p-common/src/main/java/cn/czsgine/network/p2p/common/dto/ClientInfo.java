package cn.czsgine.network.p2p.common.dto;

import lombok.Data;

import java.io.Serializable;

/**客户端信息
 * @author:czfshine
 * @date:2019/4/17 11:04
 */

@Data
public class ClientInfo implements Serializable {
    private static final long serialVersionUID = 4496340536189234945L;
    private LoginMessage logininfo;
}
