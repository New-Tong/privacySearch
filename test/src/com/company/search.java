package com.company;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

//文件搜索
public class search {
    static String serverAddress = "172.28.72.225"; // 服务器地址
    static int port = 40011; // 服务器监听的端口号
    public static void main(String[] args) throws IOException {
        byte [] tempData;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        Socket socket = null;
        try {
            socket = new Socket(serverAddress, port);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendMessage(outputStream);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int numberOfBytesRead;
        byte[] data = new byte[1024000]; // 临时存储读取的数据

// 从 inputStream 中读取数据直到没有更多的数据
//        while (inputStream.read(data, 0, data.length)!= -1) {
//            FileSplitEntity fileSplitEntity = parseResponse(data);
//            System.out.println("test");
//        }
        while ((numberOfBytesRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, numberOfBytesRead);
        }

        byte[] responseData = buffer.toByteArray();
        FileSplitEntity fileSplitEntity = parseResponse(responseData);

    }

    private static  void sendMessage(OutputStream outputStream){
        System.out.println("已连接到服务器：" + serverAddress + " 在端口：" + port);

        // 读取文件内容到字节数组
//        byte[] fileContent = Files.readAllBytes(Paths.get("C:\\Users\\Administrator\\Desktop\\test.xlsx"));
        byte[] fileContent =new byte[]{};
//      文件返回唯一标识符
        String json = "{ \"dataNo\": \"b663119d-f617-4cab-abfb-d90b5a2eabac\"}";

        // 构建模拟的数据包
        ByteBuffer buffer = ByteBuffer.allocate(2 + 2 + 1 + 1 +4+ 8 + 4 + 8 + json.getBytes().length + fileContent.length + 16);
        buffer.putShort((short) 2); // 版本号
        buffer.putShort((short) 2); // 命令类别，假设为上传
        buffer.put((byte) 0x00); // 加密模式，未加密
        buffer.put((byte) 0x00); // 认证与校验模式，未签名
        buffer.putInt(0); // 保留字段
        buffer.putLong(buffer.capacity()); // 数据包长度
        buffer.putInt(json.getBytes().length); // JSON数据包长度（这里简化处理，假设文件内容即JSON长度）
        buffer.putLong(fileContent.length); // 文件大小长度的前8字节
        buffer.put(json.getBytes()); // 文件大小长度的后8字节
        buffer.put(fileContent); // 文件内容
        buffer.put(new byte[16]); // 认证与校验域，简化处理为全0

        // 发送数据到服务器
        byte[] array = buffer.array();
        try {
            outputStream.write(array);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
     // 刷新缓冲区，确保数据被发送到服务器端

        System.out.println("数据已发送");
        printByteArray(array);
    }

    public static void printByteArray(byte[] byteArray) {
        if (byteArray == null) {
            System.out.println("Byte array is null.");
            return;
        }

        System.out.println("Byte array length: " + byteArray.length);
        System.out.print("Byte array content (hex): ");

        for (byte b : byteArray) {
            System.out.print(String.format("0x%02X ", b));
        }
        System.out.println();
    }

    // 解析接收到的字节数据为FileSplitEntity对象
    private static FileSplitEntity parseResponse(byte[] responseBuffer) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(responseBuffer);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        short version = dataInputStream.readShort();
        short command = dataInputStream.readShort();
        byte encryptionMode = dataInputStream.readByte();
        byte authMode = dataInputStream.readByte();
        int reserved = dataInputStream.readInt();
        long packetLength = dataInputStream.readLong();
        int jsonLength = dataInputStream.readInt();
        long fileSize = dataInputStream.readLong();

        // Check if there are enough bytes to read
        int remainingBytes = responseBuffer.length - 38; // subtracting the bytes read for version, command, encryptionMode, authMode, reserved, packetLength, jsonLength, and fileSize
        if (remainingBytes < jsonLength) {
            throw new IOException("Insufficient bytes to read JSON data");
        }

        // Read JSON data
        byte[] jsonBytes = new byte[jsonLength];
        dataInputStream.readFully(jsonBytes);
        String jsonData = new String(jsonBytes, StandardCharsets.UTF_8);
        System.out.println(jsonData);
        // Check if there are enough bytes to read file data
        remainingBytes -= jsonLength;
        if (remainingBytes < fileSize) {
//            throw new IOException("Insufficient bytes to read file data");
        }
        // Read file data
        byte[] fileBytes = new byte[(int) fileSize];
        dataInputStream.readFully(fileBytes);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("C:\\Users\\lynn19920229\\Desktop\\test1.xlsx");
            fos.write(fileBytes);
            System.out.println("文件保存成功");
        } catch (IOException e) {
            System.err.println("保存文件时出错：" + e.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    System.err.println("关闭文件输出流时出错：" + e.getMessage());
                }
            }
        }
        // Read authentication data
        byte[] authData = new byte[16];
        dataInputStream.readFully(authData);

        return new FileSplitEntity(version, command, encryptionMode, authMode, reserved, packetLength,
                jsonLength, fileSize, jsonData, null, authData);
    }
}
