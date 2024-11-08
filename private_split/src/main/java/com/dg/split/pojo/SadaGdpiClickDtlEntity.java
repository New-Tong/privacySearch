package com.dg.split.pojo;

import lombok.Data;

//
@Data
public class SadaGdpiClickDtlEntity {
//    客户所在ip
    private String srcip;
//    上网设备号
    private String ad;
//    用户请求时间戳
    private long ts;
//  用户请求的d
    private String ref;

    private String ua;

    private String dstip;

    private String cookie;

    private String src_port;

    private String json;

    private String datelabel;

    private String loadstamp;
}
