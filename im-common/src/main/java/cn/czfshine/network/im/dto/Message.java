package cn.czfshine.network.im.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**消息对象
 * @author:czfshine
 * @date:2019/6/7 20:38
 */

@Data
public class Message implements Serializable {

    private String username;
    private Date time;
    private int seq;

}
