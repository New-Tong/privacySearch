package com.dg.communication.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version v1.0.0
 * @belongsProject: private_search
 * @belongsPackage: com.dg.communication.exception
 * @author: XBin
 * @description: 存证通信异常
 * @createTime: 2024-01-09 14:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunicationCertException extends RuntimeException{

    /**
     * 异常码
     */
    private Integer code;

    /**
     * 异常信息
     */
    private String message;

    /**
     * 异常类型
     */
    private Object data;

    public CommunicationCertException( Integer code, String message) {

        this.code = code;
        this.message = message;
    }
}
