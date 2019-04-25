package cn.czfshine.network.p2p.client.fun;

import cn.czfshine.network.p2p.client.file.FileClient;
import cn.czsgine.network.p2p.common.dto.BlockResponse;

import java.io.IOException;

/**
 * @author:czfshine
 * @date:2019/4/18 10:29
 */
@FunctionalInterface
public interface RecvBlock{
    void recv(FileClient fileClient, BlockResponse blockResponse) throws IOException;
}