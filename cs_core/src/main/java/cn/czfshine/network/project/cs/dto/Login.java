package cn.czfshine.network.project.cs.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录消息
 * @author:czfshine
 * @date:2019/4/4 12:29
 */

@Data
public class Login implements Serializable {
    private static final long serialVersionUID = 6975590646707755956L;
    private String username;

    public Login(String username) {
        this.username = username;
    }
}
