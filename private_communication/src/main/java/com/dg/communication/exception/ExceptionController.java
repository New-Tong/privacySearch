package com.dg.communication.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @version v1.0.0
 * @belongsProject: private_search
 * @belongsPackage: com.dg.communication.exception
 * @author: XBin
 * @description: 统一异常处理类
 * @createTime: 2024-01-09 14:38
 */

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    /**
     * 与存证信息通信异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(CommunicationCertException.class)
    @ResponseBody
    public void catchConstraintViolationException(CommunicationCertException e) {
//        打印异常信息
        e.printStackTrace();
        log.error("存证通信异常：{}",e.getMessage());
        log.error("异常数据为:{}",e.getData());
        return;
    }

}
