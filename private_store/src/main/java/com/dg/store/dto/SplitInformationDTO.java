package com.dg.store.dto;

import lombok.Data;

/**
 * @version v1.0.0
 * @belongsProject: privateSearch
 * @belongsPackage: com.dg.store.com.dg.common.dto
 * @author: XBin
 * @description: 拆分信息实体类
 * @createTime: 2024-02-26 15:45
 */
@Data
public class SplitInformationDTO {

    /**
     * 分片个数
     */
    private Integer chunkInfoCount;

    /**
     * 文件名字
     */
    private String storeName;

}
