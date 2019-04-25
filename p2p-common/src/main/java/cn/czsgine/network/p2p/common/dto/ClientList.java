package cn.czsgine.network.p2p.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 拥有某个文件的客户端列表
 * @author:czfshine
 * @date:2019/4/17 11:04
 */

@Data
public class ClientList implements Serializable {
    private static final long serialVersionUID = 5901590546525025772L;
    private List<ClientInfo> clientLists=new ArrayList<>();
    private boolean empty=true;
    public void add(ClientInfo clientInfo){
        clientLists.add(clientInfo);
    }

    private FileInfo fileInfo;
}
