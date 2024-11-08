package com.dg.communication.utils;

import com.dg.communication.exception.CommunicationCertException;
import com.dg.communication.pojo.SecurityProtocolEntity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @version v1.0.0
 * @belongsProject: private_search
 * @belongsPackage: com.dg.communication.utils
 * @author: XBin
 * @description:
 * @createTime: 2023-11-20 16:37
 */

public class SocketUtils {
    //将ByteBuf消息转换成字符串
    public static String getJson(Object msg) {
        String json;
        try {
            ByteBuf buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            json = new String(bytes);
        } finally {
            ReferenceCountUtil.release(msg);
        }
        return json;

    }
    //当收到消息后，进行相关业务逻辑的处理，处理完后将结果发生给对方
    public static void sendMsg(ChannelHandlerContext ctx, String msg){
        ctx.writeAndFlush(Unpooled.buffer().writeBytes(msg.getBytes()));
    }

    //服务端或客户端，主动向对方发生消息，如果是服务端，则需要将客户端注册成功后的channel保存下来
    //如服务端需要向所有客户端广播某个消息时
    public static void sendMsg(Channel ctx, String msg){
        ctx.writeAndFlush(Unpooled.buffer().writeBytes(msg.getBytes()));
    }

    public static SecurityProtocolEntity decodePacket(byte[] packetBytes) throws CommunicationCertException {

        ByteBuffer buffer = ByteBuffer.wrap(packetBytes);

        // 读取各个字段
        /**
         *  版本号 (2字节)
         */
        short versionNumber = buffer.getShort();
        /**
         * 0x0001 上报请求   0x0002 请求响应   0x0003 存证信息上报  0x0004 存证信息反馈
         * 0x0005 证据查询   0x0006 证据查询结果反馈   0x0007 异常行为上报 命令类别 (2字节)
         */
        short commandCategory = buffer.getShort();

        /**
         *  事项类别 (2字节)
         */
        short matterCategory = buffer.getShort();

        /**
         * msgVersion 0x3000中心存证   0x3010本地存证  0x3020 上报格式
         * 消息版本号 (2字节)
         */
        short messageVersionNumber = buffer.getShort();

        /**
         * 加密模式 (1字节)
         */
        byte encryptionMode = buffer.get();

        /**
         * 认证与校验模式 (1字节)
         */
        byte authenticationMode = buffer.get();

        /**
         * 保留字段 (4字节)
         */
        int reserved = buffer.getInt();

        /**
         * 数据包长度 (4字节)
         */
        int packetLength = buffer.getInt();

        byte[] dataContentBytes = new byte[packetLength - 18 - 16];

        buffer.get(dataContentBytes);
        /**
         * 返回的json数据
         */
        String dataContent = new String(dataContentBytes, StandardCharsets.UTF_8);

        /**
         * 认证与校验域 (16字节)
         */
        byte[] authenticationField = new byte[16];

        buffer.get(authenticationField);

        return new SecurityProtocolEntity(versionNumber, commandCategory, matterCategory,
                messageVersionNumber, encryptionMode, authenticationMode,
                reserved, packetLength, dataContent, authenticationField);
    }


    public static String localIPGet() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return address.getHostAddress();
    }


    // 将字节数组转换为十六进制字符串
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


}
