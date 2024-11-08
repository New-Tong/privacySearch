package com.dg.store.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName chunk_info
 */
@Data
@TableName(value = "chunk_info")
public class ChunkInfoEntity implements Serializable {
    /**
     * 自增主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件ID
     */
    private String chunkInfoNo;

    /**
     * 分片(局部分片中的脱敏数据部分)唯一编号  uuid
     */
    private String maskDataNo;

    /**
     * 分片(局部分片中的正常数据部分)唯一编号  uuid
     */
    private String simpleDataNo;

    /**
     * 分片(局部分片中的加密数据部分)唯一编号  uuid
     */
    private String encryptDataNo;

    /**
     * 脱敏方法
     */
    private String  maskCode;

    /**
     * 分片所在的 文件索引
     */
    private Integer storeIndex;

    /**
     * 分片加密方法（脱敏方法）
     */
    private String encryptCode;

    /**
     * 级别(敏感，不敏感 ....)
     */
    private Integer encryptLevel;

    /**
     * 脱敏级别
     */
    private Integer maskLevel;

    /**
     * 是否有效  1有效  0无效
     */
    private Integer isValid;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 分片创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;

}