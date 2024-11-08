package com.dg.common.enums;

public enum FileSuffixType {

//      表格
    XLSX(1,"xlsx"),
//      json
    JSON(2,"json"),
//      文本
    TXT(3,"txt"),
//      表格
    XLS(4,"xls"),
//      未知文件
    UNKNOWN(5,"unknown");

    private  Integer code;

    private String name;

    FileSuffixType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        for (FileSuffixType fileSuffixType : FileSuffixType.values()) {
            if (fileSuffixType.getCode().equals(code)) {
                return fileSuffixType.getName();
            }
        }
        return "JSON"; // 或者可以选择抛出一个异常，如果认为code无效或者不应该出现未知code的情况
    }

    public static FileSuffixType getFileTypeByCode(Integer code)  {
        for (FileSuffixType fileSuffixType : FileSuffixType.values()) {
            if (fileSuffixType.getCode().equals(code)) {
                return fileSuffixType;
            }
        }
        return UNKNOWN;
    }
}
