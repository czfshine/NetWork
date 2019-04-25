package cn.czsgine.network.p2p.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author:czfshine
 * @date:2019/4/17 10:57
 */

@Data
public class FileList implements Serializable {
    private static final long serialVersionUID = 7759310363368022397L;
    private List<FileInfo> fileInfos=new ArrayList<>();

    public void add(FileInfo fileInfo){
        fileInfos.add(fileInfo);
    }
}
