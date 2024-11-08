package com.dg.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
/**
 * @Author:Xbin
 * Description: 大文件拆分
 * Date 2024-04-09 14:11
 * Version v2.0
*/
@Data
public class FileSplitRequestDTO {


    /**
     * 文件数据
     */
    private File file;
    /**
     * 文件类型： 1 excel表格， 2 图像， 3 文本
     */
    @NotNull(message = "文件类型不能为空")
    private Integer fileType;

    /**
     * 文件名称
     */
    @NotBlank(message = "文件名不能为空")
    private String fileName;

    /**
     * 文件后缀
     */
    private String fileSuffix;

    /**
     * 文件标题
     */
    private String fileTitle;

    /**
     * 文件概览
     */
    private String fileAbs;

    /**
     * 存取方名字
     */
    private String performerName;


    private String performerCode;

    /**
     * 脱敏的列
     */
    private List<Integer> desenColumn;
    /**
     * 脱敏级别
     */
    private List<Integer> desenLevel ;

    /**
     * 脱敏意图
     */
    private String desenIntention;

    /**
     * 脱敏要求
     */
    private List<String> desenRequirements;

    /**
     * 脱敏控制集合
     */
    private String desenControlSet;

    /**
     * 提交时间
     */
    private Date submitTime;

}
