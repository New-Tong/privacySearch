package com.dg.query.dto.req;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FindStoreRequest {

    @NotBlank(message = "数据编号不能为空")
    private String storeNo;

}
