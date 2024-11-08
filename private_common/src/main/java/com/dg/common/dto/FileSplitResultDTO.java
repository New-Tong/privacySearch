package com.dg.common.dto;

import lombok.Data;

/**
 * @version v1.0.0
 * @belongsProject: privateSearch
 * @belongsPackage: com.dg.common.dto
 * @author: XBin
 * @description:
 * @createTime: 2024-04-12 15:32
 */
@Data
public class FileSplitResultDTO {

    /**
     * 拆分结果
     */
    private Boolean status;

    /**
     *  拆分数量
     */
    private Integer chunkNum;

    /**
     * 反馈信息
     */
    private String message;
}
