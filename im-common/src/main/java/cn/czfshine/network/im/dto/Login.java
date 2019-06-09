package cn.czfshine.network.im.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:czfshine
 * @date:2019/6/7 20:53
 */

@Data
public class Login extends Message implements Serializable {
    private int tcpPort;
    private String address;
}
