package com.dg.store.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "chunkInfo")
@NoArgsConstructor
@AllArgsConstructor
public class ChunkInfoJsonEntity {


    private String key;

    /**
     *  分片数据类型   1 普通数据  2 加密的数据  3 脱敏的数据
     */
    private Integer chunkInfoType;
    /**
     * 级别
     */
    private Integer level;

    /**
     * 加密方法
     */
    private String  encryptCode;

    /**
     * 脱敏方法
     */
    private String maskCode;

    /**
     * 文件ID
     */
    private String storeInfoId;

    /**
     * 分片唯一ID 信息
     */
    private String chunkInfoNo;

    /**
     * 索引
     */
    private Integer index;

    /**
     * 列名字
     */
    private List<String> columnTitle;

    /**
     * 列索引
     */
    private List<Integer> columnIndex;

    /**
     * 对应每一列的数据
     */
    private List<List<String>> rowData;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 分片创建时间
     */
    private Date createTime;



}
