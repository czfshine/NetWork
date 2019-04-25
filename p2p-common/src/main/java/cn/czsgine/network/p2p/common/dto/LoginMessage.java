package cn.czsgine.network.p2p.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录消息.
 * @author:czfshine
 * @date:2019/4/17 10:53
 */
@Data
public class LoginMessage implements Serializable {
    private static final long serialVersionUID = -9117601100702860995L;
    private String ip;
    private int listenedTcpPort;
    private int listenedUdpPort;

    private String hostname;
    public LoginMessage(String ip, int listenedTcpPort, int listenedUdpPort) {
        this.ip = ip;
        this.listenedTcpPort = listenedTcpPort;
        this.listenedUdpPort = listenedUdpPort;
    }
}
