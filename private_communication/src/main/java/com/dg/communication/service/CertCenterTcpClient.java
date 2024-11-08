package com.dg.communication.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dg.communication.exception.CommunicationCertException;
import com.dg.communication.pojo.*;
import com.dg.communication.utils.SocketUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @CalssName NettyTcpClient
 * @Author Xbin
 * @Description 连接中心存证信息客户端，主要进行第一次发送
 * @Date 2023-11-27 11:00
 * @Version v2.0
 * @Email liuhongbindeemail@gmail.com
 */
@Service
@Slf4j
public class CertCenterTcpClient {


    private static String host;

    @Value("${tcp.cert.center.host}")
    public void setHost(String param) {
        host = param;
    }

    private static int port;

    @Value("${tcp.cert.center.port}")
    public void setPort(int param) {
        port = param;
    }

    private static Bootstrap bootstrap;

    private EventLoopGroup workerGroup;

    private static ChannelFuture future;

    private static Channel channel;

    /**
     * 最多尝试连接次数
     */
    private static final int maxConnectionAttempts = 3;

    private static final long retryDelayMillis = 5000;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private final static Map<String, MessageInfo> messageInfoMap = new ConcurrentHashMap();

    @PostConstruct
    public void start() {

        System.out.println("Starting CertTcpClient");

        executorService.scheduleAtFixedRate(this::tryConnect, 0, 4, TimeUnit.HOURS);

    }

    private void tryConnect() {
        for (int attempt = 1; attempt <= maxConnectionAttempts; attempt++) {
            try {
                System.out.println("host:"+host);
                System.out.println("port:"+port);
                connect();
                break;  // 连接成功，退出循环
            } catch (Exception e) {
                System.err.println("join " + attempt + "fail ...");
            }
        }
    }

    private void connect() throws InterruptedException {
        //        连接配置
        workerGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(
                                new ByteToMessageDecoder() {
                                    @Override
                                    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out){
                                        while (in.readableBytes() >= 17) {
                                            int length = in.getInt(in.readerIndex() + 14); // 14到17字节表示数据包长度，使用整数表示
                                            if (in.readableBytes() < length) {
                                                in.resetReaderIndex(); // 如果可读字节数不足一个完整消息，重置读取索引，等待更多数据
                                                throw new CommunicationCertException(500,"接收数据异常");
                                            }
                                            byte[] messageBytes = new byte[length];
                                            in.readBytes(messageBytes);
                                            SecurityProtocolEntity securityProtocolEntity;
                                            try {
                                                 securityProtocolEntity = SocketUtils.decodePacket(messageBytes);
                                                 System.out.println("security:"+securityProtocolEntity);
                                            }catch (CommunicationCertException e){
                                                throw new CommunicationCertException(500,"转换异常",messageBytes);
                                            }
                                            out.add(securityProtocolEntity);
                                        }
                                    }
                                },
                                new ClientHandler());
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE, true);
        future = bootstrap.connect(host, port).sync();
        channel = future.channel();
    }

    /**
     * @return void
     * @author Xbin
     * @date 2023-12-07 10:49
     */
    public static void sendMessage(MessageInfo messageInfo) {
        SecurityProtocolEntity oneSecurityProtocolEntity = messageInfo.getOneSecurityProtocolEntity();
        String dataContent = oneSecurityProtocolEntity.getDataContent();
        byte[] data = oneSecurityProtocolEntity.toByteArray();
        SendOneCertMessageInfo sendOneCertMessageInfo = JSON.parseObject(dataContent, SendOneCertMessageInfo.class);
        log.info("push message cert:");
        //        开始连接
        try {
            if (channel != null && channel.isActive()) {
                // 向服务器发送字节数组
                ByteBuf byteBuf = Unpooled.wrappedBuffer(data);
                channel.writeAndFlush(byteBuf);

            } else {
                // 处理连接未初始化或已断开的情况
                // 可以根据实际需求进行处理，例如重新连接或抛出异常
                future = bootstrap.connect(host, port).sync();
                channel = future.channel();
                // 重新发送字节数组
                ByteBuf byteBuf = Unpooled.wrappedBuffer(data);
                channel.writeAndFlush(byteBuf);
                System.err.println("Connection not initialized or already closed.");
            }
            messageInfoMap.put(sendOneCertMessageInfo.getEvidenceID(),messageInfo);
        } catch (Exception e) {
            log.error("存证系统通信连接失败");
            e.printStackTrace();
//            throw new CommunicationCertException(501,);
        }
    }

    /**
     * 关闭服务
     *
     * @return void
     * @author Xbin
     * @date 2023-11-27 18:05
     */
    @PreDestroy
    public void stop() throws InterruptedException {
        // 关闭 Netty 客户端
        if (workerGroup != null) {
            workerGroup.shutdownGracefully().sync();
        }
    }


    static class ClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) {

        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            SecurityProtocolEntity securityProtocolEntity = (SecurityProtocolEntity)msg;
            log.info("接收到了中心信息:{}", securityProtocolEntity);
            CertOneBackMessageInfo certOneBackMessageInfo = JSONObject.parseObject(securityProtocolEntity.getDataContent(), CertOneBackMessageInfo.class);
            /**
             * evidenceID 是唯一业务ID
             */
            String evidenceID = certOneBackMessageInfo.getEvidenceID();
            System.out.println("oneBack:"+JSONObject.toJSONString(certOneBackMessageInfo));
            System.out.println("evidenceId:"+evidenceID);
            MessageInfo messageInfo = messageInfoMap.get(evidenceID);

//            需要加入防伪信息
            SecurityProtocolEntity towSecurityProtocolEntity = messageInfo.getTowSecurityProtocolEntity();

            String dataContent = towSecurityProtocolEntity.getDataContent();
            SendTowCertMessageInfo sendTowCertMessageInfo = JSON.parseObject(dataContent, SendTowCertMessageInfo.class);
            System.out.println("translate1:"+JSONObject.toJSONString(sendTowCertMessageInfo));
            sendTowCertMessageInfo.setRandomidentification(certOneBackMessageInfo.getRandomidentification());
            sendTowCertMessageInfo.setEvidenceID(certOneBackMessageInfo.getEvidenceID());
            sendTowCertMessageInfo.setNonce(certOneBackMessageInfo.getData().getNonce());
            System.out.println("translate2:"+JSONObject.toJSONString(sendTowCertMessageInfo));
            towSecurityProtocolEntity.setDataContent(JSON.toJSONString(sendTowCertMessageInfo));
//            发送信息到本地存证系统
            messageInfo.setTowSecurityProtocolEntity(towSecurityProtocolEntity);
            System.out.println("messageInfo:"+JSONObject.toJSONString(messageInfo));
            CertLocalTcpClient.sendMessage(messageInfo);
            messageInfoMap.remove(evidenceID);
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            //   异常处理
            cause.printStackTrace();
        }

    }

}
