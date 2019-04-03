package cn.czfshine.network.project.cs.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:czfshine
 * @date:2019/4/3 22:03
 */

@Data
public class Message implements Serializable {
    private String who;
    private String content;
}
