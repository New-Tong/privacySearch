package com.dg.communication.service;

import com.alibaba.fastjson.JSONObject;
import com.dg.common.dto.*;
import com.dg.common.enums.CommandType;
import com.dg.common.enums.FileSuffixType;
import com.dg.communication.exception.CommunicationCertException;
import com.dg.communication.feign.IQueryService;
import com.dg.communication.feign.SplitService;
import com.dg.communication.pojo.*;
import com.dg.communication.utils.CommunicationUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @CalssName NettyTcpServer
 * @Author Xbin
 * @Description TCP通信服务，对外服务。
 * @Date 2023-11-30 11:44
 * @Version v2.0
 * @Email liuhongbindeemail@gmail.com
 */
@Service
@Slf4j
public class NettyTcpServer {

    @Value("${tcp.split.port}")
    private int port = 40011;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    @Value("${version.number}")
    private short versionNumber;

    @Autowired
    private SplitService splitService;

    @Autowired
    private IQueryService queryService;

    private final static Integer MIN_LENGTH = 50;

    private final static Integer HEADER_LENGTH = 40;


    @PostConstruct
    public void start() throws InterruptedException {
        System.out.println(splitService);
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)

                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    new ByteToMessageDecoder() {
                                        @Override
                                        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {

                                            log.info("start accept:");
                                            if (in.readableBytes() < 50) {
                                                System.out.println(ctx);
                                                return;
                                            }
                                            SplitCommunicationEntity splitCommunicationEntity = new SplitCommunicationEntity();
                                            // 解析协议头部
                                            short version = in.readShort();
                                            if (version!=versionNumber){
                                                throw new CommunicationCertException(505,"版本号错误");
                                            }else{
                                                splitCommunicationEntity.setVersionNumber(version);
                                            }
//                                            splitCommunicationEntity.setVersionNumber(version);
                                            short command = in.readShort();
                                            splitCommunicationEntity.setCommandCategory(command);
                                            byte encryptionMode = in.readByte();
                                            splitCommunicationEntity.setEncryptionMode(encryptionMode);
                                            byte authMode = in.readByte();
                                            splitCommunicationEntity.setAuthenticationMode(authMode);
                                            int reserved = in.readInt();
                                            splitCommunicationEntity.setReserved(reserved);
                                            long packetLength = in.readLong();
                                            splitCommunicationEntity.setPacketLength(packetLength);
                                            int jsonLength = in.readInt();
                                            splitCommunicationEntity.setJsonLength(jsonLength);
                                            long fileSize = in.readLong();
                                            splitCommunicationEntity.setFileSize(fileSize);
                                            int i = in.readableBytes();
                                            // 确保数据包完整
                                            if (i < packetLength - 40) {
                                                in.resetReaderIndex();
                                                return;
                                            }
                                            try{
                                                CommandType.getByCode(command);
                                            }catch (IllegalArgumentException e){
                                                throw new CommunicationCertException(505,"通信类型错误");
                                            }

                                            // 解析JSON数据
                                            byte[] jsonDataBytes1 = new byte[jsonLength];
                                            in.readBytes(jsonDataBytes1);
                                            String jsonDataTemp = new String(jsonDataBytes1);
                                            splitCommunicationEntity.setJsonData(jsonDataTemp);
                                            // 解析文件数据并写入临时文件
                                            byte[] fileDataBytesTemp = new byte[(int) fileSize];
                                            in.readBytes(fileDataBytesTemp);
                                            splitCommunicationEntity.setFileData(fileDataBytesTemp);


                                            // 解析认证与校验域
                                            byte[] authData = new byte[16];
                                            in.readBytes(authData);
                                            splitCommunicationEntity.setAuthenticationField(authData);
//                                            SplitCommunicationEntity splitCommunicationEntity = CommunicationUtils.PacketAnalyzer(in);
                                            CommandType commandType = CommandType.getByCode(splitCommunicationEntity.getCommandCategory());
                                            String jsonData = splitCommunicationEntity.getJsonData();
                                            long fileLength = 0;
                                            switch (commandType){

                                                case UPLOAD:
//                                                    文件上传
                                                    // 创建协议对象并添加到输出列表
                                                   FileSplitRequestDTO fileSplitRequestDTO = JSONObject.parseObject(jsonData, FileSplitRequestDTO.class);
                                                    fileSplitRequestDTO.setSubmitTime(new Date());
                                                    File tempFile = null;
                                                    byte[] fileDataBytes = splitCommunicationEntity.getFileData();
                                                    try {
                                                        tempFile = File.createTempFile("file-", "." + FileSuffixType.getNameByCode(fileSplitRequestDTO.getFileType()));
                                                        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                                                            fos.write(fileDataBytes);
                                                        }
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    fileSplitRequestDTO.setFile(tempFile);

//                                                    调用拆分服务
                                                    CommonResult<StoreInfoDTO> storeInfoCommonResult = splitService.largeFileSplit(fileSplitRequestDTO);
//                                                    存证系统发送信息
                                                    ServerHandler.splitFileUploadMessage(storeInfoCommonResult);

                                                    FileSearchRequestDTO uploadQueryData = new FileSearchRequestDTO();
                                                    uploadQueryData.setDataNo(storeInfoCommonResult.getData().getDataNo());
                                                    String uploadJsonData = JSONObject.toJSONString(uploadQueryData);
                                                    int uploadJsonLength = uploadJsonData.getBytes().length;
//                                                  存储头的字节个数
                                                    long uploadPacketLength = 46 + uploadJsonLength +fileLength;
                                                    SplitCommunicationEntity uploadFileSplitEntity = new SplitCommunicationEntity(versionNumber, CommandType.UPLOAD_BACK.getCode(), (byte) 0x00, (byte)0x00, 0, uploadPacketLength, uploadJsonLength, fileLength, uploadJsonData, null, new byte[16]);
                                                    out.add(uploadFileSplitEntity);
                                                    break;
                                                case DOWNLOAD:
//                                                    文件下载
                                                    FileSearchRequestDTO downloadQueryData = JSONObject.parseObject(jsonData, FileSearchRequestDTO.class);
                                                    GetStoreDTO store = queryService.getStore(downloadQueryData.getDataNo());
                                                    String downloadJsonData = JSONObject.toJSONString(store.getStoreInfoDTO());
                                                    ServerHandler.queryFileUploadMessage(store);
                                                    fileLength = store.getData().length;
                                                    int downloadJsonLength = downloadJsonData.getBytes().length;
                                                    long downloadPacketLength = 46 + downloadJsonLength +fileLength;
                                                    SplitCommunicationEntity downloadFileSplitEntity = new SplitCommunicationEntity(versionNumber,CommandType.DOWNLOAD_BACK.getCode(),(byte)0x00,(byte)0x00,0,downloadPacketLength,downloadJsonLength,fileLength,downloadJsonData,store.getData(),new byte[16]);
                                                    out.add(downloadFileSplitEntity);
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    },
                                    new ServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
//                 添加一个     监听事件
            bootstrap.bind(port).addListener(new ChannelFutureListener() {
                //                 服务器启动   时候会触发
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    System.out.println("服务器启动");
                }
            });
        } finally {
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
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

        if (workerGroup != null && bossGroup != null) {
            workerGroup.shutdownGracefully().sync();
            bossGroup.shutdownGracefully().sync();
        }

    }




}
