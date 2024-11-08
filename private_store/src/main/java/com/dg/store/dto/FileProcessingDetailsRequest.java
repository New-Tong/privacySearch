package com.dg.store.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

/**
 * @version v1.0.0
 * @belongsProject: privateSearch
 * @belongsPackage: com.dg.store.com.dg.common.dto
 * @author: XBin
 * @description: 文件处理详细信息实体类(哪些需要加密，哪些需要拆分） 这里应该就是拿到了初次分片的数据
 * @createTime: 2024-02-26 15:54
 */
@Data
public class FileProcessingDetailsRequest {

    /**
     * 文件类型：excel表格，图像，文本
     */
    @NotNull(message = "文件类型不能为空")
    private Integer fileType;

    /**
     * 分片的编号
     */
    private String chunkInfoNo;

    /**
     * 文件编号
     */
    private String storeNo;

    /**
     * 索引（在后续合成的时候可以按顺序合成）
     */
    private Integer storeIndex;

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

    /**
     * 文件数据
     */
    @NotNull(message = "拆分文件不能为空")
    private MultipartFile file;

}
