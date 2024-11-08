package com.dg.common.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @version v1.0.0
 * @belongsProject: privateSearch
 * @belongsPackage: com.dg.common.pojo
 * @author: XBin
 * @description:
 * @createTime: 2024-04-10 15:23
 */
@Data
public class StoreInfoEntity {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 数据唯一编号(文件唯一编号）
     */
    private String dataNo;

    /**
     * 用户提交时间
     */
    private Date submitTime;

    /**
     * 文件名称
     */
    private String storeName;

    /**
     * 文件后缀
     *  @see com.dg.common.enums.FileSuffixType
     */
    private String storeSuffix;

    /**
     * 文件类型
     *  @see com.dg.common.enums.FileType
     */
    private Integer storeType;

    /**
     * 文件大小 k
     */
    private BigDecimal storeSize;

    /**
     * 文件位置(保留）源文件位置。
     */
    private String storeUrl;

    /**
     * 文件内容的hash（全部内容）
     */
    private String storeHash;

    /**
     * 拆分重构存储内容标题
     */
    private String storeTitle;

    /**
     * 拆分重构存储内容摘要
     */
    private String storeAbs;

    /**
     * 拆分重构存储文件的签名
     */
    private String storeSign;

    /**
     * 拆分方法  暂时存储json，如果后续有一个结构化的数据改为建表
     */
    private String splitMethod;

    /**
     * 脱敏的列
     */
    private List<Integer> maskColumn;

    /**
     * 脱敏级别
     * @see com.dg.common.enums.DesenLevelType
     */
    private List<String> maskLevel;

    /**
     * 脱敏意图
     */
    private String maskIntention;

    /**
     * 脱敏要求
     */
    private String maskRequirements;

    /**
     * 脱敏控制集合（操作配置）
     */
    private String maskControlSet;

    /**
     * 存取方  名字
     */
    private String performerName;

    /**
     * 存取方  编号
     */
    private String performerCode;

    /**
     * 拆分结果  1成功 0 失败
     */
    private Integer isSuccess;

}
