package cn.czfshine.network.project.cs.dto;

import lombok.Data;

import java.io.Serializable;

/**消息主体
 * @author:czfshine
 * @date:2019/4/3 22:03
 */

@Data
public class Message implements Serializable {
    private static final long serialVersionUID = -7790222419742486052L;
    private String who;
    private String content;
}
