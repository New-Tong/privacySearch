package com.dg.communication.service;

import com.alibaba.fastjson.JSONObject;
import com.dg.common.dto.CommonResult;
import com.dg.common.dto.GetStoreDTO;
import com.dg.common.dto.StoreInfoDTO;
import com.dg.common.enums.FileSuffixType;
import com.dg.communication.pojo.*;
import com.dg.communication.utils.TimeUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.math.BigDecimal;
import java.util.*;

/**
 * @Author:Xbin
 * Description: 消息处理类
 * Date 2024-04-09 14:34
 * Version v2.0
*/
public class ServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 文件拆分
     *
     * @author Xbin
     * @date 2024-04-09 14:34
     * @return void
    */
    public static void splitFileUploadMessage(CommonResult<StoreInfoDTO> storeInfoDTOCommonResult) {
        MessageInfo messageInfo = new MessageInfo();
        StoreInfoDTO data = storeInfoDTOCommonResult.getData();
        // 以KB为单位的存储大小
        BigDecimal storeSizeInKB = data.getStoreSize();
        // 将KB转换为字节
        BigDecimal storeSizeInBytes = storeSizeInKB.multiply(BigDecimal.valueOf(1024));
        SendOneCertMessageInfo sendOneCertMessageInfo = new SendOneCertMessageInfo();
        SendOneCertMessageInfo.OneSendMessage oneSendMessage = new SendOneCertMessageInfo.OneSendMessage();
        long storeSize = storeSizeInBytes.longValue();
        String fileType = FileSuffixType.getNameByCode(data.getStoreType());
        oneSendMessage.setObjectMode(fileType);
        oneSendMessage.setObjectSize(String.valueOf(storeSize));
        String evidenceID = UUID.randomUUID().toString();
        Date submitTime = data.getSubmitTime();

        String dateTime = TimeUtils.getDateTime(submitTime, "yyyy-MM-dd HH:mm:ss");
        sendOneCertMessageInfo.setReqtime(dateTime);
        String dataAsign = UUID.randomUUID().toString();

        sendOneCertMessageInfo.setEvidenceID(evidenceID);
        sendOneCertMessageInfo.setDatasign(dataAsign);
        sendOneCertMessageInfo.setData(oneSendMessage);

        SecurityProtocolEntity securityProtocolEntity = new SecurityProtocolEntity((short) 1, (short) 0x3000, JSONObject.toJSONString(sendOneCertMessageInfo));
//
        TwoSendMessage twoSendMessage = new TwoSendMessage();
        twoSendMessage.setFileGloID(data.getDataNo());
        twoSendMessage.setSubmit_Time(dateTime);
        twoSendMessage.setStroeTitle(data.getStoreTitle());
        twoSendMessage.setStroeAbs(data.getStoreAbs().split(","));
        twoSendMessage.setStoreHash(data.getStoreHash());
        twoSendMessage.setStoreSize(storeSize);
        twoSendMessage.setStoreMod(fileType);
        twoSendMessage.setStoreSign(data.getStoreSign());
        twoSendMessage.setWoR(true);
        twoSendMessage.setStatus("origin");
        twoSendMessage.setRetstate(true);
        twoSendMessage.setGlobalID(data.getDataNo());
        twoSendMessage.setPerformer(data.getPerformerName());
        if(Objects.isNull(data.getMaskControlSet())){
            twoSendMessage.setDesenControlSet("test");
        }else{
            twoSendMessage.setDesenControlSet(data.getMaskControlSet());
        }
        List<String> desenIntentions = new ArrayList<>();
        List<String> desenRequirements = new ArrayList<>();
        List<Integer> desenLevels = new ArrayList<>();
        try {
            desenIntentions.add(data.getMaskIntention());
            desenIntentions.add(data.getMaskRequirements());
            desenLevels =splitFileUploadMessage(data.getMaskLevel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        twoSendMessage.setDesenIntention(desenIntentions);
        twoSendMessage.setDesenLevel(desenLevels);
        twoSendMessage.setDesenRequirements(desenRequirements);
        SendTowCertMessageInfo sendTowCertMessageInfo = new SendTowCertMessageInfo();
        sendTowCertMessageInfo.setData(twoSendMessage);
        sendTowCertMessageInfo.setGlobalID(data.getDataNo());
        sendTowCertMessageInfo.setDataHash(data.getStoreHash());
        sendTowCertMessageInfo.setDatasign(dataAsign);

        SecurityProtocolEntity twoSecurityProtocolEntity = new SecurityProtocolEntity((short) 3, (short) 0x3010, JSONObject.toJSONString(sendTowCertMessageInfo));

        messageInfo.setOneSecurityProtocolEntity(securityProtocolEntity);
        messageInfo.setTowSecurityProtocolEntity(twoSecurityProtocolEntity);
        System.out.println("handleMessageInfo:"+JSONObject.toJSONString(messageInfo));
//      第一次信息发送
        CertCenterTcpClient.sendMessage(messageInfo);

    }


    public static void queryFileUploadMessage(GetStoreDTO getStoreDTO){

        StoreInfoDTO data = getStoreDTO.getStoreInfoDTO();

        BigDecimal storeSizeInKB = data.getStoreSize(); // 假设这是以KB为单位的存储大小
        BigDecimal storeSizeInBytes = storeSizeInKB.multiply(BigDecimal.valueOf(1024)); // 将KB转换为字节
        SendOneCertMessageInfo  sendOneCertMessageInfo =new SendOneCertMessageInfo();
        SendOneCertMessageInfo.OneSendMessage oneSendMessage = new SendOneCertMessageInfo.OneSendMessage();
        long storeSize = storeSizeInBytes.longValue();
        String fileType = FileSuffixType.getNameByCode(data.getStoreType());
        oneSendMessage.setObjectMode(fileType);
        oneSendMessage.setObjectSize(String.valueOf(storeSize));
        String evidenceID = UUID.randomUUID().toString();
        Date submitTime = data.getSubmitTime();

        String dateTime = TimeUtils.getDateTime(submitTime, "yyyy-MM-dd HH:mm:ss");
        sendOneCertMessageInfo.setReqtime(dateTime);
        String dataAsign = UUID.randomUUID().toString();

        sendOneCertMessageInfo.setEvidenceID(evidenceID);
        sendOneCertMessageInfo.setDatasign(dataAsign);
        sendOneCertMessageInfo.setData(oneSendMessage);

        SecurityProtocolEntity securityProtocolEntity = new SecurityProtocolEntity((short) 1, (short) 0x3000, JSONObject.toJSONString(sendOneCertMessageInfo));
        MessageInfo messageInfo = new MessageInfo();
//
        TwoSendMessage twoSendMessage = new TwoSendMessage();
        twoSendMessage.setFileGloID(data.getDataNo());
        twoSendMessage.setSubmit_Time(dateTime);
        twoSendMessage.setStroeTitle(data.getStoreTitle());
        twoSendMessage.setStroeAbs(data.getStoreAbs().split(","));
        twoSendMessage.setStoreHash(data.getStoreHash());
        twoSendMessage.setStoreSize(storeSize);
        twoSendMessage.setStoreMod(fileType);
        twoSendMessage.setStatus("origin");
        twoSendMessage.setRetstate(true);
        twoSendMessage.setStoreSign(data.getStoreSign());
        twoSendMessage.setWoR(false);
        twoSendMessage.setGlobalID(data.getDataNo());

        twoSendMessage.setPerformer(data.getPerformerName());
//        脱敏控制级别
        if(Objects.isNull(data.getMaskControlSet())){
            twoSendMessage.setDesenControlSet("test");
        }else{
            twoSendMessage.setDesenControlSet(data.getMaskControlSet());
        }

        List<String> desenIntentions = new ArrayList<>();
        desenIntentions.add(data.getMaskIntention());
        twoSendMessage.setDesenIntention(desenIntentions);

        List<String> desenRequirements = new ArrayList<>();
        desenIntentions.add(data.getMaskRequirements());
        twoSendMessage.setDesenRequirements(desenRequirements);

        List<Integer> desenLevels = new ArrayList<>();
        String input = data.getMaskLevel();
        String[] parts = input.split(",");
        for (String part : parts) {
            int number = Integer.parseInt(part.trim());
            desenLevels.add(number);// trim() 方法用于去除字符串两端的空白字符
            // 在这里可以处理得到的数字
        }
        twoSendMessage.setDesenLevel(desenLevels);
        SendTowCertMessageInfo sendTowCertMessageInfo = new SendTowCertMessageInfo();
        sendTowCertMessageInfo.setData(twoSendMessage);
        sendTowCertMessageInfo.setGlobalID(data.getDataNo());
        sendTowCertMessageInfo.setDataHash(data.getStoreHash());
        sendTowCertMessageInfo.setDatasign(dataAsign);


        SecurityProtocolEntity twoSecurityProtocolEntity = new SecurityProtocolEntity((short) 3, (short) 0x3010,JSONObject.toJSONString(sendTowCertMessageInfo));

        messageInfo.setOneSecurityProtocolEntity(securityProtocolEntity);
        messageInfo.setTowSecurityProtocolEntity(twoSecurityProtocolEntity);
        System.out.println("handleMessageInfo:"+JSONObject.toJSONString(messageInfo));
//      第一次信息发送
        CertCenterTcpClient.sendMessage(messageInfo);

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 处理接收到的消息

        SplitCommunicationEntity fileSplitEntity = (SplitCommunicationEntity) msg;
        byte[] bytes = fileSplitEntity.toByteArray();
        System.out.println(bytes);
        // 创建 ByteBuf 对象并写入数据
        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeBytes(bytes);
        // 将缓冲区写回客户端
        ctx.writeAndFlush(buffer).addListener(ChannelFutureListener.CLOSE);

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        // 处理心跳超时等事件

    }


    public static ArrayList<Integer> splitFileUploadMessage(String message) {
        ArrayList<Integer> numberList = new ArrayList<>();
        // 将字符串按逗号分割
        String[] numbers = message.split(",");

        // 遍历每个子字符串并解析为整数
        for (String number : numbers) {
            try {
                int value = Integer.parseInt(number.trim());
                numberList.add(value);
            } catch (NumberFormatException e) {
                // 处理解析错误，您可以选择抛出异常或跳过错误的值
                System.err.println("try: " + number);
            }
        }
        return numberList;
    }

}
