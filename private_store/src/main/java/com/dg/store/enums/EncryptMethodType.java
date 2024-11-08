package com.dg.store.enums;

/**
 * @Author:Xbin
 * Description: 加密方法类型
 * Date 2024-04-10 16:06
 * Version v2.0
*/
public enum EncryptMethodType {
//      md5加密
     MD5(1,"MD5");

     private int code;

     private String name;

    EncryptMethodType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
