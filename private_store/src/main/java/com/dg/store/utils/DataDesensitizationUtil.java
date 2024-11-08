package com.dg.store.utils;

import com.dg.common.enums.DesenLevelType;

public class DataDesensitizationUtil {

    /**
     * 脱敏邮箱地址
     *
     * @param email 邮箱地址
     * @return 脱敏后的邮箱
     */
    public static String desensitizeEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int index = email.indexOf("@");
        if (index > 1) {
            return email.substring(0, 1) + "***" + email.substring(index - 1);
        }
        return email;
    }

    /**
     * 脱敏手机号码
     *
     * @param phone 手机号码
     * @return 脱敏后的手机号
     */
    public static String desensitizePhone(String phone) {
        if (phone == null || phone.length() < 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 脱敏姓名
     *
     * @param name 姓名
     * @return 脱敏后的姓名
     */
    public static String desensitizeName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        if (name.length() == 1) {
            return name;
        }
        return name.substring(0, 1) + "*".repeat(name.length() - 1);
    }

    /**
     * 脱敏居住地址
     * 保留地址的前几个字符（如省市信息）并隐藏剩余部分。
     *
     * @param address 居住地址
     * @return 脱敏后的地址
     */
    public static String desensitizeAddress(String address) {
        if (address == null || address.length() <= 6) {
            return address; // 如果地址长度较短，直接返回原地址或部分隐藏
        }
        // 假设保留前6个字符（例如，省市区信息），后面的以星号(*)代替
        return address.substring(0, 6) + "*".repeat(address.length() - 6);
    }

    /**
     * 脱敏身份证号码
     *
     * @return java.lang.String
     * @author Xbin
     * @date 2024-04-10 16:27
     */
    public static String desensitizeIdCard(String idCard) {
        if (idCard == null || idCard.length() != 18) {
            return idCard;
        }
        return idCard.substring(0, 4) + "**********" + idCard.substring(14);
    }

    /**
     * 脱敏银行卡号
     *
     * @return java.lang.String
     * @author Xbin
     * @date 2024-04-10 16:27
     */
    public static String desensitizeBankCard(String bankCard) {
        if (bankCard == null || bankCard.length() < 8) {
            return bankCard;
        }
        return bankCard.substring(0, 4) + "********" + bankCard.substring(bankCard.length() - 4);
    }

    /**
     * 脱敏出生日期
     *
     * @return java.lang.String
     * @author Xbin
     * @date 2024-04-10 16:27
     */
    public static String desensitizeBirthDate(String birthDate) {
        if (birthDate == null || birthDate.length() < 4) {
            return birthDate;
        }
        return birthDate.substring(0, 4) + "****";
    }

    /**
     * 脱敏密码
     *
     * @return java.lang.String
     * @author Xbin
     * @date 2024-04-10 16:27
     */
    public static String desensitizePassword(String password) {
        // 返回固定的星号字符串
        return "******";
    }

    /**
     * 脱敏IP地址
     *
     * @return java.lang.String
     * @author Xbin
     * @date 2024-04-10 16:27
     */
    public static String desensitizeIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return ipAddress;
        }
        int lastDotIndex = ipAddress.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return ipAddress.substring(0, lastDotIndex) + ".*";
        }
        return ipAddress;
    }

    public static String maskData(String data, int maskCode) {

        DesenLevelType desenLevelTypeById = DesenLevelType.getDesenLevelTypeById(maskCode);

        switch (desenLevelTypeById) {
            case ID:
                return desensitizeIdCard(data);
            case IP:
                return desensitizeIpAddress(data);
            case BANK:
                return desensitizeBankCard(data);
            case EMAIL:
                return desensitizeEmail(data);
            case LOCAL:
                return desensitizeAddress(data);
            case PHONE:
                return desensitizePhone(data);
            case BIRTHDAY:
                return desensitizeBirthDate(data);
            case PASSWORD:
                return desensitizePassword(data);
            case NAME:
                return desensitizeName(data);
            default:
                return data;
        }
    }
}
