package cn.czfshine.network.im.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:czfshine
 * @date:2019/6/9 10:34
 */
@Data
public class TextMessage extends Message implements Serializable {
    private static final long serialVersionUID = 9209979720260149709L;
    private String content;
}
