package com.dg.common.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @version v1.0.0
 * @belongsProject: privateSearch
 * @belongsPackage: com.dg.common.dto
 * @author: XBin
 * @description: 拆分设置信息
 * @createTime: 2024-04-09 11:27
 */
@Data
public class SplitConfigDTO {

    /**
     * 从第几行开始是有效数据
     */
    private Integer DataStartIndexIdentifier;

    /**
     * 文件大小
     */
    @NotNull(message = "文件大小不能为空")
    private Integer fileSize;
    /**
     * 加密算法类型
     */
    @NotNull(message = "加密算法类型")
    private Integer encryptCodeType;

    /**
     * 加密级别
     */
    @NotNull(message = "加密级别不能为空")
    private Integer encryptLevel;

    /**
     * 需要加密的列
     */
    private List<String> encryptionColumns;

    /**
     * 需要加密的列对应的index
     */
    private List<Integer> encryptionColumnsIndex;

    /**
     * 脱敏的列名字
     */
    private List<String> maskColumns;

    /**
     * 脱敏的列对应的index
     */
    private List<Integer> maskColumnsIndex;

    /**
     * 普通的列 名字
     */
    private List<String> simpleColumns;

    /**
     * 普通的列对应的index
     */
    private List<Integer> simpleColumnsIndex;

    /**
     * 脱敏方法
     */
    private Integer maskCode;

    /**
     * 脱敏级别
     */
    private Integer maskLevel;

}
