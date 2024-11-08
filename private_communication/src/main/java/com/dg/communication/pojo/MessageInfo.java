package com.dg.communication.pojo;

import lombok.Data;

/**
 * @version v1.0.0
 * @belongsProject: private_search
 * @belongsPackage: com.dg.communication.pojo
 * @author: XBin
 * @description: 一次通信需要发送的message
 * @createTime: 2024-01-09 16:59
 */

@Data
public class MessageInfo {

    /**
     * 存证第一次发送申请通信信息
     */
    private SecurityProtocolEntity oneSecurityProtocolEntity;

    /**
     * 存证第二次发送结果信息
     */
    private SecurityProtocolEntity towSecurityProtocolEntity;

}
