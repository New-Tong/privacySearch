package com.dg.query.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName chunk_store_relation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "chunk_store_relation")
public class ChunkStoreRelationEntity implements Serializable {

    /**
     * 自增主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件索引
     */
    private String storeInfoNo;

    /**
     * 分片信息索引
     */
    private String chunkInfoNo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否有效
     */
    private Integer isValid;


    private static final long serialVersionUID = 1L;



}