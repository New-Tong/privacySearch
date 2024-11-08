package com.dg.communication.service;


public interface NettyServerService {

    /**
     * 服务器启动
     *
     * @author Xbin
     * @date 2023-11-30 10:56
     * @return void
     */
    void start();

    /**
     * 服务器结束
     *
     * @author Xbin
     * @date 2023-11-30 10:56
     * @return void
     */
    void close();
}
