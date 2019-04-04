package cn.czfshine.network.project.cs.dto;

import lombok.Data;

import java.io.Serializable;

/**登出消息
 * @author:czfshine
 * @date:2019/4/4 13:57
 */

@Data
public class Logout implements Serializable {
    private String username;
}
