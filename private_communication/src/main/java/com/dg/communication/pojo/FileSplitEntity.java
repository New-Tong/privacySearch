package com.dg.communication.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileSplitEntity {

    /**
     *  版本号 (2字节) 暂定为001
     */
    private short version;

    /**
     * 0x0001 上传 0x0002 下载  0x003 结果返回
     *
     */
    private short command;

    /**
     * 传输内容是否加密 (1字节) 暂时未启用
     */
    private byte encryptionMode;

    /**
     * 认证与校验模式 (1字节)
     */
    private byte authMode;

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
     * json内容 (可变长)
     */
    private String jsonData;

    /**
     * 文件
     */
    private File fileData;

    /**
     * 认证与校验域 (16字节)
     */
    private byte[] authData;

    /**
     * 转为字节数组
     */
    public byte[] toByteArray() {
        byte[] fileBytes = new byte[0];
        try {
            fileBytes = readFile(fileData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteBuffer buffer = ByteBuffer.allocate(46 + jsonData.getBytes(StandardCharsets.UTF_8).length + fileBytes.length);
        buffer.putShort(version);
        buffer.putShort(command);
        buffer.put(encryptionMode);
        buffer.put(authMode);
        buffer.putInt(reserved);
        buffer.putLong(packetLength);
        buffer.putInt(jsonLength);
        buffer.putLong(fileSize);
        buffer.put(jsonData.getBytes(StandardCharsets.UTF_8));
        buffer.put(fileBytes);
        buffer.put(authData);
        return buffer.array();
    }

    private byte[] readFile(File file) throws IOException {
        if (Objects.isNull(file)){
            return new byte[]{};
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            fis.read(buffer);
            return buffer;
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }
}
