package com.dg.common.enums;

/**
 * @version v1.0.0
 * @belongsProject: privateSearch
 * @belongsPackage: com.dg.common.enums
 * @author: XBin
 * @description: 文件类型
 * @createTime: 2024-04-10 15:31
 */

public enum  FileType {
//    表格
    TABLE(1,"table"),
//    文本
    TEXT(2,"text"),
//    图片
    IMAGE(3,"image"),
//    视频
    VIDEO(4,"video"),
//    音频
    AUDIO(5,"audio");


    private Integer code;

    private String name;


    FileType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
