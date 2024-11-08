package com.dg.communication.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @version v1.0.0
 * @belongsProject: private_search
 * @belongsPackage: com.dg.communication.pojo
 * @author: XBin
 * @description: 存证通信协议实体类
 * @createTime: 2024-01-09 14:15
 */
@Data
@AllArgsConstructor
public class SecurityProtocolEntity {

    /**
     *  版本号 (2字节)
     */
    private short versionNumber = 1;

    /**
     * 0x0001 上报请求   0x0002 请求响应   0x0003 存证信息上报  0x0004 存证信息反馈
     * 0x0005 证据查询   0x0006 证据查询结果反馈   0x0007 异常行为上报 命令类别 (2字节)
     */
    private short commandCategory;
    /**
     *  事项类别 (2字节)
     */
    private short matterCategory = 48;

    /**
     * msgVersion 0x3000中心存证   0x3010本地存证  0x3020 上报格式
     * 消息版本号 (2字节)
     */
    private short messageVersionNumber;
    /**
     * 加密模式 (1字节)
     */
    private byte encryptionMode = 0x00;
    /**
     * 认证与校验模式 (1字节)
     */
    private byte authenticationMode = 0x00;
    /**
     * 保留字段 (4字节)
     */
    private int reserved = 0;
    /**
     * 数据包长度 (4字节)
     */
    private int packetLength;
    /**
     * 数据域内容 (可变长)
     */
    private String dataContent;
    /**
     * 认证与校验域 (16字节)
     */
    private byte[] authenticationField = new byte[16];

    public SecurityProtocolEntity() {
    }


    public SecurityProtocolEntity(short commandCategory, short messageVersionNumber, String dataContent) {
        this.commandCategory = commandCategory;
        this.messageVersionNumber = messageVersionNumber;
        this.dataContent = dataContent;
    }

    /**
     * 计算数据包长度
     *
     * @author Xbin
     * @date 2024-01-09 10:08
     * @return int
     */
    public int calculatePacketLength() {
        // 协议头部固定长度
        int headerLength = 2 + 2 + 2 + 2 + 1 + 1 + 4 + 4;
        int dataContentLength = dataContent.getBytes(StandardCharsets.UTF_8).length;
        int authenticationFieldLength = authenticationField.length;
        return headerLength + dataContentLength + authenticationFieldLength;
    }

    /**
     * 把这个发送过去 每次发送我们需要根据内容修改json
     *
     * @author Xbin
     * @date 2024-01-09 10:12
     * @return byte[]
     */
    public byte[] toByteArray() {

        packetLength = calculatePacketLength();
        byte[] dataBytes = dataContent.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(18 + dataBytes.length + authenticationField.length);
        buffer.putShort(versionNumber);
        buffer.putShort(commandCategory);
        buffer.putShort(matterCategory);
        buffer.putShort(messageVersionNumber);
        buffer.put(encryptionMode);
        buffer.put(authenticationMode);
        buffer.putInt(reserved);
        buffer.putInt(packetLength);
        buffer.put(dataBytes);
        buffer.put(authenticationField);

        return buffer.array();
    }
}
