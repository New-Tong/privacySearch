package com.dg.communication.controller;

import com.dg.communication.pojo.MessageInfo;

import com.dg.communication.service.CertCenterTcpClient;
import com.dg.communication.service.CertLocalTcpClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/communication")
public class CommunicationController {

    @PostMapping("/send/local/message")
    public void sendLocalMessage(MessageInfo messageInfo){
        CertLocalTcpClient.sendMessage(messageInfo);
    }

    @PostMapping("/send/center/message")
    public void sendCenterMessage(MessageInfo messageInfo){
        CertCenterTcpClient.sendMessage(messageInfo);
    }
}
