package com.dg.common.enums;

/**
 * @version v1.0.0
 * @belongsProject: privateSearch
 * @belongsPackage: com.dg.common.enums
 * @author: XBin
 * @description: 对外接口通信类型
 * @createTime: 2024-04-09 14:51
 */
public enum  CommandType {

//      文件上传
    UPLOAD((short) 1,"upload"),
//      文件下载
    DOWNLOAD((short)2,"download"),
//      文件上传返回
    UPLOAD_BACK((short)3,"upload_back"),
//      文件下载返回
    DOWNLOAD_BACK((short)4,"download_back");

    private short code;

    private String name;

    CommandType(short code,String name){

        this.code = code;

        this.name = name;

    }

    public  short getCode() {
        return code;
    }

    public void setCode(short code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // 根据代码获取对应的命令类型
    public static CommandType getByCode(short code) {
        for (CommandType type : CommandType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }


}
