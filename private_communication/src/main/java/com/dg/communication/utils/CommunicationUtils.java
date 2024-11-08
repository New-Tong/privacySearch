package com.dg.communication.utils;

import com.dg.common.enums.CommandType;
import com.dg.communication.pojo.SplitCommunicationEntity;
import com.dg.communication.exception.CommunicationCertException;
import io.netty.buffer.ByteBuf;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @version v1.0.0
 * @belongsProject: privateSearch
 * @belongsPackage: com.dg.communication.utils
 * @author: XBin
 * @description: 解析校验传入数据
 * @createTime: 2024-04-09 14:28
 */
public class CommunicationUtils {


    private static short versionNumber  =1;

    @Value("${version.number}")
    public void setVersionNumber(short  param) {
        versionNumber = param;
    }

    /**
     * 最小的请求数据包长度
     */
    private final static Integer MIN_LENGTH = 56;

    /**
     * 请求头长度（不包括json，file和后16位校验）
     */
    private final static Integer HEADER_LENGTH = 40;



    public static SplitCommunicationEntity PacketAnalyzer(ByteBuf in){

        SplitCommunicationEntity fileSplitEntity = new SplitCommunicationEntity();
//        short version = in.readShort();
        if (in.readableBytes() < MIN_LENGTH) {
            throw new CommunicationCertException(505,"不符合调用接口规则");
        }
        // 解析协议头部
        short version = in.readShort();
        if (version!=versionNumber){
            throw new CommunicationCertException(505,"版本号错误");
        }else{
            fileSplitEntity.setVersionNumber(version);
        }
//        通信类型
        short command = in.readShort();
        fileSplitEntity.setCommandCategory(command);

//        是否加密（暂时未启用）
        byte encryptionMode = in.readByte();
        fileSplitEntity.setEncryptionMode(encryptionMode);

//        认证和校验模式
        byte authMode = in.readByte();
        fileSplitEntity.setAuthenticationMode(authMode);

//        保留字段
        int reserved = in.readInt();
        fileSplitEntity.setReserved(reserved);

//        整个数据包长度
        long packetLength = in.readLong();
        fileSplitEntity.setPacketLength(packetLength);
//        json信息数据长度
        int jsonLength = in.readInt();
        fileSplitEntity.setJsonLength(jsonLength);
//        file文件数据长度
        long fileSize = in.readLong();
        fileSplitEntity.setFileSize(fileSize);


        int i = in.readableBytes();
        // 确保数据包完整
        if (i < packetLength - HEADER_LENGTH) {
            in.resetReaderIndex();
        }
        try{
           CommandType.getByCode(command);
        }catch (IllegalArgumentException e){
            throw new CommunicationCertException(505,"通信类型错误");
        }
        // 解析JSON并且 写入 json数据
        byte[] jsonDataBytes = new byte[jsonLength];
        in.readBytes(jsonDataBytes);
        String jsonData = new String(jsonDataBytes);
        fileSplitEntity.setJsonData(jsonData);

        byte[] fileDataBytes = new byte[(int) fileSize];
        in.readBytes(fileDataBytes);
        fileSplitEntity.setFileData(fileDataBytes);

        // 解析认证与校验域
        byte[] authData = new byte[16];
        in.readBytes(authData);
        fileSplitEntity.setAuthenticationField(authData);
        in.clear();
        return fileSplitEntity;
    }

}
