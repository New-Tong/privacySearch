package com.dg.communication.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @Author:Xbin
 * Description: 拆分文件通信实体类
 * Date 2024-04-09 14:21
 * Version v2.0
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SplitCommunicationEntity {
    /**
     *  版本号 (2字节)
     */
    private short versionNumber = 1;

    /**
     * 0x0001 上传 0x0002 下载  0x003 结果返回
     *
     */
    private short commandCategory;

    /**
     * 传输内容是否加密 (1字节) 暂时未启用
     */

    private byte encryptionMode;

    /**
     * 认证与校验模式 (1字节)
     */
    private byte authenticationMode;

    /**
     * 保留字段 (4字节)
     */
    private int reserved;

    /**
     * 数据包长度 (16字节)
     */
    private long packetLength;

    /**
     * json长度 (4字节)
     */
    private int jsonLength;

    /**
     * 文件长度 (16字节)
     */
    private long fileSize;

    /**
     * 数据域内容 (可变长)
     */
    private String jsonData;

    /**
     * 文件
     */
    private byte[] fileData;
    /**
     * 认证与校验域 (16字节)
     */
    private byte[] authenticationField = new byte[16];

    public byte[] toByteArray() {
        int jsonDataLength = jsonData.getBytes(StandardCharsets.UTF_8).length;
        int totalLength = 0;

        if (Objects.isNull(fileData)){
             totalLength = 46 + jsonDataLength;
        }else{
             totalLength = 46 + jsonDataLength + fileData.length;

        }
        ByteBuffer buffer = ByteBuffer.allocate(totalLength);

        buffer.putShort(versionNumber);
        buffer.putShort(commandCategory);
        buffer.put(encryptionMode);
        buffer.put(authenticationMode);
        buffer.putInt(reserved);
        buffer.putLong(packetLength);
        buffer.putInt(jsonLength);
        buffer.putLong(fileSize);
        buffer.put(jsonData.getBytes(StandardCharsets.UTF_8));
        if (!Objects.isNull(fileData)){
            buffer.put(fileData);
        }
        buffer.put(authenticationField);
        return buffer.array();
    }
}
