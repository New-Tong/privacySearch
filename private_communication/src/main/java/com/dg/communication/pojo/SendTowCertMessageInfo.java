package com.dg.communication.pojo;

import com.dg.communication.utils.SocketUtils;
import com.dg.communication.utils.TimeUtils;
import lombok.Data;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @version v1.0.0
 * @belongsProject: private_search
 * @belongsPackage: com.dg.communication.pojo
 * @author: XBin
 * @description: 第二次发送完整信息到本地存证系统
 * @createTime: 2024-01-09 10:53
 */
@Data
public class SendTowCertMessageInfo {
    /**
     * 系统ID类型  0x10000000 存证系统      0x2000000分类分级系统   0x30000000 个人信息拆分脱敏存储与重构系统（文件拆分重组）
     */
    private Integer systemId = 805306368;

    /**
     * 上报系统的IP地址
     */
    private String systemIP = SocketUtils.localIPGet();

    /**
     * 消息类型
     * 0x0001 存证信息上报请求 0x0002 请求响应  0x0003 存证信息上报  0x0004 存证消息反馈  0x0005 证据查询
     * 0x0006证据查询结果反馈  0x0007 异常行文上报
     */
    private Integer mainCMD = 1;

    /**
     * 0x0010 暂无  0x0011 暂无  0x0020 敏感信息识别 0x0021 分类分级
     * 0x0030 拆分脱敏存储与重构  0x0031 脱敏 0x0032 脱敏效果评测
     * 0x0033 脱敏技术要求合规检查 0x0040 删除指令通知与确认 0x0041 确定性删除
     * 0x0042 删除效果测评 0x0050 暂无 0x0051 暂无 0x0052 暂无 0x0053
     */
    private Integer subCMD = 48;

    /**
     * 操作日志业务内部索引号
     */
    private String operateLogID;

    /**
     * 文件全局ID
     */
    private String globalID;

    /**
     * 内部业务唯一ID （使用UUID生成）
     */
    private String evidenceID;

    /**
     * 消息类型编码表  中心存证 0x3000 十12288  本地 0x3010 十12304 上报数据 0x3020 十12320
     */
    private Integer msgVersion = 12304;

    /**
     * 证据提交时间，时间格式为 "yyyy-MM-dd HH:mm:ss
     */
    private String submittime = TimeUtils.getLocalTime("yyyy-MM-dd HH:mm:ss");

    /**
     * 对数据域的 Hash   也就是 data的 hash 例："56e3be093f377b9d984eef02a982d852d1bce062fdb505bcf87df46141fd80aa"
     */

    private String dataHash;

    /**
     * 系统A对data字段的签名 例："56e3be093f377b9d984eef02a982d852d1bce062fdb505bcf87df46141fd80aa"
     */
    private String datasign;

    /**
     * 发送完整的信息到本地存证系统
     */
    private TwoSendMessage data;

    /**
     * 随机标识（密文字段），确保存证上报前做过请求应答, 从中心存证系统获取到的（第一次返回的响应）
     */
    private String randomidentification;

    /**
     * 防伪码
     */
    private String nonce;
}
