package com.company;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
/*
  cert:
    center:
      port: 50005
      host: 172.28.72.214
    local:
      port: 50004
      host: 172.28.72.214
 */

public class FileSplitEntity {
    // 协议对象的字段，根据需求定义
    private short version;
    private short command;
    private byte encryptionMode;
    private byte authMode;
    private int reserved;
    private long packetLength;
    private int jsonLength;
    private long fileSize;
    private String jsonData;
    private File fileData;
    private byte[] authData;

    public FileSplitEntity() {
    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public short getCommand() {
        return command;
    }

    public void setCommand(short command) {
        this.command = command;
    }

    public byte getEncryptionMode() {
        return encryptionMode;
    }

    public void setEncryptionMode(byte encryptionMode) {
        this.encryptionMode = encryptionMode;
    }

    public byte getAuthMode() {
        return authMode;
    }

    public void setAuthMode(byte authMode) {
        this.authMode = authMode;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public long getPacketLength() {
        return packetLength;
    }

    public void setPacketLength(long packetLength) {
        this.packetLength = packetLength;
    }

    public int getJsonLength() {
        return jsonLength;
    }

    public void setJsonLength(int jsonLength) {
        this.jsonLength = jsonLength;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public File getFileData() {
        return fileData;
    }

    public void setFileData(File fileData) {
        this.fileData = fileData;
    }

    public byte[] getAuthData() {
        return authData;
    }

    public void setAuthData(byte[] authData) {
        this.authData = authData;
    }

    public FileSplitEntity(short version, short command, byte encryptionMode, byte authMode, int reserved, long packetLength, int jsonLength, long fileSize, String jsonData, File fileData, byte[] authData) {
        this.version = version;
        this.command = command;
        this.encryptionMode = encryptionMode;
        this.authMode = authMode;
        this.reserved = reserved;
        this.packetLength = packetLength;
        this.jsonLength = jsonLength;
        this.fileSize = fileSize;
        this.jsonData = jsonData;
        this.fileData = fileData;
        this.authData = authData;
    }

    public byte[] toByteArray() {
        byte[] fileBytes = new byte[0];
        try {
            fileBytes = readFile(fileData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.allocate(52 + jsonData.getBytes(StandardCharsets.UTF_8).length + fileBytes.length);
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
