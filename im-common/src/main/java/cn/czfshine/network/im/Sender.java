package cn.czfshine.network.im;

import cn.czfshine.network.im.dto.Message;

/**
 * @author:czfshine
 * @date:2019/6/9 10:43
 */

public interface Sender {
    void sendTo(String username, Message message);
}
