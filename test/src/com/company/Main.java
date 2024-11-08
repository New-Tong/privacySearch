package com.company;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
/*
  cert:
    center:
      port: 50005
      host: 172.28.72.214
    local:
      port: 50004
      host: 172.28.72.214
 */

//文件拆分
public class Main {

    public static void main(String[] args) {
        String serverAddress = "172.28.72.225"; // 服务器地址
        int port = 40011; // 服务器监听的端口号

        while (true) {
            try (Socket socket = new Socket(serverAddress, port)) {
                System.out.println("已连接到服务器：" + serverAddress + " 在端口：" + port);

                // 读取文件内容到字节数组
                byte[] fileContent = Files.readAllBytes(Paths.get("C:\\Users\\lynn19920229\\Desktop\\personModal.xlsx"));
                String json = "{\n" +
                        " \n" +
                        "    \"fileType\": 1,\n" +
                        "    \"fileName\": \"example.xlsx\",\n" +
                        "    \"fileTitle\": \"Example File\",\n" +
                        "    \"fileAbs\": \"test data\",\n" +
                        "    \"maskIntention\": \"保护用户个人信息 test\",\n" +
                        "    \"maskRequirements\": \"隐私保护 test\",\n" +
                        "    \"encryptCodeType\": 1,\n" +
                        "    \"encryptLevel\": 2,\n" +
                        "    \"performer_name\":\"admin\",\n" +
                        "    \"performer_code\": \"0212\",\n" +
                        "    \"desenRequirements\":[\"test\"],\n" +
                        "\"desenColumn\": [\n" +
                        "        12,\n" +
                        "        13,\n" +
                        "        21,\n" +
                        "        25,\n" +
                        "        28,\n" +
                        "        27,\n" +
                        "        42,\n" +
                        "        43\n" +
                        "    ],\n" +
                        "    \"maskCode\": 1,\n" +
                        "    \"desenLevel\": [\n" +
                        "        3,\n" +
                        "        2,\n" +
                        "        4,\n" +
                        "        2,\n" +
                        "        2,\n" +
                        "        3,\n" +
                        "        3,\n" +
                        "        4\n" +
                        "    ]\n" +
                        "}";

                // 构建模拟的数据包
                ByteBuffer buffer = ByteBuffer.allocate(2 + 2 + 1 + 1 + 4 + 8 +4 + 8 + 16 + json.getBytes().length + fileContent.length);
                buffer.putShort( (short) 2); // 版本号
                buffer.putShort((short) 1); // 命令类别，假设为上传
                buffer.put((byte)0); // 加密模式，未加密
                buffer.put((byte) 0); // 认证与校验模式，未签名
                buffer.putInt(0); // 保留字段
                buffer.putLong(buffer.capacity()); // 数据包长度
                buffer.putInt(json.getBytes().length); // JSON数据包长度（这里简化处理，假设文件内容即JSON长度）
                buffer.putLong(fileContent.length); // 文件大小长度的前8字节
                buffer.put(json.getBytes()); // 文件大小长度的后8字节
                buffer.put(fileContent); // 文件内容
                buffer.put(new byte[16]); // 认证与校验域，简化处理为全0

                byte[] array = buffer.array();
//                System.out.println(Arrays.toString(array));
//                System.out.println(array.length);
                // 发送数据到服务器
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(array);
                outputStream.flush(); // 刷新缓冲区，确保数据被发送到服务器端

                System.out.println("数据已发送");
                Thread.sleep(1000*40);
                // 接收服务器的响应
                InputStream inputStream = socket.getInputStream();
                byte[] responseBuffer = new byte[1024]; // 假设响应的数据不超过1024字节
                int bytesRead = inputStream.read(responseBuffer);
                if (bytesRead != -1) {
                    String response = new String(responseBuffer, 0, bytesRead);
                    System.out.println("收到服务器的响应：" + response);
                } else {
                    System.out.println("服务器未返回任何数据");
                }

                // 每隔一段时间发送一次数据
                Thread.sleep(5000); // 5秒
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
