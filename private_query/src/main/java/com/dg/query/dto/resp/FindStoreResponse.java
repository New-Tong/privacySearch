package com.dg.query.dto.resp;

import com.dg.query.pojo.ChunkInfoJsonEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class FindStoreResponse {

    /**
     * 数据编号
     */
    private String storeNo;

    /**
     * 拆分提交时间
     */
    private Date submitTime;

    /**
     * 分片数量
     */
    private Long chunkInfoCount;

    /**
     * 文件名称
     */
    private String storeName;

    /**
     * 文件类型
     */
    private Integer storeType;

    /**
     * 文件大小 k
     */
    private BigDecimal storeSize;

    /**
     * 文件位置(保留）
     */
    private String storeUrl;

    /**
     * 文件内容的hash
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
     * 脱敏级别(暂定
     */
    private String maskLevel;

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

    /**
     * 创建时间 文件拆分时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 分片信息
     */
    List<ChunkInfoJsonEntity> chunkInfoJsonEntities;
}
