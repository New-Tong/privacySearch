package com.dg.common.enums;

/**
 * @version v1.0.0
 * @belongsProject: privateSearch
 * @belongsPackage: com.dg.common.enums
 * @author: XBin
 * @description: 脱敏级别（根据级别返回不同的数据）
 * @createTime: 2024-04-10 14:36
 */

public enum DesenLevelType {

    //    未加密（没有明确指定就是默认）
    UN(1, "未脱敏"),
    //    姓名
    NAME(2, "姓名脱敏"),
    //    身份证
    ID(3, "身份证脱敏"),
    //    地址
    LOCAL(4, "地址脱敏"),
    //    手机号码
    PHONE(5, "手机号码脱敏"),
    //    邮箱
    EMAIL(6, "邮箱脱敏"),
    //    银行卡号
    BANK(7, "银行卡号脱敏"),
    //    密码
    PASSWORD(8, "密码脱敏"),
    //    ip地址
    IP(9, "IP脱敏"),
    //    生日
    BIRTHDAY(10, "生日脱敏");

    private int code;

    private String message;

    DesenLevelType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static DesenLevelType getDesenLevelTypeById(int code){
        for (DesenLevelType desenLevelType : DesenLevelType.values()) {
            if (desenLevelType.getCode()==code) {
                return desenLevelType;
            }
        }
        return UN;
    }
}
