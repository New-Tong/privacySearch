package com.dg.communication.service;

public interface NettyService {

    void start();

    /**
     * 检测服务器状态
     *
     * @author Xbin
     * @date 2023-11-30 11:02
     * @return boolean
    */
    boolean checkState();

    /**
     * 关闭服务器
     *
     * @author Xbin
     * @date 2023-11-30 11:03
     * @return void
     */
    void close();

}
