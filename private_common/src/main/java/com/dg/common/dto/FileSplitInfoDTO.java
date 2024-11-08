package com.dg.common.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
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
public class FileSplitInfoDTO {

    /**
     * 文件类型：表格，图像，文本
     */
    @NotNull(message = "文件类型不能为空")
    private Integer fileType;

    /**
     * 文本后缀
     */
    private String fileSuffix;

    /**
     * 文件编号
     */
    private String storeNo;

    /**
     * 分片编号
     */
    private String chunkInfoNo;

    /**
     * 索引（在后续合成的时候可以按顺序合成）
     */
    private Integer storeIndex;

    /**
     * 文件大小
     */
    @NotNull(message = "文件大小不能为空")
    private Long fileSize;

    /**
     * 对应需要脱敏的列
     */
    private List<Integer> maskColumn;

    /**
     * 脱敏方法
     * @see com.dg.common.enums.DesenLevelType
     */
    private List<Integer> maskLevel;


    /**
     * 文件数据
     */
    @NotNull(message = "拆分文件不能为空")
    private File file;

}
