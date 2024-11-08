package com.dg.communication.pojo;

import lombok.Data;

/**
 * @version v1.0.0
 * @belongsProject: private_search
 * @belongsPackage: com.dg.communication.pojo
 * @author: XBin
 * @description: 第二次本地存证返回内容
 * @createTime: 2024-01-09 11:16
 */
@Data
public class CertTwoBackMessageInfo {

    /**
     * 系统ID类型  0x10000000 存证系统      0x2000000分类分级系统   0x30000000 个人信息拆分脱敏存储与重构系统（文件拆分重组）
     */
    private Integer systemId ;

    /**
     * 上报系统的IP地址
     */
    private String systemIP;

    /**
     * 消息类型
     * 0x0001 存证信息上报请求 0x0002 请求响应  0x0003 存证信息上报  0x0004 存证消息反馈  0x0005 证据查询
     * 0x0006证据查询结果反馈  0x0007 异常行文上报
     */
    private Integer mainCMD;

    /**
     * 0x0010 暂无  0x0011 暂无  0x0020 敏感信息识别 0x0021 分类分级
     * 0x0030 拆分脱敏存储与重构  0x0031 脱敏 0x0032 脱敏效果评测
     * 0x0033 脱敏技术要求合规检查 0x0040 删除指令通知与确认 0x0041 确定性删除
     * 0x0042 删除效果测评 0x0050 暂无 0x0051 暂无 0x0052 暂无 0x0053
     */
    private Integer subCMD;

    /**
     * 内部业务唯一ID （使用UUID生成） 上报信息提供的业务内部索引号
     */
    private String evidenceID;

    /**
     * 消息类型编码表  中心存证 0x3000 十12288  本地 0x3010 十12304 上报数据 0x3020 十12320
     */
    private Integer msgVersion;

    /**
     * 存证系统应答时间，时间格式为 "yyyy-MM-dd HH:mm:ss
     */
    private String status;

    /**
     * 收条返回信息data数据域中data字段的hash值
     */
    private String certificateHash;

    /**
     * 收条返回信息data数据域中data字段的sign值
     */
    private String certificateSign;

    /**
     * 返回信息
     */
    private TowBackMessageData data;
}
@Data
class TowBackMessageData{

    /**
     * 上报信息在存证系统中存储的唯一ID
     */
    String certificateID;

    /**
     * 收条反馈时间
     */
    String certificateTime;

}
