package com.dg.communication.pojo;

import com.dg.communication.utils.TimeUtils;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @version v1.0.0
 * @belongsProject: private_search-main
 * @belongsPackage: edu.dgut.pss_file_manage_server.model
 * @author: XBin
 * @description:
 * @createTime: 2023-10-15 11:02
 */
@Data
public class TwoSendMessage {

    /**
     * 文件全局ID
     */
    private String fileGloID;

    /**
     * 文件全局ID
     */
    private String globalID;

    /**
     * 状态
     */
    private String status;

    /**
     * 路径树？？？
     */
    private String pathtree = "test";


    private String optTime = TimeUtils.getLocalTime("yyyy-MM-dd HH:mm:ss");

    /**
     * 提交日志时间，时间格式为"yyyy-MM-dd HH:mm:ss 编码格式统一为 UTF-8
     */
    private String submit_Time;

    /**
     * 拆分重构存储内容标题 例："科技大学学生户籍拆分存储"
     */
    private String StroeTitle ;

    /**
     * 拆分重构存储内容摘要 例：new String[]{"张三","450481197804234431","13843993484","1988-02-19"}
     */
    private String[] StroeAbs;

    /**
     * 拆分重构存储内容Hash 例："7cd6470346bf262b679da494890e90cf528c0b58dd008f502ab67d3e"
     */
    private String StoreHash;

    /**
     * 拆分重构存储文本大小 例：654
     */
    private Long StoreSize ;

    /**
     * 拆分重构存储对象的模态（也就是拆分对象的类型） 声音，视频，文本，表格，数据库等具体描述 例："table"
     */
    private String StoreMod;

    /**
     * 拆分重构存储文件的签名 例："sa1905kgd9u25jlk9fgh89lj234jfdbuey"
     */
    private String StoreSign;

    /**
     * 数据流动方向  True对应拆分  False对应重构
     */
    private Boolean WoR;

    /**
     * 存取方
     */
    private String Performer;

    /**
     * 执行结果 True成功  False失败
     */
    private Boolean retstate;

    /**
     * 脱敏意图
     */
    private List<String> desenIntention;

    /**
     * 脱敏要求
     */
    private List<String> desenRequirements;

    /**
     * 脱敏控制合集（操作配置）
     */
    private String desenControlSet;

    /**
     * 脱敏级别
     */
    private List<Integer> desenLevel;

}
