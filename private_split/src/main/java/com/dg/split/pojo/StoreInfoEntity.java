package com.dg.split.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author:Xbin
 * Description: store_info
 * Date 2024-04-12 15:46
 * Version v2.0
*/
@Data
@TableName("store_info")
@NoArgsConstructor
@AllArgsConstructor
public class StoreInfoEntity implements Serializable {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 数据唯一编号
     */
    private String dataNo;

    /**
     * 拆分提交时间
     */
    private Date submitTime;

    /**
     * 分片数量
     */
    private int chunkInfoCount;

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
    private Boolean isSuccess;

    /**
     * 创建时间 文件拆分时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

}